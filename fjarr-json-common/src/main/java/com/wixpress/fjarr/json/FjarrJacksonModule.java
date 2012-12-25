package com.wixpress.fjarr.json;


import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.joda.deser.PeriodDeserializer;
import com.fasterxml.jackson.datatype.joda.ser.DateTimeSerializer;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 6/5/12
 */
public class FjarrJacksonModule extends SimpleModule
{
    private static final String MODULE_NAME = FjarrJacksonModule.class.getName();
    private static final Version MODULE_VERSION = new Version(1, 0, 0, null, "com.wixpress.fjarr","fjarr-json-common" );

    public FjarrJacksonModule()
    {
        super(MODULE_NAME, MODULE_VERSION);

        setMixInAnnotation(Throwable.class, ExceptionJsonMixin.class);
        setMixInAnnotation(Interval.class, JodaIntervalMixin.class);

        addDeserializer(DateTime.class, new TimeZoneAwareDateTimeDeserializer());

        addDeserializer(Period.class, new PeriodDeserializer());
        addSerializer(Period.class, ToStringSerializer.instance);
        addSerializer(DateTime.class, new DateTimeSerializer());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setupModule(SetupContext context)
    {
        super.setupModule(context);
        context.<ObjectMapper>getOwner().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
}
