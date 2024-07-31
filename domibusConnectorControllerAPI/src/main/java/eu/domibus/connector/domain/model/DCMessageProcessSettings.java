/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.domain.model;

import eu.domibus.connector.domain.enums.AdvancedElectronicSystemType;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The `DCMessageProcessSettings` class represents the settings for processing a DC (Domibus
 * Connector) message.
 */
@Data
@NoArgsConstructor
public class DCMessageProcessSettings {
    private AdvancedElectronicSystemType validationServiceName;
}
