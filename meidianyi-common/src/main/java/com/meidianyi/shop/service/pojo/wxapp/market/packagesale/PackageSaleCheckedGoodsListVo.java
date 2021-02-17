package com.meidianyi.shop.service.pojo.wxapp.market.packagesale;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: 王兵兵
 * @create: 2020-04-10 11:53
 **/
@Getter
@Setter
public class PackageSaleCheckedGoodsListVo {
    private List<GroupGoodsVo> goodsList;
    /**  所选商品件数 */
    private Integer totalSelectNumber;
    /**  所选商品总价 */
    private BigDecimal totalSelectMoney;
    /**
     * 总金额合优惠金额
     */
    private PackageSaleGoodsListVo.TotalMoney totalMoney;


    @Getter
    @Setter
    public static class GroupGoodsVo{
        private Integer goodsNumber;
        private Byte groupId;
        private String groupName;
        /**  选的商品 */
        private List<PackageSaleCartGoodsVo> selectList;
    }
}
