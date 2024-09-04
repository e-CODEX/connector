/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.service;

import eu.ecodex.connector.c2ctests.config.ConnectorTestConfigurationProperties;
import eu.ecodex.connector.domain.configuration.EvidenceActionServiceConfigurationProperties.AS4Action;
import eu.ecodex.connector.domain.configuration.EvidenceActionServiceConfigurationProperties.AS4Service;
import eu.ecodex.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.ecodex.connector.domain.transition.DomibusConnectorActionType;
import eu.ecodex.connector.domain.transition.DomibusConnectorDocumentAESType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageAttachmentType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageContentType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageDetailsType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageDocumentType;
import eu.ecodex.connector.domain.transition.DomibusConnectorMessageType;
import eu.ecodex.connector.domain.transition.DomibusConnectorPartyType;
import eu.ecodex.connector.domain.transition.DomibusConnectorServiceType;
import eu.ecodex.connector.domain.transition.tools.ConversionTools;
import eu.ecodex.connector.test.service.DCConnector2ConnectorTestService;
import eu.ecodex.connector.ui.dto.WebMessage;
import eu.ecodex.connector.ui.dto.WebMessageDetail;
import eu.ecodex.connector.ui.dto.WebMessageDetail.Party;
import eu.ecodex.connector.ui.dto.WebMessageFileType;
import eu.europa.esig.dss.enumerations.MimeTypeEnum;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StreamUtils;

/**
 * The WebConnectorTestService class provides methods for interacting with the Domibus Connector in
 * order to submit test messages and retrieve test backend information.
 */
public class WebConnectorTestService {
    private static final Logger LOGGER = LogManager.getLogger(WebConnectorTestService.class);
    private final DCConnector2ConnectorTestService connectorTestService;
    private final ConnectorTestConfigurationProperties config;

    /**
     * Constructs a new WebConnectorTestService object.
     *
     * @param connectorTestService the DCConnector2ConnectorTestService object used to interact with
     *                             the Domibus Connector for submitting test messages and retrieving
     *                             test backend information. Must not be null.
     * @param config               the ConnectorTestConfigurationProperties object that holds the
     *                             configuration properties for the connector to connector test
     *                             action and service. Must not be null .
     */
    public WebConnectorTestService(
        DCConnector2ConnectorTestService connectorTestService,
        ConnectorTestConfigurationProperties config
    ) {
        this.connectorTestService = connectorTestService;
        this.config = config;
    }

    /**
     * Submits a test message to the Domibus Connector.
     *
     * @param testMsg the WebMessage object representing the test message to be submitted. Must not
     *                be null.
     */
    public void submitTestMessage(WebMessage testMsg) {
        DomibusConnectorMessageType connectorMsg = mapWebMessageToTransition(testMsg);
        LOGGER.debug("Submitting test message [{}]", connectorMsg);
        connectorTestService.submitTestMessage(connectorMsg);
    }

    /**
     * Retrieves the default test business PDF as a byte array.
     *
     * @return the default test business PDF as a byte array
     * @throws RuntimeException if an I/O error occurs while retrieving the default test business
     *                          PDF
     */
    public byte[] getDefaultTestBusinessPdf() {
        try {
            return StreamUtils.copyToByteArray(config.getDefaultBusinessPdf().getInputStream());
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Retrieves the default business XML as a byte array.
     *
     * @return the default business XML as a byte array
     * @throws RuntimeException if an I/O error occurs while retrieving the default business XML
     */
    public byte[] getDefaultBusinessXml() {
        try {
            return StreamUtils.copyToByteArray(config.getDefaultBusinessXml().getInputStream());
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Retrieves the test service for the web message.
     *
     * @return the test service as a {@link WebMessageDetail.Service} object
     */
    public WebMessageDetail.Service getTestService() {
        AS4Service testService = connectorTestService.getTestService(
            DomibusConnectorBusinessDomain.getDefaultMessageLaneId());
        return new WebMessageDetail.Service(testService.getName(), testService.getServiceType());
    }

    /**
     * Retrieves the test action for the web message.
     *
     * @return the test action as a {@link WebMessageDetail.Action} object
     */
    public WebMessageDetail.Action getTestAction() {
        AS4Action testAction = connectorTestService.getTestAction(
            DomibusConnectorBusinessDomain.getDefaultMessageLaneId());
        return new WebMessageDetail.Action(testAction.getAction());
    }

    public String getConnectorTestBackendName() {
        return connectorTestService.getTestBackendName();
    }

    /**
     * Maps a WebMessage to a DomibusConnectorMessageType.
     *
     * @param webMsg the WebMessage to be mapped. Must not be null.
     * @return the mapped DomibusConnectorMessageType.
     */
    public DomibusConnectorMessageType mapWebMessageToTransition(WebMessage webMsg) {
        var connectorMsg = new DomibusConnectorMessageType();
        var details = mapDetails(webMsg.getMessageInfo());

        if (!StringUtils.isEmpty(webMsg.getConversationId())) {
            details.setConversationId(webMsg.getConversationId());
        }

        if (!StringUtils.isEmpty(webMsg.getBackendMessageId())) {
            details.setBackendMessageId(webMsg.getBackendMessageId());
        }

        connectorMsg.setMessageDetails(details);

        var content = mapContent(webMsg);
        connectorMsg.setMessageContent(content);

        webMsg.getFiles().stream()
              .filter(f -> f.getFileType().equals(WebMessageFileType.BUSINESS_ATTACHMENT))
              .forEach(a -> {
                  var bytes = a.getFileContent();
                  var dataHandler = ConversionTools.convertByteArrayToDataHandler(
                      bytes,
                      MimeTypeEnum.BINARY.getMimeTypeString()
                  );

                  var attachment = new DomibusConnectorMessageAttachmentType();
                  attachment.setAttachment(dataHandler);
                  attachment.setName(a.getFileName());
                  attachment.setIdentifier(a.getFileName());
                  attachment.setMimeType(MimeTypeEnum.BINARY.getMimeTypeString());
                  connectorMsg.getMessageAttachments().add(attachment);
              });

        return connectorMsg;
    }

    private DomibusConnectorMessageContentType mapContent(WebMessage webMsg) {
        var content = new DomibusConnectorMessageContentType();

        var businessContent = webMsg.getFiles()
                                    .stream()
                                    .filter(f -> f.getFileType().equals(
                                        WebMessageFileType.BUSINESS_CONTENT)
                                    )
                                    .findFirst();

        if (businessContent.isPresent()) {
            byte[] fileContent = businessContent.get().getFileContent();
            var streamSource = ConversionTools.convertByteArrayToStreamSource(fileContent);
            content.setXmlContent(streamSource);
        }

        var businessDocument = webMsg.getFiles()
                                     .stream()
                                     .filter(f -> f.getFileType().equals(
                                         WebMessageFileType.BUSINESS_DOCUMENT)
                                     )
                                     .findFirst();

        if (businessDocument.isPresent()) {
            var fileContent = businessDocument.get().getFileContent();

            var document = new DomibusConnectorMessageDocumentType();
            document.setDocumentName(businessDocument.get().getFileName());

            document.setAesType(DomibusConnectorDocumentAESType.AUTHENTICATION_BASED);

            var dataHandler = ConversionTools.convertByteArrayToDataHandler(
                fileContent,
                MimeTypeEnum.PDF.getMimeTypeString()
            );
            document.setDocument(dataHandler);

            content.setDocument(document);
        }

        return content;
    }

    private DomibusConnectorMessageDetailsType mapDetails(WebMessageDetail webMessageDetail) {
        var details = new DomibusConnectorMessageDetailsType();
        details.setFinalRecipient(webMessageDetail.getFinalRecipient());
        details.setOriginalSender(webMessageDetail.getOriginalSender());

        Party from = webMessageDetail.getFrom();
        details.setFromParty(mapParty(from));

        var toParty = webMessageDetail.getTo();
        details.setToParty(mapParty(toParty));

        var action = new DomibusConnectorActionType();
        action.setAction(webMessageDetail.getAction().getAction());
        details.setAction(action);

        var service = new DomibusConnectorServiceType();
        service.setService(webMessageDetail.getService().getService());
        service.setServiceType(webMessageDetail.getService().getServiceType());
        details.setService(service);

        return details;
    }

    private DomibusConnectorPartyType mapParty(Party party) {
        var connectorPartyType = new DomibusConnectorPartyType();
        connectorPartyType.setPartyId(party.getPartyId());
        connectorPartyType.setPartyIdType(party.getPartyIdType());
        connectorPartyType.setRole(party.getRole());
        return connectorPartyType;
    }
}
