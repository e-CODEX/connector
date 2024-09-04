/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.common.converters;

import eu.ecodex.connector.domain.model.DomibusConnectorBusinessDomain;
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
