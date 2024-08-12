/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/model/token/TokenIssuer.java $
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
import lombok.Data;
import org.springframework.core.style.ToStringCreator;

/**
 * This class holds the data about the Issuer.
 *
 * <p>DISCLAIMER: Project owner e-CODEX</p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "IssuerType", propOrder = {"serviceProvider", "country", "advancedElectronicSystem"}
)
@Data
public class TokenIssuer implements Serializable {
    @XmlElement(name = "ServiceProvider", required = true)
    protected String serviceProvider;
    @XmlElement(name = "Country", required = true)
    protected String country;
    @XmlElement(name = "AdvancedElectronicSystem", required = true)
    protected AdvancedSystemType advancedElectronicSystem;

    /**
     * Sets the value of the serviceProvider property.
     *
     * @param value allowed object is {@link String }
     * @return this class' instance for chaining
     */
    public TokenIssuer setServiceProvider(final String value) {
        this.serviceProvider = value;
        return this;
    }

    /**
     * Sets the value of the country property.
     *
     * @param value allowed object is {@link String }
     * @return this class' instance for chaining
     */
    public TokenIssuer setCountry(final String value) {
        this.country = value;
        return this;
    }

    /**
     * Sets the value of the advancedElectronicSystem property.
     *
     * @param value allowed object is {@link eu.ecodex.dss.model.token.AdvancedSystemType }
     * @return this class' instance for chaining
     */
    public TokenIssuer setAdvancedElectronicSystem(final AdvancedSystemType value) {
        this.advancedElectronicSystem = value;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)
            .append("serviceProvider", this.serviceProvider)
            .append("country", this.country)
            .append("advancedElectronicSystem", this.advancedElectronicSystem)
            .toString();
    }
}
