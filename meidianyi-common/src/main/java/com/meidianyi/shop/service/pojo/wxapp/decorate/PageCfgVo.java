package com.meidianyi.shop.service.pojo.wxapp.decorate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


/**
 * @author: 王兵兵
 * @create: 2020-01-02 18:29
 **/
@Data
public class PageCfgVo {
    @JsonProperty(value = "is_ok")
    Byte isOk=1;
    @JsonProperty(value = "cat_id")
    Integer catId=0;
    @JsonProperty(value = "page_name")
    String pageName="";
    @JsonProperty(value = "bg_types")
    Byte bgTypes=0;
    @JsonProperty(value = "has_bottom")
    Byte hasBottom=0;
    @JsonProperty(value = "show_margin")
    Byte showMargin=0;
    @JsonProperty(value = "margin_val")
    Integer marginVal=0;
    @JsonProperty(value = "page_bg_color")
    String pageBgColor="#ffffff";
    @JsonProperty(value = "page_bg_image")
    String pageBgImage="";
    @JsonProperty(value = "last_cur_idx")
    Integer lastCurIdx;
    @JsonProperty(value = "pictorial")
    PageCfgPictorial pictorial;


    /**
     * 分享海报相关配置
     */
    @Getter
    @Setter
    public static class PageCfgPictorial{
        /**
         *是否添加分享海报
         */
        @JsonProperty(value = "is_add")
        Byte isAdd=0;

        /**
         *是所有用户可见还是仅分销员可见
         */
        @JsonProperty(value = "user_visibility")
        Byte userVisibility=0;

        /**
         *按钮名称
         */
        @JsonProperty(value = "share_btn_name")
        String shareBtnName="";

        /**
         *分享语
         */
        @JsonProperty(value = "share_desc")
        String shareDesc="";

        /**
         *分享图片路径
         */
        @JsonProperty(value = "share_img_path")
        String shareImgPath="";

        /**
         *按钮名称长度
         */
        @JsonProperty(value = "name_length")
        Integer nameLength=0;
    }
}
