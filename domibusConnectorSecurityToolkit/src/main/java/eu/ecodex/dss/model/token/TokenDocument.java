/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/model/token/TokenDocument.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.model.token;

import eu.europa.esig.xmldsig.jaxb.DigestMethodType;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import lombok.Data;

/**
 * This class holds a document to be transported.
 *
 * <p>
 * DISCLAIMER: Project owner e-CODEX
 * </p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "DocumentType",
    propOrder = {"filename", "type", "digestMethod", "digestValue", "signatureFilename"}
)
@Data
public class TokenDocument implements Serializable {
    @XmlElement(name = "Filename", required = true)
    protected String filename;
    @XmlElement(name = "Type", required = true)
    protected String type;
    @XmlElement(
        name = "DigestMethod", required = true, namespace = "http://www.w3.org/2000/09/xmldsig#"
    )
    protected DigestMethodType digestMethod;
    @XmlElement(
        name = "DigestValue", required = true, namespace = "http://www.w3.org/2000/09/xmldsig#"
    )
    protected byte[] digestValue;
    @XmlElement(name = "SignatureFilename")
    protected String signatureFilename;

    /**
     * Sets the value of the filename property.
     *
     * @param value allowed object is {@link String }
     * @return this class' instance for chaining
     */
    public TokenDocument setFilename(final String value) {
        this.filename = value;
        return this;
    }

    /**
     * Sets the value of the type property.
     *
     * @param value allowed object is {@link String}
     * @return this class' instance for chaining
     */
    public TokenDocument setType(final String value) {
        this.type = value;
        return this;
    }

    /**
     * Sets the value of the digestMethod property.
     *
     * @param digestMethod allowed object is {@link DigestMethodType}
     * @return this class' instance for chaining
     */
    public TokenDocument setDigestMethod(final DigestMethodType digestMethod) {
        this.digestMethod = digestMethod;
        return this;
    }

    /**
     * Sets the value of the digestMethod digestValue.
     *
     * @param digestValue allowed object is {@link Byte} array
     * @return this class' instance for chaining
     */
    public TokenDocument setDigestValue(final byte[] digestValue) {
        this.digestValue = digestValue;
        return this;
    }

    /**
     * Sets the value of the signaturefilename property.
     *
     * @param value allowed object is {@link String }
     * @return this class' instance for chaining
     */
    public TokenDocument setSignatureFilename(final String value) {
        this.signatureFilename = value;
        return this;
    }
}
