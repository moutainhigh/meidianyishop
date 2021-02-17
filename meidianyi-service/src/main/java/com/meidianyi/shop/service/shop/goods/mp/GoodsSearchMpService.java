package com.meidianyi.shop.service.shop.goods.mp;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.goods.label.GoodsLabelCoupleTypeEnum;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsListMpBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.list.GoodsShowStyleConfigBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.search.*;
import com.meidianyi.shop.service.shop.card.wxapp.WxCardExchangeService;
import com.meidianyi.shop.service.shop.config.ShopCommonConfigService;
import com.meidianyi.shop.service.shop.coupon.CouponService;
import com.meidianyi.shop.service.shop.goods.es.EsGoodsSearchMpService;
import com.meidianyi.shop.service.shop.goods.es.EsUtilSearchService;
import com.meidianyi.shop.service.shop.market.bargain.BargainService;
import com.meidianyi.shop.service.shop.market.goupbuy.GroupBuyService;
import com.meidianyi.shop.service.shop.market.presale.PreSaleService;
import com.meidianyi.shop.service.shop.market.seckill.SeckillService;
import com.meidianyi.shop.service.shop.patient.PatientService;
import com.meidianyi.shop.service.shop.prescription.PrescriptionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.SortField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.meidianyi.shop.db.shop.Tables.GOODS;
import static com.meidianyi.shop.db.shop.Tables.GOODS_LABEL_COUPLE;

/**
 * 小程序-商品搜索界面逻辑处理类
 * @author 李晓冰
 * @date 2020年03月11日
 */
@Service
@Slf4j
public class GoodsSearchMpService extends ShopBaseService {
    @Autowired
    EsGoodsSearchMpService esGoodsSearchMpService;
    @Autowired
    private EsUtilSearchService esUtilSearchService;

    @Autowired
    GoodsLabelMpService goodsLabelMpService;
    @Autowired
    public GoodsBrandSortMpService goodsBrandSortMp;
    @Autowired
    GoodsMpService goodsMpService;

    @Autowired
    GoodsGroupMpService goodsGroupMpService;
    @Autowired
    GroupBuyService groupBuyService;
    @Autowired
    SeckillService seckillService;
    @Autowired
    CouponService couponService;
    @Autowired
    BargainService bargainService;
    @Autowired
    PreSaleService preSaleService;
    @Autowired
    private ShopCommonConfigService shopCommonConfigService;
    @Autowired
    WxCardExchangeService wxCardExchangeSvc;
    @Autowired
    private PrescriptionService prescriptionService;
    @Autowired
    private PatientService patientService;

    /**
     * 小程序端-商品搜索界面-可使用搜索条件数据初始化
     * 由ES反向推到可用数据
     * @return {@link GoodsSearchFilterConditionMpVo}
     */
    public GoodsSearchFilterConditionMpVo getGoodsSearchFilterCondition() {
        if (esUtilSearchService.esState()) {
            try {
                log.debug("小程序-es-商品搜索条件反推");
                return esGoodsSearchMpService.getGoodsParam();
            } catch (IOException e) {
                log.debug("小程序-es-商品搜索条件反推错误-转换db获取数据:" + e.getMessage());
                return getGoodsSearchFilterConditionFromDb();
            }
        } else {
            log.debug("小程序-db-商品搜索条件反推");
            return getGoodsSearchFilterConditionFromDb();
        }
    }

    /**
     * 从数据库反小程序端商品搜索条件
     * @return {@link GoodsSearchFilterConditionMpVo}
     */
    public GoodsSearchFilterConditionMpVo getGoodsSearchFilterConditionFromDb() {
        GoodsSearchFilterConditionMpVo vo = new GoodsSearchFilterConditionMpVo();
        vo.setGoodsBrands(goodsBrandSortMp.getGoodsSearchFilterCondition());
        vo.setGoodsLabels(goodsLabelMpService.getGoodsSearchFilterCondition());
        return vo;
    }


    /**
     * 商品搜索接口统一入口，
     * 判断来自不同的pageFrom的搜索条件，然后调用对应的方法
     * @param param 搜索条件
     * @return 搜索内容
     */
    public GoodsSearchContentVo searchGoodsGate(GoodsSearchMpParam param) {

        if (param.getPageFrom() != null) {
            List<Integer> goodsIds = null;
            if (GoodsSearchMpParam.PAGE_FROM_GROUP_LIST.equals(param.getPageFrom())) {
                goodsIds = goodsGroupMpService.getGoodsIdsLimitedForGoodsSearch(param);
            } else if (GoodsSearchMpParam.PAGE_FROM_GROUP_BUY.equals(param.getPageFrom())) {
                goodsIds = getGoodsIdsLimitedForGroupBuyQrCode(param);
            } else if (GoodsSearchMpParam.PAGE_FROM_SEC_KILL.equals(param.getPageFrom())) {
                goodsIds = getGoodsIdsLimitedForSecKillQrCode(param);
            } else if (GoodsSearchMpParam.PAGE_FROM_COUPON.equals(param.getPageFrom())) {
                goodsIds = getGoodsIdsLimitedForVoucher(param);
            } else if (GoodsSearchMpParam.PAGE_FROM_BARGAIN.equals(param.getPageFrom())) {
                goodsIds = getGoodsIdsLimitedForBargainQrCode(param);
            } else if (GoodsSearchMpParam.PAGE_FROM_CARD_EXCHANGE_GOODS.equals(param.getPageFrom())) {
                goodsIds = getGoodsIdsLimitedForCardExchangeGoods(param);
            } else if (GoodsSearchMpParam.PAGE_FROM_PRE_SALE.equals(param.getPageFrom())) {
                goodsIds = getGoodsIdsLimitedForPreSaleQrCode(param);
            } else if (GoodsSearchMpParam.PAGE_FROM_MY_PRESCRIPTION_MEDICAL.equals(param.getPageFrom())) {
                goodsIds = getMyPrescriptionMedical(param);
            } else if (GoodsSearchMpParam.PAGE_FROM_PRESCRIPTION.equals(param.getPageFrom())) {
                goodsIds = getPrescriptionMedical(param);
            } else {
                // 空数组将搜索不出来商品
                goodsIds = new ArrayList<>();
            }
            param.setGoodsIds(goodsIds);
        }
        PageResult<GoodsListMpBo> pageResult = searchGoods(param);

        goodsMpService.disposeGoodsList(pageResult.dataList, param.getUserId());
        return createSearchContentVo(pageResult);
    }


    private GoodsSearchContentVo createSearchContentVo(PageResult<GoodsListMpBo> pageResult) {
        GoodsShowStyleConfigBo goodsShowStyle = goodsMpService.getGoodsShowStyle();
        GoodsSearchContentVo vo = new GoodsSearchContentVo();
        vo.setDelMarket(goodsShowStyle.getDelMarket());
        vo.setShowCart(goodsShowStyle.getShowCart());
        vo.setPageResult(pageResult);
        return vo;
    }

    /**
     * admin拼团活动扫码进入
     * @param param GoodsSearchMpParam
     * @return 该活动下的有效商品信息
     */
    private List<Integer> getGoodsIdsLimitedForGroupBuyQrCode(GoodsSearchMpParam param) {
        int activityId = param.getOuterPageParam().getActId();
        return groupBuyService.getGroupBuyCanUseGoodsIds(activityId, goodsMpService.getGoodsBaseCondition());
    }

    /**
     * admin秒杀活动扫码进入
     * @param param GoodsSearchMpParam
     * @return 该活动下的有效商品信息
     */
    private List<Integer> getGoodsIdsLimitedForSecKillQrCode(GoodsSearchMpParam param) {
        int activityId = param.getOuterPageParam().getActId();
        return seckillService.getSecKillCanUseGoodsIds(activityId, goodsMpService.getGoodsBaseCondition());
    }

    /**
     * 小程序优惠券进入
     * @param param GoodsSearchMpParam
     * @return 该活动下的有效商品信息
     */
    private List<Integer> getGoodsIdsLimitedForVoucher(GoodsSearchMpParam param) {
        logger().debug("优惠券跳转商品搜索页面");
        int voucherId = param.getOuterPageParam().getActId();
        Condition goodsCouponCondition = couponService.buildGoodsSearchCondition(voucherId);
        Condition goodsBaseCondition = goodsMpService.getGoodsBaseCondition();
        return goodsMpService.getGoodsIdsByCondition(goodsCouponCondition.and(goodsBaseCondition));
    }

    /**
     * admin砍价活动扫码进入
     * @param param GoodsSearchMpParam
     * @return 该活动下的有效商品信息
     */
    private List<Integer> getGoodsIdsLimitedForBargainQrCode(GoodsSearchMpParam param) {
        int activityId = param.getOuterPageParam().getActId();
        return bargainService.getBargainCanUseGoodsIds(activityId, goodsMpService.getGoodsBaseCondition());
    }


    /**
     * 会员卡兑换商品进入
     * @return null 表示全部商品 | List 可兑换商品的Id
     */
    private List<Integer> getGoodsIdsLimitedForCardExchangeGoods(GoodsSearchMpParam param) {
        String cardNo = param.getOuterPageParam().getCardNo();
        return wxCardExchangeSvc.getCardExchangGoodsIds(cardNo);
    }

    /**
     * admin预售活动扫码进入
     * @param param
     * @return
     */
    private List<Integer> getGoodsIdsLimitedForPreSaleQrCode(GoodsSearchMpParam param) {
        int activityId = param.getOuterPageParam().getActId();
        return preSaleService.getPreSaleCanUseGoodsIds(activityId, goodsMpService.getGoodsBaseCondition());
    }

    /**
     * 我的处方药品
     * @param param
     * @return
     */
    private List<Integer> getMyPrescriptionMedical(GoodsSearchMpParam param) {
        return prescriptionService.getPrescriptionGoodsIdsByUserId(param.getUserId());
    }

    /**
     * 获取单个处方关联的药品
     * @param param
     * @return
     */
    private List<Integer> getPrescriptionMedical(GoodsSearchMpParam param){
        String prescriptionCode = param.getOuterPageParam().getPrescriptionCode();
        return prescriptionService.getPrescriptionGoodsIdsByPrescriptionCode(prescriptionCode);
    }

    /**
     * 搜索小程序商品信息
     * @param param 商品信息过滤条件
     * @return 搜索出来的商品信息
     */
    private PageResult<GoodsListMpBo> searchGoods(GoodsSearchMpParam param) {
        PageResult<GoodsListMpBo> pageResult = null;
        param.setSoldOutGoodsShow(goodsMpService.canShowSoldOutGoods());
        if (esUtilSearchService.esState()) {
            //店铺的默认商品排序规则
            if (shopCommonConfigService.getSearchSort().equals((byte) 1)) {
                param.setShopSortItem(goodsMpService.getShopGoodsSortEnum());
                param.setShopSortDirection(SortDirectionEnum.DESC);
            }
            try {
                log.debug("小程序-es-搜索商品");
                log.info("es搜索排序-" + param.getShopSortItem());
                pageResult = esGoodsSearchMpService.queryGoodsByParam(param);
            } catch (Exception e) {
                log.debug("小程序-es-搜索商品-转换db获取数据:" + e.getMessage());
                pageResult = searchGoodsFromDb(param);
            }
        } else {
            log.debug("小程序-db-搜索商品");
            pageResult = searchGoodsFromDb(param);
        }
        return pageResult;
    }

    /**
     * 数据库搜索商品
     * @param param 搜索条件
     * @return 搜索到的内容
     */
    public PageResult<GoodsListMpBo> searchGoodsFromDb(GoodsSearchMpParam param) {

        Condition condition = buildSearchCondition(param);

        List<SortField<?>> sortFields = buildSearchOrderFields(param);

        return goodsMpService.findActivityGoodsListCapsulesDao(condition, sortFields, param.getCurrentPage(), param.getPageRows(), null);
    }

    /**
     * 商品搜索-db-条件拼接
     * @param param 搜索条件
     * @return
     */
    private Condition buildSearchCondition(GoodsSearchMpParam param) {
        Condition condition = goodsMpService.getGoodsBaseCondition();

        if (StringUtils.isNotBlank(param.getKeyWords())) {
            condition = condition.and(GOODS.GOODS_NAME.like(likeValue(param.getKeyWords())));
        }

        // 外部条件限制的商品搜索范围
        if (param.getGoodsIds() != null) {
            condition = condition.and(GOODS.GOODS_ID.in(param.getGoodsIds()));
        }
        if(param.getMinPrice()!=null){
            condition=condition.and(GOODS.SHOP_PRICE.ge(param.getMinPrice()));
        }
        if(param.getMaxPrice()!=null){
            condition=condition.and(GOODS.SHOP_PRICE.le(param.getMaxPrice()));
        }

        // 当es挂掉的时候，只查询和商家分类直接关联的商品，不进行子项查询。（功能退级）
        if (param.getSortIds() != null && param.getSortIds().size() > 0) {
            condition = condition.and(GOODS.SORT_ID.in(param.getSortIds()));
        }
        if (param.getBrandIds() != null && param.getBrandIds().size() > 0) {
            condition = condition.and(GOODS.BRAND_ID.in(param.getBrandIds()));
        }
        // 从数据库搜索时仅匹配直接关联商品的标签和关联了全部商品的标签
        // 标签直接取的是或的关系
        if (param.getLabelIds() != null && param.getLabelIds().size() > 0) {
            Result<Record2<Integer, Byte>> labelCoupleList = goodsLabelMpService.getGoodsLabelsCoupleTypeInfoByIds(param.getLabelIds());
            boolean allType = false;
            List<Integer> gtaGoodsIds = new ArrayList<>();
            for (int i = 0; i < labelCoupleList.size(); i++) {
                Record2<Integer, Byte> record2 = labelCoupleList.get(i);
                if (GoodsLabelCoupleTypeEnum.ALLTYPE.getCode().equals(record2.get(GOODS_LABEL_COUPLE.TYPE))) {
                    allType = true;
                    break;
                }
                if (GoodsLabelCoupleTypeEnum.GOODSTYPE.getCode().equals(record2.get(GOODS_LABEL_COUPLE.TYPE))) {
                    gtaGoodsIds.add(record2.get(GOODS_LABEL_COUPLE.GTA_ID));
                }
            }
            if (!allType) {
                condition = condition.and(GOODS.GOODS_ID.in(gtaGoodsIds));
            }
        }
        return condition;
    }

    /**
     * 排序字段拼接
     * @param param 请求条件
     * @return 待排序字段集合
     */
    protected List<SortField<?>> buildSearchOrderFields(GoodsSearchMpParam param) {
        List<SortField<?>> list = new ArrayList<>(2);

        if (param.getSortItem() != null && param.getSortItem() != SortItemEnum.NULL) {
            //目前用户可以指定销量和价格两种排序方式
            if (SortItemEnum.SALE_NUM.equals(param.getSortItem())) {
                if (SortDirectionEnum.DESC.equals(param.getSortDirection())) {
                    list.add(GOODS.GOODS_SALE_NUM.desc());
                } else {
                    list.add(GOODS.GOODS_SALE_NUM.asc());
                }
            } else {
                // 默认价格排序
                if (SortDirectionEnum.DESC.equals(param.getSortDirection())) {
                    list.add(GOODS.SHOP_PRICE.desc());
                } else {
                    list.add(GOODS.SHOP_PRICE.asc());
                }
            }
        }

        if (shopCommonConfigService.getSearchSort().equals(Byte.valueOf((byte) 1))) {
            list.add(goodsMpService.getShopGoodsSort());
        }
        log.info("db搜索排序-" + list);

        return list;
    }

}
