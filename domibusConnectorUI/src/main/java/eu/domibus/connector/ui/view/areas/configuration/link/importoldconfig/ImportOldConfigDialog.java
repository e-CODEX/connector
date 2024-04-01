package eu.domibus.connector.ui.view.areas.configuration.link.importoldconfig;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import eu.domibus.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.link.service.DCLinkFacade;
import eu.domibus.connector.link.utils.Connector42LinkConfigTo43LinkConfigConverter;
import eu.domibus.connector.ui.view.areas.configuration.ConfigurationPanelFactory;
import eu.domibus.connector.ui.view.areas.configuration.link.DCLinkConfigurationField;
import eu.domibus.connector.ui.view.areas.configuration.link.DCLinkPartnerField;
import eu.domibus.connector.utils.service.BeanToPropertyMapConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;


public abstract class ImportOldConfigDialog extends Dialog {
    private static final Logger LOGGER = LogManager.getLogger(ImportOldConfigDialog.class);

    protected final ObjectProvider<DCLinkConfigurationField> linkConfigurationFieldObjectProvider;
    protected final ObjectProvider<DCLinkPartnerField> linkPartnerFieldObjectProvider;
    protected final BeanToPropertyMapConverter beanToPropertyMapConverter;
    protected final DCLinkFacade dcLinkFacade;
    protected final JdbcTemplate jdbcTemplate;

    TextField linkConfigName = new TextField();

    VerticalLayout layout = new VerticalLayout();

    // Upload
    MemoryBuffer buffer = new MemoryBuffer();
    Upload upload = new Upload(buffer);
    // upload result area
    VerticalLayout resultArea = new VerticalLayout();
    private ConfigurationPanelFactory.DialogCloseCallback dialogCloseCallback;

    public ImportOldConfigDialog(
            ObjectProvider<DCLinkConfigurationField> linkConfigurationFieldObjectProvider,
            ObjectProvider<DCLinkPartnerField> linkPartnerFieldObjectProvider,
            BeanToPropertyMapConverter beanToPropertyMapConverter,
            DCLinkFacade dcLinkFacade, JdbcTemplate jdbcTemplate) {
        this.linkConfigurationFieldObjectProvider = linkConfigurationFieldObjectProvider;
        this.linkPartnerFieldObjectProvider = linkPartnerFieldObjectProvider;
        this.beanToPropertyMapConverter = beanToPropertyMapConverter;
        this.dcLinkFacade = dcLinkFacade;
        this.jdbcTemplate = jdbcTemplate;
        initUi();
    }

    private void initUi() {
        this.setWidth("80%");
        this.setHeightFull();

        add(layout);

        upload.addSucceededListener(this::uploadSecceeded);
        linkConfigName.setLabel("Link Configuration Name");

        linkConfigName.setValue("ImportedLinkConfig");

        layout.add(linkConfigName, upload, resultArea);
    }

    private void uploadSecceeded(SucceededEvent succeededEvent) {
        try {
            InputStream inputStream = buffer.getInputStream();

            Properties properties = new Properties();
            properties.load(inputStream);
            Connector42LinkConfigTo43LinkConfigConverter connector42LinkConfigTo43LinkConfigConverter =
                    new Connector42LinkConfigTo43LinkConfigConverter(
                            beanToPropertyMapConverter,
                            jdbcTemplate,
                            properties
                    );

            List<DomibusConnectorLinkPartner> linkPartners =
                    getLinkPartners(connector42LinkConfigTo43LinkConfigConverter);
            DomibusConnectorLinkConfiguration domibusConnectorLinkConfiguration =
                    linkPartners.stream().findFirst().map(p -> p.getLinkConfiguration()).get();

            DCLinkConfigurationField linkConfigPanel = linkConfigurationFieldObjectProvider.getIfAvailable();
            linkConfigPanel.setReadOnly(true);
            linkConfigPanel.setValue(domibusConnectorLinkConfiguration);
            resultArea.add(linkConfigPanel);

            // show partners....
            for (DomibusConnectorLinkPartner lp : linkPartners) {
                DCLinkPartnerField linkPartnerField = linkPartnerFieldObjectProvider.getIfAvailable();
                linkPartnerField.setReadOnly(true);
                linkPartnerField.setValue(lp);
                resultArea.add(linkPartnerField);
            }

            Button saveButton = new Button("Save Imported Config");
            saveButton.addClickListener(event -> {
                this.save(linkPartners, domibusConnectorLinkConfiguration);
            });
            resultArea.add(saveButton);
        } catch (IOException e) {
            throw new RuntimeException("Unable to parse uploaded file", e);
        }
    }

    protected void save(
            List<DomibusConnectorLinkPartner> linkPartners,
            DomibusConnectorLinkConfiguration linkConfiguration) {
        try {
            dcLinkFacade.createNewLinkConfiguration(linkConfiguration);
            for (DomibusConnectorLinkPartner linkPartner : linkPartners) {
                dcLinkFacade.createNewLinkPartner(linkPartner);
            }
            Notification.show("Successfully imported old config");
            this.close();
        } catch (Exception e) {
            LOGGER.warn("Exception occured while importing old config", e);
            Notification.show("Error during import!");
        }
    }

    public void setDialogCloseCallback(ConfigurationPanelFactory.DialogCloseCallback dialogCloseCallback) {
        this.dialogCloseCallback = dialogCloseCallback;
    }

    public void setOpened(boolean opened) {
        super.setOpened(opened);
        if (!opened && dialogCloseCallback != null) {
            dialogCloseCallback.dialogHasBeenClosed();
        }
    }

    // TODO: refactor this...
    protected abstract List<DomibusConnectorLinkPartner> getLinkPartners(
            Connector42LinkConfigTo43LinkConfigConverter connector42LinkConfigTo43LinkConfigConverter);
}
