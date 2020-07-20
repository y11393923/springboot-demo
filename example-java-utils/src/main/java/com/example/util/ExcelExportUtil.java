package com.example.util;

import com.alibaba.fastjson.JSONObject;
import com.example.vo.TaskRunFlag;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @Author: zhouyuyang
 * @Date: 2020/7/20 17:08
 */
public class ExcelExportUtil {
    private static final Logger logger = LoggerFactory.getLogger(ExcelExportUtil.class);

    private static final Integer ROW_ACCESS_WINDOW_SIZE = 1000;

    private static final Integer ROW_ACCESS_WINDOW_MIN_SIZE = 1;

    private Workbook workbook;
    private Sheet sheet;
    private Drawing patriarch;
    private TaskRunFlag taskRunFlag;

    //初始化工作区等
    private void init(int rowAccessWindowSize, String sheetName) {
        //建立缓存工作区，当超过rowAccessWindowSize时缓存到磁盘
        if(rowAccessWindowSize >= ROW_ACCESS_WINDOW_SIZE){
            rowAccessWindowSize = ROW_ACCESS_WINDOW_SIZE;
        }else if(rowAccessWindowSize < ROW_ACCESS_WINDOW_MIN_SIZE){
            rowAccessWindowSize = ROW_ACCESS_WINDOW_MIN_SIZE;
        }
        workbook = new SXSSFWorkbook(rowAccessWindowSize);
        sheet = workbook.createSheet(sheetName);
        patriarch = sheet.createDrawingPatriarch();
    }

    /**
     * 单元格映射
     */
    public static class CellMap {
        private String title;// 标题
        private String property;// 属性

        public CellMap(String title, String property) {
            this.title = title;
            this.property = property;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }

    }

    /**
     * 导出Excel
     * @param cellMapList 单元格映射列表
     * @param dataList 数据列表
     * @param rowAccessWindowSize 内存中缓存记录数
     * @param path 保存路径
     * @throws Exception
     */
    public boolean exportSXSSFExcel(String sheetName, List<CellMap> cellMapList, List<T> dataList, int rowAccessWindowSize, String path) throws Exception {
        logger.info("exportSXSSFExcel param: sheetName={}, cellMapList={}, dataList={}, rowAccessWindowSize={}, path={},cellMapList={}", sheetName, cellMapList.size(), dataList.size(), rowAccessWindowSize, path, JSONObject.toJSON(cellMapList));
        init(rowAccessWindowSize, sheetName);
        Row row;
        Cell cell;
        if (cellMapList == null || cellMapList.size() <= 0) {
            throw new Exception("cellMapList不能为空或小于等于0");
        }
        int rowIndex = 0;
        CellStyle contentStyle = createContentStyle();
        row = sheet.createRow(rowIndex++);
        sheet.setDefaultColumnWidth(20);
        int cellSize = cellMapList.size();
        for (int i = 0; i < cellSize; i++) {
            CellMap cellMap = cellMapList.get(i);
            String title = cellMap.getTitle();
            cell = row.createCell(i);
            cell.setCellStyle(createHeadStyle());
            cell.setCellValue(title);
        }
        // 数据
        int rowSize = (dataList == null) ? 0 : dataList.size();
        for (int i = rowIndex; i < rowSize + rowIndex; i++) {
            Long time1 = System.currentTimeMillis();
            T t = dataList.get(i - rowIndex);
            Class<?> clazz = t.getClass();
            row = sheet.createRow(i);
            row.setHeightInPoints(72);
            for (int j = 0; j < cellSize; j++) {
                CellMap cellMap = cellMapList.get(j);
                cell = row.createCell(j);
                cell.setCellStyle(contentStyle);
                Object value = null;
                Field field;
                try {
                    field = clazz.getDeclaredField(cellMap.getProperty());
                    field.setAccessible(true);
                    value = field.get(t);
                } catch (Exception e) {
                    logger.error("[error] >>> export excel error, j={}, error={}", j, e);
                    break;
                }
                if (value != null) {
                    if (value instanceof byte[]) {
                        cell = row.createCell(j);
                        cell.setCellStyle(contentStyle);
                        insertPictureByByteArray((byte[])value, "png", cell);
                    }else {
                        cell = row.createCell(j);
                        cell.setCellStyle(contentStyle);
                        RichTextString text = new XSSFRichTextString((String)value);
                        cell.setCellValue(text);
                    }
                } else {
                    cell = row.createCell(j);
                    cell.setCellStyle(contentStyle);
                    RichTextString text = new XSSFRichTextString("");
                    cell.setCellValue(text);
                }
            }
            logger.info("exportSXSSFExcel create row cost: {} ms", System.currentTimeMillis()-time1);
            if(!taskRunFlag.isFlag()){
                // 取消操作
                return false;
            }
        }
        if(taskRunFlag.isFlag()){
            return write2Disk(path);
        }else{
            return false;
        }
    }

    private void insertPictureByByteArray(byte[] data, String ext, Cell cell) {
        Long t = System.currentTimeMillis();
        ByteArrayOutputStream byteArrayOut = null;
        try {
            byteArrayOut = new ByteArrayOutputStream();
            BufferedImage bufferedImg = ImageIO.read(new ByteArrayInputStream(data));
            int imgType = Workbook.PICTURE_TYPE_PNG;
            if (ext.equals("png")) {
                ImageIO.write(bufferedImg, "png", byteArrayOut);
            }
            if (ext.equals("jpg")) {
                ImageIO.write(bufferedImg, "jpg", byteArrayOut);
                imgType = Workbook.PICTURE_TYPE_JPEG;
            }
            byte[] imgBt = byteArrayOut.toByteArray();

            if (cell != null) {
                //Drawing patriarch = sheet.createDrawingPatriarch();
                int r = cell.getRowIndex();
                short c = (short) cell.getColumnIndex();
                XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, c, r, c, r);
                anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
                patriarch.createPicture(anchor, workbook.addPicture(imgBt, imgType)).resize(0.5, 1);
            }
//            logger.info("insertPictureByByteArray cost {} ms", System.currentTimeMillis()-t);
        } catch (Exception e) {
            logger.error("insertPictureByByteArray error:{}", e);
        } finally {
            if (byteArrayOut != null) {
                try {
                    byteArrayOut.close();
                } catch (IOException e) {
                    logger.error("insertPictureByByteArray IOException:{}", e);
                }
            }
        }
    }

    private boolean write2Disk(String path) {
        logger.info("write2Disk start");
        Long t= System.currentTimeMillis();
        FileOutputStream out = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            out = new FileOutputStream(file);
            workbook.write(out);
        } catch (Exception e) {
            logger.error("[error] >>> write excel 2 disk error");
            return false;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                logger.error("write disk failed",e);
            }
        }
        logger.info("write2Disk cost: {} ms", System.currentTimeMillis()-t);
        return true;
    }

    private CellStyle createHeadStyle() {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.SKY_BLUE.index);
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        Font font = workbook.createFont();
        font.setColor(IndexedColors.BLACK.index);
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        return style;
    }

    private CellStyle createContentStyle() {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.WHITE.index);
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setWrapText(true);    //设置自动换行
        Font font = workbook.createFont();
        font.setBold(false);
        style.setFont(font);
        return style;
    }

    private static void createCell(String value, Row row, CellStyle contentStyle, int count) {
        Cell cell = row.createCell(count);
        cell.setCellStyle(contentStyle);
        RichTextString text = new XSSFRichTextString(value);
        cell.setCellValue(text);
    }

    private static Cell creatEmptyCell(Row row, CellStyle style, int count) {
        Cell cell = row.createCell(count);
        cell.setCellStyle(style);
        return cell;
    }

    public static void main(String[] args) {

    }

    public TaskRunFlag getTaskRunFlag() {
        return taskRunFlag;
    }

    public void setTaskRunFlag(TaskRunFlag taskRunFlag) {
        this.taskRunFlag = taskRunFlag;
    }
}
