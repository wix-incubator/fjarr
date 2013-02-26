package org.wixpress.fjarr.server.example;

import javax.validation.constraints.NotNull;

/**
 * @author alex
 * @since 12/24/12 3:40 PM
 */

public class ParamObject
{
    @NotNull
    public String s;

    public ParamObject(String s)
    {
        this.s = s;
    }
}
