package eu.domibus.connector.ui.service;

import eu.domibus.connector.ui.dto.WebReport;
import eu.domibus.connector.ui.dto.WebReportEntry;
import eu.domibus.connector.ui.persistence.service.DomibusConnectorWebReportPersistenceService;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service("webReportsService")
public class WebReportsService {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    private DomibusConnectorWebReportPersistenceService reportPersistenceService;

    public WebReportsService() {
        // TODO Auto-generated constructor stub
    }

    @Autowired
    public void setReportPersistenceService(DomibusConnectorWebReportPersistenceService reportPersistenceService) {
        this.reportPersistenceService = reportPersistenceService;
    }

    public List<WebReportEntry> generateReport(Date fromDate, Date toDate, boolean includeEvidences) {
        List<WebReportEntry> report = null;
        if (includeEvidences) {
            report = reportPersistenceService.loadReportWithEvidences(fromDate, toDate);
        } else {
            report = reportPersistenceService.loadReport(fromDate, toDate);
        }
        return report;
    }

    public InputStream generateExcel(Date fromDate, Date toDate, List<WebReport> report) {
        String sheetName = sdf.format(fromDate) + " - " + sdf.format(toDate);
        HSSFWorkbook wb = WebServiceUtil.createNewExcel(sheetName);

        Map<String, CellStyle> styles = WebServiceUtil.createStyles(wb);

        HSSFSheet sheet = wb.getSheet(sheetName);

        HSSFRow headerRow = sheet.createRow(0);
        HSSFCell cell0 = headerRow.createCell(0);
        cell0.setCellValue("Party");
        cell0.setCellStyle(styles.get("header"));
        HSSFCell cell1 = headerRow.createCell(1);
        cell1.setCellValue("Service");
        cell1.setCellStyle(styles.get("header"));
        HSSFCell cell2 = headerRow.createCell(2);
        cell2.setCellValue("Messages received from");
        cell2.setCellStyle(styles.get("header"));
        HSSFCell cell3 = headerRow.createCell(3);
        cell3.setCellValue("Messages sent to");
        cell3.setCellStyle(styles.get("header"));

        int rowIndex = 1;
        long overallReceived = 0;
        long overallSent = 0;

        for (WebReport period : report) {

            HSSFRow periodHeaderRow = sheet.createRow(rowIndex);
            HSSFCell periodHeaderCell = periodHeaderRow.createCell(0);
            periodHeaderCell.setCellValue(period.getPeriod());
            periodHeaderCell.setCellStyle(styles.get("cell_bb_center"));

            rowIndex++;
            sheet.addMergedRegion(CellRangeAddress.valueOf("$A$" + rowIndex + ":$D$" + rowIndex));

            for (WebReportEntry entry : period.getEntries()) {
                HSSFRow entryRow = sheet.createRow(rowIndex);
                HSSFCell entryCell0 = entryRow.createCell(0);
                entryCell0.setCellValue(entry.getParty());
                entryCell0.setCellStyle(styles.get("cell_b"));
                HSSFCell entryCell1 = entryRow.createCell(1);
                entryCell1.setCellValue(entry.getService());
                entryCell1.setCellStyle(styles.get("cell_b"));
                HSSFCell entryCell2 = entryRow.createCell(2);
                entryCell2.setCellValue(entry.getReceived());
                entryCell2.setCellStyle(styles.get("cell_b"));
                HSSFCell entryCell3 = entryRow.createCell(3);
                entryCell3.setCellValue(entry.getSent());
                entryCell3.setCellStyle(styles.get("cell_b"));

                rowIndex++;
            }

            HSSFRow summaryRow = sheet.createRow(rowIndex);
            HSSFCell summaryCell0 = summaryRow.createCell(0);
            summaryCell0.setCellValue("Totals");
            summaryCell0.setCellStyle(styles.get("cell_b_right"));

            HSSFCell summaryCell1 = summaryRow.createCell(1);
            summaryCell1.setCellStyle(styles.get("cell_b"));

            rowIndex++;
            sheet.addMergedRegion(CellRangeAddress.valueOf("$A$" + rowIndex + ":$B$" + rowIndex));

            HSSFCell summaryCell2 = summaryRow.createCell(2);
            summaryCell2.setCellValue(period.getSumReceived());
            summaryCell2.setCellStyle(styles.get("cell_b"));

            HSSFCell summaryCell3 = summaryRow.createCell(3);
            summaryCell3.setCellValue(period.getSumSent());
            summaryCell3.setCellStyle(styles.get("cell_b"));

            overallReceived += period.getSumReceived();
            overallSent += period.getSumSent();
        }

        HSSFRow summaryRow = sheet.createRow(rowIndex);
        HSSFCell summaryCell0 = summaryRow.createCell(0);
        summaryCell0.setCellValue("Overall Totals");
        summaryCell0.setCellStyle(styles.get("cell_bb_right"));

        HSSFCell summaryCell1 = summaryRow.createCell(1);
        summaryCell1.setCellStyle(styles.get("cell_b"));

        rowIndex++;
        sheet.addMergedRegion(CellRangeAddress.valueOf("$A$" + rowIndex + ":$B$" + rowIndex));

        HSSFCell summaryCell2 = summaryRow.createCell(2);
        summaryCell2.setCellValue(overallReceived);
        summaryCell2.setCellStyle(styles.get("cell_bb"));

        HSSFCell summaryCell3 = summaryRow.createCell(3);
        summaryCell3.setCellValue(overallSent);
        summaryCell3.setCellStyle(styles.get("cell_bb"));

        sheet.setColumnWidth(0, 256 * 10);
        sheet.setColumnWidth(1, 256 * 15);
        sheet.setColumnWidth(2, 256 * 30);
        sheet.setColumnWidth(3, 256 * 30);

        return WebServiceUtil.getInputStreamWithWorkbook(wb);
    }
}
