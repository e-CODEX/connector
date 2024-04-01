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
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import eu.domibus.connector.ui.view.areas.configuration.util.ConfigurationUtil;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;


@Component
@UIScope
@Route(value = Import.ROUTE, layout = PmodeLayout.class)
@Order(1)
@TabMetadata(title = "Import PMode-Set", tabGroup = PmodeLayout.TAB_GROUP_NAME)
public class Import extends DCVerticalLayoutWithTitleAndHelpButton implements AfterNavigationObserver {
    public static final String ROUTE = "import";
    public static final String TITLE = "Import new active PMode-Set";
    public static final String HELP_ID = "ui/pmodes/pmodeset_import.html";
    public static final String DEFAULT_CONNECTOR_STORE_PW = "changeit";

    WebPModeService pmodeService;

    byte[] pmodeFile = null;

    VerticalLayout areaImportResult = new VerticalLayout();

    Div areaPModeFileUploadResult = new Div();
    LumoLabel pModeFileUploadResultLabel = new LumoLabel();

    byte[] connectorstore = null;

    Div areaConnectorstoreUploadResult = new Div();
    LumoLabel connectorstoreUploadResultLabel = new LumoLabel();

    TextArea pModeSetDescription = new TextArea("Description:");
    TextField connectorstorePwd = new TextField("Connectorstore password");
    ComboBox<KeystoreType> connectorstoreType = new ComboBox<KeystoreType>();

    public Import(@Autowired WebPModeService pmodeService, @Autowired ConfigurationUtil util) {
        super(HELP_ID, TITLE);

        this.pmodeService = pmodeService;

        Div areaPmodeFileUpload = createPModeImportArea();

        add(areaPmodeFileUpload);
        add(areaPModeFileUploadResult);

        Div areaPModeSetDescription = createPModeSetDescriptionArea();

        add(areaPModeSetDescription);

        Div areaConnectorstoreUpload = createConnectorstoreUploadArea();

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

        Button importBtn = new Button();
        importBtn.setIcon(new Icon(VaadinIcon.EDIT));
        importBtn.setText("Import PMode-Set");
        importBtn.addClickListener(e -> {
            boolean result = false;
            result = importPModeSet(
                    pmodeFile,
                    pModeSetDescription.getValue(),
                    connectorstore,
                    connectorstorePwd.getValue(),
                    connectorstoreType.getValue()
            );
            showOutput(result, result ? "PMode-Set successfully imported!" : "Import of PMode-Set failed!");
        });
        importBtn.setEnabled(true);

        add(importBtn);

        add(areaImportResult);
    }

    private Div createPModeImportArea() {
        Div areaPmodeFileUpload = new Div();

        areaPmodeFileUpload.add(new LumoLabel("Upload new PMode file:"));

        MemoryBuffer buffer = new MemoryBuffer();

        Upload upload = new Upload(buffer);
        upload.setMaxFiles(1);
        upload.setId("Upload PModes-File");
        upload.setAcceptedFileTypes("application/xml", "text/xml");

        upload.addSucceededListener(event -> {
            pmodeFile = ((ByteArrayOutputStream) buffer.getFileData().getOutputBuffer())
                    .toByteArray();
            String fileName = buffer.getFileName();
            pModeFileUploadResultLabel.setText("File " + fileName + " uploaded");
            pModeFileUploadResultLabel.getStyle().set("color", "green");
            areaPModeFileUploadResult.add(pModeFileUploadResultLabel);
        });
        upload.addFailedListener(e -> {
            pModeFileUploadResultLabel.setText("File upload failed!");
            pModeFileUploadResultLabel.getStyle().set("color", "red");
            areaPModeFileUploadResult.add(pModeFileUploadResultLabel);
        });

        areaPmodeFileUpload.add(upload);

        return areaPmodeFileUpload;
    }

    private Div createPModeSetDescriptionArea() {
        Div areaPModeSetDescription = new Div();

        pModeSetDescription.setHelperText("Describes the contents of the PMode Set like project or use-case name");
        pModeSetDescription.setRequired(true);

        areaPModeSetDescription.add(pModeSetDescription);

        return areaPModeSetDescription;
    }

    private Div createConnectorstoreUploadArea() {
        Div areaConnectorstoreUpload = new Div();

        areaConnectorstoreUpload.add(new LumoLabel("Upload new Connectorstore file:"));

        MemoryBuffer buffer = new MemoryBuffer();

        Upload upload = new Upload(buffer);
        upload.setMaxFiles(1);
        upload.setId("Upload Connectorstore");
        //		upload.setAcceptedFileTypes("application/xml", "text/xml");

        upload.addSucceededListener(event -> {
            connectorstore = ((ByteArrayOutputStream) buffer.getFileData().getOutputBuffer())
                    .toByteArray();
            String fileName = buffer.getFileName();
            connectorstoreUploadResultLabel.setText("File " + fileName + " uploaded");
            connectorstoreUploadResultLabel.getStyle().set("color", "green");
            areaConnectorstoreUploadResult.add(connectorstoreUploadResultLabel);
        });
        upload.addFailedListener(e -> {
            connectorstoreUploadResultLabel.setText("File upload failed!");
            connectorstoreUploadResultLabel.getStyle().set("color", "red");
            areaConnectorstoreUploadResult.add(connectorstoreUploadResultLabel);
        });

        areaConnectorstoreUpload.add(upload);

        return areaConnectorstoreUpload;
    }

    private void showOutput(boolean success, String text) {
        areaImportResult.removeAll();
        areaConnectorstoreUploadResult.removeAll();
        areaPModeFileUploadResult.removeAll();

        LumoLabel resultLabel = new LumoLabel();
        resultLabel.setText("PMode-Set successfully imported!");
        if (success) {
            resultLabel.getStyle().set("color", "green");
        } else {
            resultLabel.setText("Import of PMode-Set failed!");
            resultLabel.getStyle().set("color", "red");
        }
        areaImportResult.add(resultLabel);

        if (success) {
            LumoLabel pModeFileText = new LumoLabel("Imported P-Mode file: ");
            TextArea area = new TextArea();
            area.setValue(new String(pmodeFile, StandardCharsets.UTF_8));
            area.setWidth("80vw");
            areaImportResult.setWidth("100vw");
            areaImportResult.add(pModeFileText);
            areaImportResult.add(area);
        }
    }

    private boolean importPModeSet(
            byte[] pmodeFile,
            String description,
            byte[] connectorstore,
            String connectorStorePwd,
            KeystoreType connectorstoreType) {

        if (pmodeFile == null || pmodeFile.length < 1
                || connectorstore == null || connectorstore.length < 1
                || StringUtils.isEmpty(description)) {
            return false;
        }
        DomibusConnectorKeystore connectorstoreUUID =
                pmodeService.importConnectorstore(connectorstore, connectorStorePwd, connectorstoreType);

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

        pModeSetDescription.setValue("");
        pmodeFile = null;

        connectorstore = null;
        connectorstorePwd.setValue("");
        connectorstoreType.setValue(KeystoreType.JKS);
    }
}
