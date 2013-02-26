package org.wixpress.fjarr.matcher;

import org.apache.http.Header;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static org.hamcrest.core.Is.is;

/**
 * @author alex
 * @since 1/3/13 6:31 PM
 */

public class HeaderMatcher extends org.hamcrest.TypeSafeMatcher<Header>

{
    final Matcher<String> nameMatcher;
    final Matcher<String> valueMatcher;


    public HeaderMatcher(Matcher<String> nameMatcher, Matcher<String> valueMatcher)
    {
        this.nameMatcher = nameMatcher;

        this.valueMatcher = valueMatcher;
    }

    @Override
    public boolean matchesSafely(Header header)
    {
        return nameMatcher.matches(header.getName()) && valueMatcher.matches(header.getValue());
    }



    @Override
    public void describeTo(Description description)
    {
        description.appendText("Header [")
        .appendDescriptionOf(nameMatcher)
        .appendText(":")
        .appendDescriptionOf(valueMatcher)
        .appendText("]");
    }

    public static HeaderMatcher isHeader(String name, String value)
    {
        return new HeaderMatcher(is(name),is(value));
    }
}
