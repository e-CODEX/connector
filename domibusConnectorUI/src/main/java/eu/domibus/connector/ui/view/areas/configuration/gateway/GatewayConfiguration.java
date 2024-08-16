/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.ui.view.areas.configuration.gateway;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import eu.domibus.connector.ui.forms.FormsUtil;
import eu.domibus.connector.ui.view.areas.configuration.util.ConfigurationUtil;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * GatewayConfiguration is a class that extends VerticalLayout and represents the configuration
 * settings for a gateway application.
 */
public class GatewayConfiguration extends VerticalLayout {
    public static final String ROUTE = "gwlinkws";
    ConfigurationUtil util;
    TextField gatewaySubmissionServiceLinkField = FormsUtil.getFormattedTextField();
    TextField gatewayKeyStorePathField = FormsUtil.getFormattedTextField();
    TextField gatewayKeyStorePasswordField = FormsUtil.getFormattedTextField();
    TextField gatewayKeyAliasField = FormsUtil.getFormattedTextField();
    TextField gatewayKeyPasswordField = FormsUtil.getFormattedTextField();
    TextField gatewayTruststorePathField = FormsUtil.getFormattedTextField();
    TextField gatewayTruststorePasswordField = FormsUtil.getFormattedTextField();
    TextField gatewayEncryptAliasField = FormsUtil.getFormattedTextField();

    /**
     * Constructor.
     *
     * @param util The ConfigurationUtil object used for creating and managing configuration items.
     */
    public GatewayConfiguration(@Autowired ConfigurationUtil util) {
        this.util = util;

        add(util.createConfigurationItemTextFieldDiv(
            GatewayConfigurationLabels.gatewaySubmissionLinkLabels,
            gatewaySubmissionServiceLinkField
        ));

        add(util.createConfigurationItemTextFieldDiv(
            GatewayConfigurationLabels.gatewayKeyStorePathLabels, gatewayKeyStorePathField));

        add(util.createConfigurationItemTextFieldDiv(
            GatewayConfigurationLabels.gatewayKeyStorePasswordLabels,
            gatewayKeyStorePasswordField
        ));

        add(util.createKeystoreInformationGrid(
            gatewayKeyStorePathField, gatewayKeyStorePasswordField));

        add(util.createConfigurationItemTextFieldDiv(
            GatewayConfigurationLabels.gatewayKeyAliasLabels, gatewayKeyAliasField));

        add(util.createConfigurationItemTextFieldDiv(
            GatewayConfigurationLabels.gatewayKeyPasswordLabels, gatewayKeyPasswordField));

        add(util.createConfigurationItemTextFieldDiv(
            GatewayConfigurationLabels.gatewayTrustStorePathLabels, gatewayTruststorePathField));

        add(util.createConfigurationItemTextFieldDiv(
            GatewayConfigurationLabels.gatewayTrustStorePasswordLabels,
            gatewayTruststorePasswordField
        ));

        add(util.createKeystoreInformationGrid(
            gatewayTruststorePathField, gatewayTruststorePasswordField));

        add(util.createConfigurationItemTextFieldDiv(
            GatewayConfigurationLabels.gatewayEncryptAliasLabels, gatewayEncryptAliasField));
    }
}
