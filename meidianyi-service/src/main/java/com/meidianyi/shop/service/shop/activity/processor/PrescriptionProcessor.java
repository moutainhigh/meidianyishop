package com.meidianyi.shop.service.shop.activity.processor;

import com.google.common.collect.Lists;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.pojo.shop.table.GoodsMedicalInfoDo;
import com.meidianyi.shop.common.pojo.shop.table.OrderInfoDo;
import com.meidianyi.shop.common.pojo.shop.table.PrescriptionDo;
import com.meidianyi.shop.dao.shop.order.OrderGoodsDao;
import com.meidianyi.shop.dao.shop.order.OrderMedicalHistoryDao;
import com.meidianyi.shop.dao.shop.patient.PatientDao;
import com.meidianyi.shop.dao.shop.prescription.PrescriptionDao;
import com.meidianyi.shop.dao.shop.prescription.PrescriptionItemDao;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderGoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.db.shop.tables.records.ReturnOrderRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.goods.param.SyncHisMedicalOrderRequestParam;
import com.meidianyi.shop.service.pojo.shop.order.goods.param.SyncMedicalOrderGoodsItem;
import com.meidianyi.shop.service.pojo.shop.order.refund.OrderReturnGoodsVo;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.prescription.audit.OrderGoodsSimpleAuditVo;
import com.meidianyi.shop.service.pojo.shop.patient.UserPatientDetailVo;
import com.meidianyi.shop.service.pojo.shop.patient.UserPatientParam;
import com.meidianyi.shop.service.pojo.shop.prescription.PrescriptionItemVo;
import com.meidianyi.shop.service.pojo.shop.prescription.PrescriptionVo;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderBeforeParam;
import com.meidianyi.shop.service.pojo.wxapp.order.goods.OrderGoodsBo;
import com.meidianyi.shop.service.shop.goods.MedicalGoodsService;
import com.meidianyi.shop.service.shop.order.action.base.OrderGoodsPrescriptionCalculate;
import com.meidianyi.shop.service.shop.order.goods.OrderGoodsService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import com.meidianyi.shop.service.shop.patient.PatientService;
import com.meidianyi.shop.service.shop.prescription.PrescriptionService;
import com.meidianyi.shop.service.shop.prescription.UploadPrescriptionService;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 药方
 * @author 孔德成
 * @date 2020/7/6 16:28
 */
@Service
@Slf4j
public class PrescriptionProcessor implements Processor, CreateOrderProcessor {

    @Autowired
    private PrescriptionService prescriptionService;
    @Autowired
    private MedicalGoodsService medicalGoodsService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private OrderGoodsService orderGoodsService;
    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private OrderMedicalHistoryDao orderMedicalHistoryDao;
    @Autowired
    private UploadPrescriptionService uploadPrescriptionService;
    @Autowired
    private PatientDao patientDao;
    @Autowired
    private PrescriptionDao prescriptionDao;
    @Autowired
    private OrderGoodsDao orderGoodsDao;
    @Autowired
    private PrescriptionItemDao prescriptionItemDao;

    @Override
    public Byte getPriority() {
        return 0;
    }

    @Override
    public Byte getActivityType() {
        return BaseConstant.ACTIVITY_TYPE_PRESCRIPTION;
    }

    /**
     * 药品是否有对应处方
     * @param param 参数
     * @throws MpException
     */
    @Override
    public void processInitCheckedOrderCreate(OrderBeforeParam param) throws MpException {
        log.info("药品处方检查-开始");
        //处理患者
        handlePatient(param);
        //处方
        handlePrescription(param);
        log.info("药品处方检查-结束");
    }

    /**
     * 处方
     * @param param
     */
    private void handlePrescription(OrderBeforeParam param) throws MpException {
        //默认非处方药
        param.setOrderMedicalType(OrderConstant.MEDICAL_TYPE_OTC);
        if (BaseConstant.YES.equals(param.getIsPrescription())){
            log.info("根据处方下单");
            //处方商品
            prescriptionOrderGoods(param);
        }else {
            log.info("根据商品匹配处方");
            //订单药品信息
            LinkedHashMap<String, PrescriptionVo> prescriptionMap = auditOrderGoods(param);
            //审核订单类型
            auditOrderInfo(param, prescriptionMap);
        }
    }

    private void prescriptionOrderGoods(OrderBeforeParam param) throws MpException {
        PrescriptionVo prescriptionVo = prescriptionDao.getDoByPrescriptionNo(param.getPrescriptionCode());
        if (prescriptionVo.getIsValid().equals(BaseConstant.NO)){
            throw new MpException(JsonResultCode.MSG_ORDER_MEDICAL_PRESCRIPTION_INVALID);
        }
        if (prescriptionVo.getIsUsed().equals(BaseConstant.NO)){
            log.info("根据可以用的处方下单");
            for (OrderBeforeParam.Goods goods : param.getGoods()) {
                goods.setMedicalAuditType(OrderConstant.MEDICAL_ORDER_AUDIT_TYPE_PRESCRIPTION);
                goods.setMedicalAuditStatus(OrderConstant.MEDICAL_AUDIT_PASS);
                GoodsMedicalInfoDo medicalInfo = medicalGoodsService.getByGoodsId(goods.getGoodsId());
                if (medicalInfo != null) {
                    goods.setMedicalInfo(medicalInfo);
                    if (medicalInfo.getIsRx().equals(OrderConstant.MEDICAL_TYPE_RX)){
                        param.setOrderMedicalType(OrderConstant.MEDICAL_TYPE_RX);
                    }
                }
                PrescriptionItemVo item= prescriptionItemDao.getByPrescriptionCodeGoodsIdPrdId(prescriptionVo.getPrescriptionCode(),goods.getGoodsId(),goods.getProductId());
                goods.setPrescriptionCode(prescriptionVo.getPrescriptionCode());
                goods.setPrescriptionInfo(prescriptionVo);
                goods.setPrescriptionDetailCode(item.getPrescriptionDetailCode());
            }
            //处方下单默认处方药
            param.setCheckPrescriptionStatus(OrderConstant.CHECK_ORDER_PRESCRIPTION_PASS);
            param.setOrderAuditType(OrderConstant.MEDICAL_ORDER_AUDIT_TYPE_PRESCRIPTION);
            param.setPrescriptionList(Lists.newArrayList(prescriptionVo));
        }else {
            log.info("根据历史处方下单");
            for (OrderBeforeParam.Goods goods : param.getGoods()) {
                GoodsMedicalInfoDo medicalInfo = medicalGoodsService.getByGoodsId(goods.getGoodsId());
                if (medicalInfo != null) {
                    goods.setMedicalInfo(medicalInfo);
                    if (medicalInfo.getIsRx().equals(OrderConstant.MEDICAL_TYPE_RX)){
                        param.setOrderMedicalType(OrderConstant.MEDICAL_TYPE_RX);
                    }
                }
                goods.setPrescriptionOldCode(prescriptionVo.getPrescriptionCode());
                goods.setPrescriptionInfo(prescriptionVo);
            }
            if (!OrderConstant.MEDICAL_TYPE_RX.equals(param.getOrderMedicalType())){
                log.info("订单全部非处方药,不需要审核");
                param.setOrderMedicalType(OrderConstant.MEDICAL_TYPE_OTC);
                param.setCheckPrescriptionStatus(OrderConstant.CHECK_ORDER_PRESCRIPTION_NO_NEED);
                param.setOrderAuditType(OrderConstant.MEDICAL_ORDER_AUDIT_TYPE_NOT);
                return;
            }
            for (OrderBeforeParam.Goods goods : param.getGoods()) {
                goods.setMedicalAuditType(OrderConstant.MEDICAL_ORDER_AUDIT_TYPE_AUDIT);
                goods.setMedicalAuditStatus(OrderConstant.MEDICAL_AUDIT_DEFAULT);
            }
            //处方下单默认处方药
            param.setCheckPrescriptionStatus(OrderConstant.CHECK_ORDER_PRESCRIPTION_PASS);
            param.setOrderAuditType(OrderConstant.MEDICAL_ORDER_AUDIT_TYPE_AUDIT);
            param.setPrescriptionList(Lists.newArrayList(prescriptionVo));
        }
    }

    /**
     * 处理患者信息
     * @param param
     */
    private void handlePatient(OrderBeforeParam param) {
        if (param.getPatientId()==null||param.getPatientId().equals(0)){
            Integer integer = patientService.defaultPatientId(param.getWxUserInfo().getUserId());
            param.setPatientId(integer);
        }
        UserPatientDetailVo oneInfoForWx = patientService.getOneInfoForWx(param.getWxUserInfo().getUserId(), param.getPatientId());
        param.setPatientInfo(oneInfoForWx);
    }

    /**
     * 审核类型
     * @param param
     * @param prescriptionVoMap
     */
    private void auditOrderInfo(OrderBeforeParam param,LinkedHashMap<String, PrescriptionVo> prescriptionVoMap) {
        if (OrderConstant.MEDICAL_TYPE_RX.equals(param.getOrderMedicalType())){
            if (OrderConstant.CHECK_ORDER_PRESCRIPTION_PASS.equals(param.getCheckPrescriptionStatus())){
                log.info("处方药订单,药品与处方匹配通过--审核");
                param.setOrderAuditType(OrderConstant.MEDICAL_ORDER_AUDIT_TYPE_AUDIT);
                List<PrescriptionVo> prescriptionVoList =new ArrayList<>();
                /**
                 * 优化处方的匹配,尽量减少处方数量
                 */
                List<Integer> productIdSet =new ArrayList<>(param.getProductIdSet());
                List<PrescriptionVo> prescriptionVos = new ArrayList<>(prescriptionVoMap.values());
                List<List<Integer>> collect = prescriptionVos.stream().map(PrescriptionVo::getOrderProductIdList).collect(Collectors.toList());
                // 使用动态规划算法
                int[] ints = OrderGoodsPrescriptionCalculate.smallestSufficientTeam(productIdSet, collect);
                for (int index: ints){
                    PrescriptionVo prescriptionVo = prescriptionVos.get(index);
                    prescriptionVoList.add(prescriptionVo);
                    List<Integer> productIds = prescriptionVo.getOrderProductIdList();
                    param.getGoods().stream().filter(goods ->
                            productIds.contains(goods.getProductId())|| OrderConstant.MEDICAL_ORDER_AUDIT_TYPE_AUDIT.equals(goods.getMedicalAuditType())
                    ).forEach(goods -> {
                        goods.setPrescriptionInfo(prescriptionVo);
                        goods.setPrescriptionOldCode(prescriptionVo.getPrescriptionCode());
                        goods.setPrescriptionDetailOldCode(prescriptionVo.getPrescriptionDetailOldCode());
                    });
                }
                //处方单号去重
                param.setPrescriptionList(prescriptionVoList);
            }else {
                log.info("处方药订单,存在没有匹配到订单得药品--线上开方");
                param.setOrderAuditType(OrderConstant.MEDICAL_ORDER_AUDIT_TYPE_CREATE);
            }
        }else {
            log.info("没有处方药-不审核");
            param.setOrderAuditType(OrderConstant.MEDICAL_ORDER_AUDIT_TYPE_NOT);
        }
        //处理审核以外
        if (!param.getOrderAuditType().equals(OrderConstant.MEDICAL_ORDER_AUDIT_TYPE_AUDIT)){
            param.getGoods().forEach(goods -> goods.setMedicalAuditType(param.getOrderAuditType()));
        }
    }


    /**
     * 订单药品处方信息
     * @param param
     * @return
     */
    private LinkedHashMap<String, PrescriptionVo> auditOrderGoods(OrderBeforeParam param) {
        List<PrescriptionVo> prescriptionList =new ArrayList<>();
        param.setCheckPrescriptionStatus(OrderConstant.CHECK_ORDER_PRESCRIPTION_NO_NEED);
        UserPatientParam userPatientParam = new UserPatientParam();
        userPatientParam.setUserId(param.getWxUserInfo().getUserId());
        userPatientParam.setPatientId(param.getPatientId());
        Set<Integer> productIdSet=new HashSet<>();
        LinkedHashMap<String, PrescriptionVo> prescriptionVoMap =new LinkedHashMap<>();
        for (OrderBeforeParam.Goods goods : param.getGoods()) {
            GoodsRecord goodsInfo = goods.getGoodsInfo();
            GoodsMedicalInfoDo medicalInfo = medicalGoodsService.getByGoodsId(goodsInfo.getGoodsId());
            //商品的医疗信息
            if (medicalInfo != null&&medicalInfo.getIsRx().equals(BaseConstant.YES)) {
                param.setOrderMedicalType(OrderConstant.MEDICAL_TYPE_RX);
                goods.setMedicalInfo(medicalInfo);
                //获取有效的处方
                List<PrescriptionVo> prescriptionItem = prescriptionService
                            .getByGoodsInfo(goods.getGoodsId(),userPatientParam, medicalInfo.getGoodsCommonName(), medicalInfo.getGoodsQualityRatio(), medicalInfo.getGoodsProductionEnterprise());
                //处方信息
                if (prescriptionItem!=null&&prescriptionItem.size()>0){
                    productIdSet.add(goods.getProductId());
                    for (PrescriptionVo item : prescriptionItem) {
                        if (!prescriptionVoMap.containsKey(item.getPrescriptionCode())) {
                            item.setOrderGoodsList(new ArrayList<>());
                            item.setOrderProductIdList(new ArrayList<>());
                            prescriptionVoMap.put(item.getPrescriptionCode(),item);
                        }
                        prescriptionVoMap.get(item.getPrescriptionCode()).getOrderProductIdList().add(goods.getProductId());
                        prescriptionVoMap.get(item.getPrescriptionCode()).getOrderGoodsList().add(goods);
                    }
                    prescriptionList.addAll(prescriptionItem);
                    if (param.getCheckPrescriptionStatus().equals(OrderConstant.CHECK_ORDER_PRESCRIPTION_NO_NEED)){
                        param.setCheckPrescriptionStatus(OrderConstant.CHECK_ORDER_PRESCRIPTION_PASS);
                    }
                    goods.setMedicalAuditType(OrderConstant.MEDICAL_ORDER_AUDIT_TYPE_AUDIT);
                } else {
                    log.info("{}处方药品没有找到对应的处方信息", goodsInfo.getGoodsName());
                    goods.setMedicalAuditType(OrderConstant.MEDICAL_ORDER_AUDIT_TYPE_CREATE);
                    param.setCheckPrescriptionStatus(OrderConstant.CHECK_ORDER_PRESCRIPTION_NO_PASS);
                }
            }else {
                goods.setMedicalAuditType(OrderConstant.MEDICAL_ORDER_AUDIT_TYPE_NOT);
            }
        }
        param.setProductIdSet(productIdSet);
        return prescriptionVoMap;
    }

    @Override
    public void processSaveOrderInfo(OrderBeforeParam param, OrderInfoRecord order) throws MpException {
        //更新处方状态
        if (OrderConstant.MEDICAL_ORDER_AUDIT_TYPE_PRESCRIPTION.equals(order.getOrderAuditType())){
            prescriptionDao.updatePrescriprionIsUseredAndOrderSn(param.getPrescriptionCode(),order.getOrderSn());
        }
    }

    /**
     * 支付成功
     * @param param
     * @param order
     * @throws MpException
     */
    @Override
    public void processOrderEffective(OrderBeforeParam param, OrderInfoRecord order) throws MpException {
        /**
         * 订单同步到his
         */
        try {
            if (!order.getOrderAuditType().equals(OrderConstant.MEDICAL_ORDER_AUDIT_TYPE_NOT)){
                Result<OrderGoodsRecord> goods = orderGoodsService.getByOrderId(order.getOrderId());
                uploadPrescriptionService.uploadPrescription(order.into(OrderInfoDo.class), goods.into(OrderGoodsBo.class));
            }
        }catch (Exception e){
            log.error("订单同步到his异常");
            e.printStackTrace();
        }
    }

    @Override
    public void processUpdateStock(OrderBeforeParam param, OrderInfoRecord order) throws MpException {

    }

    @Override
    public void processReturn(ReturnOrderRecord returnOrderRecord, Integer activityId, List<OrderReturnGoodsVo> returnGoods) throws MpException {
        //同步订单状态到his
        if (returnOrderRecord!=null){
            Result<OrderGoodsRecord> goodsList = orderGoodsService.getByOrderId(returnOrderRecord.getOrderId());
            SyncHisMedicalOrderRequestParam param =new SyncHisMedicalOrderRequestParam();
            param.setOrderSn(returnOrderRecord.getOrderSn());
            param.setStatus(OrderConstant.EXTERNAL_HIS_ORDER_STATUS_REFUND);
            List<SyncMedicalOrderGoodsItem> list =new ArrayList<>();
            for (OrderGoodsRecord goods : goodsList) {
                SyncMedicalOrderGoodsItem item = new SyncMedicalOrderGoodsItem();
                GoodsMedicalInfoDo medicalInfoDo = medicalGoodsService.getByGoodsId(goods.getGoodsId());
                item.setGoodsCode(medicalInfoDo.getGoodsCode());
                item.setNumber(goods.getGoodsNumber());
                list.add(item);
            }
            param.setGoodsItemList(list);
            orderGoodsService.syncMedicalOrderStatus(param);
        }else if (returnGoods!=null&&returnGoods.size()>0){
            // 定时任务和取消 处方下单的处方恢复未使用状态
            OrderReturnGoodsVo orderReturnGoodsVo = returnGoods.get(0);
            OrderInfoRecord order = orderInfoService.getByOrderId(orderReturnGoodsVo.getOrderId(), OrderInfoRecord.class);
            if (order!=null&& OrderConstant.MEDICAL_ORDER_AUDIT_TYPE_PRESCRIPTION.equals(order.getOrderAuditType())){
                List<OrderGoodsSimpleAuditVo> allGoods = orderGoodsDao.listSimpleAuditByOrderId(order.getOrderId());
                List<String> codes = allGoods.stream().map(OrderGoodsSimpleAuditVo::getPrescriptionCode).collect(Collectors.toList());
                List<PrescriptionDo> prescriptionDoList = prescriptionDao.listPrescriptionByCode(codes, PrescriptionDo.class);
                if (prescriptionDoList!=null){
                    prescriptionDoList.forEach(item->{
                        prescriptionDao.updatePrescriprionIsUnUsered(item.getPrescriptionCode());
                    });
                }
            }

        }

    }

}
