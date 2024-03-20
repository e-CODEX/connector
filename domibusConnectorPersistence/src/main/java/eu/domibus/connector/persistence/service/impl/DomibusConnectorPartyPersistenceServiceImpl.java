package eu.domibus.connector.persistence.service.impl;

import eu.domibus.connector.domain.model.DomibusConnectorParty;
import eu.domibus.connector.persistence.dao.DomibusConnectorPartyDao;
import eu.domibus.connector.persistence.model.PDomibusConnectorParty;
//import eu.domibus.connector.persistence.model.PDomibusConnectorPartyPK;
import eu.domibus.connector.persistence.service.DomibusConnectorPartyPersistenceService;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DomibusConnectorPartyPersistenceServiceImpl implements DomibusConnectorPartyPersistenceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomibusConnectorPartyPersistenceServiceImpl.class);

    DomibusConnectorPartyDao partyDao;

    @Autowired
    public void setPartyDao(DomibusConnectorPartyDao partyDao) {
        this.partyDao = partyDao;
    }

    @Override
    public eu.domibus.connector.domain.model.DomibusConnectorParty getParty(String partyId, String role) {
//        PDomibusConnectorPartyPK pk = new PDomibusConnectorPartyPK(partyId, role);
//        PDomibusConnectorParty party = partyDao.findById(pk).get();
//        PDomibusConnectorParty party = partyDao.findByPartyIdAndRoleAndDeletedIsFalse(partyId, role).get(0);
//        return mapPartyToDomain(party);
        return null;
    }

    @Override
    public eu.domibus.connector.domain.model.DomibusConnectorParty getPartyByPartyId(String partyId) {
//        PDomibusConnectorParty party = partyDao.findByPartyIdAndDeletedIsNot(partyId).get(0);
//        return mapPartyToDomain(party);
        return null;
    }

    @Override
    public DomibusConnectorParty persistNewParty(DomibusConnectorParty newParty) {
        PDomibusConnectorParty dbParty = mapPartyToPersistence(newParty);
        dbParty = this.partyDao.save(dbParty);
        return mapPartyToDomain(dbParty);
    }

    @Override
    public List<DomibusConnectorParty> getPartyList() {
        final List<DomibusConnectorParty> parties = new ArrayList<>();
//        this.partyDao.findAllByDeletedIsFalse().forEach((dbParty) -> {
//            DomibusConnectorParty p = mapPartyToDomain(dbParty);
//            parties.add(p);
//        });
        return parties;
    }

    @Override
    public void deleteParty(DomibusConnectorParty party) {
//        PDomibusConnectorParty dbParty = PartyMapper.mapPartyToPersistence(party);
//        this.partyDao.setDeleted(dbParty);
    }

    @Override
    public DomibusConnectorParty updateParty(DomibusConnectorParty oldParty, DomibusConnectorParty newParty) {
        PDomibusConnectorParty newDbParty = mapPartyToPersistence(newParty);
        PDomibusConnectorParty updatedParty = this.partyDao.save(newDbParty);
        return mapPartyToDomain(updatedParty);
    }

    @Nullable
    PDomibusConnectorParty mapPartyToPersistence(@Nullable DomibusConnectorParty party) {
        if (party == null) {
            return null;
        }
        if (party.getDbKey() != null) {
            Optional<PDomibusConnectorParty> byId = this.partyDao.findById(party.getDbKey());
            if (byId.isPresent()) {
                return byId.get();
            }
        }

        PDomibusConnectorParty persistenceParty = new PDomibusConnectorParty();
        BeanUtils.copyProperties(party, persistenceParty);
        return persistenceParty;

    }

    @Nullable
    public DomibusConnectorParty mapPartyToDomain(@Nullable PDomibusConnectorParty persistenceParty) {
        if (persistenceParty != null) {
            eu.domibus.connector.domain.model.DomibusConnectorParty p
                    = new eu.domibus.connector.domain.model.DomibusConnectorParty(
                    persistenceParty.getPartyId(),
                    persistenceParty.getPartyIdType(),
                    persistenceParty.getRole()
            );
            p.setDbKey(persistenceParty.getId());
            p.setPartyName(persistenceParty.getPmodePartyIdentifier());
            p.setRoleType(persistenceParty.getRoleType());
            return p;
        }
        return null;
    }


}
