package com.wixpress.fjarr.rpc.util;

import com.wixpress.fjarr.exceptions.TypeMismatch;
import com.wixpress.fjarr.util.DisjointUnion;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author AlexeyR
 * @since 12/6/12 7:14 PM
 */

public class DisjointUnionTests
{


    @Test
    public void testUnion() throws TypeMismatch
    {
        DisjointUnion du = DisjointUnion.from(1);
        assertThat(du.is(Integer.class), is(true));
        assertThat(du.is(String.class), is(false));
        assertThat(du.is(Long.class), is(false));

        assertThat(du.get(Integer.class), is(1));

        du = DisjointUnion.from("aaa");
        assertThat(du.is(Integer.class), is(false));
        assertThat(du.is(String.class), is(true));
        assertThat(du.is(Long.class), is(false));


        du = DisjointUnion.from(new NullPointerException());
        assertThat(du.is(Exception.class), is(true));
        assertThat(du.is(RuntimeException.class), is(true));
        assertThat(du.is(NullPointerException.class), is(true));

    }

}
