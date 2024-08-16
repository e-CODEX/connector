/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.configuration.routing;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import eu.domibus.connector.domain.enums.LinkType;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.link.service.DCLinkFacade;
import eu.domibus.connector.ui.service.WebPModeService;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * The RoutingRuleForm class is a FormLayout component that represents a form for creating or
 * editing routing rules.
 */
@Component
@Scope(SCOPE_PROTOTYPE)
public class RoutingRuleForm extends FormLayout {
    private final DCLinkFacade dcLinkFacade;
    private final WebPModeService webPModeService;
    private Label configurationSource;
    private ComboBox<String> linkName;
    private TextField description;
    private RoutingExpressionField matchClause;
    private IntegerField priority;
    private TextField routingRuleId;

    /**
     * Constructor.
     *
     * @param dcLinkFacade    The DCLinkFacade object used to interact with the DC links.
     * @param webPModeService The WebPModeService object used to interact with the P-Mode
     *                        configuration.
     */
    public RoutingRuleForm(DCLinkFacade dcLinkFacade, WebPModeService webPModeService) {
        this.dcLinkFacade = dcLinkFacade;
        this.webPModeService = webPModeService;

        initUi();
    }

    private void initUi() {
        this.setResponsiveSteps(new ResponsiveStep("100%", 1));

        configurationSource = new Label("Configuration Source");
        this.add(configurationSource);

        linkName = getBackendNameEditorComponent();
        this.add(linkName);

        description = new TextField("Description");
        this.add(description);

        matchClause = new RoutingExpressionField(this.webPModeService);
        matchClause.setLabel("Routing Expression");
        this.add(matchClause);

        priority = new IntegerField("Priority");
        this.add(priority);

        routingRuleId = new TextField("RoutingRuleId");
        routingRuleId.setReadOnly(true);
        this.add(routingRuleId);
    }

    private ComboBox<String> getBackendNameEditorComponent() {
        Set<String> collect = dcLinkFacade
            .getAllLinksOfType(LinkType.BACKEND)
            .stream()
            .map(DomibusConnectorLinkPartner::getLinkPartnerName)
            .map(DomibusConnectorLinkPartner.LinkPartnerName::getLinkName)
            .collect(Collectors.toSet());
        ComboBox<String> comboBox = new ComboBox<>("LinkName");
        comboBox.setItems(collect);
        comboBox.setAllowCustomValue(true);
        comboBox.addCustomValueSetListener(event -> comboBox.setValue(event.getDetail()));

        return comboBox;
    }
}
