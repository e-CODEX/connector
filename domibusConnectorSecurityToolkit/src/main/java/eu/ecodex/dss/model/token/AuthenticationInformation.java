/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/model/token/AuthenticationInformation.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.model.token;

import jakarta.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.Serializable;

/**
 * This class holds the data about the AuthenticationInformation.
 * 
 * <p>DISCLAIMER: Project owner e-CODEX</p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthenticationInformationType", propOrder = {"identityProvider", "usernameSynonym", "timeOfAuthentication"})
public class AuthenticationInformation implements Serializable {

    @XmlElement(name = "IdentityProvider", required = true)
    protected String identityProvider;
    @XmlElement(name = "UsernameSynonym", required = true)
    protected String usernameSynonym;
    @XmlElement(name = "TimeOfAuthentication", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timeOfAuthentication;

    /**
     * Gets the value of the identityProvider property.
     *
     * @return possible object is {@link String }
     */
    public String getIdentityProvider() {
        return identityProvider;
    }

    /**
     * Sets the value of the identityProvider property.
     *
     * @param value allowed object is {@link String }
     * @return this class' instance for chaining
     */
    public AuthenticationInformation setIdentityProvider(final String value) {
        this.identityProvider = value;
        return this;
    }

    /**
     * Gets the value of the usernameSynonym property.
     *
     * @return possible object is {@link String }
     */
    public String getUsernameSynonym() {
        return usernameSynonym;
    }

    /**
     * Sets the value of the usernameSynonym property.
     *
     * @param value allowed object is {@link String }
     * @return this class' instance for chaining
     */
    public AuthenticationInformation setUsernameSynonym(final String value) {
        this.usernameSynonym = value;
        return this;
    }

    /**
     * Gets the value of the timeOfAuthentication property.
     *
     * @return possible object is {@link javax.xml.datatype.XMLGregorianCalendar }
     */
    public XMLGregorianCalendar getTimeOfAuthentication() {
        return timeOfAuthentication;
    }

    /**
     * Sets the value of the timeOfAuthentication property.
     *
     * @param value allowed object is {@link javax.xml.datatype.XMLGregorianCalendar }
     * @return this class' instance for chaining
     */
    public AuthenticationInformation setTimeOfAuthentication(final XMLGregorianCalendar value) {
        this.timeOfAuthentication = value;
        return this;
    }

}
