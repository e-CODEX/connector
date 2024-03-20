/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/util/PDFUtil.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */
package eu.ecodex.dss.util;

import com.lowagie.text.BadElementException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.*;
import eu.europa.esig.dss.enumerations.MimeTypeEnum;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;

import eu.europa.esig.dss.spi.DSSUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Provides convenience-methods for PDF documents.
 * <p>
 * DISCLAIMER: Project owner e-CODEX
 * </p>
 * 
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
public class PDFUtil {
    
    public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm z";
    public static final String REF_FONTS = "/eu/ecodex/dss/fonts/";
    public static final String REF_IMAGES = "/eu/ecodex/dss/images/";

    private static BaseFont FOOTER_FONT;

    /**
     * Fonts
     */
    public enum Font {

        LIBERATION_REGULAR("LiberationSans-Regular.ttf"), 
        LIBERATION_BOLD_ITALIC("LiberationSans-BoldItalic.ttf"), 
        LIBERATION_BOLD("LiberationSans-Bold.ttf"), 
        LIBERATION_ITALIC("LiberationSans-Italic.ttf");

        private String name;

        Font(String name) {
            this.name = name;
        }
    }

    /**
     * Images and Logos
     */
    public enum Image {
        LOGO_ECODEX("pdf_logo_ecodex.jpg"),
        LOGO_CIP("pdf_logo_cip.png"),
        
        TECHNICAL_FAIL("pdf_icon_technical_fail.png"),
        TECHNICAL_SUFFICIENT("pdf_icon_technical_sufficient.png"),
        TECHNICAL_SUCCESSFUL("pdf_icon_technical_successful.png"),
        
        LEGAL_NOTSUCCESSFUL("pdf_icon_legal_notsuccessful.png"),
        // LEGAL_UNDETERMINED("pdf_icon_legal_unknown.png"),
        LEGAL_SUCCESSFUL("pdf_icon_legal_successful.png");
        
        private String name;

        Image(String name) {
            this.name = name;
        }
        
    }
    
    /**
     * The default constructor for PDFUtil.
     */
    private PDFUtil() {
    }

    /**
     * Creates a {@link com.lowagie.text.Font} using the font name. 
     * 
     * <p>
     * The file must be present in the classpath under /eu/ecodex/dss/fonts/.
     * </p>
     * 
     * @param font The font name
     * @param size The font size
     * @return a font
     * @throws java.io.IOException The {@link java.io.IOException}
     * @throws DocumentException The {@link DocumentException}
     */
    public static com.lowagie.text.Font createFont(final Font font, final int size) throws IOException, DocumentException {
        return createFont(font.name, size);
    }

    /**
     * Creates a {@link com.lowagie.text.Font} using the font name. 
     * <p>
     * The file must be present in the classpath under /eu/ecodex/dss/fonts/.
     * </p>
     * 
     * @param name The ttf font name
     * @param size The font size
     * @return a font
     * @throws java.io.IOException The {@link java.io.IOException}
     * @throws DocumentException The {@link DocumentException}
     */
    public static com.lowagie.text.Font createFont(final String name, final int size) throws IOException, DocumentException {
        final InputStream input = PDFUtil.class.getResourceAsStream(REF_FONTS + name);
        if (input == null) {
            throw new IllegalArgumentException("The resource '" + name + "' could not be resolved in the directory '" + REF_FONTS + "'.");
        }
        final byte[] data = DSSUtils.toByteArray(input);
        final BaseFont baseFont = BaseFont.createFont(name, BaseFont.WINANSI, BaseFont.EMBEDDED, BaseFont.CACHED, data, null);
        return new com.lowagie.text.Font(baseFont, size);
    }

    /**
     * Creates a {@link com.lowagie.text.Image} using the name. 
     * <p>
     * The file must be present in the classpath under /eu/ecodex/dss/images/.
     * </p>
     * 
     * @param image The image name
     * @return the image
     * @throws BadElementException as of the underlying classes
     * @throws IOException as of the underlying classes
     */
    public static com.lowagie.text.Image createImage(final Image image) throws BadElementException, IOException {
        return createImage(image.name);
    }
    
    /**
     * Creates a {@link com.lowagie.text.Image} using the name. 
     * <p>
     * The file must be present in the classpath under /eu/ecodex/dss/images/.
     * </p>
     * 
     * @param name The image name
     * @return the image
     * @throws BadElementException as of the underlying classes
     * @throws IOException as of the underlying classes
     */
    public static com.lowagie.text.Image createImage(final String name) throws BadElementException, IOException {
        final InputStream input = PDFUtil.class.getResourceAsStream(REF_IMAGES + name);
        if (input == null) {
            throw new IllegalArgumentException("The resource '" + name + "' could not be resolved in the directory '" + REF_IMAGES + "'.");
        }
        final byte[] data = DSSUtils.toByteArray(input);
        return com.lowagie.text.Image.getInstance(data);
    }

    /**
     * formats a date to string
     * @param cal nullable
     * @return the text (can be empty)
     */
    public static String format(final XMLGregorianCalendar cal) {
        if (cal == null) {
            return StringUtils.EMPTY;
        }
        final Date date = cal.toGregorianCalendar().getTime();
        final SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN);
        return format.format(date);
    }
    
    /**
     * ensures that a string is not exposed as "null"
     * @param text nullable
     * @return the text (can be empty)
     */
    public static String format(final String text) {
        if (text == null) {
            // could also be changed to "N/A"
            return StringUtils.EMPTY;
        } else {
            return text;
        }
    }

    /**
     * Give the number of page of a PDF document.
     * 
     * @param document a PDF document
     * @return the count result
     */
    public static int getPageCount(final DSSDocument document) {
        if (document == null || !DocumentStreamUtil.hasData(document)) {
            return 0;
        }

        PdfReader pdfReader = null;
        try {
            pdfReader = new PdfReader(document.openStream());
            return pdfReader.getNumberOfPages();
        } catch (IOException e) {
            return 0;
        } finally {
            if (pdfReader != null) {
                pdfReader.close();
            }
        }

    }

    /**
     * Validate if the document is a PDF
     *
     * @param document The {@link DSSDocument}
     * @return The result
     */
    public static boolean isPDFFile(final DSSDocument document) {
        return hasPDFContent(document, false);
    }

    /**
     * Validate if the document is a PDF and has at least one page
     *
     * @param document The {@link DSSDocument}
     * @return The result
     */
    public static boolean isPDFContent(final DSSDocument document) {
        return hasPDFContent(document, true);
    }

    private static boolean hasPDFContent(final DSSDocument document, boolean checkPageCount) {
        if (document == null || !DocumentStreamUtil.hasData(document)) {
            return false;
        }

        PdfReader reader = null;
        try {
            reader = new PdfReader(document.openStream());
            //noinspection SimplifiableIfStatement
            if (checkPageCount) {
                return reader.getNumberOfPages() > 0;
            } else {
                return true;
            }
        } catch (Exception e) {
            return false;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private static void closeQuietly(final Collection<PdfReader> readers) {
        for ( final PdfReader reader : readers ) {
            try {
                reader.close();
            } catch (Exception e) {
                // ignored
            }
        }
    }

    /**
     * Concatenate all pages inside only one PDF file. 
     * <p>
     * If the document is empty , it's ignored. 
     * If all documents are empty, it's not possible to create the document. 
     * All the documents should be a PDF file.
     * 
     * @param filename The merged file name
     * @param documents PDF files.
     * @return a document that contains the result of merging.
     * @throws java.io.IOException The {@link java.io.IOException} if all document are empty or one of them is not a PDF.
     * @throws com.lowagie.text.DocumentException as of the underlying classes
     */
    public static DSSDocument concatenate(final String filename, final DSSDocument... documents) throws IOException, DocumentException {

        // lazy create the footer font
        if (FOOTER_FONT == null) {
            FOOTER_FONT = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        }

        // used further below
        final Collection<PdfReader> sourceReaders = new ArrayList<PdfReader>();
        int targetTotalCount = 0;

        //
        for (final DSSDocument doc : documents) {
            if (doc == null || !DocumentStreamUtil.hasData(doc)) {
                // The document has no data, it's ignored
                continue;
            }
            final PdfReader sourceReader;
            try {
                sourceReader = new PdfReader(doc.openStream());
                sourceReaders.add(sourceReader);
                targetTotalCount += sourceReader.getNumberOfPages();
            } catch (Exception e) {
                closeQuietly(sourceReaders);
                throw new IOException("The document '" + doc.getName() + "' with mime-type '" + doc.getMimeType() + "' is not a PDF file");
            }
        }

        // check if there is something to concatenate
        if (targetTotalCount == 0) {
            closeQuietly(sourceReaders);
            throw new IOException("There are no pages to concatenate.");
        }

        // create the target holding all the content
        final com.lowagie.text.Document targetDoc = new com.lowagie.text.Document();
        final ByteArrayOutputStream targetStream = new ByteArrayOutputStream();

        try {

            // Create a writer for the outputstream
            final PdfWriter totalWriter = PdfWriter.getInstance(targetDoc, targetStream);
            // open to write
            targetDoc.open();
            // Holds the PDF data
            final PdfContentByte targetContent = totalWriter.getDirectContent();

            int targetCurrentCount = 0;
            for (final PdfReader sourceReader : sourceReaders) {

                final int sourceReaderCount = sourceReader.getNumberOfPages();

                // transfer all the pages
                int importedPagesCount = 0;
                while ( importedPagesCount < sourceReaderCount ) {
                    // increment the counters
                    importedPagesCount++;
                    targetCurrentCount++;

                    // transfer the source page to the total document
                    targetDoc.newPage();
                    final PdfImportedPage page = totalWriter.getImportedPage(sourceReader, importedPagesCount);
                    targetContent.addTemplate(page, 0, 0);

                    // add a footer with page-information
                    targetContent.beginText();
                    targetContent.setFontAndSize(FOOTER_FONT, 9);
                    targetContent.showTextAligned(PdfContentByte.ALIGN_CENTER, "" + targetCurrentCount + " of " + targetTotalCount, 520, 5, 0);
                    targetContent.endText();
                }
            }

        } finally {
            // close all the readers
            closeQuietly(sourceReaders);
            // close the total document
            if (targetDoc.isOpen()) {
                targetDoc.close();
            }
            targetStream.flush();
            IOUtils.closeQuietly(targetStream);
        }

        // and return the result
        return new InMemoryDocument(targetStream.toByteArray(), filename, MimeTypeEnum.PDF);
    }

}
