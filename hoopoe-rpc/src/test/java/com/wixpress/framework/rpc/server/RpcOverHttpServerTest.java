package com.wixpress.framework.rpc.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.wixpress.framework.util.ReflectionUtils;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * @author shaiy
 * @author matand
 * @since 7/3/12
 */
public class RpcOverHttpServerTest {
    
    RpcOverHttpProtocol protocol = mock(RpcOverHttpProtocol.class);
    ServiceInterfaceImpl impl = new ServiceInterfaceImpl();
    RpcOverHttpServer server = new RpcOverHttpServer(protocol, impl, ServiceInterface.class);
    
    @Test
    public void invoke() {
        String methodName = "call";
        String param = "param";
        
        List<Method> methods = ReflectionUtils.findMethods(ServiceInterface.class, methodName);
        
        JsonNode[] params = new JsonNode[]{new TextNode(param)};
        RpcParameters<?> parameters = new PositionalRpcParameters(params);
        RpcInvocation invocation = new RpcInvocation(methodName, parameters);
        invocation.setResolvedMethod(methods.get(0));
        invocation.setResolvedParameters(new Object[] {param});
        
        server.invokeMethod(mock(HttpServletRequest.class), mock(HttpServletResponse.class), mock(RpcRequest.class), invocation);
        
        assertEquals(invocation.getResult(), param);
    }
    
    static interface ServiceInterface {
        public String call(String param);
    }
    
    static class ServiceInterfaceImpl implements ServiceInterface {
        @Override
        public String call(String param) {
            return param;
        }
    }
}
