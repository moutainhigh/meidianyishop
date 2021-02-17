package com.meidianyi.shop.controller.admin;

import org.springframework.web.bind.annotation.*;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.service.pojo.shop.department.DepartmentListParam;
import com.meidianyi.shop.service.pojo.shop.department.DepartmentListVo;
import com.meidianyi.shop.service.pojo.shop.department.DepartmentOneParam;

import java.util.*;
import com.meidianyi.shop.service.shop.ShopApplication;

/**
 * @author chenjie
 */
@RestController
public class AdminDepartmentController extends AdminBaseController{
//    @Override
//    protected ShopApplication shop() {
//        return saas.getShopApp(471752);
//    }
    /**
     * 科室列表
     * @param param
     * @return
     */
    @PostMapping("/api/admin/doctor/department/list")
    public JsonResult departmentList(@RequestBody DepartmentListParam param) {
        PageResult<DepartmentListVo> departmentList = shop().departmentService.getDepartmentList(param);
        return this.success(departmentList);
    }

    /**
     * 科室列表查询子科室
     * @param id
     * @return
     */
    @GetMapping("/api/admin/doctor/department/child/list/{id}")
    public JsonResult departmentListByParentId(@PathVariable Integer id) {
        List<DepartmentListVo> departmentList = shop().departmentService.listDepartmentsByParentId(id);
        return this.success(departmentList);
    }

//    /**
//     * 科室选择框下拉列表（非树形数据，仅返回一级数据）
//     * @return param {@link DepartmentListVo}
//     */
//    @GetMapping("/api/admin/doctor/department/select/list")
//    public JsonResult getSelectList(){
//        return success(shop().departmentService.listDepartmentsByParentId(0));
//    }

    /**
     *  科室新增
     * @param param {@link DepartmentOneParam}
     */
    @PostMapping("/api/admin/doctor/department/add")
    public JsonResult insert(@RequestBody DepartmentOneParam param) {
        if (param.getName()==null) {
            return fail(JsonResultCode.DOCTOR_DEPARTMENT_NAME_IS_NULL);
        }

        boolean isExist = shop().departmentService.isNameExist(null,param.getName());
        if (isExist) {
            return fail(JsonResultCode.DOCTOR_DEPARTMENT_NAME_EXIST);
        }

        shop().departmentService.insertDepartment(param);

        return success();
    }

    /**
     * 普通分类修改
     * @param param {@link DepartmentOneParam}
     */
    @PostMapping("/api/admin/doctor/department/update")
    public JsonResult update(@RequestBody DepartmentOneParam param,Integer oldParentId) {
        if (param.getId() == null) {
            return fail(JsonResultCode.DOCTOR_DEPARTMENT_ID_IS_NULL);
        }
        boolean isExist = shop().departmentService.isNameExist(param.getId(),param.getName());
        if (isExist) {
            return fail(JsonResultCode.DOCTOR_DEPARTMENT_NAME_EXIST);
        }
        shop().departmentService.updateDepartment(param,oldParentId);
        return success();
    }

    /**
     * 根据id获取普通商家分类
     * @param departmentId 普通商家分类id
     */
    @GetMapping("/api/admin/doctor/department/info/{departmentId}")
    public JsonResult getDepartment(@PathVariable Integer departmentId) {
        if (departmentId == null) {
            return fail(JsonResultCode.DOCTOR_DEPARTMENT_ID_IS_NULL);
        }
        return success(shop().departmentService.getOneInfo(departmentId));
    }

    /**
     * 删除商家分类
     * @param departmentId 分类id
     */
    @GetMapping("/api/admin/doctor/department/delete/{departmentId}")
    public JsonResult delete(@PathVariable Integer departmentId) {
        if (departmentId == null) {
            return fail(JsonResultCode.DOCTOR_DEPARTMENT_ID_IS_NULL);
        }
        shop().departmentService.delete(departmentId);
        return success();
    }

//    /**
//     * 判断字符串集合内是否存在重复数据
//     * @param sortNames 字符串集合
//     * @return true 存在， false 不存在
//     */
//    private boolean isSortNamesRepeat(List<String> sortNames){
//        Set<String> set = new HashSet<>(sortNames);
//        return set.size()!=sortNames.size();
//    }

    /**
     * 科室树状List
     */
    @GetMapping("/api/admin/doctor/department/tree/list")
    public JsonResult getDepartmentTreeList() {
        return success(shop().departmentService.listDepartmentTree());
    }

    /**
     *  科室拉取
     */
    @PostMapping("/api/admin/doctor/department/fetch")
    public JsonResult fetchDoctorDepartments() {
        return shop().departmentService.fetchExternalDepartments();
    }

    /**
     * 科室下拉List
     */
    @GetMapping("/api/admin/doctor/department/select/list")
    public JsonResult getDepartmentSelectList() {
        return success(shop().departmentService.listDepartmentsByName(null));
    }

}
