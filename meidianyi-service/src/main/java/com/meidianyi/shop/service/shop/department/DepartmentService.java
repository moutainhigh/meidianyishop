package com.meidianyi.shop.service.shop.department;

import com.fasterxml.jackson.core.type.TypeReference;
import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalRequestConstant;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalRequestResult;
import com.meidianyi.shop.common.pojo.shop.table.DepartmentSummaryTrendDo;
import com.meidianyi.shop.dao.shop.department.DepartmentDao;
import com.meidianyi.shop.dao.shop.doctor.DoctorDepartmentCoupleDao;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.saas.shop.ShopListInfoVo;
import com.meidianyi.shop.service.pojo.shop.department.*;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorStatisticAllMinMaxVo;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorStatisticMinMaxVo;
import com.meidianyi.shop.service.shop.ShopApplication;
import com.meidianyi.shop.service.shop.config.BaseShopConfigService;
import com.meidianyi.shop.service.shop.config.ShopCommonConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.meidianyi.shop.service.shop.task.overview.GoodsStatisticTaskService.TYPE_LIST_1;

/**
 * @author chenjie
 */
@Service
public class DepartmentService extends BaseShopConfigService {
    @Autowired
    protected DepartmentDao departmentDao;
    @Autowired
    protected DoctorDepartmentCoupleDao doctorDepartmentCoupleDao;
    @Autowired
    public ShopCommonConfigService shopCommonConfigService;
    @Autowired
    public DepartmentStatisticService departmentStatisticService;
    public static final int ZERO = 0;

    public PageResult<DepartmentListVo> getDepartmentList(DepartmentListParam param) {
        PageResult<DepartmentListVo> departmentList = departmentDao.getDepartmentList(param);
        return departmentList;
    }

    public List<DepartmentListVo> listDepartmentsByParentId(Integer departmentId){
        List<DepartmentListVo> departmentList = departmentDao.listDepartmentByParentId(departmentId);
        return departmentList;
    }

    public boolean isNameExist(Integer departmentId,String name) {
        boolean flag = departmentDao.isNameExist(departmentId,name);
        return flag;
    }

    public Integer insertDepartment(DepartmentOneParam param) {
        int level = updateParentIsLeaf(param);
        param.setLevel(level);
        param.setIsLeaf((byte) 1);
        departmentDao.insertDepartment(param);
        return param.getId();
    }

    public Integer updateDepartment(DepartmentOneParam param,Integer oldParentId) {
        int level = updateParentIsLeaf(param);
        param.setLevel(level);
        departmentDao.updateDepartment(param);
        updateOldParentIsLeaf(oldParentId);
        updateDepartmentLevel(param);
        return param.getId();
    }

    public void updateOldParentIsLeaf(Integer id) {
        if (id != null && id != 0) {
            int i = departmentDao.countDepartment(id);
            if (i == 0) {
                departmentDao.updateDepartmentIsLeaf(id,(byte) 1);
            }
        }
    }

    public DepartmentOneParam getOneInfo(Integer departmentId){
        DepartmentOneParam departmentInfo = departmentDao.getOneInfo(departmentId);
        return departmentInfo;
    }

    public int delete(Integer departmentId){
        int id = departmentDao.deleteDepartment(departmentId);
        DepartmentOneParam departmentInfo = departmentDao.getOneInfo(departmentId);
        updateOldParentIsLeaf(departmentInfo.getParentId());
        return id;
    }

    /**
     * 更新父节点isLeaf并返回
     * @param param
     * @return
     */
    public void updateDepartmentLevel(DepartmentOneParam param){
        departmentDao.updateDepartmentLevel(param.getId(),param.getLevel());
        List<DepartmentOneParam> departments = departmentDao.getChildDepartment(param.getId());
        for(DepartmentOneParam department: departments) {
            department.setLevel(param.getLevel()+1);
            updateDepartmentLevel(department);
        }
    }

    /**
     * 更新父节点isLeaf并返回
     * @param param
     * @return
     */
    public int updateParentIsLeaf(DepartmentOneParam param){
        int level;
        if (param.getParentId() != null && param.getParentId() != 0) {
            departmentDao.updateDepartmentIsLeaf(param.getParentId(),(byte) 0);
            DepartmentOneParam parentDepartment = departmentDao.getOneInfo(param.getParentId());
            level = parentDepartment.getLevel()+1;
        } else {
            level = DepartmentConstant.ROOT_LEVEL;
        }
        return level;
    }

    /**
     *
     * @return
     */
    public List<DepartmentOneParam> getListByIds(List<Integer> departmentIds) {
        return departmentDao.getListByIds(departmentIds);
    }

    public List<DepartmentListVo> listDepartmentTree() {
        List<DepartmentListVo> listDepartmentTree = departmentDao.listDepartmentByParentId(0);
        for (DepartmentListVo list : listDepartmentTree) {
            if (DepartmentConstant.LEAF.equals(list.getIsLeaf())) {
                list.setChildDepartmentList(listDepartmentListVo(list));
            } else {
                List<DepartmentListVo> departmentList = departmentDao.listDepartmentByParentId(list.getId());
                list.setChildDepartmentList(departmentList);
            }
        }
        return listDepartmentTree;
    }

    public List<DepartmentListVo> listDepartmentListVo(DepartmentListVo department){
        List<DepartmentListVo> listTemp = new ArrayList<>();
        DepartmentListVo listSingle = new DepartmentListVo();
        listSingle.setId(department.getId());
        listSingle.setCode(department.getCode());
        listSingle.setName(department.getName());
        listSingle.setIsLeaf(department.getIsLeaf());
        listSingle.setLevel(department.getLevel());
        listSingle.setParentId(department.getParentId());
        listTemp.add(listSingle);
        return listTemp;
    }

    public DepartmentOneParam getDepartmentByCode(String code) {
        return departmentDao.getDepartmentByCode(code);
    }

    /**
     * 更新/新增科室
     * @param department
     */
    public void synchroDepartment(DepartmentOneParam department) {
        DepartmentOneParam oldDepartment = getDepartmentByCode(department.getCode());
        if(oldDepartment == null) {
            insertDepartment(department);
        } else {
            department.setId(oldDepartment.getId());
            department.setIsLeaf(oldDepartment.getIsLeaf());
            updateDepartment(department,oldDepartment.getParentId());
        }
    }

    public void fetchDepartments(String json) {
        List<DepartmentFetchOneParam> departmentFetchOneParams = Util.parseJson(json, new TypeReference<List<DepartmentFetchOneParam>>() {
        });
        for (DepartmentFetchOneParam list : departmentFetchOneParams) {
            DepartmentOneParam department = new DepartmentOneParam();
            department.setName(list.getDepartName());
            department.setCode(list.getDepartCode());
            if(DepartmentConstant.PID.equals(list.getpCode())) {
                department.setParentId(0);
            } else {
                department.setParentId(getDepartmentIdNew(list.getpCode()));
            }
            if (list.getState() > 1) {
                department.setIsDelete((byte) 1);
            }
            synchroDepartment(department);
        }
    }

    public Integer getDepartmentIdNew(String code) {
        DepartmentOneParam departmentInfo = getDepartmentByCode(code);
        if(departmentInfo == null) {
            DepartmentOneParam departmentTemp = new DepartmentOneParam();
            departmentTemp.setCode(code);
            insertDepartment(departmentTemp);
            return departmentTemp.getId();
        } else {
            return departmentInfo.getId();
        }
    }

    /**
     * 拉取科室列表
     * @return
     */
    public JsonResult fetchExternalDepartments(){
        String appId = ApiExternalRequestConstant.APP_ID_HIS;
        Integer shopId = getShopId();
        String serviceName = ApiExternalRequestConstant.SERVICE_NAME_FETCH_DEPARTMENT_INFOS;

        Long lastRequestTime = saas().externalRequestHistoryService.getLastRequestTime(ApiExternalRequestConstant.APP_ID_HIS, shopId, ApiExternalRequestConstant.SERVICE_NAME_FETCH_DEPARTMENT_INFOS);
        DepartmentExternalRequestParam param =new DepartmentExternalRequestParam();
        param.setStartTime(null);

        ApiExternalRequestResult apiExternalRequestResult = saas().apiExternalRequestService.externalRequestGate(appId, shopId, serviceName, Util.toJson(param));

        // 数据拉取错误
        if (!ApiExternalRequestConstant.ERROR_CODE_SUCCESS.equals(apiExternalRequestResult.getError())) {
            JsonResult result = new JsonResult();
            result.setError(apiExternalRequestResult.getError());
            result.setMessage(apiExternalRequestResult.getMsg());
            result.setContent(apiExternalRequestResult.getData());
            return result;
        }

        fetchDepartments(apiExternalRequestResult.getData());

        return JsonResult.success();
    }

    /**
     * 根据名字查找
     * @param name
     * @return
     */
    public List<DepartmentOneParam> listDepartmentsByName(String name) {
        return departmentDao.listDepartmentsByName(name);
    }

    /**
     * 根据id集合查询科室信息
     * @param departmentIds 科室id集合
     * @return 信息集合
     */
    public List<DepartmentSimpleVo> listDepartmentInfo(List<Integer> departmentIds){
        return departmentDao.listDepartmentInfo(departmentIds);
    }

    /**
     * 获取推荐科室list
     * @return
     */
    public List<DepartmentOneParam> listRecommendDepartment() {
//        List<DepartmentIdNameVo> departmentList = Util.parseJson(get("recommendDepartments"), new TypeReference<List<DepartmentIdNameVo>>() {
//        });
        DepartmentListParam departmentListParam = new DepartmentListParam();
        departmentListParam.setLimitNum(7);
        return listDepartmentsByOptions(departmentListParam);
    }

    /**
     * 小程序医师下拉科室列表
     * @return
     */
    public List<DepartmentOneParam> listDepartmentsSelect() {
        DepartmentListParam departmentListParam = new DepartmentListParam();
        List<DepartmentOneParam> departmentList = listDepartmentsByOptions(departmentListParam);
        DepartmentOneParam allItem = new DepartmentOneParam();
        allItem.setId(0);
        allItem.setName("全部科室");
        departmentList.add(0,allItem);
        return departmentList;
    }

    /**
     * 根据条件查找
     * @param param
     * @return
     */
    public List<DepartmentOneParam> listDepartmentsByOptions(DepartmentListParam param) {
        if (param.getDoctorId() != null && param.getDoctorId() > 0) {
            List<Integer> departmentIds = doctorDepartmentCoupleDao.getDepartmentIdsByDoctorId(param.getDoctorId());
            param.setDepartmentIds(departmentIds);
        }
        param.setDepartmentRecommendType(shopCommonConfigService.getDepartmentRecommendType());
        param.setConsultationRate(shopCommonConfigService.getDepartmentRecommendConsultationRate());
        param.setInquiryRate(shopCommonConfigService.getDepartmentRecommendInquiryRate());
        param.setDoctorRate(shopCommonConfigService.getDepartmentRecommendDoctorRate());
        return departmentDao.listDepartmentsByOptions(param);
    }

    /**
     * 获取所有科室的信息
     *
     * @return
     */
    public List<DepartmentOneParam> getAllDepartment() {
        return departmentDao.getAllDepartment();
    }

    /**
     * 获取科室处方统计数据
     * @param param
     * @return
     */
    public DepartmentStatisticOneParam getDepartmentInquiryData(DepartmentStatisticParam param) {
        return departmentDao.getDepartmentInquiryData(param);
    }

    /**
     * 获取科室接诊统计数据
     * @param param
     * @return
     */
    public Integer getDepartmentConsultationData(DepartmentStatisticParam param) {
        return departmentDao.getDepartmentConsultationData(param);
    }

    /**
     * 获取科室处方统计数据
     * @param param
     * @return
     */
    public DepartmentStatisticOneParam getDepartmentPrescriptionData(DepartmentStatisticParam param) {
        return departmentDao.getDepartmentPrescriptionData(param);
    }

    /**
     * 获取科室统计信息
     * @param param
     * @return
     */
    public DepartmentSummaryTrendDo getDepartmentStatisData(DepartmentStatisticParam param){
        DepartmentSummaryTrendDo data = new DepartmentSummaryTrendDo();
        data.setConsultationNumber(getDepartmentConsultationData(param));

        DepartmentStatisticOneParam inquiryData = getDepartmentInquiryData(param);
        data.setInquiryMoney(inquiryData.getInquiryMoney());
        data.setInquiryNumber(inquiryData.getInquiryNumber());

        DepartmentStatisticOneParam prescriptionData = getDepartmentPrescriptionData(param);
        data.setPrescriptionMoney(prescriptionData.getPrescriptionMoney());
        data.setPrescriptionNum(prescriptionData.getPrescriptionNum());
        return data;
    }

    /**
     * 查询医师所属科室名称
     * @param doctorId 医师id
     * @return List<String>
     */
    public List<String> getDepartmentNameByDoctor(Integer doctorId) {
        return departmentDao.getDepartmentNameByDoctor(doctorId);
    }

    public void departmentStatistics() {
        List<ShopListInfoVo> result = saas.shopService.getShopListInfo();
        result.forEach((r) -> {
            ShopApplication shop = saas.getShopApp(r.getShopId());
            List<DepartmentOneParam> allStore = shop.departmentService.getAllDepartment();
            allStore.forEach((d)->{
                shop.departmentTaskService.insertDepartmentStatistic(d.getId());
            });
            LocalDateTime today = LocalDate.now().atStartOfDay();
            Date refDate = Date.valueOf(today.minusDays(1).toLocalDate());
            DoctorStatisticAllMinMaxVo doctorStatisticAllMinMaxVo = new DoctorStatisticAllMinMaxVo();
            doctorStatisticAllMinMaxVo.setOneMinMax(departmentStatisticService.getMinMaxStatisticData(refDate,(byte) 1));
            doctorStatisticAllMinMaxVo.setWeekMinMax(departmentStatisticService.getMinMaxStatisticData(refDate,(byte) 7));
            doctorStatisticAllMinMaxVo.setMonthMinMax(departmentStatisticService.getMinMaxStatisticData(refDate,(byte) 30));
            doctorStatisticAllMinMaxVo.setSeasonMinMax(departmentStatisticService.getMinMaxStatisticData(refDate,(byte) 90));
            TYPE_LIST_1.forEach((t)->{
                DoctorStatisticMinMaxVo doctorStatisticMinMax = shop.doctorTaskService.getMinMaxByType(doctorStatisticAllMinMaxVo,t);
                shop.departmentTaskService.updateDepartmentStatisticScore(t,refDate,doctorStatisticMinMax);
            });
        });
    }
}
