package eu.domibus.connector.ui.view.areas.configuration.security;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.security.configuration.DCBusinessDocumentValidationConfigurationProperties;
import eu.domibus.connector.ui.layout.DCVerticalLayoutWithTitleAndHelpButton;
import eu.domibus.connector.ui.utils.RoleRequired;
import eu.domibus.connector.ui.view.areas.configuration.ConfigurationLayout;
import eu.domibus.connector.ui.view.areas.configuration.ConfigurationPanelFactory;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import eu.domibus.connector.ui.view.areas.configuration.security.importoldconfig.ImportBusinessDocConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Component
@UIScope
@Route(value = BusinessDocumentValidationConfigPanel.ROUTE, layout = ConfigurationLayout.class)
@RoleRequired(role = "ADMIN")
@TabMetadata(title = "ECodex Business Document Verification", tabGroup = ConfigurationLayout.TAB_GROUP_NAME)
@Order(4)
public class BusinessDocumentValidationConfigPanel extends DCVerticalLayoutWithTitleAndHelpButton {
    public static final String TITLE = "ECodex Business Document Verification";
    public static final String HELP_ID = "ui/configuration/business_document_verification.html";
    public static final String ROUTE = "businessDocumentValidation";

    public BusinessDocumentValidationConfigPanel(
            ConfigurationPanelFactory configurationPanelFactory,
            ObjectProvider<ImportBusinessDocConfig> importBusinessDocConfig,
            BusinessDocumentValidationConfigForm form) {
        super(HELP_ID, TITLE);
        ConfigurationPanelFactory.ConfigurationPanel<DCBusinessDocumentValidationConfigurationProperties>
                configurationPanel
                = configurationPanelFactory.createConfigurationPanel(
                form,
                DCBusinessDocumentValidationConfigurationProperties.class
        );
        Button b = new Button("Import old config");
        b.addClickListener(event -> {
            ImportBusinessDocConfig dialog = importBusinessDocConfig.getObject();
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
