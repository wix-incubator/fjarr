package com.wixpress.framework.rpc.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wixpress.framework.rpc.json.util.SomeObject;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
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
        mapper.registerModule(new HoopoeRpcJacksonModule());

        SomeObject o = new SomeObject();
        String payload = mapper.writer().withDefaultPrettyPrinter().writeValueAsString(o);
        System.out.print(payload);
        SomeObject transfered = mapper.reader(SomeObject.class).readValue(payload);
        assertThat(transfered, is(o));
    }
}
