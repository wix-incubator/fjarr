package com.wixpress.framework.rpc.introspect.javadoc;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.*;
import com.wixpress.hoopoe.memoize.Memoize;
import com.wixpress.hoopoe.monads.Option;
import com.wixpress.hoopoe.reflection.MethodReflector;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static com.google.common.collect.Lists.transform;
import static com.wixpress.hoopoe.monads.Option.*;
import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.*;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 1/4/12
 */

//TODO: add support for generic types after upgrading to the latest qdox
public class QDoxDocumentationProviderImpl implements DocumentationProvider
{
    private static final String RETURN_TAG = "return";
    private static final Logger LOGGER = LoggerFactory.getLogger(QDoxDocumentationProviderImpl.class);
    private static final String PARAM_TAG = "param";
    private static final Function<DocletTag,String> DOCLET_TAG_STRING_FUNCTION = new Function<DocletTag, String>()
    {
        @Override
        public String apply(DocletTag input)
        {
            return input.getName();
        }
    };

    final JavaDocBuilder javaDocBuilder;
    private final QDoxDocumentationProviderImpl.QDoxJavaParameterToNamedTypeJavaDocTransformer paramTransformer;

    public QDoxDocumentationProviderImpl(JavaDocBuilder javaDocBuilder)
    {
        this.javaDocBuilder = javaDocBuilder;
        paramTransformer = new QDoxJavaParameterToNamedTypeJavaDocTransformer();
    }

    @Memoize
    @Override
    public Option<String> getCommentForClass(Class<?> clazz)
    {
        Option<JavaClass> javaClassOption = Option.Option(javaDocBuilder.getClassByName(clazz.getName()));
        return javaClassOption.isDefined() ? Option(StringUtils.defaultIfEmpty(javaClassOption.get().getComment(), null)) : Option.<String>None();
    }

    @Memoize
    @Override
    public Option<MethodJavaDoc> getJavaDocForMethod(MethodReflector methodReflector)
    {
        Class<?> declaringClass = methodReflector.getRawMethod().getDeclaringClass();
        Option<JavaClass> javaClassOption = Option.Option(javaDocBuilder.getClassByName(declaringClass.getName()));

        if (javaClassOption.isEmpty())
            return None();

        JavaClass javaClass = javaClassOption.get();

        JavaMethod javaMethod = resolveMethod(javaClass, methodReflector);

        Option<String> comment = Option.Option(javaMethod.getComment());

        Option<TypeJavaDoc> returnJavaDocOption = makeReturnJavaDoc(javaMethod);

        List<NamedTypeJavaDoc> params = transform(asList(javaMethod.getParameters()), paramTransformer());

        //TODO
        return Option.<MethodJavaDoc>Some(new MethodJavaDocImpl(comment, returnJavaDocOption, params, Collections.<TypeJavaDoc>emptyList()));
    }

    private Function<JavaParameter, NamedTypeJavaDoc> paramTransformer()
    {
        return paramTransformer;
    }

    class QDoxJavaParameterToNamedTypeJavaDocTransformer implements Function<JavaParameter, NamedTypeJavaDoc>
    {
        @Override
        public NamedTypeJavaDoc apply(JavaParameter input)
        {
            Option<String> comment = resolveCommentForParameter(input);
            return new NamedTypeJavaDocImpl(input.getType().getValue(), comment, input.getName());
        }
    }

    private Option<String> resolveCommentForParameter(JavaParameter input)
    {
        DocletTag[] paramTags = input.getParentMethod().getTagsByName(PARAM_TAG);
        
        if (paramTags == null || paramTags.length == 0)
            return None();

        for (DocletTag tag : paramTags)
        {
            String value = tag.getValue().trim();
            if (startsWithIgnoreCase(value, input.getName()))
                return Option(defaultIfEmpty(right(value, value.length() - input.getName().length()).trim(), null));
        }

        return None();
    }


    private Function<? super DocletTag, String> docletTagNameExtractor()
    {
        return DOCLET_TAG_STRING_FUNCTION;
    }

    private Option<TypeJavaDoc> makeReturnJavaDoc(JavaMethod javaMethod)
    {
        Type returns = javaMethod.getReturns();

        if (returns.equals(Type.VOID))
            return None();
        
        String typeAsString = returns.getValue();
        Option<String> comment = None();

        DocletTag[] tags = javaMethod.getTagsByName(RETURN_TAG);

        if (tags.length > 0)
            comment = Some(tags[0].getValue());
        
        return Option.<TypeJavaDoc>Some(new TypeJavaDocImpl(typeAsString, comment));
    }

    private JavaMethod resolveMethod(JavaClass javaClass, MethodReflector methodReflector)
    {
        for (JavaMethod method : javaClass.getMethods())
        {
            if (!method.getName().equals(methodReflector.getName()))
                continue;
            
            Type returnType = method.getReturns();
            
            if (!returnType.getJavaClass().getFullyQualifiedName().equals(methodReflector.getReturnTypes().getRawClass().getName()))
                continue;

            if (Iterables.size(methodReflector.getParameterTypes()) != method.getParameters().length)
                continue;
            
            //TODO: check parameters
            
            return method;
        }

        throw new NoSuchElementException("Cannot find method");
    }
    
    static class MethodJavaDocImpl implements MethodJavaDoc
    {
        final Option<String> comment;
        final Option<TypeJavaDoc> returnArgument;
        final List<NamedTypeJavaDoc> parameters;
        final List<TypeJavaDoc> exceptions;

        MethodJavaDocImpl(Option<String> comment, Option<TypeJavaDoc> returnArgument, List<NamedTypeJavaDoc> parameters, List<TypeJavaDoc> exceptions)
        {
            this.exceptions = exceptions;
            this.parameters = parameters;
            this.returnArgument = returnArgument;
            this.comment = comment;
        }

        @Override
        public Option<String> getComment()
        {
            return comment;
        }

        @Override
        public Option<TypeJavaDoc> getReturnArgument()
        {
            return returnArgument;
        }

        @Override
        public List<NamedTypeJavaDoc> getParameters()
        {
            return parameters;
        }

        @Override
        public List<TypeJavaDoc> getExceptions()
        {
            return exceptions;
        }
    }
    
    static class NamedTypeJavaDocImpl extends TypeJavaDocImpl implements NamedTypeJavaDoc
    {
        private final String name;

        NamedTypeJavaDocImpl(String typeAsString, Option<String> comment, String name)
        {
            super(typeAsString, comment);
            this.name = name;
        }

        @Override
        public String getName()
        {
            return name;
        }
    }

    static class TypeJavaDocImpl implements TypeJavaDoc
    {
        private final String typeAsString;
        private final Option<String> comment;

        TypeJavaDocImpl(String typeAsString, Option<String> comment)
        {
            this.typeAsString = typeAsString;
            this.comment = comment;
        }

        @Override
        public String getTypeAsString()
        {
            return typeAsString;
        }

        @Override
        public Option<URL> getJavaDocUrl()
        {
            return None();
        }

        @Override
        public Option<String> getComment()
        {
            return comment;
        }
    }
}
