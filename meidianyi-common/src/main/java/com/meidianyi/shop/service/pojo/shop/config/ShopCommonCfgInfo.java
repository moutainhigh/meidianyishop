package com.meidianyi.shop.service.pojo.shop.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.service.pojo.shop.sms.account.SmsAccountInfoVo;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author 王兵兵
 *
 * 2019年7月2日
 */
@Data
@NoArgsConstructor
public class ShopCommonCfgInfo {
	/**
	 *销量展示开关
	 */
	@NotNull
	@JsonProperty(value = "sales_number")
	public Byte salesNumber;

    /**
     * 商品搜索页以及推荐商品列表中会显示购买按钮
     */
    @NotNull
    @JsonProperty(value = "show_cart")
    public ShowCartConfig showCart;

	/**
	 *是否显示划线价（市场价/销量/评论）开关，单选
     * 取值：0关闭，1显示市场价，2显示销量，3显示评价数
	 */
    @NotNull
	@JsonProperty(value = "del_market")
	public Byte delMarket;

    /**
     *是否显示售罄商品
     */
    @NotNull
    @JsonProperty(value = "sold_out_goods")
    public Byte soldOutGoods;

    /**
     *小程序端"店铺商品页"默认排序规则，五选一
     * add_time 按照商品上新时间倒序排列
     * on_sale_time 按照商品上架时间倒序排列
     * goods_sale_num 按照商品销量倒序排列
     * comment_num 按照商品评价数量倒序排列
     * pv 按照商品访问次数倒序排列，7天内访问次数最多的商品将排在商品列表最上方
     */
    @NotNull
    @JsonProperty(value = "goods_sort")
    public String goodsSort;

    @NotNull
    @JsonProperty(value = "search_sort")
    public Byte searchSort;

    @NotNull
    @JsonProperty(value = "recommend_sort")
    public Byte recommendSort;

    @NotNull
    @JsonProperty(value = "order_sort")
    public Byte orderSort;

    /**
     *是否显示购买记录
     */
    @NotNull
    @JsonProperty(value = "goods_record")
    public Byte goodsRecord;

	/**
	 *客服入口开关-商品详情页是否展示
	 */
    @NotNull
	@JsonProperty(value = "custom_service")
	public Byte customService;

    /**
     *客服入口开关-退/换货中心是否展示
     */
    @NotNull
    @JsonProperty(value = "return_service")
    public Byte returnService;

    /**
     *客服入口开关-订单详情页是否展示
     */
    @NotNull
    @JsonProperty(value = "order_detail_service")
    public Byte orderDetailService;

    /**
     *商品默认平台分类id
     */
    @NotNull
    @JsonProperty(value = "default_sort")
    public Integer defaultSort;

	/**
	 *店铺分享配置
	 */
    @NotNull
	@JsonProperty(value = "share_config")
	public ShopShareConfig shareConfig;

    /**
     *是否强制用户在购买、预约以及申请成为分销员时绑定手机号
     */
    @NotNull
    @JsonProperty(value = "bind_mobile")
    public Byte bindMobile;

    /**
     *开关开启，用户进入小程序时会弹出地理位置授权申请。
     * 用户在小程序中触发需要授权地理位置的操作时，默认会调起授权提示，不受此开关控制
     */
    @NotNull
    @JsonProperty(value = "geographic_location")
    public Byte geographicLocation;

    /**
     *商品重量配置项开关
     */
    @NotNull
    @JsonProperty(value = "goods_weight_cfg")
    public Byte goodsWeightCfg;

    /**
     *商品条码配置项开关
     */
    @NotNull
    @JsonProperty(value = "need_prd_codes")
    public Byte needPrdCodes;

    /**
     *商品分享配置
     */
    @NotNull
    @JsonProperty(value = "goods_share_cfg")
    public GoodsShareConfig goodsShareCfg;

    /**
     *后台商品搜索设置
     */
    @NotNull
    @JsonProperty(value = "accurate_search")
    public Byte accurateSearch;
    /**
     * 短信账户信息
     */
    @JsonProperty(value = "sms_account")
    private SmsAccountInfoVo smsAccount;
}
