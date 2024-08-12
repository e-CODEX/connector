/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.wp4.testenvironment.nationaldummyimplementations;

import eu.ecodex.dss.model.token.Signature;
import eu.ecodex.dss.model.token.SignatureAttributes;
import eu.ecodex.dss.model.token.SignatureCertificate;
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
 * The National_Signature_TechnicalValidationService class is an implementation of the
 * ECodexTechnicalValidationService interface. It provides methods to create a technical validation
 * result for a business document and generate a validation report in PDF format.
 */
@SuppressWarnings("checkstyle:TypeName")
public class National_Signature_TechnicalValidationService
    implements ECodexTechnicalValidationService {
    private TokenValidation tokenValidation;
    private TechnicalValidationResult technicalValidationResult;
    private Signature signature;
    private SignatureCertificate signatureCertificate;
    private SignatureAttributes signatureAttributes;
    private ValidationVerification validationVerification;

    @Override
    public TokenValidation create(DSSDocument businessDocument, DSSDocument detachedSignature)
        throws ECodexException {
        return tokenValidation;
    }

    @Override
    public DSSDocument createReportPDF(Token token) throws ECodexException {
        return null;
    }

    // No Result
    public void setInvalid_NoResult() {
        this.tokenValidation = null;
    }

    // Empty Result
    public void setInvalid_EmptyResult() {
        tokenValidation = new TokenValidation();
    }

    /**
     * Sets all the necessary data to validate a token. Creates instances of TokenValidation,
     * TechnicalValidationResult, Signature, SignatureCertificate, SignatureAttributes, and
     * ValidationVerification. Sets the appropriate values for all the properties of these
     * instances. If any exception occurs during the configuration of the signature based technical
     * validation service, it prints the exception message and stack trace to the standard error
     * stream.
     */
    // Invalid Result
    public void setInvalid_InvalidResult() {
        tokenValidation = new TokenValidation();
        technicalValidationResult = new TechnicalValidationResult();
        signature = new Signature();
        signatureCertificate = new SignatureCertificate();
        signatureAttributes = new SignatureAttributes();
        validationVerification = new ValidationVerification();

        try {
            signatureAttributes.setSignatureFormat(null); // Invalid Entry
            signatureAttributes.setSignatureLevel("QES");
            signatureAttributes.setSignatureValid(true);
            signatureAttributes.setStructureValid(true);

            signatureCertificate.setIssuer(null); // Invalid Entry
            signatureCertificate.setCertificateValid(true);
            signatureCertificate.setValidityAtSigningTime(true);

            signature.setCertificateInformation(signatureCertificate);
            signature.setSignatureInformation(signatureAttributes);
            signature.setSigningTime(
                DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar())
            );

            validationVerification.setSignatureData(signature);

            technicalValidationResult.setTrustLevel(TechnicalTrustLevel.SUCCESSFUL);
            technicalValidationResult.setComment("This document has been successfully validated");

            tokenValidation.setTechnicalResult(technicalValidationResult);
            tokenValidation.setVerificationData(validationVerification);
            tokenValidation.setVerificationTime(
                DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar())
            );
        } catch (Exception e) {
            System.err.println(
                "Exception within the configuration of the signature based technical validation "
                    + "service:"
            );
            e.printStackTrace();
        }
    }

    /**
     * Sets all the necessary data to validate a token. Creates instances of TokenValidation,
     * TechnicalValidationResult, Signature, SignatureCertificate, SignatureAttributes, and
     * ValidationVerification. Sets the appropriate values for all the properties of these
     * instances. If any exception occurs during the configuration of the signature based technical
     * validation service, it prints the exception message and stack trace to the standard error
     * stream.
     */
    public void setValid_AllDataPresent() {
        tokenValidation = new TokenValidation();
        technicalValidationResult = new TechnicalValidationResult();
        signature = new Signature();
        signatureCertificate = new SignatureCertificate();
        signatureAttributes = new SignatureAttributes();
        validationVerification = new ValidationVerification();

        try {
            signatureAttributes.setSignatureFormat("PAdES-BES");
            signatureAttributes.setSignatureLevel("QES");
            signatureAttributes.setSignatureValid(true);
            signatureAttributes.setStructureValid(true);

            signatureCertificate.setIssuer("Test-Issuer");
            signatureCertificate.setCertificateValid(true);
            signatureCertificate.setValidityAtSigningTime(true);

            signature.setCertificateInformation(signatureCertificate);
            signature.setSignatureInformation(signatureAttributes);
            signature.setSigningTime(
                DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar())
            );

            validationVerification.setSignatureData(signature);

            technicalValidationResult.setTrustLevel(TechnicalTrustLevel.SUCCESSFUL);
            technicalValidationResult.setComment("This document has been successfully validated");

            tokenValidation.setTechnicalResult(technicalValidationResult);
            tokenValidation.setVerificationData(validationVerification);
            tokenValidation.setVerificationTime(
                DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar())
            );
        } catch (Exception e) {
            System.err.println(
                "Exception within the configuration of the signature based technical validation "
                    + "service:"
            );
            e.printStackTrace();
        }
    }
}
