package com.meidianyi.shop.service.shop.order.action;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.pojo.shop.table.GoodsMedicalInfoDo;
import com.meidianyi.shop.common.pojo.shop.table.OrderGoodsDo;
import com.meidianyi.shop.common.pojo.shop.table.OrderInfoDo;
import com.meidianyi.shop.common.pojo.shop.table.OrderMedicalHistoryDo;
import com.meidianyi.shop.common.pojo.shop.table.PrescriptionItemDo;
import com.meidianyi.shop.common.pojo.shop.table.goods.GoodsDo;
import com.meidianyi.shop.dao.shop.doctor.DoctorDao;
import com.meidianyi.shop.dao.shop.goods.GoodsDao;
import com.meidianyi.shop.dao.shop.goods.GoodsMedicalInfoDao;
import com.meidianyi.shop.dao.shop.order.OrderGoodsDao;
import com.meidianyi.shop.dao.shop.order.OrderInfoDao;
import com.meidianyi.shop.dao.shop.order.OrderMedicalHistoryDao;
import com.meidianyi.shop.dao.shop.prescription.PrescriptionDao;
import com.meidianyi.shop.dao.shop.prescription.PrescriptionItemDao;
import com.meidianyi.shop.dao.shop.rebate.PrescriptionRebateDao;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.jedis.JedisKeyConstant;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.foundation.util.IncrSequenceUtil;
import com.meidianyi.shop.service.foundation.util.lock.annotation.RedisLock;
import com.meidianyi.shop.service.foundation.util.lock.annotation.RedisLockKeys;
import com.meidianyi.shop.service.pojo.shop.config.ShopBaseConfig;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorOneParam;
import com.meidianyi.shop.service.pojo.shop.medical.goods.vo.GoodsMedicalOneInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.OrderInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.goods.OrderGoodsMedicalVo;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.OrderServiceCode;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.prescription.OrderToPrescribeQueryParam;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.prescription.PrescriptionMakeParam;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.prescription.audit.OrderGoodsSimpleAuditVo;
import com.meidianyi.shop.service.pojo.shop.prescription.PrescriptionVo;
import com.meidianyi.shop.service.pojo.shop.prescription.config.PrescriptionConstant;
import com.meidianyi.shop.service.shop.doctor.DoctorService;
import com.meidianyi.shop.service.shop.goods.MedicalGoodsService;
import com.meidianyi.shop.service.shop.order.action.base.ExecuteResult;
import com.meidianyi.shop.service.shop.order.action.base.IorderOperate;
import com.meidianyi.shop.service.shop.order.action.base.OrderOperateSendMessage;
import com.meidianyi.shop.service.shop.order.goods.OrderGoodsService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import com.meidianyi.shop.service.shop.patient.PatientService;
import com.meidianyi.shop.service.shop.prescription.PrescriptionService;
import com.meidianyi.shop.service.shop.rebate.PrescriptionRebateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**订单待开方
 * @author yangpengcheng
 * @date 2020/7/24
 **/
@Service
public class OrderMakePrescriptionService extends ShopBaseService implements IorderOperate<OrderToPrescribeQueryParam, PrescriptionMakeParam> {

    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private OrderInfoDao orderInfoDao;
    @Autowired
    private MedicalGoodsService medicalGoodsService;
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private OrderGoodsService orderGoodsService;
    @Autowired
    private OrderGoodsDao orderGoodsDao;
    @Autowired
    private PrescriptionService prescriptionService;
    @Autowired
    private OrderMedicalHistoryDao orderMedicalHistoryDao;
    @Autowired
    private PatientService patientService;
    @Autowired
    private ReturnService  returnService;
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private PrescriptionRebateDao prescriptionRebateDao;
    @Autowired
    private PrescriptionRebateService prescriptionRebateService;
    @Autowired
    private DoctorDao doctorDao;
    @Autowired
    private GoodsMedicalInfoDao medicalInfoDao;
    @Autowired
    private PrescriptionDao prescriptionDao;
    @Autowired
    private PrescriptionItemDao prescriptionItemDao;
    @Autowired
    private OrderOperateSendMessage sendMessage;
    @Override
    public OrderServiceCode getServiceCode() {
        return OrderServiceCode.MAKE_PRESCRIPTION;
    }

    /**
     * 获取待开方订单以及关联药品信息
     * @param param 参数
     * @return
     * @throws MpException
     */
    @Override
    public Object query(OrderToPrescribeQueryParam param) throws MpException {
        PageResult<OrderInfoVo> orderPageResult=orderInfoDao.listOrderInfo(param);
        List<OrderInfoVo> orderList=orderPageResult.getDataList();
        PageResult<OrderGoodsMedicalVo> pageResult=new PageResult<>();
        pageResult.setPage(orderPageResult.getPage());
        List<OrderGoodsMedicalVo> orderGoodsMedicalVoList=new ArrayList<>();
        for(OrderInfoVo orderInfo:orderList){
            OrderGoodsMedicalVo orderGoodsMedicalVo=new OrderGoodsMedicalVo();
            FieldsUtil.assign(orderInfo,orderGoodsMedicalVo);
            //历史诊断
            OrderMedicalHistoryDo medicalHistoryDo= orderMedicalHistoryDao.getByOrderId(orderInfo.getOrderId());
            if(medicalHistoryDo!=null){
                medicalHistoryDo.setPatientComplain(medicalHistoryDo.getPatientComplain());
            }
            orderGoodsMedicalVo.setMedicalHistory(medicalHistoryDo);
            //药品数组
            List<GoodsMedicalOneInfoVo> goodsMedicalOneInfoVoList=new ArrayList<>();
            //根据orderId查出orderGoods列表
            List<OrderGoodsDo> orderGoodsDoList=orderGoodsService.getByOrderId(orderInfo.getOrderId()).into(OrderGoodsDo.class);
            for(OrderGoodsDo orderGoodsDo:orderGoodsDoList ){
                //只需要开方的药品
                if(orderGoodsDo.getMedicalAuditType().equals(OrderConstant.MEDICAL_ORDER_AUDIT_TYPE_CREATE)&&orderGoodsDo.getMedicalAuditStatus().equals(OrderConstant.MEDICAL_AUDIT_DEFAULT)){
                    GoodsMedicalInfoDo goodsMedicalInfoDo=medicalGoodsService.getByGoodsId(orderGoodsDo.getGoodsId());
                    GoodsMedicalOneInfoVo goodsMedicalOneInfoVo=new GoodsMedicalOneInfoVo();
                    FieldsUtil.assign(goodsMedicalInfoDo, goodsMedicalOneInfoVo);
                    GoodsDo goodsDo=goodsDao.getByGoodsId(goodsMedicalInfoDo.getGoodsId());
                    goodsMedicalOneInfoVo.setShopPrice(goodsDo.getShopPrice());
                    goodsMedicalOneInfoVo.setGoodsImg(goodsDo.getGoodsImg());
                    goodsMedicalOneInfoVo.setGoodsNumber(orderGoodsDo.getGoodsNumber());
                    goodsMedicalOneInfoVo.setPrdId(orderGoodsDo.getProductId());
                    goodsMedicalOneInfoVoList.add(goodsMedicalOneInfoVo);
                }

            }
            orderGoodsMedicalVo.setGoodsMedicalOneInfoVoList(goodsMedicalOneInfoVoList);
            orderGoodsMedicalVoList.add(orderGoodsMedicalVo);
        }
        pageResult.setDataList(orderGoodsMedicalVoList);
        return pageResult;
    }



    /**
     * 生成处方
     * @param obj 参数
     * @return
     */
    @Override
    @RedisLock(prefix = JedisKeyConstant.MAKE_PRESCRIPTION_LOCK)
    public ExecuteResult execute(@RedisLockKeys PrescriptionMakeParam obj) {
        logger().info("医师开方-开始");
        OrderInfoDo orderInfoDo=orderInfoService.getByOrderId(obj.getOrderId(),OrderInfoDo.class);
        if(!orderInfoDo.getOrderStatus().equals(OrderConstant.ORDER_TO_AUDIT_OPEN)){
            return ExecuteResult.create(JsonResultCode.CODE_ORDER_STATUS_ALREADY_CHANGE,null);
        }
        List<OrderGoodsSimpleAuditVo> allGoods = orderGoodsDao.listSimpleAuditByOrderId(orderInfoDo.getOrderId());

        List<Integer> recIds=getUnAuditAllRecIds(allGoods);
        if(obj.getAuditStatus().equals(OrderConstant.MEDICAL_AUDIT_PASS)){
            logger().info("orderId:{}开方通过",orderInfoDo.getOrderId());
            transaction(() -> {
                //生成处方，处方明细
                PrescriptionVo prescription=savePrescription(obj,orderInfoDo,allGoods,recIds);
                if(prescription!=null){
                    //处方返利入库
                    prescriptionRebateService.addPrescriptionRebate(prescription,orderInfoDo);

                }
                //更新状态
                orderInfoService.setOrderstatus(orderInfoDo.getOrderSn(),OrderConstant.ORDER_WAIT_DELIVERY);

                orderGoodsDao.updateAuditStatusByRecIds(recIds, OrderConstant.MEDICAL_AUDIT_PASS);
                //更新处方号
                orderGoodsService.updatePrescriptionCode(obj.getOrderId(),prescription.getPrescriptionCode());
                //新订单提醒
                OrderInfoRecord orderInfoRecord=new OrderInfoRecord();
                FieldsUtil.assign(orderInfoDo,orderInfoRecord);
                sendMessage.sendNewOrderMessage(orderInfoRecord);
            });

        }else if(obj.getAuditStatus().equals(OrderConstant.MEDICAL_AUDIT_NOT_PASS)){
            //审核未通过 驳回
            logger().info("orderId:{}开方驳回",orderInfoDo.getOrderId());
            //更新状态
            orderInfoDao.updateAuditStatus(orderInfoDo.getOrderId(),OrderConstant.MEDICAL_AUDIT_NOT_PASS);
            orderGoodsDao.updateAuditStatusByRecIds(recIds, OrderConstant.MEDICAL_AUDIT_NOT_PASS);
            //退款
            returnService.auditNotPassRefund(orderInfoDo.getOrderSn(),obj.getReasonType(),obj.getReasonDesc());
        }
        logger().info("医师开方-结束");

        return ExecuteResult.create();
    }
    public PrescriptionVo savePrescription(PrescriptionMakeParam param, OrderInfoDo orderInfoDo,  List<OrderGoodsSimpleAuditVo> orderGoodsList ,List<Integer> recIds){
        PrescriptionVo prescriptionVo=buildPrescription(param,orderInfoDo);
        List<PrescriptionItemDo> list =new ArrayList<>();
        BigDecimal totalPrize =BigDecimal.ZERO;
        for(OrderGoodsSimpleAuditVo goods:orderGoodsList){
            if(recIds.contains(goods.getRecId())){
                GoodsMedicalInfoDo medicalInfoDo = medicalInfoDao.getByGoodsId(goods.getGoodsId());
                PrescriptionItemDo itemDo = new PrescriptionItemDo();
                itemDo.setPrescriptionCode(prescriptionVo.getPrescriptionCode());
                itemDo.setPrescriptionDetailCode(IncrSequenceUtil.generatePrescriptionCode(PrescriptionConstant.PRESCRIPTION_DETAIL_CODE_PREFIX));
                itemDo.setPrdId(goods.getProductId());
                itemDo.setUseMethod(medicalInfoDo.getGoodsUseMethod());
                itemDo.setPerTimeUnit(medicalInfoDo.getGoodsBasicUnit());
                itemDo.setPerTimeDosageUnit(medicalInfoDo.getGoodsBasicUnit());
                itemDo.setDragSumNum((double) goods.getGoodsNumber());
                itemDo.setDragSumUnit(medicalInfoDo.getGoodsPackageUnit());
                itemDo.setGoodsImg(goods.getGoodsImg());
                itemDo.setMedicinePrice(goods.getShopPrice().multiply(BigDecimal.valueOf(goods.getGoodsNumber())));
                itemDo.setGoodsId(goods.getGoodsId());
                itemDo.setGoodsCommonName(medicalInfoDo.getGoodsCommonName());
                itemDo.setGoodsQualityRatio(medicalInfoDo.getGoodsQualityRatio());
                totalPrize = totalPrize.add(itemDo.getMedicinePrice());
                list.add(itemDo);
                orderGoodsDao.updatePrescriptionDetailCode(goods.getRecId(),itemDo.getPrescriptionDetailCode());
            }
        }
        prescriptionVo.setTotalPrice(totalPrize);
        prescriptionItemDao.batchSave(list);
        prescriptionVo.setList(list);
        prescriptionDao.save(prescriptionVo);
        return prescriptionVo;
    }

    public PrescriptionVo buildPrescription(PrescriptionMakeParam param, OrderInfoDo orderInfoDo){
        DoctorOneParam doctor=doctorDao.getOneInfo(param.getDoctorId());
        PrescriptionVo prescriptionVo =new PrescriptionVo();
        FieldsUtil.assign(param,prescriptionVo);
        prescriptionVo.setDoctorCode(doctor.getHospitalCode());
        prescriptionVo.setDoctorName(doctor.getName());
        //医师签名
        prescriptionVo.setDoctorSignature(doctor.getSignature());
        //医院公章
        ShopBaseConfig shopBaseCfgInfo=saas.shop.getShopBaseInfoById(getShopId());
        prescriptionVo.setCachet(shopBaseCfgInfo.getCachet());
        prescriptionVo.setOrderSn(orderInfoDo.getOrderSn());
        //从订单患者病例中获取
        OrderMedicalHistoryDo orderMedicalHistoryDo=orderMedicalHistoryDao.getByOrderId(param.getOrderId());
        prescriptionVo.setPatientName(orderMedicalHistoryDo.getPatientName());
        prescriptionVo.setPatientSex(orderMedicalHistoryDo.getSex());
        prescriptionVo.setPatientAge(orderMedicalHistoryDo.getAge());
        prescriptionVo.setPatientDiseaseHistory(orderMedicalHistoryDo.getDiseaseHistory());
        prescriptionVo.setPatientAllergyHistory(orderMedicalHistoryDo.getAllergyHistory());
        prescriptionVo.setIdentityType(orderMedicalHistoryDo.getIdentityType());
        prescriptionVo.setIdentityCode(orderMedicalHistoryDo.getIdentityCode());
        prescriptionVo.setPatientTreatmentCode(orderMedicalHistoryDo.getPatientTreatmentCode());
        prescriptionVo.setPrescriptionCode(IncrSequenceUtil.generatePrescriptionCode(PrescriptionConstant.PRESCRIPTION_CODE_PREFIX));
        prescriptionVo.setExpireType(PrescriptionConstant.EXPIRE_TYPE_TIME);
        prescriptionVo.setPrescriptionExpireTime(DateUtils.getTimeStampPlus(PrescriptionConstant.PRESCRIPTION_EXPIRE_DAY, ChronoUnit.DAYS));
        prescriptionVo.setStatus(PrescriptionConstant.STATUS_PASS);
        prescriptionVo.setIsValid(BaseConstant.YES);
        prescriptionVo.setUserId(orderInfoDo.getUserId());
        prescriptionVo.setIsUsed(BaseConstant.YES);
        prescriptionVo.setAuditType(OrderConstant.MEDICAL_ORDER_AUDIT_TYPE_CREATE);
        return prescriptionVo;
    }

    public List<Integer> getUnAuditAllRecIds(List<OrderGoodsSimpleAuditVo> list){
        return list.stream().filter(orderGoodsDo->{
            if(orderGoodsDo.getMedicalAuditType().equals(OrderConstant.MEDICAL_ORDER_AUDIT_TYPE_CREATE)){
                return orderGoodsDo.getMedicalAuditStatus().equals(OrderConstant.MEDICAL_AUDIT_DEFAULT);
            }
            return false;
        }).map(OrderGoodsSimpleAuditVo::getRecId).collect(Collectors.toList());
    }




}
