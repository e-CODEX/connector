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
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/model/EnvironmentConfiguration.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.model;

import lombok.Getter;

/**
 * This class holds the configuration about the environment - e.g. proxy settings. * * <p> *
 * DISCLAIMER: Project owner e-CODEX * </p> * * @author <a
 * href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a> * @version
 * $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 *
 * @deprecated Use spring beans instead!
 */
@Getter
@Deprecated // use spring beans instead!
public class EnvironmentConfiguration {
    /**
     * Holds information how to obtain the certificates of (all) the e-CODEX connectors.
     */
    private CertificateStoreInfo connectorCertificates;
    /**
     * The proxy data for http connections.
     */
    private ProxyData proxyHTTP;
    /**
     * The proxy data for https connections.
     */
    private ProxyData proxyHTTPS;

    /**
     * Checks if the store information for the e-CODEX connectors certificates is valid.
     *
     * @return the validity
     */
    public boolean isConnectorCertificatesValid() {
        return connectorCertificates != null && connectorCertificates.isValid();
    }

    /**
     * Information how to obtain the certificates of (all) the e-CODEX connectors.
     *
     * @param v the data instance
     * @return this class' instance for chaining
     */
    public EnvironmentConfiguration setConnectorCertificates(final CertificateStoreInfo v) {
        this.connectorCertificates = v;
        return this;
    }

    /**
     * Checks if the proxy data is set and valid.
     *
     * @return the validity
     */
    public boolean isProxyHTTPValid() {
        return proxyHTTP != null && proxyHTTP.isValid();
    }

    /**
     * The proxy data for http connections.
     *
     * @param v the data instance
     * @return this class' instance for chaining
     */
    public EnvironmentConfiguration setProxyHTTP(final ProxyData v) {
        this.proxyHTTP = v;
        return this;
    }

    /**
     * Checks if the proxy data is set and valid.
     *
     * @return the validity
     */
    public boolean isProxyHTTPSValid() {
        return proxyHTTPS != null && proxyHTTPS.isValid();
    }

    /**
     * The proxy data for https connections.
     *
     * @param v the data instance
     * @return this class' instance for chaining
     */
    public EnvironmentConfiguration setProxyHTTPS(final ProxyData v) {
        this.proxyHTTPS = v;
        return this;
    }
}
