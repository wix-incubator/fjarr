package com.wixpress.fjarr.util;

import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * @author alex
 * @since 2/12/13 3:13 PM
 */

public class SettableFutureTest
{
    @Test
    public void testSetValue() throws ExecutionException, InterruptedException
    {
        final SettableFuture<Integer> future = new SettableFuture<Integer>();

        long s = System.currentTimeMillis();

        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(100);
                    future.setValue(10);
                }
                catch (InterruptedException e)
                {

                }

            }
        });
        t.start();

        Integer i = future.get();
        long e = System.currentTimeMillis();
        assertThat(i, is(10));
        assertTrue(e - s > 95);

        assertThat(future.cancel(true), is(false));
        assertThat(future.cancel(false), is(false));

        // can't set second time
        assertThat(future.setValue(20), is(false));
        assertThat(future.setError(new NullPointerException()), is(false));

        // second get doesn't block
        s = System.currentTimeMillis();
        assertThat(future.get(), is(10));
        e = System.currentTimeMillis();
        assertTrue(e - s < 95);


    }


    @Test
    public void testSetError() throws ExecutionException, InterruptedException
    {
        final SettableFuture<Integer> future = new SettableFuture<Integer>();

        long s = System.currentTimeMillis();

        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(100);
                    future.setError(new NullPointerException("test test test"));
                }
                catch (InterruptedException e)
                {

                }

            }
        });
        t.start();
        try
        {
            Integer i = future.get();
            fail("Should not get here");
        }
        catch (ExecutionException e)
        {
            long end = System.currentTimeMillis();
            assertTrue(end - s > 95);
            assertTrue(e.getCause() instanceof NullPointerException);
            assertThat(e.getCause().getMessage(), is("test test test"));
        }


        assertThat(future.cancel(true), is(false));
        assertThat(future.cancel(false), is(false));

        // can't set second time
        assertThat(future.setValue(20), is(false));
        assertThat(future.setError(new NullPointerException()), is(false));

        // second get doesn't block
        s = System.currentTimeMillis();
        try
        {
            assertThat(future.get(), is(10));
            fail("Should not get here");
        }
        catch (ExecutionException e)
        {
            assertTrue(e.getCause() instanceof NullPointerException);
            assertThat(e.getCause().getMessage(), is("test test test"));
        }
        long e = System.currentTimeMillis();
        assertTrue(e - s < 95);

    }


    @Test
    public void testCancel() throws ExecutionException, InterruptedException
    {
        final SettableFuture<Integer> future = new SettableFuture<Integer>();

        long s = System.currentTimeMillis();

        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(100);
                    future.cancel(true);
                }
                catch (InterruptedException e)
                {

                }

            }
        });
        t.start();
        try
        {
            Integer i = future.get();
            fail("Should not get here");
        }
        catch (InterruptedException e)
        {
            long end = System.currentTimeMillis();
            assertTrue(end - s > 95);

        }

    }

}
