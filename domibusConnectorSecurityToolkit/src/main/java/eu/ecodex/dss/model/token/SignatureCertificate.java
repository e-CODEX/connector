/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/model/token/SignatureCertificate.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.model.token;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * This class holds the configuration about the CertificateInformation.
 * 
 * <p>DISCLAIMER: Project owner e-CODEX</p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CertificateInformationType", propOrder = {"subject", "issuer", "certificateValid", "validityAtSigningTime"})
public class SignatureCertificate implements Serializable {

    @XmlElement(name = "Subject", required = false)
    protected String subject;
    @XmlElement(name = "Issuer", required = true)
    protected String issuer;
    @XmlElement(name = "CertificateVerification")
    protected boolean certificateValid;
    @XmlElement(name = "ValidityAtSigningTime")
    protected boolean validityAtSigningTime;

    /**
     * Gets the value of the issuer property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the value of the issuer property.
     *
     * @param value allowed object is {@link String }
     * @return this class' instance for chaining
     */
    public SignatureCertificate setSubject(final String value) {
        this.subject = value;
        return this;
    }    
    
    /**
     * Gets the value of the issuer property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * Sets the value of the issuer property.
     *
     * @param value allowed object is {@link String }
     * @return this class' instance for chaining
     */
    public SignatureCertificate setIssuer(final String value) {
        this.issuer = value;
        return this;
    }

    /**
     * Gets the value of the certificateValid property.
     * 
     * @return the value
     */
    public boolean isCertificateValid() {
        return certificateValid;
    }

    /**
     * Sets the value of the certificateValid property.
     * 
     * @param value the value
     * @return this class' instance for chaining
     */
    public SignatureCertificate setCertificateValid(final boolean value) {
        this.certificateValid = value;
        return this;
    }

    /**
     * Gets the value of the validityAtSigningTime property.
     * 
     * @return the value
     */
    public boolean isValidityAtSigningTime() {
        return validityAtSigningTime;
    }

    /**
     * Sets the value of the validityAtSigningTime property.
     * 
     * @param value the value
     * @return this class' instance for chaining
     */
    public SignatureCertificate setValidityAtSigningTime(final boolean value) {
        this.validityAtSigningTime = value;
        return this;
    }

}
