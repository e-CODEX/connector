/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.wp4.testenvironment.configurations;

import eu.europa.esig.dss.service.crl.OnlineCRLSource;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.service.http.proxy.ProxyProperties;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.x509.KeyStoreCertificateSource;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;
import org.springframework.core.io.ClassPathResource;

/**
 * The ValidConfig_CertificateVerifier class provides static methods to obtain instances of the
 * CommonCertificateVerifier class with different configurations.
 *
 * <p>The ValidConfig_CertificateVerifier class does not have any instance variables or non-static
 * methods.
 */
// SUB_CONF_17
@SuppressWarnings("checkstyle:TypeName")
public class ValidConfig_CertificateVerifier {
    private static CommonCertificateVerifier verifierVariant1 = null;
    private static CommonCertificateVerifier verifierVariant2 = null;

    /**
     * Retrieves a {@link CommonCertificateVerifier} object with proxy configuration.
     *
     * <p>This method returns a {@link CommonCertificateVerifier} object initialized with a valid
     * configuration for certificate verification using proxy. The proxy settings are read from the
     * "src/test/resources/configuration.properties" file. The method sets up the proxy properties
     * for both HTTP and HTTPS connections, sets up the proxy configuration, and initializes the
     * certificate sources, CRL source, and OCSP source.
     *
     * @return the {@link CommonCertificateVerifier} object with proxy configuration
     * @throws IOException if an I/O error occurs when reading the configuration file
     */
    // Common Certificate Verifier
    // SUB_CONF_17 Variant 1
    public static CommonCertificateVerifier get_WithProxy() throws IOException {
        if (verifierVariant1 == null) {
            Properties props = new Properties();
            try {
                Reader reader = new FileReader("src/test/resources/configuration.properties");
                props.load(reader);
                reader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            var httpProxyProperties = new ProxyProperties();
            httpProxyProperties.setHost(props.getProperty("proxy.http.host"));
            httpProxyProperties.setPort(Integer.parseInt(props.getProperty("proxy.http.port")));
            httpProxyProperties.setUser(props.getProperty("proxy.http.user"));
            httpProxyProperties.setPassword(props.getProperty("proxy.http.password").toCharArray());

            var httpsProxyProperties = new ProxyProperties();
            httpsProxyProperties.setHost(props.getProperty("proxy.https.host"));
            httpsProxyProperties.setPort(Integer.parseInt(props.getProperty("proxy.https.port")));
            httpsProxyProperties.setUser(props.getProperty("proxy.https.user"));
            httpsProxyProperties.setPassword(
                props.getProperty("proxy.https.password").toCharArray()
            );

            var proxyPreferenceManager = new ProxyConfig();
            proxyPreferenceManager.setHttpProperties(httpProxyProperties);
            proxyPreferenceManager.setHttpsProperties(httpsProxyProperties);

            var normalLoader = new CommonsDataLoader();
            normalLoader.setProxyConfig(proxyPreferenceManager);

            verifierVariant1 = new CommonCertificateVerifier();

            var crlSource = new OnlineCRLSource();

            var keyStoreCertificateSource = new KeyStoreCertificateSource(
                new File("src/test/resources/keystores/signature_store.jks"), "JKS", "teststore");
            crlSource.setDataLoader(normalLoader);

            var ocspDataLoader = new CommonsDataLoader();
            ocspDataLoader.setProxyConfig(proxyPreferenceManager);
            ocspDataLoader.setContentType("application/ocsp-request");

            var ocspSource = new OnlineOCSPSource();
            ocspSource.setDataLoader(ocspDataLoader);

            var certSource = new TrustedListsCertificateSource();
            verifierVariant1.setTrustedCertSources(certSource);
            verifierVariant1.setCrlSource(crlSource);
            verifierVariant1.setOcspSource(ocspSource);
        }

        return verifierVariant1;
    }

    /**
     * Retrieves a {@link CommonCertificateVerifier} object without proxy configuration.
     *
     * <p>This method returns a {@link CommonCertificateVerifier} object that is initialized with a
     * valid configuration for certificate verification without using a proxy. The method sets up
     * the certificate sources, CRL source, and OCSP source for the verifier object.
     *
     * @return the {@link CommonCertificateVerifier} object without proxy configuration
     * @throws IOException if an I/O error occurs
     */
    // Common Certificate Verifier - No Proxy
    // SUB_CONF_17 Variant 2
    public static CommonCertificateVerifier get_NoProxy() throws IOException {
        if (verifierVariant2 == null) {
            verifierVariant2 = new CommonCertificateVerifier();

            var normalLoader = new CommonsDataLoader();

            var crlSource = new OnlineCRLSource();
            var ocspSource = new OnlineOCSPSource();

            var keyStoreCertificateSource = new KeyStoreCertificateSource(
                new ClassPathResource("/keystores/keystore.jks").getInputStream(), "JKS",
                "test123"
            );

            crlSource.setDataLoader(normalLoader);

            CommonsDataLoader ocspDataLoader = new CommonsDataLoader();
            ocspDataLoader.setContentType("application/ocsp-request");

            ocspSource.setDataLoader(ocspDataLoader);

            var certSource = new TrustedListsCertificateSource();
            verifierVariant2.setTrustedCertSources(certSource);
            verifierVariant2.setCrlSource(crlSource);
            verifierVariant2.setOcspSource(ocspSource);
        }

        return verifierVariant2;
    }
}
