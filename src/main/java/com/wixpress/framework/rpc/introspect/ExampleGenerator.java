package com.wixpress.framework.rpc.introspect;

import com.wixpress.hoopoe.reflection.genericTypes.GenericType;

import java.util.List;

/**
 * @author shaiyallin
 * @since 1/3/12
 */
public class ExampleGenerator {


    private final List<ExampleGenerationStrategy> exampleGenerationStrategies;

    public ExampleGenerator(List<ExampleGenerationStrategy> exampleGenerationStrategies) {
        this.exampleGenerationStrategies = exampleGenerationStrategies;
    }

    public Object generateExampleFor(GenericType type) {


        // try exact matches for the specified type
        for (ExampleGenerationStrategy strategy : exampleGenerationStrategies) {
            Object example = strategy.exampleFor(type);
            if (example != null) {
                return example;
            }
        }

        return "null; // failed to generate example for parameter of type " + type;

    }

}
