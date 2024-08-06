/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.ecodex.evidences.exception;

/**
 * ECodexEvidenceBuilderException is a custom exception class that extends the Exception class.
 * It is used to handle exceptions that occur in the ECodexEvidenceBuilder class.
 */
@SuppressWarnings("squid:S1135")
public class ECodexEvidenceBuilderException extends Exception {
    private static final long serialVersionUID = -8972538454498818077L;

    public ECodexEvidenceBuilderException() {
        // TODO Auto-generated constructor stub
    }

    public ECodexEvidenceBuilderException(String arg0, Throwable arg1) {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
    }

    public ECodexEvidenceBuilderException(String arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    public ECodexEvidenceBuilderException(Throwable arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }
}

