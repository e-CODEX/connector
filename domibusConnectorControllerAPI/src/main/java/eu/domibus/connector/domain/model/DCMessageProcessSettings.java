/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
