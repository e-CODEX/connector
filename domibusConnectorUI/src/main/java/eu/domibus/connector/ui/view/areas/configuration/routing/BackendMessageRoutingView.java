package eu.domibus.connector.ui.view.areas.configuration.routing;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridSortOrderBuilder;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.controller.routing.DCRoutingRulesManagerImpl;
import eu.domibus.connector.controller.routing.RoutingRule;
import eu.domibus.connector.domain.enums.ConfigurationSource;
import eu.domibus.connector.domain.enums.LinkType;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.link.service.DCLinkFacade;
import eu.domibus.connector.link.utils.Connector42RoutingRulesTo43RoutingRulesConfigConverter;
import eu.domibus.connector.ui.component.LumoLabel;
import eu.domibus.connector.ui.layout.DCVerticalLayoutWithTitleAndHelpButton;
import eu.domibus.connector.ui.service.WebBusinessDomainService;
import eu.domibus.connector.ui.utils.RoleRequired;
import eu.domibus.connector.ui.view.areas.configuration.ConfigurationLayout;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Component
@UIScope
@TabMetadata(title = "Backend Message Routing", tabGroup = ConfigurationLayout.TAB_GROUP_NAME)
@Route(value = BackendMessageRoutingView.ROUTE, layout = ConfigurationLayout.class)
@RoleRequired(role = "ADMIN")
@Order(3)
public class BackendMessageRoutingView extends DCVerticalLayoutWithTitleAndHelpButton implements AfterNavigationObserver {
    public static final String ROUTE = "backendrouting";
    public static final String TITLE = "Manage Backend Routing Rules";
    public static final String HELP_ID = "ui/configuration/backend_message_routing_ui.html";
    private static final Logger LOGGER = LogManager.getLogger(BackendMessageRoutingView.class);

    private final DCRoutingRulesManagerImpl dcRoutingRulesManagerImpl;
    private final WebBusinessDomainService webBusinessDomainService;
    private final ObjectFactory<RoutingRuleForm> routingRuleFormObjectFactory;
    private final DCLinkFacade dcLinkFacade;
    private final ObjectFactory<Connector42RoutingRulesTo43RoutingRulesConfigConverter>
            importOldConfigRoutingRulesConverterObjectFactory;

    private Grid<RoutingRule> routingRuleGrid;
    private ComboBox<String> defaultBackendNameSelectField;

    private Map<String, RoutingRule> currentRoutingRules;

    public BackendMessageRoutingView(
            DCRoutingRulesManagerImpl dcRoutingRulesManagerImpl,
            ObjectFactory<RoutingRuleForm> routingRuleFormObjectFactory,
            WebBusinessDomainService webBusinessDomainService,
            DCLinkFacade dcLinkFacade,
            ObjectFactory<Connector42RoutingRulesTo43RoutingRulesConfigConverter> importOldConfigRoutingRulesConverterObjectFactory) {
        super(HELP_ID, TITLE);
        this.routingRuleFormObjectFactory = routingRuleFormObjectFactory;
        this.dcRoutingRulesManagerImpl = dcRoutingRulesManagerImpl;
        this.webBusinessDomainService = webBusinessDomainService;
        this.dcLinkFacade = dcLinkFacade;
        this.importOldConfigRoutingRulesConverterObjectFactory = importOldConfigRoutingRulesConverterObjectFactory;
        initUI();
    }

    private void initUI() {

        Label l = new Label(
                "Here is the configuration where routing rules are configured that define how messages are routed to " +
                        "backend(s).");
        add(l);

        LumoLabel routingDescription = new LumoLabel();
        routingDescription.setText("General routing priorities:");
        routingDescription.getStyle().set("font-size", "20px");

        routingDescription.getStyle().set("font-style", "italic");
        add(routingDescription);

        Accordion routingPriorities = new Accordion();

        routingPriorities.add(
                "1. refToMessageId",
                new LumoLabel(
                        "If the message contains a refToMessageId then the backend where the original message was " +
                                "sent" +
                                " from is chosen.")
        );
        routingPriorities.add(
                "2. conversationId",
                new LumoLabel(
                        "If the message is part of a conversation the backend where prior messages of the " +
                                "conversation" +
                                " was sent from is chosen.")
        );
        routingPriorities.add(
                "3. routing Rules",
                new LumoLabel(
                        "This is the part configured on this page. \nIf there is a rule that applies to the message, " +
                                "the backend configured within the rule is chosen.")
        );
        routingPriorities.add(
                "4. default Backend",
                new LumoLabel("If none of the above is applicable, the default backend is chosen.")
        );

        add(routingPriorities);

        // TextField defaultBackendNameTextField = new TextField();
        //        defaultBackendNameTextField.setReadOnly(true); //currently no way to update this over the UI!
        //        defaultBackendNameTextField.setLabel("Configured default backend name");
        //        defaultBackendNameTextField.setValue(dcRoutingRulesManagerImpl.getDefaultBackendName
        //        (getCurrentDomain()));
        //
        //        add(defaultBackendNameTextField);
        defaultBackendNameSelectField = getBackendNameEditorComponent();
        defaultBackendNameSelectField.setLabel("Configured default backend name");
        defaultBackendNameSelectField.addValueChangeListener(this::defaultBackendChanged);
        add(defaultBackendNameSelectField);

        routingRuleGrid = new Grid<>(RoutingRule.class);
        routingRuleGrid.addColumn(getButtonRenderer());
        routingRuleGrid.getColumns().forEach(c -> c.setResizable(true));

        final List<GridSortOrder<RoutingRule>> sortByPriority =
                new GridSortOrderBuilder<RoutingRule>().thenDesc(routingRuleGrid.getColumnByKey("priority")).build();
        routingRuleGrid.sort(sortByPriority);

        this.add(routingRuleGrid);

        HorizontalLayout buttonBar = new HorizontalLayout();

        Button createNewRoutingRuleButton = new Button("Create new routing rule");
        createNewRoutingRuleButton.addClickListener(this::createNewRoutingRuleClicked);

        Button importOldRulesButton = new Button("Import old config");
        importOldRulesButton.addClickListener(this::importOldRulesButtonClicked);

        buttonBar.add(createNewRoutingRuleButton, importOldRulesButton);
        add(buttonBar);
    }

    private void importOldRulesButtonClicked(ClickEvent<Button> buttonClickEvent) {
        Connector42RoutingRulesTo43RoutingRulesConfigConverter connector42LinkConfigTo43LinkConfigConverter =
                importOldConfigRoutingRulesConverterObjectFactory.getObject();
        List<RoutingRule> routingRules = connector42LinkConfigTo43LinkConfigConverter.getRoutingRules();

        final Dialog d = new Dialog();
        VerticalLayout layout = new VerticalLayout();
        d.add(layout);
        d.setWidth("80%");
        d.setHeight("80%");

        Grid<RoutingRule> importedRulesGrid = new Grid<>(RoutingRule.class);
        importedRulesGrid.getColumns().forEach(c -> c.setResizable(true));
        importedRulesGrid.setItems(routingRules);

        HorizontalLayout buttonBar = new HorizontalLayout();
        Button ok = new Button(VaadinIcon.CHECK.create());
        ok.addClickListener(event -> {
            routingRules.forEach(r -> updateAndSaveRoutingRule(r));
            d.close();
        });
        Button cancel = new Button(VaadinIcon.CLOSE.create());
        cancel.addClickListener(event -> d.close());
        buttonBar.add(ok, cancel);

        Label l = new Label("The following rules will be imported");
        layout.add(buttonBar, l, importedRulesGrid);

        d.open();
    }

    private void defaultBackendChanged(AbstractField.ComponentValueChangeEvent<ComboBox<String>, String> comboBoxStringComponentValueChangeEvent) {
        String newBackendName = comboBoxStringComponentValueChangeEvent.getValue();
        dcRoutingRulesManagerImpl.setDefaultBackendName(getCurrentDomain(), newBackendName);
    }

    private void updateUI() {
        defaultBackendNameSelectField.setValue(dcRoutingRulesManagerImpl.getDefaultBackendName(getCurrentDomain()));
        Map<String, RoutingRule> backendRoutingRules =
                dcRoutingRulesManagerImpl.getBackendRoutingRules(getCurrentDomain());
        this.currentRoutingRules = backendRoutingRules;
        routingRuleGrid.setItems(backendRoutingRules.values());
    }

    private ComboBox<String> getBackendNameEditorComponent() {
        Set<String> collect = dcLinkFacade.getAllLinksOfType(LinkType.BACKEND)
                                          .stream()
                                          .map(DomibusConnectorLinkPartner::getLinkPartnerName)
                                          .map(DomibusConnectorLinkPartner.LinkPartnerName::getLinkName)
                                          .collect(Collectors.toSet());
        ComboBox<String> comboBox = new ComboBox<>("LinkName");
        comboBox.setItems(collect);
        comboBox.setAllowCustomValue(true);
        comboBox.addCustomValueSetListener(event -> {
            comboBox.setValue(event.getDetail());
        });

        return comboBox;
    }

    private Renderer<RoutingRule> getButtonRenderer() {
        return new ComponentRenderer<>(
                (RoutingRule routingRule) -> {
                    HorizontalLayout layout = new HorizontalLayout();
                    if (routingRule.getConfigurationSource().equals(ConfigurationSource.DB)) {
                        // edit Button
                        Button editButton = new Button();

                        editButton.setIcon(VaadinIcon.WRENCH.create());
                        editButton.addClickListener(clickEvent -> {
                            editRoutingRule(routingRule);
                        });
                        layout.add(editButton);
                        // delete button
                        Button deleteButton = new Button();
                        deleteButton.setIcon(VaadinIcon.TRASH.create());
                        deleteButton.addClickListener(clickEvent -> {
                            deleteRoutingRule(routingRule);
                        });
                        layout.add(deleteButton);
                    }

                    return layout;
                });
    }

    private void createNewRoutingRuleClicked(ClickEvent<Button> buttonClickEvent) {
        final Dialog d = new Dialog();
        d.setModal(true);
        d.setHeight("80%");
        d.setWidth("80%");
        d.setOpened(true);
        d.setCloseOnOutsideClick(false);
        d.setCloseOnEsc(false);

        Label title = new Label("Add new Routing Rule");

        HorizontalLayout saveCancelButton = new HorizontalLayout();
        saveCancelButton.add(title);

        d.add(saveCancelButton);

        Label statusLabel = new Label();

        RoutingRuleForm rrf = this.routingRuleFormObjectFactory.getObject();
        d.add(rrf);
        d.add(statusLabel);

        RoutingRule r = new RoutingRule();

        final Binder<RoutingRule> routingRuleBinder = new Binder<>(RoutingRule.class);
        routingRuleBinder.bindInstanceFields(rrf);
        routingRuleBinder.setBean(r);
        routingRuleBinder.setStatusLabel(statusLabel);

        Button saveButton = new Button(VaadinIcon.CHECK.create());
        Button cancelButton = new Button(VaadinIcon.CLOSE.create());

        cancelButton.addClickListener(e -> {
            // do nothing...
            d.close();
        });

        RoutingRule updatedRoutingRule = new RoutingRule();
        saveButton.addClickListener(e -> {
            // TODO: save routing rule...
            //            routingRuleBinder.readBean();
            BinderValidationStatus<RoutingRule> validate = routingRuleBinder.validate();
            LOGGER.info("Validation result: [{}]", validate);
            boolean b = routingRuleBinder.writeBeanIfValid(updatedRoutingRule);
            if (b) {
                d.close();
                updateAndSaveRoutingRule(updatedRoutingRule);
            }
        });

        saveCancelButton.add(saveButton);
        saveCancelButton.add(cancelButton);
    }

    private void editRoutingRule(RoutingRule r) {
        final Dialog d = new Dialog();
        d.setModal(true);
        d.setHeight("80%");
        d.setWidth("80%");
        d.setOpened(true);
        d.setCloseOnOutsideClick(false);
        d.setCloseOnEsc(false);

        Label title = new Label("Edit Routing Rule");

        HorizontalLayout saveCancelButton = new HorizontalLayout();
        saveCancelButton.add(title);

        d.add(saveCancelButton);

        Label statusLabel = new Label();

        RoutingRuleForm rrf = this.routingRuleFormObjectFactory.getObject();
        d.add(rrf);
        d.add(statusLabel);

        final Binder<RoutingRule> routingRuleBinder = new Binder<>(RoutingRule.class);
        routingRuleBinder.bindInstanceFields(rrf);
        routingRuleBinder.readBean(r);
        routingRuleBinder.setStatusLabel(statusLabel);

        Button saveButton = new Button(VaadinIcon.CHECK.create());
        Button cancelButton = new Button(VaadinIcon.CLOSE.create());

        cancelButton.addClickListener(e -> {
            // do nothing...
            d.close();
        });

        RoutingRule updatedRoutingRule = new RoutingRule();
        saveButton.addClickListener(e -> {
            // TODO: save routing rule...
            //            routingRuleBinder.readBean();
            BinderValidationStatus<RoutingRule> validate = routingRuleBinder.validate();
            LOGGER.info("Validation result: [{}]", validate);
            boolean b = routingRuleBinder.writeBeanIfValid(updatedRoutingRule);
            if (b) {
                d.close();
                updateAndSaveRoutingRule(updatedRoutingRule);
            }
        });

        saveCancelButton.add(saveButton);
        saveCancelButton.add(cancelButton);
    }

    private void updateAndSaveRoutingRule(RoutingRule rr) {
        dcRoutingRulesManagerImpl.deleteBackendRoutingRuleFromPersistence(
                webBusinessDomainService.getCurrentBusinessDomain(),
                rr.getRoutingRuleId()
        );
        rr = dcRoutingRulesManagerImpl.persistBackendRoutingRule(
                webBusinessDomainService.getCurrentBusinessDomain(),
                rr
        );
        this.currentRoutingRules.remove(rr.getRoutingRuleId());
        this.currentRoutingRules.put(rr.getRoutingRuleId(), rr);
        this.routingRuleGrid.setItems(this.currentRoutingRules.values());
    }

    private void deleteRoutingRule(RoutingRule r) {
        Dialog d = new Dialog();
        d.setModal(true);
        d.setHeight("80%");
        d.setWidth("80%");
        d.setOpened(true);

        Label l = new Label("Delete Routing Rule?");
        d.add(l);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        Button acceptButton = new Button(VaadinIcon.CHECK.create());
        Button cancelButton = new Button(VaadinIcon.CLOSE.create());
        d.add(horizontalLayout);
        horizontalLayout.add(acceptButton);
        horizontalLayout.add(cancelButton);

        acceptButton.addClickListener(e -> {
            currentRoutingRules.remove(r.getRoutingRuleId());
            dcRoutingRulesManagerImpl.deleteBackendRoutingRuleFromPersistence(
                    webBusinessDomainService.getCurrentBusinessDomain(),
                    r.getRoutingRuleId()
            );
            this.routingRuleGrid.setItems(currentRoutingRules.values());
            d.close();
        });
        cancelButton.addClickListener(e -> {
            d.close();
        });
    }


    @Override
    public void afterNavigation(AfterNavigationEvent arg0) {
        updateUI();
    }

    private DomibusConnectorBusinessDomain.BusinessDomainId getCurrentDomain() {
        // TODO: replace in multi tenancy model with service...
        return DomibusConnectorBusinessDomain.getDefaultMessageLaneId();
    }

    // TODO: for validation purpose check DCLinkFacade if backendName is a configured backend
    // warn when backend exists, but deactivated
    // error when backend does not exist
}
