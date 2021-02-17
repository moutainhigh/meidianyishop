package com.meidianyi.shop.service.shop.goods;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.excel.ExcelWriter;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Page;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.UpYunConfig;
import com.meidianyi.shop.dao.shop.goods.GoodsDao;
import com.meidianyi.shop.db.shop.Tables;
import com.meidianyi.shop.db.shop.tables.records.*;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.jedis.data.DBOperating;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributionRecommendGoodsParam;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import com.meidianyi.shop.service.pojo.shop.goods.goods.*;
import com.meidianyi.shop.service.pojo.shop.goods.label.GoodsLabelCouple;
import com.meidianyi.shop.service.pojo.shop.goods.label.GoodsLabelCoupleTypeEnum;
import com.meidianyi.shop.service.pojo.shop.goods.label.GoodsLabelSelectListVo;
import com.meidianyi.shop.service.pojo.shop.goods.spec.GoodsSpec;
import com.meidianyi.shop.service.pojo.shop.goods.spec.GoodsSpecProduct;
import com.meidianyi.shop.service.pojo.shop.goods.spec.ProductSmallInfoVo;
import com.meidianyi.shop.service.pojo.shop.member.card.CardBasicVo;
import com.meidianyi.shop.service.pojo.shop.member.card.CardConstant;
import com.meidianyi.shop.service.pojo.shop.qrcode.QrCodeTypeEnum;
import com.meidianyi.shop.service.pojo.shop.video.GoodsVideoBo;
import com.meidianyi.shop.service.pojo.wxapp.market.bargain.BargainGoodsPriceBo;
import com.meidianyi.shop.service.saas.es.EsMappingUpdateService;
import com.meidianyi.shop.service.shop.activity.dao.BargainProcessorDao;
import com.meidianyi.shop.service.shop.activity.dao.GroupBuyProcessorDao;
import com.meidianyi.shop.service.shop.activity.dao.PreSaleProcessorDao;
import com.meidianyi.shop.service.shop.activity.dao.SecKillProcessorDao;
import com.meidianyi.shop.service.shop.config.ConfigService;
import com.meidianyi.shop.service.shop.config.ShopCommonConfigCacheService;
import com.meidianyi.shop.service.shop.decoration.ChooseLinkService;
import com.meidianyi.shop.service.shop.decoration.MpDecorationService;
import com.meidianyi.shop.service.shop.goods.es.*;
import com.meidianyi.shop.service.shop.goods.es.goods.label.EsGoodsLabelCreateService;
import com.meidianyi.shop.service.shop.image.ImageService;
import com.meidianyi.shop.service.shop.image.QrCodeService;
import com.meidianyi.shop.service.shop.market.live.LiveService;
import com.meidianyi.shop.service.shop.member.MemberCardService;
import com.meidianyi.shop.service.shop.member.card.GradeCardService;
import com.meidianyi.shop.service.shop.store.store.StoreGoodsService;
import com.meidianyi.shop.service.shop.store.store.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.Tables.*;
import static com.meidianyi.shop.db.shop.tables.Goods.GOODS;
import static com.meidianyi.shop.db.shop.tables.SecKillProductDefine.SEC_KILL_PRODUCT_DEFINE;
import static com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsPageListParam.ASC;
import static org.apache.commons.lang3.math.NumberUtils.BYTE_ZERO;

/**
 * 商品
 * 商品关联的附属数据信息主要包含：商品sku(b2c_goods_spec_product)，规格名(b2c_spec),规格值(b2c_spec_vals),
 * 商品图片(b2c_goods_img),平台分类(b2c_category)和商家分类(b2c_sort)在查询商品时可以根据这两个进行筛选,
 * 商品标签(b2c_goods_label,b2c_goods_label_couple)标签可以打在平台和商家分类上，在通过标签查商品时要进行关联查询，
 * 会员等价卡价格(b2c_grade_prd)，运费模板(b2c_deliver_fee_template)，会员专享商品(b2c_goods_card_couple),商品页模板，
 * 分销改价(b2c_goods_rebate_price)
 * TODO:1.所有操作添加操作记录
 * TODO:2.自定义商品上架时间的时候在新增删除修改的时候修改定时任务
 *
 * @author 李晓冰
 * @date 2019年6月25日
 */
@Service
@Slf4j
public class GoodsService extends ShopBaseService {

    @Autowired
    public GoodsBrandService goodsBrand;
    @Autowired
    public GoodsSortService goodsSort;
    @Autowired
    public GoodsCommentService goodsComment;
    @Autowired
    public GoodsLabelService goodsLabel;
    @Autowired
    public GoodsLabelCoupleService goodsLabelCouple;
    @Autowired
    public GoodsDeliverTemplateService goodsDeliver;
    @Autowired
    LiveService liveService;
    @Autowired
    public ChooseLinkService chooseLink;
    @Autowired
    protected MemberCardService memberCardService;
    @Autowired
    protected GradeCardService gradeCardService;
    @Autowired
    public GoodsSpecProductService goodsSpecProductService;
    @Autowired
    protected QrCodeService qrCodeService;
    @Autowired
    protected ImageService imageService;
    @Autowired
    protected UpYunConfig upYunConfig;
    @Autowired
    protected MpDecorationService mpDecorationService;
    @Autowired
    public GoodsPriceService goodsPrice;
    @Autowired
    private EsFactSearchService esFactSearchService;
    @Autowired
    private EsGoodsSearchService esGoodsSearchService;
    @Autowired
    private EsGoodsCreateService esGoodsCreateService;
    @Autowired
    private EsGoodsLabelCreateService esGoodsLabelCreateService;
    @Autowired
    private EsUtilSearchService esUtilSearchService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private SecKillProcessorDao secKillProcessorDao;
    @Autowired
    private PreSaleProcessorDao preSaleProcessorDao;
    @Autowired
    private BargainProcessorDao bargainProcessorDao;
    @Autowired
    private GroupBuyProcessorDao groupBuyProcessorDao;
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private StoreService storeService;
    @Autowired
    private StoreGoodsService storeGoodsService;
    @Autowired
    private EsMappingUpdateService esMappingUpdateService;
    @Autowired
    private EsDataUpdateMqService esDataUpdateMqService;
    @Autowired
    private ShopCommonConfigCacheService shopCommonConfigCacheService;
    /**
     * 商品在售
     */
    private static final Byte IS_ON_SALE = 1;

    /**
     * 获取全品牌，标签，商家分类数据,平台分类数据
     *
     * @param param {@link com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsFilterItemInitParam}
     * @return {@link com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsFilterItemInitVo}
     */
    public GoodsFilterItemInitVo getGoodsFilterItem(GoodsFilterItemInitParam param) {
//        es查询目前不会同时查询分类数据和品牌标签数据
        if (param.canGoEs() && esUtilSearchService.esState()) {
            try {
                return esFactSearchService.assemblyFactByAdminGoodsListInit(param);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (param.getNeedGoodsNum() != null && param.getNeedGoodsNum()) {
            return getGoodsFilterItemWithGoodsNum(param);
        } else {
            return getGoodsFilterItemNotGoodsNum(param);
        }
    }

    /**
     * 根据条件查询过滤商品所有需要的标签，品牌，商家分类信息项（统计对应的商品数量）
     *
     * @param param {@link com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsFilterItemInitParam}
     * @return {@link com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsFilterItemInitVo}
     */
    private GoodsFilterItemInitVo getGoodsFilterItemWithGoodsNum(GoodsFilterItemInitParam param) {
        GoodsFilterItemInitVo vo = new GoodsFilterItemInitVo();
        // 需要商品标签
        if (Boolean.TRUE.equals(param.getNeedGoodsLabel())) {
            vo.setGoodsLabels(goodsLabel.getGoodsLabelSelectList());
        }
        // 需要商品品牌
        if (Boolean.TRUE.equals(param.getNeedGoodsBrand())) {
            vo.setGoodsBrands(goodsBrand.getGoodsBrandSelectList());
        }
        if (Boolean.TRUE.equals(param.getNeedGoodsSort()) || Boolean.TRUE.equals(param.getNeedSysCategory())) {
            List<GoodsRecord> goodsRecords = getGoodsListForFilterItem(param);
            // 需要商家分类信息
            if (Boolean.TRUE.equals(param.getNeedGoodsSort())) {
                Map<Integer, Long> sortNumMap = goodsRecords.stream().collect(Collectors.groupingBy(GoodsRecord::getSortId, Collectors.counting()));
                vo.setGoodsSorts(goodsSort.getSortSelectTree(sortNumMap));
            }
            // 需要平台分类信息
            if (Boolean.TRUE.equals(param.getNeedSysCategory())) {
                Map<Integer, Long> catNumMap = goodsRecords.stream().collect(Collectors.groupingBy(GoodsRecord::getCatId, Collectors.counting()));
                vo.setGoodsCategories(saas.sysCate.getCatSelectTree(catNumMap));
            }
        }

        return vo;
    }

    /**
     * 获取符合条件的商品的平台分类，商家分类信息
     *
     * @param param {@link com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsFilterItemInitParam}
     * @return 商品信息集合
     */
    private List<GoodsRecord> getGoodsListForFilterItem(GoodsFilterItemInitParam param) {
        GoodsPageListParam goodsPageListParam = new GoodsPageListParam();
        goodsPageListParam.setSelectType(param.getSelectType());
        goodsPageListParam.setIsOnSale(param.getIsOnSale());
        goodsPageListParam.setIsSaleOut(param.getIsSaleOut());

        Condition condition = buildOptions(goodsPageListParam);

        List<GoodsRecord> goodsRecords;

        if (GoodsPageListParam.GOODS_LIST.equals(goodsPageListParam.getSelectType())) {
            goodsRecords = db().select(GOODS.GOODS_ID, GOODS.SORT_ID, GOODS.CAT_ID).from(GOODS).where(condition).fetchInto(GoodsRecord.class);
        } else {
            goodsRecords = db().select(GOODS.GOODS_ID, GOODS.SORT_ID, GOODS.CAT_ID).from(GOODS).innerJoin(GOODS_SPEC_PRODUCT)
                    .on(GOODS.GOODS_ID.eq(GOODS_SPEC_PRODUCT.GOODS_ID)).where(condition).fetchInto(GoodsRecord.class);
        }
        return goodsRecords;
    }

    /**
     * 根据条件查询过滤商品所有需要的标签，品牌，商家分类信息项（对应的项内不统计对应的商品数量）
     *
     * @param param {@link com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsFilterItemInitParam}
     * @return {@link com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsFilterItemInitVo}
     */
    private GoodsFilterItemInitVo getGoodsFilterItemNotGoodsNum(GoodsFilterItemInitParam param) {
        GoodsFilterItemInitVo vo = new GoodsFilterItemInitVo();
        // 需要商品标签
        if (Boolean.TRUE.equals(param.getNeedGoodsLabel())) {
            vo.setGoodsLabels(goodsLabel.getGoodsLabelSelectList());
        }
        // 需要商品品牌
        if (Boolean.TRUE.equals(param.getNeedGoodsBrand())) {
            vo.setGoodsBrands(goodsBrand.getGoodsBrandSelectList());
        }
        // 需要商家分类信息
        if (Boolean.TRUE.equals(param.getNeedGoodsSort())) {
            vo.setGoodsSorts(goodsSort.getSortSelectTree());
        }
        // 需要平台分类信息
        if (Boolean.TRUE.equals(param.getNeedSysCategory())) {
            vo.setGoodsCategories(saas.sysCate.getCatSelectTree());
        }
        return vo;
    }

    /**
     * 全部商品界面：商品分页查询，包含了部分规格信息（规格价格范围，规格类型数量）
     *
     * @param goodsPageListParam {@link com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsPageListParam}
     * @return {@link com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsPageListVo}
     */
    public PageResult<GoodsPageListVo> getPageList(GoodsPageListParam goodsPageListParam) {
        PageResult<GoodsPageListVo> pageResult;
        assemblyGoodsPageListParam(goodsPageListParam);
        if (esUtilSearchService.esState()) {
            try {
                pageResult = esGoodsSearchService.searchGoodsPageByParam(goodsPageListParam);
            } catch (IOException e) {
                logger().info("【admin goods search】es error");
                pageResult = getGoodsPageByDb(goodsPageListParam);
            }
        } else {
            pageResult = getGoodsPageByDb(goodsPageListParam);
        }
        return pageResult;
    }

    /**
     * 需要对搜索条件进行二次封装
     *
     * @param goodsPageListParam 搜索条件
     */
    private void assemblyGoodsPageListParam(GoodsPageListParam goodsPageListParam) {
        //从缓存获取admin搜索分词配置
        goodsPageListParam.setOpenedAnalyzer(shopCommonConfigCacheService.enabledAnalyzerStatus());

    }

    private PageResult<GoodsPageListVo> getGoodsPageByDb(GoodsPageListParam goodsPageListParam) {
        // 拼接过滤条件
        Condition condition = this.buildOptions(goodsPageListParam);

        SelectConditionStep<?> selectFrom = db().select(GOODS.GOODS_ID, GOODS.GOODS_NAME, GOODS.GOODS_NAME.as("goods_title_name"), GOODS.GOODS_IMG, GOODS.GOODS_SN, GOODS.SHOP_PRICE,
                GOODS.SOURCE, GOODS.GOODS_TYPE, GOODS.CAT_ID, SORT.SORT_NAME, GOODS.SORT_ID, GOODS_BRAND.BRAND_NAME, GOODS.GOODS_NUMBER,
                GOODS.GOODS_SALE_NUM, GOODS.IS_CARD_EXCLUSIVE, GOODS.MARKET_PRICE)
                .from(GOODS).leftJoin(SORT).on(GOODS.SORT_ID.eq(SORT.SORT_ID)).leftJoin(GOODS_BRAND)
                .on(GOODS.BRAND_ID.eq(GOODS_BRAND.ID)).where(condition);

        // 拼接排序
        selectFrom = this.buildOrderFields(selectFrom, goodsPageListParam);

        PageResult<GoodsPageListVo> pageResult = this.getPageResult(selectFrom, goodsPageListParam.getCurrentPage(),
                goodsPageListParam.getPageRows(), GoodsPageListVo.class);

        // 结果集标签、平台分类、规格信息二次处理
        this.disposeGoodsPageListVo(pageResult.getDataList(), goodsPageListParam);
        return pageResult;
    }

    /**
     * 获取所有符合条件的商品id集合
     *
     * @param goodsPageListParam {@link com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsPageListParam}
     * @return 商品id结合
     */
    public List<Integer> getGoodsIdsListAll(GoodsPageListParam goodsPageListParam) {
//        PageResult<GoodsPageListVo> pageResult;
//        if (esUtilSearchService.esState()) {
//            try {
//                pageResult = esGoodsSearchService.searchGoodsByParam(goodsPageListParam);
//            } catch (IOException e) {
//                logger().info("es");
//                pageResult = getGoodsPageByDb(goodsPageListParam);
//            }
//        }else{
//            pageResult = getGoodsPageByDb(goodsPageListParam);
//        }
//        return pageResult.getDataList().stream().map(GoodsPageListVo::getGoodsId).collect(Collectors.toList());
        // 拼接过滤条件
        List<Integer> goodsIds;
        assemblyGoodsPageListParam(goodsPageListParam);
        try {
            goodsIds = esGoodsSearchService.getGoodsIdsByParam(goodsPageListParam);
        } catch (IOException e) {
            Condition condition = this.buildOptions(goodsPageListParam);

            goodsIds = db().select(GOODS.GOODS_ID)
                    .from(GOODS).leftJoin(SORT).on(GOODS.SORT_ID.eq(SORT.SORT_ID)).leftJoin(GOODS_BRAND)
                    .on(GOODS.BRAND_ID.eq(GOODS_BRAND.ID)).where(condition).fetch(GOODS.GOODS_ID);
        }
        return goodsIds;
    }

    /**
     * 根据商品id集合获取对应的商品信息和规格信息
     *
     * @param goodsIds
     * @return GoodsPageListVo
     */
    public List<GoodsPageListVo> getGoodsAndProductsByGoodsIds(List<Integer> goodsIds) {
        List<GoodsPageListVo> goodsPageListVos = db().select(GOODS.GOODS_ID, GOODS.GOODS_NAME, GOODS.GOODS_IMG, GOODS.GOODS_SN, GOODS.SHOP_PRICE,
                GOODS.SOURCE, GOODS.GOODS_TYPE, GOODS.CAT_ID, SORT.SORT_NAME, GOODS.SORT_ID, GOODS_BRAND.BRAND_NAME, GOODS.GOODS_NUMBER, GOODS.GOODS_SALE_NUM)
                .from(GOODS).leftJoin(SORT).on(GOODS.SORT_ID.eq(SORT.SORT_ID)).leftJoin(GOODS_BRAND)
                .on(GOODS.BRAND_ID.eq(GOODS_BRAND.ID)).where(GOODS.DEL_FLAG.eq(DelFlag.NORMAL.getCode())).and(GOODS.GOODS_ID.in(goodsIds))
                .fetchInto(GoodsPageListVo.class);

        GoodsPageListParam pageListParam = new GoodsPageListParam();
        pageListParam.setSelectType(GoodsPageListParam.GOODS_LIST_WITH_PRD);

        this.disposeGoodsPageListVo(goodsPageListVos, pageListParam);

        return goodsPageListVos;
    }

    /**
     * 商品（规格）查询或导出的通用SelectConditionStep，每条记录包含了商品部分信息和必要的规格信息
     *
     * @param goodsPageListParam {@link com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsPageListParam}
     */
    private SelectConditionStep<?> createProductSelect(GoodsPageListParam goodsPageListParam) {
        // 拼接过滤条件
        Condition condition = this.buildOptions(goodsPageListParam);

        SelectConditionStep<?> selectFrom = db().select(GOODS.GOODS_ID, GOODS.GOODS_NAME, GOODS.GOODS_NAME.as("goods_title_name"), GOODS.GOODS_IMG, GOODS.GOODS_SN, GOODS.SHOP_PRICE, GOODS.MARKET_PRICE,
                GOODS.SOURCE, GOODS.GOODS_TYPE, GOODS.CAT_ID, SORT.SORT_NAME, SORT.LEVEL, GOODS.SORT_ID, GOODS.GOODS_AD, GOODS.IS_ON_SALE, GOODS.LIMIT_BUY_NUM, GOODS.GOODS_WEIGHT, GOODS.UNIT,
                GOODS_BRAND.BRAND_NAME, GOODS.GOODS_NUMBER, GOODS.DELIVER_PLACE,
                GOODS_SPEC_PRODUCT.PRD_ID, GOODS_SPEC_PRODUCT.PRD_DESC, GOODS_SPEC_PRODUCT.PRD_PRICE, GOODS_SPEC_PRODUCT.CREATE_TIME,
                GOODS_SPEC_PRODUCT.PRD_NUMBER, GOODS_SPEC_PRODUCT.PRD_SN, GOODS_SPEC_PRODUCT.PRD_COST_PRICE, GOODS_SPEC_PRODUCT.PRD_MARKET_PRICE,
                GOODS_SPEC_PRODUCT.PRD_IMG, GOODS_SPEC_PRODUCT.PRD_CODES)
                .from(GOODS).leftJoin(SORT).on(GOODS.SORT_ID.eq(SORT.SORT_ID)).leftJoin(GOODS_BRAND)
                .on(GOODS.BRAND_ID.eq(GOODS_BRAND.ID)).innerJoin(GOODS_SPEC_PRODUCT).on(GOODS.GOODS_ID.eq(GOODS_SPEC_PRODUCT.GOODS_ID))
                .where(condition);

        // 拼接排序
        selectFrom = this.buildOrderFields(selectFrom, goodsPageListParam);

        return selectFrom;
    }

    /**
     * 商品（规格）分页查询，每条记录包含了商品部分信息和必要的规格信息
     *
     * @param goodsPageListParam {@link com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsPageListParam}
     * @return {@link com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsPageListVo}
     */
    public PageResult<GoodsPageListVo> getProductPageList(GoodsPageListParam goodsPageListParam) {
        PageResult<GoodsPageListVo> pageResult = null;
        if (esUtilSearchService.esState()) {
            try {
                pageResult = esGoodsSearchService.searchGoodsPageForProduct(goodsPageListParam);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            SelectConditionStep<?> selectFrom = this.createProductSelect(goodsPageListParam);

            pageResult = this.getPageResult(selectFrom, goodsPageListParam.getCurrentPage(),
                    goodsPageListParam.getPageRows(), GoodsPageListVo.class);

            // 结果集标签、平台分类、规格信息二次处理
            this.disposeGoodsPageListVo(pageResult.getDataList(), goodsPageListParam);
        }
        return pageResult;
    }

    /**
     * 获取符合条件的全部商品规格id集合
     *
     * @param goodsPageListParam {@link com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsPageListParam}
     * @return 规格id集合
     */
    public List<Integer> getProductIdsListAll(GoodsPageListParam goodsPageListParam) {
        // 拼接过滤条件
        Condition condition = this.buildOptions(goodsPageListParam);

        List<Integer> prdIds = db().select(GOODS_SPEC_PRODUCT.PRD_ID)
                .from(GOODS).leftJoin(SORT).on(GOODS.SORT_ID.eq(SORT.SORT_ID)).leftJoin(GOODS_BRAND)
                .on(GOODS.BRAND_ID.eq(GOODS_BRAND.ID)).innerJoin(GOODS_SPEC_PRODUCT).on(GOODS.GOODS_ID.eq(GOODS_SPEC_PRODUCT.GOODS_ID))
                .where(condition).fetch(GOODS_SPEC_PRODUCT.PRD_ID);

        return prdIds;
    }

    /**
     * 分页条件拼凑
     *
     * @param goodsPageListParam 过滤条件
     * @return where 过滤结果对象
     */
    private Condition buildOptions(GoodsPageListParam goodsPageListParam) {
        Condition condition = DSL.noCondition();
        condition = condition.and(GOODS.DEL_FLAG.eq(DelFlag.NORMAL.getCode()));
        // 处理在售状态
        condition = this.buildIsOnSaleOptions(condition, goodsPageListParam);

        // 售罄goodsName内容需要匹配：商品名称，货号，规格编码(查询规格列表时才进行匹配)
        if (!StringUtils.isBlank(goodsPageListParam.getGoodsName())) {
            if (GoodsConstant.SALE_OUT.equals(goodsPageListParam.getIsSaleOut())) {
                String likeStr = likeValue(goodsPageListParam.getGoodsName());
                Condition nameCondition = GOODS.GOODS_NAME.like(likeStr).or(GOODS.GOODS_SN.like(likeStr)).or(GOODS_SPEC_PRODUCT.PRD_SN.like(likeStr));
                condition = condition.and(nameCondition);
            } else {
                condition = condition.and(GOODS.GOODS_NAME.like(likeValue(goodsPageListParam.getGoodsName())));
            }
        }
        if (goodsPageListParam.getGoodsIds() != null && goodsPageListParam.getGoodsIds().size() > 0) {
            condition = condition.and(GOODS.GOODS_ID.in(goodsPageListParam.getGoodsIds()));
        }

        if (goodsPageListParam.getNotIncludeGoodsIds() != null && goodsPageListParam.getNotIncludeGoodsIds().size() > 0) {
            condition = condition.and(GOODS.GOODS_ID.notIn(goodsPageListParam.getNotIncludeGoodsIds()));
        }
        if (!StringUtils.isBlank(goodsPageListParam.getGoodsSn())) {
            condition = condition.and(GOODS.GOODS_SN.like(likeValue(goodsPageListParam.getGoodsSn())));
        }
        if (goodsPageListParam.getBrandId() != null) {
            condition = condition.and(GOODS.BRAND_ID.eq(goodsPageListParam.getBrandId()));
        }
        if (goodsPageListParam.getSource() != null) {
            condition = condition.and(GOODS.SOURCE.eq(goodsPageListParam.getSource()));
        }
        if (goodsPageListParam.getGoodsType() != null) {
            condition = condition.and(GOODS.GOODS_TYPE.eq(goodsPageListParam.getGoodsType()));
        }
        // 处理平台和商家分类
        condition = this.buildSortCatOpitons(condition, goodsPageListParam);
        // 处理标签分类
        condition = this.buildLabelOptions(condition, goodsPageListParam);

        if (goodsPageListParam.getSaleTimeStart() != null) {
            condition = condition.and(GOODS.SALE_TIME.ge(goodsPageListParam.getSaleTimeStart()));
        }
        if (goodsPageListParam.getSaleTimeEnd() != null) {
            condition = condition.and(GOODS.SALE_TIME.le(goodsPageListParam.getSaleTimeEnd()));
        }
        // 处理价格过滤方式
        condition = this.buildShopPriceOptions(condition, goodsPageListParam);

        return condition;
    }

    /**
     * 处理商品在售（上下架）状态
     * 出售中和已售罄都可以为上架状态（isOnSale = 1），而仓库中表示下架（isOnSale = 0）
     * 出售中：isOnSale=1&&goodsNumber!=0,已售罄：isOnSale=1&&goodsNumber==0
     * 仓库中：isOnSale=0
     *
     * @param condition          已有过滤条件
     * @param goodsPageListParam goodsPageListParam 过滤条件
     * @return condition 拼装后的过滤条件
     */
    private Condition buildIsOnSaleOptions(Condition condition, GoodsPageListParam goodsPageListParam) {
        // 查询在售
        if (GoodsConstant.ON_SALE.equals(goodsPageListParam.getIsOnSale())) {
            condition = condition.and(GOODS.IS_ON_SALE.eq(GoodsConstant.ON_SALE));
        }
        // 查询仓库中（下架）
        if (GoodsConstant.OFF_SALE.equals(goodsPageListParam.getIsOnSale())) {
            condition = condition.and(GOODS.IS_ON_SALE.eq(GoodsConstant.OFF_SALE));
        }

        // 查询商品列表的售罄
        if (GoodsConstant.SALE_OUT.equals(goodsPageListParam.getIsSaleOut()) && GoodsPageListParam.GOODS_LIST.equals(goodsPageListParam.getSelectType())) {
            condition = condition.and(GOODS.GOODS_NUMBER.eq(0));
        }
        // 查询商品列表的未售罄
        if (GoodsConstant.NOT_SALE_OUT.equals(goodsPageListParam.getIsSaleOut()) && GoodsPageListParam.GOODS_LIST.equals(goodsPageListParam.getSelectType())) {
            condition = condition.and(GOODS.GOODS_NUMBER.ne(0));
        }
        // 查询规格列表的售罄
        if (GoodsConstant.SALE_OUT.equals(goodsPageListParam.getIsSaleOut()) && GoodsPageListParam.GOODS_PRD_LIST.equals(goodsPageListParam.getSelectType())) {
            condition = condition.and(GOODS_SPEC_PRODUCT.PRD_NUMBER.eq(0));
        }
        // 查询规格列表的未售罄
        if (GoodsConstant.NOT_SALE_OUT.equals(goodsPageListParam.getIsSaleOut()) && GoodsPageListParam.GOODS_PRD_LIST.equals(goodsPageListParam.getSelectType())) {
            condition = condition.and(GOODS_SPEC_PRODUCT.PRD_NUMBER.ne(0));
        }
        return condition;
    }

    /**
     * 处理平台分类和商家分类过滤条件
     *
     * @param condition          已有过滤条件
     * @param goodsPageListParam goodsPageListParam 过滤条件
     * @return condition 拼装后的过滤条件
     */
    private Condition buildSortCatOpitons(Condition condition, GoodsPageListParam goodsPageListParam) {
        // 平台分类，首先需要根据当前平台分类id查询出所有的子孙节点
//        if (goodsPageListParam.getCatId() != null) {
//            List<Integer> catIds = new ArrayList<>();
//            catIds.add(goodsPageListParam.getCatId());
//            List<Integer> childrenId = saas.sysCate.findChildrenByParentId(catIds);
//            condition = condition.and(GOODS.CAT_ID.in(childrenId));
//        }
        // 商家分类
        if (goodsPageListParam.getSortId() != null) {
            List<Integer> childrenId = goodsSort.getChildrenIdByParentIdsDao(Collections.singletonList(goodsPageListParam.getSortId()));
            condition = condition.and(GOODS.SORT_ID.in(childrenId));
        }
        return condition;
    }

    /**
     * 处理标签过滤条件
     *
     * @param condition          已有过滤条件
     * @param goodsPageListParam goodsPageListParam 滤条件
     * @return condition 拼装后的过滤条件
     */
    private Condition buildLabelOptions(Condition condition, GoodsPageListParam goodsPageListParam) {
        if (goodsPageListParam.getLabelId() != null) {
            // 根据标签id值过滤出该标签对应的商品id或者平台、商家分类id
            Map<Byte, List<Integer>> byteListMap = goodsLabelCouple.selectGatIdsByLabelIds(Arrays.asList(goodsPageListParam.getLabelId()));
            Condition labelCondition = DSL.falseCondition();
            // 处理标签直接打在商品上的情况
            List<Integer> integers = byteListMap.get(GoodsLabelCoupleTypeEnum.GOODSTYPE.getCode());
            if (integers != null && integers.size() > 0) {
                labelCondition = labelCondition.or(GOODS.GOODS_ID.in(integers));
            }
            // 处理标签打在商家分类上
            integers = byteListMap.get(GoodsLabelCoupleTypeEnum.SORTTYPE.getCode());
            if (integers != null && integers.size() > 0) {
                List<Integer> sortChildrenId = goodsSort.getChildrenIdByParentIdsDao(integers);
                labelCondition = labelCondition.or(GOODS.SORT_ID.in(sortChildrenId));
            }
            // 处理标签打在平台分类上
//            integers = byteListMap.get(GoodsLabelCoupleTypeEnum.CATTYPE.getCode());
//            if (integers != null && integers.size() > 0) {
//                List<Integer> catChildrenId = saas.sysCate.findChildrenByParentId(integers);
//                labelCondition = labelCondition.or(GOODS.CAT_ID.in(catChildrenId));
//            }
            condition = condition.and(labelCondition);
        }
        return condition;
    }

    /**
     * 处理价格过滤条件
     * 当{@link com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsPageListParam#getSelectType()} 为2时价格按照规格价格进行筛选，否则按照商品价格进行筛选
     *
     * @param condition          已有过滤条件
     * @param goodsPageListParam goodsPageListParam 带过滤条件的待执行语句
     * @return condition 拼装后的过滤条件
     */
    private Condition buildShopPriceOptions(Condition condition, GoodsPageListParam goodsPageListParam) {
        if (GoodsPageListParam.GOODS_PRD_LIST.equals(goodsPageListParam.getSelectType())) {
            if (goodsPageListParam.getLowShopPrice() != null) {
                condition = condition.and(GOODS_SPEC_PRODUCT.PRD_PRICE.ge(goodsPageListParam.getLowShopPrice()));
            }
            if (goodsPageListParam.getHighShopPrice() != null) {
                condition = condition.and(GOODS_SPEC_PRODUCT.PRD_PRICE.le(goodsPageListParam.getHighShopPrice()));
            }
        } else {
            if (goodsPageListParam.getLowShopPrice() != null) {
                condition = condition.and(GOODS.SHOP_PRICE.ge(goodsPageListParam.getLowShopPrice()));
            }
            if (goodsPageListParam.getHighShopPrice() != null) {
                condition = condition.and(GOODS.SHOP_PRICE.le(goodsPageListParam.getHighShopPrice()));
            }
        }
        return condition;
    }

    /**
     * 排序条件过滤
     *
     * @param scs                待排序语句
     * @param goodsPageListParam
     * @return
     */
    private SelectConditionStep<?> buildOrderFields(SelectConditionStep<?> scs, GoodsPageListParam goodsPageListParam) {
        // 筛选排序条件，默认是根据创建时间进行排序
        String orderField = goodsPageListParam.getOrderField();
        String orderDire = goodsPageListParam.getOrderDirection();

        if (StringUtils.isBlank(orderField)) {
            scs.orderBy(GOODS.CREATE_TIME.desc());
            return scs;
        }

        if (GoodsPageListParam.SHOP_PRICE.equals(orderField)) {
            if (ASC.equals(orderDire)) {
                scs.orderBy(GOODS.SHOP_PRICE.asc(), GOODS.CREATE_TIME.desc());
            } else {
                scs.orderBy(GOODS.SHOP_PRICE.desc(), GOODS.CREATE_TIME.desc());
            }
        }

        if (GoodsPageListParam.GOODS_NUMBER.equals(orderField)) {
            if (ASC.equals(orderDire)) {
                scs.orderBy(GOODS.GOODS_NUMBER.asc(), GOODS.CREATE_TIME.desc());
            } else {
                scs.orderBy(GOODS.GOODS_NUMBER.desc(), GOODS.CREATE_TIME.desc());
            }
        }

        if (GoodsPageListParam.GOODS_SALE_NUM.equals(orderField)) {
            if (ASC.equals(orderDire)) {
                scs.orderBy(GOODS.GOODS_SALE_NUM.asc(), GOODS.CREATE_TIME.desc());
            } else {
                scs.orderBy(GOODS.GOODS_SALE_NUM.desc(), GOODS.CREATE_TIME.desc());
            }
        }
        return scs;
    }

    /**
     * 处理商品或规格分页查询结果，设置对应的标签、平台分类、商品规格信息等
     *
     * @param dataList      分页结果集
     * @param pageListParam 数据筛选条件
     */
    protected void disposeGoodsPageListVo(List<GoodsPageListVo> dataList, GoodsPageListParam pageListParam) {

        // 处理商品平台分类：通过id值获取name值
//        saas.sysCate.disposeCategoryName(dataList);

        // 处理标签名称准备数据
        List<Integer> goodsIds = new ArrayList<>(dataList.size());
        List<Integer> sortIds = new ArrayList<>(dataList.size());
        for (GoodsPageListVo goodsPageListVo : dataList) {
            goodsIds.add(goodsPageListVo.getGoodsId());
            sortIds.add(goodsPageListVo.getSortId());
        }
        sortIds = goodsSort.getChildrenIdByParentIdsDao(sortIds);

        Map<Integer, List<GoodsLabelSelectListVo>> goodsPointLabels = goodsLabel.getGtaLabelMap(goodsIds, GoodsLabelCoupleTypeEnum.GOODSTYPE);
        Map<Integer, List<GoodsLabelSelectListVo>> goodsSortLabels = goodsLabel.getGtaLabelMap(sortIds, GoodsLabelCoupleTypeEnum.SORTTYPE);
        List<GoodsLabelSelectListVo> allGoodsLabels = goodsLabel.getAllGoodsLabels();

        // 获取商品对应的规格集合数据
        Map<Integer, List<GoodsSpecProduct>> goodsIdPrdGroups = goodsSpecProductService.selectGoodsSpecPrdGroup(goodsIds);

        dataList.forEach(item -> {
            // 设置指定标签
            if (goodsPointLabels.get(item.getGoodsId()) != null && goodsPointLabels.get(item.getGoodsId()).size() > 0) {
                item.getGoodsPointLabels().addAll(goodsPointLabels.get(item.getGoodsId()));
            }
            // 设置普通标签
            if (goodsSortLabels.get(item.getSortId()) != null && goodsSortLabels.get(item.getSortId()).size() > 0) {
                item.getGoodsNormalLabels().addAll(goodsSortLabels.get(item.getSortId()));
            }
            if (allGoodsLabels.size() > 0) {
                item.getGoodsNormalLabels().addAll(allGoodsLabels);
            }

            // 设置图片绝对地址
            item.setGoodsImg(getImgFullUrlUtil(item.getGoodsImg()));
            item.setPrdImg(getImgFullUrlUtil(item.getPrdImg()));
            // 处理商品对应的规格数据,由于分页问题导致无法连表查询
            List<GoodsSpecProduct> goodsSpecProducts = goodsIdPrdGroups.get(item.getGoodsId());
            this.disposeGoodsSpecProductsInfo(item, goodsSpecProducts, pageListParam);
        });
    }

    /**
     * 为商品设置相应的规格信息
     *
     * @param goods             {@link com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsPageListVo}
     * @param goodsSpecProducts 商品对应的{@link com.meidianyi.shop.service.pojo.shop.goods.spec.GoodsSpecProduct}集合
     */
    private void disposeGoodsSpecProductsInfo(GoodsPageListVo goods, List<GoodsSpecProduct> goodsSpecProducts, GoodsPageListParam pageListParam) {
        // 这种情况一般是手动修改了数据库数据但是存在错误
        if (goodsSpecProducts == null || goodsSpecProducts.size() == 0) {
            return;
        }
        // 默认规格
        if (goodsSpecProducts.size() == 1 && StringUtils.isBlank(goodsSpecProducts.get(0).getPrdSpecs())) {
            GoodsSpecProduct prd = goodsSpecProducts.get(0);
            goods.setPrdId(prd.getPrdId());
            goods.setPrdMaxShopPrice(prd.getPrdPrice());
            goods.setPrdMinShopPrice(prd.getPrdPrice());
            goods.setPrdTypeNum(0);
            goods.setIsDefaultPrd(true);
        } else {
            BigDecimal maxPrice = BigDecimal.valueOf(-1);
            BigDecimal minPrice = BigDecimal.valueOf(Double.MAX_VALUE);
            for (GoodsSpecProduct goodsSpecProduct : goodsSpecProducts) {
                if (maxPrice.compareTo(goodsSpecProduct.getPrdPrice()) < 0) {
                    maxPrice = goodsSpecProduct.getPrdPrice();
                }
                if (minPrice.compareTo(goodsSpecProduct.getPrdPrice()) > 0) {
                    minPrice = goodsSpecProduct.getPrdPrice();
                }
            }
            goods.setPrdMaxShopPrice(maxPrice);
            goods.setPrdMinShopPrice(minPrice);
            goods.setPrdTypeNum(goodsSpecProducts.size());
            goods.setIsDefaultPrd(false);

            if (GoodsPageListParam.GOODS_LIST_WITH_PRD.equals(pageListParam.getSelectType())) {
                goods.setGoodsSpecProducts(goodsSpecProducts);
            }
        }
    }

    /**
     * 规格
     *
     * @param goodsId 商品ID
     * @return GoodsProductVo
     */
    public List<GoodsProductVo> getAllProductListByGoodsId(Integer goodsId) {
        return goodsSpecProductService.getAllProductListByGoodsId(goodsId).into(GoodsProductVo.class);
    }

    /**
     * @param ids 商品ID列表
     * @return 商品id列表
     * @author 黄荣刚
     */
    public List<GoodsView> selectGoodsViewList(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        List<GoodsView> goodsViewList = db().select(GOODS.GOODS_ID, GOODS.GOODS_NAME, GOODS.GOODS_IMG,
                GOODS.GOODS_NUMBER, GOODS.SHOP_PRICE, GOODS.UNIT).from(GOODS).where(GOODS.GOODS_ID.in(ids))
                .fetchInto(GoodsView.class);
        goodsViewList.forEach(item -> item.setGoodsImg(getImgFullUrlUtil(item.getGoodsImg())));
        return goodsViewList;
    }

    /**
     * 取单个GoodsView
     */
    public GoodsView getGoodsView(Integer goodsId) {
        GoodsView goodsView = db().select(GOODS.GOODS_ID, GOODS.GOODS_NAME, GOODS.GOODS_IMG, GOODS.GOODS_NUMBER, GOODS.SHOP_PRICE, GOODS.UNIT).
                from(GOODS).where(GOODS.GOODS_ID.eq(goodsId)).
                fetchOne().into(GoodsView.class);
        goodsView.setGoodsImg(getImgFullUrlUtil(goodsView.getGoodsImg()));
        return goodsView;
    }

    /**
     * 取单个GoodsView
     */
    public GoodsView getGoodsViewByProductId(Integer productId) {
        Record6<Integer, String, String, Integer, BigDecimal, String> record = db().select(GOODS.GOODS_ID, GOODS.GOODS_NAME, GOODS.GOODS_IMG, GOODS.GOODS_NUMBER, GOODS.SHOP_PRICE, GOODS.UNIT).
                from(GOODS).innerJoin(GOODS_SPEC_PRODUCT).on(GOODS_SPEC_PRODUCT.GOODS_ID.eq(GOODS.GOODS_ID))
                .where(GOODS_SPEC_PRODUCT.PRD_ID.eq(productId)).
                        fetchOne();
        if (record != null) {
            GoodsView goodsView = record.into(GoodsView.class);
            goodsView.setGoodsImg(getImgFullUrlUtil(goodsView.getGoodsImg()));
            return goodsView;
        }
        return null;
    }

    /**
     * 取单个GoodsSmallVo
     */
    public GoodsSmallVo getGoodsSmallVo(Integer goodsId) {
        GoodsSmallVo goods = db().selectFrom(GOODS).where(GOODS.GOODS_ID.eq(goodsId)).
                fetchAnyInto(GoodsSmallVo.class);
        goods.setGoodsImg(getImgFullUrlUtil(goods.getGoodsImg()));
        return goods;
    }

    /**
     * 根据id获取商品运费模板id
     *
     * @param goodsId 商品id
     * @return id or null
     */
    public Integer getGoodsDeliverTemplateIdById(Integer goodsId) {
        return db().select(GOODS.DELIVER_TEMPLATE_ID).from(GOODS).where(GOODS.GOODS_ID.eq(goodsId)).fetchAny(GOODS.DELIVER_TEMPLATE_ID);
    }

    /**
     * 获取所有商品所关联的有效品牌id集合
     *
     * @return 品牌id集合
     */
    public List<Integer> getGoodsBrandIds() {
        Condition condition = GOODS.DEL_FLAG.eq(DelFlag.NORMAL.getCode())
                .and(Tables.GOODS.IS_ON_SALE.eq(GoodsConstant.ON_SALE)).and(GOODS.BRAND_ID.ne(GoodsConstant.GOODS_DEFAULT_BRAND_ID));
        // 是否展示售罄
        Byte soldOutGoods = configService.shopCommonConfigService.getSoldOutGoods();
        // 不展示售罄商品
        if (!GoodsConstant.SOLD_OUT_GOODS_SHOW.equals(soldOutGoods)) {
            condition = condition.and(GOODS.GOODS_NUMBER.gt(0));
        }

        return db().select(GOODS.BRAND_ID).from(GOODS).where(condition).fetch(GOODS.BRAND_ID);
    }


    /**
     * 添加商品无锁函数
     * 先插入商品，从而得到商品的id， 然后插入商品规格的属性和规格值，
     * 从而得到规格属性和规格值的id, 最后拼凑出prdSpecs再插入具体的商品规格
     *
     * @param goods 商品信息
     */
    public GoodsDataIllegalEnumWrap insert(Goods goods) {
        GoodsDataIllegalEnumWrap codeWrap = new GoodsDataIllegalEnumWrap();
        codeWrap.setIllegalEnum(GoodsDataIIllegalEnum.GOODS_OK);
        try {
            transaction(() ->
            {
                insertGoods(goods);

                // 商品图片增加
                if (goods.getGoodsImgs() != null && goods.getGoodsImgs().size() != 0) {
                    insertGoodsImgs(goods.getGoodsImgs(), goods.getGoodsId());
                }

                // 商品关联标签添加
                if (goods.getGoodsLabels() != null && goods.getGoodsLabels().size() != 0) {
                    insertGoodsLabels(goods.getGoodsLabels(), goods.getGoodsId());
                }

                // 商品规格处理
                goodsSpecProductService.insert(goods.getGoodsSpecProducts(), goods.getGoodsSpecs(), goods.getGoodsId());

                //插入商品规格对应的会员卡价格
                insertGradePrd(goods.getGoodsGradePrds(), goods.getGoodsSpecProducts(), goods.getGoodsId());
                //插入商品专属会员信息
                insertMemberCards(goods);

                //插入商品分销改价信息
                insertGoodsRebatePrices(goods.getGoodsRebatePrices(), goods.getGoodsSpecProducts(), goods.getGoodsId());
                codeWrap.setGoodsId(goods.getGoodsId());
            });
        } catch (Exception e) {
            codeWrap.setIllegalEnum(GoodsDataIIllegalEnum.GOODS_DATA_ILLEGAL_FORMAT_FOR_DB);
            logger().warn("商品新增error:" + e.getMessage());
        }
        return codeWrap;
    }

    /**
     * 插入数据并设置对应入参的id值
     *
     * @param goods {@link com.meidianyi.shop.service.pojo.shop.goods.goods}
     */
    private void insertGoods(Goods goods) {
        //计算商品的价格和库存量
        calculateGoodsPriceWeightAndNumber(goods);

        if (StringUtils.isBlank(goods.getGoodsSn())) {
            int count = db().fetchCount(GOODS) + 1;
            String timeStr = DateUtils.getLocalDateFullTightFormat();
            goods.setGoodsSn(String.format("G%s-%08d", timeStr, count));
        }

        // 设置商品分享海报配置信息
        setGoodsShareConfig(goods);

        GoodsRecord goodsRecord = db().newRecord(GOODS, goods);
        goodsRecord.insert();
        goods.setGoodsId(goodsRecord.getGoodsId());
    }

    /**
     * 设置商品分享海报配置信息
     *
     * @param goods {@link com.meidianyi.shop.service.pojo.shop.goods.goods}
     */
    private void setGoodsShareConfig(Goods goods) {
        //商品海报分享设置
        if (goods.getGoodsSharePostConfig() == null) {
            GoodsSharePostConfig goodsSharePostConfig = new GoodsSharePostConfig();
            goodsSharePostConfig.setShareAction(GoodsSharePostConfig.DEFAULT_ACTION);
            goodsSharePostConfig.setShareImgAction(GoodsSharePostConfig.DEFAULT_SHARE_IMG_ACTION);
            goods.setGoodsSharePostConfig(goodsSharePostConfig);
        }
        goods.setShareConfig(Util.toJson(goods.getGoodsSharePostConfig()));
    }

    /**
     * 插入商品专属会员卡
     *
     * @param goods
     */
    private void insertMemberCards(Goods goods) {
        if (goods.getIsCardExclusive() == null || goods.getIsCardExclusive() == 0 || goods.getMemberCardIds() == null || goods.getMemberCardIds().size() == 0) {
            return;
        }
        memberCardService.batchUpdateGoods(Arrays.asList(goods.getGoodsId()), goods.getMemberCardIds(), CardConstant.COUPLE_TP_GOODS);
    }

    /**
     * 插入商品规格对应的会员卡价格
     *
     * @param goodsGradePrds    商品规格对应会员卡
     * @param goodsSpecProducts 商品规格
     * @param goodsId           商品id
     */
    private void insertGradePrd(List<GoodsGradePrd> goodsGradePrds, List<GoodsSpecProduct> goodsSpecProducts, Integer goodsId) {

        if (goodsGradePrds == null || goodsGradePrds.size() == 0) {
            return;
        }

        Map<String, Integer> collect = goodsSpecProducts.stream().collect(Collectors.toMap(GoodsSpecProduct::getPrdDesc, GoodsSpecProduct::getPrdId));
        DefaultDSLContext db = db();
        List<GradePrdRecord> gradePrdRecords = goodsGradePrds.stream().map(goodsGradePrd -> {
            GradePrdRecord gradePrdRecord = db.newRecord(GRADE_PRD, goodsGradePrd);
            gradePrdRecord.setGoodsId(goodsId);
            gradePrdRecord.setPrdId(collect.get(goodsGradePrd.getPrdDesc()));
            return gradePrdRecord;
        }).collect(Collectors.toList());

        db.batchInsert(gradePrdRecords).execute();
    }

    /**
     * 插入商品分销改价信息
     *
     * @param goodsRebatePrices
     * @param goodsSpecProducts
     * @param goodsId
     */
    private void insertGoodsRebatePrices(List<GoodsRebatePrice> goodsRebatePrices, List<GoodsSpecProduct> goodsSpecProducts, Integer goodsId) {

        if (goodsRebatePrices == null || goodsRebatePrices.size() == 0) {
            return;
        }

        Map<String, Integer> collect = goodsSpecProducts.stream().collect(Collectors.toMap(GoodsSpecProduct::getPrdDesc, GoodsSpecProduct::getPrdId));
        DefaultDSLContext db = db();
        List<GoodsRebatePriceRecord> goodsRebatePriceRecords = goodsRebatePrices.stream().map(goodsRebatePrice -> {
            GoodsRebatePriceRecord goodsRebatePriceRecord = db.newRecord(GOODS_REBATE_PRICE, goodsRebatePrice);
            goodsRebatePriceRecord.setGoodsId(goodsId);
            goodsRebatePriceRecord.setProductId(collect.get(goodsRebatePrice.getPrdDesc()));
            return goodsRebatePriceRecord;
        }).collect(Collectors.toList());

        db.batchInsert(goodsRebatePriceRecords).execute();
    }

    /**
     * 商品图片插入
     *
     * @param goodsImgs 商品图片地址列表
     * @param goodsId   商品id
     */
    private void insertGoodsImgs(List<String> goodsImgs, Integer goodsId) {

        InsertValuesStep2<GoodsImgRecord, Integer, String> insertInto = db().insertInto(GOODS_IMG, GOODS_IMG.GOODS_ID,
                GOODS_IMG.IMG_URL);

        for (String imgUrl : goodsImgs) {
            insertInto.values(goodsId, imgUrl);
        }

        insertInto.execute();
    }

    /**
     * 商品标签插入
     *
     * @param goodsLabels 标签id列表
     * @param goodsId     商品id
     */
    private void insertGoodsLabels(List<Integer> goodsLabels, Integer goodsId) {

        List<GoodsLabelCouple> list = new ArrayList<>(goodsLabels.size());

        for (Integer labelId : goodsLabels) {
            list.add(new GoodsLabelCouple(null, labelId, goodsId, GoodsLabelCoupleTypeEnum.GOODSTYPE.getCode()));
        }
        goodsLabelCouple.batchInsert(list);
    }

    /**
     * 预处理通过规格信息计算出商品的库存，最小商品价格信息, 并将结果注入到传入的引用对象。
     *
     * @param goods
     */
    private void calculateGoodsPriceWeightAndNumber(Goods goods) {
        // 当存在商品规格时，统计商品总数和最低商品价格
        BigDecimal smallestGoodsPrice = BigDecimal.valueOf(Double.MAX_VALUE);
        BigDecimal largestMarketPrice = BigDecimal.valueOf(Double.MIN_VALUE);
        BigDecimal smallestCostPrice = BigDecimal.valueOf(Double.MAX_VALUE);
        BigDecimal smallestGoodsWeight = BigDecimal.valueOf(Double.MAX_VALUE);

        Integer goodsSumNumber = 0;
        for (GoodsSpecProduct specProduct : goods.getGoodsSpecProducts()) {
            goodsSumNumber += specProduct.getPrdNumber();
            if (smallestGoodsPrice.compareTo(specProduct.getPrdPrice()) > 0) {
                smallestGoodsPrice = specProduct.getPrdPrice();
            }
            if (specProduct.getPrdMarketPrice() != null && largestMarketPrice.compareTo(specProduct.getPrdMarketPrice()) < 0) {
                largestMarketPrice = specProduct.getPrdMarketPrice();
            }
            if (specProduct.getPrdCostPrice() != null && smallestCostPrice.compareTo(specProduct.getPrdCostPrice()) > 0) {
                smallestCostPrice = specProduct.getPrdCostPrice();
            }
            if (specProduct.getPrdWeight() != null && smallestGoodsWeight.compareTo(specProduct.getPrdWeight()) > 0) {
                smallestGoodsWeight = specProduct.getPrdWeight();
            }

        }
        goods.setGoodsNumber(goodsSumNumber);
        goods.setShopPrice(smallestGoodsPrice);
        goods.setMarketPrice(BigDecimal.valueOf(Double.MIN_VALUE).compareTo(largestMarketPrice) == 0 ? null : largestMarketPrice);
        goods.setCostPrice(BigDecimal.valueOf(Double.MAX_VALUE).compareTo(smallestCostPrice) == 0 ? null : smallestCostPrice);
        goods.setGoodsWeight(BigDecimal.valueOf(Double.MAX_VALUE).compareTo(smallestGoodsWeight) == 0 ? null : smallestGoodsWeight);
    }


    /**
     * 多规格商品修改数量
     *
     * @param param
     */
    public void updateGoodsPrdNumbers(GoodsPrdNumEditParam param) {

        GoodsRecord goodsRecord = new GoodsRecord();
        goodsRecord.setGoodsId(param.getGoodsId());
        int goodsNum = 0;
        List<GoodsSpecProductRecord> prdList = new ArrayList<>(param.getPrdNumInfos().size());
        for (PrdPriceNumberParam prdNumInfo : param.getPrdNumInfos()) {
            GoodsSpecProductRecord record = new GoodsSpecProductRecord();
            record.setPrdId(prdNumInfo.getPrdId());
            record.setPrdNumber(prdNumInfo.getPrdNumber());
            goodsNum += prdNumInfo.getPrdNumber();
            prdList.add(record);
        }
        goodsRecord.setGoodsNumber(goodsNum);

        transaction(() -> {
            db().batchUpdate(prdList).execute();
            db().executeUpdate(goodsRecord);
        });
        //es更新
        try {
            if (esUtilSearchService.esState()) {
                esGoodsCreateService.updateEsGoodsIndex(param.getGoodsId(), getShopId());
            }
        } catch (Exception e) {
            logger().debug("多商品修改数量-同步es数据异常：" + e.getMessage());
        }
    }

    /**
     * 商品修改
     *
     * @param goods
     */
    public GoodsDataIllegalEnumWrap update(Goods goods) {
        GoodsDataIllegalEnumWrap codeWrap = new GoodsDataIllegalEnumWrap();

        try {
            transaction(() -> {
                //存在重复值则直接返回
                GoodsDataIIllegalEnum goodsDataIllegalEnum = columnValueExistCheckForUpdate(goods);
                codeWrap.setIllegalEnum(goodsDataIllegalEnum);
                if (!GoodsDataIIllegalEnum.GOODS_OK.equals(codeWrap.getIllegalEnum())) {
                    return;
                }

                updateGoods(goods);
                // 商品图片修改
                updateImgs(goods);
                // 商品关联标签添加
                updateLabels(goods);
                // 修改商品规格
                updateSpecPrd(goods);
                // 修改商品规格会员价
                updateGradePrd(goods.getGoodsGradePrds(), goods.getGoodsSpecProducts(), goods.getGoodsId());
                // 修改专属会员卡
                updateMemberCards(goods);
                //修改分销改价
                updateGoodsRebatePrices(goods.getGoodsRebatePrices(), goods.getGoodsSpecProducts(), goods.getGoodsId());
            });
        } catch (Exception e) {
            logger().debug("商品修改error:" + e.getMessage());
            codeWrap.setIllegalEnum(GoodsDataIIllegalEnum.GOODS_DATA_ILLEGAL_FORMAT_FOR_DB);
        }

        if (!GoodsDataIIllegalEnum.GOODS_OK.equals(codeWrap.getIllegalEnum())) {
            return codeWrap;
        }
        //es更新
        try {
            updateEs(goods.getGoodsId());
        } catch (Exception e) {
            logger().debug("商品修改-同步es数据异常：" + e.getMessage());
            codeWrap.setIllegalEnum(GoodsDataIIllegalEnum.GOODS_FAIL);
            return codeWrap;
        }
        return codeWrap;
    }

    /**
     * 修改商品表
     *
     * @param goods {@link com.meidianyi.shop.service.pojo.shop.goods.goods}
     */
    private void updateGoods(Goods goods) {
        //计算商品的价格和库存量
        calculateGoodsPriceWeightAndNumber(goods);

        //设置商品分享海报配置信息
        setGoodsShareConfig(goods);

        GoodsRecord goodsRecord = db().fetchOne(GOODS, GOODS.GOODS_ID.eq(goods.getGoodsId()));

        assign(goods, goodsRecord);
        if (goods.getRoomId() == null) {
            goodsRecord.setRoomId(null);
        }
        if (goods.getMarketPrice() == null) {
            goodsRecord.setMarketPrice(null);
        }
        if (goods.getGoodsWeight() == null) {
            goodsRecord.setGoodsWeight(null);
        }
        goodsRecord.store();
    }

    /**
     * 修改商品规格对应的会员卡价格，由于是一种关联表，每次修改可以先删除旧的然后再新增
     *
     * @param goodsGradePrds    商品规格对应会员卡
     * @param goodsSpecProducts 商品规格
     * @param goodsId           商品id
     */
    private void updateGradePrd(List<GoodsGradePrd> goodsGradePrds, List<GoodsSpecProduct> goodsSpecProducts, Integer goodsId) {
        // 删除旧的项，
        db().update(GRADE_PRD).set(GRADE_PRD.DEL_FLAG, DelFlag.DISABLE.getCode()).where(GRADE_PRD.GOODS_ID.eq(goodsId)).execute();
        insertGradePrd(goodsGradePrds, goodsSpecProducts, goodsId);
    }

    /**
     * 修改商品专属会员卡
     *
     * @param goods
     */
    private void updateMemberCards(Goods goods) {
        //删除关联会员专属信息
        memberCardService.deleteOwnEnjoyGoodsByGcta(Arrays.asList(goods.getGoodsId()), CardConstant.COUPLE_TP_GOODS);
        insertMemberCards(goods);
    }

    /**
     * 插入商品分销改价信息
     *
     * @param goodsRebatePrices
     * @param goodsSpecProducts
     * @param goodsId
     */
    private void updateGoodsRebatePrices(List<GoodsRebatePrice> goodsRebatePrices, List<GoodsSpecProduct> goodsSpecProducts, Integer goodsId) {
        db().update(GOODS_REBATE_PRICE)
                .set(GOODS_REBATE_PRICE.DEL_FLAG, DelFlag.DISABLE.getCode()).where(GOODS_REBATE_PRICE.GOODS_ID.eq(goodsId)).execute();
        insertGoodsRebatePrices(goodsRebatePrices, goodsSpecProducts, goodsId);
    }

    /**
     * 修改商品图片
     *
     * @param goods
     */
    private void updateImgs(Goods goods) {
        List<Integer> goodsIds = Arrays.asList(goods.getGoodsId());

        deleteImg(goodsIds);

        if (goods.getGoodsImgs() != null && goods.getGoodsImgs().size() != 0) {
            insertGoodsImgs(goods.getGoodsImgs(), goods.getGoodsId());
        }
    }

    /**
     * 修改商品标签
     *
     * @param goods
     */
    private void updateLabels(Goods goods) {
        List<Integer> goodsIds = Arrays.asList(goods.getGoodsId());

        goodsLabelCouple.deleteByGoodsIds(goodsIds);

        if (goods.getGoodsLabels() != null && goods.getGoodsLabels().size() != 0) {
            insertGoodsLabels(goods.getGoodsLabels(), goods.getGoodsId());
            goodsLabel.resetLabelIsNone(goods.getGoodsLabels());
        }
    }

    /**
     * 修改商品sku
     *
     * @param goods 商品项
     */
    private void updateSpecPrd(Goods goods) {
        List<GoodsSpecProduct> oldPrds = filterOldGoodsSpecProduct(goods.getGoodsSpecProducts());

        List<GoodsSpecProduct> newPrds = filterNewGoodsSpecProduct(goods.getGoodsSpecProducts());

        // 用户在修该商品的时候删除了部分规格项则并修改了部分规格项，则需要将无效规格从数据库删除，并更新相应规格项
        goodsSpecProductService.updateAndDeleteForGoodsUpdate(oldPrds, goods.getGoodsSpecs(), goods.getGoodsId());

        // 用户从默认规格改为自定义规格或者新增加了规格值或规格项(可能newPrds是空数组，没有新增规格)
        goodsSpecProductService.insertForUpdate(newPrds, goods.getGoodsSpecs(), goods.getGoodsId());
    }

    /**
     * 根据对象是否存prdId值提取出原有的规格对象（用来执行update操作，可能存在被删除项）
     *
     * @param goodsSpecProducts 前端出入的全部规格对象
     * @return 原有的规格对象
     */
    private List<GoodsSpecProduct> filterOldGoodsSpecProduct(List<GoodsSpecProduct> goodsSpecProducts) {
        List<GoodsSpecProduct> oldPrds = goodsSpecProducts.stream()
                .filter(goodsSpecProduct -> goodsSpecProduct.getPrdId() != null).collect(Collectors.toList());
        return oldPrds;
    }

    /**
     * 根据对象是否存prdId值提取出新的规格对象（用来执行insert操作）
     *
     * @param goodsSpecProducts 前端出入的全部规格对象
     * @return 新的规格对象
     */
    private List<GoodsSpecProduct> filterNewGoodsSpecProduct(List<GoodsSpecProduct> goodsSpecProducts) {
        List<GoodsSpecProduct> newPrds = goodsSpecProducts.stream()
                .filter(goodsSpecProduct -> goodsSpecProduct.getPrdId() == null).collect(Collectors.toList());
        return newPrds;
    }

    /**
     * 更新商品Es
     *
     * @param goodsIds
     */
    public void updateEs(List<Integer> goodsIds) {
        try {
            if (esUtilSearchService.esState() && esMappingUpdateService.getEsStatus()) {
                esGoodsCreateService.batchUpdateEsGoodsIndex(goodsIds, getShopId());
                esGoodsLabelCreateService.createEsLabelIndexForGoodsId(goodsIds, DBOperating.UPDATE);
            } else {
                esDataUpdateMqService.addEsGoodsIndex(goodsIds, getShopId(), DBOperating.UPDATE);
                esDataUpdateMqService.updateGoodsLabelByLabelId(getShopId(), DBOperating.UPDATE, goodsIds, null);
            }
        } catch (Exception e) {
            logger().debug("批量更新商品数据-同步es数据异常:" + e.getMessage());
            throw e;
        }
    }

    /**
     * 根据单个商品id同步修改商品es信息
     *
     * @param goodsId
     */
    public void updateEs(Integer goodsId) {
        if (esUtilSearchService.esState() && esMappingUpdateService.getEsStatus()) {
            esGoodsCreateService.updateEsGoodsIndex(goodsId, getShopId());
            esGoodsLabelCreateService.createEsLabelIndexForGoodsId(goodsId);
        }
    }

    /**
     * 删除商品时-同步es操作(非异步操作)
     *
     * @param goodsIds
     */
    public void updateEsDeleteSync(List<Integer> goodsIds) {
        try {
            //es服务正常时,索引同更步新，否则走队列
            if (esUtilSearchService.esState()) {
                esGoodsCreateService.deleteEsGoods(goodsIds, getShopId());
                esGoodsLabelCreateService.createEsLabelIndexForGoodsId(goodsIds, DBOperating.DELETE);
            } else {
                esDataUpdateMqService.addEsGoodsIndex(goodsIds, getShopId(), DBOperating.UPDATE);
                esDataUpdateMqService.updateGoodsLabelByLabelId(getShopId(), DBOperating.UPDATE, goodsIds, null);
            }
        } catch (Exception e) {
            logger().debug("商品删除-es同步数据异常：" + e.getMessage());
        }
    }

    /**
     * 清除指定的sortId
     *
     * @param sortIds
     */
    public void clearSortId(List<Integer> sortIds) {
        db().update(GOODS).set(GOODS.SORT_ID, 0)
                .where(GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(GOODS.SORT_ID.in(sortIds))).execute();
    }

    /**
     * 清除指定的brandId
     *
     * @param brandIds
     */
    public void clearBrandId(List<Integer> brandIds) {
        db().update(GOODS).set(GOODS.BRAND_ID, 0)
                .where(GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(GOODS.BRAND_ID.in(brandIds))).execute();
    }


    public void updateGoodsForImport(GoodsRecord goodsRecord) {
        db().executeUpdate(goodsRecord);
    }

    public void updateGoodsChildImgsForImport(Integer goodsId, List<String> imgs) {
        List<Integer> goodsIds = Arrays.asList(goodsId);

        deleteImg(goodsIds);

        if (imgs != null && imgs.size() != 0) {
            insertGoodsImgs(imgs, goodsId);
        }
    }
    /*************商品相关信息验证代码*************/
    /**
     * 判断商品会员卡价格是否正确
     *
     * @param goods {@link com.meidianyi.shop.service.pojo.shop.goods.goods}
     * @return
     */
    public boolean isGradePrdPriceOk(Goods goods) {
        //判断商品特定等级会员卡的价格是否存在大于对应规格价钱的情况
        if (goods.getGoodsGradePrds() != null && goods.getGoodsGradePrds().size() > 0) {
            Map<String, BigDecimal> collect =
                    goods.getGoodsSpecProducts()
                            .stream()
                            .collect(Collectors.toMap(GoodsSpecProduct::getPrdDesc, GoodsSpecProduct::getPrdPrice));

            boolean r = goods.getGoodsGradePrds().stream().anyMatch(goodsGradePrd -> {
                if (goodsGradePrd.getGradePrice() == null || goodsGradePrd.getGrade() == null) {
                    return true;
                }
                return goodsGradePrd.getGradePrice().compareTo(collect.get(goodsGradePrd.getPrdDesc())) > 0;
            });

            if (r) {
                return false;
            }
        }
        return true;
    }


    /**
     * 商品新增接口数据重复检查（非原子操作）
     *
     * @param goods 商品
     * @return {@link com.meidianyi.shop.common.foundation.data.JsonResult}
     */
    public GoodsDataIIllegalEnum columnValueExistCheckForInsert(Goods goods) {
        GoodsColumnCheckExistParam gcep = new GoodsColumnCheckExistParam();
        gcep.setColumnCheckFor(GoodsColumnCheckExistParam.ColumnCheckForEnum.E_GOODS);

        //检查商品名称是否重复
        gcep.setGoodsName(goods.getGoodsName());
        if (isColumnValueExist(gcep)) {
            return GoodsDataIIllegalEnum.GOODS_NAME_EXIST;
        }
        gcep.setGoodsName(null);

        //用户输入了商品货号则进行检查是否重复
        if (goods.getGoodsSn() != null) {
            gcep.setGoodsSn(goods.getGoodsSn());
            if (isColumnValueExist(gcep)) {
                return GoodsDataIIllegalEnum.GOODS_SN_EXIST;
            }
            gcep.setGoodsSn(null);
        }
        List<String> prdSns = new ArrayList<>(goods.getGoodsSpecProducts().size());
        List<String> prdCodes = new ArrayList<>(goods.getGoodsSpecProducts().size());
        for (GoodsSpecProduct prd : goods.getGoodsSpecProducts()) {
            if (StringUtils.isNotBlank(prd.getPrdSn())) {
                prdSns.add(prd.getPrdSn());
            }
            if (StringUtils.isNotBlank(prd.getPrdCodes())) {
                prdSns.add(prd.getPrdCodes());
            }
        }
        List<String> skuPrdSnExist = goodsSpecProductService.findSkuPrdSnExist(prdSns);
        if (skuPrdSnExist.size() > 0) {
            return GoodsDataIIllegalEnum.GOODS_PRD_SN_EXIST;
        }
        List<String> skuPrdCodesExist = goodsSpecProductService.findSkuPrdCodesExist(prdCodes);
        if (skuPrdCodesExist.size() > 0) {
            return GoodsDataIIllegalEnum.GOODS_PRD_SN_EXIST;
        }

        return GoodsDataIIllegalEnum.GOODS_OK;
    }

    /**
     * 更新数据时判断传入的商品名称、货号，sku码和规格名值是否重复。
     *
     * @param goods 商品
     * @return {@link JsonResult#getError()}!=0表示存在重复
     */
    private GoodsDataIIllegalEnum columnValueExistCheckForUpdate(Goods goods) {
        GoodsColumnCheckExistParam gcep = new GoodsColumnCheckExistParam();
        gcep.setColumnCheckFor(GoodsColumnCheckExistParam.ColumnCheckForEnum.E_GOODS);

        //检查商品名称是否重复
        gcep.setGoodsName(goods.getGoodsName());
        gcep.setGoodsId(goods.getGoodsId());
        if (isColumnValueExist(gcep)) {
            return GoodsDataIIllegalEnum.GOODS_NAME_EXIST;
        }
        gcep.setGoodsName(null);
        gcep.setGoodsId(null);

        //用户输入了商品货号则进行检查是否重复
        if (goods.getGoodsSn() != null) {
            gcep.setGoodsSn(goods.getGoodsSn());
            gcep.setGoodsId(goods.getGoodsId());
            if (isColumnValueExist(gcep)) {
                return GoodsDataIIllegalEnum.GOODS_SN_EXIST;
            }
            gcep.setGoodsSn(null);
            gcep.setGoodsId(null);
        }

        gcep.setColumnCheckFor(GoodsColumnCheckExistParam.ColumnCheckForEnum.E_GOODS_SPEC_PRODUCTION);
        //检查sku sn是否重复
        for (GoodsSpecProduct goodsSpecProduct : goods.getGoodsSpecProducts()) {
            gcep.setPrdId(goodsSpecProduct.getPrdId());

            if (StringUtils.isNotBlank(goodsSpecProduct.getPrdSn())) {
                gcep.setPrdSn(goodsSpecProduct.getPrdSn());
                gcep.setPrdId(goodsSpecProduct.getPrdId());
                if (isColumnValueExist(gcep)) {
                    return GoodsDataIIllegalEnum.GOODS_PRD_SN_EXIST;
                }
                gcep.setPrdSn(null);
            }
            if (StringUtils.isNotBlank(goodsSpecProduct.getPrdCodes())) {
                gcep.setPrdCodes(goodsSpecProduct.getPrdCodes());
                if (isColumnValueExist(gcep)) {
                    return GoodsDataIIllegalEnum.GOODS_PRD_CODES_EXIST;
                }
            }
        }
        return GoodsDataIIllegalEnum.GOODS_OK;
    }

    /*************结束*************/


    /**
     * 判断字段值是否重复
     *
     * @param goodsColumnExistParam
     * @return
     */
    public boolean isColumnValueExist(GoodsColumnCheckExistParam goodsColumnExistParam) {
        SelectSelectStep<Record1<Integer>> selectFrom = db().selectCount();

        SelectConditionStep<?> scs;

        switch (goodsColumnExistParam.getColumnCheckFor()) {
            case E_GOODS_SPEC_PRODUCTION:
                scs = buildGoodsSpecPrdColumnExistOption(selectFrom.from(GOODS_SPEC_PRODUCT), goodsColumnExistParam);
                break;
            default:
                scs = buildGoodsColumnExistOption(selectFrom.from(GOODS), goodsColumnExistParam);
        }

        Record record = scs.fetchOne();
        Integer count = record.getValue(0, Integer.class);

        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isGoodsNameExist(Integer goodsId, String goodsName) {
        GoodsColumnCheckExistParam param = new GoodsColumnCheckExistParam();
        param.setColumnCheckFor(GoodsColumnCheckExistParam.ColumnCheckForEnum.E_GOODS);
        param.setGoodsId(goodsId);
        param.setGoodsName(goodsName);
        return isColumnValueExist(param);
    }

    public List<String> findGoodsNameExist(List<String> goodsNames) {
        return db().select(GOODS.GOODS_NAME).from(GOODS)
                .where(GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(GOODS.GOODS_NAME.in(goodsNames)))
                .fetch(GOODS.GOODS_NAME);
    }

    public List<String> findGoodsSnExist(List<String> goodsSns) {
        return db().select(GOODS.GOODS_SN).from(GOODS)
                .where(GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(GOODS.GOODS_SN.in(goodsSns)))
                .fetch(GOODS.GOODS_SN);
    }

    /**
     * 根据货品编号获取商品
     *
     * @param goodsSn
     * @return
     */
    public GoodsRecord getGoodsRecordByGoodsSn(String goodsSn) {
        return db().selectFrom(GOODS).where(GOODS.GOODS_SN.eq(goodsSn).and(GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)))
                .fetchAny();
    }

    /**
     * 商品名和商品码查重
     *
     * @param select
     * @param goodsColumnExistParam
     * @return
     */
    private SelectConditionStep<?> buildGoodsColumnExistOption(SelectJoinStep<?> select,
                                                               GoodsColumnCheckExistParam goodsColumnExistParam) {
        SelectConditionStep<?> scs = select.where(GOODS.DEL_FLAG.eq(DelFlag.NORMAL.getCode()));

        if (StringUtils.isNotBlank(goodsColumnExistParam.getGoodsName())) {
            scs = scs.and(GOODS.GOODS_NAME.eq(goodsColumnExistParam.getGoodsName()));
        }

        if (StringUtils.isNotBlank(goodsColumnExistParam.getGoodsSn())) {
            scs = scs.and(GOODS.GOODS_SN.eq(goodsColumnExistParam.getGoodsSn()));
        }

        // update 修改条目时排除自身
        if (goodsColumnExistParam.getGoodsId() != null) {
            scs = scs.and(GOODS.GOODS_ID.ne(goodsColumnExistParam.getGoodsId()));
        }
        return scs;
    }

    /**
     * 商品规格字段重复检查
     *
     * @param select
     * @param goodsColumnExistParam
     * @return
     */
    private SelectConditionStep<?> buildGoodsSpecPrdColumnExistOption(SelectJoinStep<?> select,
                                                                      GoodsColumnCheckExistParam goodsColumnExistParam) {
        //判断del_flag应该可以去掉，目前删除商品的时候会把sku备份到bak里面，prd表内是真删除
        SelectConditionStep<?> scs = select.where(DSL.noCondition());

        if (StringUtils.isNotBlank(goodsColumnExistParam.getPrdSn())) {
            scs = scs.and(GOODS_SPEC_PRODUCT.PRD_SN.eq(goodsColumnExistParam.getPrdSn()));
        }
        if (StringUtils.isNotBlank(goodsColumnExistParam.getPrdCodes())) {
            scs = scs.and(GOODS_SPEC_PRODUCT.PRD_CODES.eq(goodsColumnExistParam.getPrdCodes()));
        }
        // 修改去重
        if (goodsColumnExistParam.getPrdId() != null) {
            scs = scs.and(GOODS_SPEC_PRODUCT.PRD_ID.ne(goodsColumnExistParam.getPrdId()));
        }
        return scs;
    }

    /**
     * 商品批量修改方法
     * 批量处理中会同时修改同一商品的全部规格数据，单独修改某一规格库存和价格时不可使用本方法
     *
     * @param operateParam
     */
    public void batchOperate(GoodsBatchOperateParam operateParam) {
        List<Integer> goodsLabels = operateParam.getGoodsLabels();
        if (goodsLabels != null && goodsLabels.size() > 0) {
            goodsLabelCouple.batchUpdateLabelCouple(operateParam.getGoodsIds(), goodsLabels, GoodsLabelCoupleTypeEnum.GOODSTYPE);
        } else if (operateParam.getIsCardExclusive() != null) {
            transaction(() -> {
                db().batchUpdate(operateParam.toUpdateGoodsRecord()).execute();
                memberCardService.deleteOwnEnjoyGoodsByGcta(operateParam.getGoodsIds(), CardConstant.COUPLE_TP_GOODS);
                if (GoodsConstant.CARD_EXCLUSIVE.equals(operateParam.getIsCardExclusive())) {
                    memberCardService.batchUpdateGoods(operateParam.getGoodsIds(), operateParam.getCardIds(), CardConstant.COUPLE_TP_GOODS);
                }
            });
        } else {
            List<GoodsRecord> goodsRecords = operateParam.toUpdateGoodsRecord();
            Map<Integer, List<PrdPriceNumberParam>> goodsPriceNumbers = operateParam.getGoodsPriceNumbers();
            List<GoodsSpecProductRecord> goodsSpecProductRecords = new ArrayList<>(0);
            // 单独处理规格价格和规格数量,
            if (goodsPriceNumbers != null && goodsPriceNumbers.size() > 0) {
                goodsRecords.forEach(goodsRecord -> {
                    Integer goodsId = goodsRecord.getGoodsId();
                    List<PrdPriceNumberParam> prdPriceNumberParams = goodsPriceNumbers.get(goodsId);
                    if (prdPriceNumberParams == null || prdPriceNumberParams.size() == 0) {
                        return;
                    }
                    BigDecimal smallestShopPrice = BigDecimal.valueOf(Double.MAX_VALUE);
                    for (PrdPriceNumberParam prdPriceNumberParam : prdPriceNumberParams) {
                        GoodsSpecProductRecord record = new GoodsSpecProductRecord();
                        record.setPrdId(prdPriceNumberParam.getPrdId());
                        if (prdPriceNumberParam.getShopPrice() != null) {
                            record.setPrdPrice(prdPriceNumberParam.getShopPrice());
                            if (smallestShopPrice.compareTo(prdPriceNumberParam.getShopPrice()) > 0) {
                                smallestShopPrice = prdPriceNumberParam.getShopPrice();
                            }
                        }
                        goodsSpecProductRecords.add(record);
                    }
                    goodsRecord.setShopPrice(smallestShopPrice);
                });
            }
            transaction(() -> {
                db().batchUpdate(goodsSpecProductRecords).execute();
                db().batchUpdate(goodsRecords).execute();
            });

        }
        //更新es
        updateEs(operateParam.getGoodsIds());
    }

    /**
     * 商品批量上下架处理
     *
     * @param operateParam {@link GoodsBatchOperateParam#getIsOnSale()}==1上架，==0 下架
     */
    public void batchIsOnSaleOperate(GoodsBatchOperateParam operateParam) {
        List<GoodsRecord> goodsRecords = operateParam.toUpdateGoodsRecord();
        db().batchUpdate(goodsRecords).execute();
        updateEs(operateParam.getGoodsIds());
    }

    /**
     * 单独修改某一个商品规格的数量或价格,用于admin商品列表界面修改
     *
     * @param param
     */
    public void updateGoodsPrdPriceNumbers(PrdPriceNumberParam param) {
        GoodsSpecProductRecord targetRecord = db().selectFrom(GOODS_SPEC_PRODUCT).where(GOODS_SPEC_PRODUCT.PRD_ID.eq(param.getPrdId())).fetchAny();

        if (targetRecord == null) {
            return;
        }
        // 下面两个if其实是或的关系，这样写可以预防接口参数调用错误
        if (param.getGoodsNumber() != null) {
            targetRecord.setPrdNumber(param.getGoodsNumber());
        }
        if (param.getShopPrice() != null) {
            targetRecord.setPrdPrice(param.getShopPrice());
        }


        GoodsRecord goodsRecord = db().selectFrom(GOODS).where(GOODS.GOODS_ID.eq(targetRecord.getGoodsId())).fetchAny();

        Result<GoodsSpecProductRecord> prds = db().selectFrom(GOODS_SPEC_PRODUCT)
                .where(GOODS_SPEC_PRODUCT.GOODS_ID.eq(goodsRecord.getGoodsId()).and(GOODS_SPEC_PRODUCT.PRD_ID.ne(param.getPrdId()))).fetch();
        prds.add(targetRecord);

        // 从新计算商品总数量和最低价
        BigDecimal minPrice = prds.get(0).getPrdPrice();
        int goodsNum = 0;
        for (GoodsSpecProductRecord prd : prds) {
            if (minPrice.compareTo(prd.getPrdPrice()) > 0) {
                minPrice = prd.getPrdPrice();
            }
            goodsNum += prd.getPrdNumber();
        }
        goodsRecord.setShopPrice(minPrice);
        goodsRecord.setGoodsNumber(goodsNum);

        transaction(() -> {
            // 更新目标规格的数据和对应的商品的数据
            targetRecord.update();
            goodsRecord.update();
        });
        //更新es
        try {
            if (esUtilSearchService.esState()) {
                esGoodsCreateService.updateEsGoodsIndex(goodsRecord.getGoodsId(), getShopId());
            }
        } catch (Exception e) {
            logger().debug("更新商品规格库存价格-同步es数据异常:" + e.getMessage());
        }
    }

    /**
     * 商品批量删除及单个删除
     *
     * @param operateParam
     */
    public void delete(GoodsBatchOperateParam operateParam) {
        List<Integer> goodsIds = operateParam.getGoodsIds();
        transaction(() -> {
            DSLContext db = db();
            db.update(GOODS).set(GOODS.DEL_FLAG, DelFlag.DISABLE.getCode())
                    .set(GOODS.GOODS_SN,
                            DSL.concat(DelFlag.DEL_ITEM_PREFIX).concat(GOODS.GOODS_ID).concat(DelFlag.DEL_ITEM_SPLITER)
                                    .concat(GOODS.GOODS_SN))
                    .set(GOODS.GOODS_NAME, DSL.concat(DelFlag.DEL_ITEM_PREFIX).concat(GOODS.GOODS_ID)
                            .concat(DelFlag.DEL_ITEM_SPLITER).concat(GOODS.GOODS_NAME))
                    .where(GOODS.GOODS_ID.in(goodsIds)).execute();

            // 删除关联图片
            deleteImg(goodsIds);
            // 删除关联规格
            goodsSpecProductService.deleteByGoodsIds(goodsIds);
            //删除关联标签
            goodsLabelCouple.deleteByGoodsIds(goodsIds);
            //删除商品规格会员价信息
            deleteGradePrd(goodsIds);
            //删除关联会员专属信息
            memberCardService.deleteOwnEnjoyGoodsByGcta(goodsIds, CardConstant.COUPLE_TP_GOODS);
            //删除商品规格分销信息
            deleteGoodsRebatePrices(goodsIds);
        });

        updateEsDeleteSync(goodsIds);
    }

    /**
     * 删除关联图片
     *
     * @param goodsIds
     */
    private void deleteImg(List<Integer> goodsIds) {
        db().delete(GOODS_IMG).where(GOODS_IMG.GOODS_ID.in(goodsIds)).execute();
    }

    /**
     * 删除商品规格会员价信息
     *
     * @param goodsIds
     */
    private void deleteGradePrd(List<Integer> goodsIds) {
        db().update(GRADE_PRD).set(GRADE_PRD.DEL_FLAG, DelFlag.DISABLE.getCode()).where(GRADE_PRD.GOODS_ID.in(goodsIds)).execute();
    }

    /**
     * 删除商品规格分销价信息
     *
     * @param goodsIds
     */
    private void deleteGoodsRebatePrices(List<Integer> goodsIds) {
        db().update(GOODS_REBATE_PRICE).set(GOODS_REBATE_PRICE.DEL_FLAG, DelFlag.DISABLE.getCode())
                .where(GOODS_REBATE_PRICE.GOODS_ID.in(goodsIds)).execute();
    }

    /**
     * 统计未删除商品总数量
     *
     * @return 商品数量
     */
    public Integer selectGoodsCount() {
        int i = db().fetchCount(GOODS, GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE));
        return i;
    }

    /**
     * 查询商品详情
     *
     * @param goodsId 商品id
     * @return 商品信息
     */
    public GoodsVo select(Integer goodsId) {
        Record record = getGoodsAndBrandById(goodsId);
        if (record == null) {
            return null;
        }

        GoodsVo goodsVo = record.into(GoodsVo.class);

        //设置主绝对路径图片,都是全路径
        goodsVo.setGoodsImgPath(goodsVo.getGoodsImg());
        goodsVo.setGoodsImg(getImgFullUrlUtil(goodsVo.getGoodsImg()));

        // 设置商品视频全路径信息
        goodsVo.setGoodsVideoUrl(getVideoFullUrlUtil(goodsVo.getGoodsVideo(), true));
        goodsVo.setGoodsVideoImgUrl(getVideoFullUrlUtil(goodsVo.getGoodsVideoImg(), false));

        // 设置幅图片
        setGoodsImgs(goodsVo);

        //设置商品指定标签
        setGoodsTags(goodsId, goodsVo);


        //设置sku
        setGoodsSku(goodsId, goodsVo);

        //设置商品规格会员价
        List<GoodsGradePrd> goodsGradePrds = selectGoodsGradePrd(goodsId);
        goodsVo.setGoodsGradePrds(goodsGradePrds);

        // 设置专属会员卡信息
        List<Integer> cardIds = selectMemberCard(goodsId);
        goodsVo.setMemberCardIds(cardIds);

        //设置模板名称
        XcxCustomerPageRecord pageDecorate = mpDecorationService.getPageById(goodsVo.getGoodsPageId());
        if (pageDecorate == null) {
            goodsVo.setGoodsPageName(null);
        } else {
            goodsVo.setGoodsPageName(pageDecorate.getPageName());
        }

        // 设置规格分销信息
        List<GoodsRebatePrice> goodsRebatePrices = selectGoodsRebatePrice(goodsId);
        goodsVo.setGoodsRebatePrices(goodsRebatePrices);

        // 反序列化商品海报分享信息
        GoodsSharePostConfig goodsSharePostConfig = Util.parseJson(goodsVo.getShareConfig(), GoodsSharePostConfig.class);
        goodsSharePostConfig.setShareImgPath(goodsSharePostConfig.getShareImgUrl());
        goodsSharePostConfig.setShareImgUrl(getImgFullUrlUtil(goodsSharePostConfig.getShareImgUrl()));
        goodsVo.setGoodsSharePostConfig(goodsSharePostConfig);

        // 设置直播间名称
        if (goodsVo.getRoomId() != null) {
            LiveBroadcastRecord liveBroadcastRecord = liveService.getLiveInfoByRoomId(goodsVo.getRoomId());
            if (liveBroadcastRecord == null) {
                goodsVo.setRoomId(null);
            } else {
                goodsVo.setRoomName(liveBroadcastRecord.getName());
            }
        }

        return goodsVo;
    }

    private void setGoodsSku(Integer goodsId, GoodsVo goodsVo) {
        List<GoodsSpecProduct> goodsSpecProducts = goodsSpecProductService.selectByGoodsId(goodsId);
        goodsSpecProducts.forEach(goodsSpecProduct -> goodsSpecProduct.setPrdImgUrl(getImgFullUrlUtil(goodsSpecProduct.getPrdImg())));
        goodsVo.setGoodsSpecProducts(goodsSpecProducts);

        List<GoodsSpec> goodsSpecs = goodsSpecProductService.selectSpecByGoodsId(goodsId);
        goodsVo.setGoodsSpecs(goodsSpecs);
    }

    private void setGoodsTags(Integer goodsId, GoodsVo goodsVo) {
        Map<Integer, List<GoodsLabelSelectListVo>> gtaLabelMap = goodsLabel.getGtaLabelMap(Arrays.asList(goodsId), GoodsLabelCoupleTypeEnum.GOODSTYPE);
        goodsVo.setGoodsLabelPointListVos(gtaLabelMap.get(goodsId) == null ? new ArrayList<>(0) : gtaLabelMap.get(goodsId));
        goodsVo.setGoodsLabelNormalListVos(new ArrayList<>(5));
        // 商家分类关联标签
        if (!GoodsConstant.GOODS_SORT_DEFAULT_VALUE.equals(goodsVo.getSortId())) {
            Map<Integer, List<GoodsLabelSelectListVo>> gtaLabelSortMap = goodsLabel.getGtaLabelMap(Arrays.asList(goodsVo.getSortId()), GoodsLabelCoupleTypeEnum.SORTTYPE);
            if (gtaLabelSortMap.get(goodsVo.getSortId()) != null && gtaLabelSortMap.get(goodsVo.getSortId()).size() > 0) {
                goodsVo.getGoodsLabelNormalListVos().addAll(gtaLabelSortMap.get(goodsVo.getSortId()));
            }
        }
        // 绑定全部商品上的标签
        List<GoodsLabelSelectListVo> allGoodsLabels = goodsLabel.getAllGoodsLabels();
        goodsVo.getGoodsLabelNormalListVos().addAll(allGoodsLabels);
    }

    private Record getGoodsAndBrandById(Integer goodsId) {
        return db().select()
                .from(GOODS).leftJoin(GOODS_BRAND).on(GOODS.BRAND_ID.eq(GOODS_BRAND.ID))
                .leftJoin(SORT).on(GOODS.SORT_ID.eq(SORT.SORT_ID))
                .where(GOODS.GOODS_ID.eq(goodsId).and(GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))).fetchAny();
    }

    /**
     * 设置商品副图片
     *
     * @param goods 商品对象
     */
    private void setGoodsImgs(GoodsVo goods) {
        Integer goodsId = goods.getGoodsId();

        List<String> fetch = db().select(GOODS_IMG.IMG_URL).from(GOODS_IMG).where(GOODS_IMG.GOODS_ID.eq(goodsId)).fetch(GOODS_IMG.IMG_URL);
        List<String> goodsImgs = new ArrayList<>(fetch.size());
        List<String> goodsImgsPath = new ArrayList<>(fetch.size());
        // 设置图片绝对路径
        fetch.forEach(item -> {
            goodsImgs.add(getImgFullUrlUtil(item));
            goodsImgsPath.add(item);
        });
        goods.setGoodsImgs(goodsImgs);
        goods.setGoodsImgsPath(goodsImgsPath);
    }

    /**
     * 获取商品规格会员价
     *
     * @param goodsId
     * @return
     */
    public List<GoodsGradePrd> selectGoodsGradePrd(Integer goodsId) {
        return db().select().from(GRADE_PRD).where(GRADE_PRD.GOODS_ID.eq(goodsId))
                .and(GRADE_PRD.DEL_FLAG.eq(DelFlag.NORMAL.getCode())).fetchInto(GoodsGradePrd.class);
    }

    /**
     * 批量获取商品规格会员价
     *
     * @param goodsIds
     * @return
     */
    public Map<Integer, List<GoodsGradePrd>> selectGoodsGradePrdByGoodsIds(List<Integer> goodsIds) {
        return db().select().from(GRADE_PRD).where(GRADE_PRD.GOODS_ID.in(goodsIds))
                .and(GRADE_PRD.DEL_FLAG.eq(DelFlag.NORMAL.getCode())).fetchInto(GoodsGradePrd.class)
                .stream().collect(Collectors.groupingBy(GoodsGradePrd::getGoodsId));
    }

    public Result<GoodsSpecProductRecord> getProductByGoodsId(Integer goodsId) {
        Result<GoodsSpecProductRecord> fetch = db().selectFrom(GOODS_SPEC_PRODUCT).where(GOODS_SPEC_PRODUCT.GOODS_ID.eq(goodsId)).fetch();
        return fetch;
    }

    /**
     * 获取商品专享会员卡
     *
     * @param goodsId 商品id
     * @return 会员卡ids
     */
    private List<Integer> selectMemberCard(Integer goodsId) {
        return memberCardService.selectOwnEnjoyCardByGcta(goodsId, CardConstant.COUPLE_TP_GOODS);
    }

    /**
     * 获取商品规格分销信息
     *
     * @param goodsId 商品id
     * @return 商品规格分销集合
     */
    private List<GoodsRebatePrice> selectGoodsRebatePrice(Integer goodsId) {
        List<GoodsRebatePrice> goodsRebatePrices = db().select().from(GOODS_REBATE_PRICE).where(GOODS_REBATE_PRICE.GOODS_ID.eq(goodsId))
                .and(GOODS_REBATE_PRICE.DEL_FLAG.eq(DelFlag.NORMAL.getCode())).fetchInto(GoodsRebatePrice.class);

        return goodsRebatePrices;
    }

    /**
     * 通过商品id数组查询商品
     */
    public Map<Integer, GoodsRecord> getGoodsByIds(List<Integer> goodsIds) {
        return db().selectFrom(GOODS).where(GOODS.GOODS_ID.in(goodsIds)).
                fetchMap(GOODS.GOODS_ID);
    }

    /**
     * 通过商品id数组查询门店商品
     *
     * @return
     */
    public Map<Integer, Result<StoreGoodsRecord>> getStoreGoodsByIds(List<Integer> prdIds, Integer storeId) {
        return db().selectFrom(STORE_GOODS).where(STORE_GOODS.PRD_ID.in(prdIds)).
                and(STORE_GOODS.IS_DELETE.eq(DelFlag.NORMAL_VALUE)).
                and(STORE_GOODS.STORE_ID.eq(storeId)).
                and(STORE_GOODS.IS_ON_SALE.eq(IS_ON_SALE)).
                fetchGroups(STORE_GOODS.PRD_ID);
    }


    /**
     * 通过商品id数组查询商品
     */
    public Map<Integer, GoodsRecord> getIsSaleGoodsByIds(List<Integer> goodsIds) {
        return db().selectFrom(GOODS).where(GOODS.GOODS_ID.in(goodsIds)).
                and(GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)).
                fetchMap(GOODS.GOODS_ID);
    }

    /**
     * 获取商品小程序展示页面
     *
     * @param goodsId 商品id
     * @return GoodsQrCodeVo
     */
    public GoodsQrCodeVo getGoodsQrCode(Integer goodsId) {
        GoodsActivityType goodsActivityType = getGoodsActivityType(goodsId);
        String params;
        if (goodsActivityType == null) {
            params = "gid=" + goodsId + "&aid=&atp=";
        } else {
            params = String.format("gid=%d&aid=%d&atp=%d", goodsId, goodsActivityType.getActivityId(), goodsActivityType.getActivityType());
        }
        log.debug("urlParam:{}", params);
        String mpQrCode = qrCodeService.getMpQrCode(QrCodeTypeEnum.GOODS_ITEM, params);
        log.debug("qrCode img full url:{}", mpQrCode);
        GoodsQrCodeVo goodsQrCodeVo = new GoodsQrCodeVo();
        goodsQrCodeVo.setImgFullUrl(mpQrCode);
        goodsQrCodeVo.setPageUrl(QrCodeTypeEnum.GOODS_ITEM.getPathUrl(params));
        return goodsQrCodeVo;
    }

    /**
     * 将相对路劲修改为全路径
     *
     * @param relativePath 相对路径
     * @return null或全路径
     */
    public String getImgFullUrlUtil(String relativePath) {
        if (StringUtils.isBlank(relativePath)) {
            return null;
        } else {
            return imageService.imageUrl(relativePath);
        }
    }

    /**
     * 商品视频和快照图片相对路径转换全路径
     *
     * @param relativePath    相对路径
     * @param videoOrSnapShop true: 视频，false: 快照
     * @return 全路径
     */
    private String getVideoFullUrlUtil(String relativePath, boolean videoOrSnapShop) {
        if (StringUtils.isBlank(relativePath)) {
            return null;
        } else {
            return videoOrSnapShop ? upYunConfig.videoUrl(relativePath) : upYunConfig.imageUrl(relativePath);
        }
    }

    /**
     * 通过商品id查询商品
     */
    public Optional<GoodsRecord> getGoodsById(Integer goodsId) {
        return db().selectFrom(GOODS).where(GOODS.GOODS_ID.eq(goodsId)).
                fetchOptional();
    }

    /**
     * 通过商品id查询商品重量
     */
    public BigDecimal getGoodsWeightById(Integer goodsId) {
        Record record = db().select(GOODS.GOODS_WEIGHT).from(GOODS).where(GOODS.GOODS_ID.eq(goodsId).and(GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))).fetchOne();
        if (record != null) {
            return record.into(BigDecimal.class);
        }
        return null;
    }

    /**
     * 根据规格ID查寻商品信息
     *
     * @param productId
     * @return
     */
    public Record getGoodsByProductId(Integer productId) {
        return db().select().from(GOODS).leftJoin(GOODS_SPEC_PRODUCT).on(GOODS.GOODS_ID.eq(GOODS_SPEC_PRODUCT.GOODS_ID)).where(GOODS_SPEC_PRODUCT.PRD_ID.eq(productId)).fetchOne();
    }

    /**
     * 获取商品规格信息,用于页面显示
     *
     * @param productId
     * @return
     */
    public ProductSmallInfoVo getProductVoInfoByProductId(Integer productId) {
        return db().select(GOODS.GOODS_ID, GOODS.GOODS_NAME, GOODS_SPEC_PRODUCT.PRD_DESC, GOODS.GOODS_IMG, GOODS_SPEC_PRODUCT.PRD_NUMBER, GOODS_SPEC_PRODUCT.PRD_PRICE, GOODS.IS_ON_SALE)
                .from(GOODS_SPEC_PRODUCT)
                .leftJoin(GOODS).on(GOODS.GOODS_ID.eq(GOODS_SPEC_PRODUCT.GOODS_ID))
                .where(GOODS_SPEC_PRODUCT.PRD_ID.eq(productId)).fetchOneInto(ProductSmallInfoVo.class);
    }

    /**
     * 商品导出数据的条数
     *
     * @param param
     * @return
     */
    public GoodsExportColumnVo getExportGoodsListSize(GoodsPageListParam param) {
        GoodsExportColumnVo vo = new GoodsExportColumnVo();
        // 拼接过滤条件
        Condition condition = this.buildOptions(param);

        SelectConditionStep<?> selectFrom = db().selectCount()
                .from(GOODS).leftJoin(SORT).on(GOODS.SORT_ID.eq(SORT.SORT_ID)).leftJoin(GOODS_BRAND)
                .on(GOODS.BRAND_ID.eq(GOODS_BRAND.ID)).innerJoin(GOODS_SPEC_PRODUCT).on(GOODS.GOODS_ID.eq(GOODS_SPEC_PRODUCT.GOODS_ID))
                .where(condition);

        vo.setRows(selectFrom.fetchOne().into(Integer.class));
        vo.setColumns(saas.getShopApp(getShopId()).config.goodsCfg.getGoodsExportList());
        return vo;
    }

    public List<Integer> getAllGoodsId() {
        return db().select(GOODS.GOODS_ID)
                .from(GOODS)
                .where(GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
                .fetch()
                .getValues(GOODS.GOODS_ID);
    }

    /**
     * 商品列表导出（规格为主）
     *
     * @param param
     * @param lang
     * @return workbook
     */
    public Workbook exportGoodsList(GoodsExportParam param, String lang) {
        SelectConditionStep<?> selectFrom = this.createProductSelect(param);
        List<GoodsExportVo> list = selectFrom.limit(param.getExportRowStart() - 1, param.getExportRowEnd() - param.getExportRowStart() + 1).fetchInto(GoodsExportVo.class);

        Set<Integer> sortIds = new HashSet<>(list.size());
        Set<Integer> goodsIds = new HashSet<>(list.size());
        List<Integer> prdIds = new ArrayList<>(list.size());

        list.stream().forEach(vo -> {
            if (vo.getSortId() != null) {
                sortIds.add(vo.getSortId());
            }
            goodsIds.add(vo.getGoodsId());
            prdIds.add(vo.getPrdId());
        });
        // 提前处理辅助数据
        Map<Integer, SortRecord> sortMap = goodsSort.getNormalSortByIds(new ArrayList<>(sortIds)).stream().collect(Collectors.toMap(SortRecord::getSortId, Function.identity()));

        boolean needGoodsMainImg = param.getColumns().contains(GoodsExportVo.GOODS_IMG);
        boolean needGoodsSecondaryImgs = param.getColumns().contains(GoodsExportVo.IMG_URL);
        boolean needGradeCardPrice = param.getColumns().contains(GoodsExportVo.GRADE_CARD_PRICE);


        Map<Integer, List<String>> goodsSecondaryImgsMap = null;
        List<CardBasicVo> allAvailableGradeCards = null;
        Map<Integer, Map<String, GradePrdRecord>> prdGradeMaps = null;

        if (needGoodsSecondaryImgs) {
            goodsSecondaryImgsMap = getGoodsImageList(new ArrayList<>(goodsIds));
        }
        if (needGradeCardPrice) {
            allAvailableGradeCards = gradeCardService.getAllAvailableGradeCards();
            List<String> grades = allAvailableGradeCards.stream().map(CardBasicVo::getGrade).collect(Collectors.toList());
            prdGradeMaps = selectAvailableGradeInfoByPrdIds(prdIds, grades);
            param.getColumns().addAll(grades);
        }

        //循环处理需要处理的列
        for (GoodsExportVo goods : list) {
            if (goods.getSortId() != null) {
                SortRecord sortRecord = sortMap.get(goods.getSortId());
                if (sortRecord != null) {
                    if (GoodsConstant.ROOT_LEVEL.equals(sortRecord.getLevel())) {
                        goods.setSortNameParent(sortRecord.getSortName());
                    } else {
                        goods.setSortNameChild(sortRecord.getSortName());
                        SortRecord parent = sortMap.get(sortRecord.getParentId());
                        goods.setSortNameParent((parent == null) ? null : parent.getSortName());
                    }
                }
            }

            if (needGoodsMainImg) {
                goods.setGoodsImg(getImgFullUrlUtil(goods.getGoodsImg()));
            }

            if (needGoodsSecondaryImgs) {
                List<String> imgs = goodsSecondaryImgsMap.get(goods.getGoodsId());
                if (imgs != null) {
                    goods.setImgUrl(String.join(";", imgs));
                }
            }

            if (needGradeCardPrice) {
                Map<String, GradePrdRecord> gradeMap = prdGradeMaps.get(goods.getPrdId());
                if (gradeMap != null) {
                    setGoodsExportVoGradePrice(goods, gradeMap);
                }
            }

        }

        Workbook workbook = ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(lang, workbook);
        excelWriter.writeModelList(list, GoodsExportVo.class, param.getColumns());
        return workbook;
    }

    private void setGoodsExportVoGradePrice(GoodsExportVo vo, Map<String, GradePrdRecord> gradePrdRecordMap) {
        for (Map.Entry<String, GradePrdRecord> entry : gradePrdRecordMap.entrySet()) {
            switch (entry.getKey()) {
                case "v1":
                    vo.setV1(entry.getValue().getGradePrice());
                    break;
                case "v2":
                    vo.setV2(entry.getValue().getGradePrice());
                    break;
                case "v3":
                    vo.setV3(entry.getValue().getGradePrice());
                    break;
                case "v4":
                    vo.setV4(entry.getValue().getGradePrice());
                    break;
                case "v5":
                    vo.setV5(entry.getValue().getGradePrice());
                    break;
                case "v6":
                    vo.setV6(entry.getValue().getGradePrice());
                    break;
                case "v7":
                    vo.setV7(entry.getValue().getGradePrice());
                    break;
                case "v8":
                    vo.setV8(entry.getValue().getGradePrice());
                    break;
                case "v9":
                    vo.setV9(entry.getValue().getGradePrice());
                    break;
                default:
                    ;
            }
        }
    }

    private Map<Integer, Map<String, GradePrdRecord>> selectAvailableGradeInfoByPrdIds(List<Integer> prdIds, List<String> availabelGrades) {
        List<GradePrdRecord> gradePrdRecords = db().selectFrom(GRADE_PRD)
                .where(GRADE_PRD.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(GRADE_PRD.GRADE.in(availabelGrades)).and(GRADE_PRD.PRD_ID.in(prdIds))).fetchInto(GradePrdRecord.class);
        return gradePrdRecords.stream().collect(Collectors.groupingBy(GradePrdRecord::getPrdId, Collectors.toMap(GradePrdRecord::getGrade, Function.identity())));
    }

    /**
     * 取指定商品信息
     *
     * @param idList
     * @param isCanUse
     * @return
     */
    public List<GoodsSmallVo> getGoodsList(List<Integer> idList, boolean isCanUse) {
        SelectConditionStep<? extends Record> sql = db().select(GOODS.GOODS_ID, GOODS.GOODS_SN, GOODS.IS_ON_SALE, GOODS.GOODS_NUMBER, GOODS.DEL_FLAG, GOODS.GOODS_IMG,
                GOODS.SHOP_PRICE, GOODS.GOODS_NAME, GOODS.MARKET_PRICE, GOODS.GOODS_TYPE, GOODS.IS_CARD_EXCLUSIVE)
                .from(GOODS)
                .where(GOODS.GOODS_ID.in(idList));
        if (isCanUse) {
            sql.and(GOODS.IS_ON_SALE.eq(GoodsConstant.ON_SALE))
                    .and(GOODS.DEL_FLAG.eq(DelFlag.NORMAL.getCode()));
        }

        return sql.fetchInto(GoodsSmallVo.class);
    }


    /**
     * 获取商品goodsType
     *
     * @param goodsIds
     * @return
     * @throws MpException
     */
    public Map<Integer, Byte> getGoodsType(List<Integer> goodsIds) throws MpException {
        Map<Integer, Byte> goodsTypes = db().select(GOODS.GOODS_ID, GOODS.GOODS_TYPE).from(GOODS).where(GOODS.GOODS_ID.in(goodsIds)).fetchMap(GOODS.GOODS_ID, GOODS.GOODS_TYPE);
        return goodsTypes;
    }

    /**
     * 下单时获取goods
     *
     * @param goodsIds
     * @return
     * @throws MpException
     * @author 王帅
     */
    public Map<Integer, GoodsRecord> getGoodsToOrder(List<Integer> goodsIds) {
        Map<Integer, GoodsRecord> goods = getGoodsByIds(goodsIds);
        return goods;
    }

    /**
     * 下单时校验门店库存
     *
     * @param prdIds 商品id列表
     * @return Map<Integer, StoreGoodsRecord>
     */
    public  Map<Integer, Result<StoreGoodsRecord>> getStoreGoodsToOrder(List<Integer> prdIds, Integer storeId) {
        return getStoreGoodsByIds(prdIds, storeId);
    }

    /**
     * 批量更新商品、规格的数量和销量
     *
     * @param params 待更新商品、规格数量销量信息
     */
    public void batchUpdateGoodsNumsAndSaleNumsForOrder(List<BatchUpdateGoodsNumAndSaleNumForOrderParam> params) {

        if (params == null || params.size() == 0) {
            return;
        }

        List<Integer> goodsIds = new ArrayList<>(params.size());
        List<Integer> prdIds = new ArrayList<>(params.size());

        params.forEach(param -> {
            goodsIds.add(param.getGoodsId());
            prdIds.addAll(param.getProductsInfo().stream().map(BatchUpdateGoodsNumAndSaleNumForOrderParam.ProductNumInfo::getPrdId).collect(Collectors.toList()));
        });

        // 查询数据库商品信息，规格信息，准备进行数据修改
        Map<Integer, GoodsRecord> goodsRecords = db().select(GOODS.GOODS_ID, GOODS.GOODS_NUMBER, GOODS.GOODS_SALE_NUM).from(GOODS).where(GOODS.GOODS_ID.in(goodsIds))
                .fetchMap(GOODS.GOODS_ID, GoodsRecord.class);
        Map<Integer, GoodsSpecProductRecord> prdRecordsMap = db().select(GOODS_SPEC_PRODUCT.PRD_ID, GOODS_SPEC_PRODUCT.PRD_NUMBER).from(GOODS_SPEC_PRODUCT).where(GOODS_SPEC_PRODUCT.PRD_ID.in(prdIds))
                .fetchMap(GOODS_SPEC_PRODUCT.PRD_ID, GoodsSpecProductRecord.class);

        params.forEach(param -> {
            GoodsRecord goodsRecord = goodsRecords.get(param.getGoodsId());
            goodsRecord.setGoodsNumber(param.getGoodsNum());
            goodsRecord.setGoodsSaleNum(param.getSaleNum());

            param.getProductsInfo().forEach(prdInfo -> {
                GoodsSpecProductRecord record = prdRecordsMap.get(prdInfo.getPrdId());
                record.setPrdNumber(prdInfo.getPrdNum());
            });
        });
        transaction(() -> {
            db().batchUpdate(goodsRecords.values()).execute();
            db().batchUpdate(prdRecordsMap.values()).execute();
        });
        try {
            if (esUtilSearchService.esState()) {
                esGoodsCreateService.batchUpdateEsGoodsIndex(goodsIds, getShopId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Unsalable goods integer.滞销商品数量
     * 商品一个月内未进行过更新, 并且一个月内没参与任何订单(也就是没人买过)
     *
     * @return the integer
     */
    public Integer unsalableGoods() {
        try {
            return esUtilSearchService.getZhiXiaoGoodsNumbers().size();
        } catch (Exception e) {
            log.error("ES查询滞销商品数量失败, 原因如下: {}", e.getMessage());
            Timestamp fixedTime = Timestamp.valueOf(LocalDateTime.now().minus(30, ChronoUnit.DAYS));
            Select<? extends Record1<Integer>> select = db().select(ORDER_GOODS.GOODS_ID).from(ORDER_GOODS).leftJoin(ORDER_INFO)
                    .on(ORDER_GOODS.ORDER_ID.eq(ORDER_INFO.ORDER_ID))
                    .where(ORDER_INFO.CREATE_TIME.greaterOrEqual(fixedTime));
            return db().fetchCount(GOODS, GOODS.DEL_FLAG.eq(BYTE_ZERO).and(GOODS.GOODS_ID.notIn(select)).and(GOODS.UPDATE_TIME.lessOrEqual(fixedTime)));
        }
    }

    /**
     * Unsalable goods integer.滞销商品id集合
     * 商品一个月内未进行过更新, 并且一个月内没参与任何订单(也就是没人买过)
     *
     * @return the integer
     */
    public Set<Integer> unsalableGoodsSet() {
        Timestamp fixedTime = Timestamp.valueOf(LocalDateTime.now().minus(30, ChronoUnit.DAYS));
        Select<? extends Record1<Integer>> select = db().select(ORDER_GOODS.GOODS_ID).from(ORDER_GOODS).leftJoin(ORDER_INFO)
                .on(ORDER_GOODS.ORDER_ID.eq(ORDER_INFO.ORDER_ID))
                .where(ORDER_INFO.CREATE_TIME.greaterOrEqual(fixedTime));
        Condition condition = GOODS.DEL_FLAG.eq(BYTE_ZERO).and(GOODS.GOODS_ID.notIn(select)).and(GOODS.UPDATE_TIME.lessOrEqual(fixedTime));
        return db().select(GOODS.GOODS_ID).from(GOODS).where(condition).fetchSet(GOODS.GOODS_ID);
    }

    /**
     * Small commodity inventory integer.统计商品库存偏小
     *
     * @param num the num
     * @return the integer
     */
    public int smallCommodityInventory(Integer num) {
        return db().fetchCount(GOODS, GOODS.DEL_FLAG.eq(BYTE_ZERO).and(GOODS.GOODS_NUMBER.lessThan(num)));
    }

    /**
     * Small commodity inventory integer.统计商品库存偏小的商品id集合
     *
     * @param num the num
     * @return the integer
     */
    public Set<Integer> smallCommodityInventorySet(Integer num) {
        return db().select(GOODS.GOODS_ID).from(GOODS)
                .where(GOODS.DEL_FLAG.eq(BYTE_ZERO).and(GOODS.GOODS_NUMBER.lessThan(num)))
                .fetchSet(GOODS.GOODS_ID);
    }

    /**
     * 批量将活动商品改回普通商品
     *
     * @param goodsIds
     */
    public void changeToNormalType(List<Integer> goodsIds) {
        db().update(GOODS).set(GOODS.GOODS_TYPE, BaseConstant.ACTIVITY_TYPE_GENERAL).where(GOODS.GOODS_ID.in(goodsIds)).execute();
    }

    /**
     * 获取商品所有图片列表
     */
    public List<String> getGoodsAllImageList(Integer goodsId) {
        List<String> list = getGoodsImageList(goodsId);
        GoodsRecord goodsRecord = db().selectFrom(GOODS).where(GOODS.GOODS_ID.eq(goodsId)).fetchAny();
        if (goodsRecord != null) {
            list.add(0, getImgFullUrlUtil(goodsRecord.getGoodsImg()));
        }
        return list;
    }

    /**
     * 获取商品幅图图片列表
     *
     * @param goodsId
     * @return
     */
    public List<String> getGoodsImageList(Integer goodsId) {
        Result<GoodsImgRecord> fetch = db().selectFrom(GOODS_IMG).where(GOODS_IMG.GOODS_ID.eq(goodsId)).orderBy(GOODS_IMG.IMG_DESC.desc()).fetch();
        List<String> list = new ArrayList<String>();
        for (GoodsImgRecord item : fetch) {
            list.add(getImgFullUrlUtil(item.getImgUrl()));
        }
        return list;

    }

    /**
     * 商品图片列表
     *
     * @param goodsIds goods id
     * @return Map<goodsid, List < url>>
     */
    public Map<Integer, List<String>> getGoodsImageList(List<Integer> goodsIds) {
        Map<Integer, Result<GoodsImgRecord>> fetch = db().selectFrom(GOODS_IMG)
                .where(GOODS_IMG.GOODS_ID.in(goodsIds))
                .orderBy(GOODS_IMG.IMG_DESC.desc()).fetchGroups(GOODS_IMG.GOODS_ID);
        Map<Integer, List<String>> imgUrlMap = Maps.newHashMap();
        if (fetch.isEmpty()) {
            return imgUrlMap;
        }
        fetch.forEach((key, value) -> {
            List<String> urls = value.stream()
                    .map(y -> getImgFullUrlUtil(y.getImgUrl()))
                    .collect(Collectors.toList());
            imgUrlMap.put(key, urls);
        });
        return imgUrlMap;
    }

    public Map<Integer, GoodsVideoBo> getGoodsVideo(List<Integer> goodsIds) {
        Result<Record7<Integer, Integer, Integer, Integer, String, String, Integer>> fetch = db().
                select(GOODS.GOODS_ID, UPLOADED_VIDEO.VIDEO_ID, UPLOADED_VIDEO.VIDEO_HEIGHT,
                        UPLOADED_VIDEO.VIDEO_WIDTH, GOODS.GOODS_VIDEO, GOODS.GOODS_VIDEO_IMG, GOODS.GOODS_VIDEO_SIZE).
                from(UPLOADED_VIDEO).
                leftJoin(GOODS).
                on(GOODS.GOODS_VIDEO_ID.eq(UPLOADED_VIDEO.VIDEO_ID)).
                where(GOODS.GOODS_ID.in(goodsIds)).fetch();
        List<GoodsVideoBo> bos = Lists.newArrayList();
        fetch.forEach(x -> {
            GoodsVideoBo bo = new GoodsVideoBo(x);
            bo.setImgUrl(getImgFullUrlUtil(bo.getImgUrl()));
            bo.setUrl(getVideoFullUrlUtil(bo.getUrl(), true));
            bos.add(bo);
        });
        return bos.stream().collect(Collectors.toMap(GoodsVideoBo::getGoodsId, Function.identity()));
    }

    /**
     * 根据品牌id获取商品id
     *
     * @param brandId 品牌id
     * @return goodsId list
     */
    public List<Integer> getGoodsIdByBrandId(Integer brandId) {
        return db().select(GOODS.GOODS_ID).
                from(GOODS).
                where(GOODS.DEL_FLAG.eq(DelFlag.NORMAL.getCode())).
                and(GOODS.BRAND_ID.eq(brandId)).
                fetch(GOODS.GOODS_ID);
    }

    /**
     * 根据品牌id获取商品ids
     *
     * @param brandIds 品牌ids
     * @return goodsId list
     */
    public List<Integer> getGoodsIdByBrandId(List<Integer> brandIds) {
        return db().select(GOODS.GOODS_ID).
                from(GOODS).
                where(GOODS.DEL_FLAG.eq(DelFlag.NORMAL.getCode())).
                and(GOODS.BRAND_ID.in(brandIds)).
                fetch(GOODS.GOODS_ID);
    }

    /**
     * 根据sortId list获取商品id
     *
     * @param sortIds 品牌id
     * @return goodsId list
     */
    public List<Integer> getGoodsIdBySortId(List<Integer> sortIds) {
        return db().select(GOODS.GOODS_ID).
                from(GOODS).
                where(GOODS.DEL_FLAG.eq(DelFlag.NORMAL.getCode())).
                and(GOODS.SORT_ID.in(sortIds)).
                fetch(GOODS.GOODS_ID);
    }

    /**
     * 手动上架所有待上架商品
     * 待上架商品：商品是指定时间上架的商品，且指定时间小于当前时间 （saleType=1 待上架，state!=2 审核未违规）
     */
    public void onSaleGoods() {
        List<Integer> goodsIds = db().select().from(GOODS)
                .where(GOODS.DEL_FLAG.eq(DelFlag.NORMAL.getCode())).and(GOODS.IS_ON_SALE.eq(GoodsConstant.OFF_SALE))
                .and(GOODS.GOODS_NUMBER.gt(0)).and(GOODS.STATE.ne(GoodsConstant.INVALIDATE_OFF_SALE))
                .and(GOODS.SALE_TYPE.eq(GoodsConstant.POINT_TIME_TO_ON_SALE)).and(GOODS.SALE_TIME.le(DateUtils.getLocalDateTime()))
                .fetch(GOODS.GOODS_ID);

        db().update(GOODS).set(GOODS.IS_ON_SALE, GoodsConstant.ON_SALE)
                .set(GOODS.SALE_TYPE, GoodsConstant.NOT_TIME_TO_ON_SALE)
                .where(GOODS.GOODS_ID.in(goodsIds)).execute();

        try {
            if (!goodsIds.isEmpty() && esUtilSearchService.esState()) {
                esGoodsCreateService.batchUpdateEsGoodsIndex(goodsIds, getShopId());
                esGoodsLabelCreateService.createEsLabelIndexForGoodsId(goodsIds, DBOperating.UPDATE);
            } else {
                esDataUpdateMqService.addEsGoodsIndex(goodsIds, getShopId(), DBOperating.UPDATE);
                esDataUpdateMqService.updateGoodsLabelByLabelId(getShopId(), DBOperating.UPDATE, goodsIds, null);
            }
        } catch (Exception e) {
            logger().debug("手动上架所有待上架商品-同步es数据异常：" + e.getMessage());
        }
    }

    /**
     * 取单个完整Goods
     *
     * @return
     */
    public GoodsRecord getGoodsRecordById(int goodsId) {
        return db().fetchAny(GOODS, GOODS.GOODS_ID.eq(goodsId));
    }

    /**
     * 取多个完整Goods
     *
     * @return
     */
    public Map<Integer, GoodsRecord> getGoodsRecordByIds(List<Integer> goodsIds) {
        return db().selectFrom(GOODS).where(GOODS.GOODS_ID.in(goodsIds)).fetchMap(Tables.GOODS.GOODS_ID);
    }

    /**
     * 获取满足条件的商品数量
     *
     * @param param 条件
     * @return 商品数量
     */
    public Integer getGoodsNum(GoodsNumCountParam param) {
        return getGoodsNum(Collections.singletonList(param)).get(0);
    }

    /**
     * 获取满足过滤条件的商品数量集合
     *
     * @param params 过滤条件集合
     * @return 商品数量集合
     */
    public List<Integer> getGoodsNum(List<GoodsNumCountParam> params) {
        Condition baseCondition = GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(GOODS.IS_ON_SALE.eq(GoodsConstant.ON_SALE));
        Byte soldOutGoods = configService.shopCommonConfigService.getSoldOutGoods();
        if (!GoodsConstant.SOLD_OUT_GOODS_SHOW.equals(soldOutGoods)) {
            baseCondition = baseCondition.and(GOODS.GOODS_NUMBER.gt(0));
        }
        List<Integer> goodsNums = new ArrayList<>();

        for (GoodsNumCountParam param : params) {
            Condition condition = baseCondition;
            if (param.getLabelId() != null) {
                List<Integer> allIds = goodsLabelCouple.getGoodsLabelCouple(Collections.singletonList(param.getLabelId()), GoodsLabelCoupleTypeEnum.ALLTYPE.getCode());
                if (allIds.size() == 0) {
                    List<Integer> sortIds = goodsLabelCouple.getGoodsLabelCouple(Collections.singletonList(param.getLabelId()), GoodsLabelCoupleTypeEnum.SORTTYPE.getCode());
                    List<Integer> goodsIds = goodsLabelCouple.getGoodsLabelCouple(Collections.singletonList(param.getLabelId()), GoodsLabelCoupleTypeEnum.GOODSTYPE.getCode());
                    Condition idCondition = DSL.noCondition();
                    if (sortIds.size() > 0) {
                        List<Integer> targetIds = goodsSort.getChildrenIdByParentIdsDao(sortIds);
                        idCondition = idCondition.or(GOODS.SORT_ID.in(targetIds));
                    }
                    if (goodsIds.size() > 0) {
                        idCondition = idCondition.or(GOODS.GOODS_ID.in(goodsIds));
                    }
                    condition = condition.and(idCondition);
                } else {
                    // 存在全部商品行标签
                    goodsNums.add(getGoodsNumDao(condition));
                    continue;
                }
            }

            if (param.getSortId() != null) {
                List<Integer> targetIds = goodsSort.getChildrenIdByParentIdsDao(Collections.singletonList(param.getSortId()));
                condition = condition.and(GOODS.SORT_ID.in(targetIds));
            }

            if (param.getBrandId() != null) {
                condition = condition.and(GOODS.BRAND_ID.eq(param.getBrandId()));
            }

            if (param.getGoodsIds() != null) {
                condition = condition.and(GOODS.GOODS_ID.in(param.getGoodsIds()));
            }
            goodsNums.add(getGoodsNumDao(condition));
        }
        return goodsNums;
    }

    /**
     * 查询普通商品分享时使用的分享信息
     *
     * @param goodsId 商品id
     * @return 商品分享信息
     */
    public GoodsVo selectGoodsShareInfo(Integer goodsId) {
        Record4<Integer, String, String, String> record = db().select(GOODS.GOODS_ID, GOODS.GOODS_NAME, GOODS.GOODS_IMG, GOODS.SHARE_CONFIG)
                .from(GOODS).where(GOODS.GOODS_ID.eq(goodsId).and(GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)))
                .fetchAny();

        if (record == null) {
            return null;
        }
        GoodsVo goodsVo = record.into(GoodsVo.class);

        // 反序列化商品海报分享信息
        GoodsSharePostConfig goodsSharePostConfig = Util.parseJson(goodsVo.getShareConfig(), GoodsSharePostConfig.class);
        goodsSharePostConfig.setShareImgPath(goodsSharePostConfig.getShareImgUrl());
        goodsVo.setGoodsSharePostConfig(goodsSharePostConfig);
        return goodsVo;
    }

    /**
     * 获取指定条件的商品的数量
     *
     * @param condition 指定的条件
     * @return 商品的数量
     */
    public Integer getGoodsNumDao(Condition condition) {
        return db().fetchCount(GOODS, condition);
    }

    /**
     * Exist boolean.商品是否存在
     *
     * @param goodsId the goods id
     * @return the boolean
     */
    public boolean exist(Integer goodsId) {
        return db().fetchExists(GOODS, GOODS.GOODS_ID.eq(goodsId).and(GOODS.DEL_FLAG.eq(BYTE_ZERO)));
    }

    /**
     * 查询当前商品所处于的活动类型，秒杀、预售、砍价、拼团或其它
     *
     * @param goodsId 商品id
     * @return {@link GoodsActivityType} 活动类型信息,如果商品不属于上述四种活动则返回null
     */
    private GoodsActivityType getGoodsActivityType(Integer goodsId) {
        GoodsActivityType type = new GoodsActivityType();
        Timestamp now = DateUtils.getLocalDateTime();
        // 秒杀
        Map<Integer, List<Record3<Integer, Integer, BigDecimal>>> goodsSecKillListInfo = secKillProcessorDao.getGoodsSecKillListInfo(Collections.singletonList(goodsId), now);
        if (goodsSecKillListInfo.containsKey(goodsId)) {
            Record3<Integer, Integer, BigDecimal> record3 = goodsSecKillListInfo.get(goodsId).get(0);
            type.setActivityId(record3.get(SEC_KILL_PRODUCT_DEFINE.SK_ID));
            type.setActivityType(BaseConstant.ACTIVITY_TYPE_SEC_KILL);
            return type;
        }
        // 预售
        Map<Integer, List<Record3<Integer, Integer, BigDecimal>>> goodsPreSaleListInfo = preSaleProcessorDao.getGoodsPreSaleListInfo(Collections.singletonList(goodsId), now);
        if (goodsPreSaleListInfo.containsKey(goodsId)) {
            Record3<Integer, Integer, BigDecimal> record3 = goodsPreSaleListInfo.get(goodsId).get(0);
            type.setActivityId(record3.get(PRESALE.ID));
            type.setActivityType(BaseConstant.ACTIVITY_TYPE_PRE_SALE);
            return type;
        }

        // 砍价
        Map<Integer, BargainGoodsPriceBo> goodsBargainListInfo = bargainProcessorDao.getGoodsBargainListInfo(Collections.singletonList(goodsId), now);
        if (goodsBargainListInfo.containsKey(goodsId)) {
            BargainGoodsPriceBo bargain = goodsBargainListInfo.get(goodsId);
            type.setActivityId(bargain.getId());
            type.setActivityType(BaseConstant.ACTIVITY_TYPE_BARGAIN);
            return type;
        }

        // 拼团
        Map<Integer, List<Record3<Integer, Integer, BigDecimal>>> goodsGroupBuyListInfo = groupBuyProcessorDao.getGoodsGroupBuyListInfo(Collections.singletonList(goodsId), now);
        if (goodsGroupBuyListInfo.containsKey(goodsId)) {
            Record3<Integer, Integer, BigDecimal> record3 = goodsGroupBuyListInfo.get(goodsId).get(0);
            type.setActivityId(record3.get(GROUP_BUY_PRODUCT_DEFINE.ACTIVITY_ID));
            type.setActivityType(BaseConstant.ACTIVITY_TYPE_GROUP_BUY);
            return type;
        }
        // 其它普通商品
        return null;
    }

    /**
     * 得到指定条件下的商品id集合
     *
     * @param catIds   指定平台分类id,逗号分隔的字符串
     * @param sortIds  指定商家分类id,逗号分隔的字符串
     * @param brandIds 指定品牌id,逗号分隔的字符串
     *                 多种条件取并集
     * @return 商品id
     */
    public List<Integer> getOnShelfGoodsIdList(List<Integer> catIds, List<Integer> sortIds, List<Integer> brandIds) {
        SelectConditionStep<Record1<Integer>> selectConditionStep = db().select(GOODS.GOODS_ID)
                .from(GOODS)
                .where(GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
                .and(GOODS.IS_ON_SALE.eq(GoodsConstant.ON_SALE));

        Condition filterCondition = DSL.noCondition();
        //指定平台分类
        if (CollectionUtils.isNotEmpty(catIds)) {
            filterCondition = filterCondition.or(GOODS.CAT_ID.in(saas.sysCate.getAllChild(catIds)));
        }
        //指定商家分类
        if (CollectionUtils.isNotEmpty(sortIds)) {
            //在所有父子节点中查找
            filterCondition = filterCondition.or(GOODS.SORT_ID.in(goodsSort.getChildrenIdByParentIdsDao(sortIds)));
        }
        //指定品牌
        if (CollectionUtils.isNotEmpty(brandIds)) {
            filterCondition = filterCondition.or(GOODS.BRAND_ID.in(brandIds));
        }
        selectConditionStep.and(filterCondition);

        // 是否展示售罄
        Byte soldOutGoods = configService.shopCommonConfigService.getSoldOutGoods();
        if (soldOutGoods == null || soldOutGoods.equals(NumberUtils.BYTE_ZERO)) {
            selectConditionStep.and(GOODS.GOODS_NUMBER.greaterThan(NumberUtils.INTEGER_ZERO));
        }
        return selectConditionStep.fetchInto(Integer.class);
    }

    /**
     * 所有设置了专享商品标志的商品ID
     *
     * @return
     */
    public List<Integer> getAllExclusiveGoodsIds() {
        return db().select(GOODS.GOODS_ID).from(GOODS).where(GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)).and(GOODS.IS_CARD_EXCLUSIVE.eq(GoodsConstant.CARD_EXCLUSIVE)).fetchInto(Integer.class);
    }

    /**
     * 刷新goods表里的comment_num
     *
     * @param goodsId
     */
    public void updateGoodsCommentNum(int goodsId) {
        int goodsComm = goodsComment.getGoodsCommentNum(goodsId);
        db().update(GOODS).set(GOODS.COMMENT_NUM, goodsComm).where(GOODS.GOODS_ID.eq(goodsId)).execute();

        //es更新
        try {
            if (esUtilSearchService.esState()) {
                esGoodsCreateService.updateEsGoodsIndex(goodsId, getShopId());
            }
        } catch (Exception e) {
            logger().debug("商品修改-同步es数据异常：" + e.getMessage());
        }
    }

    /**
     * 根据推荐商品id集合分页获取商品
     *
     * @param param
     * @return
     */
    public PageResult<GoodsVo> selectPage(DistributionRecommendGoodsParam param) {
        if (param.getCurrentPage() <= 0) {
            param.setCurrentPage(1);
        }

        String[] split = param.getRecommendGoodsId().split(",");

        PageResult<GoodsVo> result = new PageResult<>();
        Integer currentPage = param.getCurrentPage();
        Integer pageRows = param.getPageRows();
        result.page = Page.getPage(split.length, currentPage, pageRows);

        List<GoodsVo> goodsVoList = new ArrayList<>();
        GoodsVo goodsVo;

        //分页处理-优化性能
        for (int i = (currentPage - 1) * pageRows, j = 0;
             (currentPage - 1) * pageRows < split.length && j < split.length - (currentPage - 1) * pageRows && j < pageRows;
             i++, j++) {
            goodsVo = this.select(Integer.parseInt(split[i]));
            goodsVoList.add(goodsVo);
        }

        result.setDataList(goodsVoList);
        return result;
    }

    /**
     * 通过分类id集合获取商品id集合
     *
     * @param sortId
     * @return
     */
    public List<Integer> listGoodsId(List<Integer> sortId) {
        return goodsDao.listGoodsId(sortId);
    }

    /**
     * 根据prdId获取商品名称
     *
     * @param prdId
     * @return
     */
    public String getGoodsNameByPrdId(Integer prdId) {
        return goodsDao.getGoodsNameByPrdId(prdId);
    }

    public List<Integer> getGoodsIdsByLabelId(Integer labelId){
        Condition condition = DSL.noCondition();
        GoodsPageListParam goodsPageListParam = new GoodsPageListParam();
        goodsPageListParam.setLabelId(labelId);
        condition = this.buildLabelOptions(condition, goodsPageListParam);
        return db().select(GOODS.GOODS_ID).from(GOODS).where(condition).fetchInto(Integer.class);
    }
}
