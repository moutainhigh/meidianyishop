package com.meidianyi.shop.controller.system;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.main.tables.records.MpAuthShopRecord;
import com.meidianyi.shop.service.pojo.saas.shop.ShopMpListParam;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpAuthShopListParam;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpAuthShopListVo;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpAuthShopVo;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpCurrentTempIdVo;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpDeployQueryParam;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpOperateListParam;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpOperateVo;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpPackageVersionVo;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpUploadListParam;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpVersionListParam;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpVersionListVo;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpVersionParam;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpVersionVo;
import com.meidianyi.shop.service.saas.shop.MpAuthShopService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.open.bean.result.WxOpenResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;

/**
 *
 * @author lixinguo
 *
 */
@RestController
public class SystemMpAuthShopController extends SystemBaseController {


	private static Logger log = LoggerFactory.getLogger(FieldsUtil.class);
	/**
	 * 小程序模板版本分页
	 *
	 * @param param
	 * @return
	 */
	@PostMapping("/api/system/mp/version/list")
	public JsonResult list(@RequestBody MpVersionListParam param) {
		PageResult<MpVersionVo> pageList = saas.shop.mpVersion.getPageList(param);
		return success(pageList);
	}

	/**
	 * 同步小程序模板库
	 *
	 * @return
	 * @throws WxErrorException
	 */
	@PostMapping("/api/system/mp/version/syn")
	public JsonResult synMpList() throws WxErrorException {
		return success(saas.shop.mpVersion.synMpVersionList());
	}

	/**
	 * 设置版本
	 *
	 * @return
	 */
	@GetMapping("/api/system/mp/version/set/{templateId}")
	public JsonResult setVersion(@PathVariable Integer templateId) {
		saas.shop.mpVersion.setCurrentUseTemplateId(templateId);
		return success();
	}

	/**
	 * 批量提交审核
	 *
	 * @return
	 */
	@GetMapping("/api/system/mp/version/batch{templateId}")
	public JsonResult batchPublish(@PathVariable Integer templateId) {

		return success();
	}

	/**
	 * 小程序模板发布
	 *
	 * @param param
	 * @return
	 * @throws WxErrorException
	 * @throws IOException
	 */
	@PostMapping("/api/system/mp/publish")
	public JsonResult mpPublishAction(@RequestBody MpDeployQueryParam param) throws WxErrorException, IOException {
		MpAuthShopService mp = saas.shop.mp;
		if (!mp.isAuthOk(param.getAppId())) {
			return fail(JsonResultCode.WX_MA_APP_ID_NOT_AUTH);
		}
		WxOpenResult result = new WxOpenResult();
		try {
			switch (param.getAct()) {
			case MpDeployQueryParam.ACT_ADD_TESTER: {
				result = mp.bindTester(param.getAppId(), param.getWechatId());
				break;
			}

			case MpDeployQueryParam.ACT_DEL_TESTER: {
				result = mp.unbindTester(param.getAppId(), param.getWechatId());
				break;
			}
			case MpDeployQueryParam.ACT_GET_CATEGORY: {
				result = mp.getCategory(param.getAppId());
				break;
			}
			case MpDeployQueryParam.ACT_GET_PAGE_CFG: {
				result = mp.getPage(param.getAppId());
				break;
			}
			case MpDeployQueryParam.ACT_GET_TESTER_QR: {
				result = mp.getTestQrCode(param.getAppId());
				break;
			}
			case MpDeployQueryParam.ACT_MODIFY_DOMAIN: {
				result = mp.modifyDomain(param.getAppId());
				break;
			}
			case MpDeployQueryParam.ACT_PUBLISH_CODE: {
				result = mp.publishAuditSuccessCode(param.getAppId());
				break;
			}
			case MpDeployQueryParam.ACT_SUBMIT_AUDIT: {
				result = mp.submitAudit(param.getAppId());
				break;
			}
			case MpDeployQueryParam.ACT_UPDATE_MP: {
				result=mp.updateAppInfo(param.getAppId());
				break;
			}
			case MpDeployQueryParam.ACT_REFRESH_AUDIT_STATE: {
				result = mp.refreshAppInfo(param.getAppId());
				break;
			}
			case MpDeployQueryParam.ACT_UPLOAD_AUDIT: {
				result = mp.uploadCodeAndApplyAudit(param.getAppId(), param.getTemplateId());
				break;
			}
			case MpDeployQueryParam.ACT_UPLOAD_CODE: {
				result = mp.uploadCode(param.getAppId(), param.getTemplateId());
				break;
			}
			case MpDeployQueryParam.SETTING_SUB_MERCHANT: {
				result = mp.setSubMerchant(param);
				break;
			}
			default: {
				return fail(JsonResultCode.CODE_PARAM_ERROR);
			}
			}
		} catch (WxErrorException e) {
			result.setErrcode(String.valueOf(e.getError().getErrorCode()));
			result.setErrmsg(e.getError().getErrorMsg());
			log.debug(e.getMessage(),e);
			//加入日志中
			mp.erroInsert(param, result);
		}catch (Exception e) {
			result.setErrcode("500");
			result.setErrmsg(e.getMessage());
			log.debug(e.getMessage(),e);
			mp.erroInsert(param, result);
		}
		return result.isSuccess() ? success(result) : wxfail(result);
	}

	/**
	 * 得到小程序信息
	 *
	 * @param appId 小程序id
	 * @return
	 */
	@GetMapping("/api/system/mp/get/{appId}")
	public JsonResult getMp(@PathVariable String appId) {
		MpAuthShopRecord record = saas.shop.mp.getAuthShopByAppIdAddUrl(appId);
		if (record == null) {
			return fail(JsonResultCode.CODE_PARAM_ERROR);
		}
		return success(record.into(MpAuthShopVo.class));
	}

	/**
	 * 获取小程序版本下拉列表
	 *
	 * @return 下拉列表值
	 */
	@GetMapping("/api/system/mp/version/user/version/list")
	public JsonResult getMpUserVersionList() {
		return success(saas.shop.mpVersion.getMpUserVersionList());
	}

	/**
	 * 小程序版本操作日志分页列表
	 *
	 * @param param 过滤信息
	 * @return 分页结果值
	 */
	@PostMapping("/api/system/mp/operate/log/list")
	public JsonResult logList(@RequestBody MpOperateListParam param) {
		String language = StringUtils.isEmpty(request.getHeader("V-Lang"))?"":request.getHeader("V-Lang");
		PageResult<MpOperateVo> mpOperateVoPageResult = saas.shop.mpOperateLog.logList(param,language);
		return success(mpOperateVoPageResult);
	}

	/**
	 * 更改当前包版本
	 *
	 * @param pVersionVo
	 * @return
	 */
	@PostMapping(value = "/api/system/mp/package/version")
	public JsonResult setPackageVersion(@RequestBody @Valid MpPackageVersionVo pVersionVo) {
		Integer updatePackVersion = saas.shop.mpVersion.updatePackVersion(pVersionVo.getTemplateId(),
				pVersionVo.getPackageVersion());
		if (updatePackVersion < 0) {
			return fail();
		}
		return success();

	}

	/**
	 * 小程序授权列表分页查询
	 *
	 * @param param
	 * @return
	 */
	@PostMapping("/api/system/mp/auth/list")
	public JsonResult authList(@RequestBody MpAuthShopListParam param) {
		PageResult<MpAuthShopListVo> authList = saas.shop.mp.getAuthList(param);

		return success(authList);
	}

	/**
	 * 小程序版本统计
	 *
	 * @param mVersionParam
	 * @return
	 */
	@PostMapping("/api/system/mp/version/stat")
	public JsonResult mpVersionStat(@RequestBody MpVersionParam mVersionParam) {
		PageResult<MpVersionListVo> mpStat = saas.shop.mpVersion.getMpStat(mVersionParam);
		return success(mpStat);

	}

	/**
	 * 当前模板id
	 *
	 * @return
	 */
	@GetMapping("/api/system/mp/info/useTemplateId/{appId}")
	public JsonResult currentTmpId(@PathVariable String appId) {
		Integer currentUseTemplateId = saas.shop.mpVersion.getCurrentUseTemplateId(appId);
		MpCurrentTempIdVo mpCurrentTempIdVo = new MpCurrentTempIdVo();
		mpCurrentTempIdVo.setCurrentUseTemplateId(currentUseTemplateId);
		return success(mpCurrentTempIdVo);
	}

	/**
	 * 批量提交小程序审核 batch_apply
	 * @param templateId
	 * @return
	 */
	@GetMapping("/api/system/mp/version/batch_apply/{templateId}")
	public JsonResult batchApply(@PathVariable Integer templateId) {
		if(templateId==null) {
			return fail();
		}
		JsonResultCode rBoolean = saas.shop.mp.batchUploadCodeAndApplyAudit(templateId);
		if(rBoolean.equals(JsonResultCode.CODE_SUCCESS)) {
			return success();
		}
		return fail(rBoolean);
	}

	/**
	 * 查询小程序审核
	 * @param param
	 * @return
	 */
	@PostMapping("/api/system/back/process/list")
	public JsonResult getBackProcessList(@RequestBody MpUploadListParam param) {
		return success(saas.shop.backProcessService.getPageList(param));

	}

	/**
	 * 终止小程序审核
	 * @return
	 */
	@GetMapping("/api/system/back/process/stop/{recId}")
	public JsonResult stopUpload(@PathVariable Integer recId) {
		Boolean stopBatchUpload = saas.shop.mp.stopBatchUpload(recId);
		if(!stopBatchUpload) {
			//任务不存在或已完成，请重新检查
			return fail(JsonResultCode.WX_JOB_PROBLEM);
		}
		return success();

	}

	/**
	 * 店铺发布列表
	 * @param param
	 * @return
	 */
	@PostMapping(value = "/api/system/shop/mp/list")
	public JsonResult getShopMpList(@RequestBody ShopMpListParam param) {
		return success(saas.shop.mp.getShopMpList(param));

	}
	}
