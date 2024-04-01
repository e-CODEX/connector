package eu.domibus.connector.ui.service;

import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import org.springframework.stereotype.Component;


@UIScope
@Component
public class WebBusinessDomainService {
    public DomibusConnectorBusinessDomain.BusinessDomainId getCurrentBusinessDomain() {
        // TODO: for IMPL Business Domain Configuration within UI,
        // extend this to retrieve current business Domain
        return DomibusConnectorBusinessDomain.getDefaultMessageLaneId();
    }
}
