package com.wixpress.fjarr.it;

import com.wixpress.fjarr.client.RpcClientProtocol;
import com.wixpress.fjarr.client.RpcInvoker;

/**
* @author: ittaiz
* @since: 5/29/13
 * The compiler performs an optimization when it seen an abstract class
 *  is not used and so this class is needed to export the contract test
*/
class Dummy_Only_Exists_So_That_Base_Contract_Test_Has_Class_File extends BaseJsonContractTest {
    protected RpcClientProtocol buildProtocol() {return null;}
    protected RpcInvoker buildInvoker() {return null;}
}
