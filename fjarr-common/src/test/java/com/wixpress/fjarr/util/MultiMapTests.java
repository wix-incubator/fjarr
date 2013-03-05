package com.wixpress.fjarr.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


public class MultiMapTests {

    @Test
    public void testPositive() {
        MultiMap<String, String> m = new MultiMap<String, String>()
                .with("a", "b", "c")
                .with("e", "d", "f");

        assertThat(m.get("a"), anyOf(is("b"), is("c")));
        assertThat(m.get("e"), anyOf(is("d"), is("f")));

        assertThat(m.getAll("a"), hasItems("b", "c"));
        assertThat(m.getAll("e"), hasItems("d", "f"));
        MultiMap<String, String> m1 = new MultiMap<String, String>()
                .with("a", "b", "c")
                .with("e", "d", "f");

        assertTrue(m.equals(m1));


    }

    @Test
    public void testNegative() {
        MultiMap<String, String> m = new MultiMap<String, String>();

        assertThat(m.get("a"), nullValue());
        assertThat(m.get("e"), nullValue());

        assertThat(m.getAll("a"), nullValue());
        assertThat(m.getAll("e"), nullValue());

    }


}
