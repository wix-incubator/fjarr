package com.wixpress.fjarr.util;

import java.io.*;

/**
 * @author AlexeyR
 * @since 11/25/12 12:39 PM
 */

/**
 * Utilities for IO
 */
public class IOUtils
{
    public final static int BUFFER_SIZE = 1024 * 4;

    /**
     * Adapted from Apache-Commons IOUtils class for keeping the dependencies minimal
     *
     * @param is
     * @param charsetName
     * @return
     * @throws IOException
     */
    public static String toString(InputStream is, String charsetName) throws IOException
    {
        StringWriter output = new StringWriter(BUFFER_SIZE);
        InputStreamReader input = new InputStreamReader(is, charsetName);
        char[] buffer = new char[BUFFER_SIZE];
        int n = 0;
        while (-1 != (n = input.read(buffer)))
        {
            output.write(buffer, 0, n);
        }

        return output.toString();
    }

    public static void close(OutputStream os)
    {
        try
        {
            if (os != null)
            {
                os.close();
            }
        }
        catch (IOException e)
        {
            // swallow
        }
    }
}
