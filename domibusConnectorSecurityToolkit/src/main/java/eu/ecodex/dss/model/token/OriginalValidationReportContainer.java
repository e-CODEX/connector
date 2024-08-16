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
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/model/token/OriginalValidationReportContainer.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.model.token;

import eu.europa.esig.dss.validation.reports.Reports;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * This class holds the details of the validation report itself (from DSS).
 *
 * <p>
 * DISCLAIMER: Project owner e-CODEX
 * </p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SourceType", propOrder = {"any"})
@Getter
public class OriginalValidationReportContainer implements Serializable {
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlTransient
    private Reports reports;

    /**
     * Gets the value of the any property.
     *
     * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore, any
     * modification you make to the returned list will be present inside the JAXB object. This is
     * why there is not a <CODE>set</CODE> method for the any property.
     *
     * <p>For example, to add a new item, do as follows:
     *
     * <pre>
     * getAny().add(newItem);
     * </pre>
     *
     * @return Objects of the following type(s) are allowed in the list {@link Object }
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<>();
        }
        return this.any;
    }

    /**
     * Sets the value of the reports property.
     *
     * <p>This method sets the value of the reports property in the
     * OriginalValidationReportContainer object.
     * It assigns the provided Reports object to the reports property and adds the xmlDiagnosticData
     * and xmlSimpleReport from the Reports object to the any property of the
     * OriginalValidationReportContainer object.</p>
     *
     * @param reports The Reports object containing the xmlDiagnosticData and xmlSimpleReport to be
     *                added.
     * @see OriginalValidationReportContainer#setReports
     */
    public void setReports(Reports reports) {
        this.reports = reports;
        this.getAny().add(reports.getXmlDiagnosticData());
        this.getAny().add(reports.getXmlSimpleReport());
    }

    /**
     * a wrapper for an entry in the list that allows marshalling/unmarshalling for simple java
     * types.
     */
    @XmlRootElement
    @NoArgsConstructor
    public static class SimpleTypeEntry {
        /**
         * The wrapped value (e.g. String et al).
         */
        @XmlElement
        public Object value;

        /**
         * Assignment constructor.
         *
         * @param value the value
         */
        public SimpleTypeEntry(final Object value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }
}
