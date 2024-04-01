/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/model/ProxyData.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */
package eu.ecodex.dss.model;

import org.apache.commons.lang.StringUtils;


/**
 * This class holds attributes regarding a to be used proxy.
 *
 * <p>
 * DISCLAIMER: Project owner e-CODEX
 * </p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
public class ProxyData {
    /**
     * the host
     */
    private String host;
    /**
     * the port
     */
    private int port;
    /**
     * the user's name to authenticate against the proxy
     */
    private String authName;
    /**
     * the user's pass to authenticate against the proxy
     */
    private String authPass;

    /**
     * the host
     *
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * the host
     *
     * @param v the value
     * @return this class' instance for chaining
     */
    public ProxyData setHost(final String v) {
        this.host = v;
        return this;
    }

    /**
     * the port
     *
     * @return the port, should be ignored if less than 1
     */
    public int getPort() {
        return port;
    }

    /**
     * the port
     *
     * @param v the value, if less than 1 shall be ignored
     * @return this class' instance for chaining
     */
    public ProxyData setPort(final int v) {
        this.port = v;
        return this;
    }

    /**
     * checks if the data is valid. that is: host is not empty and port greater than 0
     *
     * @return the validity of the data
     */
    public boolean isValid() {
        return !StringUtils.isEmpty(host) && port > 0;
    }

    /**
     * the user's name to authenticate against the proxy
     *
     * @return the value
     */
    public String getAuthName() {
        return authName;
    }

    /**
     * the user's name to authenticate against the proxy
     *
     * @param v the value
     * @return this class' instance for chaining
     */
    public ProxyData setAuthName(final String v) {
        this.authName = v;
        return this;
    }

    /**
     * the user's password to authenticate against the proxy
     *
     * @return the value
     */
    public String getAuthPass() {
        return authPass;
    }

    /**
     * the user's password to authenticate against the proxy
     *
     * @param v the value
     * @return this class' instance for chaining
     */
    public ProxyData setAuthPass(final String v) {
        this.authPass = v;
        return this;
    }

    /**
     * checks if authentication data is provided. that is: authName and authPass are not empty
     *
     * @return the authentication state of the data
     */
    public boolean hasAuth() {
        return !StringUtils.isEmpty(authName) && !StringUtils.isEmpty(authPass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "ProxyData{" +
                "host=" + (host == null ? null : ('\'' + host + '\'')) +
                ", port=" + port +
                ", authName=" + (authName == null ? null : ('\'' + authName + '\'')) +
                ", authPass=" + (authPass == null ? null : ('\'' + authPass + '\'')) +
                '}';
    }
}
