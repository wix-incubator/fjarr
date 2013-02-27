package com.wixpress.fjarr.util;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author alex
 * @since 2/12/13 1:57 PM
 */

@SuppressWarnings("unchecked")
public class SettableFuture<T> implements Future<T>
{

    private AtomicReference<Either> result = new AtomicReference<Either>(Either.notSet());

    private final CountDownLatch sync = new CountDownLatch(1);

    @Override
    public boolean cancel(boolean mayInterruptIfRunning)
    {

        if (!mayInterruptIfRunning)
            return false;
        boolean res = result.compareAndSet(Either.notSet(), Either.cancelled());
        if (res)
        {
            sync.countDown();
            return res;
        }
        return result.get() instanceof Either.Cancelled;
    }

    @Override
    public boolean isCancelled()
    {
        return result.get() instanceof Either.Cancelled;
    }

    @Override
    public boolean isDone()
    {
        return sync.getCount() != 0;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException
    {
        sync.await();
        return (T) result.get().getOrThrow();
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
    {
        sync.await(timeout, unit);
        return (T) result.get().getOrThrow();
    }

    public boolean setValue(T value)
    {

        boolean res = result.compareAndSet(Either.notSet(), Either.value(value));
        if (res)
        {
            sync.countDown();
            return res;
        }

        return false;
    }


    public boolean setError(Throwable exception)
    {

        boolean res = result.compareAndSet(Either.notSet(), Either.executionError(exception));
        if (res)
        {
            sync.countDown();
            return res;
        }

        return false;
    }

    protected static abstract class Either<T>
    {

        private final static NotSet notSetInstance = new NotSet();


        abstract T getOrThrow() throws InterruptedException, ExecutionException;


        protected static <T> Either value(T value)
        {
            return new Value(value);
        }

        protected static Either cancelled()
        {
            return new Cancelled();
        }

        protected static Either notSet()
        {
            return notSetInstance;
        }


        protected static Either executionError(Throwable exception)
        {
            return new ExecutionEx(exception);
        }


        protected static class Value<T> extends Either<T>
        {
            final T value;

            public Value(T value)
            {
                this.value = value;
            }


            @Override
            public T getOrThrow() throws InterruptedException, ExecutionException
            {
                return value;
            }
        }

        protected static class Cancelled<T> extends Either<T>
        {

            @Override
            public T getOrThrow() throws InterruptedException, ExecutionException
            {
                throw new InterruptedException("Waiting for response was cancelled");
            }
        }


        protected static class NotSet extends Either
        {

            @Override
            public Object getOrThrow() throws InterruptedException, ExecutionException
            {
                throw new ExecutionException("Response not set", new NullPointerException("Response not set"));
            }
        }


        protected static class ExecutionEx<T> extends Either<T>
        {

            private final Throwable cause;

            public ExecutionEx(Throwable cause)
            {
                this.cause = cause;
            }


            @Override
            public T getOrThrow() throws InterruptedException, ExecutionException
            {
                throw new ExecutionException("Failed executing request", cause);
            }
        }
    }
}
