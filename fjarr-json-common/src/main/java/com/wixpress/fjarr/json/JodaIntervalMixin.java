package com.wixpress.fjarr.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.joda.time.DateTime;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 8/31/11
 */
@JsonIgnoreProperties({"endMillis", "startMillis", "afterNow", "beforeNow", "chronology"})
public abstract class JodaIntervalMixin
{
    @JsonCreator()
    public JodaIntervalMixin(
            @JsonProperty("start") @JsonDeserialize(using = TimeZoneAwareDateTimeDeserializer.class) DateTime start,
            @JsonProperty("end") @JsonDeserialize(using = TimeZoneAwareDateTimeDeserializer.class)DateTime end) {}
}
