package com.wixpress.framework.rpc.introspect;

import com.wixpress.fjarr.monads.Option;
import com.wixpress.fjarr.url.Url;

import java.util.List;

import static com.wixpress.fjarr.monads.Option.None;

/**
* @author shaiyallin
* @since 1/3/12
*/
public class SerivceDescription {
    private final String name;
    private final Url url;
    private final List<MethodDescription> methods;
    private final Option<String> comment;

    public SerivceDescription(String name, Url url, List<MethodDescription> methods, Option<String> comment) {
        this.name = name;
        this.url = url;
        this.methods = methods;
        this.comment = comment;
    }

    SerivceDescription(String name, Url url, List<MethodDescription> methods) {
        this.name = name;
        this.url = url;
        this.methods = methods;
        this.comment = None();
    }

    public Option<String> getComment() {
        return comment;
    }

    public String getName() {
        return name;
    }

    public Url getUrl() {
        return url;
    }

    public List<MethodDescription> getMethods() {
        return methods;
    }

    @Override
    public String toString() {
        return "SerivceDescription{" +
                "name='" + name + '\'' +
                ", url=" + url +
                ", methods=" + methods +
                '}';
    }
}
