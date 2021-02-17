package com.meidianyi.shop.service.shop.department;

import com.meidianyi.shop.common.pojo.shop.table.DepartmentSummaryTrendDo;
import com.meidianyi.shop.dao.shop.department.DepartmentSummaryTrendDao;
import com.meidianyi.shop.service.pojo.shop.department.DepartmentStatisticParam;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorStatisticMinMaxVo;
import com.meidianyi.shop.service.pojo.shop.store.statistic.StatisticConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author chenjie
 * @date 2020年09月09日
 */
@Service
public class DepartmentStatisticService {

    @Autowired
    protected DepartmentSummaryTrendDao departmentSummaryTrendDao;

    @Autowired
    public DepartmentService departmentService;
    /**
     * 统计科室相关数据
     * @param param
     */
    public void statisticDepartment(DepartmentStatisticParam param) {
        DepartmentSummaryTrendDo departmentSummary = getDepartmentSummary(param);
        DepartmentSummaryTrendDo hasStatisticInfo = getStoreStatistic(param);
        if (hasStatisticInfo != null) {
            departmentSummary.setId(hasStatisticInfo.getId());
            updateDepartmentStatistic(departmentSummary);
        } else {
            insertDepartmentStatistic(departmentSummary);
        }
    }

    public DepartmentSummaryTrendDo getDepartmentSummary(DepartmentStatisticParam param) {
        DepartmentSummaryTrendDo departmentSummaryTrendDo = departmentService.getDepartmentStatisData(param);
        departmentSummaryTrendDo.setRefDate(param.getRefDate());
        departmentSummaryTrendDo.setType(param.getType());
        departmentSummaryTrendDo.setDepartmentId(param.getDepartmentId());
        return departmentSummaryTrendDo;
    }

    /**
     * 查询记录
     *
     * @param param
     * @return
     */
    public DepartmentSummaryTrendDo getStoreStatistic(DepartmentStatisticParam param) {
        if (StatisticConstant.TYPE_TODAY.equals(param.getType())) {
            LocalDateTime today = LocalDate.now().atStartOfDay();
            param.setStartTime(Timestamp.valueOf(today));
            param.setEndTime(Timestamp.valueOf(today.plusDays(1)));
            return getDepartmentSummary(param);
        } else {
            return departmentSummaryTrendDao.getDepartmentStatistic(param);
        }
    }

    /**
     * 添加记录
     *
     * @param param
     * @return
     */
    public void insertDepartmentStatistic(DepartmentSummaryTrendDo param) {
        departmentSummaryTrendDao.insertDepartmentStatistic(param);
    }

    /**
     * 更新记录
     *
     * @param param
     * @return
     */
    public void updateDepartmentStatistic(DepartmentSummaryTrendDo param) {
        departmentSummaryTrendDao.updateDepartmentStatistic(param);
    }

    public DoctorStatisticMinMaxVo getMinMaxStatisticData(Date refDate, Byte type){
        return departmentSummaryTrendDao.getMinMaxStatisticData(refDate,type);
    }

    public void updateDepartmentStatisticScore(Byte type,Date refDate, DoctorStatisticMinMaxVo doctorStatisticMinMax) {
        departmentSummaryTrendDao.updateDepartmentStatisticConsultationScore(type,refDate,doctorStatisticMinMax);
        departmentSummaryTrendDao.updateDepartmentStatisticInquiryScore(type,refDate,doctorStatisticMinMax);
    }
}
