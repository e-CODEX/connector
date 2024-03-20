package eu.domibus.connector.dss.configuration;

import eu.domibus.connector.common.annotations.MapNested;
import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import eu.ecodex.utils.configuration.api.annotation.ConfigurationDescription;
import eu.ecodex.utils.configuration.api.annotation.ConfigurationLabel;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.annotation.CheckForNull;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@MapNested
public class CertificateVerifierConfigurationProperties {

    @NotBlank
    private String certificateVerifierName = "default";

    private boolean trustStoreEnabled = true;

    //TODO: add not null validation if trustStoreEnabled = true
    @Valid
    @NestedConfigurationProperty
    @ConfigurationLabel("Trust Store")
    @ConfigurationDescription("This store holds all valid certificates for validation")
    @MapNested
    private StoreConfigurationProperties trustStore; // = new StoreConfigurationProperties();

    private boolean ignoreStoreEnabled = false;

    /**
     * Any certificate within this store
     * would not be considered for certificate validation
     */
    //TODO: add not null validation if trustStoreEnabled = true
    @Valid
    @MapNested
    @NestedConfigurationProperty
    @ConfigurationLabel("Ignore Store")
    @ConfigurationDescription("This store holds all ignored certificates for validation.\n" +
            "Any certificate within this store\n" +
            "would not be considered for certificate validation")
    private StoreConfigurationProperties ignoreStore;

    /**
     * The trust source which should be used
     * the sources are configured under: {@link BasicDssConfigurationProperties}
     */
    @ConfigurationLabel("Trusted List Source")
    @ConfigurationDescription("The names of trusted list source. The sources are configured under: " + BasicDssConfigurationProperties.PREFIX + ".trust-source.*")
    private String trustedListSource;

    /**
     * should ocsp be queried
     */
    private boolean ocspEnabled = true;

    /**
     * should a crl be queried
     */
    private boolean crlEnabled = true;

    /**
     * should AIA be used
     */
    private boolean aiaEnabled = true;

    public @CheckForNull String getCertificateVerifierName() {
        return certificateVerifierName;
    }

    public void setCertificateVerifierName(String certificateVerifierName) {
        this.certificateVerifierName = certificateVerifierName;
    }

    public boolean isTrustStoreEnabled() {
        return trustStoreEnabled;
    }

    public void setTrustStoreEnabled(boolean trustStoreEnabled) {
        this.trustStoreEnabled = trustStoreEnabled;
    }

    public @CheckForNull StoreConfigurationProperties getTrustStore() {
        return trustStore;
    }

    public void setTrustStore(StoreConfigurationProperties trustStore) {
        this.trustStore = trustStore;
    }

    public @CheckForNull StoreConfigurationProperties getIgnoreStore() {
        return ignoreStore;
    }

    public void setIgnoreStore(StoreConfigurationProperties ignoreStore) {
        this.ignoreStore = ignoreStore;
    }

    public @CheckForNull String getTrustedListSource() {
        return trustedListSource;
    }

    public void setTrustedListSource(String trustedListSource) {
        this.trustedListSource = trustedListSource;
    }

    public boolean isOcspEnabled() {
        return ocspEnabled;
    }

    public void setOcspEnabled(boolean ocspEnabled) {
        this.ocspEnabled = ocspEnabled;
    }

    public boolean isCrlEnabled() {
        return crlEnabled;
    }

    public void setCrlEnabled(boolean crlEnabled) {
        this.crlEnabled = crlEnabled;
    }

    public boolean isIgnoreStoreEnabled() {
        return ignoreStoreEnabled;
    }

    public void setIgnoreStoreEnabled(boolean ignoreStoreEnabled) {
        this.ignoreStoreEnabled = ignoreStoreEnabled;
    }

    public boolean isAiaEnabled() {
        return aiaEnabled;
    }

    public void setAiaEnabled(boolean aiaEnabled) {
        this.aiaEnabled = aiaEnabled;
    }
}
