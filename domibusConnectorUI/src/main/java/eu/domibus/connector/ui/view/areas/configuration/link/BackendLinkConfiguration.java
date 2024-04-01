package eu.domibus.connector.ui.view.areas.configuration.link;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.domain.enums.LinkType;
import eu.domibus.connector.link.service.DCLinkFacade;
import eu.domibus.connector.ui.utils.RoleRequired;
import eu.domibus.connector.ui.view.areas.configuration.ConfigurationLayout;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import eu.domibus.connector.ui.view.areas.configuration.link.importoldconfig.ImportOldBackendConfigDialog;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Component
@UIScope
@TabMetadata(title = "Backend Configuration", tabGroup = ConfigurationLayout.TAB_GROUP_NAME)
@Route(value = BackendLinkConfiguration.ROUTE, layout = ConfigurationLayout.class)
@RoleRequired(role = "ADMIN")
@Order(1)
public class BackendLinkConfiguration extends LinkConfiguration {

    public static final String ROUTE = "backendlink";

    public static final String TITLE = "Backend Configuration";

    private final ObjectProvider<ImportOldBackendConfigDialog> importOldBackendConfig;

    private final Button importOldConfigButton = new Button("Import Link Config From 4.2 Connector Properties");

    public BackendLinkConfiguration(
            DCLinkFacade dcLinkFacade,
            ObjectProvider<ImportOldBackendConfigDialog> importOldGatewayConfigDialog,
            ApplicationContext applicationContext) {
        super(dcLinkFacade, LinkType.BACKEND, TITLE);
        this.importOldBackendConfig = importOldGatewayConfigDialog;
        importOldConfigButton.addClickListener(this::importOldConfig);
        super.buttonBar.add(importOldConfigButton);
    }

    private void importOldConfig(ClickEvent<Button> buttonClickEvent) {
        ImportOldBackendConfigDialog dialog = importOldBackendConfig.getObject();
        dialog.setDialogCloseCallback(this::refreshList);
        dialog.open();
    }
}
