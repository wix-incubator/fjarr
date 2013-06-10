package com.wixpress.fjarr.it;

import com.wixpress.fjarr.client.RpcClientProxy;
import com.wixpress.fjarr.client.exceptions.RpcTransportException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;

import static com.wixpress.fjarr.it.JEmbeddedJettyResponder.*;
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
    private static JEmbeddedJettyResponder server;
    protected ReturnOneService service;

    @BeforeClass
    public static void init() throws Exception {
        server = new JEmbeddedJettyResponder(SERVER_PORT);
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
        CountDownLatch busyWait = new CountDownLatch(1);
        server.respondWith(busyWaitingResponse(busyWait));
        try {
            service.getOne();
            fail(SHOULD_HAVE_THROWN_AN_RPC_TRANSPORTATION_EXCEPTION);
        } catch (RpcTransportException rpcTransportationException) {
            assertThat(rpcTransportationException.getCause(), instanceOf(SocketTimeoutException.class));
        } finally {
            busyWait.countDown();
        }
    }

    @Test
    public void shouldThrowTransportExceptionIfNotStatusOk() {
        server.respondWith(forbiddenResponse());
        try {
            service.getOne();
            fail(SHOULD_HAVE_THROWN_AN_RPC_TRANSPORTATION_EXCEPTION);
        } catch (RpcTransportException rpcTransportException) {
            assertThat(rpcTransportException.getStatusCode(), is(HttpURLConnection.HTTP_FORBIDDEN));
        }
    }

    @Test(expected = RpcTransportException.class)
    public void shouldThrowTransportExceptionIfEmptyResponse() {
        server.respondWith(emptyResponse());
        service.getOne();
    }

}
