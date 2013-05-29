package com.wixpress.fjarr.IT.springmvc;

import com.wixpress.fjarr.IT.BaseItTest;
import com.wixpress.fjarr.IT.util.ITSpringServer;
import com.wixpress.fjarr.client.*;
import com.wixpress.fjarr.client.exceptions.RpcInvocationException;
import com.wixpress.fjarr.example.InputDTO;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.wixpress.fjarr.IT.HttpComponentsInvokerFactory.aDefaultHttpComponentsInvoker;
import static com.wixpress.fjarr.IT.JsonRPCClientProtocolFactory.aJsonRpcClientProtocolFrom;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author alex
 * @since 1/6/13 5:29 PM
 */

public class SpringMvcWithHttpClientTest extends BaseItTest {

    @BeforeClass
    public static void init() throws Exception {
        server = new ITSpringServer(9191, ITServerConfig.class);
        server.start();
    }

    @AfterClass
    public static void fini() throws Exception {
        server.stop();
    }


    @Test
    public void testValidationSuccess() {
        service.withInputThatNeedsValidation(new InputDTO(""));
    }

    @Test
    public void testValidationFailure() {
        try {
            service.withInputThatNeedsValidation(new InputDTO());
        } catch (RpcInvocationException e) {
            assertThat(e.getMessage(), is("JSON-RPC Error -32603: \"Validation failed\""));
        }
    }

    @Override
    protected RpcClientProtocol getProtocol() {
        return aJsonRpcClientProtocolFrom(buildObjectMapperWithFjarrModule());
    }

    @Override
    protected RpcInvoker getInvoker() {
        return aDefaultHttpComponentsInvoker();
    }
}
