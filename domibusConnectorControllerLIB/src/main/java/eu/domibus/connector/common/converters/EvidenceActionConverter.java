/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.common.converters;

import eu.domibus.connector.domain.configuration.EvidenceActionServiceConfigurationProperties;
import org.springframework.core.convert.converter.Converter;

/**
 * The EvidenceActionConverter class is a Converter that converts a String to an
 * EvidenceActionServiceConfigurationProperties.AS4Action object.
 */
public class EvidenceActionConverter
    implements Converter<String, EvidenceActionServiceConfigurationProperties.AS4Action> {
    @Override
    public EvidenceActionServiceConfigurationProperties.AS4Action convert(String source) {
        if (source == null) {
            return null;
        }
        return new EvidenceActionServiceConfigurationProperties.AS4Action(source);
    }
}
