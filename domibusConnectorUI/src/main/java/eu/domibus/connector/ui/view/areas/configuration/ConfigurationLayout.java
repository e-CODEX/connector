package eu.domibus.connector.ui.view.areas.configuration;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.persistence.service.DomibusConnectorPropertiesPersistenceService;
import eu.domibus.connector.ui.layout.DCMainLayout;
import eu.domibus.connector.ui.utils.DCTabHandler;
import eu.domibus.connector.ui.view.areas.configuration.util.ConfigurationUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import java.util.Objects;


@UIScope
@org.springframework.stereotype.Component
@RoutePrefix(ConfigurationLayout.ROUTE)
@ParentLayout(DCMainLayout.class)
public class ConfigurationLayout extends VerticalLayout implements BeforeEnterObserver, RouterLayout {

    public static final String ROUTE = "configuration";
    public static final String TAB_GROUP_NAME = "Configuration";

    protected final static Logger LOGGER = LoggerFactory.getLogger(ConfigurationLayout.class);
    private Div pageContent;

    Button saveConfiguration;
    Button resetConfiguration;
    Button reloadConfiguration;

    private DCTabHandler DCTabHandler = new DCTabHandler();


    private final DomibusConnectorPropertiesPersistenceService propertiesPersistenceService;
    private final ConfigurationUtil util;
    private final ApplicationContext applicationContext;

    public ConfigurationLayout(DomibusConnectorPropertiesPersistenceService propertiesPersistenceService, ConfigurationUtil util, ApplicationContext applicationContext) {
        this.propertiesPersistenceService = propertiesPersistenceService;
        this.util = util;
        this.applicationContext = applicationContext;
    }


    @PostConstruct
    public void init() {

        DCTabHandler.createTabs(applicationContext, TAB_GROUP_NAME);

        pageContent = new Div();
        pageContent.setSizeFull();

//        add(createConfigurationButtonBar()); //deactivated, because does not work
        add(DCTabHandler.getTabs(), pageContent);

        this.expand(pageContent);
        this.setHeight("80vh");
    }


    public void showRouterLayoutContent(HasElement content) {
        if (content != null) {
            pageContent.getElement()
                    .appendChild(Objects.requireNonNull(content.getElement()));
        }
    }


    private HorizontalLayout createConfigurationButtonBar() {
        HorizontalLayout configurationButtonBar = new HorizontalLayout();


        Div reset = new Div();
        String resetActionText = "Discard Changes";
        resetConfiguration = new Button(
                new Icon(VaadinIcon.REFRESH));
        resetConfiguration.setText(resetActionText);
        resetConfiguration.addClickListener(e -> {
            Button confirmButton = new Button("Confirm");
            Dialog confirmDialog = createConfigurationConfirmDialog(
                    resetActionText,
                    confirmButton,
                    "All changes since the last time of saving will be discarded."
            );
            confirmButton.addClickListener(e2 -> {
                util.resetConfiguration();
                confirmDialog.close();
            });

            confirmDialog.open();
        });
        reset.add(resetConfiguration);
        configurationButtonBar.add(reset);

        Div save = new Div();
        String saveActionText = "Save Changes";
        saveConfiguration = new Button(
                new Icon(VaadinIcon.EDIT));
        saveConfiguration.setText(saveActionText);
        saveConfiguration.addClickListener(e -> {
            Button confirmButton = new Button("Confirm");
            Dialog confirmDialog = createConfigurationConfirmDialog(
                    saveActionText,
                    confirmButton,
                    "All changed configuration properties will be saved into the database table DOMIBUS_CONNECTOR_PROPERTIES.",
                    "Be aware that changes to the configuration except backend client configuration will only take effect after restart of the domibusConnector.",
                    "Also take note that the configured properties in the property files will NOT be changed!"
            );
            confirmButton.addClickListener(e2 -> {
                util.saveConfiguration();
                confirmDialog.close();
            });

            confirmDialog.open();
        });
        save.add(saveConfiguration);
        configurationButtonBar.add(save);

        Div reload = new Div();
        String reloadActionText = "Reload Configuration";
        reloadConfiguration = new Button(
                new Icon(VaadinIcon.FILE_REFRESH));
        reloadConfiguration.setText(reloadActionText);
        reloadConfiguration.addClickListener(e -> {
            Button confirmButton = new Button("Confirm");
            Dialog confirmDialog = createConfigurationConfirmDialog(
                    reloadActionText,
                    confirmButton,
                    "All configuration properties will be reloaded to the state they had when the domibusConnector was started the last time.",
                    "Be aware that this also effects configuration properties that have already been changed and saved since the last start of the domibusConnector!",
                    "If there are changed properties that are already saved, those changes will be reset in the database to the status of the last startup as well."
            );
            confirmButton.addClickListener(e2 -> {
                util.reloadConfiguration();
                confirmDialog.close();
            });

            confirmDialog.open();
        });
        reload.add(reloadConfiguration);
        reload.getStyle().set("align", "right");
        configurationButtonBar.add(reload);

        configurationButtonBar.setWidth("900px");
        configurationButtonBar.expand(save);
        configurationButtonBar.setHeight("20px");
        configurationButtonBar.setPadding(true);
        configurationButtonBar.getStyle().set("padding-bottom", "30px");

        return configurationButtonBar;
    }

    private Dialog createConfigurationConfirmDialog(String headerString, Button confirmButton, String... infoStrings) {
        Dialog confirmDialog = new Dialog();

        Div headerContent = new Div();
        Label header = new Label(headerString);
        header.getStyle().set("font-weight", "bold");
        header.getStyle().set("font-style", "italic");
        header.getStyle().set("margin-bottom", "10px");
        headerContent.getStyle().set("text-align", "center");
        headerContent.getStyle().set("padding", "10px");
        headerContent.getStyle().set("margin-bottom", "10px");
        headerContent.add(header);
        confirmDialog.add(headerContent);

        Div content = new Div();
        VerticalLayout contentLayout = new VerticalLayout();
        for (String infoString : infoStrings) {
            Label infoLabel = new Label(infoString);
            infoLabel.getStyle().set("margin-top", "0px");
            contentLayout.add(infoLabel);
        }
        contentLayout.getStyle().set("text-align", "center");
        contentLayout.getStyle().set("padding", "0px");
        contentLayout.setAlignItems(Alignment.CENTER);
        content.add(contentLayout);
        content.getStyle().set("text-align", "center");
        confirmDialog.add(content);

        Div confirmCancelButtonContent = new Div();
        confirmCancelButtonContent.getStyle().set("text-align", "center");
        confirmCancelButtonContent.getStyle().set("padding", "10px");
        confirmCancelButtonContent.add(confirmButton);

        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(e3 -> {
            confirmDialog.close();
        });
        confirmCancelButtonContent.add(cancelButton);
        confirmDialog.add(confirmCancelButtonContent);

        return confirmDialog;
    }


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        DCTabHandler.beforeEnter(event);

//        boolean enabled = SecurityUtils.isUserInRole(UserRole.ADMIN.toString());
//        saveConfiguration.setEnabled(enabled);
//        reloadConfiguration.setEnabled(enabled);
//        resetConfiguration.setEnabled(enabled);

    }


}
