package com.wixpress.fjarr.json.util;

import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 * Created by IntelliJ IDEA.
 * User: daniels
 * Date: 8/31/11
 */
public class SomeObject
{
    private Interval interval = new Interval(new DateTime(1978, 12, 1, 0, 0, 0, 0), new DateTime());

    private String string = "someString";

    public Interval getInterval()
    {
        return interval;
    }

    public void setInterval(Interval interval)
    {
        this.interval = interval;
    }

    public String getString()
    {
        return string;
    }

    public void setString(String string)
    {
        this.string = string;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        SomeObject that = (SomeObject) o;

        if (interval != null ? !interval.equals(that.interval) : that.interval != null)
        {
            return false;
        }
        if (string != null ? !string.equals(that.string) : that.string != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = interval != null ? interval.hashCode() : 0;
        result = 31 * result + (string != null ? string.hashCode() : 0);
        return result;
    }
}
