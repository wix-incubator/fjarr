package com.wixpress.fjarr.json;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 8/28/11
 */
public class TimeZoneAwareDateTimeDeserializer extends StdScalarDeserializer<DateTime>
{
    public TimeZoneAwareDateTimeDeserializer()
    {
        super(DateTime.class);
    }

    @Override
    public DateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException
    {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_NUMBER_INT)
        {
            return new DateTime(jp.getLongValue(), DateTimeZone.UTC);
        }
        if (t == JsonToken.VALUE_STRING)
        {
            String str = jp.getText().trim();
            if (str.length() == 0)
            {
                return null;                // [JACKSON-360]
            }
            return new DateTime(str);       // Take TimeZone portion from str
        }
        throw ctxt.mappingException(getValueClass());
    }
}
