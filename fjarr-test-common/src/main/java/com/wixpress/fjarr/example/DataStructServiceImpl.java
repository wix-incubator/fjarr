package com.wixpress.fjarr.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author alexeyr
 * @since Oct 4, 2010 10:59:03 AM
 */


public class DataStructServiceImpl implements DataStructService {
    public final static UUID TEST_UUID = UUID.fromString("00000000-0000-0000-0000-000000001111");

    @Override
    public DataStruct getData() {
        return new DataStruct(10, "test with no children", 0, TEST_UUID);
    }


    @Override
    public DataStruct getDataWithChildrenMap() {
        DataStruct ds = new DataStruct(20, "test with children in map", 2., TEST_UUID);
        ds.populateMap();
        return ds;
    }

    @Override
    public DataStruct getDataWithChildrenList() {
        DataStruct ds = new DataStruct(30, "test with children in list", 3.5, TEST_UUID);
        ds.populateList();
        return ds;
    }

    @Override
    public DataStruct getDataWithChildrenSet() {
        DataStruct ds = new DataStruct(40, "test with children in set", 4.6, TEST_UUID);
        ds.populateSet();
        return ds;
    }

    @Override
    public DataStruct[] getDatasWithWithAll() {
        DataStruct[] dses = new DataStruct[3];
        dses[0] = new DataStruct(20, "test with children in map", 2., TEST_UUID);
        dses[0].populateMap();
        dses[1] = new DataStruct(30, "test with children in list", 3.5, TEST_UUID);
        dses[1].populateList();
        dses[2] = new DataStruct(40, "test with children in set", 4.6, TEST_UUID);
        dses[2].populateSet();
        return dses;
    }

    @Override
    public DataStruct throwRuntimeException() {
        try {
            throw new DataStructServiceException("cause");
        } catch (Exception e) {
            throw new DataStructServiceRuntimeException("wrapper", e);
        }
    }


    @Override
    public DataStruct throwNPE() {
        throw new NullPointerException("NPE test");
    }

    @Override
    public DataStruct throwCheckedException() throws DataStructServiceException {
        try {
            throw new IOException("cause");
        } catch (Exception e) {
            throw new DataStructServiceException("wrapper", e);
        }
    }

    @Override
    public DataStruct throwCheckedComplexException() throws DataStructServiceComplexException {
        throw new DataStructServiceComplexException("test", 10);
    }

    @Override
    public void withInputThatNeedsValidation(InputDTO dto) {
    }

    @Override
    public List<DataStruct> getDataStruct(List<UUID> ids) {
        List<DataStruct> lst = new ArrayList<DataStruct>();
        for (UUID uid : ids) {
            lst.add(new DataStruct(1, "test", 2., uid));
        }
        return lst;
    }

    @Override
    public void voidReturnType() {

    }

    @Override
    public DataStruct getNullDataStruct() {
        return null;
    }

    @Override
    public int getOneAsPrimitiveValue() {
        return 1;
    }

    @Override
    public int returnsSamePrimitiveInput(int primitiveInput) {
        return primitiveInput;
    }

}
