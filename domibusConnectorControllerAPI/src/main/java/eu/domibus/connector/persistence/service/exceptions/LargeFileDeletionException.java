/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
