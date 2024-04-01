package eu.domibus.connector.ui.view.areas.configuration.security;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import eu.domibus.connector.ui.forms.FormsUtil;
import eu.domibus.connector.ui.view.areas.configuration.util.ConfigurationItemChapterDiv;
import eu.domibus.connector.ui.view.areas.configuration.util.ConfigurationUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;


/**
 * This class should handle the following properties:
 * <p>
 * connector.security.key-store.path
 * connector.security.key-store.password
 * connector.security.private-key.alias
 * connector.security.private-key.password
 * <p>
 * connector.security.trust-store.path
 * connector.security.trust-store.password
 * <p>
 * token.issuer.country
 * token.issuer.service-provider
 * token.issuer.advanced-electronic-system-type
 * <p>
 * security.lotl.scheme.uri
 * security.lotl.url
 * security.oj.url
 */
//@Component
//@UIScope
//@Route(value = SecurityConfiguration.ROUTE, layout = ConfigurationLayout.class)
//@RoleRequired(role = "ADMIN")
//@TabMetadata(title = "ECodex Lib Configuration", tabGroup = ConfigurationLayout.TAB_GROUP_NAME)
public class SecurityConfiguration extends VerticalLayout {
    public static final String ROUTE = "ecodexlib";

    private static final long serialVersionUID = 1L;

    ConfigurationUtil util;

    TextField tokenIssuerCountryField = FormsUtil.getFormattedTextField();
    TextField tokenIssuerServiceProviderField = FormsUtil.getFormattedTextField();
    TextField tokenIssuerIdentityProvider = FormsUtil.getFormattedTextField();
    ComboBox<String> tokenIssuerAESValueBox = new ComboBox<String>();

    TextField lotlSchemeURIField = FormsUtil.getFormattedTextField();
    TextField lotlURLField = FormsUtil.getFormattedTextField();
    TextField ojURIField = FormsUtil.getFormattedTextField();

    TextField keyStorePathField = FormsUtil.getFormattedTextField();
    TextField keyStorePasswordField = FormsUtil.getFormattedTextField();
    TextField keyAliasField = FormsUtil.getFormattedTextField();
    TextField keyPasswordField = FormsUtil.getFormattedTextField();
    TextField truststorePathField = FormsUtil.getFormattedTextField();
    TextField truststorePasswordField = FormsUtil.getFormattedTextField();

    public SecurityConfiguration(@Autowired ConfigurationUtil util) {
        this.util = util;

        add(new ConfigurationItemChapterDiv("Token issuer configuration:"));

        add(util.createConfigurationItemTextFieldDiv(
                SecurityConfigurationLabels.tokenIssuerCountryLabels,
                tokenIssuerCountryField
        ));

        add(util.createConfigurationItemTextFieldDiv(
                SecurityConfigurationLabels.tokenIssuerServiceProviderLabels,
                tokenIssuerServiceProviderField
        ));

        List<String> aesValues = new ArrayList<String>();
        aesValues.add("SIGNATURE_BASED");
        aesValues.add("AUTHENTICATION_BASED");
        add(util.createConfigurationItemComboBoxDiv(
                SecurityConfigurationLabels.tokenIssuerAESValueLabels,
                tokenIssuerAESValueBox,
                aesValues
        ));

        add(util.createConfigurationItemTextFieldDiv(
                SecurityConfigurationLabels.tokenIssuerIdentityProvider,
                tokenIssuerIdentityProvider
        ));

        add(new ConfigurationItemChapterDiv("Trusted lists configuration:"));

        add(util.createConfigurationItemTextFieldDiv(
                SecurityConfigurationLabels.lotlSchemeURILabels,
                lotlSchemeURIField
        ));

        add(util.createConfigurationItemTextFieldDiv(SecurityConfigurationLabels.lotlURLLabels, lotlURLField));

        add(util.createConfigurationItemTextFieldDiv(SecurityConfigurationLabels.ojURLLabels, ojURIField));

        add(new ConfigurationItemChapterDiv("Keystore/Truststore configuration:"));

        add(util.createConfigurationItemTextFieldDiv(
                SecurityConfigurationLabels.securityKeyStorePathLabels,
                keyStorePathField
        ));

        add(util.createConfigurationItemTextFieldDiv(
                SecurityConfigurationLabels.securityKeyStorePasswordLabels,
                keyStorePasswordField
        ));

        add(util.createKeystoreInformationGrid(keyStorePathField, keyStorePasswordField));

        add(util.createConfigurationItemTextFieldDiv(
                SecurityConfigurationLabels.securityKeyAliasLabels,
                keyAliasField
        ));

        add(util.createConfigurationItemTextFieldDiv(
                SecurityConfigurationLabels.securityKeyPasswordLabels,
                keyPasswordField
        ));

        add(util.createConfigurationItemTextFieldDiv(
                SecurityConfigurationLabels.securityTrustStorePathLabels,
                truststorePathField
        ));

        add(util.createConfigurationItemTextFieldDiv(
                SecurityConfigurationLabels.securityTrustStorePasswordLabels,
                truststorePasswordField
        ));

        add(util.createKeystoreInformationGrid(truststorePathField, truststorePasswordField));
    }
}
