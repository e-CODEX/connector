package eu.domibus.connector.persistence.service.impl;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageConfirmation;
import eu.domibus.connector.persistence.model.PDomibusConnectorEvidence;
import eu.domibus.connector.persistence.service.impl.helper.EvidenceTypeMapper;
import eu.domibus.connector.persistence.service.impl.helper.MapperHelper;

import javax.annotation.Nullable;
import java.io.UnsupportedEncodingException;

public class MessageConfirmationMapper {


    public static DomibusConnectorMessageConfirmation mapFromDbToDomain(PDomibusConnectorEvidence e) {
        DomibusConnectorMessageConfirmation confirmation = new DomibusConnectorMessageConfirmation();
        if (e.getEvidence() != null) {
            confirmation.setEvidence(e.getEvidence().getBytes());
        }
        confirmation.setEvidenceType(EvidenceTypeMapper.mapEvidenceFromDbToDomain(e.getType()));
        return confirmation;
    }

    public static PDomibusConnectorEvidence mapFromDomainIntoDb(PDomibusConnectorEvidence evidence, DomibusConnectorMessageConfirmation confirmation) {
        evidence.setType(EvidenceTypeMapper.mapEvidenceTypeFromDomainToDb(confirmation.getEvidenceType()));
        if (confirmation.getEvidence() != null) {
            evidence.setEvidence(MapperHelper.convertByteArrayToString(confirmation.getEvidence()));
        }
        return evidence;
    }

    public static PDomibusConnectorEvidence mapFromDomainToDb(DomibusConnectorMessageConfirmation confirmation) {
        PDomibusConnectorEvidence evidence = new PDomibusConnectorEvidence();
        return mapFromDomainIntoDb(evidence, confirmation);
    }

}
