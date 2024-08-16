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
 * The ImportOldBackendConfigDialog class represents a dialog box for importing old backend
 * configurations.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Lazy
public class ImportOldBackendConfigDialog extends ImportOldConfigDialog {
    /**
     * Constructor.
     *
     * @param linkConfigurationFieldObjectProvider The object provider for
     *                                             DCLinkConfigurationField.
     * @param linkPartnerFieldObjectProvider       The object provider for DCLinkPartnerField.
     * @param beanToPropertyMapConverter           The converter for converting beans to property
     *                                             maps.
     * @param dcLinkFacade                         The facade for handling DC link operations.
     * @param jdbcTemplate                         The JDBC template for accessing the database.
     */
    public ImportOldBackendConfigDialog(
        ObjectProvider<DCLinkConfigurationField> linkConfigurationFieldObjectProvider,
        ObjectProvider<DCLinkPartnerField> linkPartnerFieldObjectProvider,
        BeanToPropertyMapConverter beanToPropertyMapConverter,
        DCLinkFacade dcLinkFacade,
        JdbcTemplate jdbcTemplate) {
        super(
            linkConfigurationFieldObjectProvider, linkPartnerFieldObjectProvider,
            beanToPropertyMapConverter, dcLinkFacade, jdbcTemplate
        );
    }

    @Override
    protected List<DomibusConnectorLinkPartner> getLinkPartners(
        Connector42LinkConfigTo43LinkConfigConverter connector42LinkConfigTo43LinkConfigConverter) {
        return connector42LinkConfigTo43LinkConfigConverter.getBackendPartners();
    }
}
