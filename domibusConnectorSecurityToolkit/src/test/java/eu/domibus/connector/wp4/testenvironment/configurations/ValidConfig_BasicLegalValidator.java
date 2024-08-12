/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.wp4.testenvironment.configurations;

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
