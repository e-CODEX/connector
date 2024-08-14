/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The WebMessageFile class represents a file attached to a web message.
 */
@Data
@NoArgsConstructor
@SuppressWarnings("squid:S1135")
public class WebMessageFile {
    private String fileName;
    private WebMessageFileType fileType;
    private byte[] fileContent;

    /**
     * Constructs a new WebMessageFile object with the given file name, file type, and file
     * content.
     *
     * @param fileName    the name of the file
     * @param fileType    the type of the file, chosen from the available WebMessageFileType
     *                    options
     * @param fileContent the content of the file as a byte array
     */
    public WebMessageFile(String fileName, WebMessageFileType fileType, byte[] fileContent) {
        super();
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileContent = fileContent;
    }
}
