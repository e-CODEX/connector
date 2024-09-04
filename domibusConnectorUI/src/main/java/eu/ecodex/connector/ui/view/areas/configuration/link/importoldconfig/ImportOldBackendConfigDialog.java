/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.view.areas.configuration.link.importoldconfig;

import eu.ecodex.connector.domain.model.DomibusConnectorLinkPartner;
import eu.ecodex.connector.link.service.DCLinkFacade;
import eu.ecodex.connector.link.utils.Connector42LinkConfigTo43LinkConfigConverter;
import eu.ecodex.connector.ui.view.areas.configuration.link.DCLinkConfigurationField;
import eu.ecodex.connector.ui.view.areas.configuration.link.DCLinkPartnerField;
import eu.ecodex.connector.utils.service.BeanToPropertyMapConverter;
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
