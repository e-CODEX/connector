/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.security.aes;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.security.configuration.DCBusinessDocumentValidationConfigurationProperties;
import eu.ecodex.dss.model.token.AuthenticationInformation;
import eu.ecodex.dss.model.token.OriginalValidationReportContainer;
import eu.ecodex.dss.model.token.TechnicalTrustLevel;
import eu.ecodex.dss.model.token.TechnicalValidationResult;
import eu.ecodex.dss.model.token.Token;
import eu.ecodex.dss.model.token.TokenValidation;
import eu.ecodex.dss.model.token.ValidationVerification;
import eu.ecodex.dss.service.ECodexException;
import eu.ecodex.dss.service.ECodexTechnicalValidationService;
import eu.ecodex.dss.util.PdfValidationReportService;
import eu.europa.esig.dss.enumerations.MimeTypeEnum;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import java.io.ByteArrayOutputStream;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeFactory;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * The OriginalSenderBasedAESAuthenticationServiceFactory class is responsible for creating
 * instances of the ECodexTechnicalValidationService interface for performing authentication-based
 * technical validation. It implements the DCAuthenticationBasedTechnicalValidationServiceFactory
 * interface.
 *
 * <p>This class is annotated with the @Service annotation, indicating that it is a Spring service
 * component.
 *
 * <p>The OriginalSenderBasedAESAuthenticationServiceFactory class contains an inner class
 * DomibusConnectorAESTechnicalValidationService that implements the
 * ECodexTechnicalValidationService interface for performing technical validation on the main
 * business document.
 *
 * <p>The DomibusConnectorAESTechnicalValidationService class has two constructors. It takes a
 * DomibusConnectorMessage object and a DomibusConnectorAESTokenValidationCreator object as
 * parameters. It provides implementations for the create and createReportPDF methods declared in
 * the ECodexTechnicalValidationService interface.
 *
 * <p>The DomibusConnectorAESTechnicalValidationService class also contains a private method
 * createReportPDFImpl that is used internally to create a PDF report based on the token passed as
 * parameter.
 *
 * <p>The OriginalSenderBasedAESAuthenticationServiceFactory class also contains an inner class
 * DomibusConnectorAESTokenValidationCreator that is responsible for creating instances of the
 * TokenValidation class that represents the technical validation result and report. It takes a
 * String identityProvider as parameter in its constructor and provides an implementation for the
 * createTokenValidation method.
 *
 * <p>Note: This class does not provide any usage examples or code fragments, and is not tagged
 * with
 *
 * @see DCAuthenticationBasedTechnicalValidationServiceFactory
 * @see ECodexTechnicalValidationService
 * @see DomibusConnectorMessage
 * @see DCBusinessDocumentValidationConfigurationProperties
 *      .AuthenticationValidationConfigurationProperties
 * @see TokenValidation
 */
@SuppressWarnings("checkstyle:LineLength")
@Service
public class OriginalSenderBasedAESAuthenticationServiceFactory
    implements DCAuthenticationBasedTechnicalValidationServiceFactory {
    @Override
    public ECodexTechnicalValidationService createTechnicalValidationService(
        DomibusConnectorMessage message,
        DCBusinessDocumentValidationConfigurationProperties
            .AuthenticationValidationConfigurationProperties config) {
        var domibusConnectorAESTokenValidationCreator =
            new DomibusConnectorAESTokenValidationCreator(config.getIdentityProvider());
        return new DomibusConnectorAESTechnicalValidationService(
            message, domibusConnectorAESTokenValidationCreator);
    }

    static class DomibusConnectorAESTechnicalValidationService
        implements ECodexTechnicalValidationService {
        private static final Logger LOGGER =
            LoggerFactory.getLogger(DomibusConnectorAESTechnicalValidationService.class);
        private final DomibusConnectorAESTokenValidationCreator delegate;
        private final DomibusConnectorMessage message;

        public DomibusConnectorAESTechnicalValidationService(
            final DomibusConnectorMessage message,
            final DomibusConnectorAESTokenValidationCreator delegate) {
            super();
            this.message = message;
            this.delegate = delegate;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TokenValidation create(
            final DSSDocument businessDocument, final DSSDocument detachedSignature)
            throws ECodexException {
            LOGGER.debug(
                "create businessDocument: [{}] detachedSignature: [{}]", businessDocument,
                detachedSignature
            );
            try {
                return delegate.createTokenValidation(message);
            } catch (Exception e) {
                throw ECodexException.wrap(e);
            }
        }

        /**
         * {@inheritDoc}
         *
         * <p>The report must contain exactly one object of type
         */
        @Override
        public DSSDocument createReportPDF(final Token token) throws ECodexException {
            try {
                return createReportPDFImpl(token);
            } catch (Exception e) {
                throw ECodexException.wrap(e);
            }
        }

        private DSSDocument createReportPDFImpl(final Token token) throws ECodexException {
            if (token == null) {
                throw new ECodexException("the token (in parameter) must not be null");
            }
            final var tokenValidation = token.getValidation();
            if (tokenValidation == null) {
                throw new ECodexException("the token (in parameter) must have a validation object");
            }
            final ValidationVerification tokenVerificationData =
                tokenValidation.getVerificationData();
            if (tokenVerificationData == null) {
                throw new ECodexException(
                    "the token (in parameter) must have a validation object with an existing "
                        + "verification data"
                );
            }

            final OriginalValidationReportContainer report =
                tokenValidation.getOriginalValidationReport();

            if (report == null) {
                // return an empty document
                return new InMemoryDocument(new byte[0]);
            }

            final List<Object> reportData = report.getAny();
            if (reportData == null || reportData.isEmpty()) {
                // return an empty document
                return new InMemoryDocument(new byte[0]);
            }

            final var pdfStream = new ByteArrayOutputStream();

            try {
                if (report.getReports() != null) {
                    var reports = report.getReports();
                    final var diagnosticData = reports.getDiagnosticData();
                    final var simpleReport = reports.getSimpleReport();
                    // create and write the pdf version to the stream
                    final var pdfService = new PdfValidationReportService();
                    pdfService.createReport(diagnosticData, simpleReport, pdfStream);
                    return new InMemoryDocument(
                        pdfStream.toByteArray(), "dss-report.pdf", MimeTypeEnum.PDF);
                } else {
                    return new InMemoryDocument(new byte[0]);
                }
            } catch (Exception e) {
                throw ECodexException.wrap(e);
            } finally {
                IOUtils.closeQuietly(pdfStream);
            }
        }
    }

    static class DomibusConnectorAESTokenValidationCreator {
        private static final org.apache.logging.log4j.Logger LOGGER =
            LogManager.getLogger(DomibusConnectorAESTokenValidationCreator.class);
        private final String identityProvider;

        public DomibusConnectorAESTokenValidationCreator(String identityProvider) {
            this.identityProvider = identityProvider;
        }

        TokenValidation createTokenValidation(DomibusConnectorMessage message) throws Exception {
            final var validationResult = new TechnicalValidationResult();
            final var validationVerification = new ValidationVerification();

            final var tokenAuthentication = new AuthenticationInformation();

            tokenAuthentication.setIdentityProvider(identityProvider);
            tokenAuthentication.setUsernameSynonym(message.getMessageDetails().getOriginalSender());
            tokenAuthentication.setTimeOfAuthentication(
                DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));

            validationVerification.setAuthenticationData(tokenAuthentication);

            var tokenValidation = new TokenValidation();
            tokenValidation.setTechnicalResult(validationResult);
            tokenValidation.setVerificationData(validationVerification);
            tokenValidation.setVerificationTime(
                DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));

            try {
                // passed all the previous checks
                decide(
                    TechnicalTrustLevel.SUCCESSFUL,
                    "The authentication is valid.",
                    tokenValidation
                );
            } catch (Exception e) {
                LOGGER.warn("Exception occurred during createTokenValidation", e);

                // Cannot generate the DSS validation report
                validationResult.setTrustLevel(TechnicalTrustLevel.FAIL);
                validationResult.setComment(
                    "An error occurred, while validating the signature via DSS.");
                LOGGER.warn(
                    "b/o encountered exception: result determined to {}: {}",
                    validationResult.getTrustLevel(),
                    validationResult.getComment()
                );
            }
            return tokenValidation;
        }

        private void decide(
            final TechnicalTrustLevel level, final String comments, TokenValidation validation) {
            final TechnicalValidationResult r = validation.getTechnicalResult();
            r.setTrustLevel(level);
            r.setComment(comments);
            LOGGER.debug("result determined to {}: {}", r.getTrustLevel(), r.getComment());
        }
    }
}
