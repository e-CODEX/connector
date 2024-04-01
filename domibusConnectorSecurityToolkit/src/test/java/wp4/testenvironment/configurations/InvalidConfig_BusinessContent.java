package wp4.testenvironment.configurations;

import eu.ecodex.dss.model.BusinessContent;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;


// SUB_CONF_14
public class InvalidConfig_BusinessContent {
    static BusinessContent content;
    private static final String Path_Attachment = "src/test/resources/documents/Attachment2.bmp";
    // SUB_CONF_14 Variant 1
    // Empty Business Content
    public static BusinessContent get_EmptyContent() {
        content = new BusinessContent();
        return content;
    }

    // SUB_CONF_14 Variant 2
    // Business Content with missing Business Document
    public static BusinessContent get_MissingBusinessDocument() {
        content = new BusinessContent();

        ByteArrayOutputStream baos = null;
        FileInputStream fis = null;
        File f = null;

        try {
            f = new File(Path_Attachment);

            baos = new ByteArrayOutputStream();
            fis = new FileInputStream(f);

            byte[] buffer = new byte[1024];

            for (int len = fis.read(buffer); len > 0; len = fis.read(buffer)) {
                baos.write(buffer, 0, len);
            }

            fis.close();

            byte[] byteDocument = baos.toByteArray();
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

    // SUB_CONF_14 Variant 3
    // Empty Business Content
    public static BusinessContent get_NullContent() {
        return null;
    }
}
