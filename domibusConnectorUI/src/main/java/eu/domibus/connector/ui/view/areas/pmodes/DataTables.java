/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.ui.view.areas.pmodes;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.common.service.DCKeyStoreService;
import eu.domibus.connector.domain.model.DomibusConnectorAction;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.domibus.connector.domain.model.DomibusConnectorKeystore;
import eu.domibus.connector.domain.model.DomibusConnectorPModeSet;
import eu.domibus.connector.domain.model.DomibusConnectorParty;
import eu.domibus.connector.domain.model.DomibusConnectorService;
import eu.domibus.connector.ui.component.LumoLabel;
import eu.domibus.connector.ui.layout.DCVerticalLayoutWithTitleAndHelpButton;
import eu.domibus.connector.ui.service.WebKeystoreService.CertificateInfo;
import eu.domibus.connector.ui.service.WebPModeService;
import eu.domibus.connector.ui.utils.UiStyle;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import eu.domibus.connector.ui.view.areas.configuration.util.ConfigurationUtil;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Represents a DataTables class that provides functionality for displaying and manipulating data in
 * a table format.
 *
 * @see DCVerticalLayoutWithTitleAndHelpButton
 */
@Component
@UIScope
@Route(value = DataTables.ROUTE, layout = PmodeLayout.class)
@Order(2)
@TabMetadata(title = "PMode-Set Data", tabGroup = PmodeLayout.TAB_GROUP_NAME)
public class DataTables extends DCVerticalLayoutWithTitleAndHelpButton
    implements AfterNavigationObserver {
    public static final String ROUTE = "pmodedata";
    public static final String TITLE = "PMode-Set Data";
    public static final String HELP_ID = "ui/pmodes/pmodeset_data.html";
    WebPModeService pmodeService;
    ConfigurationUtil util;
    DomibusConnectorPModeSet activePModeSet;
    LumoLabel uploadedAt;
    LumoLabel noActivePModeSet;
    Div areaNoActivePModeSetDiv = new Div();
    VerticalLayout activePModeSetLayout = new VerticalLayout();
    Anchor downloadPModesAnchor = new Anchor();
    TextArea description = new TextArea("Description:");
    Button updateDescription = new Button("Update PMode-Set description");
    LumoLabel updateDescriptionResult = new LumoLabel();
    Grid<CertificateInfo> connectorstoreInformationGrid;
    TextField connectorstorePassword = new TextField("Connectorstore password:");
    LumoLabel connectorstoreResultLabel = new LumoLabel();
    Button updateConnectorstorePassword = new Button("Update connectorstore password");
    Grid<DomibusConnectorParty> partyGrid;
    Grid<DomibusConnectorAction> actionGrid;
    Grid<DomibusConnectorService> serviceGrid;
    Grid<DomibusConnectorPModeSet> connectorPModeSetGrid;

    /**
     * Constructor.
     *
     * @param pmodeService The WebPModeService dependency used to retrieve the current PMode set.
     * @param util         The ConfigurationUtil dependency used for configuration purposes.
     */
    public DataTables(@Autowired WebPModeService pmodeService, @Autowired ConfigurationUtil util) {
        super(HELP_ID, TITLE);

        this.pmodeService = pmodeService;
        this.util = util;

        // CAVE: activePModeSet can be null!!
        activePModeSet = this.pmodeService.getCurrentPModeSet(
            DomibusConnectorBusinessDomain.getDefaultMessageLaneId()).orElse(null);

        createActivePmodeSetDiv();

        VerticalLayout histPModeSetsDiv = createPModeHistory();

        var main = new VerticalLayout(activePModeSetLayout, histPModeSetsDiv);
        main.setAlignItems(Alignment.STRETCH);
        main.setHeight("100%");
        add(main);
        setHeight("100vh");
        setWidth(UiStyle.WIDTH_100_VW);
    }

    private void createActivePmodeSetDiv() {
        activePModeSetLayout.setWidth(UiStyle.WIDTH_100_VW);

        noActivePModeSet = createChapterText(
            "No active PModes-Set found! Please import PModes and Connectorstore!");
        noActivePModeSet.getStyle().set(UiStyle.TAG_COLOR, "red");

        activePModeSetLayout.add(areaNoActivePModeSetDiv);

        LumoLabel activePModeSetLabel = createChapterText("Active PMode Set data:");

        activePModeSetLayout.add(activePModeSetLabel);

        var uploadedAtHeader = new LumoLabel("Active PMode Set uploaded at: ");
        uploadedAt = new LumoLabel();
        activePModeSetLayout.add(uploadedAtHeader);
        activePModeSetLayout.add(uploadedAt);

        downloadPModesAnchor.getElement().setAttribute(UiStyle.TAG_DOWNLOAD, true);
        downloadPModesAnchor.setTarget("_blank");
        downloadPModesAnchor.setTitle("Download active PModes");

        var downloadActivePModesButton = new LumoLabel("Download active PModes");
        downloadPModesAnchor.add(downloadActivePModesButton);
        activePModeSetLayout.add(downloadPModesAnchor);

        activePModeSetLayout.add(description);

        updateDescription.addClickListener(e -> {
            updateDescriptionResult.setText("");
            if (StringUtils.isEmpty(description.getValue())) {
                updateDescriptionResult.setText("Description must not be empty!");
                updateDescriptionResult.getStyle().set(UiStyle.TAG_COLOR, "red");
                return;
            }
            activePModeSet.setDescription(description.getValue());
            pmodeService.updateActivePModeSetDescription(activePModeSet);
            updateDescriptionResult.setText("Description updated.");
            updateDescriptionResult.getStyle().set(UiStyle.TAG_COLOR, "green");
        });
        activePModeSetLayout.add(updateDescription);
        activePModeSetLayout.add(updateDescriptionResult);

        activePModeSetLayout.add(createServicesDiv());
        activePModeSetLayout.add(createActionsDiv());
        activePModeSetLayout.add(createPartiesDiv());
        activePModeSetLayout.add(createConnectorstoreDiv());
    }

    private VerticalLayout createPModeHistory() {
        var histPModeSetDiv = new VerticalLayout();
        histPModeSetDiv.setWidth("100vw");

        var histPModeSetLabel = createChapterText("Previous PMode Sets:");

        histPModeSetDiv.add(histPModeSetLabel);

        connectorPModeSetGrid = new Grid<>();

        connectorPModeSetGrid.addColumn(DomibusConnectorPModeSet::getCreateDate)
                             .setHeader("Created date")
                             .setWidth(UiStyle.WIDTH_500_PX).setSortable(true).setResizable(true);
        connectorPModeSetGrid.addColumn(DomibusConnectorPModeSet::getDescription)
                             .setHeader("Description")
                             .setWidth(UiStyle.WIDTH_500_PX).setSortable(true).setResizable(true);
        connectorPModeSetGrid.addComponentColumn(this::createDownloadPModesAnchor)
                             .setHeader("PModes").setWidth("200px").setSortable(false)
                             .setResizable(true);
        connectorPModeSetGrid.setWidth("1220px");
        connectorPModeSetGrid.setHeight(UiStyle.WIDTH_320_PX);
        connectorPModeSetGrid.setMultiSort(true);

        histPModeSetDiv.add(connectorPModeSetGrid);

        return histPModeSetDiv;
    }

    private Anchor createDownloadPModesAnchor(DomibusConnectorPModeSet connectorPModeSet) {
        var downloadPModesAnchor = new Anchor();
        if (connectorPModeSet.getpModes() != null && connectorPModeSet.getCreateDate() != null) {
            final var resource = new StreamResource(
                "pModes-" + connectorPModeSet.getCreateDate() + ".xml",
                () -> new ByteArrayInputStream(connectorPModeSet.getpModes())
            );

            downloadPModesAnchor.setHref(resource);
        } else {
            downloadPModesAnchor.setEnabled(false);
        }
        downloadPModesAnchor.getElement().setAttribute(UiStyle.TAG_DOWNLOAD, true);
        downloadPModesAnchor.setTarget("_blank");
        downloadPModesAnchor.setTitle("Download PModes");

        var downloadPModesButton = new LumoLabel(UiStyle.TAG_DOWNLOAD);
        downloadPModesAnchor.add(downloadPModesButton);

        return downloadPModesAnchor;
    }

    private VerticalLayout createConnectorstoreDiv() {
        var connectorStoreLayout = new VerticalLayout();

        var connectorstoreLabel = createGridTitleText("Connectorstore contents:");
        connectorStoreLayout.add(connectorstoreLabel);

        connectorstoreInformationGrid = util.createKeystoreInformationGrid();
        connectorstoreInformationGrid.setVisible(false);

        connectorStoreLayout.add(connectorstoreInformationGrid);

        connectorStoreLayout.add(connectorstorePassword);

        connectorStoreLayout.add(connectorstoreResultLabel);

        updateConnectorstorePassword.addClickListener(e -> {
            activePModeSet.getConnectorstore().setPasswordPlain(connectorstorePassword.getValue());
            try {
                pmodeService.updateConnectorstorePassword(
                    activePModeSet, connectorstorePassword.getValue());

                reloadPage();
            } catch (Exception e1) {
                String text = e1.getMessage();
                if (e1.getCause() != null) {
                    text += e1.getCause().getMessage();
                }
                connectorstoreResultLabel.setText("Exception updating password! " + text);
                connectorstoreResultLabel.getStyle().set(UiStyle.TAG_COLOR, "red");
            }
        });
        connectorStoreLayout.add(updateConnectorstorePassword);

        return connectorStoreLayout;
    }

    private Div createServicesDiv() {
        var services = new Div();

        var servicesLabel = createGridTitleText("Services within active PMode-Set:");
        services.add(servicesLabel);

        serviceGrid = new Grid<>();

        serviceGrid.addColumn(DomibusConnectorService::getService).setHeader("Service")
                   .setWidth(UiStyle.WIDTH_500_PX).setSortable(true).setResizable(true);
        serviceGrid.addColumn(DomibusConnectorService::getServiceType).setHeader("Service Type")
                   .setWidth(UiStyle.WIDTH_500_PX).setSortable(true).setResizable(true);
        serviceGrid.setWidth("1020px");
        serviceGrid.setHeight(UiStyle.WIDTH_320_PX);
        serviceGrid.setMultiSort(true);
        serviceGrid.setVisible(false);

        services.add(serviceGrid);
        return services;
    }

    private Div createActionsDiv() {
        var actions = new Div();

        var actionsLabel = createGridTitleText("Actions within active PMode-Set:");
        actions.add(actionsLabel);

        actionGrid = new Grid<>();

        actionGrid.addColumn(DomibusConnectorAction::getAction).setHeader("Action")
                  .setWidth("600px").setSortable(true).setResizable(true);
        actionGrid.setWidth("620px");
        actionGrid.setHeight(UiStyle.WIDTH_320_PX);
        actionGrid.setMultiSort(true);
        actionGrid.setVisible(false);

        actions.add(actionGrid);
        return actions;
    }

    private Div createPartiesDiv() {
        var parties = new Div();
        var partiesLabel = createGridTitleText("Parties within active PMode-Set:");
        parties.add(partiesLabel);

        partyGrid = new Grid<>();

        partyGrid.addColumn(DomibusConnectorParty::getPartyId).setHeader("Party ID")
                 .setWidth("250px").setSortable(true).setResizable(true);
        partyGrid.addColumn(DomibusConnectorParty::getPartyIdType).setHeader("Party ID Type")
                 .setWidth(UiStyle.WIDTH_500_PX).setSortable(true).setResizable(true);
        partyGrid.addColumn(DomibusConnectorParty::getRole).setHeader("Role")
                 .setWidth(UiStyle.WIDTH_500_PX)
                 .setSortable(true).setResizable(true);
        partyGrid.addColumn(DomibusConnectorParty::getRoleType).setHeader("Role Type")
                 .setWidth(UiStyle.WIDTH_500_PX).setSortable(true).setResizable(true);
        partyGrid.setWidth("1760px");
        partyGrid.setHeight(UiStyle.WIDTH_320_PX);
        partyGrid.setMultiSort(true);
        partyGrid.setVisible(false);

        parties.add(partyGrid);
        return parties;
    }

    private void reloadPage() {
        UI.getCurrent().navigate(DataTables.class);
    }

    private LumoLabel createChapterText(String text) {
        var label = new LumoLabel();
        label.setText(text);
        label.getStyle().set("font-size", "20px");

        label.getStyle().set("font-style", "bold");
        return label;
    }

    private LumoLabel createGridTitleText(String text) {
        var label = new LumoLabel();
        label.setText(text);
        label.getStyle().set("font-size", "20px");

        label.getStyle().set("font-style", "italic");
        return label;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent arg0) {
        activePModeSet = this.pmodeService.getCurrentPModeSet(
            DomibusConnectorBusinessDomain.getDefaultMessageLaneId()).orElse(null);

        areaNoActivePModeSetDiv.removeAll();

        if (activePModeSet == null) {
            areaNoActivePModeSetDiv.add(noActivePModeSet);
            updateConnectorstorePassword.setEnabled(false);
            downloadPModesAnchor.removeHref();
            updateDescription.setEnabled(false);
        } else {
            updateDescription.setEnabled(true);
            if (activePModeSet.getCreateDate() != null) {
                uploadedAt.setText(activePModeSet.getCreateDate().toString());
            } else {
                uploadedAt.setText("");
            }
            if (activePModeSet.getpModes() != null && activePModeSet.getpModes().length > 1) {
                var downloadFileName = "pModes-";
                downloadFileName += activePModeSet.getCreateDate() != null
                    ? activePModeSet.getCreateDate().toString() : "unknown";
                downloadFileName += ".xml";
                final var resource = new StreamResource(
                    downloadFileName,
                    () -> new ByteArrayInputStream(activePModeSet.getpModes())
                );

                downloadPModesAnchor.setHref(resource);
            } else {
                downloadPModesAnchor.removeHref();
            }
            if (!StringUtils.isEmpty(activePModeSet.getDescription())) {
                description.setValue(activePModeSet.getDescription());
            } else {
                description.setValue("");
            }
            updateDescriptionResult.setText("");

            var serviceList = this.pmodeService.getServiceList();
            if (!CollectionUtils.isEmpty(serviceList)) {
                serviceGrid.setItems(serviceList);
                serviceGrid.setVisible(true);
            }

            List<DomibusConnectorAction> actionList = pmodeService.getActionList();
            if (!CollectionUtils.isEmpty(actionList)) {
                actionGrid.setItems(actionList);
                actionGrid.setVisible(true);
            }

            List<DomibusConnectorParty> partyList = this.pmodeService.getPartyList();
            if (!CollectionUtils.isEmpty(partyList)) {
                partyGrid.setItems(partyList);
                partyGrid.setVisible(true);
            }

            connectorstoreResultLabel.setText("");
            connectorstorePassword.setValue("");
            if (activePModeSet.getConnectorstore() == null) {
                connectorstoreResultLabel.setText("No connectorstore linked to PMode-Set!");
                updateConnectorstorePassword.setEnabled(false);
            } else {
                DomibusConnectorKeystore connectorstore = activePModeSet.getConnectorstore();
                if (connectorstore.getKeystoreBytes() == null
                    || connectorstore.getKeystoreBytes().length < 1) {
                    connectorstoreResultLabel.setText("Connectorstore empty!");
                } else {
                    try {
                        List<CertificateInfo> storeContents = util.getKeystoreInformation(
                            new ByteArrayInputStream(connectorstore.getKeystoreBytes()),
                            connectorstore.getPasswordPlain()
                        );
                        if (!CollectionUtils.isEmpty(storeContents)) {
                            connectorstoreInformationGrid.setItems(storeContents);
                            connectorstoreInformationGrid.setVisible(true);
                        }
                        connectorstoreResultLabel.setText("");
                    } catch (DCKeyStoreService.CannotLoadKeyStoreException e) {
                        String text = e.getMessage();
                        if (e.getCause() != null) {
                            text += e.getCause().getMessage();
                        }
                        connectorstoreResultLabel.setText("Cannot load connectorstore! " + text);
                        connectorstoreInformationGrid.setItems(new ArrayList<>());
                        connectorstoreInformationGrid.setVisible(false);
                    }
                }
                if (!StringUtils.isEmpty(connectorstore.getPasswordPlain())) {
                    connectorstorePassword.setValue(connectorstore.getPasswordPlain());
                }
                updateConnectorstorePassword.setEnabled(true);
            }
            if (!StringUtils.isEmpty(connectorstoreResultLabel.getText())) {
                connectorstoreResultLabel.getStyle().set(UiStyle.TAG_COLOR, "red");
            }
        }

        List<DomibusConnectorPModeSet> inactivePModesList =
            this.pmodeService.getInactivePModeSets();
        connectorPModeSetGrid.setItems(inactivePModesList);
    }
}
