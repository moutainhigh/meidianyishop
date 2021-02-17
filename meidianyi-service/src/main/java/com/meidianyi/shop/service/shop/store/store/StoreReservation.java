package com.meidianyi.shop.service.shop.store.store;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryResult;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.DomainConfig;
import com.meidianyi.shop.db.shop.tables.records.CommentServiceRecord;
import com.meidianyi.shop.db.shop.tables.records.ServiceOrderRecord;
import com.meidianyi.shop.db.shop.tables.records.UserRecord;
import com.meidianyi.shop.service.foundation.exception.Assert;
import com.meidianyi.shop.service.foundation.exception.BusinessException;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobInfo;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobsConstant;
import com.meidianyi.shop.service.pojo.shop.market.message.RabbitMessageParam;
import com.meidianyi.shop.service.pojo.shop.market.message.RabbitParamConstant;
import com.meidianyi.shop.service.pojo.shop.message.MpTemplateConfig;
import com.meidianyi.shop.service.pojo.shop.message.MpTemplateData;
import com.meidianyi.shop.service.pojo.shop.store.comment.ServiceCommentVo;
import com.meidianyi.shop.service.pojo.shop.store.service.StoreServiceParam;
import com.meidianyi.shop.service.pojo.shop.store.service.order.OrderCloseQueenParam;
import com.meidianyi.shop.service.pojo.shop.store.service.order.ServiceOrderDetailVo;
import com.meidianyi.shop.service.pojo.shop.store.service.order.ServiceOrderListQueryVo;
import com.meidianyi.shop.service.pojo.shop.store.store.StorePojo;
import com.meidianyi.shop.service.pojo.shop.store.technician.TechnicianInfo;
import com.meidianyi.shop.service.pojo.wxapp.pay.base.WebPayVo;
import com.meidianyi.shop.service.pojo.wxapp.pay.jsapi.JsApiVo;
import com.meidianyi.shop.service.pojo.wxapp.store.*;
import com.meidianyi.shop.service.saas.region.ProvinceService;
import com.meidianyi.shop.service.saas.schedule.TaskJobMainService;
import com.meidianyi.shop.service.saas.shop.ShopService;
import com.meidianyi.shop.service.shop.config.ShopCommonConfigService;
import com.meidianyi.shop.service.shop.config.StoreConfigService;
import com.meidianyi.shop.service.shop.config.TradeService;
import com.meidianyi.shop.service.shop.goods.GoodsSpecProductService;
import com.meidianyi.shop.service.shop.member.*;
import com.meidianyi.shop.service.shop.member.dao.UserCardDaoService;
import com.meidianyi.shop.service.shop.order.invoice.InvoiceService;
import com.meidianyi.shop.service.shop.order.store.StoreOrderService;
import com.meidianyi.shop.service.shop.payment.MpPaymentService;
import com.meidianyi.shop.service.shop.payment.PaymentService;
import com.meidianyi.shop.service.shop.store.comment.ServiceCommentService;
import com.meidianyi.shop.service.shop.store.postsale.ServiceTechnicianService;
import com.meidianyi.shop.service.shop.store.service.ServiceOrderService;
import com.meidianyi.shop.service.shop.store.service.StoreServiceService;
import com.meidianyi.shop.service.shop.task.wechat.MaMpScheduleTaskService;
import com.meidianyi.shop.service.shop.user.message.WechatMessageTemplateService;
import com.meidianyi.shop.service.shop.user.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import static com.meidianyi.shop.common.foundation.data.JsonResultCode.CODE_DATA_NOT_EXIST;
import static com.meidianyi.shop.common.foundation.util.BigDecimalUtil.BIGDECIMAL_ZERO;
import static com.meidianyi.shop.db.shop.tables.ServiceOrder.SERVICE_ORDER;
import static com.meidianyi.shop.db.shop.tables.Store.STORE;
import static com.meidianyi.shop.db.shop.tables.StoreService.STORE_SERVICE;
import static com.meidianyi.shop.service.shop.store.service.ServiceOrderService.*;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.math.NumberUtils.*;

/**
 * @author liufei
 * @date 11/14/19
 * 门店服务预约
 */
@Slf4j
@Service
public class StoreReservation extends ShopBaseService {
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
     * The Comment service.服务评论
     */
    @Autowired
    public ServiceCommentService commentService;

    /**
     * The Common config service.服务评论配置
     */
    @Autowired
    public ShopCommonConfigService commonConfigService;

    /**
     * The Message template service.
     */
    @Autowired
    public WechatMessageTemplateService messageTemplateService;

    /**
     * The Province service.省市区
     */
    @Autowired
    public ProvinceService provinceService;

    /**
     * The Domain config.
     */
    @Autowired
    public DomainConfig domainConfig;

    /**
     * The Mp schedule task service.
     */
    @Autowired
    private MaMpScheduleTaskService mpScheduleTaskService;

    @Autowired
    private TaskJobMainService taskJobMainService;

    /**
     * The constant HH_MM_FORMATTER.
     */
    public static final DateTimeFormatter HH_MM_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Reservation detail reservation detail vo.门店服务预约详情
     *
     * @param serviceId the service id
     * @return the reservation detail vo
     */
    public ReservationDetailVo reservationDetail(Integer serviceId) {
        // 门店服务信息
        StoreServiceParam service = storeService.getStoreService(serviceId);
        Assert.notNull(service, JsonResultCode.CODE_STORE_SERVICE_NOT_EXIST);
        // 门店信息
        StorePojo storePojo = store.getStore(service.getStoreId());
        Assert.notNull(storePojo, JsonResultCode.CODE_STORE_NOT_EXIST);

        // 设置服务起始日期, 不能预约过期的服务, 持续时间最多两个月
        LocalDate now = LocalDate.now();
        LocalDate startDate = service.getStartDate().toLocalDate().compareTo(now) > 0 ? service.getStartDate().toLocalDate() : now;
        LocalDate twoMonthsLater = startDate.plus(2, ChronoUnit.MONTHS);
        LocalDate endDate = service.getEndDate().toLocalDate().compareTo(twoMonthsLater) <= 0 ? service.getEndDate().toLocalDate() : twoMonthsLater;
        log.debug("服务有效预约日期为:{} - {}", startDate, endDate);

        // 服务可预约时段, 用服务时长(单位/分钟)切分成当前可预约的分段服务
        LocalTime startPeriod = LocalTime.parse(service.getStartPeriod(), HH_MM_FORMATTER);
        LocalTime endPeriod = LocalTime.parse(service.getEndPeriod(), HH_MM_FORMATTER);
        int serviceDuration = service.getServiceDuration();
        log.debug("服务可预约时段为:{} - {}; 单次服务时长为: {}", startPeriod, endPeriod, serviceDuration);

        List<ReservationInfo> reservationList = new ArrayList<>();
        List<ServiceOrderListQueryVo> allOrders = serviceOrderService.getOrderListInfo(service.getId(), null, startDate, endDate, ORDER_STATUS_WAIT_SERVICE);
        List<TechnicianInfo> allTechnicianInfoList = technicianService.getTechnicianList(service.getStoreId(), startDate, endDate);
        //

        for (LocalDate i = startDate; i.isBefore(endDate) || i.equals(endDate); i = i.plusDays(1)) {
            LocalDate tem = i;
            List<ServiceOrderListQueryVo> orders = allOrders.stream().filter(o -> o.getServiceDate().equals(tem.format(DATE_TIME_FORMATTER))).collect(toList());
            List<TechnicianInfo> technicianInfoList = allTechnicianInfoList.stream().filter(t -> t.getWorkDate().equals(tem.format(DATE_TIME_FORMATTER))).collect(toList());
            ReservationInfo reservationInfo = createReservationInfo(i, startPeriod, endPeriod, serviceDuration, service, orders, technicianInfoList);
            if (Objects.nonNull(reservationInfo)) {
                reservationList.add(reservationInfo);
            }
        }
        return ReservationDetailVo.builder()
            .storeInfo(storePojo)
            .serviceInfo(service)
            // 获取服务的最新评论
            .commentInfo(commentService.getNewestcomment(service.getId()))
            .reservationInfoList(reservationList)
            // 是否强制用户绑定手机号
            .isBindMobile(commonConfigService.getBindMobile())
            // 获取门店职称配置
            .technicianTitle(storeConfigService.getTechnicianTitle())
            .build();
    }

    /**
     * Create reservation info reservation info.
     *
     * @param date            the date
     * @param startPeriod     the start period
     * @param endPeriod       the end period
     * @param serviceDuration the service duration
     * @param serviceInfo     the service info
     * @return the reservation info
     */
    private ReservationInfo createReservationInfo(LocalDate date, LocalTime startPeriod, LocalTime endPeriod, int serviceDuration, StoreServiceParam serviceInfo, List<ServiceOrderListQueryVo> orders, List<TechnicianInfo> technicianInfoList) {
        LocalTime localTime = LocalTime.now();
        if (date.isEqual(LocalDate.now()) && localTime.isAfter(startPeriod)) {
            // 如果可服务日期是当天, 获取当天距离当前时间最近的一次可服务时间段
            startPeriod = startPeriod.plus((((localTime.toSecondOfDay() / 60 - startPeriod.toSecondOfDay() / 60) / serviceDuration + 1) * serviceDuration), ChronoUnit.MINUTES);
        }
        List<ReservationTime> reservationTimeList = new ArrayList<>();
        for (LocalTime i = startPeriod;
             i.isBefore(endPeriod) && (i.plusMinutes(serviceDuration).isBefore(endPeriod) || i.plusMinutes(serviceDuration).equals(endPeriod)) && i.plusMinutes(serviceDuration).isAfter(i);
             i = i.plus(serviceDuration, ChronoUnit.MINUTES)) {
            ReservationTime reservationTime = createReservationTime(i, i.plusMinutes(serviceDuration), serviceInfo, orders, technicianInfoList);
            if (reservationTime != null) {
                reservationTimeList.add(reservationTime);
            }
        }
        return CollectionUtils.isNotEmpty(reservationTimeList) ? ReservationInfo.builder()
            .reservationTimeList(reservationTimeList)
            .reservationDate(date)
            .build() : null;
    }

    /**
     * Create reservation time reservation info . reservation time.
     *
     * @param startPeriod the start period
     * @param endPeriod   the end period
     * @param serviceInfo the service info
     * @return the reservation time
     */
    private ReservationTime createReservationTime(LocalTime startPeriod, LocalTime endPeriod, StoreServiceParam serviceInfo, List<ServiceOrderListQueryVo> orderListQueryVos, List<TechnicianInfo> technicianInfoList) {
        Integer serviceId = serviceInfo.getId();
        int serviceNum = Objects.nonNull(serviceInfo.getServicesNumber()) ? serviceInfo.getServicesNumber() : 0;
        int tecServiceNum = Objects.nonNull(serviceInfo.getTechServicesNumber()) ? serviceInfo.getTechServicesNumber() : 0;
        List<TechnicianInfo> result = null;
        // 服务类型:0无技师1有技师
        Byte technicianFlag = serviceInfo.getServiceType();
        if (technicianFlag.equals(BYTE_ZERO)) {
            // 服务数量为0表示无上限,可以无限接受服务预约
            if (serviceNum != 0) {
                if (serviceOrderService.checkMaxNumOfReservations(orderListQueryVos, startPeriod, endPeriod) >= serviceNum) {
                    return null;
                }
            }
        } else {
            result = technicianInfoList.stream().filter((e) -> {
                // 过滤不支持给定服务的技师
                Byte serviceType = e.getServiceType();
                if (BYTE_ZERO.equals(serviceType)) {
                    return true;
                }
                if (BYTE_ONE.equals(serviceType)) {
                    return Objects.requireNonNull(Util.json2Object(e.getServiceList(), new TypeReference<List<Integer>>() {
                    }, false)).contains(serviceId);
                }
                return false;
            }).filter((e) -> {
                // 过滤不在给定服务时间段内的技师
                LocalTime tempStart = LocalTime.parse(e.getBegcreateTime(), HH_MM_FORMATTER);
                LocalTime tempEnd = LocalTime.parse(e.getEndTime(), HH_MM_FORMATTER);
                return (startPeriod.isAfter(tempStart) || startPeriod.equals(tempStart)) && (endPeriod.isBefore(tempEnd) || endPeriod.equals(tempEnd));
            }).filter((e) -> {
                // 过滤超过技师单时段服务数量上限的技师
                if (tecServiceNum == 0) {
                    return true;
                }
                return serviceOrderService.checkMaxNumOfReservations(orderListQueryVos.stream().filter(o -> o.getTechnicianId().equals(e.getId())).collect(toList()), startPeriod, endPeriod) < tecServiceNum;
            }).collect(toList());
            if (CollectionUtils.isEmpty(result)) {
                return null;
            }
        }
        return ReservationTime.builder()
            .startTime(startPeriod)
            .endTime(endPeriod)
            .technicianFlag(technicianFlag)
            .technicianPojoList(result).build();
    }

    /**
     * Submit reservation.创建门店服务预约订单
     */
    public ReservationOrder createReservation(Integer serviceId, Integer userId) {
        // 门店服务信息
        StoreServiceParam service = storeService.getStoreService(serviceId);
        Assert.notNull(service, JsonResultCode.CODE_STORE_SERVICE_NOT_EXIST);
        log.debug(",门店服务信息:{}", service);
        // 门店信息
        Integer storeId = service.getStoreId();
        StorePojo storePojo = store.getStore(storeId);
        Assert.notNull(storePojo, JsonResultCode.CODE_STORE_NOT_EXIST);
        log.debug(",门店信息:{}", storePojo);
        return ReservationOrder.builder()
            // 获取用户余额account
            .account(Optional.ofNullable(userService.getUserByUserId(userId)).orElseThrow(() -> new BusinessException(CODE_DATA_NOT_EXIST, "userId:" + userId)).getAccount())
            // 获取支付开关配置, 会员卡余额支付,余额支付
            .balanceFirst(tradeService.getBalanceFirst())
            .cardFirst(tradeService.getCardFirst())
            // 获取支持的支付方式
            .paymentVoList(new ArrayList<>(Optional.ofNullable(paymentService.getSupportPayment()).orElse(new HashMap<>(8)).values()))
            // 获取指定用户最近的一个服务预约订单信息(主要是获取用户的名称和手机号;没有就算了)
            .recentOrderInfo(serviceOrderService.getRecentOrderInfo(userId))
            // 获取门店职称配置
            .technicianTitle(storeConfigService.getTechnicianTitle())
            // 获取店铺logo
            .shopAvatar(Optional.ofNullable(shopService.getShopById(getShopId())).orElseThrow(() -> new BusinessException(CODE_DATA_NOT_EXIST, "shopId:" + getShopId())).getShopAvatar())
            // 获取有效用户会员卡列表
            .cardList(userCardDaoService.getStoreValidCardList(userId, storeId))
            .storePojo(storePojo)
            .service(service)
            .build();
    }

    /**
     * Submit reservation.提交确认门店服务预约订单
     *
     * @param param the param
     */
    public WebPayVo submitReservation(SubmitReservationParam param) {
        Integer serviceId = param.getServiceId();
        if (!serviceOrderService.checkReservationNum(param)) {
            // 预约人数已达上限
            throw new BusinessException(JsonResultCode.CODE_RESERVATION_UPPER_LIMIT);
        }
        String serviceName = storeService.getStoreService(serviceId).getServiceName();
        ServiceOrderRecord serviceOrder = new ServiceOrderRecord();
        FieldsUtil.assignNotNull(param, serviceOrder);
        // 事务前置校验
        ServiceOrderTran orderTran = serviceOrderService.checkBeforeCreate(serviceOrder);
        AtomicReference<WebPayVo> webPayVo = new AtomicReference<>();
        this.transaction(() -> {
            // 创建订单
            String orderSn = serviceOrderService.createServiceOrder(orderTran);
            log.debug("订单创建完成，订单编号为：{}", orderSn);
            if (serviceOrder.getMoneyPaid().compareTo(BIGDECIMAL_ZERO) > 0) {
                // 微信支付接口调用
                String openId = userService.getUserByUserId(param.getUserId()).getWxOpenid();
                webPayVo.set(mpPaymentService.wxUnitOrder(param.getClientIp(), serviceName, orderSn, serviceOrder.getMoneyPaid(), openId));
                log.debug("微信支付接口调用结果：{}", webPayVo.get());
                // 记录prepayId到订单表中
                serviceOrderService.updateSingleField(orderSn, SERVICE_ORDER.PREPAY_ID, webPayVo.get().getResult().getPrepayId());
            } else {
                // 如果不需要调用微信支付，直接将订单状态由待付款改为待服务
                serviceOrderService.updateServiceOrderStatus(orderSn, ORDER_STATUS_WAIT_SERVICE, ORDER_STATUS_NAME_WAIT_SERVICE);
                log.info("订单支付成功，发送预约成功通知！");
                // 预约成功通知
                sendAppointmentSuccess(orderTran.getServiceOrder());
                log.info("预约成功通知已下发");
            }
            if (Objects.isNull(webPayVo.get())) {
                webPayVo.set(new WebPayVo() {{
                    setOrderSn(orderSn);
                }});
            } else {
                webPayVo.get().setOrderSn(orderSn);
            }
        });
        return webPayVo.get();
    }

    /**
     * Continue pay web pay vo.
     *
     * @param orderSn  the order sn
     * @param clientIp the client ip
     * @return the web pay vo
     */
    public WebPayVo continuePay(String orderSn, String clientIp) {
        WxPayOrderQueryResult queryResult = null;
        try {
            // 查询微信支付订单信息
            queryResult = mpPaymentService.wxQueryOrder(orderSn);
        } catch (WxPayException e) {
            log.debug("微信订单查询失败：{}", e.getMessage());
            // 未查到相关订单信息，删除此订单重新生成新订单支付
            serviceOrderService.updateSingleField(orderSn, SERVICE_ORDER.DEL_FLAG, BYTE_ONE);
            SubmitReservationParam param = new SubmitReservationParam();
            FieldsUtil.assignNotNull(serviceOrderService.getRecord(orderSn), param);
            param.setClientIp(clientIp);
            // 重新走支付流程
            return submitReservation(param);
        }
        if (Objects.isNull(queryResult)) {
            // 未查到相关订单信息，删除此订单重新生成新订单支付
            serviceOrderService.updateSingleField(orderSn, SERVICE_ORDER.DEL_FLAG, BYTE_ONE);
            SubmitReservationParam param = new SubmitReservationParam();
            FieldsUtil.assignNotNull(serviceOrderService.getRecord(orderSn), param);
            param.setClientIp(clientIp);
            // 重新走支付流程
            return submitReservation(param);
        }
        log.debug("微信订单查询结果：{}", queryResult.toString());
        WebPayVo webPayVo = JsApiVo.builder().build();
        switch (queryResult.getTradeState()) {
            case WxPayConstants.WxpayTradeStatus.SUCCESS:
                // 返回订单已支付
                webPayVo.setOrderType(WxPayConstants.WxpayTradeStatus.SUCCESS);
                break;
            case WxPayConstants.WxpayTradeStatus.CLOSED:
                // 订单已关闭，删除此订单，并重新创建订单支付
                webPayVo.setOrderType(WxPayConstants.WxpayTradeStatus.CLOSED);
                break;
            case WxPayConstants.WxpayTradeStatus.USER_PAYING:
                // 返回正在支付
                webPayVo.setOrderType(WxPayConstants.WxpayTradeStatus.USER_PAYING);
                break;
            case WxPayConstants.WxpayTradeStatus.PAY_ERROR:
                break;
            case WxPayConstants.WxpayTradeStatus.NOTPAY:
                // 订单未支付，继续支付
                String prepayId = serviceOrderService.selectSingleField(orderSn, SERVICE_ORDER.PREPAY_ID);
                log.debug("prepayId的值为：{}", prepayId);
                if (StringUtils.isBlank(prepayId)) {
                    SubmitReservationParam param = new SubmitReservationParam();
                    FieldsUtil.assignNotNull(serviceOrderService.getRecord(orderSn), param);
                    param.setClientIp(clientIp);
                    // 重新走支付流程
                    webPayVo = submitReservation(param);
                } else {
                    try {
                        webPayVo = mpPaymentService.continuePay(queryResult, prepayId);
                    } catch (MpException e) {
                        log.debug("支付失败：{}", e.getMessage());
                        webPayVo.setOrderType(WxPayConstants.WxpayTradeStatus.PAY_ERROR);
                        break;
                    }
                }
            case WxPayConstants.WxpayTradeStatus.REFUND:
                break;
            case WxPayConstants.WxpayTradeStatus.REVOKED:
                break;
            default:
                log.debug("订单查询结果未匹配到任何状态");
                break;
        }
        webPayVo.setOrderSn(orderSn);
        return webPayVo;
    }

    /**
     * Prefix check boolean.预约成功通知
     *
     * @param serviceOrder the service order
     */
    public void sendAppointmentSuccess(ServiceOrderRecord serviceOrder) {
        int serviceId = serviceOrder.getServiceId();
        StoreServiceParam service = storeService.getStoreService(serviceId);
        if (Objects.isNull(service)) {
            log.info("服务{}不存在！", serviceId);
            return;
        }
        int userId = serviceOrder.getUserId();
        UserRecord userInfo = userService.getUserByUserId(userId);
        String officeAppId = saas.shop.mp.findOffcialByShopId(getShopId());
        if (officeAppId == null) {
            logger().info("店铺" + getShopId() + "没有关注公众号");
            return;
        }
        StorePojo storeInfo = store.getStore(serviceOrder.getStoreId());
        List<Integer> userIdList = new ArrayList<>();
        UserRecord wxUserInfo = mpScheduleTaskService.checkMp(userInfo.getWxUnionId(), officeAppId);
        if (null == wxUserInfo) {
            // 用户未关注公众号，不发送公众号模板消息给用户
            log.info("用户{}未关注公众号{}，不发送公众号模板消息给用户", userId, officeAppId);
            return;
        }
        userIdList.add(wxUserInfo.getUserId());
        String page = "pages/appointinfo/appointinfo?order_sn=" + serviceOrder.getOrderSn();
        String serviceDate = serviceOrder.getServiceDate();
        String servicePeriod = serviceOrder.getServicePeriod();
        String subscriber = serviceOrder.getSubscriber();
        String mobile = serviceOrder.getMobile();
        String storeName = storeInfo.getStoreName();
        String serviceName = service.getServiceName();
        String first = "您已成功预约" + serviceName + "!";
        String remake = "温馨提示：请您按照预约日期 " + serviceDate + "-" + servicePeriod + " 准时前往" + storeName + "。";
        String[][] data = new String[][]{{first, "#173177"}
            , {serviceName, "#173177"}
            , {serviceDate + " " + servicePeriod.substring(0, 5), "#173177"}
            , {subscriber, "#173177"}
            , {mobile, "#173177"}
            , {storeName, "#173177"}
            , {remake, "#173177"}};
        RabbitMessageParam param = RabbitMessageParam.builder()
            .mpTemplateData(MpTemplateData.builder().config(MpTemplateConfig.APPOINTMENT_SUCCESS).data(data).build())
            .page(page).shopId(getShopId()).userIdList(userIdList).type(RabbitParamConstant.Type.BOOKING_SUCCESS)
            .build();
        logger().info("预约成功通知发送模板消息");
        saas.taskJobMainService.dispatchImmediately(param, RabbitMessageParam.class.getName(), getShopId(), TaskJobsConstant.TaskJobEnum.SEND_MESSAGE.getExecutionType());
    }

    /**
     * Gets reservation detail.获取服务预约订单详情(根据订单号)
     *
     * @param param the param
     * @return the reservation detail
     */
    public ReservationDetail getReservationDetail(ReservationDetail param) {
        List<ReservationDetail> list = getReservationDetail(SERVICE_ORDER.ORDER_SN.eq(param.getOrderSn()), ReservationDetail.class);
        list.forEach((e) -> {
            // 门店图片中选一张作为主图
            List<String> imgs = Util.json2Object(e.getStoreImgs(), new TypeReference<List<String>>() {
            }, false);
            e.setStoreImg(CollectionUtils.isNotEmpty(imgs) ? imgs.get(0) : StringUtils.EMPTY);
            // 服务图片中选一张做 主图
            imgs = Util.json2Object(e.getServiceImg(), new TypeReference<List<String>>() {
            }, false);
            e.setServiceImg(CollectionUtils.isNotEmpty(imgs) ? imgs.get(0) : StringUtils.EMPTY);
        });
        Iterator<ReservationDetail> iterator = list.iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    /**
     * Gets reservation detail.获取服务预约订单详情(根据订单id)
     *
     * @param orderId the order id
     * @return the reservation detail
     */
    public ReservationDetail getReservationDetail(Integer orderId) {
        List<ReservationDetail> list = getReservationDetail(SERVICE_ORDER.ORDER_ID.eq(orderId), ReservationDetail.class);
        list.forEach((e) -> {
            // 门店图片中选一张作为主图
            List<String> imgs = Util.json2Object(e.getStoreImgs(), new TypeReference<List<String>>() {
            }, false);
            e.setStoreImg(CollectionUtils.isNotEmpty(imgs) ? imgs.get(0) : StringUtils.EMPTY);
            // 服务图片中选一张做 主图
            imgs = Util.json2Object(e.getServiceImg(), new TypeReference<List<String>>() {
            }, false);
            e.setServiceImg(CollectionUtils.isNotEmpty(imgs) ? imgs.get(0) : StringUtils.EMPTY);
        });
        Iterator<ReservationDetail> iterator = list.iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    /**
     * Gets reservation detail.
     *
     * @param condition the condition
     * @return the reservation detail
     */
    public <T> List<T> getReservationDetail(Condition condition, Class<T> clazz) {
        return db().select(
            SERVICE_ORDER.ORDER_ID
            , SERVICE_ORDER.ORDER_SN
            , SERVICE_ORDER.ORDER_STATUS
            , SERVICE_ORDER.ORDER_STATUS_NAME
            , SERVICE_ORDER.TECHNICIAN_ID
            , SERVICE_ORDER.TECHNICIAN_NAME
            , SERVICE_ORDER.SERVICE_DATE
            , SERVICE_ORDER.SERVICE_PERIOD
            , SERVICE_ORDER.VERIFY_CODE
            , SERVICE_ORDER.CREATE_TIME,
            SERVICE_ORDER.SERVICE_ID
            , SERVICE_ORDER.STORE_ID
            , SERVICE_ORDER.MONEY_PAID
            , STORE_SERVICE.SERVICE_NAME
            , STORE_SERVICE.SERVICE_PRICE
            , STORE_SERVICE.SERVICE_SUBSIST
            , STORE_SERVICE.SERVICE_IMG
            , STORE.STORE_NAME
            , STORE.STORE_IMGS
            , STORE.PROVINCE_CODE
            , STORE.CITY_CODE
            , STORE.DISTRICT_CODE
            , STORE.ADDRESS
            , STORE.LATITUDE
            , STORE.LONGITUDE
            , STORE.MOBILE
        ).
            from(SERVICE_ORDER).leftJoin(STORE_SERVICE).on(SERVICE_ORDER.SERVICE_ID.eq(STORE_SERVICE.ID))
            .leftJoin(STORE).on(SERVICE_ORDER.STORE_ID.eq(STORE.STORE_ID))
            .where(condition).and(SERVICE_ORDER.DEL_FLAG.eq(BYTE_ZERO))
            .orderBy(SERVICE_ORDER.CREATE_TIME.desc())
            .fetchInto(clazz);
    }

    /**
     * Confirm complete reservation detail.服务订单确认完成
     *
     * @param param the param
     * @return the reservation detail
     */
    public ReservationDetail confirmComplete(ReservationDetail param) {
        String username = Optional.ofNullable(userService.getUserInfo(param.getUserId())).orElseThrow(() -> new BusinessException(CODE_DATA_NOT_EXIST, "user:" + param.getUserId())).getUsername();
        Map<Field<?>, Object> map = new HashMap<Field<?>, Object>(5) {{
            put(SERVICE_ORDER.FINISHED_TIME, Timestamp.valueOf(LocalDateTime.now()));
            put(SERVICE_ORDER.ORDER_STATUS, ORDER_STATUS_FINISHED);
            put(SERVICE_ORDER.ORDER_STATUS_NAME, ORDER_STATUS_NAME_FINISHED);
            put(SERVICE_ORDER.VERIFY_ADMIN, username);
            put(SERVICE_ORDER.VERIFY_TYPE, BYTE_ONE);
        }};
        this.transaction(() -> {
            serviceOrderService.updateServiceOrder(param.getOrderId(), map);
            //预约完成服务销量加一
            Integer serviceId = serviceOrderService.selectSingleField(param.getOrderId(), SERVICE_ORDER.SERVICE_ID);
            storeService.updateSingleField(serviceId, STORE_SERVICE.SALE_NUM, STORE_SERVICE.SALE_NUM.add(INTEGER_ONE));
        });
        return getReservationDetail(param.getOrderId());
    }

    /**
     * Reservation list map.预约列表
     *
     * @param userId the user id
     * @return the map
     */
    public Map<Byte, List<ReservationListVo>> reservationList(Integer userId) {
        List<ReservationListVo> list = getReservationDetail(SERVICE_ORDER.USER_ID.eq(userId), ReservationListVo.class);
        list.forEach((e) -> {
            // 门店图片中选一张作为主图
            List<String> imgs = Util.json2Object(e.getStoreImgs(), new TypeReference<List<String>>() {
            }, false);
            e.setStoreImg(CollectionUtils.isNotEmpty(imgs) ? imgs.get(0) : StringUtils.EMPTY);
            // 服务图片中选一张做 主图
            imgs = Util.json2Object(e.getServiceImg(), new TypeReference<List<String>>() {
            }, false);
            e.setServiceImg(CollectionUtils.isNotEmpty(imgs) ? imgs.get(0) : StringUtils.EMPTY);
            if (commentService.isComment(e.getOrderSn())) {
                e.setFlag(BYTE_ONE);
            }
        });
        Map<Byte, List<ReservationListVo>> map = list.stream().collect(groupingBy(ReservationListVo::getOrderStatus));
        return map;
    }

    /**
     * Reservation list list.预约列表
     *
     * @param userId      the user id
     * @param orderStatus the order status
     * @return the list
     */
    public List<ReservationListVo> reservationList(Integer userId, Byte orderStatus) {
        List<ReservationListVo> list;
        if (Objects.isNull(orderStatus)) {
            list = getReservationDetail(SERVICE_ORDER.USER_ID.eq(userId), ReservationListVo.class);
        } else {
            list = getReservationDetail(SERVICE_ORDER.USER_ID.eq(userId).and(SERVICE_ORDER.ORDER_STATUS.eq(orderStatus)), ReservationListVo.class);
        }
        list.forEach((e) -> {
            // 门店图片中选一张作为主图
            List<String> imgs = Util.json2Object(e.getStoreImgs(), new TypeReference<List<String>>() {
            }, false);
            e.setStoreImg(CollectionUtils.isNotEmpty(imgs) ? imgs.get(0) : StringUtils.EMPTY);
            // 服务图片中选一张做 主图
            imgs = Util.json2Object(e.getServiceImg(), new TypeReference<List<String>>() {
            }, false);
            e.setServiceImg(CollectionUtils.isNotEmpty(imgs) ? imgs.get(0) : StringUtils.EMPTY);
            // 图片加域名处理
            e.setServiceImg(domainConfig.imageUrl(e.getServiceImg()));
            if (commentService.isComment(e.getOrderSn())) {
                e.setFlag(BYTE_ONE);
            }
        });
        return list;
    }

    /**
     * Reservation del.预约订单删除
     *
     * @param orderId the order id
     */
    public void reservationDel(Integer orderId) {
        serviceOrderService.updateSingleField(orderId, SERVICE_ORDER.DEL_FLAG, BYTE_ONE);
    }

    /**
     * Cancel wait to pay reservation.取消待付款订单
     *  门店服务不支持退款（包括用户余额，会员卡余额）
     * @param param
     */
    public void cancelWaitToPayReservation(ReservationDetail param) {
        Map<Field<?>, Object> map = new HashMap<Field<?>, Object>(5) {{
            put(SERVICE_ORDER.CANCELLED_TIME, Timestamp.valueOf(LocalDateTime.now()));
            put(SERVICE_ORDER.ORDER_STATUS, ORDER_STATUS_CANCELED);
            put(SERVICE_ORDER.ORDER_STATUS_NAME, ORDER_STATUS_NAME_CANCELED);
            put(SERVICE_ORDER.CANCEL_REASON, param.getCancelReason());
        }};
        serviceOrderService.updateServiceOrder(param.getOrderId(), map);
        int shopId = getShopId();
        // 调用微信关闭订单接口
        CompletableFuture.supplyAsync(() -> cancelWxOrder(param.getOrderSn(),shopId));
    }

    private boolean cancelWxOrder(String orderSn, int shopId) {
        // 队列五分钟后调用微信关闭订单接口

        OrderCloseQueenParam param = new OrderCloseQueenParam();
        param.setShopId(shopId);
        param.setOrderSn(orderSn);
        Timestamp startTime = DateUtils.getDalyedDateTime(60*5);

        TaskJobInfo info = TaskJobInfo.builder(shopId)
            .type(TaskJobsConstant.EXECUTION_TIMING)
            .content(param)
            .className(OrderCloseQueenParam.class.getName())
            .startTime(startTime)
            .executionType(TaskJobsConstant.TaskJobEnum.WX_CLOSEORDER)
            .builder();
       taskJobMainService.dispatch(info);
        return true;
    }

    /**
     * Gets store mobile.获取门店联系方式
     *
     * @param storeId the store id
     * @return the store mobile
     */
    public String getStoreMobile(int storeId) {
        return db().select(STORE.MOBILE).from(STORE).where(STORE.STORE_ID.eq(storeId)).fetchOneInto(String.class);
    }

    /**
     * Reservation comment service comment vo.订单评价
     *
     * @param orderSn the order sn
     * @return the service comment vo
     */
    public ServiceCommentVo reservationComment(String orderSn) {
        ServiceCommentVo vo = new ServiceCommentVo();
        ServiceOrderDetailVo temp = serviceOrderService.getServiceOrderDetail(orderSn);
        vo.setOrderSn(temp.getOrderSn());
        vo.setServiceId(temp.getServiceId());
        vo.setServiceName(temp.getServiceName());
        vo.setServiceImg(temp.getServiceImg());
        vo.setServiceDate(temp.getServiceDate());
        vo.setServicePeriod(temp.getServicePeriod());
        ServiceCommentVo result = commentService.getCommentByOrderSn(orderSn);
        if (Objects.isNull(result)) {
            return vo;
        }
        FieldsUtil.assignNotNull(result, vo);
        vo.setTechnicianTitle(storeConfigService.getTechnicianTitle());
        return vo;
    }

    /**
     * Create comment.添加预约评价
     *
     * @param param the param
     */
    public void createComment(ServiceCommentVo param) {
        if (commentService.isComment(param.getOrderSn())) {
            throw new BusinessException(JsonResultCode.CODE_DATA_ALREADY_EXIST, "Comment ");
        }
        // 门店服务评论配置：0不用审核    1先发后审   2先审后发
        Byte commConfig = storeConfigService.getServiceComment();
        CommentServiceRecord serviceRecord = new CommentServiceRecord();
        ServiceOrderDetailVo temp = serviceOrderService.getServiceOrderDetail(param.getOrderSn());
        FieldsUtil.assignNotNull(temp, param);
        FieldsUtil.assignNotNull(param, serviceRecord);
        // 0:未审批,1:审批通过,2:审批未通过（不用审核时评价状态直接为通过，否则为待审核）
        serviceRecord.setFlag(BYTE_ZERO.equals(commConfig) ? BYTE_ONE : BYTE_ZERO);
        commentService.createComment(serviceRecord);
    }

    /**
     * 需要自动取消的待付款订单
     * @return
     */
    public Result<ServiceOrderRecord> getExpiredUnpaidOrders(){
        int cancelTime = shopCommonConfigService.getCancelTime();
        Timestamp expiredTime = DateUtils.getDalyedDateTime(- cancelTime * 60);
        return db().selectFrom(SERVICE_ORDER).where(SERVICE_ORDER.ORDER_STATUS.eq(ORDER_STATUS_WAIT_PAY)).and(SERVICE_ORDER.CREATE_TIME.lt(expiredTime)).fetch();
    }
}
