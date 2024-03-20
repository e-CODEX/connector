package eu.domibus.connector.ui.service;

import java.io.IOException;
import java.util.Optional;

import javax.activation.DataHandler;
import javax.xml.transform.Source;

import eu.domibus.connector.c2ctests.config.ConnectorTestConfigurationProperties;
import eu.europa.esig.dss.enumerations.MimeTypeEnum;
import org.apache.commons.lang3.StringUtils;

import eu.domibus.connector.domain.configuration.EvidenceActionServiceConfigurationProperties.AS4Action;
import eu.domibus.connector.domain.configuration.EvidenceActionServiceConfigurationProperties.AS4Service;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.domibus.connector.domain.transition.DomibusConnectorActionType;
import eu.domibus.connector.domain.transition.DomibusConnectorDocumentAESType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageAttachmentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageContentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDetailsType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDocumentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorPartyType;
import eu.domibus.connector.domain.transition.DomibusConnectorServiceType;
import eu.domibus.connector.domain.transition.tools.ConversionTools;
import eu.domibus.connector.test.service.DCConnector2ConnectorTestService;
import eu.domibus.connector.ui.dto.WebMessage;
import eu.domibus.connector.ui.dto.WebMessageDetail;
import eu.domibus.connector.ui.dto.WebMessageDetail.Party;
import eu.domibus.connector.ui.dto.WebMessageFile;
import eu.domibus.connector.ui.dto.WebMessageFileType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StreamUtils;


public class WebConnectorTestService {

	private static final Logger LOGGER = LogManager.getLogger(WebConnectorTestService.class);

	private final DCConnector2ConnectorTestService connectorTestService;
	private final ConnectorTestConfigurationProperties config;

	public WebConnectorTestService(DCConnector2ConnectorTestService connectorTestService,
								   ConnectorTestConfigurationProperties config
	) {
		this.connectorTestService = connectorTestService;
		this.config = config;
	}
	
	public void submitTestMessage(WebMessage testMsg) {
		DomibusConnectorMessageType connectorMsg = mapWebMessageToTransition(testMsg);
		LOGGER.debug("Submitting test message [{}]", connectorMsg);
		connectorTestService.submitTestMessage(connectorMsg);
	}

	public byte[] getDefaultTestBusinessPdf() {
		try {
			return StreamUtils.copyToByteArray(config.getDefaultBusinessPdf().getInputStream());
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	public byte[] getDefaultBusinessXml() {
		try {
			return StreamUtils.copyToByteArray(config.getDefaultBusinessXml().getInputStream());
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	public WebMessageDetail.Service getTestService() {
		AS4Service testService = connectorTestService.getTestService(DomibusConnectorBusinessDomain.getDefaultMessageLaneId());
		WebMessageDetail.Service service = new WebMessageDetail.Service(testService.getName(), testService.getServiceType());
		return service;
	}
	
	public WebMessageDetail.Action getTestAction() {
		AS4Action testAction = connectorTestService.getTestAction(DomibusConnectorBusinessDomain.getDefaultMessageLaneId());
		WebMessageDetail.Action action = new WebMessageDetail.Action(testAction.getAction());
		return action;
	}
	
	public String getConnectorTestBackendName() {
		return connectorTestService.getTestBackendName();
	}
	
	public DomibusConnectorMessageType mapWebMessageToTransition(WebMessage webMsg) {
		DomibusConnectorMessageType connectorMsg = new DomibusConnectorMessageType();
		
		DomibusConnectorMessageDetailsType details = mapDetails(webMsg.getMessageInfo());
		
		if(!StringUtils.isEmpty(webMsg.getConversationId()))
			details.setConversationId(webMsg.getConversationId());
		
		if(!StringUtils.isEmpty(webMsg.getBackendMessageId()))
			details.setBackendMessageId(webMsg.getBackendMessageId());
		
		connectorMsg.setMessageDetails(details);
		
		DomibusConnectorMessageContentType content = mapContent(webMsg);
		connectorMsg.setMessageContent(content );
		
		webMsg.getFiles().stream()
		.filter(f -> f.getFileType().equals(WebMessageFileType.BUSINESS_ATTACHMENT))
		.forEach(a -> {
			byte[] bytes = a.getFileContent();
			DataHandler dh = ConversionTools.convertByteArrayToDataHandler(bytes, MimeTypeEnum.BINARY.getMimeTypeString());
			
			DomibusConnectorMessageAttachmentType attachment = new DomibusConnectorMessageAttachmentType();
			attachment.setAttachment(dh);
			attachment.setName(a.getFileName());
			attachment.setIdentifier(a.getFileName());
			attachment.setMimeType(MimeTypeEnum.BINARY.getMimeTypeString());
			connectorMsg.getMessageAttachments().add(attachment );
		});
		
		return connectorMsg;
	}
	
	private DomibusConnectorMessageContentType mapContent(WebMessage webMsg) {
		DomibusConnectorMessageContentType content = new DomibusConnectorMessageContentType();
		
		Optional<WebMessageFile> businessContent = webMsg.getFiles().stream()
				.filter(f -> f.getFileType().equals(WebMessageFileType.BUSINESS_CONTENT))
				.findFirst();
		
		if(businessContent.isPresent()) {
			byte[] fileContent = businessContent.get().getFileContent();
			Source streamSource = ConversionTools.convertByteArrayToStreamSource(fileContent);
			content.setXmlContent(streamSource);
		}
		
		Optional<WebMessageFile> businessDocument = webMsg.getFiles().stream()
				.filter(f -> f.getFileType().equals(WebMessageFileType.BUSINESS_DOCUMENT))
				.findFirst();
		
		if(businessDocument.isPresent()) {
			byte[] fileContent = businessDocument.get().getFileContent();

			DomibusConnectorMessageDocumentType document = new DomibusConnectorMessageDocumentType();
			document.setDocumentName(businessDocument.get().getFileName());
			
			document.setAesType(DomibusConnectorDocumentAESType.AUTHENTICATION_BASED);
			
			DataHandler dh = ConversionTools.convertByteArrayToDataHandler(fileContent, MimeTypeEnum.PDF.getMimeTypeString());
			document.setDocument(dh );
			
			content.setDocument(document );
		}
		
		
		
		return content;
	}

	private DomibusConnectorMessageDetailsType mapDetails(WebMessageDetail pDetails) {
		DomibusConnectorMessageDetailsType details = new DomibusConnectorMessageDetailsType();
		details.setFinalRecipient(pDetails.getFinalRecipient());
		details.setOriginalSender(pDetails.getOriginalSender());
		
		Party from = pDetails.getFrom();
		details.setFromParty(mapParty(from));
		
		Party to = pDetails.getTo();
		details.setToParty(mapParty(to));
		
		DomibusConnectorActionType action = new DomibusConnectorActionType();
		action.setAction(pDetails.getAction().getAction());
		details.setAction(action);
		
		DomibusConnectorServiceType service = new DomibusConnectorServiceType();
		service.setService(pDetails.getService().getService());
		service.setServiceType(pDetails.getService().getServiceType());
		details.setService(service);
		
		return details;
	}
	
	private DomibusConnectorPartyType mapParty(Party pParty) {
		DomibusConnectorPartyType cParty = new DomibusConnectorPartyType();
		cParty.setPartyId(pParty.getPartyId());
		cParty.setPartyIdType(pParty.getPartyIdType());
		cParty.setRole(pParty.getRole());
		return cParty;
	}

}
