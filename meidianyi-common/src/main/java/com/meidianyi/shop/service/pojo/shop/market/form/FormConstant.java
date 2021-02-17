package com.meidianyi.shop.service.pojo.shop.market.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liufei
 * @date 2019/8/9
 * 表单元素常量
 */
@Data
public class FormConstant {
    /**
     * 姓名
     */
    public static final String M_INPUT_NAME = "m_input_name";
    /**
     * 手机号
     */
    public static final String M_INPUT_MOBILE = "m_input_mobile";
    /**
     * 地址
     */
    public static final String M_ADDRESS = "m_address";
    /**
     * 邮箱
     */
    public static final String M_INPUT_EMAIL = "m_input_email";
    /**
     * 输入框
     */
    public static final String M_INPUT_TEXT = "m_input_text";
    /**
     * 性别
     */
    public static final String M_SEX = "m_sex";
    /**
     * 选项
     */
    public static final String M_CHOOSE = "m_choose";
    /**
     * 下拉
     */
    public static final String M_SLIDE = "m_slide";
    /**
     * 日期
     */
    public static final String M_DATES = "m_dates";
    /**
     * 图片
     */
    public static final String M_IMGS = "m_imgs";
    /**
     * 视频
     */
    public static final String M_UPLOAD_VIDEO = "m_upload_video";

    /**
     * 以下模块为表单展示模块，不会含有用户反馈信息
     * m_scroll_image轮播图
     * m_rich_text富文本
     * m_image_small图片广告
     * m_dashed_line辅助线
     * m_text文本模块
     * m_blank辅助空白
     * m_phone电话模块
     * m_official_accounts公众号
     */

    public static Map<String, String> ALL = new HashMap<String, String>(){
        private static final long serialVersionUID = 7311691185434226748L;

        {
            put(M_INPUT_NAME, M_INPUT_NAME);
            put(M_INPUT_MOBILE, M_INPUT_MOBILE);
            put(M_ADDRESS, M_ADDRESS);
            put(M_INPUT_EMAIL, M_INPUT_EMAIL);
            put(M_INPUT_TEXT, M_INPUT_TEXT);
            put(M_SEX, M_SEX);
            put(M_CHOOSE, M_CHOOSE);
            put(M_SLIDE, M_SLIDE);
            put(M_DATES, M_DATES);
            put(M_IMGS, M_IMGS);
            put(M_UPLOAD_VIDEO, M_UPLOAD_VIDEO);
        }
    };
    public static Map<String, String> SPECIAL = new HashMap<String, String>(){
		private static final long serialVersionUID = -888856750061624135L;
		{
            put(M_SEX, M_SEX);
            put(M_CHOOSE, M_CHOOSE);
            put(M_SLIDE, M_SLIDE);
        }
    };
    public static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * form_page表中page_content字符串中
     * 模块名为m_choose,m_sex,m_slide三个时，module_value的值会可能存在多个
     * 存储格式统一为："select":{"key":"value",...}
     * 定义此常量用来存取该列表值
     */
    public static final String SELECT = "selects";
    /**
     * 表单模块名常量
     */
    public static final String MODULE_NAME = "module_name";

    /**
     * 新建表单配置的参与送优惠卷列表
     */
    public static final String SEND_COUPON_LIST = "send_coupon_list";
    /**
     * 新建表单配置的参与送积分值
     */
//    public static final String SEND_SCORE_NUM = "send_score_num";
    /**
     * 优惠券活动id
     */
    public static final String COUPON_ID = "coupon_id";

    public static final String FORM_CHAR = "Form";
    public static final String FORM_TEXT = "表单";

    /**
     * 表单配置项元素
     */
    public static final String BG_IMG = "bg_img";
    public static final String GET_TIMES ="get_times";
    public static final String POST_TIMES ="post_times";
    public static final String DAY_TIMES ="day_times";
    public static final String SEND_SCORE ="send_score";
    public static final String SEND_SCORE_NUMBER ="send_score_number";
    public static final String SEND_COUPON ="send_coupon";
    public static final String TOTAL_TIMES ="total_times";
    public static final String PAGE_NAME = "page_name";
    public static final String FORM_DEFAULT_BG_IMG = "image/admin/pictorial/form_bg.png";

    @Getter
    @Setter
    public static class  ModuleUploadVideo{
        @JsonProperty("video_src")
        private String videoSrc;
        @JsonProperty("video_img_src")
        private String videoImgSrc;
        @JsonProperty("video_id")
        private Integer videoId;
    }

}

