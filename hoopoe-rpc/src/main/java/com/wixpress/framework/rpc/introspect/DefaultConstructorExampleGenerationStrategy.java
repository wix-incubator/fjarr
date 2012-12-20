package com.wixpress.framework.rpc.introspect;

import com.wixpress.fjarr.reflection.genericTypes.GenericType;

import java.lang.reflect.Constructor;

/**
 * @author shaiyallin
 * @since 1/3/12
 */
public class DefaultConstructorExampleGenerationStrategy implements ExampleGenerationStrategy {

    @SuppressWarnings("unchecked")
    @Override
    public <T> T exampleFor(GenericType type) {
        try {
            Constructor<T> defaultCtor = (Constructor<T>) type.getRawClass().getConstructor();
            if (defaultCtor != null) {
                return defaultCtor.newInstance();
            }
        } catch (Exception e) {
            // fallback
        }

        return null;
    }
}
