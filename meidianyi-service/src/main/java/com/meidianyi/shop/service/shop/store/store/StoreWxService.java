package com.meidianyi.shop.service.shop.store.store;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.SmsApiConfig;
import com.meidianyi.shop.dao.main.StoreAccountDao;
import com.meidianyi.shop.dao.shop.address.UserAddressDao;
import com.meidianyi.shop.dao.shop.order.OrderGoodsDao;
import com.meidianyi.shop.dao.shop.order.OrderInfoDao;
import com.meidianyi.shop.dao.shop.order.ReturnOrderDao;
import com.meidianyi.shop.dao.shop.pharmacist.PharmacistDao;
import com.meidianyi.shop.dao.shop.store.StoreDao;
import com.meidianyi.shop.dao.shop.user.UserDao;
import com.meidianyi.shop.db.shop.tables.records.StoreOrderRecord;
import com.meidianyi.shop.db.shop.tables.records.StoreRecord;
import com.meidianyi.shop.db.shop.tables.records.UserRecord;
import com.meidianyi.shop.service.foundation.exception.BusinessException;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.jedis.JedisManager;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.auth.AuthConstant;
import com.meidianyi.shop.service.pojo.shop.goods.spec.GoodsSpecProduct;
import com.meidianyi.shop.service.pojo.shop.member.address.UserAddressVo;
import com.meidianyi.shop.service.pojo.shop.member.card.CardConstant;
import com.meidianyi.shop.service.pojo.shop.member.card.MemberCardPojo;
import com.meidianyi.shop.service.pojo.shop.member.card.ValidUserCardBean;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.goods.OrderGoodsVo;
import com.meidianyi.shop.service.pojo.shop.order.invoice.InvoiceVo;
import com.meidianyi.shop.service.pojo.shop.store.account.StoreAccountVo;
import com.meidianyi.shop.service.pojo.shop.store.service.StoreServiceCategoryListQueryParam;
import com.meidianyi.shop.service.pojo.shop.store.service.StoreServiceCategoryListQueryVo;
import com.meidianyi.shop.service.pojo.shop.store.store.StorePojo;
import com.meidianyi.shop.service.pojo.wxapp.login.WxAppSessionUser;
import com.meidianyi.shop.service.pojo.wxapp.pay.base.WebPayVo;
import com.meidianyi.shop.service.pojo.wxapp.store.Location;
import com.meidianyi.shop.service.pojo.wxapp.store.StoreInfoParam;
import com.meidianyi.shop.service.pojo.wxapp.store.StoreInfoVo;
import com.meidianyi.shop.service.pojo.wxapp.store.StoreListParam;
import com.meidianyi.shop.service.pojo.wxapp.store.StoreOrderTran;
import com.meidianyi.shop.service.pojo.wxapp.store.StorePayOrderInfo;
import com.meidianyi.shop.service.pojo.wxapp.store.StorePayOrderVo;
import com.meidianyi.shop.service.pojo.wxapp.store.showmain.StoreClerkAuthParam;
import com.meidianyi.shop.service.pojo.wxapp.store.showmain.StoreMainShowVo;
import com.meidianyi.shop.service.pojo.wxapp.store.showmain.StoreMonthStatisticVo;
import com.meidianyi.shop.service.pojo.wxapp.store.showmain.StoreOrderConstant;
import com.meidianyi.shop.service.pojo.wxapp.store.showmain.StoreOrderListParam;
import com.meidianyi.shop.service.pojo.wxapp.store.showmain.StoreOrderListVo;
import com.meidianyi.shop.service.pojo.wxapp.store.showmain.StoreStatisticVo;
import com.meidianyi.shop.service.saas.region.ProvinceService;
import com.meidianyi.shop.service.saas.shop.ShopService;
import com.meidianyi.shop.service.shop.config.ShopCommonConfigService;
import com.meidianyi.shop.service.shop.config.StoreConfigService;
import com.meidianyi.shop.service.shop.config.TradeService;
import com.meidianyi.shop.service.shop.goods.GoodsSpecProductService;
import com.meidianyi.shop.service.shop.member.AccountService;
import com.meidianyi.shop.service.shop.member.BaseScoreCfgService;
import com.meidianyi.shop.service.shop.member.MemberCardService;
import com.meidianyi.shop.service.shop.member.ScoreCfgService;
import com.meidianyi.shop.service.shop.member.ScoreService;
import com.meidianyi.shop.service.shop.member.UserCardService;
import com.meidianyi.shop.service.shop.member.dao.UserCardDaoService;
import com.meidianyi.shop.service.shop.order.invoice.InvoiceService;
import com.meidianyi.shop.service.shop.order.record.OrderActionService;
import com.meidianyi.shop.service.shop.order.ship.ShipInfoService;
import com.meidianyi.shop.service.shop.order.store.StoreOrderService;
import com.meidianyi.shop.service.shop.payment.MpPaymentService;
import com.meidianyi.shop.service.shop.payment.PaymentService;
import com.meidianyi.shop.service.shop.store.postsale.ServiceTechnicianService;
import com.meidianyi.shop.service.shop.store.service.ServiceOrderService;
import com.meidianyi.shop.service.shop.store.service.StoreServiceService;
import com.meidianyi.shop.service.shop.user.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.meidianyi.shop.dao.shop.store.StoreDao.STORE_TYPE_NORMAL_STORE;
import static com.meidianyi.shop.db.shop.tables.Store.STORE;
import static com.meidianyi.shop.db.shop.tables.StoreGoods.STORE_GOODS;
import static com.meidianyi.shop.db.shop.tables.StoreOrder.STORE_ORDER;
import static com.meidianyi.shop.db.shop.tables.User.USER;
import static com.meidianyi.shop.service.pojo.wxapp.store.StoreConstant.PAY_SUCCESS;
import static com.meidianyi.shop.service.pojo.wxapp.store.StoreConstant.PAY_SUCCESS_NAME;
import static com.meidianyi.shop.service.pojo.wxapp.store.StoreConstant.STORE_BUY;
import static com.meidianyi.shop.service.shop.store.store.StoreService.formatDouble;
import static java.util.Collections.EMPTY_LIST;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.math.NumberUtils.BYTE_ONE;
import static org.apache.commons.lang3.math.NumberUtils.BYTE_ZERO;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ZERO;
import static org.apache.commons.lang3.math.NumberUtils.SHORT_ONE;

/**
 * The type Store wx service.
 *
 * @author liufei
 * @date 10 /22/19
 */
@Slf4j
@Service
public class StoreWxService extends ShopBaseService {
    /**
     * The User service.
     */
    @Autowired
    public UserService userService;
    /**
     * 门店
     */
    @Autowired
    public StoreService store;
    /**
     * 门店服务
     */
    @Autowired
    public StoreServiceService storeService;
    /**
     * The Goods spec product service.商品规格
     */
    @Autowired
    public GoodsSpecProductService goodsSpecProductService;
    /**
     * The Store config service.门店配置
     */
    @Autowired
    public StoreConfigService storeConfigService;
    /**
     * The Member card service.会员卡
     */
    @Autowired
    public MemberCardService memberCardService;

    /**
     * The Shop common config service.门店公共配置
     */
    @Autowired
    public ShopCommonConfigService shopCommonConfigService;

    /**
     * The User card dao service.会员卡
     */
    @Autowired
    public UserCardDaoService userCardDaoService;

    /**
     * The User card service.
     */
    @Autowired
    public UserCardService userCardService;

    /**
     * The Invoice service.发票
     */
    @Autowired
    public InvoiceService invoiceService;

    /**
     * The Store order service.门店订单
     */
    @Autowired
    public StoreOrderService storeOrderService;

    /**
     * The Mp payment service.
     */
    @Autowired
    public MpPaymentService mpPaymentService;

    /**
     * The Account service.余额管理
     */
    @Autowired
    public AccountService accountService;

    /**
     * The Score service.积分服务
     */
    @Autowired
    public ScoreService scoreService;

    /**
     * The Score cfg service.积分配置
     */
    @Autowired
    public ScoreCfgService scoreCfgService;

    /**
     * The Service order service.门店服务订单
     */
    @Autowired
    public ServiceOrderService serviceOrderService;

    /**
     * The Technician service.门店技师
     */
    @Autowired
    public ServiceTechnicianService technicianService;

    /**
     * The Payment service.支付
     */
    @Autowired
    public PaymentService paymentService;

    /**
     * The Trade service.交易服务配置
     */
    @Autowired
    public TradeService tradeService;

    /**
     * The Shop service.店铺
     */
    @Autowired
    public ShopService shopService;

    /**
     * The Province service.省市区
     */
    @Autowired
    public ProvinceService provinceService;

    @Autowired
    public BaseScoreCfgService baseScoreCfgService;
    @Autowired
    private StoreAccountDao storeAccountDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private JedisManager jedisManager;
    @Autowired
    private PharmacistDao pharmacistDao;
    @Autowired
    private StoreDao storeDao;
    @Autowired
    private OrderInfoDao orderInfoDao;
    @Autowired
    private OrderActionService orderActionService;
    @Autowired
    private OrderGoodsDao orderGoodsDao;
    @Autowired
    private UserAddressDao userAddressDao;
    @Autowired
    private ShipInfoService shipInfoService;
    @Autowired
    private ReturnOrderDao returnOrderDao;

    /**
     * The constant BYTE_TWO.
     */
    public static final Byte BYTE_TWO = 2;

    private static final Condition DEL_CONDITION = STORE.DEL_FLAG.eq(BYTE_ZERO);

    /**
     * 门店列表查询-小程序端
     *
     * @param param 查询入参
     * @return StorePageListVo list
     */
    public List<StorePojo> getList(StoreListParam param) {
        List<StorePojo> storeList = new ArrayList<>();
        Location location = param.getLocation();

        // type为0,普通入口
        if (BYTE_ZERO.equals(param.getType())) {
            storeList = getStoreByCustomCondition(new HashMap<String, Byte>(2) {{
                put("scan_stores", param.getScanStores());
            }}, null);
        } else if (BYTE_ONE.equals(param.getType()) && param.getCardId() != null) {
            // type为1,并且cardId不为空;表示入口为会员卡详情页
            MemberCardPojo memberCardPojo = memberCardService.getMemberCardInfoById(param.getCardId());
            if (memberCardPojo.getStoreUseSwitch().equals(CardConstant.UNAVAILABLE_IN_STORE)) {
                return new ArrayList<>(0);
            } else {
                // 会员卡支持门店列表,为空支持所有
                List<Integer> supportStoreList = Util.json2Object(memberCardPojo.getStoreList(), new TypeReference<List<Integer>>() {
                }, false);
                if (supportStoreList.size()>0 && supportStoreList.get(0) != 0) {
                    storeList = db().selectFrom(STORE).where(STORE.STORE_ID.in(supportStoreList)).and(DEL_CONDITION).fetchInto(StorePojo.class);
                } else {
                    storeList = getStoreByCustomCondition(new HashMap<String, Byte>(2) {{
                        put("scan_stores", param.getScanStores());
                    }}, null);
                }
            }
        } else if (BYTE_TWO.equals(param.getType())) {
//            type为2 ,表示入口为商品详情页自提/同城配送过来
            if (param.getGoodsId() != null) {
                // 根据商品id获取商品规格id列表
                List<GoodsSpecProduct> list = goodsSpecProductService.selectByGoodsId(param.getGoodsId());
                Set<Integer> prdIds = list.stream().map(GoodsSpecProduct::getPrdId).collect(Collectors.toSet());
                log.debug("商品[{}]对应的sku为:{}", param.getGoodsId(), prdIds.toString());
                // 获得该商品可购买的门店列表
                storeList = getCanBuyStoreList(prdIds, param.getDeliverType(), location, BYTE_ONE);
                log.debug("可购买的门店列表:{}", storeList.stream().map(StorePojo::getStoreId).collect(Collectors.toSet()));
            } else {
                // 缺少商品参数 商品规格
                throw new BusinessException("缺少商品参数 商品规格");
            }
        }
        // 查询开启“扫码购”功能的门店ID列表配置
        List<Integer> storeScanIds = getStoreScanIds();
        // 筛掉不支持扫码购的门店或者添加是否支持扫码购的标示位
        if (param.getScanStores().equals(BYTE_ONE)) {
            storeList = storeList.stream().filter(s -> storeScanIds.contains(s.getStoreId())).collect(toList());
        } else {
            storeList.forEach(s -> {
                s.setScanBuy(storeScanIds.contains(s.getStoreId()) ? BYTE_ONE : BYTE_ZERO);
            });
        }
        // 设置图片和距离
        boolean locationAuth = param.getLocationAuth() == BYTE_ONE;
        storeList.forEach(s -> {
            String storeImgs = s.getStoreImgs();
            log.debug("门店宣传图列表为:{}", storeImgs);
            List<String> imgs = Util.json2Object(storeImgs, new TypeReference<List<String>>() {
            }, false);
            if (CollectionUtils.isNotEmpty(imgs)) {
                // 设置门店主图中的第一张为门店列表展示图
                s.setStoreImgs(imgs.get(INTEGER_ZERO));
            } else {
                s.setStoreImgs(null);
            }
            if (locationAuth) {
                log.debug("用户已开启授权地理位置信息");
                if (Objects.isNull(location)) {
                    log.error("入参用户地理位置信息缺失！");
                    s.setDistance("0.00");
                } else {
                    double distance = Util.getDistance(location.getLatitude(), location.getLongitude(), Double.parseDouble(s.getLatitude()), Double.parseDouble(s.getLongitude()));
                    log.debug("门店 {} 距离用户位置 {} km", s.getStoreName(), distance);
                    s.setDistance(formatDouble(distance));
                }
            } else {
                // 未开启地理位置授权，门店距离为null
                log.debug("用户未开启授权地理位置信息");
                s.setDistance(null);
            }
        });
        // 结果按照距离 从小到大排序
        if (locationAuth) {
            Collections.sort(storeList);
        }
        return storeList;
    }

    /**
     * Gets store scan ids.查询开启“扫码购”功能的门店ID列表配置
     */
    public List<Integer> getStoreScanIds() {
        String storeScanValue = storeConfigService.getStoreScanIds();
        log.debug("cfg配置表中store_scan_ids（开启“扫码购”功能的门店ID列表）值为:{}", storeScanValue);
        List<Integer> storeScanIds = Util.json2Object(storeScanValue, new TypeReference<List<Integer>>() {
        }, false);
        return CollectionUtils.isEmpty(storeScanIds) ? EMPTY_LIST : storeScanIds;
    }

    /**
     * Gets store by custom condition.
     *
     * @param condition the condition 自定义任意条件
     * @param fields    the fields 自定义结果字段集
     * @return the store by custom condition
     */
    public List<StorePojo> getStoreByCustomCondition(Map<String, ?> condition, List<TableField<StoreRecord, ?>> fields) {
        String scanStores="scan_stores";
        if (condition.get(scanStores) != null) {
            Byte scanStore = (Byte) condition.get(scanStores);
            SelectConditionStep<StoreRecord> conditionStep = db()
                .selectFrom(STORE).where(DEL_CONDITION)
                .and(STORE.STORE_TYPE.eq(STORE_TYPE_NORMAL_STORE));
            if (!BYTE_ZERO.equals(scanStore)) {
                conditionStep.and(STORE.POS_SHOP_ID.greaterThan(INTEGER_ZERO));
            }
            return conditionStep.fetchInto(StorePojo.class);
        } else {
            return null;
        }
    }

    /**
     * The entry point of application.获得可购买的门店列表
     *
     * @param prdIds      商品规格id列表
     * @param deliverType 配送类型,1:自提,2:同城配送
     * @param location    用户位置
     * @param isFromStore todo 同城服务参数
     * @return the can buy store list
     */
    public List<StorePojo> getCanBuyStoreList(Set<Integer> prdIds, Byte deliverType, Location location, Byte isFromStore) {
        SelectConditionStep<Record1<Integer>> conditionStep = db().select(STORE.STORE_ID).from(STORE_GOODS)
            .leftJoin(STORE).on(STORE_GOODS.STORE_ID.eq(STORE.STORE_ID))
            .where(STORE_GOODS.PRD_ID.in(prdIds))
            .and(STORE_GOODS.IS_ON_SALE.eq(BYTE_ONE))
            .and(STORE.BUSINESS_STATE.eq(BYTE_ONE))
            .and(STORE.DEL_FLAG.eq(BYTE_ZERO));
        if (deliverType.equals(BYTE_ZERO)) {
            conditionStep.and(STORE.AUTO_PICK.eq(SHORT_ONE));
        }
        if (deliverType.equals(BYTE_ONE)) {
            // TODO 同城配送 新增字段
//            conditionStep.and(STORE.city.eq(SHORT_ONE));
        }
        List<Integer> storeIds = conditionStep.groupBy(STORE.STORE_ID)
            .having(DSL.count(STORE.STORE_ID).eq(prdIds.size()))
            .fetchInto(Integer.class);
        List<StorePojo> storeLists = db().selectFrom(STORE).where(STORE.STORE_ID.in(storeIds)).fetchInto(StorePojo.class);
        if (deliverType.equals(BYTE_ONE)) {
            // TODO 查询支持同城配送的门店列表
            cityServiceCanUseStoreList(storeLists, location, isFromStore);
        }
        return storeLists;
    }

    /**
     * The entry point of application.todo 获得同城配送可用的门店列表
     *
     * @param storeLists  the store lists
     * @param location    the location
     * @param isFromStore the is from store
     */
    public void cityServiceCanUseStoreList(List<StorePojo> storeLists, Location location, Byte isFromStore) {

    }

    /**
     * Gets wxapp store detail.门店信息详情-小程序
     *
     * @param param the param
     * @return the wxapp store detail
     */
    public StoreInfoVo getWxappStoreDetail(StoreInfoParam param) {
        param.setScene();
        Integer storeId = param.getStoreId();
        Integer userId = param.getUserId();
        if (userId != null) {
            // 更新用户表 门店来源(-1未录入0后台>0为门店) todo 记录一下用户第一次登录的门店, 可删
            userService.updateFields(USER.USER_ID.eq(userId).and(USER.SOURCE.eq(-1)), new HashMap<Field<?>, Integer>(1) {{
                put(USER.SOURCE, storeId);
            }});
        }
        StorePojo storePojo = store.getStore(storeId);
        Objects.requireNonNull(storePojo, "店铺不存在");
        StoreInfoVo storeInfoVo = new StoreInfoVo();
        FieldsUtil.assignNotNull(storePojo, storeInfoVo);

        // 获取服务列表,按服务分类归纳
        List<StoreServiceCategoryListQueryVo> catService = storeService.getAllStoreServiceCategory(new StoreServiceCategoryListQueryParam() {{
            setStoreId(storeId);
        }});
        catService.forEach(e -> e.setServiceList(storeService.getStoreServiceByCatId(storeId, e.getCatId())));
        storeInfoVo.setServiceCat(catService);
        storeInfoVo.setAllService(storeService.getWxAllStoreServiceByStoreId(storeId));
        // todo 扫码购
//        List<String> storeScanIds = Arrays.asList(storeConfigService.getStoreScanIds().split(","));
        // todo 获取购物车商品数
        // todo 获取待核销扫码购订单
        // 门店买单开关配置
        storeInfoVo.setStoreBuy(storeConfigService.getStoreBuy());
        // 门店距离
        if (param.getLocationAuth() == 1) {
            Location location = param.getLocation();
            if (Objects.isNull(location)) {
                log.error("入参缺少用户地理位置信息，无法计算门店距离！");
            } else {
                double distance = Util.getDistance(location.getLatitude(),
                    location.getLongitude(),
                    Double.parseDouble(storeInfoVo.getLatitude()),
                    Double.parseDouble(storeInfoVo.getLongitude()));
                storeInfoVo.setDistance(formatDouble(distance));
            }
        } else {
            storeInfoVo.setDistance(null);
        }
        return storeInfoVo;
    }

    /**
     * Store pay order.门店买单-小程序
     *
     * @param param the param
     * @return the store pay order vo
     */
    public StorePayOrderVo storePayOrder(StoreInfoParam param) {
        int storeId = param.getStoreId();
        int userId = param.getUserId();
        StorePayOrderVo payOrderVo = new StorePayOrderVo();
        // 用户积分和余额
        UserRecord userRecord = userService.getUserByUserId(userId);
        payOrderVo.setScore(userRecord.getScore());
        payOrderVo.setAccount(userRecord.getAccount());
        // 获取发票开关配置
        payOrderVo.setInvoiceSwitch(shopCommonConfigService.getInvoice());
        // 获取有效用户会员卡列表
        List<ValidUserCardBean> cardList = userCardDaoService.getStoreValidCardList(userId, storeId);
        payOrderVo.setMemberCardList(cardList);
        // 门店营业状态和删除标示
        StorePojo storePojo = store.getStore(storeId);
        payOrderVo.setStoreBusinessState(storePojo.getBusinessState());
        payOrderVo.setDelFlag(storePojo.getDelFlag());
        // 门店买单开关配置
        payOrderVo.setStoreBuy(storeConfigService.getStoreBuy());
        // 交易配置
        payOrderVo.setDefaultPayConf(tradeService.getDefaultPayConf());
        payOrderVo.setPayStatusList(tradeService.getPaymentEnabled());
        // 积分使用规则
        payOrderVo.setScoreDiscountRatio(baseScoreCfgService.getScoreDiscountRatio());
        payOrderVo.setScorePayNum(baseScoreCfgService.getScorePayNum());
        payOrderVo.setScorePayLimit(baseScoreCfgService.getScorePayLimit());
        //积分兑换比
        payOrderVo.setScoreProportion(baseScoreCfgService.getScoreProportion());
        return payOrderVo;
    }

    /**
     * Store pay.门店买单支付
     *
     * @param param the param 门店订单信息
     * @return the string 订单编号
     */
    public WebPayVo storePay(StoreInfoParam param) {
        // 订单编号, 创建支付订单时生成,支付成功后用来获取订单详情
        AtomicReference<String> orderSn = new AtomicReference<>();
        int userId = param.getUserId();
        // 用户信息
        UserRecord userRecord = db().selectFrom(USER).where(USER.USER_ID.eq(userId)).fetchOneInto(USER);
        // 门店订单信息
        StorePayOrderInfo orderInfo = param.getOrderInfo();
        // 发票信息
        Integer invoiceId = orderInfo.getInvoiceId();
        InvoiceVo invoiceVo = Objects.nonNull(invoiceId) ? invoiceService.get(invoiceId) : null;
        // 事务db前置校验
        StoreOrderTran storeOrderTran = storeOrderService.checkBeforeCreate(userRecord, invoiceVo, orderInfo);
        AtomicReference<WebPayVo> webPayVo = new AtomicReference<>();
        this.transaction(() -> {
            // 创建门店订单
            orderSn.set(storeOrderService.createStoreOrder(storeOrderTran));
            if (BigDecimalUtil.greaterThanZero(orderInfo.getMoneyPaid())) {
                //微信支付接口
                String openId = userService.getUserByUserId(param.getUserId()).getWxOpenid();
                webPayVo.set(mpPaymentService.wxUnitOrder(param.getClientIp(), STORE_BUY, orderSn.get(), orderInfo.getMoneyPaid(), openId));
                log.debug("微信支付接口调用结果：{}", webPayVo.get());
                // 更新记录微信预支付id：prepayid
                storeOrderService.updatePrepayIdByOrderSn(orderSn.get(), webPayVo.get().getResult().getPrepayId());
            } else {
                // 更新门店订单支付状态
                storeOrderService.updateRecord(STORE_ORDER.ORDER_SN.eq(orderSn.get()), new StoreOrderRecord() {{
                    setPayTime(Timestamp.valueOf(LocalDateTime.now()));
                    setOrderStatus(PAY_SUCCESS);
                    setOrderStatusName(PAY_SUCCESS_NAME);
                }});
                storeOrderService.storePay2SendScore(storeOrderTran.getStoreOrder());
            }
            if (Objects.isNull(webPayVo.get())) {
                webPayVo.set(new WebPayVo() {{
                    setOrderSn(orderSn.get());
                }});
            } else {
                webPayVo.get().setOrderSn(orderSn.get());
            }
        });
        return webPayVo.get();
    }

    /**
     * 店员认证
     * @param param
     * @return
     */
    public Integer storeClerkAuth(StoreClerkAuthParam param, WxAppSessionUser wxAppSessionUser)throws MpException {
        StoreAccountVo storeAccountVo=storeAccountDao.storeAccountAuth(param);
        //校验是否能认证
        checkStoreClerkAuth(param,storeAccountVo,wxAppSessionUser);
        transaction(()->{

            if(param.getIsPharmacist().equals((byte)1)){
                //是否药师
                storeAccountDao.updateSignature(storeAccountVo.getAccountId(),param.getSignature());
            }
            userDao.updateUserType(param.getUserId(), AuthConstant.AUTH_TYPE_STORE_ACCOUNT_USER);
            storeAccountDao.updateUserId(storeAccountVo.getAccountId(),param.getUserId());
        });
        return storeAccountVo.getAccountId();
    }

    public void checkStoreClerkAuth(StoreClerkAuthParam param,StoreAccountVo storeAccountVo, WxAppSessionUser wxAppSessionUser)throws MpException {
        if(wxAppSessionUser.getUserType()!=null&&!AuthConstant.AUTH_TYPE_NORMAL_USER.equals(wxAppSessionUser.getUserType())){
            throw new MpException(JsonResultCode.AUTH_ALREADY_AUTHED);
        }
        if(!checkMobileCode(param)){
            throw new MpException(JsonResultCode.STORE_CLERK_AUTH_INFO_SMS_ERROR);
        }
        if(storeAccountVo==null||!Util.md5(param.getPassword()).equals(storeAccountVo.getAccountPasswd())){
            throw new MpException(JsonResultCode.STORE_CLERK_AUTH_INFO_ERROR);
        }
        if(storeAccountVo.getStatus()==0){
            throw new MpException(JsonResultCode.STORE_CLERK_AUTH_IS_DISABLED);
        }
        if(storeAccountVo.getUserId()>0){
            throw new MpException(JsonResultCode.STORE_CLERK_AUTH_AlREADY_ERROR);
        }
    }
    /**
     * 短信验证码校验
     * @return
     */
    private boolean checkMobileCode(StoreClerkAuthParam param) {
        String key = String.format(SmsApiConfig.REDIS_KEY_SMS_CHECK_SALESCLERK_MOBILE, getShopId(), param.getUserId(), param.getMobile());
        String s = jedisManager.get(key);
        if (!Strings.isBlank(s) && !Strings.isBlank(param.getMobileCheckCode())) {
            return s.equals(param.getMobileCheckCode());
        }
        return false;
    }

    /**
     * 店员端首页
     * @param storeAccountVo
     * @return
     */
    public StoreMainShowVo storeMainShow(StoreAccountVo storeAccountVo){
        StoreMainShowVo storeMainShowVo=new StoreMainShowVo();
        //可用门店
        List<StoreStatisticVo> storeList=storeDao.getListByStoreIds(storeAccountVo.getStoreLists());

        //门店数据
        for(StoreStatisticVo statisticVo:storeList){
            //待处理数
            Integer waitReceiveOrderNum= orderInfoDao.countNumByStoreIdOrderStatus(statisticVo.getStoreId(),storeAccountVo.getAccountId());
            //已完成数量
            Integer finishedOrderNum= shipInfoService.getCountFinishedNumByAccountIdUserId(storeAccountVo.getAccountId(),storeAccountVo.getUserId(),statisticVo.getStoreId(),null,null);
            //配送中数量
            Integer deliveryOrderNum= shipInfoService.getCountDeliveryNumByAccountIdUserId(storeAccountVo.getAccountId(),storeAccountVo.getUserId(),null,null);
            List<Byte> returnStatusList=new ArrayList<>();
            returnStatusList.add(OrderConstant.REFUND_STATUS_AUDITING);
            returnStatusList.add(OrderConstant.REFUND_STATUS_AUDIT_PASS);
            returnStatusList.add(OrderConstant.REFUND_STATUS_APPLY_REFUND_OR_SHIPPING);
            //待售后数量
            Integer saleAfterOrderNum=returnOrderDao.getCountByReturnStatus(returnStatusList,statisticVo.getStoreId());
            statisticVo.setWaitHandleOrderNum(waitReceiveOrderNum);
            statisticVo.setFinishedOrderNum(finishedOrderNum);
            statisticVo.setDeliveryOrderNum(deliveryOrderNum);
            statisticVo.setSaleAfterOrderNum(saleAfterOrderNum);
        }
        //本月数据
        StoreMonthStatisticVo monthVo=new StoreMonthStatisticVo();
        Timestamp startTime=DateUtil.beginOfMonth(DateUtils.getLocalDateTime()).toTimestamp();
        Timestamp endTime=DateUtil.endOfMonth(DateUtils.getLocalDateTime()).toTimestamp();
        List<Integer> storeIdList=storeList.stream().map(StoreStatisticVo::getStoreId).collect(Collectors.toList());
        Integer waitHandleNum= orderInfoDao.countNumByStoreIdOrderStatusAndTime(storeIdList, storeAccountVo.getAccountId(),startTime,endTime);
        monthVo.setWaitHandleNum(waitHandleNum);
        //已完成的数量
        Integer finishedNum=shipInfoService.getCountFinishedNumByAccountIdUserId(storeAccountVo.getAccountId(),storeAccountVo.getUserId(),null,startTime,endTime);
        //配送中数量
        Integer deliveryNum= shipInfoService.getCountDeliveryNumByAccountIdUserId(storeAccountVo.getAccountId(),storeAccountVo.getUserId(),startTime,endTime);
        monthVo.setDeliveryNum(deliveryNum);
        monthVo.setFinishedNum(finishedNum);
        storeMainShowVo.setStoreAccount(storeAccountVo);
        storeMainShowVo.setStatisticList(storeList);
        storeMainShowVo.setMonthVo(monthVo);
        return storeMainShowVo;
    }

    /**
     * 获取待处理订单和已完成订单列表
     * @param param
     * @return
     */
    public PageResult<StoreOrderListVo> getStoreClerkOrderList(StoreOrderListParam param){
        PageResult<StoreOrderListVo> orderInfoList=new PageResult<>();
        //已完成的
        if(StoreOrderConstant.FINISHED.equals(param.getStatus())){
            orderInfoList=orderActionService.getStoreClerkOrderFinishedList(param);
        }else if(StoreOrderConstant.WAIT_HANDLE.equals(param.getStatus())){
            //待处理的
            List<Byte> orderStatusList=new ArrayList<>();
            //待处理的订单状态
            orderStatusList.add(OrderConstant.ORDER_WAIT_DELIVERY);
            orderStatusList.add(OrderConstant.ORDER_SHIPPED);
            orderStatusList.add(OrderConstant.ORDER_RECEIVED);
            param.setOrderStatusList(orderStatusList);
            orderInfoList = orderInfoDao.getStoreClerkOrderList(param);
        }

        List<StoreOrderListVo> dataList=orderInfoList.getDataList();
        //orderGoods
        for(StoreOrderListVo orderInfo:dataList){
            List<OrderGoodsVo> orderGoodsList = orderGoodsDao.getOrderGoodsListByOrderId(orderInfo.getOrderId());
            orderInfo.setOrderGoodsList(orderGoodsList);
            UserAddressVo userAddressVo = userAddressDao.getUserAddressInfoByAddressId(orderInfo.getAddressId());
            orderInfo.setUserAddress(userAddressVo);
        }
        return orderInfoList;
    }

    /**
     * 获取医院类型门店信息
     * @return
     */
    public StoreInfoVo getHospitalInfo(Location location) {
        StorePojo storePojo = store.getHospitalInfo();
        StoreInfoVo storeInfoVo = new StoreInfoVo();
        if (storePojo == null){
            return null;
        }
        FieldsUtil.assignNotNull(storePojo, storeInfoVo);
        double distance = Util.getDistance(location.getLatitude(),
            location.getLongitude(),
            Double.parseDouble(storeInfoVo.getLatitude()),
            Double.parseDouble(storeInfoVo.getLongitude()));
        log.debug("医院 {} 距离用户位置 {} km", storeInfoVo.getStoreName(), distance);
        storeInfoVo.setDistance(formatDouble(distance));
        return storeInfoVo;
    }

}
