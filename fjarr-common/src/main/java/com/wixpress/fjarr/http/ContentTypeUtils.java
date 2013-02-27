package com.wixpress.fjarr.http;

import com.wixpress.fjarr.util.StringUtils;

/**
 * @author alex
 * @since 2/12/13 4:16 PM
 */

public class ContentTypeUtils
{
    //Content-Type: text/html; charset=ISO-8859-4


    public static String extractCharSet(String contentTypeHeader, String defaultCharSet)
    {
        if (StringUtils.isBlank(contentTypeHeader))
            return defaultCharSet;

        String[] parts = contentTypeHeader.split(";");
        for (String part : parts)
        {
            if (part.trim().startsWith("charset"))
            {
                String[] p = part.split("=");
                if (p.length >= 2)
                {
                    return p[1].trim();
                }
            }
        }
        return defaultCharSet;
    }


    public static String join(String contentType, String characterEncoding)
    {
        return contentType + "; charset=" + characterEncoding;
    }
}
