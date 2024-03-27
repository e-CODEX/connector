package eu.domibus.connector.common.converters;

import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import org.springframework.core.convert.converter.Converter;


public class BusinessDomainIdConverter implements Converter<String, DomibusConnectorBusinessDomain.BusinessDomainId> {
    @Override
    public DomibusConnectorBusinessDomain.BusinessDomainId convert(String source) {
        if (source == null) {
            return null;
        }
        return new DomibusConnectorBusinessDomain.BusinessDomainId(source);
    }
}
