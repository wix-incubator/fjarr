package org.wixpress.fjarr.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.wixpress.fjarr.client.exceptions.RpcInvocationException;
import org.wixpress.fjarr.example.DataStruct;
import org.wixpress.fjarr.example.DataStructServiceException;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JsonRpcClientProtocolTest {

    @Test
    public void testWriteRequest() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new FjarrJacksonModule());
        JsonRpcClientProtocol p = new JsonRpcClientProtocol(mapper);


        assertThat(p.writeRequest("t1", new Object[]{1, 2, 3}),is("{\"id\":0,\"jsonrpc\":\"2.0\",\"method\":\"t1\",\"params\":[1,2,3]}"));
        assertThat(p.writeRequest("t2", new Object[]{"a","b", "c"}),is("{\"id\":1,\"jsonrpc\":\"2.0\",\"method\":\"t2\",\"params\":[\"a\",\"b\",\"c\"]}"));

        final UUID uuid = UUID.randomUUID();
        DataStruct ds = new DataStruct(1,"a",0.3, uuid);
        assertThat(p.writeRequest("t", new Object[]{ds}),is("{\"id\":2,\"jsonrpc\":\"2.0\",\"method\":\"t\",\"params\":{\"iteger\":1,\"string\":\"a\",\"dbl\":0.3,\"uuid\":\""+uuid.toString()+"\",\"map\":{},\"list\":[],\"set\":[]}}"));

    }

    @Test
    public void testReadResponse()
    {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new FjarrJacksonModule());
        JsonRpcClientProtocol p = new JsonRpcClientProtocol(mapper);

        assertThat((Integer) p.readResponse(Integer.TYPE, "{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"result\":1}"), is(1));
        assertThat((Long)p.readResponse(Long.TYPE,"{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"result\":1}"),is(1l));
        assertThat((String)p.readResponse(String.class,"{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"result\":\"aaa\"}"),is("aaa"));


        final UUID uuid = UUID.randomUUID();
        DataStruct ds = new DataStruct(1,"a",0.3, uuid);
        assertThat((DataStruct) p.readResponse(DataStruct.class,
                "{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"result\":{\"iteger\":1,\"string\":\"a\",\"dbl\":0.3,\"uuid\":\""
                        + uuid.toString() + "\",\"map\":{},\"list\":[],\"set\":[]}}"),is(ds));
    }

    @Test
    public void testReadErrorResponse()
    {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new FjarrJacksonModule());
        JsonRpcClientProtocol p = new JsonRpcClientProtocol(mapper);

        try
        {
        p.readResponse(Integer.TYPE, "{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"error\":{\"code\":-32603,\"message\":\"test\",\"data\":{" +
                "\"cause\":null,\"stackTrace\":[{\"methodName\":\"testErrorResponse\",\"fileName\":\"JsonRpcProtocolParserTest.java\"," +
                "\"lineNumber\":258,\"className\":\"org.wixpress.fjarr.rpc.json.JsonRpcProtocolParserTest\",\"nativeMethod\":false}," +
                "{\"methodName\":\"invoke0\",\"fileName\":\"NativeMethodAccessorImpl.java\",\"lineNumber\":-2,\"className\":" +
                "\"sun.reflect.NativeMethodAccessorImpl\",\"nativeMethod\":true},{\"methodName\":\"invoke\",\"fileName\":\"NativeMethodAccessorImpl.java\"" +
                ",\"lineNumber\":57,\"className\":\"sun.reflect.NativeMethodAccessorImpl\",\"nativeMethod\":false},{\"methodName\"" +
                ":\"invoke\",\"fileName\":\"DelegatingMethodAccessorImpl.java\",\"lineNumber\":43,\"className\":" +
                "\"sun.reflect.DelegatingMethodAccessorImpl\",\"nativeMethod\":false},{\"methodName\":\"invoke\"," +
                "\"fileName\":\"Method.java\",\"lineNumber\":601,\"className\":\"java.lang.reflect.Method\"," +
                "\"nativeMethod\":false},{\"methodName\":\"runReflectiveCall\",\"fileName\":\"FrameworkMethod.java\",\"" +
                "lineNumber\":44,\"className\":\"org.junit.runners.model.FrameworkMethod$1\",\"nativeMethod\":false},{\"methodName\":" +
                "\"run\",\"fileName\":\"ReflectiveCallable.java\",\"lineNumber\":15,\"className\":\"org.junit.internal.runners.model.ReflectiveCallable\"" +
                ",\"nativeMethod\":false},{\"methodName\":\"invokeExplosively\",\"fileName\":\"FrameworkMethod.java\",\"lineNumber\"" +
                ":41,\"className\":\"org.junit.runners.model.FrameworkMethod\",\"nativeMethod\":false},{\"methodName\":\"evaluate\"," +
                "\"fileName\":\"InvokeMethod.java\",\"lineNumber\":20,\"className\":\"org.junit.internal.runners.statements.InvokeMethod\"," +
                "\"nativeMethod\":false},{\"methodName\":\"runChild\",\"fileName\":\"BlockJUnit4ClassRunner.java\",\"lineNumber\":76,\"className\":" +
                "\"org.junit.runners.BlockJUnit4ClassRunner\",\"nativeMethod\":false},{\"methodName\":\"runChild\",\"fileName\":" +
                "\"BlockJUnit4ClassRunner.java\",\"lineNumber\":50,\"className\":\"org.junit.runners.BlockJUnit4ClassRunner\",\"nativeMethod\":" +
                "false},{\"methodName\":\"run\",\"fileName\":\"ParentRunner.java\",\"lineNumber\":193,\"className\":" +
                "\"org.junit.runners.ParentRunner$3\",\"nativeMethod\":false},{\"methodName\":\"schedule\",\"fileName\":\"ParentRunner.java\"" +
                ",\"lineNumber\":52,\"className\":\"org.junit.runners.ParentRunner$1\",\"nativeMethod\":false},{\"methodName\":\"runChildren\"" +
                ",\"fileName\":\"ParentRunner.java\",\"lineNumber\":191,\"className\":\"org.junit.runners.ParentRunner\",\"nativeMethod\":false}" +
                ",{\"methodName\":\"access$000\",\"fileName\":\"ParentRunner.java\",\"lineNumber\":42,\"className\":\"org.junit.runners.ParentRunner\"" +
                ",\"nativeMethod\":false},{\"methodName\":\"evaluate\",\"fileName\":\"ParentRunner.java\",\"lineNumber\":184,\"className\":" +
                "\"org.junit.runners.ParentRunner$2\",\"nativeMethod\":false},{\"methodName\":\"run\",\"fileName\":\"ParentRunner.java\",\"lineNumber\"" +
                ":236,\"className\":\"org.junit.runners.ParentRunner\",\"nativeMethod\":false},{\"methodName\":\"run\",\"fileName\":\"JUnitCore.java\"" +
                ",\"lineNumber\":157,\"className\":\"org.junit.runner.JUnitCore\",\"nativeMethod\":false},{\"methodName\":\"startRunnerWithArgs\"" +
                ",\"fileName\":\"JUnit4IdeaTestRunner.java\",\"lineNumber\":76,\"className\":\"com.intellij.junit4.JUnit4IdeaTestRunner\"" +
                ",\"nativeMethod\":false},{\"methodName\":\"prepareStreamsAndStart\",\"fileName\":\"JUnitStarter.java\",\"lineNumber\":195," +
                "\"className\":\"com.intellij.rt.execution.junit.JUnitStarter\",\"nativeMethod\":false},{\"methodName\":\"main\",\"fileName\":" +
                "\"JUnitStarter.java\",\"lineNumber\":63,\"className\":\"com.intellij.rt.execution.junit.JUnitStarter\",\"nativeMethod\":false}]," +
                "\"message\":\"test\",\"localizedMessage\":\"test\",\"suppressed\":[]}}}");
        }
        catch (RpcInvocationException e)
        {
            assertThat(e.getMessage(), is("JSON-RPC Error -32603: \"test\""));
            assertThat(e.getErrorCode(), is(-32603));
        }

    }


    @Test
    public void testReadKnownErrorResponse() throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new FjarrJacksonModule());
        JsonRpcClientProtocol p = new JsonRpcClientProtocol(mapper);


        String exception = mapper.writeValueAsString(new DataStructServiceException("abc"));
        try
        {
            p.readResponse(Integer.TYPE, "{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"error\":{\"code\":-32603,\"message\":\"test\",\"data\":" +
                    exception +
                    "}}");
        }
        catch (RpcInvocationException e)
        {
            assertThat(e.getMessage(), is("abc"));
            assertThat(e.getErrorCode(), is(0));
            assertThat(e.getServerException(), CoreMatchers.instanceOf(DataStructServiceException.class));
            DataStructServiceException dse = (DataStructServiceException)e.getServerException();
            assertThat(dse.getMessage(), is("abc"));

        }

    }


}
