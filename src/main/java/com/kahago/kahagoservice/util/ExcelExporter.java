package com.kahago.kahagoservice.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Hendro yuwono
 */
public class ExcelExporter<T> {

    private Class aClass;
    private List<T> dataExcel;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    public ExcelExporter(Class aClass, List<T> list) {
        this.dataExcel =  list;
        this.aClass = aClass;
        this.createWorkbook();
    }

    public ByteArrayInputStream exporter() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private void createWorkbook() {
        this.workbook = new XSSFWorkbook();
        this.sheet = workbook.createSheet();
        this.initHeaderTable(0, 0);
        this.initBodyTable(1, 0);
    }

    private CellStyle fontHeaderColumnAndCellStyle() {
        XSSFFont font = workbook.createFont();
        font.setBold(true);

        CellStyle styleHeader = workbook.createCellStyle();
        styleHeader.setFont(font);
        styleHeader.setWrapText(true);
        styleHeader.setFillPattern(FillPatternType.DIAMONDS);
        return styleHeader;
    }

    private void initHeaderTable(int positionOfRow, int positionOfColumn) {
        CellStyle styleHeader = fontHeaderColumnAndCellStyle();

        Row row = sheet.createRow(positionOfRow);
        for (Field field : aClass.getDeclaredFields()) {
            field.setAccessible(true);

            sheet.setColumnWidth(positionOfColumn, 4000);
            Cell cell = row.createCell(positionOfColumn);
            cell.setCellValue(splitCamelCase(field.getName()));
            cell.setCellStyle(styleHeader);
            positionOfColumn++;
        }
    }

    private void initBodyTable(int positionOfRow, int positionOfColumn) {
        try {
            for (Object object : dataExcel) {
                Row row = sheet.createRow(positionOfRow);
                int column = positionOfColumn;
                for (Field field : aClass.getDeclaredFields()) {
                    field.setAccessible(true);
                    Object value = field.get(object);
                    if (value != null) {
                        if (value instanceof String)
                            row.createCell(column).setCellValue((String) value);
                        else if (value instanceof Double)
                            row.createCell(column).setCellValue((Double) value);
                        else if (value instanceof Integer || value instanceof Long)
                            row.createCell(column).setCellValue((Long) value);
                        else if (value instanceof LocalDate)
                            row.createCell(column).setCellValue((LocalDate) value);
                        else if (value instanceof LocalDateTime)
                            row.createCell(column).setCellValue((LocalDateTime) value);
                    }

                    column++;
                }
                positionOfRow++;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private String splitCamelCase(String s) {
        return s.replaceAll(
                String.format("%s|%s|%s", "(?<=[A-Z])(?=[A-Z][a-z])", "(?<=[^A-Z])(?=[A-Z])", "(?<=[A-Za-z])(?=[^A-Za-z])"),
                " "
        );
    }
}
