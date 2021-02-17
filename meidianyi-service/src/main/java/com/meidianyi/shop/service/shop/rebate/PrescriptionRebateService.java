package com.meidianyi.shop.service.shop.rebate;

import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.excel.ExcelWriter;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.pojo.shop.table.GoodsMedicalInfoDo;
import com.meidianyi.shop.common.pojo.shop.table.OrderGoodsDo;
import com.meidianyi.shop.common.pojo.shop.table.OrderInfoDo;
import com.meidianyi.shop.common.pojo.shop.table.PrescriptionDo;
import com.meidianyi.shop.common.pojo.shop.table.PrescriptionItemDo;
import com.meidianyi.shop.dao.shop.goods.GoodsMedicalInfoDao;
import com.meidianyi.shop.dao.shop.order.OrderGoodsDao;
import com.meidianyi.shop.dao.shop.prescription.PrescriptionDao;
import com.meidianyi.shop.dao.shop.prescription.PrescriptionItemDao;
import com.meidianyi.shop.dao.shop.rebate.PrescriptionRebateDao;
import com.meidianyi.shop.db.shop.tables.records.OrderGoodsRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.config.rebate.RebateConfig;
import com.meidianyi.shop.service.pojo.shop.config.rebate.RebateConfigConstant;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorOneParam;
import com.meidianyi.shop.service.pojo.shop.medical.goods.MedicalGoodsConstant;
import com.meidianyi.shop.service.pojo.shop.prescription.config.PrescriptionConstant;
import com.meidianyi.shop.service.pojo.shop.rebate.PrescriptionRebateConstant;
import com.meidianyi.shop.service.pojo.shop.rebate.PrescriptionRebateListParam;
import com.meidianyi.shop.service.pojo.shop.rebate.PrescriptionRebateParam;
import com.meidianyi.shop.service.pojo.shop.rebate.PrescriptionRebateReportVo;
import com.meidianyi.shop.service.pojo.shop.rebate.PrescriptionRebateVo;
import com.meidianyi.shop.service.pojo.shop.rebate.RebateReportConstant;
import com.meidianyi.shop.service.shop.config.RebateConfigService;
import com.meidianyi.shop.service.shop.doctor.DoctorService;
import com.meidianyi.shop.service.shop.order.goods.OrderGoodsService;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.YES;

/**
 * @author yangpengcheng
 * @date 2020/8/26
 **/
@Service
public class PrescriptionRebateService extends ShopBaseService {
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private PrescriptionRebateDao prescriptionRebateDao;
    @Autowired
    private PrescriptionItemDao prescriptionItemDao;
    @Autowired
    private RebateConfigService rebateConfigService;
    @Autowired
    private GoodsMedicalInfoDao medicalInfoDao;
    @Autowired
    private OrderGoodsDao orderGoodsDao;
    @Autowired
    private PrescriptionDao prescriptionDao;
    @Autowired
    private OrderGoodsService orderGoodsService;

    /**
     * 处方返利信息入库
     * @param prescription
     */
    public void addPrescriptionRebate(PrescriptionDo prescription,OrderInfoDo order){
        RebateConfig rebateConfig=rebateConfigService.getRebateConfig();
        if(rebateConfig!=null&& RebateConfigConstant.SWITCH_ON.equals(rebateConfig.getStatus())){
            List<PrescriptionItemDo> itemList=prescriptionItemDao.listOrderGoodsByPrescriptionCode(prescription.getPrescriptionCode());
            //计算应返利
            updatePrescriptionItem(itemList,order,rebateConfig);
            //返利信息入库
            calculatePrescriptionRebate(prescription,itemList);
            prescriptionDao.updateSettlementFlag(prescription.getPrescriptionCode(),PrescriptionConstant.SETTLEMENT_WAIT);
        }else {
            prescriptionDao.updateSettlementFlag(prescription.getPrescriptionCode(), PrescriptionConstant.SETTLEMENT_NOT);
        }

    }

    /**
     * 计算对应处方明细应返利
     * @param itemList
     * @param order
     */
    public void updatePrescriptionItem( List<PrescriptionItemDo> itemList,OrderInfoDo order,RebateConfig rebateConfig){
        List<OrderGoodsRecord> goodsRecordList = orderGoodsService.getByOrderId(order.getOrderId()).into(OrderGoodsRecord.class);
        goodsRecordList = goodsRecordList.stream().filter(goodsRecord -> {
            if (YES == goodsRecord.getIsGift()) {
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        Integer sums=goodsRecordList.stream().mapToInt(OrderGoodsRecord::getGoodsNumber).sum();
        if(sums==0){
            return;
        }
        BigDecimal avgScoreDiscount = BigDecimalUtil.divide(order.getScoreDiscount(), BigDecimal.valueOf(sums), RoundingMode.HALF_UP);

        for(PrescriptionItemDo item:itemList){
            BigDecimal sharingProportion=rebateConfig.getGoodsSharingProportion().divide(BigDecimalUtil.BIGDECIMAL_100,BigDecimalUtil.FOUR_SCALE,BigDecimal.ROUND_DOWN);
            BigDecimal rxProportion=rebateConfig.getRxMedicalDoctorProportion().divide(BigDecimalUtil.BIGDECIMAL_100,BigDecimalUtil.FOUR_SCALE,BigDecimal.ROUND_DOWN);
            BigDecimal platformRxProportion=rebateConfig.getRxMedicalPlatformProportion().divide(BigDecimalUtil.BIGDECIMAL_100,BigDecimalUtil.FOUR_SCALE,BigDecimal.ROUND_DOWN);
            BigDecimal noRxProportion=rebateConfig.getNoRxMedicalDoctorProportion().divide(BigDecimalUtil.BIGDECIMAL_100,BigDecimalUtil.FOUR_SCALE,BigDecimal.ROUND_DOWN);
            BigDecimal platformNoRxProportion=rebateConfig.getNoRxMedicalPlatformProportion().divide(BigDecimalUtil.BIGDECIMAL_100,BigDecimalUtil.FOUR_SCALE,BigDecimal.ROUND_DOWN);
            GoodsMedicalInfoDo medicalInfoDo = medicalInfoDao.getByGoodsId(item.getGoodsId());
            item.setGoodsSharingProportion(sharingProportion);
            if(MedicalGoodsConstant.IS_RX.equals(medicalInfoDo.getIsRx())){
                item.setRebateProportion(rxProportion);
                item.setPlatformRebateProportion(platformRxProportion);
            }else {
                item.setRebateProportion(noRxProportion);
                item.setPlatformRebateProportion(platformNoRxProportion);
            }
            OrderGoodsDo orderGoodsDo=orderGoodsDao.getByPrescriptionDetailCode(item.getPrescriptionDetailCode());
            //可计算返利商品金额
            BigDecimal canRebateMoney = BigDecimalUtil.subtrac(orderGoodsDo.getDiscountedTotalPrice(), BigDecimalUtil.multiply(avgScoreDiscount, new BigDecimal(orderGoodsDo.getGoodsNumber())));
            canRebateMoney = BigDecimalUtil.compareTo(canRebateMoney, null) > 0 ? canRebateMoney : BigDecimalUtil.BIGDECIMAL_ZERO;
            item.setCanCalculateMoney(canRebateMoney);
            //应返利金额
            item.setTotalRebateMoney(item.getCanCalculateMoney().multiply(item.getGoodsSharingProportion())
                .multiply(item.getRebateProportion())
                .setScale(BigDecimalUtil.FOUR_SCALE,BigDecimal.ROUND_DOWN));
            //平台应返利金额
            item.setPlatformRebateMoney(item.getCanCalculateMoney().multiply(item.getGoodsSharingProportion())
                .multiply(item.getPlatformRebateProportion())
                .setScale(BigDecimalUtil.FOUR_SCALE,BigDecimal.ROUND_DOWN));
            item.setRealRebateMoney(item.getTotalRebateMoney());
            item.setPlatformRealRebateMoney(item.getPlatformRebateMoney());
            prescriptionItemDao.updatePrescriptionItem(item);
        }
    }

    /**
     * 返利信息入库
     * @param prescription
     * @param itemList
     */
    public void calculatePrescriptionRebate(PrescriptionDo prescription,List<PrescriptionItemDo> itemList){
        DoctorOneParam doctor=doctorService.getDoctorByCode(prescription.getDoctorCode());
        //总返利金额
        BigDecimal totalRebateMoney=itemList.stream().map(PrescriptionItemDo::getTotalRebateMoney).reduce(BigDecimal.ZERO,BigDecimal::add);
        BigDecimal platformTotalRebateMoney=itemList.stream().map(PrescriptionItemDo::getPlatformRebateMoney).reduce(BigDecimal.ZERO,BigDecimal::add);
        //总金额
        BigDecimal totalMoney=itemList.stream().map(PrescriptionItemDo::getMedicinePrice).reduce(BigDecimal.ZERO,BigDecimal::add);
//        for(PrescriptionItemDo item :itemList){
//            BigDecimal num=BigDecimal.valueOf(item.getDragSumNum());
//            BigDecimal itemTotalMoney=item.getMedicinePrice().multiply(num);
//            totalMoney=totalMoney.add(itemTotalMoney);
//        }
        BigDecimal canRebateMoneyTotal=itemList.stream().map(PrescriptionItemDo::getCanCalculateMoney).reduce(BigDecimal.ZERO,BigDecimal::add);
        PrescriptionRebateParam rebateParam=new PrescriptionRebateParam();
        rebateParam.setPrescriptionCode(prescription.getPrescriptionCode());
        rebateParam.setStatus(PrescriptionRebateConstant.TO_REBATE);
        rebateParam.setDoctorId(doctor.getId());
        rebateParam.setTotalMoney(totalMoney.setScale(BigDecimalUtil.DEFAULT_SCALE,BigDecimal.ROUND_DOWN));
        rebateParam.setTotalRebateMoney(totalRebateMoney.setScale(BigDecimalUtil.DEFAULT_SCALE,BigDecimal.ROUND_DOWN));
        rebateParam.setCanCalculateMoney(canRebateMoneyTotal);
        rebateParam.setRealRebateMoney(rebateParam.getTotalRebateMoney());
        //平台返利金额
        rebateParam.setPlatformRebateMoney(platformTotalRebateMoney.setScale(BigDecimalUtil.DEFAULT_SCALE,BigDecimal.ROUND_DOWN));
        rebateParam.setPlatformRealRebateMoney(rebateParam.getTotalRebateMoney());
        prescriptionRebateDao.addPrescriptionRebate(rebateParam);
    }
    /**
     * 分页查询列表
     * @param param
     * @return
     */
    public PageResult<PrescriptionRebateVo> getPageList(PrescriptionRebateListParam param){
        PageResult<PrescriptionRebateVo> result=prescriptionRebateDao.getPageList(param);
        List<PrescriptionRebateVo> list=result.getDataList();
        for(PrescriptionRebateVo vo:list){
            List<PrescriptionItemDo> prescriptionItemDos =prescriptionItemDao.listOrderGoodsByPrescriptionCode(vo.getPrescriptionCode());
            vo.setMedicalList(prescriptionItemDos);
        }
        return result;
    }

    /**
     * 报表导出
     * @param param
     * @return
     */
    public Workbook listExport(PrescriptionRebateListParam param,String lang){
        List<PrescriptionRebateReportVo> list=prescriptionRebateDao.getList(param);
        list.stream().forEach(vo -> {
            if(PrescriptionRebateConstant.TO_REBATE.equals(vo.getStatus())){
                vo.setStatusName(RebateReportConstant.WAIT_REBATE);
            }else if(PrescriptionRebateConstant.REBATED.equals(vo.getStatus())){
                vo.setStatusName(RebateReportConstant.REBATED);
            }else if(PrescriptionRebateConstant.REBATE_FAIL.equals(vo.getStatus())){
                vo.setStatusName(RebateReportConstant.REBATE_FAIL);
            }
        });
        Workbook workbook= ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(lang,workbook);
        excelWriter.writeModelList(list,PrescriptionRebateReportVo.class);
        return workbook;
    }

    public BigDecimal getRealRebateByDoctorDate(Integer doctorId, Timestamp startTime, Timestamp endTime) {
       return prescriptionRebateDao.getRealRebateByDoctorDate(doctorId,startTime,endTime);
    }
}
