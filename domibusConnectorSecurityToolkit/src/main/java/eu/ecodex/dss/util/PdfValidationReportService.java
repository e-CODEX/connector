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
 * Project: Digital Signature Services (DSS)
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/container/tags/ecodex-container-DSS-library-2.5.0.3/apps/dss/dss-report/src/main/java/eu/europa/ec/markt/dss/report/PdfValidationReportService.java $
 * $Revision: 2149 $
 * $Date: 2013-05-29 20:59:24 +0200 (Wed, 29 May 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.util;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.simplereport.SimpleReport;
import eu.europa.esig.dss.xml.utils.NamespaceContextMap;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import javax.imageio.ImageIO;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This service create a PDF report from the validation report of the document.
 *
 * <p>DISCLAIMER: Project owner DG-MARKT.
 *
 * @author <a href="mailto:dgmarkt.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 2149 $ - $Date: 2013-05-29 20:59:24 +0200 (Wed, 29 May 2013) $
 */
@SuppressWarnings("squid:S1135")
@NoArgsConstructor
public class PdfValidationReportService {
    private static final XPathFactory factory = XPathFactory.newInstance();
    public static final String NAMESPACE = "http://dss.markt.ec.europa.eu/validation/diagnostic";
    private static final NamespaceContextMap nsContext;
    private static final Map<String, String> namespaces;

    static {
        namespaces = new HashMap<>();
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

                okImage = Image.getInstance(
                    ImageIO.read(PdfValidationReportService.class.getResourceAsStream("/ok.jpg")),
                    null
                );
                okImage.scaleToFit(9, 9);
                okImage.setSpacingAfter(25);
                okImage.setSmask(false);

                koImage = Image.getInstance(ImageIO.read(
                    PdfValidationReportService.class.getResourceAsStream("/error.jpg")), null);
                koImage.scaleToFit(9, 9);
                koImage.setSmask(false);
            } catch (Exception e) {
                throw new ExceptionInInitializerError(e);
            }
        }

        private static Font createFont(final String name, final int size)
            throws IOException, DocumentException {
            final byte[] data;
            final BaseFont bfo;
            data = IOUtils.toByteArray(
                PdfValidationReportService.class.getResourceAsStream("/" + name));
            bfo = BaseFont.createFont(name, BaseFont.WINANSI, BaseFont.EMBEDDED, BaseFont.CACHED,
                                      data, null
            );
            return new Font(bfo, size);
        }
    }

    private enum ParagraphStyle {
        HEADER1,
        HEADER2,
        HEADER3,
        HEADER4,
        HEADER5,
        DEFAULT,
        CODE
    }

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    /**
     * Creates a PDF report based on the provided diagnostic data and simple report. The report is
     * generated and written to the specified output stream.
     *
     * @param diagnosticData The diagnostic data used to generate the report.
     * @param simpleReport   The simple report containing the required information for the report.
     * @param pdfStream      The output stream where the generated PDF report will be written.
     * @throws IOException if an I/O error occurs while generating or writing the report.
     */
    public void createReport(
        DiagnosticData diagnosticData, SimpleReport simpleReport, OutputStream pdfStream)
        throws IOException {

        try {
            var document = new Document();
            var writer = PdfWriter.getInstance(document, pdfStream);
            writer.setPdfVersion(PdfWriter.PDF_VERSION_1_4);
            writer.setPDFXConformance(PdfWriter.PDFA1B);
            document.open();

            document.add(paragraph("Time information", ParagraphStyle.HEADER1));
            document.add(
                paragraph("Verification Time: " + sdf.format(simpleReport.getValidationTime()))
            );

            var i = 1;
            final var signatureIdList = simpleReport.getSignatureIdList();
            for (var signatureId : signatureIdList) {
                writeSignatureInformation(document, signatureId, diagnosticData, simpleReport, i++);
            }
            writer.createXmpMetadata();
            document.close();
        } catch (DocumentException e) {
            throw new IOException(e);
        }
    }

    private void writeSignatureInformation(
        Document document, final String signatureId, final DiagnosticData diagnosticData,
        final SimpleReport simpleReport,
        int index) throws DocumentException {

        document.add(
            paragraph("Signature information " + index, ParagraphStyle.HEADER1)
        );
        document.add(
            paragraph(
                "Signature verification", simpleReport.isValid(signatureId),
                ParagraphStyle.DEFAULT
            ));
        document.add(
            paragraph("Signature algorithm: " + diagnosticData.getSignatureEncryptionAlgorithm(
                signatureId))
        );

        document.add(paragraph("Certificate Path Revocation Analysis", ParagraphStyle.HEADER2));

        final List<String> signatureCertificateChain =
            diagnosticData.getSignatureCertificateChain(signatureId);

        var valid = false;
        for (String certificateId : signatureCertificateChain) {

            final boolean validCertificate =
                diagnosticData.getCertificateRevocationStatus(certificateId)
                              .isGood();
            if (!validCertificate) {

                valid = false;
                break;
            }
            valid = true;
        }
        document.add(paragraph("Summary", valid, null));

        document.add(paragraph("Certificate Verification", ParagraphStyle.HEADER3));
        if (signatureCertificateChain.isEmpty()) {
            document.add(paragraph("No Certificate Verification is available!"));
        } else {
            for (String certificateId : signatureCertificateChain) {
                writeCertificateVerification(document, certificateId, diagnosticData);
            }
        }

        final String signingCertificateId = diagnosticData.getSigningCertificateId(signatureId);
        document.add(paragraph("Signature Level Analysis", ParagraphStyle.HEADER2));
        final var signatureFormat = diagnosticData
            .getSignatureFormat(signatureId)
            .toString(); // TODO: use enumerations here instead of toString()
        if (signatureFormat == null || signatureFormat.isEmpty()) {
            document.add(paragraph("No Signature Level Analysis is available."));
        } else {
            document.add(paragraph("Signature format: " + signatureFormat));
            writeLevelBES(
                document, signatureId, signingCertificateId, diagnosticData,
                signatureCertificateChain
            );
            writeLevelEPES(document, signatureId, diagnosticData);
            writeLevelT(document, signatureId, diagnosticData);
            writeLevelX(document, signatureId, diagnosticData);
            writeLevelA(document, signatureId, diagnosticData);
        }

        document.add(paragraph("Final Conclusion", ParagraphStyle.HEADER2));
        document.add(
            paragraph("The signature is: " + simpleReport.getSignatureQualification(signatureId)
                                                         .name()));
    }

    private void writeLevelBES(
        final Document document, final String signatureId, String signingCertificateId,
        final DiagnosticData diagnosticData,
        List<String> certificateChain) throws DocumentException {

        final boolean signingCertificateIdentified =
            diagnosticData.isSigningCertificateIdentified(signatureId);

        if (!signingCertificateIdentified) {
            document.add(paragraph("Signature Level BES", false, ParagraphStyle.HEADER3));
            return;
        }

        final var signatureDate = diagnosticData.getSignatureDate(signatureId);
        document.add(
            paragraph("Signature Level BES", signatureDate != null, ParagraphStyle.HEADER3));

        document.add(paragraph("Signing certificate: " + diagnosticData.getCertificateIssuerDN(
            signingCertificateId)));

        document.add(paragraph("Signing time: " + sdf.format(signatureDate)));

        document.add(paragraph("Certificates", ParagraphStyle.HEADER4));
        document.add(paragraph("Number of certificates in the chain: " + certificateChain.size()));
    }

    private void writeLevelEPES(
        final Document document, String signatureId, final DiagnosticData diagnosticData)
        throws DocumentException {

        final String policyId = diagnosticData.getPolicyId(signatureId);
        if (policyId == null || policyId.isEmpty()) {
            document.add(paragraph("Signature Level EPES", false, ParagraphStyle.HEADER3));
            return;
        }

        document.add(paragraph("Signature Level EPES ", true, ParagraphStyle.HEADER3));
        document.add(paragraph("Signature policy: " + policyId));
    }

    private void writeLevelT(
        final Document document, String signatureId, final DiagnosticData diagnosticData)
        throws DocumentException {

        final boolean levelPresent = diagnosticData.isThereTLevel(signatureId);
        if (!levelPresent) {
            document.add(paragraph("Signature Level T", false, ParagraphStyle.HEADER3));
            return;
        }
        final boolean tLevelTechnicallyValid = diagnosticData.isTLevelTechnicallyValid(signatureId);
        document.add(
            paragraph("Signature Level T", tLevelTechnicallyValid, ParagraphStyle.HEADER3)
        );
        final List<String> timestampIdList = diagnosticData.getTimestampIdList(signatureId);
        document.add(paragraph("Number of timestamps found: " + timestampIdList.size()));
        for (final String timestampId : timestampIdList) {

            writeTimestampResultInformation(document, timestampId, diagnosticData);
        }
    }

    private void writeLevelX(
        final Document document, String signatureId, final DiagnosticData diagnosticData)
        throws DocumentException {

        final boolean levelPresent = diagnosticData.isThereXLevel(signatureId);
        if (!levelPresent) {
            document.add(paragraph("Signature Level X", false, ParagraphStyle.HEADER3));
            return;
        }
        final boolean xLevelTechnicallyValid = diagnosticData.isXLevelTechnicallyValid(signatureId);
        document.add(
            paragraph("Signature Level X", xLevelTechnicallyValid, ParagraphStyle.HEADER3));
        final List<String> timestampIdList = diagnosticData.getTimestampIdList(signatureId);
        document.add(paragraph("Number of timestamps found: " + timestampIdList.size()));
        for (final String timestampId : timestampIdList) {

            writeTimestampResultInformation(document, timestampId, diagnosticData);
        }
    }

    private void writeLevelA(
        final Document document, String signatureId, final DiagnosticData diagnosticData)
        throws DocumentException {

        final boolean levelPresent = diagnosticData.isThereALevel(signatureId);
        if (!levelPresent) {
            document.add(paragraph("Signature Level A", false, ParagraphStyle.HEADER3));
            return;
        }
        final boolean aLevelTechnicallyValid = diagnosticData.isALevelTechnicallyValid(signatureId);
        document.add(
            paragraph("Signature Level A", aLevelTechnicallyValid, ParagraphStyle.HEADER3)
        );
        final List<String> timestampIdList = diagnosticData.getTimestampIdList(signatureId);
        document.add(paragraph("Number of timestamps found: " + timestampIdList.size()));
        for (final String timestampId : timestampIdList) {
            writeTimestampResultInformation(document, timestampId, diagnosticData);
        }
    }

    private void writeTimestampResultInformation(
        Document document, final String timestampId, DiagnosticData diagnosticData)
        throws DocumentException {
        document.add(paragraph("Timestamp id: " + timestampId, ParagraphStyle.HEADER5));
        document.add(paragraph("Timestamp type: " + diagnosticData.getTimestampType(timestampId)));
    }

    private void writeCertificate(Document document, final X509Certificate cert)
        throws DocumentException {
        document.add(paragraph("Certificate of " + new T() {
            Object object() {
                return cert.getSubjectX500Principal();
            }
        }, ParagraphStyle.HEADER5));
        document.add(paragraph("Version: " + new T() {
            Object object() {
                return cert.getVersion();
            }
        }));
        document.add(paragraph("Subject: " + new T() {
            Object object() {
                return cert.getSubjectX500Principal();
            }
        }));
        document.add(paragraph("Issuer: " + new T() {
            Object object() {
                return cert.getIssuerX500Principal();
            }
        }));

        try {
            var writer = new StringWriter();
            var out = new JcaPEMWriter(writer);
            out.writeObject(cert);
            out.close();

            document.add(paragraph(writer.toString(), ParagraphStyle.CODE));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void writeCertificateVerification(
        Document document, final String certificateId, DiagnosticData diagnosticData)
        throws DocumentException {
        document.add(
            paragraph(diagnosticData.getCertificateDN(certificateId), ParagraphStyle.HEADER5));
        document.add(
            paragraph("Issuer name: " + diagnosticData.getCertificateIssuerDN(certificateId))
        );
        document.add(
            paragraph(
                "Serial Number: " + diagnosticData.getCertificateSerialNumber(certificateId)));
        document.add(
            paragraph("Validity at validation time: " + diagnosticData.isValidCertificate(
                certificateId)));
        document.add(
            paragraph(
                "Certificate Revocation status: " + diagnosticData.getCertificateRevocationStatus(
                    certificateId)));
    }

    /**
     * This provides some convenience to avoid NPE or usage of if-else/conditionals in order to get
     * a text.
     */
    private abstract static class T {
        abstract Object object();

        @Override
        public String toString() {
            try {
                return object().toString();
            } catch (Exception e) {
                return "N/A";
            }
        }
    }

    /**
     * This provides some convenience to avoid NPE or usage of if-else/conditionals in order to get
     * a text.
     */
    private abstract static class B {
        abstract boolean object();

        public boolean toBoolean() {
            try {
                return object();
            } catch (Exception e) {
                return false;
            }
        }
    }

    private Paragraph paragraph(String s) {
        return paragraph(s, ParagraphStyle.DEFAULT);
    }

    private Paragraph paragraph(String s, ParagraphStyle style) {
        return paragraph(null, s, style);
    }

    private Paragraph paragraph(String s, B r, ParagraphStyle style) {
        return paragraph(s, r.toBoolean(), style);
    }

    private Paragraph paragraph(String s, boolean r, ParagraphStyle style) {
        return paragraph(r ? Resources.okImage : Resources.koImage, s, style);
    }

    private Paragraph paragraph(Image img, String s, ParagraphStyle style) {
        if (style == null) {
            style = ParagraphStyle.DEFAULT;
        }

        var paragraph = new Paragraph("", Resources.defaultFont);

        var font = Resources.defaultFont;
        if (style == ParagraphStyle.HEADER1) {
            font = Resources.header1Font;
            paragraph.setSpacingBefore(20);
        } else if (style == ParagraphStyle.HEADER2) {
            font = Resources.header2Font;
            paragraph.setSpacingBefore(8);
        } else if (style == ParagraphStyle.HEADER3) {
            font = Resources.header3Font;
            paragraph.setSpacingBefore(8);
        } else if (style == ParagraphStyle.HEADER4) {
            font = Resources.header4Font;
            paragraph.setSpacingBefore(8);
        } else if (style == ParagraphStyle.HEADER5) {
            font = Resources.header5Font;
            paragraph.setSpacingBefore(8);
        } else if (style == ParagraphStyle.CODE) {
            font = Resources.monoFont;
            paragraph.setSpacingBefore(8);
        }

        if (img != null) {
            paragraph.add(new Chunk(img, 0, -1));
            paragraph.add(new Chunk(" ", font));
        }

        paragraph.add(new Chunk(s, font));

        return paragraph;
    }

    /**
     * Formats the provided XPath expression with the given parameters.
     *
     * @param path   The XPath expression to be formatted.
     * @param params The parameters to be injected into the XPath expression.
     * @return The formatted XPath expression.
     */
    private static String format(final String path, final Object... params) {
        String formattedXPath;
        if (params.length > 0) {

            formattedXPath = String.format(path, params);
        } else {

            formattedXPath = path;
        }
        formattedXPath = addNamespacePrefix(formattedXPath);
        return formattedXPath;
    }

    private static String addNamespacePrefix(final String formatedXPath) {
        if (formatedXPath.startsWith("/dss:") || formatedXPath.startsWith("./dss:")) {
            // Already formated.
            return formatedXPath;
        }
        var copiedFormatedXPath = formatedXPath;
        CharSequence from = "//";
        CharSequence to = "{#double}/";
        var special = copiedFormatedXPath.contains("//");
        if (special) {
            copiedFormatedXPath = copiedFormatedXPath.replace(from, to);
        }
        var tokenizer = new StringTokenizer(copiedFormatedXPath, "/");

        var stringBuilder = new StringBuilder();

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            final boolean isDot = ".".equals(token);
            final boolean isCount = "count(".equals(token) || "count(.".equals(token);
            final boolean isDoubleDot = "..".equals(token);
            final boolean isAt = token.startsWith("@");
            final boolean isText = token.equals("text()");
            final boolean isDoubleSlash = token.equals("{#double}");
            final String slash = isDot || isCount || isDoubleSlash ? "" : "/";
            String prefix =
                isDot || isCount || isDoubleDot || isAt || isText || isDoubleSlash ? "" : "dss:";
            stringBuilder.append(slash).append(prefix).append(token);
        }

        var normalizedXPath = stringBuilder.toString();
        if (special) {
            normalizedXPath = normalizedXPath.replace(to, from);
        }
        return normalizedXPath;
    }

    /**
     * Creates a new XPathExpression object from the given XPath string.
     *
     * @param xpathString The XPath string to compile into an XPathExpression.
     * @return A new XPathExpression object representing the compiled XPath string.
     * @throws DSSException if an error occurs while compiling the XPath string.
     */
    private static XPathExpression createXPathExpression(final String xpathString) {
        final var xpath = factory.newXPath();
        xpath.setNamespaceContext(nsContext);

        try {
            return xpath.compile(xpathString);
        } catch (XPathExpressionException ex) {
            throw new DSSException(ex);
        }
    }

    /**
     * Returns the NodeList corresponding to the XPath query.
     *
     * @param xmlNode    The node where the search should be performed.
     * @param pathString XPath query string
     * @return the evaluation
     * @throws DSSException in case of an XPathExpressionException
     */
    public static NodeList getNodeList(final Node xmlNode, final String pathString)
        throws DSSException {
        try {
            final var expr = createXPathExpression(pathString);
            return (NodeList) expr.evaluate(xmlNode, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new DSSException(e);
        }
    }
}
