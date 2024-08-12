/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.wp4.testenvironment.configurations;

import eu.ecodex.dss.model.BusinessContent;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import org.apache.commons.io.IOUtils;

/**
 * The ValidConfig_BusinessContent class defines methods to retrieve different versions of a
 * BusinessContent object for testing purposes. Each method corresponds to a different variant of
 * the business content configuration.
 */
// SUB-CONF-13
@SuppressWarnings("checkstyle:TypeName")
public class ValidConfig_BusinessContent {
    private static final String PATH_UNSIGNED_FILE = "src/test/resources/documents/Unsigned.xml";
    private static final String PATH_ATTACHMENT = "src/test/resources/documents/Attachment2.bmp";
    private static final String PATH_SIGNED_FILE = "src/test/resources/documents/One_Sig_New.pdf";
    private static final String PATH_UNSUPPORTED_SIGNED_FILE =
        "src/test/resources/documents/business_document_with_unsupported_signature.xml";
    private static final String PATH_DETACHED_SIGNATURE =
        "src/test/resources/documents/Test_Data.xml.pkcs7";
    private static final String PATH_DETACHED_SIGNED_FILE =
        "src/test/resources/documents/Test_Data.xml";
    private static final String PATH_MULTISIGNED_FILE =
        "src/test/resources/documents/Evidence_workflow_Multisigned.pdf";
    private static final String PATH_MULTISIGNED_FILE_ONE_INVALID =
        "src/test/resources/documents/Two_Sig-One_Invalid.pdf";
    private static BusinessContent content;

    /**
     * Retrieves a signed file without any attachments as a {@link BusinessContent} object.
     *
     * @return the {@link BusinessContent} object containing the signed file
     */
    // SUB-CONF-13 - Variant 1
    public static BusinessContent get_SignedFile_WithoutAttachments() {
        content = new BusinessContent();
        ByteArrayOutputStream outputStream = null;
        FileInputStream fis = null;
        File file;

        try {
            file = new File(PATH_SIGNED_FILE);
            outputStream = new ByteArrayOutputStream();
            fis = new FileInputStream(file);

            byte[] buffer = new byte[1024];

            for (int len = fis.read(buffer); len > 0; len = fis.read(buffer)) {
                outputStream.write(buffer, 0, len);
            }

            fis.close();

            var byteDocument = outputStream.toByteArray();

            outputStream.close();

            content.setDocument(new InMemoryDocument(byteDocument, "Business_Content"));

            return content;
        } catch (Exception e) {
            System.err.println(
                "Exception within the configuration of the business content - Variant 1:"
            );
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(outputStream);
        }
    }

    // SUB-CONF-13 - Variant 2
    // --- Removed due to more specific tests ---

    /**
     * Retrieves a signed file with attachments as a {@link BusinessContent} object.
     *
     * @return the {@link BusinessContent} object containing the signed file and its attachments, or
     *      null if an exception occurs during the configuration process.
     */
    // SUB-CONF-13 - Variant 3
    public static BusinessContent get_SignedFile_WithAttachments() {
        content = new BusinessContent();

        ByteArrayOutputStream outputStream = null;
        FileInputStream fis = null;
        File file;

        try {
            file = new File(PATH_SIGNED_FILE);

            outputStream = new ByteArrayOutputStream();
            fis = new FileInputStream(file);

            byte[] buffer = new byte[1024];

            for (int len = fis.read(buffer); len > 0; len = fis.read(buffer)) {
                outputStream.write(buffer, 0, len);
            }

            fis.close();

            var byteDocument = outputStream.toByteArray();

            outputStream.close();

            content.setDocument(new InMemoryDocument(byteDocument, "Business_Content"));
            file = new File(PATH_ATTACHMENT);

            outputStream = new ByteArrayOutputStream();
            fis = new FileInputStream(file);

            for (int len = fis.read(buffer); len > 0; len = fis.read(buffer)) {
                outputStream.write(buffer, 0, len);
            }

            fis.close();

            byteDocument = outputStream.toByteArray();
            outputStream.close();

            var attachments = new ArrayList<DSSDocument>();
            attachments.add(new InMemoryDocument(byteDocument, "Attachment_1"));

            content.setAttachments(attachments);

            return content;
        } catch (Exception e) {
            System.err.println(
                "Exception within the configuration of the business content - Variant 1:");
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(outputStream);
        }
    }

    /**
     * Retrieves a business content object with an unsupported signed file and attachments.
     *
     * <p>This method creates a {@link BusinessContent} object and sets the signed file and its
     * attachments. The signed file is read from the file defined by the constant
     * {@code Path_Unsupported_SignedFile} and converted to a byte array. The attachments are read
     * from the file defined by the constant {@code Path_Attachment} and also converted to byte
     * arrays. The signed file and attachments are then added to the business content object.
     *
     * <p>If any exception occurs during the configuration process, an error message is printed to
     * the console and {@code null} is returned.
     *
     * @return the {@link BusinessContent} object containing the signed file and its attachments, or
     *      null if an exception occurs during the configuration process.
     * @see BusinessContent
     * @see BusinessContent#setDocument(DSSDocument)
     */
    // SUB-CONF-13 - Variant 4
    public static BusinessContent get_UnsupportedSignedFile_WithAttachments() {
        content = new BusinessContent();
        ByteArrayOutputStream outputStream = null;
        FileInputStream fis = null;
        File file;

        try {
            file = new File(PATH_UNSUPPORTED_SIGNED_FILE);

            outputStream = new ByteArrayOutputStream();
            fis = new FileInputStream(file);

            byte[] buffer = new byte[1024];

            for (int len = fis.read(buffer); len > 0; len = fis.read(buffer)) {
                outputStream.write(buffer, 0, len);
            }

            fis.close();

            var byteDocument = outputStream.toByteArray();

            outputStream.close();

            content.setDocument(new InMemoryDocument(byteDocument, "Business_Content"));
            file = new File(PATH_ATTACHMENT);

            outputStream = new ByteArrayOutputStream();
            fis = new FileInputStream(file);

            for (int len = fis.read(buffer); len > 0; len = fis.read(buffer)) {
                outputStream.write(buffer, 0, len);
            }

            fis.close();

            byteDocument = outputStream.toByteArray();
            outputStream.close();

            ArrayList<DSSDocument> attachments = new ArrayList<>();
            attachments.add(new InMemoryDocument(byteDocument, "Attachment_1"));

            content.setAttachments(attachments);

            return content;
        } catch (Exception e) {
            System.err.println(
                "Exception within the configuration of the business content - Variant 1:");
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(outputStream);
        }
    }

    /**
     * Retrieves a detached signed file with attachments as a {@link BusinessContent} object.
     *
     * <p>This method creates a {@link BusinessContent} object and sets the detached signed file
     * and its attachments. The detached signed file is read from the file defined by the constant
     * {@code Path_Detached_SignedFile} and converted to a byte array. The attachments are read from
     * the file defined by the constant {@code Path_Attachment} and also converted to byte arrays.
     * The detached signed file and attachments are then added to the business content object.
     *
     * <p>If any exception occurs during the configuration process, an error message is printed to
     * the console and {@code null} is returned.
     *
     * @return the {@link BusinessContent} object containing the detached signed file and its
     *      attachments, or null if an exception occurs during the configuration process.
     * @see BusinessContent
     * @see BusinessContent#setDocument(DSSDocument)
     */
    // SUB-CONF-13 - Variant 5
    public static BusinessContent get_DetachedSignedFile_WithAttachments() {
        content = new BusinessContent();

        ByteArrayOutputStream outputStream = null;
        FileInputStream fileInputStream = null;

        try {
            File file = new File(PATH_DETACHED_SIGNED_FILE);

            outputStream = new ByteArrayOutputStream();
            fileInputStream = new FileInputStream(file);

            byte[] buffer = new byte[1024];

            for (var len = fileInputStream.read(buffer); len > 0;
                 len = fileInputStream.read(buffer)) {
                outputStream.write(buffer, 0, len);
            }

            fileInputStream.close();

            byte[] byteDocument = outputStream.toByteArray();

            content.setDocument(new InMemoryDocument(byteDocument, "Business_Content"));

            file = new File(PATH_ATTACHMENT);

            outputStream = new ByteArrayOutputStream();
            fileInputStream = new FileInputStream(file);

            for (int len = fileInputStream.read(buffer); len > 0;
                 len = fileInputStream.read(buffer)) {
                outputStream.write(buffer, 0, len);
            }

            fileInputStream.close();

            byteDocument = outputStream.toByteArray();
            outputStream.close();

            var attachments = new ArrayList<DSSDocument>();
            attachments.add(new InMemoryDocument(byteDocument, "Attachment_1"));

            content.setAttachments(attachments);

            file = new File(PATH_DETACHED_SIGNATURE);

            outputStream = new ByteArrayOutputStream();
            fileInputStream = new FileInputStream(file);

            for (int len = fileInputStream.read(buffer); len > 0;
                 len = fileInputStream.read(buffer)) {
                outputStream.write(buffer, 0, len);
            }

            fileInputStream.close();

            byteDocument = outputStream.toByteArray();
            outputStream.close();

            content.setDetachedSignature(new InMemoryDocument(byteDocument, "Detached_Signature"));

            return content;
        } catch (Exception e) {
            System.err.println(
                "Exception within the configuration of the business content - Variant 1:");
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(fileInputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }

    /**
     * Retrieves an unsigned file with attachments as a {@link BusinessContent} object.
     *
     * <p>This method creates a {@link BusinessContent} object and sets the unsigned file and its
     * attachments. The unsigned file is read from the file defined by the constant
     * {@code Path_UnsignedFile} and converted to a byte array. The attachments are read from the
     * file defined by the constant {@code Path_Attachment} and also converted to byte arrays. The
     * unsigned file and attachments are then added to the business content object.
     *
     * @return the {@link BusinessContent} object containing the unsigned file and its attachments,
     *      or null if an exception occurs during the configuration process.
     * @see BusinessContent
     * @see BusinessContent#setDocument(DSSDocument)
     */
    // SUB-CONF-13 - Variant 6
    public static BusinessContent get_UnsignedFile_WithAttachments() {
        content = new BusinessContent();

        ByteArrayOutputStream outputStream = null;
        FileInputStream fis = null;
        File file;

        try {
            file = new File(PATH_UNSIGNED_FILE);

            outputStream = new ByteArrayOutputStream();
            fis = new FileInputStream(file);

            byte[] buffer = new byte[1024];

            for (int len = fis.read(buffer); len > 0; len = fis.read(buffer)) {
                outputStream.write(buffer, 0, len);
            }

            fis.close();

            byte[] byteDocument = outputStream.toByteArray();

            outputStream.close();

            content.setDocument(new InMemoryDocument(byteDocument, "Business_Content"));
            //----------------------------------------------------------
            file = new File(PATH_ATTACHMENT);

            outputStream = new ByteArrayOutputStream();
            fis = new FileInputStream(file);

            for (int len = fis.read(buffer); len > 0; len = fis.read(buffer)) {
                outputStream.write(buffer, 0, len);
            }

            fis.close();

            byteDocument = outputStream.toByteArray();
            outputStream.close();

            var attachments = new ArrayList<DSSDocument>();
            attachments.add(new InMemoryDocument(byteDocument, "Attachment_1"));

            content.setAttachments(attachments);

            return content;
        } catch (Exception e) {
            System.err.println(
                "Exception within the configuration of the business content - Variant 1:");
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(outputStream);
        }
    }

    /**
     * Retrieves an unsigned file without any attachments as a {@link BusinessContent} object.
     *
     * <p>This method creates a new {@link BusinessContent} object and sets the unsigned file as
     * its document. The unsigned file is read from the file defined by the constant
     * {@code Path_UnsignedFile} and converted to a byte array. The document is then added to the
     * {@link BusinessContent} object. No attachments are added to the object.
     *
     * @return the {@link BusinessContent} object containing the unsigned file without any
     *      attachments, or {@code null} if an exception occurs during the configuration process.
     * @see BusinessContent
     * @see BusinessContent#setDocument(DSSDocument)
     */
    // SUB-CONF-13 - Variant 7
    public static BusinessContent get_UnsignedFile_WithoutAttachments() {
        content = new BusinessContent();

        ByteArrayOutputStream outputStream = null;
        FileInputStream fileInputStream = null;

        try {
            File file = new File(PATH_UNSIGNED_FILE);

            outputStream = new ByteArrayOutputStream();
            fileInputStream = new FileInputStream(file);

            var buffer = new byte[1024];

            for (var len = fileInputStream.read(buffer); len > 0;
                 len = fileInputStream.read(buffer)) {
                outputStream.write(buffer, 0, len);
            }

            fileInputStream.close();

            var byteDocument = outputStream.toByteArray();

            content.setDocument(new InMemoryDocument(byteDocument, "Business_Content"));

            return content;
        } catch (Exception e) {
            System.err.println(
                "Exception within the configuration of the business content - Variant 1:");
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(fileInputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }

    /**
     * Retrieves a multisigned file without any attachments as a {@link BusinessContent} object.
     *
     * <p>This method creates a new {@link BusinessContent} object and sets the multisigned file as
     * its document. The multisigned file is read from the file defined by the constant
     * {@code Path_MultisignedFile} and converted to a byte array. The document is then added to the
     * {@link BusinessContent} object. No attachments are added to the object.
     *
     * @return the {@link BusinessContent} object containing the multisigned file without any
     *      attachments, or {@code null} if an exception occurs during the configuration process.
     * @see BusinessContent
     * @see BusinessContent#setDocument(DSSDocument)
     */
    // SUB-CONF-13 - Variant 8
    public static BusinessContent get_MultisignedFile_WithoutAttachments() {
        content = new BusinessContent();
        ByteArrayOutputStream outputStream = null;
        FileInputStream fileInputStream = null;

        try {
            File file = new File(PATH_MULTISIGNED_FILE);

            outputStream = new ByteArrayOutputStream();
            fileInputStream = new FileInputStream(file);

            var buffer = new byte[1024];

            for (var len = fileInputStream.read(buffer); len > 0;
                 len = fileInputStream.read(buffer)) {
                outputStream.write(buffer, 0, len);
            }

            fileInputStream.close();
            var byteDocument = outputStream.toByteArray();
            content.setDocument(new InMemoryDocument(byteDocument, "Business_Content"));

            return content;
        } catch (Exception e) {
            System.err.println(
                "Exception within the configuration of the business content - Variant 1:");
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(fileInputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }

    /**
     * Retrieves a multisigned file with one invalid signature and without any attachments as a
     * {@link BusinessContent} object.
     *
     * <p>This method creates a new {@link BusinessContent} object and sets the multisigned
     * file as its document. The multisigned file is read from the file defined by the constant
     * {@code Path_MultisignedFile_One_Invalid} and converted to a byte array. The document is then
     * added to the {@link BusinessContent} object. No attachments are added to the object.
     *
     * @return the {@link BusinessContent} object containing the multisigned file with one invalid
     *      signature and without any attachments, or {@code null} if an exception occurs during the
     *      configuration process.
     * @see BusinessContent
     * @see BusinessContent#setDocument(DSSDocument)
     */
    // SUB-CONF-13 - Variant 9
    public static BusinessContent get_MultisignedFile_One_Invalid_WithoutAttachments() {
        content = new BusinessContent();
        ByteArrayOutputStream outputStream = null;
        FileInputStream fis = null;

        try {
            File file = new File(PATH_MULTISIGNED_FILE_ONE_INVALID);

            outputStream = new ByteArrayOutputStream();
            fis = new FileInputStream(file);

            var buffer = new byte[1024];

            for (var len = fis.read(buffer); len > 0; len = fis.read(buffer)) {
                outputStream.write(buffer, 0, len);
            }

            fis.close();
            var byteDocument = outputStream.toByteArray();
            content.setDocument(new InMemoryDocument(byteDocument, "Business_Content"));

            return content;
        } catch (Exception e) {
            System.err.println(
                "Exception within the configuration of the business content - Variant 1:");
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(outputStream);
        }
    }
}
