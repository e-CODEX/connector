/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/service/ECodexContainerService.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */
package eu.ecodex.dss.service;

import java.io.InputStream;

import eu.ecodex.dss.model.BusinessContent;
import eu.ecodex.dss.model.ECodexContainer;
import eu.ecodex.dss.model.EnvironmentConfiguration;
import eu.ecodex.dss.model.SignatureParameters;
import eu.ecodex.dss.model.checks.CheckResult;
import eu.ecodex.dss.model.token.TokenIssuer;

/**
 * the contract about handling the {@link eu.ecodex.dss.model.ECodexContainer}
 *
 * 
 * note that all the methods may - but only - throw a {@link eu.ecodex.dss.service.ECodexException}.
 * 
 *
 * <p>
 * DISCLAIMER: Project owner e-CODEX
 * </p>
 * 
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
public interface ECodexContainerService {

    /**
     * establishes the configuration (one time operation)
     * This shall also update the connector certificates.
     *
     * @param environmentConfiguration the value (may be null)
     */
//    void setEnvironmentConfiguration(final EnvironmentConfiguration environmentConfiguration);

    /**
     * configures the parameters for signing the ASiC-S container
     * 
     * @param signingParameters the signature parameters for signing the container itself (in signatures.xml) and the
     *            token (PDF + xml); based on P12 file
     */
//    void setContainerSignatureParameters(final SignatureParameters signingParameters);

    /**
     * sets the service creating the tokenvalidation and the pdf-appendix of the validation report; used in
     * {@link #create(eu.ecodex.dss.model.BusinessContent, eu.ecodex.dss.model.token.TokenIssuer)}.
     * 
     * @param validationService the value
     */
//    void setTechnicalValidationService(final ECodexTechnicalValidationService validationService);

    /**
     * sets the service providing a validation in legal context; used in
     * {@link #create(eu.ecodex.dss.model.BusinessContent, eu.ecodex.dss.model.token.TokenIssuer)}.
     * 
     * @param validationService the value
     */
//    void setLegalValidationService(final ECodexLegalValidationService validationService);

    /**
     * creates the container:
     * - creates the token
     * - creates and signs the PDF representation of the token
     * - creates and signs the eCodexContainer itself
     *
     * the implementation typically uses the {@link ECodexTechnicalValidationService} (set as attribute) to gather the validation
     * report both as model object and its PDF representation. it will check the created TokenValidation for correct
     * content (non-null values and some dependencies) - e.g. if authentication-based then validationverification must
     * have provided authenticationdata.
     *
     * 
     * IMPLEMENTORS: This method is allowed to throw only {@link ECodexException}!
     * 
     * @param businessContent the documents and attachments
     * 
     * @return the result
     * @throws ECodexException wrapper around any exception occurred
     */
    ECodexContainer create(final BusinessContent businessContent) throws ECodexException;

    /**
     * 
     * uses the inputstream representing the ASiC-S container (file or whatever = a zip)
     *
     * 
     * IMPLEMENTORS: This method is allowed to throw only {@link ECodexException}!
     * 
     * @param asicInputStream the stream representing the ASiC-S zip
     * @param tokenStream the stream representing the token xml
     * 
     * @return the result
     * @throws ECodexException wrapper around any exception occurred
     */
    ECodexContainer receive(final InputStream asicInputStream, final InputStream tokenStream) throws ECodexException;

    /**
     * checks the integrity of the ASiC-S container on base of the signatures.xml via the container's
     * {@link ECodexContainer#asicDocument} attribute.
     * read: it verifies that a) the signed content and the signature match. b) the token xml contains the (detached)
     * signature of the token pdf c) the token pdf is signed
     * 
     * 
     * IMPLEMENTORS: This method is allowed to throw only {@link ECodexException}!
     * 
     * @param container the object
     * 
     * @return the result
     * @throws ECodexException wrapper around any exception occurred
     */
    CheckResult check(final ECodexContainer container) throws ECodexException;

    /**
     * creates a new container instance having the same attribute instance like the provided container.
     * but adds a new signature created upon the SignedContent.zip to the META-INF/signatures.xml.
     * the new container instance has then a complete new ASiC document.
     *
     * 
     * IMPLEMENTORS: This method is allowed to throw only {@link ECodexException}!
     * 
     * @param container the container
     * 
     * @return a newly created instance on base of the original one including the new signature
     * @throws ECodexException wrapper around any exception occurred
     */
    ECodexContainer addSignature(final ECodexContainer container) throws ECodexException;

}
