package com.meidianyi.shop.service.shop.order.inquiry;

import cn.hutool.core.date.DateUtil;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.meidianyi.shop.common.foundation.data.ImSessionConstant;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.excel.ExcelWriter;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.common.pojo.main.table.PlatformTotalRebateDo;
import com.meidianyi.shop.common.pojo.shop.table.ImSessionDo;
import com.meidianyi.shop.common.pojo.shop.table.InquiryOrderDo;
import com.meidianyi.shop.common.pojo.shop.table.InquiryOrderRefundListDo;
import com.meidianyi.shop.common.pojo.shop.table.UserDo;
import com.meidianyi.shop.dao.main.platform.PlatformTotalRebateDao;
import com.meidianyi.shop.dao.shop.user.UserDao;
import com.meidianyi.shop.dao.shop.department.DepartmentDao;
import com.meidianyi.shop.dao.shop.order.InquiryOrderDao;
import com.meidianyi.shop.dao.shop.rebate.DoctorTotalRebateDao;
import com.meidianyi.shop.dao.shop.rebate.InquiryOrderRebateDao;
import com.meidianyi.shop.dao.shop.refund.InquiryOrderRefundListDao;
import com.meidianyi.shop.db.shop.tables.records.PaymentRecordRecord;
import com.meidianyi.shop.db.shop.tables.records.UserRecord;
import com.meidianyi.shop.service.foundation.exception.BusinessException;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.foundation.util.IncrSequenceUtil;
import com.meidianyi.shop.service.pojo.shop.config.rebate.RebateConfig;
import com.meidianyi.shop.service.pojo.shop.config.rebate.RebateConfigConstant;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorOneParam;
import com.meidianyi.shop.service.pojo.shop.maptemplate.ConsultationOrderPayParam;
import com.meidianyi.shop.service.pojo.shop.maptemplate.ConsultationSuccessParam;
import com.meidianyi.shop.service.pojo.shop.maptemplate.OrderRefundSuccessParam;
import com.meidianyi.shop.service.pojo.shop.operation.RecordTradeEnum;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.patient.PatientInquiryOrderVo;
import com.meidianyi.shop.service.pojo.shop.patient.PatientInquiryVo;
import com.meidianyi.shop.service.pojo.shop.patient.PatientOneParam;
import com.meidianyi.shop.service.pojo.shop.prescription.PrescriptionDoctorVo;
import com.meidianyi.shop.service.pojo.shop.rebate.InquiryOrderRebateConstant;
import com.meidianyi.shop.service.pojo.shop.rebate.InquiryOrderRebateParam;
import com.meidianyi.shop.service.pojo.shop.rebate.InquiryOrderRebateVo;
import com.meidianyi.shop.service.pojo.wxapp.image.ImageSimpleVo;
import com.meidianyi.shop.service.pojo.wxapp.medical.im.param.ImSessionNewParam;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.InquiryOrderConstant;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.InquiryOrderListParam;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.InquiryOrderOnParam;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.InquiryOrderParam;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.InquiryOrderStatisticsParam;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.InquiryToPayParam;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.vo.InquiryOrderDetailVo;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.vo.InquiryOrderStatisticsVo;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.vo.InquiryOrderTotalVo;
import com.meidianyi.shop.service.pojo.wxapp.pay.base.WebPayVo;
import com.meidianyi.shop.service.shop.config.RebateConfigService;
import com.meidianyi.shop.service.shop.doctor.DoctorService;
import com.meidianyi.shop.service.shop.im.ImSessionService;
import com.meidianyi.shop.service.shop.maptemplatesend.MapTemplateSendService;
import com.meidianyi.shop.service.shop.order.refund.ReturnMethodService;
import com.meidianyi.shop.service.shop.order.trade.TradesRecordService;
import com.meidianyi.shop.service.shop.patient.PatientService;
import com.meidianyi.shop.service.shop.payment.MpPaymentService;
import com.meidianyi.shop.service.shop.payment.PaymentRecordService;
import com.meidianyi.shop.service.shop.user.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yangpengcheng
 */
@Service
public class InquiryOrderService extends ShopBaseService {
    public static final String BLANK = "测试";
    public static final BigDecimal HUNDRED = new BigDecimal("100");
    public static final int DECIMAL_POINT= 4;
    @Autowired
    private InquiryOrderDao inquiryOrderDao;
    @Autowired
    private PatientService patientService;
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private DepartmentDao departmentDao;
    @Autowired
    private MpPaymentService mpPaymentService;
    @Autowired
    private UserService userService;
    @Autowired
    private PaymentRecordService paymentRecord;
    @Autowired
    private ReturnMethodService returnMethodService;
    @Autowired
    private TradesRecordService tradesRecord;
    @Autowired
    private InquiryOrderRefundListDao inquiryOrderRefundListDao;
    @Autowired
    private ImSessionService imSessionService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private MapTemplateSendService mapTemplateSendService;
    @Autowired
    private RebateConfigService rebateConfigService;
    @Autowired
    private InquiryOrderRebateDao inquiryOrderRebateDao;
    @Autowired
    private DoctorTotalRebateDao doctorTotalRebateDao;
    @Autowired
    private PlatformTotalRebateDao platformTotalRebateDao;

    /**
     * 问询订单列表
     * @param param
     * @return
     */
    public PageResult<PatientInquiryVo> getInquiryOrderList(InquiryOrderListParam param){
        return inquiryOrderDao.getInquiryOrderList(param);
    }

    /**
     * 订单id获得订单
     * @param orderId
     * @return
     */
    public InquiryOrderDetailVo getDetailByOrderId(Integer orderId){
        InquiryOrderDo inquiryOrderDo=inquiryOrderDao.getByOrderId(orderId);
        UserDo userDo=userDao.getUserById(inquiryOrderDo.getUserId());
        InquiryOrderDetailVo inquiryOrderDetailVo=new InquiryOrderDetailVo();
        FieldsUtil.assign(inquiryOrderDo,inquiryOrderDetailVo);
        inquiryOrderDetailVo.setUserMobile(userDo.getMobile());
        inquiryOrderDetailVo.setUserName(userDo.getUsername());
        //退款记录
        inquiryOrderDetailVo.setRefundList(inquiryOrderRefundListDao.getListByOrderSn(inquiryOrderDo.getOrderSn()));
        //咨询返利详情
        InquiryOrderRebateVo rebateVo=inquiryOrderRebateDao.getRebateByOrderSn(inquiryOrderDo.getOrderSn());
        inquiryOrderDetailVo.setRebate(rebateVo);
        return inquiryOrderDetailVo;
    }

    /**
     * 订单号获得订单
     * @param orderSn
     * @return
     */
    public InquiryOrderDetailVo getDetailByOrderSn(String orderSn){
        InquiryOrderDo inquiryOrderDo=inquiryOrderDao.getByOrderSn(orderSn);
        InquiryOrderDetailVo inquiryOrderDetailVo=new InquiryOrderDetailVo();
        FieldsUtil.assign(inquiryOrderDo,inquiryOrderDetailVo);
        UserDo userDo=userDao.getUserById(inquiryOrderDo.getUserId());
        inquiryOrderDetailVo.setUserMobile(userDo.getMobile());
        inquiryOrderDetailVo.setUserName(userDo.getUsername());
        //退款记录
        inquiryOrderDetailVo.setRefundList(inquiryOrderRefundListDao.getListByOrderSn(inquiryOrderDo.getOrderSn()));
        //咨询返利详情
        InquiryOrderRebateVo rebateVo=inquiryOrderRebateDao.getRebateByOrderSn(inquiryOrderDo.getOrderSn());
        inquiryOrderDetailVo.setRebate(rebateVo);
        inquiryOrderDetailVo.setPatientAge(DateUtils.getAgeByBirthDay(inquiryOrderDetailVo.getPatientBirthday()));
        return inquiryOrderDetailVo;
    }

    /**
     * 更改问诊状态
     * @param param
     */
    public void updateOrder(InquiryOrderOnParam param){
        InquiryOrderDo inquiryOrderDo=inquiryOrderDao.getByOrderSn(param.getOrderSn());
        FieldsUtil.assign(param,inquiryOrderDo);
        inquiryOrderDo.setOrderStatus(param.getOrderStatus());
        transaction(() ->{
            //更新会话状态修改为进行中
            if(param.getOrderStatus().equals(InquiryOrderConstant.ORDER_RECEIVING)){
                inquiryOrderDo.setLimitTime(DateUtils.getTimeStampPlus(InquiryOrderConstant.EXPIRY_TIME_HOUR, ChronoUnit.HOURS));
                imSessionService.updateSessionToGoingOn(param.getSessionId());
                //接诊发送提醒
                sendConsultationMessage(inquiryOrderDo);

            }
            //更新会话状态为关闭
            if(param.getOrderStatus().equals(InquiryOrderConstant.ORDER_FINISHED)){
                inquiryOrderDo.setFinishedTime(DateUtils.getLocalDateTime());
                //未结算返利的更改状态
                if(InquiryOrderConstant.SETTLEMENT_WAIT.equals(inquiryOrderDo.getSettlementFlag())){
                    inquiryOrderDo.setSettlementFlag(InquiryOrderConstant.SETTLEMENT_FINISH);
                    //完成问诊，更改返利状态
                    inquiryOrderRebateDao.updateStatus(inquiryOrderDo.getOrderSn(),InquiryOrderRebateConstant.REBATED,null);
                    //第一次正常结束的时候统计返利金额
                    setDoctorTotalRebate(inquiryOrderDo);
                }
                imSessionService.closeImSession(param.getSessionId());
            }
            inquiryOrderDao.update(inquiryOrderDo);
        });

    }

    /**
     * 接诊发送提醒
     * @param inquiryOrderDo
     */
    public void sendConsultationMessage( InquiryOrderDo inquiryOrderDo){
        List<Integer> list=new ArrayList<>();
        list.add(inquiryOrderDo.getUserId());
        ConsultationSuccessParam consultationSuccessParam=ConsultationSuccessParam.builder().patientName(inquiryOrderDo.getPatientName())
            .receiveTime(DateUtils.getLocalDateFormat())
            .doctorName(inquiryOrderDo.getDoctorName()).userIds(list).build();
        mapTemplateSendService.sendConsultationSuccessMessage(consultationSuccessParam);
    }

    /**
     * 统计返利金额
     * @param inquiryOrderDo
     */
    public void setDoctorTotalRebate( InquiryOrderDo inquiryOrderDo){
        ImSessionDo im=imSessionService.getSessionInfoByOrderSn(inquiryOrderDo.getOrderSn());
        if(im.getContinueSessionCount().equals(ImSessionConstant.CONTINUE_SESSION_TIME)){
            doctorTotalRebateDao.updateDoctorTotalRebate(inquiryOrderDo.getDoctorId(),inquiryOrderDo.getTotalRebateMoney());
        }
        //统计平台返利
        PlatformTotalRebateDo platformTotalRebateDo=new PlatformTotalRebateDo();
        platformTotalRebateDo.setShopId(inquiryOrderDo.getShopId());
        platformTotalRebateDo.setTotalMoney(inquiryOrderDo.getTotalRebateMoney());
        platformTotalRebateDo.setFinalMoney(inquiryOrderDo.getTotalRebateMoney());
        platformTotalRebateDao.savePlatFormTotalRebate(platformTotalRebateDo);
    }

    /**
     * 新增
     * @param inquiryOrderDo
     */
    public void insert(InquiryOrderDo inquiryOrderDo){
        int orderId=inquiryOrderDao.save(inquiryOrderDo);
        inquiryOrderDo.setOrderId(orderId);
    }

    public List<InquiryOrderDo> getCanceledToPaidCloseOrder(){
        return inquiryOrderDao.getCanceledToPaidCloseOrder();
    }
    public List<InquiryOrderDo> getCanceledToWaitingCloseOrder(){
        return inquiryOrderDao.getCanceledToWaitingCloseOrder();
    }

    /**
     * 未完成的问诊
     * @param param
     * @return
     */
    public InquiryOrderDo getUndoneOrder(InquiryOrderParam param){
        List<InquiryOrderDo> list=inquiryOrderDao.getOrderByParams(param);
        List<InquiryOrderDo> retList=list.stream().filter(inquiryOrderDo -> {
            Byte orderStatus=inquiryOrderDo.getOrderStatus();
                if(orderStatus.equals(InquiryOrderConstant.ORDER_TO_RECEIVE)||orderStatus.equals(InquiryOrderConstant.ORDER_RECEIVING)) {
                    return true;
                }
                return false;
        }).collect(Collectors.toList());
        if(retList!=null&&retList.size()>0){
            return retList.get(0);
        }
        return null;
    }
    /**
     * 问诊支付回调完成
     * @param order
     * @param paymentRecord
     * @throws MpException
     */
    public void inquiryOrderFinish(InquiryOrderDo order, PaymentRecordRecord paymentRecord) throws MpException {
        logger().info("问诊订单-支付完成(回调)-开始");
        order.setOrderStatus(InquiryOrderConstant.ORDER_TO_RECEIVE);
        order.setPaySn(paymentRecord==null?"":paymentRecord.getPaySn());
        order.setPayTime(DateUtils.getLocalDateTime());
        transaction(()->{
            //返利配置计算返利
            addRebate(order);
            //更新问诊订单状态为待接诊
            inquiryOrderDao.update(order);
            //添加会话问诊
            ImSessionNewParam imSessionNewParam=new ImSessionNewParam();
            FieldsUtil.assign(order,imSessionNewParam);
            imSessionService.insertNewSession(imSessionNewParam);
            sendConsultationNewOrderMessage(order);
        });

        logger().info("问诊订单-支付完成(回调)-结束");

    }

    /**
     * 发送新咨询提醒
     * @param order
     * @throws MpException
     */
    public void sendConsultationNewOrderMessage(InquiryOrderDo order) throws MpException {
        List<Integer> list=new ArrayList<>();
        DoctorOneParam doctor = doctorService.getOneInfo(order.getDoctorId());
        list.add(doctor.getUserId());
        ConsultationOrderPayParam param=ConsultationOrderPayParam.builder().patientData(order.getPatientName()).diseaseDetail(order.getDescriptionDisease())
            .doctorName(order.getDoctorName()).orderSn(order.getOrderSn()).createTime(DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL,order.getCreateTime()))
            .userIds(list).build();
        mapTemplateSendService.sendConsultationOrderMessage(param);
    }
    /**
     * 返利入库
     * @param order
     */
    public void addRebate(InquiryOrderDo order){
        RebateConfig rebateConfig=this.rebateConfigService.getRebateConfig();
        if(rebateConfig!=null&&RebateConfigConstant.SWITCH_ON.equals(rebateConfig.getStatus())){
            BigDecimal proportion=rebateConfig.getInquiryOrderDoctorProportion().divide(HUNDRED,DECIMAL_POINT,BigDecimal.ROUND_DOWN);
            BigDecimal platformProportion=rebateConfig.getInquiryOrderPlatformProportion().divide(HUNDRED,DECIMAL_POINT,BigDecimal.ROUND_DOWN);
            order.setRebateProportion(proportion);
            order.setPlatformRebateProportion(platformProportion);
            order.setTotalRebateMoney(order.getOrderAmount().multiply(proportion).setScale(BigDecimalUtil.DEFAULT_SCALE,BigDecimal.ROUND_DOWN));
            order.setPlatformRebateMoney(order.getOrderAmount().multiply(platformProportion).setScale(BigDecimalUtil.DEFAULT_SCALE,BigDecimal.ROUND_UP));
            order.setSettlementFlag(InquiryOrderConstant.SETTLEMENT_WAIT);
            //返利入库
            InquiryOrderRebateParam param =new InquiryOrderRebateParam();
            param.setOrderSn(order.getOrderSn());
            param.setDoctorId(order.getDoctorId());
            param.setTotalRebateMoney(order.getTotalRebateMoney().setScale(BigDecimalUtil.DEFAULT_SCALE,BigDecimal.ROUND_DOWN));
            param.setPlatformRebateMoney(order.getPlatformRebateMoney().setScale(BigDecimalUtil.DEFAULT_SCALE,BigDecimal.ROUND_UP));
            param.setStatus(InquiryOrderRebateConstant.TO_REBATE);
            param.setTotalMoney(order.getOrderAmount());
            inquiryOrderRebateDao.addInquiryOrderRebate(param);
        }else {
            order.setSettlementFlag(InquiryOrderConstant.SETTLEMENT_NOT);
        }
    }

    /**
     * 支付微信接口
     * @param param
     * @return
     */
    public WebPayVo payInquiryOrder(InquiryToPayParam param) throws MpException{
        logger().info("创建问诊订单-开始");
        WebPayVo vo = new WebPayVo();
        //支付类型
        String payCode = InquiryOrderConstant.PAY_CODE_WX_PAY;
        InquiryOrderDo inquiryOrderDo=new InquiryOrderDo();
        String orderSn=saveInquiryOrder(param,payCode,inquiryOrderDo);
        UserRecord userRecord=userService.getUserByUserId(param.getUser().getUserId());

        InquiryOrderDo orderInfo=inquiryOrderDao.getByOrderSn(orderSn);
        //临时添加支付回调，正式使用删除
        String test = "test";
        if (param.getDescriptionDisease().contains(test)||param.getOrderAmount().compareTo(BigDecimal.ZERO)<=0) {
            inquiryOrderFinish(orderInfo,new PaymentRecordRecord());
        } else {
            //微信支付接口
            try {
                vo = mpPaymentService.wxUnitOrder(param.getClientIp(), InquiryOrderConstant.GOODS_NAME, orderSn, param.getOrderAmount(), userRecord.getWxOpenid());
            } catch (WxPayException e) {
                logger().error("微信预支付调用接口失败WxPayException，订单号：{},异常：{}", orderSn, e);
                throw new BusinessException(JsonResultCode.CODE_ORDER_WXPAY_UNIFIEDORDER_FAIL);
            }catch (MpException e) {
                logger().error("微信预支付调用接口失败Exception，订单号：{},异常：{}", orderSn, e.getMessage());
                throw new MpException(JsonResultCode.CODE_ORDER_WXPAY_UNIFIEDORDER_FAIL);
            }
            logger().debug("微信支付接口调用结果：{}", vo);
            // 更新记录微信预支付id：prepayid
            inquiryOrderDao.updatePrepayId(orderSn,vo.getResult().getPrepayId());


            logger().debug("微信支付创建订单结束");
        }
        vo.setOrderSn(orderSn);
        return vo;
    }
    private String saveInquiryOrder(InquiryToPayParam payParam, String payCode, InquiryOrderDo inquiryOrderDo) throws MpException {
        if(StringUtils.isNotBlank(payParam.getOrderSn())){
            return payParam.getOrderSn();
        }
        Integer shopId=getShopId();
        String orderSn = IncrSequenceUtil.generateOrderSn(InquiryOrderConstant.INQUIRY_ORDER_SN_PREFIX);
        PatientOneParam patientOneParam=patientService.getOneInfo(payParam.getPatientId());
        DoctorOneParam doctor = doctorService.getOneInfo(payParam.getDoctorId());
        inquiryOrderDo.setOrderSn(orderSn);
        inquiryOrderDo.setShopId(shopId);
        inquiryOrderDo.setOrderAmount(payParam.getOrderAmount());
        inquiryOrderDo.setUserId(payParam.getUser().getUserId());
        inquiryOrderDo.setPatientId(payParam.getPatientId());
        inquiryOrderDo.setDoctorId(payParam.getDoctorId());
        inquiryOrderDo.setDoctorName(doctor.getName());
        inquiryOrderDo.setOrderStatus(InquiryOrderConstant.ORDER_TO_PAID);
        inquiryOrderDo.setPayCode(payCode);
        inquiryOrderDo.setPatientName(patientOneParam.getName());
        inquiryOrderDo.setPatientSex(patientOneParam.getSex());
        inquiryOrderDo.setPatientMobile(patientOneParam.getMobile());
        inquiryOrderDo.setPatientBirthday(patientOneParam.getBirthday());
        inquiryOrderDo.setPatientIdentityCode(patientOneParam.getIdentityCode());
        inquiryOrderDo.setPatientIdentityType(patientOneParam.getIdentityType());
        List<ImageSimpleVo> imageList=payParam.getImageList();
        String imageUrl=Util.toJson(imageList);
        inquiryOrderDo.setImageUrl(imageUrl);
        inquiryOrderDo.setDescriptionDisease(payParam.getDescriptionDisease());
        inquiryOrderDao.save(inquiryOrderDo);
        return orderSn;
    }

    /**
     * 退款调用,可多次退
     * @param inquiryOrderOnParam
     * @return
     */
    public void refund( InquiryOrderOnParam inquiryOrderOnParam) throws MpException{
        InquiryOrderDo inquiryOrderDo=inquiryOrderDao.getByOrderSn(inquiryOrderOnParam.getOrderSn());
        refundInquiryOrder(inquiryOrderDo, inquiryOrderOnParam.getRefundMoney(),inquiryOrderOnParam.getRefundReason());
        transaction(()->{
            //问诊退款，更改返利状态
            if(InquiryOrderConstant.SETTLEMENT_WAIT.equals(inquiryOrderDo.getSettlementFlag())){
                inquiryOrderRebateDao.updateStatus(inquiryOrderDo.getOrderSn(), InquiryOrderRebateConstant.REBATE_FAIL,InquiryOrderRebateConstant.REASON_OPERATE_REFUND);
                inquiryOrderDo.setSettlementFlag(InquiryOrderConstant.SETTLEMENT_NOT);
                inquiryOrderDao.update(inquiryOrderDo);
            }
        });

    }

    /**
     * 医师退款调用
     * @param inquiryOrderOnParam
     * @throws MpException
     */
    public void doctorRefund(InquiryOrderOnParam inquiryOrderOnParam)throws MpException{
        InquiryOrderDo inquiryOrderDo=inquiryOrderDao.getByOrderSn(inquiryOrderOnParam.getOrderSn());
        refundInquiryOrder(inquiryOrderDo, inquiryOrderDo.getOrderAmount(),inquiryOrderOnParam.getRefundReason());
        transaction(()->{
            //问诊退款，更改返利状态
            inquiryOrderRebateDao.updateStatus(inquiryOrderDo.getOrderSn(), InquiryOrderRebateConstant.REBATE_FAIL,InquiryOrderRebateConstant.REASON_DOCTOR_REFUND);
            inquiryOrderDo.setSettlementFlag(InquiryOrderConstant.SETTLEMENT_NOT);
            inquiryOrderDao.update(inquiryOrderDo);
        });

    }

    /**
     * 退款调用
     * @param order
     * @throws MpException
     */
    public void refundInquiryOrder(InquiryOrderDo order,BigDecimal refundMoney,String refundReason)throws MpException{
        logger().info("问诊订单退款-开始start,orderSn:{}"+order.getOrderSn());
        if(InquiryOrderConstant.ORDER_REFUND.equals(order.getOrderStatus())){
            throw new MpException(JsonResultCode.INQUIRY_ORDER_REFUND_MONEY_EXCESS);
        }
        //0元订单
        if(order.getOrderAmount().compareTo(BigDecimal.ZERO)==0){
            order.setOrderStatus(InquiryOrderConstant.ORDER_REFUND);
            inquiryOrderDao.update(order);
            List<String> orderSnList=new ArrayList<>();
            orderSnList.add(order.getOrderSn());
            imSessionService.batchCancelSession(orderSnList);
            return;
        }
        boolean successFlag=true;
        if(InquiryOrderConstant.PAY_CODE_WX_PAY.equals(order.getPayCode())){
            //退款流水号
            String refundSn = IncrSequenceUtil.generateOrderSn(InquiryOrderConstant.INQUIRY_ORDER_SN_PREFIX);
            //支付记录
            PaymentRecordRecord payRecord = paymentRecord.getPaymentRecordByOrderSn(order.getOrderSn());
            if(payRecord == null) {
                logger().error("wxPayRefund 微信支付记录未找到 order_sn={}",order.getOrderSn());
                throw new MpException(JsonResultCode.CODE_ORDER_RETURN_WXPAYREFUND_NO_RECORD);
            }
            BigDecimal refundable=BigDecimalUtil.subtrac(order.getOrderAmount(),order.getRefundMoney());
            if(BigDecimalUtil.compareTo(refundMoney,refundable)>0){
                logger().error("orderSn:{},退款金额超出可退金额",order.getOrderSn());
                throw new MpException(JsonResultCode.INQUIRY_ORDER_REFUND_MONEY_EXCESS);
            }
            int moneyParam=BigDecimalUtil.multiply(refundMoney, new BigDecimal(Byte.valueOf(OrderConstant.TUAN_FEN_RATIO).toString())).intValue();
            if(moneyParam<=0){
                logger().error("orderSn:{},退款金额不正确{}",order.getOrderSn(),refundMoney);
                throw new MpException(JsonResultCode.INQUIRY_ORDER_REFUND_MONEY_ERROR);
            }
            //微信金额单为为分需单位换算
            returnMethodService.refundByApi(order.getPayCode(),payRecord.getTradeNo(), refundSn,BigDecimalUtil.multiply(payRecord.getTotalFee(), new BigDecimal(Byte.valueOf(OrderConstant.TUAN_FEN_RATIO).toString())).intValue(),BigDecimalUtil.multiply(refundMoney, new BigDecimal(Byte.valueOf(OrderConstant.TUAN_FEN_RATIO).toString())).intValue() );
        }
        //退款记录
        InquiryOrderRefundListDo refundListDo=new InquiryOrderRefundListDo();
        refundListDo.setOrderSn(order.getOrderSn());
        refundListDo.setMoneyAmount(refundMoney);
        refundListDo.setRefundReason(refundReason);
        refundListDo.setUserId(order.getUserId());
        refundListDo.setIsSuccess(successFlag?InquiryOrderConstant.REFUND_SUCCESS:InquiryOrderConstant.REFUND_FAILED);
        inquiryOrderRefundListDao.save(refundListDo);
        //更新状态
        BigDecimal newRefundMoney=order.getRefundMoney().add(refundMoney);
        order.setRefundMoney(newRefundMoney);
        if(BigDecimalUtil.compareTo(order.getOrderAmount(),newRefundMoney)==0){
            //已退款
            order.setOrderStatus(InquiryOrderConstant.ORDER_REFUND);
        }else {
            //部分退款
            order.setOrderStatus(InquiryOrderConstant.ORDER_PART_REFUND);

        }
        inquiryOrderDao.update(order);
        //取消会话
        List<String> orderSnList=new ArrayList<>();
        orderSnList.add(order.getOrderSn());
        imSessionService.batchCancelSession(orderSnList);
        //交易记录
        tradesRecord.addRecord(refundMoney,order.getOrderSn(),order.getUserId(), TradesRecordService.TRADE_CONTENT_MONEY, RecordTradeEnum.TYPE_CASH_REFUND.val(),RecordTradeEnum.TRADE_FLOW_OUT.val(),TradesRecordService.TRADE_STATUS_ARRIVAL);
        //退费消息
        sendOrderRefundSuccessMessage(order,refundMoney);
        logger().info("问诊订单退款-结束end,orderSn:{}"+order.getOrderSn());
    }

    /**
     * 发送退款成功消息
     * @param order
     * @param refundMoney
     */
    public void sendOrderRefundSuccessMessage(InquiryOrderDo order,BigDecimal refundMoney){
        OrderRefundSuccessParam param=OrderRefundSuccessParam.builder().refundMoney(refundMoney.toString())
            .payTime(DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL,order.getPayTime())).userIds(Collections.singletonList(order.getUserId())).build();
        mapTemplateSendService.sendOrderRefundSuccessMessage(param);
    }
    /**
     * 问诊订单统计报表查询
     * @param param
     * @return
     */
    public PageResult<InquiryOrderStatisticsVo> orderStatistics(InquiryOrderStatisticsParam param){
        beginAndEndOfDay(param);
        PageResult<InquiryOrderStatisticsVo> result=inquiryOrderDao.orderStatisticsPage(param);
        return result;
    }

    /**
     * 问诊订单统计报表导出
     * @param param
     * @param lang
     * @return
     */
    public Workbook orderStatisticsExport(InquiryOrderStatisticsParam param, String lang){
        beginAndEndOfDay(param);
        List<InquiryOrderStatisticsVo> list=inquiryOrderDao.orderStatistics(param);
        Workbook workbook= ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(lang,workbook);
        excelWriter.writeModelList(list,InquiryOrderStatisticsVo.class);
        return workbook;
    }

    /**
     * 报表总数total查询
     * @param param
     * @return
     */
    public InquiryOrderTotalVo orderStatisticsTotal(InquiryOrderStatisticsParam param){
        beginAndEndOfDay(param);
        InquiryOrderTotalVo inquiryOrderTotalVo=inquiryOrderDao.orderStatisticsTotal(param);
        return inquiryOrderTotalVo;
    }

    /**
     * 日期的时分秒开始和结束
     * @param param
     */
    public void beginAndEndOfDay(InquiryOrderStatisticsParam param){
        Timestamp startDate = param.getStartTime();
        Timestamp endDate = param.getEndTime();
        if (startDate != null ) {
            startDate = DateUtil.beginOfDay(startDate).toTimestamp();
            param.setStartTime(startDate);
        }if( endDate != null){
            endDate = DateUtil.endOfDay(endDate).toTimestamp();
            param.setEndTime(endDate);
        }
    }

    /**
     * 查询患者关联问诊订单数量
     * @param patientId 患者id
     * @return Integer
     */
    public PatientInquiryOrderVo getInquiryNumberByPatient(Integer patientId, Integer doctorId) {
        return inquiryOrderDao.getInquiryNumberByPatientId(patientId, doctorId);
    }

    /**
     * 查找时间段内医师的接诊量
     * @param doctorId
     * @param startTime
     * @param endTime
     */
    public Integer countByDateDoctor(Integer doctorId, Timestamp startTime, Timestamp endTime) {
        return inquiryOrderDao.countByDateDoctorId(doctorId,startTime,endTime);

    }

    /**
     * 查询用户关联医师处方
     * @param doctorId 医师id
     * @param userId 用户id
     * @return PrescriptionDoctorVo
     */
    public PrescriptionDoctorVo getDoctorInquiry(Integer doctorId, Integer userId){
        return inquiryOrderDao.getDoctorInquiry(doctorId, userId);
    }
}
