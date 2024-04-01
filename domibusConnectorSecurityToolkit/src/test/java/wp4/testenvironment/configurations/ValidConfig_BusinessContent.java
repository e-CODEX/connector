package wp4.testenvironment.configurations;

import eu.ecodex.dss.model.BusinessContent;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;


// SUB-CONF-13
public class ValidConfig_BusinessContent {
    private static final String Path_UnsignedFile = "src/test/resources/documents/Unsigned.xml";
    private static final String Path_Attachment = "src/test/resources/documents/Attachment2.bmp";
    // private static String Path_SignedFile = "src/test/resources/documents/One_Sig.pdf";
    private static final String Path_SignedFile = "src/test/resources/documents/One_Sig_New.pdf";
    private static final String Path_Unsupported_SignedFile =
            "src/test/resources/documents/business_document_with_unsupported_signature.xml";
    //	private static String Path_Detached_Signature = "src/test/resources/documents/e-CODEX D4.9 Developed Modules
    //	and Building Blocks_v1.pdf.pkcs7";
    //	private static String Path_Detached_SignedFile = "src/test/resources/documents/e-CODEX D4.9 Developed Modules
    //	and Building Blocks_v1.pdf";
    private static final String Path_Detached_Signature = "src/test/resources/documents/Test_Data.xml.pkcs7";
    private static final String Path_Detached_SignedFile = "src/test/resources/documents/Test_Data.xml";
    //	private static String Path_MultisignedFile = "src/test/resources/documents/Two_Sig.pdf";
    private static final String Path_MultisignedFile = "src/test/resources/documents/Evidence_workflow_Multisigned.pdf";
    private static final String Path_MultisignedFile_One_Invalid = "src/test/resources/documents/Two_Sig-One_Invalid.pdf";

    private static BusinessContent content;

    // SUB-CONF-13 - Variant 1
    public static BusinessContent get_SignedFile_WithoutAttachments() {
        content = new BusinessContent();

        ByteArrayOutputStream baos = null;
        FileInputStream fis = null;
        File f = null;

        try {
            f = new File(Path_SignedFile);

            baos = new ByteArrayOutputStream();
            fis = new FileInputStream(f);

            byte[] buffer = new byte[1024];

            for (int len = fis.read(buffer); len > 0; len = fis.read(buffer)) {
                baos.write(buffer, 0, len);
            }

            fis.close();

            byte[] byteDocument = baos.toByteArray();

            baos.close();

            content.setDocument(new InMemoryDocument(byteDocument, "Business_Content"));

            return content;
        } catch (Exception e) {
            System.err.println("Exception within the configuration of the business content - Variant 1:");
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(baos);
        }
    }

    // SUB-CONF-13 - Variant 2
    // --- Removed due to more specific tests ---

    // SUB-CONF-13 - Variant 3
    public static BusinessContent get_SignedFile_WithAttachments() {
        content = new BusinessContent();

        ByteArrayOutputStream baos = null;
        FileInputStream fis = null;
        File f = null;

        try {
            f = new File(Path_SignedFile);

            baos = new ByteArrayOutputStream();
            fis = new FileInputStream(f);

            byte[] buffer = new byte[1024];

            for (int len = fis.read(buffer); len > 0; len = fis.read(buffer)) {
                baos.write(buffer, 0, len);
            }

            fis.close();

            byte[] byteDocument = baos.toByteArray();

            baos.close();

            content.setDocument(new InMemoryDocument(byteDocument, "Business_Content"));
            //----------------------------------------------------------
            f = new File(Path_Attachment);

            baos = new ByteArrayOutputStream();
            fis = new FileInputStream(f);

            for (int len = fis.read(buffer); len > 0; len = fis.read(buffer)) {
                baos.write(buffer, 0, len);
            }

            fis.close();

            byteDocument = baos.toByteArray();
            baos.close();

            ArrayList<DSSDocument> attachments = new ArrayList<DSSDocument>();
            attachments.add(new InMemoryDocument(byteDocument, "Attachment_1"));

            content.setAttachments(attachments);

            return content;
        } catch (Exception e) {
            System.err.println("Exception within the configuration of the business content - Variant 1:");
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(baos);
        }
    }

    // SUB-CONF-13 - Variant 4
    public static BusinessContent get_UnsupportedSignedFile_WithAttachments() {
        content = new BusinessContent();

        ByteArrayOutputStream baos = null;
        FileInputStream fis = null;
        File f = null;

        try {
            f = new File(Path_Unsupported_SignedFile);

            baos = new ByteArrayOutputStream();
            fis = new FileInputStream(f);

            byte[] buffer = new byte[1024];

            for (int len = fis.read(buffer); len > 0; len = fis.read(buffer)) {
                baos.write(buffer, 0, len);
            }

            fis.close();

            byte[] byteDocument = baos.toByteArray();

            baos.close();

            content.setDocument(new InMemoryDocument(byteDocument, "Business_Content"));
            //----------------------------------------------------------
            f = new File(Path_Attachment);

            baos = new ByteArrayOutputStream();
            fis = new FileInputStream(f);

            for (int len = fis.read(buffer); len > 0; len = fis.read(buffer)) {
                baos.write(buffer, 0, len);
            }

            fis.close();

            byteDocument = baos.toByteArray();
            baos.close();

            ArrayList<DSSDocument> attachments = new ArrayList<DSSDocument>();
            attachments.add(new InMemoryDocument(byteDocument, "Attachment_1"));

            content.setAttachments(attachments);

            return content;
        } catch (Exception e) {
            System.err.println("Exception within the configuration of the business content - Variant 1:");
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(baos);
        }
    }

    // SUB-CONF-13 - Variant 5
    public static BusinessContent get_DetachedSignedFile_WithAttachments() {
        content = new BusinessContent();

        ByteArrayOutputStream baos = null;
        FileInputStream fis = null;

        try {
            File f = new File(Path_Detached_SignedFile);

            baos = new ByteArrayOutputStream();
            fis = new FileInputStream(f);

            byte[] buffer = new byte[1024];

            for (int len = fis.read(buffer); len > 0; len = fis.read(buffer)) {
                baos.write(buffer, 0, len);
            }

            fis.close();

            byte[] byteDocument = baos.toByteArray();

            content.setDocument(new InMemoryDocument(byteDocument, "Business_Content"));

            //----------------------------------------------------------
            f = new File(Path_Attachment);

            baos = new ByteArrayOutputStream();
            fis = new FileInputStream(f);

            for (int len = fis.read(buffer); len > 0; len = fis.read(buffer)) {
                baos.write(buffer, 0, len);
            }

            fis.close();

            byteDocument = baos.toByteArray();
            baos.close();

            ArrayList<DSSDocument> attachments = new ArrayList<DSSDocument>();
            attachments.add(new InMemoryDocument(byteDocument, "Attachment_1"));

            content.setAttachments(attachments);

            //----------------------------------------------------------
            f = new File(Path_Detached_Signature);

            baos = new ByteArrayOutputStream();
            fis = new FileInputStream(f);

            for (int len = fis.read(buffer); len > 0; len = fis.read(buffer)) {
                baos.write(buffer, 0, len);
            }

            fis.close();

            byteDocument = baos.toByteArray();
            baos.close();

            content.setDetachedSignature(new InMemoryDocument(byteDocument, "Detached_Signature"));

            return content;
        } catch (Exception e) {
            System.err.println("Exception within the configuration of the business content - Variant 1:");
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(baos);
        }
    }

    // SUB-CONF-13 - Variant 6
    public static BusinessContent get_UnsignedFile_WithAttachments() {
        content = new BusinessContent();

        ByteArrayOutputStream baos = null;
        FileInputStream fis = null;
        File f = null;

        try {
            f = new File(Path_UnsignedFile);

            baos = new ByteArrayOutputStream();
            fis = new FileInputStream(f);

            byte[] buffer = new byte[1024];

            for (int len = fis.read(buffer); len > 0; len = fis.read(buffer)) {
                baos.write(buffer, 0, len);
            }

            fis.close();

            byte[] byteDocument = baos.toByteArray();

            baos.close();

            content.setDocument(new InMemoryDocument(byteDocument, "Business_Content"));
            //----------------------------------------------------------
            f = new File(Path_Attachment);

            baos = new ByteArrayOutputStream();
            fis = new FileInputStream(f);

            for (int len = fis.read(buffer); len > 0; len = fis.read(buffer)) {
                baos.write(buffer, 0, len);
            }

            fis.close();

            byteDocument = baos.toByteArray();
            baos.close();

            ArrayList<DSSDocument> attachments = new ArrayList<DSSDocument>();
            attachments.add(new InMemoryDocument(byteDocument, "Attachment_1"));

            content.setAttachments(attachments);

            return content;
        } catch (Exception e) {
            System.err.println("Exception within the configuration of the business content - Variant 1:");
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(baos);
        }
    }

    // SUB-CONF-13 - Variant 7
    public static BusinessContent get_UnsignedFile_WithoutAttachments() {
        content = new BusinessContent();

        ByteArrayOutputStream baos = null;
        FileInputStream fis = null;

        try {
            File f = new File(Path_UnsignedFile);

            baos = new ByteArrayOutputStream();
            fis = new FileInputStream(f);

            byte[] buffer = new byte[1024];

            for (int len = fis.read(buffer); len > 0; len = fis.read(buffer)) {
                baos.write(buffer, 0, len);
            }

            fis.close();

            byte[] byteDocument = baos.toByteArray();

            content.setDocument(new InMemoryDocument(byteDocument, "Business_Content"));

            return content;
        } catch (Exception e) {
            System.err.println("Exception within the configuration of the business content - Variant 1:");
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(baos);
        }
    }

    // SUB-CONF-13 - Variant 8
    public static BusinessContent get_MultisignedFile_WithoutAttachments() {
        content = new BusinessContent();

        ByteArrayOutputStream baos = null;
        FileInputStream fis = null;

        try {
            File f = new File(Path_MultisignedFile);

            baos = new ByteArrayOutputStream();
            fis = new FileInputStream(f);

            byte[] buffer = new byte[1024];

            for (int len = fis.read(buffer); len > 0; len = fis.read(buffer)) {
                baos.write(buffer, 0, len);
            }

            fis.close();

            byte[] byteDocument = baos.toByteArray();

            content.setDocument(new InMemoryDocument(byteDocument, "Business_Content"));

            return content;
        } catch (Exception e) {
            System.err.println("Exception within the configuration of the business content - Variant 1:");
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(baos);
        }
    }

    // SUB-CONF-13 - Variant 9
    public static BusinessContent get_MultisignedFile_One_Invalid_WithoutAttachments() {
        content = new BusinessContent();

        ByteArrayOutputStream baos = null;
        FileInputStream fis = null;

        try {
            File f = new File(Path_MultisignedFile_One_Invalid);

            baos = new ByteArrayOutputStream();
            fis = new FileInputStream(f);

            byte[] buffer = new byte[1024];

            for (int len = fis.read(buffer); len > 0; len = fis.read(buffer)) {
                baos.write(buffer, 0, len);
            }

            fis.close();

            byte[] byteDocument = baos.toByteArray();

            content.setDocument(new InMemoryDocument(byteDocument, "Business_Content"));

            return content;
        } catch (Exception e) {
            System.err.println("Exception within the configuration of the business content - Variant 1:");
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(baos);
        }
    }
}
