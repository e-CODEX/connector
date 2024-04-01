package eu.domibus.connector.ui.view.areas.configuration.security;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.security.configuration.DCEcodexContainerProperties;
import eu.domibus.connector.ui.layout.DCVerticalLayoutWithTitleAndHelpButton;
import eu.domibus.connector.ui.utils.RoleRequired;
import eu.domibus.connector.ui.view.areas.configuration.ConfigurationLayout;
import eu.domibus.connector.ui.view.areas.configuration.ConfigurationPanelFactory;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import eu.domibus.connector.ui.view.areas.configuration.security.importoldconfig.ImportEcodexContainerConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Component
@UIScope
@Route(value = EcxContainerConfigPanel.ROUTE, layout = ConfigurationLayout.class)
@RoleRequired(role = "ADMIN")
@TabMetadata(title = "ECodex Container Configuration", tabGroup = ConfigurationLayout.TAB_GROUP_NAME)
@Order(5)
public class EcxContainerConfigPanel extends DCVerticalLayoutWithTitleAndHelpButton {
    public static final String ROUTE = "ecxContainer";
    public static final String TITLE = "ECodex Container Configuration";
    public static final String HELP_ID = "ui/configuration/ecodex_container_configuration.html";

    public EcxContainerConfigPanel(
            ConfigurationPanelFactory configurationPanelFactory,
            ObjectProvider<ImportEcodexContainerConfig> importEcodexContainerConfig,
            EcxContainerConfigForm form) {
        super(HELP_ID, TITLE);
        ConfigurationPanelFactory.ConfigurationPanel<DCEcodexContainerProperties> configurationPanel
                = configurationPanelFactory.createConfigurationPanel(form, DCEcodexContainerProperties.class);

        Button b = new Button("Import old config");
        b.addClickListener(event -> {
            ImportEcodexContainerConfig dialog = importEcodexContainerConfig.getObject();
            //          due some reason dialogCloseActionListener does not work
            //            dialog.addDialogCloseActionListener((ComponentEventListener<Dialog.DialogCloseActionEvent>)
            //            event1 -> configurationPanel.refreshUI());
            dialog.setDialogCloseCallback(configurationPanel::refreshUI);
            dialog.open();
        });
        this.add(b);
        this.add(configurationPanel);
    }
}
