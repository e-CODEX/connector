package eu.domibus.connector.ui.view.areas.configuration.gateway;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import eu.domibus.connector.ui.forms.FormsUtil;
import eu.domibus.connector.ui.view.areas.configuration.util.ConfigurationUtil;
import org.springframework.beans.factory.annotation.Autowired;


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

    public GatewayConfiguration(@Autowired ConfigurationUtil util) {
        this.util = util;

        add(util.createConfigurationItemTextFieldDiv(
                GatewayConfigurationLabels.gatewaySubmissionLinkLabels,
                gatewaySubmissionServiceLinkField
        ));

        add(util.createConfigurationItemTextFieldDiv(
                GatewayConfigurationLabels.gatewayKeyStorePathLabels,
                gatewayKeyStorePathField
        ));

        add(util.createConfigurationItemTextFieldDiv(
                GatewayConfigurationLabels.gatewayKeyStorePasswordLabels,
                gatewayKeyStorePasswordField
        ));

        add(util.createKeystoreInformationGrid(gatewayKeyStorePathField, gatewayKeyStorePasswordField));

        add(util.createConfigurationItemTextFieldDiv(
                GatewayConfigurationLabels.gatewayKeyAliasLabels,
                gatewayKeyAliasField
        ));

        add(util.createConfigurationItemTextFieldDiv(
                GatewayConfigurationLabels.gatewayKeyPasswordLabels,
                gatewayKeyPasswordField
        ));

        add(util.createConfigurationItemTextFieldDiv(
                GatewayConfigurationLabels.gatewayTrustStorePathLabels,
                gatewayTruststorePathField
        ));

        add(util.createConfigurationItemTextFieldDiv(
                GatewayConfigurationLabels.gatewayTrustStorePasswordLabels,
                gatewayTruststorePasswordField
        ));

        add(util.createKeystoreInformationGrid(gatewayTruststorePathField, gatewayTruststorePasswordField));

        add(util.createConfigurationItemTextFieldDiv(
                GatewayConfigurationLabels.gatewayEncryptAliasLabels,
                gatewayEncryptAliasField
        ));
    }
}
