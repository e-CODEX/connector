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
 * The InvalidConfig_SigBasedNationalTechValidator class provides static methods to obtain instances
 * of ECodexTechnicalValidationService with different types of invalid configurations. These invalid
 * configurations are used for testing purposes. The class contains three variants of the
 * get_SigBasedNationalTechValidator method, each returning an instance of the
 * National_Signature_TechnicalValidationService class with a different invalid configuration.
 *
 * <p>The ECodexTechnicalValidationService interface is a contract for creating the technical
 * validation (result and report) on the business document. The ECodexTechnicalValidationService
 * object is called as a delegate by the DSS implementation of the ECodexContainerService interface.
 * Implementors of the ECodexTechnicalValidationService interface must guarantee that the object
 * instances provided in parameters do not become tampered or changed. The execution of the methods
 * in the interface must be thread-safe. All the methods in the interface may throw an
 * ECodexException.
 */
// SUB_CONF_07
@SuppressWarnings("checkstyle:TypeName")
public class InvalidConfig_SigBasedNationalTechValidator {
    /**
     * Returns an instance of ECodexTechnicalValidationService with a null result. It creates an
     * instance of National_Signature_TechnicalValidationService and sets the validation result to
     * null. This method is used for testing purposes.
     *
     * @return an instance of ECodexTechnicalValidationService with a null result
     */
    // SUB_CONF_07 Variant 1
    public static ECodexTechnicalValidationService get_SigBasedNationalTechValidator_NullResult() {
        var workingService = new National_Signature_TechnicalValidationService();
        workingService.setInvalid_NoResult();

        return workingService;
    }

    /**
     * Returns an instance of {@link ECodexTechnicalValidationService} with an empty result. This
     * method creates an instance of {@link National_Signature_TechnicalValidationService} and sets
     * the validation result as empty.
     *
     * <p>This method is used for testing purposes and should not be used in production code.
     *
     * @return an instance of {@link ECodexTechnicalValidationService} with an empty result
     */
    // SUB_CONF_07 Variant 2
    public static ECodexTechnicalValidationService get_SigBasedNationalTechValidator_EmptyResult() {
        var workingService = new National_Signature_TechnicalValidationService();
        workingService.setInvalid_EmptyResult();

        return workingService;
    }

    /**
     * Creates an instance of {@link ECodexTechnicalValidationService} with an invalid result.
     *
     * <p>This method creates an instance of {@link National_Signature_TechnicalValidationService}
     * and sets the validation result to invalid. The validation result is constructed by creating
     * various objects such as Signature, SignatureCertificate, SignatureAttributes, and
     * ValidationVerification. The SignatureAttributes object is set with an invalid entry, and the
     * SignatureCertificate object is set with an invalid issuer. The created objects are then used
     * to construct the TokenValidation object, which represents the validation result.
     *
     * <p>This method is used for testing purposes and should not be used in production code.
     *
     * @return an instance of {@link ECodexTechnicalValidationService} with an invalid result
     */
    // SUB_CONF_07 Variant 3
    public static ECodexTechnicalValidationService
    get_SigBasedNationalTechValidator_InvalidResult() {
        var workingService = new National_Signature_TechnicalValidationService();
        workingService.setInvalid_InvalidResult();

        return workingService;
    }
}
