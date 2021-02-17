package com.meidianyi.shop.service.pojo.shop.market.fullcut;

import com.meidianyi.shop.service.pojo.saas.category.SysCatevo;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsView;
import com.meidianyi.shop.service.pojo.shop.goods.sort.GoodsSortSelectListVo;
import com.meidianyi.shop.service.pojo.shop.member.card.SimpleMemberCardVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.brand.GoodsBrandMpVo;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author: 王兵兵
 * @create: 2019-08-12 16:16
 **/
@Data
public class MrkingStrategyVo {
    /** 活动主键 */
    private Integer id;

    /** 活动名称 */
    private String actName;

    /** 类型,1每满减 2满减 3满折 4仅第X件打折 */
    private Byte type;

    /** 活动类型，0-全部商品参与活动；1-选中商品参与活动（由商品、平台分类、商家分类、品牌等ID字符串指定）*/
    private Byte actType;

    /** 优惠规则 */
    private List<MrkingStrategyCondition> condition;

    /** 开始时间 */
    private Timestamp startTime;

    /** 结束时间 */
    private Timestamp endTime;

    /** 活动状态：0停用，1启用 */
    private Byte status;

    /**优先级 */
    private Integer strategyPriority;


    /** 指定商品可用时，选择的商品信息。全部商品参与活动时为空 */
    private List<Integer> recommendGoodsIds;
    private List<GoodsView> recommendGoods;

    /** 指定商品可用时，选择的平台分类信息。全部商品参与活动时为空 */
    private List<Integer> recommendCatIds;
    private List<SysCatevo> recommendCat;

    /** 指定商品可用时，选择的品牌信息。全部商品参与活动时为空 */
    private List<Integer> recommendBrandIds;
    private List<GoodsBrandMpVo> recommendBrand;

    /** 指定商品可用时，选择的商家分类信息。全部商品参与活动时为空 */
    private List<Integer> recommendSortIds;
    private List<GoodsSortSelectListVo> recommendSort;

    /** 限制持有会员卡的用户可参与时时，选择的会员卡信息。全部用户可参与活动时为空 */
    private List<Integer> cardIds;
    private List<SimpleMemberCardVo> memberCards;
}
