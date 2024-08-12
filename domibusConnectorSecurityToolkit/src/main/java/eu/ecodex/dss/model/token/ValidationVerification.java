/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
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
