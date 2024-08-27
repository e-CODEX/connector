/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.ui.view.areas.configuration.security;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import eu.domibus.connector.ui.forms.FormsUtil;
import eu.domibus.connector.ui.view.areas.configuration.util.ConfigurationItemChapterDiv;
import eu.domibus.connector.ui.view.areas.configuration.util.ConfigurationUtil;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class represents the security configuration for the application.
 */
public class SecurityConfiguration extends VerticalLayout {
    public static final String ROUTE = "ecodexlib";
    private static final long serialVersionUID = 1L;
    ConfigurationUtil util;
    TextField tokenIssuerCountryField = FormsUtil.getFormattedTextField();
    TextField tokenIssuerServiceProviderField = FormsUtil.getFormattedTextField();
    TextField tokenIssuerIdentityProvider = FormsUtil.getFormattedTextField();
    ComboBox<String> tokenIssuerAESValueBox = new ComboBox<>();
    TextField lotlSchemeURIField = FormsUtil.getFormattedTextField();
    TextField lotlURLField = FormsUtil.getFormattedTextField();
    TextField ojURIField = FormsUtil.getFormattedTextField();
    TextField keyStorePathField = FormsUtil.getFormattedTextField();
    TextField keyStorePasswordField = FormsUtil.getFormattedTextField();
    TextField keyAliasField = FormsUtil.getFormattedTextField();
    TextField keyPasswordField = FormsUtil.getFormattedTextField();
    TextField truststorePathField = FormsUtil.getFormattedTextField();
    TextField truststorePasswordField = FormsUtil.getFormattedTextField();

    /**
     * Constructor.
     *
     * @param util the ConfigurationUtil object used for creating configuration items
     */
    public SecurityConfiguration(@Autowired ConfigurationUtil util) {
        this.util = util;

        add(new ConfigurationItemChapterDiv("Token issuer configuration:"));

        add(util.createConfigurationItemTextFieldDiv(
            SecurityConfigurationLabels.tokenIssuerCountryLabels, tokenIssuerCountryField));

        add(util.createConfigurationItemTextFieldDiv(
            SecurityConfigurationLabels.tokenIssuerServiceProviderLabels,
            tokenIssuerServiceProviderField
        ));

        List<String> aesValues = new ArrayList<>();
        aesValues.add("SIGNATURE_BASED");
        aesValues.add("AUTHENTICATION_BASED");
        add(util.createConfigurationItemComboBoxDiv(
            SecurityConfigurationLabels.tokenIssuerAESValueLabels,
            tokenIssuerAESValueBox,
            aesValues
        ));

        add(util.createConfigurationItemTextFieldDiv(
            SecurityConfigurationLabels.tokenIssuerIdentityProvider, tokenIssuerIdentityProvider));

        add(new ConfigurationItemChapterDiv("Trusted lists configuration:"));

        add(util.createConfigurationItemTextFieldDiv(
            SecurityConfigurationLabels.lotlSchemeURILabels, lotlSchemeURIField));

        add(util.createConfigurationItemTextFieldDiv(
            SecurityConfigurationLabels.lotlURLLabels, lotlURLField));

        add(util.createConfigurationItemTextFieldDiv(
            SecurityConfigurationLabels.ojURLLabels, ojURIField));

        add(new ConfigurationItemChapterDiv("Keystore/Truststore configuration:"));

        add(util.createConfigurationItemTextFieldDiv(
            SecurityConfigurationLabels.securityKeyStorePathLabels, keyStorePathField));

        add(util.createConfigurationItemTextFieldDiv(
            SecurityConfigurationLabels.securityKeyStorePasswordLabels, keyStorePasswordField));

        add(util.createKeystoreInformationGrid(keyStorePathField, keyStorePasswordField));

        add(util.createConfigurationItemTextFieldDiv(
            SecurityConfigurationLabels.securityKeyAliasLabels, keyAliasField));

        add(util.createConfigurationItemTextFieldDiv(
            SecurityConfigurationLabels.securityKeyPasswordLabels, keyPasswordField));

        add(util.createConfigurationItemTextFieldDiv(
            SecurityConfigurationLabels.securityTrustStorePathLabels, truststorePathField));

        add(util.createConfigurationItemTextFieldDiv(
            SecurityConfigurationLabels.securityTrustStorePasswordLabels, truststorePasswordField));

        add(util.createKeystoreInformationGrid(truststorePathField, truststorePasswordField));
    }
}
