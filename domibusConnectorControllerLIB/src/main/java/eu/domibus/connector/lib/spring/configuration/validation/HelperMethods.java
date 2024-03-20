package eu.domibus.connector.lib.spring.configuration.validation;

import eu.domibus.connector.common.service.DCKeyStoreService;
import eu.domibus.connector.lib.spring.configuration.KeyConfigurationProperties;
import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidatorContext;
import java.security.*;

@Component
public class HelperMethods {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelperMethods.class);

    private final DCKeyStoreService dcKeyStoreService;

    public HelperMethods(DCKeyStoreService dcKeyStoreService) {
        this.dcKeyStoreService = dcKeyStoreService;
    }

    public boolean checkKeyIsLoadable(ConstraintValidatorContext context, StoreConfigurationProperties storeConfig, KeyConfigurationProperties keyConfig) {
        if (keyConfig == null || keyConfig.getAlias() == null || storeConfig == null) { //DO NOT CHECK IF keyConfig or keyAlias or storeConfig is null
            LOGGER.trace("checkKeyIsLoadable skipped because either keyConfig, keyConfig.alias or storeConfig is null!");
            return true;
        }
        LOGGER.trace("checkKeyIsLoadable with context [{}], storeConfig [{}], keyConfig [{}]", context, storeConfig, keyConfig);
        String alias = keyConfig.getAlias();
        String password = keyConfig.getPassword();
        String logPassword = "** enableDebugToSee **";
        if (LOGGER.isDebugEnabled()) {
            logPassword = password;
        }

        char[] passwordArray = password.toCharArray();


        KeyStore keyStore;
        try {
            keyStore = dcKeyStoreService.loadKeyStore(storeConfig);
        } catch (Exception e) {
            context.buildConstraintViolationWithTemplate(e.getMessage()).addConstraintViolation(); //TODO: add PropertyNode
            return false;
        }


        try {
            if (!keyStore.containsAlias(alias)) {
                String error = String.format("key alias [%s] does not exist in key store!", alias);

                context.buildConstraintViolationWithTemplate(error)
                        .addPropertyNode("privateKey")
                        .addPropertyNode("alias")
                        .addConstraintViolation();
                return false;
            }

            Key key = keyStore.getKey(alias, passwordArray);
            if (key != null) {
                return true;
            } else {
                String error = String.format("Cannot retrieve key with alias [%s] and password [%s]! ", alias, logPassword);
                context.buildConstraintViolationWithTemplate(error).addConstraintViolation();
            }
        } catch (KeyStoreException e) {

            String error = String.format("key with alias [%s] and pw [%s] could not recovered! KeyStoreException!", alias, logPassword);
            LOGGER.warn(error, e);
            context.buildConstraintViolationWithTemplate(error).addConstraintViolation();
        } catch (NoSuchAlgorithmException e) {
            String error = String.format("key with alias [%s] and pw [%s] could not recovered! No such algorithm exception", alias, logPassword);
            LOGGER.warn(error, e);
            context.buildConstraintViolationWithTemplate(error).addConstraintViolation();
        } catch (UnrecoverableKeyException e) {
            String error = String.format("key with alias [%s] could not recovered! Check if the password [%s] is correct", alias, logPassword);
            context.buildConstraintViolationWithTemplate(error).addConstraintViolation();
        }
        return false;
    }

}
