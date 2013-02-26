package org.wixpress.fjarr.server.example;

/**
 * @author alex
 * @since 12/24/12 3:43 PM
 */

public class TestServiceImpl implements TestService
{
    @Override
    public ReturnObject getData()
    {
        return new ReturnObject(10, "aaaa");
    }

    @Override
    public ReturnObject getDataWithParam(ParamObject p)
    {
        return new ReturnObject(20, p.s);
    }
}
