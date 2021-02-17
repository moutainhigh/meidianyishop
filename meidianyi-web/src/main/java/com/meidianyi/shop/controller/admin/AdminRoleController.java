package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.main.tables.records.ShopAccountRecord;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.service.pojo.shop.auth.AdminTokenAuthInfo;
import com.meidianyi.shop.service.pojo.shop.auth.ShopManageParam;
import com.meidianyi.shop.service.pojo.shop.auth.ShopManagePwdParam;
import com.meidianyi.shop.service.pojo.shop.auth.ShopManageVo;
import com.meidianyi.shop.service.pojo.shop.auth.ShopReq;
import com.meidianyi.shop.service.pojo.shop.auth.ShopSelectResp;
import com.meidianyi.shop.service.pojo.shop.auth.ShopSubAccountAddParam;
import com.meidianyi.shop.service.pojo.shop.auth.ShopSubAccountEditParam;
import com.meidianyi.shop.service.pojo.shop.auth.ShopSubAccountParam;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 *
 * @author 新国
 *
 */
@RequestMapping("/api")
@Controller
@RestController
public class AdminRoleController extends AdminBaseController {
	final String TOKEN = "V-Token";

	/**
	 * 查询店铺列表
	 *
	 * @return
	 */
	@PostMapping(value = "/admin/account/shop/select")
	public JsonResult shopSelect() {
		AdminTokenAuthInfo info = adminAuth.user();
		if (info == null) {
			return fail(JsonResultCode.CODE_ACCOUNT_LOGIN_EXPIRED);
		}
		// saas.shop.accout
		List<ShopRecord> shopList = saas.shop.getRoleShopList(info.getSysId(), info.getSubAccountId());
		if (shopList.size() == 0) {
			logger().info("用户sysId：" + info.getSysId() + "，店铺列表为空");
		}
		ShopSelectResp response = new ShopSelectResp();
		response.setDataList(saas.shop.getShopList(info, shopList));
		response.setVersionMap(saas.shop.version.getVersionMap());
		return success(response);

	}

	/**
	 * 切换店铺
	 *
	 * @param shopId
	 * @return
	 */
	@RequestMapping(value = "/admin/account/shop/switch")
	public JsonResult switchShop(@RequestBody ShopReq shopReq) {
		// 更新redis
		if (adminAuth.switchShopLogin(shopReq.getShopId())) {
			return success();
		}
		return fail(JsonResultCode.CODE_ACCOUNT_SHOP_NULL);
	}

	/**
	 * 账户设置的查询 只让主账户进入设置
	 *
	 * @return
	 */
	@RequestMapping(value = "/admin/account/manage/query")
	public JsonResult manageQuery() {
		AdminTokenAuthInfo info = adminAuth.user();
		if (info.isSubLogin()) {
			// 权限不足
			return fail(JsonResultCode.CODE_ACCOUNT_ROLE__AUTH_INSUFFICIENT);
		}
		ShopManageVo shopRecord = saas.shop.account.getRow(info.getUserName(), info.getSysId());
		if (shopRecord == null) {
			return fail();
		}
		return success(shopRecord);
	}

	/**
	 * 账户设置更新
	 *
	 * @param shopManageParam
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/admin/account/manage")
	public JsonResult manage(@RequestBody @Valid ShopManageParam shopManageParam, BindingResult result) {
		// saas.shop.accout.updateAccountInfo()
		if (result.hasErrors()) {
			return this.fail(result.getFieldError().getDefaultMessage());
		}
		AdminTokenAuthInfo info = adminAuth.user();
		ShopAccountRecord pojo = new ShopAccountRecord();
		pojo.setSysId(info.getSysId());
		pojo.setAccountName(shopManageParam.getAccountName());
		pojo.setShopAvatar(shopManageParam.getShopAvatar());
		if (saas.shop.account.updateById(pojo) < 0) {
			return fail(JsonResultCode.CODE_FAIL);
		}
		adminAuth.updateAccountName(shopManageParam.getAccountName());
		return success(JsonResultCode.CODE_SUCCESS);
	}

	/**
	 * 更新用户密码
	 * @param sParam
	 * @return
	 */
	@PostMapping(value = "/admin/account/manage/updatepwd")
	public JsonResult managePasswd(@RequestBody @Valid ShopManagePwdParam sParam, BindingResult result) {
		if (result.hasErrors()) {
			return this.fail(result.getFieldError().getDefaultMessage());
		}
		AdminTokenAuthInfo info = adminAuth.user();
		if (info == null) {
			return fail(JsonResultCode.CODE_ACCOUNT_LOGIN_EXPIRED);
		}
		if (!sParam.getNewPasswd().equals(sParam.confNewPasswd)) {
			return fail(JsonResultCode.CODE_ACCOUNT_PASSWD_NO_SAME);
		}
		ShopAccountRecord oldRecode = saas.shop.account.verify(info.getUserName(), sParam.getPasswd());
		if(oldRecode==null) {
			return fail(JsonResultCode.CODE_ACCOUNT_OLD_PASSWD_ERROR);
		}
		if(sParam.getNewPasswd().equals(sParam.getPasswd())) {
			return fail(JsonResultCode.CODE_ACCOUNT_OLD_NEW_PASSWD_NO_SAME);
		}
		ShopAccountRecord pojo = new ShopAccountRecord();
		pojo.setSysId(info.getSysId());
		pojo.setPassword(Util.md5(sParam.newPasswd));

		if (saas.shop.account.updateById(pojo) < 0) {
			return fail(JsonResultCode.CODE_FAIL);
		}
		return success(JsonResultCode.CODE_SUCCESS);
	}

	/**
	 * 查询子账户
	 * @param param
	 * @return
	 */
	@PostMapping(value = "/admin/account/user/list")
	public JsonResult subUserList(@RequestBody ShopSubAccountParam param) {
		return success(saas.overviewService.childAccountService.getAccountUserList(adminAuth.user().sysId,
				param.getCurrentPage(), param.getPageRows()));

	}
	/**
	 * 添加子账户
	 * @param param
	 * @return
	 */
	@PostMapping(value = "/admin/account/user/add")
	public JsonResult subUserAdd(@RequestBody @Valid ShopSubAccountAddParam param) {
		JsonResultCode addSubAccount = saas.overviewService.childAccountService.addSubAccount(adminAuth.user().sysId, param);
		if(addSubAccount!=null) {
			return fail(addSubAccount);
		}
		return success();
	}

	/**
	 * 删除子账户
	 * @param param
	 * @return
	 */
	@GetMapping(value = "/admin/account/user/del/{accountId}")
	public JsonResult csubUserDel(@PathVariable Integer accountId) {
		int account = saas.overviewService.childAccountService.delSubAccount(adminAuth.user().sysId, accountId);
		if(account>0) {
			return success();
		}
		return fail();
	}


	/**
	 * 编辑子账户
	 * @param param
	 * @return
	 */
	@PostMapping(value = "/admin/account/user/edit")
	public JsonResult subUserEdit(@RequestBody @Valid ShopSubAccountEditParam param) {

		int account = saas.overviewService.childAccountService.editSubAccount(adminAuth.user().sysId, param.getAccountId(), param.getAccountName(), param.getAccountPwd(),param.getMobile());
		if(account>0) {
			return success();
		}
		return fail();
	}

	/**
	 * 查询单个店铺的信息
	 * @return
	 */
	@GetMapping(value = "/admin/account/shop/oneInfo")
	public JsonResult shopInfo() {
		AdminTokenAuthInfo info = adminAuth.user();
		if (info == null) {
			return fail(JsonResultCode.CODE_ACCOUNT_LOGIN_EXPIRED);
		}
		return success(saas.shop.getShopInfo(info.getLoginShopId()));
	}
}
