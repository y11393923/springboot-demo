package com.example.util;

import com.alibaba.fastjson.JSON;
import com.example.annotaion.ExcelColumn;
import com.example.vo.ExcelConvertResponse;
import com.example.vo.ExcelHandleError;
import com.example.vo.ExcelHandleResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @Author: zhouyuyang
 * @Date: 2020/7/20 16:52
 */
public class ExcelReadUtil {

    private static final Logger logger = LoggerFactory.getLogger(ExcelReadUtil.class);

    private Workbook workbook;

    public ExcelReadUtil(InputStream excelStream) {
        workbook = this.createWorkbook(excelStream);
    }

    private Workbook createWorkbook(InputStream excelStream) {
        try {
            return workbook = WorkbookFactory.create(excelStream);
        } catch (IOException e) {
            logger.error("create work book failed", e);
        } catch (InvalidFormatException e) {
            logger.error("create work book format failed", e);
        }
        return null;
    }

    public Workbook getWorkBook() {
        return workbook;
    }

    public void closeWorkBook() {
        if (workbook != null) {
            try {
                workbook.close();
            } catch (Exception e) {
                logger.error("close workbook exception", e);
            }
        }
    }

    /**
     * @param sheetName Excel sheet页序号
     * @param file     excel文件
     * @param cls      Class对象, 不支持对象嵌套, 不支持基础数据类型
     * @return
     * @throws Exception
     */
    public <T> ExcelConvertResponse<T> convertExcelToBeans(File file, Class<T> cls, String sheetName) {
        try {
            this.createWorkbook(new FileInputStream(file));
            return convertExcelToBeans(cls, sheetName);
        } catch (Exception e) {
            logger.error("convertExcelToBeans exception, "
                    + "file:" + file.getPath() + ", class=" + cls.getName() + ", msg:" + e.getMessage());
        }
        return new ExcelConvertResponse<>();
    }

    /**
     * excel第n个sheet页内容转换bean
     *
     * @param sheetName Excel sheet页序号
     * @param cls      Class对象, 不支持对象嵌套, 不支持基础数据类型
     * @return
     * @throws Exception
     */
    public <T> ExcelConvertResponse<T> convertExcelToBeans(Class<T> cls, String sheetName) {
        try {
            return readExcel(cls, sheetName);
        } catch (Exception e) {
            logger.error("convertExcelToBeans exception, class=" + cls.getName() + ", msg:" + e.getMessage(), e);
        }
        return new ExcelConvertResponse<>();
    }

    public boolean checkExcelFormat(String checkSheetName) {
        Sheet sheet = workbook.getSheet(checkSheetName);
        return sheet != null;
    }

    /**
     * 读取excel文件的第（sheetNum）个sheet页
     *
     * @param sheetName Excel sheet页序号
     * @param cls      been对象clazz
     * @return
     * @throws Exception
     * @throws Exception
     */
    private <T> ExcelConvertResponse<T> readExcel(Class<T> cls, String sheetName) throws Exception {
        ExcelConvertResponse<T> excelConvertResponse = new ExcelConvertResponse<>();
        List<ExcelHandleError> excelHandleErrors = new ArrayList<>();
        List<T> list = new ArrayList<>();
        // 解析class field
        Field[] fields = cls.getDeclaredFields();
        Map<String, String> textToKey = new HashMap<String, String>();
        ExcelColumn _excel = null;
        for (Field field : fields) {
            _excel = field.getAnnotation(ExcelColumn.class);
            if (_excel == null) {
                continue;
            }
            textToKey.put(_excel.name(), field.getName());
        }
        Sheet sheet = workbook.getSheet(sheetName);
        Row title = sheet.getRow(0);
        String[] titles = new String[title.getPhysicalNumberOfCells()];
        for (int i = 0; i < title.getPhysicalNumberOfCells(); i++) {
            titles[i] = title.getCell(i).getStringCellValue();
        }
        T t = null;
        Cell cell = null;
        Row row = null;
        int size = 0;
        //开始循环读取行-列
        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            row = sheet.getRow(rowIndex);
            if (isRowEmpty(row)) {
                continue;
            }
            size++;
            t = cls.newInstance();
            boolean isFormatCorrect = true;
            for (int columnIndex = 0; columnIndex < titles.length; columnIndex++) {
                cell = row.getCell(columnIndex);
                if (cell == null) {
                    //单元格内容空
                    continue;
                }
                Object o;
                // 判断是否具有合并单元格
                boolean isMerge = isMergedRegion(sheet, rowIndex, columnIndex);
                if (isMerge) {
                    o = getMergedRegionValue(sheet, rowIndex, columnIndex);
                } else {
                    o = getCellValue(cell);
                }
                if (o == null) {
                    continue;
                }
                ExcelHandleResponse<ExcelHandleError> excelHandleResponse = setCellValueToBean(textToKey.get(titles[columnIndex]), fields, o, t);
                if (excelHandleResponse != null) {
                    boolean success = excelHandleResponse.isSuccess();
                    //标签格式转换失败
                    if (!success) {
                        excelHandleErrors.add(excelHandleResponse.getData());
                        isFormatCorrect = false;
                        break;
                    }
                }
            }
            if (isFormatCorrect) {
                list.add(t);
            }
        }
        excelConvertResponse.setSucceessList(list);
        excelConvertResponse.setSize(size);
        excelConvertResponse.setErrorResultList(excelHandleErrors);
        return excelConvertResponse;
    }

    public boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK && StringUtils.isNotBlank(cell.toString())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 单元格内容赋值给bean属性
     *
     * @param key    当前单元格对应的Bean字段
     * @param fields Bean所有的字段数组
     * @throws Exception
     * @throws
     * @throws Exception
     */
    private <T> void setCellValueToBeanProperty(String key, Field[] fields, Object o, T t) throws Exception {
        if (o == null) {
            return;
        }
        for (Field field : fields) {
            field.setAccessible(true);
            if (!field.getName().equals(key)) {
                continue;
            }
            field.set(t, getFormatValue(field, o));
        }
    }

    private <T> ExcelHandleResponse<ExcelHandleError> setCellValueToBean(String key, Field[] fields, Object o, T t) throws Exception {
        if (o == null) {
            return null;
        }
        ExcelHandleResponse<ExcelHandleError> response = new ExcelHandleResponse<>();
        for (Field field : fields) {
            field.setAccessible(true);
            if (!field.getName().equals(key)) {
                continue;
            }
            Object o1 = getFormatValue(field, o);
            if (o1 instanceof ExcelHandleError) {
                response.setSuccess(false);
                response.setData((ExcelHandleError) o1);
                return response;
            } else {
                field.set(t, o1);
                response.setSuccess(true);
                return response;
            }
        }
        return null;
    }

    /**
     * 获取格式化后的值(根据dataFormat注解)
     *
     * @param field
     * @param o
     * @return
     */
    @SuppressWarnings("unchecked")
    private Object getFormatValue(Field field, Object o) {
        ExcelColumn _excel = field.getAnnotation(ExcelColumn.class);
        Map<String, String> map = null;
        //读取 dataFormat注解, 用于key到value转换
        String formatStr;
        if (_excel != null && (formatStr = _excel.dataFormat()) != null && formatStr.length() > 0) {
            try {
                map = JSON.parseObject(formatStr, Map.class);
            } catch (Exception e) {
                logger.error("setCellValueToBeanProperty jsonToObject error, "
                        + "ignore, field:" + field + ", dataFormat:" + formatStr + ", msg:" + e);
                map = null;
            }
        }
        if (map != null) {
            o = (Object) map.get(o);
        }
        if (o == null) {
            return o;
        }
        try {
            if (field.getType().equals(o.getClass())) {
                return o;
            }
            if (field.getType().equals(Date.class)) {
                if (o != null) {
                    Date date = DateUtil.parseStringToDate(o.toString(), DateUtil.DATE_PATTERN_DEFAULT);
                    if (date != null) {
                        return date;
                    }
                }
                return org.apache.poi.ss.usermodel.DateUtil.getJavaDate(Double.parseDouble(o.toString().trim()));
            }
            if (field.getType().equals(String.class)) {
                return o.toString().trim();
            }
            if (field.getType().equals(Long.class)) {
                return Long.parseLong(o.toString().trim());
            }
            if (field.getType().equals(Integer.class)) {
                Integer v = null;
                try {
                    v = Integer.parseInt(o.toString().trim());
                } catch (Exception e) {
                }
                if (v == null) {
                    //excel常规格式的数字默认带精度，强制转换
                    v = (int) Double.parseDouble(o.toString().trim());
                }
                return v;
            }
            if (field.getType().equals(BigDecimal.class)) {
                return BigDecimal.valueOf(Double.parseDouble(o.toString().trim()));
            }
            if (field.getType().equals(Boolean.class)) {
                return Boolean.parseBoolean(o.toString().trim());
            }
            if (field.getType().equals(Float.class)) {
                return Float.parseFloat(o.toString().trim());
            }
            if (field.getType().equals(Double.class)) {
                return Double.parseDouble(o.toString().trim());
            }
        } catch (Exception e) {
            logger.error("setCellValueToBeanProperty setField exception, field:" + field + ", object:" + o.getClass()
                    + ", msg=" + e.getMessage());
            return new ExcelHandleError(o.toString().trim(), _excel.name(), _excel.name() + "格式有误");
            //throw e;
        }
        return o.toString().trim();
    }

    /**
     * 读取单元格内容
     *
     * @param cell
     * @return
     */
    private Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        Object o = null;
        switch (cell.getCellType()) {
            case XSSFCell.CELL_TYPE_BOOLEAN:
                o = cell.getBooleanCellValue();
                break;
            case XSSFCell.CELL_TYPE_NUMERIC:
                o = cell.getNumericCellValue();
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    o = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(cell.getNumericCellValue());
                } else {
                    o = new DecimalFormat("#.######").format(cell.getNumericCellValue());
                }
                break;
            case XSSFCell.CELL_TYPE_STRING:
                o = cell.getStringCellValue().trim();
                break;
            case XSSFCell.CELL_TYPE_ERROR:
                o = cell.getErrorCellValue();
                break;
            case XSSFCell.CELL_TYPE_BLANK:
                o = null;
                break;
            case XSSFCell.CELL_TYPE_FORMULA:
                o = cell.getCellFormula();
                break;
            default:
                o = null;
                break;
        }
        return o;
    }

    /**
     * 判断指定的单元格是否是合并单元格
     *
     * @param sheet
     * @param row    行下标
     * @param column 列下标
     * @return
     */
    private boolean isMergedRegion(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = (CellRangeAddress) sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if (row >= firstRow && row <= lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取合并单元格的值
     *
     * @param sheet
     * @param row
     * @param column
     * @return
     */
    private Object getMergedRegionValue(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress ca = sheet.getMergedRegion(i);
            int firstColumn = ca.getFirstColumn();
            int lastColumn = ca.getLastColumn();
            int firstRow = ca.getFirstRow();
            int lastRow = ca.getLastRow();
            if (row >= firstRow && row <= lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    Row fRow = sheet.getRow(firstRow);
                    Cell fCell = fRow.getCell(firstColumn);
                    return getCellValue(fCell);
                }
            }
        }
        return null;
    }
}
