package com.meidianyi.shop.service.saas.shop;

import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.main.tables.records.MpAuthShopRecord;
import com.meidianyi.shop.db.main.tables.records.MpOfficialAccountRecord;
import com.meidianyi.shop.db.main.tables.records.MpOfficialAccountUserRecord;
import com.meidianyi.shop.service.foundation.jedis.JedisManager;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobsConstant.TaskJobEnum;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpOfficeAccountVo;
import com.meidianyi.shop.service.pojo.saas.shop.officeaccount.MaMpBindParam;
import com.meidianyi.shop.service.pojo.saas.shop.officeaccount.MpOaPayManageParam;
import com.meidianyi.shop.service.pojo.saas.shop.officeaccount.MpOfficeAccountListParam;
import com.meidianyi.shop.service.pojo.saas.shop.officeaccount.MpOfficeAccountListVo;
import com.meidianyi.shop.service.pojo.shop.auth.AdminTokenAuthInfo;
import com.meidianyi.shop.service.pojo.shop.auth.AuthConstant;
import com.meidianyi.shop.service.pojo.shop.auth.StoreTokenAuthInfo;
import com.meidianyi.shop.service.pojo.shop.market.message.BindRabbitParam;
import com.meidianyi.shop.service.pojo.wxapp.login.WxAppSessionUser;
import com.meidianyi.shop.service.saas.image.SystemImageService;
import com.meidianyi.shop.service.shop.user.user.MpOfficialAccountUserByShop;
import com.meidianyi.shop.service.wechat.api.WxOpenMpampLinkService;
import com.meidianyi.shop.service.wechat.bean.open.WxOpenMiniLinkGetItems;
import com.meidianyi.shop.service.wechat.bean.open.WxOpenMiniLinkGetResult;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpQrcodeService;
import me.chanjar.weixin.mp.api.WxMpUserService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import me.chanjar.weixin.mp.bean.result.WxMpUserList;
import me.chanjar.weixin.open.bean.auth.WxOpenAuthorizationInfo;
import me.chanjar.weixin.open.bean.auth.WxOpenAuthorizerInfo;
import me.chanjar.weixin.open.bean.result.WxOpenAuthorizerInfoResult;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Record;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.SelectOnConditionStep;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.meidianyi.shop.db.main.tables.MpOfficialAccount.MP_OFFICIAL_ACCOUNT;
import static com.meidianyi.shop.db.main.tables.MpOfficialAccountUser.MP_OFFICIAL_ACCOUNT_USER;
import static com.meidianyi.shop.db.main.tables.ShopAccount.SHOP_ACCOUNT;

/**
 * 公众号相关
 *
 * @author zhaojianqiang
 *
 *         2019年8月21日 上午10:07:55
 */
@Service
public class ShopOfficialAccount extends MainBaseService {

	private static final String EN_US = "en_US";

	@Autowired
	protected SystemImageService image;

	@Autowired
	protected JedisManager jedis;

	/**
	 * 获取数据库公众号列表
     *
	 * @param oaListParam
	 * @return
	 */
	public PageResult<MpOfficeAccountListVo> getPageList(MpOfficeAccountListParam oaListParam) {
		SelectOnConditionStep<Record> select = db().select(MP_OFFICIAL_ACCOUNT.asterisk()).from(MP_OFFICIAL_ACCOUNT)
				.innerJoin(SHOP_ACCOUNT).on(MP_OFFICIAL_ACCOUNT.SYS_ID.eq(SHOP_ACCOUNT.SYS_ID));

		select.where(MP_OFFICIAL_ACCOUNT.SYS_ID.eq(oaListParam.getSysId()));
		select.orderBy(MP_OFFICIAL_ACCOUNT.CREATE_TIME.desc());
		// 因为页面不展示二维码。没有给图像加image.imageUrl方法。想加的加个循环吧
		PageResult<MpOfficeAccountListVo> pageResult = this.getPageResult(select, oaListParam.getCurrentPage(),
				oaListParam.getPageRows(), MpOfficeAccountListVo.class);
		for(MpOfficeAccountListVo vo:pageResult.dataList) {
			List<String> nickNams=new ArrayList<String>();
			Result<MpAuthShopRecord> officialAccountMps = saas.shop.mp.getOfficialAccountMps(vo.getAppId());
			for(MpAuthShopRecord record:officialAccountMps) {
				nickNams.add(record.getNickName());
			}
			vo.setMpNickName(nickNams);
		}
		return pageResult;
	}

	/**
	 * 单个公众号的信息
     *
	 * @param appId
	 * @param sysId
	 * @return
	 */
	public MpOfficeAccountListVo getOfficeAccountByAppIdAndsysId(String appId, Integer sysId) {
		MpOfficeAccountListVo fetch = db().select(MP_OFFICIAL_ACCOUNT.asterisk()).from(MP_OFFICIAL_ACCOUNT)
				.innerJoin(SHOP_ACCOUNT).on(MP_OFFICIAL_ACCOUNT.SYS_ID.eq(SHOP_ACCOUNT.SYS_ID))
				.where(MP_OFFICIAL_ACCOUNT.SYS_ID.eq(sysId).and(MP_OFFICIAL_ACCOUNT.APP_ID.eq(appId)))
				.fetchAnyInto(MpOfficeAccountListVo.class);
		if (fetch != null) {
			fetch.setQrcodeUrl(image.imageUrl(fetch.getQrcodeUrl()));
		}
		return fetch;
	}

	public Result<MpOfficialAccountRecord> getSamePrincipalOffice(String principalName) {
		return db().fetch(MP_OFFICIAL_ACCOUNT,
				MP_OFFICIAL_ACCOUNT.PRINCIPAL_NAME.eq(principalName).and(MP_OFFICIAL_ACCOUNT.IS_AUTH_OK.eq((byte) 1)));
	}

	/**
	 * 公众号
	 * @param principalName
	 * @return
	 */
	public List<MaMpBindParam> getSamePrincipalOfficeList(String principalName) {
		Result<Record3<String, String, String>> fetch = db()
				.select(MP_OFFICIAL_ACCOUNT.APP_ID, MP_OFFICIAL_ACCOUNT.BIND_OPEN_APP_ID,DSL.field("1", String.class).as("type")).from(MP_OFFICIAL_ACCOUNT)
				.where(MP_OFFICIAL_ACCOUNT.PRINCIPAL_NAME.eq(principalName)
						.and(MP_OFFICIAL_ACCOUNT.IS_AUTH_OK.eq((byte) 1)))
				.fetch();
		List<MaMpBindParam> into =new ArrayList<MaMpBindParam>();
		if(fetch!=null) {
			into = fetch.into(MaMpBindParam.class);
		}
		return into;
	}
	/**
	 * 提现配置
     *
	 * @param oaParam
	 * @return
	 */
	public Integer updatePayInfo(MpOaPayManageParam oaParam) {
		MpOfficialAccountRecord newRecord = MP_OFFICIAL_ACCOUNT.newRecord();
		FieldsUtil.assignNotNull(oaParam, newRecord);
		return db().executeUpdate(newRecord);
	}

	public MpOfficeAccountListVo getOfficeAccountByAppId(String appId) {
		return db().select(MP_OFFICIAL_ACCOUNT.asterisk()).from(MP_OFFICIAL_ACCOUNT).innerJoin(SHOP_ACCOUNT)
				.on(MP_OFFICIAL_ACCOUNT.SYS_ID.eq(SHOP_ACCOUNT.SYS_ID)).where((MP_OFFICIAL_ACCOUNT.APP_ID.eq(appId)))
				.fetchAnyInto(MpOfficeAccountListVo.class);
	}

	public Record getOfficeAccountByAppIdRecord(String appId) {
		return db().select(MP_OFFICIAL_ACCOUNT.asterisk()).from(MP_OFFICIAL_ACCOUNT).innerJoin(SHOP_ACCOUNT)
				.on(MP_OFFICIAL_ACCOUNT.SYS_ID.eq(SHOP_ACCOUNT.SYS_ID)).where((MP_OFFICIAL_ACCOUNT.APP_ID.eq(appId))).fetchAny();
	}


	public Result<MpOfficialAccountRecord> getOfficialAccountBySysId(Integer sysId) {
		Result<MpOfficialAccountRecord> fetch = db().selectFrom(MP_OFFICIAL_ACCOUNT)
				.where(MP_OFFICIAL_ACCOUNT.SYS_ID.eq(sysId)).fetch();
		return fetch;
	}

    public Result<MpOfficialAccountRecord> getOfficialAccountBySysId(Integer sysId, Byte isAuthOk) {
        return db().selectFrom(MP_OFFICIAL_ACCOUNT)
            .where(MP_OFFICIAL_ACCOUNT.SYS_ID.eq(sysId)).and(MP_OFFICIAL_ACCOUNT.IS_AUTH_OK.eq(isAuthOk)).fetch();
    }

    public int isOfficialAccountBySysId(Integer sysId, Byte isAuthOk) {
        return db().fetchCount(MP_OFFICIAL_ACCOUNT, MP_OFFICIAL_ACCOUNT.SYS_ID.eq(sysId)
            .and(MP_OFFICIAL_ACCOUNT.IS_AUTH_OK.eq(isAuthOk)));
    }

	public List<MpOfficeAccountVo> findSamePrincipalMiniAndMp(Result<MpOfficialAccountRecord> oaRecords,
                                                              MpAuthShopRecord miniRecord) {
		List<MpOfficeAccountVo> list = new ArrayList<MpOfficeAccountVo>();
		for (MpOfficialAccountRecord rAccountRecord : oaRecords) {
			if (rAccountRecord.getIsAuthOk().equals((byte) 1)) {
				// 已授权了
				if (rAccountRecord.getPrincipalName().equals(miniRecord.getPrincipalName())) {
					MpOfficeAccountVo vo=new MpOfficeAccountVo();
					vo.setAppId(rAccountRecord.getAppId());
					vo.setNickName(rAccountRecord.getNickName());
					vo.setIsAuthOk(rAccountRecord.getIsAuthOk());
					list.add(vo);
				}
			}
		}
		return list;

	}

	/**
	 * 添加微信公众号授权
     *
	 * @return
	 */
	public MpOfficialAccountRecord addMpOfficialAccountInfo(Integer sysId,
			WxOpenAuthorizerInfoResult authorizerInfoResult) {
		WxOpenAuthorizationInfo authorizationInfo = authorizerInfoResult.getAuthorizationInfo();
		String authorizerAppid = authorizationInfo.getAuthorizerAppid();
		WxOpenAuthorizerInfo authorizerInfo = authorizerInfoResult.getAuthorizerInfo();
		MpOfficialAccountRecord officeRecord = MP_OFFICIAL_ACCOUNT.newRecord();
		// 授权方认证类型，-1代表未认证，0代表微信认证，1代表新浪微博认证，2代表腾讯微博认证，3代表已资质认证通过但还未通过名称认证，
		// 4代表已资质认证通过、还未通过名称认证，但通过了新浪微博认证，5代表已资质认证通过、还未通过名称认证，但通过了腾讯微博认证
		Integer verifyTypeInfo = authorizerInfo.getVerifyTypeInfo();

		// 授权方公众号类型，0代表订阅号，1代表由历史老帐号升级后的订阅号，2代表服务号
		Integer serviceTypeInfo = authorizerInfo.getServiceTypeInfo();
		// 二维码图片的URL，开发者最好自行也进行保存
		String qrcodeUrl = authorizerInfo.getQrcodeUrl();
		String mpQrCode = getMpQrCode(authorizerAppid, qrcodeUrl);
		officeRecord.setAppId(authorizerAppid);
		officeRecord.setNickName(authorizerInfo.getNickName());
		officeRecord.setUserName(authorizerInfo.getUserName());
		officeRecord.setAlias(authorizerInfo.getAlias());
		officeRecord.setVerifyTypeInfo(String.valueOf(verifyTypeInfo));
		officeRecord.setHeadImg(authorizerInfo.getHeadImg());
		officeRecord.setFuncInfo(Util.toJson(authorizationInfo.getFuncInfo()));
		officeRecord.setIsAuthOk((byte) 1);
		officeRecord.setLastAuthTime(new Timestamp(System.currentTimeMillis()));
		officeRecord.setOpenPay(authorizerInfo.getBusinessInfo().get("open_pay").byteValue());
		officeRecord.setOpenCard(authorizerInfo.getBusinessInfo().get("open_card").byteValue());
		officeRecord.setAuthorizerInfo(Util.toJson(authorizerInfo));
		officeRecord.setAuthorizationInfo(Util.toJson(authorizationInfo));
		officeRecord.setPrincipalName(authorizerInfo.getPrincipalName());
		// php上是 'account_type' => $verifyType == -1 ? ($serviceType == 2 ? 2 : 0)
		// :($serviceType == 2 ? 3 : 1)
		officeRecord.setAccountType(
				(byte) (verifyTypeInfo == -1 ? (serviceTypeInfo == 2 ? 2 : 0) : (serviceTypeInfo == 2 ? 3 : 1)));
		officeRecord.setSysId(sysId);
		officeRecord.setQrcodeUrl(mpQrCode);
		if (getOfficeAccountByAppId(authorizerAppid) == null) {
			// 插入
			db().executeInsert(officeRecord);
		} else {
			// 更新
			db().executeUpdate(officeRecord);
		}
		return officeRecord;

	}

	/**
	 * 绑定同一主体公众号和小程序到开放平台账号 ,个人账号不支持绑定
     *
	 * @param principalName
	 */
	public void bindAllSamePrincipalOpenAppId(String principalName) throws WxErrorException {
		logger().info("传入的principalName："+principalName);
        String principalPersonal = "个人";
        if (!principalName.equals(principalPersonal)) {
			List<MaMpBindParam> apps = getSamePrincipalOfficeList(principalName);
			List<MaMpBindParam> samePrincipalMaList = saas.shop.mp.getSamePrincipalMaList(principalName);
            samePrincipalMaList.addAll(apps);
			bindSamePrincipalApps(samePrincipalMaList);
		}
	}

	public void bindSamePrincipalApps(List<MaMpBindParam> apps) throws WxErrorException {
		String openAppId = null;
		for (MaMpBindParam app : apps) {
			if (!StringUtils.isEmpty(app.getBindOpenAppId())) {
				openAppId = app.getBindOpenAppId();
			}
		}
		logger().info("绑定的bindOpenAppId为" + openAppId);
		for (MaMpBindParam app : apps) {
            if(StringUtils.isNotEmpty(openAppId)&&openAppId.equals(app.getBindOpenAppId())) { continue; }
            openAppId = saas.shop.mp.bindOpenAppId(false, app.getAppId(), openAppId);
			if (!openAppId.equals(app.getBindOpenAppId())) {
				// 更新数据库
                String typeMp = "1";
                if (app.getType().equals(typeMp)) {
					// 公众号
					int execute = db().update(MP_OFFICIAL_ACCOUNT).set(MP_OFFICIAL_ACCOUNT.BIND_OPEN_APP_ID, openAppId)
							.where(MP_OFFICIAL_ACCOUNT.APP_ID.eq(app.getAppId())).execute();
					logger().info("公众号："+app.getAppId()+"更新bindOpenAppId："+openAppId+"结果："+execute);
				}
                String typeMa = "2";
                if (app.getType().equals(typeMa)) {
					// 小程序
					int updateBindOpenAppId = saas.shop.mp.updateBindOpenAppId(app.getAppId(), openAppId);
					logger().info("小程序："+app.getAppId()+"更新bindOpenAppId："+openAppId+"结果："+updateBindOpenAppId);
				}
			}
		}

	}

	public String getMpQrCode(String appId, String qrcodeUrl) {
		String path = "upload/mini_main/qrcode/";
		String filename = "qrcode_" + getDate("yyyyMMddHHmmss") + "_" + getRandom(10000, 99999) + ".png";
		String relativePath = "public/" + path + filename;
		Boolean addImgeToUp = false;
		try {
			addImgeToUp = saas.sysImage.addImgeToUp(qrcodeUrl, relativePath);
		} catch (Exception e) {
			logger().error("公众号" + appId + "头像上传又拍云失败");
			e.printStackTrace();
		}
		logger().debug("公众号" + appId + "头像上传又拍云" + addImgeToUp);
		return relativePath;
	}

	protected String getRandom(int min, int max) {
		Random random = new Random();
		int s = random.nextInt(max) % (max - min + 1) + min;
		return String.valueOf(s);
	}

	protected String getDate(String patter) {
		LocalDateTime localDate = LocalDateTime.now();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(patter);
		String date = dtf.format(localDate);
		return date;
	}

	/**
	 * 获得公众号场景二维码
	 * @param user
	 * @param appId
	 * @return
	 * @throws WxErrorException
	 */
	public String generateThirdPartCode(AdminTokenAuthInfo user, String appId) throws WxErrorException {
		logger().info("传入appid为：{}",appId);
		boolean subLogin = user.isSubLogin();
		// 主账户是1
		int accountAction = subLogin ? 2 : 1;
		int accountId = subLogin ? user.subAccountId : user.sysId;
		int expireSeconds = 24 * 60 * 30 * 60 - 60;
		//$sceneValue = $shopId.'&'.$accountAction.'&'.$accountId;
		String sceneValue = user.loginShopId.toString() + "&" + String.valueOf(accountAction) + "&"+ String.valueOf(accountId);
		//查看缓存中是否存了二维码
		String key = "official_scene_ticket@"+sceneValue;
		String string = jedis.get(key);
		if(StringUtils.isNotEmpty(string)) {
			logger().info("公众号 " + appId + "创建二维码,缓存中存在" +string);
			return string;
		}

		WxMpQrcodeService qrcodeService = open().getWxOpenComponentService().getWxMpServiceByAppid(appId).getQrcodeService();
		WxMpQrCodeTicket qrCodeCreateTmpTicket = qrcodeService.qrCodeCreateTmpTicket(sceneValue, expireSeconds);
		// 获取的二维码ticket
		String ticket = qrCodeCreateTmpTicket.getTicket();
		logger().info("公众号 " + appId + "创建二维码ticket的值" + ticket);
		String qrCodePictureUrl = qrcodeService.qrCodePictureUrl(ticket);
		logger().info("公众号 " + appId + "通过ticket换取二维码的结果 " + qrCodePictureUrl);
		jedis.set(key, qrCodePictureUrl, expireSeconds);
		return qrCodePictureUrl;
	}

	/**
	 * 批量获取公众号关注用户信息--加入队列操作
	 * @param appId
	 * @param language
	 * @param sysId
	 * @param shopId
	 */
	public void batchGetUsersByRabbitMq(String appId, String language, Integer sysId,Integer shopId) {
		BindRabbitParam param=new BindRabbitParam();
		param.setAppId(appId);
		param.setLanguage(language);
		param.setSysId(sysId);
		logger().debug("批量获取公众号关注用户"+param.toString());
		saas.taskJobMainService.dispatchImmediately(param, BindRabbitParam.class.getName(),shopId,TaskJobEnum.MP_BIND_MA.getExecutionType());
		//rabbitTemplate.convertAndSend("bind.mamp.template", "bind.mamp.key", param);
	}

	/**
	 * 批量获取公众号用户信息
     *
	 * @param appId
	 * @param language
	 * @param sysId
	 */
	public void batchGetUsers(String appId, String language, Integer sysId) {
		// https://developers.weixin.qq.com/doc/offiaccount/User_Management/Getting_a_User_List.html
		// https://developers.weixin.qq.com/doc/offiaccount/User_Management/Get_users_basic_information_UnionID.html#UinonId
		if (language != null && language.equals(EN_US)) {
			language = "en";
		}
		try {
			String nextOpenid = null;
			WxMpUserService userService = open().getWxOpenComponentService().getWxMpServiceByAppid(appId)
					.getUserService();
			while (true) {
				WxMpUserList userList = userService.userList(nextOpenid);
				long total = userList.getTotal();
				logger().debug("appId:" + appId + "用户总数" + total);
				for (String opneid : userList.getOpenids()) {
					WxMpUser userInfo = userService.userInfo(opneid, language);
					logger().debug(userInfo.toString());
					MpOfficialAccountUserRecord record = MP_OFFICIAL_ACCOUNT_USER.newRecord();
					if (userInfo.getSubscribe()) {
						logger().debug("userInfo.getSubscribe()" + userInfo.getSubscribe());
					}
					record.setOpenid(userInfo.getOpenId());
					record.setAppId(appId);
					record.setSysId(sysId);
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
					addOrUpdateUser(appId, record, userInfo.getUnionId(), userInfo.getOpenId());
				}
				nextOpenid = userList.getNextOpenid();
				if (StringUtils.isEmpty(nextOpenid)) {
					break;
				}
			}
		} catch (WxErrorException e) {
			logger().debug(e.getMessage(), e);

		}
	}

	/**
	 * 添加或更新用户
     *
	 * @param appId
	 * @param record
	 * @param unionId
	 * @param openId
	 */
	public Integer addOrUpdateUser(String appId, MpOfficialAccountUserRecord record, String unionId, String openId) {
		logger().info("添加或更新用户");
		MpOfficialAccountUserRecord fetchAny = MP_OFFICIAL_ACCOUNT_USER.newRecord();
		if ((!StringUtils.isEmpty(appId)) && (!StringUtils.isEmpty(openId))) {
			// 只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。
			logger().info("appId："+appId+" openId:"+openId);
			fetchAny = db().selectFrom(MP_OFFICIAL_ACCOUNT_USER)
					.where(MP_OFFICIAL_ACCOUNT_USER.APP_ID.eq(appId).and(MP_OFFICIAL_ACCOUNT_USER.OPENID.eq(openId)))
					.fetchAny();
		} else if ((!StringUtils.isEmpty(appId)) && (!StringUtils.isEmpty(unionId))) {
			logger().info("appId："+appId+" unionId:"+unionId);
			fetchAny = db().selectFrom(MP_OFFICIAL_ACCOUNT_USER)
					.where(MP_OFFICIAL_ACCOUNT_USER.APP_ID.eq(appId).and(MP_OFFICIAL_ACCOUNT_USER.UNIONID.eq(unionId)))
					.fetchAny();
		}else {
			logger().info("addOrUpdateUser invalid data, data:");
			return 0;
		}
		int execute=0;
		if (fetchAny == null) {
			// 插入
			logger().info("插入");
			 execute = db().executeInsert(record);
		} else {
			// 更新
			logger().info("更新");
			record.setRecId(fetchAny.getRecId());
			execute = db().executeUpdate(record);
		}
		syncSubOfficialUser(appId, record, unionId, openId);
		return execute;
	}

	/**
	 * 公众号和小程序关系
     *
	 * @param appId
	 * @param userName
	 * @return
	 * @throws WxErrorException https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1513255108_gFkRI&token=&lang=zh_CN
	 */
	public Boolean wxamplinkget(String appId, String userName) throws WxErrorException {
		WxOpenMpampLinkService mpService = open.getMpExtService();
		WxOpenMiniLinkGetResult bindMiniInfo = mpService.getBindMiniInfo(appId);
		List<WxOpenMiniLinkGetItems> items = bindMiniInfo.getWxopens().getItems();
		for (WxOpenMiniLinkGetItems item : items) {
			if (userName.equals(item.getUsername())) {
				// 1：已关联；2：等待小程序管理员确认中；3：小程序管理员拒绝关联12：等到公众号管理员确认中；
				logger().info("公众号:" + appId + " 绑定的小程序userName:" + userName + "状态值:" + item.getStatus());
				if (item.getStatus() == 1) {
					return true;
				}
			}
		}
		return false;
	}

    /**
	 * 同步公众号用户到从库
     *
	 * @param appId
	 * @param record
	 * @param unionId
	 * @param openId
	 */
	public void syncSubOfficialUser(String appId, MpOfficialAccountUserRecord record, String unionId, String openId) {
		logger().info("同步公众号用户到从库");
		com.meidianyi.shop.db.shop.tables.records.MpOfficialAccountUserRecord into = record.into(com.meidianyi.shop.db.shop.tables.records.MpOfficialAccountUserRecord.class);
		Result<MpAuthShopRecord> officialAccountMps = saas.shop.mp.getOfficialAccountMps(appId);
		for (MpAuthShopRecord mpAuthShopRecord : officialAccountMps) {
			MpOfficialAccountUserByShop officialAccountUser = saas.getShopApp(mpAuthShopRecord.getShopId()).officialAccountUser;
			officialAccountUser.addOrUpdateUser(appId, into, unionId, openId);
		}

	}

    /**
     * 门店后台获得公众号场景二维码
     * @param user
     * @param appId
     * @return
     * @throws WxErrorException
     */
    public String generateThirdPartCodeForStore(StoreTokenAuthInfo user, String appId) throws WxErrorException {
        logger().info("传入appid为：{}",appId);
        // 主账户是1
        int accountAction = 3;
        int accountId = user.storeAccountId;
        int expireSeconds = 24 * 60 * 30 * 60 - 60;
        //$sceneValue = $shopId.'&'.$accountAction.'&'.$accountId;
        String sceneValue = user.loginShopId.toString() + "&" + String.valueOf(accountAction) + "&"+ String.valueOf(accountId);
        //查看缓存中是否存了二维码
        String key = "official_scene_ticket@"+sceneValue;
        String string = jedis.get(key);
        if(StringUtils.isNotEmpty(string)) {
            logger().info("公众号 " + appId + "创建二维码,缓存中存在" +string);
            return string;
        }

        WxMpQrcodeService qrcodeService = open().getWxOpenComponentService().getWxMpServiceByAppid(appId).getQrcodeService();
        WxMpQrCodeTicket qrCodeCreateTmpTicket = qrcodeService.qrCodeCreateTmpTicket(sceneValue, expireSeconds);
        // 获取的二维码ticket
        String ticket = qrCodeCreateTmpTicket.getTicket();
        logger().info("公众号 " + appId + "创建二维码ticket的值" + ticket);
        String qrCodePictureUrl = qrcodeService.qrCodePictureUrl(ticket);
        logger().info("公众号 " + appId + "通过ticket换取二维码的结果 " + qrCodePictureUrl);
        jedis.set(key, qrCodePictureUrl, expireSeconds);
        return qrCodePictureUrl;
    }
    /**
     * 小程序获得公众号场景二维码
     * @param wxAppSessionUser
     * @param appId
     * @return
     * @throws WxErrorException
     */
    public String generateThirdPartCodeForWx(WxAppSessionUser wxAppSessionUser, String appId) throws WxErrorException {
        logger().info("传入appid为：{}",appId);
        // 店员账号3
        int accountAction = 3;
        int accountId = wxAppSessionUser.getStoreAccountId();
        if(wxAppSessionUser.getUserType().equals(AuthConstant.AUTH_TYPE_DOCTOR_USER)){
            accountAction=4;
            accountId=wxAppSessionUser.getDoctorId();
        }

        int expireSeconds = 24 * 60 * 30 * 60 - 60;
        //$sceneValue = $shopId.'&'.$accountAction.'&'.$accountId;
        String sceneValue = wxAppSessionUser.getShopId().toString() + "&" + String.valueOf(accountAction) + "&"+ String.valueOf(accountId);
        //查看缓存中是否存了二维码
        String key = "official_scene_ticket@"+sceneValue;
        String string = jedis.get(key);
        if(StringUtils.isNotEmpty(string)) {
            logger().info("公众号 " + appId + "创建二维码,缓存中存在" +string);
            return string;
        }

        WxMpQrcodeService qrcodeService = open().getWxOpenComponentService().getWxMpServiceByAppid(appId).getQrcodeService();
        WxMpQrCodeTicket qrCodeCreateTmpTicket = qrcodeService.qrCodeCreateTmpTicket(sceneValue, expireSeconds);
        // 获取的二维码ticket
        String ticket = qrCodeCreateTmpTicket.getTicket();
        logger().info("公众号 " + appId + "创建二维码ticket的值" + ticket);
        String qrCodePictureUrl = qrcodeService.qrCodePictureUrl(ticket);
        logger().info("公众号 " + appId + "通过ticket换取二维码的结果 " + qrCodePictureUrl);
        jedis.set(key, qrCodePictureUrl, expireSeconds);
        return qrCodePictureUrl;
    }
}
