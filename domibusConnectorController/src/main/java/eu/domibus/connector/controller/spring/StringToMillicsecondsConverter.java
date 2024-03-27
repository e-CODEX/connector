package eu.domibus.connector.controller.spring;

import eu.domibus.connector.lib.spring.DomibusConnectorDuration;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


/**
 * This class provides a conversion service from String to DomibusConnectorDuration
 */
@Component
@ConfigurationPropertiesBinding
public class StringToMillicsecondsConverter implements Converter<String, DomibusConnectorDuration> {
    @Override
    public DomibusConnectorDuration convert(String source) {
        return DomibusConnectorDuration.valueOf(source);
    }
}
