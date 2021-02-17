package com.meidianyi.shop.common.foundation.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lixinguo
 */
public class RegexUtil {

    private static final List<String> STR = Arrays.asList(
        "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|");


    private static final String PATTERN = "(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]";

    public static List<String> getSubStrList(String first, String lastStr, String content) {
        List<String> result = new ArrayList<>();
        for (String key : STR) {
            if (first.contains(key)) {
                first = first.replace(key, "\\" + key);
            }
            if (lastStr.contains(key)) {
                lastStr = lastStr.replace(key, "\\" + key);
            }
        }
        StringBuilder pStr = new StringBuilder("(?<=")
            .append(first)
            .append(").*?(?=")
            .append(lastStr)
            .append(")");
        Pattern p = Pattern.compile(pStr.toString());
        Matcher m = p.matcher(content);
        while (m.find()) {
            result.add(m.group());
        }
        return result;
    }

    public static boolean checkUrl(String url) {
        return Pattern.matches(PATTERN, url);
    }

    /**
     * 清除html内多余内容，style,script,lineFeed,document
     * @param html
     * @return
     */
    public static String cleanBodyContent(String html) {
        String retStr = null;
        if (StringUtils.isNotBlank(html) && html.contains("<body>")) {
            String bodyReg = "<body>([\\s\\S]*)</body>";
            Pattern compile = Pattern.compile(bodyReg);
            Matcher matcher = compile.matcher(html);
            while (matcher.find()) {
                retStr = matcher.group(1);
            }
        }else {
            retStr = html;
        }

        if (retStr != null) {
            retStr = removeScript(retStr);
            retStr = removeStyle(retStr);
            retStr = removeDocument(retStr);
            retStr = removeLineFeed(retStr);
            retStr = removeMediaTag(retStr);
        }
        return retStr;
    }

    /**
     * 移除html 内脚本
     *
     * @param html html内容
     * @return 移除后的内容
     */
    private static String removeScript(String html) {
        final String scriptReg = "(<script[\\s\\S]*?/>)|(?:<script[^/>]*?>[\\s\\S]*?<(\\s)*?/script(\\s\\S)*?>)";
        return html.replaceAll(scriptReg, "");
    }

    /**
     * 移除html 内style
     *
     * @param html html内容
     * @return 移除后的内容
     */
    private static String removeStyle(String html) {
        final String styleReg = "<style[^>]*>[\\s\\S]*</style>";
        return html.replaceAll(styleReg, "");
    }

    /**
     * 移除html 内换行符
     *
     * @param html html内容
     * @return 移除后的内容
     */
    private static String removeLineFeed(String html) {
        final String lineFeed = "[\\r\\n]*";
        return html.replaceAll(lineFeed, "");
    }

    /**
     * 移除html 内document声明
     *
     * @param html html内容
     * @return 移除后的内容
     */
    private static String removeDocument(String html) {
        final String documentReg = "<!DOCTYPE[^>]*>";
        return html.replaceAll(documentReg,"");
    }

    /**
     * 移除html 内iframe声明
     *
     * @param html html内容
     * @return 移除后的内容
     */
    private static String removeIframe(String html) {
        final String iframeReg = "<iframe[^>]*>[\\s\\S]*</iframe>";
        return html.replaceAll(iframeReg,"");
    }
    private static String removeAtag(String html) {
        final String aTagReg = "<a[^>]*>[\\s\\S]*</a>";
        return html.replaceAll(aTagReg,"");
    }
    private static String removeSvgTag(String html) {
        final String aTagReg = "<svg[^>]*>[\\s\\S]*</svg>";
        return html.replaceAll(aTagReg,"");
    }
    private static String removeCanvasTag(String html) {
        final String aTagReg = "<canvas[^>]*>[\\s\\S]*</canvas>";
        return html.replaceAll(aTagReg,"");
    }
    private static String removeMediaTag(String html) {
        return  html.replaceAll("<audio[^>]*>[\\s\\S]*</audio>", "")
            .replaceAll("<video[^>]*>[\\s\\S]*</video>","")
            .replaceAll("<object[^>]*>[\\s\\S]*</object>","")
            .replaceAll("<embed[^>]*?/>","");
    }

    private static ThreadLocal<Pattern> imgPatternThreadLocal = new ThreadLocal<>();

    public static Pattern getImgTagPattern(){
        Pattern pattern = imgPatternThreadLocal.get();
        if (pattern == null) {
            String imgReg = "<img[\\s\\S]*?src=\"([\\s\\S]*?)\"[\\s\\S]*?/?>";
             pattern = Pattern.compile(imgReg);
            imgPatternThreadLocal.set(pattern);
        }
        return pattern;
    }

    private static ThreadLocal<Pattern> backgroundPatternThreadLocal = new ThreadLocal<>();

    public static Pattern getBgUrlPattern() {
        Pattern pattern = backgroundPatternThreadLocal.get();
        if (pattern == null) {
            String bgUrlReg = "(?:background-image:[\\s\\S]*?url\\(([\\s\\S]*?)\\);)|(?:background:[\\s\\S]*?url\\(([\\s\\S]*?)\\))";
            pattern = Pattern.compile(bgUrlReg);
            backgroundPatternThreadLocal.set(pattern);
        }
        return pattern;
    }

    /**
     * 去掉域名，取资源路径
     *
     * @param url
     * @return
     */
    public static String getUri(String url) {
        String regx = "(?://|https://|http://)" + "(?:(?:\\w+\\.){2,3}|[a-zA-Z0-9]+)(?:\\w+)" + "(?::[0-9]+)?" + "([^?]*)";

        Pattern p = Pattern.compile(regx);
        Matcher matcher = p.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return url;
    }

}
