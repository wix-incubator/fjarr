package com.wixpress.fjarr.example;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * @author alexeyr
 * @since Oct 4, 2010 11:01:17 AM
 */

public interface DataStructService {
    DataStruct getData();

    DataStruct getDataWithChildrenMap();

    DataStruct getDataWithChildrenList();

    DataStruct getDataWithChildrenSet();

    DataStruct[] getDatasWithWithAll();


    List<DataStruct> getDataStruct(List<UUID> ids);

    DataStruct throwRuntimeException();

    DataStruct throwNPE();

    DataStruct throwCheckedException() throws DataStructServiceException;

    DataStruct throwCheckedComplexException() throws DataStructServiceComplexException;

    public void withInputThatNeedsValidation(InputDTO dto);

    public String toString();

    void voidReturnType();

    DataStruct getNullDataStruct();

    int getOneAsPrimitiveValue();

    int returnsSamePrimitiveInput(int primitiveInput);

    DataStruct returnsSameDataStructInput(DataStruct dataStruct);

    Collection<DataStruct> returnsSameDataStructsMultipleInputs(DataStruct firstDataStruct, DataStruct secondDataStruct);

    void callLongRunningMethod(long durationToSleep);
}
