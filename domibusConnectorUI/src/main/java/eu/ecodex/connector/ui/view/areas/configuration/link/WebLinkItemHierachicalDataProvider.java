/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.view.areas.configuration.link;

import com.vaadin.flow.data.provider.hierarchy.AbstractHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import eu.ecodex.connector.domain.enums.LinkType;
import eu.ecodex.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.ecodex.connector.domain.model.DomibusConnectorLinkPartner;
import eu.ecodex.connector.link.api.LinkPlugin;
import eu.ecodex.connector.link.service.DCLinkFacade;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * The WebLinkItemHierarchicalDataProvider class is a data provider that implements the
 * AbstractHierarchicalDataProvider interface.
 *
 * @see AbstractHierarchicalDataProvider
 */
public class WebLinkItemHierachicalDataProvider
    extends AbstractHierarchicalDataProvider<WebLinkItem, WebLinkItemFilter> {
    private final DCLinkFacade dcLinkFacade;
    private final LinkType linkType;

    /**
     * Constructor.
     *
     * @param dcLinkFacade the DCLinkFacade used for retrieving link information
     * @param linkType     the type of link
     */
    public WebLinkItemHierachicalDataProvider(
        DCLinkFacade dcLinkFacade,
        LinkType linkType) {
        this.dcLinkFacade = dcLinkFacade;
        this.linkType = linkType;
    }

    @Override
    public int getChildCount(HierarchicalQuery<WebLinkItem, WebLinkItemFilter> hierarchicalQuery) {
        return (int) doQuery(hierarchicalQuery).count();
    }

    @Override
    public Stream<WebLinkItem> fetchChildren(
        HierarchicalQuery<WebLinkItem, WebLinkItemFilter> hierarchicalQuery) {
        return doQuery(hierarchicalQuery);
    }

    private Stream<WebLinkItem> doQuery(
        HierarchicalQuery<WebLinkItem, WebLinkItemFilter> hierarchicalQuery) {
        Optional<WebLinkItem> parentOptional = hierarchicalQuery.getParentOptional();
        if (parentOptional.isPresent()) {
            WebLinkItem parent = parentOptional.get();
            return dcLinkFacade.getAllLinksOfType(linkType)
                               .stream()
                               .filter(partner -> Objects.equals(
                                   partner.getLinkConfiguration(),
                                   parent.getLinkConfiguration()
                               ))
                               .map(this::mapToWebLinkItem);
        } else {
            return dcLinkFacade.getAllLinkConfigurations(linkType)
                               .stream()
                               .map(this::mapToWebLinkItem);
        }
    }

    private WebLinkItem mapToWebLinkItem(
        DomibusConnectorLinkConfiguration domibusConnectorLinkConfiguration) {
        var webLinkItem = new WebLinkItem.WebLinkConfigurationItem();
        webLinkItem.setLinkConfiguration(domibusConnectorLinkConfiguration);
        webLinkItem.setLinkPlugin(getPlugin(domibusConnectorLinkConfiguration));
        return webLinkItem;
    }

    private WebLinkItem mapToWebLinkItem(
        DomibusConnectorLinkPartner domibusConnectorLinkConfiguration) {
        var webLinkItem = new WebLinkItem.WebLinkPartnerItem();
        webLinkItem.setLinkPartner(domibusConnectorLinkConfiguration);
        webLinkItem.setLinkPlugin(
            getPlugin(domibusConnectorLinkConfiguration.getLinkConfiguration())
        );
        return webLinkItem;
    }

    private LinkPlugin getPlugin(DomibusConnectorLinkConfiguration config) {
        if (config == null) {
            return null;
        }
        return dcLinkFacade.getLinkPluginByName(config.getLinkImpl()).orElse(null);
    }

    @Override
    public boolean hasChildren(WebLinkItem webLinkItem) {
        DomibusConnectorLinkConfiguration linkConfiguration = webLinkItem.getLinkConfiguration();
        if (linkConfiguration != null) {
            return dcLinkFacade.getAllLinks()
                               .stream()
                               .anyMatch(l -> Objects.equals(
                                   l.getLinkConfiguration(),
                                   linkConfiguration
                               ));
        }
        return false;
    }

    @Override
    public boolean isInMemory() {
        return false;
    }
}
