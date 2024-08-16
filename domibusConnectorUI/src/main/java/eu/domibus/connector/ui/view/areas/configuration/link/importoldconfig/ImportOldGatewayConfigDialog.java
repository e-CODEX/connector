/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.configuration.link.importoldconfig;

import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.link.service.DCLinkFacade;
import eu.domibus.connector.link.utils.Connector42LinkConfigTo43LinkConfigConverter;
import eu.domibus.connector.ui.view.areas.configuration.link.DCLinkConfigurationField;
import eu.domibus.connector.ui.view.areas.configuration.link.DCLinkPartnerField;
import eu.domibus.connector.utils.service.BeanToPropertyMapConverter;
import java.util.List;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * The ImportOldGatewayConfigDialog class is a component that extends the ImportOldConfigDialog
 * class. It provides a dialog for importing old gateway configuration into the system.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Lazy
public class ImportOldGatewayConfigDialog extends ImportOldConfigDialog {
    /**
     * Constructor.
     *
     * @param linkConfigurationFieldObjectProvider - A provider object for DCLinkConfigurationField
     *                                             instances
     * @param linkPartnerFieldObjectProvider       - A provider object for DCLinkPartnerField
     *                                             instances
     * @param beanToPropertyMapConverter           - A converter object for converting between bean
     *                                             objects and property maps
     * @param dcLinkFacade                         - An object that provides access to the DC Link
     *                                             data and operations
     * @param jdbcTemplate                         - A Spring JdbcTemplate for performing database
     *                                             operations
     */
    public ImportOldGatewayConfigDialog(
        ObjectProvider<DCLinkConfigurationField> linkConfigurationFieldObjectProvider,
        ObjectProvider<DCLinkPartnerField> linkPartnerFieldObjectProvider,
        BeanToPropertyMapConverter beanToPropertyMapConverter,
        DCLinkFacade dcLinkFacade,
        JdbcTemplate jdbcTemplate
    ) {
        super(
            linkConfigurationFieldObjectProvider, linkPartnerFieldObjectProvider,
            beanToPropertyMapConverter, dcLinkFacade, jdbcTemplate
        );
    }

    @Override
    protected List<DomibusConnectorLinkPartner> getLinkPartners(
        Connector42LinkConfigTo43LinkConfigConverter connector42LinkConfigTo43LinkConfigConverter) {
        return connector42LinkConfigTo43LinkConfigConverter.getGwPartner();
    }
}
