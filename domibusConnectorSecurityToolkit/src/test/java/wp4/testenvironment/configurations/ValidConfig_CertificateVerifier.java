package wp4.testenvironment.configurations;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;


import eu.europa.esig.dss.service.crl.OnlineCRLSource;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.service.http.proxy.ProxyProperties;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.x509.KeyStoreCertificateSource;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import org.springframework.core.io.ClassPathResource;


// SUB_CONF_17
public class ValidConfig_CertificateVerifier {

	private static CommonCertificateVerifier verifierVariant1 = null;
	private static CommonCertificateVerifier verifierVariant2 = null;

	// Common Certificate Verifier
	// SUB_CONF_17 Variant 1 
	public static CommonCertificateVerifier get_WithProxy() throws IOException {
		
		if(verifierVariant1 == null) {
			Properties props = new Properties();
			
			try{
				Reader reader = new FileReader("src/test/resources/configuration.properties");
				props.load(reader);
				reader.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			
			ProxyProperties httpProxyProperties = new ProxyProperties();
			httpProxyProperties.setHost(props.getProperty("proxy.http.host"));
			httpProxyProperties.setPort(Integer.valueOf(props.getProperty("proxy.http.port")));
			httpProxyProperties.setUser(props.getProperty("proxy.http.user"));
			httpProxyProperties.setPassword(props.getProperty("proxy.http.password"));

			ProxyProperties httpsProxyProperties = new ProxyProperties();
			httpsProxyProperties.setHost(props.getProperty("proxy.https.host"));
			httpsProxyProperties.setPort(Integer.valueOf(props.getProperty("proxy.https.port")));
			httpsProxyProperties.setUser(props.getProperty("proxy.https.user"));
			httpsProxyProperties.setPassword(props.getProperty("proxy.https.password"));
			
			ProxyConfig proxyPreferenceManager = new ProxyConfig();
			proxyPreferenceManager.setHttpProperties(httpProxyProperties);
			proxyPreferenceManager.setHttpsProperties(httpsProxyProperties);
			
			CommonsDataLoader normalLoader = new CommonsDataLoader();
			normalLoader.setProxyConfig(proxyPreferenceManager);
		
			verifierVariant1 = new CommonCertificateVerifier(); 
			
			TrustedListsCertificateSource certSource = new TrustedListsCertificateSource();
			OnlineCRLSource crlSource = new OnlineCRLSource();
			OnlineOCSPSource ocspSource = new OnlineOCSPSource();
	        
//	        TSLRepository tslRepository = new TSLRepository();
//	        tslRepository.setTrustedListsCertificateSource(certSource);
			
	        KeyStoreCertificateSource keyStoreCertificateSource = new KeyStoreCertificateSource(new File("src/test/resources/keystores/signature_store.jks"), "JKS", "teststore");
	        
//	        TSLValidationJob job = new TSLValidationJob();
//	        job.setDataLoader(normalLoader);
//	        job.setLotlRootSchemeInfoUri("https://ec.europa.eu/information_society/policy/esignature/trusted-list/tl.html");
//	        job.setLotlUrl("https://ec.europa.eu/information_society/policy/esignature/trusted-list/tl-mp.xml");
//			job.setOjContentKeyStore(keyStoreCertificateSource);
//	        job.setOjUrl("http://eur-lex.europa.eu/legalcontent/EN/TXT/?uri=uriserv:OJ.C_.2016.233.01.0001.01.ENG");
//	        job.setLotlCode("EU");
//	        job.setRepository(tslRepository);
//	        job.initRepository();
			
			crlSource.setDataLoader(normalLoader);
	
			CommonsDataLoader ocspDataLoader = new CommonsDataLoader();
			ocspDataLoader.setProxyConfig(proxyPreferenceManager);
			ocspDataLoader.setContentType("application/ocsp-request");
			
			ocspSource.setDataLoader(ocspDataLoader);
	
			verifierVariant1.setTrustedCertSources(certSource);
			verifierVariant1.setCrlSource(crlSource);
			verifierVariant1.setOcspSource(ocspSource);
		} 
		
		return verifierVariant1;
	}
	
	// Common Certificate Verifier - No Proxy
	// SUB_CONF_17 Variant 2
	public static CommonCertificateVerifier get_NoProxy() throws IOException {
		
		if(verifierVariant2 == null) {
			verifierVariant2 = new CommonCertificateVerifier(); 
			
			CommonsDataLoader normalLoader = new CommonsDataLoader();
			
			TrustedListsCertificateSource certSource = new TrustedListsCertificateSource();
			OnlineCRLSource crlSource = new OnlineCRLSource();
			OnlineOCSPSource ocspSource = new OnlineOCSPSource();
	        
//	        TSLRepository tslRepository = new TSLRepository();
//	        tslRepository.setTrustedListsCertificateSource(certSource);
			
	        KeyStoreCertificateSource keyStoreCertificateSource = new KeyStoreCertificateSource(new ClassPathResource("/keystores/keystore.jks").getInputStream(), "JKS", "test123");
	        
//	        TSLValidationJob job = new TSLValidationJob();
//	        job.setDataLoader(normalLoader);
//	        job.setLotlRootSchemeInfoUri("https://ec.europa.eu/information_society/policy/esignature/trusted-list/tl.html");
//	        job.setLotlUrl("https://ec.europa.eu/information_society/policy/esignature/trusted-list/tl-mp.xml");
//			job.setOjContentKeyStore(keyStoreCertificateSource);
//	        job.setOjUrl("http://eur-lex.europa.eu/legalcontent/EN/TXT/?uri=uriserv:OJ.C_.2016.233.01.0001.01.ENG");
//	        job.setLotlCode("EU");
//	        job.setRepository(tslRepository);
//	        job.refresh();
			
			crlSource.setDataLoader(normalLoader);
	
			CommonsDataLoader ocspDataLoader = new CommonsDataLoader();
			ocspDataLoader.setContentType("application/ocsp-request");
			
			ocspSource.setDataLoader(ocspDataLoader);
	
			verifierVariant2.setTrustedCertSources(certSource);
			verifierVariant2.setCrlSource(crlSource);
			verifierVariant2.setOcspSource(ocspSource);
		}
		
		return verifierVariant2;
	}
}
