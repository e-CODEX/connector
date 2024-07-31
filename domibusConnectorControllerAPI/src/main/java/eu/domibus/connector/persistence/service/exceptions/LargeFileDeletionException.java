/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.service.exceptions;

import eu.domibus.connector.domain.model.LargeFileReference;
import lombok.Getter;
import lombok.Setter;

/**
 * Exception class that represents an error that occurs while deleting a large file.
 */
public class LargeFileDeletionException extends LargeFileException {

    @Getter
    @Setter
    private LargeFileReference referenceFailedToDelete;

    public LargeFileDeletionException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }
}
