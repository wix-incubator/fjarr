package com.wixpress.fjarr.matchers;

import com.wixpress.fjarr.util.MultiMap;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsAnything;

import java.util.Set;

import static org.hamcrest.core.Is.is;

/**
 * @author alex
 * @since 1/4/13 12:08 PM
 */

public class IsMultiMapcontining<K, V> extends TypeSafeMatcher<MultiMap<K, V>>
{
    private final org.hamcrest.Matcher<K> keyMatcher;
    private final org.hamcrest.Matcher<V> valueMatcher;

    public IsMultiMapcontining(org.hamcrest.Matcher<K> keyMatcher, org.hamcrest.Matcher<V> valueMatcher)
    {
        this.keyMatcher = keyMatcher;
        this.valueMatcher = valueMatcher;
    }


    @Override
    public boolean matchesSafely(MultiMap<K, V> multiMap)
    {
        for (K k : multiMap.keySet())
        {
            if (keyMatcher.matches(k))
            {
                Set<V> vs = multiMap.getAll(k);
                for (V v : vs)
                {
                    if (valueMatcher.matches(v))
                        return true;
                }
            }
        }
        return false;
    }

    @Override
    public void describeTo(Description description)
    {
        description.appendText("multimap containing [")
                .appendDescriptionOf(keyMatcher)
                .appendText("->")
                .appendDescriptionOf(valueMatcher)
                .appendText("]");
    }


    @org.hamcrest.Factory
    public static <K, V> org.hamcrest.Matcher<MultiMap<K, V>> hasEntry(org.hamcrest.Matcher<K> keyMatcher, org.hamcrest.Matcher<V> valueMatcher)
    {
        return new IsMultiMapcontining<K, V>(keyMatcher, valueMatcher);
    }

    @org.hamcrest.Factory
    public static <K, V> org.hamcrest.Matcher<MultiMap<K, V>> hasEntry(K key, V value)
    {
        return new IsMultiMapcontining<K, V>(is(key), is(value));
    }

    @org.hamcrest.Factory
    public static <K, V> org.hamcrest.Matcher<MultiMap<K, V>> hasKey(org.hamcrest.Matcher<K> keyMatcher)
    {
        return new IsMultiMapcontining<K, V>(keyMatcher,(Matcher<V>)IsAnything.anything());
    }

    @org.hamcrest.Factory
    public static <K, V> org.hamcrest.Matcher<MultiMap<K, V>> hasKey(K key)
    {
        return new IsMultiMapcontining<K, V>(is(key), (Matcher<V>)IsAnything.anything());
    }

    @org.hamcrest.Factory
    public static <K, V> org.hamcrest.Matcher<MultiMap<K, V>> hasValue(org.hamcrest.Matcher<V> valueMatcher)
    {
        return new IsMultiMapcontining<K, V>((Matcher<K>)IsAnything.anything(), valueMatcher);
    }

    @org.hamcrest.Factory
    public static <K, V> org.hamcrest.Matcher<MultiMap<K, V>> hasValue(V value)
    {
        return new IsMultiMapcontining<K, V>((Matcher<K>)IsAnything.anything(), is(value));
    }
}
