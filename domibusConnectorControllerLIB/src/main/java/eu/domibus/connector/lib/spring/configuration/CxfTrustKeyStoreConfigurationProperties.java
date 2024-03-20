package eu.domibus.connector.lib.spring.configuration;

import eu.domibus.connector.lib.spring.configuration.validation.CheckKeyIsLoadableFromKeyStore;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@CheckKeyIsLoadableFromKeyStore
@Valid
public class CxfTrustKeyStoreConfigurationProperties extends KeyAndKeyStoreAndTrustStoreConfigurationProperties {


    @Valid
    @NotNull
    private String encryptAlias;

    private boolean loadCaCerts = true;

    public String getEncryptAlias() {
        return encryptAlias;
    }

    public void setEncryptAlias(String encryptAlias) {
        this.encryptAlias = encryptAlias;
    }

    public boolean isLoadCaCerts() {
        return loadCaCerts;
    }

    public void setLoadCaCerts(boolean loadCaCerts) {
        this.loadCaCerts = loadCaCerts;
    }
}
