package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.saas.shop.version.VersionName;
import com.meidianyi.shop.service.pojo.shop.store.technician.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author 黄荣刚
 * @date 2019年7月15日
 *
 */
@RestController
@RequestMapping("/api/admin/store")
public class AdminServiceTechnicianController extends AdminBaseController {
	/**
	 * 分页查询售后信息，支持售后姓名、售后分组、售后电话模糊查询
	 * @param param
	 * @return
	 */
	@PostMapping("/services/technician/list")
	public JsonResult getList(@RequestBody @Valid ServiceTechnicianPageListParam param) {
        if(checkPurview()){
            PageResult<ServiceTechnicianPojo> list = shop().store.serviceTechnician.getPageList(param);
            return success(list);
        }else{
            return fail(JsonResultCode.CODE_ACCOUNT_VERSIN_NO_POWER);
        }
	}
	/**
	 * 根据ID查一条售后信息
	 * @param id
	 * @return
	 */
	@GetMapping("/services/technician/select/{id}")
	public JsonResult select(@PathVariable Integer id) {
        if(checkPurview()){
            ServiceTechnicianPojo pojo = shop().store.serviceTechnician.select(id);
            if(pojo == null) {
                return fail(JsonResultCode.CODE_FAIL);
            }else {
                return success(pojo);
            }
        }else{
            return fail(JsonResultCode.CODE_ACCOUNT_VERSIN_NO_POWER);
        }
	}
	/**
	 * 添加售后
	 * @param param
	 * @return
	 */
	@PostMapping("/services/technician/add")
	public JsonResult insert(@RequestBody @Valid ServiceTechnicianParam param) {
        if(checkPurview()){
            int result = shop().store.serviceTechnician.insert(param);
            if(result == 0) {
                return fail(JsonResultCode.CODE_FAIL);
            }
            return success(JsonResultCode.CODE_SUCCESS);
        }else{
            return fail(JsonResultCode.CODE_ACCOUNT_VERSIN_NO_POWER);
        }
	}

	/**
	 * 修改售后
	 * @param param
	 * @return
	 */
	@PostMapping("/services/technician/update")
	public JsonResult update(@RequestBody @Valid ServiceTechnicianParam param) {
        if(checkPurview()){
            if(param.getId()== null) {
                return fail(JsonResultCode.STORE_STORE_ID_NULL,JsonResultMessage.STORE_STORE_ID_NULL);
            }
            int result = shop().store.serviceTechnician.update(param);
            if(result == 0) {
                return fail(JsonResultCode.CODE_FAIL);
            }
            return success(JsonResultCode.CODE_SUCCESS);
        }else{
            return fail(JsonResultCode.CODE_ACCOUNT_VERSIN_NO_POWER);
        }
	}

	/**
	 * 删除售后
	 * @param id
	 * @return
	 */
	@PostMapping("/services/technician/delete/{id}")
	public JsonResult delete(@PathVariable @NotNull Integer id) {
        if(checkPurview()){
            int result = shop().store.serviceTechnician.delete(id);
            if(result>0) {
                return success(JsonResultCode.CODE_SUCCESS);
            }
            return fail(JsonResultCode.CODE_FAIL);
        }else{
            return fail(JsonResultCode.CODE_ACCOUNT_VERSIN_NO_POWER);
        }
	}

    /*
     *
	   * 以下为售后分组功能接口
     *
     *
	 */

    /**
	 * 分页查询售后分组列表
	 * @param param 包含门店ID、分页信息
     * @return
	 */
	@PostMapping("/services/technician/group/list")
	public JsonResult getTechnicianGroupList(@RequestBody @Valid TechnicianGroupPageListParam param) {
        if(checkPurview()){
            PageResult<ServiceTechnicianGroup> result = shop().store.serviceTechnician.groupService.getPageList(param);
            return success(result);
        }else{
            return fail(JsonResultCode.CODE_ACCOUNT_VERSIN_NO_POWER);
        }
	}

    /**
     * 技师分组下拉接口
     */
    @GetMapping("/services/technician/group/all/{storeId}")
    public JsonResult getTechnicianGroupAllList(@PathVariable Integer storeId) {
        if(checkPurview()){
            return success(shop().store.serviceTechnician.groupService.getGroupAllList(storeId));
        }else{
            return fail(JsonResultCode.CODE_ACCOUNT_VERSIN_NO_POWER);
        }
    }

    @PostMapping("/services/technician/group/add")
	public JsonResult addTechnicianGroup(@RequestBody @Valid ServiceTechnicianGroupParam param) {
        if(checkPurview()){
            int result = shop().store.serviceTechnician.groupService.insert(param);
            if(result>0) {
                return success(JsonResultCode.CODE_SUCCESS);
            }
            return fail(JsonResultCode.CODE_FAIL);
        }else{
            return fail(JsonResultCode.CODE_ACCOUNT_VERSIN_NO_POWER);
        }
	}

    @PostMapping("/services/technician/group/delete/{groupId}")
	public JsonResult deleteTechnicianGroup(@PathVariable Integer  groupId) {
        if(checkPurview()){
            shop().store.serviceTechnician.groupService.delete(groupId);
            return success();
        }else{
            return fail(JsonResultCode.CODE_ACCOUNT_VERSIN_NO_POWER);
        }
	}

    @PostMapping("/services/technician/group/update")
	public JsonResult updateTechnicianGroup(@RequestBody @Valid ServiceTechnicianGroupParam param) {
        if(checkPurview()){
            if(param.getGroupId() == null) {
                return fail(JsonResultCode.CODE_FAIL);
            }
            int result = shop().store.serviceTechnician.groupService.update(param);
            if(result>0) {
                return success(JsonResultCode.CODE_SUCCESS);
            }
            return fail(JsonResultCode.CODE_FAIL);
        }else{
            return fail(JsonResultCode.CODE_ACCOUNT_VERSIN_NO_POWER);
        }

	}

    /**
     * Gets tech by store service.根据门店，服务查询可用技师列表
     *
     * @param param the param
     * @return the tech by store service
     */
    @PostMapping("/services/technician/getTechByStoreService")
    public JsonResult getTechByStoreService(@RequestBody @Validated TechnicianParam param) {
        if(checkPurview()){
            return success(shop().store.serviceTechnician.getTechByStoreService(param));
        }else{
            return fail(JsonResultCode.CODE_ACCOUNT_VERSIN_NO_POWER);
        }
    }

    /**
     * 校验权限
     * @return
     */
    private boolean checkPurview(){
        String[] verifies = saas.shop.version.verifyVerPurview(shopId(), VersionName.SUB_5_SERVICE);
        String strTrue = "true";
        if(strTrue.equals(verifies[0])){
            return true;
        }
        return false;
    }
}
