package com.meidianyi.shop.service.saas.shop;

import cn.binarywang.wx.miniapp.util.json.WxMaGsonBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.*;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.DomainConfig;
import com.meidianyi.shop.db.main.tables.MpAuthShop;
import com.meidianyi.shop.db.main.tables.records.*;
import com.meidianyi.shop.db.shop.tables.records.UserRecord;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobsConstant;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobsConstant.TaskJobEnum;
import com.meidianyi.shop.service.pojo.saas.shop.ShopMpListParam;
import com.meidianyi.shop.service.pojo.saas.shop.ShopMpListVo;
import com.meidianyi.shop.service.pojo.saas.shop.mp.*;
import com.meidianyi.shop.service.pojo.saas.shop.officeaccount.MaMpBindParam;
import com.meidianyi.shop.service.pojo.saas.shop.officeaccount.MpOfficeAccountListVo;
import com.meidianyi.shop.service.pojo.shop.config.trade.WxpayConfigParam;
import com.meidianyi.shop.service.pojo.shop.market.live.LiveCheckVo;
import com.meidianyi.shop.service.pojo.shop.market.message.BatchUploadCodeParam;
import com.meidianyi.shop.service.pojo.shop.market.message.RabbitMessageParam;
import com.meidianyi.shop.service.pojo.shop.market.message.RabbitParamConstant;
import com.meidianyi.shop.service.pojo.shop.message.MpTemplateConfig;
import com.meidianyi.shop.service.pojo.shop.message.MpTemplateData;
import com.meidianyi.shop.service.saas.image.SystemImageService;
import com.meidianyi.shop.service.saas.shop.official.message.MpOfficialAccountMessageService;
import com.meidianyi.shop.service.shop.decoration.AppletsJumpService;
import com.meidianyi.shop.service.wechat.api.WxOpenAccountService;
import com.meidianyi.shop.service.wechat.bean.ma.MpWxMaOpenCommitExtInfo;
import com.meidianyi.shop.service.wechat.bean.ma.WxContentTemplate;
import com.meidianyi.shop.service.wechat.bean.open.MaWxPlusInListInner;
import com.meidianyi.shop.service.wechat.bean.open.MaWxPlusInResult;
import com.meidianyi.shop.service.wechat.bean.open.WxOpenGetResult;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.open.api.WxOpenMaService;
import me.chanjar.weixin.open.bean.WxOpenCreateResult;
import me.chanjar.weixin.open.bean.auth.WxOpenAuthorizationInfo;
import me.chanjar.weixin.open.bean.auth.WxOpenAuthorizerInfo;
import me.chanjar.weixin.open.bean.ma.WxOpenMaCategory;
import me.chanjar.weixin.open.bean.ma.WxOpenMaMember;
import me.chanjar.weixin.open.bean.ma.WxOpenMaSubmitAudit;
import me.chanjar.weixin.open.bean.message.WxOpenMaSubmitAuditMessage;
import me.chanjar.weixin.open.bean.result.*;
import org.apache.commons.lang3.StringUtils;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static com.meidianyi.shop.db.main.tables.MpAuthShop.MP_AUTH_SHOP;
import static com.meidianyi.shop.db.main.tables.MpOfficialAccount.MP_OFFICIAL_ACCOUNT;
import static com.meidianyi.shop.db.main.tables.MpOfficialAccountUser.MP_OFFICIAL_ACCOUNT_USER;
import static com.meidianyi.shop.db.main.tables.Shop.SHOP;

/**
 *
 * @author 新国
 *
 */
@Service

public class MpAuthShopService extends MainBaseService {

	@Autowired
	protected DomainConfig domainConfig;

	@Autowired
	protected SystemImageService image;

	@Autowired
	protected  MpOfficialAccountMessageService accountMessageService;


	public static final Byte AUTH_OK = 1;
	public static final Byte AUTH_CANCEL = 0;

	public static final Byte AUDIT_STATE_NO_SUBMIT = 0;
	public static final Byte AUDIT_STATE_AUDITING = 1;
	public static final Byte AUDIT_STATE_AUDIT_SUCCESS = 2;
	public static final Byte AUDIT_STATE_AUDIT_FAILED = 3;

	public static final Integer WX_AUTH_STATUS_PASSED = 0;
	public static final Integer WX_AUTH_STATUS_AUDIT_FAILED = 1;
	public static final Integer WX_AUTH_STATUS_AUDITING = 2;

	/**
	 * 添加小程序信息
	 *
	 * @param appId
	 * @param shopId
	 * @return
	 * @throws WxErrorException
	 */
	public MpAuthShopRecord addMpAuthAccountInfo(String appId, Integer shopId) throws WxErrorException {

		WxOpenAuthorizerInfoResult authInfo = open().getWxOpenComponentService().getAuthorizerInfo(appId);
		MpAuthShopRecord record = db().newRecord(MP_AUTH_SHOP);
		WxOpenAuthorizationInfo authorizationInfo = authInfo.getAuthorizationInfo();
		WxOpenAuthorizerInfo authorizerInfo = authInfo.getAuthorizerInfo();
		record.setAppId(authorizationInfo.getAuthorizerAppid());
		record.setShopId((shopId));
		record.setNickName(authorizerInfo.getNickName());
		record.setUserName(authorizerInfo.getUserName());
		record.setAlias(authorizerInfo.getAlias());
		record.setVerifyTypeInfo(authorizerInfo.getVerifyTypeInfo().toString());
		record.setHeadImg(authorizerInfo.getHeadImg());
		record.setFuncInfo(Util.toJson(authorizationInfo.getFuncInfo()));
		record.setOpenCard(authorizerInfo.getBusinessInfo().get("open_card").byteValue());
		record.setOpenPay(authorizerInfo.getBusinessInfo().get("open_pay").byteValue());
		record.setIsAuthOk((byte) 1);
		record.setAuthorizationInfo(Util.toJson(authorizationInfo));
		record.setAuthorizerInfo(Util.toJson(authorizerInfo));
		record.setPrincipalName(authorizerInfo.getPrincipalName());
		record.setQrcodeUrl(getMpQrCode(appId, authorizerInfo));
        //表中非空，塞入空值
		record.setBindOpenAppId("");
		if (this.getAuthShopByAppId(appId) == null) {
			logger().info("插入");
			record.insert();
			// TODO: log operation
		} else {
			logger().info("更新");
			record.update();
			// TODO: log operation
		}
		return record;
    }
    /**
     * 更新小程序信息
     *
     * @param appId 小程序id
     * @return 是否更新成功
     */
    public WxOpenResult updateAppInfo(String appId) throws WxErrorException {
        String updateSuccessMsg="更新授信息";
        String updateErrorMsg="database has no data";

        MpAuthShopRecord record = db().selectFrom(MP_AUTH_SHOP).where(MP_AUTH_SHOP.APP_ID.eq(appId)).fetchAny();
        WxOpenResult wxOpenResult = new WxOpenResult();
        if (record == null) {
            wxOpenResult.setErrcode("1");
            wxOpenResult.setErrmsg(updateErrorMsg);
            return wxOpenResult;
        }
        WxOpenAuthorizerInfoResult authInfo = open().getWxOpenComponentService().getAuthorizerInfo(appId);
        WxOpenAuthorizationInfo authorizationInfo = authInfo.getAuthorizationInfo();
        WxOpenAuthorizerInfo authorizerInfo = authInfo.getAuthorizerInfo();

        record.setNickName(authorizerInfo.getNickName());
        record.setUserName(authorizerInfo.getUserName());
        record.setAlias(authorizerInfo.getAlias());
        record.setVerifyTypeInfo(authorizerInfo.getVerifyTypeInfo().toString());
        record.setHeadImg(authorizerInfo.getHeadImg());
        record.setFuncInfo(Util.toJson(authorizationInfo.getFuncInfo()));
        record.setOpenCard(authorizerInfo.getBusinessInfo().get("open_card").byteValue());
        record.setOpenPay(authorizerInfo.getBusinessInfo().get("open_pay").byteValue());
        record.setIsAuthOk((byte) 1);
        record.setAuthorizationInfo(Util.toJson(authorizationInfo));
        record.setAuthorizerInfo(Util.toJson(authorizerInfo));
        record.setPrincipalName(authorizerInfo.getPrincipalName());
        record.setQrcodeUrl(getMpQrCode(appId, authorizerInfo));
        //setTestRecord(record, appId);
        record.update();

        wxOpenResult.setErrcode("0");
        wxOpenResult.setErrmsg(updateSuccessMsg);

        operateLogGlobal(record, MpOperateLogService.OP_TYPE_UPDATE_MP, wxOpenResult, WxContentTemplate.WX_UPDATE_MP_SUCCESS.code, new String[] {});
        return wxOpenResult;
    }

	/**
	 * appId是否授权成功
	 *
	 * @param appId
	 * @return
	 */
	public Boolean isAuthOk(String appId) {
		MpAuthShopRecord mp = getAuthShopByAppId(appId);
		return (mp != null && mp.getIsAuthOk().equals(AUTH_OK));
	}

	/**
	 * 店铺是否授权成功
	 *
	 * @param shopId
	 * @return
	 */
	public Boolean isAuthOk(Integer shopId) {
		MpAuthShopRecord mp = getAuthShopByShopId(shopId);
		return (mp != null && mp.getIsAuthOk().equals(AUTH_OK));
	}

	/**
	 * 获取小程序服务
	 *
	 * @param shopId
	 * @return
	 */
	public WxOpenMaService getMaServiceByShopId(Integer shopId) {
		MpAuthShopRecord mp = getAuthShopByShopId(shopId);
		Assert.isTrue(mp != null && mp.getIsAuthOk().equals(AUTH_OK), "Miniprogram is not authed!");
		return open().getWxOpenComponentService().getWxMaServiceByAppid(mp.getAppId());
	}

	/**
	 * 获取小程序服务
	 *
	 * @param appId
	 * @return
	 */
	public WxOpenMaService getMaServiceByAppId(String appId) {
		MpAuthShopRecord mp = getAuthShopByAppId(appId);
		Assert.isTrue(mp != null && mp.getIsAuthOk().equals(AUTH_OK), "Miniprogram is not authed!");
		return open().getWxOpenComponentService().getWxMaServiceByAppid(mp.getAppId());
	}

	/**
	 * 获取小程序服务
	 *
	 * @param appId
	 * @return
	 */
	public WxOpenMaService getMaServiceByMpAppId(MpAuthShopRecord mp) {
		if (mp != null && mp.getIsAuthOk().equals(AUTH_OK)) {
			throw new IllegalArgumentException("Miniprogram is not authed!");
		}
		return open().getWxOpenComponentService().getWxMaServiceByAppid(mp.getAppId());
	}

	/**
	 * 通过appId得到小程序信息
	 *
	 * @param appId
	 * @return
	 */
	public MpAuthShopRecord getAuthShopByAppId(String appId) {
		MpAuthShopRecord fetchAny = db().fetchAny(MP_AUTH_SHOP, MP_AUTH_SHOP.APP_ID.eq(appId));
		return fetchAny;
	}


	/**
	 * 通过appId得到小程序信息，返回的图片带url
	 *
	 * @param appId
	 * @return
	 */
	public MpAuthShopRecord getAuthShopByAppIdAddUrl(String appId) {
		MpAuthShopRecord fetchAny = db().fetchAny(MP_AUTH_SHOP, MP_AUTH_SHOP.APP_ID.eq(appId));
		if(fetchAny!=null) {
			fetchAny.setQrcodeUrl(image.imageUrl(fetchAny.getQrcodeUrl()));
			fetchAny.setTestQrPath(image.imageUrl(fetchAny.getTestQrPath()));
		}
		return fetchAny;
	}
	/**
	 * 通过shopId得到小程序信息
	 *
	 * @param shopId
	 * @return
	 */
	public MpAuthShopRecord getAuthShopByShopId(Integer shopId) {
		MpAuthShopRecord fetchAny = db().fetchAny(MP_AUTH_SHOP, MP_AUTH_SHOP.SHOP_ID.eq((shopId)));
		return fetchAny;
	}


	/**
	 * 通过shopId得到小程序信息
	 *
	 * @param shopId
	 * @return
	 */
	public MpAuthShopRecord getAuthShopByShopIdAddUrl(Integer shopId) {
		MpAuthShopRecord fetchAny = db().fetchAny(MP_AUTH_SHOP, MP_AUTH_SHOP.SHOP_ID.eq((shopId)));
		if(fetchAny!=null) {
			fetchAny.setQrcodeUrl(image.imageUrl(fetchAny.getQrcodeUrl()));
			fetchAny.setTestQrPath(image.imageUrl(fetchAny.getTestQrPath()));
		}
		return fetchAny;
	}

	/**
	 * 设置小程序支付配置
	 *
	 * @param appId
	 * @param mchId       商户号
	 * @param key         支付密钥
	 * @param certContent 证书内容
	 * @param keyContent  私钥内容
	 * @return
	 */
	public int setPaymentInfo(String appId, String mchId, String key, String certContent, String keyContent) {
		return db().update(MP_AUTH_SHOP).set(MP_AUTH_SHOP.PAY_MCH_ID, mchId).set(MP_AUTH_SHOP.PAY_KEY, key)
				.set(MP_AUTH_SHOP.PAY_CERT_CONTENT, certContent).set(MP_AUTH_SHOP.PAY_KEY_CONTENT, keyContent)
				.where(MP_AUTH_SHOP.APP_ID.eq(appId)).execute();
	}

	/**
	 * 更新微信支付配置
	 *
	 * @param wxpayConfigParam
	 * @return
	 */
	public int udpateWxpayConfig(WxpayConfigParam wxpayConfigParam) {
        MpAuthShopRecord mpAuthShopRecord = db().newRecord(MP_AUTH_SHOP);
        if (wxpayConfigParam.getIsSubMerchant().equals((byte) 1)) {
            wxpayConfigParam.setPayKeyContent(null);
            wxpayConfigParam.setPayCertContent(null);
        }
        assign(wxpayConfigParam, mpAuthShopRecord);
        return mpAuthShopRecord.update();
    }

	/**
	 * 根据appid检测MpAuthShop表中数据存在性
	 * @return true存在，false不存在
	 */
	public boolean checkAuthShopExist(String appId) {
		Condition conditionAuthShop = MpAuthShop.MP_AUTH_SHOP.APP_ID.eq(appId);
        return db().fetchExists(MpAuthShop.MP_AUTH_SHOP, conditionAuthShop);
	}

	/**
	 * 查询微信支付配置
	 */
    public WxpayConfigParam getWxpayConfig(String appId) {
        Optional<WxpayConfigParam> optionalInto = db()
            .select(MP_AUTH_SHOP.APP_ID, MP_AUTH_SHOP.PAY_MCH_ID,
                MP_AUTH_SHOP.PAY_KEY, MP_AUTH_SHOP.PAY_CERT_CONTENT,
                MP_AUTH_SHOP.PAY_KEY_CONTENT)
            .from(MP_AUTH_SHOP).where(MP_AUTH_SHOP.APP_ID.eq(appId))
            .fetchOptionalInto(WxpayConfigParam.class);
        MpAuthShopRecord mpAuthShopRecord = db().fetchAny(MP_AUTH_SHOP, MP_AUTH_SHOP.APP_ID.eq(appId));
        if (mpAuthShopRecord != null) {
            WxpayConfigParam res = mpAuthShopRecord.into(WxpayConfigParam.class);
            if (mpAuthShopRecord.getIsSubMerchant().equals((byte) 1)) {
                //微信子商户
                res.setPayCertContent(null);
                res.setPayKeyContent(null);
            }
            return res;
        } else {
            return new WxpayConfigParam();
        }
    }

	/**
	 * 设置小程序服务器域名和 业务域名
	 *
	 * @param appId
	 * @return
	 * @throws WxErrorException
	 */
	public WxOpenMaDomainResult modifyDomain(String appId) throws WxErrorException {
		MpAuthShopRecord mp = this.getAuthShopByAppId(appId);
		String action = "add";
		String[] httpsDomains = { domainConfig.mainUrl("", "https") };
		String[] wssDomains = { domainConfig.mainUrl("", "wss") };
		WxOpenMaService maService = this.getMaServiceByAppId(appId);
		WxOpenMaDomainResult result=new WxOpenMaDomainResult();
		String noNewDomainCode = "85017";
		try {
			result = maService.modifyDomain(action, Arrays.asList(httpsDomains),
					Arrays.asList(wssDomains), Arrays.asList(httpsDomains), Arrays.asList(httpsDomains));
		} catch (WxErrorException e) {
			logger().info(e.getMessage(),e);
			//没有新增域名，请确认小程序已经添加了域名或该域名是否没有在第三方平台添加
			logger().info("appId:"+appId+"修改域名modifyDomain失败："+e.getError().getErrorCode()+"  "+e.getError().getErrorMsg());
			WxOpenResult fromJson =null;
			if(noNewDomainCode.equals(String.valueOf(e.getError().getErrorCode()))) {
				String setWebViewDomain = maService.setWebViewDomain(action, Arrays.asList(httpsDomains));
				fromJson = WxMaGsonBuilder.create().fromJson(setWebViewDomain, WxOpenResult.class);
				if(fromJson.isSuccess()) {
					mp.setIsModifyDomain((byte) 1);
					mp.update();
				}else {
					logger().info("appId:"+appId+"修改域名setWebViewDomain失败"+fromJson.getErrcode()+"  "+fromJson.getErrmsg());
				}
			}
			result.setErrcode(fromJson.getErrcode());
			result.setErrmsg(fromJson.getErrmsg());
			operateLogGlobal(mp, MpOperateLogService.OP_TYPE_MODIFY_DOMAIN, result, WxContentTemplate.WX_MODIFY_DOMAIN_SUCCESS.code, new String[] {});
			return result;
		}
		if (result.isSuccess()) {
			mp.setIsModifyDomain((byte) 1);
			mp.update();
		}else {
			logger().info("appId:"+appId+"修改域名modifyDomain失败"+result.getErrcode()+"  "+result.getErrmsg());
		}
		operateLogGlobal(mp, MpOperateLogService.OP_TYPE_MODIFY_DOMAIN, result, WxContentTemplate.WX_MODIFY_DOMAIN_SUCCESS.code, new String[] {});
		return result;
	}

	/**
	 * 上传代码
	 *
	 * @param appId
	 * @param templateId
	 * @param userVersion
	 * @param userDesc
	 * @return
	 * @throws WxErrorException
	 */
	public WxOpenResult uploadCode(String appId, Integer templateId)
			throws WxErrorException {
		MpVersionRecord version = saas.shop.mpVersion.getRow(templateId);
		if(null==version) {
			WxOpenResult wxOpenResult = new WxOpenResult();
			wxOpenResult.setErrcode("100050");
			wxOpenResult.setErrmsg("模板id不存在");
			return wxOpenResult;
		}
		MpAuthShopRecord mp = this.getAuthShopByAppId(appId);
		WxOpenMaService maService = this.getMaServiceByAppId(appId);
		MpWxMaOpenCommitExtInfo extInfo = new MpWxMaOpenCommitExtInfo();

		ShopRecord sRecord = saas.shop.getShopById(mp.getShopId());
		extInfo.setExtAppid(appId);
		extInfo.addExt("main_host", domainConfig.getMainDomain());
		extInfo.addExt("image_host", domainConfig.getImageDomain());
		extInfo.addExt("shop_id", mp.getShopId().toString());
		extInfo.addExt("version", templateId.toString());
		extInfo.addExt("currency", sRecord.getCurrency());
		extInfo.addExt("shopLanguage", sRecord.getShopLanguage());

		AppletsJumpService appletsJumpService = saas.getShopApp(mp.getShopId()).appletsJump;
		extInfo.setNavigateToMiniProgramAppIdList(appletsJumpService.getMpJumpAppIdList());
		//上传代码保存小程序跳转的提交的appid 版本号，appid ,状态
		appletsJumpService.saveMpJumpAppIdList(extInfo.getNavigateToMiniProgramAppIdList(), templateId);

		JsonObject params = new JsonObject();
		params.addProperty("template_id", templateId);
		params.addProperty("user_version", version.getUserVersion());
		params.addProperty("user_desc", version.getUserDesc());
		params.addProperty("ext_json", Util.toJson(extInfo));
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		JsonParser parser = new JsonParser();
		JsonElement je = parser.parse(params.toString());
		String response = maService.post(WxOpenMaService.API_CODE_COMMIT, gson.toJson(je));
		logger().info("发送的信息");
		logger().info(gson.toJson(je));
		WxOpenResult result = WxMaGsonBuilder.create().fromJson(response, WxOpenResult.class);
		logger().info("https://api.weixin.qq.com/wxa/commit返回信息");
		logger().info(result.toString());
		if(result.isSuccess()) {
			MpAuthShopRecord upMp=MP_AUTH_SHOP.newRecord();
			upMp.setAppId(appId);
			upMp.setUploadState((byte) 1);
			upMp.setBindTemplateId(templateId);
			upMp.setLastUploadTime(Timestamp.valueOf(LocalDateTime.now()));
			db().executeUpdate(upMp);
		}
		operateLogGlobal(mp, MpOperateLogService.OP_TYPE_UPLOAD_CODE, result, WxContentTemplate.WX_UPLOAD_CODE_SUCCESS.code, new String[] {});
		//更新申请发布小程序为已发布
		saas.shop.mpJumpVersion.updateMpJumpVersion(mp.getShopId());
		return result;
	}

	/**
	 * 记录操作日志
	 *
	 * @param mp
	 * @param operateType
	 * @param result
	 */
	public void operateLog(MpAuthShopRecord mp, Byte operateType, WxOpenResult result) {
		String message = result.isSuccess() ? "" : "code:" + result.getErrcode() + ", message:" + result.getErrmsg();
		Byte operateState = result.isSuccess() ? MpOperateLogService.OP_STATE_SUCCESS
				: MpOperateLogService.OP_STATE_FAILED;
		saas.shop.mpOperateLog.log(mp.getAppId(), mp.getBindTemplateId(), operateType, operateState, message);
	}

	/**
	 * 记录操作日志多语言
	 *
	 * @param mp
	 * @param operateType
	 * @param result
	 */
	public void operateLogGlobal(MpAuthShopRecord mp, Byte operateType, WxOpenResult result,Integer templateIds, String... datas) {
		Byte operateState = result.isSuccess() ? MpOperateLogService.OP_STATE_SUCCESS
				: MpOperateLogService.OP_STATE_FAILED;
		saas.shop.mpOperateLog.insertRecord(mp.getBindTemplateId(), operateType, mp.getAppId(), operateState, templateIds, datas);
	}

	/**
	 * 绑定体验者
	 *
	 * @param appId
	 * @param wechatId
	 * @return
	 * @throws WxErrorException
	 */
	public WxOpenResult bindTester(String appId, String wechatId) throws WxErrorException {
		logger().info("绑定体验者开始···································");
		MpAuthShopRecord mp = this.getAuthShopByAppId(appId);
		List<String> testers = StringUtils.isBlank(mp.getTester()) ? new ArrayList<>()
				: Util.parseJson(mp.getTester(), new TypeReference<List<String>>() {
				});
		WxOpenMaService maService = this.getMaServiceByAppId(appId);
		WxOpenResult result = maService.bindTester(wechatId);
		logger().info("绑定体验者"+result);
		logger().info(result.getErrcode()+result.getErrmsg());
		if (result.isSuccess()) {
			testers.add(wechatId);
			mp.setTester(Util.toJson(testers));
			mp.update();
		}
		//绑定测试者
		operateLogGlobal(mp, MpOperateLogService.OP_TYPE_ADD_TESTER, result, WxContentTemplate.WX_BIND_TESTER_SUCCESS.code, new String[] {wechatId});
		return result;
	}

	public String getMpQrCode(String appId, WxOpenAuthorizerInfo authorizerInfo) {
		String path = "pages/bottom/bottom";
		String filename = appId + "_" + Util.md5(path) + ".jpg";
		String relativePath = "upload/saas/mp/app_code/" + filename;
		Boolean addImgeToUp=false;
		try {
			byte[] createWxaCodeBytes = open().getWxOpenComponentService().getWxMaServiceByAppid(appId).getQrcodeService().createWxaCodeBytes(path, 430, true, null, false);
			addImgeToUp = saas.sysImage.uploadToUpYunByByte(relativePath, createWxaCodeBytes);
		} catch (WxErrorException e) {
			logger().error("appId" + appId +"获取小程序二维码失败");
			e.printStackTrace();
		} catch (Exception e) {
			logger().error("appId" + appId + "头像上传又拍云失败");
			e.printStackTrace();
		}
		logger().info("appId" + appId + "头像上传又拍云" + addImgeToUp);
		return relativePath;
	}

	/**
	 * 解除绑定
	 *
	 * @param appId
	 * @param wechatId
	 * @return
	 * @throws WxErrorException
	 */
	public WxOpenResult unbindTester(String appId, String wechatId) throws WxErrorException {
		MpAuthShopRecord mp = this.getAuthShopByAppId(appId);
		List<String> testers = StringUtils.isBlank(mp.getTester()) ? new ArrayList<>()
				: Util.parseJson(mp.getTester(), new TypeReference<List<String>>() {
				});
		if (!testers.contains(wechatId)) {
			return successResult(wechatId);
		}
		WxOpenMaService maService = this.getMaServiceByAppId(appId);
		WxOpenResult result = maService.unbindTester(wechatId);
		if (result.isSuccess()) {
			testers.remove(wechatId);
			mp.setTester(Util.toJson(testers));
			mp.update();
		}
		operateLogGlobal(mp, MpOperateLogService.OP_TYPE_DEL_TESTER, result, WxContentTemplate.WX_DEL_TESTER_SUCCESS.code, new String[] {wechatId});
		return result;
	}

	/**
	 * 正确结果
	 *
	 * @return
	 */
	public WxOpenResult successResult(String message) {
		WxOpenResult result = new WxOpenResult();
		result.setErrcode("0");
		result.setErrmsg(message);
		return result;
	}

	/**
	 * 自定义错误消息
	 *
	 * @param message
	 * @return
	 */
	public WxOpenResult failResult(String message) {
		WxOpenResult result = new WxOpenResult();
		result.setErrcode("-2");
		result.setErrmsg(message);
		return result;
	}

	/**
	 * 得到体验码
	 *
	 * @param appId
	 * @throws WxErrorException
	 * @throws IOException
	 * @throws Exception
	 */
	public WxOpenResult getTestQrCode(String appId) throws WxErrorException, IOException {
		MpAuthShopRecord mp = this.getAuthShopByAppId(appId);
		WxOpenMaService maService = this.getMaServiceByAppId(appId);
		String pagePath = "pages/bottom/bottom";
		File file = maService.getTestQrcode(pagePath, null);
		String relativePath = "upload/saas/qr/" + appId + ".jpg";
		WxOpenResult result=new WxOpenResult();
		try {
			image.uploadToUpYun(relativePath, file);
		} catch (Exception e) {
			result.setErrcode("-2");
			result.setErrmsg(e.getMessage());
			operateLogGlobal(mp, MpOperateLogService.OP_TYPE_GET_TESTER_QR, result, WxContentTemplate.WX_GET_TESTER_QR_FAIL.code, new String[] {"-2",e.getMessage()});
			return failResult(e.getMessage());
		}
		mp.setTestQrPath(relativePath);
		mp.update();
		operateLogGlobal(mp, MpOperateLogService.OP_TYPE_GET_TESTER_QR, result, WxContentTemplate.WX_GET_TESTER_QR_SUCCESS.code, new String[] {});
		return successResult(relativePath);
	}

	/**
	 * 得到可选类目
	 *
	 * @param appId
	 * @return
	 * @throws WxErrorException
	 */
	public WxOpenMaCategoryListResult getCategory(String appId) throws WxErrorException {
		WxOpenMaService maService = this.getMaServiceByAppId(appId);
		MpAuthShopRecord mp = this.getAuthShopByAppId(appId);
		WxOpenMaCategoryListResult result = maService.getCategoryList();
		if (result.isSuccess()) {
			mp.setCategory(Util.toJson(result.getCategoryList()));
			mp.update();
		}
		operateLogGlobal(mp, MpOperateLogService.OP_TYPE_GET_CATEGORY, result, WxContentTemplate.WX_GET_CATEGORY_SUCCESS.code, new String[] {});
		return result;
	}

	/**
	 * 得到小程序页面配置列表
	 *
	 * @param appId
	 * @return
	 * @throws WxErrorException
	 */
	public WxOpenMaPageListResult getPage(String appId) throws WxErrorException {
		MpAuthShopRecord mp = this.getAuthShopByAppId(appId);
		WxOpenMaService maService = this.getMaServiceByAppId(appId);
		WxOpenMaPageListResult result = maService.getPageList();
		if (result.isSuccess()) {
			mp.setPageCfg(Util.toJson(result.getPageList()));
			mp.update();
		}
		//更新部署日志
		updateDeployData(result.getPageList(), appId);
		operateLogGlobal(mp, MpOperateLogService.OP_TYPE_GET_PAGE_CFG, result, WxContentTemplate.WX_GET_PAGE_CFG_SUCCESS.code, new String[] {});
		return result;
	}

	/**
	 * 提交审核
	 *
	 * @param appId
	 * @return
	 * @throws WxErrorException
	 */
	public WxOpenMaSubmitAuditResult submitAudit(String appId) throws WxErrorException {
		MpAuthShopRecord mp = this.getAuthShopByAppId(appId);
		WxOpenMaService maService = this.getMaServiceByAppId(appId);

		WxOpenMaSubmitAudit audit = new WxOpenMaSubmitAudit();
		List<WxOpenMaCategory> categoryList = null;
		//获取授权小程序帐号的可选类目 first_class  first_id
		if (!StringUtils.isBlank(mp.getCategory())) {
			categoryList = Util.parseJson(mp.getCategory(), new TypeReference<List<WxOpenMaCategory>>() {
			});
		} else {
			WxOpenMaCategoryListResult category = this.getCategory(appId);
			categoryList = category.getCategoryList();
		}
		//获取小程序的第三方提交代码的页面配置  address
		List<String> pageCfg=new ArrayList<String>();
		pageCfg.add("pages/bottom/bottom");
		if (!StringUtils.isBlank(mp.getPageCfg())) {
			pageCfg = Util.parseJson(mp.getPageCfg(), new TypeReference<List<String>>() {
			});
		}else {
			WxOpenMaPageListResult page = this.getPage(appId);
			pageCfg=page.getPageList();
		}
		String pagePath = pageCfg.get(0);
		audit.setPagePath(pagePath);
		audit.setFirstClass(categoryList.get(0).getFirstClass());
		audit.setTag(categoryList.get(0).getFirstClass());
		audit.setTitle("首页");
		audit.setFirstId(categoryList.get(0).getFirstId());
		audit.setSecondClass(categoryList.get(0).getSecondClass());
		audit.setSecondId(categoryList.get(0).getSecondId());
		audit.setThirdId(categoryList.get(0).getThirdId());
		audit.setThirdClass(categoryList.get(0).getThirdClass());
		WxOpenMaSubmitAuditMessage submitAuditMessage = new WxOpenMaSubmitAuditMessage();
		List<WxOpenMaSubmitAudit> itemList = new ArrayList<>();
		itemList.add(audit);
		submitAuditMessage.setItemList(itemList);
		WxOpenMaSubmitAuditResult result = maService.submitAudit(submitAuditMessage);
		if(result.isSuccess()) {
			//更新数据库字段
			MpAuthShopRecord upMp = MP_AUTH_SHOP.newRecord();
			upMp.setAppId(appId);
			//审核中
			upMp.setAuditState((byte)1);
			upMp.setAuditId(result.getAuditId());
			upMp.setSubmitAuditTime(new Timestamp(System.currentTimeMillis()));
			db().executeUpdate(upMp);
		}
		operateLogGlobal(mp, MpOperateLogService.OP_TYPE_SUBMIT_AUDIT, result, WxContentTemplate.WX_SUBMIT_AUDIT_SUCCESS.code, new String[] {});
		return result;
	}

	/**
	 * 发布审核成功代码
	 *
	 * @param appId
	 * @return
	 * @throws WxErrorException
	 */
	public WxOpenResult publishAuditSuccessCode(String appId) throws WxErrorException {
		MpAuthShopRecord mp = this.getAuthShopByAppId(appId);
		WxOpenResult result=new WxOpenResult();
		if (mp.getAuditId() != null && mp.getAuditState().equals(AUDIT_STATE_AUDIT_SUCCESS)) {
			//审核成功的代码
			WxOpenMaService maService = this.getMaServiceByAppId(appId);
			result = maService.releaesAudited();
			if(result.isSuccess()) {
				//更新数据库
				updatePush(appId);
			}
			operateLogGlobal(mp, MpOperateLogService.OP_TYPE_PUBLISH_CODE, result, WxContentTemplate.WX_PUBLISH_CODE_SUCCESS.code, new String[] {});
			return result;
		}
		result.setErrcode(JsonResultCode.WX_MA_NEED_AUDITING_CODE_SUCCESS.toString());
		//请等待代码审核成功或未上传代码
		result.setErrmsg(JsonResultMessage.WX_MA_NEED_AUDITING_CODE_SUCCESS);
		operateLogGlobal(mp, MpOperateLogService.OP_TYPE_PUBLISH_CODE, result, WxContentTemplate.WX_PUBLISH_CODE_SUCCESS.code, new String[] {result.getErrcode(),result.getErrmsg()});
		return result;
	}

	/**
	 * 上传代码并提交审核
	 *
	 * @param appId
	 * @param templateId
	 * @param userVersion
	 * @param userDesc
	 * @return
	 * @throws WxErrorException
	 */
	public WxOpenResult uploadCodeAndApplyAudit(String appId, Integer templateId)
			throws WxErrorException {
		WxOpenResult result = this.modifyDomain(appId);
		if (result.isSuccess()) {
			result = this.uploadCode(appId, templateId);
			if (result.isSuccess()) {
				result = this.submitAudit(appId);
			}
		}
		return result;
	}

	/**
	 * 批量上传代码并提交审核
	 *
	 * @param templateId
	 * @param userVersion
	 * @param userDesc
	 * @throws WxErrorException
	 */
	public JsonResultCode batchUploadCodeAndApplyAudit(Integer templateId){

		MpVersionRecord row = saas.shop.mpVersion.getRow(templateId);
		List<Byte> asList = Arrays.asList(TaskJobsConstant.STATUS_NEW,TaskJobsConstant.STATUS_EXECUTING);
		int recId = saas.shop.backProcessService.insertByInfo(row.into(MpVersionVo.class), this.getClass().getName(), 0);
		Boolean boolean1 = saas.taskJobMainService.assertHasStatusTaskJob(TaskJobEnum.BATCH_UPLOAD.getExecutionType(), asList);
		if(boolean1) {
			//已经有相同任务在运行，当前任务停止
			saas.shop.backProcessService.fail(recId, "已经有相同任务在运行，当前任务停止");
			return JsonResultCode.WX_ONLY_ONE;
		}
		MpAuthShopListParam param = new MpAuthShopListParam();
		param.setIsAuthOk((byte) 1);
		param.setAuditState((byte) 1);
		List<MpAuthShopListVo> mpList = getCanSubmitAuditMps(param,param.buildOptionByUpload()).fetchInto(MpAuthShopListVo.class);
		if(mpList.size()==0) {
			//没有要提交的
			saas.shop.backProcessService.fail(recId, "没有符合要求的小程序");
			return JsonResultCode.WX_NO_REQUIRED;
		}else {
			Integer currentUseTemplateId = saas.shop.mpVersion.getCurrentUseTemplateId(null,row.getPackageVersion());
			BatchUploadCodeParam param1=new BatchUploadCodeParam();
			param1.setList(mpList);
			param1.setRecId(recId);
			param1.setTemplateId(currentUseTemplateId);
			param1.setPackageVersion(row.getPackageVersion());
			Integer mainId = saas.taskJobMainService.dispatchImmediately(param1,BatchUploadCodeParam.class.getName(),0,TaskJobEnum.BATCH_UPLOAD.getExecutionType());
			logger().info("获取的任务id为"+mainId);
			saas.shop.backProcessService.updateProcessId(recId, mainId);
			return JsonResultCode.CODE_SUCCESS;
		}
	}

	/**
	 * 更新小程序审核状态,只有审核中，才可以获取最后审核状态
	 *
	 * @param appId
	 * @return
	 * @throws WxErrorException
	 */
	public WxOpenMaQueryAuditResult refreshAppInfo(String appId) throws WxErrorException {
		MpAuthShopRecord mp = this.getAuthShopByAppId(appId);
		WxOpenMaQueryAuditResult result=new WxOpenMaQueryAuditResult();
		if (mp.getAuditId() != null && mp.getAuditState().equals(AUDIT_STATE_AUDITING)) {
			WxOpenMaService maService = this.getMaServiceByAppId(appId);
			result = maService.getAuditStatus(mp.getAuditId());
			if (result.isSuccess()) {
				if (result.getStatus().equals(WX_AUTH_STATUS_PASSED)) {
					mp.setAuditState(AUDIT_STATE_AUDIT_SUCCESS);
					mp.setAuditOkTime(Timestamp.valueOf(LocalDateTime.now()));
					mp.update();
				} else if (result.getStatus().equals(WX_AUTH_STATUS_AUDIT_FAILED)) {
					mp.setAuditState(AUDIT_STATE_AUDIT_FAILED);
					mp.setAuditFailReason(result.getReason());
					mp.update();
				}
			}
			if(result.getStatus().equals(WX_AUTH_STATUS_AUDITING)) {
				//状态成功没有返回reason
				operateLogGlobal(mp, MpOperateLogService.OP_TYPE_REFRESH_AUDIT_STATE, result, WxContentTemplate.WX_REFRESH_AUDIT_STATE_SUCCESSON2.code, new String[] {String.valueOf(result.getStatus())});
			}else {
				operateLogGlobal(mp, MpOperateLogService.OP_TYPE_REFRESH_AUDIT_STATE, result, WxContentTemplate.WX_REFRESH_AUDIT_STATE_SUCCESS.code, new String[] {String.valueOf(result.getStatus()),result.getReason()});
			}
			return result;
		}
		//尚未上传代码
		result.setErrcode(JsonResultCode.WX_MA_NEED_UPLOADCODE.toString());
		result.setErrmsg(JsonResultMessage.WX_MA_NEED_UPLOADCODE);
		operateLogGlobal(mp, MpOperateLogService.OP_TYPE_REFRESH_AUDIT_STATE, result, WxContentTemplate.WX_REFRESH_AUDIT_STATE_FAIL.code, new String[] {result.getErrcode(),result.getErrmsg()});
		return result;
	}

    /**
     *  根据店铺id获取小程序审核状态
     * @param shopId 店铺id
     * @return  小程序审核信息
     */
	public MpAuditStateVo getAppAuditInfo(Integer shopId){

        MpAuthShopRecord mp = this.getAuthShopByShopId(shopId);
        MpAuditStateVo mpAuditStateVo = new MpAuditStateVo();
        mpAuditStateVo.setAppId(mp.getAppId());
        mpAuditStateVo.setAuditId(mp.getAuditId());
        mpAuditStateVo.setAuditState(mp.getAuditState());
        mpAuditStateVo.setSubmitAuditTime(mp.getSubmitAuditTime());
        mpAuditStateVo.setAuditOkTime(mp.getAuditOkTime());
        mpAuditStateVo.setAuditFailReason(mp.getAuditFailReason());

        return mpAuditStateVo;
    }


	public WxOpenMaQueryAuditResult getLatestAuditStatus(String appId) throws WxErrorException {
		WxOpenMaService maService = this.getMaServiceByAppId(appId);
		WxOpenMaQueryAuditResult latestAuditStatus = maService.getLatestAuditStatus();
		if(latestAuditStatus.isSuccess()) {
			return latestAuditStatus;
		}
		return latestAuditStatus;
	}

	/**
	 * 绑定同一主体的小程序和公众号到开放平台账号
	 * @param record
	 * @throws WxErrorException
	 */
	public void bindAllSamePrincipalOpenAppId(MpAuthShopRecord record) throws WxErrorException {
		Result<MpAuthShopRecord> samePrincipalMpApps = getSamePrincipalMpApps(record.getPrincipalName());
		String openAppId = null;
		// 遍历所有主体相同的号，查找不为空的openAppId
		for (MpAuthShopRecord mShopRecord : samePrincipalMpApps) {
			if (!StringUtils.isEmpty(mShopRecord.getBindOpenAppId())) {
				openAppId = mShopRecord.getBindOpenAppId();
				break;
			}
		}
		for (MpAuthShopRecord mRecord : samePrincipalMpApps) {
			openAppId = bindOpenAppId(true,mRecord.getAppId(), openAppId);
			if (!openAppId.equals(mRecord.getBindOpenAppId())) {
				// 更新数据库
				mRecord.setBindOpenAppId(openAppId);
				db().executeUpdate(mRecord);
			}
		}
	}

	public int updateBindOpenAppId(String appId, String bindAppId) {
		return db().update(MP_AUTH_SHOP).set(MP_AUTH_SHOP.BIND_OPEN_APP_ID, bindAppId)
				.where(MP_AUTH_SHOP.APP_ID.eq(appId)).execute();
	}

	/**
	 * 小程序未绑定微信开放平台帐号
	 */
	private static final int   WECHAT_OPEN_PLATFORM_ACCOUNT_NOT_BOUND =89002;
	/**
	 * 用接口绑定小程序或者公众号appId到开放平台账号
	 *
	 * @param isMa     是否是小程序
	 * @param appId     小程序或者公众号appId
	 * @param openAppId 开放平台账号，为空需要新创建
	 * @return
	 * @throws WxErrorException
	 */
	public String bindOpenAppId(Boolean isMa,String appId, String openAppId) throws WxErrorException {
		WxOpenAccountService service = isMa ? open.getMaExtService() : open.getMpExtService();
		WxOpenGetResult result = null;
		try {
			result = service.getOpenAccount(appId);
			if (StringUtils.isEmpty(openAppId)) {
				return result.getOpenAppid();
			}

			if (!openAppId.equals(result.getOpenAppid())) {
				// 如果openAppId不相同，解绑原openAppId，并绑定新的openAppId
				service.unbindOpenAppId(appId, result.getOpenAppid());
				service.bindOpenAppId(appId, openAppId);
			}
			return openAppId;
		} catch (WxErrorException e) {
			if (e.getError().getErrorCode() == WECHAT_OPEN_PLATFORM_ACCOUNT_NOT_BOUND) {

				// 该公众号/小程序未绑定微信开放平台帐号
				// 如果openAppId为空，则创建开放平台账号，否则绑定当前平台账号
				if (StringUtils.isEmpty(openAppId)) {
					WxOpenCreateResult openCreateResult = service.createOpenAccount(appId);
					return openCreateResult.getOpenAppid();
				} else {
					service.bindOpenAppId(appId, openAppId);
					return openAppId;
				}
			} else {
				throw new WxErrorException(e.getError(), e);
			}
		}
	}

	/**
	 * 查询主体名称相同的
	 *
	 * @param principalName
	 * @return
	 */
	public Result<MpAuthShopRecord> getSamePrincipalMpApps(String principalName) {
		return db().fetch(MP_AUTH_SHOP,
				MP_AUTH_SHOP.PRINCIPAL_NAME.eq(principalName).and(MP_AUTH_SHOP.IS_AUTH_OK.eq((byte) 1)));
	}

	/**
	 * 小程序
	 * @param principalName
	 * @return
	 */
	public List<MaMpBindParam> getSamePrincipalMaList(String principalName) {
		Result<Record3<String, String, String>> fetch = db()
				.select(MP_AUTH_SHOP.APP_ID, MP_AUTH_SHOP.BIND_OPEN_APP_ID,DSL.field("2", String.class).as("type")).from(MP_AUTH_SHOP)
				.where(MP_AUTH_SHOP.PRINCIPAL_NAME.eq(principalName)
						.and(MP_AUTH_SHOP.IS_AUTH_OK.eq((byte) 1)))
				.fetch();
		List<MaMpBindParam> into =new ArrayList<MaMpBindParam>();
		if(fetch!=null) {
			into = fetch.into(MaMpBindParam.class);
		}
		return into;
	}

    /**
     * 获取小程序授权列表
     *
     * @param param 过滤参数
     * @return 分页内容
     */
    public PageResult<MpAuthShopListVo> getAuthList(MpAuthShopListParam param) {
        SelectConditionStep<Record13<String, Integer, String, String, Byte, String, Byte, Timestamp, Integer, Byte, Byte, Integer, Timestamp>> select =getCanSubmitAuditMps(param,param.buildOption());
        return this.getPageResult(select, param.getCurrentPage(), param.getPageRows(), MpAuthShopListVo.class);
    }


    public SelectConditionStep<Record13<String, Integer, String, String, Byte, String, Byte, Timestamp, Integer, Byte, Byte, Integer, Timestamp>> getCanSubmitAuditMps(MpAuthShopListParam param, Condition condition) {
    	  String shopFieldName=SHOP.SHOP_ID.getName();
          String expireFieldName=SHOP.EXPIRE_TIME.getName();

          Table<Record2<Integer, Timestamp>> nested =
              db().select(SHOP.SHOP_ID.as(shopFieldName),
                  (SHOP.EXPIRE_TIME).as(expireFieldName))
                  .from(SHOP).asTable("nested");

          Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

          if (param.getShopState() != null && param.getShopState() == 0) {
              condition = condition.and(nested.field(expireFieldName, Timestamp.class).lt(Timestamp.valueOf(LocalDateTime.now())));
          }

          if (param.getShopState() != null && param.getShopState() == 1) {
              condition = condition.and(nested.field(expireFieldName, Timestamp.class).ge(Timestamp.valueOf(LocalDateTime.now())));
          }

           return db().select(MP_AUTH_SHOP.APP_ID, MP_AUTH_SHOP.SHOP_ID, MP_AUTH_SHOP.NICK_NAME, MP_AUTH_SHOP.HEAD_IMG,
              MP_AUTH_SHOP.IS_AUTH_OK, MP_AUTH_SHOP.VERIFY_TYPE_INFO, MP_AUTH_SHOP.OPEN_PAY, MP_AUTH_SHOP.LAST_AUTH_TIME,
              MP_AUTH_SHOP.BIND_TEMPLATE_ID, MP_AUTH_SHOP.AUDIT_STATE, MP_AUTH_SHOP.PUBLISH_STATE,
              DSL.when(nested.field(expireFieldName, Timestamp.class).lt(timestamp), 0).otherwise(1).as("shopState"),
              MP_AUTH_SHOP.CREATE_TIME)
              .from(MP_AUTH_SHOP).leftJoin(nested).on(nested.field(shopFieldName, Integer.class).eq(MP_AUTH_SHOP.SHOP_ID))
              .where(condition);
    }

    /**
     * 	获得小程序发版版本  1 正常版本 2 好物推荐版本 3 直播普通 4 直播好物
     * @param appId
     * @return
     */
	public Byte getMpPackageVersion(String appId) {
//		Byte plugin = getPlugin(appId);
		logger().info("小程序：{}的版本为：{}", appId, "默认正常版本");
		return 1;
	}

	/**
	 * 	获得插件
	 * @param appId
	 * @return
	 */
	public Byte getPlugin(String appId) {
		WxOpenAccountService service =open.getMaExtService();
    	Map<String, String> map=new HashMap<String, String>(0);
    	map.put("action", "list");
    	MaWxPlusInResult plugInManage=null;
    	try {
			plugInManage = service.plugInManage(appId, map);
		} catch (WxErrorException e) {
			e.printStackTrace();
		}
    	//好物圈的appId
		String haoWu = "wx56c8f077de74b07c";
		//直播的appId
		String live = "wx2b03c6e691cd7370";
		boolean hasGoodsShipping=false;
		boolean hasLive=false;
        String errorCodeSuccess = "0";
        String errorMessageOk = "ok";
        if(plugInManage.getErrcode().equals(errorCodeSuccess)&&plugInManage.getErrmsg().equals(errorMessageOk)) {
    		 List<MaWxPlusInListInner> pluginList = plugInManage.getPluginList();
    		for(MaWxPlusInListInner inner:pluginList) {
    		    // 审核通过
                String statusAuditOk = "2";
                if (inner.getAppid().equals(haoWu) && inner.getStatus().equals(statusAuditOk)) {
					hasGoodsShipping=true;
					logger().info("小程序：{}，有好物圈权限",appId);
    				continue;
    			}
				if (inner.getAppid().equals(live) && inner.getStatus().equals(statusAuditOk)) {
					boolean checkHasLive = checkHasLive(getAuthShopByAppId(appId));
					if(checkHasLive) {
						logger().info("小程序：{}，有直播权限",appId);
						hasLive=true;
					}
    			}
    		}
    	}
    	if(hasGoodsShipping&&hasLive) {
    		return 4;
    	}if(hasLive) {
    		return 3;
    	}if(hasGoodsShipping) {
    		return 2;
    	}
		return 1;
	}


	/**
	 * 更新部署日志
	 */
	public void updateDeployData(List<String> pageList,String appId) {
		Byte templateId = getMpPackageVersion(appId);
		Integer tempId=Integer.parseInt(templateId.toString());
		MpDeployHistoryRecord deployInfo = saas().deployHistoryService.getDeployInfo(appId, tempId);
		if(deployInfo!=null) {
			saas().deployHistoryService.addRow(appId, tempId);
		}
		saas().deployHistoryService.update(appId, tempId, pageList);

	}

	public void updatePush(String appId) {
		MpAuthShopRecord newRecord = MP_AUTH_SHOP.newRecord();
		newRecord.setAppId(appId);
		newRecord.setPublishState((byte) 1);
		newRecord.setPublishTime(new Timestamp(System.currentTimeMillis()));
		db().executeUpdate(newRecord);

	}

	/**
	 * 设置支付方式
	 */
	public WxOpenResult setSubMerchant(MpDeployQueryParam param) {
		MpAuthShopRecord mp = this.getAuthShopByAppId(param.getAppId());
		WxOpenResult wxOpenResult = new WxOpenResult();
		wxOpenResult.setErrcode(String.valueOf(JsonResultCode.CODE_FAIL));
		wxOpenResult.setErrmsg(String.valueOf(JsonResultMessage.MSG_FAIL));
		if (mp == null) {
			return wxOpenResult;
		}
		// TODO 生成证书
		/**
		 * "0"：微信直连支付 "1"：微铺宝子商户支付 "2"：通联子商户支付 "3"：微信国际融合钱包支付
		 */
		if (param.getIsSubMerchant() == null) {
			// TODO 返回字段为空
			wxOpenResult.setErrcode(String.valueOf(JsonResultCode.WX_MA_ISSUBMERCHANT_ISNULL));
			wxOpenResult.setErrmsg(String.valueOf(JsonResultMessage.WX_MA_ISSUBMERCHANT_ISNULL));
			return wxOpenResult;
		}
		switch (param.getIsSubMerchant()) {
		case 0:
			// 微信直连支付
            setWxPayConfig(param, mp, wxOpenResult);
            break;
		case 1:
			// 微铺宝子商户支付
            setWxPaySubMerchantConfig(param, mp, wxOpenResult);
            break;
		case 2:
			// 通联子商户支付
            if (setUnionPayConfig(param, mp, wxOpenResult)) {
                return wxOpenResult;
            }
            break;
		case 3:
			// 微信国际融合钱包支付 [, 'merchant_category_code', 'fee_type']
            if (setWxInternationalPayConfig(param, mp, wxOpenResult)) {
                return wxOpenResult;
            }
            break;

		default:
			break;
		}
		if(wxOpenResult.getErrcode().equals(String.valueOf(JsonResultCode.CODE_SUCCESS))) {
			operateLogGlobal(mp, MpOperateLogService.OP_TYPE_SETTING_SUB_MERCHANT, wxOpenResult, WxContentTemplate.WX_SETTING_SUB_MERCHANT_SUCCESS.code, new String[] {String.valueOf(param.getIsSubMerchant())});
		}
		return wxOpenResult;
	}

    private boolean setWxInternationalPayConfig(MpDeployQueryParam param, MpAuthShopRecord mp, WxOpenResult wxOpenResult) {
        if (StringUtils.isEmpty(param.getMerchantCategoryCode()) || StringUtils.isEmpty(param.getFeeType())) {
            wxOpenResult.setErrcode(String.valueOf(JsonResultCode.WX_MA_TABLE_ISNULL));
            wxOpenResult.setErrmsg(JsonResultMessage.WX_MA_TABLE_ISNULL);
            operateLogGlobal(mp, MpOperateLogService.OP_TYPE_SETTING_SUB_MERCHANT, wxOpenResult, WxContentTemplate.WX_TABLE_ISNULL.code,new String[] {String.valueOf(param.getIsSubMerchant())});
            return true;
        }
        int execute2 = db().update(MP_AUTH_SHOP)
                .set(MP_AUTH_SHOP.MERCHANT_CATEGORY_CODE, param.getMerchantCategoryCode())
                .set(MP_AUTH_SHOP.FEE_TYPE, param.getFeeType())
                .set(MP_AUTH_SHOP.IS_SUB_MERCHANT, param.getIsSubMerchant().byteValue()).where(MP_AUTH_SHOP.APP_ID.eq(param.getAppId())).execute();
        if (execute2 > 0) {
            wxOpenResult.setErrcode(String.valueOf(JsonResultCode.CODE_SUCCESS));
            wxOpenResult.setErrmsg(JsonResultMessage.MSG_SUCCESS);
        }
        return false;
    }

    private boolean setUnionPayConfig(MpDeployQueryParam param, MpAuthShopRecord mp, WxOpenResult wxOpenResult) {
        if (StringUtils.isEmpty(param.getUnionPayAppId()) || StringUtils.isEmpty(param.getUnionPayCusId())
                || StringUtils.isEmpty(param.getUnionPayAppKey())) {
            wxOpenResult.setErrcode(String.valueOf(JsonResultCode.WX_MA_TABLE_ISNULL));
            wxOpenResult.setErrmsg(JsonResultMessage.WX_MA_TABLE_ISNULL);
            operateLogGlobal(mp, MpOperateLogService.OP_TYPE_SETTING_SUB_MERCHANT, wxOpenResult, WxContentTemplate.WX_TABLE_ISNULL.code, new String[] {String.valueOf(param.getIsSubMerchant())});
            return true;
        }
        int execute = db().update(MP_AUTH_SHOP).set(MP_AUTH_SHOP.UNION_PAY_APP_ID, param.getUnionPayAppId())
                .set(MP_AUTH_SHOP.UNION_PAY_CUS_ID, param.getUnionPayCusId())
                .set(MP_AUTH_SHOP.UNION_PAY_APP_KEY, param.getUnionPayAppKey())
                .set(MP_AUTH_SHOP.IS_SUB_MERCHANT, param.getIsSubMerchant().byteValue()).where(MP_AUTH_SHOP.APP_ID.eq(param.getAppId())).execute();
        if (execute > 0) {
            wxOpenResult.setErrcode(String.valueOf(JsonResultCode.CODE_SUCCESS));
            wxOpenResult.setErrmsg(JsonResultMessage.MSG_SUCCESS);
        }
        return false;
    }

    private void setWxPaySubMerchantConfig(MpDeployQueryParam param, MpAuthShopRecord mp, WxOpenResult wxOpenResult) {
        int execute4 = db().update(MP_AUTH_SHOP).set(MP_AUTH_SHOP.IS_SUB_MERCHANT,AUDIT_STATE_AUDITING).where(MP_AUTH_SHOP.APP_ID.eq(param.getAppId())).execute();
        Integer templateIds2= WxContentTemplate.WX_SETTING_SUB_MERCHANT_FAIL.code;
        if(execute4>0) {
            templateIds2=WxContentTemplate.WX_SETTING_SUB_MERCHANT_SUCCESS.code;
        }
        operateLogGlobal(mp, MpOperateLogService.OP_TYPE_SETTING_SUB_MERCHANT, wxOpenResult, templateIds2, new String[] {String.valueOf(param.getIsSubMerchant())});
    }

    private void setWxPayConfig(MpDeployQueryParam param, MpAuthShopRecord mp, WxOpenResult wxOpenResult) {
        int execute3 = db().update(MP_AUTH_SHOP).set(MP_AUTH_SHOP.IS_SUB_MERCHANT,AUDIT_STATE_NO_SUBMIT).where(MP_AUTH_SHOP.APP_ID.eq(param.getAppId())).execute();
        Integer templateIds= WxContentTemplate.WX_SETTING_SUB_MERCHANT_FAIL.code;
        if(execute3>0) {
            templateIds=WxContentTemplate.WX_SETTING_SUB_MERCHANT_SUCCESS.code;
        }
        operateLogGlobal(mp, MpOperateLogService.OP_TYPE_SETTING_SUB_MERCHANT, wxOpenResult, templateIds, new String[] {String.valueOf(param.getIsSubMerchant())});
    }

    /**
	 * 将抛错信息存入log表
	 * @param act
	 * @param result
	 */
	public void erroInsert(MpDeployQueryParam param,WxOpenResult result){
		MpAuthShopRecord mp = this.getAuthShopByAppId(param.getAppId());
		String errorCode500 = "500";
		if(result.getErrcode().equals(errorCode500)) {
			//error错误
			operateLogGlobal(mp, MpOperateLogService.OP_STATE_FAILED, result, WxContentTemplate.WX_ERROE.code, new String[] {param.getAct(),result.getErrcode(),result.getErrmsg()});
		}
		switch (param.getAct()) {
		case MpDeployQueryParam.ACT_ADD_TESTER://绑定体验者
			operateLogGlobal(mp, MpOperateLogService.OP_TYPE_ADD_TESTER, result, WxContentTemplate.WX_BIND_TESTER_FAIL.code, new String[] {param.getWechatId(),result.getErrcode(),result.getErrmsg()});
			break;
		case MpDeployQueryParam.ACT_DEL_TESTER:// 删除体验者
			operateLogGlobal(mp, MpOperateLogService.OP_TYPE_DEL_TESTER, result, WxContentTemplate.WX_DEL_TESTER_FAIL.code, new String[] {param.getWechatId(),result.getErrcode(),result.getErrmsg()});
			break;
		case MpDeployQueryParam.ACT_GET_CATEGORY://获取可选类目
			operateLogGlobal(mp, MpOperateLogService.OP_TYPE_GET_CATEGORY, result, WxContentTemplate.WX_GET_CATEGORY_FAIL.code, new String[] {result.getErrcode(),result.getErrmsg()});
			break;
		case MpDeployQueryParam.ACT_GET_PAGE_CFG://获取页面配置
			operateLogGlobal(mp, MpOperateLogService.OP_TYPE_GET_PAGE_CFG, result, WxContentTemplate.WX_GET_PAGE_CFG_FAIL.code, new String[] {result.getErrcode(),result.getErrmsg()});
			break;
		case MpDeployQueryParam.ACT_GET_TESTER_QR://获取体验者二维码
			operateLogGlobal(mp, MpOperateLogService.OP_TYPE_GET_TESTER_QR, result, WxContentTemplate.WX_GET_NO_RECORD_FAIL.code, new String[] {result.getErrcode(),result.getErrmsg()});
			break;
		case MpDeployQueryParam.ACT_MODIFY_DOMAIN:// 设置服务器域名
			operateLogGlobal(mp, MpOperateLogService.OP_TYPE_MODIFY_DOMAIN, result, WxContentTemplate.WX_MODIFY_DOMAIN_FAIL.code, new String[] {result.getErrcode(),result.getErrmsg()});
			break;
		case MpDeployQueryParam.ACT_PUBLISH_CODE://发布代码
			operateLogGlobal(mp, MpOperateLogService.OP_TYPE_PUBLISH_CODE, result, WxContentTemplate.WX_PUBLISH_CODE_FAIL.code, new String[] {result.getErrcode(),result.getErrmsg()});
			break;
		case MpDeployQueryParam.ACT_SUBMIT_AUDIT://提交审核
			operateLogGlobal(mp, MpOperateLogService.OP_TYPE_SUBMIT_AUDIT, result, WxContentTemplate.WX_SUBMIT_AUDIT_FAIL.code, new String[] {result.getErrcode(),result.getErrmsg()});
			break;
		case MpDeployQueryParam.ACT_UPDATE_MP://更新小程序信息
			operateLogGlobal(mp, MpOperateLogService.OP_TYPE_UPDATE_MP, result, WxContentTemplate.WX_UPDATE_MP_FAIL.code, new String[] {result.getErrcode(),result.getErrmsg()});
			break;
		case MpDeployQueryParam.ACT_REFRESH_AUDIT_STATE:// 刷新审核状态
			operateLogGlobal(mp, MpOperateLogService.OP_TYPE_REFRESH_AUDIT_STATE, result, WxContentTemplate.WX_REFRESH_AUDIT_STATE_FAIL.code, new String[] {result.getErrcode(),result.getErrmsg()});
			break;
		case MpDeployQueryParam.ACT_UPLOAD_AUDIT://上传代码并提交审核   一键提交审核
			operateLogGlobal(mp, MpOperateLogService.OP_TYPE_UPLOAD_AUDIT, result, WxContentTemplate.WX_UPLOAD_AUDIT_FAIL.code, new String[] {result.getErrcode(),result.getErrmsg()});
			break;
		case MpDeployQueryParam.ACT_UPLOAD_CODE://上传代码

			operateLogGlobal(mp, MpOperateLogService.OP_TYPE_UPLOAD_CODE, result, WxContentTemplate.WX_UPLOAD_CODE_FAIL.code, new String[] {String.valueOf(param.getIsSubMerchant()),result.getErrcode(),result.getErrmsg()});
			break;
		case MpDeployQueryParam.SETTING_SUB_MERCHANT://设置支付方式
			operateLogGlobal(mp, MpOperateLogService.OP_TYPE_SETTING_SUB_MERCHANT, result, WxContentTemplate.WX_SETTING_SUB_MERCHANT_FAIL.code, new String[] {result.getErrcode(),result.getErrmsg()});
			break;
		default:
			operateLogGlobal(mp, MpOperateLogService.OP_STATE_FAILED, result, WxContentTemplate.WX_ERROE.code, new String[] {param.getAct(),result.getErrcode(),result.getErrmsg()});
		}
	}


	public Integer updateRow(MpAuthShopRecord authShopByShopId) {
		return db().executeUpdate(authShopByShopId);
	}





	/**
	 * 微信回调执行的函数/wechat/notify/app/event/{appId}/callback
	 * @param inMessage
	 * @param appId
	 * @return
	 */
	public WxMpXmlOutMessage appEventHandler(WxMpXmlMessage inMessage,String appId) {
		/*
		 * WebAppAudit::class, OfficialAccountMessage::class, MessageTrans::class,
		 */
		// 小程序有审核结果通知
		webAppAudit(inMessage, appId);
		WxMpXmlOutTextMessage wxMessage = officialAccountMessage(inMessage, appId);
		WxMpXmlOutMessage messageTrans = messageTrans(inMessage);
		if(wxMessage==null) {
			logger().info("准备返回客服消息");
			return messageTrans;
		}
		return wxMessage;
	}


	/**
	 * 小程序有审核结果通知
	 * @param inMessage
	 * @param appId
	 */
	public void webAppAudit(WxMpXmlMessage inMessage,String appId) {
		processAuditEvent(inMessage, appId);
	}

	public void processAuditEvent(WxMpXmlMessage inMessage, String appId) {
		// processAuditEvent($appId, $message['Event'], $message['Reason']);
        String event = "event";
        String weappAuditSuccess = "weapp_audit_success";
        String weappAuditFail = "weapp_audit_fail";
        boolean isWeappAuditEvent = inMessage.getMsgType().equals(event) && (inMessage.getEvent().equals(weappAuditSuccess)
            || inMessage.getEvent().equals(weappAuditFail));
        if (isWeappAuditEvent) {
			logger().info("小程序有审核结果通知"+inMessage.getEvent());
			MpAuthShopRecord mpRecord = getAuthShopByAppId(appId);
			WxOpenResult wxOpenResult=new WxOpenResult();
			if(mpRecord!=null) {
				Integer bindTemplateId = mpRecord.getBindTemplateId();
				if(inMessage.getEvent().equals(weappAuditSuccess)) {
					mpRecord.setAuditState((byte) 2);
					mpRecord.setAuditOkTime(new Timestamp(System.currentTimeMillis()));
					db().executeUpdate(mpRecord);
					wxOpenResult.setErrcode("0");
					operateLogGlobal(mpRecord, MpOperateLogService.OP_TYPE_AUDIT_SUCCESS, wxOpenResult, WxContentTemplate.WX_AUDIT_SUCCESS.code,new String[] {});
					//审核自动发布代码
					try {
						//发布审核成功代码
						publishAuditSuccessCode(appId);
						//调用更新小程序跳转appid可用状态
						AppletsJumpService appletsJumpService = saas.getShopApp(mpRecord.getShopId()).appletsJump;
						appletsJumpService.updateMpJumpAppIdList(bindTemplateId);
						//return true
					} catch (WxErrorException e) {
						e.printStackTrace();
					}

				}else {
					mpRecord.setAuditState((byte) 3);
					mpRecord.setAuditFailReason(inMessage.getReason());
					db().executeUpdate(mpRecord);
					operateLogGlobal(mpRecord, MpOperateLogService.OP_TYPE_AUDIT_FAILED, wxOpenResult, WxContentTemplate.WX_AUDIT_FAIL.code,new String[] {inMessage.getReason()});
				}
			}
		}

	}

	public WxMpXmlOutTextMessage officialAccountMessage(WxMpXmlMessage inMessage,String appId) {
		WxMpXmlOutTextMessage wxMessage = processMessage(inMessage, appId);
		return wxMessage;
	}
	/**
	 * 处理公众号消息
	 * @param inMessage
	 * @param appId
	 * @return
	 */
	public WxMpXmlOutTextMessage processMessage(WxMpXmlMessage inMessage,String appId) {
		MpOfficeAccountListVo officeAccountByAppId = saas.shop.officeAccount.getOfficeAccountByAppId(appId);
		WxMpXmlOutTextMessage process=null;
		if (officeAccountByAppId != null) {
			// 是公众号
			try {
				process = processSubscribeEvent(inMessage, appId, officeAccountByAppId);
			} catch (WxErrorException e) {
				logger().info(e.getMessage(),e);
			}
			if(StringUtils.isEmpty(process.getToUserName())) {
				process=null;
			}
		}else {
			logger().info("processMessage方法接收的appid "+appId+"在数据库中不存在");
		}
		return process;
	}

	public WxMpXmlOutTextMessage processSubscribeEvent(WxMpXmlMessage inMessage,String appId,MpOfficeAccountListVo officeAccountByAppId) throws WxErrorException {
		//subscribe（订阅）
		WxMpXmlOutTextMessage message = WxMpXmlOutMessage.TEXT().build();
        if (processSubscribeEvent(inMessage, appId, officeAccountByAppId, message)) {
            return message;
        }
        if (processUnSubscribeEvent(inMessage, appId, message)) {
            return message;
        }
        return message;
	}

    private boolean processUnSubscribeEvent(WxMpXmlMessage inMessage, String appId, WxMpXmlOutTextMessage message) throws WxErrorException {
        //取消订阅
        String unsubscribe = "unsubscribe";
        if(StringUtils.isNotEmpty(inMessage.getEvent())&&inMessage.getEvent().equals(unsubscribe)) {
			logger().info("开始解绑");
			WxMpUser userInfo = open().getWxOpenComponentService().getWxMpServiceByAppid(appId).getUserService().userInfo(inMessage.getFromUser());
			logger().info("用户Openid"+userInfo.getOpenId()+"解绑公众号"+appId);
			if(userInfo!=null) {
				MpOfficialAccountUserRecord record=MP_OFFICIAL_ACCOUNT_USER.newRecord();
				record.setAppId(appId);
				record.setOpenid(userInfo.getOpenId());
				record.setSubscribe((byte)0);
				saas.shop.officeAccount.addOrUpdateUser(appId, record, userInfo.getUnionId(), userInfo.getOpenId());
				logger().info("用户Openid"+userInfo.getOpenId()+"解绑公众号完成");
                return true;
			}
		}
        return false;
    }

    private boolean processSubscribeEvent(WxMpXmlMessage inMessage, String appId, MpOfficeAccountListVo officeAccountByAppId, WxMpXmlOutTextMessage message) throws WxErrorException {
        String subscribe = "subscribe";
        if(StringUtils.isNotEmpty(inMessage.getEvent())&&inMessage.getEvent().equals(subscribe)) {
			logger().info("开始绑定公众号");
			//公众号获取用户信息
			WxMpUser userInfo = open().getWxOpenComponentService().getWxMpServiceByAppid(appId).getUserService().userInfo(inMessage.getFromUser());
			logger().info("用户Openid："+userInfo.getOpenId()+"开始绑定公众号："+appId);
			if(userInfo!=null) {
				MpOfficialAccountUserRecord record=MP_OFFICIAL_ACCOUNT_USER.newRecord();
				record.setOpenid(userInfo.getOpenId());
				record.setAppId(appId);
				record.setSysId(officeAccountByAppId.getSysId());
				record.setSubscribe(userInfo.getSubscribe() ? (byte) 1 : (byte) 0);
				record.setNickname(userInfo.getNickname());
				record.setSex(userInfo.getSex().byteValue());
				record.setLanguage(userInfo.getLanguage());
				record.setCity(userInfo.getCity());
				record.setProvince(userInfo.getProvince());
				record.setCountry(userInfo.getCountry());
				record.setHeadimgurl(userInfo.getHeadImgUrl());
				record.setSubscribeTime(new Timestamp(userInfo.getSubscribeTime() * 1000L));
				record.setUnionid(userInfo.getUnionId());
				saas.shop.officeAccount.addOrUpdateUser(appId, record, userInfo.getUnionId(), userInfo.getOpenId());
				//得到公众号关联的小程序
				Result<MpAuthShopRecord> officialAccountMps = getOfficialAccountMps(appId);
				boolean parseAccountInfo = saas.shop.account.parseAccountInfo(appId, inMessage.getEventKey(), record.getOpenid());
				logger().info("parseAccountInfo result "+parseAccountInfo);
				if(parseAccountInfo) {
					logger().info("用户Openid"+userInfo.getOpenId()+"组装响应消息 欢迎关注， 您可在这里及时接收新订单提醒");
					//packageResponseMsg 组装响应消息 欢迎关注， 您可在这里及时接收新订单提醒'
				    message.setToUserName(userInfo.getOpenId());
				    message.setFromUserName(inMessage.getToUser());
				    message.setContent("欢迎关注，您可在这里及时接收新订单提醒");
				    message.setCreateTime(System.currentTimeMillis() / 1000L);
                    return true;
				}else {
					logger().info("用户Openid"+userInfo.getOpenId()+"为你精心准备了关注礼品，快来点击查看吧!");
					for(MpAuthShopRecord authShopRecord:officialAccountMps) {
						Record shop = saas.shop.getShop(authShopRecord.getShopId());
						String shopName = shop.get(SHOP.SHOP_NAME);
						String firest="为你精心准备了关注礼品，快来点击查看吧!";
						String page="pages/auth/auth";//String page="pages/auth/auth";pages/index/index
						String content="点击进入小程序";
						//MpOfficialAccountUserRecord user = saas.shop.mpOfficialAccountUserService.getUser(appId, userInfo.getOpenId());
						List<Integer> userIdList = new ArrayList<Integer>();
						String[][] data = new String[][] { {firest,"#173177"},{shopName,"#173177"},{Util.getdate("yyyy-MM-dd HH:mm:ss"),"#173177"},{content,"#173177"},{"","#173177"}};
						Integer mpTempleType = RabbitParamConstant.Type.MP_TEMPLE_TYP_NO;
						//unioId在登录过小程序后才会传
						if(StringUtils.isEmpty(userInfo.getUnionId())) {
							logger().info("用户没有登录过小程序");
							com.meidianyi.shop.db.shop.tables.records.MpOfficialAccountUserRecord user = saas
									.getShopApp(authShopRecord.getShopId()).officialAccountUser.getUser(appId,
											userInfo.getOpenId());
							userIdList.add(user.getRecId());
							mpTempleType = RabbitParamConstant.Type.MP_TEMPLE_TYPE_NO_USER;
						}else {
							logger().info("用户登录过小程序");
							UserRecord user = saas.getShopApp(authShopRecord.getShopId()).user.getUserByUnionId(userInfo.getUnionId());
							userIdList.add(user.getUserId());
						}
						RabbitMessageParam param = paramBuild(authShopRecord, page, userIdList, data,mpTempleType);
						saas.taskJobMainService.dispatchImmediately(param, RabbitMessageParam.class.getName(), authShopRecord.getShopId(), TaskJobEnum.SEND_MESSAGE.getExecutionType());
					}
				}
                return true;
			}
		}
        return false;
    }

    private RabbitMessageParam paramBuild(MpAuthShopRecord authShopRecord, String page, List<Integer> userIdList,
			String[][] data,Integer mpTempleType) {
		RabbitMessageParam param = RabbitMessageParam.builder()
				.mpTemplateData(
						MpTemplateData.builder().config(MpTemplateConfig.PUSHMSG).data(data).build())
				.page(page).shopId(authShopRecord.getShopId()).userIdList(userIdList)
				.type(mpTempleType).build();
		return param;
	}


	/**
	 * 组装List<WxMpTemplateData>的信息
	 * @param record
	 * @param firest
	 * @param shopName
	 * @param content
	 * @param page
	 * @param remark
	 * @return
	 */
	private List<WxMpTemplateData> fexMessage(MpOfficialAccountUserRecord record,String firest,String shopName,String content,String page,String remark) {
		Map<String,String> map=new HashMap<String, String>();
		map.put("first", firest);
		map.put("keyword1", shopName);
		map.put("keyword2", Util.getdate("YYYY-MM-dd HH:mm:ss"));
		map.put("keyword3", content);
		map.put("remark", remark);
		List<WxMpTemplateData> keywordValuesDatas=new ArrayList<WxMpTemplateData>();
		Set<String> keySets = map.keySet();
		for(String  keySet:keySets) {
			WxMpTemplateData data=new WxMpTemplateData();
			data.setName(keySet);
			data.setValue(map.get(keySet));
			data.setColor("#173177");
			keywordValuesDatas.add(data);
		}
		return keywordValuesDatas;
	}



	/**
	 * 得到公众号关联的小程序
	 * @param appId
	 * @return
	 */
	public Result<MpAuthShopRecord> getOfficialAccountMps(String appId) {
		return db().selectFrom(MP_AUTH_SHOP).where(MP_AUTH_SHOP.LINK_OFFICIAL_APP_ID.eq(appId)).fetch();
	}


	/**
	 * 消息都转发给客服
	 * @param inMessage
	 * @return
	 */
	public WxMpXmlOutMessage messageTrans(WxMpXmlMessage inMessage) {
		WxMpXmlOutTextMessage build = WxMpXmlOutMessage.TEXT().build();
		build.setToUserName(inMessage.getFromUser());
		build.setFromUserName(inMessage.getToUser());
		build.setCreateTime((System.currentTimeMillis() / 1000L));
		build.setMsgType(WxConsts.XmlMsgType.TRANSFER_CUSTOMER_SERVICE);
		logger().info("\n 发给客服的报文：\n{}",build.toXml().toString());
		return build;
	}

	/**
	 * 终止 任务
	 * @param recId
	 * @return
	 */
	public Boolean stopBatchUpload(Integer recId) {
		BackProcessRecord row = saas.shop.backProcessService.getRow(recId);
		if(row==null) {
			return false;
		}
		if(row.getProcessId()==0) {
			return false;
		}
		//判断任务是否可以终止
		if(saas.taskJobMainService.assertExecuting(row.getProcessId())) {
			//更改任务状态为 3 ，终止
            saas.taskJobMainService.updateTaskJobStatus(row.getProcessId(), TaskJobsConstant.STATUS_TERMINATION);
			return true;
		}
		return false;
	}

	/**店铺发布列表
	 *
	 * @param param
	 * @return
	 * @return
	 */
	public PageResult<ShopMpListVo> getShopMpList(ShopMpListParam param) {
		SelectJoinStep<Record11<String, Integer, Timestamp, String, Timestamp, Timestamp, Integer, Byte, String, String, Byte>> selectFrom = db()
				.select(MP_AUTH_SHOP.APP_ID, MP_AUTH_SHOP.SHOP_ID, MP_AUTH_SHOP.CREATE_TIME, MP_AUTH_SHOP.NICK_NAME,
						MP_AUTH_SHOP.PUBLISH_TIME, MP_AUTH_SHOP.LAST_UPLOAD_TIME, MP_AUTH_SHOP.BIND_TEMPLATE_ID,
						MP_AUTH_SHOP.OPEN_PAY, MP_AUTH_SHOP.PRINCIPAL_NAME, SHOP.SHOP_TYPE, SHOP.IS_ENABLED)
				.from(MP_AUTH_SHOP, SHOP);
		buildOptionsMp(param, selectFrom);
		selectFrom.where(MP_AUTH_SHOP.SHOP_ID.eq(SHOP.SHOP_ID));
		selectFrom.orderBy(SHOP.CREATED.desc());
		selectFrom.orderBy(MP_AUTH_SHOP.SHOP_ID.desc());
		selectFrom.orderBy(MP_AUTH_SHOP.CREATE_TIME.desc());
		PageResult<ShopMpListVo> pageResult = this.getPageResult(selectFrom, param.getCurrentPage(), param.getPageRows(), ShopMpListVo.class);
		for(ShopMpListVo vo:pageResult.dataList) {
			vo.setRenewMoney(saas.shop.renew.getShopRenewTotal(vo.getShopId()));
			Timestamp expireTime = saas.shop.renew.getShopRenewExpireTime(vo.getShopId());
			vo.setExpireTime(expireTime);
			String expireStatus = "1";
			if (expireTime != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(expireTime);
				cal.set(Calendar.HOUR, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				if (cal.getTimeInMillis() > Calendar.getInstance().getTimeInMillis()) {
					expireStatus = "0";
				}
			}
			vo.setShopExpireStatus(expireStatus);
			List<MpOperateVo> operateLog = saas.shop.mpOperateLog.getOperateLog(vo.getAppId());
			if(operateLog.size()>0) {
				MpOperateVo mpOperateVo = operateLog.get(0);
				vo.setStartTime(mpOperateVo.getCreateTime());
				vo.setTemplateId(mpOperateVo.getTemplateId());
				vo.setUserVersion(mpOperateVo.getUserVersion());
				MpOperateVo mpOperateVo2 = operateLog.get(operateLog.size()-1);
				vo.setLastUploadTime(mpOperateVo2.getCreateTime());
				vo.setBindTemplateId(mpOperateVo2.getTemplateId());
				vo.setBindUserVersion(mpOperateVo2.getUserVersion());
			}
		}
		return pageResult;
	}


	private void buildOptionsMp(ShopMpListParam param,SelectJoinStep<?> selectFrom) {
		if(StringUtils.isNotEmpty(param.getKeywords())) {
			selectFrom.where(MP_AUTH_SHOP.SHOP_ID.like(likeValue(param.getKeywords())).or(MP_AUTH_SHOP.NICK_NAME.like(likeValue(param.getKeywords())).or(SHOP.SHOP_NAME.like(likeValue(param.getKeywords())))));
		}
		if(StringUtils.isNotEmpty(param.getShopType())) {
			selectFrom.where(SHOP.SHOP_TYPE.eq(param.getShopType()));
		}
		if(param.getOpenPay()!=null) {
			selectFrom.where(MP_AUTH_SHOP.OPEN_PAY.eq(param.getOpenPay()));
		}
		if(param.getIsEnabled()!=null) {
			selectFrom.where(SHOP.IS_ENABLED.eq(param.getIsEnabled()));
		}
	}


	/**
	 * 根据shopId获取小程序对应的公众号
	 * @param shopId
	 * @return
	 */
	public String findOffcialByShopId(Integer shopId) {
		Record1<String> fetchAny = db().select(MP_OFFICIAL_ACCOUNT.APP_ID).from(MP_AUTH_SHOP, MP_OFFICIAL_ACCOUNT)
				.where(MP_AUTH_SHOP.LINK_OFFICIAL_APP_ID.eq(MP_OFFICIAL_ACCOUNT.APP_ID)
						.and(MP_AUTH_SHOP.SHOP_ID.eq(shopId)))
				.fetchAny();
		String into=null;
		if(fetchAny!=null) {
			into = fetchAny.into(String.class);
		}
		return into;
	}


	/**
	 * 获取小程序AppId
	 *
	 * @param shopId
	 * @return
	 */
	public String getAppIdByShopId(Integer shopId) {
		MpAuthShopRecord mp = getAuthShopByShopId(shopId);
		Assert.isTrue(mp != null && mp.getIsAuthOk().equals(AUTH_OK),"mp is null ");
		return mp.getAppId();
	}

	public List<String> getAllTester(String appId) throws WxErrorException{
		WxOpenMaService maService = this.getMaServiceByAppId(appId);
		WxOpenMaTesterListResult testerList = maService.getTesterList();
		List<String> list = new ArrayList<String>();
		if (!testerList.isSuccess()) {
			return null;
		}
		List<WxOpenMaMember> membersList = testerList.getMembersList();
		for (WxOpenMaMember wxOpenMaMember : membersList) {
			list.add(wxOpenMaMember.getUserstr());
		}
		return list;
	}

	private void setTestRecord(MpAuthShopRecord record,String appId) {
		logger().info("appid：{}更新体验者",appId);
		List<String> allTester=null;
		try {
			allTester = getAllTester(appId);
		} catch (WxErrorException e) {
			e.printStackTrace();
		}
		if(allTester!=null) {
			record.setTester(Util.toJson(allTester));
		}
	}

	/**
	 * 检查是否有直播权限集
	 * @param record
	 * @return
	 */
	public boolean checkHasLive(MpAuthShopRecord record) {
		String funcInfo = record.getFuncInfo();
		funcInfo = funcInfo.replace("[", "");
		funcInfo = funcInfo.replace("]", "");
		logger().info("权限集为：{}" + funcInfo);
		String[] split = funcInfo.split(",");
		for (String string : split) {
            String liveAuthorityCode = "52";
            if(string.equals(liveAuthorityCode)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 直播的校验
	 * @param shopId
	 * @return
	 */
	public LiveCheckVo checkLive(Integer shopId) {
		MpAuthShopRecord authShop = getAuthShopByShopId(shopId);
		boolean isAuthLive=true;
		if(authShop==null) {
			return new LiveCheckVo(isAuthLive,null,false);
		}
		boolean hasLiveFunc = checkHasLive(authShop);
		logger().info("店铺：{}，是否有直播权限：{}",shopId,hasLiveFunc);
		if(hasLiveFunc) {
			isAuthLive=false;
		}
		Byte packageVersion = saas.shop.mpOperateLog.getLastAuditSuccessPackage(authShop.getAppId());
		Byte three=3;
		Byte four=4;
		List<Byte> list=new ArrayList<Byte>();
		list.add(three);
		list.add(four);
		if(!list.contains(packageVersion)) {
			isAuthLive=true;
		}
		return new LiveCheckVo(isAuthLive,authShop.getAuditState(),hasLiveFunc);
	}
}
