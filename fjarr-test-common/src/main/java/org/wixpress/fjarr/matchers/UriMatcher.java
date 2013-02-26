package org.wixpress.fjarr.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.net.URI;

/**
 * @author alex
 * @since 1/3/13 6:58 PM
 */

public class UriMatcher extends TypeSafeMatcher<URI>
{

    final URI expected;

    public UriMatcher(URI expected)
    {
        this.expected = expected;
    }

    @Override
    public boolean matchesSafely(URI uri)
    {
        if (expected.getScheme() != null ? !expected.getScheme().equals(uri.getScheme()) : uri.getScheme() != null) return false;
        if (expected.getUserInfo() != null ? !expected.getUserInfo().equals(uri.getUserInfo()) : uri.getUserInfo() != null) return false;
        if (expected.getHost() != null ? !expected.getHost().equals(uri.getHost()) : uri.getHost() != null) return false;
        if (expected.getQuery() != null ? !expected.getQuery().equals(uri.getQuery()) : uri.getQuery() != null) return false;

        return expected.getPort() == uri.getPort();


    }

    @Override
    public void describeTo(Description description)
    {
        description.appendText(expected.toString());
    }

    public static UriMatcher isUri(URI expected)
    {
        return new UriMatcher(expected);
    }
}
