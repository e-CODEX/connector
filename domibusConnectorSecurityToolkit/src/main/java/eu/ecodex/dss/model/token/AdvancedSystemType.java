/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/model/token/AdvancedSystemType.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.model.token;

import org.apache.commons.lang.StringUtils;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

/**
 * This class holds the type of the Advanced Electronic System using the e-CODEX DSS
 * 
 * <p>
 * DISCLAIMER: Project owner e-CODEX
 * </p>
 * 
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
@XmlType(name = "AdvancedSystemEnum")
@XmlEnum
public enum AdvancedSystemType {

    @XmlEnumValue("Signature-based")
    SIGNATURE_BASED("Signature-based", "Signature-based"),

    @XmlEnumValue("Authentication-based")
    AUTHENTICATION_BASED("Authentication-based", "Authentication-based");

    private final String value;
    private final String text;

    /**
     * constructor
     * 
     * @param value the value "Signature-based" or "Authentication-based"
     * @param text the textual representation "Signature-based" or "Authentication-based"
     */
    AdvancedSystemType(final String value, final String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * the underlying string value
     * @return "Signature-based" or "Authentication-based"
     */
    public String getValue() {
        return value;
    }

    /**
     * the underlying string value
     * @return "Signature-based" or "Authentication-based"
     */
    public String getText() {
        return text;
    }

    /**
     * factory retrieval method; if the instance is not found, then an IllegalArgumentException is thrown.
     * 
     * @param v either "Authentication-based" or "Authentication-based"
     * @return the enum instance
     */
    public static AdvancedSystemType fromValue(final String v) {
        if (StringUtils.isEmpty(v)) {
            throw new IllegalArgumentException("value must not be empty");
        }
        for (AdvancedSystemType c : AdvancedSystemType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
