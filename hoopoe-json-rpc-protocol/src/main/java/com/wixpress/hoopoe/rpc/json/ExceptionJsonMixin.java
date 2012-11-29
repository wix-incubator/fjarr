package com.wixpress.hoopoe.rpc.json;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author alexeyr
 * @since 7/11/11 4:59 PM
 */

@JsonTypeInfo(include = JsonTypeInfo.As.PROPERTY, property = "@class", use = JsonTypeInfo.Id.CLASS)
public abstract class ExceptionJsonMixin
{
    @JsonIgnore
    public abstract Throwable getCause();
}
