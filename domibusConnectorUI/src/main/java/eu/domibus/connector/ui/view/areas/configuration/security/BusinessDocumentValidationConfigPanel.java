package eu.domibus.connector.ui.view.areas.configuration.security;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.common.service.ConfigurationPropertyManagerService;
import eu.domibus.connector.common.service.CurrentBusinessDomain;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageBuilder;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageDetailsBuilder;
import eu.domibus.connector.security.configuration.DCBusinessDocumentValidationConfigurationProperties;
import eu.domibus.connector.security.configuration.DCEcodexContainerProperties;
import eu.domibus.connector.security.container.service.ECodexContainerFactoryService;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinderFactory;
import eu.domibus.connector.ui.layout.DCVerticalLayoutWithTitleAndHelpButton;
import eu.domibus.connector.ui.utils.RoleRequired;
import eu.domibus.connector.ui.view.areas.configuration.ConfigurationLayout;
import eu.domibus.connector.ui.view.areas.configuration.ConfigurationPanelFactory;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import eu.domibus.connector.ui.view.areas.configuration.security.importoldconfig.ImportBusinessDocConfig;
import eu.ecodex.dss.model.BusinessContent;
import eu.ecodex.dss.model.ECodexContainer;
import eu.ecodex.dss.service.ECodexContainerService;
import eu.ecodex.dss.service.ECodexException;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.validation.Validator;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.Collectors;

@Component
@UIScope
@Route(value = BusinessDocumentValidationConfigPanel.ROUTE, layout = ConfigurationLayout.class)
@RoleRequired(role = "ADMIN")
@TabMetadata(title = "ECodex Business Document Verification", tabGroup = ConfigurationLayout.TAB_GROUP_NAME)
@Order(4)
public class BusinessDocumentValidationConfigPanel extends DCVerticalLayoutWithTitleAndHelpButton {
	
	public static final String TITLE = "ECodex Business Document Verification";
	public static final String HELP_ID = "ui/configuration/business_document_verification.html";

    public static final String ROUTE = "businessDocumentValidation";

    public BusinessDocumentValidationConfigPanel(ConfigurationPanelFactory configurationPanelFactory,
                                                 ObjectProvider<ImportBusinessDocConfig> importBusinessDocConfig,
                                                 BusinessDocumentValidationConfigForm form) {
    	super(HELP_ID, TITLE);
        ConfigurationPanelFactory.ConfigurationPanel<DCBusinessDocumentValidationConfigurationProperties> configurationPanel
                = configurationPanelFactory.createConfigurationPanel(form, DCBusinessDocumentValidationConfigurationProperties.class);
        Button b = new Button("Import old config");
        b.addClickListener(event -> {
            ImportBusinessDocConfig dialog = importBusinessDocConfig.getObject();
//          due some reason dialogCloseActionListener does not work
//            dialog.addDialogCloseActionListener((ComponentEventListener<Dialog.DialogCloseActionEvent>) event1 -> configurationPanel.refreshUI());
            dialog.setDialogCloseCallback(configurationPanel::refreshUI);
            dialog.open();
        });
        this.add(b);
        this.add(configurationPanel);
    }

}
