package org.wixpress.fjarr.json;

import org.wixpress.fjarr.server.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author alex
 * @since 1/16/13 1:26 PM
 */

public class JsonRpcExtensionMethodExecutor extends BaseRpcRequestLifecycleEventHandler
{

    Map<String, JsonRpcExtensionMethodWrapper> extensionMethods = new HashMap<String, JsonRpcExtensionMethodWrapper>();

    public JsonRpcExtensionMethodExecutor(JsonRpcExtensionMethod... methods)
    {
        for (JsonRpcExtensionMethod method : methods)
        {
            try
            {
                final String methodName = method.getMethodName();
                if (!methodName.startsWith("rpc."))
                    throw new IllegalArgumentException("All json-rpc extension methods MUST start with \"rpc.\"");
                extensionMethods.put(methodName, new JsonRpcExtensionMethodWrapper(method));
            }
            catch (NoSuchMethodException e)
            {
                // should never happen
            }
        }
    }

    @Override
    public LifecycleEventFlow handleRpcInvocationMethodResolving(ParsedRpcRequest request, RpcResponse response, RpcInvocation invocation)
    {
        final String methodName = invocation.getMethodName();
        if (methodName.startsWith("rpc.") && extensionMethods.containsKey(methodName))
        {
            try
            {
                final JsonRpcExtensionMethodWrapper extensionMethod = extensionMethods.get(methodName);
                Object result = extensionMethod.method.invoke();
                invocation.setInvocationResult(result);
                invocation.setResolvedMethod(extensionMethod.reflectedMethod);
                invocation.setResolvedParameters(new Object[0]);

                return LifecycleEventFlow.stopRequest();
            }
            catch (Exception e)
            {
                invocation.setError(e);
            }
        }

        return LifecycleEventFlow.proceed();
    }


    private static class JsonRpcExtensionMethodWrapper
    {
        public final JsonRpcExtensionMethod method;
        public final Method reflectedMethod;


        private JsonRpcExtensionMethodWrapper(JsonRpcExtensionMethod method) throws NoSuchMethodException
        {
            this.method = method;
            reflectedMethod = method.getClass().getMethod("invoke", new Class[0]);
        }
    }
}
