package com.example.demoregler.services.excel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Thie class contains methods to generate a sheet
 */

@Setter
@Getter
@AllArgsConstructor
//@NoArgsConstructor
public class RuleSheetGeneratorUtilities {

    /**
     * static RuleSheetGenerator.rowNumber controls the rownumber, so we start with it.
     * Nested loop: we loop rows and each column at each row.
     * Each cell: We create the cell, set the style, get the value from String[][] and set the value in the cell.
     * After each row the startColumnNumber has to be set back again. ColumnNumber keeps the count on the row.
     * RuleSheetGenerator.rowNumber increments after each row.
     * @param wb workbook
     * @param sheet
     * @param startColumnNumber
     * @param values are stored in two-dimensional array of String.
     *               The first dimension is row[], the second dimension is column[]
     *               Each value in array represents a row.
     */
    public void generateRows(XSSFWorkbook wb, XSSFSheet sheet, int startColumnNumber, String[][] values) {

        int columnCounter = startColumnNumber;

        //loops rows (one value in values = row)
        for (int row = RuleSheetGenerator.rowNumber; row < values.length; row++) {
            //loops columns at the row
            for (int column = 0; column < values[row].length; column++) {

                if (sheet.getRow(row) == null) sheet.createRow(row);

                Cell cell = sheet.getRow(row).createCell(columnCounter);
                setCellStyle(wb, cell);
                String value = values[row][column];

                if (value != null && value.equals("emptyCell")) value = "";

                cell.setCellValue(value);
                columnCounter++;
            }
            RuleSheetGenerator.rowNumber++;
            columnCounter = startColumnNumber;
        }
    }

    /**
     * This method generates one row and values on the columns are stores in List.
     * static RuleSheetGenerator.rowNumber controls the rownumber.
     * @param wb workbook
     * @param sheet
     * @param startColumnNumber
     * @param values are stored in List of String.
     */
    public void generateRowFromList(XSSFWorkbook wb, XSSFSheet sheet, int startColumnNumber, List<String> values) {

        if (sheet.getRow(RuleSheetGenerator.rowNumber) == null) sheet.createRow(RuleSheetGenerator.rowNumber);
        for (String s : values) {
            Cell cell = sheet.getRow(RuleSheetGenerator.rowNumber).createCell(startColumnNumber);
            setCellStyle(wb, cell);
            cell.setCellValue(s);
            startColumnNumber++;
        }
    }

    /**
     * CellRangeAddress creates a region with first rownumber, second rownumber, first column and second column.
     * We can use it to merge alle cels in this region.
     * Here we will create one row with merged cells: -o-o-o-o-o etc.
     * If the row does not exists, we create it.
     * static RuleSheetGenerator.rowNumber controls the rownumber.
     * @param sheet
     */
    public void generateRowWithPattern(XSSFSheet sheet){

        if (sheet.getRow(RuleSheetGenerator.rowNumber) == null) sheet.createRow(RuleSheetGenerator.rowNumber);
        String str = "-o";
        String repeated = str.repeat(125);
        Cell cell = sheet.getRow(RuleSheetGenerator.rowNumber).createCell(0);
        sheet.addMergedRegion(new CellRangeAddress(RuleSheetGenerator.rowNumber,RuleSheetGenerator.rowNumber,0,12));
        cell.setCellValue(repeated);
    }

    public void generateIndexRows(XSSFSheet sheet, List<String> ruleNames){
        //her ska jeg sætte navne ind i sheet
        // og lav dem til links?
    }


    /**
     * We create cellStyle and XSSF-font to make the cell bold
     * default celleStyle also has to be added
     * @param wb workbook
     * @param cell that has to be bold
     */

    public void setBold(XSSFWorkbook wb, Cell cell) {
        XSSFCellStyle cellStyle = wb.createCellStyle();
        XSSFFont font = wb.createFont();
        font.setBold(true);
        cellStyle.setFont(font);
        cellStyle.setWrapText(true);
        cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cell.setCellStyle(cellStyle);
    }

    /**
     * This method creates default cellStyle for each cell:
     * wrapText, vertical alignment is top and horizontal alignment is left.
     * This applies to not-bold cells
     * @param wb
     * @param cell
     */
    public void setCellStyle(XSSFWorkbook wb, Cell cell){
        XSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setWrapText(true);
        cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cell.setCellStyle(cellStyle);
    }

    /**
     *
     * @param time comes as a Timstamp from the JSON
     * @return formatted time in accordance with pattern
     */
    public String convertTimestamp(Timestamp time) {
        Date date = new Date(time.getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        return formatter.format(date);
    }

    /**
     * Sheet includes both autosized and customized column width
     * column width 2560 = 10 characters is default:
     * https://grobmeier.solutions/generating-excel-sheets-with-spring-and-apache-poi.html
     * @param sheet
     */
    public void styleColumnWidth(XSSFSheet sheet){

        sheet.setColumnWidth(0, 1000);
        for(int i = 1; i < 3; i++) sheet.autoSizeColumn(i, true);

        for(int i = 3; i < 7; i++) sheet.setColumnWidth(i, 6500);

        for (int i = 7; i < 12; i++)sheet.autoSizeColumn(i, true);
    }

}
