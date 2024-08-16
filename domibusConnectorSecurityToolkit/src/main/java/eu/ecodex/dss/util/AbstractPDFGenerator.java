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
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/util/AbstractPDFGenerator.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.util;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import eu.ecodex.dss.model.token.Token;
import eu.europa.esig.dss.model.DSSDocument;
import java.awt.Color;
import lombok.NoArgsConstructor;

/**
 * An abstract class to provide some basic resources and functionality.
 *
 * <p>DISCLAIMER: Project owner e-CODEX
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
@NoArgsConstructor
public abstract class AbstractPDFGenerator {
    protected static final Font FONT_H1;
    protected static final Font FONT_H2;
    protected static final Font FONT_H3;
    protected static final Font FONT_H4;
    protected static final Font FONT_TEXT;
    protected static final Font FONT_FOOTER;
    protected static final Image IMG_LOGO_ECODEX;
    protected static final Image IMG_LOGO_CIP;
    protected static final Image IMG_TECHNICAL_FAIL;
    protected static final Image IMG_TECHNICAL_SUFFICIENT;
    protected static final Image IMG_TECHNICAL_SUCCESSFUL;
    protected static final Image IMG_LEGAL_NOT_SUCCESSFUL;
    protected static final Image IMG_LEGAL_SUCCESSFUL;
    protected static final Color TABLE_BACKGROUND;

    static {
        try {
            FONT_H1 = PDFUtil.createFont(PDFUtil.Font.LIBERATION_BOLD, 24);
            FONT_H2 = PDFUtil.createFont(PDFUtil.Font.LIBERATION_BOLD_ITALIC, 12);
            FONT_H3 = PDFUtil.createFont(PDFUtil.Font.LIBERATION_REGULAR, 20);
            FONT_H4 = PDFUtil.createFont(PDFUtil.Font.LIBERATION_BOLD_ITALIC, 10);
            FONT_TEXT = PDFUtil.createFont(PDFUtil.Font.LIBERATION_REGULAR, 9);
            FONT_FOOTER = PDFUtil.createFont(PDFUtil.Font.LIBERATION_REGULAR, 11);

            IMG_LOGO_ECODEX = PDFUtil.createImage(PDFUtil.Image.LOGO_ECODEX);
            IMG_LOGO_CIP = PDFUtil.createImage(PDFUtil.Image.LOGO_CIP);

            IMG_TECHNICAL_FAIL = PDFUtil.createImage(PDFUtil.Image.TECHNICAL_FAIL);
            IMG_TECHNICAL_SUFFICIENT = PDFUtil.createImage(PDFUtil.Image.TECHNICAL_SUFFICIENT);
            IMG_TECHNICAL_SUCCESSFUL = PDFUtil.createImage(PDFUtil.Image.TECHNICAL_SUCCESSFUL);

            IMG_LEGAL_NOT_SUCCESSFUL = PDFUtil.createImage(PDFUtil.Image.LEGAL_NOTSUCCESSFUL);
            IMG_LEGAL_SUCCESSFUL = PDFUtil.createImage(PDFUtil.Image.LEGAL_SUCCESSFUL);

            TABLE_BACKGROUND = new Color(157, 206, 237);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generate the PDF page(s).
     *
     * @param token The token
     * @return The document
     * @throws com.lowagie.text.DocumentException The exception.
     */
    public abstract DSSDocument generate(final Token token) throws DocumentException;

    protected void addPageHeader(final com.lowagie.text.Document document, final Token token)
        throws DocumentException {
        final var table = new PdfPTable(3);
        table.setSpacingAfter(10);
        table.setWidths(new int[] {40, 40, 20});

        var cell = new PdfPCell();
        cell.addElement(IMG_LOGO_ECODEX);
        cell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell();
        cell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell();
        cell.addElement(IMG_LOGO_CIP);
        cell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);

        document.add(table);
    }
}
