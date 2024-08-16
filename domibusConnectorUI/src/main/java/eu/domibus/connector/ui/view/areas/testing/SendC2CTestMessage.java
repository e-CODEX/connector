/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

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
import eu.domibus.connector.ui.utils.UiStyle;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * This class represents a test message form for sending C2C (Connector-to-Connector) test
 * messages.
 *
 * @see DCVerticalLayoutWithTitleAndHelpButton
 * @see VerticalLayout
 * @see Div
 * @see Anchor
 * @see Button
 * @see com.vaadin.flow.component.html.H2
 * @see VaadinIcon
 * @see com.vaadin.flow.router.RouteConfiguration
 * @see eu.domibus.connector.ui.view.DashboardView
 */
@Component
@UIScope
@Route(value = SendC2CTestMessage.ROUTE, layout = ConnectorTestsLayout.class)
@Order(2)
@TabMetadata(title = "Send Connector Test Message", tabGroup = ConnectorTestsLayout.TAB_GROUP_NAME)
public class SendC2CTestMessage extends DCVerticalLayoutWithTitleAndHelpButton
    implements AfterNavigationObserver {
    public static final String ROUTE = "sendmessage";
    public static final String TITLE = "Send Connector Test Message";
    public static final String HELP_ID = "ui/c2ctests/send_connector_test_message.html";
    private final ConnectorTestMessageForm messageForm;
    private final VerticalLayout messageFilesArea = new VerticalLayout();
    Div resultArea;
    Button setInitialFilesButton;
    Button uploadFileButton;
    Button submitMessageButton;
    private final WebPModeService webPModeService;
    private final WebConnectorTestService webTestService;
    private final DomibusConnectorMessageIdGenerator messageIdGenerator;
    boolean filesEnabled = false;

    /**
     * Constructor.
     *
     * @param webPModeService       The WebPModeService used for retrieving party information.
     * @param webTestService     The WebConnectorTestService used for performing web tests.
     * @param messageIdGenerator The DomibusConnectorMessageIdGenerator used for generating message
     *                           IDs.
     */
    public SendC2CTestMessage(
        @Autowired WebPModeService webPModeService,
        @Autowired WebConnectorTestService webTestService,
        @Autowired DomibusConnectorMessageIdGenerator messageIdGenerator) {
        super(HELP_ID, TITLE);
        this.messageForm = new ConnectorTestMessageForm();
        this.webTestService = webTestService;
        this.webPModeService = webPModeService;
        this.messageIdGenerator = messageIdGenerator;
        this.messageForm.setParties(webPModeService.getPartyList());

        var messageDetailsArea = new VerticalLayout();
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

        var buttons = new HorizontalLayout(
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
            var resultLabel = new LumoLabel();
            if (!validateMessageForSubmission()) {
                resultLabel.setText(
                    "For message submission a BUSINESS_CONTENT and BUSINESS_DOCUMENT must be "
                        + "present!"
                );
                resultLabel.getStyle().set(UiStyle.TAG_COLOR, "red");
            } else if (webTestService == null) {
                resultLabel.setText(
                    "The service required to submit test messages is not available! Check the "
                        + "configuration!"
                );
                resultLabel.getStyle().set(UiStyle.TAG_COLOR, "red");
            } else {
                webTestService.submitTestMessage(messageForm.getMessage());
                resultLabel.setText("Message successfully submitted!");
                resultLabel.getStyle().set(UiStyle.TAG_COLOR, UiStyle.COLOR_GREEN);
            }
            refreshPage(resultLabel);
        }
    }

    private void uploadFileButtonClicked(ClickEvent<Button> e) {
        var uploadFileDialog = new UploadMessageFileDialog();
        var uploadFile = new Button(new Icon(VaadinIcon.UPLOAD));
        uploadFile.setText("Add File to message");
        uploadFile.addClickListener(e1 -> {
            if (!StringUtils.isEmpty(uploadFileDialog.getFileName())
                && uploadFileDialog.getFileType().getValue() != null
                && uploadFileDialog.getFileContents() != null
                && uploadFileDialog.getFileContents().length > 0) {
                String nok = checkFileValid(
                    uploadFileDialog.getFileName(),
                    uploadFileDialog.getFileType().getValue()
                );
                var resultLabel = new LumoLabel();
                if (nok == null) {
                    var messageFile = new WebMessageFile(
                        uploadFileDialog.getFileName(), uploadFileDialog.getFileType().getValue(),
                        uploadFileDialog.getFileContents()
                    );
                    messageForm.getMessage().getFiles().add(messageFile);
                    resultLabel.setText("File successfully added to message");
                    resultLabel.getStyle().set(UiStyle.TAG_COLOR, UiStyle.COLOR_GREEN);
                } else {
                    resultLabel.setText(nok);
                    resultLabel.getStyle().set(UiStyle.TAG_COLOR, "red");
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
            var resultLabel = new LumoLabel();
            resultLabel.setText(
                "The service required to submit test messages is not available! Check the "
                    + "configuration!"
            );
            resultLabel.getStyle().set(UiStyle.TAG_COLOR, "red");

            uploadFileButton.setEnabled(false);
            submitMessageButton.setEnabled(false);

            refreshPage(resultLabel);
            return;
        }

        var webMessage = new WebMessage();
        // set defaults
        webMessage.setConversationId(UUID.randomUUID().toString());
        webMessage.getMessageInfo().setFinalRecipient("finalRecipient");
        webMessage.getMessageInfo().setOriginalSender("originalSender");

        resultArea.removeAll();

        var domibusConnectorMessageId =
            messageIdGenerator.generateDomibusConnectorMessageId();
        webMessage.setBackendMessageId(domibusConnectorMessageId.getConnectorMessageId());

        boolean actionSet = loadAndValidateTestAction(webMessage);

        boolean serviceSet = loadAndValidateTestService(webMessage);

        boolean fromPartySet = loadAndValidateFromParty(webMessage);

        if (!actionSet || !serviceSet || !fromPartySet) {
            resultArea.setVisible(true);
            uploadFileButton.setEnabled(false);
            submitMessageButton.setEnabled(false);
        } else {
            resultArea.setVisible(false);
            uploadFileButton.setEnabled(true);
            submitMessageButton.setEnabled(true);
            messageForm.setMessage(webMessage);

            refreshPage(null);
        }
    }

    private boolean loadAndValidateFromParty(WebMessage webMessage) {
        var connectorParty = webPModeService.getHomeParty();
        if (connectorParty != null) {
            var homeParty = new WebMessageDetail.Party(
                connectorParty.getPartyId(),
                connectorParty.getPartyIdType(),
                connectorParty.getRole()
            );
            webMessage.getMessageInfo().setFrom(homeParty);
            return true;
        } else {
            var resultLabel = new LumoLabel();
            resultLabel.setText("FromParty could not be set! \n"
                                    + "Please check property 'gateway.name' and if the Party with "
                                    + "the given PartyID and RoleType INITIATOR is part of the "
                                    + "current active PMode Set. \n"
                                    + "Alternatively re-import the current PMode Set.");
            resultLabel.getStyle().set(UiStyle.TAG_COLOR, UiStyle.COLOR_RED);

            resultArea.add(resultLabel);
            resultArea.setVisible(true);
        }
        return false;
    }

    private boolean loadAndValidateTestAction(WebMessage msg) {
        List<DomibusConnectorAction> actionList = webPModeService.getActionList();
        if (actionList != null && !actionList.isEmpty()) {
            var action = webTestService.getTestAction();
            var testAction = actionList
                .stream()
                .filter(p -> (p.getAction().equals(action.getAction())))
                .findFirst();
            if (testAction.isPresent()) {
                msg.getMessageInfo().setAction(action);
                return true;
            } else {
                var resultLabel = new LumoLabel();
                resultLabel.setText(
                    "Active PMode Set not valid. Connector test Action " + action.getAction()
                        + " not in active PMode Set!");
                resultLabel.getStyle().set(UiStyle.TAG_COLOR, UiStyle.COLOR_RED);

                resultArea.add(resultLabel);
                resultArea.setVisible(true);
            }
        } else {
            var resultLabel = new LumoLabel();
            resultLabel.setText(
                "No active PMode Set imported or active PMode Set not valid. No Actions available!"
            );
            resultLabel.getStyle().set(UiStyle.TAG_COLOR, UiStyle.COLOR_RED);

            resultArea.add(resultLabel);
            resultArea.setVisible(true);
        }
        return false;
    }

    private boolean loadAndValidateTestService(WebMessage msg) {
        List<DomibusConnectorService> serviceList = webPModeService.getServiceList();
        if (serviceList != null && !serviceList.isEmpty()) {
            var service = webTestService.getTestService();
            var testService = serviceList
                .stream()
                .filter(p -> (p.getService().equals(service.getService())))
                .findFirst();
            if (testService.isPresent()) {
                msg.getMessageInfo().setService(service);
                return true;
            } else {
                var resultLabel = new LumoLabel();
                resultLabel.setText(
                    "Active PMode Set not valid. Connector test Service " + service.getService()
                        + " not in active PMode Set!");
                resultLabel.getStyle().set(UiStyle.TAG_COLOR, UiStyle.COLOR_RED);

                resultArea.add(resultLabel);
                resultArea.setVisible(true);
            }
        } else {
            var resultLabel = new LumoLabel();
            resultLabel.setText(
                "No active PMode Set imported or active PMode Set not valid. No Services available!"
            );
            resultLabel.getStyle().set(UiStyle.TAG_COLOR, UiStyle.COLOR_RED);

            resultArea.add(resultLabel);
            resultArea.setVisible(true);
        }
        return false;
    }

    private String checkFileValid(String fileName, WebMessageFileType fileType) {
        if (messageForm.getMessage().getFiles() != null) {

            for (WebMessageFile file : messageForm.getMessage().getFiles()) {
                if (file.getFileName().equals(fileName)) {
                    return "File with that name already part of the message!";
                }
                switch (fileType) {
                    case BUSINESS_CONTENT:
                        if (file.getFileType().equals(fileType)) {
                            return "BUSINESS_CONTENT already part of the message! Must not be "
                                + "more than one!";
                        }
                        break;
                    case BUSINESS_DOCUMENT:
                        if (file.getFileType().equals(fileType)) {
                            return "BUSINESS_DOCUMENT already part of the message! Must not be "
                                + "more than one!";
                        }
                        break;
                    case DETACHED_SIGNATURE:
                        if (file.getFileType().equals(fileType)) {
                            return "DETACHED_SIGNATURE already part of the message! Must not be "
                                + "more than one!";
                        }
                        break;
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

    private boolean validateMessageForSubmission() {
        var businessDocumentFound = false;
        var businessContentFound = false;
        for (WebMessageFile file : messageForm.getMessage().getFiles()) {
            if (file.getFileType().equals(WebMessageFileType.BUSINESS_CONTENT)) {
                businessContentFound = true;
            }
            if (file.getFileType().equals(WebMessageFileType.BUSINESS_DOCUMENT)) {
                businessDocumentFound = true;
            }
        }
        return businessContentFound && businessDocumentFound;
    }

    private void setInitialFiles() {
        var messageFilePdf =
            new WebMessageFile("defaultTestFilePdf", WebMessageFileType.BUSINESS_DOCUMENT,
                               webTestService.getDefaultTestBusinessPdf()
            );
        if (checkFileValid(messageFilePdf.getFileName(), messageFilePdf.getFileType()) == null) {
            messageForm.getMessage().getFiles().add(messageFilePdf);
        }
        var messageFileXml =
            new WebMessageFile("defaultTestFileXml", WebMessageFileType.BUSINESS_CONTENT,
                               webTestService.getDefaultBusinessXml()
            );
        if (checkFileValid(messageFileXml.getFileName(), messageFileXml.getFileType()) == null) {
            messageForm.getMessage().getFiles().add(messageFileXml);
        }
    }

    private void buildMessageFilesArea() {

        messageFilesArea.removeAll();

        var files = new Div();
        files.setWidth(UiStyle.WIDTH_100_VW);
        var filesLabel = new LumoLabel();
        filesLabel.setText("Files:");
        filesLabel.getStyle().set(UiStyle.FONT_SIZE_STYLE, "20px");
        files.add(filesLabel);

        messageFilesArea.add(files);

        var details = new Div();
        details.setWidth(UiStyle.WIDTH_100_VW);

        if (filesEnabled) {
            Grid<WebMessageFile> grid = new Grid<>();
            grid.setItems(messageForm.getMessage().getFiles());
            grid.addComponentColumn(this::getDeleteFileLink)
                .setHeader("Delete").setWidth("50px");
            grid.addComponentColumn(this::createDownloadButton)
                .setHeader("Filename").setWidth(UiStyle.WIDTH_500_PX);
            grid.addColumn(WebMessageFile::getFileType).setHeader("Filetype").setWidth("450px");

            grid.setWidth("1000px");
            grid.setMultiSort(true);

            details.add(grid);
        }
        messageFilesArea.add(details);

        messageFilesArea.setWidth(UiStyle.WIDTH_100_VW);
        messageFilesArea.setVisible(filesEnabled);
    }

    /**
     * Refreshes the page by updating the UI elements based on the current state of the
     * application.
     *
     * @param result The LumoLabel to display as a result on the page. If null, the result area will
     *               be cleared and hidden.
     */
    public void refreshPage(LumoLabel result) {
        filesEnabled = messageForm.getMessage() != null
            && messageForm.getMessage().getFiles() != null
            && !messageForm.getMessage().getFiles().isEmpty();

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
        final var resource = new StreamResource(
            file.getFileName(),
            () -> new ByteArrayInputStream(file.getFileContent())
        );

        var downloadAnchor = new Anchor();
        downloadAnchor.setHref(resource);
        downloadAnchor.getElement().setAttribute("download", true);
        downloadAnchor.setTarget("_blank");
        downloadAnchor.setTitle(file.getFileName());

        var button = new Label(file.getFileName());
        downloadAnchor.add(button);

        return downloadAnchor;
    }

    private Button getDeleteFileLink(WebMessageFile file) {
        var deleteFileButton = new Button(new Icon(VaadinIcon.ERASER));
        deleteFileButton.addClickListener(e -> {
            var headerContent = new Div();
            var header = new Label("Delete file from message");
            header.getStyle().set("font-weight", "bold");
            header.getStyle().set("font-style", "italic");
            headerContent.getStyle().set(UiStyle.ALIGNMENT_STYLE, UiStyle.ALIGNMENT_CENTER);
            headerContent.getStyle().set("padding", "10px");
            headerContent.add(header);

            var deleteMessageDialog = new Dialog();
            deleteMessageDialog.add(headerContent);

            var labelContent = new Div();
            var label = new LumoLabel(
                "Are you sure you want to delete this file from the message? Storage file is "
                    + "deleted as well!"
            );

            labelContent.add(label);
            deleteMessageDialog.add(labelContent);

            var deleteButton = new Button("Delete File");
            deleteButton.addClickListener(e1 -> {
                messageForm.getMessage().getFiles().remove(file);
                var resultLabel = new LumoLabel();
                resultLabel.setText("File " + file.getFileName() + " deleted successfully");
                resultLabel.getStyle().set("color", "green");
                deleteMessageDialog.close();
                refreshPage(resultLabel);
            });
            deleteMessageDialog.add(deleteButton);
            deleteMessageDialog.open();
        });
        return deleteFileButton;
    }
}
