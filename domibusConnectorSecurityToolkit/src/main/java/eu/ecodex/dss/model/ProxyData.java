/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

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

import lombok.Getter;
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
@Getter
public class ProxyData {
    /**
     * The host.
     */
    private String host;
    /**
     * The port.
     */
    private int port;
    /**
     * The user's name to authenticate against the proxy.
     */
    private String authName;
    /**
     * The user's pass to authenticate against the proxy.
     */
    private String authPass;

    /**
     * The host.
     *
     * @param v the value
     * @return this class' instance for chaining
     */
    public ProxyData setHost(final String v) {
        this.host = v;
        return this;
    }

    /**
     * Sets the port of the ProxyData object.
     *
     * @param port the value, if less than 1 shall be ignored
     * @return this class' instance for chaining
     */
    public ProxyData setPort(final int port) {
        this.port = port;
        return this;
    }

    /**
     * Checks if the data is valid. that is: host is not empty and port greater than 0.
     *
     * @return the validity of the data.
     */
    public boolean isValid() {
        return !StringUtils.isEmpty(host) && port > 0;
    }

    /**
     * The user's name to authenticate against the proxy.
     *
     * @param authName the value
     * @return this class' instance for chaining
     */
    public ProxyData setAuthName(final String authName) {
        this.authName = authName;
        return this;
    }

    /**
     * The user's password to authenticate against the proxy.
     *
     * @param authPass the value
     * @return this class' instance for chaining
     */
    public ProxyData setAuthPass(final String authPass) {
        this.authPass = authPass;
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
        return "ProxyData{"
            + "host=" + (host == null ? null : ('\'' + host + '\''))
            + ", port=" + port
            + ", authName=" + (authName == null ? null : ('\'' + authName + '\''))
            + ", authPass=" + (authPass == null ? null : ('\'' + authPass + '\''))
            + '}';
    }
}
