/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/service/impl/dss/DSSTokenValidationCreator.java $
 * $Revision: 1904 $
 * $Date: 2013-05-02 14:29:29 +0200 (jeu., 02 mai 2013) $
 * $Author: meyerfr $
 */
package eu.ecodex.dss.service.impl.dss;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import eu.domibus.connector.dss.configuration.SignatureValidationConfigurationProperties;
import eu.europa.esig.dss.detailedreport.DetailedReport;
import eu.europa.esig.dss.diagnostic.CertificateWrapper;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.enumerations.SignatureForm;
import eu.europa.esig.dss.enumerations.SignatureQualification;
import eu.europa.esig.dss.policy.EtsiValidationPolicy;
import eu.europa.esig.dss.simplereport.SimpleReport;
import eu.europa.esig.dss.simplereport.jaxb.XmlToken;
import eu.europa.esig.dss.spi.x509.CertificateSource;
import eu.europa.esig.dss.validation.executor.DocumentProcessExecutor;
import org.apache.commons.lang.StringUtils;

import eu.ecodex.dss.model.token.OriginalValidationReportContainer;
import eu.ecodex.dss.model.token.Signature;
import eu.ecodex.dss.model.token.SignatureAttributes;
import eu.ecodex.dss.model.token.SignatureCertificate;
import eu.ecodex.dss.model.token.TechnicalTrustLevel;
import eu.ecodex.dss.model.token.TechnicalValidationResult;
import eu.ecodex.dss.model.token.TokenValidation;
import eu.ecodex.dss.model.token.ValidationVerification;
import eu.ecodex.dss.util.LogDelegate;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.validation.AdvancedSignature;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;
import eu.europa.esig.dss.model.x509.CertificateToken;

/**
 * this class creates the token validation; the execution is thread-safe
 *
 * <p>
 * DISCLAIMER: Project owner e-CODEX
 * </p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1904 $ - $Date: 2013-05-02 14:29:29 +0200 (jeu., 02 mai 2013) $
 */
class DSSTokenValidationCreator {

	private static final LogDelegate LOG = new LogDelegate(DSSTokenValidationCreator.class);

	private final CertificateVerifier certificateVerifier;
	private final DSSDocument businessDocument;
	private final DSSDocument detachedSignature;
	private final DocumentProcessExecutor processExecutor;
	private final EtsiValidationPolicy etsiValidationPolicy;

	private TokenValidation tValidation;

	private CertificateSource ignoredCertificateStore;

	/**
	 * this holds the latest data of the threads
	 */
	private static final ThreadLocal<SoftReference<DecisionData>> DATA_CACHE = new ThreadLocal<SoftReference<DecisionData>>();

	/**
	 * constructor for the objects to be used
	 *
	 * @param certificateVerifier the delegate actually providing the verification
	 * @param businessDocument    the document for which the validation has to be done
	 * @param detachedSignature   the optional detached signature document; if present this will be used to provide the signature
	 * @param processExecutor
	 */
	DSSTokenValidationCreator(
			final EtsiValidationPolicy etsiValidationPolicy,
			final CertificateVerifier certificateVerifier,
			final DSSDocument businessDocument,
			final DSSDocument detachedSignature,
			DocumentProcessExecutor processExecutor) {
		this.etsiValidationPolicy = etsiValidationPolicy;
		this.certificateVerifier = certificateVerifier;
		this.businessDocument = businessDocument;
		this.detachedSignature = detachedSignature;
		this.processExecutor = processExecutor;
	}

	/**
	 * gives access to the created object
	 *
	 * @return the value
	 */
	public TokenValidation getResult() {
		return tValidation;
	}

	/**
	 * creates the tokenValidation object, this method will be executed only if the object has not been created before.
	 * see also <a href="http://www.jira.e-codex.eu/browse/ECDX-25">http://www.jira.e-codex.eu/browse/ECDX-25</a> to get details about the requirements
	 *
	 * @throws Exception as of the underlying logic
	 */
	void run() throws Exception {
		if (tValidation != null) {
			LOG.lInfo("result was already created for this class instance. skipping execution.");
			return;
		}

		LOG.lInfo("creating result");

		tValidation = new TokenValidation();

		final TechnicalValidationResult validationResult = new TechnicalValidationResult();
		final ValidationVerification validationVerification = new ValidationVerification();

		final List<Signature> signatures = new ArrayList<Signature>();

		// Default TokenValidationCreator takes only a signed document into account
		validationVerification.setAuthenticationData(null);
		validationVerification.setSignatureData(signatures);

		tValidation.setTechnicalResult(validationResult);
		tValidation.setVerificationData(validationVerification);

		try {
			runImpl();
		} catch (Exception e) {
			LOG.mCause("run", e);

			// => set also the verification time if needed
			if (tValidation.getVerificationTime() == null) {
				tValidation.setVerificationTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
			}

			// => set also the original validation report if needed
			if (tValidation.getOriginalValidationReport() == null) {
				tValidation.setOriginalValidationReport(new OriginalValidationReportContainer());
			}

			validationResult.setTrustLevel(TechnicalTrustLevel.FAIL);
			validationResult.setComment("An error occurred, while validating the signature via DSS.");
			LOG.lWarn("b/o encountered exception: result determined to {}: {}", validationResult.getTrustLevel(), validationResult.getComment());
		}
	}

	private void runImpl() throws Exception {

		final TechnicalValidationResult validationResult = tValidation.getTechnicalResult();

		// Create the validation report
		final SignedDocumentValidator validator;
		if (detachedSignature != null) {
			validator = SignedDocumentValidator.fromDocument(detachedSignature);
			DSSDocument doc = businessDocument;
			List<DSSDocument> docList = new ArrayList<DSSDocument>();
			docList.add(doc);
			validator.setDetachedContents(docList);
		} else {
			LOG.lDetail("acquiring SignedDocumentValidator upon document via contained signature");
			validator = SignedDocumentValidator.fromDocument(businessDocument);
		}

		validator.setProcessExecutor(processExecutor);
		validator.setCertificateVerifier(certificateVerifier);

		// Validate the document and generate the validation report
		LOG.lDetail("validating document");
		//TODO: use config here...
//		final InputStream resourceAsStream = DSSECodexContainerService.class.getResourceAsStream("/validation/102853/constraint.xml");

		Reports reports = validator.validateDocument(etsiValidationPolicy);
		final SimpleReport simpleReport = reports.getSimpleReport();
		final DiagnosticData diagnosticData = reports.getDiagnosticData();
		final List<AdvancedSignature> signatures = validator.getSignatures();

		LOG.lDetail("DSS validation report created: {}", simpleReport);

		final DetailedReport detailedReport = reports.getDetailedReport();
		LOG.lDetail("Detailed Report: \n{}", detailedReport);

		// set the verificationTime
		if (simpleReport.getValidationTime() != null) {
			final GregorianCalendar calGreg = new GregorianCalendar();
			calGreg.setTime(simpleReport.getValidationTime());
			tValidation.setVerificationTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(calGreg));
		} else {
			// fall back to new date if no data in the report
			LOG.lInfo("no time-information/verification-time found in DSS validation report. setting the current datetime.");
			tValidation.setVerificationTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
		}
		LOG.lDetail("verification time resolved to {}", tValidation.getVerificationTime());

		// AK: Removed XMLDSIG as it is detected by DSS but doesn't give enough information
		List<AdvancedSignature> invalidSignatures = new ArrayList<AdvancedSignature>();

		for (AdvancedSignature curSignature : signatures) {
			if(simpleReport.getSignatureFormat(curSignature.getId()) != null && simpleReport.getSignatureFormat(curSignature.getId()).getSignatureForm().equals(SignatureForm.XAdES)){
				invalidSignatures.add(curSignature);
			}
		}

		for (AdvancedSignature curSignature : invalidSignatures) {
			signatures.remove(curSignature);
		}

		// detect the significant signature = the latest one in time
		// actually the order returned by iText and hence DSS is not reliable in terms of signing-time order
		// so, we sort the list accordingly
		// final AdvancedSignature lastSignature = SignatureTimeComparator.getLast(signatures);
		// LOG.lDetail("using the signature information at index {} in the DSS validation report for further determination: {}", signatures.indexOf(lastSignature), lastSignature);

		// inspect the info and derive some diagnosis and validation data
		ArrayList<DecisionData> decisionData = new ArrayList<DecisionData>();
		List<String> idsToRemove = new ArrayList<String>();

		for (AdvancedSignature curSignature : signatures) {
			if(ignoredCertificateStore != null && !ignoredCertificateStore.getByPublicKey(curSignature.getSigningCertificateToken().getCertificate().getPublicKey()).isEmpty()) {
				LOG.lDetail("Removing curSignature [{}] because the according certificate is within ignore list", curSignature);
				idsToRemove.add(curSignature.getId());
				diagnosticData.getSignatureIdList().remove(curSignature.getId());
			} else {
				Signature signature = new Signature();
				SignatureAttributes tokenSignatureAttributes = new SignatureAttributes();
				SignatureCertificate tokenSignatureCertificate = new SignatureCertificate();
				TechnicalValidationResult tokenSignatureResult = new TechnicalValidationResult();

				final DecisionData data = computeData(curSignature, reports);
				decisionData.add(data);

				// propagate to token validation instance
				signature.setSigningTime(data.diagnosis.signingTime);

				tokenSignatureCertificate.setSubject(data.diagnosis.signingCertificateSubject);
				tokenSignatureCertificate.setCertificateValid(data.validation.signatureCertStatus != TechnicalTrustLevel.FAIL);
				tokenSignatureCertificate.setIssuer(data.diagnosis.signingCertificateIssuer);
				tokenSignatureCertificate.setValidityAtSigningTime(data.validation.signatureCertHistory != TechnicalTrustLevel.FAIL);

				tokenSignatureAttributes.setSignatureFormat(data.diagnosis.signatureFormatLevel);
				tokenSignatureAttributes.setSignatureLevel(data.diagnosis.signatureConclusion.name());
				tokenSignatureAttributes.setSignatureValid(data.validation.signatureComputation);
				// structure verification is always true if the DSS report is generated and contains signature information.
				tokenSignatureAttributes.setStructureValid(true);

				tokenSignatureResult.setTrustLevel(data.diagnosis.trustLevel);
				tokenSignatureResult.setComment(data.diagnosis.comment);

				signature.setCertificateInformation(tokenSignatureCertificate);
				signature.setSignatureInformation(tokenSignatureAttributes);
				signature.setTechnicalResult(tokenSignatureResult);

				tValidation.getVerificationData().addSignatureData(signature);
			}
		}

		if (signatures.isEmpty() || idsToRemove.size() == signatures.size()) {
			// No signature information in the validation report
			validationResult.setTrustLevel(TechnicalTrustLevel.FAIL);
			// http://www.jira.e-codex.eu/browse/ECDX-45: tokenSignature.setUnsigned(true);
			validationResult.setComment("Unable to find a signature.");

			// For the reason of downwardcompatibility, at least one Signature-Entry needs to be present
			tValidation.getVerificationData().addSignatureData(new Signature());

			LOG.lWarn("no valid signature information found in DSS validation report - setting FAIL level.");
		}

		TechnicalValidationResult result = tValidation.getTechnicalResult();
		LOG.lInfo("General result determined to lowest level: {}: {}", result.getTrustLevel(), result.getComment());

		removeSignaturesFromSimpleReport(simpleReport, idsToRemove);

		// Add the original report to the token validation
		LOG.lDetail("propagating DSS validation report");
		final OriginalValidationReportContainer tReportContainer = new OriginalValidationReportContainer();
		tReportContainer.setReports(reports);
		tValidation.setOriginalValidationReport(tReportContainer);
	}

	private void removeSignaturesFromSimpleReport(final SimpleReport simpleReport, final List<String> idsToRemove) {

//      int decreaseSigCount = 0;
//		int decreaseValid = 0;

		List<XmlToken> signatures = simpleReport.getJaxbModel().getSignatureOrTimestamp();
		List<XmlToken> toRemove = new ArrayList<XmlToken>();

		for (XmlToken curSig : signatures) {
			if(idsToRemove.contains(curSig.getId())) {
				simpleReport.getSignatureIdList().remove(curSig.getId());
				toRemove.add(curSig);
			}
		}

		for (XmlToken curSig : toRemove) {
			signatures.remove(curSig);
		}
	
		/*
		Element reportElement = simpleReport.getRootElement();
		 
        NodeList signatureNodes = reportElement.getElementsByTagName("Signature");
        
        for(int i = 0; i < signatureNodes.getLength(); i++){
        	
        	Node curNode = signatureNodes.item(i);
        	
        	if(curNode.hasAttributes()){
        		Node idAttribute = curNode.getAttributes().getNamedItem("Id");
        		
        		if(idAttribute != null && idsToRemove.contains(idAttribute.getNodeValue())){
        			decreaseSigCount++;
        			NodeList children = curNode.getChildNodes();
                	
        			for(int y = 0; y < children.getLength(); y++){
                		Node curChild = children.item(y);
                		if(curChild.getNodeName().equals("Indication")){
                			if(curChild.getTextContent().equals(Indication.PASSED) || curChild.getTextContent().equals(Indication.TOTAL_PASSED)){
                				decreaseValid++;
                			}
                			break;
                		}
                	}
        			
        			reportElement.removeChild(curNode);
                	i--;		
        		}
        	}
        }

       	NodeList rootChildren = reportElement.getChildNodes();
    	
    	for(int y = 0; y < rootChildren.getLength(); y++){
    		Node curChild = rootChildren.item(y);
    		if(curChild.getNodeName().equals("ValidSignaturesCount")){
    			int valid = Integer.parseInt(curChild.getTextContent());
    			valid = valid - decreaseValid;
    			curChild.setTextContent(String.valueOf(valid));
    		} else if(curChild.getNodeName().equals("SignaturesCount")){
    			int sigCount = Integer.parseInt(curChild.getTextContent());
    			sigCount = sigCount - decreaseSigCount;
    			curChild.setTextContent(String.valueOf(sigCount));
    		}
    	}
    	*/
	}

	private DecisionData computeData(final AdvancedSignature signature, Reports reports) throws Exception {

		DiagnosticData diagnosticData = reports.getDiagnosticData();
		SimpleReport simpleReport = reports.getSimpleReport();

		final String signatureId = signature.getId();
		final String certificateId = diagnosticData.getSigningCertificateId(signatureId);

		// compute some diagnosis data
		LOG.lDetail("computing the diagnosis data");
		final XMLGregorianCalendar signingTime = TechnicalValidationUtil.getSigningTime(signature, signatureId);
		final CertificateToken signingCertificateToken = TechnicalValidationUtil.getCertificateToken(signature, certificateId);
		final X509Certificate signingCertificate = TechnicalValidationUtil.getCertificate(signingCertificateToken);
		final String signingCertificateSubject = TechnicalValidationUtil.getSigningCertificateSubjectName(signingCertificate);
		final String signingCertificateIssuer = TechnicalValidationUtil.getSigningCertificateIssuerName(signingCertificate);
		final String signatureFormatLevel = TechnicalValidationUtil.getSignatureFormatLevelAsString(simpleReport, signatureId);// PAdES-BES etc
		final SignatureQualification signatureConclusion = TechnicalValidationUtil.getSignatureConclusion(simpleReport, signatureId); // QES etc

		CertificateWrapper issuerCertificateWrapper = null;

		if(signingCertificateToken.isSelfSigned()) {
			issuerCertificateWrapper = TechnicalValidationUtil.getCertificateWrapper(diagnosticData.getUsedCertificates(), signingCertificateIssuer);
		} else {
			issuerCertificateWrapper = TechnicalValidationUtil.getCertificateWrapper(diagnosticData.getUsedCertificates(), signingCertificateIssuer);
		}

		final CertificateToken issuingCertificateToken = TechnicalValidationUtil.getCertificateToken(signature, issuerCertificateWrapper.getId());
		final X509Certificate issuingCertificate = TechnicalValidationUtil.getCertificate(issuingCertificateToken);

		// compute some validation attributes
		LOG.lDetail("computing the validation data");
		final boolean validSignatureComputation = TechnicalValidationUtil.checkSignatureCorrectness(simpleReport, signatureId);
		final boolean validSignatureConclusion = TechnicalValidationUtil.checkSignatureConclusion(simpleReport, reports.getDetailedReport(), signatureId);
		final boolean validSignatureFormat = !StringUtils.isEmpty(signatureFormatLevel);

		final TechnicalTrustLevel validSignatureCertStatus = TechnicalValidationUtil.checkCertificateRevocation(signingCertificateToken, diagnosticData.getCertificateRevocationStatus(certificateId).isRevoked());
		final TechnicalTrustLevel validSignatureCertHistory = TechnicalValidationUtil.checkCertificateValidity(signingCertificateToken, signingTime);
		final boolean validTrustAnchor = TechnicalValidationUtil.checkTrustAnchor(diagnosticData, certificateId);
		final TechnicalTrustLevel validIssuerCertStatus = TechnicalValidationUtil.checkCertificateRevocation(issuingCertificateToken);
		final TechnicalTrustLevel validIssuerCertHistory = TechnicalValidationUtil.checkCertificateValidity(issuingCertificateToken, signingTime);

		// log the data
		LOG.lDetail("data details:\n " +
						"signingTime={}\n signatureFormatLevel={}\n signingCertificate={}\n signingCertificateSubject={}\n signingCertificateIssuer={}\n signatureConclusion={}\n " +
						"issuerCertificate={}\n validSignatureComputation={}\n validSignatureCertStatus={}\n validSignatureCertHistory={}\n validTrustAnchor={}\n " +
						"validSignatureConclusion={}\n validSignatureFormat={}\n validIssuerCertStatus={}\n validIssuerCertHistory={}\n", signingTime, signatureFormatLevel,
				signingCertificate, signingCertificateSubject, signingCertificateIssuer, signatureConclusion, issuingCertificate, validSignatureComputation, validSignatureCertStatus, validSignatureCertHistory,
				validTrustAnchor, validSignatureConclusion, validSignatureFormat, validIssuerCertStatus, validIssuerCertHistory
		);

		// create the data container and put it in the cache
		final DiagnosisData diagnosis = new DiagnosisData(signingTime, signingCertificate, signingCertificateSubject, signingCertificateIssuer, signatureFormatLevel, signatureConclusion, issuingCertificate, TechnicalTrustLevel.FAIL, "Not yet determined!");
		final ValidationData validation = new ValidationData(validSignatureComputation, validSignatureConclusion, validSignatureFormat, validSignatureCertStatus,
				validSignatureCertHistory, validTrustAnchor, validIssuerCertStatus, validIssuerCertHistory);
		final DecisionData decision = new DecisionData(diagnosis, validation);

		determineDecision(decision);

		DATA_CACHE.set(new SoftReference<DecisionData>(decision));

		return decision;
	}

	private void determineDecision(DecisionData decisionData) {

		// ########## PART A -> FAIL checks ##########################################
		LOG.lDetail("deciding: PART A -> FAIL checks");

		// 1. The signature has to be mathematical correct. Otherwise the result of the technical validation has to be RED
		if (!decisionData.validation.signatureComputation) {
			decide(decisionData, TechnicalTrustLevel.FAIL, "The signature is not mathematically correct.");
			return;
		}
		// 2. If DSS can recognize and analyze the signature format (e.g. PAdES-BES), the final conclusion can be GREEN, otherwise the final conclusion will be RED
		if (!decisionData.validation.signatureFormat) {
			decide(decisionData, TechnicalTrustLevel.FAIL, "The signature format could not be detected.");
			return;
		}
		// 3. QES, ADES_QC and ADES are allowed signatures levels and can create a GREEN result, an UNDETERMINED signature level will not be allowed and the final conclusion will be RED
		if (!decisionData.validation.signatureConclusion) {
			decide(decisionData, TechnicalTrustLevel.FAIL, "The signature conclusion is not sufficient.");
			return;
		}
		// 4. The signing certificate at least has to be valid at the time of signing (not revoked, not expired, recognizable by DSS).
		if (decisionData.validation.signatureCertStatus == TechnicalTrustLevel.FAIL || decisionData.validation.signatureCertHistory == TechnicalTrustLevel.FAIL) {
			decide(decisionData, TechnicalTrustLevel.FAIL, "The signature certificate is not valid at the time of signing (non-active or revoked).");
			return;
		}

		// 5. The only certificate with the need to be checked is the issuing certificate for the signing certificate.
		//    A validation down to a root certificate is not necessary.
		//    For this certificate, the same rules apply as they do for the signing certificate,
		//    with the addition, that the issuing certificate had to be valid at the time the signing certificate started to become valid.

		// checks that the signing certificate has be signed by the issuer certificate
		if (decisionData.validation.issuerCertStatus == TechnicalTrustLevel.FAIL) {
			decide(decisionData, TechnicalTrustLevel.FAIL, "The issuer certificate could not be detected or is invalid.");
			return;
		}
		if (decisionData.validation.issuerCertHistory != TechnicalTrustLevel.SUCCESSFUL) { // there is no SUFFICIENT for this check
			decide(decisionData, TechnicalTrustLevel.FAIL, "The issuer certificate is not valid at the time of signing (revoked, expired or not recognisable).");
			return;
		}

		// ########## PART B -> SUFFICIENT checks ####################################
		LOG.lDetail("deciding: PART B -> SUFFICIENT checks");

		// 4-3. Being valid at the time of signing with an CRL and/or an OCSP being defined, but none of them is reachable: YELLOW
		if (decisionData.validation.signatureCertStatus == TechnicalTrustLevel.SUFFICIENT || decisionData.validation.signatureCertHistory == TechnicalTrustLevel.SUFFICIENT) {
			decide(decisionData, TechnicalTrustLevel.SUFFICIENT,
					"The signature certificate's validity at the time of signing could not be fully determined (OCSP/CRL data not available).");
			return;
		}

		if (decisionData.diagnosis.signatureConclusion == SignatureQualification.QESIG) {
			// 5-1. In case of qualified signatures, the issuing certificate has to be present and verifiable at a national TSL.
			//      Otherwise the signature can be assessed with “AdES_QC” and the comment “Unable to verify the certificates issuer at a national TSL”.
			if (!decisionData.validation.trustAnchor) {
				final SignatureAttributes tokenSignatureAttributes = tValidation.getVerificationData().getSignatureData().get(0).getSignatureInformation();
				tokenSignatureAttributes.setSignatureLevel(SignatureQualification.ADESIG_QC.name());
				decide(decisionData, TechnicalTrustLevel.SUFFICIENT, "Unable to verify the certificate's issuer at a national TSL.");
				return;
			}
		} else //noinspection ConstantConditions
			if (decisionData.diagnosis.signatureConclusion == SignatureQualification.ADESIG_QC || decisionData.diagnosis.signatureConclusion == SignatureQualification.ADESIG_QC) {
				// only for clarity
				// 5-2. In case of AdES and AdES_QC, the issuing certificate should be validated against a TSL if possible
				//      and the result of the verification should be part of the technical validation part of the “Trust Ok”-Token.
				//      The outcome of this validation thereby does not affect the result of the technical validation.
			}

		// ########## PART C -> finally reached SUCCESSFUL ###########################
		LOG.lDetail("deciding: PART C -> finally reached SUCCESSFUL");

		// passed all the previous checks
		decide(decisionData, TechnicalTrustLevel.SUCCESSFUL, "The signature is valid.");
	}

	private void decide(final DecisionData data, final TechnicalTrustLevel level, final String comments) {

		DiagnosisData diag = data.getDiagnosis();

		if(diag != null){
			diag.setTrustLevel(level);
			diag.setComment(comments);
			LOG.lInfo("result determined to {}: {}", level, comments);
		}else{
			LOG.lWarn("Result hasn't been set. Diagnosis Data missing!");
		}

		final TechnicalValidationResult r = tValidation.getTechnicalResult();

		//TODO: Just a dummy decider: Take the lowest result as the general result.
		TechnicalTrustLevel curLevel = r.getTrustLevel();
		if(curLevel == null	||
				(curLevel.equals(TechnicalTrustLevel.SUCCESSFUL) && (level.equals(TechnicalTrustLevel.SUFFICIENT) || level.equals(TechnicalTrustLevel.FAIL))) ||
				(curLevel.equals(TechnicalTrustLevel.SUFFICIENT) && level.equals(TechnicalTrustLevel.FAIL))) {
			r.setTrustLevel(level);
			r.setComment(comments);
		}
	}

	/**
	 * gives access to the latest data that was used for computing the decision in the current thread
	 *
	 * @return the data (if stored and not garbage collected in the meantime)
	 */
	public static DecisionData getCachedDecisionData() {
		final SoftReference<DecisionData> ref = DATA_CACHE.get();
		return (ref == null) ? null : ref.get();
	}

	/**
	 * provides the data that is used to decide on the result; immutable
	 */
	public static class DecisionData {
		private final DiagnosisData diagnosis;
		private final ValidationData validation;
		private TechnicalTrustLevel level;

		/**
		 * constructor
		 *
		 * @param diagnosis  the value
		 * @param validation the value
		 */
		DecisionData(final DiagnosisData diagnosis, final ValidationData validation) {
			this.diagnosis = diagnosis;
			this.validation = validation;
		}

		/**
		 * gives access to the diagnosis data
		 *
		 * @return the value
		 */
		public DiagnosisData getDiagnosis() {
			return diagnosis;
		}

		/**
		 * gives access to the validation data
		 *
		 * @return the value
		 */
		public ValidationData getValidation() {
			return validation;
		}

		/**
		 * gives access to the decision
		 *
		 * @return the value
		 */
		public TechnicalTrustLevel getLevel() {
			return level;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return "DecisionData{" +
					"\nlevel=\n" + level +
					"\nvalidation=\n" + validation +
					"\ndiagnosis=\n" + diagnosis +
					"\n}";
		}
	}

	/**
	 * provides the data that is used for the validation; immutable
	 */
	public static class DiagnosisData {
		public final XMLGregorianCalendar signingTime;
		public final X509Certificate signingCertificate;
		public final String signingCertificateIssuer;
		public final String signingCertificateSubject;
		public final String signatureFormatLevel;
		public final SignatureQualification signatureConclusion;
		public final X509Certificate issuerCertificate;
		private TechnicalTrustLevel trustLevel;
		private String comment;

		/**
		 * constructor
		 *
		 * @param signingTime              the value
		 * @param signingCertificate       the value
		 * @param signingCertificateSubject the value
		 * @param signingCertificateIssuer the value
		 * @param signatureFormatLevel     the value
		 * @param signatureConclusion      the value
		 * @param issuerCertificate        the value
		 */
		DiagnosisData(final XMLGregorianCalendar signingTime, final X509Certificate signingCertificate, final String signingCertificateSubject, final String signingCertificateIssuer, final String signatureFormatLevel,
					  final SignatureQualification signatureConclusion, final X509Certificate issuerCertificate, final TechnicalTrustLevel trustLevel, final String comment) {
			this.signingTime = signingTime;
			this.signingCertificate = signingCertificate;
			this.signingCertificateSubject = signingCertificateSubject;
			this.signingCertificateIssuer = signingCertificateIssuer;
			this.signatureFormatLevel = signatureFormatLevel;
			this.signatureConclusion = signatureConclusion;
			this.issuerCertificate = issuerCertificate;
			this.trustLevel = trustLevel;
			this.comment = comment;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return "DiagnosisData{" +
					"\n   signingTime=" + signingTime +
					"\n   signingCertificateIssuer='" + signingCertificateIssuer + '\'' +
					"\n   signatureFormatLevel='" + signatureFormatLevel + '\'' +
					"\n   signatureConclusion=" + signatureConclusion +
					"\n   signingCertificate=" + signingCertificate +
					"\n   issuerCertificate=" + issuerCertificate +
					"\n}";
		}

		protected void setComment(String comment){
			this.comment = comment;
		}

		protected void setTrustLevel(TechnicalTrustLevel trustLevel){
			this.trustLevel = trustLevel;
		}
	}

	/**
	 * provides the result of the validation; immutable
	 */
	public static class ValidationData {
		public final boolean signatureComputation;
		public final boolean signatureConclusion;
		public final boolean signatureFormat;
		public final TechnicalTrustLevel signatureCertStatus;
		public final TechnicalTrustLevel signatureCertHistory;
		public final boolean trustAnchor;
		public final TechnicalTrustLevel issuerCertStatus;
		public final TechnicalTrustLevel issuerCertHistory;

		/**
		 * constructor
		 *
		 * @param signatureComputation the value
		 * @param signatureConclusion  the value
		 * @param signatureFormat      the value
		 * @param signatureCertStatus  the value
		 * @param signatureCertHistory the value
		 * @param trustAnchor          the value
		 * @param issuerCertStatus     the value
		 * @param issuerCertHistory    the value
		 */
		ValidationData(final boolean signatureComputation, final boolean signatureConclusion, final boolean signatureFormat, final TechnicalTrustLevel signatureCertStatus,
					   final TechnicalTrustLevel signatureCertHistory, final boolean trustAnchor, final TechnicalTrustLevel issuerCertStatus,
					   final TechnicalTrustLevel issuerCertHistory) {
			this.signatureComputation = signatureComputation;
			this.signatureConclusion = signatureConclusion;
			this.signatureFormat = signatureFormat;
			this.signatureCertStatus = signatureCertStatus;
			this.signatureCertHistory = signatureCertHistory;
			this.trustAnchor = trustAnchor;
			this.issuerCertStatus = issuerCertStatus;
			this.issuerCertHistory = issuerCertHistory;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return "ValidationData{" +
					"\n   signatureComputation=" + signatureComputation +
					"\n   signatureConclusion=" + signatureConclusion +
					"\n   signatureFormat=" + signatureFormat +
					"\n   signatureCertStatus=" + signatureCertStatus +
					"\n   signatureCertHistory=" + signatureCertHistory +
					"\n   trustAnchor=" + trustAnchor +
					"\n   issuerCertStatus=" + issuerCertStatus +
					"\n   issuerCertHistory=" + issuerCertHistory +
					"\n}";
		}
	}

	// NOT APPLICABLE WITH V4
	//	/**
	//	 * the list of {@link SignatureInformation}s returned by the DSS validation may contain null values, if no signing certificate has been found.
	//	 * so these values have to be filtered out.
	//	 * see also {@link SignatureInformation}
	//	 */
	//	public static class SignatureInformationListCleaner {
	//
	//		/**
	//		 * the list of {@link SignatureInformation}s returned by the DSS validation may contain null values, if no signing certificate has been found.
	//		 * so these values are skipped from the returned list.
	//		 *
	//		 * @param infos the nullable list
	//		 * @return always (even if infos is null) a list with no null values inside
	//		 */
	//        public static List<SignatureInformation> run(final List<SignatureInformation> infos) {
	//            final List<SignatureInformation> result = new ArrayList<SignatureInformation>();
	//            if (infos == null) {
	//                return result;
	//            }
	//            for (final SignatureInformation info : infos) {
	//                if (info == null) {
	//                    continue;
	//                }
	//                result.add(info);
	//            }
	//            return result;
	//        }
	//}

	/**
	 * compares two signatures (information) via the signing time in the order <code>null - t0 - t1 ...</code>.
	 * a signature without signing time will be treated as "lower" than one with.
	 * see also {@link AdvancedSignature}
	 */
	public static class SignatureTimeComparator implements Comparator<AdvancedSignature> {

		/**
		 * a singleton that can be used to avoid re-instantiation.
		 */
		public static final SignatureTimeComparator INSTANCE = new SignatureTimeComparator();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compare(final AdvancedSignature o1, final AdvancedSignature o2) {
			final Date t1 = getSigningTime(o1);
			final Date t2 = getSigningTime(o2);
			if (t1 == null && t2 == null) {
				return 0;
			}
			if (t1 == null && t2 != null) {
				return -1;
			}
			if (t1 != null && t2 == null) {
				return 1;
			}
			return t1.compareTo(t2);
		}

		/**
		 * derives the signing time using the {@link AdvancedSignature}
		 *
		 * @param si the object
		 * @return the date
		 */
		private static Date getSigningTime(final AdvancedSignature si) {
			try {
				return si.getSigningTime();
			} catch (Exception e) {
				return null;
			}
		}

		/**
		 * creates a copy of the list in parameter and sort that using the {@link eu.ecodex.dss.service.impl.dss.DSSTokenValidationCreator.SignatureTimeComparator} singleton
		 *
		 * @param signatures the non-null list
		 * @return the sorted copy of the list
		 */
		public static List<AdvancedSignature> createSortedList(final List<AdvancedSignature> signatures) {
			final List<AdvancedSignature> result = new ArrayList<AdvancedSignature>(signatures);
			sort(result);
			return result;
		}

		/**
		 * sorts a list using the {@link eu.ecodex.dss.service.impl.dss.DSSTokenValidationCreator.SignatureTimeComparator} singleton
		 *
		 * @param signatures the list
		 */
		public static void sort(final List<AdvancedSignature> signatures) {
			Collections.sort(signatures, INSTANCE);
		}

		/**
		 * retrieves the first (earliest) SignatureInformation in the list
		 *
		 * @param infos the non-null list
		 * @return the result (may be null)
		 */
		public static AdvancedSignature getFirst(final List<AdvancedSignature> infos) {
			final List<AdvancedSignature> sorted = createSortedList(infos);
			return sorted.isEmpty() ? null : sorted.get(0);
		}

		/**
		 * retrieves the last (latest) SignatureInformation in the list
		 *
		 * @param signatures the non-null list
		 * @return the result (may be null)
		 */
		public static AdvancedSignature getLast(final List<AdvancedSignature> signatures) {

			final List<AdvancedSignature> sorted = createSortedList(signatures);
			return sorted.isEmpty() ? null : sorted.get(sorted.size() - 1);
		}

	}

	public void setIgnoredCertificatesStore(CertificateSource ignoredCertificatesStore) {
		this.ignoredCertificateStore = ignoredCertificatesStore;
	}
}