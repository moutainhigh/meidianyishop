package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.shop.title.TitleListParam;
import com.meidianyi.shop.service.pojo.shop.title.TitleOneParam;
import com.meidianyi.shop.service.shop.ShopApplication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author chenjie
 */
@RestController
public class AdminTitleController extends AdminBaseController {
//    @Override
//    protected ShopApplication shop() {
//        return saas.getShopApp(471752);
//    }
    /**
     * 职称列表
     * @param param
     * @return
     */
    @PostMapping("/api/admin/doctor/title/list")
    public JsonResult titleList(@RequestBody TitleListParam param) {
        PageResult<TitleOneParam> titleList = shop().titleService.getTitleList(param);
        return this.success(titleList);
    }

    /**
     * 职称选择框下拉列表
     * @return param
     */
    @GetMapping("/api/admin/doctor/title/select/list")
    public JsonResult getSelectList(){
        return success(shop().titleService.listTitles());
    }

    /**
     *  职称新增/修改
     * @param param {@link TitleOneParam}
     */
    @PostMapping("/api/admin/doctor/title/add")
    public JsonResult insert(@RequestBody TitleOneParam param) {
        if (param.getName()==null) {
            return fail(JsonResultCode.DOCTOR_TITLE_NAME_IS_NULL);
        }

        boolean isExist = shop().titleService.isNameExist(param.getId(),param.getName());
        if (isExist) {
            return fail(JsonResultCode.DOCTOR_TITLE_NAME_EXIST);
        }
        if (param.getId()==null) {
            shop().titleService.insertTitle(param);
        }else{
            shop().titleService.updateTitle(param);
        }

        return success();
    }

//    /**
//     * 普通分类修改
//     * @param param {@link DepartmentOneParam}
//     */
//    @PostMapping("/api/admin/doctor/department/update")
//    public JsonResult update(@RequestBody DepartmentOneParam param,Integer oldParentId) {
//        if (param.getId() == null) {
//            return fail(JsonResultCode.DOCTOR_DEPARTMENT_ID_IS_NULL);
//        }
//        boolean isExist = shop().departmentService.isNameExist(param.getId(),param.getName());
//        if (isExist) {
//            return fail(JsonResultCode.DOCTOR_DEPARTMENT_NAME_EXIST);
//        }
//        shop().departmentService.updateDepartment(param,oldParentId);
//        return success();
//    }

    /**
     * 根据id获取职称
     * @param titleId 普通商家分类id
     */
    @GetMapping("/api/admin/doctor/title/info/{titleId}")
    public JsonResult getTitle(@PathVariable Integer titleId) {
        if (titleId == null) {
            return fail(JsonResultCode.DOCTOR_TITLE_ID_IS_NULL);
        }
        return success(shop().titleService.getOneInfo(titleId));
    }

    /**
     * 删除职称
     * @param titleId 职称id
     */
    @GetMapping("/api/admin/doctor/title/delete/{titleId}")
    public JsonResult delete(@PathVariable Integer titleId) {
        if (titleId == null) {
            return fail(JsonResultCode.DOCTOR_DEPARTMENT_ID_IS_NULL);
        }
        shop().titleService.deleteTitle(titleId);
        return success();
    }


    /**
     *  职称拉取
     */
    @PostMapping("/api/admin/doctor/title/fetch")
    public JsonResult fetchDoctorTitles() {
        return shop().titleService.fetchExternalTitles();
    }
}
