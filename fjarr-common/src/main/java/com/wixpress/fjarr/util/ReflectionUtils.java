package com.wixpress.fjarr.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author AlexeyR
 * @since 11/25/12 3:28 PM
 */

public class ReflectionUtils
{
    private static Map<ClassMethod, List<Method>> reflectionCache = new HashMap<ClassMethod, List<Method>>();
    /**
     * Attempt to find all {@link java.lang.reflect.Method} on the supplied class with the supplied name.
     * Searches all superclasses up to <code>Object</code>. The returned list is constructed in the following order:
     * first the methods in the supplied class, and then methods in the superclass and so on.
     * <p>Returns an empty <code>List</code> if no {@link java.lang.reflect.Method} can be found.
     *
     * @param clazz the class to introspect
     * @param name  the name of the method
     * @return the Method object, or <code>null</code> if none found
     */
    public static List<Method> findMethods(Class<?> clazz, String name)
    {
        ClassMethod cacheKey = new ClassMethod(clazz,name);
        if (reflectionCache.containsKey(cacheKey))
            return reflectionCache.get(cacheKey);

        Class<?> searchType = clazz;
        List<Method> ret = new ArrayList<Method>();
        while (searchType != null)
        {
            Method[] methods = (searchType.isInterface() ? searchType.getMethods() : searchType.getDeclaredMethods());
            for (Method method : methods)
            {
                if (name.equals(method.getName()))
                {
                    ret.add(method);
                }
            }
            searchType = searchType.getSuperclass();
        }

        reflectionCache.put(cacheKey, ret);
        return ret;
    }

    private final static class ClassMethod
    {
        public Class<?> clazz;
        public String method;

        private ClassMethod(Class<?> clazz, String method)
        {
            this.clazz = clazz;
            this.method = method;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ClassMethod that = (ClassMethod) o;

            if (clazz != null ? !clazz.equals(that.clazz) : that.clazz != null) return false;
            if (method != null ? !method.equals(that.method) : that.method != null) return false;

            return true;
        }

        @Override
        public int hashCode()
        {
            int result = clazz != null ? clazz.hashCode() : 0;
            result = 31 * result + (method != null ? method.hashCode() : 0);
            return result;
        }
    }
}
