package com.wixpress.framework.rpc.introspect;

import com.wixpress.framework.rpc.introspect.javadoc.DocumentationProvider;
import com.wixpress.hoopoe.monads.Option;

import static com.wixpress.framework.rpc.introspect.javadoc.DocumentationProvider.MethodJavaDoc;
import static com.wixpress.hoopoe.monads.Option.None;

/**
* @author shaiyallin
* @since 1/3/12
*/
public class MethodDescription {
    private final String name;
    private final String example;
    private final Option<MethodJavaDoc> javaDoc;

    MethodDescription(String name, String example, Option<MethodJavaDoc> javaDoc) {
        this.name = name;
        this.example = example;
        this.javaDoc = javaDoc;
    }

    MethodDescription(String name, String example) {
        this.name = name;
        this.example = example;
        this.javaDoc = None();
    }

    public Option<MethodJavaDoc> getJavaDoc() {
        return javaDoc;
    }

    public String getName() {
        return name;
    }

    public String getExample() {
        return example;
    }

    @Override
    public String toString() {
        return "MethodDescription{" +
                "name='" + name + '\'' +
                ", example='" + example + '\'' +
                '}';
    }
}
