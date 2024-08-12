/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.wp4.testenvironment.nationaldummyimplementations;

import eu.ecodex.dss.model.token.LegalTrustLevel;
import eu.ecodex.dss.model.token.LegalValidationResult;
import eu.ecodex.dss.model.token.Token;
import eu.ecodex.dss.service.ECodexException;
import eu.ecodex.dss.service.ECodexLegalValidationService;

/**
 * The National_LegalValidationService class is an implementation of the
 * ECodexLegalValidationService interface. It provides methods to create legal validation results
 * for a given token.
 */
@SuppressWarnings("checkstyle:TypeName")
public class National_LegalValidationService implements ECodexLegalValidationService {
    private LegalValidationResult result;

    @Override
    public LegalValidationResult create(Token token) throws ECodexException {
        return result;
    }

    /**
     * This method sets the legal validation result to a valid state where all data is present. It
     * creates a new instance of LegalValidationResult and sets the trust level to
     * LegalTrustLevel.SUCCESSFUL and the disclaimer to "Just a disclaimer".
     */
    public void setValid_AllDataPresent() {
        result = new LegalValidationResult();
        result.setDisclaimer("Just a disclaimer");
        result.setTrustLevel(LegalTrustLevel.SUCCESSFUL);
    }

    public void setValid_NoDisclaimer() {
        result = new LegalValidationResult();
        result.setTrustLevel(LegalTrustLevel.SUCCESSFUL);
    }

    public void setInvalid_NullResult() {
        this.result = null;
    }

    public void setInvalid_EmptyResult() {
        result = new LegalValidationResult();
    }

    public void setInvalid_MissingTrustLevel() {
        result = new LegalValidationResult();
        result.setTrustLevel(null);
    }
}
