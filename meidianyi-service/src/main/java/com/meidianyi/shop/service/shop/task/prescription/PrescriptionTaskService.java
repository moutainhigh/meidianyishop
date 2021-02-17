package com.meidianyi.shop.service.shop.task.prescription;

import com.meidianyi.shop.common.pojo.shop.table.PrescriptionDo;
import com.meidianyi.shop.dao.shop.prescription.PrescriptionDao;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.prescription.FetchPrescriptionVo;
import com.meidianyi.shop.service.pojo.shop.prescription.config.PrescriptionConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yangpengcheng
 * @date 2020/8/6
 **/
@Service
public class PrescriptionTaskService  extends ShopBaseService {
    @Autowired
    private PrescriptionDao prescriptionDao;

    /**
     * 定时任务处理过期的处方
     */

    public void expiredPrescription(){
        logger().info("过期处方定时任务开始,shopId{}",getShopId());
        List<PrescriptionDo> list=prescriptionDao.getAllExpiredPrescription();
        list.forEach(prescriptionDo -> {
            logger().info("过期处方,处方号{}",prescriptionDo.getPrescriptionCode());
            FetchPrescriptionVo fetchPrescriptionVo=new FetchPrescriptionVo();
            fetchPrescriptionVo.setPrescriptionCode(prescriptionDo.getPrescriptionCode());
            //设为过期
            fetchPrescriptionVo.setExpireType(PrescriptionConstant.EXPIRE_TYPE_INVALID);
            prescriptionDao.updateHisPrescription(fetchPrescriptionVo);
        });
        logger().info("过期处方定时任务结束,shopId{}",getShopId());
    }
}
