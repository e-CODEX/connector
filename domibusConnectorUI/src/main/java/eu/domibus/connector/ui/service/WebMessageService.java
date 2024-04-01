package eu.domibus.connector.ui.service;

import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.domibus.connector.ui.dto.WebMessage;
import eu.domibus.connector.ui.persistence.service.DomibusConnectorWebMessagePersistenceService;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;


@Service("webMessageService")
public class WebMessageService {
    private final DomibusConnectorWebMessagePersistenceService messagePersistenceService;

    public WebMessageService(DomibusConnectorWebMessagePersistenceService messagePersistenceService) {
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
                ebmsId,
                DomibusConnectorMessageDirection.GATEWAY_TO_BACKEND
        );
    }

    public Optional<WebMessage> getMessageByBackendMessageId(String backendMessageId) {
        return messagePersistenceService.findMessageByNationalId(
                backendMessageId,
                DomibusConnectorMessageDirection.BACKEND_TO_GATEWAY
        );
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

    public InputStream generateExcel(LinkedList<WebMessage> messages) {
        String sheetName = "Messages List";
        HSSFWorkbook wb = WebServiceUtil.createNewExcel(sheetName);

        Map<String, CellStyle> styles = WebServiceUtil.createStyles(wb);

        HSSFSheet sheet = wb.getSheet(sheetName);

        HSSFRow headerRow = sheet.createRow(0);
        HSSFCell cell0 = headerRow.createCell(0);
        cell0.setCellValue("Connector Message ID");
        cell0.setCellStyle(styles.get("header"));
        HSSFCell cell1 = headerRow.createCell(1);
        cell1.setCellValue("From Party ID");
        cell1.setCellStyle(styles.get("header"));
        HSSFCell cell2 = headerRow.createCell(2);
        cell2.setCellValue("To Party ID");
        cell2.setCellStyle(styles.get("header"));
        HSSFCell cell3 = headerRow.createCell(3);
        cell3.setCellValue("Service");
        cell3.setCellStyle(styles.get("header"));
        HSSFCell cell4 = headerRow.createCell(4);
        cell4.setCellValue("Action");
        cell4.setCellStyle(styles.get("header"));
        HSSFCell cell5 = headerRow.createCell(5);
        cell5.setCellValue("Created");
        cell5.setCellStyle(styles.get("header"));
        HSSFCell cell6 = headerRow.createCell(6);
        cell6.setCellValue("Delivered Backend");
        cell6.setCellStyle(styles.get("header"));
        HSSFCell cell7 = headerRow.createCell(7);
        cell7.setCellValue("Delivered Gateway");
        cell7.setCellStyle(styles.get("header"));
        HSSFCell cell8 = headerRow.createCell(8);
        cell8.setCellValue("Backend Client");
        cell8.setCellStyle(styles.get("header"));

        int rowIndex = 1;

        for (WebMessage message : messages) {

            HSSFRow entryRow = sheet.createRow(rowIndex);
            HSSFCell entryCell0 = entryRow.createCell(0);
            entryCell0.setCellValue(message.getConnectorMessageId());
            entryCell0.setCellStyle(styles.get("cell_b"));
            HSSFCell entryCell1 = entryRow.createCell(1);
            entryCell1.setCellValue(message.getMessageInfo().getFrom().getPartyId());
            entryCell1.setCellStyle(styles.get("cell_b"));
            HSSFCell entryCell2 = entryRow.createCell(2);
            entryCell2.setCellValue(message.getMessageInfo().getTo().getPartyId());
            entryCell2.setCellStyle(styles.get("cell_b"));
            HSSFCell entryCell3 = entryRow.createCell(3);
            entryCell3.setCellValue(message.getMessageInfo().getService().getService());
            entryCell3.setCellStyle(styles.get("cell_b"));
            HSSFCell entryCell4 = entryRow.createCell(4);
            entryCell4.setCellValue(message.getMessageInfo().getAction().getAction());
            entryCell4.setCellStyle(styles.get("cell_b"));
            HSSFCell entryCell5 = entryRow.createCell(5);
            entryCell5.setCellValue(message.getCreatedString());
            entryCell5.setCellStyle(styles.get("cell_b"));
            HSSFCell entryCell6 = entryRow.createCell(6);
            entryCell6.setCellValue(message.getDeliveredToNationalSystemString());
            entryCell6.setCellStyle(styles.get("cell_b"));
            HSSFCell entryCell7 = entryRow.createCell(7);
            entryCell7.setCellValue(message.getDeliveredToGatewayString());
            entryCell7.setCellStyle(styles.get("cell_b"));
            HSSFCell entryCell8 = entryRow.createCell(8);
            entryCell8.setCellValue(message.getBackendName());
            entryCell8.setCellStyle(styles.get("cell_b"));

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
