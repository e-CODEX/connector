package eu.domibus.connector.link.api;

import eu.domibus.connector.link.service.SubmitToLinkPartner;
import eu.domibus.connector.domain.model.DomibusConnectorLinkConfiguration;
import lombok.Data;
import org.springframework.context.ConfigurableApplicationContext;

import javax.annotation.CheckForNull;

@Data
public class ActiveLink {

    private LinkPlugin linkPlugin;

    private DomibusConnectorLinkConfiguration linkConfiguration;

    @CheckForNull
    private ConfigurableApplicationContext childContext;

    @CheckForNull
    private SubmitToLinkPartner submitToLink;

}
