package com.wixpress.framework.rpc.introspect.javadoc;

import com.wixpress.fjarr.monads.Option;
import com.wixpress.fjarr.reflection.MethodReflector;

import java.net.URL;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 1/4/12
 */
public interface DocumentationProvider
{
    Option<String> getCommentForClass(Class<?> clazz);
    Option<MethodJavaDoc> getJavaDocForMethod(MethodReflector methodReflector);


    public interface MethodJavaDoc
    {
        Option<String> getComment();
        Option<TypeJavaDoc> getReturnArgument();
        List<NamedTypeJavaDoc> getParameters();
        List<TypeJavaDoc> getExceptions();
    }

    public interface TypeJavaDoc
    {
        String getTypeAsString();
        Option<URL> getJavaDocUrl();
        Option<String> getComment();
    }

    public interface NamedTypeJavaDoc extends TypeJavaDoc
    {
        String getName();
    }
}
