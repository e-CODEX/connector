package eu.domibus.connector.persistence.service.exceptions;

import eu.domibus.connector.domain.model.LargeFileReference;


public class LargeFileDeletionException extends LargeFileException {
    private LargeFileReference referenceFailedToDelete;

    public LargeFileDeletionException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public LargeFileReference getReferenceFailedToDelete() {
        return referenceFailedToDelete;
    }

    public void setReferenceFailedToDelete(LargeFileReference referenceFailedToDelete) {
        this.referenceFailedToDelete = referenceFailedToDelete;
    }
}
