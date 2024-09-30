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
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTemplate;
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
    public static final Font FONT_H1;
    public static final Font FONT_H2;
    public static final Font FONT_H3;
    public static final Font FONT_H4;
    public static final Font FONT_TEXT;
    public static final Font FONT_FOOTER;
    public static final Image IMG_LOGO_ECODEX;
    public static final Image IMG_TECHNICAL_FAIL;
    public static final Image IMG_TECHNICAL_SUFFICIENT;
    public static final Image IMG_TECHNICAL_SUCCESSFUL;
    public static final Image IMG_LEGAL_NOT_SUCCESSFUL;
    public static final Image IMG_LEGAL_SUCCESSFUL;
    public static final Color TABLE_BACKGROUND;

    static {
        try {
            FONT_H1 = new Font(Font.HELVETICA, 24, Font.BOLD);
            FONT_H2 = new Font(Font.HELVETICA, 12, Font.BOLDITALIC);
            FONT_H3 = new Font(Font.HELVETICA, 20);
            FONT_H4 = new Font(Font.HELVETICA, 10, Font.BOLDITALIC);
            FONT_TEXT = new Font(Font.HELVETICA, 9);
            FONT_FOOTER = new Font(Font.HELVETICA, 11);

            IMG_LOGO_ECODEX = PDFUtil.createImage(PDFUtil.Image.HEADER_IMAGE_LOGO);

            var fail = PDFUtil.createImage(PDFUtil.Image.FAIL);
            var successful = PDFUtil.createImage(PDFUtil.Image.SUCCESSFUL);
            var sufficient = PDFUtil.createImage(PDFUtil.Image.SUFFICIENT);

            var reused = Image.getInstance(fail);
            IMG_TECHNICAL_FAIL = fail;
            IMG_TECHNICAL_SUFFICIENT = sufficient;
            IMG_TECHNICAL_SUCCESSFUL = successful;

            IMG_LEGAL_NOT_SUCCESSFUL = fail;
            IMG_LEGAL_SUCCESSFUL = successful;

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
     * @throws DocumentException The exception.
     */
    public abstract DSSDocument generate(final Token token) throws DocumentException;

    public void addPageHeader(final com.lowagie.text.Document document, final Token token)
        throws DocumentException {
        final var table = new PdfPTable(1);
        table.setSpacingAfter(50);
//        table.setWidths(new int[] {30, 70});

        var cell = new PdfPCell();
        cell.addElement(IMG_LOGO_ECODEX);
        cell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        document.add(table);
    }
}
