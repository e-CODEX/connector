package eu.domibus.connector.link.api;

import eu.domibus.connector.link.service.SubmitToLinkPartner;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import jakarta.annotation.Nullable;
import lombok.Data;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Optional;

public class ActiveLinkPartner {

    private DomibusConnectorLinkPartner linkPartner;

    private ActiveLink parentLink;

    @Nullable
    private ConfigurableApplicationContext childContext;

    @Nullable
    private SubmitToLinkPartner submitToLink;

    public DomibusConnectorLinkPartner getLinkPartner() {
        return linkPartner;
    }

    public void setLinkPartner(DomibusConnectorLinkPartner linkPartner) {
        this.linkPartner = linkPartner;
    }

    public ActiveLink getParentLink() {
        return parentLink;
    }

    public void setParentLink(ActiveLink parentLink) {
        this.parentLink = parentLink;
    }

    public Optional<ConfigurableApplicationContext> getChildContext() {
        return Optional.ofNullable(childContext);
    }

    public void setChildContext(ConfigurableApplicationContext childContext) {
        this.childContext = childContext;
    }

    public Optional<SubmitToLinkPartner> getSubmitToLink() {
        return Optional.ofNullable(submitToLink);
    }

    public void setSubmitToLink(SubmitToLinkPartner submitToLink) {
        this.submitToLink = submitToLink;
    }
}
