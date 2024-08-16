/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.service;

import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.domibus.connector.ui.dto.WebMessage;
import eu.domibus.connector.ui.persistence.service.DomibusConnectorWebMessagePersistenceService;
import eu.domibus.connector.ui.utils.UiStyle;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.stereotype.Service;

/**
 * The WebMessageService class provides methods for retrieving and manipulating web messages in the
 * Domibus Connector system. It interacts with a DomibusConnectorWebMessagePersistenceService to
 * perform these operations.
 *
 * @see DomibusConnectorWebMessagePersistenceService
 */
@Service("webMessageService")
public class WebMessageService {
    private final DomibusConnectorWebMessagePersistenceService messagePersistenceService;

    public WebMessageService(
        DomibusConnectorWebMessagePersistenceService messagePersistenceService) {
        this.messagePersistenceService = messagePersistenceService;
    }

    public LinkedList<WebMessage> getInitialList() {
        return messagePersistenceService.getAllMessages();
    }

    public Optional<WebMessage> getMessageByConnectorId(String connectorMessageId) {
        return messagePersistenceService.getMessageByConnectorId(connectorMessageId);
    }

    public Optional<WebMessage> getMessageByEbmsId(String ebmsId) {
        return messagePersistenceService.findMessageByEbmsId(
            ebmsId, DomibusConnectorMessageDirection.GATEWAY_TO_BACKEND);
    }

    public Optional<WebMessage> getMessageByBackendMessageId(String backendMessageId) {
        return messagePersistenceService.findMessageByNationalId(
            backendMessageId, DomibusConnectorMessageDirection.BACKEND_TO_GATEWAY);
    }

    public LinkedList<WebMessage> getMessagesByPeriod(Date fromDate, Date toDate) {
        return messagePersistenceService.getMessagesWithinPeriod(fromDate, toDate);
    }

    public LinkedList<WebMessage> getMessagesByConversationId(String conversationId) {
        return messagePersistenceService.findMessagesByConversationId(conversationId);
    }

    public LinkedList<WebMessage> getConnectorTestMessages(String c2cTestBackendName) {
        return messagePersistenceService.findConnectorTestMessages(c2cTestBackendName);
    }

    /**
     * Generates an Excel file containing the list of web messages.
     *
     * @param messages A LinkedList of web messages to be included in the Excel file.
     * @return An InputStream representing the generated Excel file.
     */
    public InputStream generateExcel(LinkedList<WebMessage> messages) {
        var sheetName = "Messages List";
        var wb = WebServiceUtil.createNewExcel(sheetName);

        Map<String, CellStyle> styles = WebServiceUtil.createStyles(wb);

        var sheet = wb.getSheet(sheetName);

        var headerRow = sheet.createRow(0);
        var cell0 = headerRow.createCell(0);
        cell0.setCellValue("Connector Message ID");
        cell0.setCellStyle(styles.get(UiStyle.TAG_HEADER));
        var cell1 = headerRow.createCell(1);
        cell1.setCellValue("From Party ID");
        cell1.setCellStyle(styles.get(UiStyle.TAG_HEADER));
        var cell2 = headerRow.createCell(2);
        cell2.setCellValue("To Party ID");
        cell2.setCellStyle(styles.get(UiStyle.TAG_HEADER));
        var cell3 = headerRow.createCell(3);
        cell3.setCellValue("Service");
        cell3.setCellStyle(styles.get(UiStyle.TAG_HEADER));
        var cell4 = headerRow.createCell(4);
        cell4.setCellValue("Action");
        cell4.setCellStyle(styles.get(UiStyle.TAG_HEADER));
        var cell5 = headerRow.createCell(5);
        cell5.setCellValue("Created");
        cell5.setCellStyle(styles.get(UiStyle.TAG_HEADER));
        var cell6 = headerRow.createCell(6);
        cell6.setCellValue("Delivered Backend");
        cell6.setCellStyle(styles.get(UiStyle.TAG_HEADER));
        var cell7 = headerRow.createCell(7);
        cell7.setCellValue("Delivered Gateway");
        cell7.setCellStyle(styles.get(UiStyle.TAG_HEADER));
        var cell8 = headerRow.createCell(8);
        cell8.setCellValue("Backend Client");
        cell8.setCellStyle(styles.get(UiStyle.TAG_HEADER));

        var rowIndex = 1;

        for (WebMessage message : messages) {
            var entryRow = sheet.createRow(rowIndex);
            var entryCell0 = entryRow.createCell(0);
            entryCell0.setCellValue(message.getConnectorMessageId());
            entryCell0.setCellStyle(styles.get(UiStyle.TAG_CELL_B));
            var entryCell1 = entryRow.createCell(1);
            entryCell1.setCellValue(message.getMessageInfo().getFrom().getPartyId());
            entryCell1.setCellStyle(styles.get(UiStyle.TAG_CELL_B));
            var entryCell2 = entryRow.createCell(2);
            entryCell2.setCellValue(message.getMessageInfo().getTo().getPartyId());
            entryCell2.setCellStyle(styles.get(UiStyle.TAG_CELL_B));
            var entryCell3 = entryRow.createCell(3);
            entryCell3.setCellValue(message.getMessageInfo().getService().getService());
            entryCell3.setCellStyle(styles.get(UiStyle.TAG_CELL_B));
            var entryCell4 = entryRow.createCell(4);
            entryCell4.setCellValue(message.getMessageInfo().getAction().getAction());
            entryCell4.setCellStyle(styles.get(UiStyle.TAG_CELL_B));
            var entryCell5 = entryRow.createCell(5);
            entryCell5.setCellValue(message.getCreatedString());
            entryCell5.setCellStyle(styles.get(UiStyle.TAG_CELL_B));
            var entryCell6 = entryRow.createCell(6);
            entryCell6.setCellValue(message.getDeliveredToNationalSystemString());
            entryCell6.setCellStyle(styles.get(UiStyle.TAG_CELL_B));
            var entryCell7 = entryRow.createCell(7);
            entryCell7.setCellValue(message.getDeliveredToGatewayString());
            entryCell7.setCellStyle(styles.get(UiStyle.TAG_CELL_B));
            var entryCell8 = entryRow.createCell(8);
            entryCell8.setCellValue(message.getBackendName());
            entryCell8.setCellStyle(styles.get(UiStyle.TAG_CELL_B));

            rowIndex++;
        }

        sheet.setColumnWidth(0, 256 * 60);
        sheet.setColumnWidth(1, 256 * 15);
        sheet.setColumnWidth(2, 256 * 15);
        sheet.setColumnWidth(3, 256 * 20);
        sheet.setColumnWidth(4, 256 * 20);
        sheet.setColumnWidth(5, 256 * 40);
        sheet.setColumnWidth(6, 256 * 40);
        sheet.setColumnWidth(7, 256 * 40);
        sheet.setColumnWidth(8, 256 * 30);

        return WebServiceUtil.getInputStreamWithWorkbook(wb);
    }
}
