package com.meidianyi.shop.service.pojo.shop.market.form.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.util.List;

/**
 * 表单设置pojo
 * @author 孔德成
 * @date 2020/4/28
 */
@Getter
@Setter
@ToString
public class FormCfgBo {
    /**标题*/
    @JsonProperty("page_name")
    private String pageName;

    /**有效期*/
    @JsonProperty("is_forever_valid")
    private Byte isForeverValid;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("start_time")
    private Timestamp startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("end_time")
    private Timestamp endTime;
    /**底部导航*/
    @JsonProperty("has_bottom")
    private Byte hasBottom;
    /**提交次数限制 1不限制 */
    @JsonProperty("post_times")
    private Byte postTimes;
    /**每天提交值*/
    @JsonProperty("post_times")
    private Integer dayTimes;
    /**累计提交值*/
    @JsonProperty("total_times")
    private Integer totalTimes;
    /**总反馈数量限制*/
    @JsonProperty("get_times")
    private Integer getTimes;
    /**提交按钮文字*/
    @JsonProperty("notice_name")
    private String noticeName;
    /**提交按钮文字颜色*/
    @JsonProperty("font_color")
    private String fontColor;
    /**提交按钮背景颜色*/
    @JsonProperty("bg_color")
    private String bgColor;
    /**表单海报背景图片*/
    @JsonProperty("bg_img")
    private String bgImg;
    /**自定义跳转checked*/
    @JsonProperty("set_own_link")
    private Integer setOwnLink;
    /**自定义按钮名称*/
    @JsonProperty("custom_btn_name")
    private String customBtnName;
    /**跳转链接*/
    @JsonProperty("custom_link_path")
    private String customLinkPath;
    /**跳转链接名称*/
    @JsonProperty("custom_link_name")
    private String customLinkName;
    /**参与送优惠卷checkbox*/
    @JsonProperty("send_coupon")
    private Integer sendCoupon;
    /**选择的优惠卷数据列表*/
    @JsonProperty("send_coupon_list")
    private List<SendCoupon> sendCouponList;
    /**参与送积分选中*/
    @JsonProperty("send_score")
    private Byte sendScore;
    /**输入的送积分*/
    @JsonProperty("send_score_number")
    private Integer sendScoreNumber;
    /**授权手机号*/
    @JsonProperty("authorized_name")
    private Integer authorizedName;
    /**授权用户信息*/
    @JsonProperty("authorized_mobile")
    private Integer authorizedMobile;

}
