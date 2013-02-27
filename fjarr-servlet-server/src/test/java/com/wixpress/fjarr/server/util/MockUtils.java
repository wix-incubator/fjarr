package com.wixpress.fjarr.server.util;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * @author AlexeyR
 * @since 12/6/12 12:54 PM
 */

public class MockUtils
{
    public static <E> Enumeration<E> fromIterator(final Iterator<E> iterator)
    {
        return new Enumeration<E>()
        {
            @Override
            public boolean hasMoreElements()
            {
                return iterator.hasNext();
            }

            @Override
            public E nextElement()
            {
                return iterator.next();
            }
        };
    }
}
