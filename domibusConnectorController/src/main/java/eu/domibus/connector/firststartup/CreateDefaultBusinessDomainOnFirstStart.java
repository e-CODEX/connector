/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.firststartup;

import eu.domibus.connector.common.persistence.dao.DomibusConnectorBusinessDomainDao;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.domibus.connector.persistence.model.PDomibusConnectorMessageLane;
import eu.domibus.connector.tools.logging.LoggingMarker;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Configuration;

/**
 * Class responsible for creating the default business domain on the first start.
 */
@Configuration(value = CreateDefaultBusinessDomainOnFirstStart.BEAN_NAME)
public class CreateDefaultBusinessDomainOnFirstStart {
    public static final String BEAN_NAME = "CreateDefaultBusinessDomainOnFirstStartBean";
    private static final Logger LOGGER =
        LogManager.getLogger(CreateDefaultBusinessDomainOnFirstStart.class);
    private final DomibusConnectorBusinessDomainDao messageLaneDao;

    public CreateDefaultBusinessDomainOnFirstStart(
        DomibusConnectorBusinessDomainDao messageLaneDao) {
        this.messageLaneDao = messageLaneDao;
    }

    /**
     * Creates the default business domain if it doesn't already exist.
     * If the default business domain does not exist in the database,
     * a new instance of `PDomibusConnectorMessageLane` is created and
     * saved to the database.
     *
     * <p>This method is executed after the bean initialization is complete
     * and within a transaction.
     */
    @PostConstruct
    @Transactional
    public void createDefaultBusinessDomain() {
        Optional<PDomibusConnectorMessageLane> byName = messageLaneDao.findByName(
            new DomibusConnectorBusinessDomain.BusinessDomainId(
                DomibusConnectorBusinessDomain.DEFAULT_LANE_NAME));
        if (byName.isEmpty()) {
            LOGGER.info(
                LoggingMarker.Log4jMarker.CONFIG, "Create default Business Message Domain [{}]",
                DomibusConnectorBusinessDomain.DEFAULT_LANE_NAME
            );
            var newLane = new PDomibusConnectorMessageLane();
            newLane.setDescription("The default business message domain");
            newLane.setName(new DomibusConnectorBusinessDomain.BusinessDomainId(
                DomibusConnectorBusinessDomain.DEFAULT_LANE_NAME));
            messageLaneDao.save(newLane);
        }
    }
}
