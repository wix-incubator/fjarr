package org.wixpress.fjarr.util;

import org.wixpress.fjarr.exceptions.TypeMismatch;

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

    public boolean is(Class clazz)
    {
        return type.equals(clazz);
    }

    public <T> T get(Class<T> clazz) throws TypeMismatch
    {
        if (this.is(clazz))
            return (T) o;
        else
            throw new TypeMismatch("Trying to get a mismatched type from Disjoint Union");
    }
}
