/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.pmodes;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.domain.model.DomibusConnectorKeystore;
import eu.domibus.connector.domain.model.DomibusConnectorKeystore.KeystoreType;
import eu.domibus.connector.ui.component.LumoLabel;
import eu.domibus.connector.ui.layout.DCVerticalLayoutWithTitleAndHelpButton;
import eu.domibus.connector.ui.service.WebPModeService;
import eu.domibus.connector.ui.utils.UiStyle;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import eu.domibus.connector.ui.view.areas.configuration.util.ConfigurationUtil;
import io.micrometer.core.instrument.util.StringUtils;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The Import class is a component that allows users to import a PMode-Set, which includes a PMode
 * file and a connectorstore file. It provides a UI for uploading these files, entering a
 * description for the PMode Set, and providing a password for the connector store.
 */
@Component
@UIScope
@Route(value = Import.ROUTE, layout = PmodeLayout.class)
@Order(1)
@TabMetadata(title = "Import PMode-Set", tabGroup = PmodeLayout.TAB_GROUP_NAME)
public class Import extends DCVerticalLayoutWithTitleAndHelpButton
    implements AfterNavigationObserver {
    public static final String ROUTE = "import";
    public static final String TITLE = "Import new active PMode-Set";
    public static final String HELP_ID = "ui/pmodes/pmodeset_import.html";
    public static final String DEFAULT_CONNECTOR_STORE_PW = "changeit";
    WebPModeService pmodeService;
    byte[] pmodeFile = null;
    VerticalLayout areaImportResult = new VerticalLayout();
    Div areaPModeFileUploadResult = new Div();
    LumoLabel connectorPModeFileUploadResultLabel = new LumoLabel();
    byte[] connectorstore = null;
    Div areaConnectorstoreUploadResult = new Div();
    LumoLabel connectorstoreUploadResultLabel = new LumoLabel();
    TextArea connectorPModeSetDescription = new TextArea("Description:");
    TextField connectorstorePwd = new TextField("Connectorstore password");
    ComboBox<KeystoreType> connectorstoreType = new ComboBox<>();

    /**
     * Constructor.
     *
     * @param pmodeService The WebPModeService used for importing the PMode.
     * @param util         The ConfigurationUtil used for importing the PMode.
     */
    public Import(@Autowired WebPModeService pmodeService, @Autowired ConfigurationUtil util) {
        super(HELP_ID, TITLE);

        this.pmodeService = pmodeService;

        var areaPmodeFileUpload = createPModeImportArea();

        add(areaPmodeFileUpload);
        add(areaPModeFileUploadResult);

        var areaPModeSetDescription = createPModeSetDescriptionArea();

        add(areaPModeSetDescription);

        var areaConnectorstoreUpload = createConnectorstoreUploadArea();

        add(areaConnectorstoreUpload);
        add(areaConnectorstoreUploadResult);

        connectorstorePwd.setHelperText("The password of the truststore.");
        add(connectorstorePwd);
        connectorstorePwd.setValue(DEFAULT_CONNECTOR_STORE_PW);

        connectorstoreType.setLabel("Connectorstore type: ");
        connectorstoreType.setHelperText("The type of the truststore. Usually JKS.");
        connectorstoreType.setItems(KeystoreType.values());
        connectorstoreType.setValue(KeystoreType.JKS);
        add(connectorstoreType);

        var importBtn = new Button();
        importBtn.setIcon(new Icon(VaadinIcon.EDIT));
        importBtn.setText("Import PMode-Set");
        importBtn.addClickListener(e -> {
            boolean result;
            result = importPModeSet(
                pmodeFile,
                connectorPModeSetDescription.getValue(),
                connectorstore,
                connectorstorePwd.getValue(),
                connectorstoreType.getValue()
            );
            showOutput(
                result,
                result ? "PMode-Set successfully imported!" : "Import of PMode-Set failed!"
            );
        });
        importBtn.setEnabled(true);

        add(importBtn);

        add(areaImportResult);
    }

    private Div createPModeImportArea() {
        var areaPmodeFileUpload = new Div();

        areaPmodeFileUpload.add(new LumoLabel("Upload new PMode file:"));

        var buffer = new MemoryBuffer();

        var upload = new Upload(buffer);
        upload.setMaxFiles(1);
        upload.setId("Upload PModes-File");
        upload.setAcceptedFileTypes("application/xml", "text/xml");

        upload.addSucceededListener(event -> {
            pmodeFile = ((ByteArrayOutputStream) buffer.getFileData().getOutputBuffer())
                .toByteArray();
            String fileName = buffer.getFileName();
            connectorPModeFileUploadResultLabel.setText("File " + fileName + " uploaded");
            connectorPModeFileUploadResultLabel.getStyle()
                                               .set(UiStyle.TAG_COLOR, UiStyle.COLOR_GREEN);
            areaPModeFileUploadResult.add(connectorPModeFileUploadResultLabel);
        });
        upload.addFailedListener(e -> {
            connectorPModeFileUploadResultLabel.setText("File upload failed!");
            connectorPModeFileUploadResultLabel.getStyle().set(UiStyle.TAG_COLOR, "red");
            areaPModeFileUploadResult.add(connectorPModeFileUploadResultLabel);
        });

        areaPmodeFileUpload.add(upload);

        return areaPmodeFileUpload;
    }

    private Div createPModeSetDescriptionArea() {
        var areaPModeSetDescription = new Div();

        connectorPModeSetDescription.setHelperText(
            "Describes the contents of the PMode Set like project or use-case name");
        connectorPModeSetDescription.setRequired(true);

        areaPModeSetDescription.add(connectorPModeSetDescription);

        return areaPModeSetDescription;
    }

    private Div createConnectorstoreUploadArea() {
        var areaConnectorstoreUpload = new Div();

        areaConnectorstoreUpload.add(new LumoLabel("Upload new Connectorstore file:"));

        var buffer = new MemoryBuffer();

        var upload = new Upload(buffer);
        upload.setMaxFiles(1);
        upload.setId("Upload Connectorstore");

        upload.addSucceededListener(event -> {
            connectorstore = ((ByteArrayOutputStream) buffer.getFileData().getOutputBuffer())
                .toByteArray();
            String fileName = buffer.getFileName();
            connectorstoreUploadResultLabel.setText("File " + fileName + " uploaded");
            connectorstoreUploadResultLabel.getStyle().set(UiStyle.TAG_COLOR, UiStyle.COLOR_GREEN);
            areaConnectorstoreUploadResult.add(connectorstoreUploadResultLabel);
        });
        upload.addFailedListener(e -> {
            connectorstoreUploadResultLabel.setText("File upload failed!");
            connectorstoreUploadResultLabel.getStyle().set(UiStyle.TAG_COLOR, "red");
            areaConnectorstoreUploadResult.add(connectorstoreUploadResultLabel);
        });

        areaConnectorstoreUpload.add(upload);

        return areaConnectorstoreUpload;
    }

    private void showOutput(boolean success, String text) {
        areaImportResult.removeAll();
        areaConnectorstoreUploadResult.removeAll();
        areaPModeFileUploadResult.removeAll();

        var resultLabel = new LumoLabel();
        resultLabel.setText("PMode-Set successfully imported!");
        if (success) {
            resultLabel.getStyle().set(UiStyle.TAG_COLOR, UiStyle.COLOR_GREEN);
        } else {
            resultLabel.setText("Import of PMode-Set failed!");
            resultLabel.getStyle().set(UiStyle.TAG_COLOR, "red");
        }
        areaImportResult.add(resultLabel);

        if (success) {
            var area = new TextArea();
            area.setValue(new String(pmodeFile, StandardCharsets.UTF_8));
            area.setWidth("80vw");
            areaImportResult.setWidth("100vw");

            var importPModeLabel = new LumoLabel("Imported P-Mode file: ");
            areaImportResult.add(importPModeLabel);
            areaImportResult.add(area);
        }
    }

    private boolean importPModeSet(
        byte[] pmodeFile, String description, byte[] connectorstore, String connectorStorePwd,
        KeystoreType connectorstoreType) {

        if (pmodeFile == null || pmodeFile.length < 1
            || connectorstore == null || connectorstore.length < 1
            || StringUtils.isEmpty(description)) {
            return false;
        }
        DomibusConnectorKeystore connectorstoreUUID =
            pmodeService.importConnectorstore(
                connectorstore, connectorStorePwd, connectorstoreType
            );

        if (connectorstoreUUID != null) {
            return pmodeService.importPModes(pmodeFile, description, connectorstoreUUID);
        } else {
            return false;
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent arg0) {
        areaImportResult.removeAll();
        areaConnectorstoreUploadResult.removeAll();
        areaPModeFileUploadResult.removeAll();

        connectorPModeSetDescription.setValue("");
        pmodeFile = null;

        connectorstore = null;
        connectorstorePwd.setValue("");
        connectorstoreType.setValue(KeystoreType.JKS);
    }
}
