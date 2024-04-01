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
import org.apache.commons.lang.StringUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.Serializable;
import java.util.List;


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
public class Token implements Serializable {
    @XmlElement(name = "Issuer", required = true)
    protected TokenIssuer issuer;
    @XmlElement(name = "Document", required = true)
    protected TokenDocument document;
    @XmlElement(name = "Validation", required = true)
    protected TokenValidation validation;

    /**
     * Gets the value of the issuer property.
     *
     * @return possible object is {@link eu.ecodex.dss.model.token.TokenIssuer }
     */
    public TokenIssuer getIssuer() {
        return issuer;
    }

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
     * Gets the value of the document property.
     *
     * @return possible object is {@link eu.ecodex.dss.model.token.TokenDocument }
     */
    public TokenDocument getDocument() {
        return document;
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
     * Gets the value of the validation property.
     *
     * @return possible object is {@link eu.ecodex.dss.model.token.TokenValidation }
     */
    public TokenValidation getValidation() {
        return validation;
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
     * Get the value of the issuer country
     *
     * @return possible object is {@link String} or null.
     */
    public String getIssuerCountry() {
        return (issuer == null) ? null : issuer.getCountry();
    }

    /**
     * Get the value of the issuer service provider
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
     * @return possible object is {@link byte[]} or null.
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
        final ValidationVerification o = getValidationVerificationData();
        return (o == null) ? null : o.getAuthenticationData();
    }

    /**
     * Gets the provider of the authentication data.
     *
     * @return possible object is {@link String}
     */
    public String getValidationVerificationAuthenticationProvider() {
        final AuthenticationInformation o = getValidationVerificationAuthenticationData();
        return (o == null) ? null : o.getIdentityProvider();
    }

    /**
     * Gets the username-synonym of the authentication data.
     *
     * @return possible object is {@link String}
     */
    public String getValidationVerificationAuthenticationUsername() {
        final AuthenticationInformation o = getValidationVerificationAuthenticationData();
        return (o == null) ? null : o.getUsernameSynonym();
    }

    /**
     * Gets the time of the authentication data.
     *
     * @return possible object is {@link XMLGregorianCalendar}
     */
    public XMLGregorianCalendar getValidationVerificationAuthenticationTime() {
        final AuthenticationInformation o = getValidationVerificationAuthenticationData();
        return (o == null) ? null : o.getTimeOfAuthentication();
    }

    /**
     * Gets the value of signature data.
     * Deprecated due to rework: Now multiple signatures are possible!
     * Will return data for first Signature or no result.
     *
     * @return possible object is {@link Signature}
     */
    @Deprecated
    public Signature getValidationVerificationSignatureData() {
        final ValidationVerification o = getValidationVerificationData();
        if (!isValidationVerificationSignatureUnsigned()) {
            return o.getSignatureData().get(0);
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
        final ValidationVerification o = getValidationVerificationData();
        return (o == null) ? null : o.getSignatureData();
    }

    /**
     * Gets the value of signature certificate.
     * Deprecated due to rework: Now multiple signatures are possible!
     * Will return data for first Signature or no result.
     *
     * @return possible object is {@link SignatureCertificate}
     */
    @Deprecated
    public SignatureCertificate getValidationVerificationSignatureCertificateInformation() {
        final Signature o = getValidationVerificationSignatureData();
        return (o == null) ? null : o.getCertificateInformation();
    }

    /**
     * Gets the value of signature certificate for a given signature.
     *
     * @param signature allowed object is {@link eu.ecodex.dss.model.token.Signature }
     * @return possible object is {@link SignatureCertificate}
     */
    public SignatureCertificate getValidationVerificationSignatureCertificateInformation(Signature signature) {
        return (signature == null) ? null : signature.getCertificateInformation();
    }

    /**
     * Gets the certificate issuer
     * Deprecated due to rework: Now multiple signatures are possible!
     * Will return data for first Signature or no result.
     *
     * @return possible object is {@link String} or null.
     */
    @Deprecated
    public String getValidationVerificationSignatureCertificateIssuer() {
        final SignatureCertificate o = getValidationVerificationSignatureCertificateInformation();
        return (o == null) ? null : o.getIssuer();
    }

    /**
     * Gets the value of the issuer for a signature certificate for a given signature.
     *
     * @param signatureCertificate allowed object is {@link eu.ecodex.dss.model.token.SignatureCertificate }
     * @return possible object is {@link String} or null.
     */
    public String getValidationVerificationSignatureCertificateIssuer(SignatureCertificate signatureCertificate) {
        return (signatureCertificate == null) ? null : signatureCertificate.getIssuer();
    }

    /**
     * Gets the value of signature information.
     * Deprecated due to rework: Now multiple signatures are possible!
     * Will return data for first Signature or no result.
     *
     * @return possible object is {@link SignatureAttributes}
     */
    @Deprecated
    public SignatureAttributes getValidationVerificationSignatureInformation() {
        final Signature o = getValidationVerificationSignatureData();
        return (o == null) ? null : o.getSignatureInformation();
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
     * Gets the value of signature attribute format.
     * Deprecated due to rework: Now multiple signatures are possible!
     * Will return data for first Signature or no result.
     *
     * @return possible object is {@link String} or null
     */
    @Deprecated
    public String getValidationVerificationSignatureFormat() {
        final SignatureAttributes o = getValidationVerificationSignatureInformation();
        return (o == null) ? null : o.getSignatureFormat();
    }

    /**
     * Gets the value of signature attribute format.
     *
     * @param signatureAttributes allowed object is {@link eu.ecodex.dss.model.token.SignatureAttributes }
     * @return possible object is {@link String} or null
     */
    public String getValidationVerificationSignatureFormat(SignatureAttributes signatureAttributes) {
        return (signatureAttributes == null) ? null : signatureAttributes.getSignatureFormat();
    }

    /**
     * Gets the value of signature attribute level
     * Deprecated due to rework: Now multiple signatures are possible!
     * Will return data for first Signature or no result.
     *
     * @return possible object is {@link String} or null.
     */
    @Deprecated
    public String getValidationVerificationSignatureLevel() {
        final SignatureAttributes o = getValidationVerificationSignatureInformation();
        return (o == null) ? null : o.getSignatureLevel();
    }

    /**
     * Gets the value of signature attribute level
     *
     * @param signatureAttributes allowed object is {@link eu.ecodex.dss.model.token.SignatureAttributes }
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
            return signatures.get(0).getAuthenticationCertValidation() == null &&
                    signatures.get(0).getCertificateInformation() == null &&
                    signatures.get(0).getSignatureInformation() == null &&
                    signatures.get(0).getSigningTime() == null &&
                    signatures.get(0).getTechnicalResult() == null;
        } else {
            return true;
        }
    }

    /**
     * Gets the value of signing time.
     * Deprecated due to rework: Now multiple signatures are possible!
     * Will return data for first Signature or no result.
     *
     * @return possible object is {@link javax.xml.datatype.XMLGregorianCalendar}
     */
    @Deprecated
    public XMLGregorianCalendar getValidationVerificationSignatureSigningTime() {
        final Signature o = getValidationVerificationSignatureData();
        return (o == null) ? null : o.getSigningTime();
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
        final TechnicalValidationResult o = getTechnicalValidationResult();
        return (o == null) ? TechnicalTrustLevel.FAIL : o.getTrustLevel();
    }

    /**
     * Gets the value of technical comment.
     *
     * @return possible object is {@link String} or null
     */
    public String getTechnicalValidationResultComments() {
        final TechnicalValidationResult o = getTechnicalValidationResult();
        return (o == null) ? null : o.getComment();
    }

    /**
     * Gets the value of legal trust level.
     *
     * @return possible object is {@link TechnicalTrustLevel}
     */
    public LegalTrustLevel getLegalValidationResultTrustLevel() {
        final LegalValidationResult o = getLegalValidationResult();
        return (o == null) ? LegalTrustLevel.NOT_SUCCESSFUL : o.getTrustLevel();
    }

    /**
     * Gets the value of legal disclaimer.
     *
     * @return possible object is {@link String} or null
     */
    public String getLegalValidationResultDisclaimer() {
        final LegalValidationResult o = getLegalValidationResult();
        return (o == null) ? null : o.getDisclaimer();
    }

    /**
     * Gets the value of certificate verification.
     * Deprecated due to rework: Now multiple signatures are possible!
     * Will return data for first Signature or no result.
     *
     * @return possible object is true or false
     */
    @Deprecated
    public boolean isValidationVerificationSignatureCertificateValid() {
        final SignatureCertificate o = getValidationVerificationSignatureCertificateInformation();
        return (o != null) && o.isCertificateValid();
    }

    /**
     * Gets the value of certificate verification.
     *
     * @param signatureCertificate allowed object is {@link eu.ecodex.dss.model.token.SignatureCertificate }
     * @return possible object is true or false
     */
    public boolean isValidationVerificationSignatureCertificateValid(SignatureCertificate signatureCertificate) {
        return (signatureCertificate != null) && signatureCertificate.isCertificateValid();
    }

    /**
     * Gets the value of signature verification.
     * Deprecated due to rework: Now multiple signatures are possible!
     * Will return data for first Signature or no result.
     *
     * @return possible object is true or false
     */
    @Deprecated
    public boolean isValidationVerificationSignatureValid() {
        final SignatureAttributes o = getValidationVerificationSignatureInformation();
        return (o != null) && o.isSignatureValid();
    }

    /**
     * Gets the value of signature verification.
     *
     * @param signatureAttributes allowed object is {@link eu.ecodex.dss.model.token.SignatureAttributes }
     * @return possible object is true or false
     */
    public boolean isValidationVerificationSignatureValid(SignatureAttributes signatureAttributes) {
        return (signatureAttributes != null) && signatureAttributes.isSignatureValid();
    }

    /**
     * Gets the value of structure verification.
     * Deprecated due to rework: Now multiple signatures are possible!
     * Will return data for first Signature or no result.
     *
     * @return possible object is true or false
     */
    @Deprecated
    public boolean isValidationVerificationSignatureStructureValid() {
        final SignatureAttributes o = getValidationVerificationSignatureInformation();
        return (o != null) && o.isStructureValid();
    }

    /**
     * Gets the value of structure verification.
     *
     * @param signatureAttributes allowed object is {@link eu.ecodex.dss.model.token.SignatureAttributes }
     * @return possible object is true or false
     */
    public boolean isValidationVerificationSignatureStructureValid(SignatureAttributes signatureAttributes) {
        return (signatureAttributes != null) && signatureAttributes.isStructureValid();
    }

    /**
     * Gets the value of validity as signing time.
     * Deprecated due to rework: Now multiple signatures are possible!
     * Will return data for first Signature or no result.
     *
     * @return possible object is true or false
     */
    @Deprecated
    public boolean isValidationVerificationSignatureCertificateValidityAtSigningTime() {
        final SignatureCertificate o = getValidationVerificationSignatureCertificateInformation();
        return (o != null) && o.isValidityAtSigningTime();
    }

    /**
     * Gets the value of validity as signing time.
     *
     * @param signatureCertificate allowed object is {@link eu.ecodex.dss.model.token.SignatureCertificate }
     * @return possible object is true or false
     */
    public boolean isValidationVerificationSignatureCertificateValidityAtSigningTime(SignatureCertificate signatureCertificate) {
        return (signatureCertificate != null) && signatureCertificate.isValidityAtSigningTime();
    }
}
