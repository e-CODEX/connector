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
import eu.domibus.connector.ui.view.areas.configuration.link.importoldconfig.ImportOldGatewayConfigDialog;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Component
@UIScope
@TabMetadata(title = "Gateway Configuration", tabGroup = ConfigurationLayout.TAB_GROUP_NAME)
@Route(value = GatewayLinkConfiguration.ROUTE, layout = ConfigurationLayout.class)
@RoleRequired(role = "ADMIN")
@Order(2)
public class GatewayLinkConfiguration extends LinkConfiguration {
    public static final String ROUTE = "gwlink";
    public static final String TITLE = "Gateway Configuration";

    private final ObjectProvider<ImportOldGatewayConfigDialog> importOldGatewayConfigDialog;
    private final Button importOldConfigButton = new Button("Import Link Config From 4.2 Connector Properties");

    public GatewayLinkConfiguration(
            DCLinkFacade dcLinkFacade,
            ObjectProvider<ImportOldGatewayConfigDialog> importOldGatewayConfigDialog,
            ApplicationContext applicationContext) {
        super(dcLinkFacade, LinkType.GATEWAY, TITLE);
        this.importOldGatewayConfigDialog = importOldGatewayConfigDialog;
        importOldConfigButton.addClickListener(this::importOldConfig);
        super.buttonBar.add(importOldConfigButton);
    }

    private void importOldConfig(ClickEvent<Button> buttonClickEvent) {
        ImportOldGatewayConfigDialog dialog = importOldGatewayConfigDialog.getObject();
        dialog.setDialogCloseCallback(this::refreshList);
        dialog.open();
    }
}
