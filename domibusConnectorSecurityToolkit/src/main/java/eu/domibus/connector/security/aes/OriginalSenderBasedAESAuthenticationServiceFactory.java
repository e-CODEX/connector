package eu.domibus.connector.security.aes;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.security.configuration.DCBusinessDocumentValidationConfigurationProperties;
import eu.ecodex.dss.model.token.*;
import eu.ecodex.dss.service.ECodexException;
import eu.ecodex.dss.service.ECodexTechnicalValidationService;
import eu.ecodex.dss.util.PdfValidationReportService;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.enumerations.MimeTypeEnum;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.simplereport.SimpleReport;
import eu.europa.esig.dss.validation.reports.Reports;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.xml.datatype.DatatypeFactory;
import java.io.ByteArrayOutputStream;
import java.util.GregorianCalendar;
import java.util.List;

/**
 *
 */
@Service
public class OriginalSenderBasedAESAuthenticationServiceFactory implements DCAuthenticationBasedTechnicalValidationServiceFactory {


    @Override
    public ECodexTechnicalValidationService createTechnicalValidationService(DomibusConnectorMessage message, DCBusinessDocumentValidationConfigurationProperties.AuthenticationValidationConfigurationProperties config) {
        DomibusConnectorAESTokenValidationCreator domibusConnectorAESTokenValidationCreator = new DomibusConnectorAESTokenValidationCreator(config.getIdentityProvider());
        return new DomibusConnectorAESTechnicalValidationService(message, domibusConnectorAESTokenValidationCreator);
    }

    static class DomibusConnectorAESTechnicalValidationService implements ECodexTechnicalValidationService {

        private static final Logger LOGGER = LoggerFactory.getLogger(DomibusConnectorAESTechnicalValidationService.class);

        private final DomibusConnectorAESTokenValidationCreator delegate;

        private final DomibusConnectorMessage message;

        public DomibusConnectorAESTechnicalValidationService(final DomibusConnectorMessage message, final DomibusConnectorAESTokenValidationCreator delegate) {
            super();
            this.message = message;
            this.delegate = delegate;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TokenValidation create(final DSSDocument businessDocument, final DSSDocument detachedSignature)
                throws ECodexException {
            LOGGER.debug("create businessDocument: [{}] detachedSignature: [{}]", businessDocument, detachedSignature);
            try {
                return delegate.createTokenValidation(message);
            } catch (Exception e) {
                throw ECodexException.wrap(e);
            }
        }

        /**
         * {@inheritDoc}
         *
         * the report must contain exactly one object of type
         *
         */
        @Override
        public DSSDocument createReportPDF(final Token token) throws ECodexException {
            // LOGGER.mEnter("createReportPDF", token);
            try {
                return createReportPDFImpl(token);
            } catch (Exception e) {
                // LOGGER.mCause("createReportPDF", e, token);
                throw ECodexException.wrap(e);
                // } finally {
                // LOGGER.mExit("createReportPDF", token);
            }
        }

        private DSSDocument createReportPDFImpl(final Token token) throws ECodexException {
            if (token == null) {
                throw new ECodexException("the token (in parameter) must not be null");
            }
            final TokenValidation tokenValidation = token.getValidation();
            if (tokenValidation == null) {
                throw new ECodexException("the token (in parameter) must have a validation object");
            }
            final ValidationVerification tokenVerificationData = tokenValidation.getVerificationData();
            if (tokenVerificationData == null) {
                throw new ECodexException(
                        "the token (in parameter) must have a validation object with an existing verification data");
            }

            final OriginalValidationReportContainer report = tokenValidation.getOriginalValidationReport();

            if (report == null) {
                // return an empty document
                return new InMemoryDocument(new byte[0]);
            }

            final List<Object> reportDatas = report.getAny();
            if (reportDatas == null || reportDatas.isEmpty()) {
                // return an empty document
                return new InMemoryDocument(new byte[0]);
            }

            final ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();

            try {
                if (report.getReports() != null) {
                    Reports reports = report.getReports();
                    final DiagnosticData diagnosticData = reports.getDiagnosticData();
                    final SimpleReport simpleReport = reports.getSimpleReport();
                    // create and write the pdf version to the stream
                    final PdfValidationReportService pdfService = new PdfValidationReportService();
                    pdfService.createReport(diagnosticData, simpleReport, pdfStream);
                    return new InMemoryDocument(pdfStream.toByteArray(), "dss-report.pdf", MimeTypeEnum.PDF);
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

        private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(DomibusConnectorAESTokenValidationCreator.class);


        private final String identityProvider;

        public DomibusConnectorAESTokenValidationCreator(String identityProvider) {
            this.identityProvider = identityProvider;
        }

        TokenValidation createTokenValidation(DomibusConnectorMessage message) throws Exception {

            TokenValidation tValidation = new TokenValidation();

            final TechnicalValidationResult validationResult = new TechnicalValidationResult();
            final ValidationVerification validationVerification = new ValidationVerification();

            final AuthenticationInformation tokenAuthentication = new AuthenticationInformation();

            tokenAuthentication.setIdentityProvider(identityProvider);
            tokenAuthentication.setUsernameSynonym(message.getMessageDetails().getOriginalSender());
            tokenAuthentication.setTimeOfAuthentication(
                        DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));

            validationVerification.setAuthenticationData(tokenAuthentication);

            tValidation.setTechnicalResult(validationResult);
            tValidation.setVerificationData(validationVerification);
            tValidation.setVerificationTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));

            try {
                // passed all the previous checks
                decide(TechnicalTrustLevel.SUCCESSFUL, "The authentication is valid.", tValidation);

            } catch (Exception e) {
                LOGGER.warn("Exception occured during createTokenValidation", e);

                // Cannot generate the DSS validation report
                validationResult.setTrustLevel(TechnicalTrustLevel.FAIL);
                validationResult.setComment("An error occured, while validating the signature via DSS.");
                LOGGER.warn("b/o encountered exception: result determined to {}: {}", validationResult.getTrustLevel(),
                        validationResult.getComment());
            }
            return tValidation;
        }

        private void decide(final TechnicalTrustLevel level, final String comments, TokenValidation tValidation) {
            final TechnicalValidationResult r = tValidation.getTechnicalResult();
            r.setTrustLevel(level);
            r.setComment(comments);
            LOGGER.debug("result determined to {}: {}", r.getTrustLevel(), r.getComment());
        }

    }
}
