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

import jakarta.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.Serializable;

/**
 * This class holds the data about the Validation.
 * 
 * <p>DISCLAIMER: Project owner e-CODEX</p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ValidationType", propOrder = {"verificationTime", "verificationData", "technicalResult", "legalResult", "originalValidationReport"})
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
     * Gets the value of the verificationTime property.
     *
     * @return possible object is {@link javax.xml.datatype.XMLGregorianCalendar }
     */
    public XMLGregorianCalendar getVerificationTime() {
        return verificationTime;
    }

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
     * Gets the value of the verificationData property.
     *
     * @return possible object is {@link eu.ecodex.dss.model.token.ValidationVerification }
     */
    public ValidationVerification getVerificationData() {
        return verificationData;
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
     * Gets the value of the result property.
     *
     * @return possible object is {@link TechnicalValidationResult }
     */
    public TechnicalValidationResult getTechnicalResult() {
        return technicalResult;
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
     * Gets the value of the result property.
     *
     * @return possible object is {@link LegalValidationResult }
     */
    public LegalValidationResult getLegalResult() {
        return legalResult;
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
     * Gets the value of the originalValidationReport property.
     *
     * @return possible object is {@link eu.ecodex.dss.model.token.OriginalValidationReportContainer }
     */
    public OriginalValidationReportContainer getOriginalValidationReport() {
        return originalValidationReport;
    }

    /**
     * Sets the value of the originalValidationReport property.
     *
     * @param value allowed object is {@link eu.ecodex.dss.model.token.OriginalValidationReportContainer }
     * @return this class' instance for chaining
     */
    public TokenValidation setOriginalValidationReport(final OriginalValidationReportContainer value) {
        this.originalValidationReport = value;
        return this;
    }

}
