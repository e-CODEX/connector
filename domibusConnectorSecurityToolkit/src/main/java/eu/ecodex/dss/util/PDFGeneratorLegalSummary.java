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
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/util/PDFGeneratorLegalSummary.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.util;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import eu.ecodex.dss.model.token.LegalTrustLevel;
import eu.ecodex.dss.model.token.Token;
import eu.europa.esig.dss.enumerations.MimeTypeEnum;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import org.apache.commons.io.IOUtils;

/**
 * This creates the legal summary page of the trustoktoken.
 *
 * <p>DISCLAIMER: Project owner e-CODEX
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
public class PDFGeneratorLegalSummary extends AbstractPDFGenerator {
    /**
     * The default constructor for PDFReportGenerator.
     */
    public PDFGeneratorLegalSummary() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DSSDocument generate(final Token token) throws DocumentException {

        // Result

        final var output = new ByteArrayOutputStream();

        try (final var document = new com.lowagie.text.Document()) {
            final var writer = PdfWriter.getInstance(document, output);
            writer.setPdfVersion(PdfWriter.PDF_VERSION_1_4);

            document.open();

            addPageHeader(document, token);
            addTitle(document, token);
            addGeneral(document, token);
            addResult(document, token);
            addStamp(document, token);
        } finally {
            IOUtils.closeQuietly(output);
        }

        return new InMemoryDocument(
            output.toByteArray(), "token-summary-legal.pdf", MimeTypeEnum.PDF
        );
    }

    private void addTitle(final com.lowagie.text.Document document, final Token token)
        throws DocumentException {
        var paragraph = new Paragraph("e-CODEX", FONT_H1);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setSpacingAfter(30);
        document.add(paragraph);

        paragraph = new Paragraph("e-Justice Communication via Online Data Exchange", FONT_H2);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setSpacingAfter(80);
        document.add(paragraph);

        paragraph = new Paragraph("Trust OK-Token", FONT_H3);

        var titleCell = new PdfPCell();
        titleCell.setPadding(5);
        titleCell.setPaddingBottom(10);
        titleCell.setPhrase(paragraph);
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        titleCell.setBorderColor(Color.YELLOW);
        titleCell.setBorderWidth(2);
        titleCell.setBackgroundColor(TABLE_BACKGROUND);

        final var table = new PdfPTable(1);
        table.addCell(titleCell);
        table.setSpacingAfter(30);

        document.add(table);
    }

    private void addGeneral(final com.lowagie.text.Document document, final Token token)
        throws DocumentException {

        // get data from structure
        final var country = PDFUtil.format(token.getIssuerCountry());
        final String advancedElectronicSystem =
            PDFUtil.format(token.getAdvancedElectronicSystemText());

        final var verificationTime = PDFUtil.format(token.getValidationVerificationTime());

        final var documentType = PDFUtil.format(token.getDocumentType());
        final var documentName = PDFUtil.format(token.getDocumentName());

        // create the pdf
        final var table = new PdfPTable(2);
        table.setSpacingAfter(20);

        final var captionCell = new PdfPCell();
        captionCell.setPhrase(new Paragraph("General Information", FONT_TEXT));
        captionCell.setColspan(2);
        captionCell.setBorder(Rectangle.NO_BORDER);

        final var leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        final var rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        table.addCell(captionCell);

        leftCell.setPhrase(new Paragraph("Issuing Country", FONT_TEXT));
        rightCell.setPhrase(new Paragraph(country, FONT_TEXT));
        table.addCell(leftCell);
        table.addCell(rightCell);

        leftCell.setPhrase(new Paragraph("Advanced Electronic System", FONT_TEXT));
        rightCell.setPhrase(new Paragraph(advancedElectronicSystem, FONT_TEXT));
        table.addCell(leftCell);
        table.addCell(rightCell);

        leftCell.setPhrase(new Paragraph("Document Information", FONT_TEXT));
        rightCell.setPhrase(new Paragraph(documentType + ", \"" + documentName + "\"", FONT_TEXT));
        table.addCell(leftCell);
        table.addCell(rightCell);

        leftCell.setPhrase(new Paragraph("Time of Issuance", FONT_TEXT));
        rightCell.setPhrase(new Paragraph(verificationTime, FONT_TEXT));
        table.addCell(leftCell);
        table.addCell(rightCell);

        document.add(table);
    }

    private void addResult(final com.lowagie.text.Document document, final Token token)
        throws DocumentException {

        // get data from structure
        final LegalTrustLevel trustLevel = token.getLegalValidationResultTrustLevel();
        final String validationResult =
            PDFUtil.format(trustLevel == null ? null : trustLevel.getText());

        // create the pdf
        final var table = new PdfPTable(2);
        table.setSpacingAfter(20);
        final var captionCell = new PdfPCell();
        captionCell.setPhrase(new Paragraph("Legal Result", FONT_TEXT));
        captionCell.setColspan(2);
        captionCell.setBorder(Rectangle.NO_BORDER);

        table.addCell(captionCell);

        final var leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        final var rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        leftCell.setPhrase(new Paragraph("Evaluation of the Document", FONT_TEXT));
        rightCell.setPhrase(new Paragraph(validationResult, FONT_TEXT));
        table.addCell(leftCell);
        table.addCell(rightCell);

        document.add(table);
    }

    private void addStamp(final com.lowagie.text.Document document, final Token token)
        throws DocumentException {
        // get data from structure
        final LegalTrustLevel trustlevel = token.getLegalValidationResultTrustLevel();
        final var trustImage = switch (trustlevel) {
            case SUCCESSFUL -> IMG_LEGAL_SUCCESSFUL;
            default -> IMG_LEGAL_NOT_SUCCESSFUL;
        };
        final String disclaimer = PDFUtil.format(token.getLegalValidationResultDisclaimer());

        // create the pdf
        final var table = new PdfPTable(2);
        table.setWidths(new int[] {70, 30});
        table.setSpacingBefore(10);

        final var leftCell = new PdfPCell();
        leftCell.addElement(new Paragraph(disclaimer, FONT_FOOTER));
        leftCell.addElement(new Paragraph(" "));
        leftCell.addElement(new Paragraph(
            "Further details can be found in the attached validation report and its "
                + "technical assessment.",
            FONT_FOOTER
        ));
        leftCell.setBorderColor(Color.YELLOW);
        leftCell.setBorderWidth(2);
        leftCell.setBorderWidthRight(0);
        leftCell.setPadding(5);
        leftCell.setBackgroundColor(TABLE_BACKGROUND);
        table.addCell(leftCell);

        final var rightCell = new PdfPCell();
        rightCell.addElement(trustImage);
        rightCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        rightCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        rightCell.setBorderColor(Color.YELLOW);
        rightCell.setBorderWidth(2);
        rightCell.setBorderWidthLeft(0);
        rightCell.setPadding(5);
        rightCell.setBackgroundColor(TABLE_BACKGROUND);
        table.addCell(rightCell);

        document.add(table);
    }
}
