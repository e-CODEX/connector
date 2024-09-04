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

import eu.ecodex.connector.wp4.testenvironment.nationaldummyimplementations.National_Signature_TechnicalValidationService;
import eu.ecodex.dss.service.ECodexTechnicalValidationService;

/**
 * ValidConfig_SigBasedNationalTechValidator is a class that provides a method to obtain an instance
 * of {@link ECodexTechnicalValidationService} with a specific configuration for signature-based
 * national technical validation.
 *
 * <p>he class contains a static method {@code get_SigBasedNationalTechValidator} that returns an
 * instance of {@link National_Signature_TechnicalValidationService}. This method ensures that all
 * required data is present and properly configured for the technical validation of a national
 * signature.
 *
 * <p>The returned service object implements the {@link ECodexTechnicalValidationService}
 * interface, which defines methods for creating the technical validation and generating a
 * validation report in PDF format.
 *
 * <p>The use of this class is recommended when performing signature-based national technical
 * validation. However, it is important to provide the necessary data before using the returned
 * service object.
 *
 * <p>Note: This class does not provide documentation for its type member properties.
 */
// SUB_CONF_04
@SuppressWarnings("checkstyle:TypeName")
public class ValidConfig_SigBasedNationalTechValidator {
    /**
     * Returns an instance of {@link ECodexTechnicalValidationService} with a specific configuration
     * for signature-based national technical validation.
     *
     * <p>The method creates an instance of {@link National_Signature_TechnicalValidationService}
     * and sets it up for signature-based national technical validation by calling the
     * {@code setValid_AllDataPresent} method on the instance. This method ensures that all required
     * data is present and properly configured for the technical validation of a national
     * signature.
     *
     * <p>The returned service object implements the {@link ECodexTechnicalValidationService}
     * interface, which defines methods for creating the technical validation and generating a
     * validation report in PDF format.
     *
     * @return an instance of {@link ECodexTechnicalValidationService} configured for
     *      signature-based national technical validation
     */
    // SUB_CONF_04 Variant 1
    public static ECodexTechnicalValidationService get_SigBasedNationalTechValidator() {
        var workingService = new National_Signature_TechnicalValidationService();
        workingService.setValid_AllDataPresent();

        return workingService;
    }
}
