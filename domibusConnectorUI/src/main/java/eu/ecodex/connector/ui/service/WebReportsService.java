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

import eu.ecodex.connector.ui.dto.WebReport;
import eu.ecodex.connector.ui.dto.WebReportEntry;
import eu.ecodex.connector.ui.persistence.service.DomibusConnectorWebReportPersistenceService;
import eu.ecodex.connector.ui.utils.UiStyle;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The WebReportsService class is responsible for generating web-based reports and Excel files. It
 * utilizes the DomibusConnectorWebReportPersistenceService to load report data from the database.
 */
@NoArgsConstructor
@Service("webReportsService")
public class WebReportsService {
    private DomibusConnectorWebReportPersistenceService reportPersistenceService;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    @Autowired
    public void setReportPersistenceService(
        DomibusConnectorWebReportPersistenceService reportPersistenceService) {
        this.reportPersistenceService = reportPersistenceService;
    }

    /**
     * Generates a web-based report based on the specified date range and inclusion of evidences.
     *
     * @param fromDate         The starting date of the report range.
     * @param toDate           The ending date of the report range.
     * @param includeEvidences Specifies whether to include evidences in the report.
     * @return The generated report as a list of WebReportEntry objects. Returns null if the report
     *      cannot be generated.
     */
    public List<WebReportEntry> generateReport(
        Date fromDate, Date toDate, boolean includeEvidences) {
        List<WebReportEntry> report;
        if (includeEvidences) {
            report = reportPersistenceService.loadReportWithEvidences(fromDate, toDate);
        } else {
            report = reportPersistenceService.loadReport(fromDate, toDate);
        }
        return report;
    }

    /**
     * Generates an Excel file based on the specified date range and a list of web reports.
     *
     * @param fromDate The starting date of the report range.
     * @param toDate   The ending date of the report range.
     * @param report   The list of web reports containing the data for the Excel file.
     * @return An InputStream representing the generated Excel file.
     */
    public InputStream generateExcel(Date fromDate, Date toDate, List<WebReport> report) {
        var sheetName = sdf.format(fromDate) + " - " + sdf.format(toDate);
        var wb = WebServiceUtil.createNewExcel(sheetName);

        Map<String, CellStyle> styles = WebServiceUtil.createStyles(wb);

        var sheet = wb.getSheet(sheetName);

        var headerRow = sheet.createRow(0);
        var cell0 = headerRow.createCell(0);
        cell0.setCellValue("Party");
        cell0.setCellStyle(styles.get(UiStyle.TAG_HEADER));
        var cell1 = headerRow.createCell(1);
        cell1.setCellValue("Service");
        cell1.setCellStyle(styles.get(UiStyle.TAG_HEADER));
        var cell2 = headerRow.createCell(2);
        cell2.setCellValue("Messages received from");
        cell2.setCellStyle(styles.get(UiStyle.TAG_HEADER));
        var cell3 = headerRow.createCell(3);
        cell3.setCellValue("Messages sent to");
        cell3.setCellStyle(styles.get(UiStyle.TAG_HEADER));

        var rowIndex = 1;
        long overallReceived = 0;
        long overallSent = 0;

        for (WebReport period : report) {

            var periodHeaderRow = sheet.createRow(rowIndex);
            var periodHeaderCell = periodHeaderRow.createCell(0);
            periodHeaderCell.setCellValue(period.getPeriod());
            periodHeaderCell.setCellStyle(styles.get("cell_bb_center"));

            rowIndex++;
            sheet.addMergedRegion(CellRangeAddress.valueOf("$A$" + rowIndex + ":$D$" + rowIndex));

            for (WebReportEntry entry : period.getEntries()) {
                var entryRow = sheet.createRow(rowIndex);
                var entryCell0 = entryRow.createCell(0);
                entryCell0.setCellValue(entry.getParty());
                entryCell0.setCellStyle(styles.get(UiStyle.TAG_CELL_B));
                var entryCell1 = entryRow.createCell(1);
                entryCell1.setCellValue(entry.getService());
                entryCell1.setCellStyle(styles.get(UiStyle.TAG_CELL_B));
                var entryCell2 = entryRow.createCell(2);
                entryCell2.setCellValue(entry.getReceived());
                entryCell2.setCellStyle(styles.get(UiStyle.TAG_CELL_B));
                var entryCell3 = entryRow.createCell(3);
                entryCell3.setCellValue(entry.getSent());
                entryCell3.setCellStyle(styles.get(UiStyle.TAG_CELL_B));

                rowIndex++;
            }

            var summaryRow = sheet.createRow(rowIndex);
            var summaryCell0 = summaryRow.createCell(0);
            summaryCell0.setCellValue("Totals");
            summaryCell0.setCellStyle(styles.get("cell_b_right"));

            var summaryCell1 = summaryRow.createCell(1);
            summaryCell1.setCellStyle(styles.get(UiStyle.TAG_CELL_B));

            rowIndex++;
            sheet.addMergedRegion(CellRangeAddress.valueOf("$A$" + rowIndex + ":$B$" + rowIndex));

            var summaryCell2 = summaryRow.createCell(2);
            summaryCell2.setCellValue(period.getSumReceived());
            summaryCell2.setCellStyle(styles.get(UiStyle.TAG_CELL_B));

            var summaryCell3 = summaryRow.createCell(3);
            summaryCell3.setCellValue(period.getSumSent());
            summaryCell3.setCellStyle(styles.get(UiStyle.TAG_CELL_B));

            overallReceived += period.getSumReceived();
            overallSent += period.getSumSent();
        }

        var summaryRow = sheet.createRow(rowIndex);
        var summaryCell0 = summaryRow.createCell(0);
        summaryCell0.setCellValue("Overall Totals");
        summaryCell0.setCellStyle(styles.get("cell_bb_right"));

        var summaryCell1 = summaryRow.createCell(1);
        summaryCell1.setCellStyle(styles.get(UiStyle.TAG_CELL_B));

        rowIndex++;
        sheet.addMergedRegion(CellRangeAddress.valueOf("$A$" + rowIndex + ":$B$" + rowIndex));

        var summaryCell2 = summaryRow.createCell(2);
        summaryCell2.setCellValue(overallReceived);
        summaryCell2.setCellStyle(styles.get("cell_bb"));

        var summaryCell3 = summaryRow.createCell(3);
        summaryCell3.setCellValue(overallSent);
        summaryCell3.setCellStyle(styles.get("cell_bb"));

        sheet.setColumnWidth(0, 256 * 10);
        sheet.setColumnWidth(1, 256 * 15);
        sheet.setColumnWidth(2, 256 * 30);
        sheet.setColumnWidth(3, 256 * 30);

        return WebServiceUtil.getInputStreamWithWorkbook(wb);
    }
}
