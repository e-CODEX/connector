package eu.domibus.connector.common.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ClassPathResource;


public class ClasspathResourceToStringConverter implements Converter<ClassPathResource, String> {
    @Override
    public String convert(ClassPathResource source) {
        return "classpath:" + source.getPath();
    }
}
