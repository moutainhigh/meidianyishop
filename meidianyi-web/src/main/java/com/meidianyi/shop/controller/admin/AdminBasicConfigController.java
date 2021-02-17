package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.main.tables.records.ShopAccountRecord;
import com.meidianyi.shop.db.main.tables.records.ShopChildAccountRecord;
import com.meidianyi.shop.db.main.tables.records.ShopChildRoleRecord;
import com.meidianyi.shop.db.main.tables.records.ShopRoleRecord;
import com.meidianyi.shop.service.pojo.shop.config.ShopBaseConfig;
import com.meidianyi.shop.service.pojo.shop.config.ShopCommonCfgInfo;
import com.meidianyi.shop.service.pojo.shop.config.ShopMsgTempConfig;
import com.meidianyi.shop.service.pojo.shop.config.ShopMsgTempJsonConfig;
import com.meidianyi.shop.service.pojo.shop.config.group.*;
import com.meidianyi.shop.service.pojo.shop.config.message.MessageConfigParam;
import com.meidianyi.shop.service.pojo.shop.config.pledge.PledgeInfo;
import com.meidianyi.shop.service.pojo.shop.config.pledge.PledgeParam;
import com.meidianyi.shop.service.pojo.shop.config.pledge.PledgeStateUpdateParam;
import com.meidianyi.shop.service.pojo.shop.config.pledge.PledgeVo;
import com.meidianyi.shop.service.pojo.shop.config.pledge.group.PledgeStateUpdateGroup;
import com.meidianyi.shop.service.pojo.shop.config.pledge.group.UpdateGroup;
import com.meidianyi.shop.service.pojo.shop.department.DepartmentRecommendCfgParam;
import com.meidianyi.shop.service.pojo.shop.operation.RecordAdminActionInfo;
import com.meidianyi.shop.service.pojo.shop.operation.RecordAdminActionParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 商家--基础配置
 * @author: 卢光耀
 * @date: 2019-07-09 15:05
 *
*/
@RestController
@RequestMapping(value = "/api/admin/config")
public class AdminBasicConfigController extends AdminBaseController{
	private static int TWENTYFIVE=25;

	@Value(value = "${official.appId}")
	private String bindAppId;

    /**
     * 服务承诺--列表
     * @return JsonResult
     */
    @GetMapping(value = "/pledge/list")
    public JsonResult getPledgeList(){
        PledgeVo vo = new PledgeVo();
        List<PledgeInfo> result = shop().shopBasicConfig.shopPledge.getPledgeList();
        String value = shop().config.pledgeCfg.getPledgeConfig();
        vo.setList(result);
        vo.setState(Byte.valueOf(value));
        return success(vo);
    }

    /**
     * 服务承诺--新增
     * @param param
     * @return JsonResult
     */
    @PostMapping(value = "/pledge/add")
    public JsonResult insertPledge(@RequestBody @Validated PledgeParam param){

        boolean canInsert = shop().shopBasicConfig.shopPledge.judgeInsertParam();
        if ( canInsert ){
            shop().shopBasicConfig.shopPledge.insertPledge(param);
            return success();
        }else{
            return fail(JsonResultCode.CODE_CONFIG_PLEDGE_EXCEED);
        }
    }

    /**
     * 模版配置-更改
     * @param param
     */
    @PostMapping(value = "/message/template/update")
    public JsonResult updateMessageConfig(@RequestBody MessageConfigParam param){
        shop().config.messageConfigService.updateMessageConfig(param);
        return success();
    }
    /**
     * 模版配置-查询
     * @return
     */
    @GetMapping(value = "/message/template/query")
    public JsonResult queryMessageConfig(){
        return success(shop().config.messageConfigService.getAllMessageConfig());
    }
    /**
     * 服务承诺--修改
     * @param param
     * @return JsonResult
     */
    @PostMapping(value = "/pledge/updateInfo")
    public JsonResult updatePledge(@RequestBody @Validated({UpdateGroup.class}) PledgeParam param){
        shop().shopBasicConfig.shopPledge.updatePledge(param);
        return success();
    }
    /**
     * 服务承诺--开启/关闭服务(单个)
     * @param param
     * @return JsonResult
     */
    @PostMapping(value = "/pledge/updateState")
    public JsonResult updatePledgeState(
            @RequestBody @Validated({PledgeStateUpdateGroup.class}) PledgeStateUpdateParam param){
        shop().shopBasicConfig.shopPledge.updatePledgeState(param);
        return success();
    }
    /**
     * 服务承诺--删除(逻辑删除)
     * @param param
     * @return JsonResult
     */
    @PostMapping(value = "/pledge/delete")
    public JsonResult deletePledge(
            @RequestBody @Validated PledgeStateUpdateParam param){
        shop().shopBasicConfig.shopPledge.deletePledgeState(param);
        return success();
    }
    /**
     * 服务承诺--开启/关闭服务(总)
     * @return JsonResult
     */
    @GetMapping(value = "/pledge/updateTotalSwitch")
    public JsonResult closePledge(String state){
        shop().config.pledgeCfg.setPledgeConfig(state);
        return success();
    }
    @PostMapping(value = "/record/getPage")
    public JsonResult getRecordAccountActionPage(@RequestBody RecordAdminActionParam param, HttpServletRequest request){
		String language = StringUtils.isEmpty(request.getHeader("V-Lang"))?"":request.getHeader("V-Lang");
        PageResult<RecordAdminActionInfo> storeGroupPageResult = shop().record.getRecordPage(param,language);
        return success(storeGroupPageResult);
	}



	/**
	 * 基础配置 / 权限组管理/ 添加权限组
	 *
	 * @return
	 */
	@PostMapping(value = "/role/group/add")
	public JsonResult addRole(@RequestBody ShopRoleParam param) {
		if ((!isAllEmpty(param.getPrivilegePass()))&&(!StringUtils.isEmpty(param.getPrivilegePass().get(1)))) {
			if (StringUtils.isEmpty(param.getLoginPass()) || StringUtils.isEmpty(param.getRolePass())) {
				// 请输入密码
				return fail(JsonResultCode.CODE_MSG_ACCOUNT_PASSWD_NOT_NULL);
			}
			// 验证登陆密码
			ShopAccountRecord shopRecord = saas.shop.account.verify(adminAuth.user().getUserName(),
					param.getLoginPass());
			if (shopRecord == null) {
				// 管理员登陆密码错误
				return fail(JsonResultCode.CODE_ACCOUNT_PASSWD_ERROR);
			}
		}
		int num = saas.shop.role.insertRole(param, adminAuth.user());
		if (num != 1) {
			return fail(JsonResultCode.CODE_FAIL);
		}
		return success(JsonResultCode.CODE_SUCCESS);
	}

	/**
	 * 基础配置 / 权限组管理/ 添加权限组 ->查询
	 *
	 * @param param
	 * @return
	 */
	@PostMapping(value = "/role/group/list")
	public JsonResult listRole(@RequestBody ShopRoleAddListParam param) {
		return success(saas.shop.role.getInfo(adminAuth.user().getSysId(),adminAuth.user().loginShopId,param));
	}

	/**
	 * 基础配置 / 权限组管理/ 添加权限组 ->删除
	 *
	 * @param param
	 * @return
	 */
	@PostMapping(value = "/role/group/del")
	public JsonResult delRole(@RequestBody ShopRoleDelParam param) {
		ShopRoleRecord sRecord = saas.shop.role.getRoleByIdAndSysId(param.getRoleId(), adminAuth.user().getSysId());
		if (sRecord == null) {
			// 您没有权限操作此角色
			return fail(JsonResultCode.CODE_ACCOUNT_SHOP_ROLE_INSUFFICIENT);
		}
		// 是否有子账户在用这个角色
		ShopChildRoleRecord sChildRecord = saas.shop.subAccount.checkByRecode(param.getRoleId(), adminAuth.user());
		if (sChildRecord != null) {
			// 有子账户正在使用此角色，请修改后再删除
			return fail(JsonResultCode.CODE_ACCOUNT_SHOP_ROLE_OCCUPY);
		}
		int delNum = saas.shop.role.deleteById(param.getRoleId(), adminAuth.user().getSysId(),
				adminAuth.user().getLoginShopId());
		if (delNum != 1) {
			// 删除失败
			return fail(JsonResultCode.CODE_FAIL);
		}
		return success(JsonResultCode.CODE_SUCCESS);
	}


	/**
	 * 基础配置 / 权限组管理/ 添加权限组 ->编辑，先返回角色对应的权限
	 * @param param
	 * @return
	 */
	@PostMapping(value = "/role/group/editView")
	public JsonResult editRole(@RequestBody ShopRoleDelParam param) {
		ShopRoleRecord sRecord = saas.shop.role.getRoleByIdAndSysId(param.getRoleId(), adminAuth.user().getSysId());
		if (sRecord == null) {
			// 您没有权限操作此角色
			return fail(JsonResultCode.CODE_ACCOUNT_SHOP_ROLE_INSUFFICIENT);
		}
		ShopRoleParam vo = new ShopRoleParam();
		vo.setRoleName(sRecord.getRoleName());
		vo.setPrivilegeList(Util.parseJson(sRecord.getPrivilegeList(), List.class));
		vo.setPrivilegePass(Util.parseJson(sRecord.getPrivilegePass(), List.class));
		return success(vo);
	}

	/**
	 * 基础配置 / 权限组管理/ 添加权限组 ->编辑，编辑后提交
	 *
	 * @param upParam
	 * @return
	 */
	@PostMapping(value = "/role/group/editUpdate")
	public JsonResult updateRole(@RequestBody ShopRoleGroupUpdateParam upParam) {
		if ((!isAllEmpty(upParam.getPrivilegePass()))&&(!StringUtils.isEmpty(upParam.getPrivilegePass().get(1)))) {
			if (StringUtils.isEmpty(upParam.getLoginPass()) || StringUtils.isEmpty(upParam.getRolePass())) {
				// 请输入密码
				return fail(JsonResultCode.CODE_MSG_ACCOUNT_PASSWD_NOT_NULL);
			}
			// 验证登陆密码
			ShopAccountRecord shopRecord = saas.shop.account.verify(adminAuth.user().getUserName(),
					upParam.getLoginPass());
			if (shopRecord == null) {
				// 管理员登陆密码错误
				return fail(JsonResultCode.CODE_ACCOUNT_PASSWD_ERROR);
			}
		}
		ShopRoleRecord sRecord = saas.shop.role.getRoleByIdAndSysId(upParam.getRoleId(), adminAuth.user().getSysId());
		if (sRecord == null) {
			// 您没有权限操作此角色
			return fail(JsonResultCode.CODE_ACCOUNT_SHOP_ROLE_INSUFFICIENT);
		}
		int updateNum = saas.shop.role.updateRole(upParam, adminAuth.user());
		if (updateNum != 1) {
			// 更新失败
			return fail(JsonResultCode.CODE_FAIL);
		}
		return success(JsonResultCode.CODE_SUCCESS);
	}

	/**
	 * 店铺基础配置-取店铺通用设置信息
	 * @return
	 */
	@GetMapping(value = "/shop/common/get")
	public JsonResult getShopCommonInfo()   {
		return success(shop().config.shopCommonConfigService.getShopCommonCfg());
	}

	/**
	 * 店铺基础配置-取店铺基础信息
	 * @return
	 */
	@GetMapping(value = "/shop/base/get")
	public JsonResult getShopBaseInfo() {
		return this.success(saas.shop.getShopBaseInfoById(this.shopId()));
	}

	/**
	 * 店铺基础配置-店铺基础信息更新
	 * @return
	 */
	@PostMapping(value = "/shop/base/update")
	public JsonResult updateShopBaseInfo(@RequestBody @Valid ShopBaseConfig shop) {
		saas.shop.updateShopBaseInfo(shop,shopId());
		return this.success();
	}

	/**
	 * 店铺基础配置-店铺通用配置更新
	 * @return
	 */
	@PostMapping(value = "/shop/common/update")
	public JsonResult updateShopCommonInfo(@RequestBody @Valid ShopCommonCfgInfo commonCfg) {
		if(shop().config.shopCommonConfigService.updateShopCommonInfo(commonCfg)) {
			return this.success();
		}else {
			return this.fail(JsonResultCode.CODE_FAIL);
		}
	}
	@GetMapping("/shop/common/open/goods/weight/cfg")
	public JsonResult openGoodsWeightConfig(){
	    return success(shop().config.shopCommonConfigService.setGoodsWeightCfg((byte) 1));
    }

	/**
	 * 店铺子账户管理-查询
	 *
	 * @return
	 */
	@RequestMapping(value = "/role/query")
	public JsonResult queryaddRoleToMobile(@RequestBody ShopRoleAddListParam sAddListParam) {
		if (StringUtils.isEmpty(adminAuth.user().getLoginShopId())) {
			// 请先选择店铺
			return fail();
		}
		// 子账户手机号
		List<ShopChildAccountVo> mobileList = saas.shop.subAccount.getInfoBySysId(adminAuth.user().getSysId());
		// 权限组
		List<ShopRoleVo> groupRoleList = saas.shop.role.getInfo(adminAuth.user().getSysId(),adminAuth.user().loginShopId);
		// 下面显示的子账户对应的列表
		//List<ShopRoleAddListVo> totalList = saas.shop.subAccount.queryRoleAndAccount(adminAuth.user().getSysId());
		PageResult<ShopRoleAddListVo> accountRolePageList = saas.shop.subAccount.getAccountRolePageList(adminAuth.user().loginShopId, sAddListParam);
		PageResult<ShopRoleAddListVo> userList = saas.shop.mpOfficialAccountUserService.getUserList(accountRolePageList, bindAppId);
		ShopChildAccountListVo voList = new ShopChildAccountListVo();
		voList.setMobileList(mobileList);
		voList.setTotalList(userList);
		voList.setGroupRoleList(groupRoleList);
		return success(voList);
	}

	/**
	 * 店铺子账户管理-保存/编辑
	 *
	 * @param sAddParam
	 * @return
	 */
	@RequestMapping(value = "/role/add")
	public JsonResult addRoleToMobile(@RequestBody ShopRoleAddParam sAddParam) {

		ShopChildRoleRecord sChildRecord = saas.shop.subAccount.checkByRecodeAndAccId(sAddParam.getRoleId(),
				sAddParam.getAccountId(), adminAuth.user());
		if (sChildRecord != null) {
			// 账号已分配角色
			return fail(JsonResultCode.CODE_ACCOUNT_ASSIGNED_ROLE);
		}
		JsonResultCode code = checkPower(sAddParam.getRoleId(), sAddParam.getAccountId());
		if (!code.equals(JsonResultCode.CODE_SUCCESS)) {
			return fail(code);
		}
		int insetNum = saas.shop.subAccount.insertshopChildRole(sAddParam, adminAuth.user());
		if (insetNum != 1) {
			return fail(JsonResultCode.CODE_FAIL);
		}
		return success(JsonResultCode.CODE_SUCCESS);
	}

	@RequestMapping(value = "/role/edit")
	public JsonResult editRole(@RequestBody ShopRoleUpdateParam sUpdateParam) {
		JsonResultCode code = checkPower(sUpdateParam.getRoleId(), sUpdateParam.getAccountId());
		if (!code.equals(JsonResultCode.CODE_SUCCESS)) {
			return fail(code);
		}

		int upNum = saas.shop.subAccount.updateShopChildRole(sUpdateParam, adminAuth.user());
		if (upNum != 1) {
			return fail(JsonResultCode.CODE_FAIL);
		}
		return success(JsonResultCode.CODE_SUCCESS);
	}

	/**
	 * 店铺子账户管理-保存/删除
	 *
	 * @param sUpdateParam
	 * @return
	 */
	@RequestMapping(value = "/role/del")
	public JsonResult deleteRoleAccount(@RequestBody ShopRoleUpdateParam sUpdateParam) {
		JsonResultCode code = checkPower(sUpdateParam.getRoleId(), sUpdateParam.getAccountId());
		if (!code.equals(JsonResultCode.CODE_SUCCESS)) {
			return fail(code);
		}
		int delNum = saas.shop.subAccount.deleteShopChildRole(sUpdateParam);
		if (delNum != 1) {
			return fail(JsonResultCode.CODE_FAIL);
		}
		return success(JsonResultCode.CODE_SUCCESS);
	}

	/**
	 * TODO 公众号解绑
	 */

	/**
	 * 校验
	 *
	 * @param roleId
	 * @param accountId
	 * @return
	 */
	protected JsonResultCode checkPower(Integer roleId, Integer accountId) {
		if (StringUtils.isEmpty(adminAuth.user().getLoginShopId())) {
			// 请先选择店铺
			return JsonResultCode.CODE_FAIL;
		}

		ShopRoleRecord sRecord = saas.shop.role.getRoleByIdAndSysId(roleId, adminAuth.user().getSysId());
		if (sRecord == null) {
			// 您没有权限操作此角色
			return JsonResultCode.CODE_ACCOUNT_SHOP_ROLE_INSUFFICIENT;
		}
		ShopChildAccountRecord sChildAccRecord = saas.shop.subAccount.getSubAccountInfo(adminAuth.user().getSysId(),
				accountId);
		if (sChildAccRecord == null) {
			// 您没有权限操作此子账户
			return JsonResultCode.CODE_ACCOUNT_SHOP_ROLE_INSUFFICIENT;
		}
		return JsonResultCode.CODE_SUCCESS;
	}


	/**
	 * 模板消息查询
	 * @return
	 */
	@RequestMapping("/message/template/basic/query")
	public JsonResult queryTemplate() {
		ShopMsgTempConfig shopTempConfig = shop().config.shopMsgTemplateService.getShopTempConfig();
		if (shopTempConfig == null) {
			// 插入
			String json = ShopMsgTempJsonConfig.JSON;
			shopTempConfig = Util.parseJson(json, ShopMsgTempConfig.class);
			shop().config.shopMsgTemplateService.setShopTempConfig(json);
		}
		return success(shopTempConfig);
	}


	/**
	 * 模板消息更新
	 * @param sConfig
	 * @return
	 */
	@RequestMapping("/message/template/basic/update")
	public JsonResult updateTemplate(@RequestBody ShopMsgTempConfig sConfig) {
		if (sConfig.getA().length > TWENTYFIVE) {
			// 小程序消息不能大于25条
			return fail(JsonResultCode.CODE_CONFIG_A_NUM_GREATER);
		}
		if (sConfig.getB().length > TWENTYFIVE) {
			// 公众号消息不能大于25条
			return fail(JsonResultCode.CODE_CONFIG_B_NUM_GREATER);
		}

		int setNum = shop().config.shopMsgTemplateService.setShopTempConfig(sConfig);
		if (setNum != 1) {
			return fail();
		}
		return success(sConfig);
	}

	/**
	 * 获取所有默认权限集
	 * @return
	 */
	@RequestMapping("/role/group/getList")
	public JsonResult getAuthorityList() {
		return success(saas.shop.menu.getAuthority());

	}

	/**
	 * 数组元素判空
	 * @param list
	 * @return
	 */
	private Boolean isAllEmpty(List<?> list) {
		if (StringUtils.isEmpty(list)) {
			return true;
		} else {
			for (Object object : list) {
				if (!StringUtils.isEmpty(object)) {
					return false;
				}
			}
		}
		return true;

	}

    /**
     * 更新店铺医生科室推荐配置
     * @return
     */
    @PostMapping(value = "/shop/doctor/recommend/update")
    public JsonResult updateDoctorDepartmentCfg(@RequestBody @Valid DepartmentRecommendCfgParam commonCfg) {
        if(shop().config.shopCommonConfigService.updateDoctorDepartmentCfg(commonCfg)) {
            return this.success();
        }else {
            return this.fail(JsonResultCode.CODE_FAIL);
        }
    }

    /**
     * 获取店铺医生科室推荐配置
     * @return
     */
    @GetMapping(value = "/shop/doctor/recommend/get")
    public JsonResult getDoctorDepartmentCfg() {
        return success(shop().config.shopCommonConfigService.getDoctorDepartmentCfg());
    }
}
