package eu.domibus.connector.ui.view.areas.testing;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.controller.service.DomibusConnectorMessageIdGenerator;
import eu.domibus.connector.domain.model.DomibusConnectorAction;
import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.domain.model.DomibusConnectorParty;
import eu.domibus.connector.domain.model.DomibusConnectorService;
import eu.domibus.connector.ui.component.LumoLabel;
import eu.domibus.connector.ui.dto.WebMessage;
import eu.domibus.connector.ui.dto.WebMessageDetail;
import eu.domibus.connector.ui.dto.WebMessageFile;
import eu.domibus.connector.ui.dto.WebMessageFileType;
import eu.domibus.connector.ui.forms.ConnectorTestMessageForm;
import eu.domibus.connector.ui.layout.DCVerticalLayoutWithTitleAndHelpButton;
import eu.domibus.connector.ui.service.WebConnectorTestService;
import eu.domibus.connector.ui.service.WebPModeService;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Component
@UIScope
@Route(value = SendC2CTestMessage.ROUTE, layout = ConnectorTestsLayout.class)
@Order(2)
@TabMetadata(title = "Send Connector Test Message", tabGroup = ConnectorTestsLayout.TAB_GROUP_NAME)
public class SendC2CTestMessage extends DCVerticalLayoutWithTitleAndHelpButton implements AfterNavigationObserver {
    public static final String ROUTE = "sendmessage";
    public static final String TITLE = "Send Connector Test Message";
    public static final String HELP_ID = "ui/c2ctests/send_connector_test_message.html";

    private final ConnectorTestMessageForm messageForm;
    private final VerticalLayout messageFilesArea = new VerticalLayout();
    private final WebPModeService pModeService;
    private final WebConnectorTestService webTestService;
    private final DomibusConnectorMessageIdGenerator messageIdGenerator;
    Div resultArea;
    Button setInitialFilesButton;
    Button uploadFileButton;
    Button submitMessageButton;
    boolean filesEnabled = false;

    public SendC2CTestMessage(
            @Autowired WebPModeService pModeService,
            @Autowired WebConnectorTestService webTestService,
            @Autowired DomibusConnectorMessageIdGenerator messageIdGenerator) {
        super(HELP_ID, TITLE);
        this.messageForm = new ConnectorTestMessageForm();
        this.webTestService = webTestService;
        this.pModeService = pModeService;
        this.messageIdGenerator = messageIdGenerator;
        this.messageForm.setParties(pModeService.getPartyList());

        VerticalLayout messageDetailsArea = new VerticalLayout();
        messageForm.getStyle().set("margin-top", "25px");

        messageDetailsArea.add(messageForm);
        messageForm.setEnabled(true);
        messageDetailsArea.setWidth("500px");
        add(messageDetailsArea);

        add(messageFilesArea);

        setInitialFilesButton = new Button();
        setInitialFilesButton.setIcon(new Icon(VaadinIcon.RECORDS));
        setInitialFilesButton.setText("Set default files");
        setInitialFilesButton.addClickListener(this::setDefaultFilesButtonClicked);

        uploadFileButton = new Button();
        uploadFileButton.setIcon(new Icon(VaadinIcon.UPLOAD));
        uploadFileButton.setText("Add File to message");
        uploadFileButton.addClickListener(this::uploadFileButtonClicked);

        submitMessageButton = new Button(new Icon(VaadinIcon.CLOUD_UPLOAD_O));
        submitMessageButton.setText("Submit Message");
        submitMessageButton.addClickListener(this::submitMessageButtonClicked);

        HorizontalLayout buttons = new HorizontalLayout(
                setInitialFilesButton, uploadFileButton, submitMessageButton
        );
        buttons.setWidth("100vw");
        add(buttons);

        resultArea = new Div();

        add(resultArea);
    }

    private void setDefaultFilesButtonClicked(ClickEvent<Button> e) {
        setInitialFiles();
        refreshPage(null);
    }

    private void submitMessageButtonClicked(ClickEvent<Button> e) {
        if (validateMessageForm()) {
            LumoLabel resultLabel = new LumoLabel();
            if (!validateMessageForSumission()) {
                resultLabel.setText("For message submission a BUSINESS_CONTENT and BUSINESS_DOCUMENT must be " +
                                            "present!");
                resultLabel.getStyle().set("color", "red");
            } else if (webTestService == null) {
                resultLabel.setText(
                        "The service required to submit test messages is not available! Check the configuration!");
                resultLabel.getStyle().set("color", "red");
            } else {
                webTestService.submitTestMessage(messageForm.getMessage());
                resultLabel.setText("Message successfully submitted!");
                resultLabel.getStyle().set("color", "green");
            }
            refreshPage(resultLabel);
        }
    }

    private void uploadFileButtonClicked(ClickEvent<Button> e) {
        UploadMessageFileDialog uploadFileDialog = new UploadMessageFileDialog();
        Button uploadFile = new Button(new Icon(VaadinIcon.UPLOAD));
        uploadFile.setText("Add File to message");
        uploadFile.addClickListener(e1 -> {
            if (!StringUtils.isEmpty(uploadFileDialog.getFileName()) &&
                    uploadFileDialog.getFileType().getValue() != null &&
                    uploadFileDialog.getFileContents() != null && uploadFileDialog.getFileContents().length > 0) {
                String nok = checkFileValid(uploadFileDialog.getFileName(), uploadFileDialog.getFileType().getValue());
                LumoLabel resultLabel = new LumoLabel();
                if (nok == null) {
                    WebMessageFile messageFile = new WebMessageFile(
                            uploadFileDialog.getFileName(),
                            uploadFileDialog.getFileType().getValue(),
                            uploadFileDialog.getFileContents()
                    );
                    messageForm.getMessage().getFiles().add(messageFile);
                    resultLabel.setText("File successfully added to message");
                    resultLabel.getStyle().set("color", "green");
                } else {
                    resultLabel.setText(nok);
                    resultLabel.getStyle().set("color", "red");
                }
                uploadFileDialog.close();
                refreshPage(resultLabel);
            }
        });
        uploadFileDialog.add(uploadFile);
        uploadFileDialog.open();
    }

    @Override
    public void afterNavigation(AfterNavigationEvent arg0) {
        if (webTestService == null) {
            LumoLabel resultLabel = new LumoLabel();
            resultLabel.setText(
                    "The service required to submit test messages is not available! Check the configuration!");
            resultLabel.getStyle().set("color", "red");

            uploadFileButton.setEnabled(false);
            submitMessageButton.setEnabled(false);

            refreshPage(resultLabel);
            return;
        }

        WebMessage msg = new WebMessage();
        // set defaults
        msg.setConversationId(UUID.randomUUID().toString());
        msg.getMessageInfo().setFinalRecipient("finalRecipient");
        msg.getMessageInfo().setOriginalSender("originalSender");

        resultArea.removeAll();

        DomibusConnectorMessageId domibusConnectorMessageId = messageIdGenerator.generateDomibusConnectorMessageId();
        msg.setBackendMessageId(domibusConnectorMessageId.getConnectorMessageId());

        boolean actionSet = loadAndValidateTestAction(msg);

        boolean serviceSet = loadAndValidateTestService(msg);

        boolean fromPartySet = loadAndValidateFromParty(msg);

        if (!actionSet || !serviceSet || !fromPartySet) {
            resultArea.setVisible(true);
            uploadFileButton.setEnabled(false);
            submitMessageButton.setEnabled(false);
        } else {
            resultArea.setVisible(false);
            uploadFileButton.setEnabled(true);
            submitMessageButton.setEnabled(true);
            messageForm.setMessage(msg);

            refreshPage(null);
        }
    }

    private boolean loadAndValidateFromParty(WebMessage msg) {
        DomibusConnectorParty pParty = pModeService.getHomeParty();
        if (pParty != null) {
            WebMessageDetail.Party homeParty =
                    new WebMessageDetail.Party(pParty.getPartyId(), pParty.getPartyIdType(), pParty.getRole());
            msg.getMessageInfo().setFrom(homeParty);
            return true;
        } else {
            LumoLabel resultLabel = new LumoLabel();
            resultLabel.setText("FromParty could not be set! \n"
                                        + "Please check property 'gateway.name' and if the Party with the given " +
                                        "PartyID and RoleType INITIATOR is part of the current active PMode Set. \n"
                                        + "Alternatively re-import the current PMode Set.");
            resultLabel.getStyle().set("color", "red");

            resultArea.add(resultLabel);
            resultArea.setVisible(true);
        }
        return false;
    }

    private boolean loadAndValidateTestAction(WebMessage msg) {
        List<DomibusConnectorAction> actionList = pModeService.getActionList();
        if (actionList != null && !actionList.isEmpty()) {
            WebMessageDetail.Action action = webTestService.getTestAction();
            Optional<DomibusConnectorAction> testAction =
                    actionList.stream()
                              .filter(p -> (p.getAction().equals(action.getAction())))
                              .findFirst();
            if (testAction.isPresent()) {
                msg.getMessageInfo().setAction(action);
                return true;
            } else {
                LumoLabel resultLabel = new LumoLabel();
                resultLabel.setText(
                        "Active PMode Set not valid. Connector test Action " + action.getAction() + " not " +
                                "in active PMode Set!"
                );
                resultLabel.getStyle().set("color", "red");

                resultArea.add(resultLabel);
                resultArea.setVisible(true);
            }
        } else {
            LumoLabel resultLabel = new LumoLabel();
            resultLabel.setText("No active PMode Set imported or active PMode Set not valid. No Actions available!");
            resultLabel.getStyle().set("color", "red");

            resultArea.add(resultLabel);
            resultArea.setVisible(true);
        }
        return false;
    }

    private boolean loadAndValidateTestService(WebMessage msg) {
        List<DomibusConnectorService> serviceList = pModeService.getServiceList();
        if (serviceList != null && !serviceList.isEmpty()) {
            WebMessageDetail.Service service = webTestService.getTestService();
            Optional<DomibusConnectorService> testService = serviceList
                    .stream()
                    .filter(p -> (p.getService().equals(service.getService())))
                    .findFirst();
            if (testService.isPresent()) {
                msg.getMessageInfo().setService(service);
                return true;
            } else {
                LumoLabel resultLabel = new LumoLabel();
                resultLabel.setText(
                        "Active PMode Set not valid. Connector test Service " + service.getService()
                                + " not in active PMode Set!"
                );
                resultLabel.getStyle().set("color", "red");

                resultArea.add(resultLabel);
                resultArea.setVisible(true);
            }
        } else {
            LumoLabel resultLabel = new LumoLabel();
            resultLabel.setText("No active PMode Set imported or active PMode Set not valid. No Services available!");
            resultLabel.getStyle().set("color", "red");

            resultArea.add(resultLabel);
            resultArea.setVisible(true);
        }
        return false;
    }

    private String checkFileValid(String fileName, WebMessageFileType fileType) {
        if (messageForm.getMessage().getFiles() != null) {
            Iterator<WebMessageFile> fileIterator = messageForm.getMessage().getFiles().iterator();

            while (fileIterator.hasNext()) {
                WebMessageFile file = fileIterator.next();
                if (file.getFileName().equals(fileName)) {
                    return "File with that name already part of the message!";
                }
                switch (fileType) {
                    case BUSINESS_CONTENT:
                        if (file.getFileType().equals(fileType))
                            return "BUSINESS_CONTENT already part of the message! Must not be more than one!";
                    case BUSINESS_DOCUMENT:
                        if (file.getFileType().equals(fileType))
                            return "BUSINESS_DOCUMENT already part of the message! Must not be more than one!";
                    case DETACHED_SIGNATURE:
                        if (file.getFileType().equals(fileType))
                            return "DETACHED_SIGNATURE already part of the message! Must not be more than one!";
                    default:
                }
            }
        }
        return null;
    }

    private boolean validateMessageForm() {
        BinderValidationStatus<WebMessage> validationStatus = messageForm.getBinder().validate();
        return validationStatus.isOk();
    }

    private boolean validateMessageForSumission() {
        boolean businessDocumentFound = false;
        boolean businessContentFound = false;
        Iterator<WebMessageFile> it = messageForm.getMessage().getFiles().iterator();
        while (it.hasNext()) {
            WebMessageFile file = it.next();
            if (file.getFileType().equals(WebMessageFileType.BUSINESS_CONTENT))
                businessContentFound = true;
            if (file.getFileType().equals(WebMessageFileType.BUSINESS_DOCUMENT))
                businessDocumentFound = true;
        }
        return businessContentFound && businessDocumentFound;
    }

    private void setInitialFiles() {
        WebMessageFile messageFilePdf = new WebMessageFile(
                "defaultTestFilePdf",
                WebMessageFileType.BUSINESS_DOCUMENT,
                webTestService.getDefaultTestBusinessPdf()
        );
        if (checkFileValid(messageFilePdf.getFileName(), messageFilePdf.getFileType()) == null) {
            messageForm.getMessage().getFiles().add(messageFilePdf);
        }
        WebMessageFile messageFileXml = new WebMessageFile(
                "defaultTestFileXml",
                WebMessageFileType.BUSINESS_CONTENT,
                webTestService.getDefaultBusinessXml()
        );
        if (checkFileValid(messageFileXml.getFileName(), messageFileXml.getFileType()) == null) {
            messageForm.getMessage().getFiles().add(messageFileXml);
        }
    }

    private void buildMessageFilesArea() {
        messageFilesArea.removeAll();

        Div files = new Div();
        files.setWidth("100vw");
        LumoLabel filesLabel = new LumoLabel();
        filesLabel.setText("Files:");
        filesLabel.getStyle().set("font-size", "20px");
        files.add(filesLabel);

        messageFilesArea.add(files);

        Div details = new Div();
        details.setWidth("100vw");

        if (filesEnabled) {

            Grid<WebMessageFile> grid = new Grid<>();

            grid.setItems(messageForm.getMessage().getFiles());

            grid.addComponentColumn(webMessageFile -> getDeleteFileLink(webMessageFile)).setHeader("Delete")
                .setWidth("50px");
            grid.addComponentColumn(webMessageFile -> createDownloadButton(webMessageFile)).setHeader("Filename")
                .setWidth("500px");
            grid.addColumn(WebMessageFile::getFileType).setHeader("Filetype").setWidth("450px");

            grid.setWidth("1000px");
            grid.setMultiSort(true);

            details.add(grid);
        }
        messageFilesArea.add(details);

        messageFilesArea.setWidth("100vw");
        messageFilesArea.setVisible(filesEnabled);
    }

    public void refreshPage(LumoLabel result) {
        filesEnabled = messageForm.getMessage() != null &&
                messageForm.getMessage().getFiles() != null &&
                !messageForm.getMessage().getFiles().isEmpty();

        buildMessageFilesArea();

        if (result != null) {
            resultArea.removeAll();
            resultArea.add(result);
            resultArea.setVisible(true);
        } else {
            resultArea.removeAll();
            resultArea.setVisible(false);
        }
    }

    private Anchor createDownloadButton(WebMessageFile file) {
        Label button = new Label(file.getFileName());
        final StreamResource resource = new StreamResource(
                file.getFileName(),
                () -> new ByteArrayInputStream(file.getFileContent())
        );

        Anchor downloadAnchor = new Anchor();
        downloadAnchor.setHref(resource);
        downloadAnchor.getElement().setAttribute("download", true);
        downloadAnchor.setTarget("_blank");
        downloadAnchor.setTitle(file.getFileName());
        downloadAnchor.add(button);

        return downloadAnchor;
    }

    private Button getDeleteFileLink(WebMessageFile file) {
        Button deleteFileButton = new Button(new Icon(VaadinIcon.ERASER));
        deleteFileButton.addClickListener(e -> {
            Dialog deleteMessageDialog = new Dialog();

            Div headerContent = new Div();
            Label header = new Label("Delete file from message");
            header.getStyle().set("font-weight", "bold");
            header.getStyle().set("font-style", "italic");
            headerContent.getStyle().set("text-align", "center");
            headerContent.getStyle().set("padding", "10px");
            headerContent.add(header);
            deleteMessageDialog.add(headerContent);

            Div labelContent = new Div();
            LumoLabel label = new LumoLabel(
                    "Are you sure you want to delete this file from the message? Storage file is deleted as well!");

            labelContent.add(label);
            deleteMessageDialog.add(labelContent);

            Button delButton = new Button("Delete File");
            delButton.addClickListener(e1 -> {
                messageForm.getMessage().getFiles().remove(file);
                LumoLabel resultLabel = new LumoLabel();
                resultLabel.setText("File " + file.getFileName() + " deleted successfully");
                resultLabel.getStyle().set("color", "green");
                deleteMessageDialog.close();
                refreshPage(resultLabel);
            });
            deleteMessageDialog.add(delButton);
            deleteMessageDialog.open();
        });
        return deleteFileButton;
    }
}
