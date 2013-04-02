package com.wixpress.fjarr.server;

/**
 * @author alex
 * @since 12/23/12 4:45 PM
 */

public class LifecycleEventFlow
{


    /**
     * Proceed to the next EventHandler
     *
     * @return Proceed
     */
    public static LifecycleEventFlow proceed()
    {
        return new Proceed();
    }

    /**
     * Don't proceed to the next event handler, but continue with request execution
     *
     * @return StopEvent
     */
    public static LifecycleEventFlow stopEvent()
    {
        return new StopEvent();
    }

    /**
     * Stop request execution. The event handler should write all the appropriate data to the RpcResponse before
     * returning this
     *
     * @return StopRequest
     */
    public static LifecycleEventFlow stopRequest()
    {
        return new StopRequest();
    }

    /**
     * Throw a RuntimeException. The exception will be handled elsewhere.
     *
     * @param exception a RuntimeException
     * @return Throw
     */
    public static LifecycleEventFlow raise(RuntimeException exception)
    {
        return new Throw(exception);
    }


    public boolean isProceed()
    {
        return false;
    }

    public static class Throw extends LifecycleEventFlow
    {
        private RuntimeException exception;

        public Throw(RuntimeException exception)
        {
            this.exception = exception;
        }

        public void raise() throws RuntimeException
        {
            throw exception;
        }
    }

    public static class Proceed extends LifecycleEventFlow
    {
        @Override
        public boolean isProceed()
        {
            return true;
        }
    }

    public static class StopEvent extends LifecycleEventFlow
    {

    }


    public static class StopRequest extends LifecycleEventFlow
    {

    }


}
