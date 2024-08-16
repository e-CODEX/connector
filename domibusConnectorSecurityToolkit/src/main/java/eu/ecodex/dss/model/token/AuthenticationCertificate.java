/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
