/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.wp4.testenvironment.configurations;

import eu.ecodex.dss.service.ECodexLegalValidationService;
import eu.ecodex.dss.service.impl.dss.DSSECodexLegalValidationService;

/**
 * This class represents a basic legal validator for a valid configuration. It provides a static
 * method to retrieve an instance of the {@link ECodexLegalValidationService} interface.
 *
 * @see ECodexLegalValidationService
 */
// SUB_CONF_10
@SuppressWarnings("checkstyle:TypeName")
public class ValidConfig_BasicLegalValidator {
    /**
     * Retrieves an instance of the ECodexLegalValidationService interface.
     *
     * @return An instance of the ECodexLegalValidationService interface.
     */
    // SUB_CONF_10 Variant 1
    public static ECodexLegalValidationService get_LegalValidator() {
        return new DSSECodexLegalValidationService();
    }
}
