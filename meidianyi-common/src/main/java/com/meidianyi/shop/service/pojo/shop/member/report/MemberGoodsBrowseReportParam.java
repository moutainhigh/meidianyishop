package com.meidianyi.shop.service.pojo.shop.member.report;

import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 用户浏览记录
 * @author 孔德成
 * @date 2020/9/24 11:20
 */
@Data
public class MemberGoodsBrowseReportParam extends BasePageParam {


    /**
     * 商品名
     */
    private String goodsName;
    /**
     * 用户id
     */
    @NotNull
    private Integer userId;
    /**
     * 收藏
     */
    private Byte isCollect;
    /**
     *是否加购
     */
    private Byte isAddCart;
    /**
     * 是否购买
     */
    private Byte isBuy;
    /**
     * 处方
     */
    private Byte isPrescription;
}
