/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
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
