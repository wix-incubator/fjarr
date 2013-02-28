package com.wixpress.fjarr.util;

import com.wixpress.fjarr.exceptions.TypeMismatch;

/**
 * @author AlexeyR
 * @since 12/6/12 6:55 PM
 */

public class DisjointUnion
{

    final Object o;
    final Class type;

    public DisjointUnion(Object o)
    {
        this.o = o;
        this.type = o.getClass();
    }

    public static <T> DisjointUnion from(T value)
    {
        return new DisjointUnion(value);
    }

    @SuppressWarnings("unchecked")
    /**
     * Check if the contained value is of type, or the type is a superclass of the value
     */
    public boolean is(Class clazz)
    {
        return type.equals(clazz) || clazz.isAssignableFrom(type);
    }

    public <T> T get(Class<T> clazz) throws TypeMismatch
    {
        if (this.is(clazz))
            return (T) o;
        else
            throw new TypeMismatch("Trying to get a mismatched type from Disjoint Union");
    }
}
