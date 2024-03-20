package eu.domibus.connector.persistence.dao;

import com.github.database.rider.core.api.dataset.DataSet;
import eu.domibus.connector.controller.service.TransportStateService;
import eu.domibus.connector.domain.enums.TransportState;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.persistence.model.PDomibusConnectorTransportStep;
import eu.domibus.connector.persistence.model.PDomibusConnectorTransportStepStatusUpdate;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.github.database.rider.core.api.dataset.SeedStrategy.CLEAN_INSERT;
import static org.assertj.core.api.Assertions.assertThat;

@CommonPersistenceTest
@DataSet(value = "/database/testdata/dbunit/DomibusConnectorTransportStep.xml", strategy = CLEAN_INSERT)
public class DomibusConnectorTransportStepDaoTest {

    @Autowired
    DomibusConnectorTransportStepDao dao;

    @Autowired
    DomibusConnectorMessageDao msgDao;

    @Test
    void getHighestAttemptBy() {
        Optional<Integer> highestAttemptBy = dao.getHighestAttemptBy("msg1", new DomibusConnectorLinkPartner.LinkPartnerName("partner1"));
        assertThat(highestAttemptBy.get()).isEqualTo(4);
    }

    @Test
    void getHighestAttemptBy_noPartner() {
        Optional<Integer> highestAttemptBy = dao.getHighestAttemptBy("msg1", new DomibusConnectorLinkPartner.LinkPartnerName("notexistant"));
        assertThat(highestAttemptBy).isEmpty();
    }

    @Test
    void testSaveRetrieve() {

        PDomibusConnectorTransportStep transportStep = new PDomibusConnectorTransportStep();
        transportStep.setAttempt(1);
        transportStep.setConnectorMessageId("msg1");
        transportStep.setLinkPartnerName(new DomibusConnectorLinkPartner.LinkPartnerName("l1"));
        transportStep.setTransportId(new TransportStateService.TransportId("msg1_1"));

        PDomibusConnectorTransportStepStatusUpdate u = new PDomibusConnectorTransportStepStatusUpdate();
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
    public void testFindStepByLastState() {
        Pageable pageable = Pageable.ofSize(20);
        DomibusConnectorLinkPartner.LinkPartnerName[] lp = {new DomibusConnectorLinkPartner.LinkPartnerName("partner1")};

        Assertions.assertAll(
                () -> assertThat(dao.findLastAttemptStepByLastStateAndLinkPartnerIsOneOf(
                        new String[]{TransportState.FAILED.getDbName()},
                                lp,
                                pageable)
                        .getTotalElements()).isEqualTo(2), //there should be 2 entries where the last updated state is failed
                () -> assertThat(dao.findLastAttemptStepByLastStateAndLinkPartnerIsOneOf(
                        new String[]{TransportState.PENDING.getDbName(), TransportState.FAILED.getDbName()},
                                lp,
                                pageable)
                        .getTotalElements()).isEqualTo(3), //there should be 3 entries where the last updated state is failed OR pending
                () -> assertThat(dao.findLastAttemptStepByLastStateAndLinkPartnerIsOneOf(
                        new String[]{TransportState.PENDING.getDbName()},
                                lp,
                                pageable)
                        .getTotalElements()).isEqualTo(1) //there should be 1 entry where the last updated state is pending
        );

    }


    @Test
    public void testFindStepByLastState_Pending() {
        Pageable pageable = Pageable.ofSize(20);
        DomibusConnectorLinkPartner.LinkPartnerName[] lp = {new DomibusConnectorLinkPartner.LinkPartnerName("partner2")};

        Assertions.assertAll(
                () -> assertThat(dao.findLastAttemptStepByLastStateAndLinkPartnerIsOneOf(
                                new String[]{TransportState.PENDING.getDbName()},
                                lp,
                                pageable)
                        .getTotalElements()).isEqualTo(1) //there should be 1 entry with last state of pending
        );
    }

    @Test
    public void testFindStepByLastState_Sorting() {
        Pageable pageable = PageRequest.of(0, 20, Sort.by("created").descending());

        DomibusConnectorLinkPartner.LinkPartnerName[] lp = {new DomibusConnectorLinkPartner.LinkPartnerName("partner2")};

        Assertions.assertAll(
                () -> assertThat(dao.findLastAttemptStepByLastStateAndLinkPartnerIsOneOf(
                                new String[]{TransportState.PENDING.getDbName()},
                                lp,
                                pageable)
                        .getTotalElements()).isEqualTo(1) //there should be 1 entry with last state of pending
        );
    }

    @Test
    @Sql(statements = {"DELETE FROM DC_TRANSPORT_STEP_STATUS;", "DELETE FROM DC_TRANSPORT_STEP;"})
    public void testFindLastAttemptStepByLastStateAndLinkPartnerIsOneOf_withEmptyDB() {

        Page<PDomibusConnectorTransportStep> hallo = dao.findLastAttemptStepByLastStateAndLinkPartnerIsOneOf(
                Stream.of(TransportState.values()).map(Enum::toString).toArray(String[]::new),
                Arrays.array(new DomibusConnectorLinkPartner.LinkPartnerName("hallo")), Pageable.ofSize(20)
        );

        dao.findLastAttemptStepByLastStateAndLinkPartnerIsOneOf(
                new String[]{},
                Arrays.array(new DomibusConnectorLinkPartner.LinkPartnerName("hallo")), Pageable.ofSize(20)
        );

    }


    @Test
    @Disabled
    @Sql(scripts = {
            "classpath:/database/testdata/sql/DC_TRANSPORT_STEP_STATUS_CLEAN.sql",
            "classpath:/database/testdata/sql/DC_TRANSPORT_STEP_CLEAN.sql",
            "classpath:/database/testdata/sql/DC_TRANSPORT_STEP.sql",
            "classpath:/database/testdata/sql/DC_TRANSPORT_STEP_STATUS.sql"
    })
    public void testLotData() {


        Page<PDomibusConnectorTransportStep> allStates = dao.findLastAttemptStepByLastStateAndLinkPartnerIsOneOf(
                Stream.of(TransportState.values()).map(Enum::toString).toArray(String[]::new),
                Arrays.array(new DomibusConnectorLinkPartner.LinkPartnerName("CN=mla_connector_client")), Pageable.ofSize(20)
        );
//        assertThat(allStates.getTotalElements()).isEqualTo(560);

        Page<PDomibusConnectorTransportStep> pending = dao.findLastAttemptStepByLastStateAndLinkPartnerIsOneOf(
                Stream.of(TransportState.PENDING).map(Enum::toString).toArray(String[]::new),
                Arrays.array(new DomibusConnectorLinkPartner.LinkPartnerName("mla")), Pageable.ofSize(200)
        );
        assertThat(pending.getTotalElements()).isEqualTo(0);

        List<PDomibusConnectorTransportStep> lastStateIsPending =
                dao.findByMsgLinkPartnerAndLastStateIs(new DomibusConnectorLinkPartner.LinkPartnerName("mla"), TransportState.PENDING.getDbName());
        assertThat(lastStateIsPending).hasSize(0);


    }



}