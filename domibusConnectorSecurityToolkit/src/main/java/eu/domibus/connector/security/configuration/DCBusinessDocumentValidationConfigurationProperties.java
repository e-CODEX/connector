package eu.domibus.connector.security.configuration;

import eu.domibus.connector.common.annotations.BusinessDomainScoped;
import eu.domibus.connector.common.annotations.MapNested;
import eu.domibus.connector.common.annotations.UseConverter;
import eu.domibus.connector.domain.enums.AdvancedElectronicSystemType;
import eu.domibus.connector.dss.configuration.SignatureValidationConfigurationProperties;
import eu.domibus.connector.security.aes.DCAuthenticationBasedTechnicalValidationServiceFactory;
import eu.domibus.connector.security.aes.OriginalSenderBasedAESAuthenticationServiceFactory;
import eu.domibus.connector.security.configuration.validation.CheckAllowedAdvancedElectronicSystemType;
import org.apache.commons.collections4.set.ListOrderedSet;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This configuration class holds the settings for
 * <ul>
 *     <li>Validating the business document</li>
 * </ul>
 */
@Validated
@ConfigurationProperties(prefix = DCBusinessDocumentValidationConfigurationProperties.PREFIX)
@BusinessDomainScoped
@Component
@CheckAllowedAdvancedElectronicSystemType
@MapNested
public class DCBusinessDocumentValidationConfigurationProperties {

    public static final String PREFIX = "connector.business-document-sent";

    /**
     * The country where the connector is located
     */
    @NotBlank
    String country = "";

    /**
     * Name of the service provider which is operating the
     * connector
     */
    @NotBlank
    String serviceProvider = "";

    /**
     * The default AdvancedSystemType which should be used
     * <ul>
     *     <li>SIGNATURE_BASED</li>
     *     <li>AUTHENTICATION_BASED</li>
     * </ul>
     *
     * For SIGNATURE_BASED the signatureValidation properties must be configured
     * For AUTHENTICATION_BASED the
     *
     */
    @NotNull
    private AdvancedElectronicSystemType defaultAdvancedSystemType;

    /**
     * Provides a list of the allowed SystemTypes
     *  only a allowed system type can be 5
     */
    @NotNull
    @UseConverter
    private ListOrderedSet<AdvancedElectronicSystemType> allowedAdvancedSystemTypes = ListOrderedSet.listOrderedSet(Arrays.asList(AdvancedElectronicSystemType.values()));

    /**
     *  If true the client can override the for the specific message used system type
     *   the system type must be within the list of allowedAdvancedSystemTypes
     */
    private boolean allowSystemTypeOverrideByClient = true;

    /**
     * Configuration for signature validation,
     *  used when the advancedSystemType is SIGNATURE_BASED
     */
    @Valid
    @NestedConfigurationProperty
    @MapNested
    private SignatureValidationConfigurationProperties signatureValidation;

    @Valid
    @NestedConfigurationProperty
    @MapNested
    private DCBusinessDocumentValidationConfigurationProperties.AuthenticationValidationConfigurationProperties authenticationValidation;

    public Set<AdvancedElectronicSystemType> getAllowedAdvancedSystemTypes() {
        return allowedAdvancedSystemTypes;
    }

    public void setAllowedAdvancedSystemTypes(Set<AdvancedElectronicSystemType> allowedAdvancedSystemTypes) {
        this.allowedAdvancedSystemTypes = ListOrderedSet.listOrderedSet(allowedAdvancedSystemTypes);
    }

    public boolean isAllowSystemTypeOverrideByClient() {
        return allowSystemTypeOverrideByClient;
    }

    public void setAllowSystemTypeOverrideByClient(boolean allowSystemTypeOverrideByClient) {
        this.allowSystemTypeOverrideByClient = allowSystemTypeOverrideByClient;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(String serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public AuthenticationValidationConfigurationProperties getAuthenticationValidation() {
        return authenticationValidation;
    }

    public void setAuthenticationValidation(AuthenticationValidationConfigurationProperties authenticationValidation) {
        this.authenticationValidation = authenticationValidation;
    }

    public AdvancedElectronicSystemType getDefaultAdvancedSystemType() {
        return defaultAdvancedSystemType;
    }

    public void setDefaultAdvancedSystemType(AdvancedElectronicSystemType defaultAdvancedSystemType) {
        this.defaultAdvancedSystemType = defaultAdvancedSystemType;
    }

    public SignatureValidationConfigurationProperties getSignatureValidation() {
        return signatureValidation;
    }

    public void setSignatureValidation(SignatureValidationConfigurationProperties signatureValidation) {
        this.signatureValidation = signatureValidation;
    }

    public static class AuthenticationValidationConfigurationProperties {
        /**
         * If the AUTHENTICATION_BASED is used, the identity provider must be set
         *  the identity provider is the system which has authenticated the user
         */
        @NotBlank
        private String identityProvider;

        @NotNull
        @UseConverter
        private Class<? extends DCAuthenticationBasedTechnicalValidationServiceFactory> authenticatorServiceFactoryClass = OriginalSenderBasedAESAuthenticationServiceFactory.class;

        @NotNull
        private Map<String, String> properties = new HashMap<>();

        public String getIdentityProvider() {
            return identityProvider;
        }

        public void setIdentityProvider(String identityProvider) {
            this.identityProvider = identityProvider;
        }

        public Class<? extends DCAuthenticationBasedTechnicalValidationServiceFactory> getAuthenticatorServiceFactoryClass() {
            return authenticatorServiceFactoryClass;
        }

        public void setAuthenticatorServiceFactoryClass(Class<? extends DCAuthenticationBasedTechnicalValidationServiceFactory> authenticatorServiceFactoryClass) {
            this.authenticatorServiceFactoryClass = authenticatorServiceFactoryClass;
        }

        public Map<String, String> getProperties() {
            return properties;
        }

        public void setProperties(Map<String, String> properties) {
            this.properties = properties;
        }
    }
}
