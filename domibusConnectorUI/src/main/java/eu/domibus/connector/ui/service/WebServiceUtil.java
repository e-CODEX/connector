/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.service;

import eu.domibus.connector.ui.utils.UiStyle;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * This class provides utility methods for performing operations related to web services and Excel
 * files.
 */
@UtilityClass
public class WebServiceUtil {
    /**
     * Returns an InputStream with the provided HSSFWorkbook object as the content.
     *
     * @param wb The HSSFWorkbook object representing the Excel workbook.
     * @return An InputStream representing the generated Excel file.
     */
    public static InputStream getInputStreamWithWorkbook(HSSFWorkbook wb) {
        var outputStream = new ByteArrayOutputStream();

        try {
            wb.write(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    /**
     * Creates a new Excel workbook with a single sheet.
     *
     * @param initialSheetName The name of the initial sheet in the workbook.
     * @return The created HSSFWorkbook object representing the Excel workbook.
     */
    public static HSSFWorkbook createNewExcel(String initialSheetName) {
        var workbook = new HSSFWorkbook();
        var sheet = workbook.createSheet(initialSheetName);

        // turn off gridlines
        sheet.setDisplayGridlines(false);
        sheet.setPrintGridlines(false);
        sheet.setFitToPage(true);
        sheet.setHorizontallyCenter(true);
        PrintSetup printSetup = sheet.getPrintSetup();

        // the following three statements are required only for HSSF
        sheet.setAutobreaks(true);
        printSetup.setFitHeight((short) 1);
        printSetup.setFitWidth((short) 1);

        return workbook;
    }

    /**
     * Create a library of cell styles.
     */
    public static Map<String, CellStyle> createStyles(Workbook wb) {
        CellStyle style;
        var headerFont = wb.createFont();
        headerFont.setBold(true);
        style = createBorderedStyle(wb);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(headerFont);

        Map<String, CellStyle> styles = new HashMap<>();
        styles.put(UiStyle.TAG_HEADER, style);

        var font1 = wb.createFont();
        font1.setBold(true);
        style = createBorderedStyle(wb);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setFont(font1);
        styles.put(UiStyle.TAG_CELL_B, style);

        style = createBorderedStyle(wb);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setFont(font1);
        styles.put("cell_b_right", style);

        var font2 = wb.createFont();
        font2.setColor(IndexedColors.BLUE.getIndex());
        font2.setBold(true);
        style = createBorderedStyle(wb);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setFont(font2);
        styles.put("cell_bb", style);

        style = createBorderedStyle(wb);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFont(font2);
        styles.put("cell_bb_center", style);

        style = createBorderedStyle(wb);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setFont(font2);
        styles.put("cell_bb_right", style);

        return styles;
    }

    private static CellStyle createBorderedStyle(Workbook wb) {
        var style = wb.createCellStyle();
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        return style;
    }
}
