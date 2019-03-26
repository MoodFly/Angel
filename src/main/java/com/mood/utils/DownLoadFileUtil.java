package com.mood.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DownLoadFileUtil {
    public void dowmloadFile(List<String> imageUrl,String serverFile, HttpServletResponse response){
        response.setContentType("application/octet-stream");
        response.setHeader("Accept-Ranges","bytes");
        response.setCharacterEncoding("utf-8");

        try{
            if (imageUrl.size()==1){
                imageUrl.forEach(x->{
                    String filename="";
                    if (x.contains(".jpg")){
                        filename+=x.substring(x.indexOf("o_"),x.indexOf(".jpg"));
                    }else if (x.contains(".png")){
                        filename+=x.substring(x.indexOf("o_"),x.indexOf(".png"));
                    }else if (x.contains(".gif")){
                        filename+=x.substring(x.indexOf("o_"),x.indexOf(".gif"));
                    }else if (x.contains(".jpeg")){
                        filename+=x.substring(x.indexOf("o_"),x.indexOf(".jpeg"));
                    }else {
                        filename+="download_image.jpg";
                    }
                    response.addHeader("Content-Disposition", "attachment;filename="+filename);
                    download(x,filename,response);
                });
            }else {
                String file=serverFile+"zip.zip";
                ZipOutputStream outStream = new ZipOutputStream(new FileOutputStream(file));
                for (String x : imageUrl) {
                    String filename="";
                    if (x.contains(".jpg")){
                        filename+=x.substring(x.indexOf("o_"),x.indexOf(".jpg"))+".jpg";
                    }else if (x.contains(".png")){
                        filename+=x.substring(x.indexOf("o_"),x.indexOf(".png"))+".png";
                    }else if (x.contains(".gif")){
                        filename+=x.substring(x.indexOf("o_"),x.indexOf(".gif"))+".gif";
                    }else if (x.contains(".jpeg")){
                        filename+=x.substring(x.indexOf("o_"),x.indexOf(".jpeg"))+".jpeg";
                    }else {
                        filename+="download_image.jpg";
                    }
                    outStream.putNextEntry(new ZipEntry(filename));
                    outStream.write(download(x));
                }
                outStream.finish();
                File tempFile=new File(serverFile);
                if (!tempFile.exists()){
                    tempFile.mkdir();
                }
                InputStream fis = new BufferedInputStream(new FileInputStream(file));
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                fis.close();
                response.addHeader("Content-Disposition", "attachment;filename="+"zip.zip");
                response.getOutputStream().write(buffer);
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {

        }
    }
    private static void download(String url, String filename,HttpServletResponse response) {
        try {
            byte[] data = download(url);
            OutputStream ouputStream=response.getOutputStream();
            ouputStream.write(data);
            ouputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static byte[]  download(String url) {
        System.out.println(url);
        HttpClientUtil httpClientUtil=HttpClientUtil.getInstance();
        HttpEntity resp = null;
        try {
            resp =httpClientUtil.httpGetMethod(url);
            InputStream is =  resp.getContent();
            byte[] data = readInputStream(is);
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unexpected code " + resp);
        }
        return null;
    }
    /**
     * 读取字节输入流内容
     *
     * @return
     */
    private static byte[] readInputStream(InputStream is) {
        ByteArrayOutputStream writer = new ByteArrayOutputStream();
        byte[] buff = new byte[1024 * 2];
        int len = 0;
        try {
            while ((len = is.read(buff)) != -1) {
                writer.write(buff, 0, len);
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer.toByteArray();
    }
    public static void main(String[] args ){

    }
}
