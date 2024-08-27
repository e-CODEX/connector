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
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/model/token/TokenValidation.java $
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
import lombok.Data;

/**
 * This class holds the data about the Validation.
 *
 * <p>DISCLAIMER: Project owner e-CODEX</p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "ValidationType",
    propOrder = {"verificationTime", "verificationData", "technicalResult", "legalResult",
        "originalValidationReport"}
)
@Data
public class TokenValidation implements Serializable {
    @XmlElement(name = "VerificationTime", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar verificationTime;
    @XmlElement(name = "VerificationData", required = true)
    protected ValidationVerification verificationData;
    @XmlElement(name = "TechnicalResult", required = true)
    protected TechnicalValidationResult technicalResult;
    @XmlElement(name = "LegalResult", required = true)
    protected LegalValidationResult legalResult;
    @XmlElement(name = "OriginalValidationReport")
    protected OriginalValidationReportContainer originalValidationReport;

    /**
     * Sets the value of the verificationTime property.
     *
     * @param value allowed object is {@link javax.xml.datatype.XMLGregorianCalendar }
     * @return this class' instance for chaining
     */
    public TokenValidation setVerificationTime(final XMLGregorianCalendar value) {
        this.verificationTime = value;
        return this;
    }

    /**
     * Sets the value of the verificationData property.
     *
     * @param value allowed object is {@link eu.ecodex.dss.model.token.ValidationVerification }
     * @return this class' instance for chaining
     */
    public TokenValidation setVerificationData(final ValidationVerification value) {
        this.verificationData = value;
        return this;
    }

    /**
     * Sets the value of the result property.
     *
     * @param value allowed object is {@link TechnicalValidationResult }
     * @return this class' instance for chaining
     */
    public TokenValidation setTechnicalResult(final TechnicalValidationResult value) {
        this.technicalResult = value;
        return this;
    }

    /**
     * Sets the value of the result property.
     *
     * @param value allowed object is {@link LegalValidationResult }
     * @return this class' instance for chaining
     */
    public TokenValidation setLegalResult(final LegalValidationResult value) {
        this.legalResult = value;
        return this;
    }

    /**
     * Sets the value of the originalValidationReport property.
     *
     * @param value allowed object is
     *              {@link eu.ecodex.dss.model.token.OriginalValidationReportContainer }
     * @return this class' instance for chaining
     */
    public TokenValidation setOriginalValidationReport(
        final OriginalValidationReportContainer value) {
        this.originalValidationReport = value;
        return this;
    }
}
