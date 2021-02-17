package com.meidianyi.shop.service.pojo.shop.config.distribution;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 分销配置出入参
 * @author 常乐
 * 2019年7月17日
 */
@Data
@NoArgsConstructor
public class DistributionParam {
	/**
	 * 分销开关
	 */
	@JsonProperty(value = "status")
	public Byte status;

	/**
	 * 分销员审核开关
	 */
	@JsonProperty(value = "judge_status")
	public Byte judgeStatus;

	/**
	 * 分销员排名开关
	 */
	@JsonProperty(value = "rank_status")
	public Byte rankStatus;

    /**
     * 独立商品推广页开关 0：关闭；1：开启
     */
    @JsonProperty(value = "goods_promote_status")
    public Byte goodsPromoteStatus;

    /**
     * 推广商品页使用范围 0：全部分销员；1：部分分销员
     */
    @JsonProperty(value = "goods_promote_scope")
    public Byte goodsPromoteScope;

    /**
     * 指定分销员 使用独立推广页
     */
    @JsonProperty(value = "goods_promote_distributor")
    public String goodsPromoteDistributor;

    /**
     * 指定分销员等级 使用独立推广页
     */
    @JsonProperty(value = "goods_promote_level")
    public String goodsPromoteLevel;

    /**
     * 是否可以直接绑定 0：否；1：是
     */
    @JsonProperty(value = "directly_bind")
    public Byte directlyBind;
	/**
	 * 返利有效期
	 */
	@JsonProperty(value = "vaild")
	public Byte vaild;

	/**
	 * 推广模版文案id
	 */
	@JsonProperty(value = "rebate_page_id")
	public int rebatePageId;

	/**
	 * 分销员保护期
	 */
	@JsonProperty(value = "protect_date")
	public Byte protectDate;

	/**
	 * 分销中心页面名称
	 */
	@JsonProperty(value = "desc")
	public String desc;

	/**
	 * 海报背景图
	 */
	@JsonProperty(value = "bg_img")
	public String bgImg;

	/**
	 * 邀请文案
	 */
	@JsonProperty(value = "rebate_center_name")
	public String rebateCenterName;

	/**
	 * 提现开关
	 */
	@JsonProperty(value = "withdraw_status")
	public Byte withdrawStatus;

	/**
	 * 返利最小提现金额
	 */
	@JsonProperty(value = "withdraw_cash")
	public Byte withdrawCash;

    /**
     * 提现设置
     */
    @JsonProperty(value = "withdraw_config")
    public String withdrawConfig;

	/**
	 * 返利方式
	 */
	@JsonProperty(value = "withdraw_source")
	public String withdrawSource;

	/**
	 * 分销员审核开关开启后，是否需要提个人信息
	 */
	@JsonProperty(value = "activation")
	public Byte activation;

	/**
	 * 个人信息容
	 */
	@JsonProperty(value = "activation_cfg")
	public List<String> activationCfg;


	/**
	 * 推荐商品 0：不限时；1：默认；2：自定义
	 */
	@JsonProperty(value = "distribution_goods_type")
	public byte distributionGoodsType;

	/**
	 * 推荐商品ID
	 */
	@JsonProperty(value = "recommend_goods_id")
	public String recommendGoodsId;

	/**
	 * 邀请码
	 */
	@JsonProperty(value = "invitation_code")
	public Integer invitationCode;

	@JsonProperty(value = "auto_examine")
    public Byte autoExamine;

    /**
     * 自定义激活项
     */
	@JsonProperty(value = "custom_options")
    public List<CustomOptions> customOptions;

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

    }

    @Data
    public static class Options{
	    @JsonProperty(value = "option_title")
        public String optionTitle;

    }

    /**是否有分销员*/
    @NotNull
    public Integer hasDistributor;

}
