package com.wixpress.fjarr.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wixpress.fjarr.it.util.ITServer;
import com.wixpress.fjarr.client.RpcClientProtocol;
import com.wixpress.fjarr.client.RpcClientProxy;
import com.wixpress.fjarr.client.RpcInvoker;
import com.wixpress.fjarr.example.*;
import com.wixpress.fjarr.json.FjarrJacksonModule;
import org.junit.Before;
import org.junit.Test;

import java.net.URISyntaxException;
import java.util.*;

import static com.wixpress.fjarr.example.DataStruct.aDataStructWithAllCollections;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * @author alex
 * @since 1/6/13 5:25 PM
 */

public abstract class BaseContractTest {
    public static final int SERVER_PORT = 9191;
    public static final String DEFAULT_SERVICE_ROOT = "http://127.0.0.1:"+SERVER_PORT+"/DataStructService";
    protected static ITServer server;

    protected final String serviceRoot = DEFAULT_SERVICE_ROOT;
    protected DataStructService service;
    protected final RpcClientProtocol protocol = buildProtocol();
    protected final RpcInvoker invoker = buildInvoker();

    @Before
    public void setupClientSide() throws URISyntaxException {
        service = RpcClientProxy.create(DataStructService.class,
                serviceRoot,
                invoker,
                protocol);
    }

    protected abstract RpcClientProtocol buildProtocol();

    protected abstract RpcInvoker buildInvoker();

    @Test
    public void testLocalInvocationToString() {
        String s = service.toString();
        assertThat(s, containsString("RPC Proxy for"));
    }

    @Test
    public void testDataStructService() {
        DataStruct ds = service.getData();

        assertThat(ds.getString(), is("test with no children"));
        assertThat(ds.getAnInt(), is(10));
        assertThat(ds.getDbl(), is(0.0));
        assertTrue(ds.getUuid().equals(DataStructServiceImpl.TEST_UUID));
        assertEquals(DataStructServiceImpl.TEST_UUID.hashCode(), ds.getUuid().hashCode());

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
        assertThat(ds.getAnInt(), is(20));
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
    public void returnsPrimitiveValue(){
        assertEquals(1, service.getOneAsPrimitiveValue());
    }

    @Test
    public void singlePrimitiveInputIsPassedCorrectly(){
        int primitiveInput = 5;
        assertEquals(primitiveInput,service.returnsSamePrimitiveInput(primitiveInput));
    }


    @Test
    public void singleComplexInputPassedCorrectly(){
        DataStruct dataStruct = aDataStructWithAllCollections(5,"someString",2.3,UUID.randomUUID());
        assertThat(service.returnsSameDataStructInput(dataStruct),is(dataStruct));
    }


    @Test
    public void testCheckedException() {
        try {
            service.throwCheckedException();
            fail("Excption should have been thrown");
        } catch (DataStructServiceException e) {
            assertThat(e.getMessage(), is("wrapper"));
        }
    }

    @Test
    public void causeIsNotPropagatedBackToCaller() {
        try {
            service.throwCheckedException();
            fail("Excption should have been thrown");
        } catch (DataStructServiceException e) {
            assertNull(e.getCause());
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

    @Test(expected = DataStructServiceRuntimeException.class)
    public void testRuntimeException() {
        service.throwRuntimeException();
    }


    @Test(expected = NullPointerException.class)
    public void testNPE() {
        service.throwNPE();
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
