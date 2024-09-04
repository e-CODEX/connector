/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.dss.configuration;

import eu.ecodex.connector.tools.logging.LoggingMarker;
import eu.europa.esig.dss.service.NonceSource;
import eu.europa.esig.dss.service.SecureRandomNonceSource;
import eu.europa.esig.dss.service.crl.OnlineCRLSource;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.service.http.commons.OCSPDataLoader;
import eu.europa.esig.dss.service.http.commons.TimestampDataLoader;
import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.service.http.proxy.ProxyProperties;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.service.tsp.OnlineTSPSource;
import eu.europa.esig.dss.spi.client.http.DataLoader;
import eu.europa.esig.dss.spi.x509.tsp.CompositeTSPSource;
import eu.europa.esig.dss.spi.x509.tsp.TSPSource;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * Configuration class for BasicDss.
 */
@Configuration
@EnableConfigurationProperties(BasicDssConfigurationProperties.class)
public class BasicDssConfiguration {
    private static final Logger LOGGER = LogManager.getLogger(BasicDssConfiguration.class);
    public static final String PROXY_CONFIG_BEAN_NAME = "domibusConnectorProxyConfig";
    public static final String DEFAULT_DATALOADER_BEAN_NAME = "defaultDataLoader";
    public static final String DEFAULT_OCSP_SOURCE_BEAN_NAME = "defaultOcspLoader";
    public static final String DEFAULT_CRL_SOURCE_BEAN_NAME = "defaultCrlLoader";
    public static final String DEFAULT_TIMESTAMPE_SOURCE_BEAN_NAME =
        "defaultCompositeTimestampSource";

    /**
     * Returns the {@code ProxyConfig} object for the {@code domibusConnectorProxyConfig} method.
     *
     * @param basicDssConfigurationProperties the basic DSS configuration properties object
     * @return the configured {@code ProxyConfig} object
     */
    @Bean(name = PROXY_CONFIG_BEAN_NAME)
    public ProxyConfig domibusConnectorProxyConfig(
        BasicDssConfigurationProperties basicDssConfigurationProperties
    ) {
        var proxyConfig = new ProxyConfig();

        // HTTPS Proxy
        if (basicDssConfigurationProperties.getHttpsProxy() != null) {
            proxyConfig.setHttpsProperties(basicDssConfigurationProperties.getHttpsProxy());
            LOGGER.info(
                LoggingMarker.Log4jMarker.CONFIG,
                "Setting DSS https proxy config [{}] from {}.https-proxy.*",
                basicDssConfigurationProperties.getHttpsProxy(),
                BasicDssConfigurationProperties.PREFIX
            );
        } else if (StringUtils.hasText(System.getProperty("https.proxyHost"))) {
            var httpsProxy = new ProxyProperties();
            httpsProxy.setHost(System.getProperty("https.proxyHost"));
            String port = System.getProperty("https.proxyPort");
            try {
                httpsProxy.setPort(Integer.parseInt(port));
                LOGGER.info(
                    LoggingMarker.Log4jMarker.CONFIG,
                    "Setting DSS https proxy config [{}] from SystemProperties", httpsProxy
                );
                proxyConfig.setHttpProperties(httpsProxy);
            } catch (NumberFormatException nfe) {
                // do nothing.
            }
        } else if (StringUtils.hasText(System.getenv("HTTPS_PROXY"))) {
            var envVariable = "HTTPS_PROXY";

            var proxyProperties = getProxyPropertiesFromSystemEnv(envVariable);

            if (proxyProperties != null) {
                proxyConfig.setHttpsProperties(proxyProperties);
                LOGGER.info(
                    LoggingMarker.Log4jMarker.CONFIG,
                    "Setting DSS https proxy config [{}] from Environment variable [{}]",
                    proxyProperties, envVariable
                );
            }
        } else {
            LOGGER.info(
                LoggingMarker.Log4jMarker.CONFIG,
                "Setting DSS https proxy to nothing. No proxy configured!"
            );
        }

        // HTTP Proxy
        if (basicDssConfigurationProperties.getHttpProxy() != null) {
            proxyConfig.setHttpProperties(basicDssConfigurationProperties.getHttpProxy());
            LOGGER.info(
                LoggingMarker.Log4jMarker.CONFIG,
                "Setting DSS http proxy config [{}] from {}.http-proxy.*",
                basicDssConfigurationProperties.getHttpsProxy(),
                BasicDssConfigurationProperties.PREFIX
            );
        } else if (StringUtils.hasText(System.getProperty("http.proxyHost"))) {
            var httpProxy = new ProxyProperties();
            httpProxy.setHost(System.getProperty("http.proxyHost"));
            String port = System.getProperty("http.proxyPort");
            try {
                httpProxy.setPort(Integer.parseInt(port));
                LOGGER.info(
                    LoggingMarker.Log4jMarker.CONFIG,
                    "Setting DSS http proxy config [{}] from SystemProperties", httpProxy
                );
                proxyConfig.setHttpProperties(httpProxy);
            } catch (NumberFormatException nfe) {
                // do nothing..
            }
        } else if (StringUtils.hasText(System.getenv("HTTP_PROXY"))) {
            var envVariable = "HTTP_PROXY";

            var proxyProperties = getProxyPropertiesFromSystemEnv(envVariable);

            if (proxyProperties != null) {
                proxyConfig.setHttpProperties(proxyProperties);
                LOGGER.info(
                    LoggingMarker.Log4jMarker.CONFIG,
                    "Setting DSS http proxy config [{}] from Environment variable [{}]",
                    proxyProperties, envVariable
                );
            }
        } else {
            LOGGER.info(
                LoggingMarker.Log4jMarker.CONFIG,
                "Setting DSS http proxy to nothing. No proxy configured!"
            );
        }

        return proxyConfig;
    }

    private ProxyProperties getProxyPropertiesFromSystemEnv(String envVariable) {
        String value = System.getenv(envVariable);

        ProxyProperties proxyProperties = null;

        var compile = Pattern.compile("(?:http://|https://)([\\w.]+):(\\d+)");
        var matcher = compile.matcher(value);
        if (matcher.matches() && matcher.groupCount() == 2) {

            proxyProperties = new ProxyProperties();
            String proxyHost = matcher.group(1);
            String proxyPort = matcher.group(2);
            try {
                proxyProperties.setPort(Integer.parseInt(proxyPort));
            } catch (NumberFormatException nfe) {
                // do nothing.
            }
            proxyProperties.setHost(proxyHost);
        }
        return proxyProperties;
    }

    /**
     * Returns the default DataLoader instance using the provided proxy configuration.
     *
     * @param proxyConfig the ProxyConfig object to be set on the DataLoader
     * @return the default DataLoader instance with the provided proxy configuration
     */
    @Bean(name = DEFAULT_DATALOADER_BEAN_NAME)
    public DataLoader defaultDataLoader(ProxyConfig proxyConfig) {
        var commonsDataLoader = new CommonsDataLoader();
        commonsDataLoader.setProxyConfig(proxyConfig);
        return commonsDataLoader;
    }

    /**
     * Returns the default NonceSource instance.
     *
     * @return the default NonceSource instance
     */
    @Bean
    public NonceSource defaultNonceSource() {
        return new SecureRandomNonceSource();
    }

    /**
     * Returns the default OnlineOCSPSource instance with the provided ProxyConfig and NonceSource.
     *
     * @param proxyConfig  the ProxyConfig object to be set on the OCSPDataLoader
     * @param nonceSource  the NonceSource object to be set on the OnlineOCSPSource
     * @return the default OnlineOCSPSource instance with the provided ProxyConfig and NonceSource
     */
    @Bean(name = DEFAULT_OCSP_SOURCE_BEAN_NAME)
    public OnlineOCSPSource defaultOcspOnlineSource(
        ProxyConfig proxyConfig, NonceSource nonceSource) {
        var ocspDataLoader = new OCSPDataLoader();
        ocspDataLoader.setProxyConfig(proxyConfig);

        var onlineOCSPSource = new OnlineOCSPSource();
        onlineOCSPSource.setDataLoader(ocspDataLoader);
        onlineOCSPSource.setNonceSource(nonceSource);

        return onlineOCSPSource;
    }

    /**
     * Returns the default OnlineCRLSource instance using the provided DataLoader.
     *
     * @param dataLoader the DataLoader object to be set on the OnlineCRLSource
     * @return the default OnlineCRLSource instance with the provided DataLoader
     */
    @Bean(name = DEFAULT_CRL_SOURCE_BEAN_NAME)
    public OnlineCRLSource defaultOnlineCrlSource(DataLoader dataLoader) {
        var onlineCRLSource = new OnlineCRLSource();
        onlineCRLSource.setDataLoader(dataLoader);
        return onlineCRLSource;
    }

    @Bean(name = DEFAULT_TIMESTAMPE_SOURCE_BEAN_NAME)
    CompositeTSPSource compositeTSPSource(
        ProxyConfig proxyConfig, NonceSource nonceSource,
        BasicDssConfigurationProperties basicDssConfigurationProperties) {
        var timestampDataLoader = new TimestampDataLoader();
        timestampDataLoader.setProxyConfig(proxyConfig);

        Map<String, BasicDssConfigurationProperties.Tsp> timeStampServers =
            basicDssConfigurationProperties.getTimeStampServers();

        Map<String, TSPSource> collect = timeStampServers
            .entrySet()
            .stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                    String policyOid = entry.getValue().getPolicyOid();
                    var onlineTSPSource = new OnlineTSPSource();
                    onlineTSPSource.setNonceSource(nonceSource);
                    onlineTSPSource.setDataLoader(timestampDataLoader);
                    if (StringUtils.hasText(policyOid)) {
                        onlineTSPSource.setPolicyOid(policyOid);
                    }
                    var url = entry.getValue().getUrl();
                    if (StringUtils.hasText(url)) {
                        onlineTSPSource.setTspServer(url);
                    } else {
                        throw new IllegalArgumentException("Illegal tsp url!");
                    }
                    LOGGER.info(
                        LoggingMarker.Log4jMarker.CONFIG,
                        "Adding TimeStampServer with key [{}] url [{}] and policyOid [{}] "
                            + "to TSP Sources",
                        entry.getKey(), url, policyOid
                    );
                    return onlineTSPSource;
                }
            ));

        var compositeTSPSource = new CompositeTSPSource();
        compositeTSPSource.setTspSources(collect);

        return compositeTSPSource;
    }
}
