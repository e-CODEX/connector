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

import eu.ecodex.dss.model.token.AuthenticationInformation;
import eu.ecodex.dss.model.token.TechnicalTrustLevel;
import eu.ecodex.dss.model.token.TechnicalValidationResult;
import eu.ecodex.dss.model.token.Token;
import eu.ecodex.dss.model.token.TokenValidation;
import eu.ecodex.dss.model.token.ValidationVerification;
import eu.ecodex.dss.service.ECodexException;
import eu.ecodex.dss.service.ECodexTechnicalValidationService;
import eu.europa.esig.dss.model.DSSDocument;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeFactory;

/**
 * The National_Authentication_TechnicalValidationService class is responsible for performing the
 * technical validation of a document using national authentication information. It implements the
 * ECodexTechnicalValidationService interface.
 */
@SuppressWarnings("checkstyle:TypeName")
public class National_Authentication_TechnicalValidationService
    implements ECodexTechnicalValidationService {
    private TokenValidation tknValidation;
    private TechnicalValidationResult techResult;
    private AuthenticationInformation authInfo;
    private ValidationVerification validationVerification;

    @Override
    public TokenValidation create(DSSDocument businessDocument, DSSDocument detachedSignature)
        throws ECodexException {
        return tknValidation;
    }

    @Override
    public DSSDocument createReportPDF(Token token) {
        return null;
    }

    /**
     * Sets the values of all the data required for a valid token validation. Initializes and sets
     * the values of the TokenValidation, TechnicalValidationResult, AuthenticationInformation, and
     * ValidationVerification objects.
     */
    public void setValid_AllDataPresent() {
        tknValidation = new TokenValidation();
        techResult = new TechnicalValidationResult();
        authInfo = new AuthenticationInformation();
        validationVerification = new ValidationVerification();

        try {
            authInfo.setIdentityProvider("Identity Inc.");
            authInfo.setUsernameSynonym("John Doe");
            authInfo.setTimeOfAuthentication(
                DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar())
            );

            validationVerification.setAuthenticationData(authInfo);

            techResult.setTrustLevel(TechnicalTrustLevel.SUCCESSFUL);
            techResult.setComment("This document has been successfully validated");

            tknValidation.setTechnicalResult(techResult);
            tknValidation.setVerificationData(validationVerification);
            tknValidation.setVerificationTime(
                DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
        } catch (Exception e) {
            System.err.println(
                "Exception within the configuration of the authentication based technical "
                    + "validation service:"
            );
            e.printStackTrace();
        }
    }

    // No Result
    public void setInvalid_NullResult() {
        this.tknValidation = null;
    }

    // Empty Result
    public void setInvalid_EmptyResult() {
        tknValidation = new TokenValidation();
    }

    /**
     * Sets the values of all the data required for an invalid token validation result. Initializes
     * and sets the values of the TokenValidation, TechnicalValidationResult,
     * AuthenticationInformation, and ValidationVerification objects. This method sets invalid data
     * for the AuthenticationInformation object by setting the usernameSynonym property to null. It
     * also sets valid data for the TechnicalValidationResult object and initializes the other
     * objects with default values.
     */
    // Invalid Result
    public void setInvalid_InvalidResult() {
        tknValidation = new TokenValidation();
        techResult = new TechnicalValidationResult();
        authInfo = new AuthenticationInformation();
        validationVerification = new ValidationVerification();

        try {
            authInfo.setIdentityProvider("Identity Inc.");
            authInfo.setUsernameSynonym(null); // Invalid Entry
            authInfo.setTimeOfAuthentication(
                DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));

            validationVerification.setAuthenticationData(authInfo);

            techResult.setTrustLevel(TechnicalTrustLevel.SUCCESSFUL);
            techResult.setComment("This document has been successfully validated");

            tknValidation.setTechnicalResult(techResult);
            tknValidation.setVerificationData(validationVerification);
            tknValidation.setVerificationTime(
                DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
        } catch (Exception e) {
            System.err.println(
                "Exception within the configuration of the authentication based technical "
                    + "validation service:"
            );
            e.printStackTrace();
        }
    }
}
