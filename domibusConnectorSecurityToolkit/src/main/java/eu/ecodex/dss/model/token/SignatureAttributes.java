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
import lombok.Data;

/**
 * This class holds information about the signature.
 *
 * <p>DISCLAIMER: Project owner e-CODEX</p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "SignatureInformationType",
    propOrder = {"signatureValid", "structureValid", "signatureFormat", "signatureLevel"}
)
@Data
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
