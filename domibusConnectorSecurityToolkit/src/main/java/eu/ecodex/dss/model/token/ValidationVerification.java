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
public class ValidationVerification implements Serializable {

    @XmlElement(name = "SignatureData")
    protected List<Signature> signatureData;
    @XmlElement(name = "AuthenticationData")
    protected AuthenticationInformation authenticationData;

    /**
     * Gets the value of the signatureData property.
     *
     * @return possible object is {@link eu.ecodex.dss.model.token.Signature }
     */
    public List<Signature> getSignatureData() {
        return signatureData;
    }

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
        if(this.signatureData == null){
        	this.signatureData = new ArrayList<Signature>();
        }
        
        if(value != null) {
        	this.signatureData.add(value);
        }
        return this;
    }
    
    /**
     * Gets the value of the authenticationData property.
     *
     * @return possible object is {@link eu.ecodex.dss.model.token.AuthenticationInformation }
     */
    public AuthenticationInformation getAuthenticationData() {
        return authenticationData;
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
