package org.wixpress.fjarr.rpc.http;

import org.junit.Test;

import static org.wixpress.fjarr.http.ContentTypeUtils.extractCharSet;
import static org.wixpress.fjarr.http.ContentTypeUtils.join;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author alex
 * @since 2/12/13 4:43 PM
 */

public class ContentTypeUtilsTest
{
    @Test
    public void joinTest()
    {
        assertThat(join("text/html","ISO-8859-4"),is("text/html; charset=ISO-8859-4"));
        assertThat(join("text/html","UTF-8"),is("text/html; charset=UTF-8"));
    }

@Test
    public void parseTest()
    {
        assertThat(extractCharSet("text/html; charset=ISO-8859-4","UTF-9"),is("ISO-8859-4"));
        assertThat(extractCharSet("text/html;","UTF-9"),is("UTF-9"));
        assertThat(extractCharSet("","UTF-9"),is("UTF-9"));
        assertThat(extractCharSet(null,"UTF-9"),is("UTF-9"));
        assertThat(extractCharSet("text/html; a=b;              c=d; charset=ISO-8859-4","UTF-9"),is("ISO-8859-4"));

    }
}
