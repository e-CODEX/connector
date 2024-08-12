/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */


package eu.ecodex.dss.model.token;

import lombok.Data;

/**
 * This class holds information about the validation result for a certificate used for an
 * authentication-based system.
 *
 * <p>DISCLAIMER: Project owner e-CODEX
 *
 * @author e-CODEX WP4 - klara
 */
@Data
public class AuthenticationCertificate {
    protected boolean validationSuccessful;

    public AuthenticationCertificate() {
        this.validationSuccessful = false;
    }
}
