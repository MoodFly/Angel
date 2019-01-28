package com.mood.base;

import com.mood.utils.DateUtil;
import org.apache.commons.codec.CharEncoding;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

/**
 * @author: by Mood
 * @date: 2018-8-30 11:11:11
 * @Description: base 控制层
 * @version: 1.0
 */
public class BaseController {
    private final Logger logger = LoggerFactory.getLogger(BaseController.class);
    /**
     * @param request
     * @param response
     * @param sxssfWorkbook
     * @param filename
     * @author: by Mood
     * @date: 2011-01-14 11:11:11
     * @Description: 执行poi导出
     * @version: 1.0
     */
    protected void export(HttpServletRequest request, HttpServletResponse response, SXSSFWorkbook sxssfWorkbook, String filename) {
        OutputStream ouputStream = null;
        try{
            response.setContentType("application/vnd.ms-excel");
            String browserName = request.getHeader("User-Agent") != null ? request.getHeader("User-Agent").toUpperCase() : "";
            if(null == filename){
                filename =  DateUtil.getDay(DateUtil.defaultDateTimeFormatter) + ".xlsx";
            }else{
                filename = filename + '-' + DateUtil.getDay(DateUtil.defaultDateTimeFormatter) + ".xlsx";
            }
            if (browserName.contains("IE") || browserName.contains("INTERNETEXPLORER")) {
                filename = URLEncoder.encode(filename, CharEncoding.UTF_8);
                filename = filename.replaceAll("\\+", "%20");
            } else {
                filename = new String(filename.getBytes(CharEncoding.UTF_8), "ISO8859-1");
            }
            if (browserName.contains("FIREFOX")) {
                response.addHeader("Content-Disposition", "attachment;filename=\"" + filename + "\"");
            } else {
                response.addHeader("Content-Disposition", "attachment;filename=" + filename);
            }
            ouputStream = response.getOutputStream();
            sxssfWorkbook.write(ouputStream);
            ouputStream.flush();
        } catch (Exception e) {
            logger.error("export error. e = {}", e);
        } finally {
            IOUtils.closeQuietly(ouputStream);
            try {
                sxssfWorkbook.close();
            } catch (IOException e) {
                logger.error("close sxssf work book error and e : ", e);
            }
        }
    }

}
