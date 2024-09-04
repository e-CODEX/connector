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
import eu.ecodex.connector.controller.service.TransportStateService;
import eu.ecodex.connector.domain.enums.TransportState;
import eu.ecodex.connector.domain.model.DomibusConnectorLinkPartner;
import eu.ecodex.connector.persistence.model.PDomibusConnectorTransportStep;
import eu.ecodex.connector.persistence.model.PDomibusConnectorTransportStepStatusUpdate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;

@CommonPersistenceTest
@DataSet(
    value = "/database/testdata/dbunit/DomibusConnectorTransportStep.xml", strategy = CLEAN_INSERT
)
class DomibusConnectorTransportStepDaoTest {
    @Autowired
    DomibusConnectorTransportStepDao dao;
    @Autowired
    DomibusConnectorMessageDao msgDao;

    @Test
    void getHighestAttemptBy() {
        Optional<Integer> highestAttemptBy = dao.getHighestAttemptBy(
            "msg1",
            new DomibusConnectorLinkPartner.LinkPartnerName(
                "partner1")
        );
        assertThat(highestAttemptBy.get()).isEqualTo(4);
    }

    @Test
    void getHighestAttemptBy_noPartner() {
        Optional<Integer> highestAttemptBy = dao.getHighestAttemptBy(
            "msg1",
            new DomibusConnectorLinkPartner.LinkPartnerName(
                "notexistant")
        );
        assertThat(highestAttemptBy).isEmpty();
    }

    @Test
    void testSaveRetrieve() {
        PDomibusConnectorTransportStep transportStep = new PDomibusConnectorTransportStep();
        transportStep.setAttempt(1);
        transportStep.setConnectorMessageId("msg1");
        transportStep.setLinkPartnerName(new DomibusConnectorLinkPartner.LinkPartnerName("l1"));
        transportStep.setTransportId(new TransportStateService.TransportId("msg1_1"));

        PDomibusConnectorTransportStepStatusUpdate u =
            new PDomibusConnectorTransportStepStatusUpdate();
        u.setCreated(LocalDateTime.now());
        u.setTransportState(TransportState.ACCEPTED);
        u.setText("text");

        transportStep.getStatusUpdates().add(u);

        PDomibusConnectorTransportStep save = dao.save(transportStep);

        Long id = save.getId();

        PDomibusConnectorTransportStep byId = dao.findById(id).get();

        byId.getStatusUpdates().forEach(s -> System.out.println(s));
    }

    @Test
    void testFindStepByLastState() {
        Pageable pageable = Pageable.ofSize(20);
        DomibusConnectorLinkPartner.LinkPartnerName[] lp =
            {new DomibusConnectorLinkPartner.LinkPartnerName("partner1")};

        Assertions.assertAll(
            () -> assertThat(dao.findLastAttemptStepByLastStateAndLinkPartnerIsOneOf(
                                    new String[] {TransportState.FAILED.getDbName()},
                                    lp,
                                    pageable
                                )
                                .getTotalElements()).isEqualTo(2),
            // there should be 2 entries where the last updated state is failed
            () -> assertThat(dao.findLastAttemptStepByLastStateAndLinkPartnerIsOneOf(
                                    new String[] {TransportState.PENDING.getDbName(),
                                        TransportState.FAILED.getDbName()},
                                    lp,
                                    pageable
                                )
                                .getTotalElements()).isEqualTo(3),
            // there should be 3 entries where the last updated state is failed OR pending
            () -> assertThat(dao.findLastAttemptStepByLastStateAndLinkPartnerIsOneOf(
                                    new String[] {TransportState.PENDING.getDbName()},
                                    lp,
                                    pageable
                                )
                                .getTotalElements()).isEqualTo(1)
        );
    }

    @Test
    void testFindStepByLastState_Pending() {
        Pageable pageable = Pageable.ofSize(20);
        DomibusConnectorLinkPartner.LinkPartnerName[] lp =
            {new DomibusConnectorLinkPartner.LinkPartnerName("partner2")};

        Assertions.assertAll(
            () -> assertThat(dao.findLastAttemptStepByLastStateAndLinkPartnerIsOneOf(
                                    new String[] {TransportState.PENDING.getDbName()},
                                    lp,
                                    pageable
                                )
                                .getTotalElements()).isEqualTo(1)
        );
    }

    @Test
    void testFindStepByLastState_Sorting() {
        Pageable pageable = PageRequest.of(0, 20, Sort.by("created").descending());

        DomibusConnectorLinkPartner.LinkPartnerName[] lp =
            {new DomibusConnectorLinkPartner.LinkPartnerName("partner2")};

        Assertions.assertAll(
            () -> assertThat(dao.findLastAttemptStepByLastStateAndLinkPartnerIsOneOf(
                                    new String[] {TransportState.PENDING.getDbName()},
                                    lp,
                                    pageable
                                )
                                .getTotalElements()).isEqualTo(1)
        );
    }

    @Test
    @Sql(statements = {"DELETE FROM DC_TRANSPORT_STEP_STATUS;", "DELETE FROM DC_TRANSPORT_STEP;"})
    void testFindLastAttemptStepByLastStateAndLinkPartnerIsOneOf_withEmptyDB() {
        Page<PDomibusConnectorTransportStep> hallo =
            dao.findLastAttemptStepByLastStateAndLinkPartnerIsOneOf(
                Stream.of(TransportState.values()).map(Enum::toString).toArray(String[]::new),
                Arrays.array(new DomibusConnectorLinkPartner.LinkPartnerName("hallo")),
                Pageable.ofSize(20)
            );

        dao.findLastAttemptStepByLastStateAndLinkPartnerIsOneOf(
            new String[] {},
            Arrays.array(new DomibusConnectorLinkPartner.LinkPartnerName("hallo")),
            Pageable.ofSize(20)
        );
    }

    @Test
    @Disabled
    @Sql(
        scripts = {
            "classpath:/database/testdata/sql/DC_TRANSPORT_STEP_STATUS_CLEAN.sql",
            "classpath:/database/testdata/sql/DC_TRANSPORT_STEP_CLEAN.sql",
            "classpath:/database/testdata/sql/DC_TRANSPORT_STEP.sql",
            "classpath:/database/testdata/sql/DC_TRANSPORT_STEP_STATUS.sql"
        }
    )
    void testLotData() {
        Page<PDomibusConnectorTransportStep> allStates =
            dao.findLastAttemptStepByLastStateAndLinkPartnerIsOneOf(
                Stream.of(TransportState.values()).map(Enum::toString).toArray(String[]::new),
                Arrays.array(
                    new DomibusConnectorLinkPartner.LinkPartnerName("CN=mla_connector_client")),
                Pageable.ofSize(20)
            );

        Page<PDomibusConnectorTransportStep> pending =
            dao.findLastAttemptStepByLastStateAndLinkPartnerIsOneOf(
                Stream.of(TransportState.PENDING).map(Enum::toString).toArray(String[]::new),
                Arrays.array(new DomibusConnectorLinkPartner.LinkPartnerName("mla")),
                Pageable.ofSize(200)
            );
        assertThat(pending.getTotalElements()).isZero();

        List<PDomibusConnectorTransportStep> lastStateIsPending =
            dao.findByMsgLinkPartnerAndLastStateIs(
                new DomibusConnectorLinkPartner.LinkPartnerName("mla"),
                TransportState.PENDING.getDbName()
            );
        assertThat(lastStateIsPending).isEmpty();
    }
}
