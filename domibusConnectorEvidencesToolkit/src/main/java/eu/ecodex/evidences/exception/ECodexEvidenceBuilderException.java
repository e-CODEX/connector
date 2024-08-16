/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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

