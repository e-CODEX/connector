/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.wp4.testenvironment.configurations;

import eu.domibus.connector.wp4.testenvironment.nationaldummyimplementations.National_LegalValidationService;
import eu.ecodex.dss.service.ECodexLegalValidationService;

/**
 * The InvalidConfig_NationalLegalValidator class provides static methods to obtain an instance of
 * the ECodexLegalValidationService that represents various invalid configurations .
 *
 * <p>This class is used in test cases to simulate different scenarios of invalid national legal
 * validation.
 */
// SUB-CONF-11
@SuppressWarnings("checkstyle:TypeName")
public class InvalidConfig_NationalLegalValidator {
    /**
     * Returns a {@code ECodexLegalValidationService} with an invalid configuration that represents
     * a null result. This method is used in test cases to simulate a scenario where the national
     * legal validation service returns a null result.
     *
     * @return a {@code ECodexLegalValidationService} with an invalid configuration representing a
     *      null result
     */
    // SUB-CONF-11 Variant 1
    public static ECodexLegalValidationService get_NationalLegalValidator_NullResult() {
        var workingService = new National_LegalValidationService();
        workingService.setInvalid_NullResult();

        return workingService;
    }

    /**
     * Returns an instance of ECodexLegalValidationService that represents an invalid configuration
     * with an empty result. This method is used in test cases to simulate a scenario where the
     * national legal validation service returns an empty result.
     *
     * @return an instance of ECodexLegalValidationService with an invalid configuration
     *      representing an empty result
     */
    // SUB-CONF-11 Variant 2
    public static ECodexLegalValidationService get_NationalLegalValidator_EmptyResult() {
        var workingService = new National_LegalValidationService();
        workingService.setInvalid_EmptyResult();

        return workingService;
    }

    /**
     * Returns an instance of ECodexLegalValidationService that represents an invalid configuration
     * with a missing trust level. This method is used in test cases to simulate a scenario where
     * the national legal validation service returns a validation result with a null trust level.
     *
     * @return an instance of ECodexLegalValidationService with an invalid configuration
     *      representing a missing trust level
     */
    // SUB-CONF-11 Variant 3
    public static ECodexLegalValidationService get_NationalLegalValidator_MissingTrustLevel() {
        var workingService = new National_LegalValidationService();
        workingService.setInvalid_MissingTrustLevel();

        return workingService;
    }
}
