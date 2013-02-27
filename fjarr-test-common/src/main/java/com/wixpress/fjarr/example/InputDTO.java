package com.wixpress.fjarr.example;

import java.io.Serializable;

/**
 * @author Shaiy
 * @since 07/02/11 16:13
 */
public class InputDTO implements Serializable
{
    private String value;

    public InputDTO()
    {
    }

    public InputDTO(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
}
