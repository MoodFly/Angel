package com.mood.utils;

import com.mood.annotation.SheetName;
import com.mood.annotation.Column;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

/**
 * @author: by Mood
 * @date: 2011-01-14 11:11:11
 * @Description: 导出工具包
 * @version: 1.0
 */
public class ExportUtil {
    public static <T> SXSSFWorkbook exportExcel(Collection<T> data, Class<T> clazz) throws Exception {
        Annotation annotation = clazz.getAnnotation(SheetName.class);
        String title;
        if(annotation != null){
            title = ((SheetName)annotation).Param();
        }else{
            title = DateUtil.getDayTime(DateUtil.defaultDateTimeFormatter).toString();
        }
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet(title);
        sheet.setDefaultColumnWidth(20);
        Iterator<T> it= data.iterator();
        int index = 0;
        Row row = sheet.createRow(0);
        Cell cellHeader;
        Field[] declaredFields = clazz.getDeclaredFields();
        int ci = 0;
        for(Field field : declaredFields){
            Annotation fa = field.getAnnotation(Column.class);
            if(fa == null){
                continue;
            }
            cellHeader = row.createCell(ci);
            cellHeader.setCellValue(((Column)fa).Param());
            ci++;
        }
        while (it.hasNext()){
            index++;
            row = sheet.createRow(index);
            T t=it.next();
            Field[] fields = t.getClass().getDeclaredFields();
            for (Field x : fields) {
                int i = 0;
                Cell cell = row.createCell(i);
                try {
                    Object result = t.getClass().getMethod("", new Class[]{}).invoke(t, null);
                    if (result instanceof Boolean) {
                        cell.setCellValue((Boolean) result);
                    } else if (result instanceof String) {
                        cell.setCellValue((String) result);
                    } else if (result instanceof Date) {
                        cell.setCellValue(DateUtil.dateFormat((Date)result,DateUtil.defaultDateTimeFormatter));
                    }else if (result instanceof Double) {
                        cell.setCellValue((Double)result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                i++;
            }
        }
        return workbook;
    }

}
