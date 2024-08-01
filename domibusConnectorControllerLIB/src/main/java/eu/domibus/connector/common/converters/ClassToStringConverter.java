/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.common.converters;

import org.springframework.core.convert.converter.Converter;

/**
 * The ClassToStringConverter class is an implementation of the Converter interface that converts a
 * Class object to its string representation.
 */
public class ClassToStringConverter implements Converter<Class<?>, String> {
    @Override
    public String convert(Class<?> source) {
        return source.getName();
    }
}
