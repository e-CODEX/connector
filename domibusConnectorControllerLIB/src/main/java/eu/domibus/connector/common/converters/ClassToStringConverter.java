package eu.domibus.connector.common.converters;

import org.springframework.core.convert.converter.Converter;


public class ClassToStringConverter implements Converter<Class<?>, String> {
    @Override
    public String convert(Class<?> source) {
        return source.getName();
    }
}
