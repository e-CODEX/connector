/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/model/token/SignatureAttributes.java $
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
 * This class holds information about the signature.
 * 
 * <p>DISCLAIMER: Project owner e-CODEX</p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SignatureInformationType", propOrder = { "signatureValid", "structureValid", "signatureFormat", "signatureLevel"})
public class SignatureAttributes implements Serializable {

    @XmlElement(name = "SignatureVerification")
    protected boolean signatureValid;
    @XmlElement(name = "StructureVerification")
    protected boolean structureValid;
    @XmlElement(name = "SignatureFormat", required = true)
    protected String signatureFormat;
    @XmlElement(name = "SignatureLevel")
    protected String signatureLevel;

    /**
     * Gets the value of the signatureValid property.
     * 
     * @return the value
     */
    public boolean isSignatureValid() {
        return signatureValid;
    }

    /**
     * Sets the value of the signatureValid property.
     * 
     * @param value the value
     * @return this class' instance for chaining
     */
    public SignatureAttributes setSignatureValid(final boolean value) {
        this.signatureValid = value;
        return this;
    }

    /**
     * Gets the value of the structureValid property.
     * 
     * @return the value
     */
    public boolean isStructureValid() {
        return structureValid;
    }

    /**
     * Sets the value of the structureValid property.
     * 
     * @param value the value
     * @return this class' instance for chaining
     */
    public SignatureAttributes setStructureValid(final boolean value) {
        this.structureValid = value;
        return this;
    }

    /**
     * Gets the value of the signatureFormat property.
     *
     * @return possible object is {@link String }
     */
    public String getSignatureFormat() {
        return signatureFormat;
    }

    /**
     * Sets the value of the signatureFormat property.
     *
     * @param value allowed object is {@link String }
     * @return this class' instance for chaining
     */
    public SignatureAttributes setSignatureFormat(final String value) {
        this.signatureFormat = value;
        return this;
    }

    /**
     * Gets the value of the signatureLevel property.
     *
     * @return possible object is {@link String }
     */
    public String getSignatureLevel() {
        return signatureLevel;
    }

    /**
     * Sets the value of the signatureLevel property.
     *
     * @param value allowed object is {@link String }
     * @return this class' instance for chaining
     */
    public SignatureAttributes setSignatureLevel(final String value) {
        this.signatureLevel = value;
        return this;
    }

}
