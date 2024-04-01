package eu.domibus.connector.ui.view.areas.configuration.link;

import com.vaadin.flow.data.provider.hierarchy.AbstractHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import eu.domibus.connector.domain.enums.LinkType;
import eu.domibus.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.link.api.LinkPlugin;
import eu.domibus.connector.link.service.DCLinkFacade;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;


public class WebLinkItemHierachicalDataProvider extends AbstractHierarchicalDataProvider<WebLinkItem,
        WebLinkItemFilter> {
    private final DCLinkFacade dcLinkFacade;
    private final LinkType linkType;

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
    public Stream<WebLinkItem> fetchChildren(HierarchicalQuery<WebLinkItem, WebLinkItemFilter> hierarchicalQuery) {
        return doQuery(hierarchicalQuery);
    }

    @Override
    public boolean hasChildren(WebLinkItem webLinkItem) {
        DomibusConnectorLinkConfiguration linkConfiguration = webLinkItem.getLinkConfiguration();
        if (linkConfiguration != null) {
            return dcLinkFacade.getAllLinks()
                               .stream()
                               .anyMatch(l -> Objects.equals(l.getLinkConfiguration(), linkConfiguration));
        }
        return false;
    }

    private Stream<WebLinkItem> doQuery(HierarchicalQuery<WebLinkItem, WebLinkItemFilter> hierarchicalQuery) {

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

    private WebLinkItem mapToWebLinkItem(DomibusConnectorLinkConfiguration domibusConnectorLinkConfiguration) {
        WebLinkItem.WebLinkConfigurationItem webLinkItem = new WebLinkItem.WebLinkConfigurationItem();
        webLinkItem.setLinkConfiguration(domibusConnectorLinkConfiguration);
        webLinkItem.setLinkPlugin(getPlugin(domibusConnectorLinkConfiguration));
        return webLinkItem;
    }

    private WebLinkItem mapToWebLinkItem(DomibusConnectorLinkPartner domibusConnectorLinkConfiguration) {
        WebLinkItem.WebLinkPartnerItem webLinkItem = new WebLinkItem.WebLinkPartnerItem();
        webLinkItem.setLinkPartner(domibusConnectorLinkConfiguration);
        webLinkItem.setLinkPlugin(getPlugin(domibusConnectorLinkConfiguration.getLinkConfiguration()));
        return webLinkItem;
    }

    private LinkPlugin getPlugin(DomibusConnectorLinkConfiguration config) {
        if (config == null) {
            return null;
        }
        return dcLinkFacade.getLinkPluginByName(config.getLinkImpl()).orElse(null);
    }

    @Override
    public boolean isInMemory() {
        return false;
    }
}
