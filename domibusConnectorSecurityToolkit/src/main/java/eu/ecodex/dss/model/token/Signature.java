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
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/model/token/Signature.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.model.token;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.Data;

/**
 * This class holds information about the signature.
 *
 * <p>http://www.jira.e-codex.eu/browse/ECDX-45: is/setUnsigned();
 *
 * <p>DISCLAIMER: Project owner e-CODEX</p>
 *
 * <p>{@literal @XmlAccessorType(XmlAccessType.FIELD) changed to -->
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * @XmlAccessorType(XmlAccessType.NONE) to allow to handle unsigned property. }
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(
    name = "SignatureDataType",
    propOrder = {"signingTime", "signatureInformation", "certificateInformation", "technicalResult"}
)
@Data
public class Signature implements Serializable {
    @XmlElement(name = "SigningTime")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar signingTime;
    @XmlElement(name = "SignatureInformation")
    protected SignatureAttributes signatureInformation;
    @XmlElement(name = "CertificateInformation")
    protected SignatureCertificate certificateInformation;
    @XmlElement(name = "TechnicalResult")
    protected TechnicalValidationResult technicalResult;
    protected AuthenticationCertificate authenticationCertValidation;

    /**
     * Sets the value of the authenticationCertValidation property.
     *
     * @param authenticationCertValidation the value
     * @return this class' instance for chaining
     */
    public Signature setAuthenticationCertValidation(
        AuthenticationCertificate authenticationCertValidation) {
        this.authenticationCertValidation = authenticationCertValidation;
        return this;
    }

    /**
     * Sets the value of the signingTime property.
     *
     * @param value allowed object is {@link javax.xml.datatype.XMLGregorianCalendar }
     * @return this class' instance for chaining
     */
    public Signature setSigningTime(final XMLGregorianCalendar value) {
        this.signingTime = value;
        return this;
    }

    /**
     * Sets the value of the signatureInformation property.
     *
     * @param value allowed object is {@link eu.ecodex.dss.model.token.SignatureAttributes }
     * @return this class' instance for chaining
     */
    public Signature setSignatureInformation(final SignatureAttributes value) {
        this.signatureInformation = value;
        return this;
    }

    /**
     * Sets the value of the certificateInformation property.
     *
     * @param value allowed object is {@link eu.ecodex.dss.model.token.SignatureCertificate }
     * @return this class' instance for chaining
     */
    public Signature setCertificateInformation(final SignatureCertificate value) {
        this.certificateInformation = value;
        return this;
    }
}
