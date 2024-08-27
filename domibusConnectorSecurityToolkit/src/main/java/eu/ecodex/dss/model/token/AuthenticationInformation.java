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
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/model/token/AuthenticationInformation.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.model.token;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.Getter;

/**
 * This class holds the data about the AuthenticationInformation.
 *
 * <p>DISCLAIMER: Project owner e-CODEX
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "AuthenticationInformationType",
    propOrder = {"identityProvider", "usernameSynonym", "timeOfAuthentication"}
)
@Getter
public class AuthenticationInformation implements Serializable {
    @XmlElement(name = "IdentityProvider", required = true)
    protected String identityProvider;
    @XmlElement(name = "UsernameSynonym", required = true)
    protected String usernameSynonym;
    @XmlElement(name = "TimeOfAuthentication", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timeOfAuthentication;

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
