package com.wixpress.framework.rpc.json;


import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 8/31/11
 *
 * @Deprecated Use @see HoopoeRpcJacksonModule instead
 */
@Deprecated
public abstract class ObjectMapperConfigurer
{
    public static void configureObjectMapper(ObjectMapper mapper)
    {
        mapper.addMixInAnnotations(Throwable.class, ExceptionJsonMixin.class);


        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        mapper.addMixInAnnotations(Interval.class, JodaIntervalMixin.class);

        SimpleModule module = new SimpleModule(ObjectMapperConfigurer.class.getPackage().getName(), new Version(1, 0, 0, null));
        module.addDeserializer(DateTime.class, new TimeZoneAwareDateTimeDeserializer());
        mapper.registerModule(module);
    }
}
