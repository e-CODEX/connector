/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/model/CertificateStoreInfo.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.model;

import lombok.Data;
import org.springframework.core.io.Resource;

/**
 * Holds information how to access the store holding the e-CODEX connector certificates used for
 * ASiC-S validation.
 *
 * <p>DISCLAIMER: Project owner e-CODEX
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * @deprecated Use certificate source instead.
 */
@SuppressWarnings("squid:S1135")
@Deprecated // TODO replace with certificate source...
@Data
public class CertificateStoreInfo {
    private Resource location;
    private String password;

    /**
     * The info is valid, if a non-empty location is provided.
     *
     * @return the result
     */
    public boolean isValid() {
        return location.isReadable();
    }

    /**
     * The location (in url format) for loading the keystore.
     *
     * @param v the value
     * @return this class' instance for chaining
     */
    public CertificateStoreInfo setLocation(final Resource v) {
        this.location = v;
        return this;
    }

    /**
     * Sets the password for loading the keystore.
     *
     * @param password the value
     * @return this class' instance for chaining
     */
    public CertificateStoreInfo setPassword(final String password) {
        this.password = password;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "CertificateStoreInfo{"
            + "location=" + (location == null ? null : ('\'' + location.toString() + '\''))
            + ", password=" + (password == null ? null : "[set]")
            + '}';
    }
}
