package eu.domibus.connector.link.api;

import eu.domibus.connector.link.service.SubmitToLinkPartner;
import eu.domibus.connector.domain.model.DomibusConnectorLinkConfiguration;
import jakarta.annotation.Nullable;
import lombok.Data;
import org.springframework.context.ConfigurableApplicationContext;


@Data
public class ActiveLink {

    private LinkPlugin linkPlugin;

    private DomibusConnectorLinkConfiguration linkConfiguration;

    @Nullable
    private ConfigurableApplicationContext childContext;

    @Nullable
    private SubmitToLinkPartner submitToLink;

}
