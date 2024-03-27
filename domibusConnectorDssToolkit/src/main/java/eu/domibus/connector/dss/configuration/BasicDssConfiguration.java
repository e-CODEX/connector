package eu.domibus.connector.dss.configuration;

import eu.domibus.connector.tools.logging.LoggingMarker;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Configuration
@EnableConfigurationProperties(BasicDssConfigurationProperties.class)
public class BasicDssConfiguration {
    public static final String PROXY_CONFIG_BEAN_NAME = "domibusConnectorProxyConfig";
    public static final String DEFAULT_DATALOADER_BEAN_NAME = "defaultDataLoader";
    public static final String DEFAULT_OCSP_SOURCE_BEAN_NAME = "defaultOcspLoader";
    public static final String DEFAULT_CRL_SOURCE_BEAN_NAME = "defaultCrlLoader";
    public static final String DEFAULT_TIMESTAMPE_SOURCE_BEAN_NAME = "defaultCompositeTimestampSource";
    private static final Logger LOGGER = LogManager.getLogger(BasicDssConfiguration.class);

    @Bean(name = PROXY_CONFIG_BEAN_NAME)
    public ProxyConfig domibusConnectorProxyConfig(
            BasicDssConfigurationProperties basicDssConfigurationProperties) {
        ProxyConfig proxyConfig = new ProxyConfig();
        // HTTPS Proxy
        if (basicDssConfigurationProperties.getHttpsProxy() != null) {
            proxyConfig.setHttpsProperties(basicDssConfigurationProperties.getHttpsProxy());
            LOGGER.info(
                    LoggingMarker.Log4jMarker.CONFIG,
                    "Setting DSS https proxy config [{}] from {}.https-proxy.*",
                    basicDssConfigurationProperties.getHttpsProxy(), BasicDssConfigurationProperties.PREFIX
            );
        } else if (StringUtils.hasText(System.getProperty("https.proxyHost"))) {
            ProxyProperties httpsProxy = new ProxyProperties();
            httpsProxy.setHost(System.getProperty("https.proxyHost"));
            String port = System.getProperty("https.proxyPort");
            try {
                httpsProxy.setPort(Integer.parseInt(port));
                LOGGER.info(
                        LoggingMarker.Log4jMarker.CONFIG,
                        "Setting DSS https proxy config [{}] from SystemProperties",
                        httpsProxy
                );
                proxyConfig.setHttpProperties(httpsProxy);
            } catch (NumberFormatException nfe) {
                // do nothing..
            }
        } else if (StringUtils.hasText(System.getenv("HTTPS_PROXY"))) {
            String envVariable = "HTTPS_PROXY";
            ProxyProperties proxyProperties = getProxyPropertiesFromSystemEnv(envVariable);

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
            ProxyProperties httpProxy = new ProxyProperties();
            httpProxy.setHost(System.getProperty("http.proxyHost"));
            String port = System.getProperty("http.proxyPort");
            try {
                httpProxy.setPort(Integer.parseInt(port));
                LOGGER.info(
                        LoggingMarker.Log4jMarker.CONFIG,
                        "Setting DSS http proxy config [{}] from SystemProperties",
                        httpProxy
                );
                proxyConfig.setHttpProperties(httpProxy);
            } catch (NumberFormatException nfe) {
                // do nothing..
            }
        } else if (StringUtils.hasText(System.getenv("HTTP_PROXY"))) {
            String envVariable = "HTTP_PROXY";

            ProxyProperties proxyProperties = getProxyPropertiesFromSystemEnv(envVariable);

            if (proxyProperties != null) {
                proxyConfig.setHttpProperties(proxyProperties);
                LOGGER.info(
                        LoggingMarker.Log4jMarker.CONFIG,
                        "Setting DSS http proxy config [{}] from Environment variable [{}]",
                        proxyProperties,
                        envVariable
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

        Pattern compile = Pattern.compile("(?:http:\\/\\/|https:\\/\\/)([\\w\\.]+):(\\d+)");
        Matcher matcher = compile.matcher(value);
        if (matcher.matches() && matcher.groupCount() == 2) {

            proxyProperties = new ProxyProperties();
            String proxyHost = matcher.group(1);
            String proxyPort = matcher.group(2);
            try {
                proxyProperties.setPort(Integer.parseInt(proxyPort));
            } catch (NumberFormatException nfe) {
                // do nothing..
            }
            proxyProperties.setHost(proxyHost);
        }
        return proxyProperties;
    }

    @Bean(name = DEFAULT_DATALOADER_BEAN_NAME)
    public DataLoader defaultDataLoader(ProxyConfig proxyConfig) {
        CommonsDataLoader commonsDataLoader = new CommonsDataLoader();
        commonsDataLoader.setProxyConfig(proxyConfig);
        return commonsDataLoader;
    }

    @Bean
    public NonceSource defaultNonceSource() {
        NonceSource nonceSource = new SecureRandomNonceSource();
        return nonceSource;
    }

    @Bean(name = DEFAULT_OCSP_SOURCE_BEAN_NAME)
    public OnlineOCSPSource defaultOcspOnlineSource(ProxyConfig proxyConfig, NonceSource nonceSource) {
        OCSPDataLoader ocspDataLoader = new OCSPDataLoader();
        ocspDataLoader.setProxyConfig(proxyConfig);

        OnlineOCSPSource onlineOCSPSource = new OnlineOCSPSource();
        onlineOCSPSource.setDataLoader(ocspDataLoader);
        onlineOCSPSource.setNonceSource(nonceSource);

        return onlineOCSPSource;
    }

    @Bean(name = DEFAULT_CRL_SOURCE_BEAN_NAME)
    public OnlineCRLSource defaultOnlineCrlSource(DataLoader dataLoader) {
        OnlineCRLSource onlineCRLSource = new OnlineCRLSource();
        onlineCRLSource.setDataLoader(dataLoader);
        return onlineCRLSource;
    }

    @Bean(name = DEFAULT_TIMESTAMPE_SOURCE_BEAN_NAME)
    CompositeTSPSource compositeTSPSource(
            ProxyConfig proxyConfig,
            NonceSource nonceSource,
            BasicDssConfigurationProperties basicDssConfigurationProperties) {
        TimestampDataLoader timestampDataLoader = new TimestampDataLoader();
        timestampDataLoader.setProxyConfig(proxyConfig);

        Map<String, BasicDssConfigurationProperties.Tsp> timeStampServers =
                basicDssConfigurationProperties.getTimeStampServers();

        Map<String, TSPSource> collect = timeStampServers
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        entry -> {
                            String url = entry.getValue().getUrl();
                            String policyOid = entry.getValue().getPolicyOid();
                            OnlineTSPSource onlineTSPSource = new OnlineTSPSource();
                            onlineTSPSource.setNonceSource(nonceSource);
                            onlineTSPSource.setDataLoader(timestampDataLoader);
                            if (StringUtils.hasText(policyOid)) {
                                onlineTSPSource.setPolicyOid(policyOid);
                            }
                            if (StringUtils.hasText(url)) {
                                onlineTSPSource.setTspServer(url);
                            } else {
                                throw new IllegalArgumentException("Illegal tsp url!");
                            }
                            LOGGER.info(
                                    LoggingMarker.Log4jMarker.CONFIG,
                                    "Adding TimeStampServer with key [{}] url [{}] and policyOid [{}] to TSP Sources",
                                    entry.getKey(), url, policyOid
                            );
                            return onlineTSPSource;
                        }
                ));

        CompositeTSPSource compositeTSPSource = new CompositeTSPSource();
        compositeTSPSource.setTspSources(collect);

        return compositeTSPSource;
    }
}
