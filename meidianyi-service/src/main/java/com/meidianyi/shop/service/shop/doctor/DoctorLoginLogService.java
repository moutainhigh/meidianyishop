package com.meidianyi.shop.service.shop.doctor;

import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.pojo.shop.table.DoctorLoginLogDo;
import com.meidianyi.shop.dao.shop.doctor.DoctorLoginLogDao;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorAttendanceDivideVo;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorAttendanceListParam;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorAttendanceOneParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 孔德成
 * @date 2020/9/16 14:05
 */
@Service
public class DoctorLoginLogService extends ShopBaseService {


    @Autowired
    private DoctorLoginLogDao doctorLoginLogDao;

    public final static Byte  THIS_MONTH = 1;
    public final static BigDecimal  DECIMAL_ZERO = new BigDecimal(0);
    public final static BigDecimal  DECIMAL_HALF = new BigDecimal("0.5");
    public final static BigDecimal  DECIMAL_THIRD_QUARTER = new BigDecimal("0.75");
    public final static BigDecimal  DECIMAL_TWO = new BigDecimal(2);
    public final static BigDecimal  DECIMAL_DIFFER = new BigDecimal("0.000001");


    /**
     * 添加医师登录记录
     *
     * @param param
     * @return
     */
    public Integer save(DoctorLoginLogDo param) {
        return doctorLoginLogDao.save(param);

    }

    public Integer getDoctorNum(BigDecimal min, BigDecimal max,Byte type){
        List<Integer> doctorIds = doctorLoginLogDao.getDoctorIds(min,max,type);
        return doctorIds.size();
    }

    public DoctorAttendanceDivideVo getDoctorAttendanceDivide(Byte type){
        DoctorAttendanceDivideVo doctorAttendanceDivideVo = new DoctorAttendanceDivideVo();
        doctorAttendanceDivideVo.setHalfNum(getDoctorNum(DECIMAL_ZERO,DECIMAL_HALF,type));
        doctorAttendanceDivideVo.setThirdQuarterNum(getDoctorNum(DECIMAL_HALF,DECIMAL_THIRD_QUARTER,type));
        doctorAttendanceDivideVo.setFourthQuarterNum(getDoctorNum(DECIMAL_THIRD_QUARTER,DECIMAL_TWO,type));
        return doctorAttendanceDivideVo;
    }

    /**
     * 医师出勤率
     * @param param
     * @return
     */
    public PageResult<DoctorAttendanceOneParam> getDoctorAttendancePage(DoctorAttendanceListParam param) {
        PageResult<DoctorAttendanceOneParam> dataList = doctorLoginLogDao.getDoctorAttendancePage(param);
        if(dataList.getDataList().size()==0){
            return dataList;
        }
        BigDecimal lastRate = dataList.getDataList().get(0).getLoginRate();
        Integer lastRank  = doctorLoginLogDao.getDoctorAttendanceRank(lastRate.add(DECIMAL_DIFFER),param.getType());
        Integer index = 1;
        for(DoctorAttendanceOneParam data:dataList.getDataList()) {
            if(!lastRate.equals(data.getLoginRate())) {
                lastRank = (dataList.getPage().getCurrentPage() - 1)*5 + index;
            }
            lastRate = data.getLoginRate();
                data.setLoginRate(data.getLoginRate().setScale(2,BigDecimal.ROUND_HALF_UP));
            data.setLoginRank(lastRank);
            index++;
        }
        return dataList;
    }

}
