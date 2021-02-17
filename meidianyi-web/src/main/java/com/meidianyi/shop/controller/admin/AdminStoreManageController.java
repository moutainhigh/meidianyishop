package com.meidianyi.shop.controller.admin;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.main.tables.records.StoreAccountRecord;
import com.meidianyi.shop.service.pojo.shop.store.account.StoreAccountEditParam;
import com.meidianyi.shop.service.pojo.shop.store.account.StoreAccountParam;
import com.meidianyi.shop.service.pojo.shop.store.account.StoreAccountVo;
import com.meidianyi.shop.service.pojo.shop.store.account.StoreInfoVo;
import com.meidianyi.shop.service.pojo.shop.store.account.StoreManageParam;
import com.meidianyi.shop.service.pojo.shop.store.authority.StoreAuthListPage;
import com.meidianyi.shop.service.pojo.shop.store.authority.StoreAuthListParam;
import com.meidianyi.shop.service.pojo.shop.store.authority.StoreAuthVo;
import com.meidianyi.shop.service.pojo.shop.store.authority.StoreConstant;
import com.meidianyi.shop.service.saas.shop.StoreManageService;

/**
 * 门店权限管理
 * 
 * @author zhaojianqiang
 * @time 上午10:34:38
 */
@RestController
public class AdminStoreManageController extends AdminBaseController {

	/**
	 * 获取门店权限列表和选中状态
	 * 
	 * @return
	 */
	@GetMapping("/api/admin/store/account/getSetting")
	public JsonResult getSettingInfo() {
		StoreAuthVo getstoreJson = saas.shop.storeManageService.getstoreJson(shopId());
		return success(getstoreJson);

	}

	/**
	 * 设置门店页面权限
	 * 
	 * @return
	 */
	@PostMapping("/api/admin/store/account/setting")
	public JsonResult settingInfo(@RequestBody @Valid StoreAuthListParam param) {
		boolean setting = saas.shop.storeManageService.setting(param, shopId());
		return setting ? success() : fail();
	}

	/**
	 * 门店账户列表
	 * 
	 * @param param
	 * @return
	 */
	@PostMapping("/api/admin/store/account/list")
	public JsonResult getAccount(@RequestBody StoreAuthListPage param) {
		PageResult<StoreAccountVo> accountList = saas.shop.storeManageService.getAccountList(shopId(), param);
		return success(accountList);
	}

	/**
	 * 应用门店弹窗查询
	 * 
	 * @param accountId
	 * @return
	 */
	@GetMapping("/api/admin/store/account/storeInfo/{accountId}")
	public JsonResult getStoreInfo(@PathVariable Integer accountId) {
		StoreInfoVo storeInfo = saas.shop.storeManageService.getStoreInfo(accountId, shopId());
		return storeInfo == null ? fail(JsonResultCode.CODE_ACCOUNT_ID_NOT) : success(storeInfo);
	}

	/**
	 * 一些处理
	 * 
	 * @param param
	 * @return
	 */
	@PostMapping("/api/admin/store/account/manage")
	public JsonResult accountStoreManage(@RequestBody @Valid StoreManageParam param) {
		String action = param.getAct();
		Integer accountId = param.getAccountId();
		boolean flag = false;
		JsonResultCode code = JsonResultCode.CODE_FAIL;
		StoreManageService storeManageService = saas.shop.storeManageService;
		switch (action) {
		case StoreConstant.ACT_CONFIG:
			logger().info("设置");
			flag = storeManageService.config(param);
			break;
		case StoreConstant.ACT_DEL:
			logger().info("删除");
			int update = storeManageService.storeAccountService.delStoreAccount(accountId);
			flag = update > 0;
			break;
		case StoreConstant.ACT_STOP:
			logger().info("停用");
			int stop = storeManageService.stop(accountId);
			flag = stop > 0;
			break;
		case StoreConstant.ACT_STATRT:
			logger().info("启用");
			code = storeManageService.start(accountId);
			flag = code.equals(JsonResultCode.CODE_SUCCESS);
			break;
		default:
			break;
		}
		if (!flag) {
			return fail(code);
		} else {
			return success();
		}
	}

	/**
	 * 新增
	 * 
	 * @param param
	 * @return
	 */
	@PostMapping("/api/admin/store/account/create")
	public JsonResult accountStoreCreate(@RequestBody @Valid StoreAccountParam param) {
		StoreAccountRecord record = saas.shop.storeManageService.storeAccountService.findInfo(param.getAccountName(),
				param.getMobile(), null);
		if (record != null) {
			// 账户名或手机号重复了
			return fail(JsonResultCode.CODE_ACCOUNT_MOBILE_SAME);
		}
		int create = saas.shop.storeManageService.storeAccountService.create(param, shopId());
		return create > 0 ? success() : fail();

	}

	/**
	 * 编辑
	 * 
	 * @param param
	 * @return
	 */
	@PostMapping("/api/admin/store/account/edit")
	public JsonResult accountStoreEdit(@RequestBody @Valid StoreAccountEditParam param) {
		StoreAccountRecord record = saas.shop.storeManageService.storeAccountService.findInfo(param.getAccountName(),
				param.getMobile(), param.getAccountId());
		if (record != null) {
			// 账户名或手机号重复了
			return fail(JsonResultCode.CODE_ACCOUNT_MOBILE_SAME);
		}
		Boolean check = saas.shop.storeManageService.check(param.getAccountPasswd());
		if (!check) {
			// 密码格式不正确
			return fail(JsonResultCode.CODE_ACCOUNT_PASSWD_LENGTH_LIMIT);
		}
		int edit = saas.shop.storeManageService.storeAccountService.edit(param);
		return edit > 0 ? success() : fail();

	}

	/**
	 * 根据id查询单一的信息
	 * 
	 * @param accountId
	 * @return
	 */
	@GetMapping("/api/admin/store/account/getOne/{accountId}")
	public JsonResult getOneAccount(@PathVariable Integer accountId) {
		StoreAccountVo storeInfoById = saas.shop.storeManageService.storeAccountService.getStoreInfoById(accountId);
		return success(storeInfoById);
	}
}
