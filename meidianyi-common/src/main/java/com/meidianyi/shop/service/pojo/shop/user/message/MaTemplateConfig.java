package com.meidianyi.shop.service.pojo.shop.user.message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.meidianyi.shop.common.foundation.util.RegexUtil;

/**
 * 小程序消息模版类
 * @author 卢光耀
 * @date 2019-08-23 10:06
 *
*/
public enum MaTemplateConfig {
    /**
     * 消息推送
     */
    ACTIVITY_CONFIG(
        "AT0654",
        "申请进度通知",
        "业务名称{{keyword1.DATA}}业务状态{{keyword2.DATA}}",
        new Integer[]{1, 2},
                    1,
        new String[][]{{"keyword1","#173177"},{"keyword2","#173177"}});


    private String id;
    private String title;
    private String content;
    private Integer[] keywordIds;
    private Integer emphasisKeywordSn;
    private Map<String,String> colors;

    @JsonCreator
    public static MaTemplateConfig getConfig(String id){
        for(MaTemplateConfig item : values()){
            if(item.getId().equals(id) ){
                return item;
            }
        }
        return null;
    }


    MaTemplateConfig(String id,String title,String content,Integer[] keywordIds,
                     Integer emphasisKeywordSn,String[][] colors){
        final Map<String, String> map = new HashMap<>((int) (colors.length * 1.5));
        this.id = id;
        this.title = title;
        this.content = content;
        this.keywordIds = keywordIds;
        this.emphasisKeywordSn = emphasisKeywordSn;
        List<String> list = RegexUtil.getSubStrList("{{",".",content);
        for(String s: list){
            map.put(s,"#173177");
        }
        for( int i =0, len = colors.length; i<len;i++ ){
            String[] object = colors[i];
            map.put(object[0],object[1]);
        }
        this.colors = map;
    }
    MaTemplateConfig(String id,String title,String content,Integer[] keywordIds,
                     Integer emphasisKeywordSn){
        List<String> list = RegexUtil.getSubStrList("{{",".",content);
        final Map<String, String> map = new HashMap<>((int) (list.size() * 1.5));
        this.id = id;
        this.title = title;
        this.content = content;
        this.keywordIds = keywordIds;
        this.emphasisKeywordSn = emphasisKeywordSn;
        for(String s: list){
            map.put(s,"#173177");
        }
        this.colors = map;
    }
    @JsonValue
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Integer[] getKeywordIds() {
        return keywordIds;
    }

    public Integer getEmphasisKeywordSn() {
        return emphasisKeywordSn;
    }

    public Map<String,String> getColors() {
        return colors;
    }


}
