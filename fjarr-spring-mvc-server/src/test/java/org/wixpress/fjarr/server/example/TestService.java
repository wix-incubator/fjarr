package org.wixpress.fjarr.server.example;

/**
 * @author alex
 * @since 12/24/12 3:39 PM
 */

public interface TestService
{
    ReturnObject getData();

    ReturnObject getDataWithParam(ParamObject p);
}
