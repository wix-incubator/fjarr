package com.wixpress.fjarr.it;

import com.wixpress.fjarr.client.RpcClientProxy;
import com.wixpress.fjarr.client.exceptions.RpcTransportException;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;

import static com.wixpress.fjarr.it.factories.FjarrObjectMapperFactory.anObjectMapperWithFjarrModule;
import static com.wixpress.fjarr.it.factories.HttpComponentsInvokerFactory.aDefaultHttpComponentsInvoker;
import static com.wixpress.fjarr.it.factories.JsonRPCClientProtocolFactory.aJsonRpcClientProtocolFrom;
import static com.wixpress.fjarr.it.factories.ServiceRootFactory.aServiceRootFor;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * @author ittaiz
 * @since 6/6/13
 */

public class TransportErrorClientHandlingContractTest {
    public static final int SERVER_PORT = 9191;
    public static final String SHOULD_HAVE_THROWN_AN_RPC_TRANSPORTATION_EXCEPTION = "should have thrown an RpcTransportationException";
    private static Server server;
    private static ResponseAction responseAction;
    private final static CountDownLatch KEEP_BUSY_LATCH = new CountDownLatch(1);
    protected ReturnOneService service;

    @BeforeClass
    public static void init() throws Exception {
        server = new Server(SERVER_PORT);
        server.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                switch (responseAction) {
                    case EMPTY:
                        response.setStatus(HttpServletResponse.SC_OK);
                        break;
                    case FORBIDDEN:
                        response.sendError(HttpServletResponse.SC_FORBIDDEN);
                        break;
                    case BUSY_WAIT:
                        try {
                            KEEP_BUSY_LATCH.await();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        break;
                }
                baseRequest.setHandled(true);
            }
        });
        server.start();
    }

    @AfterClass
    public static void fini() throws Exception {
        server.stop();
    }

    @Before
    public final void setupService() throws URISyntaxException {
        service = aReturnOneServiceWith(aServiceRootFor(ReturnOneService.class, SERVER_PORT));
    }

    protected ReturnOneService aReturnOneServiceWith(String currentServiceRoot) {
        return RpcClientProxy.create(ReturnOneService.class,
                currentServiceRoot,
                aDefaultHttpComponentsInvoker(),
                aJsonRpcClientProtocolFrom(anObjectMapperWithFjarrModule()));
    }

    @Test(expected = RpcTransportException.class)
    public void ifNoServerExistsATransportErrorIsThrown() {
        service = aReturnOneServiceWith(aServiceRootFor(ReturnOneService.class, SERVER_PORT + 1));
        service.getOne();
    }

    @Test
    public void shouldThrowSocketTimeoutExceptionForLongRunningMethod() {
        responseAction = ResponseAction.BUSY_WAIT;
        try {
            service.getOne();
            fail(SHOULD_HAVE_THROWN_AN_RPC_TRANSPORTATION_EXCEPTION);
        } catch (RpcTransportException rpcTransportationException) {
            assertThat(rpcTransportationException.getCause(), instanceOf(SocketTimeoutException.class));
        } finally {
            KEEP_BUSY_LATCH.countDown();
        }
    }

    @Test
    public void shouldThrowTransportExceptionIfNotStatusOk() {
        responseAction = ResponseAction.FORBIDDEN;
        try {
            service.getOne();
            fail(SHOULD_HAVE_THROWN_AN_RPC_TRANSPORTATION_EXCEPTION);
        } catch (RpcTransportException rpcTransportException) {
            assertThat(rpcTransportException.getStatusCode(), is(HttpURLConnection.HTTP_FORBIDDEN));
        }
    }

    @Test(expected = RpcTransportException.class)
    public void shouldThrowTransportExceptionIfEmptyResponse() {
        responseAction = ResponseAction.EMPTY;
        service.getOne();
    }

    private static enum ResponseAction {
        EMPTY, FORBIDDEN, BUSY_WAIT
    }
}
