package org.wixpress.fjarr.rpc.util;

import org.wixpress.fjarr.util.DisjointUnion;
import org.wixpress.fjarr.exceptions.TypeMismatch;
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




    }

}
