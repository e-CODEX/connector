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
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/model/token/LegalValidationResult.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.model.token;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import lombok.Getter;

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
@Getter
public class LegalValidationResult implements Serializable {
    @XmlElement(name = "TrustLevel", required = true)
    protected LegalTrustLevel trustLevel;
    @XmlElement(name = "Disclaimer")
    protected String disclaimer;

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
