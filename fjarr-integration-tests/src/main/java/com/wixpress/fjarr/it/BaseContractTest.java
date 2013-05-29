package com.wixpress.fjarr.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wixpress.fjarr.it.util.ITServer;
import com.wixpress.fjarr.client.RpcClient;
import com.wixpress.fjarr.client.RpcClientProtocol;
import com.wixpress.fjarr.client.RpcClientProxy;
import com.wixpress.fjarr.client.RpcInvoker;
import com.wixpress.fjarr.example.*;
import com.wixpress.fjarr.json.FjarrJacksonModule;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * @author alex
 * @since 1/6/13 5:25 PM
 */

public abstract class BaseContractTest {
    protected static ITServer server;

    protected DataStructService service;
    protected RpcClient client;
    protected String serviceRoot;

    @Before
    public void setupClientSide() throws URISyntaxException {
        serviceRoot = "http://127.0.0.1:9191/DataStructService";

        final RpcClientProtocol protocol = getProtocol();
        final RpcInvoker invoker = getInvoker();
        service = RpcClientProxy.create(DataStructService.class,
                serviceRoot,
                invoker,
                protocol);


        client = new RpcClient(new URI(serviceRoot), protocol, invoker);
    }

    protected abstract RpcClientProtocol getProtocol();

    protected abstract RpcInvoker getInvoker();

    @Test
    public void testLocalInvocationToString() {
        String s = service.toString();
        assertThat(s, containsString("RPC Proxy for"));
    }

    @Test
    public void testDataStructService() {
        DataStruct ds = service.getData();

        assertThat(ds.getString(), is("test with no children"));
        assertThat(ds.getIteger(), is(10));
        assertThat(ds.getDbl(), is(0.0));
        assertTrue(ds.getUuid().equals(DataStructServiceImpl.TEST_UUID));
        assertEquals(DataStructServiceImpl.TEST_UUID.hashCode(), ds.getUuid().hashCode());

    }


    @Test
    @Ignore("Fails inconsistently with java.net.SocketException: Unexpected end of file aNettyInvokerFrom server")
    public void testInvalidJsonReturnsHttpStatus400() throws Exception {
        RestTemplate template = new RestTemplate();
        String content = "{ \"some\": \"invalid request\" ]";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json-rpc");
        headers.add("Content-Length", Integer.toString(content.getBytes().length));
        HttpEntity<String> request = new HttpEntity<String>(content, headers);
        try {
            template.exchange(serviceRoot, HttpMethod.POST, request, String.class);
            fail("Exception expected here");
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        }
    }

    @Test
    public void testParams() {
        ArrayList<UUID> arr = new ArrayList<UUID>();
        UUID uuid1 = UUID.fromString("3c586170-50de-4327-bc5f-d7d09cfc758a");
        UUID uuid2 = UUID.fromString("3c586170-50de-4327-bc5f-d7d09cfc7582");
        arr.add(uuid1);
        arr.add(uuid2);

        List<DataStruct> lst = service.getDataStruct(arr);
        assertEquals(2, lst.size());
        assertEquals(uuid1, lst.get(0).getUuid());
        assertEquals(uuid1.hashCode(), lst.get(0).getUuid().hashCode());
        assertEquals(uuid2, lst.get(1).getUuid());
        assertEquals(uuid2.hashCode(), lst.get(1).getUuid().hashCode());
    }


    @Test
    public void testDataStructServiceWithMap() {
        DataStruct ds = service.getDataWithChildrenMap();
        assertThat(ds.getString(), is("test with children in map"));
        assertThat(ds.getIteger(), is(20));
        assertThat(ds.getDbl(), is(2.0));
        assertThat(ds.getMap().size(), is(10));

        checkChildren(ds.getMap());

    }


    @Test
    public void testDataStructServiceWithList() {
        DataStruct ds = service.getDataWithChildrenList();
        assertThat(ds.getList().size(), is(10));
        checkChildren(ds.getList());
    }


    @Test
    public void testDataStructServiceWithSet() {
        DataStruct ds = service.getDataWithChildrenSet();
        assertThat(ds.getSet().size(), is(10));
        checkChildren(ds.getSet());
    }

    @Test
    public void testDataStructServiceWithAll() {
        DataStruct[] dses = service.getDatasWithWithAll();

        assertThat(dses.length, is(3));

        assertThat(dses[0].getMap().size(), is(10));
        checkChildren(dses[0].getMap());

        assertThat(dses[1].getList().size(), is(10));
        checkChildren(dses[1].getList());

        assertThat(dses[2].getSet().size(), is(10));
        checkChildren(dses[2].getSet());

    }

    @Test
    public void testVoidMethod() {
        service.voidReturnType();
    }

    @Test
    public void testNullReturnValue() {
        assertThat(service.getNullDataStruct(), is(nullValue()));
    }

    @Test
    public void testCheckedException() {
        try {
            service.throwCheckedException();
            fail("Excption should have been thrown");
        } catch (DataStructServiceException e) {
            assertThat(e.getMessage(), is("wrapper"));
            assertNull(e.getCause());
            //            assertNotNull(e.getCause());
            //            assertThat(e.getCause().getMessage(), is("cause"));
            //            assertTrue(e.getCause() instanceof Throwable);
            //            assertNull(e.getCause().getCause());
        }
    }


    @Test
    public void testCheckedComplexException() {
        try {
            service.throwCheckedComplexException();
            fail("Excption should have been thrown");
        } catch (DataStructServiceComplexException e) {
            assertThat(e.getMessage(), is("test"));
            assertThat(e.getI(), is(10));

            assertNull(e.getCause());
        }
    }

    @Test
    public void testRuntimeException() {
        try {
            service.throwRuntimeException();
            fail("Excption should have been thrown");
        } catch (DataStructServiceRuntimeException e) {
            // expected
        }
    }


    @Test
    public void testNPE() {
        try {
            service.throwNPE();
            fail("Excption should have been thrown");
        } catch (NullPointerException e) {
            // expected
        }
    }

    @Test
    public void testDescribe() throws Throwable {
        String response = client.invoke("aaa", "rpc.getServiceName", String.class);
        assertThat(response, is("com.wixpress.fjarr.example.DataStructService"));
    }


    private void checkChildren(Map<Integer, DataStructChild> map) {
        for (int i = 0; i < 10; i++) {
            DataStructChild dsc = map.get(i);

            assertThat(dsc.getName(), is(Integer.toString(i)));
            assertTrue(dsc.getValue() instanceof Integer);
            assertThat((Integer) dsc.getValue(), is(i));
        }
    }

    private void checkChildren(List<DataStructChild> list) {
        for (int i = 0; i < 10; i++) {
            DataStructChild dsc = list.get(i);

            assertThat(dsc.getName(), is(Integer.toString(i)));
            assertTrue(dsc.getValue() instanceof Integer);
            assertThat((Integer) dsc.getValue(), is(i));
        }
    }

    private void checkChildren(Set<DataStructChild> set) {
        HashMap<Integer, Boolean> map = new HashMap<Integer, Boolean>();
        for (DataStructChild dsc : set) {
            assertTrue(dsc.getValue() instanceof Integer);
            map.put((Integer) dsc.getValue(), true);
            assertThat((dsc.getValue()).toString(), is(dsc.getName()));
        }

        for (int i = 0; i < 10; i++) {
            assertTrue(map.containsKey(i));
            assertTrue(map.get(i));
        }
    }

    protected static ObjectMapper buildObjectMapperWithFjarrModule(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new FjarrJacksonModule());
        return mapper;
    }

}
