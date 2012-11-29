package com.wixpress.hoopoe.rpc.utils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author AlexeyR
 * @since 11/29/12 1:23 PM
 */

public interface ReadOnlyMultiMap<KType, VType>
{
    public int size();

    public boolean isEmpty();

    public boolean containsKey(KType key);

    public boolean containsValue(VType value);

    public VType get(KType key);

    public Set<VType> getAll(KType key);


    public Set<KType> keySet();

    public Collection<VType> values();

    public Set<Map.Entry<KType, VType>> entrySet();

}
