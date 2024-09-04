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
 * The ValidConfig_AuthBasedNationalTechValidator class is responsible for creating an instance of
 * the ECodexTechnicalValidationService implementation called
 * National_Authentication_TechnicalValidationService with valid configuration. It provides a method
 * get_AuthBasedNationalTechValidator() which returns an instance of the
 * National_Authentication_TechnicalValidationService with all the required data set for a valid
 * token validation.
 *
 * <p>The National_Authentication_TechnicalValidationService implements the
 * ECodexTechnicalValidationService interface, which is responsible for creating the technical
 * validation result and report on the business document using either a national implementation or
 * the one of the DSS library. The National_Authentication_TechnicalValidationService class sets the
 * values of all the data required for a valid token validation. It initializes and sets the values
 * of the TokenValidation, TechnicalValidationResult, AuthenticationInformation, and
 * ValidationVerification objects. The setValid_AllDataPresent() method is used to configure and set
 * the required data for a valid token validation. The ECodexTechnicalValidationService interface
 * provides methods for creating the technical validation result and report, as well as creating a
 * PDF document to be used as the human-readable part of the token.pdf for the validation report
 * details.
 *
 * <p>Note: The National_Authentication_TechnicalValidationService class also provides methods for
 * setting invalid data for the token validation result for testing purposes.
 */
// SUB-CONF-03
@SuppressWarnings("checkstyle:TypeName")
public class ValidConfig_AuthBasedNationalTechValidator {
    /**
     * Retrieves an instance of the {@link National_Authentication_TechnicalValidationService} with
     * all the required data set for a valid token validation.
     *
     * @return an instance of the {@link National_Authentication_TechnicalValidationService} with
     *      all the required data set for a valid token validation
     */
    // SUB-CONF-03 Variant 1
    public static ECodexTechnicalValidationService get_AuthBasedNationalTechValidator() {
        var workingService = new National_Authentication_TechnicalValidationService();
        workingService.setValid_AllDataPresent();

        return workingService;
    }
}
