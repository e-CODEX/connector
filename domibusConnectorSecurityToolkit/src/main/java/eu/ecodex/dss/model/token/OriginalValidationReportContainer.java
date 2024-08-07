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

import jakarta.xml.bind.annotation.*;

import eu.europa.esig.dss.validation.reports.Reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
@XmlType(name = "SourceType", propOrder = { "any" })
public class OriginalValidationReportContainer implements Serializable {
    @XmlAnyElement(lax = true)
    protected List<Object> any;

    @XmlTransient
    private Reports reports;
    
    /**
     * Gets the value of the any property.
     * 
     * 
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the any property.
     * 
     * 
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getAny().add(newItem);
     * </pre>
     * 
     * 
     * @return Objects of the following type(s) are allowed in the list {@link Object }
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    public void setReports(Reports reports) {
		this.reports = reports;
		this.getAny().add(reports.getXmlDiagnosticData());
		this.getAny().add(reports.getXmlSimpleReport());
	}
    
    public Reports getReports() {
		return this.reports;
	}
    
    /**
     * a wrapper for an entry in the list that allows marshalling/unmarshalling for simple java types.
     */
    @XmlRootElement
    public static class SimpleTypeEntry {
        /**
         * the wrapped value (e.g. String et al)
         */
        @XmlElement
        public Object value;

        /**
         * default constructor
         */
        public SimpleTypeEntry() {
        }

        /**
         * assignment constructor
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
