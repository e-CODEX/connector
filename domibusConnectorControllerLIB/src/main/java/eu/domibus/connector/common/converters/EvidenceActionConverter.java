package eu.domibus.connector.common.converters;

import eu.domibus.connector.domain.configuration.EvidenceActionServiceConfigurationProperties;
import org.springframework.core.convert.converter.Converter;


public class EvidenceActionConverter implements Converter<String,
        EvidenceActionServiceConfigurationProperties.AS4Action> {
    @Override
    public EvidenceActionServiceConfigurationProperties.AS4Action convert(String source) {
        if (source == null) {
            return null;
        }
        return new EvidenceActionServiceConfigurationProperties.AS4Action(source);
    }
}
