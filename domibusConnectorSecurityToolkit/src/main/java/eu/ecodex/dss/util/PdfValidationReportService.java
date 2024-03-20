/*
 * Project: Digital Signature Services (DSS)
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/container/tags/ecodex-container-DSS-library-2.5.0.3/apps/dss/dss-report/src/main/java/eu/europa/ec/markt/dss/report/PdfValidationReportService.java $
 * $Revision: 2149 $
 * $Date: 2013-05-29 20:59:24 +0200 (Wed, 29 May 2013) $
 * $Author: meyerfr $
 */
package eu.ecodex.dss.util;


import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import eu.europa.esig.dss.NamespaceContextMap;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.simplereport.SimpleReport;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.xml.xpath.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;


/**
 * This service create a PDF report from the validation report of the document.
 *
 *
 * DISCLAIMER: Project owner DG-MARKT.
 *
 * @author <a href="mailto:dgmarkt.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 2149 $ - $Date: 2013-05-29 20:59:24 +0200 (Wed, 29 May 2013) $
 */
public class PdfValidationReportService {

	private static final XPathFactory factory = XPathFactory.newInstance();

	public static final String NAMESPACE = "http://dss.markt.ec.europa.eu/validation/diagnostic";

	private static final NamespaceContextMap nsContext;

	private static final Map<String, String> namespaces;

	static {

		namespaces = new HashMap<String, String>();
		namespaces.put("dss", NAMESPACE);
		nsContext = new NamespaceContextMap();

		Set<String> keys = namespaces.keySet();
		
		for (String curKey : keys) {
			nsContext.registerNamespace(curKey, namespaces.get(curKey));
		}
	}

	private static final class Resources {
		private static final Font defaultFont;
		private static final Font header1Font;
		private static final Font header2Font;
		private static final Font header3Font;
		private static final Font header4Font;
		private static final Font header5Font;
		private static final Font monoFont;
		private static final Image okImage;
		private static final Image koImage;

		static {
			try {
				defaultFont = createFont("LiberationSans-Regular.ttf", 9);
				header1Font = createFont("LiberationSans-Bold.ttf", 14);
				header1Font.setColor(54, 95, 145);
				header2Font = createFont("LiberationSans-Bold.ttf", 13);
				header2Font.setColor(79, 129, 189);
				header3Font = createFont("LiberationSans-Bold.ttf", 12);
				header3Font.setColor(79, 129, 189);
				header4Font = createFont("LiberationSans-BoldItalic.ttf", 11);
				header4Font.setColor(79, 129, 189);
				header5Font = createFont("LiberationSans-Regular.ttf", 10);
				header5Font.setColor(79, 129, 189);
				monoFont = createFont("LiberationMono-Regular.ttf", 8);

				okImage = Image.getInstance(ImageIO.read(PdfValidationReportService.class.getResourceAsStream("/ok.jpg")), null);
				okImage.scaleToFit(9, 9);
				okImage.setSpacingAfter(25);
				okImage.setSmask(false);

				koImage = Image.getInstance(ImageIO.read(PdfValidationReportService.class.getResourceAsStream("/error.jpg")), null);
				koImage.scaleToFit(9, 9);
				koImage.setSmask(false);

			} catch (Exception e) {
				throw new ExceptionInInitializerError(e);
			}
		}

		private static Font createFont(final String name, final int size) throws IOException, DocumentException {
			final byte[] data;
			final BaseFont bfo;
			data = IOUtils.toByteArray(PdfValidationReportService.class.getResourceAsStream("/" + name));
			bfo = BaseFont.createFont(name, BaseFont.WINANSI, BaseFont.EMBEDDED, BaseFont.CACHED, data, null);
			return new Font(bfo, size);
		}
	}

	private enum ParagraphStyle {
		HEADER1, HEADER2, HEADER3, HEADER4, HEADER5, DEFAULT, CODE
	}

	private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	/**
	 * The default constructor for PdfValidationReportService.
	 */
	public PdfValidationReportService() {
	}

	public void createReport(DiagnosticData diagnosticData, SimpleReport simpleReport, OutputStream pdfStream) throws IOException {

		try {
			Document document = new Document();
			PdfWriter writer = PdfWriter.getInstance(document, pdfStream);
			writer.setPdfVersion(PdfWriter.PDF_VERSION_1_4);
			writer.setPDFXConformance(PdfWriter.PDFA1B);
			document.open();

			document.add(p("Time information", ParagraphStyle.HEADER1));
			document.add(p("Verification Time: " + sdf.format(simpleReport.getValidationTime())));

			int i = 1;
			final List<String> signatureIdList = simpleReport.getSignatureIdList();
			// final List<String> signatureIdList = diagnosticData.getSignatureIdList();
			for (String signatureId : signatureIdList) {

				writeSignatureInformation(document, signatureId, diagnosticData, simpleReport, i++);
			}

			writer.createXmpMetadata();
			document.close();
		} catch (DocumentException e) {
			throw new IOException(e);
		}
	}

	private void writeSignatureInformation(Document document, final String signatureId, final DiagnosticData diagnosticData, final SimpleReport simpleReport,
	                                       int index) throws DocumentException {

		document.add(p("Signature information " + index, ParagraphStyle.HEADER1));
		document.add(p("Signature verification", simpleReport.isValid(signatureId), ParagraphStyle.DEFAULT));
		document.add(p("Signature algorithm: " + diagnosticData.getSignatureEncryptionAlgorithm(signatureId)));

		document.add(p("Certificate Path Revocation Analysis", ParagraphStyle.HEADER2));

		final List<String> signatureCertificateChain = diagnosticData.getSignatureCertificateChain(signatureId);

		boolean valid = false;
		for (String certificateId : signatureCertificateChain) {
			
			final boolean validCertificate = diagnosticData.getCertificateRevocationStatus(certificateId).isGood(); //isValidCertificate(certificateId);
			if (!validCertificate) {

				valid = false;
				break;
			}
			valid = true;
		}
		document.add(p("Summary", valid, null));

		document.add(p("Certificate Verification", ParagraphStyle.HEADER3));
		if (signatureCertificateChain.isEmpty()) {
			document.add(p("No Certificate Verification is available!"));
		} else {
			for (String certificateId : signatureCertificateChain) {
				writeCertificateVerification(document, certificateId, diagnosticData);
			}
		}

		final String signingCertificateId = diagnosticData.getSigningCertificateId(signatureId);
		
//		document.add(p("Trusted List Information", ParagraphStyle.HEADER3));
//		document.add(p("Service was found", DSSUtils.isNotBlank(diagnosticData.getCertificateTSPServiceName(signingCertificateId)), null));
//		document.add(p("Trusted List is well-signed", diagnosticData.isCertificateRelatedTSLWellSigned(signingCertificateId), null));

		
		document.add(p("Signature Level Analysis", ParagraphStyle.HEADER2));
		final String signatureFormat = diagnosticData.getSignatureFormat(signatureId).toString(); //TODO: use enumerations here instead of toString()
		if (signatureFormat == null || signatureFormat.equals("")) {
			document.add(p("No Signature Level Analysis is available."));
		} else {

			document.add(p("Signature format: " + signatureFormat));
			writeLevelBES(document, signatureId, signingCertificateId, diagnosticData, signatureCertificateChain);
			writeLevelEPES(document, signatureId, diagnosticData);
			writeLevelT(document, signatureId, diagnosticData);
			// writeLevelC(document, diagnosticData);
			writeLevelX(document, signatureId, diagnosticData);
			// writeLevelXL(document, diagnosticData);
			writeLevelA(document, signatureId, diagnosticData);
		}

//		document.add(p("Qualification Verification", ParagraphStyle.HEADER2));
//		document.add(p("QCWithSSCD", diagnosticData.hasCertificateQCWithSSCDQualification(signingCertificateId), null));
//		document.add(p("QCNoSSCD", diagnosticData.hasCertificateQCNoSSCDQualification(signingCertificateId), null));
//		document.add(p("QCSSCDStatusAsInCert", diagnosticData.hasCertificateQCSSCDStatusAsInCertQualification(signingCertificateId), null));
//		document.add(p("QCForLegalPerson", diagnosticData.hasCertificateQCForLegalPersonQualification(signingCertificateId), null));
//
//		document.add(p("QC Statement Information", ParagraphStyle.HEADER2));
//		document.add(p("QCP presence", diagnosticData.is isCertificateQCP(signingCertificateId), null));
//		document.add(p("QCP+ presence", diagnosticData.isCertificateQCPPlus(signingCertificateId), null));
//		document.add(p("QcCompliance presence", diagnosticData.isCertificateQCC(signingCertificateId), null));
//		document.add(p("QcSSCD presence", diagnosticData.isCertificateQCSSCD(signingCertificateId), null));

		document.add(p("Final Conclusion", ParagraphStyle.HEADER2));
		document.add(p("The signature is: " + simpleReport.getSignatureQualification(signatureId).name())); // getSignatureLevel(signatureId).name()));
	}

	/*
	private ArrayList<String> getSignatureCertificateChain(String signatureId, DiagnosticData diagnosticData) {

		final Element rootElement = diagnosticData.getRootElement();
		final String xPathQuery = format("/DiagnosticData/Signature[@Id='%s']/CertificateChain/ChainCertificate/@Id", signatureId);
		final NodeList nodeList = getNodeList(rootElement, xPathQuery);
		final ArrayList<String> signatureCertificateChain = new ArrayList<String>();
		for (int ii = 0; ii < nodeList.getLength(); ii++) {

			final Node node = nodeList.item(ii);
			if (node != null && node.getNodeType() == Node.ATTRIBUTE_NODE) {

				final String textContent = node.getTextContent();
				signatureCertificateChain.add(textContent);
			}
		}
		return signatureCertificateChain;
	}
	*/

	private void writeLevelBES(final Document document, final String signatureId, String signingCertificateId, final DiagnosticData diagnosticData,
	                           List<String> certificateChain) throws DocumentException {

		final boolean signingCertificateIdentified = diagnosticData.isSigningCertificateIdentified(signatureId);

		if (!signingCertificateIdentified) {
			document.add(p("Signature Level BES", false, ParagraphStyle.HEADER3));
			return;
		}

		final Date signatureDate = diagnosticData.getSignatureDate(signatureId);
		document.add(p("Signature Level BES", signatureDate != null, ParagraphStyle.HEADER3));

		document.add(p("Signing certificate: " + diagnosticData.getCertificateIssuerDN(signingCertificateId)));

		document.add(p("Signing time: " + sdf.format(signatureDate)));

		document.add(p("Certificates", ParagraphStyle.HEADER4));
		document.add(p("Number of certificates in the chain: " + certificateChain.size()));
		//		for (X509Certificate c : level.getCertificates()) {
		//			writeCertificate(document, c);
		//		}
	}

	private void writeLevelEPES(final Document document, String signatureId, final DiagnosticData diagnosticData) throws DocumentException {

		final String policyId = diagnosticData.getPolicyId(signatureId);
		if (policyId == null || policyId.equals("")) {
			document.add(p("Signature Level EPES", false, ParagraphStyle.HEADER3));
			return;
		}

		document.add(p("Signature Level EPES ", true, ParagraphStyle.HEADER3));
		document.add(p("Signature policy: " + policyId));
	}

	private void writeLevelT(final Document document, String signatureId, final DiagnosticData diagnosticData) throws DocumentException {

		final boolean levelPresent = diagnosticData.isThereTLevel(signatureId);
		if (!levelPresent) {
			document.add(p("Signature Level T", false, ParagraphStyle.HEADER3));
			return;
		}
		final boolean tLevelTechnicallyValid = diagnosticData.isTLevelTechnicallyValid(signatureId);
		document.add(p("Signature Level T", tLevelTechnicallyValid, ParagraphStyle.HEADER3));
		final List<String> timestampIdList = diagnosticData.getTimestampIdList(signatureId);
		document.add(p("Number of timestamps found: " + timestampIdList.size()));
		for (final String timestampId : timestampIdList) {

			writeTimestampResultInformation(document, timestampId, diagnosticData);
		}
	}

	//	private void writeLevelC(final Document document, final DiagnosticData si) throws DocumentException {
	//		if (si == null || si.getSignatureLevelAnalysis() == null || si.getSignatureLevelAnalysis().getLevelC() == null) {
	//			// document.add(p("Signature Level C", false, ParagraphStyle.HEADER3));
	//			return;
	//		}
	//
	//		final SignatureLevelC level = si.getSignatureLevelAnalysis().getLevelC();
	//
	//		document.add(p("Signature Level C", new R() {
	//			Result o() {
	//				return level.getLevelReached();
	//			}
	//		}, ParagraphStyle.HEADER3));
	//
	//		if (level.getCertificateRefsVerification() != null && level.getCertificateRefsVerification().isValid()) {
	//			document.add(p("All the certificate references needed are in the signature."));
	//		} else {
	//			document.add(p("Some required certificate references are not in the signature."));
	//		}
	//
	//		if (level.getRevocationRefsVerification() != null && level.getRevocationRefsVerification().isValid()) {
	//			document.add(p("All the revocation information references needed are in the signature."));
	//		} else {
	//			document.add(p("Some required revocation information references are not in the signature."));
	//		}
	//	}

	private void writeLevelX(final Document document, String signatureId, final DiagnosticData diagnosticData) throws DocumentException {

		final boolean levelPresent = diagnosticData.isThereXLevel(signatureId);
		if (!levelPresent) {
			document.add(p("Signature Level X", false, ParagraphStyle.HEADER3));
			return;
		}
		final boolean xLevelTechnicallyValid = diagnosticData.isXLevelTechnicallyValid(signatureId);
		document.add(p("Signature Level X", xLevelTechnicallyValid, ParagraphStyle.HEADER3));
		final List<String> timestampIdList = diagnosticData.getTimestampIdList(signatureId);
		document.add(p("Number of timestamps found: " + timestampIdList.size()));
		for (final String timestampId : timestampIdList) {

			writeTimestampResultInformation(document, timestampId, diagnosticData);
		}
	}

	//	private void writeLevelXL(final Document document, final DiagnosticData si) throws DocumentException {
	//		if (si == null || si.getSignatureLevelAnalysis() == null || si.getSignatureLevelAnalysis().getLevelXL() == null) {
	//			// document.add(p("Signature Level XL", false, ParagraphStyle.HEADER3));
	//			return;
	//		}
	//
	//		final SignatureLevelXL level = si.getSignatureLevelAnalysis().getLevelXL();
	//
	//		document.add(p("Signature Level XL", new R() {
	//			Result o() {
	//				return level.getLevelReached();
	//			}
	//		}, ParagraphStyle.HEADER3));
	//
	//		if (level.getCertificateValuesVerification() != null && level.getCertificateValuesVerification().isValid()) {
	//			document.add(p("All the certificates needed are in the signature."));
	//		} else {
	//			document.add(p("Some required certificates are not in the signature."));
	//		}
	//
	//		if (level.getRevocationValuesVerification() != null && level.getRevocationValuesVerification().isValid()) {
	//			document.add(p("All the revocation information needed are in the signature."));
	//		} else {
	//			document.add(p("Some required revocation information are not in the signature."));
	//		}
	//	}

	private void writeLevelA(final Document document, String signatureId, final DiagnosticData diagnosticData) throws DocumentException {

		final boolean levelPresent = diagnosticData.isThereALevel(signatureId);
		if (!levelPresent) {
			document.add(p("Signature Level A", false, ParagraphStyle.HEADER3));
			return;
		}
		final boolean aLevelTechnicallyValid = diagnosticData.isALevelTechnicallyValid(signatureId);
		document.add(p("Signature Level A", aLevelTechnicallyValid, ParagraphStyle.HEADER3));
		final List<String> timestampIdList = diagnosticData.getTimestampIdList(signatureId);
		document.add(p("Number of timestamps found: " + timestampIdList.size()));
		for (final String timestampId : timestampIdList) {

			writeTimestampResultInformation(document, timestampId, diagnosticData);
		}
	}

	
	private void writeTimestampResultInformation(Document document, final String timestampId, DiagnosticData diagnosticData) throws DocumentException {

		document.add(p("Timestamp id: " + timestampId, ParagraphStyle.HEADER5));
		
		document.add(p("Timestamp type: " + diagnosticData.getTimestampType(timestampId)));
		
//		document.add(p("Signature algorithm: " + diagnosticData.getTimestampDigestAlgorithm(timestampId)));
//		document.add(p("Signature verification: " + diagnosticData.isTimestampMessageImprintIntact(timestampId), ParagraphStyle.DEFAULT));
//		document.add(p("Creation time: " + sdf.format(diagnosticData.getTimestampProductionTime(timestampId))));
	}
	

	private void writeCertificate(Document document, final X509Certificate cert) throws DocumentException {
		document.add(p("Certificate of " + new T() {
			Object o() {
				return cert.getSubjectX500Principal();
			}
		}, ParagraphStyle.HEADER5));
		document.add(p("Version: " + new T() {
			Object o() {
				return cert.getVersion();
			}
		}));
		document.add(p("Subject: " + new T() {
			Object o() {
				return cert.getSubjectX500Principal();
			}
		}));
		document.add(p("Issuer: " + new T() {
			Object o() {
				return cert.getIssuerX500Principal();
			}
		}));

		try {
			StringWriter writer = new StringWriter();
			JcaPEMWriter out = new JcaPEMWriter(writer);
			out.writeObject(cert);
			out.close();

			document.add(p(writer.toString(), ParagraphStyle.CODE));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private void writeCertificateVerification(Document document, final String certificateId, DiagnosticData diagnosticData) throws DocumentException {
		document.add(p(diagnosticData.getCertificateDN(certificateId), ParagraphStyle.HEADER5));
		document.add(p("Issuer name: " + diagnosticData.getCertificateIssuerDN(certificateId)));
		document.add(p("Serial Number: " + diagnosticData.getCertificateSerialNumber(certificateId)));
		document.add(p("Validity at validation time: " + diagnosticData.isValidCertificate(certificateId)));
		document.add(p("Certificate Revocation status: " + diagnosticData.getCertificateRevocationStatus(certificateId)));
	}

	/**
	 * this provides some convenience to avoid NPE or usage of if-else/conditionals in order to get a text
	 */
	private static abstract class T {
		abstract Object o();

		@Override
		public String toString() {
			try {
				return o().toString();
			} catch (Exception e) {
				return "N/A";
			}
		}
	}

	/**
	 * this provides some convenience to avoid NPE or usage of if-else/conditionals in order to get a text
	 */
	private static abstract class B {
		abstract boolean o();

		public boolean toBoolean() {
			try {
				return o();
			} catch (Exception e) {
				return false;
			}
		}
	}

	private Paragraph p(String s) {
		return p(s, ParagraphStyle.DEFAULT);
	}

	private Paragraph p(String s, ParagraphStyle style) {
		return p(null, s, style);
	}

	private Paragraph p(String s, B r, ParagraphStyle style) {
		return p(s, r.toBoolean(), style);
	}

	private Paragraph p(String s, boolean r, ParagraphStyle style) {
		return p(r ? Resources.okImage : Resources.koImage, s, style);
	}

	private Paragraph p(Image img, String s, ParagraphStyle style) {

		if (style == null) {
			style = ParagraphStyle.DEFAULT;
		}

		Paragraph p = new Paragraph("", Resources.defaultFont);

		Font font = Resources.defaultFont;
		if (style == ParagraphStyle.HEADER1) {
			font = Resources.header1Font;
			p.setSpacingBefore(20);
		} else if (style == ParagraphStyle.HEADER2) {
			font = Resources.header2Font;
			p.setSpacingBefore(8);
		} else if (style == ParagraphStyle.HEADER3) {
			font = Resources.header3Font;
			p.setSpacingBefore(8);
		} else if (style == ParagraphStyle.HEADER4) {
			font = Resources.header4Font;
			p.setSpacingBefore(8);
		} else if (style == ParagraphStyle.HEADER5) {
			font = Resources.header5Font;
			p.setSpacingBefore(8);
		} else if (style == ParagraphStyle.CODE) {
			font = Resources.monoFont;
			p.setSpacingBefore(8);
		}

		if (img != null) {
			p.add(new Chunk(img, 0, -1));
			p.add(new Chunk(" ", font));
		}

		p.add(new Chunk(s, font));

		return p;

	}

	/**
	 * @param xPath
	 * @param params
	 * @return
	 */
	private static String format(final String xPath, final Object... params) {

		String formattedXPath = null;
		if (params.length > 0) {

			formattedXPath = String.format(xPath, params);
		} else {

			formattedXPath = xPath;
		}
		formattedXPath = addNamespacePrefix(formattedXPath);
		return formattedXPath;
	}

	private static String addNamespacePrefix(final String formatedXPath) {

		if (formatedXPath.startsWith("/dss:") || formatedXPath.startsWith("./dss:")) {

			// Already formated.
			return formatedXPath;
		}
		String formatedXPath_ = formatedXPath;
		CharSequence from = "//";
		CharSequence to = "{#double}/";
		boolean special = formatedXPath_.indexOf("//") != -1;
		if (special) {
			formatedXPath_ = formatedXPath_.replace(from, to);
		}
		StringTokenizer tokenizer = new StringTokenizer(formatedXPath_, "/");

		StringBuilder stringBuilder = new StringBuilder();

		while (tokenizer.hasMoreTokens())

		{

			String token = tokenizer.nextToken();

			final boolean isDot = ".".equals(token);
			final boolean isCount = "count(".equals(token) || "count(.".equals(token);
			final boolean isDoubleDot = "..".equals(token);
			final boolean isAt = token.startsWith("@");
			final boolean isText = token.equals("text()");
			final boolean isDoubleSlash = token.equals("{#double}");
			final String slash = isDot || isCount || isDoubleSlash ? "" : "/";
			String prefix = isDot || isCount || isDoubleDot || isAt || isText || isDoubleSlash ? "" : "dss:";

			stringBuilder.append(slash).append(prefix).append(token);
		}

		// System.out.println("");
		// System.out.println("--> " + formatedXPath);
		// System.out.println("--> " + stringBuilder.toString());
		String normalizedXPath = stringBuilder.toString();
		if (special) {
			normalizedXPath = normalizedXPath.replace(to, from);
		}
		return normalizedXPath;
	}

	/**
	 * @param xpathString XPath query string
	 * @return
	 */
	private static XPathExpression createXPathExpression(final String xpathString) {

		final XPath xpath = factory.newXPath();
		xpath.setNamespaceContext(nsContext);

		try {
			final XPathExpression expr = xpath.compile(xpathString);
			return expr;
		} catch (XPathExpressionException ex) {
			throw new DSSException(ex);
		}
	}

	/**
	 * Returns the NodeList corresponding to the XPath query.
	 *
	 * @param xmlNode     The node where the search should be performed.
	 * @param xPathString XPath query string
	 * @return the evaluation
	 * @throws DSSException in case of an XPathExpressionException
	 */
	public static NodeList getNodeList(final Node xmlNode, final String xPathString) throws DSSException {

		try {

			final XPathExpression expr = createXPathExpression(xPathString);
			final NodeList evaluated = (NodeList) expr.evaluate(xmlNode, XPathConstants.NODESET);
			return evaluated;
		} catch (XPathExpressionException e) {

			throw new DSSException(e);
		}
	}
}
