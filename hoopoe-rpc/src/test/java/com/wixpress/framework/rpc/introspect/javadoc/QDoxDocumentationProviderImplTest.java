package com.wixpress.framework.rpc.introspect.javadoc;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.wixpress.fjarr.monads.Option;
import com.wixpress.fjarr.reflection.ClassReflector;
import com.wixpress.fjarr.reflection.StaticReflectors;
import com.wixpress.fjarr.reflection.genericTypes.GenericType;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newLinkedList;
import static com.wixpress.framework.rpc.introspect.javadoc.DocumentationProvider.*;
import static com.wixpress.fjarr.monads.Option.Some;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 1/4/12
 */
public class QDoxDocumentationProviderImplTest
{
    private QDoxDocumentationProviderImpl provider;

    public QDoxDocumentationProviderImplTest() throws Exception
    {
        JavaDocBuilder builder = new ClasspathJavaDocBuilderFactoryBean().getObject();
        provider = new QDoxDocumentationProviderImpl(builder);
    }

    @Test
    public void getCommentForClass()
    {
        Option<String> classComment = provider.getCommentForClass(SomeService.class);

        assertTrue(classComment.isDefined());
    }
    
    @Test
    public void getJavaDocForMethodSimpleTypes()
    {
        Option<MethodJavaDoc> methodJavaDocOption = resolveMethod("getAge", typeOf(String.class), typeOf(Double.class));

        assertTrue(methodJavaDocOption.isDefined());

        MethodJavaDoc methodJavaDoc = methodJavaDocOption.get();
        
        assertThat(methodJavaDoc.getComment(), is(Some("Returns the user age")));

        assertTrue(methodJavaDoc.getReturnArgument().isDefined());

        assertThat(methodJavaDoc.getReturnArgument().get().getComment(), is(Some("age")));
        assertThat(methodJavaDoc.getReturnArgument().get().getTypeAsString(), is("java.lang.Integer"));

        List<NamedTypeJavaDoc> params = methodJavaDoc.getParameters();
        
        assertThat(params.size(), is(2));
        NamedTypeJavaDoc firstParam = params.get(0);
        assertThat(firstParam.getName(), is("userName"));
        assertThat(firstParam.getTypeAsString(), is("java.lang.String"));
        assertThat(firstParam.getComment(), is(Some("name of the user")));
        
        NamedTypeJavaDoc secondParam = params.get(1);
        
        assertTrue(secondParam.getComment().isEmpty());
        assertThat(secondParam.getName(), is("someParamWithoutComment"));
        assertThat(secondParam.getTypeAsString(), is("java.lang.Double"));

        //TODO: exceptions
    }

    @Test
    public void getJavaDocForMethodVoidReturn() throws Exception
    {
        Option<MethodJavaDoc> methodJavaDocOption = resolveMethod("increase");

        assertTrue(methodJavaDocOption.isDefined());

        MethodJavaDoc methodJavaDoc = methodJavaDocOption.get();

        assertTrue(methodJavaDoc.getReturnArgument().isEmpty());
        assertThat(methodJavaDoc.getParameters().size(), is(0));
        assertThat(methodJavaDoc.getComment(), is(Some("Increases something")));
    }

    @Test
    public void getJavaDocForMethodSomeReallyWeirdTypes()
    {
        Option<MethodJavaDoc> methodJavaDocOption = resolveMethod("someWeirdTypes", typeOf(Map.class));

        assertTrue(methodJavaDocOption.isDefined());

        MethodJavaDoc methodJavaDoc = methodJavaDocOption.get();

        assertThat(methodJavaDoc.getComment(), is(Some("Some weird method parameters")));

        Option<TypeJavaDoc> returnArgumentOption = methodJavaDoc.getReturnArgument();
        assertTrue(returnArgumentOption.isDefined());
        
        TypeJavaDoc returnArgument = returnArgumentOption.get();
                
        assertThat(returnArgument.getTypeAsString(), is("java.util.List"));

        //TODO
    }

    private GenericType typeOf(Class<?> clazz) {
        return StaticReflectors.Reflectors().reflect(clazz).getGenericClass();
    }

    private Option<MethodJavaDoc> resolveMethod(String methodName, GenericType ... argTypes)
    {
        ClassReflector classReflector = StaticReflectors.Reflectors().reflect(SomeService.class);
        return provider.getJavaDocForMethod(classReflector.getMethod(methodName, argTypes));
    }
}
