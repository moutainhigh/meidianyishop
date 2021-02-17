package com.meidianyi.shop.common.foundation.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 赵晓东
 * @description 手写汉字识别 对接科大讯飞
 * @create 2020-09-29 15:49
 * 手写文字识别WebAPI接口调用示例接口文档(必看):https://doc.xfyun.cn/rest_api/%E6%89%8B%E5%86%99%E6%96%87%E5%AD%97%E8%AF%86%E5%88%AB.html
 * 图片属性：jpg/png/bmp,最短边至少15px，最长边最大4096px,编码后大小不超过4M,识别文字语种：中英文
 * webapi OCR服务参考帖子(必看)：http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=39111&highlight=OCR
 * (Very Important)创建完webapi应用添加服务之后一定要设置ip白名单，找到控制台--我的应用--设置ip白名单，如何设置参考：http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=41891
 * 错误码链接：https://www.xfyun.cn/document/error-code (code返回错误码时必看)
 **/

public class OcrUtils {

    /**
     * 手写文字识别webapi接口地址
     **/
    private static final String WEB_OCR_URL = "http://webapi.xfyun.cn/v1/service/v1/ocr/handwriting";
    /**
     * 应用APPID(必须为webapi类型应用,并开通手写文字识别服务,参考帖子如何创建一个webapi应用：http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=36481)
     **/
    private static final String APP_ID = "5f72d45d";
    /**
     * 接口密钥(webapi类型应用开通手写文字识别后，控制台--我的应用---手写文字识别---相应服务的apikey)
     **/
    private static final String API_KEY = "79737d82020a9024d26d833d04337548";
    /**
     * 默认语言
     */
    private static final String LANGUAGE = "en";
    /**
     * 定位信息
     */
    private static final String LOCATION = "false";
    /**
     * 编码模式
     */
    private static final String CHAR_SET_NAME = "UTF-8";

    /**
     * 组装请求头
     * @param language 语言编码
     * @param location 定位信息
     * @return Map<String, String> 请求头
     * @throws UnsupportedEncodingException The Character Encoding is not supported.
     * @throws ParseException Signals that an error has been reached unexpectedly while parsing.
     */
    private Map<String, String> constructHeader(String language, String location) throws UnsupportedEncodingException, ParseException {
        // 系统当前时间戳
        String curTime = System.currentTimeMillis() / 1000L + "";
        // 业务参数
        String param = "{\"language\":\""+language+"\""+",\"location\":\"" + location + "\"}";
        String xParam = new String(Base64.encodeBase64(param.getBytes(StandardCharsets.UTF_8)));
        // 生成令牌
        String xCheckSum = DigestUtils.md5Hex(API_KEY + curTime + xParam);
        // 组装请求头
        Map<String, String> header = new HashMap<String, String>(10);
        header.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        header.put("X-Param", xParam);
        header.put("X-CurTime", curTime);
        header.put("X-CheckSum", xCheckSum);
        header.put("X-Appid", APP_ID);
        return header;
    }

    public String ocr(String failPath) throws IOException,ParseException {
        Map<String, String> header = constructHeader(LANGUAGE, LOCATION);
        // 读取图像文件，转二进制数组，然后Base64编码
        byte[] imageByteArray = FileUtil.read2ByteArray(failPath);
        String imageBase64 = new String(Base64.encodeBase64(imageByteArray), CHAR_SET_NAME);
        String bodyParam = "image=" + imageBase64;
        return HttpUtil.doPost(WEB_OCR_URL, header, bodyParam);
    }
}
