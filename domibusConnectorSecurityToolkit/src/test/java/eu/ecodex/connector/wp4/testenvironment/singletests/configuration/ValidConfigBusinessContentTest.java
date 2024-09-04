/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.wp4.testenvironment.singletests.configuration;

import eu.ecodex.connector.wp4.testenvironment.configurations.ValidConfig_BusinessContent;
import eu.ecodex.dss.model.BusinessContent;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

// SUB-CONF-13
@SuppressWarnings("checkstyle:TypeName")
class ValidConfigBusinessContentTest {
    private static final String PATH_UNSIGNED_FILE = "src/test/resources/documents/Unsigned.xml";
    private static final String PATH_ATTACHMENT = "src/test/resources/documents/Attachment2.bmp";
    private static final String PATH_SIGNED_FILE = "src/test/resources/documents/One_Sig_New.pdf";
    private static final String PATH_UNSUPPORTED_SIGNED_FILE =
        "src/test/resources/documents/business_document_with_unsupported_signature.xml";
    private static final String PATH_DETACHED_SIGNATURE =
        "src/test/resources/documents/Test_Data.xml.pkcs7";
    private static final String PATH_DETACHED_SIGNED_FILE =
        "src/test/resources/documents/Test_Data.xml";

    /**
     * Variant 1 - Object with Signed Business Document (Supported by DSS) and without attachments.
     */
    @Test
    void test_Signed_PDF_without_Attachments() {
        BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithoutAttachments();

        DSSDocument signedDocument = getDocument(PATH_SIGNED_FILE, "Business_Content");
        byte[] signedDocumentByte = getByte(signedDocument);
        byte[] businessContentDocument = getByte(content.getDocument());

        Assertions.assertNotNull(content.getDocument());
        Assertions.assertEquals(signedDocument.getName(), content.getDocument().getName());
        Assertions.assertArrayEquals(signedDocumentByte, businessContentDocument);
        Assertions.assertEquals(signedDocument.getMimeType(), content.getDocument().getMimeType());
    }

    /**
     * Variant 3 - Object with Signed Business Document (Supported by DSS) and with attachments.
     */
    @Test
    void test_Signed_PDF_with_Attachments() {
        BusinessContent content = ValidConfig_BusinessContent.get_SignedFile_WithAttachments();

        DSSDocument signedDocument = getDocument(PATH_SIGNED_FILE, "Business_Content");
        byte[] signedDocumentByte = getByte(signedDocument);
        byte[] businessContentDocument = getByte(content.getDocument());

        Assertions.assertNotNull(content.getDocument());
        Assertions.assertEquals(signedDocument.getName(), content.getDocument().getName());
        Assertions.assertArrayEquals(signedDocumentByte, businessContentDocument);
        Assertions.assertEquals(signedDocument.getMimeType(), content.getDocument().getMimeType());

        List<DSSDocument> attachments = content.getAttachments();

        Assertions.assertFalse(attachments.isEmpty());
        Assertions.assertEquals(1, attachments.size());

        DSSDocument attachment = attachments.getFirst();
        DSSDocument compareAttachment = getDocument(PATH_ATTACHMENT, "Attachment_1");

        byte[] attachmentByte = getByte(attachment);
        byte[] compareAttachmentByte = getByte(compareAttachment);

        Assertions.assertEquals(attachment.getName(), compareAttachment.getName());
        Assertions.assertArrayEquals(attachmentByte, compareAttachmentByte);
        Assertions.assertEquals(attachment.getMimeType(), compareAttachment.getMimeType());
    }

    /**
     * Variant 4 - Object with Signed Business Document (Not Supported by DSS) and with
     * attachments.
     */
    @Test
    void test_Unsupported_PDF_with_Attachments() {
        BusinessContent content =
            ValidConfig_BusinessContent.get_UnsupportedSignedFile_WithAttachments();

        DSSDocument signedDocument = getDocument(PATH_UNSUPPORTED_SIGNED_FILE, "Business_Content");
        byte[] signedDocumentByte = getByte(signedDocument);
        byte[] businessContentDocument = getByte(content.getDocument());

        Assertions.assertNotNull(content.getDocument());
        Assertions.assertEquals(signedDocument.getName(), content.getDocument().getName());
        Assertions.assertArrayEquals(signedDocumentByte, businessContentDocument);
        Assertions.assertEquals(signedDocument.getMimeType(), content.getDocument().getMimeType());

        List<DSSDocument> attachments = content.getAttachments();

        Assertions.assertFalse(attachments.isEmpty());
        Assertions.assertEquals(1, attachments.size());

        DSSDocument attachment = attachments.getFirst();
        DSSDocument compareAttachment = getDocument(PATH_ATTACHMENT, "Attachment_1");

        byte[] attachmentByte = getByte(attachment);
        byte[] compareAttachmentByte = getByte(compareAttachment);

        Assertions.assertEquals(attachment.getName(), compareAttachment.getName());
        Assertions.assertArrayEquals(attachmentByte, compareAttachmentByte);
        Assertions.assertEquals(attachment.getMimeType(), compareAttachment.getMimeType());
    }

    /**
     * Variant 5 - Object with Detached-Signed Business Document (Supported by DSS) and with
     * attachments.
     */
    @Test
    void test_Detached_Signed_PDF_with_Attachments() {
        BusinessContent content =
            ValidConfig_BusinessContent.get_DetachedSignedFile_WithAttachments();

        DSSDocument signedDocument = getDocument(PATH_DETACHED_SIGNED_FILE, "Business_Content");
        byte[] signedDocumentByte = getByte(signedDocument);
        byte[] businessContentDocument = getByte(content.getDocument());

        Assertions.assertNotNull(content.getDocument());
        Assertions.assertEquals(signedDocument.getName(), content.getDocument().getName());
        Assertions.assertArrayEquals(signedDocumentByte, businessContentDocument);
        Assertions.assertEquals(signedDocument.getMimeType(), content.getDocument().getMimeType());

        DSSDocument signature = getDocument(PATH_DETACHED_SIGNATURE, "Detached_Signature");
        byte[] signatureByte = getByte(signature);
        byte[] businessContentSignature = getByte(content.getDetachedSignature());

        Assertions.assertNotNull(content.getDetachedSignature());
        Assertions.assertEquals(signature.getName(), content.getDetachedSignature().getName());
        Assertions.assertArrayEquals(signatureByte, businessContentSignature);
        Assertions.assertEquals(
            signature.getMimeType(), content.getDetachedSignature().getMimeType());

        List<DSSDocument> attachments = content.getAttachments();

        Assertions.assertFalse(attachments.isEmpty());
        Assertions.assertEquals(1, attachments.size());

        DSSDocument attachment = attachments.getFirst();
        DSSDocument compareAttachment = getDocument(PATH_ATTACHMENT, "Attachment_1");

        byte[] attachmentByte = getByte(attachment);
        byte[] compareAttachmentByte = getByte(compareAttachment);

        Assertions.assertEquals(attachment.getName(), compareAttachment.getName());
        Assertions.assertArrayEquals(attachmentByte, compareAttachmentByte);
        Assertions.assertEquals(attachment.getMimeType(), compareAttachment.getMimeType());
    }

    /**
     * Variant 6 - Object with Unsigned Business Document and with attachments.
     */
    @Test
    void test_Unsigned_PDF_with_Attachments() {
        BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithAttachments();

        DSSDocument signedDocument = getDocument(PATH_UNSIGNED_FILE, "Business_Content");
        byte[] signedDocumentByte = getByte(signedDocument);
        byte[] businessContentDocument = getByte(content.getDocument());

        Assertions.assertNotNull(content.getDocument());
        Assertions.assertEquals(signedDocument.getName(), content.getDocument().getName());
        Assertions.assertArrayEquals(signedDocumentByte, businessContentDocument);
        Assertions.assertEquals(signedDocument.getMimeType(), content.getDocument().getMimeType());

        List<DSSDocument> attachments = content.getAttachments();

        Assertions.assertFalse(attachments.isEmpty());
        Assertions.assertEquals(1, attachments.size());

        DSSDocument attachment = attachments.getFirst();
        DSSDocument compareAttachment = getDocument(PATH_ATTACHMENT, "Attachment_1");

        byte[] attachmentByte = getByte(attachment);
        byte[] compareAttachmentByte = getByte(compareAttachment);

        Assertions.assertEquals(attachment.getName(), compareAttachment.getName());
        Assertions.assertArrayEquals(attachmentByte, compareAttachmentByte);
        Assertions.assertEquals(attachment.getMimeType(), compareAttachment.getMimeType());
    }

    /**
     * Variant 7 - Object with Unsigned Business Document and without attachments.
     */
    @Test
    void test_Unsigned_PDF_without_Attachments() {
        BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithoutAttachments();

        DSSDocument signedDocument = getDocument(PATH_UNSIGNED_FILE, "Business_Content");
        byte[] signedDocumentByte = getByte(signedDocument);
        byte[] businessContentDocument = getByte(content.getDocument());

        Assertions.assertNotNull(content.getDocument());
        Assertions.assertEquals(signedDocument.getName(), content.getDocument().getName());
        Assertions.assertArrayEquals(signedDocumentByte, businessContentDocument);
        Assertions.assertEquals(signedDocument.getMimeType(), content.getDocument().getMimeType());
    }

    private DSSDocument getDocument(String path, String name) {

        ByteArrayOutputStream outputStream = null;
        FileInputStream fis = null;

        try {
            File f = new File(path);

            outputStream = new ByteArrayOutputStream();
            fis = new FileInputStream(f);

            byte[] buffer = new byte[1024];

            for (int len = fis.read(buffer); len > 0; len = fis.read(buffer)) {
                outputStream.write(buffer, 0, len);
            }

            fis.close();

            byte[] byteDocument = outputStream.toByteArray();

            return new InMemoryDocument(byteDocument, name);
        } catch (Exception e) {
            System.err.println("Exception within the configuration of the business content");
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(outputStream);
        }
    }

    private byte[] getByte(DSSDocument document) {
        ByteArrayOutputStream outputStream = null;
        InputStream is = null;

        try {
            outputStream = new ByteArrayOutputStream();
            is = document.openStream();

            byte[] buffer = new byte[1024];

            for (int len = is.read(buffer); len > 0; len = is.read(buffer)) {
                outputStream.write(buffer, 0, len);
            }

            is.close();

            return outputStream.toByteArray();
        } catch (Exception e) {
            System.err.println("Exception within the configuration of the business content");
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(outputStream);
        }
    }
}
