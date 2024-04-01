package eu.domibus.connector.ui.view.areas.configuration.processing;

import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.controller.spring.ConnectorMessageProcessingProperties;
import eu.domibus.connector.ui.layout.DCVerticalLayoutWithTitleAndHelpButton;
import eu.domibus.connector.ui.utils.RoleRequired;
import eu.domibus.connector.ui.view.areas.configuration.ConfigurationLayout;
import eu.domibus.connector.ui.view.areas.configuration.ConfigurationPanelFactory;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Component
@UIScope
@Route(value = ConnectorMessageProcessingConfigPanel.ROUTE, layout = ConfigurationLayout.class)
@RoleRequired(role = "ADMIN")
@TabMetadata(title = "General Message Processing Config", tabGroup = ConfigurationLayout.TAB_GROUP_NAME)
@Order(40)
public class ConnectorMessageProcessingConfigPanel extends DCVerticalLayoutWithTitleAndHelpButton {
    public static final String TITLE = "General Message Processing Config";
    public static final String HELP_ID = "ui/configuration/message_processing_config.html";
    public static final String ROUTE = "messageProcessingConfig";

    public ConnectorMessageProcessingConfigPanel(
            ConfigurationPanelFactory configurationPanelFactory,
            ConnectorMessageProcessingPropertiesConfigForm form) {
        super(HELP_ID, TITLE);
        ConfigurationPanelFactory.ConfigurationPanel<ConnectorMessageProcessingProperties> configurationPanel
                = configurationPanelFactory.createConfigurationPanel(form, ConnectorMessageProcessingProperties.class);

        this.add(configurationPanel);
    }
}


