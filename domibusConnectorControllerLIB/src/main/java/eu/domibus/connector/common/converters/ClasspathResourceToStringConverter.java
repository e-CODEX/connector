/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.common.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ClassPathResource;

/**
 * Converts a ClassPathResource object to a String representation. The converted string will have
 * the format "classpath".
 */
public class ClasspathResourceToStringConverter implements Converter<ClassPathResource, String> {
    @Override
    public String convert(ClassPathResource source) {
        return "classpath:" + source.getPath();
    }
}
