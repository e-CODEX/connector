/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/model/token/LegalValidationResult.java $
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
 * This class holds the data about the ValidationResult.
 * 
 * <p>DISCLAIMER: Project owner e-CODEX</p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LegalResultType", propOrder = {"trustLevel", "disclaimer"})
public class LegalValidationResult implements Serializable {

    @XmlElement(name = "TrustLevel", required = true)
    protected LegalTrustLevel trustLevel;
    @XmlElement(name = "Disclaimer")
    protected String disclaimer;

    /**
     * Gets the value of the trustLevel property.
     *
     * @return possible object is {@link LegalTrustLevel }
     */
    public LegalTrustLevel getTrustLevel() {
        return trustLevel;
    }

    /**
     * Sets the value of the trustLevel property.
     *
     * @param value allowed object is {@link LegalTrustLevel }
     * @return this class' instance for chaining
     */
    public LegalValidationResult setTrustLevel(final LegalTrustLevel value) {
        this.trustLevel = value;
        return this;
    }

    /**
     * Gets the value of the disclaimer property.
     *
     * @return possible object is {@link String }
     */
    public String getDisclaimer() {
        return disclaimer;
    }

    /**
     * Sets the value of the comment property.
     *
     * @param value allowed object is {@link String }
     * @return this class' instance for chaining
     */
    public LegalValidationResult setDisclaimer(final String value) {
        this.disclaimer = value;
        return this;
    }

}
