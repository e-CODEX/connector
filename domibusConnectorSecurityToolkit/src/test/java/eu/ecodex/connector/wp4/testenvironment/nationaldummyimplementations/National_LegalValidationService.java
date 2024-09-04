/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.wp4.testenvironment.nationaldummyimplementations;

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
