package com.meidianyi.shop.service.pojo.shop.market.form.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * 表单模块
 * @author 孔德成
 * @date 2020/4/29
 */
@Getter
@Setter
@ToString
public class FormModulesBo {

    /**
     * 标号
     */
    @JsonProperty("cur_idx")
    private String curIdx;
    /**
     * 模块名
     */
    @JsonProperty("module_name")
    private String moduleName;
    /**
     * 是否点击确认
     */
    @JsonProperty("ok_ajax")
    private Byte okAjax;
    /**
     * 条件验证
     */
    private Byte confirm;
    /**
     * 标题文字input值
     */
    @JsonProperty("form_title")
    private String formTitle;
    /**
     * 提示语input值
     */
    private String placeholder;
    /**
     * 展现形式radio
     */
    @JsonProperty("image_type")
    private Byte imageType;
    /**
     *展现形式
     */
    @JsonProperty("show_types")
    private Byte showTypes;

    @JsonProperty("show_type")
    private Byte showType;
    /**
     * 展现形式
     */
    @JsonProperty("with_detail")
    private Integer withDetail;
    /**
     * 图标图片地址
     */
    @JsonProperty("name_url")
    private String nameUrl;
    /**
     * 校验最小值
     */
    @JsonProperty("least_number")
    private Integer leastNumber;
    /**
     * 校验最大值
     */
    @JsonProperty("most_number")
    private Integer mostNumber;
    /**
     * 图片校验 最多上传六个
     */
    @JsonProperty("max_number")
    private Integer maxNumber;
    /**
     * 宽度
     */
    @JsonProperty("max_number")
    private Integer widthSize;
    /**
     * 图片 大小校验
     */
    @JsonProperty("size_types")
    private Integer sizeTypes;
    /**
     * 高度
     */
    @JsonProperty("height_size")
    private Integer heightSize;
    /**
     * 轮播图 图片地址
     */
    @JsonProperty("img_items")
    private List<FormImgItem> imgItems;
    /**
     * 预览原图
     */
    @JsonProperty("is_preview")
    private Byte isPreview;
    /**
     * 富文本内容
     */
    @JsonProperty("rich_text")
    private String richText;
    /**
     * 广告 文本 标题
     */
    private String title;
    /**
     * 广告 连接
     */
    @JsonProperty("title_link")
    private String titleLink;
    /**
     * 广告 图片
     */
    @JsonProperty("img_url")
    private String imgUrl;

    @JsonProperty("fonts_size")
    private Integer fontsSize;

    @JsonProperty("fonts_color")
    private String fontsColor;

    @JsonProperty("bgs_color")
    private String bgsColor;

    @JsonProperty("show_pos")
    private String showPos;

    @JsonProperty("blank_height")
    private String blankHeight;

    @JsonProperty("sps_icon")
    private String spsIcon;

    @JsonProperty("align_type")
    private Byte alignType;

    private String color;

    @JsonProperty("background_color")
    private String backgroundColor;

    /**
     * 选择项
     */
    private Map<String,String> selects;
}
