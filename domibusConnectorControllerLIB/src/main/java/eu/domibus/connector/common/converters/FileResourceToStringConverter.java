/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.common.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.FileSystemResource;

/**
 * The FileResourceToStringConverter class is an implementation of the Converter interface that
 * converts a FileSystemResource object to its string representation.
 */
public class FileResourceToStringConverter implements Converter<FileSystemResource, String> {
    @Override
    public String convert(FileSystemResource source) {
        return "file:" + source.getPath();
    }
}
