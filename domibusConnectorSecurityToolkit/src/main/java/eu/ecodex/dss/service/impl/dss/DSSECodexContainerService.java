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
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/service/impl/dss/DSSECodexContainerService.java $
 * $Revision: 2267 $
 * $Date: 2013-06-21 16:20:05 +0200 (ven., 21 juin 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.service.impl.dss;

import eu.ecodex.dss.model.BusinessContent;
import eu.ecodex.dss.model.ECodexContainer;
import eu.ecodex.dss.model.SignatureCheckers;
import eu.ecodex.dss.model.SignatureParameters;
import eu.ecodex.dss.model.checks.CheckResult;
import eu.ecodex.dss.model.token.AdvancedSystemType;
import eu.ecodex.dss.model.token.AuthenticationInformation;
import eu.ecodex.dss.model.token.OriginalValidationReportContainer;
import eu.ecodex.dss.model.token.Signature;
import eu.ecodex.dss.model.token.TechnicalTrustLevel;
import eu.ecodex.dss.model.token.Token;
import eu.ecodex.dss.model.token.TokenDocument;
import eu.ecodex.dss.model.token.TokenIssuer;
import eu.ecodex.dss.service.ContainerFileDefinitions;
import eu.ecodex.dss.service.ECodexBusinessException;
import eu.ecodex.dss.service.ECodexContainerService;
import eu.ecodex.dss.service.ECodexException;
import eu.ecodex.dss.service.ECodexLegalValidationService;
import eu.ecodex.dss.service.ECodexTechnicalValidationService;
import eu.ecodex.dss.service.checks.BusinessContentChecker;
import eu.ecodex.dss.service.checks.ECodexContainerChecker;
import eu.ecodex.dss.service.checks.TokenIssuerChecker;
import eu.ecodex.dss.util.DigestUtil;
import eu.ecodex.dss.util.DocumentStreamUtil;
import eu.ecodex.dss.util.LogDelegate;
import eu.ecodex.dss.util.PDFGeneratorLegalSummary;
import eu.ecodex.dss.util.PDFGeneratorTechnicalSummary;
import eu.ecodex.dss.util.PDFUtil;
import eu.ecodex.dss.util.TokenStreamUtil;
import eu.ecodex.dss.util.XmlStreamUtil;
import eu.ecodex.dss.util.ZipStreamUtil;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.MimeTypeEnum;
import eu.europa.esig.dss.enumerations.SignaturePackaging;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.xmldsig.jaxb.DigestMethodType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.xml.XMLConstants;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;

/**
 * The DSS implementation of the services.
 *
 * <p>DISCLAIMER: Project owner e-CODEX
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 2267 $ - $Date: 2013-06-21 16:20:05 +0200 (ven., 21 juin 2013) $
 */
@SuppressWarnings("squid:S1135")
public class DSSECodexContainerService implements ECodexContainerService {
    private static final LogDelegate LOG = new LogDelegate(DSSECodexContainerService.class);
    private static final BusinessContentChecker CHECKER_BUSINESS_CONTENT =
        new BusinessContentChecker();
    private static final TokenIssuerChecker CHECKER_TOKEN_ISSUER = new TokenIssuerChecker();
    private static final ECodexContainerChecker CHECKER_ECODEX_CONTAINER =
        new ECodexContainerChecker();
    public static final String ADD_SIGNATURE_METHOD = "addSignature";
    public static final String CHECK_METHOD = "check";
    public static final String CREATE_METHOD = "create";
    public static final String RECEIVE_METHOD = "receive";
    private final ECodexTechnicalValidationService technicalValidationService;
    private final ECodexLegalValidationService legalValidationService;
    private final SignatureParameters signingParameters;
    private final TokenIssuer tokenIssuer;
    private final DSSSignatureChecker<ECodexContainer.AsicDocumentTypeECodex> asicsSignatureChecker;
    private final DSSSignatureChecker<ECodexContainer.TokenXmlTypesECodex> xmlTokenSignatureChecker;
    private final DSSSignatureChecker<ECodexContainer.TokenPdfTypeECodex> pdfTokenSignatureChecker;
    private final PDFGeneratorLegalSummary pdfGeneratorLegal = new PDFGeneratorLegalSummary();
    private final PDFGeneratorTechnicalSummary pdfGeneratorTechnical =
        new PDFGeneratorTechnicalSummary();

    /**
     * This class represents a DSSECodexContainerService.
     *
     * @param technicalValidationService The ECodexTechnicalValidationService used for technical
     *                                   validation.
     * @param legalValidationService     The ECodexLegalValidationService used for legal
     *                                   validation.
     * @param signingParameters          The SignatureParameters used for signing.
     * @param tokenIssuer                The TokenIssuer used for token issuance.
     * @param signatureCheckers          The SignatureCheckers used for signature checking.
     */
    public DSSECodexContainerService(
        ECodexTechnicalValidationService technicalValidationService,
        ECodexLegalValidationService legalValidationService,
        SignatureParameters signingParameters,
        TokenIssuer tokenIssuer,
        SignatureCheckers signatureCheckers
    ) {
        this.technicalValidationService = technicalValidationService;
        this.legalValidationService = legalValidationService;
        this.signingParameters = signingParameters;
        this.tokenIssuer = tokenIssuer;
        this.asicsSignatureChecker = signatureCheckers.getAsicsSignatureChecker();
        this.xmlTokenSignatureChecker = signatureCheckers.getXmlTokenSignatureChecker();
        this.pdfTokenSignatureChecker = signatureCheckers.getPdfTokenSignatureChecker();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Attaches an additional XAdES signature (based on this class' signingparameters) for the
     * signed content to the signatures.xml.
     */
    @Override
    public ECodexContainer addSignature(final ECodexContainer container) throws ECodexException {
        LOG.mEnter(ADD_SIGNATURE_METHOD, container);
        try {
            return addSignatureImpl(container);
        } catch (Exception e) {
            LOG.mCause(ADD_SIGNATURE_METHOD, e, container);
            throw ECodexException.wrap(e);
        } finally {
            LOG.mExit(ADD_SIGNATURE_METHOD, container);
        }
    }

    /**
     * Adds an additional XAdES signature (based on the provided signing parameters) for the signed
     * content to the signatures.xml in the provided ECodexContainer.
     *
     * @param container The ECodexContainer to which the signature should be added. Must not be
     *                  null.
     * @return An updated ECodexContainer with the additional signature.
     * @throws ECodexException if any error occurs during the signature addition.
     */
    private ECodexContainer addSignatureImpl(final ECodexContainer container)
        throws ECodexException {
        throwIfNull(container, "The container is not provided in parameter");
        throwIfNull(signingParameters, "the signature parameters have not been set");

        final DSSDocument asicDocument = container.getAsicDocument();
        throwIfNull(asicDocument, "The container contains no asic document");
        if (!DocumentStreamUtil.hasData(asicDocument)) {
            throw new ECodexException("The container's asic document contains no data");
        }
        if (!ZipStreamUtil.isZipFile(asicDocument)) {
            throw new ECodexException("The container's asic document is not in zip format");
        }

        DSSDocument signedContent = null;
        DSSDocument signatures = null;

        try {
            // Retrieve the signed content and the signatures
            for (final DSSDocument document : ZipStreamUtil.extract(asicDocument)) {
                final String documentName = document.getName();
                if (ContainerFileDefinitions.SIGNED_CONTENT_REF.equalsIgnoreCase(documentName)) {
                    signedContent = document;
                }
                if (ContainerFileDefinitions.SIGNATURES_REF.equalsIgnoreCase(documentName)) {
                    signatures = document;
                }
            }
        } catch (IOException e) {
            throw new ECodexException("The asic document can not be extracted", e);
        }

        // check for the existence of these attributes
        throwIfNull(
            signedContent, "The file " + ContainerFileDefinitions.SIGNED_CONTENT_REF
                + " can not be found in the asic container");
        throwIfNull(
            signatures, "The file " + ContainerFileDefinitions.SIGNATURES_REF
                + " can not be found in the asic container");

        InputStream input = null;
        ZipInputStream zipInput = null;

        ByteArrayOutputStream output = null;
        ZipOutputStream zipOutput = null;

        try {
            // create the new XAdES signature
            final var signedContentXAdES = SigningUtil.signXAdES(
                signingParameters,
                signedContent,
                SignaturePackaging.DETACHED
            );
            // add that to the existing ones
            final var signedContentSignatures = appendSignature(signedContentXAdES, signatures);

            // now copy the asic (zip) but replace the old META-INF/signatures.xml with the new one
            input = asicDocument.openStream();
            zipInput = new ZipInputStream(input);
            output = new ByteArrayOutputStream();
            zipOutput = new ZipOutputStream(output);

            while (true) {
                final ZipEntry entry = zipInput.getNextEntry();
                if (entry == null) {
                    break;
                }

                final var newEntry = new ZipEntry(entry.getName());
                zipOutput.putNextEntry(newEntry);

                if (ContainerFileDefinitions.SIGNATURES_REF.equals(entry.getName())) {
                    zipOutput.write(signedContentSignatures);
                } else {
                    zipOutput.write(DSSUtils.toByteArray(zipInput));
                }
            }
        } catch (Exception e) {
            throw ECodexException.wrap(e);
        } finally {
            IOUtils.closeQuietly(zipInput);
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(zipOutput);
            IOUtils.closeQuietly(output);
        }

        try {
            // create a new container
            final var updatedContainer = new ECodexContainer();
            // use the references to the old objects
            updatedContainer.setToken(container.getToken());
            updatedContainer.setBusinessContent(container.getBusinessContent());
            updatedContainer.setTokenPDF(container.getTokenPDF());
            updatedContainer.setTokenXML(container.getTokenXML());

            // but with a new asic
            updatedContainer.setAsicDocument(new InMemoryDocument(
                output.toByteArray(),
                ContainerFileDefinitions.SIGNED_CONTENT_ASIC_REF
            ));

            return updatedContainer;
        } catch (Exception e) {
            throw ECodexException.wrap(e);
        }
    }

    /**
     * Adds the XML signatures originating from the source document to the target document.
     *
     * @param source the xml document providing the signature
     * @param target the xml document where the signature has to be added
     * @return a byte array representing the updated XML
     * @throws Exception from the underlying framework
     */
    private byte[] appendSignature(final DSSDocument source, final DSSDocument target)
        throws Exception {
        try (var output = new ByteArrayOutputStream()) {
            final var factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setNamespaceAware(true);

            final var builder = factory.newDocumentBuilder();
            org.w3c.dom.Document doc =
                builder.parse(new ByteArrayInputStream(DSSUtils.toByteArray(target.openStream())));
            org.w3c.dom.Document newSignatureContent = builder.parse(source.openStream());

            final var element = doc.getDocumentElement();
            final Element adopted =
                (Element) doc.importNode(newSignatureContent.getDocumentElement(), true);
            element.appendChild(adopted);

            final javax.xml.transform.Result outputResult = new StreamResult(output);
            final var transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            final var transformer = transformerFactory.newTransformer();
            final Source dom = new DOMSource(doc);
            transformer.transform(dom, outputResult);

            return output.toByteArray();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CheckResult check(final ECodexContainer container) throws ECodexException {
        LOG.mEnter(CHECK_METHOD, container);
        try {
            return checkImpl(container);
        } catch (Exception e) {
            LOG.mCause(CHECK_METHOD, e, container);
            throw ECodexException.wrap(e);
        } finally {
            LOG.mExit(CHECK_METHOD, container);
        }
    }

    private CheckResult checkImpl(final ECodexContainer container) {
        final var checkResult = new CheckResult();

        // we check for strict compliance of the container and the content
        // if there's any finding, we directly abort. (but still add the intermediate problems to
        // the total results)
        final CheckResult checkContainerResult = CHECKER_ECODEX_CONTAINER.run(container);
        checkResult.addProblems(checkContainerResult);
        if (checkResult.isProblematic()) {
            // The integrity of the container isn't sufficient
            return checkResult;
        }
        final CheckResult checkContentResult =
            CHECKER_BUSINESS_CONTENT.run(container.getBusinessContent());
        checkResult.addProblems(checkContentResult);
        if (checkResult.isProblematic()) {
            // The integrity of the business content isn't sufficient
            return checkResult;
        }
        final CheckResult checkIssuerResult = CHECKER_TOKEN_ISSUER.run(
            container.getToken() == null ? null : container.getToken().getIssuer());
        checkResult.addProblems(checkIssuerResult);
        if (checkResult.isProblematic()) {
            // The integrity of the issuer isn't sufficient
            return checkResult;
        }

        // Compare the digest of the business document with the digest information contained in
        // the token
        final DSSDocument businessDocument = container.getBusinessDocument();
        final var tokenDocument = container.getToken().getDocument();

        tokenDocument.getDigestMethod().setAlgorithm(
            tokenDocument.getDigestMethod().getAlgorithm().replaceAll("-", ""));
        final var digestAlgorithm =
            DigestAlgorithm.forName(tokenDocument.getDigestMethod().getAlgorithm());
        final byte[] documentDigest =
            DigestUtil.digest(DocumentStreamUtil.getData(businessDocument), digestAlgorithm);
        final byte[] tokenDigest = tokenDocument.getDigestValue();
        if (!Arrays.equals(documentDigest, tokenDigest)) {
            checkResult.addProblem(
                true,
                "The digested document from the token is not the same as the digested "
                    + "business document"
            );
        }

        // Validate the trustOkToken XML signature
        final CheckResult checkXmlResult = xmlTokenSignatureChecker.checkSignature(container);
        checkResult.addProblems(checkXmlResult);

        // Validate the trustOkToken PDF signature
        final CheckResult checkPdfResult = pdfTokenSignatureChecker.checkSignature(container);
        checkResult.addProblems(checkPdfResult);

        // Validate the Asic document signature
        final CheckResult checkAsicResult = asicsSignatureChecker.checkSignature(container);
        checkResult.addProblems(checkAsicResult);

        return checkResult;
    }

    /**
     * {@inheritDoc}
     */
    public ECodexContainer create(final BusinessContent businessContent) throws ECodexException {
        LOG.mEnter(CREATE_METHOD, businessContent, tokenIssuer);
        try {
            return createImpl(businessContent, tokenIssuer);
        } catch (Exception e) {
            LOG.mCause(CREATE_METHOD, e, businessContent, tokenIssuer);
            throw ECodexException.wrap(e);
        } finally {
            LOG.mExit(CREATE_METHOD, businessContent, tokenIssuer);
        }
    }

    private ECodexContainer createImpl(
        final BusinessContent businessContent, final TokenIssuer issuer) throws Exception {
        // check some preconditions
        throwIfNull(legalValidationService, "the legal validation service has not been set");
        throwIfNull(
            technicalValidationService, "the technical validation service has not been set");
        throwIfNull(
            pdfGeneratorLegal, "the pdf generator for the legal assessment has not been set");
        throwIfNull(
            pdfGeneratorTechnical,
            "the pdf generator for the technical assessment has not been set"
        );
        throwIfNull(signingParameters, "the signature parameters have not been set");

        // pre-check the parameters for validity
        final CheckResult checkBusinessContent = CHECKER_BUSINESS_CONTENT.run(businessContent);
        if (!checkBusinessContent.isSuccessful()) {
            throw new ECodexBusinessException(
                "the parameter 'business content' is not valid", checkBusinessContent);
        }
        final CheckResult checkTokenIssuer = CHECKER_TOKEN_ISSUER.run(issuer);
        if (!checkTokenIssuer.isSuccessful()) {
            throw new ECodexBusinessException(
                "the parameter 'token issuer' is not valid", checkTokenIssuer);
        }

        final DSSDocument businessDocument = businessContent.getDocument();
        final DSSDocument detachedSignature = businessContent.getDetachedSignature();

        // Create the token
        final var token = createToken(businessDocument, detachedSignature, issuer);

        // Create the optional appendix.pdf
        final DSSDocument appendixPDF = technicalValidationService.createReportPDF(token);
        // Create the mandatory legal summary
        final DSSDocument summaryLegal = pdfGeneratorLegal.generate(token);
        throwIfNull(
            !PDFUtil.isPDFContent(summaryLegal) ? null : Boolean.TRUE,
            "the legal pdf generator did not create a proper document"
        );
        // Create the mandatory technical summary
        final DSSDocument summaryTechnical = pdfGeneratorTechnical.generate(token);
        throwIfNull(
            !PDFUtil.isPDFContent(summaryTechnical) ? null : Boolean.TRUE,
            "the technical pdf generator did not create a proper document"
        );
        // Create the trustOkToken.pdf
        final DSSDocument tokenPDF =
            PDFUtil.concatenate(ContainerFileDefinitions.TOKEN_PDF_REF, summaryLegal,
                                summaryTechnical, appendixPDF
            );
        // Sign trustOkToken.pdf
        final DSSDocument signedTokenPDF = SigningUtil.signPAdES(signingParameters, tokenPDF);

        // Create trustOkToken.xml
        final OriginalValidationReportContainer validationOriginalReport =
            token.getValidationOriginalReport();
        final var tokenValidation = token.getValidation();
        final var originalValidationReportContainer =
            new OriginalValidationReportContainer();
        tokenValidation.setOriginalValidationReport(originalValidationReportContainer);

        final ByteArrayOutputStream encodeXMLToken = TokenStreamUtil.encodeXMLStream(token);

        tokenValidation.setOriginalValidationReport(validationOriginalReport);
        var stringXMLToken = encodeXMLToken.toString();

        if (validationOriginalReport != null) {

            validationOriginalReport.getAny();
            if (validationOriginalReport.getReports() != null) {
                final var reports = validationOriginalReport.getReports();
                String replacement = reports.getXmlSimpleReport();

                replacement = replacement.replace(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", "");
                replacement = replacement.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
                stringXMLToken = stringXMLToken.replace(
                    "<OriginalValidationReport/>",
                    "<OriginalValidationReport>\n" + replacement
                        + "</OriginalValidationReport>\n"
                );
            }
        }
        final DSSDocument tokenXML =
            new InMemoryDocument(stringXMLToken.getBytes(), ContainerFileDefinitions.TOKEN_XML_REF);

        // Sign trustOkToken.xml
        final DSSDocument signedTokenXML =
            SigningUtil.signXAdES(signingParameters, tokenXML, SignaturePackaging.ENVELOPED);
        // Create asic document
        final DSSDocument asicDocument =
            createAsicDocument(businessContent, signedTokenPDF, signedTokenXML);

        // Set the eCodexContainer
        final var container = new ECodexContainer();
        container.setAsicDocument(asicDocument);
        container.setBusinessContent(businessContent);
        container.setToken(token);
        container.setTokenPDF(signedTokenPDF);
        container.setTokenXML(signedTokenXML);

        return container;
    }

    /**
     * create the token with the provided data: issuer, tokenvalidation, tokendocument (including
     * the digest algorithm/method/value) this is also the invocation point for the technical and
     * legal validations provided by the corresponding configured services.
     *
     * @param businessDocument  for the tokendocument
     * @param detachedSignature for DETACHED signatures
     * @param issuer            for the token
     * @return a newly created token
     */
    private Token createToken(
        final DSSDocument businessDocument, final DSSDocument detachedSignature,
        final TokenIssuer issuer) throws ECodexException {
        final var token = new Token();
        token.setIssuer(issuer);

        final var tokenValidation =
            technicalValidationService.create(businessDocument, detachedSignature);
        // test some post-conditions according to the contract of the method
        throwIfNull(
            tokenValidation,
            "the technical validation service did not create a token validation object"
        );
        throwIfNull(
            tokenValidation.getTechnicalResult(),
            "the technical validation service did not create a technical validation "
                + "result in the token validation object"
        );
        throwIfNull(
            tokenValidation.getTechnicalResult().getTrustLevel(),
            "the technical validation service did not create a trust-level in the "
                + "technical validation result in the token validation object"
        );
        // store in the token
        token.setValidation(tokenValidation);

        var validationVerification = tokenValidation.getVerificationData();
        List<Signature> sigList = validationVerification != null
            ? validationVerification.getSignatureData()
            : null;

        if (issuer.getAdvancedElectronicSystem() == AdvancedSystemType.AUTHENTICATION_BASED
            && technicalValidationService
            instanceof DSSECodexTechnicalValidationService validationService) {
            if (sigList != null && !sigList.isEmpty()) {
                if (sigList.size() != 1) {
                    LOG.lWarn(
                        "Invalid number of authentication certificates detected: {} - "
                            + "Only one signature/certificate is supported!",
                        sigList.size()
                    );
                    tokenValidation.getTechnicalResult().setTrustLevel(TechnicalTrustLevel.FAIL);
                    tokenValidation.getTechnicalResult().setComment(
                        "Invalid count of signatures on the authentication data.");
                } else {
                    var currentSIgnature = sigList.getFirst();
                    var authenticationCertValidation = validationService
                        .verifyAuthenticationCertificate(businessDocument, detachedSignature);
                    currentSIgnature.setAuthenticationCertValidation(authenticationCertValidation);
                }
            } else {
                if (tokenValidation.getVerificationData().getAuthenticationData() == null) {
                    var authInfo = new AuthenticationInformation();
                    authInfo.setIdentityProvider("Identityprovider missing!");
                    authInfo.setUsernameSynonym("Userdata missing!");
                    try {
                        authInfo.setTimeOfAuthentication(
                            DatatypeFactory.newInstance().newXMLGregorianCalendar(
                                new GregorianCalendar()
                            )
                        );
                    } catch (DatatypeConfigurationException e) {
                        throw new ECodexException(e);
                    }
                    validationVerification.setAuthenticationData(authInfo);
                }

                tokenValidation.getTechnicalResult().setTrustLevel(TechnicalTrustLevel.FAIL);
                tokenValidation.getTechnicalResult()
                               .setComment("Neither authentication nor signature data present.");
            }
        }

        // Create the token document
        final var tokenDocument = new TokenDocument();
        tokenDocument.setFilename(businessDocument.getName());
        if (businessDocument.getMimeType() != null) {
            tokenDocument.setType(businessDocument.getMimeType().getMimeTypeString());
        }
        if (detachedSignature != null) {
            tokenDocument.setSignatureFilename(detachedSignature.getName());
        }
        // use the SHA256 signing algorithm
        final var digestAlgorithm =
            DigestAlgorithm.SHA256; // note, that this could be a configuration

        // 2014/07/12 klara: Hotfix - Added "-" between SHA and the respective number for
        // downward compatibility (e.g. SHA256 becomes SHA-256)
        String digestAlgorithmName = digestAlgorithm.getName().replace("SHA", "SHA-");

        final var digestMethod = new DigestMethodType();
        digestMethod.setAlgorithm(digestAlgorithmName);
        tokenDocument.setDigestMethod(digestMethod);
        final byte[] data = DocumentStreamUtil.getData(businessDocument);
        final byte[] digest = DigestUtil.digest(data, digestAlgorithm);
        tokenDocument.setDigestValue(digest);
        // store in the token
        token.setDocument(tokenDocument);

        // create the legal validation
        final var legalValidationResult = legalValidationService.create(token);
        // test some post-conditions according to the contract of the method
        throwIfNull(
            legalValidationResult,
            "the legal validation service did not create a legal validation result"
        );
        throwIfNull(
            legalValidationResult.getTrustLevel(),
            "the legal validation service did not create a trustlevel in the legal "
                + "validation result"
        );
        // store in the token
        tokenValidation.setLegalResult(legalValidationResult);

        return token;
    }

    /**
     * Create the ASiC-S document.
     *
     * <p>It creates an archive that contains the business document, the attachments and the
     * trustOkToken.pdf It sign the archive in accordance with the standard PAdES and the profile
     * PAdES-BES
     *
     * @param businessContent the business content
     * @param trustOkTokenPDF the trustOkToken PDF document
     * @param trustOkTokenXML the trustOkToken XML document (currently not included in the signed
     *                        content)
     * @return The ASiC-S container
     * @throws java.io.IOException                    from the underlying framework
     * @throws java.security.NoSuchAlgorithmException from the underlying framework
     */
    private DSSDocument createAsicDocument(
        final BusinessContent businessContent, final DSSDocument trustOkTokenPDF,
        final DSSDocument trustOkTokenXML) throws Exception {

        final DSSDocument businessDocument = businessContent.getDocument();

        ByteArrayOutputStream signedContentBytes = null;
        ZipOutputStream signedContentZip = null;

        try {
            // create the content zip
            signedContentBytes = new ByteArrayOutputStream();
            signedContentZip = new ZipOutputStream(signedContentBytes);
            signedContentZip.setLevel(ZipEntry.DEFLATED);

            // Put the main document
            final String docName =
                StringUtils.isEmpty(businessDocument.getName()) ? "businessdocument" :
                    businessDocument.getName();
            signedContentZip.putNextEntry(new ZipEntry(docName));
            signedContentZip.write(DSSUtils.toByteArray(businessDocument.openStream()));

            // add an optional detached signature
            if (businessContent.hasDetachedSignature()) {
                final DSSDocument detachedSignature = businessContent.getDetachedSignature();
                final String sigName =
                    StringUtils.isEmpty(detachedSignature.getName()) ? "detachedsignature" :
                        detachedSignature.getName();
                signedContentZip.putNextEntry(new ZipEntry(sigName));
                signedContentZip.write(DSSUtils.toByteArray(detachedSignature.openStream()));
            }

            // Put the attachments
            final List<DSSDocument> attachments = businessContent.getAttachments();
            for (var i = 0; i < attachments.size(); i++) {
                final DSSDocument attachment = attachments.get(i);
                final String attachmentName =
                    StringUtils.isEmpty(attachment.getName()) ? ("attachment" + i) :
                        attachment.getName();
                signedContentZip.putNextEntry(new ZipEntry(attachmentName));
                signedContentZip.write(DSSUtils.toByteArray(attachment.openStream()));
            }

            // Put the trustOkToken.pdf
            signedContentZip.putNextEntry(new ZipEntry(ContainerFileDefinitions.TOKEN_PDF_REF));
            signedContentZip.write(DSSUtils.toByteArray(trustOkTokenPDF.openStream()));

            // Put the trustOkToken.xml
            // noinspection ConstantIfStatement
            if (false) { // purposely excluded, because the xml shall be shipped in parallel to
                // the asic-container according to WP4 decision
                signedContentZip.putNextEntry(new ZipEntry(ContainerFileDefinitions.TOKEN_XML_REF));
                signedContentZip.write(DSSUtils.toByteArray(trustOkTokenXML.openStream()));
            }

            // close the content zip
            signedContentZip.close();

            // create asic-s container
            final DSSDocument toBeSigned = new InMemoryDocument(
                signedContentBytes.toByteArray(),
                ContainerFileDefinitions.SIGNED_CONTENT_REF,
                MimeTypeEnum.BINARY
            ); // TODO: replace InMemoryDocument with Streaming Document
            return SigningUtil.signASiC(signingParameters, toBeSigned);
        } finally {
            IOUtils.closeQuietly(signedContentZip);
            IOUtils.closeQuietly(signedContentBytes);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ECodexContainer receive(final InputStream asicInputStream, final InputStream tokenStream)
        throws ECodexException {
        LOG.mEnter(RECEIVE_METHOD, asicInputStream, tokenStream);
        try {
            return receiveImpl(asicInputStream, tokenStream);
        } catch (Exception e) {
            LOG.mCause(RECEIVE_METHOD, e, asicInputStream, tokenStream);
            throw ECodexException.wrap(e);
        } finally {
            LOG.mExit(RECEIVE_METHOD, asicInputStream, tokenStream);
        }
    }

    private ECodexContainer receiveImpl(
        final InputStream asicInputStream, final InputStream tokenStream) throws Exception {
        final DSSDocument asicDocument = new InMemoryDocument(
            DSSUtils.toByteArray(asicInputStream),
            ContainerFileDefinitions.SIGNED_CONTENT_ASIC_REF
        );
        if (!DocumentStreamUtil.hasData(asicDocument)) {
            throw new ECodexException("The asic data must not be a null/empty data stream");
        }

        final DSSDocument tokenXML = new InMemoryDocument(
            DSSUtils.toByteArray(tokenStream),
            ContainerFileDefinitions.TOKEN_XML_REF
        );
        if (!DocumentStreamUtil.hasData(tokenXML)) {
            throw new ECodexException("The token data must not be a null/empty data stream");
        }

        if (!ZipStreamUtil.isZipFile(asicDocument)) {
            throw new ECodexException("The asic data must be in zip format");
        }

        if (!XmlStreamUtil.isXmlFile(tokenXML)) {
            throw new ECodexException("The token data must be in XML format");
        }

        final var token = TokenStreamUtil.decodeXMLStream(tokenXML.openStream());

        final var eCodexContainer = new ECodexContainer();
        eCodexContainer.setAsicDocument(asicDocument);
        eCodexContainer.setToken(token);
        eCodexContainer.setTokenXML(tokenXML);

        // Get the main document name
        final String businessDocumentName = token.getDocument().getFilename();
        final String detachedSignatureName = token.getDocument().getSignatureFilename();

        final List<DSSDocument> asicDocumentList = ZipStreamUtil.extract(asicDocument);
        for (final DSSDocument asicEntry : asicDocumentList) {
            // Create the business content with content of signedContent.zip
            final String asicEntryName = asicEntry.getName();

            if (!ContainerFileDefinitions.SIGNED_CONTENT_REF.equalsIgnoreCase(asicEntryName)) {
                continue;
            }

            final var businessContent = new BusinessContent();
            eCodexContainer.setBusinessContent(businessContent);

            final List<DSSDocument> zipDocumentList = ZipStreamUtil.extract(asicEntry);
            for (final DSSDocument signedContentEntry : zipDocumentList) {
                final String signedContentEntryName = signedContentEntry.getName();

                if (ContainerFileDefinitions.TOKEN_PDF_REF.equalsIgnoreCase(
                    signedContentEntryName)) {
                    // Set the token pdf
                    eCodexContainer.setTokenPDF(signedContentEntry);
                } else if (!StringUtils.isEmpty(businessDocumentName)
                    && businessDocumentName.equalsIgnoreCase(signedContentEntryName)) {
                    // Set the main document
                    businessContent.setDocument(signedContentEntry);
                } else if (!StringUtils.isEmpty(detachedSignatureName)
                    && detachedSignatureName.equalsIgnoreCase(signedContentEntryName)) {
                    // Set the optional detached signature
                    businessContent.setDetachedSignature(signedContentEntry);
                } else {
                    // add an optional the attachment
                    businessContent.addAttachment(signedContentEntry);
                }
            }
        }
        return eCodexContainer;
    }

    /**
     * Throws an exception with the given message, if the object is null.
     *
     * @param object  the to be tested object
     * @param message the exception message
     * @throws ECodexException if the object is null
     */
    private static void throwIfNull(final Object object, final String message)
        throws ECodexException {
        if (object != null) {
            return;
        }
        throw new ECodexException(message);
    }
}
