/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/model/token/Signature.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.model.token;

import java.io.Serializable;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * This class holds information about the signature.
 *
 * <p>DISCLAIMER: Project owner e-CODEX</p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */

/**
 * http://www.jira.e-codex.eu/browse/ECDX-45: is/setUnsigned();
 *
 * {@literal @XmlAccessorType(XmlAccessType.FIELD) changed to -->  @XmlAccessorType(XmlAccessType.NONE) to allow to handle unsigned property. }
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "SignatureDataType", propOrder = {"signingTime", "signatureInformation", "certificateInformation", "technicalResult"})
public class Signature implements Serializable {

    @XmlElement(name = "SigningTime")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar signingTime;
    @XmlElement(name = "SignatureInformation")
    protected SignatureAttributes signatureInformation;
    @XmlElement(name = "CertificateInformation")
    protected SignatureCertificate certificateInformation;
    @XmlElement(name = "TechnicalResult")
    protected TechnicalValidationResult technicalResult;
    
    public TechnicalValidationResult getTechnicalResult() {
		return technicalResult;
	}

	public void setTechnicalResult(TechnicalValidationResult technicalResult) {
		this.technicalResult = technicalResult;
	}

	protected AuthenticationCertificate authenticationCertValidation;

    /**
     * Gets the value of the authenticationCertValidation property.
     *
     * @return possible object is {@link eu.ecodex.dss.model.token.AuthenticationCertificate }
     */   
    public AuthenticationCertificate getAuthenticationCertValidation() {
		return authenticationCertValidation;
	}

    /**
     * Sets the value of the authenticationCertValidation property.
     *
     * @param authenticationCertValidation the value
     * @return this class' instance for chaining
     */    
	public Signature setAuthenticationCertValidation(AuthenticationCertificate authenticationCertValidation) {
		this.authenticationCertValidation = authenticationCertValidation;
		return this;
	}    

    /**
     * Gets the value of the signingTime property.
     *
     * @return possible object is {@link javax.xml.datatype.XMLGregorianCalendar }
     */
    public XMLGregorianCalendar getSigningTime() {
        return signingTime;
    }

    /**
     * Sets the value of the signingTime property.
     *
     * @param value allowed object is {@link javax.xml.datatype.XMLGregorianCalendar }
     * @return this class' instance for chaining
     */
    public Signature setSigningTime(final XMLGregorianCalendar value) {
        this.signingTime = value;
        return this;
    }

    /**
     * Gets the value of the signatureInformation property.
     *
     * @return possible object is {@link eu.ecodex.dss.model.token.SignatureAttributes }
     */
    public SignatureAttributes getSignatureInformation() {
        return signatureInformation;
    }

    /**
     * Sets the value of the signatureInformation property.
     *
     * @param value allowed object is {@link eu.ecodex.dss.model.token.SignatureAttributes }
     * @return this class' instance for chaining
     */
    public Signature setSignatureInformation(final SignatureAttributes value) {
        this.signatureInformation = value;
        return this;
    }

    /**
     * Gets the value of the certificateInformation property.
     *
     * @return possible object is {@link eu.ecodex.dss.model.token.SignatureCertificate }
     */
    public SignatureCertificate getCertificateInformation() {
        return certificateInformation;
    }

    /**
     * Sets the value of the certificateInformation property.
     *
     * @param value allowed object is {@link eu.ecodex.dss.model.token.SignatureCertificate }
     * @return this class' instance for chaining
     */
    public Signature setCertificateInformation(final SignatureCertificate value) {
        this.certificateInformation = value;
        return this;
    }

}
