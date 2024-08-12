/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.wp4.testenvironment.configurations;

import eu.domibus.connector.wp4.testenvironment.nationaldummyimplementations.National_LegalValidationService;
import eu.ecodex.dss.service.ECodexLegalValidationService;

/**
 * The ValidConfig_NationalLegalValidator class provides static methods to retrieve instances of
 * ECodexLegalValidationService for different variants of national legal validation.
 *
 * <p>The ValidConfig_NationalLegalValidator class defines the following variants of national legal
 * validation:
 * <ul>
 *   <li>Variant 1: {@link #get_NationalLegalValidator_FullData()}</li>
 *   <li>Variant 2: {@link #get_NationalLegalValidator_NoDisclaimer()}</li>
 * </ul>
 *
 * <p>Note that the ECodexLegalValidationService interface defines the contract for creating the
 * legal validation, and implementors of this interface must guarantee that the object instances
 * provided in parameters do not become tampered and that the execution is thread-safe.
 * All methods may throw an ECodexException.
 *
 * <p>DISCLAIMER: Project owner e-CODEX
 */
// SUB_CONF_09
@SuppressWarnings("checkstyle:TypeName")
public class ValidConfig_NationalLegalValidator {
    /**
     * Retrieves an instance of {@link ECodexLegalValidationService} for national legal validation
     * with full data. This method creates a new instance of {@link National_LegalValidationService}
     * and sets it to a valid state where all data is present. The trust level is set to
     * {@link eu.ecodex.dss.model.token.LegalTrustLevel#SUCCESSFUL} and the disclaimer is set to
     * "Just a disclaimer".
     *
     * @return an instance of {@link ECodexLegalValidationService} for national legal validation
     *      with full data
     */
    // SUB_CONF_09 Variant 1
    public static ECodexLegalValidationService get_NationalLegalValidator_FullData() {
        var workingService = new National_LegalValidationService();
        workingService.setValid_AllDataPresent();

        return workingService;
    }

    /**
     * Retrieves an instance of {@link ECodexLegalValidationService} for national legal validation
     * without a disclaimer. This method creates a new instance of
     * {@link National_LegalValidationService} and sets it to a valid state where the trust level is
     * set to {@link eu.ecodex.dss.model.token.LegalTrustLevel#SUCCESSFUL } and no disclaimer is
     * present.
     *
     * @return an instance of {@link ECodexLegalValidationService} for national legal validation
     *      without a disclaimer
     */
    // SUB_CONF_09 Variant 2
    public static ECodexLegalValidationService get_NationalLegalValidator_NoDisclaimer() {
        var workingService = new National_LegalValidationService();
        workingService.setValid_NoDisclaimer();

        return workingService;
    }
}
