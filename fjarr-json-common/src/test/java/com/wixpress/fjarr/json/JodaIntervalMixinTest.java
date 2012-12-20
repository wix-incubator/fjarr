package com.wixpress.fjarr.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wixpress.fjarr.json.util.SomeObject;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 8/31/11
 */
public class JodaIntervalMixinTest
{
    @Test
    public void test() throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new FjarrJacksonModule());

        SomeObject o = new SomeObject();
        String payload = mapper.writer().withDefaultPrettyPrinter().writeValueAsString(o);
        System.out.print(payload);
        SomeObject transfered = mapper.reader(SomeObject.class).readValue(payload);
        Assert.assertThat(transfered, CoreMatchers.is(o));
    }
}
