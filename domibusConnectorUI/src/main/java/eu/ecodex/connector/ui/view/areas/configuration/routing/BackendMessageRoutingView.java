/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.view.areas.configuration.routing;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridSortOrderBuilder;
import com.vaadin.flow.component.html.NativeLabel;
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
import eu.ecodex.connector.controller.routing.DCRoutingRulesManagerImpl;
import eu.ecodex.connector.controller.routing.RoutingRule;
import eu.ecodex.connector.domain.enums.ConfigurationSource;
import eu.ecodex.connector.domain.enums.LinkType;
import eu.ecodex.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.DomibusConnectorLinkPartner;
import eu.ecodex.connector.link.service.DCLinkFacade;
import eu.ecodex.connector.link.utils.Connector42RoutingRulesTo43RoutingRulesConfigConverter;
import eu.ecodex.connector.ui.component.LumoLabel;
import eu.ecodex.connector.ui.layout.DCVerticalLayoutWithTitleAndHelpButton;
import eu.ecodex.connector.ui.service.WebBusinessDomainService;
import eu.ecodex.connector.ui.utils.RoleRequired;
import eu.ecodex.connector.ui.view.areas.configuration.ConfigurationLayout;
import eu.ecodex.connector.ui.view.areas.configuration.TabMetadata;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * This class represents a view for managing backend message routing rules.
 */
@Component
@UIScope
@TabMetadata(title = "Backend Message Routing", tabGroup = ConfigurationLayout.TAB_GROUP_NAME)
@Route(value = BackendMessageRoutingView.ROUTE, layout = ConfigurationLayout.class)
@RoleRequired(role = "ADMIN")
@Order(3)
@SuppressWarnings("squid:S1135")
public class BackendMessageRoutingView extends DCVerticalLayoutWithTitleAndHelpButton
    implements AfterNavigationObserver {
    private static final Logger LOGGER = LogManager.getLogger(BackendMessageRoutingView.class);
    public static final String ROUTE = "backendrouting";
    public static final String TITLE = "Manage Backend Routing Rules";
    public static final String HELP_ID = "ui/configuration/backend_message_routing_ui.html";
    private final DCRoutingRulesManagerImpl dcRoutingRulesManagerImpl;
    private final WebBusinessDomainService webBusinessDomainService;
    private final ObjectFactory<RoutingRuleForm> routingRuleFormObjectFactory;
    private final DCLinkFacade dcLinkFacade;
    private final ObjectFactory<Connector42RoutingRulesTo43RoutingRulesConfigConverter>
        importOldConfigRoutingRulesConverterObjectFactory;
    private Grid<RoutingRule> routingRuleGrid;
    private ComboBox<String> defaultBackendNameSelectField;
    private Map<String, RoutingRule> currentRoutingRules;

    /**
     * Constructor.
     *
     * @param dcRoutingRulesManagerImpl                         The DCRoutingRulesManagerImpl
     *                                                          instance used to hold and manage
     *                                                          routing rules.
     * @param routingRuleFormObjectFactory                      The ObjectFactory instance used to
     *                                                          create RoutingRuleForm instances.
     * @param webBusinessDomainService                          The WebBusinessDomainService
     *                                                          instance used to interact with
     *                                                          business domains.
     * @param dcLinkFacade                                      The DCLinkFacade instance used to
     *                                                          retrieve backend link information.
     * @param importOldConfigRoutingRulesConverterObjectFactory The ObjectFactory instance used to
     *                                                          create Connector42RoutingRulesTo43
     *                                                          RoutingRulesConfigConverter
     *                                                          instances for converting old routing
     *                                                          rule configurations.
     */
    public BackendMessageRoutingView(
        DCRoutingRulesManagerImpl dcRoutingRulesManagerImpl,
        ObjectFactory<RoutingRuleForm> routingRuleFormObjectFactory,
        WebBusinessDomainService webBusinessDomainService,
        DCLinkFacade dcLinkFacade,
        ObjectFactory<Connector42RoutingRulesTo43RoutingRulesConfigConverter>
            importOldConfigRoutingRulesConverterObjectFactory) {
        super(HELP_ID, TITLE);
        this.routingRuleFormObjectFactory = routingRuleFormObjectFactory;
        this.dcRoutingRulesManagerImpl = dcRoutingRulesManagerImpl;
        this.webBusinessDomainService = webBusinessDomainService;
        this.dcLinkFacade = dcLinkFacade;
        this.importOldConfigRoutingRulesConverterObjectFactory =
            importOldConfigRoutingRulesConverterObjectFactory;
        initUI();
    }

    private void initUI() {
        var backendRoutingLabel = new NativeLabel(
            "Here is the configuration where routing rules are configured that define how "
                + "messages are routed to backend(s)."
        );
        add(backendRoutingLabel);

        var routingDescription = new LumoLabel();
        routingDescription.setText("General routing priorities:");
        routingDescription.getStyle().set("font-size", "20px");

        routingDescription.getStyle().set("font-style", "italic");
        add(routingDescription);

        var routingPriorities = new Accordion();

        routingPriorities.add(
            "1. refToMessageId", new LumoLabel(
                "If the message contains a refToMessageId then the backend where the original "
                    + "message was sent from is chosen."));
        routingPriorities.add(
            "2. conversationId", new LumoLabel(
                "If the message is part of a conversation the backend where prior messages of "
                    + "the conversation was sent from is chosen."));
        routingPriorities.add(
            "3. routing Rules", new LumoLabel(
                "This is the part configured on this page. \nIf there is a rule that applies "
                    + "to the message, the backend configured within the rule is chosen."));
        routingPriorities.add(
            "4. default Backend", new LumoLabel(
                "If none of the above is applicable, the default backend is chosen."));

        add(routingPriorities);

        defaultBackendNameSelectField = getBackendNameEditorComponent();
        defaultBackendNameSelectField.setLabel("Configured default backend name");
        defaultBackendNameSelectField.addValueChangeListener(this::defaultBackendChanged);
        add(defaultBackendNameSelectField);

        routingRuleGrid = new Grid<>(RoutingRule.class);
        routingRuleGrid.addColumn(getButtonRenderer());
        routingRuleGrid.getColumns().forEach(c -> c.setResizable(true));

        final List<GridSortOrder<RoutingRule>> sortByPriority =
            new GridSortOrderBuilder<RoutingRule>().thenDesc(
                routingRuleGrid.getColumnByKey("priority")).build();
        routingRuleGrid.sort(sortByPriority);

        this.add(routingRuleGrid);

        var buttonBar = new HorizontalLayout();

        var createNewRoutingRuleButton = new Button("Create new routing rule");
        createNewRoutingRuleButton.addClickListener(this::createNewRoutingRuleClicked);

        var importOldRulesButton = new Button("Import old config");
        importOldRulesButton.addClickListener(this::importOldRulesButtonClicked);

        buttonBar.add(createNewRoutingRuleButton, importOldRulesButton);
        add(buttonBar);
    }

    private void importOldRulesButtonClicked(ClickEvent<Button> buttonClickEvent) {
        final var dialog = new Dialog();
        var layout = new VerticalLayout();
        dialog.add(layout);
        dialog.setWidth("80%");
        dialog.setHeight("80%");

        var importedRulesGrid = new Grid<>(RoutingRule.class);
        importedRulesGrid.getColumns().forEach(c -> c.setResizable(true));

        var connector42LinkConfigTo43LinkConfigConverter =
            importOldConfigRoutingRulesConverterObjectFactory.getObject();
        var routingRules = connector42LinkConfigTo43LinkConfigConverter.getRoutingRules();
        importedRulesGrid.setItems(routingRules);

        var buttonBar = new HorizontalLayout();
        var ok = new Button(VaadinIcon.CHECK.create());
        ok.addClickListener(event -> {
            routingRules.forEach(this::updateAndSaveRoutingRule);
            dialog.close();
        });
        var cancel = new Button(VaadinIcon.CLOSE.create());
        cancel.addClickListener(event -> dialog.close());
        buttonBar.add(ok, cancel);

        var messageLabel = new NativeLabel("The following rules will be imported");
        layout.add(buttonBar, messageLabel, importedRulesGrid);

        dialog.open();
    }

    private void defaultBackendChanged(
        AbstractField.ComponentValueChangeEvent<ComboBox<String>, String>
            comboBoxStringComponentValueChangeEvent) {
        var newBackendName = comboBoxStringComponentValueChangeEvent.getValue();
        dcRoutingRulesManagerImpl.setDefaultBackendName(getCurrentDomain(), newBackendName);
    }

    private void updateUI() {
        defaultBackendNameSelectField.setValue(
            dcRoutingRulesManagerImpl.getDefaultBackendName(getCurrentDomain()));
        Map<String, RoutingRule> backendRoutingRules =
            dcRoutingRulesManagerImpl.getBackendRoutingRules(getCurrentDomain());
        this.currentRoutingRules = backendRoutingRules;
        routingRuleGrid.setItems(backendRoutingRules.values());
    }

    private ComboBox<String> getBackendNameEditorComponent() {
        var collect = dcLinkFacade.getAllLinksOfType(LinkType.BACKEND)
                                  .stream()
                                  .map(DomibusConnectorLinkPartner::getLinkPartnerName)
                                  .map(
                                      DomibusConnectorLinkPartner.LinkPartnerName::getLinkName)
                                  .collect(Collectors.toSet());
        ComboBox<String> comboBox = new ComboBox<>("LinkName");
        comboBox.setItems(collect);
        comboBox.setAllowCustomValue(true);
        comboBox.addCustomValueSetListener(event -> comboBox.setValue(event.getDetail()));

        return comboBox;
    }

    private Renderer<RoutingRule> getButtonRenderer() {
        return new ComponentRenderer<>(
            (RoutingRule routingRule) -> {
                var layout = new HorizontalLayout();
                if (routingRule.getConfigurationSource().equals(ConfigurationSource.DB)) {
                    // edit Button
                    var editButton = new Button();

                    editButton.setIcon(VaadinIcon.WRENCH.create());
                    editButton.addClickListener(clickEvent -> editRoutingRule(routingRule));
                    layout.add(editButton);
                    // delete button
                    var deleteButton = new Button();
                    deleteButton.setIcon(VaadinIcon.TRASH.create());
                    deleteButton.addClickListener(clickEvent -> deleteRoutingRule(routingRule));
                    layout.add(deleteButton);
                }

                return layout;
            });
    }

    private void createNewRoutingRuleClicked(ClickEvent<Button> buttonClickEvent) {
        final var dialog = new Dialog();
        dialog.setModal(true);
        dialog.setHeight("80%");
        dialog.setWidth("80%");
        dialog.setOpened(true);
        dialog.setCloseOnOutsideClick(false);
        dialog.setCloseOnEsc(false);

        var title = new NativeLabel("Add new Routing Rule");

        var saveCancelButton = new HorizontalLayout();
        saveCancelButton.add(title);

        dialog.add(saveCancelButton);

        var statusLabel = new NativeLabel();

        var rrf = this.routingRuleFormObjectFactory.getObject();
        dialog.add(rrf);
        dialog.add(statusLabel);

        var routingRule = new RoutingRule();

        final Binder<RoutingRule> routingRuleBinder = new Binder<>(RoutingRule.class);
        routingRuleBinder.bindInstanceFields(rrf);
        routingRuleBinder.setBean(routingRule);
        routingRuleBinder.setStatusLabel(statusLabel);

        var saveButton = new Button(VaadinIcon.CHECK.create());
        var cancelButton = new Button(VaadinIcon.CLOSE.create());

        cancelButton.addClickListener(e -> dialog.close());

        var updatedRoutingRule = new RoutingRule();
        saveButton.addClickListener(e -> {
            // TODO: save routing rule...
            //            routingRuleBinder.readBean();
            BinderValidationStatus<RoutingRule> validate = routingRuleBinder.validate();
            LOGGER.info("Validation result: [{}]", validate);
            boolean b = routingRuleBinder.writeBeanIfValid(updatedRoutingRule);
            if (b) {
                dialog.close();
                updateAndSaveRoutingRule(updatedRoutingRule);
            }
        });

        saveCancelButton.add(saveButton);
        saveCancelButton.add(cancelButton);
    }

    private void editRoutingRule(RoutingRule r) {
        final var dialog = new Dialog();
        dialog.setModal(true);
        dialog.setHeight("80%");
        dialog.setWidth("80%");
        dialog.setOpened(true);
        dialog.setCloseOnOutsideClick(false);
        dialog.setCloseOnEsc(false);

        var title = new NativeLabel("Edit Routing Rule");

        var saveCancelButton = new HorizontalLayout();
        saveCancelButton.add(title);

        dialog.add(saveCancelButton);

        var statusLabel = new NativeLabel();

        var routingRuleForm = this.routingRuleFormObjectFactory.getObject();
        dialog.add(routingRuleForm);
        dialog.add(statusLabel);

        final Binder<RoutingRule> routingRuleBinder = new Binder<>(RoutingRule.class);
        routingRuleBinder.bindInstanceFields(routingRuleForm);
        routingRuleBinder.readBean(r);
        routingRuleBinder.setStatusLabel(statusLabel);

        var saveButton = new Button(VaadinIcon.CHECK.create());
        var cancelButton = new Button(VaadinIcon.CLOSE.create());

        cancelButton.addClickListener(e -> dialog.close());

        var updatedRoutingRule = new RoutingRule();
        saveButton.addClickListener(e -> {
            // TODO: save routing rule...
            //            routingRuleBinder.readBean();
            BinderValidationStatus<RoutingRule> validate = routingRuleBinder.validate();
            LOGGER.info("Validation result: [{}]", validate);
            boolean b = routingRuleBinder.writeBeanIfValid(updatedRoutingRule);
            if (b) {
                dialog.close();
                updateAndSaveRoutingRule(updatedRoutingRule);
            }
        });

        saveCancelButton.add(saveButton);
        saveCancelButton.add(cancelButton);
    }

    private void updateAndSaveRoutingRule(RoutingRule routingRule) {
        dcRoutingRulesManagerImpl.deleteBackendRoutingRuleFromPersistence(
            webBusinessDomainService.getCurrentBusinessDomain(), routingRule.getRoutingRuleId());
        routingRule = dcRoutingRulesManagerImpl.persistBackendRoutingRule(
            webBusinessDomainService.getCurrentBusinessDomain(), routingRule);
        this.currentRoutingRules.remove(routingRule.getRoutingRuleId());
        this.currentRoutingRules.put(routingRule.getRoutingRuleId(), routingRule);
        this.routingRuleGrid.setItems(this.currentRoutingRules.values());
    }

    private void deleteRoutingRule(RoutingRule r) {
        var dialog = new Dialog();
        dialog.setModal(true);
        dialog.setHeight("80%");
        dialog.setWidth("80%");
        dialog.setOpened(true);

        var deletionLabel = new NativeLabel("Delete Routing Rule?");
        dialog.add(deletionLabel);

        var horizontalLayout = new HorizontalLayout();
        var acceptButton = new Button(VaadinIcon.CHECK.create());
        var cancelButton = new Button(VaadinIcon.CLOSE.create());
        dialog.add(horizontalLayout);
        horizontalLayout.add(acceptButton);
        horizontalLayout.add(cancelButton);

        acceptButton.addClickListener(e -> {
            currentRoutingRules.remove(r.getRoutingRuleId());
            dcRoutingRulesManagerImpl.deleteBackendRoutingRuleFromPersistence(
                webBusinessDomainService.getCurrentBusinessDomain(), r.getRoutingRuleId());
            this.routingRuleGrid.setItems(currentRoutingRules.values());
            dialog.close();
        });
        cancelButton.addClickListener(e -> dialog.close());
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
