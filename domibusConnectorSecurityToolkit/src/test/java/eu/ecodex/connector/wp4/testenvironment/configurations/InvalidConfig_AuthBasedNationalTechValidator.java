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

import eu.ecodex.connector.wp4.testenvironment.nationaldummyimplementations.National_Authentication_TechnicalValidationService;
import eu.ecodex.dss.service.ECodexTechnicalValidationService;

/**
 * The InvalidConfig_AuthBasedNationalTechValidator class provides static methods to retrieve
 * instances of the ECodexTechnicalValidationService interface with different invalid
 * configurations.
 */
// SUB-CONF_06
@SuppressWarnings("checkstyle:TypeName")
public class InvalidConfig_AuthBasedNationalTechValidator {
    /**
     * Retrieves an instance of ECodexTechnicalValidationService with invalid configuration where
     * the result is null.
     *
     * <p>The method creates an instance of National_Authentication_TechnicalValidationService and
     * sets the result to null. This indicates a failed validation. The method is a part of the
     * InvalidConfig_AuthBasedNationalTechValidator class and is designated as variant 1 in
     * sub-configuration 06.
     *
     * @return an instance of ECodexTechnicalValidationService with null result
     */
    // SUB-CONF_06 Variant 1
    public static ECodexTechnicalValidationService get_AuthBasedNationalTechValidator_NullResult() {
        var workingService = new National_Authentication_TechnicalValidationService();
        workingService.setInvalid_NullResult();

        return workingService;
    }

    /**
     * Returns an instance of {@link ECodexTechnicalValidationService} with an invalid configuration
     * where the result is empty.
     *
     * <p>This method creates an instance of
     * {@link National_Authentication_TechnicalValidationService} and sets the result to empty. This
     * indicates a failed validation.
     *
     * @return an instance of {@link ECodexTechnicalValidationService} with empty result
     */
    // SUB-CONF_06 Variant 2
    public static ECodexTechnicalValidationService
    get_AuthBasedNationalTechValidator_EmptyResult() {
        var workingService = new National_Authentication_TechnicalValidationService();
        workingService.setInvalid_EmptyResult();

        return workingService;
    }

    /**
     * Returns an instance of ECodexTechnicalValidationService with an invalid configuration where
     * the result is invalid.
     *
     * <p>This method creates an instance of National_Authentication_TechnicalValidationService and
     * sets the result to an invalid state. This indicates a failed validation.
     *
     * @return an instance of ECodexTechnicalValidationService with an invalid result
     */
    // SUB-CONF_06 Variant 3
    public static ECodexTechnicalValidationService
    get_AuthBasedNationalTechValidator_InvalidResult() {
        var workingService = new National_Authentication_TechnicalValidationService();
        workingService.setInvalid_InvalidResult();

        return workingService;
    }
}
