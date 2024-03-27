package eu.domibus.connector.common.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.FileSystemResource;


public class FileResourceToStringConverter implements Converter<FileSystemResource, String> {
    @Override
    public String convert(FileSystemResource source) {
        return "file:" + source.getPath();
    }
}
