/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/service/impl/dss/ConnectorCertificatesStore.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.service.impl.dss;

import eu.ecodex.dss.model.CertificateStoreInfo;
import eu.ecodex.dss.util.LogDelegate;
import java.math.BigInteger;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;

/**
 * Extracts all {@link X509Certificate}s from the keystore, caches them and provides functionality
 * to check if a certificate is contained. The initialise keystore is NOT automatically inspected,
 * but via {@link #lookup}.
 *
 * <p>DISCLAIMER: Project owner e-CODEX
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * @see CertificateStoreInfo
 * @deprecated Use DSS certificate source instead
 */
@NoArgsConstructor
@Deprecated // replace with DSS certificate source
public class ConnectorCertificatesStore {
    private static final LogDelegate LOG = new LogDelegate(ConnectorCertificatesStore.class);
    private Map<BigInteger, byte[]> lookup = null;

    /**
     * Represents a store for connector certificates used for ASiC-S validation. The store is
     * updated by loading a keystore from the provided {@link CertificateStoreInfo} object. The
     * certificates are extracted from the keystore and stored in an internal map. Duplicate
     * certificates are ignored.
     *
     * @param info the information about the keystore. If null or invalid, the update operation is
     *             ignored. The location must provide a loadable keystore or an exception will be
     *             thrown.
     * @see CertificateStoreInfo
     */
    public ConnectorCertificatesStore(final CertificateStoreInfo info) {
        try {
            this.update(info);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * will load the keystore from the provided info, creating a new cache and extracting its X509
     * certificates. note that {@link #isValid(java.security.cert.X509Certificate)} will always
     * report true, if nothing has been loaded. duplicate certificates are ignored, that is if the
     * serial number has already been stored internally.
     *
     * @param info if null or invalid, ignored. otherwise the location must provide a loadable
     *             keystore or an exception will be thrown
     * @return the number of cached certificates or -1 in case no feasible info was provided
     * @throws Exception as of the underlying libraries (e.g. loading the keystore)
     */
    public synchronized int update(final CertificateStoreInfo info) throws Exception {
        // set the map to null, so that we have a state that signals: the keystore has not
        // been loaded
        lookup = null;

        // check if configuration is feasible
        if (info == null) {
            LOG.lWarn(
                "no information about the keystore provided - all further validations "
                    + "will be positive");
            return -1;
        }
        if (!info.isValid()) {
            LOG.lWarn(
                "the information about the keystore is invalid - all further validations "
                    + "will be positive");
            return -1;
        }

        // load a JKS keystore from the location-url
        final Resource ksLocation = info.getLocation();
        LOG.lDetail("loading keystore from resource: {}", ksLocation);
        final var keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        final var keyStoreStream = ksLocation.getInputStream();
        try {
            keyStore.load(
                keyStoreStream,
                (info.getPassword() == null) ? null : info.getPassword().toCharArray()
            );
        } finally {
            IOUtils.closeQuietly(keyStoreStream);
        }

        // create the lookup map and store the X509Certificates
        lookup = new HashMap<>();

        final ArrayList<String> aliases = Collections.list(keyStore.aliases());
        LOG.lDetail("found {} aliases: {}", aliases.size(), aliases);

        for (final String alias : aliases) {
            if (!keyStore.isCertificateEntry(alias)) {
                LOG.lDetail("ignored alias {} not representing a certificate entry", alias);
                continue;
            }

            final var certificate = keyStore.getCertificate(alias);
            if (!(certificate instanceof X509Certificate)) {
                LOG.lWarn("ignored alias {} not representing an X509 certificate", alias);
                continue;
            }

            final var x509Certificate = (X509Certificate) certificate;
            final BigInteger cSN = x509Certificate.getSerialNumber();
            if (cSN == null) {
                LOG.lWarn("alias {} has no serial number - ignored", alias);
                continue;
            }
            final byte[] cEnc = x509Certificate.getEncoded();
            if (cEnc == null) {
                LOG.lWarn("alias {} has no ASN.1 DER bytes - ignored", alias);
                continue;
            }

            if (lookup.get(cSN) != null) {
                LOG.lWarn("alias {} serial number {} already stored - ignored", alias, cSN);
                continue;
            }

            lookup.put(cSN, cEnc);
            LOG.lDetail("added alias {} with serial number {}", aliases, cSN);
        }

        LOG.lDetail("cached {} X509 certificates", lookup.size());

        if (lookup.isEmpty()) {
            LOG.lDetail(
                "no usable certificate was found in the keystore - all further "
                    + "validations will be negative! (keystore url: {})",
                ksLocation
            );
        }

        return lookup.size();
    }

    /**
     * checks if the cert is one of the certificates of the connectors. that is implementation-wise,
     * if the certificate's serial number is in the internal map and the ASN.1 DER byte arrays are
     * the same. note that the method will also return true, if the keystore has NOT been loaded.
     *
     * @param cert the to be checked certificate
     * @return true if the cert is found in the keystore or the keystore was not loaded
     * @throws Exception as of underlying classes
     */
    public boolean isValid(final X509Certificate cert) throws Exception {
        if (cert == null) {
            LOG.lWarn("isValid called with null certificate - will return false");
            return false;
        }
        if (lookup == null) {
            LOG.lWarn("no keystore has been loaded - certificate in parameter is deemed valid");
            return true;
        }

        final BigInteger cSN = cert.getSerialNumber();
        final byte[] storeDER = lookup.get(cSN);
        if (storeDER == null) {
            LOG.lDetail(
                "no certificate found in keystore for serial number {} - will return false", cSN);
            return false;
        }
        final byte[] certDER = cert.getEncoded();
        if (certDER == null) {
            LOG.lWarn("certificate in parameter has no ASN.1 DER bytes - will return false");
            return false;
        }

        if (!Arrays.equals(storeDER, certDER)) {
            LOG.lWarn(
                "certificate in parameter conflicts with corresponding keystore "
                    + "certificate (ASN.1 DER bytes not equal) - will return false");
            return false;
        }

        LOG.lDetail("certificate found in keystore for serial number {}", cSN);
        return true;
    }
}
