package com.meidianyi.shop.service.foundation.upyun;

import com.UpYun;
import com.upyun.UpException;
import com.meidianyi.shop.config.UpYunConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * 又拍云上传工具
 * @author 孔德成
 * @date 2020/7/13 16:02
 */
@Service
public class UpYunManager {
    @Autowired
    protected UpYunConfig upYunConfig;

    /**
     * 获取又拍云客户端
     * @return
     */
    public UpYun getUpYunClient() {
        return new UpYun(upYunConfig.getServer(), upYunConfig.getName(), upYunConfig.getPassword());
    }

    /**
     * 上传到又拍云
     *
     * @param upYunPath 又拍云路径
     * @param localFile 本地文件
     * @return
     * @throws IOException
     * @throws Exception
     */
    public  boolean uploadToUpYun(String upYunPath, File localFile) throws IOException, Exception {
        return this.getUpYunClient().writeFile(upYunPath, localFile);
    }


    /**
     * 上传图片到又拍云
     * @param  upYunPath
     * @param  inStream
     * @return
     * @throws IOException
     * @throws UpException
     */
    public  boolean uploadToUpYunBySteam(String upYunPath, InputStream inStream) throws IOException, UpException {
        return this.getUpYunClient().writeFile(upYunPath, inStream, true, null);
    }

    /**
     * 根据byte[] 上传图片到又拍云
     * @return
     * @throws Exception
     */
    public  boolean uploadToUpYunByByte(String filePath, byte[] datas) throws Exception {
        return this.getUpYunClient().writeFile(filePath,datas,true);
    }

    /**
     * 获取文件信息
     * @param filePath
     * @return null 是不存在
     */
    public Map<String, String> getFileInfo(String filePath) throws IOException, UpException {
        return this.getUpYunClient().getFileInfo(filePath);
    }

}
