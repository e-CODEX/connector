package eu.domibus.connector.ui.view.areas.configuration.routing;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import eu.domibus.connector.domain.enums.LinkType;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.link.service.DCLinkFacade;
import eu.domibus.connector.ui.service.WebPModeService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;


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
        comboBox.addCustomValueSetListener(event -> {
            comboBox.setValue(event.getDetail());
        });

        //        if (StringUtils.hasText(routingRule.getLinkName())) {
        //            collect.add(routingRule.getLinkName()); //add current value...
        //            comboBox.setValue(routingRule.getLinkName());
        //        }

        return comboBox;
    }
}
