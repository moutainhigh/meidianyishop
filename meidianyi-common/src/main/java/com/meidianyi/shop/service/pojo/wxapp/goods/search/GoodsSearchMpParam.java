package com.meidianyi.shop.service.pojo.wxapp.goods.search;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 小程序商品搜索界面-用户搜索条件
 * @author 李晓冰
 * @date 2019年12月09日
 */
@Getter
@Setter
public class GoodsSearchMpParam extends BasePageParam {


    /**用户id，controller 层获取*/
    private Integer userId;


    /**搜索输入的关键字*/
    private String keyWords;

    /**商品最低价*/
    private BigDecimal minPrice;

    /**商品最高价格*/
    private BigDecimal maxPrice;

    /**商品类型：实物商品 OR 非实物商品*/
    private Byte goodsType;

    /**商家分类id集合，为了满足从商品分组处跳转时使用*/
    private List<Integer> sortIds;

    /**品牌id集合*/
    private List<Integer> brandIds;

    /**活动类型集合*/
    private List<Integer> activityTypes;

    /**标签id集合*/
    private List<Integer> labelIds;

    /**用户指定的排序字段*/
    private SortItemEnum sortItem;
    /**
     * 用户指定的排序方向
     */
    private SortDirectionEnum sortDirection;

    /**
     * 店铺默认的排序字段-es使用字段
     */
    private SortItemEnum shopSortItem;
    /**
     * 店铺默认的排序方向-es使用字段
     */
    private SortDirectionEnum shopSortDirection;

    /**
     * 是否展示售罄商品 true展示 false否
     */
    private Boolean soldOutGoodsShow;

    /**
     * 外面搜索条件限制的商品范围，null表示不限制，长度为0的数组表示没有合法商品
     */
    private List<Integer> goodsIds;
    /**
     * 需要排除的商品id
     */
    private List<Integer> excludeGoodsIds;

    // =========分销推广商品相关========= start
    /**
     * 分销员id
     */
    private Integer distributorId;
    /**
     * 推广商品集合-分销推广页使用
     */
    private List<Integer> rebateGoodsIds;
    /**
     * 从分销员分享的推广商品链接跳转至商品搜索页面，展示其关联的商品信息 pageFrom=26
     */
    public static final Byte PAGE_FROM_DISTRIBUTOR_GOODS = BaseConstant.ACTIVITY_TYPE_DISTRIBUTOR_GOODS;
    // =========分销推广商品相关========= end

    /**
     * 从商品分组页面跳转至此
     */
    public static final Byte PAGE_FROM_GROUP_LIST = 0;
    /**
     * admin拼团活动分享码跳转 pageFrom =1
     */
    public static final Byte PAGE_FROM_GROUP_BUY = BaseConstant.ACTIVITY_TYPE_GROUP_BUY;
    /**
     * admin秒杀活动分享码跳转 pageFrom =5
     */
    public static final Byte PAGE_FROM_SEC_KILL = BaseConstant.ACTIVITY_TYPE_SEC_KILL;
    /**从优惠券跳转至商品搜索页面，展示其关联的商品信息 pageFrom=20*/
    public static final Byte PAGE_FROM_COUPON=BaseConstant.ACTIVITY_TYPE_COUPON;
    /**从砍价活动跳转至商品搜索页面，展示其关联的商品信息 pageFrom=3*/
    public static final Byte PAGE_FROM_BARGAIN=BaseConstant.ACTIVITY_TYPE_BARGAIN;
    /**从兑换商品页面的搜索 */
    public static final Byte PAGE_FROM_CARD_EXCHANGE_GOODS=BaseConstant.ACTIVITY_TYPE_EXCHANG_ORDER;
    /**从预售活动跳转至商品搜索页面，展示其关联的商品信息 pageFrom=10*/
    public static final Byte PAGE_FROM_PRE_SALE =BaseConstant.ACTIVITY_TYPE_PRE_SALE;
    /**从我的处方药跳转至商品搜索页*/
    public static final Byte PAGE_FROM_MY_PRESCRIPTION_MEDICAL = 101;
    /**从处方跳转至搜索页*/
    public static final Byte PAGE_FROM_PRESCRIPTION = 102;
    /**从哪个页面跳转至搜索页面，目前用于区分从商品分组模块跳转至此，目前从分组跳转时未从es查数据*/
    private Byte pageFrom;

    private GoodsSearchMpOuterParam outerPageParam;
}
