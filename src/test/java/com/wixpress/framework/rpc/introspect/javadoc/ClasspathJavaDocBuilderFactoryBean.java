package com.wixpress.framework.rpc.introspect.javadoc;

import com.thoughtworks.qdox.JavaDocBuilder;
import org.junit.Ignore;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 1/5/12
 */
@Ignore
public class ClasspathJavaDocBuilderFactoryBean implements FactoryBean<JavaDocBuilder>
{
    private JavaDocBuilder builder;

    @Override
    public JavaDocBuilder getObject() throws Exception
    {
        if (builder == null)
            initBuilder();

        return builder;
    }

    @Override
    public Class<?> getObjectType()
    {
        return JavaDocBuilder.class;
    }

    @Override
    public boolean isSingleton()
    {
        return true;
    }

    private void initBuilder() throws IOException
    {
        Resource resource = new DefaultResourceLoader()
                .getResource("classpath:com/wixpress/framework/rpc/introspect/javadoc/SomeService.java");

        builder = new JavaDocBuilder();

        builder.addSource(resource.getURL());
    }
}
