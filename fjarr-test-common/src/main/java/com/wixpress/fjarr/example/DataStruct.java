package com.wixpress.fjarr.example;

import java.io.Serializable;
import java.util.*;

/**
 * @author alexeyr
 * @since Oct 4, 2010 11:01:47 AM
 */

public class DataStruct implements Serializable
{
    int anInt;
    String string;
    double dbl;
    UUID uuid;

    Map<Integer, DataStructChild> map = new HashMap<Integer, DataStructChild>();
    List<DataStructChild> list = new ArrayList<DataStructChild>();
    Set<DataStructChild> set = new HashSet<DataStructChild>();

    public DataStruct()
    {
    }

    public DataStruct(int anInt, String string, double dbl, UUID uuid)
    {
        this.anInt = anInt;
        this.string = string;
        this.dbl = dbl;
        this.uuid = uuid;
    }

    public int getAnInt()
    {
        return anInt;
    }

    public void setAnInt(int anInt)
    {
        this.anInt = anInt;
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

        if (anInt != that.anInt) return false;
        if (Double.compare(that.dbl, dbl) != 0) return false;
        if (list != null ? !list.equals(that.list) : that.list != null) return false;
        if (map != null ? !map.equals(that.map) : that.map != null) return false;
        if (set != null ? !set.equals(that.set) : that.set != null) return false;
        if (string != null ? !string.equals(that.string) : that.string != null) return false;
        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = anInt;
        result = 31 * result + (string != null ? string.hashCode() : 0);
        temp = Double.doubleToLongBits(dbl);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
        result = 31 * result + (map != null ? map.hashCode() : 0);
        result = 31 * result + (list != null ? list.hashCode() : 0);
        result = 31 * result + (set != null ? set.hashCode() : 0);
        return result;
    }

    void populateMap() {
        for (int i = 0; i < 10; i++) {
            getMap().put(i, new DataStructChild(Integer.toString(i), i));
        }
    }

    void populateList() {
        for (int i = 0; i < 10; i++) {
            getList().add(new DataStructChild(Integer.toString(i), i));
        }
    }

    void populateSet() {
        for (int i = 0; i < 10; i++) {
            getSet().add(new DataStructChild(Integer.toString(i), i));
        }
    }
}

