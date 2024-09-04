/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.dao;

import static com.github.database.rider.core.api.dataset.SeedStrategy.CLEAN_INSERT;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.database.rider.core.api.dataset.DataSet;
import eu.ecodex.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.ecodex.connector.persistence.model.PDomibusConnectorPModeSet;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@CommonPersistenceTest
@DataSet(value = "/database/testdata/dbunit/DomibusConnectorPModeSet.xml", strategy = CLEAN_INSERT)
class DomibusConnectorPModeSetDaoDBUnit {
    @Autowired
    DomibusConnectorPModeSetDao dao;

    @Test
    void getCurrentActivePModeSet() {
        List<PDomibusConnectorPModeSet> currentActivePModeSet =
            dao.getCurrentActivePModeSet(DomibusConnectorBusinessDomain.getDefaultMessageLaneId());

        assertThat(currentActivePModeSet).hasSize(1);
    }
}
