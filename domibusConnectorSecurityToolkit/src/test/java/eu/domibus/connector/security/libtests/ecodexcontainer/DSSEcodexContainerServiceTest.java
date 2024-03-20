package eu.domibus.connector.security.libtests.ecodexcontainer;

import eu.domibus.connector.security.DomibusSecurityToolkitImpl;
import eu.ecodex.dss.model.*;
import eu.ecodex.dss.model.token.AdvancedSystemType;
import eu.ecodex.dss.model.token.TokenIssuer;
import eu.ecodex.dss.service.ECodexLegalValidationService;
import eu.ecodex.dss.service.impl.dss.DSSECodexContainerService;
import eu.ecodex.dss.service.impl.dss.DSSECodexLegalValidationService;
import eu.ecodex.dss.util.SignatureParametersFactory;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;
import eu.europa.esig.dss.enumerations.MimeTypeEnum;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;

import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.service.http.proxy.ProxyProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exploration tests for DSSEcodexContainerService
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Disabled("test is failing on ci server")
public class DSSEcodexContainerServiceTest {

    private Properties properties;
    
    @BeforeEach
    public void setUp() {
        this.properties = loadTestProperties();
    }
    
    
    private DSSECodexContainerService initContainerService() throws Exception {
        DSSECodexContainerService containerService = null; // new DSSECodexContainerService(technicalValidationService, legalValidationService, signingParameters, certificateVerifier, connectorCertificatesSource, processExecutor, asicsSignatureChecker, xmlTokenSignatureChecker, pdfTokenSignatureChecker);
        
        
//        EnvironmentConfiguration environmentConfiguration = initEnvironmentConfiguration();
        
        
//        ECodexLegalValidationService ecodexLegalValidationService = new DSSECodexLegalValidationService();
//        ecodexLegalValidationService.setEnvironmentConfiguration(environmentConfiguration);
//        containerService.setLegalValidationService(ecodexLegalValidationService);
        
        
//        DSSECodexTechnicalValidationService technicalValidationService = new DSSECodexTechnicalValidationService(certificateVerifier, processExecutor, trustedListCertificatesSource, ignoredCertificatesStore);
//        technicalValidationService.setEnvironmentConfiguration(environmentConfiguration);
//        technicalValidationService.setProxyPreferenceManager(initProxyConfig());
//
//        technicalValidationService.initAuthenticationCertificateVerification();
        
//        containerService.setTechnicalValidationService(technicalValidationService);
        

        CertificateStoreInfo certStore = new CertificateStoreInfo();
        certStore.setLocation(new ClassPathResource("/keys/connector-keystore.jks"));
        certStore.setPassword("connector");
        String keyAlias = "domibusConnector";
        String keyPassword = "connector";
        EncryptionAlgorithm encryptionAlgorithm = EncryptionAlgorithm.RSA;
        DigestAlgorithm digestAlgorithm = DigestAlgorithm.SHA1;    
        
        SignatureParameters signingParameters = SignatureParametersFactory.create(certStore, keyAlias, keyPassword, encryptionAlgorithm, digestAlgorithm);
        assertThat(signingParameters).isNotNull();
//        containerService.setContainerSignatureParameters(signingParameters);
        
        
//        CertificateVerifier certificateVerifier = Mockito.mock(CertificateVerifier.class);        
//        containerService.setCertificateVerifier(certificateVerifier);
                
        return containerService;
    }
    
    private EnvironmentConfiguration initEnvironmentConfiguration() {
        EnvironmentConfiguration envConfig = new EnvironmentConfiguration();         
        envConfig.setProxyHTTPS(initHttpsProxyData());                
        envConfig.setProxyHTTP(initHttpProxyData());
        return envConfig;
    }
    
    //ecodex container proxy config
    private ProxyConfig initProxyConfig() {
        ProxyConfig proxyConfig = new ProxyConfig();
        
        ProxyProperties httpsProxyProperties = new ProxyProperties();
        httpsProxyProperties.setHost(properties.getProperty("https.proxy.host"));
        httpsProxyProperties.setPort(Integer.valueOf(properties.getProperty("https.proxy.port")));
        httpsProxyProperties.setPassword(properties.getProperty("https.proxy.password"));
        httpsProxyProperties.setUser(properties.getProperty("https.proxy.user"));
        proxyConfig.setHttpsProperties(httpsProxyProperties);
        
        ProxyProperties httpProxyProperties = new ProxyProperties();
        httpProxyProperties.setHost(properties.getProperty("http.proxy.host"));
        httpProxyProperties.setPort(Integer.valueOf(properties.getProperty("http.proxy.port")));
        httpProxyProperties.setPassword(properties.getProperty("http.proxy.password"));
        httpProxyProperties.setUser(properties.getProperty("http.proxy.user"));
        proxyConfig.setHttpProperties(httpProxyProperties);
        
        return proxyConfig;
    }
    
    private ProxyData initHttpsProxyData() {
        ProxyData httpProxy = new ProxyData();
        httpProxy.setPort(Integer.valueOf(properties.getProperty("https.proxy.port")));
        httpProxy.setHost(properties.getProperty("https.proxy.host"));
        httpProxy.setAuthName(properties.getProperty("https.proxy.user"));
        httpProxy.setAuthPass(properties.getProperty("https.proxy.password"));
        return httpProxy;
    }
        
    private ProxyData initHttpProxyData() {
        ProxyData httpProxy = new ProxyData();
        httpProxy.setPort(Integer.valueOf(properties.getProperty("http.proxy.port")));
        httpProxy.setHost(properties.getProperty("http.proxy.host"));
        httpProxy.setAuthName(properties.getProperty("http.proxy.user"));
        httpProxy.setAuthPass(properties.getProperty("http.proxy.password"));
        return httpProxy;
    }
    
    private Properties loadTestProperties() {
        try {
            Properties loadedTestProperties = new Properties();
            InputStream testPropertiesInputStream = this.getClass().getResourceAsStream("/test.properties");
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
    public void testCreateBigContainer() throws Exception {
        
    }
    
    
    @Test
    public void testCreateContainer() throws Exception {
        DSSECodexContainerService containerService = initContainerService(); 
                        
        BusinessContent businessContent = new BusinessContent();
        
        DSSDocument xmlDocument = new InMemoryDocument(
            loadByteArrayFromClassPathRessource("/examples/Form_A.xml"),
            DomibusSecurityToolkitImpl.CONTENT_XML_IDENTIFIER + ".xml",
                MimeTypeEnum.PDF);
   
        businessContent.setDocument(xmlDocument);
        
        
        DSSDocument formAPdf = new InMemoryDocument(
                            loadByteArrayFromClassPathRessource("/examples/Form_A.pdf"),
                            DomibusSecurityToolkitImpl.MAIN_DOCUMENT_NAME + ".pdf",
                MimeTypeEnum.PDF);
        businessContent.addAttachment(formAPdf);
        
        //big dss document (4GB random bytes)
//        DSSDocument doc = new MyBigDssFile();
//        businessContent.addAttachment(doc);
        
        DSSDocument attachmentOne = new InMemoryDocument(
                loadByteArrayFromClassPathRessource("/examples/supercool.pdf"),
                "supercool.pdf",
                MimeTypeEnum.PDF);
        
        businessContent.addAttachment(attachmentOne);
        
        TokenIssuer tokenIssuer = createTokenIssuer();
        
        ECodexContainer container = containerService.create(businessContent);
        DSSDocument asicDocument = container.getAsicDocument();
        assertThat(asicDocument).isNotNull();
        
    }
    
    private TokenIssuer createTokenIssuer() {
        TokenIssuer tokenIssuer = new TokenIssuer();
        tokenIssuer.setAdvancedElectronicSystem(AdvancedSystemType.SIGNATURE_BASED);
        tokenIssuer.setCountry("AT");
        tokenIssuer.setServiceProvider("BRZ");
        return tokenIssuer;
    }
    
    private byte[] loadByteArrayFromClassPathRessource(String ressource) {
        try {
            InputStream inputStream = getClass().getResourceAsStream(ressource);
            return StreamUtils.copyToByteArray(inputStream);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        } 
    }
    

}
