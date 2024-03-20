package eu.domibus.connector.dss.configuration;

import eu.europa.esig.dss.service.http.proxy.ProxyProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = BasicDssConfigurationProperties.PREFIX)
@Validated
public class BasicDssConfigurationProperties {

    public static final String PREFIX = "connector.dss";

    @NestedConfigurationProperty
    private ProxyProperties httpsProxy;

    @NestedConfigurationProperty
    private ProxyProperties httpProxy;

    private Map<String, @Valid Tsp> timeStampServers = new HashMap<>();

    private Map<String, @Valid TrustListSourceConfigurationProperties> trustListSources = new HashMap<>();

    @NotNull
    private Duration tlCacheExpiration = Duration.ofDays(1);

    @NotNull
    private Path tlCacheLocation = Paths.get("./tlcache");

    public ProxyProperties getHttpsProxy() {
        return httpsProxy;
    }

    public void setHttpsProxy(ProxyProperties httpsProxy) {
        this.httpsProxy = httpsProxy;
    }

    public ProxyProperties getHttpProxy() {
        return httpProxy;
    }

    public void setHttpProxy(ProxyProperties httpProxy) {
        this.httpProxy = httpProxy;
    }

    public Map<String, Tsp> getTimeStampServers() {
        return timeStampServers;
    }

    public void setTimeStampServers(Map<String, Tsp> timeStampServers) {
        this.timeStampServers = timeStampServers;
    }

    public Map<String, TrustListSourceConfigurationProperties> getTrustListSources() {
        return trustListSources;
    }

    public void setTrustListSources(Map<String, TrustListSourceConfigurationProperties> trustListSources) {
        this.trustListSources = trustListSources;
    }

    public Duration getTlCacheExpiration() {
        return tlCacheExpiration;
    }

    public void setTlCacheExpiration(Duration tlCacheExpiration) {
        this.tlCacheExpiration = tlCacheExpiration;
    }

    public Path getTlCacheLocation() {
        return tlCacheLocation;
    }

    public void setTlCacheLocation(Path tlCacheLocation) {
        this.tlCacheLocation = tlCacheLocation;
    }

    @Valid
    public static class Tsp {

        //@Pattern(regexp = "^(https|http):\\/\\/", message = "Only http or https urls are allowed")
        @NotBlank
        private String url;

        private String policyOid;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPolicyOid() {
            return policyOid;
        }

        public void setPolicyOid(String policyOid) {
            this.policyOid = policyOid;
        }
    }

}
