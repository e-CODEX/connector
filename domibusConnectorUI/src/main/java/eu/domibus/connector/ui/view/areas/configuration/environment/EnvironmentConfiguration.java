package eu.domibus.connector.ui.view.areas.configuration.environment;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import eu.domibus.connector.ui.component.LumoCheckbox;
import eu.domibus.connector.ui.forms.FormsUtil;
import eu.domibus.connector.ui.layout.DCVerticalLayoutWithTitleAndHelpButton;
import eu.domibus.connector.ui.view.areas.configuration.util.ConfigurationItemChapterDiv;
import eu.domibus.connector.ui.view.areas.configuration.util.ConfigurationUtil;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author riederb
 * <p>
 * This class should handle the following parameters:
 * <p>
 * connector.gatewaylink.ws.submissionEndpointAddress
 * <p>
 * connector.test.service
 * connector.test.action
 * <p>
 * http.proxy.enabled
 * http.proxy.host
 * http.proxy.port
 * http.proxy.user=
 * http.proxy.password=
 * <p>
 * https.proxy.enabled
 * https.proxy.host
 * https.proxy.port
 * https.proxy.user=
 * https.proxy.password=
 * <p>
 * spring.datasource.driver-class-name
 * spring.datasource.username
 * spring.datasource.url
 * spring.datasource.password
 * spring.jpa.properties.hibernate.dialect
 */
public class EnvironmentConfiguration extends DCVerticalLayoutWithTitleAndHelpButton {
    public static final String ROUTE = "environment";
    public static final String TITLE = "Environment Configuration";
    public static final String HELP_ID = "ui/configuration/environment_configuration.html";

    private static final long serialVersionUID = 1L;
    ConfigurationUtil util;

    LumoCheckbox useHttpProxyBox = new LumoCheckbox();
    TextField httpProxyHostField = FormsUtil.getFormattedTextField();
    TextField httpProxyPortField = FormsUtil.getFormattedTextField();
    TextField httpProxyUserField = FormsUtil.getFormattedTextField();
    TextField httpProxyPasswordField = FormsUtil.getFormattedTextField();

    LumoCheckbox useHttpsProxyBox = new LumoCheckbox();
    TextField httpsProxyHostField = FormsUtil.getFormattedTextField();
    TextField httpsProxyPortField = FormsUtil.getFormattedTextField();
    TextField httpsProxyUserField = FormsUtil.getFormattedTextField();
    TextField httpsProxyPasswordField = FormsUtil.getFormattedTextField();

    public EnvironmentConfiguration(@Autowired ConfigurationUtil util) {
        super(HELP_ID, TITLE);

        this.util = util;

        add(new ConfigurationItemChapterDiv("Proxy Configuration:"));

        useHttpProxyBox.addValueChangeListener(e -> {
            httpProxyHostField.setReadOnly(!e.getValue());
            httpProxyPortField.setReadOnly(!e.getValue());
            httpProxyUserField.setReadOnly(!e.getValue());
            httpProxyPasswordField.setReadOnly(!e.getValue());
        });
        Div useHttpProxy = util.createConfigurationItemCheckboxDiv(
                EnvironmentConfigurationLabels.useHttpProxyLabels,
                useHttpProxyBox
        );
        add(useHttpProxy);

        Div httpProxyHost = util.createConfigurationItemTextFieldDiv(
                EnvironmentConfigurationLabels.httpProxyHostLabels,
                httpProxyHostField
        );
        add(httpProxyHost);

        httpProxyPortField.setWidth("300px");
        Div httpProxyPort = util.createConfigurationItemTextFieldDiv(
                EnvironmentConfigurationLabels.httpProxyPortLabels,
                httpProxyPortField
        );
        add(httpProxyPort);

        Div httpProxyUser = util.createConfigurationItemTextFieldDiv(
                EnvironmentConfigurationLabels.httpProxyUserLabels,
                httpProxyUserField
        );
        add(httpProxyUser);

        Div httpProxyPassword = util.createConfigurationItemTextFieldDiv(
                EnvironmentConfigurationLabels.httpProxyPasswordLabels,
                httpProxyPasswordField
        );
        add(httpProxyPassword);

        useHttpsProxyBox.addValueChangeListener(e -> {
            httpsProxyHostField.setReadOnly(!e.getValue());
            httpsProxyPortField.setReadOnly(!e.getValue());
            httpsProxyUserField.setReadOnly(!e.getValue());
            httpsProxyPasswordField.setReadOnly(!e.getValue());
        });
        Div useHttpsProxy = util.createConfigurationItemCheckboxDiv(
                EnvironmentConfigurationLabels.useHttpsProxyLabels,
                useHttpsProxyBox
        );
        add(useHttpsProxy);

        Div httpsProxyHost = util.createConfigurationItemTextFieldDiv(
                EnvironmentConfigurationLabels.httpsProxyHostLabels,
                httpsProxyHostField
        );
        add(httpsProxyHost);

        httpsProxyPortField.setWidth("300px");
        Div httpsProxyPort = util.createConfigurationItemTextFieldDiv(
                EnvironmentConfigurationLabels.httpsProxyPortLabels,
                httpsProxyPortField
        );
        add(httpsProxyPort);

        Div httpsProxyUser = util.createConfigurationItemTextFieldDiv(
                EnvironmentConfigurationLabels.httpsProxyUserLabels,
                httpsProxyUserField
        );
        add(httpsProxyUser);

        Div httpsProxyPassword = util.createConfigurationItemTextFieldDiv(
                EnvironmentConfigurationLabels.httpsProxyPasswordLabels,
                httpsProxyPasswordField
        );
        add(httpsProxyPassword);
    }
}
