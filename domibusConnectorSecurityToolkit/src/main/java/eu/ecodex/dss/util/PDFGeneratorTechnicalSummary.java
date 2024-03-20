/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/util/PDFGeneratorTechnicalSummary.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */
package eu.ecodex.dss.util;

import java.io.ByteArrayOutputStream;
import java.util.List;

import eu.europa.esig.dss.enumerations.MimeType;
import eu.europa.esig.dss.enumerations.MimeTypeEnum;
import eu.europa.esig.dss.model.DSSDocument;
import org.apache.commons.io.IOUtils;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import eu.ecodex.dss.model.token.AdvancedSystemType;
import eu.ecodex.dss.model.token.Signature;
import eu.ecodex.dss.model.token.SignatureAttributes;
import eu.ecodex.dss.model.token.SignatureCertificate;
import eu.ecodex.dss.model.token.TechnicalTrustLevel;
import eu.ecodex.dss.model.token.Token;
import eu.ecodex.dss.model.token.TokenValidation;
import eu.ecodex.dss.model.token.ValidationVerification;

import eu.europa.esig.dss.model.InMemoryDocument;

/**
 * This creates the technical summary page of the trustoktoken
 * <p>
 * DISCLAIMER: Project owner e-CODEX
 * </p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
public class PDFGeneratorTechnicalSummary extends AbstractPDFGenerator {

    private static final String STATE_SUCCESS = "Successful";
    private static final String STATE_FAIL = "Fail";

    /**
     * The default constructor for PDFReportGenerator.
     */
    public PDFGeneratorTechnicalSummary() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DSSDocument generate(final Token token) throws DocumentException {

        // Result

        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final com.lowagie.text.Document document = new com.lowagie.text.Document();

        try {

            final PdfWriter writer = PdfWriter.getInstance(document, output);
            writer.setPdfVersion(PdfWriter.PDF_VERSION_1_4);

            document.open();

            addPageHeader(document, token);
            addTitle(document);
            addGeneral(document, token);
            
            final AdvancedSystemType systemType = token.getAdvancedElectronicSystem();
        	final TokenValidation validation = token.getValidation();
        	final ValidationVerification valVeri = (validation != null) ? validation.getVerificationData() : null;
        	final List<Signature> signatureList = (valVeri != null) ? valVeri.getSignatureData() : null;
        	
            if (systemType == AdvancedSystemType.AUTHENTICATION_BASED) {
            	if(signatureList != null && 
            			!signatureList.isEmpty() &&
            			signatureList.get(0).getCertificateInformation() != null &&
            			signatureList.get(0).getSignatureInformation() != null &&
            			signatureList.get(0).getSigningTime() != null &&
            			signatureList.get(0).getTechnicalResult() != null) {
            		
               		for (int sigCounter = 0; sigCounter < signatureList.size(); sigCounter++)
            		{
            			Signature signature = signatureList.get(sigCounter);
            			
            			if(sigCounter > 0) {
            				document.newPage();
            				
            	            addPageHeader(document, token);
            	            addTitle(document);
            	            addGeneral(document, token);
            			}
            			
            			addSignatureNumber(document, sigCounter + 1, signatureList.size());
    	                addSignatureResults(document, signature);
    	                addSignatureCertificate(document, signature);
    	                addResult(document, signature);
    	                addStamp(document, signature.getTechnicalResult().getTrustLevel());
					}
            	} else {
            		addAuthenticationResults(document, token);
            		addResult(document, token);
            		addStamp(document, token.getTechnicalValidationResultTrustLevel());
            	}
            	
            } else {
            	
            	if(signatureList != null && !signatureList.isEmpty()) {
            		for (int sigCounter = 0; sigCounter < signatureList.size(); sigCounter++)
            		{
            			Signature signature = signatureList.get(sigCounter);
            			
            			if(sigCounter > 0) {
            				document.newPage();
            				
            	            addPageHeader(document, token);
            	            addTitle(document);
            	            addGeneral(document, token);
            			}
            			
            			addSignatureNumber(document, sigCounter + 1, signatureList.size());
            			
    	                addSignatureResults(document, signature);
    	                addSignatureCertificate(document, signature);
    	                
            			// AK: For Backwardcompatibility with existing, national systems.
            			if(signatureList.size() == 1 &&
            					(signature.getTechnicalResult() == null || signature.getTechnicalResult().getTrustLevel() == null)) {
            				addResult(document, token);
            				addStamp(document, token.getTechnicalValidationResultTrustLevel());
            			} else {
        	                addResult(document, signature);
        	                addStamp(document, signature.getTechnicalResult().getTrustLevel());            				
            			}
					}
            	}else{
            		addResult(document, token);
            		addStamp(document, token.getTechnicalValidationResultTrustLevel());
            	}
            }

        } finally {
            if (document.isOpen()) {
                document.close();
            }
            IOUtils.closeQuietly(output);
        }

        return new InMemoryDocument(output.toByteArray(), "token-summary-technical.pdf", MimeTypeEnum.PDF);

    }

    private void addTitle(final com.lowagie.text.Document document) throws DocumentException {
        // Title
        Paragraph paragraph = new Paragraph("Technical Assessment of the Validation Report", FONT_H2);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setSpacingAfter(30);
        document.add(paragraph);
    }

    private void addSignatureNumber(final com.lowagie.text.Document document, int curNumber, int maxNumber) throws DocumentException {
        // Title
        Paragraph paragraph = new Paragraph("Signature " + curNumber + " of " + maxNumber, FONT_H4);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setSpacingAfter(30);
        document.add(paragraph);
    }
    
    private void addGeneral(final com.lowagie.text.Document document, final Token token) throws DocumentException {

        // get data from structure
        final String country = PDFUtil.format(token.getIssuerCountry());
        final String advancedElectronicSystem = PDFUtil.format(token.getAdvancedElectronicSystemText());

        final String verificationTime = PDFUtil.format(token.getValidationVerificationTime());
        final String documentType = PDFUtil.format(token.getDocumentType());
        final String documentName = PDFUtil.format(token.getDocumentName());

        // create the pdf
        final PdfPTable table = new PdfPTable(2);
        table.setSpacingAfter(20);

        final PdfPCell captionCell = new PdfPCell();
        captionCell.setPhrase(new Paragraph("General Information", FONT_TEXT));
        captionCell.setColspan(2);
        captionCell.setBorder(Rectangle.NO_BORDER);

        final PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);

        final PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);

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

        leftCell.setPhrase(new Paragraph("Verification Time", FONT_TEXT));
        rightCell.setPhrase(new Paragraph(verificationTime, FONT_TEXT));
        table.addCell(leftCell);
        table.addCell(rightCell);

        document.add(table);
    }

    private void addAuthenticationResults(final com.lowagie.text.Document document, final Token token) throws DocumentException {

        // get data from structure
        final String authProvider = PDFUtil.format(token.getValidationVerificationAuthenticationProvider());
        final String authUser = PDFUtil.format(token.getValidationVerificationAuthenticationUsername());
        final String authTime = PDFUtil.format(token.getValidationVerificationAuthenticationTime());

        // create the pdf
        final PdfPTable table = new PdfPTable(2);
        table.setSpacingAfter(20);

        final PdfPCell captionCell = new PdfPCell();
        captionCell.setPhrase(new Paragraph("Authentication Information", FONT_TEXT));
        captionCell.setColspan(2);
        captionCell.setBorder(Rectangle.NO_BORDER);

        table.addCell(captionCell);

        final PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);

        final PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);

        leftCell.setPhrase(new Paragraph("Identity Provider", FONT_TEXT));
        rightCell.setPhrase(new Paragraph(authProvider, FONT_TEXT));
        table.addCell(leftCell);
        table.addCell(rightCell);

        leftCell.setPhrase(new Paragraph("Username Synonym", FONT_TEXT));
        rightCell.setPhrase(new Paragraph(authUser, FONT_TEXT));
        table.addCell(leftCell);
        table.addCell(rightCell);

        leftCell.setPhrase(new Paragraph("Time of Authentication", FONT_TEXT));
        rightCell.setPhrase(new Paragraph(authTime, FONT_TEXT));
        table.addCell(leftCell);
        table.addCell(rightCell);

        document.add(table);
    }

    private void addSignatureResults(final com.lowagie.text.Document document, final Signature signature) throws DocumentException {

    	// create the pdf
        final PdfPTable table = new PdfPTable(2);
        table.setSpacingAfter(20);

        final PdfPCell captionCell = new PdfPCell();
        captionCell.setPhrase(new Paragraph("Signature information", FONT_TEXT));
        captionCell.setColspan(2);
        captionCell.setBorder(Rectangle.NO_BORDER);

        table.addCell(captionCell);

    	SignatureAttributes signatureAttributes = signature.getSignatureInformation();
        
    	if(signatureAttributes != null) {
	    	// get data from structure
	        final String signingTime = PDFUtil.format(signature.getSigningTime());
	        final String structureVerification = signatureAttributes.isStructureValid() ? STATE_SUCCESS : STATE_FAIL;
	        final String signatureVerification = signatureAttributes.isSignatureValid() ? STATE_SUCCESS : STATE_FAIL;
	        final String signatureLevel = PDFUtil.format(signatureAttributes.getSignatureLevel());

	        final PdfPCell leftCell = new PdfPCell();
	        leftCell.setBorder(Rectangle.NO_BORDER);
	        leftCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
	
	        final PdfPCell rightCell = new PdfPCell();
	        rightCell.setBorder(Rectangle.NO_BORDER);
	        rightCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
	
	        leftCell.setPhrase(new Paragraph("Signing Time", FONT_TEXT));
	        rightCell.setPhrase(new Paragraph(signingTime, FONT_TEXT));
	        table.addCell(leftCell);
	        table.addCell(rightCell);
	
	        leftCell.setPhrase(new Paragraph("Structure Verification", FONT_TEXT));
	        rightCell.setPhrase(new Paragraph(structureVerification, FONT_TEXT));
	        table.addCell(leftCell);
	        table.addCell(rightCell);
	
	        leftCell.setPhrase(new Paragraph("Signature Verification", FONT_TEXT));
	        rightCell.setPhrase(new Paragraph(signatureVerification, FONT_TEXT));
	        table.addCell(leftCell);
	        table.addCell(rightCell);
	
	        leftCell.setPhrase(new Paragraph("Signature Level", FONT_TEXT));
	        rightCell.setPhrase(new Paragraph(signatureLevel, FONT_TEXT));
	        table.addCell(leftCell);
	        table.addCell(rightCell);
    	} else {
    		// Just in case. Should never happen as the token structure is invalid when the signature attributes are missing.
            captionCell.setPhrase(new Paragraph("Signature Attributes missing. No signature information available!", FONT_TEXT));
            captionCell.setHorizontalAlignment(PdfCell.ALIGN_CENTER);

            table.addCell(captionCell);
    	}
        document.add(table);
    }

    private void addSignatureCertificate(final com.lowagie.text.Document document, final Signature signature) throws DocumentException {

    	SignatureCertificate sigCert = (signature != null) ? signature.getCertificateInformation() : null;
    	
    	if(sigCert == null){
    		return;
    	}
    	
        // get data from structure
    	final String certificateOwner = PDFUtil.format(sigCert.getSubject());
        final String certificateIssuer = PDFUtil.format(sigCert.getIssuer());
        final String certificateVerification = sigCert.isCertificateValid() ? STATE_SUCCESS : STATE_FAIL;
        final String validityAtSigningTime = sigCert.isValidityAtSigningTime() ? STATE_SUCCESS : STATE_FAIL;

        // create the pdf
        final PdfPTable table = new PdfPTable(2);
        table.setSpacingAfter(20);

        final PdfPCell captionCell = new PdfPCell();
        captionCell.setPhrase(new Paragraph("Certificate information", FONT_TEXT));
        captionCell.setColspan(2);
        captionCell.setBorder(Rectangle.NO_BORDER);

        table.addCell(captionCell);

        final PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);

        final PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);

        leftCell.setPhrase(new Paragraph("Signatory", FONT_TEXT));
        rightCell.setPhrase(new Paragraph(certificateOwner, FONT_TEXT));
        table.addCell(leftCell);
        table.addCell(rightCell);
        
        leftCell.setPhrase(new Paragraph("Issuer", FONT_TEXT));
        rightCell.setPhrase(new Paragraph(certificateIssuer, FONT_TEXT));
        table.addCell(leftCell);
        table.addCell(rightCell);

        leftCell.setPhrase(new Paragraph("Certificate Verification", FONT_TEXT));
        rightCell.setPhrase(new Paragraph(certificateVerification, FONT_TEXT));
        table.addCell(leftCell);
        table.addCell(rightCell);

        leftCell.setPhrase(new Paragraph("Validity At Signing Time", FONT_TEXT));
        rightCell.setPhrase(new Paragraph(validityAtSigningTime, FONT_TEXT));
        table.addCell(leftCell);
        table.addCell(rightCell);

        document.add(table);
    }

    private void addResult(final com.lowagie.text.Document document, final Signature signature) throws DocumentException {

        // get data from structure
        final TechnicalTrustLevel trustLevel = signature.getTechnicalResult().getTrustLevel();
        final String validationResult = PDFUtil.format(trustLevel == null ? null : trustLevel.getText());
        final String comment = PDFUtil.format(signature.getTechnicalResult().getComment() == null ? "" : signature.getTechnicalResult().getComment());

        // create the pdf
        final PdfPTable table = new PdfPTable(2);
        table.setSpacingAfter(20);

        final PdfPCell captionCell = new PdfPCell();
        captionCell.setPhrase(new Paragraph("Technical Result", FONT_TEXT));
        captionCell.setColspan(2);
        captionCell.setBorder(Rectangle.NO_BORDER);

        table.addCell(captionCell);

        final PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);

        final PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);

        leftCell.setPhrase(new Paragraph("Validation of the Document", FONT_TEXT));
        rightCell.setPhrase(new Paragraph(validationResult, FONT_TEXT));
        table.addCell(leftCell);
        table.addCell(rightCell);

        leftCell.setPhrase(new Paragraph("Comment", FONT_TEXT));
        rightCell.setPhrase(new Paragraph(comment, FONT_TEXT));
        table.addCell(leftCell);
        table.addCell(rightCell);
        
        document.add(table);
    }
    
    private void addResult(final com.lowagie.text.Document document, final Token token) throws DocumentException {

        // get data from structure
        final TechnicalTrustLevel trustLevel = token.getTechnicalValidationResultTrustLevel();
        final String validationResult = PDFUtil.format(trustLevel == null ? null : trustLevel.getText());

        // create the pdf
        final PdfPTable table = new PdfPTable(2);
        table.setSpacingAfter(20);

        final PdfPCell captionCell = new PdfPCell();
        captionCell.setPhrase(new Paragraph("Technical Result", FONT_TEXT));
        captionCell.setColspan(2);
        captionCell.setBorder(Rectangle.NO_BORDER);

        table.addCell(captionCell);

        final PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);

        final PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);

        leftCell.setPhrase(new Paragraph("Validation of the Document", FONT_TEXT));
        rightCell.setPhrase(new Paragraph(validationResult, FONT_TEXT));
        table.addCell(leftCell);
        table.addCell(rightCell);

        TokenValidation validation = token.getValidation();
        ValidationVerification verification = (validation != null) ? validation.getVerificationData() : null;
        List<Signature> signatures = (verification != null) ? verification.getSignatureData() : null;
        
        if (signatures == null || signatures.isEmpty()) {

        	final String comment = PDFUtil.format(validation.getTechnicalResult().getComment() == null ? "Unable to find signatures" : validation.getTechnicalResult().getComment());

            leftCell.setPhrase(new Paragraph("Comment", FONT_TEXT));
            rightCell.setPhrase(new Paragraph(comment, FONT_TEXT));
            table.addCell(leftCell);
            table.addCell(rightCell);
        }

        document.add(table);
    }

    private void addStamp(final com.lowagie.text.Document document, final TechnicalTrustLevel trustLevel) throws DocumentException {
        // get data from structure
        Image resultImg = IMG_TECHNICAL_FAIL;
        if (trustLevel == TechnicalTrustLevel.SUCCESSFUL) {
            resultImg = IMG_TECHNICAL_SUCCESSFULL;
        } else if (trustLevel == TechnicalTrustLevel.SUFFICIENT) {
            resultImg = IMG_TECHNICAL_SUFFICIENT;
        }

        // create the pdf
        final PdfPTable table = new PdfPTable(2);
        table.setWidths(new int[]{70, 30});
        table.setSpacingBefore(10);

        final PdfPCell lValidationCell = new PdfPCell();
        lValidationCell.addElement(new Paragraph(""));
        lValidationCell.setPadding(5);
        lValidationCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(lValidationCell);

        final PdfPCell rValidationCell = new PdfPCell();
        rValidationCell.setBorder(Rectangle.NO_BORDER);
        rValidationCell.addElement(resultImg);
        rValidationCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        rValidationCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        rValidationCell.setPadding(5);
        table.addCell(rValidationCell);

        document.add(table);
    }

}
