package com.meidianyi.shop.service.pojo.shop.config.center;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.common.foundation.validator.CharacterValid;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author 孔德成
 *  未完成，先以json格式直接存放数据库，等前端确定参数在完善
 * @description [{"module_name":"global","page_style":"1"},{"module_name":"center_header","is_show":1,"bg_type":"0"},{"module_name":"account_money","is_show":0,"title":"\u6211\u7684\u8d44\u4ea7","content":[{"icon_name":"account","is_show":"1"},{"icon_name":"score","is_show":"1"},{"icon_name":"coupon","is_show":"1"},{"icon_name":"card","is_show":"1"}]},{"module_name":"order","is_show":1,"title":"\u6211\u7684\u8ba2\u5355","module_style":"1","content":[{"icon_name":"wait_pay","icon":"http:\/\/mpdevimg2.weipubao.cn\/image\/admin\/uc_config\/uc_order_icon1.png","is_show":1},{"icon_name":"wait_deliver","icon":"http:\/\/mpdevimg2.weipubao.cn\/image\/admin\/uc_config\/uc_order_icon2.png","is_show":1},{"icon_name":"wait_receive","icon":"http:\/\/mpdevimg2.weipubao.cn\/image\/admin\/uc_config\/uc_order_icon3.png","is_show":1},{"icon_name":"wait_comment","icon":"http:\/\/mpdevimg2.weipubao.cn\/image\/admin\/uc_config\/uc_order_icon4.png","is_show":1},{"icon_name":"refund","icon":"http:\/\/mpdevimg2.weipubao.cn\/image\/admin\/uc_config\/uc_order_icon5.png","is_show":1}]},{"module_name":"appointment","is_show":1,"title":"\u6211\u7684\u9884\u7ea6"},{"module_name":"use_record","is_show":1,"title":"\u4f7f\u7528\u8bb0\u5f55","is_show_collect":1,"is_show_buy_history":1,"is_show_footprint":1},{"module_name":"service","title":"\u6211\u7684\u670d\u52a1","is_show":1,"content":[{"is_show":"1","icon_name":"distribution","icon":"http:\/\/mpdevimg2.weipubao.cn\/upload\/4748160\/image\/20190528\/I6eh09rewaciTaji.png","link":"","link_name":""},{"is_show":"1","icon_name":"bargain","icon":"http:\/\/mpdevimg2.weipubao.cn\/upload\/4748160\/image\/20190527\/BpUFvnYjTCWYSJvB.png","link":"","link_name":""},{"is_show":"1","icon_name":"award","icon":"http:\/\/mpdevimg2.weipubao.cn\/upload\/4748160\/image\/20190528\/I6eh09rewaciTaji.png","link":"","link_name":""},{"is_show":"0","icon_name":"store_list","icon":"http:\/\/mpdevimg2.weipubao.cn\/image\/admin\/uc_config\/icon_store.png","link":"","link_name":""},{"is_show":"1","icon_name":"user_activate","icon":"http:\/\/mpdevimg2.weipubao.cn\/image\/admin\/uc_config\/icon_member.png","link":"","link_name":""},{"is_show":"1","icon_name":"order_verify","icon":"http:\/\/mpdevimg2.weipubao.cn\/image\/admin\/uc_config\/icon_scan.png","link":"","link_name":""},{"is_show":"1","icon_name":"present_list","icon":"http:\/\/mpdevimg2.weipubao.cn\/upload\/0\/image\/20190708\/crop_mgsjwtZBwX9XkCts.jpeg","link":"","link_name":""}]}]
 * @date 2019/7/11 10:32
 */
@Data
@NoArgsConstructor
public class UserCenterConfigParam {

    /**
     *  模块标识：
     *      global ：页面布局样式
     *      center_header: 账户资产信息展示项配置
     *      account_money:账户资产信息展示项配置
     *      order:订单信息展示项配置
     *      appointment:预约信息展示项配置
     *      use_record:店铺使用相关数据展示项配置
     *      service:服务展示项配置
     *
     */
    @NotBlank
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "module_name")
    private String moduleName;
    /**
     *页面风格
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "page_style")
    private String pageStyle;
    /**
     *
     * 个人信息背景色配置
     * 展示 true显示 false隐藏
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "is_show")
    private String isShow;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "bg_type")
    private String bgType;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "bg_img")
    private String bgImg;
    /**
     * 标题
     */
    @CharacterValid(letterDigit =false,chinese =false, max = 10)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String title;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "module_style")
    private String moduleStyle;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "is_show_collect")
    private String isShowCollect;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "is_show_buy_history")
    private String isShowBuyHistory;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "is_show_footprint")
    private String isShowFootprint;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<UserCenterContentConfig> content;



}
