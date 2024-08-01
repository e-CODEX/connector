/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.common.converters;

import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import org.springframework.core.convert.converter.Converter;

/**
 * The BusinessDomainIdConverter class is a converter implemented using the Spring Converter
 * interface. It converts a String value to a BusinessDomainId object from the
 * DomibusConnectorBusinessDomain class.
 */
public class BusinessDomainIdConverter
    implements Converter<String, DomibusConnectorBusinessDomain.BusinessDomainId> {
    @Override
    public DomibusConnectorBusinessDomain.BusinessDomainId convert(String source) {
        return new DomibusConnectorBusinessDomain.BusinessDomainId(source);
    }
}
