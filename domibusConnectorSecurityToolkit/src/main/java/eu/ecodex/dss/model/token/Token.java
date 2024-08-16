/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/model/token/Token.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.model.token;

import eu.europa.esig.xmldsig.jaxb.DigestMethodType;
import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 * This class holds the token which is the overall container for all data.
 *
 * <p>
 * DISCLAIMER: Project owner e-CODEX
 * </p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TokenType", propOrder = {"issuer", "document", "validation"})
@Data
public class Token implements Serializable {
    @XmlElement(name = "Issuer", required = true)
    protected TokenIssuer issuer;
    @XmlElement(name = "Document", required = true)
    protected TokenDocument document;
    @XmlElement(name = "Validation", required = true)
    protected TokenValidation validation;

    /**
     * Sets the value of the issuer property.
     *
     * @param value allowed object is {@link eu.ecodex.dss.model.token.TokenIssuer }
     * @return this class' instance for chaining
     */
    public Token setIssuer(final TokenIssuer value) {
        this.issuer = value;
        return this;
    }

    /**
     * Sets the value of the document property.
     *
     * @param value allowed object is {@link eu.ecodex.dss.model.token.TokenDocument }
     * @return this class' instance for chaining
     */
    public Token setDocument(final TokenDocument value) {
        this.document = value;
        return this;
    }

    /**
     * Sets the value of the validation property.
     *
     * @param value allowed object is {@link eu.ecodex.dss.model.token.TokenValidation }
     * @return this class' instance for chaining
     */
    public Token setValidation(final TokenValidation value) {
        this.validation = value;
        return this;
    }

    // -- convenience method to access attributes in the data structure -------

    /**
     * Get the value of the issuer country.
     *
     * @return possible object is {@link String} or null.
     */
    public String getIssuerCountry() {
        return (issuer == null) ? null : issuer.getCountry();
    }

    /**
     * Get the value of the issuer service provider.
     *
     * @return possible object is {@link String} or null.
     */
    public String getIssuerServiceProvider() {
        return (issuer == null) ? null : issuer.getServiceProvider();
    }

    /**
     * Gets the advanced electronic system.
     *
     * @return possible object is {@link AdvancedSystemType} or empty.
     */
    public AdvancedSystemType getAdvancedElectronicSystem() {
        return (issuer == null) ? null : issuer.getAdvancedElectronicSystem();
    }

    /**
     * Gets the value of advanced electronic system.
     *
     * @return possible object is {@link String} or empty.
     */
    public String getAdvancedElectronicSystemText() {
        final AdvancedSystemType type = getAdvancedElectronicSystem();
        return (type == null) ? StringUtils.EMPTY : type.getText();
    }

    /**
     * Gets the value of the token document name.
     *
     * @return possible object is {@link String} or empty.
     */
    public String getDocumentName() {
        return (document == null) ? StringUtils.EMPTY : document.getFilename();
    }

    /**
     * Get the value of the token document type.
     *
     * @return possible object is {@link String} or empty.
     */
    public String getDocumentType() {
        return (document == null) ? StringUtils.EMPTY : document.getType();
    }

    /**
     * Get the value of the token document digest method.
     *
     * @return possible object is {@link DigestMethodType} or null.
     */
    public DigestMethodType getDocumentDigestMethod() {
        return (document == null) ? null : document.getDigestMethod();
    }

    /**
     * Get the value of the token document digest value.
     *
     * @return The digest value of the document as a byte array,
     *         or null if the document is null.
     */
    public byte[] getDocumentDigestValue() {
        return (document == null) ? null : document.getDigestValue();
    }

    /**
     * Gets the value of the technical validation result.
     *
     * @return possible object is {@link TechnicalValidationResult}
     */
    public TechnicalValidationResult getTechnicalValidationResult() {
        return (validation == null) ? null : validation.getTechnicalResult();
    }

    /**
     * Gets the value of the legal validation result.
     *
     * @return possible object is {@link LegalValidationResult}
     */
    public LegalValidationResult getLegalValidationResult() {
        return (validation == null) ? null : validation.getLegalResult();
    }

    /**
     * Gets the value of original validation report.
     *
     * @return possible object is {@link OriginalValidationReportContainer}
     */
    public OriginalValidationReportContainer getValidationOriginalReport() {
        return (validation == null) ? null : validation.getOriginalValidationReport();
    }

    /**
     * Gets the value of verification data.
     *
     * @return possible object is {@link ValidationVerification}
     */
    public ValidationVerification getValidationVerificationData() {
        return (validation == null) ? null : validation.getVerificationData();
    }

    /**
     * Gets the value of verification time.
     *
     * @return possible object is {@link javax.xml.datatype.XMLGregorianCalendar}
     */
    public XMLGregorianCalendar getValidationVerificationTime() {
        return (validation == null) ? null : validation.getVerificationTime();
    }

    /**
     * Gets the value of authentication data.
     *
     * @return possible object is {@link AuthenticationInformation}
     */
    public AuthenticationInformation getValidationVerificationAuthenticationData() {
        final var verificationData = getValidationVerificationData();
        return (verificationData == null) ? null : verificationData.getAuthenticationData();
    }

    /**
     * Gets the provider of the authentication data.
     *
     * @return possible object is {@link String}
     */
    public String getValidationVerificationAuthenticationProvider() {
        final var authenticationData = getValidationVerificationAuthenticationData();
        return (authenticationData == null) ? null : authenticationData.getIdentityProvider();
    }

    /**
     * Gets the username-synonym of the authentication data.
     *
     * @return possible object is {@link String}
     */
    public String getValidationVerificationAuthenticationUsername() {
        final var authenticationData = getValidationVerificationAuthenticationData();
        return (authenticationData == null) ? null : authenticationData.getUsernameSynonym();
    }

    /**
     * Gets the time of the authentication data.
     *
     * @return possible object is {@link XMLGregorianCalendar}
     */
    public XMLGregorianCalendar getValidationVerificationAuthenticationTime() {
        final var authenticationData = getValidationVerificationAuthenticationData();
        return (authenticationData == null) ? null : authenticationData.getTimeOfAuthentication();
    }

    /**
     * Gets the value of signature data. Deprecated due to rework: Now multiple signatures are
     * possible! Will return data for first Signature or no result.
     *
     * @return possible object is {@link Signature}
     * @deprecated This method is deprecated and may be removed in a future release.
     */
    @Deprecated
    public Signature getValidationVerificationSignatureData() {
        final var verificationData = getValidationVerificationData();
        if (!isValidationVerificationSignatureUnsigned()) {
            return verificationData.getSignatureData().getFirst();
        } else {
            return null;
        }
    }

    /**
     * Gets the value of signature data.
     *
     * @return possible object is {@link Signature}
     */
    public List<Signature> getValidationVerificationSignatureDataList() {
        final var verificationData = getValidationVerificationData();
        return (verificationData == null) ? null : verificationData.getSignatureData();
    }

    /**
     * Gets the value of signature certificate. Deprecated due to rework: Now multiple signatures
     * are possible! Will return data for first Signature or no result.
     *
     * @return possible object is {@link SignatureCertificate}
     * @deprecated This method is deprecated and should not be used.
     */
    @Deprecated
    public SignatureCertificate getValidationVerificationSignatureCertificateInformation() {
        final var signatureData = getValidationVerificationSignatureData();
        return (signatureData == null) ? null : signatureData.getCertificateInformation();
    }

    /**
     * Gets the value of signature certificate for a given signature.
     *
     * @param signature allowed object is {@link eu.ecodex.dss.model.token.Signature }
     * @return possible object is {@link SignatureCertificate}
     */
    public SignatureCertificate getValidationVerificationSignatureCertificateInformation(
        Signature signature) {
        return (signature == null) ? null : signature.getCertificateInformation();
    }

    /**
     * Gets the certificate issuer Deprecated due to rework: Now multiple signatures are possible!
     * Will return data for first Signature or no result.
     *
     * @return possible object is {@link String} or null.
     * @deprecated This method is deprecated and will be removed in a future release. Use
     *      {@link #getValidationVerificationSignatureCertificateInformation()} and access the
     *      issuer field directly instead.
     */
    @Deprecated
    public String getValidationVerificationSignatureCertificateIssuer() {
        final var certificateInformation =
            getValidationVerificationSignatureCertificateInformation();
        return (certificateInformation == null) ? null : certificateInformation.getIssuer();
    }

    /**
     * Gets the value of the issuer for a signature certificate for a given signature.
     *
     * @param signatureCertificate allowed object is
     *                             {@link eu.ecodex.dss.model.token.SignatureCertificate }
     * @return possible object is {@link String} or null.
     */
    public String getValidationVerificationSignatureCertificateIssuer(
        SignatureCertificate signatureCertificate) {
        return (signatureCertificate == null) ? null : signatureCertificate.getIssuer();
    }

    /**
     * Gets the value of signature information. Deprecated due to rework: Now multiple signatures
     * are possible! Will return data for first Signature or no result.
     *
     * @return possible object is {@link SignatureAttributes}
     * @deprecated This method is deprecated. Use `getValidationVerificationSignatureData` instead.
     */
    @Deprecated
    public SignatureAttributes getValidationVerificationSignatureInformation() {
        final var verificationSignatureData = getValidationVerificationSignatureData();
        return (verificationSignatureData == null)
            ? null
            : verificationSignatureData.getSignatureInformation();
    }

    /**
     * Gets the value of signature information.
     *
     * @param signature allowed object is {@link eu.ecodex.dss.model.token.Signature }
     * @return possible object is {@link SignatureAttributes}
     */
    public SignatureAttributes getValidationVerificationSignatureInformation(Signature signature) {
        return (signature == null) ? null : signature.getSignatureInformation();
    }

    /**
     * Gets the value of signature attribute format. Deprecated due to rework: Now multiple
     * signatures are possible! Will return data for first Signature or no result.
     *
     * @return possible object is {@link String} or null
     * @deprecated This method is deprecated and should not be used.
     */
    @Deprecated
    public String getValidationVerificationSignatureFormat() {
        final SignatureAttributes o = getValidationVerificationSignatureInformation();
        return (o == null) ? null : o.getSignatureFormat();
    }

    /**
     * Gets the value of signature attribute format.
     *
     * @param signatureAttributes allowed object is
     *                            {@link eu.ecodex.dss.model.token.SignatureAttributes }
     * @return possible object is {@link String} or null
     */
    public String getValidationVerificationSignatureFormat(
        SignatureAttributes signatureAttributes) {
        return (signatureAttributes == null) ? null : signatureAttributes.getSignatureFormat();
    }

    /**
     * Gets the value of signature attribute level Deprecated due to rework: Now multiple signatures
     * are possible! Will return data for first Signature or no result.
     *
     * @return possible object is {@link String} or null.
     * @deprecated This method has been deprecated and may be removed in a future release. Use
     *      {@link #getValidationVerificationSignatureInformation()} instead.
     */
    @Deprecated
    public String getValidationVerificationSignatureLevel() {
        final SignatureAttributes o = getValidationVerificationSignatureInformation();
        return (o == null) ? null : o.getSignatureLevel();
    }

    /**
     * Gets the value of signature attribute level.
     *
     * @param signatureAttributes allowed object is
     *                            {@link eu.ecodex.dss.model.token.SignatureAttributes }
     * @return possible object is {@link String} or null.
     */
    public String getValidationVerificationSignatureLevel(SignatureAttributes signatureAttributes) {
        return (signatureAttributes == null) ? null : signatureAttributes.getSignatureLevel();
    }

    /**
     * Gets the value of signing status.
     *
     * @return true = the document is unsigned
     */
    public boolean isValidationVerificationSignatureUnsigned() {
        List<Signature> signatures = getValidationVerificationSignatureDataList();

        // Pre 1.09 version of "being unsigned"
        if (signatures != null && !signatures.isEmpty()) {
            return signatures.getFirst().getAuthenticationCertValidation() == null
                && signatures.getFirst().getCertificateInformation() == null
                && signatures.getFirst().getSignatureInformation() == null
                && signatures.getFirst().getSigningTime() == null
                && signatures.getFirst().getTechnicalResult() == null;
        } else {
            return true;
        }
    }

    /**
     * Gets the value of signing time. Deprecated due to rework: Now multiple signatures are
     * possible! Will return data for first Signature or no result.
     *
     * @return possible object is {@link javax.xml.datatype.XMLGregorianCalendar}
     * @see XMLGregorianCalendar
     * @deprecated This method has been deprecated and may be removed in a future version. Use the
     *      {@link #getValidationVerificationSignatureData()} method instead to obtain the signature
     *      data and get the signing time from there.
     */
    @Deprecated
    public XMLGregorianCalendar getValidationVerificationSignatureSigningTime() {
        final var signatureData = getValidationVerificationSignatureData();
        return (signatureData == null) ? null : signatureData.getSigningTime();
    }

    /**
     * Gets the value of signing time.
     *
     * @param signature allowed object is {@link eu.ecodex.dss.model.token.Signature }
     * @return possible object is {@link javax.xml.datatype.XMLGregorianCalendar}
     */
    public XMLGregorianCalendar getValidationVerificationSignatureSigningTime(Signature signature) {
        return (signature == null) ? null : signature.getSigningTime();
    }

    /**
     * Gets the value of technical trust level.
     *
     * @return possible object is {@link TechnicalTrustLevel}
     */
    public TechnicalTrustLevel getTechnicalValidationResultTrustLevel() {
        final var validationResult = getTechnicalValidationResult();
        return (validationResult == null)
            ? TechnicalTrustLevel.FAIL
            : validationResult.getTrustLevel();
    }

    /**
     * Gets the value of technical comment.
     *
     * @return possible object is {@link String} or null
     */
    public String getTechnicalValidationResultComments() {
        final var validationResult = getTechnicalValidationResult();
        return (validationResult == null) ? null : validationResult.getComment();
    }

    /**
     * Gets the value of legal trust level.
     *
     * @return possible object is {@link TechnicalTrustLevel}
     */
    public LegalTrustLevel getLegalValidationResultTrustLevel() {
        final var validationResult = getLegalValidationResult();
        return (validationResult == null)
            ? LegalTrustLevel.NOT_SUCCESSFUL
            : validationResult.getTrustLevel();
    }

    /**
     * Gets the value of legal disclaimer.
     *
     * @return possible object is {@link String} or null
     */
    public String getLegalValidationResultDisclaimer() {
        final var validationResult = getLegalValidationResult();
        return (validationResult == null) ? null : validationResult.getDisclaimer();
    }

    /**
     * Gets the value of certificate verification. Deprecated due to rework: Now multiple signatures
     * are possible! Will return data for first Signature or no result.
     *
     * @return possible object is true or false
     * @deprecated This method is deprecated and may be removed in future versions. Please use an
     *      alternative method.
     */
    @Deprecated
    public boolean isValidationVerificationSignatureCertificateValid() {
        final var certificateInformation =
            getValidationVerificationSignatureCertificateInformation();
        return (certificateInformation != null) && certificateInformation.isCertificateValid();
    }

    /**
     * Gets the value of certificate verification.
     *
     * @param signatureCertificate allowed object is
     *                             {@link eu.ecodex.dss.model.token.SignatureCertificate }
     * @return possible object is true or false
     */
    public boolean isValidationVerificationSignatureCertificateValid(
        SignatureCertificate signatureCertificate) {
        return (signatureCertificate != null) && signatureCertificate.isCertificateValid();
    }

    /**
     * Gets the value of signature verification. Deprecated due to rework: Now multiple signatures
     * are possible! Will return data for first Signature or no result.
     *
     * @return possible object is true or false
     * @deprecated This method is deprecated and will be removed in a future version. Use the
     *      {@link #getValidationVerificationSignatureInformation()} method instead.
     */
    @Deprecated
    public boolean isValidationVerificationSignatureValid() {
        final var signatureInformation = getValidationVerificationSignatureInformation();
        return (signatureInformation != null) && signatureInformation.isSignatureValid();
    }

    /**
     * Gets the value of signature verification.
     *
     * @param signatureAttributes allowed object is
     *                            {@link eu.ecodex.dss.model.token.SignatureAttributes }
     * @return possible object is true or false
     */
    public boolean isValidationVerificationSignatureValid(SignatureAttributes signatureAttributes) {
        return (signatureAttributes != null) && signatureAttributes.isSignatureValid();
    }

    /**
     * Gets the value of structure verification. Deprecated due to rework: Now multiple signatures
     * are possible! Will return data for first Signature or no result.
     *
     * @return possible object is true or false
     * @deprecated This method is deprecated.
     */
    @Deprecated
    public boolean isValidationVerificationSignatureStructureValid() {
        final var signatureInformation = getValidationVerificationSignatureInformation();
        return (signatureInformation != null) && signatureInformation.isStructureValid();
    }

    /**
     * Gets the value of structure verification.
     *
     * @param signatureAttributes allowed object is
     *                            {@link eu.ecodex.dss.model.token.SignatureAttributes }
     * @return possible object is true or false
     */
    public boolean isValidationVerificationSignatureStructureValid(
        SignatureAttributes signatureAttributes) {
        return (signatureAttributes != null) && signatureAttributes.isStructureValid();
    }

    /**
     * Gets the value of validity as signing time. Deprecated due to rework: Now multiple signatures
     * are possible! Will return data for first Signature or no result.
     *
     * @return possible object is true or false
     * @deprecated This method is deprecated and is no longer recommended for use.
     */
    @Deprecated
    public boolean isValidationVerificationSignatureCertificateValidityAtSigningTime() {
        final var certificateInformation =
            getValidationVerificationSignatureCertificateInformation();
        return (certificateInformation != null) && certificateInformation.isValidityAtSigningTime();
    }

    /**
     * Gets the value of validity as signing time.
     *
     * @param signatureCertificate allowed object is
     *                             {@link eu.ecodex.dss.model.token.SignatureCertificate }
     * @return possible object is true or false
     */
    public boolean isValidationVerificationSignatureCertificateValidityAtSigningTime(
        SignatureCertificate signatureCertificate) {
        return (signatureCertificate != null) && signatureCertificate.isValidityAtSigningTime();
    }
}
