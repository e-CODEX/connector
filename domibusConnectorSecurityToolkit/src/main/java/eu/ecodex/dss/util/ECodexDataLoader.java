package eu.ecodex.dss.util;

import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.client.http.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;


@Deprecated // has become part of DSS Utils
public class ECodexDataLoader extends CommonsDataLoader {
    private static final Logger LOG = LoggerFactory.getLogger(ECodexDataLoader.class);

    private boolean allowLDAP = true;

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
                LOG.warn("LDAP has been disabled by configuration. Checks against LDAP-CRL will fail by default!");
                LOG.warn("Cannot download CRL from: " + urlString);
                return null; // throw new DSSException("Cannot download CRL from: " + urlString);
            }
        } else {
            LOG.warn("DSS framework only supports HTTP, HTTPS, FTP and LDAP CRL's urlString.");
            LOG.warn("Cannot download CRL from: " + urlString);
            return null; // throw new DSSException("Cannot download CRL from: " + urlString);
        }
    }

    /**
     * This method retrieves data using LDAP protocol.
     * - CRL from given LDAP url, e.g. ldap://ldap.infonotary.com/dc=identity-ca,dc=infonotary,dc=com
     *
     * @param urlString
     * @return
     */
    public byte[] ldapGet(final String urlString) {
        final Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, urlString);
        try {

            final DirContext ctx = new InitialDirContext(env);
            final Attributes attributes = ctx.getAttributes("");
            final javax.naming.directory.Attribute attribute = attributes.get("certificateRevocationList;binary");
            final byte[] ldapBytes = (byte[]) attribute.get();
            if (ldapBytes == null || ldapBytes.length == 0) {
                throw new DSSException("Cannot download CRL from: " + urlString);
            }
            return ldapBytes;
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
        }
        return null;
    }

    protected byte[] fileGet(String urlString) {
        try {
            return DSSUtils.toByteArray(new URL(urlString).openStream());
        } catch (IOException e) {
            LOG.warn(e.toString(), e);
        }
        return null;
    }

    public boolean isAllowLDAP() {
        return allowLDAP;
    }

    public void setAllowLDAP(boolean allowLDAP) {
        this.allowLDAP = allowLDAP;
    }
}
