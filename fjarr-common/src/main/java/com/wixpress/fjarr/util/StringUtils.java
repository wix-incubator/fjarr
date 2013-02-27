package com.wixpress.fjarr.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author AlexeyR
 * @since 11/25/12 2:38 PM
 */

public class StringUtils
{
    /**
     * Copied from Spring's StringUtils
     *
     * @param collection
     * @param delimiter
     * @return
     */
    public static String collectionToDelimitedString(Collection<?> collection, String delimiter)
    {
        if (collection == null || collection.isEmpty())
            return "";

        StringBuilder sb = new StringBuilder();
        Iterator<?> it = collection.iterator();
        while (it.hasNext())
        {
            sb.append(it.next());
            if (it.hasNext())
            {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    public static boolean isBlank(String s)
    {
        return s == null || s.trim().equals("");

    }

    public static boolean isNotBlank(String s)
    {
        return !(s == null || s.trim().equals(""));
    }

}
