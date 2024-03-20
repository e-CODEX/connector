package eu.domibus.connectorplugins.link.gwwspullplugin;

import eu.domibus.connector.lib.spring.configuration.CxfTrustKeyStoreConfigurationProperties;
import eu.domibus.connector.lib.spring.configuration.KeyAndKeyStoreAndTrustStoreConfigurationProperties;
import eu.ecodex.utils.configuration.api.annotation.ConfigurationDescription;
import eu.ecodex.utils.configuration.api.annotation.ConfigurationLabel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "")
@Validated
public class DCGatewayPullPluginConfigurationProperties {

    @NotNull
    @NotBlank
    @ConfigurationLabel("Pull Gateway Service URL")
    @ConfigurationDescription("The URL of the domibus connector pull gateway plugin webservice.\nEg.: <domibus url>/services/pull-gw")
    private String gwAddress;

    /**
     * SSL Key Store configuration
     *
     * The SSL-Key Store holds the path to the keyStore and the keyStore password to access the private-key which is needed to establish the TLS connection
     * to the Gateway. The private key is used to authenticate against the Gateway.
     */
    @NestedConfigurationProperty
    @ConfigurationDescription("TLS between GW - Connector")
    private KeyAndKeyStoreAndTrustStoreConfigurationProperties tls;

    @Valid
    @NestedConfigurationProperty
    @NotNull
    @ConfigurationDescription("CXF encryption, signing, certs connector - GW")
    private CxfTrustKeyStoreConfigurationProperties soap;

    @Valid
    @NotNull
    @ConfigurationLabel("WS Policy for GW <-> Connector Link")
    @ConfigurationDescription("This Property is used to define the location of the ws policy which is used for communication with the gateway. \nDefault: /wsdl/backend.policy.xml")
    private Resource wsPolicy = new ClassPathResource("/wsdl/backend.policy.xml");


    @ConfigurationLabel("WS Policy for GW <-> Connector Link")
    @ConfigurationDescription("This Property is used to activate CXF logging interceptor, which will log the CXF requests. \nDefault: false")
    private boolean cxfLoggingEnabled = false;

    public String getGwAddress() {
        return gwAddress;
    }

    public void setGwAddress(String gwAddress) {
        this.gwAddress = gwAddress;
    }

    public KeyAndKeyStoreAndTrustStoreConfigurationProperties getTls() {
        return tls;
    }

    public void setTls(KeyAndKeyStoreAndTrustStoreConfigurationProperties tls) {
        this.tls = tls;
    }

    public CxfTrustKeyStoreConfigurationProperties getSoap() {
        return soap;
    }

    public void setSoap(CxfTrustKeyStoreConfigurationProperties soap) {
        this.soap = soap;
    }

    public Resource getWsPolicy() {
        return wsPolicy;
    }

    public void setWsPolicy(Resource wsPolicy) {
        this.wsPolicy = wsPolicy;
    }

    public boolean isCxfLoggingEnabled() {
        return cxfLoggingEnabled;
    }

    public void setCxfLoggingEnabled(boolean cxfLoggingEnabled) {
        this.cxfLoggingEnabled = cxfLoggingEnabled;
    }
}
