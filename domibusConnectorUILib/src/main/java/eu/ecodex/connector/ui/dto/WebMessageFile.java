/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.dto;

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
