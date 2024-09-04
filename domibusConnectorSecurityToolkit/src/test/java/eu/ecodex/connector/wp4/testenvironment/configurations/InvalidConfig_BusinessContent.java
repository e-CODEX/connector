/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.wp4.testenvironment.configurations;

import eu.ecodex.dss.model.BusinessContent;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import org.apache.commons.io.IOUtils;

/**
 * The InvalidConfig_BusinessContent class provides static methods for creating invalid instances of
 * the BusinessContent class. These invalid instances represent different error scenarios within the
 * configuration of the business content.
 *
 * <p><strong>Additional Information:</strong></p>
 * This class is dependent on the BusinessContent class, which holds the document and its
 * attachments.
 *
 * @see BusinessContent
 */
// SUB_CONF_14
@SuppressWarnings("checkstyle:TypeName")
public class InvalidConfig_BusinessContent {
    private static String Path_Attachment = "src/test/resources/documents/Attachment2.bmp";
    static BusinessContent content;

    // SUB_CONF_14 Variant 1
    // Empty Business Content
    public static BusinessContent get_EmptyContent() {
        content = new BusinessContent();
        return content;
    }

    /**
     * Retrieves the BusinessContent with a missing Business Document. This method is used to create
     * an invalid instance of BusinessContent that represents a business content configuration
     * error.
     *
     * @return the BusinessContent instance with no document or attachments
     */
    // SUB_CONF_14 Variant 2
    // Business Content with missing Business Document
    public static BusinessContent get_MissingBusinessDocument() {
        content = new BusinessContent();

        ByteArrayOutputStream baos = null;
        FileInputStream fis = null;
        File file;

        try {
            file = new File(Path_Attachment);

            baos = new ByteArrayOutputStream();
            fis = new FileInputStream(file);

            var buffer = new byte[1024];

            for (int len = fis.read(buffer); len > 0; len = fis.read(buffer)) {
                baos.write(buffer, 0, len);
            }

            fis.close();

            byte[] byteDocument = baos.toByteArray();
            baos.close();

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
            IOUtils.closeQuietly(baos);
        }
    }

    // SUB_CONF_14 Variant 3
    // Empty Business Content
    public static BusinessContent get_NullContent() {
        return null;
    }
}
