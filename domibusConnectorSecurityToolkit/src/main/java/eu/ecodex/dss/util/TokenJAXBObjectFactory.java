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
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/util/TokenJAXBObjectFactory.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.util;

import eu.ecodex.dss.model.token.AuthenticationInformation;
import eu.ecodex.dss.model.token.LegalValidationResult;
import eu.ecodex.dss.model.token.OriginalValidationReportContainer;
import eu.ecodex.dss.model.token.Signature;
import eu.ecodex.dss.model.token.SignatureAttributes;
import eu.ecodex.dss.model.token.SignatureCertificate;
import eu.ecodex.dss.model.token.TechnicalValidationResult;
import eu.ecodex.dss.model.token.Token;
import eu.ecodex.dss.model.token.TokenDocument;
import eu.ecodex.dss.model.token.TokenIssuer;
import eu.ecodex.dss.model.token.TokenValidation;
import eu.ecodex.dss.model.token.ValidationVerification;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import lombok.NoArgsConstructor;

/**
 * This object contains factory methods for each Java content interface and Java element interface
 * generated in the eu.ecodex.dss.model.token package.
 *
 * <p>An ObjectFactory allows you to programmatically construct new instances of the Java
 * representation for XML content. The Java representation of XML content can consist of schema
 * derived interfaces and classes representing the binding of schema type definitions, element
 * declarations and model groups.  Factory methods for each of these are provided in this
 * class.</p>
 *
 * <p>DISCLAIMER: Project owner e-CODEX
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
@XmlRegistry
@NoArgsConstructor
public class TokenJAXBObjectFactory {
    private static final QName _TrustOkToken_QNAME = new QName("", "TrustOkToken");

    /**
     * Create an instance of {@link eu.ecodex.dss.model.token.Token }.
     *
     * @return the new instance of xsd type TokenType
     */
    public Token createToken() {
        return new Token();
    }

    /**
     * Create an instance of {@link eu.ecodex.dss.model.token.SignatureAttributes }.
     *
     * @return the new instance of xsd type SignatureInformationType
     */
    public SignatureAttributes createSignatureAttributes() {
        return new SignatureAttributes();
    }

    /**
     * Create an instance of {@link eu.ecodex.dss.model.token.TokenIssuer }.
     *
     * @return the new instance of xsd type IssuerType
     */
    public TokenIssuer createTokenIssuer() {
        return new TokenIssuer();
    }

    /**
     * Create an instance of {@link eu.ecodex.dss.model.token.ValidationVerification }.
     *
     * @return the new instance of xsd type VerificationDataType
     */
    public ValidationVerification createValidationVerification() {
        return new ValidationVerification();
    }

    /**
     * Create an instance of {@link eu.ecodex.dss.model.token.AuthenticationInformation }.
     *
     * @return the new instance of xsd type AuthenticationInformationType
     */
    public AuthenticationInformation createAuthenticationInformation() {
        return new AuthenticationInformation();
    }

    /**
     * Create an instance of {@link eu.ecodex.dss.model.token.TokenValidation }.
     *
     * @return the new instance of xsd type
     */
    public TokenValidation createTokenValidation() {
        return new TokenValidation();
    }

    /**
     * Create an instance of {@link eu.ecodex.dss.model.token.OriginalValidationReportContainer }.
     *
     * @return the new instance of xsd type
     */
    public OriginalValidationReportContainer createOriginalValidationReportContainer() {
        return new OriginalValidationReportContainer();
    }

    /**
     * Create an instance of {@link eu.ecodex.dss.model.token.SignatureCertificate }.
     *
     * @return the new instance of xsd type
     */
    public SignatureCertificate createSignatureCertificate() {
        return new SignatureCertificate();
    }

    /**
     * Create an instance of {@link eu.ecodex.dss.model.token.Signature }.
     *
     * @return the new instance
     */
    public Signature createSignature() {
        return new Signature();
    }

    /**
     * Create an instance of {@link eu.ecodex.dss.model.token.TokenDocument }.
     *
     * @return the new instance
     */
    public TokenDocument createTokenDocument() {
        return new TokenDocument();
    }

    /**
     * Create an instance of {@link eu.ecodex.dss.model.token.TechnicalValidationResult }.
     *
     * @return the new instance
     */
    public TechnicalValidationResult createTechnicalValidationResult() {
        return new TechnicalValidationResult();
    }

    /**
     * Create an instance of {@link eu.ecodex.dss.model.token.LegalValidationResult }.
     *
     * @return the new instance
     */
    public LegalValidationResult createLegalValidationResult() {
        return new LegalValidationResult();
    }

    /**
     * Create an instance of
     * {@link jakarta.xml.bind.JAXBElement }{@code &lt;}{@link
     * eu.ecodex.dss.model.token.Token}{@code &gt;}.
     *
     * @param value the token
     * @return the new instance representing the marshalled object
     */
    @XmlElementDecl(namespace = "", name = "TrustOkToken")
    public JAXBElement<Token> createTrustOkToken(final Token value) {
        return new JAXBElement<>(_TrustOkToken_QNAME, Token.class, null, value);
    }
}
