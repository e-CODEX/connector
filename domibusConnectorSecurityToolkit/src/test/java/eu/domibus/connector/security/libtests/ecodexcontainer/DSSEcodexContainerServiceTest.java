package eu.domibus.connector.security.libtests.ecodexcontainer;

import static org.assertj.core.api.Assertions.assertThat;

import eu.domibus.connector.security.DomibusSecurityToolkitImpl;
import eu.ecodex.dss.model.BusinessContent;
import eu.ecodex.dss.model.CertificateStoreInfo;
import eu.ecodex.dss.model.EnvironmentConfiguration;
import eu.ecodex.dss.model.ProxyData;
import eu.ecodex.dss.model.token.AdvancedSystemType;
import eu.ecodex.dss.model.token.TokenIssuer;
import eu.ecodex.dss.service.impl.dss.DSSECodexContainerService;
import eu.ecodex.dss.util.SignatureParametersFactory;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;
import eu.europa.esig.dss.enumerations.MimeTypeEnum;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.service.http.proxy.ProxyProperties;
import java.io.IOException;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

/**
 * Exploration tests for DSSEcodexContainerService.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@SuppressWarnings("squid:S1135")
@Disabled("test is failing on ci server")
class DSSEcodexContainerServiceTest {
    private Properties properties;

    @BeforeEach
    public void setUp() {
        this.properties = loadTestProperties();
    }

    private DSSECodexContainerService initContainerService() throws Exception {
        DSSECodexContainerService containerService = null;
        

        CertificateStoreInfo certStore = new CertificateStoreInfo();
        certStore.setLocation(new ClassPathResource("/keys/connector-keystore.jks"));
        certStore.setPassword("connector");
        var keyAlias = "domibusConnector";
        var keyPassword = "connector";
        var encryptionAlgorithm = EncryptionAlgorithm.RSA;
        var digestAlgorithm = DigestAlgorithm.SHA1;

        var signingParameters = SignatureParametersFactory.create(
            certStore, keyAlias, keyPassword, encryptionAlgorithm, digestAlgorithm
        );
        assertThat(signingParameters).isNotNull();

        return null;
    }

    private EnvironmentConfiguration initEnvironmentConfiguration() {
        var envConfig = new EnvironmentConfiguration();
        envConfig.setProxyHTTPS(initHttpsProxyData());
        envConfig.setProxyHTTP(initHttpProxyData());
        return envConfig;
    }

    // ecodex container proxy config
    private ProxyConfig initProxyConfig() {
        var httpsProxyProperties = new ProxyProperties();
        httpsProxyProperties.setHost(properties.getProperty("https.proxy.host"));
        httpsProxyProperties.setPort(Integer.parseInt(properties.getProperty("https.proxy.port")));
        httpsProxyProperties.setPassword(properties.getProperty("https.proxy.password"));
        httpsProxyProperties.setUser(properties.getProperty("https.proxy.user"));

        var proxyConfig = new ProxyConfig();
        proxyConfig.setHttpsProperties(httpsProxyProperties);

        var httpProxyProperties = new ProxyProperties();
        httpProxyProperties.setHost(properties.getProperty("http.proxy.host"));
        httpProxyProperties.setPort(Integer.parseInt(properties.getProperty("http.proxy.port")));
        httpProxyProperties.setPassword(properties.getProperty("http.proxy.password"));
        httpProxyProperties.setUser(properties.getProperty("http.proxy.user"));
        proxyConfig.setHttpProperties(httpProxyProperties);

        return proxyConfig;
    }

    private ProxyData initHttpsProxyData() {
        var httpProxy = new ProxyData();
        httpProxy.setPort(Integer.parseInt(properties.getProperty("https.proxy.port")));
        httpProxy.setHost(properties.getProperty("https.proxy.host"));
        httpProxy.setAuthName(properties.getProperty("https.proxy.user"));
        httpProxy.setAuthPass(properties.getProperty("https.proxy.password"));
        return httpProxy;
    }

    private ProxyData initHttpProxyData() {
        var httpProxy = new ProxyData();
        httpProxy.setPort(Integer.parseInt(properties.getProperty("http.proxy.port")));
        httpProxy.setHost(properties.getProperty("http.proxy.host"));
        httpProxy.setAuthName(properties.getProperty("http.proxy.user"));
        httpProxy.setAuthPass(properties.getProperty("http.proxy.password"));
        return httpProxy;
    }

    private Properties loadTestProperties() {
        try {
            var loadedTestProperties = new Properties();
            var testPropertiesInputStream = this.getClass().getResourceAsStream("/test.properties");
            if (testPropertiesInputStream == null) {
                throw new RuntimeException("test.properties could not be load as resource!");
            }
            loadedTestProperties.load(testPropertiesInputStream);
            return loadedTestProperties;
        } catch (IOException ioe) {
            throw new RuntimeException("test.properties could not be read!");
        }
    }

    @Test
    void testCreateBigContainer() throws Exception {
        // TODO see why this test body is empty
    }

    @Test
    void testCreateContainer() throws Exception {
        var containerService = initContainerService();
        var businessContent = new BusinessContent();
        var xmlDocument = new InMemoryDocument(
            loadByteArrayFromClassPathResource("/examples/Form_A.xml"),
            DomibusSecurityToolkitImpl.CONTENT_XML_IDENTIFIER + ".xml",
            MimeTypeEnum.PDF
        );

        businessContent.setDocument(xmlDocument);

        var formAPdf = new InMemoryDocument(
            loadByteArrayFromClassPathResource("/examples/Form_A.pdf"),
            DomibusSecurityToolkitImpl.MAIN_DOCUMENT_NAME + ".pdf",
            MimeTypeEnum.PDF
        );
        businessContent.addAttachment(formAPdf);

        // big dss document (4GB random bytes)
        //        DSSDocument doc = new MyBigDssFile();
        //        businessContent.addAttachment(doc);

        var attachmentOne = new InMemoryDocument(
            loadByteArrayFromClassPathResource("/examples/supercool.pdf"),
            "supercool.pdf",
            MimeTypeEnum.PDF
        );

        businessContent.addAttachment(attachmentOne);
        var tokenIssuer = createTokenIssuer();
        var container = containerService.create(businessContent);
        var asicDocument = container.getAsicDocument();
        assertThat(asicDocument).isNotNull();
    }

    private TokenIssuer createTokenIssuer() {
        var tokenIssuer = new TokenIssuer();
        tokenIssuer.setAdvancedElectronicSystem(AdvancedSystemType.SIGNATURE_BASED);
        tokenIssuer.setCountry("AT");
        tokenIssuer.setServiceProvider("BRZ");
        return tokenIssuer;
    }

    private byte[] loadByteArrayFromClassPathResource(String resource) {
        try {
            var inputStream = getClass().getResourceAsStream(resource);
            return StreamUtils.copyToByteArray(inputStream);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
}
