/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.ui.view.areas.configuration.link;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouteConfiguration;
import eu.domibus.connector.domain.enums.ConfigurationSource;
import eu.domibus.connector.domain.enums.LinkType;
import eu.domibus.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.link.api.LinkPlugin;
import eu.domibus.connector.link.api.PluginFeature;
import eu.domibus.connector.link.api.exception.LinkPluginException;
import eu.domibus.connector.link.service.DCLinkFacade;
import eu.domibus.connector.ui.layout.DCVerticalLayoutWithTitleAndHelpButton;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The abstract class {@code LinkConfiguration} represents a configuration UI for managing web
 * links.
 *
 * @see DCVerticalLayoutWithTitleAndHelpButton
 * @see AfterNavigationObserver
 */
@SuppressWarnings("squid:S1135")
public abstract class LinkConfiguration extends DCVerticalLayoutWithTitleAndHelpButton
    implements AfterNavigationObserver {
    public static final String HELP_ID = "ui/configuration/link_configuration.html";
    private final DCLinkFacade dcLinkFacade;
    private final LinkType linkType;
    private final WebLinkItemHierachicalDataProvider webLinkItemHierachicalDataProvider;
    private final TreeGrid<WebLinkItem> treeGrid = new TreeGrid<>();
    protected Button addLinkButton = new Button("Add Link");
    protected HorizontalLayout buttonBar = new HorizontalLayout();

    /**
     * Constructor.
     *
     * @param dcLinkFacade The DCLinkFacade object used to access and manipulate link related data.
     * @param linkType     The type of link.
     * @param TITLE        The title of the link configuration.
     */
    @SuppressWarnings("checkstyle:ParameterName")
    protected LinkConfiguration(
        DCLinkFacade dcLinkFacade,
        LinkType linkType, final String TITLE) {
        super(HELP_ID, TITLE);
        this.webLinkItemHierachicalDataProvider =
            new WebLinkItemHierachicalDataProvider(dcLinkFacade, linkType);
        this.dcLinkFacade = dcLinkFacade;
        this.linkType = linkType;
    }

    @PostConstruct
    private void initUI() {
        this.setSizeFull();
        addAndExpand(buttonBar);
        buttonBar.add(addLinkButton);
        addLinkButton.addClickListener(this::addLinkConfigurationButtonClicked);
        treeGrid.setDataProvider(webLinkItemHierachicalDataProvider);

        treeGrid.addHierarchyColumn(WebLinkItem::getName)
                .setResizable(true)
                .setHeader("Name");

        treeGrid.addComponentColumn((ValueProvider<WebLinkItem, Component>) webLinkItem -> {
                    var editButton = new Button(new Icon(VaadinIcon.EDIT));
                    editButton.addClickListener(event -> editWebLinkItem(webLinkItem, event));
                    return editButton;
                }).setWidth("4em")
                .setResizable(true)
                .setHeader("Edit");

        treeGrid.addComponentColumn((ValueProvider<WebLinkItem, Component>) webLinkItem -> {
                    var deleteButton = new Button(new Icon(VaadinIcon.TRASH));
                    deleteButton.addClickListener(event -> deleteWebLinkItem(webLinkItem, event));
                    deleteButton.setEnabled(
                        webLinkItem.getConfigurationSource() == ConfigurationSource.DB
                    );
                    return deleteButton;
                }).setWidth("4em")
                .setResizable(true)
                .setHeader("Delete");

        treeGrid.addComponentColumn(
                    (ValueProvider<WebLinkItem, ? extends Component>) webLinkItem -> {
                        if (webLinkItem instanceof WebLinkItem.WebLinkConfigurationItem linkItem) {
                            var addButton = new Button(new Icon(VaadinIcon.PLUS));
                            addButton.addClickListener(event -> addLinkPartner(linkItem, event));
                            addButton.setEnabled(
                                webLinkItem.getConfigurationSource() == ConfigurationSource.DB
                            );
                            return addButton;
                        } else {
                            return new Div();
                        }
                    }).setWidth("4em")
                .setResizable(true)
                .setHeader("Add Link Partner");

        treeGrid.addColumn(WebLinkItem::getConfigurationSource)
                .setWidth("4em")
                .setResizable(true)
                .setHeader("Configured via");
        treeGrid.addColumn(WebLinkItem::isEnabled)
                .setWidth("4em")
                .setResizable(true)
                .setHeader("Start on startup");

        treeGrid.addComponentColumn(
                    (ValueProvider<WebLinkItem, ? extends Component>) webLinkItem -> {
                        if (webLinkItem instanceof WebLinkItem.WebLinkConfigurationItem) {
                            var linkConfiguration = webLinkItem.getLinkConfiguration();
                        } else if (webLinkItem instanceof WebLinkItem.WebLinkPartnerItem) {
                            DomibusConnectorLinkPartner d = webLinkItem.getLinkPartner();
                            return dcLinkFacade.isActive(d)
                                ? new Span("running")
                                : new Span("stopped");
                        }
                        return new Div();
                    })
                .setResizable(true)
                .setHeader("Current Link State");

        treeGrid.addComponentColumn(
                    (ValueProvider<WebLinkItem, ? extends Component>) webLinkItem -> {
                        var horizontalLayout = new HorizontalLayout();
                        if (webLinkItem instanceof WebLinkItem.WebLinkPartnerItem) {
                            DomibusConnectorLinkPartner linkPartner = webLinkItem.getLinkPartner();

                            var startLinkButton = new Button(new Icon(VaadinIcon.PLAY));
                            startLinkButton.addClickListener(
                                event -> startLinkButtonClicked(event, linkPartner));
                            startLinkButton.setEnabled(!dcLinkFacade.isActive(linkPartner));
                            horizontalLayout.add(startLinkButton);

                            var stopLinkButton = new Button(new Icon(VaadinIcon.STOP));
                            stopLinkButton.addClickListener(
                                event -> stopLinkButtonClicked(event, linkPartner)
                            );

                            Optional<LinkPlugin> linkPlugin = webLinkItem.getLinkPlugin();
                            boolean stopButtonEnabled = linkPlugin
                                .map(
                                    l -> l.getFeatures()
                                          .contains(PluginFeature.SUPPORTS_LINK_PARTNER_SHUTDOWN)
                                )
                                .orElse(false);
                            stopButtonEnabled = stopButtonEnabled
                                && dcLinkFacade.isActive(linkPartner);
                            stopLinkButton.setEnabled(stopButtonEnabled);
                            horizontalLayout.add(stopLinkButton);
                        }
                        return horizontalLayout;
                    })
                .setResizable(true);

        addAndExpand(treeGrid);
        treeGrid.setSizeFull();
    }

    private void editWebLinkItem(WebLinkItem webLinkItem, ClickEvent<Button> buttonClickEvent) {
        Map<String, String> s = new HashMap<>();
        if (webLinkItem.getConfigurationSource() == ConfigurationSource.DB) {
            s.put(DCLinkConfigurationView.EDIT_MODE_TYPE_QUERY_PARAM, EditMode.EDIT.name());
        } else {
            s.put(DCLinkConfigurationView.EDIT_MODE_TYPE_QUERY_PARAM, EditMode.VIEW.name());
        }
        s.put(DCLinkConfigurationView.LINK_TYPE_QUERY_PARAM, linkType.name());
        // navigate to edit view
        if (webLinkItem instanceof WebLinkItem.WebLinkPartnerItem) {
            var linkPartnerName = webLinkItem.getLinkPartner().getLinkPartnerName();
            var linkName = linkPartnerName.getLinkName();
            var url = RouteConfiguration.forSessionScope()
                                        .getUrl(DCLinkPartnerView.class, linkName);

            var queryParameters = QueryParameters.simple(s);

            getUI().ifPresent(ui -> ui.navigate(url, queryParameters));
        } else if (webLinkItem instanceof WebLinkItem.WebLinkConfigurationItem) {
            DomibusConnectorLinkConfiguration linkConfiguration =
                webLinkItem.getLinkConfiguration();
            var configName = linkConfiguration.getConfigName().toString();

            String url = RouteConfiguration.forSessionScope()
                                           .getUrl(DCLinkConfigurationView.class, configName);

            var queryParameters = QueryParameters.simple(s);
            getUI().ifPresent(ui -> ui.navigate(url, queryParameters));
        }
    }

    private void deleteWebLinkItem(WebLinkItem webLinkItem, ClickEvent<Button> buttonClickEvent) {
        // TODO: open delete dialog...
        if (webLinkItem instanceof WebLinkItem.WebLinkPartnerItem) {
            dcLinkFacade.deleteLinkPartner(webLinkItem.getLinkPartner());
        } else if (webLinkItem instanceof WebLinkItem.WebLinkConfigurationItem) {
            DomibusConnectorLinkConfiguration linkConfiguration =
                webLinkItem.getLinkConfiguration();
            dcLinkFacade.deleteLinkConfiguration(linkConfiguration);
        }
        refreshList();
    }

    private void addLinkPartner(
        WebLinkItem.WebLinkConfigurationItem webLinkItem, ClickEvent<Button> buttonClickEvent) {

        Map<String, String> s = new HashMap<>();
        s.put(DCLinkConfigurationView.LINK_TYPE_QUERY_PARAM, linkType.name());
        s.put(DCLinkConfigurationView.EDIT_MODE_TYPE_QUERY_PARAM, EditMode.CREATE.name());
        s.put(
            DCLinkPartnerView.LINK_CONFIGURATION_NAME,
            webLinkItem.getLinkConfiguration().getConfigName().toString()
        );
        var url = RouteConfiguration.forSessionScope().getUrl(DCLinkPartnerView.class);
        var queryParameters = QueryParameters.simple(s);
        getUI().ifPresent(ui -> ui.navigate(url, queryParameters));
    }

    private void stopLinkButtonClicked(
        ClickEvent<Button> event, DomibusConnectorLinkPartner linkPartner) {
        try {
            dcLinkFacade.shutdownLinkPartner(linkPartner);
            Notification.show("Link " + linkPartner.getLinkPartnerName() + " stopped");
        } finally {
            refreshList();
        }
    }

    private void startLinkButtonClicked(
        ClickEvent<Button> event, DomibusConnectorLinkPartner linkPartner) {
        try {
            dcLinkFacade.startLinkPartner(linkPartner);
            Notification.show("Link " + linkPartner.getLinkPartnerName() + " started");
        } catch (LinkPluginException e) {
            Notification.show(
                "Link " + linkPartner.getLinkPartnerName() + " start failed!\n" + e.getMessage());
        }
        refreshList();
    }

    private void addLinkConfigurationButtonClicked(ClickEvent<Button> buttonClickEvent) {

        String url = RouteConfiguration.forSessionScope()
                                       .getUrl(DCLinkConfigurationView.class
                                       );
        Map<String, String> s = new HashMap<>();
        s.put(DCLinkConfigurationView.LINK_TYPE_QUERY_PARAM, linkType.name());
        s.put(DCLinkConfigurationView.EDIT_MODE_TYPE_QUERY_PARAM, EditMode.CREATE.name());
        var queryParameters = QueryParameters.simple(s);
        getUI().ifPresent(ui -> ui.navigate(url, queryParameters));
    }

    public void afterNavigation(AfterNavigationEvent event) {
        refreshList();
    }

    protected void refreshList() {

        webLinkItemHierachicalDataProvider.refreshAll();
        treeGrid.expand(webLinkItemHierachicalDataProvider
                            .fetchChildren(new HierarchicalQuery<>(new WebLinkItemFilter(), null))
                            .collect(Collectors.toSet())); // expand root items.

        // TODO: show warning label, if more than one link or configuration with same name detected!
    }
}
