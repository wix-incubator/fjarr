package com.wixpress.fjarr.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wixpress.fjarr.json.FjarrJacksonModule;
import com.wixpress.hoopoe.reflection.parameters.AnnotationParameterNameDiscoverer;
import com.wixpress.fjarr.json.JsonRpcProtocol;
import com.wixpress.fjarr.server.util.MockHttpServletRequest;
import com.wixpress.fjarr.server.util.MockHttpServletResponse;
import com.wixpress.fjarr.example.*;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * @author AlexeyR
 * @since 12/3/12 5:02 PM
 */

public class RpcTest
{


    private static RpcServlet servlet;
    private static ObjectMapper objectMapper = new ObjectMapper();

    @BeforeClass
    public static void init()
    {
        objectMapper.registerModule(new FjarrJacksonModule());

        servlet = new RpcServlet(
                new RpcServer(
                        new JsonRpcProtocol(
                                objectMapper,
                                new AnnotationParameterNameDiscoverer()),
                        new DataStructServiceImpl(),
                        DataStructService.class));
    }

    @Test
    public void test() throws IOException, ExecutionException, InterruptedException, ServletException
    {
        HttpServletRequest request = mockJsonRpcRequest("getData");
        MockHttpServletResponse response = new MockHttpServletResponse();

        servlet.doPost(request, response);

        assertThat(response.getStatusCode(), is(200));
        DataStruct ds = parseJsonSingleResponse(response.getBody());

        assertThat(ds.getString(), is("test with no children"));
        assertThat(ds.getIteger(), is(10));
        assertThat(ds.getDbl(), is(0.0));
        assertTrue(ds.getUuid().equals(DataStructServiceImpl.TEST_UUID));
        assertEquals(DataStructServiceImpl.TEST_UUID.hashCode(), ds.getUuid().hashCode());

    }


    @Test

    public void testInvalidJsonReturnsHttpStatus400() throws Exception
    {
        String body = "{ \"some\": \"invalid request\" ]";
        HttpServletRequest request = MockHttpServletRequest.post()
                .withHeader("Host", "www.example.com")
                .withHeader("Content-Type", "application/json-rpc")
                .withBody(body)
                .build();

        MockHttpServletResponse response = new MockHttpServletResponse();

        servlet.doPost(request, response);
        assertThat(response.getStatusCode(), is(400));
    }


    @Test
    public void testParams() throws IOException, ExecutionException, InterruptedException, ServletException
    {
        ArrayList<UUID> arr = new ArrayList<UUID>();
        UUID uuid1 = UUID.fromString("3c586170-50de-4327-bc5f-d7d09cfc758a");
        UUID uuid2 = UUID.fromString("3c586170-50de-4327-bc5f-d7d09cfc7582");
        arr.add(uuid1);
        arr.add(uuid2);
        HttpServletRequest request = mockJsonRpcRequest("getDataStruct", arr);
        MockHttpServletResponse response = new MockHttpServletResponse();

        servlet.doPost(request, response);

        assertThat(response.getStatusCode(), is(200));
        List<DataStruct> lst = parseJsonListResponse(response.getBody());

        assertEquals(2, lst.size());
        assertEquals(uuid1, lst.get(0).getUuid());
        assertEquals(uuid1.hashCode(), lst.get(0).getUuid().hashCode());
        assertEquals(uuid2, lst.get(1).getUuid());
        assertEquals(uuid2.hashCode(), lst.get(1).getUuid().hashCode());

    }

    @Test
    public void testDataStructServiceWithMap() throws IOException, ServletException
    {

        HttpServletRequest request = mockJsonRpcRequest("getDataWithChildrenMap");
        MockHttpServletResponse response = new MockHttpServletResponse();

        servlet.doPost(request, response);


        DataStruct ds = parseJsonSingleResponse(response.getBody());
        assertThat(ds.getString(), is("test with children in map"));
        assertThat(ds.getIteger(), is(20));
        assertThat(ds.getDbl(), is(2.0));
        assertThat(ds.getMap().size(), is(10));

        AsserChildren(ds.getMap());

    }

    @Test
    public void testDataStructServiceWithList() throws IOException, ServletException
    {

        HttpServletRequest request = mockJsonRpcRequest("getDataWithChildrenList");
        MockHttpServletResponse response = new MockHttpServletResponse();

        servlet.doPost(request, response);


        DataStruct ds = parseJsonSingleResponse(response.getBody());

        assertThat(ds.getList().size(), is(10));
        AsserChildren(ds.getList());
    }


    @Test
    public void testDataStructServiceWithSet() throws IOException, ServletException
    {
        HttpServletRequest request = mockJsonRpcRequest("getDataWithChildrenSet");
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doPost(request, response);

        DataStruct ds = parseJsonSingleResponse(response.getBody());

        assertThat(ds.getSet().size(), is(10));
        AsserChildren(ds.getSet());
    }

    @Test
    public void testDataStructServiceWithAll() throws IOException, ServletException
    {
        HttpServletRequest request = mockJsonRpcRequest("getDatasWithWithAll");
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doPost(request, response);

        List<DataStruct> dses = parseJsonListResponse(response.getBody());

        assertThat(dses.size(), is(3));

        assertThat(dses.get(0).getMap().size(), is(10));
        AsserChildren(dses.get(0).getMap());

        assertThat(dses.get(1).getList().size(), is(10));
        AsserChildren(dses.get(1).getList());

        assertThat(dses.get(2).getSet().size(), is(10));
        AsserChildren(dses.get(2).getSet());
    }


    @Test
    public void testCheckedComplexException() throws IOException, ServletException
    {
        try
        {
            HttpServletRequest request = mockJsonRpcRequest("throwCheckedComplexException");
            MockHttpServletResponse response = new MockHttpServletResponse();
            servlet.doPost(request, response);

            parseAndThrowExceptionFromResponse(response.getBody());

            fail("Exception should have been thrown");
        }
        catch (Exception e)
        {
            assertThat(e, instanceOf(DataStructServiceComplexException.class));
            assertThat(e.getMessage(), is("test"));
            assertThat(((DataStructServiceComplexException) e).getI(), is(10));

            assertNull(e.getCause());
        }
    }

    @Test
    public void testRuntimeException() throws IOException, ServletException
    {
        try
        {
            HttpServletRequest request = mockJsonRpcRequest("throwRuntimeException");
            MockHttpServletResponse response = new MockHttpServletResponse();
            servlet.doPost(request, response);

            parseAndThrowExceptionFromResponse(response.getBody());

            fail("Exception should have been thrown");
        }
        catch (Exception e)
        {
            assertThat(e, instanceOf(DataStructServiceRuntimeException.class));
        }
    }

    @Test
    public void testNPE() throws IOException, ServletException
    {
        try
        {
            HttpServletRequest request = mockJsonRpcRequest("throwNPE");
            MockHttpServletResponse response = new MockHttpServletResponse();
            servlet.doPost(request, response);

            parseAndThrowExceptionFromResponse(response.getBody());
            fail("Exception should have been thrown");
        }

        catch (Exception e)
        {
            assertThat(e, instanceOf(NullPointerException.class));
        }
    }

    private HttpServletRequest mockJsonRpcRequest(String method, Object... params) throws IOException
    {

        StringBuilder body = new StringBuilder("{ \"id\" : 0, \"jsonrpc\" : \"2.0\",  \"method\" : \"" + method + "\",\"params\" : ");
        body.append(objectMapper.writeValueAsString(params));
        body.append("}");
        return MockHttpServletRequest.post()
                .withHeader("Host", "www.example.com")
                .withHeader("Content-Type", "application/json-rpc")
                .withBody(body.toString())
                .build();
    }

    private DataStruct parseJsonSingleResponse(String response) throws IOException
    {
        ObjectNode root = (ObjectNode) objectMapper.readTree(new StringReader(response));
        ObjectNode resp = (ObjectNode) root.get("result");
        return objectMapper.treeToValue(resp, DataStruct.class);

    }

    private DataStruct parseAndThrowExceptionFromResponse(String response) throws Exception
    {
        ObjectNode root = (ObjectNode) objectMapper.readTree(new StringReader(response));

        ObjectNode data = (ObjectNode) root.get("error").get("data");
        final ObjectReader reader = objectMapper.reader();
        throw reader.readValue(reader.treeAsTokens(data), Exception.class);
    }

    private List<DataStruct> parseJsonListResponse(String response) throws IOException
    {
        ObjectNode root = (ObjectNode) objectMapper.readTree(new StringReader(response));
        final ObjectReader reader = objectMapper.reader();
        return reader.readValue(reader.treeAsTokens(root.get("result")), objectMapper.getTypeFactory().constructCollectionType(List.class, DataStruct.class));

    }

    private void AsserChildren(Map<Integer, DataStructChild> map)
    {
        for (int i = 0; i < 10; i++)
        {
            DataStructChild dsc = map.get(i);

            assertThat(dsc.getName(), is(Integer.toString(i)));
            assertTrue(dsc.getValue() instanceof Integer);
            assertThat((Integer) dsc.getValue(), is(i));
        }
    }

    private void AsserChildren(List<DataStructChild> list)
    {
        for (int i = 0; i < 10; i++)
        {
            DataStructChild dsc = list.get(i);

            assertThat(dsc.getName(), is(Integer.toString(i)));
            assertTrue(dsc.getValue() instanceof Integer);
            assertThat((Integer) dsc.getValue(), is(i));
        }
    }

    private void AsserChildren(Set<DataStructChild> set)
    {
        HashMap<Integer, Boolean> map = new HashMap<Integer, Boolean>();
        for (DataStructChild dsc : set)
        {
            assertTrue(dsc.getValue() instanceof Integer);
            map.put((Integer) dsc.getValue(), true);
            assertThat((dsc.getValue()).toString(), is(dsc.getName()));
        }

        for (int i = 0; i < 10; i++)
        {
            assertTrue(map.containsKey(i));
            assertTrue(map.get(i));
        }
    }


}
