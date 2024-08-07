/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/model/token/TechnicalValidationResult.java $
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
@XmlType(name = "TechnicalResultType", propOrder = {"trustLevel", "comment" })
public class TechnicalValidationResult implements Serializable {

    @XmlElement(name = "TrustLevel", required = true)
    protected TechnicalTrustLevel trustLevel;
    @XmlElement(name = "Comments")
    protected String comment;

    /**
     * Gets the value of the trustLevel property.
     *
     * @return possible object is {@link TechnicalTrustLevel }
     */
    public TechnicalTrustLevel getTrustLevel() {
        return trustLevel;
    }

    /**
     * Sets the value of the trustLevel property.
     *
     * @param value allowed object is {@link TechnicalTrustLevel }
     * @return this class' instance for chaining
     */
    public TechnicalValidationResult setTrustLevel(final TechnicalTrustLevel value) {
        this.trustLevel = value;
        return this;
    }

    /**
     * Gets the value of the comment property.
     *
     * @return possible object is {@link String }
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the value of the comment property.
     *
     * @param value allowed object is {@link String }
     * @return this class' instance for chaining
     */
    public TechnicalValidationResult setComment(final String value) {
        this.comment = value;
        return this;
    }

}
