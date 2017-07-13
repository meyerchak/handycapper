package com.robinhowlett.handycapper.xlsx;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created by rhowlett on 7/10/17.
 */
public class XSSFCellCreator {

    public static XSSFCell asBoolean(XSSFRow row, int columnIndex, Boolean value) {
        XSSFCell cell = row.createCell(columnIndex, Cell.CELL_TYPE_BOOLEAN);
        cell.setCellValue((value != null) ? value : null);
        return cell;
    }

    public static XSSFCell asNumber(XSSFRow row, int columnIndex, Number value) {
        XSSFCell cell = row.createCell(columnIndex, Cell.CELL_TYPE_NUMERIC);
        if (value != null) {
            cell.setCellValue(value.doubleValue());
        }
        return cell;
    }

    public static XSSFCell asText(XSSFRow row, int columnIndex, Object value) {
        XSSFCell cell = row.createCell(columnIndex, Cell.CELL_TYPE_STRING);
        if (value != null) {
            cell.setCellValue((String) value);
        }
        return cell;
    }

    public static XSSFCell asRichText(XSSFRow row, int columnIndex, RichTextString value) {
        XSSFCell cell = row.createCell(columnIndex, Cell.CELL_TYPE_STRING);
        cell.setCellValue(value);
        return cell;
    }

    public static XSSFCell asDate(XSSFRow row, int columnIndex, LocalDate localDate) {
        XSSFCell cell = row.createCell(columnIndex, Cell.CELL_TYPE_STRING);
        if (localDate != null) {
            Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            cell.setCellValue(date);
        }
        return cell;
    }
}
