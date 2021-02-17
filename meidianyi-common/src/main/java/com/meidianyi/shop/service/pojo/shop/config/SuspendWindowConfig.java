package com.meidianyi.shop.service.pojo.shop.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: 王兵兵
 * @create: 2020-05-08 17:24
 **/
@Getter
@Setter
public class SuspendWindowConfig {
    /**
     * 悬浮窗是否生效
     * 0：关，1：开
     */
    @NonNull
    @JsonProperty(value = "page_flag")
    private Byte pageFlag = 0;

    /**
     * 应用页面
     * -2个人中心，-3商品详情页，1及以上为自定义页面
     */
    @NonNull
    @JsonProperty(value = "page_ids")
    private List<Integer> pageIds = new ArrayList<>();

    /**
     * 悬浮方式
     * 1：固定位置显示，2：上滑消失下滑显示
     */
    @NonNull
    @JsonProperty(value = "suspend_pattern")
    private Byte suspendPattern = 1;

    /**
     * 主图标是否开启
     * 0：关，1：开
     */
    @NonNull
    @JsonProperty(value = "main_flag")
    private Byte mainFlag = 0;

    /**
     * 主图标展开前图片
     */
    @NonNull
    @JsonProperty(value = "main_before")
    private String mainBefore = "";

    /**
     * 主图标展开后图片
     */
    @NonNull
    @JsonProperty(value = "main_after")
    private String mainAfter = "";

    /**
     * 子图标列表
     */
    @NonNull
    @JsonProperty(value = "children_arr")
    private List<ChildIcon> childrenArr = new ArrayList<>();


    @Getter
    @Setter
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", defaultImpl = ChildIcon.class, visible = true)
    @JsonSubTypes(value = {
        @JsonSubTypes.Type(value = ChildIconPhone.class, name = "2"),
        @JsonSubTypes.Type(value = ChildIconCustom.class, name = "0"),
        @JsonSubTypes.Type(value = ChildIconCustom.class, name = "4"),
    })
    public static class ChildIcon {
        /***
         * 区别子图标类型
         * 0自定义图标，1客服，2电话，3分享，4购物车，5返回顶部
         */
        private Byte type;

        /**
         * 子图标开关
         */
        @JsonProperty(value = "child_flag")
        private Byte childFlag = 0;

        /**
         * 子图标图片
         */
        @JsonProperty(value = "img")
        private String img = "";

        /**
         * 是否独立于主图展示
         * 0否 ，1是
         */
        @JsonProperty(value = "own_flag")
        private Byte ownFlag = 0;

        /**
         * 子图标名称
         */
        @JsonProperty(value = "name")
        private String name = "";
    }

    @Getter
    @Setter
    public static class ChildIconPhone extends ChildIcon {
        /**
         * 电话图标专用
         */
        @JsonProperty(value = "phone")
        private String phone = "";
    }

    @Getter
    @Setter
    public static class ChildIconCustom extends ChildIcon {
        /**
         * 自定义跳转链接
         */
        @JsonProperty(value = "custom_link")
        private String customLink = "";
    }
}
