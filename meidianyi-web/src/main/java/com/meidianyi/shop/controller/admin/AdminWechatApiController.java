package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.main.tables.records.MpAuthShopRecord;
import com.meidianyi.shop.db.main.tables.records.MpOfficialAccountRecord;
import com.meidianyi.shop.db.main.tables.records.MpVersionRecord;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpAuditStateVo;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpAuthShopToAdminVo;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpDeployQueryParam;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpOfficeAccountVo;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpOperateListParam;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpOperateVo;
import com.meidianyi.shop.service.pojo.saas.shop.officeaccount.MpOaPayManageParam;
import com.meidianyi.shop.service.pojo.saas.shop.officeaccount.MpOfficeAccountListParam;
import com.meidianyi.shop.service.pojo.saas.shop.officeaccount.MpOfficeAccountListVo;
import com.meidianyi.shop.service.pojo.shop.auth.AdminTokenAuthInfo;
import com.meidianyi.shop.service.pojo.shop.config.WxShoppingListConfig;
import com.meidianyi.shop.service.saas.shop.MpAuthShopService;
import com.meidianyi.shop.service.shop.sms.SmsAccountService;
import com.meidianyi.shop.service.wechat.OpenPlatform;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.open.bean.result.WxOpenResult;
import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @author lixinguo
 */
@RestController
public class AdminWechatApiController extends AdminBaseController {

	@Autowired
	protected OpenPlatform open;

	@Value(value = "${official.appId}")
	private String bindAppId;
	@Autowired
	private SmsAccountService smsAccountService;

	/**
	 * 开始小程序授权
	 *
	 * @return
	 */
	@GetMapping(value = "/api/admin/start/auth")
	@ResponseBody
	public JsonResult startAuthorization() {
		Logger logger = logger();

		Integer shopId = this.shopId();

		logger.debug("授权店铺ID: {}", shopId);

		String url = this.mainUrl("/wechat/notify/authorization/callback?shop_id=" + shopId);

		logger.debug("授权回调url: {}", url);

		try {
			String authType = "1";
			String bizAppId = null;
			MpAuthShopRecord mp = saas.shop.mp.getAuthShopByShopId(shopId);
			if (mp != null) {
				bizAppId = mp.getAppId();

				logger.debug("授权bizAppId: {}", bizAppId);
			}
			url = open.getWxOpenComponentService().getPreAuthUrl(url, null, bizAppId);

			logger.debug("授权二维码页面url: {}", url);

			return success(url);
		} catch (WxErrorException e) {
			logger.error("WxErrorException code:{} messge:{}", e.getError().getErrorCode(), e.getError().getErrorMsg());
			return fail();
		}
	}

	/**
	 * 开始公众号授权
	 *
	 * @return
	 */
	@GetMapping(value = "/api/admin/official/account/authorization")
	@ResponseBody
	public JsonResult startOfficialAccountAuthorization() {
		String url = this.mainUrl("/wechat/notify/authorization/callback?sys_id=" + this.adminAuth.user().getSysId());

		try {
			String authType = "1";
			String bizAppid = null;
			url = open.getWxOpenComponentService().getPreAuthUrl(url, authType, bizAppid);

			return success(url);
		} catch (WxErrorException e) {
			e.printStackTrace();
			return fail();
		}
	}

	/**
	 * 得到小程序信息
	 *
	 * @return
	 */
	@GetMapping("/api/admin/mp/get")
	public JsonResult getMp() {
		MpAuthShopRecord record = saas.shop.mp.getAuthShopByShopIdAddUrl(this.shopId());
		if (record == null) {
			return fail(JsonResultCode.WX_MA_NEED_AUTHORIZATION);
		}
		//可以绑定的公众号列表
		logger().info("开始查询可以绑定的公众号列表");
		Result<MpOfficialAccountRecord> officialAccountBySysId = saas.shop.officeAccount.getOfficialAccountBySysId(adminAuth.user().sysId);
		List<MpOfficeAccountVo> officialList = saas.shop.officeAccount.findSamePrincipalMiniAndMp(officialAccountBySysId, record);
		MpAuthShopToAdminVo into = record.into(MpAuthShopToAdminVo.class);
		into.setOfficialList(officialList);
		//查询已绑定的公众号信息
		logger().info("开始查询已绑定的公众号信息");
		Record officialAccount = saas.shop.officeAccount.getOfficeAccountByAppIdRecord(record.getLinkOfficialAppId());
		//最新版本id
		logger().info("开始查询最新版本id");
		MpVersionRecord mpRecord = saas.shop.mpVersion.getCurrentUseVersion(record.getAppId(),(byte)1);
		into.setCurrentTemplateId(mpRecord == null ? 0 : mpRecord.getTemplateId());
		into.setCurrentUserVersion(mpRecord == null ? "0.0.0" : mpRecord.getUserVersion());
		//当前版本名字
		logger().info("开始查询当前版本名字，版本ID为"+record.getBindTemplateId());
		MpVersionRecord row = saas.shop.mpVersion.getRow(record.getBindTemplateId());
		if(null==row) {
			//没有上传过
			logger().info("还没上传过代码，模板为0");
			into.setBindUserVersion("0.0.0");
		}else {
			into.setBindUserVersion(row.getUserVersion());
		}
		if(officialAccount!=null) {
			into.setOfficialAccount(officialAccount.into(MpOfficeAccountVo.class));
		}
		return success(into);
	}

	/**
	 * 设置小程序好物圈
	 *
	 * @param config
	 */
	@PostMapping("/api/admin/wxshopping/update")
	public JsonResult switchWxShoppingList(@RequestBody WxShoppingListConfig config) {
		shop().shoppingListConfig.setShoppingListConfig(config);
		return success();
	}

	/**
	 * 查看小程序好物圈情况
	 *
	 * @return
	 */
	@GetMapping("/api/admin/wxshopping/list")
	public JsonResult getWxShoppongList() {
		WxShoppingListConfig shoppingListConfig = shop().shoppingListConfig.getShoppingListConfig();

		return success(shoppingListConfig);
	}

	/**
	 * 店家小程序版本操作日志分页列表
	 *
	 * @param param 过滤信息
	 * @return 分页结果值
	 */
	@PostMapping("/api/admin/mp/operate/log/list")
	public JsonResult logList(@RequestBody MpOperateListParam param) {
		Integer shopId = shopId();
		Boolean authOk = saas.shop.mp.isAuthOk(shopId);
		if (!authOk) {
			return fail(JsonResultCode.WX_MA_SHOP_HAS_NO_AP);
		}

		String language = StringUtils.isEmpty(request.getHeader("V-Lang")) ? "" : request.getHeader("V-Lang");
		PageResult<MpOperateVo> mpOperateVoPageResult = saas.shop.mpOperateLog.logList(param, shopId, language);
		return success(mpOperateVoPageResult);
	}

	/**
	 * 获取小程序版本下拉列表
	 *
	 * @return 下拉列表值
	 */
	@GetMapping("/api/admin/mp/version/user/version/list")
	public JsonResult getMpUserVersionList() {
		return success(saas.shop.mpVersion.getMpUserVersionList());
	}

	/**
	 * 获取店铺相关联的小程序的审核信息
	 *
	 * @return 审核信息
	 */
	@GetMapping("/api/admin/mp/audit/get")
	public JsonResult getAppAduitInfo() {
		Integer shopId = shopId();
		Boolean authOk = saas.shop.mp.isAuthOk(shopId);
		if (!authOk) {
			return fail(JsonResultCode.WX_MA_SHOP_HAS_NO_AP);
		}

		MpAuditStateVo appAuditInfo = saas.shop.mp.getAppAuditInfo(shopId);

		return success(appAuditInfo);
	}

	/**
	 * 获取公众号列表
	 *
	 * @param oaListParam
	 * @return
	 */
	@PostMapping("/api/admin/public/service/auth/list")
	public JsonResult serviceAuthList(@RequestBody MpOfficeAccountListParam oaListParam) {
		oaListParam.setSysId(adminAuth.user().getSysId());
		return success(saas.shop.officeAccount.getPageList(oaListParam));
	}

	/**
	 * 获取单个公众号信息
	 *
	 * @param oaListParam
	 * @return
	 */
	@PostMapping("/api/admin/public/service/auth/oneList")
	public JsonResult serviceAuthDetail(@RequestBody MpOfficeAccountListParam oaListParam) {
		return success(saas.shop.officeAccount.getOfficeAccountByAppIdAndsysId(oaListParam.getAppId(), adminAuth.user().getSysId()));

	}

	protected Boolean checkSysId(Integer sendSysId) {
		if (StringUtils.isEmpty(sendSysId)) {
			return false;
		}
		AdminTokenAuthInfo user = adminAuth.user();
		Assert.isTrue(user != null && user.isShopLogin(),"user is null");
		if (!sendSysId.equals(user.sysId)) {
			// 没有权限
			return false;
		}
		return true;
	}

	/**
	 * 提现配置
	 *
	 * @param oaParam
	 * @return
	 */
	@PostMapping("/api/admin/public/service/auth/payManage")
	public JsonResult payManage(@RequestBody MpOaPayManageParam oaParam) {
		oaParam.setSysId(adminAuth.user().getSysId());
		Integer updatePayInfo = saas.shop.officeAccount.updatePayInfo(oaParam);
		if(updatePayInfo>0) {
			return success();
		}
		return fail();

	}

	/**
	 * 获取绑定店铺的二维码
	 * @return
	 */
	@GetMapping("/api/admin/public/service/bind/getOfficialQrCode")
	public JsonResult generateThirdPartCode() {
        AdminTokenAuthInfo adminTokenAuthInfo=adminAuth.user();
        MpAuthShopRecord wxapp = saas.shop.mp.getAuthShopByShopId(adminTokenAuthInfo.getLoginShopId());
        try {
			if(!adminAuth.user().shopLogin) {
				return fail(JsonResultCode.CODE_ACCOUNT_ROLE__SHOP_SELECT);
			}
			String generateThirdPartCode = saas.shop.officeAccount.generateThirdPartCode(adminAuth.user(),wxapp.getLinkOfficialAppId());
			return success(generateThirdPartCode);
		} catch (WxErrorException e) {
			logger().debug(e.getMessage(),e);
		}
		return fail();

	}

	/**
	 * 公众号绑定小程序
	 * @param appId 公众号的appid
	 * @return
	 */
	@GetMapping("/api/admin/public/service/bind/official/{appId}")
	public JsonResult bindOfficialAccount(@PathVariable String appId) {
		String language = StringUtils.isEmpty(request.getHeader("V-Lang")) ? null : request.getHeader("V-Lang");
		MpAuthShopRecord authShopByShopId = saas.shop.mp.getAuthShopByShopId(adminAuth.user().loginShopId);
		System.out.println(authShopByShopId);
		if (authShopByShopId == null) {
			// 请先绑定小程序
			return fail(JsonResultCode.WX_MA_SHOP_HAS_NO_AP);
		}
		if (!authShopByShopId.getIsAuthOk().equals((byte) 1)) {
			// 小程序未授权
			return fail(JsonResultCode.WX_MA_APP_ID_NOT_AUTH);
		}
		System.out.println("authShopByShopId.getLinkOfficialAppId()"+authShopByShopId.getLinkOfficialAppId());
		if (!StringUtils.isEmpty(authShopByShopId.getLinkOfficialAppId())) {
			MpOfficeAccountListVo officeAccountByAppId = saas.shop.officeAccount.getOfficeAccountByAppId(authShopByShopId.getLinkOfficialAppId());
			if (officeAccountByAppId != null && officeAccountByAppId.getIsAuthOk() == 1) {
				// 该小程序已存在绑定公众号
				return fail(JsonResultCode.WX_MA_HAVE_MP);
			}
		}
		//判断客户公众号绑定的小程序是不是现在选择的
		try {
			Boolean wxamplinkget = saas.shop.officeAccount.wxamplinkget(appId,authShopByShopId.getUserName());
			if(!wxamplinkget) {
				//请选择正确的公众号
				return fail(JsonResultCode.WX_MP_NEED_CHOOSE_RIGHT);
			}
		} catch (WxErrorException e) {
			logger().debug(e.getMessage(), e);
			WxOpenResult result = new WxOpenResult();
			result.setErrcode(String.valueOf(e.getError().getErrorCode()));
			result.setErrmsg(e.getError().getErrorMsg());
			return wxfail(result);
		}
		//让客户自己去开放平台把公众号和小程序绑定。这个方法只改变数据库的值
		authShopByShopId.setLinkOfficialAppId(appId);
		saas.shop.mp.updateRow(authShopByShopId);
		//跑个异步任务去 批量获取公众号用户信息
		saas.shop.officeAccount.batchGetUsersByRabbitMq(appId, language, adminAuth.user().getSysId(), adminAuth.user().loginShopId);
		return success();

	}

	/**
	 * 上传代码并提交 admin用
	 * @param param
	 * @return
	 * @throws WxErrorException
	 * @throws IOException
	 */
	@PostMapping("/api/admin/mp/publish")
	public JsonResult mpPublishAction(@RequestBody MpDeployQueryParam param) throws WxErrorException, IOException {
		MpAuthShopService mp = saas.shop.mp;
		if (!mp.isAuthOk(param.getAppId())) {
			return fail(JsonResultCode.WX_MA_APP_ID_NOT_AUTH);
		}
		WxOpenResult result = new WxOpenResult();
		try {
			result = mp.uploadCodeAndApplyAudit(param.getAppId(), param.getTemplateId());
		} catch (WxErrorException e) {
			result.setErrcode(String.valueOf(e.getError().getErrorCode()));
			result.setErrmsg(e.getError().getErrorMsg());
			logger().debug(e.getMessage(),e);
			//加入日志中
			mp.erroInsert(param, result);
		}catch (Exception e) {
			result.setErrcode("500");
			result.setErrmsg(e.getMessage());
			logger().debug(e.getMessage(),e);
			mp.erroInsert(param, result);
		}
		return result.isSuccess() ? success(result) : wxfail(result);

	}


}
