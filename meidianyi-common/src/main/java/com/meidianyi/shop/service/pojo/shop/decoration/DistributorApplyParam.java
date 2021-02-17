package com.meidianyi.shop.service.pojo.shop.decoration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 申请成为分销员入参
 * @author 常乐
 * 2019年10月16日
 */
@Data
public class DistributorApplyParam {
    /**
     * 用户ID
     */
	private Integer userId;
    /**
     * 审核校验
     */
	private InfoField activationFields;
    /**
     * 审核字段
     */
	private String configFields;

    @Data
    public static class InfoField{
        /**
         * 真实姓名
         */
        @JsonProperty(value = "real_name")
        public String realName;
        /**
         * 手机号
         */
        @JsonProperty(value = "mobile")
        public String mobile;
        /**
         * 身份证号
         */
        @JsonProperty(value = "cid")
        public String cid;
        /**
         * 地址
         */
        @JsonProperty(value = "address")
        public String address;
        /**
         *性别
         */
        @JsonProperty(value = "sex")
        public String sex;
        /**
         *出生日
         */
        @JsonProperty(value = "birthday_day")
        public String birthdayDay;
        /**
         * 出生月
         */
        @JsonProperty(value = "birthday_month")
        public String birthdayMonth;
        /**
         * 出生年
         */
        @JsonProperty(value = "birthday_year")
        public String birthdayYear;
        /**
         * 省
         */
        @JsonProperty(value = "province_code")
        public String provinceCode;
        /**
         * 城市
         */
        @JsonProperty(value = "city_code")
        public String cityCode;
        /**
         * 区
         */
        @JsonProperty(value = "district_code")
        public String districtCode;
        /**
         * 婚姻状况
         */
        @JsonProperty(value = "marital_status")
        public Integer maritalStatus;
        /**
         * 教育程度
         */
        @JsonProperty(value = "education")
        public Integer education;
        /**
         * 所属行业
         */
        @JsonProperty(value = "industry_info")
        public Integer industryInfo;
        /**
         * 分销分组
         */
        @JsonProperty(value = "rebate_group")
        public Integer rebateGroup;
        /**
         * 备注
         */
        @JsonProperty(value = "remarks")
        public String remarks;
        /**
         * 上传图片
         */
        @JsonProperty(value = "upload_image")
        public String uploadImage;
        /**
         * 邀请码
         */
        @JsonProperty(value = "invitation_code")
        public String invitationCode;
        /**
         * 自定义激活项
         */
        @JsonProperty(value = "custom_options")
        public List<CustomOptions> customOptions;
        /**
         * 所属行业名称
         */
        public String industryName;
        /**
         * 教育程度名称
         */
        public String educationName;
        /**
         * 婚姻状况名称
         */
        public String maritalName;
        /**
         * 分销分组名称
         */
        public String rebateGroupName;
    }
    @Data
    public static class CustomOptions{
        /**
         * 选项类型 0：单选；1：多选；2：文本
         */
        @JsonProperty(value = "custom_type")
        public Integer customType;

        /**
         * 标题
         */
        @JsonProperty(value = "custom_title")
        public String customTitle;

        /**
         * 选项值
         */
        @JsonProperty(value = "option_arr")
        public List<Options> optionArr;

        /**
         * 条件验证 0：非必填；1：必填
         */
        @JsonProperty(value = "option_ver")
        public Byte optionVer;

        /**
         * 选中状态
         */
        @JsonProperty(value = "is_checked")
        public Byte isChecked;
        /**
         * 文本内容
         */
        @JsonProperty(value = "text")
        public String text;
    }

    @Data
    public static class Options{
        @JsonProperty(value = "option_title")
        public String optionTitle;

        /**
         * 选中状态
         */
        @JsonProperty(value = "checked")
        public String checked;

    }
}
