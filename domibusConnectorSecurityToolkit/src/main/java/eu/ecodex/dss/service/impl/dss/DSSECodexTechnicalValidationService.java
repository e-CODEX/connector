/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/service/impl/dss/DSSECodexTechnicalValidationService.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */
package eu.ecodex.dss.service.impl.dss;

import eu.ecodex.dss.model.token.AuthenticationCertificate;
import eu.ecodex.dss.model.token.OriginalValidationReportContainer;
import eu.ecodex.dss.model.token.Token;
import eu.ecodex.dss.model.token.TokenValidation;
import eu.ecodex.dss.model.token.ValidationVerification;
import eu.ecodex.dss.service.ECodexException;
import eu.ecodex.dss.service.ECodexTechnicalValidationService;
import eu.ecodex.dss.util.LogDelegate;
import eu.ecodex.dss.util.PdfValidationReportService;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.enumerations.MimeTypeEnum;
import eu.europa.esig.dss.model.DSSDocument;

import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.InMemoryDocument;

import eu.europa.esig.dss.policy.EtsiValidationPolicy;
import eu.europa.esig.dss.simplereport.SimpleReport;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;

import eu.europa.esig.dss.spi.x509.CandidatesForSigningCertificate;
import eu.europa.esig.dss.spi.x509.CertificateSource;
import eu.europa.esig.dss.spi.x509.CertificateValidity;
import eu.europa.esig.dss.validation.AdvancedSignature;

import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.executor.DocumentProcessExecutor;
import eu.europa.esig.dss.validation.reports.Reports;

import eu.europa.esig.dss.model.x509.CertificateToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.security.auth.x500.X500Principal;

import org.apache.commons.io.IOUtils;

/**
 * The DSS implementation of the services
 *
 * <p>
 * DISCLAIMER: Project owner e-CODEX
 * </p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
public class DSSECodexTechnicalValidationService implements ECodexTechnicalValidationService {

	private static final LogDelegate LOG = new LogDelegate(DSSECodexTechnicalValidationService.class);

	//	private ProxyConfig preferenceManager;
	private final EtsiValidationPolicy etsiValidationPolicy;
	private final CertificateVerifier certificateVerifier;
	private final DocumentProcessExecutor processExecutor;
	private final Optional<TrustedListsCertificateSource> trustedListCertificatesSource;
	private final Optional<CertificateSource> ignoredCertificatesStore;

	public DSSECodexTechnicalValidationService(EtsiValidationPolicy etsiValidationPolicy,
											   CertificateVerifier certificateVerifier,
											   DocumentProcessExecutor processExecutor,
											   Optional<TrustedListsCertificateSource> trustedListCertificatesSource,
											   Optional<CertificateSource> ignoredCertificatesStore) {
		this.etsiValidationPolicy = etsiValidationPolicy;
		this.certificateVerifier = certificateVerifier;
		this.processExecutor = processExecutor;
		this.trustedListCertificatesSource = trustedListCertificatesSource;
		this.ignoredCertificatesStore = ignoredCertificatesStore;
	}


	/**
	 * The method {@code #setProcessExecutor} must be called before.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public TokenValidation create(final DSSDocument businessDocument, final DSSDocument detachedSignature) throws ECodexException {
		LOG.mEnter("create", businessDocument, detachedSignature);
		try {
			final DSSTokenValidationCreator delegate = new DSSTokenValidationCreator(etsiValidationPolicy, certificateVerifier, businessDocument, detachedSignature, processExecutor);
			delegate.setIgnoredCertificatesStore(ignoredCertificatesStore.orElse(null));
			delegate.run();
			return delegate.getResult();
		} catch (Exception e) {
			LOG.mCause("create", e, businessDocument, detachedSignature);
			throw ECodexException.wrap(e);
		} finally {
			LOG.mExit("create", businessDocument, detachedSignature);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * the report must contain exactly one object of type {@link DiagnosticData}
	 */
	@Override
	public DSSDocument createReportPDF(final Token token) throws ECodexException {
		LOG.mEnter("createReportPDF", token);
		try {
			return createReportPDFImpl(token);
		} catch (Exception e) {
			LOG.mCause("createReportPDF", e, token);
			throw ECodexException.wrap(e);
		} finally {
			LOG.mExit("createReportPDF", token);
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
			throw new ECodexException("the token (in parameter) must have a validation object with an existing verification data");
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

		// TODO: Reaktivieren?
//		if (reportDatas.size() > 2) {
//			throw new ECodexException("the report (of the token's validation object) must contain not more than two objects, but has " + reportDatas.size());
//		}
//		final Object reportData = reportDatas.get(0);
//		if (!(reportData instanceof DiagnosticData)) {
//			throw new ECodexException(
//				  "the report (of the token's validation object) must contain exactly one instance of type " + DiagnosticData.class + ", but is " + (reportData == null ? null : reportData
//						.getClass())
//			);
//		}
//
//		final Object reportData2 = reportDatas.get(1);
//		if (!(reportData2 instanceof SimpleReport)) {
//			throw new ECodexException(
//				  "the report (of the token's validation object) must contain exactly one instance of type " + SimpleReport.class + ", but is " + (reportData2 == null ? null : reportData2
//						.getClass())
//			);
//		}

		final ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();

		try {
			if(report.getReports() != null) {
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

//	/**
//	 * sets the certificate verifier used for the token validation
//	 *
//	 * @param certificateVerifier the value
//	 */
//	public void setCertificateVerifier(final CertificateVerifier certificateVerifier) {
//		this.certificateVerifier = certificateVerifier;
//		LOG.lConfig("set certificateVerifier: " + certificateVerifier);
//	}

	/**
	 * {@inheritDoc}
	 *
	 *
	 * note that you first have to use {@link #setProxyPreferenceManager} in order that this call has some effect -
	 * otherwise
	 * it will be ignored
	 */
//	public void setEnvironmentConfiguration(final EnvironmentConfiguration environmentConfiguration) {
//		this.environmentConfiguration = environmentConfiguration;
//
//		LOG.lConfig("set environment configuration: " + environmentConfiguration);
//
//		if (preferenceManager == null) {
//			LOG.lWarn("NO preference manager set - unable to forward environment configuration", environmentConfiguration);
//			return;
//		}
//
//		final ProxyData proxyHttp = (environmentConfiguration == null) ? null : environmentConfiguration.getProxyHTTP();
//		final ProxyData proxyHttps = (environmentConfiguration == null) ? null : environmentConfiguration.getProxyHTTPS();
//
//		if(proxyHttp != null) {
//			LOG.lInfo("HTTP Configuration detected in EnvironmentConfiguration: Taking configuration from EnvironmentConfiguration.");
//			ProxyProperties httpProperties = new ProxyProperties();
//			httpProperties.setHost((proxyHttp == null) ? null : proxyHttp.getHost());
//			httpProperties.setPort((proxyHttp == null) ? null : proxyHttp.getPort());
//			httpProperties.setUser((proxyHttp == null) ? null : proxyHttp.getAuthName());
//			httpProperties.setPassword((proxyHttp == null) ? null : proxyHttp.getAuthPass());
//			preferenceManager.setHttpProperties(httpProperties);
//		} else {
//			LOG.lInfo("No HTTP Configuration detected in EnvironmentConfiguration: Taking configuration from ProxyPreferenceManager.");
//		}
//
//		if(proxyHttps != null) {
//			LOG.lInfo("HTTPS Configuration detected in EnvironmentConfiguration: Taking configuration from EnvironmentConfiguration.");
//			ProxyProperties httpsProperties = new ProxyProperties();
//			httpsProperties.setHost((proxyHttps == null) ? null : proxyHttps.getHost());
//			httpsProperties.setPort((proxyHttps == null) ? null : proxyHttps.getPort());
//			httpsProperties.setUser((proxyHttps == null) ? null : proxyHttps.getAuthName());
//			httpsProperties.setPassword((proxyHttps == null) ? null : proxyHttps.getAuthPass());
//			preferenceManager.setHttpProperties(httpsProperties);
//		} else {
//			LOG.lInfo("No HTTPS Configuration detected in EnvironmentConfiguration: Taking configuration from ProxyPreferenceManager.");
//		}
//
//		String logMessage = "Proxy Configuration for security library: \n" +
//							"HTTP Proxy Enabled: " + (preferenceManager.getHttpProperties() != null);
//
//		if(preferenceManager.getHttpProperties() != null) {
//			logMessage = logMessage + "\nHTTP Proxy Host: " + preferenceManager.getHttpProperties().getHost();
//		}
//
//		logMessage = logMessage + "\nHTTPS Proxy Enabled: " + (preferenceManager.getHttpsProperties() != null);
//
//		if(preferenceManager.getHttpsProperties() != null) {
//			logMessage = logMessage + "\nHTTPS Proxy Host: " + preferenceManager.getHttpsProperties().getHost();
//		}
//
//		LOG.lInfo(logMessage);
//	}

//	/**
//	 * sets the proxy preference manager, which is required for {@link #setEnvironmentConfiguration(eu.ecodex.dss.model.EnvironmentConfiguration)}
//	 *
//	 * @param proxyPreferenceManager the value
//	 */
//	public void setProxyPreferenceManager(final ProxyConfig proxyPreferenceManager) {
//		this.preferenceManager = proxyPreferenceManager;
//		LOG.lConfig("set proxy preference manager: " + proxyPreferenceManager);
//
//		String logMessage = "Proxy Configuration for security library: \n" +
//				"HTTP Proxy Enabled: " + (preferenceManager.getHttpProperties() == null);
//
//		if(preferenceManager.getHttpProperties() != null) {
//			logMessage = logMessage + "\nHTTP Proxy Host: " + preferenceManager.getHttpProperties().getHost();
//		}
//
//		logMessage = logMessage + "\nHTTPS Proxy Enabled: " + (preferenceManager.getHttpsProperties() == null);
//
//		if(preferenceManager.getHttpsProperties() != null) {
//			logMessage = logMessage + "\nHTTPS Proxy Host: " + preferenceManager.getHttpsProperties().getHost();
//		}
//
//		LOG.lInfo(logMessage);
//	}

//	/**
//	 * This method sets the {@code ProcessExecutor} that holds the logic of the validation.
//	 *
//	 * @param processExecutor
//	 */
//	public void setProcessExecutor(DocumentProcessExecutor processExecutor) {
//		this.processExecutor = processExecutor;
//	}

//    // klara
//	/**
//	 * This method is used for one-time initialization of a configured TSL for the purpose of authentication certificate verification.
//	 * A TSL can be configured using e.g. {@link #setAuthenticationCertificateTSL(String authenticationCertificateTSL)}.
//	 *
//	 * @throws ECodexException
//	 **/
//	public void initAuthenticationCertificateVerification() throws ECodexException{
//
//		org.w3c.dom.Document inMemoryTSL = createInMemoryTSL();
//
//		ReactiveDataLoader dataLoader = new ReactiveDataLoader(inMemoryTSL, this.authenticationCertificateTSL, this.preferenceManager);
//
//		dataLoader.isLOTL(this.isLOTL);

//		TLInfo test = new TLInfo();

//		trustedListCertificatesSource = new TrustedListsCertificateSource();

//		TSLRepository tslRepository = new TSLRepository();
//		tslRepository.setTrustedListsCertificateSource(trustedListCertificatesSource);

//        TSLValidationJob job = new TSLValidationJob();
//        job.setDataLoader(dataLoader);
//        job.setCheckTSLSignatures(false);
//        job.setRepository(tslRepository);

//		if(!isLOTL){
//			job.setLotlUrl("inmemory:intermediatetsl");
//		}else if(authenticationCertificateTSL instanceof String){
//			job.setLotlUrl((String) authenticationCertificateTSL);
//		}else if(authenticationCertificateTSL instanceof InputStream){
//			job.setLotlUrl("inmemory:inputstream");
//		}else if(authenticationCertificateTSL instanceof byte[]){
//			job.setLotlUrl("inmemory:bytearray");
//		}else{
//			// trustedListCertificatesSource.setLotlCertificate(null);
//		}

//		try {
//			job.initRepository();
//		} catch (Exception e) {
//			throw new ECodexException(e);
//		}
//	}

//	// klara
//	/**
//	 * This method provides the information to the library whether the given TSL is a LOTL, a TSL containing a list of
//	 * TSLs, or a normal TSL. The default value of this attribute is "false".
//	 * - false: TSL is a list of trusted certificates
//	 * - true: TSL is a list of lists
//	 *
//	 * @param isLOTL
//	 **/
//	public void isAuthenticationCertificateLOTL(boolean isLOTL) {
//		this.isLOTL = isLOTL;
//	}

	// klara: Added method for certificate verification against TSL of authentication-certificates
	/**
	 * This method checks whether the certificate a signature has been created with on a given document (within the document or detached) is present within
	 * a defined TSL. Before being able to verify the TSL (trustedListCertificatesSource) needs to be configured
	 *
	 *
	 * @param businessDocument
	 * @param detachedSignature - null if no detached signature is present
	 * @return AuthenticationCertificate - The result of the verification
	 * @throws ECodexException
	 **/
	protected AuthenticationCertificate verifyAuthenticationCertificate(final DSSDocument businessDocument, final DSSDocument detachedSignature) throws ECodexException {

		AuthenticationCertificate validationResult = new AuthenticationCertificate();

		if(trustedListCertificatesSource.isPresent()) {

			X509Certificate signatureCert = getCertificate(businessDocument, detachedSignature);

			if(signatureCert != null)
			{
				X500Principal subjectName = signatureCert.getSubjectX500Principal();

				List<CertificateToken> tslCerts = trustedListCertificatesSource.get().getCertificates();

				if(tslCerts != null && !tslCerts.isEmpty())	{
					Iterator<CertificateToken> it = tslCerts.iterator();

					while(it.hasNext()){
						CertificateToken curCertAndContext = it.next();

						if(curCertAndContext.getCertificate().getSubjectX500Principal().equals(subjectName)){
							if(curCertAndContext.getCertificate().equals(signatureCert)){
								validationResult.setValidationSuccessful(true);
							}
						}
					}
				}else{
					validationResult.setValidationSuccessful(false);
					LOG.lInfo("TSL of authentication certificates was null or empty - No certificate will be deemed to bevalid.");
				}
			}
		} else {
			validationResult.setValidationSuccessful(false);
			LOG.lInfo("Attribute authenticationCertificateTSL has not been set - Validation of signature certificate against TSL of authentication service providers will always fail!");
		}

//    	if(this.authenticationCertificateTSL != null) {
//
//    		X509Certificate signatureCert = getCertificate(businessDocument, detachedSignature);
//
//    		if(signatureCert != null)
//    		{
//    			X500Principal subjectName = signatureCert.getSubjectX500Principal();
//
//				List<CertificateToken> tslCerts = trustedListCertificatesSource.getCertificates();
//
//				if(tslCerts != null && !tslCerts.isEmpty())	{
//					Iterator<CertificateToken> it = tslCerts.iterator();
//
//					while(it.hasNext()){
//						CertificateToken curCertAndContext = it.next();
//
//						if(curCertAndContext.getCertificate().getSubjectX500Principal().equals(subjectName)){
//							if(curCertAndContext.getCertificate().equals(signatureCert)){
//								validationResult.setValidationSuccessful(true);
//							}
//						}
//					}
//				}else{
//					validationResult.setValidationSuccessful(false);
//					LOG.lInfo("TSL of authentication certificates was null or empty - No certificate will be deemed to bevalid.");
//				}
//    		}
//    	} else {
//    		validationResult.setValidationSuccessful(false);
//    		LOG.lInfo("Attribute authenticationCertificateTSL has not been set - Validation of signature certificate against TSL of authentication service providers will always fail!");
//    	}

		return validationResult;
	}



	// klara
	private X509Certificate getCertificate(DSSDocument businessDocument, DSSDocument detachedSignature) throws ECodexException {

		X509Certificate signatureCert = null;

		if(detachedSignature == null)
		{
			InputStream is = null;
			ByteArrayOutputStream baos = null;

			try {
				is = businessDocument.openStream();
				baos = new ByteArrayOutputStream();

				int nRead;
				byte[] data = new byte[16384];

				while ((nRead = is.read(data, 0, data.length)) != -1) {
					baos.write(data, 0, nRead);
				}

				baos.flush();
				byte[] document = baos.toByteArray();

				InMemoryDocument file = new InMemoryDocument(document);

				try{
					SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(file);
					validator.setCertificateVerifier(new CommonCertificateVerifier(true)); //TODO: use CertificateVerifier factory

					Reports rep = validator.validateDocument();
//					String certId = rep.getDiagnosticData().getFirstSigningCertificateId();
					String certId = rep.getDiagnosticData().getFirstSignatureId();

					List<AdvancedSignature> signatures = validator.getSignatures();
					Iterator<AdvancedSignature> it = signatures.listIterator();

					Date lastSignature = null;

					//klara: This could become troublesome with timezones
					while(it.hasNext()) {
						AdvancedSignature curSig = it.next();
						if(lastSignature == null || lastSignature.after(curSig.getSigningTime())){

							CandidatesForSigningCertificate candidates = curSig.getCandidatesForSigningCertificate();

							signatureCert =  candidates.getCertificateValidityList()
									.stream()
									.map(CertificateValidity::getCertificateToken)
									.filter(c -> c.getDSSIdAsString().equals(certId))
									.map(CertificateToken::getCertificate)
									.findFirst().orElse(null);

						}
					}
				} catch(DSSException ex) {
					/* This exception is thrown by "SignedDocumentValidator.fromDocument(file)" in case of
					 * - Document Type is not recognized (No PDF, ASiC-S, XML or CMS file)
					 * - File is to short (Length < 5 Byte)
					 *
					 *  No reason to react here. Result is "No signing certificate in place"
					 */
				}

			} catch (IOException e) {
				throw new ECodexException(e);
			} finally {
				if(is != null)
					IOUtils.closeQuietly(is);
				if(baos != null)
					IOUtils.closeQuietly(baos);
			}
		}

		return signatureCert;
	}

//	/**
//	 * This method can be used to configure a TSL to be used for the verification of whether certificates used to sign
//	 * a document are representing a, authentication service provider (and thereby are present on the TSL)
//	 *
//	 * @param authenticationCertificateTSL - The URI to a TSL to be used. Currently supported are "http:", "https:" and "file:"
//	 **/
//    public void setAuthenticationCertificateTSL(String authenticationCertificateTSL) {
//		this.authenticationCertificateTSL = authenticationCertificateTSL;
//	}
//
//	/**
//	 * This method can be used to configure a TSL to be used for the verification of whether certificates used to sign
//	 * a document are representing a, authentication service provider (and thereby are present on the TSL)
//	 *
//	 * @param authenticationCertificateTSL - The InputStream to a TSL
//	 **/
//    public void setAuthenticationCertificateTSL(InputStream authenticationCertificateTSL) {
//		this.authenticationCertificateTSL = authenticationCertificateTSL;
//	}
//
//	/**
//	 * This method can be used to configure a TSL to be used for the verification of whether certificates used to sign
//	 * a document are representing a, authentication service provider (and thereby are present on the TSL)
//	 *
//	 * @param authenticationCertificateTSL - The TSL as byte[]
//	 **/
//    public void setAuthenticationCertificateTSL(byte[] authenticationCertificateTSL) {
//		this.authenticationCertificateTSL = authenticationCertificateTSL;
//	}

//    public void setIgnoredCertificatesStore(CertificateStoreInfo ignoredCertificatesStore) {
//		try {
//			LOG.lConfig("updating the ignored certificates");
//			if(this.ignoredCertificatesStore == null){
//				this.ignoredCertificatesStore = new ConnectorCertificatesStore();
//			}
//
//			this.ignoredCertificatesStore.update(ignoredCertificatesStore);
//		} catch (Exception e) {
//			LOG.lError("could not update the ignored certificates", e);
//			throw (e instanceof RuntimeException) ? (RuntimeException) e : new RuntimeException(e);
//		}
//    }
}
