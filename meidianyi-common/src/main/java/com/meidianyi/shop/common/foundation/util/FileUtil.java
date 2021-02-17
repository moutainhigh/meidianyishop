package com.meidianyi.shop.common.foundation.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import com.meidianyi.shop.common.pojo.shop.base.Base64DecodedMultipartFile;

import java.io.*;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/***
 * File Utils
 * @author 卢光耀
 * @date 2019-09-05 10:30
 *
*/
@Slf4j
public class FileUtil {

    private FileUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 创建文件
     * @param fileName 文件名
     * @param path  路径
     * @param content   内容
     * @throws IOException
     */
    public static void createFile(String fileName,String path,String content) {
        logStart(fileName,path);
        assertFilePath(path);
        File file = new File(path,fileName);
        try (Writer write = new FileWriter(file);){
            write.write(content);
            write.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logEnd(file.getName(),file.getPath());

    }

    /**
     *  创建压缩文件
     * @param fileName 需要压缩的文件名
     * @param zipFileName  压缩文件的名称
     * @param path  存储路径
     * @param content  内容
     * @throws IOException
     */
    public static void createZip(String fileName,String zipFileName,String path,String content) {
        createFile(fileName,path,content);
        File sourceFile = new File(path,fileName);
        try (FileInputStream sourceFileInputStream = new FileInputStream(sourceFile);
             FileOutputStream fzOutStream= new FileOutputStream(path+"/"+zipFileName);
             ZipOutputStream zipOut =new ZipOutputStream(fzOutStream)){

            ZipEntry zipEntry = new ZipEntry(sourceFile.getName());
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while((length = sourceFileInputStream.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            sourceFile.delete();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文件
     * @param targetFile 目标文件
     */
    public static void deleteFile(File targetFile){
        targetFile.delete();
    }

    private static void assertFilePath(String path){
        File directory = new File(path);
        directory.mkdirs();
    }
    private static void logStart(String fileName,String path){
        StringBuilder logStartStr = new StringBuilder();
        logStartStr.append("\n").append("传入文件名:").append(fileName).append("\n");
        logStartStr.append("传入路径:").append(path).append("\n");
        log.info(logStartStr.toString());
    }
    private static void logEnd(String fileName,String path){
        StringBuilder logEndStr = new StringBuilder();
        logEndStr.append("\n").append("创建文件:").append(fileName).append("\n");
        logEndStr.append("文件路径:").append(path).append("\n");
        log.info(logEndStr.toString());
    }

    /**
     * base64 转文件
     * @param base64Str
     * @return
     */
    public static MultipartFile base64MutipartFile(String base64Str) {
        try {
            String[] baseStr = base64Str.split(",");
            boolean isLegal = baseStr[0].matches("^data:.+/.+;base64");
            if (!isLegal){
                return null;
            }
            byte[] b = Base64.getDecoder().decode(baseStr[1]);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            return new Base64DecodedMultipartFile(b, baseStr[0]);
        } catch (Exception e) {
            log.debug("base64转文件失败");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 读取文件内容为二进制数组
     * @param filePath 文件路径
     * @return byte[] 二进制数组
     */
    public static byte[] read2ByteArray(String filePath) throws IOException {
        InputStream in = new FileInputStream(filePath);
        byte[] data = inputStream2ByteArray(in);
        in.close();
        return data;
    }

    /**
     * 流转二进制数组
     * @param in inputStream
     * @return byte[] 二进制数组
     */
    private static byte[] inputStream2ByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }

}
