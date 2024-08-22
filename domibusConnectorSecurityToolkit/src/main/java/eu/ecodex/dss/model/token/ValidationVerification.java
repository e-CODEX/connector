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
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/model/token/ValidationVerification.java $
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
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * This class holds information about the verification.
 *
 * <p>DISCLAIMER: Project owner e-CODEX</p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VerificationDataType", propOrder = {"signatureData", "authenticationData"})
@Data
public class ValidationVerification implements Serializable {
    @XmlElement(name = "SignatureData")
    protected List<Signature> signatureData;
    @XmlElement(name = "AuthenticationData")
    protected AuthenticationInformation authenticationData;

    /**
     * Sets the value of the signatureData property.
     *
     * @param value allowed object is {@link eu.ecodex.dss.model.token.Signature }
     * @return this class' instance for chaining
     */
    public ValidationVerification setSignatureData(final List<Signature> value) {
        this.signatureData = value;
        return this;
    }

    /**
     * Sets the signature data for the validation verification.
     *
     * @param value the signature data to add
     * @return this ValidationVerification instance
     * @deprecated This method is deprecated and should not be used. Use the addSignatureData method
     *      instead.
     */
    @Deprecated
    public ValidationVerification setSignatureData(final Signature value) {
        this.addSignatureData(value);
        return this;
    }

    /**
     * Adds the value to the signatureData property.
     *
     * @param value allowed object is {@link eu.ecodex.dss.model.token.Signature }
     * @return this class' instance for chaining
     */
    public ValidationVerification addSignatureData(final Signature value) {
        if (this.signatureData == null) {
            this.signatureData = new ArrayList<>();
        }

        if (value != null) {
            this.signatureData.add(value);
        }
        return this;
    }

    /**
     * Sets the value of the authenticationData property.
     *
     * @param value allowed object is {@link eu.ecodex.dss.model.token.AuthenticationInformation }
     * @return this class' instance for chaining
     */
    public ValidationVerification setAuthenticationData(final AuthenticationInformation value) {
        this.authenticationData = value;
        return this;
    }
}
