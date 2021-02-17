package com.meidianyi.shop.service.shop.store.store;

import com.google.common.collect.Maps;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalRequestConstant;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalRequestResult;
import com.meidianyi.shop.common.pojo.shop.table.StoreDo;
import com.meidianyi.shop.dao.shop.store.StoreDao;
import com.meidianyi.shop.db.shop.tables.records.ArticleRecord;
import com.meidianyi.shop.db.shop.tables.records.StoreGroupRecord;
import com.meidianyi.shop.db.shop.tables.records.StoreRecord;
import com.meidianyi.shop.service.foundation.exception.BusinessException;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.saas.shop.ShopConst;
import com.meidianyi.shop.service.pojo.shop.config.trade.OrderProcessParam;
import com.meidianyi.shop.service.pojo.shop.image.ShareQrCodeVo;
import com.meidianyi.shop.service.pojo.shop.member.address.UserAddressVo;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.qrcode.QrCodeTypeEnum;
import com.meidianyi.shop.service.pojo.shop.store.account.StoreInfo;
import com.meidianyi.shop.service.pojo.shop.store.article.ArticleParam;
import com.meidianyi.shop.service.pojo.shop.store.article.ArticlePojo;
import com.meidianyi.shop.service.pojo.shop.store.goods.StoreGoodsBaseCheckInfo;
import com.meidianyi.shop.service.pojo.shop.store.goods.StoreGoodsCheckQueryParam;
import com.meidianyi.shop.service.pojo.shop.store.goods.StoreGoodsCheckQueryVo;
import com.meidianyi.shop.service.pojo.shop.store.group.StoreGroup;
import com.meidianyi.shop.service.pojo.shop.store.group.StoreGroupQueryParam;
import com.meidianyi.shop.service.pojo.shop.store.store.*;
import com.meidianyi.shop.service.pojo.wxapp.order.address.OrderAddressParam;
import com.meidianyi.shop.service.saas.overview.ShopOverviewService;
import com.meidianyi.shop.service.shop.config.TradeService;
import com.meidianyi.shop.service.shop.image.QrCodeService;
import com.meidianyi.shop.service.shop.store.comment.ServiceCommentService;
import com.meidianyi.shop.service.shop.store.group.StoreGroupService;
import com.meidianyi.shop.service.shop.store.postsale.ServiceTechnicianService;
import com.meidianyi.shop.service.shop.store.service.ServiceOrderService;
import com.meidianyi.shop.service.shop.store.service.StoreServiceService;
import com.meidianyi.shop.service.shop.store.verify.StoreVerifierService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.tools.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.*;

import static com.meidianyi.shop.dao.shop.store.StoreDao.STORE_TYPE_HOSPITAL;
import static com.meidianyi.shop.db.shop.tables.Article.ARTICLE;
import static com.meidianyi.shop.db.shop.tables.CommentService.COMMENT_SERVICE;
import static com.meidianyi.shop.db.shop.tables.Store.STORE;
import static com.meidianyi.shop.db.shop.tables.StoreGoods.STORE_GOODS;
import static com.meidianyi.shop.db.shop.tables.StoreGroup.STORE_GROUP;
import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.*;
import static com.meidianyi.shop.service.pojo.shop.store.store.StorePojo.IS_EXIST_HOSPITAL;
import static org.apache.commons.lang3.math.NumberUtils.BYTE_ZERO;


/**
 * @author 王兵兵
 * <p>
 * 2019年7月4日
 */
@Slf4j
@Service
public class StoreService extends ShopBaseService {

    /**
     * 营业
     */
    private static byte BUSINESS_STATE_ON = 1;

    /**
     * 关店
     */
    private static byte BUSINESS_STATE_OFF = 1;
    /**
     * 核销员
     */
    @Autowired
    public StoreVerifierService storeVerifier;

    /**
     * 门店商品
     */
    @Autowired
    public StoreGoodsService storeGoods;

    /**
     * 门店服务
     */
    @Autowired
    public StoreServiceService storeService;

    /**
     * 门店分组
     */
    @Autowired
    public StoreGroupService storeGroup;

    /**
     * 技师管理
     */
    @Autowired
    public ServiceTechnicianService serviceTechnician;

    /**
     * 服务预约（serviceOrder）
     */
    @Autowired
    public ServiceOrderService serviceOrder;

    /**
     * 服务评价管理
     */
    @Autowired
    public ServiceCommentService serviceComment;
    /**
     * The Wx service.小程序端接口调用
     */
    @Autowired
    public StoreWxService wxService;

    @Autowired
    public TradeService trade;

    /**
     * The Reservation.小程序端门店服务预约
     */
    @Autowired
    public StoreReservation reservation;
    /**
     * 店铺等级
     */
    @Autowired
    public ShopOverviewService shopOverviewService;
    @Autowired
    public StoreDao storeDao;

    /**
     * 门店列表分页查询
     * @param param
     * @return StorePageListVo
     */
    public StoreVo getPageList(StoreListQueryParam param) {
        SelectWhereStep<? extends Record> select = db().select(
            STORE.STORE_ID, STORE.STORE_NAME,STORE.STORE_CODE ,STORE.POS_SHOP_ID, STORE_GROUP.GROUP_NAME, STORE.PROVINCE_CODE,
                STORE.CITY_CODE, STORE.DISTRICT_CODE, STORE.ADDRESS, STORE.MANAGER,STORE.STORE_EXPRESS,
            STORE.MOBILE, STORE.OPENING_TIME, STORE.CLOSE_TIME, STORE.BUSINESS_STATE, STORE.AUTO_PICK, STORE.BUSINESS_TYPE, STORE.CITY_SERVICE,
            STORE.STORE_TYPE
        ).from(STORE)
            .leftJoin(STORE_GROUP).on(STORE.GROUP.eq(STORE_GROUP.GROUP_ID));

        select = this.buildOptions(select, param);
        select.where(STORE.DEL_FLAG.eq(DelFlag.NORMAL.getCode())).orderBy(STORE.CREATE_TIME.desc(), STORE.STORE_TYPE.desc());
        PageResult<StorePageListVo> pageResult = getPageResult(select, param.getCurrentPage(), param.getPageRows(), StorePageListVo.class);
        Integer totalNum = 0;
        String shopVersion = shopOverviewService.getShopVersion(getShopId());
        if (ShopConst.ShopType.V_1.equals(shopVersion)) {
            totalNum = 1;
        } else if (ShopConst.ShopType.V_2.equals(shopVersion)) {
            totalNum = 5;
        } else if (ShopConst.ShopType.V_3.equals(shopVersion)) {
            totalNum = 10;
        } else if (ShopConst.ShopType.V_4.equals(shopVersion)) {
            totalNum = 200;
        }
        Integer nowNum = pageResult.getDataList().size();
        Integer canCreateNum = totalNum - nowNum;
        StoreVo storeVo = new StoreVo();
        storeVo.setShopVersion(shopVersion);
        storeVo.setCanCreateNum(canCreateNum);
        storeVo.setStorePageListVo(pageResult);
        return storeVo;
    }

    public SelectWhereStep<? extends Record> buildOptions(SelectWhereStep<? extends Record> select, StoreListQueryParam param) {
        if (param == null) {
            return select;
        }
        if (!StringUtils.isEmpty(param.getKeywords())) {
            select.where(STORE.STORE_NAME.contains(param.getKeywords()).or(STORE.MANAGER.contains(param.getKeywords())).or(STORE.STORE_CODE.like(param.getKeywords())));
        }
        //查询条件-营业状态
        if (param.getBusinessState() != null) {
            select.where(STORE.BUSINESS_STATE.eq(param.getBusinessState()));
        }
        //查询条件-门店自提
        if (param.getAutoPick() != null) {
            select.where(STORE.AUTO_PICK.eq(param.getAutoPick()));
        }
        //查询条件-同城配送
        if (param.getCityService() != null) {
            select.where(STORE.CITY_SERVICE.eq(param.getCityService()));
        }
        if (param.getManager() != null) {
            select.where(STORE.MANAGER.like(likeValue(param.getManager())));
        }
        //查询条件-门店过滤
        if (param.getStoreIds() != null) {
            select.where(STORE.STORE_ID.in(param.getStoreIds()));
        }
        return select;
    }

    /**
     * 新增门店
     * @param store
     * @return
     */
    public Byte addStore(StorePojo store){
        // 判断是否存在医院类型门店
        if (storeDao.isExistHospitalStore() && STORE_TYPE_HOSPITAL.equals(store.getStoreType())) {
            return IS_EXIST_HOSPITAL;
        }
        if (store.getPickDetail() != null) {
            store.setPickTimeDetail(Util.toJson(store.getPickDetail()));
        }
        StoreRecord record = new StoreRecord();
        this.assign(store, record);
        return db().executeInsert(record) > 0 ? YES : NO;
    }

    /**
     * 更新门店
     * @param store
     * @return
     */
    public Boolean updateStore(StorePojo store) {
        if (store.getPickDetail() != null) {
            store.setPickTimeDetail(Util.toJson(store.getPickDetail()));
        }
        StoreRecord record = new StoreRecord();
        this.assign(store, record);
        if (store.getGroup() == null || store.getGroup() <= 0) {
            record.setGroup(null);
        }
        return db().executeUpdate(record) > 0 ? true : false;
    }

    /**
     * 批量更新门店信息
     */
    public void batchUpdateStore(List<StorePojo> storeList) {
        List<StoreRecord> resultList = new ArrayList<StoreRecord>(64) {
            private static final long serialVersionUID = 8882723512039998814L;

            {
                storeList.forEach(store -> {
                    if (store.getPickDetail() != null) {
                        store.setPickTimeDetail(Util.toJson(store.getPickDetail()));
                    }
                    StoreRecord record = new StoreRecord();
                    assign(store, record);
                    add(record);
                });
            }
        };
        db().batchUpdate(resultList).execute();
    }

    /**
     * 删除门店
     * @param storeId
     * @return
     */
    public Boolean delStore(Integer storeId) {
        return db().update(STORE).set(STORE.DEL_FLAG, DelFlag.DISABLE.getCode()).where(STORE.STORE_ID.eq(storeId)).execute() > 0;
    }

    /**
     * 取单个门店信息
     * @param storeId
     * @return StorePojo
     */
    public StorePojo getStore(Integer storeId) {
        StoreRecord r = db().fetchOne(STORE, STORE.STORE_ID.eq(storeId));
        if (r == null) {
            return null;
        }
        StorePojo storePojo = r.into(StorePojo.class);
        if (!StringUtils.isEmpty(storePojo.getPickTimeDetail())) {
            storePojo.setPickDetail(Util.json2Object(storePojo.getPickTimeDetail(), StorePickDetailPojo.class, false));
        }
        return storePojo;
    }

    /**
     * 获取医院类型门店信息
     * @return StorePojo
     */
    public StorePojo getHospitalInfo() {
        return storeDao.getHospitalInfo();
    }

    /**
     * 通过posShopId获取门店信息
     * @param posStoreId 门店使用的pos id
     * @return 门店信息
     */
    public StoreRecord getStoreByPosShopId(Integer posStoreId) {
        return db().selectFrom(STORE)
            .where(STORE.POS_SHOP_ID.eq(posStoreId).and(STORE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))).fetchAny();
    }

    /**
     * 检查门店编码是否可用,返回true表示可用
     * @param posShopId
     * @return Boolean
     */
    public Boolean checkStoreCoding(Integer posShopId) {
        Condition condition = STORE.POS_SHOP_ID.eq(posShopId).and(STORE.DEL_FLAG.eq(DelFlag.NORMAL.getCode()));
        if (null != db().fetchAny(STORE, condition)) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * 门店分组列表-查询
     * @param param
     * @return
     */
    public PageResult<StoreGroup> getStoreGroupPageList(StoreGroupQueryParam param) {
        SelectWhereStep<? extends Record> select = db().select(STORE_GROUP.GROUP_ID, STORE_GROUP.GROUP_NAME,
            STORE_GROUP.CREATE_TIME, DSL.count(STORE.GROUP).as("numbers"))
            .from(STORE_GROUP)
            .leftJoin(STORE).on(STORE.GROUP.eq(STORE_GROUP.GROUP_ID));
        buildParams(select, param);
        select.groupBy(STORE_GROUP.GROUP_ID).orderBy(STORE_GROUP.CREATE_TIME.asc());
        if (null != param.getCurrentPage()) {
            return getPageResult(select, param.getCurrentPage(), param.getPageRows(), StoreGroup.class);
        } else {
            return getPageResult(select, param.getPageRows(), StoreGroup.class);
        }
    }

    public void buildParams(SelectWhereStep<? extends Record> select, StoreGroupQueryParam param) {
        if (param != null) {
            if (param.getGroupName() != null && !"".equals(param.getGroupName())) {
                if (param.isNeedAccurateQuery()) {
                    select.where(STORE_GROUP.GROUP_NAME.eq(param.getGroupName()));
                } else {
                    select.where(STORE_GROUP.GROUP_NAME.like(this.likeValue(param.getGroupName())));
                }
            }
        }
    }

    /**
     * 门店分组-(检查组名是否可用)
     * @param param
     * @return true可用，fasle不可用
     */
    public boolean isStoreGroupExist(StoreGroupQueryParam param) {
        param.setNeedAccurateQuery(Boolean.TRUE);
        SelectWhereStep<? extends Record> select = db().select(STORE_GROUP.GROUP_NAME)
            .from(STORE_GROUP);
        buildParams(select, param);
        return db().fetchCount(select) > 0 ? Boolean.FALSE : Boolean.TRUE;
    }

    /**
     * 门店分组-新增
     * @param param
     * @return
     */
    public int insertStoreGroup(StoreGroupQueryParam param) {
        StoreGroupRecord record = db().newRecord(STORE_GROUP, param);
        return record.insert();
    }

    /**
     * 门店分组-修改
     * @param param
     * @return
     */
    public int updateStoreGroup(StoreGroupQueryParam param) {
        StoreGroupRecord record = db().newRecord(STORE_GROUP, param);
        record.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        return record.update();
    }

    /**
     * 门店分组-删除
     * @param param
     * @return
     */
    public void deleteStoreGroup(StoreGroupQueryParam param) {
        db().transaction(configuration -> {
            DSLContext dslContext = DSL.using(configuration);
            StoreGroupRecord record = dslContext.newRecord(STORE_GROUP, param);
            List<Integer> result = dslContext.select(STORE.STORE_ID)
                .from(STORE)
                .where(STORE.GROUP.eq(param.getGroupId()))
                .fetch(STORE.STORE_ID);
            if (result.size() > 0) {
                dslContext.update(STORE)
                    .set(STORE.GROUP, (Integer) null)
                    .where(STORE.STORE_ID.in(result))
                    .execute();
            }
            dslContext.delete(STORE_GROUP)
                .where(STORE_GROUP.GROUP_ID.eq(param.getGroupId()))
                .execute();
        });
    }

    public List<StoreBasicVo> getStoreListByStoreIds(List<Integer> storeId) {
        logger().info("正在根据门店id数组查询门店基本信息");
        return db().select(STORE.STORE_ID, STORE.STORE_NAME)
            .from(STORE)
            .where(STORE.STORE_ID.in(storeId))
            .and(STORE.DEL_FLAG.eq(DelFlag.NORMAL.getCode()))
            .fetchInto(StoreBasicVo.class);
    }

    /**
     * 获取所有门店id和名称
     */
    public List<StoreBasicVo> getAllStore() {
        logger().info("获取所有门店id和名称");
        return db().select(STORE.STORE_ID, STORE.STORE_NAME)
            .from(STORE)
            .where(STORE.DEL_FLAG.eq(DelFlag.NORMAL.getCode()))
            .fetchInto(StoreBasicVo.class);
    }

    /**
     * 查询所有非医院类型门店
     * @return List<StoreBasicVo>
     */
    public List<StoreBasicVo> getAllStoreWithoutHospital() {
        logger().info("获取除医院类型的门店集合");
        return storeDao.getAllStoreWithoutHospital();
    }

    /**
     * 获取门店名称
     * @param sourceId
     * @return
     */
    public String getStoreName(Integer sourceId) {
        Record1<String> record = db().select(STORE.STORE_NAME).from(STORE)
            .where(STORE.STORE_ID.eq(sourceId)).fetchAny();
        if (record != null) {
            return record.into(String.class);
        }
        return "";
    }

    @Autowired
    QrCodeService qrCodeService;

    /**
     * Share store service share qr code vo.通用分享方法
     * @param qrCodeTypeEnum the qr code type enum
     * @param pathParam      the path param
     * @return the share qr code vo
     */
    public ShareQrCodeVo share(QrCodeTypeEnum qrCodeTypeEnum, String pathParam) {
        String imageUrl = qrCodeService.getMpQrCode(qrCodeTypeEnum, pathParam);
        ShareQrCodeVo vo = new ShareQrCodeVo();
        vo.setImageUrl(imageUrl);
        vo.setPagePath(QrCodeTypeEnum.SHOP_SHARE.getUrl());
        return vo;
    }

    /**
     * 王帅
     * 过滤门店
     * @param expressList 配送方式list
     * @param productIds  规格ids
     * @param address     地址
     * @param isFormStore 是否门店下单（暂时未用）
     * @return
     */
    public List<StorePojo>[] filterExpressList(Byte[] expressList, List<Integer> productIds, UserAddressVo address, byte isFormStore) {
        List<StorePojo>[] result = new ArrayList[4];
        //自提
        if (expressList[OrderConstant.DELIVER_TYPE_SELF] == OrderConstant.YES) {
            result[OrderConstant.DELIVER_TYPE_SELF] = getCanBuyStoreList(productIds, OrderConstant.DELIVER_TYPE_SELF, address, isFormStore);
        }
        //同城配送
        if (expressList[OrderConstant.CITY_EXPRESS_SERVICE] == OrderConstant.YES) {
            result[OrderConstant.CITY_EXPRESS_SERVICE] = getCanBuyStoreList(productIds, OrderConstant.CITY_EXPRESS_SERVICE, address, isFormStore);
        }
        return result;
    }

    /**
     * 王帅
     * @param productIds  规格ids
     * @param express     配送方式
     * @param address     地址
     * @param isFormStore ??
     * @return
     */
    public List<StorePojo> getCanBuyStoreList(List<Integer> productIds, byte express, UserAddressVo address, byte isFormStore) {
        //条件
        Condition condition = STORE_GOODS.PRD_ID.in(productIds).and(STORE_GOODS.IS_ON_SALE.eq(StoreGoodsService.ON_SALE))
                .and(STORE.BUSINESS_STATE.eq(BUSINESS_STATE_ON)).and(STORE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE));
        //自提
        condition = express == OrderConstant.DELIVER_TYPE_SELF ? condition.and(STORE.AUTO_PICK.eq((short) OrderConstant.YES)) : condition;
        //TODO 同城配送
        condition = express == OrderConstant.CITY_EXPRESS_SERVICE ? condition.and(STORE.AUTO_PICK.eq((short) OrderConstant.YES)) : condition;
        //获取门店id
        List<Integer> storeIds = db().select(STORE.STORE_ID, DSL.count(STORE.STORE_ID))
                .from(STORE)
                .innerJoin(STORE_GOODS).on(STORE.STORE_ID.eq(STORE_GOODS.STORE_ID)).
            where(condition).
            groupBy(STORE.STORE_ID).
            having(DSL.count(STORE.STORE_ID).eq(productIds.size())).
            fetch(STORE.STORE_ID);
        if (CollectionUtils.isEmpty(storeIds)) {
            return null;
        }
        List<StorePojo> storeList = db().select().from(STORE).where(STORE.STORE_ID.in(storeIds)).fetchInto(StorePojo.class);
        if (express == OrderConstant.CITY_EXPRESS_SERVICE) {
            //TODO 同城配送特殊处理
            storeList = storeList;
        }
        return storeList;
    }

    /**
     * Gets charge store list.所有服务评价待审核的门店列表
     * @return the charge store list
     */
    public Map<Integer, String> getChargeStoreList() {
        return db().selectDistinct(COMMENT_SERVICE.STORE_ID, STORE.STORE_NAME).from(COMMENT_SERVICE)
            .leftJoin(STORE).on(COMMENT_SERVICE.STORE_ID.eq(STORE.STORE_ID))
            .where(COMMENT_SERVICE.DEL_FLAG.eq(BYTE_ZERO))
            .and(COMMENT_SERVICE.FLAG.eq(BYTE_ZERO))
            .fetchMap(COMMENT_SERVICE.STORE_ID, STORE.STORE_NAME);
    }

    /**
     * 获得门店数量
     * @param storeIds
     * @return
     */
    public Integer getStoreNum(List<Integer> storeIds) {
        return db().select(DSL.sum(STORE.STORE_ID)).from(STORE).where(STORE.STORE_ID.in(storeIds)).fetchAnyInto(Integer.class);
    }

    /**
     * 根据id列表获取店铺
     * @param storeIds
     * @return
     */
    public List<StoreInfo> getStoreByIds(List<Integer> storeIds) {
        return db().selectFrom(STORE).where(STORE.STORE_ID.in(storeIds)).fetchInto(StoreInfo.class);
    }

    /**
     * 根据id列表获取店铺
     * @param storeIds
     * @return
     */
    public List<StoreOrderVo> getStoreInfoByIds(List<Integer> storeIds) {
        return db().selectFrom(STORE).where(STORE.STORE_ID.in(storeIds)).fetchInto(StoreOrderVo.class);
    }

    /**
     * 获取所有门店id和名称
     */
    public List<StoreInfo> getAllStores() {
        logger().info("获取所有门店id和名称");
        return db().select(STORE.STORE_ID, STORE.STORE_NAME)
            .from(STORE).fetchInto(StoreInfo.class);
    }

    /**
     * 新增公告
     * @param articlePojo
     * @return
     */
    public Boolean addArticle(ArticlePojo articlePojo) {
        ArticleRecord record = db().newRecord(ARTICLE, articlePojo);
        if (StringUtils.isEmpty(record.getTitle()) || "".equals(articlePojo.getTitle())) {
            throw new BusinessException(JsonResultCode.CODE_PARAM_ERROR, ",标题不能为空");
        }
        return db().executeInsert(record) > 0 ? true : false;
    }

    /**
     * 更新公告
     * @param articlePojo
     * @return
     */
    public Boolean updateArticle(ArticlePojo articlePojo) {
        ArticleRecord record = db().newRecord(ARTICLE, articlePojo);
        if (StringUtils.isEmpty(record.getTitle()) || "".equals(articlePojo.getTitle())) {
            throw new BusinessException(JsonResultCode.CODE_PARAM_ERROR, ",标题不能为空");
        }
        return db().executeUpdate(record) > 0;
    }

    /**
     * 删除公告
     * @param articleId
     * @return
     */
    public Boolean delArticle(Integer articleId) {
        return db().update(ARTICLE).set(ARTICLE.IS_DEL, DelFlag.DISABLE.getCode()).where(ARTICLE.ARTICLE_ID.eq(articleId)).execute() > 0 ? true : false;
    }

    /**
     * 取单个公告信息
     * @param articleId
     * @return ArticlePojo
     */
    public ArticlePojo getArticle(Integer articleId) {
        ArticleRecord r = db().fetchOne(ARTICLE, ARTICLE.ARTICLE_ID.eq(articleId));
        if (r == null) {
            return null;
        }
        return r.into(ArticlePojo.class);
    }

    /**
     * 门店公告分页查询
     * @param param 标题 发布状态
     * @return 分页信息
     */
    public PageResult<ArticlePojo> articleList(ArticleParam param) {
        SelectConditionStep<? extends Record> sql = db().select()
            .from(ARTICLE)
            .where(ARTICLE.IS_DEL.eq(DelFlag.NORMAL_VALUE));
        //查询条件-标题
        if (param.getTitle() != null && !"".equals(param.getTitle())) {
            sql.and(ARTICLE.TITLE.like(this.likeValue(param.getTitle())));
        }
        //查询条件-发布状态
        if (!ArticleParam.ALL_STATUS.equals(param.getStatus())) {
            sql.and(ARTICLE.STATUS.eq(param.getStatus()));
        }
        PageResult<ArticlePojo> result = this.getPageResult(sql, param.getCurrentPage(), param.getPageRows(), ArticlePojo.class);
        return result;
    }

    /**
     * 发布公告
     * @param articleId
     * @return
     */
    public Boolean releaseArticle(Integer articleId) {
        return db().update(ARTICLE).set(ARTICLE.STATUS, NumberUtils.BYTE_ONE).where(ARTICLE.ARTICLE_ID.eq(articleId)).execute() > 0 ? true : false;
    }

    /**
     * 获取门店和自提按钮开关
     */
    public StoreConfigVo getStoreBtnConfig() {
        OrderProcessParam config = trade.getOrderProcessConfig();
        StoreConfigVo storeConfigVo = new StoreConfigVo();
        storeConfigVo.setFetch(config.getFetch());
        storeConfigVo.setCityService(config.getCityService());
        return storeConfigVo;
    }

    public StoreBasicVo getStoreByNo(String storeNo) {
        return storeDao.getStoreByNo(storeNo);
    }

    /**
     * 查询当前在营业的门店列表(传地址)
     * @param orderAddressParam 地址经纬度
     * @return Map<Double, StoreDo>
     */
    public Map<String, StoreDo> getStoreListOpen(OrderAddressParam orderAddressParam) throws MpException {
        // 三方库拉取可用门店列表 **
        // List<String> storeCodes = checkStoreGoods(orderAddressParam.getStoreGoodsBaseCheckInfoList());
        // 不拉取三方库，校验本地可用门店
        Double storeDistance = trade.getStoreDistance();
        // 查询是否有上架当前药品，库存大于0的门店
        List<String> storeCodes = storeGoods.checkStoreGoodsIsOnSale(orderAddressParam.getStoreGoodsBaseCheckInfoList());
        // 药品在门店没库存或没上架
        if (storeCodes.isEmpty()) {
            throw new MpException(JsonResultCode.CODE_STORE_GOODS_IS_EMPTY);
        }
        List<String> storeCodesNew = new ArrayList<String>(new TreeSet<String>(storeCodes));
        // 查询可以购买当前药品的门店是否在营业
        List<StoreDo> stores = storeDao.getStoreOpen(storeCodesNew, orderAddressParam.getDeliveryType());
        // 提示当前门店未营业
        if (stores.isEmpty()) {
            throw new MpException(JsonResultCode.CODE_NO_STORE_OPEN);
        }
        logger().info("门店库存校验{}", stores);
        Map<String, StoreDo> map = new HashMap<>(15);
        stores.forEach(e -> {
            double distance = Util.getDistance(Double.parseDouble(orderAddressParam.getLng()),
                Double.parseDouble(orderAddressParam.getLat()),
                Double.parseDouble(e.getLongitude()),
                Double.parseDouble(e.getLatitude()));
            if (distance <= storeDistance) {
                map.put(formatDouble(distance), e);
            }
        });
        // 超出配送距离
        if (map.size() == 0) {
            throw new MpException(JsonResultCode.CODE_STORE_OUT_OF_DISTANCE);
        }
        sortByKey(map, false);
        return map;
    }

    /**
     * 查询当前在营业的门店列表(不传地址)
     * @return Map<Double, StoreDo>
     */
    public Map<String, StoreDo> getStoreListOpen(List<StoreGoodsBaseCheckInfo> storeGoodsBaseCheckInfoList) {
        // 三方库拉取可用门店列表 **
        // List<String> storeCodes = checkStoreGoods(storeGoodsBaseCheckInfoList);
        // 不拉取三方库，校验本地可用门店
        List<String> storeCodes = storeGoods.checkStoreGoodsIsOnSale(storeGoodsBaseCheckInfoList);
        List<String> storeCodesNew = new ArrayList<String>(new TreeSet<String>(storeCodes));
        List<StoreDo> stores = storeDao.getStoreOpen(storeCodesNew, DELIVER_TYPE_COURIER);
        logger().info("门店库存校验{}", stores);
        Map<String, StoreDo> map = new IdentityHashMap<>(15);
        stores.forEach(e -> {
            map.put(formatDouble(0D), e);
        });
        return map;
    }

    /**
     * @param d double值
     * @return String
     */
    public static String formatDouble(double d) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(d);
    }

    /**
     * 调取药房接口查询库存是否充足 供getStoreListOpen方法调用
     * @param storeGoodsBaseCheckInfoList 药品列表
     * @return List<String> 返回null表示接口校验异常，否则表示可用的药店编码集合
     */
    private List<String> checkStoreGoods(List<StoreGoodsBaseCheckInfo> storeGoodsBaseCheckInfoList) {
        StoreGoodsCheckQueryParam param = new StoreGoodsCheckQueryParam();
        param.setGoodsItems(storeGoodsBaseCheckInfoList);

        String appId = ApiExternalRequestConstant.APP_ID_STORE;
        Integer shopId = getShopId();
        String serviceName = ApiExternalRequestConstant.SERVICE_NAME_GET_STOCK_ENOUGH_SHOP_LIST;
        ApiExternalRequestResult apiExternalRequestResult = saas().apiExternalRequestService.externalRequestGate(appId, shopId, serviceName, Util.toJson(param));
        // 数据拉取错误
        if (!ApiExternalRequestConstant.ERROR_CODE_SUCCESS.equals(apiExternalRequestResult.getError())) {
            logger().debug("获取药品库存足够门店接口异常：error " + apiExternalRequestResult.getError() + ",msg " + apiExternalRequestResult.getMsg());
            return null;
        }
        String dataJson = apiExternalRequestResult.getData();
        StoreGoodsCheckQueryVo storeGoodsCheckQueryVo = Util.parseJson(dataJson, StoreGoodsCheckQueryVo.class);
        if (storeGoodsCheckQueryVo == null) {
            return null;
        } else {
            return storeGoodsCheckQueryVo.getShopList();
        }
    }

    /**
     * 根据map的key排序
     * @param map    待排序的map
     * @param isDesc 是否降序，true：降序，false：升序
     * @return 排序好的map
     */
    private static <K extends Comparable<? super K>, V> Map<K, V> sortByKey(Map<K, V> map, boolean isDesc) {
        Map<K, V> result = Maps.newLinkedHashMap();
        if (isDesc) {
            map.entrySet().stream().sorted(Map.Entry.<K, V>comparingByKey().reversed())
                .forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        } else {
            map.entrySet().stream().sorted(Map.Entry.<K, V>comparingByKey())
                .forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        }
        return result;
    }

    /**
     * 获取所有门店id和名称
     */
    public List<StoreBasicVo> getAllStoreForLeader(List<Integer> storeIds) {
        logger().info("获取所有门店id和名称");
        return db().select(STORE.STORE_ID, STORE.STORE_NAME)
            .from(STORE)
            .where(STORE.DEL_FLAG.eq(DelFlag.NORMAL.getCode()))
            .and(STORE.STORE_ID.in(storeIds))
            .fetchInto(StoreBasicVo.class);
    }

}
