package com.wixpress.framework.rpc.introspect.javadoc;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * This service does something good to the world
 */
public interface SomeService
{
    /**
     * Returns the user age
     * @param userName name of the user
     * @return age
     * @throws java.util.NoSuchElementException if no user exists with the given name
     */
    Integer getAge(String userName, Double someParamWithoutComment) throws NoSuchElementException;

    /**
     * Increases something
     * @throws IllegalStateException if something cannot be increased
     */

    void increase() throws IllegalStateException;

    /**
     * Some weird method parameters
     * @param mapOfIntegers a map
     */
    List<? extends StringBuilder> someWeirdTypes(Map<String, Integer> mapOfIntegers);
}
