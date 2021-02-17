package com.meidianyi.shop.common.foundation.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.jooq.tools.json.JSONObject;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.util.Map;

/**
 * @author 孔德成
 * @date 2019/11/18 10:25
 */
@Slf4j
public class HttpsUtils {
    private static final String CHARSET = "UTF-8";
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static PoolingHttpClientConnectionManager connMgr;
    private static RequestConfig requestConfig;
    private static final int MAX_TIMEOUT = 7000;
    private static final String HTTPS = "https";
    private static final String THE_QUESTION_MARK = "?";

    static {
        // 设置连接池
        connMgr = new PoolingHttpClientConnectionManager();
        // 设置连接池大小
        connMgr.setMaxTotal(30);
        connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());
        // 1秒不活动后验证连接
        connMgr.setValidateAfterInactivity(1000);
        RequestConfig.Builder configBuilder = RequestConfig.custom();
        // 设置连接超时
        configBuilder.setConnectTimeout(MAX_TIMEOUT);
        // 设置读取超时
        configBuilder.setSocketTimeout(MAX_TIMEOUT);
        // 设置从连接池获取连接实例的超时
        configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);

        requestConfig = configBuilder.build();
    }
    /**
     * GET 请求
     *
     * @param url     请求地址
     * @param params  请求参数
     * @param headers 请求头
     * @param isHttps 是否 HTTPS请求
     * @return
     * @throws IOException
     */
    public static String get(String url, Map<String, Object> params, Map<String, String> headers, boolean isHttps) {
        return http(GET, url, params, headers, isHttps);
    }
    /**
     * GET 请求
     *
     * @param url     请求地址
     * @param params  请求参数
     * @param isHttps 是否 HTTPS请求
     * @return
     * @throws IOException
     */
    public static String get(String url, Map<String, Object> params, boolean isHttps) {
        return http(GET, url, params, null, isHttps);
    }

    /**
     * GET 请求 下载
     *
     * @param url     请求地址
     * @param params  请求参数
     * @param isHttps 是否 HTTPS请求
     * @return
     * @throws IOException
     */
    public static void get(String url, Map<String, Object> params, boolean isHttps,String path,String fileName) {
        getFile(GET, url, params, null, isHttps,path,fileName);
    }

    /**
     * POST 请求
     *
     * @param url     请求地址
     * @param params  请求参数
     * @param headers 请求头
     * @param isHttps 是否 HTTPS请求
     * @return
     * @throws IOException
     */
    public static String post(String url, Map<String, Object> params, Map<String, String> headers, boolean isHttps) {
        return http(POST, url, params, headers, isHttps);
    }
    /**
     * POST 请求
     *
     * @param url     请求地址
     * @param params  请求参数
     * @param isHttps 是否 HTTPS请求
     * @return
     * @throws IOException
     */
    public static String post(String url, Map<String, Object> params, boolean isHttps) {
        return http(POST, url, params, null, isHttps);
    }

    /**
     * 下载外链图片内容使用
     * @param url 外链地址
     * @param headers 可能需要的参数
     * @return 图片字节码
     */
    public static byte[] getByteArray(String url, Map<String, String> headers) throws IOException {
        HttpClient httpClient;
        log.info("下载图片：请求地址 = {},请求参数 = {},请求协议是否是https = {}",url,JSONObject.toJSONString(headers),url.contains("https"));
        if (url.contains(HTTPS)) {
            httpClient = createSslClientDefault();
        } else {
            httpClient =  HttpClients.createDefault();
        }
        HttpGet get = new HttpGet(url);
        if (headers != null && headers.size() > 0) {
            headers.forEach(get::setHeader);
        }

        InputStream inputStream = null;
        try {
            HttpResponse response = httpClient.execute(get);
            byte[] bytes = EntityUtils.toByteArray(response.getEntity());
            inputStream = response.getEntity().getContent();
            return bytes;
        } catch (IOException e) {
            log.debug("下载文件：请求{}异常 msg:{}", url, e.getMessage());
            throw e;
        }finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static String http(String method, String url, Map<String, Object> params,
                               Map<String, String> headers, boolean isHttps) {
        long start = System.currentTimeMillis();
        try {
            log.info("请求方式 = {},请求地址 = {},请求参数 = {},请求头信息 = {},https请求 = {}", method, url, JSONObject.toJSONString(params), JSONObject.toJSONString(headers), isHttps);
            HttpClient httpClient;
            if (isHttps) {
                httpClient = createSslClientDefault();
            } else {
                httpClient = HttpClients.createDefault();
            }
            if (POST.equalsIgnoreCase(method)) {
                HttpPost post = new HttpPost(url);
                if (headers != null) {
                    headers.forEach(post::setHeader);
                }
                if (params != null) {
                    post.setEntity(new StringEntity(JSONObject.toJSONString(params), ContentType.APPLICATION_JSON));
                }
                HttpResponse response = httpClient.execute(post);
                return parseRes(response, CHARSET);
            }
            if (GET.equalsIgnoreCase(method)) {
                if (params != null && params.size() > 0) {
                    if (url.contains(THE_QUESTION_MARK)) {
                        url += "&" + buildUrlParams(params);
                    } else {
                        url += "?" + buildUrlParams(params);
                    }
                }
                HttpGet get = new HttpGet(url);
                if (headers != null && headers.size() > 0) {
                    headers.forEach(get::setHeader);
                }
                HttpResponse response = httpClient.execute(get);
                return parseRes(response, CHARSET);
            }
            throw new RuntimeException("Unsupported request method");
        } catch (IOException e) {
            throw new RuntimeException("请求异常：" + url);
        } finally {
            log.info("请求耗时：{}", System.currentTimeMillis() - start + " ms");
        }
    }

    private static void getFile(String method, String url, Map<String, Object> params, Map<String, String> headers, boolean isHttps,String path,String fileName){
        try {
            HttpClient httpClient;
            if (isHttps) {
                httpClient = createSslClientDefault();
            } else {
                httpClient = HttpClients.createDefault();
            }
            if (POST.equalsIgnoreCase(method)) {
                HttpPost post = new HttpPost(url);
                if (headers != null) {
                    headers.forEach(post::setHeader);
                }
                if (params != null) {
                    post.setEntity(new StringEntity(JSONObject.toJSONString(params), ContentType.APPLICATION_JSON));
                }
                HttpResponse response = httpClient.execute(post);
                getFile(response,path+fileName);
                return;
            }
            if (GET.equalsIgnoreCase(method)) {
                if (params != null && params.size() > 0) {
                    if (url.contains(THE_QUESTION_MARK)) {
                        url += "&" + buildUrlParams(params);
                    } else {
                        url += "?" + buildUrlParams(params);
                    }
                }
                HttpGet get = new HttpGet(url);
                if (headers != null && headers.size() > 0) {
                    headers.forEach(get::setHeader);
                }
                HttpResponse response = httpClient.execute(get);
                getFile(response,path+fileName);
                return;
            }
            throw new RuntimeException("Unsupported request method");
        } catch (IOException e) {
            log.error("报错",e);
            throw new RuntimeException("请求异常：" + url);
        }
    }

    private static void getFile(HttpResponse response,String destFileName) throws IOException {
        HttpEntity entity = response.getEntity();

        InputStream in = entity.getContent();

        File file =new File(destFileName);

        try{

            FileOutputStream fout =new FileOutputStream(file);

            int l = -1;
            byte[] tmp =new byte[1024];

            while((l = in.read(tmp)) != -1) {
                fout.write(tmp,0, l);
            }
            fout.flush();
            fout.close();
        }finally{
            // 关闭低层流。
            in.close();
        }
    }

    /**
     * 解析响应结果
     *
     * @param response
     * @param charSet
     * @return
     * @throws IOException
     */
    private static String parseRes(HttpResponse response, String charSet) throws IOException {
        return EntityUtils.toString(response.getEntity(), charSet);
    }

    /**
     * 创建https
     *
     * @return
     */
    private static CloseableHttpClient createSslClientDefault() {
        try {
            HttpClientBuilder custom = HttpClients.custom();
            custom.setSSLSocketFactory(createSslConnSocketFactory());
            custom.setConnectionManager(connMgr);
            custom.setDefaultRequestConfig(requestConfig);
            return custom.build();
        }catch (Exception e){
            e.printStackTrace();
        }
        return HttpClients.createDefault();
    }

    /**
     * 创建SSL安全连接
     *
     * @return
     */
    private static SSLConnectionSocketFactory createSslConnSocketFactory() throws RuntimeException {
        SSLConnectionSocketFactory sslsf = null;
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (TrustStrategy) (chain, authType) -> true).build();
            sslsf = new SSLConnectionSocketFactory(sslContext, (arg0, arg1) -> true);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e.getMessage());
        }
        return sslsf;
    }

    /**
     * map to urlEncode 参数
     *
     * @param params
     * @return
     * @throws IOException
     */
    private static String buildUrlParams(Map<String, Object> params) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            sb.append(URLEncoder.encode(entry.getKey(), CHARSET));
            sb.append("=");
            sb.append(URLEncoder.encode(entry.getValue().toString(), CHARSET));
            sb.append("&");
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }
}
