/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.ecodex.dss.util;

import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.client.http.Protocol;
import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.directory.InitialDirContext;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ECodexDataLoader class is a deprecated class that extends the CommonsDataLoader class. It is
 * responsible for loading data from different protocols such as file, HTTP, FTP, and LDAP. The
 * class provides a method, get, which takes a URL as input and returns the loaded data as a byte
 * array.
 *
 * @deprecated This class has become part of DSS Utils library. It is recommended to use the
 *      DataLoader class or other appropriate classes from DSS Utils library for data loading
 *      operations.
 */
@Data
@Deprecated // has become part of DSS Utils
public class ECodexDataLoader extends CommonsDataLoader {
    private boolean allowLDAP = true;
    private static final Logger LOG = LoggerFactory.getLogger(ECodexDataLoader.class);

    @Override
    public byte[] get(final String urlString) throws DSSException {
        if (Protocol.isFileUrl(urlString)) {
            return fileGet(urlString);
        } else if (Protocol.isHttpUrl(urlString)) {
            return httpGet(urlString);
        } else if (Protocol.isFtpUrl(urlString)) {
            return ftpGet(urlString);
        } else if (Protocol.isLdapUrl(urlString)) {
            if (allowLDAP) {
                return ldapGet(urlString);
            } else {
                LOG.warn(
                    "LDAP has been disabled by configuration. Checks against LDAP-CRL will fail by"
                        + " default!"
                );
                LOG.warn("Cannot download CRL from: {}", urlString);
                return new byte[0];
            }
        } else {
            LOG.warn("DSS framework only supports HTTP, HTTPS, FTP and LDAP CRL's urlString.");
            LOG.warn("Cannot download CRL from: {}", urlString);
            return new byte[0];
        }
    }

    @Override
    protected byte[] fileGet(String urlString) {
        try {
            return DSSUtils.toByteArray(new URL(urlString).openStream());
        } catch (IOException e) {
            LOG.warn(e.toString(), e);
        }
        return new byte[0];
    }

    /**
     * This method retrieves data using LDAP protocol. - CRL from given LDAP url, e.g.
     * ldap://ldap.infonotary.com/dc=identity-ca,dc=infonotary,dc=com
     *
     * @param urlString the URL of the LDAP server.
     * @return the byte array retrieved from the LDAP server, or null if an error occurred.
     */
    @Override
    public byte[] ldapGet(final String urlString) {
        final var env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, urlString);
        try {

            final var ctx = new InitialDirContext(env);
            final var attributes = ctx.getAttributes("");
            final var attribute = attributes.get("certificateRevocationList;binary");
            final byte[] ldapBytes = (byte[]) attribute.get();
            if (ldapBytes == null || ldapBytes.length == 0) {
                throw new DSSException("Cannot download CRL from: " + urlString);
            }
            return ldapBytes;
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
        }
        return new byte[0];
    }
}
