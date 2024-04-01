package wp4.testenvironment.singletests.configuration;

import eu.ecodex.dss.model.BusinessContent;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import wp4.testenvironment.configurations.ValidConfig_BusinessContent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;


// SUB-CONF-13
class Test_ValidConfig_BusinessContent_Test {
    private static final String Path_UnsignedFile = "src/test/resources/documents/Unsigned.xml";
    private static final String Path_Attachment = "src/test/resources/documents/Attachment2.bmp";
    //	private static String Path_SignedFile = "src/test/resources/documents/One_Sig.pdf";
    private static final String Path_SignedFile = "src/test/resources/documents/One_Sig_New.pdf";
    private static final String Path_Unsupported_SignedFile =
            "src/test/resources/documents/business_document_with_unsupported_signature.xml";
    //	private static String Path_Detached_Signature = "src/test/resources/documents/dataFR.xml";
    //	private static String Path_Detached_SignedFile = "src/test/resources/documents/dataFR.pdf";
    private static final String Path_Detached_Signature = "src/test/resources/documents/Test_Data.xml.pkcs7";
    private static final String Path_Detached_SignedFile = "src/test/resources/documents/Test_Data.xml";

    /*
     * Variant 1 - Object with Signed Business Document (Supported by DSS) and without attachments
     */
    @Test
    void test_Signed_PDF_without_Attachments() throws Exception {
        BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithoutAttachments();

        DSSDocument signedDocument = getDocument(Path_SignedFile, "Business_Content");
        byte[] signedDocumentByte = getByte(signedDocument);
        byte[] businessContentDocument = getByte(content.getDocument());

        Assertions.assertNotNull(content.getDocument());
        Assertions.assertEquals(signedDocument.getName(), content.getDocument().getName());
        Assertions.assertArrayEquals(signedDocumentByte, businessContentDocument);
        Assertions.assertEquals(signedDocument.getMimeType(), content.getDocument().getMimeType());
    }

    /*
     * Variant 2 - Object with Business Document and with attachments
     *  Removed due to more specific tests
     */

    /*
     * Variant 3 - Object with Signed Business Document (Supported by DSS) and with attachments
     */
    @Test
    void test_Signed_PDF_with_Attachments() throws Exception {
        BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithAttachments();

        DSSDocument signedDocument = getDocument(Path_SignedFile, "Business_Content");
        byte[] signedDocumentByte = getByte(signedDocument);
        byte[] businessContentDocument = getByte(content.getDocument());

        Assertions.assertNotNull(content.getDocument());
        Assertions.assertEquals(signedDocument.getName(), content.getDocument().getName());
        Assertions.assertArrayEquals(signedDocumentByte, businessContentDocument);
        Assertions.assertEquals(signedDocument.getMimeType(), content.getDocument().getMimeType());

        List<DSSDocument> attachments = content.getAttachments();

        Assertions.assertFalse(attachments.isEmpty());
        Assertions.assertEquals(1, attachments.size());

        DSSDocument attachment = attachments.get(0);
        DSSDocument compareAttachment = getDocument(Path_Attachment, "Attachment_1");

        byte[] attachmentByte = getByte(attachment);
        byte[] compareAttachmentByte = getByte(compareAttachment);

        Assertions.assertEquals(attachment.getName(), compareAttachment.getName());
        Assertions.assertArrayEquals(attachmentByte, compareAttachmentByte);
        Assertions.assertEquals(attachment.getMimeType(), compareAttachment.getMimeType());
    }

    /*
     * Variant 4 - Object with Signed Business Document (Not Supported by DSS) and with attachments
     */
    @Test
    void test_Unsupported_PDF_with_Attachments() throws Exception {
        BusinessContent content = ValidConfig_BusinessContent.get_UnsupportedSignedFile_WithAttachments();

        DSSDocument signedDocument = getDocument(Path_Unsupported_SignedFile, "Business_Content");
        byte[] signedDocumentByte = getByte(signedDocument);
        byte[] businessContentDocument = getByte(content.getDocument());

        Assertions.assertNotNull(content.getDocument());
        Assertions.assertEquals(signedDocument.getName(), content.getDocument().getName());
        Assertions.assertArrayEquals(signedDocumentByte, businessContentDocument);
        Assertions.assertEquals(signedDocument.getMimeType(), content.getDocument().getMimeType());

        List<DSSDocument> attachments = content.getAttachments();

        Assertions.assertFalse(attachments.isEmpty());
        Assertions.assertEquals(1, attachments.size());

        DSSDocument attachment = attachments.get(0);
        DSSDocument compareAttachment = getDocument(Path_Attachment, "Attachment_1");

        byte[] attachmentByte = getByte(attachment);
        byte[] compareAttachmentByte = getByte(compareAttachment);

        Assertions.assertEquals(attachment.getName(), compareAttachment.getName());
        Assertions.assertArrayEquals(attachmentByte, compareAttachmentByte);
        Assertions.assertEquals(attachment.getMimeType(), compareAttachment.getMimeType());
    }

    /*
     * Variant 5 - Object with Detached-Signed Business Document (Supported by DSS) and with attachments
     */
    @Test
    void test_Detached_Signed_PDF_with_Attachments() throws Exception {
        BusinessContent content = ValidConfig_BusinessContent.get_DetachedSignedFile_WithAttachments();

        DSSDocument signedDocument = getDocument(Path_Detached_SignedFile, "Business_Content");
        byte[] signedDocumentByte = getByte(signedDocument);
        byte[] businessContentDocument = getByte(content.getDocument());

        Assertions.assertNotNull(content.getDocument());
        Assertions.assertEquals(signedDocument.getName(), content.getDocument().getName());
        Assertions.assertArrayEquals(signedDocumentByte, businessContentDocument);
        Assertions.assertEquals(signedDocument.getMimeType(), content.getDocument().getMimeType());

        DSSDocument signature = getDocument(Path_Detached_Signature, "Detached_Signature");
        byte[] signatureByte = getByte(signature);
        byte[] businessContentSignature = getByte(content.getDetachedSignature());

        Assertions.assertNotNull(content.getDetachedSignature());
        Assertions.assertEquals(signature.getName(), content.getDetachedSignature().getName());
        Assertions.assertArrayEquals(signatureByte, businessContentSignature);
        Assertions.assertEquals(signature.getMimeType(), content.getDetachedSignature().getMimeType());

        List<DSSDocument> attachments = content.getAttachments();

        Assertions.assertFalse(attachments.isEmpty());
        Assertions.assertEquals(1, attachments.size());

        DSSDocument attachment = attachments.get(0);
        DSSDocument compareAttachment = getDocument(Path_Attachment, "Attachment_1");

        byte[] attachmentByte = getByte(attachment);
        byte[] compareAttachmentByte = getByte(compareAttachment);

        Assertions.assertEquals(attachment.getName(), compareAttachment.getName());
        Assertions.assertArrayEquals(attachmentByte, compareAttachmentByte);
        Assertions.assertEquals(attachment.getMimeType(), compareAttachment.getMimeType());
    }

    /*
     * Variant 6 - Object with Unsigned Business Document and with attachments
     */
    @Test
    void test_Unsigned_PDF_with_Attachments() throws Exception {
        BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithAttachments();

        DSSDocument signedDocument = getDocument(Path_UnsignedFile, "Business_Content");
        byte[] signedDocumentByte = getByte(signedDocument);
        byte[] businessContentDocument = getByte(content.getDocument());

        Assertions.assertNotNull(content.getDocument());
        Assertions.assertEquals(signedDocument.getName(), content.getDocument().getName());
        Assertions.assertArrayEquals(signedDocumentByte, businessContentDocument);
        Assertions.assertEquals(signedDocument.getMimeType(), content.getDocument().getMimeType());

        List<DSSDocument> attachments = content.getAttachments();

        Assertions.assertFalse(attachments.isEmpty());
        Assertions.assertEquals(1, attachments.size());

        DSSDocument attachment = attachments.get(0);
        DSSDocument compareAttachment = getDocument(Path_Attachment, "Attachment_1");

        byte[] attachmentByte = getByte(attachment);
        byte[] compareAttachmentByte = getByte(compareAttachment);

        Assertions.assertEquals(attachment.getName(), compareAttachment.getName());
        Assertions.assertArrayEquals(attachmentByte, compareAttachmentByte);
        Assertions.assertEquals(attachment.getMimeType(), compareAttachment.getMimeType());
    }

    /*
     * Variant 7 - Object with Unsigned Business Document and without attachments
     */
    @Test
    void test_Unsigned_PDF_without_Attachments() throws Exception {
        BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithoutAttachments();

        DSSDocument signedDocument = getDocument(Path_UnsignedFile, "Business_Content");
        byte[] signedDocumentByte = getByte(signedDocument);
        byte[] businessContentDocument = getByte(content.getDocument());

        Assertions.assertNotNull(content.getDocument());
        Assertions.assertEquals(signedDocument.getName(), content.getDocument().getName());
        Assertions.assertArrayEquals(signedDocumentByte, businessContentDocument);
        Assertions.assertEquals(signedDocument.getMimeType(), content.getDocument().getMimeType());
    }

    private DSSDocument getDocument(String path, String name) {
        ByteArrayOutputStream baos = null;
        FileInputStream fis = null;

        try {
            File f = new File(path);

            baos = new ByteArrayOutputStream();
            fis = new FileInputStream(f);

            byte[] buffer = new byte[1024];

            for (int len = fis.read(buffer); len > 0; len = fis.read(buffer)) {
                baos.write(buffer, 0, len);
            }

            fis.close();

            byte[] byteDocument = baos.toByteArray();

            return new InMemoryDocument(byteDocument, name);
        } catch (Exception e) {
            System.err.println("Exception within the configuration of the business content");
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(baos);
        }
    }


    private byte[] getByte(DSSDocument document) {
        ByteArrayOutputStream baos = null;
        InputStream is = null;

        try {
            baos = new ByteArrayOutputStream();
            is = document.openStream();

            byte[] buffer = new byte[1024];

            for (int len = is.read(buffer); len > 0; len = is.read(buffer)) {
                baos.write(buffer, 0, len);
            }

            is.close();

            byte[] byteDocument = baos.toByteArray();

            return byteDocument;
        } catch (Exception e) {
            System.err.println("Exception within the configuration of the business content");
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(baos);
        }
    }
}
