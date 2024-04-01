/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/model
 * /EnvironmentConfiguration.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.model;

/**
 * This class holds the configuration about the environment - e.g. proxy settings.
 *
 * <p>
 * DISCLAIMER: Project owner e-CODEX
 * </p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
@Deprecated // use spring beans instead!
public class EnvironmentConfiguration {
    /**
     * holds information how to obtain the certificates of (all) the e-CODEX connectors
     */
    private CertificateStoreInfo connectorCertificates;
    /**
     * the proxy data for http connections
     */
    private ProxyData proxyHTTP;
    /**
     * the proxy data for https connections
     */
    private ProxyData proxyHTTPS;

    /**
     * checks if the store information for the e-CODEX connectors certificates is valid
     *
     * @return the validity
     */
    public boolean isConnectorCertificatesValid() {
        return connectorCertificates != null && connectorCertificates.isValid();
    }

    /**
     * information how to obtain the certificates of (all) the e-CODEX connectors
     *
     * @return the value
     */
    public CertificateStoreInfo getConnectorCertificates() {
        return this.connectorCertificates;
    }

    /**
     * information how to obtain the certificates of (all) the e-CODEX connectors
     *
     * @param v the data instance
     * @return this class' instance for chaining
     */
    public EnvironmentConfiguration setConnectorCertificates(final CertificateStoreInfo v) {
        this.connectorCertificates = v;
        return this;
    }

    /**
     * checks if the proxy data is set and valid
     *
     * @return the validity
     */
    public boolean isProxyHTTPValid() {
        return proxyHTTP != null && proxyHTTP.isValid();
    }

    /**
     * the proxy data for http connections
     *
     * @return the data instance
     */
    public ProxyData getProxyHTTP() {
        return proxyHTTP;
    }

    /**
     * the proxy data for http connections
     *
     * @param v the data instance
     * @return this class' instance for chaining
     */
    public EnvironmentConfiguration setProxyHTTP(final ProxyData v) {
        this.proxyHTTP = v;
        return this;
    }

    /**
     * checks if the proxy data is set and valid
     *
     * @return the validity
     */
    public boolean isProxyHTTPSValid() {
        return proxyHTTPS != null && proxyHTTPS.isValid();
    }

    /**
     * the proxy data for https connections
     *
     * @return the data instance
     */
    public ProxyData getProxyHTTPS() {
        return proxyHTTPS;
    }

    /**
     * the proxy data for https connections
     *
     * @param v the data instance
     * @return this class' instance for chaining
     */
    public EnvironmentConfiguration setProxyHTTPS(final ProxyData v) {
        this.proxyHTTPS = v;
        return this;
    }
}
