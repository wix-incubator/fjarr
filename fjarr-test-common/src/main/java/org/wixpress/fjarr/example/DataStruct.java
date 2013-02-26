package org.wixpress.fjarr.example;

import java.io.Serializable;
import java.util.*;

/**
 * @author alexeyr
 * @since Oct 4, 2010 11:01:47 AM
 */

public class DataStruct implements Serializable
{
    int iteger;
    String string;
    double dbl;
    UUID uuid;

    Map<Integer, DataStructChild> map = new HashMap<Integer, DataStructChild>();
    List<DataStructChild> list = new ArrayList<DataStructChild>();
    Set<DataStructChild> set = new HashSet<DataStructChild>();

    public DataStruct()
    {
    }

    public DataStruct(int iteger, String string, double dbl, UUID uuid)
    {
        this.iteger = iteger;
        this.string = string;
        this.dbl = dbl;
        this.uuid = uuid;
    }

    public int getIteger()
    {
        return iteger;
    }

    public void setIteger(int iteger)
    {
        this.iteger = iteger;
    }

    public String getString()
    {
        return string;
    }

    public void setString(String string)
    {
        this.string = string;
    }

    public double getDbl()
    {
        return dbl;
    }

    public void setDbl(double dbl)
    {
        this.dbl = dbl;
    }

    public Map<Integer, DataStructChild> getMap()
    {
        return map;
    }

    public List<DataStructChild> getList()
    {
        return list;
    }

    public Set<DataStructChild> getSet()
    {
        return set;
    }

    public UUID getUuid()
    {
        return uuid;
    }

    public void setUuid(UUID uuid)
    {
        this.uuid = uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataStruct that = (DataStruct) o;

        if (Double.compare(that.dbl, dbl) != 0) return false;
        if (iteger != that.iteger) return false;
        if (string != null ? !string.equals(that.string) : that.string != null) return false;
        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = iteger;
        result = 31 * result + (string != null ? string.hashCode() : 0);
        temp = dbl != +0.0d ? Double.doubleToLongBits(dbl) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
        return result;
    }
}

