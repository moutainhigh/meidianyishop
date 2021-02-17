package com.meidianyi.shop.service.shop.user.user;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;

import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.dao.shop.user.UserDao;
import com.meidianyi.shop.dao.shop.patient.PatientDao;
import com.meidianyi.shop.dao.shop.patient.UserPatientCoupleDao;
import com.meidianyi.shop.db.main.tables.records.DictCityRecord;
import com.meidianyi.shop.db.main.tables.records.DictDistrictRecord;
import com.meidianyi.shop.db.main.tables.records.DictProvinceRecord;
import com.meidianyi.shop.db.shop.tables.User;
import com.meidianyi.shop.db.shop.tables.UserDetail;
import com.meidianyi.shop.db.shop.tables.records.ChannelRecord;
import com.meidianyi.shop.db.shop.tables.records.FriendPromoteActivityRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderVerifierRecord;
import com.meidianyi.shop.db.shop.tables.records.ShopCfgRecord;
import com.meidianyi.shop.db.shop.tables.records.UserCardRecord;
import com.meidianyi.shop.db.shop.tables.records.UserDetailRecord;
import com.meidianyi.shop.db.shop.tables.records.UserImportDetailRecord;
import com.meidianyi.shop.db.shop.tables.records.UserRecord;
import com.meidianyi.shop.service.foundation.jedis.JedisManager;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.saas.shop.version.VersionConfig;
import com.meidianyi.shop.service.pojo.shop.config.distribution.DistributionParam;
import com.meidianyi.shop.service.pojo.shop.distribution.UserTotalFanliVo;
import com.meidianyi.shop.service.pojo.shop.member.card.ValidUserCardBean;
import com.meidianyi.shop.service.pojo.shop.member.score.CheckSignVo;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.patient.PatientOneParam;
import com.meidianyi.shop.service.pojo.shop.prescription.PrescriptionDoctorVo;
import com.meidianyi.shop.service.pojo.shop.qrcode.QrCodeTypeEnum;
import com.meidianyi.shop.service.pojo.shop.user.detail.UserAssociatedDoctorParam;
import com.meidianyi.shop.service.pojo.shop.user.detail.UserAssociatedDoctorVo;
import com.meidianyi.shop.service.pojo.wxapp.account.UserAccountSetParam;
import com.meidianyi.shop.service.pojo.wxapp.account.UserAccountSetVo;
import com.meidianyi.shop.service.pojo.wxapp.account.UserInfo;
import com.meidianyi.shop.service.pojo.wxapp.account.UserSysVo;
import com.meidianyi.shop.service.pojo.wxapp.account.WxAppAccountParam;
import com.meidianyi.shop.service.pojo.wxapp.login.WxAppLoginParam;
import com.meidianyi.shop.service.pojo.wxapp.login.WxAppLoginParam.PathQuery;
import com.meidianyi.shop.service.pojo.wxapp.login.WxAppSessionUser;
import com.meidianyi.shop.service.pojo.wxapp.login.WxAppSessionUser.WxUserInfo;
import com.meidianyi.shop.service.saas.shop.ShopImageManageService;
import com.meidianyi.shop.service.shop.config.ConfigService;
import com.meidianyi.shop.service.shop.coupon.CouponService;
import com.meidianyi.shop.service.shop.department.DepartmentService;
import com.meidianyi.shop.service.shop.distribution.UserTotalFanliService;
import com.meidianyi.shop.service.shop.goods.FootPrintService;
import com.meidianyi.shop.service.shop.image.QrCodeService;
import com.meidianyi.shop.service.shop.member.UserCardService;
import com.meidianyi.shop.service.shop.member.wxapp.WxUserCardService;
import com.meidianyi.shop.service.shop.order.OrderReadService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import com.meidianyi.shop.service.shop.order.inquiry.InquiryOrderService;
import com.meidianyi.shop.service.shop.prescription.PrescriptionService;
import com.meidianyi.shop.service.shop.store.store.StoreService;
import com.meidianyi.shop.service.shop.user.user.collection.UserCollectionService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.open.api.WxOpenMaService;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.meidianyi.shop.db.shop.Tables.DOCTOR;
import static com.meidianyi.shop.db.shop.Tables.SHOP_CFG;
import static com.meidianyi.shop.db.shop.tables.User.USER;
import static com.meidianyi.shop.db.shop.tables.UserCard.USER_CARD;
import static com.meidianyi.shop.db.shop.tables.UserDetail.USER_DETAIL;
import static com.meidianyi.shop.service.pojo.shop.user.UserInviteSourceConstant.*;

/**
 * @author lixinguo
 */
@Service
public class UserService extends ShopBaseService {

    public static final String WAIT_PAY = "wait_pay";
    public static final String WAIT_RECEIVE = "wait_receive";
    public static final String WAIT_CONFIRM = "wait_confirm";
    public static final String WAIT_DELIVERY = "wait_delivery";
    public static final String SHIPPED = "shipped";
    public static final String REFUND = "refund";
    public static final String RETURNING = "returning";
    @Autowired
	public UserDetailService userDetail;

	@Autowired
	public UserCardService userCard;

	@Autowired
	public CouponService coupon;

	@Autowired
	public StoreService storeService;

	@Autowired
	public UserCollectionService collection;

	@Autowired
	private OrderInfoService orderInfo;

	@Autowired
	private FootPrintService footPrintService;

	@Autowired
	private ConfigService config;

	@Autowired
	public ShopImageManageService image;

	@Autowired
	public QrCodeService qrCode;

	@Autowired
	public OrderReadService orderReadService;

	@Autowired
	public WxUserCardService wxUserCardService;

	@Autowired
	private UserTotalFanliService userTotalFanliService;
	@Autowired
	private UserPatientCoupleDao userPatientCoupleDao;
	@Autowired
	protected PatientDao patientDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private PrescriptionService prescriptionService;
	@Autowired
	private InquiryOrderService inquiryOrderService;


    private int[] userActiveEnter = { 1001, 1005, 1006, 1019, 1020, 1024, 1026, 1027, 1023, 1028, 1034, 1035, 1037,
			1038, 1042, 1014, 1043, 1045, 1046, 1052, 1053, 1056, 1057, 1058, 1064, 1067, 1068, 1071, 1072, 1073, 1074,
			1078, 1079, 1081, 1082, 1084, 1089, 1090, 1091, 1092, 1095, 1097, 1102, 1039, 1103, 1104, 1129, 1099, 1059,
			1054, 1022, 1030 };

	private int[] userPassiveEnter = { 1007, 1008, 1017, 1029, 1036, 1044, 1047, 1048, 1049, 1096 };

	private int[] scanqrode = { 1011, 1012, 1013, 1025, 1031, 1032 };

	final static private String SESSION_SIGN_KEY = "weipubao!@#miniprogram";

	final protected String userCenterJson = "user.center.json";

	public static final Byte SYCUPDATE= 1;
	public static final Byte SYCINSERT= 0;

	private static final Byte IS_SETTING_STATUS = 2;

	private static final Integer DEFAULT_PROVINCE_ID = 100000;

	private static final Long DEFAULT_NEW_USER_TIME = 120L;

	@Autowired
	protected JedisManager jedis;

	/**
	 * 通过openId获取用户
	 *
	 * @param openId
	 * @return
	 */
	public UserRecord getUserByOpenId(String openId) {
		return db().fetchAny(USER, USER.WX_OPENID.eq(openId));
	}

	/**
	 * 通过userId获取用户
	 *
	 * @param userId
	 * @return
	 */
	public UserRecord getUserByUserId(Integer userId) {
		return db().fetchAny(USER, USER.USER_ID.eq(userId));
	}

	/**
	 * 登陆用户 TODO:简单登陆，以后添加复杂功能。
	 *
	 * @param loginUser
	 * @return
	 * @throws WxErrorException
	 */
	public UserRecord loginGetUser(WxAppLoginParam loginUser) throws WxErrorException {
		Integer shopId = this.getShopId();
		WxOpenMaService maService = saas.shop.mp.getMaServiceByShopId(shopId);
		WxMaJscode2SessionResult result = maService.jsCode2SessionInfo(loginUser.getCode());
		logger().info("获取登录后的session信息. " + result.toString());
		UserRecord record = null;
		if (StringUtils.isNotEmpty(result.getOpenid())) {
			logger().info("进入getUserId");
			record = getUserId(result.getOpenid(), loginUser.getName(), loginUser.getAvatar(), loginUser.getPathQuery(),
					result);
		}
		// 记录unionid
		if (record != null && StringUtils.isNotEmpty(result.getUnionid())) {
			if (!result.getUnionid().equals(record.getWxUnionId())) {
				record.setWxUnionId(result.getUnionid());
				int update = record.update();
				//syncMainUser(record);
				logger().info("更新unionid" + update);
				// TODO syncUserToCrm
			}
		}
		if (StringUtils.isNotEmpty(loginUser.getSystemVersion())
				&& (!loginUser.getSystemVersion().equals(record.getDevice()))) {
			record.setDevice(loginUser.getSystemVersion());
			int update = record.update();
			//syncMainUser(record);
			logger().info("更新Device" + update);
		}
		String sessionKey = getSessionKey(shopId, record.getUserId());
		jedis.set(sessionKey, result.getSessionKey(), 60 * 60 * 24);
		logger().info("更新sessionKey："+sessionKey);
		return record;
	}

    /**
     * 得到当前用户类型
     * @param userId 用户id
     * @return Byte
     */
    public Byte getUserType(Integer userId){
        return db().select(USER.USER_TYPE).from(USER).where(USER.USER_ID.eq(userId)).fetchOneInto(Byte.class);
    }

    /**
     * 得到当前医师
     * @param userId 用户id
     * @return String
     */
    public String getDoctorId(Integer userId){
        return db().select(DOCTOR.HOSPITAL_CODE).from(DOCTOR).where(DOCTOR.USER_ID.eq(userId)).fetchOneInto(String.class);
    }

	private String getSessionKey(Integer shopId, Integer userId) {
		return SESSION_SIGN_KEY + Util.md5(shopId + "_" + userId);
	}

	public List<UserRecord> getUserRecordByIds(List<Integer> idList) {
		return db().select(USER.WX_OPENID, USER.WX_UNION_ID, USER.USER_ID).from(USER).where(USER.USER_ID.in(idList))
				.fetchInto(USER);
	}


    /**
     * 根据openid获取user_id
     */
    public UserRecord getUserId(String openid, String userName, String avatar, PathQuery pathQuery, WxMaJscode2SessionResult result) {
		UserRecord ret = db().selectFrom(USER).where(USER.WX_OPENID.eq(openid)).fetchAny();
		if (ret != null) {
            boolean canUpdateUserName = StringUtils.isNotEmpty(userName)
                && (ret.getWxOpenid().equals(ret.getUsername()) || (!ret.getUsername().equals(userName)));
            if (canUpdateUserName) {
				db().update(USER).set(USER.USERNAME, userName).where(USER.WX_OPENID.eq(openid)).execute();
				ret.setWxOpenid(openid);
			}
			if (StringUtils.isNotBlank(avatar)) {
				UserDetailRecord userDetail = this.getUserDetail(ret.getUserId());
				if (userDetail != null) {
					if (StringUtils.isEmpty(userDetail.getUserAvatar())
							|| (!avatar.equals(userDetail.getUserAvatar()))) {
						db().update(USER_DETAIL).set(USER_DETAIL.USERNAME, userName)
								.set(USER_DETAIL.USER_AVATAR, avatar).where(USER_DETAIL.USER_ID.eq(ret.getUserId()))
								.execute();
						userDetail.setUserAvatar(avatar);
						userDetail.setUsername(userName);
					}
				}
			}
			logger().info(openid + " 老用户");
			return ret;
		} else {
			logger().info(openid + " 新用户");
			// 新用户
			Map<String, String> source = getInviteSource(pathQuery);
			// 根据微信场景值判断会员来源
			int userSource = getuserSource(pathQuery.getScene());
			// 主库是否插入
			UserRecord user = db().newRecord(USER);
			user.setWxOpenid(openid);
			user.setScene(userSource);
			user.setUsername(userName == null ? openid : userName);
			if (!source.isEmpty()) {
				logger().info("邀请来源不空" + source.toString());
				user.setInviteSource(source.get("invite_source"));
				String string = source.get("invite_act_id");
				logger().info("活动id：{}",string);
				user.setInviteActId(Integer.parseInt(string == null ? "0" : string));
			} else {
				logger().info("邀请来源为空");
			}
			String unionId = (!StringUtils.isBlank(result.getUnionid())) ? result.getUnionid() : "";
			user.setWxUnionId(unionId);
			int insert = user.insert();
            user.refresh();
			logger().info("插入user结果 " + insert);
			if (insert < 0) {
				return null;
			}
			Integer userId = getUserByOpenId(openid).getUserId();
			if (userName == null) {
				String name = "用户" + (10000 + userId);
				user.setUsername(name);
				user.update();
				logger().info("更新用户名为" + name);
			}
            saveUserDetail(avatar, user, userId);
            logger().info("开始同步Detail");
			String path = pathQuery.getPath();
			Map<String, String> query = pathQuery.getQuery();
			String groupDrawId = pathQuery.getQuery().get("group_draw_id");
            String pingLotteryInfoPath = "pages1/pinlotteryinfo/pinlotteryinfo";
            String pingLotterInfoPath2 = "pages/pinlotteryinfo/pinlotteryinfo";
            boolean isPinLotteryInvite = (path.equals(pingLotteryInfoPath) || path.equals(pingLotterInfoPath2)) && groupDrawId != null
                && pathQuery.getQuery().get("invite_id") != null;
            if (isPinLotteryInvite) {
				pathQuery.getQuery().put("user_id", userId.toString());
				saas.getShopApp(this.getShopId()).groupDraw.groupDrawInvite.createInviteRecord(path,  Integer.valueOf(groupDrawId),query, (byte) 1);
			}
            String indexPath = "pages/index/index";
            String itemPath = "pages/item/item";
            final boolean c = path.equals(indexPath) || path.equals(itemPath) && pathQuery.getQuery().get("c") != null;
            if (c) {
				saas.getShopApp(getShopId()).channelService.recordChannel(pathQuery.getQuery().get("c"), userId, (byte) 1);
			}
			return user;
		}
	}

    private void saveUserDetail(String avatar, UserRecord user, Integer userId) {
        UserDetailRecord uDetailRecord = db().newRecord(USER_DETAIL);
        uDetailRecord.setUserId(userId);
        uDetailRecord.setUsername(user.getUsername());
        uDetailRecord.setUserAvatar(avatar == null ? "/image/admin/head_icon.png" : avatar);
        uDetailRecord.insert();
    }

    public UserDetailRecord getUserDetail(Integer userId) {
		return db().fetchAny(USER_DETAIL, USER_DETAIL.USER_ID.eq(userId));
	}

	public int getuserSource(Integer scene) {
	    // 未获取
		int userSource = -4;
		if (IntStream.of(userActiveEnter).anyMatch(x -> x == scene)) {
		    // 主动进入
			userSource = -1;
		}
		if (IntStream.of(userPassiveEnter).anyMatch(x -> x == scene)) {
            // 被动进入
		    userSource = -2;
		}
		if (IntStream.of(scanqrode).anyMatch(x -> x == scene)) {
            // 扫码进入
		    userSource = -3;
		}
		return userSource;
	}

	public Map<String, String> getInviteSource(PathQuery pathQuery) {
		String path = pathQuery.getPath();
		logger().info("登录路径"+path);
		logger().info("pathQuery的内容：{}",pathQuery.toString());
        int initialCapacity = 16;
        Map<String, String> map = new HashMap<String, String>(initialCapacity);
        if (path.equals(GROUP_BUY_ITEM_PATH) || path.equals(GROUP_BUY_INFO_PATH)) {
			map.put("invite_source", "groupbuy");// 多人拼团
			map.put("invite_act_id", pathQuery.getQuery().get("pin_group_id"));
		}
        if (path.equals(BARGAIN_LIST_PATH) || path.equals(BARGAIN_INFO_PATH)) {
			map.put("invite_source", "bargain");// 砍价
			map.put("invite_act_id", pathQuery.getQuery().get("bargain_id"));
		}
        if (path.equals(SEC_KILL_ITEM_PATH)) {
			map.put("invite_source", "seckill");// 秒杀 跟item合并了，没用
			map.put("invite_act_id", pathQuery.getQuery().get("sk_id"));
		}
        if (path.equals(INTEGRAL_PATH)) {
			map.put("invite_source", "integral");// 积分购买
			map.put("invite_act_id", pathQuery.getQuery().get("integral_goods_id"));
		}
        if (path.equals(LOTTERY_PATH)) {
			map.put("invite_source", "lottery");// 抽奖
			map.put("invite_act_id", pathQuery.getQuery().get("lotteryId"));
		}
        if (path.equals(FORM_PATH)) {
			map.put("invite_source", "form");// 表单
			map.put("invite_act_id", pathQuery.getQuery().get("page_id"));
		}
        if (path.equals(CARD_INFO_PATH)) {
			map.put("invite_source", "membercard");// 会员卡
			map.put("invite_act_id", pathQuery.getQuery().get("cardNo"));
		}
        if (path.equals(ITEM_PATH)) {
            String paramChanel = "c";
            if (!StringUtils.isEmpty(pathQuery.getQuery().get(paramChanel))) {
				// 渠道页
				ChannelRecord channelInfo = saas.getShopApp(this.getShopId()).channelService
						.getChannelInfo(pathQuery.getQuery().get(paramChanel));
				if (channelInfo != null) {
					map.put("invite_source", PARAM_CHANNEL);
					map.put("invite_act_id", channelInfo.getId().toString());
				} else {
					map.put("invite_source", "scanqrcode");
					// TODO php上有问题 app/Service/Shop/User/User/User.php
					map.put("invite_act_id","0");
				}
			}
            if (!StringUtils.isEmpty(pathQuery.getQuery().get(PARAM_SHARE_AWARD))) {
				map.put("invite_source", "share_award");// 分享有礼
                String paramShareId = "share_id";
                map.put("invite_act_id", pathQuery.getQuery().get(paramShareId));
			} else {
				map.put("invite_source", "goods"); // 商品
                String paramGoodId = "good_id";
                map.put("invite_act_id", pathQuery.getQuery().get(paramGoodId));
			}
		}
        return getInviteSourceToContinue(pathQuery, map);
	}

	public Map<String, String> getInviteSourceToContinue(PathQuery pathQuery,Map<String, String> map) {
        String path = pathQuery.getPath();
        if (path.equals(INDEX_PATH)) {
            // TODO 有问题
            ChannelRecord channelInfo = saas.getShopApp(this.getShopId()).channelService
                .getChannelInfo(pathQuery.getQuery().get(PARAM_CHANNEL));
            if (channelInfo != null) {
                map.put("invite_source", PARAM_CHANNEL);
                map.put("invite_act_id", channelInfo.getId().toString());
            } else {
                map.put("invite_source", "scanqrcode");
                // TODO php上有问题 app/Service/Shop/User/User/User.php
                map.put("invite_act_id", "1");
            }
        }
        if (path.equals(MAIN_GOODS_LIST_PATH)) {
            map.put("invite_source", "purchase_price");// 加价购
            map.put("invite_act_id", pathQuery.getQuery().get(PARAM_IDENTITY_ID));
        }
        if (path.equals(FULL_PRICE_PATH)) {
            map.put("invite_source", "purchase_price");// 满折满减
            map.put("invite_act_id", pathQuery.getQuery().get(PARAM_IDENTITY_ID));
        }
        if (path.equals(PIN_LOTTER_INFO_PATH)||path.equals(PIN_LOTTERY_INFO_PATH2)) {
            map.put("invite_source", "group_draw");// 拼团抽奖
            String paramGroupDrawId = "group_draw_id";
            map.put("invite_act_id", pathQuery.getQuery().get(paramGroupDrawId));
        }
        if (path.equals(PIN_INTEGRATION_PATH)) {
            map.put("invite_source", "pin_integration");// 拼团抽奖
            String paramPid = "pid";
            map.put("invite_act_id", pathQuery.getQuery().get(paramPid));
        }
        if (path.equals(PROMOTE_INFO_PATH)) {
            map.put("invite_source", "friend_promote");// 好友助力
            String paramActCode = "actCode";
            FriendPromoteActivityRecord promoteInfo = saas.getShopApp(this.getShopId()).friendPromoteService
                .promoteInfo(0, pathQuery.getQuery().get(paramActCode));
            map.put("invite_act_id", promoteInfo.getId().toString());
        }
        return map;
    }

	/**
	 * 更新用户昵称，头像
	 *
	 * @param param
	 * @return
	 */
	public boolean updateUser(WxAppAccountParam param,String tokenPrefix) {
		Integer userId = param.getUserId();
		String username = param.getUsername();
		String userAvatar = param.getUserAvatar();
		UserRecord record = getUserByUserId(userId);
		UserDetailRecord userDetailRecord = getUserDetail(userId);
		// 更新昵称
        boolean canUpdateUserName = StringUtils.isNotEmpty(username)
            && (StringUtils.isEmpty(record.getUsername())) || (!username.equals(record.getUsername()));
        if (canUpdateUserName) {
			db().update(USER).set(USER.USERNAME, username).where(USER.USER_ID.eq(param.getUserId())).execute();
			db().update(USER_DETAIL).set(USER_DETAIL.USERNAME, username)
					.where(USER_DETAIL.USER_ID.eq(param.getUserId())).execute();
			logger().info("更新昵称");
			record.setUsername(username);
			//syncMainUser(record);
			if(userDetailRecord!=null) {
				userDetailRecord.setUsername(username);
				//syncMainUserDetail(userDetailRecord);
			}

		}
		// 更新头像
        boolean canUpdateAvatar = StringUtils.isNotEmpty(userAvatar) && (StringUtils.isEmpty(userDetailRecord.getUserAvatar())
            || (!userAvatar.equals(userDetailRecord.getUserAvatar())));
        if (canUpdateAvatar) {
			db().update(USER_DETAIL).set(USER_DETAIL.USER_AVATAR, userAvatar)
					.where(USER_DETAIL.USER_ID.eq(param.getUserId())).execute();
			userDetailRecord.setUserAvatar(userAvatar);
			//syncMainUserDetail(userDetailRecord);
		}
		Integer shopId = this.getShopId();
		WxOpenMaService maService = saas.shop.mp.getMaServiceByShopId(shopId);
		String sessionKey2 = getSessionKey(shopId, userId);
		String sessionKey = jedis.get(sessionKey2);
		logger().info("获取sessionKey："+sessionKey2+"结果"+StringUtils.isEmpty(sessionKey));
		WxMaUserInfo userInfo = maService.getUserService().getUserInfo(sessionKey,
				param.getEncryptedData(), param.getIv());
		if (userInfo != null) {
			logger().info("获取用户信息"+userInfo.toString());
			if (!userInfo.getUnionId().equals(record.getWxUnionId())) {
				db().update(USER).set(USER.WX_UNION_ID, userInfo.getUnionId()).where(USER.USER_ID.eq(userId)).execute();
				record.setWxUnionId(userInfo.getUnionId());
				//syncMainUser(record);
				// TODO $crmResult = shop($shopId)->serviceRequest->crmApi->init();
			}
		} else {
			logger().error("wxDecryptData error:" + param.toString());
			return false;
		}
		String token = tokenPrefix + Util.md5(shopId + "_" +userId);
		String json = jedis.get(token);
		WxAppSessionUser parseJson = Util.parseJson(json, WxAppSessionUser.class);
		if((!parseJson.getUserAvatar().equals(userAvatar))||(!parseJson.getUsername().equals(username))) {
			logger().info("redis更新用户名和头像地址");
			parseJson.setUserAvatar(userAvatar);
			parseJson.setUsername(username);
			jedis.set(token, Util.toJson(parseJson));
		}
		return true;
	}

	/**
	 * 个人中心数据
	 *
	 * @param userId
	 * @return
	 */
	public List<Map<String, Object>> parseCenterModule(Integer userId) {
		List<Map<String, Object>> moduleData = getCenterModule();
		UserRecord userByUserId = getUserByUserId(userId);
		if(userByUserId==null) {
			return null;
		}

		for(Map<String, Object> module:moduleData) {
            String centerHeaderModule = "center_header";
            if (module.get("module_name").equals(centerHeaderModule)) {
				logger().info("进入center_header");
//				module=parseCenterHeader(userId, module);
				logger().info("完成center_header");
			}
            String accountMoneyModule = "account_money";
            if (module.get("module_name").equals(accountMoneyModule)) {
				logger().info("进入account_money");
				module.put("content", parseAccountMoney(userByUserId, (List<Map<String, Object>>)module.get("content")));
				logger().info("完成account_money");
			}
            String orderModule = "order";
            if (module.get("module_name").equals(orderModule)) {
				logger().info("进入order");
				module.put("content", parseMyOrder(userByUserId.getUserId(), (List<Map<String, Object>>)module.get("content")));
				logger().info("完成order");
			}
            String appointmentModule = "appointment";
            if (module.get("module_name").equals(appointmentModule)) {
				logger().info("进入appointment");
				module.put("appointment_info", storeService.serviceOrder.getUserLastOrderInfo(userId));
				logger().info("完成appointment");
			}
            String useRecordModule = "use_record";
            if (module.get("module_name").equals(useRecordModule)) {
				logger().info("进入use_record");
				module.put("collect", collection.getUserCollectNumber(userId));
				module.put("buy_history", orderInfo.getUserBuyGoodsNum(userId));
				module.put("footprint", footPrintService.getfootPrintNum(userId));
				logger().info("完成use_record");
			}
            String serviceModule = "service";
            if (module.get("module_name").equals(serviceModule)) {
				logger().info("进入service");
				module.put("content", parseMyService(userByUserId, (List<Map<String, Object>>)module.get("content")));
				logger().info("完成service");
			}
            String currentPatientModule = "current_patient";
            if (module.get("module_name").equals(currentPatientModule)) {
				logger().info("进入当前就诊人");
				module.put("content", currentPatient(userByUserId.getUserId()));
				logger().info("完成当前就诊人");
			}
			if(StringUtils.isNotEmpty(String.valueOf(module.get("bg_img")))) {
				module.put("bg_img", image.imageUrl(String.valueOf(module.get("bg_img"))));
			}
		}
		logger().info("parseCenterModule出");
		return moduleData;
	}

	/**
	 * 当前患者
	 * @param userId
	 * @return
	 */
	private PatientOneParam currentPatient(Integer userId) {
		Integer patientId = userPatientCoupleDao.defaultPatientIdByUser(userId);
		PatientOneParam oneInfo = patientDao.getOneInfo(patientId);
		return oneInfo;
	}

	/**
	 * 我的服务
	 * @param user
	 * @param data
	 * @return
	 */
	public List<Map<String, Object>> parseMyService(UserRecord user, List<Map<String, Object>> data) {
		for (Map<String, Object> iconItem : data) {
			iconItem.put("icon", image.imageUrl(String.valueOf(iconItem.get("icon"))));
            String distributionIconName = "distribution";
            if (iconItem.get("icon_name").equals(distributionIconName)) {
				// $default = '{"status":0,"judge_status":0,"rank_status":0,"protect_date":-1}';

				DistributionParam rebate = config.distributionCfg.getDistributionCfg();
				if(rebate==null) {
					rebate = new DistributionParam();
					rebate.setStatus((byte)0);
					rebate.setJudgeStatus((byte)0);
					rebate.setRankStatus((byte)0);
					rebate.setProtectDate((byte)-1);
				}
				if (isOne(iconItem.get("show_type")) && (!user.getIsDistributor().equals(SYCUPDATE))
						&& Objects.equals(rebate.getStatus(), SYCUPDATE)
						&& Objects.equals(rebate.getJudgeStatus(), SYCUPDATE)) {
					iconItem.put("is_show", 0);

				}else {
					iconItem.put("is_distributor", user.getIsDistributor());
					iconItem.put("judge_status", rebate.getJudgeStatus());
					iconItem.put("is_rebate", rebate.getStatus());
					iconItem.put("withdraw_status", rebate.getWithdrawStatus());
					UserTotalFanliVo userRebate = userTotalFanliService.getUserRebate(user.getUserId());
					BigDecimal totalMoney = userRebate.getTotalMoney()==null?new BigDecimal("0"):userRebate.getTotalMoney();
					BigDecimal account = user.getAccount();
					iconItem.put("withdraw_money", totalMoney.compareTo(account)==-1?totalMoney:account);
					iconItem.put("rebate_center_name", rebate.getRebateCenterName()==null?"分销中心":rebate.getRebateCenterName());
					if(!isOne(rebate.getWithdrawConfig())) {
						iconItem.put("withdraw_money", 0);
					}
				}
			}
            String userActivateIconName = "user_activate";
            if (iconItem.get("icon_name").equals(userActivateIconName)) {
				Boolean activateIsNotice = isNoticeUserActivation(user.getUserId());
				iconItem.put("activate_is_notice", activateIsNotice);
				iconItem.put("is_show", isOne(iconItem.get("is_show"))?(activateIsNotice?1:0):0);
			}
            String orderVerifyIconName = "order_verify";
            if (iconItem.get("icon_name").equals(orderVerifyIconName)) {
				Result<OrderVerifierRecord> oRecords = storeService.storeVerifier.getUserVerifyStores(user.getUserId());
				iconItem.put("is_verifier", oRecords.size()>0?1:0);
				iconItem.put("is_show", isOne(iconItem.get("is_show"))?iconItem.get("is_verifier"):0);
			}
		}
		return data;
	}


	private Boolean isOne(Object object) {
		return Objects.equals(object, 1)|| Objects.equals(object, "1");
	}

	/**
	 * 我的订单
	 * @param userId
	 * @param data
     * @return
	 */
	public List<Map<String, Object>> parseMyOrder(Integer userId, List<Map<String, Object>> data) {
		Map<Byte, Integer> orderStatusNum = orderReadService.statistic(userId);
		for (Map<String, Object> iconItem : data) {
			iconItem.put("icon", image.imageUrl(String.valueOf(iconItem.get("icon"))));
			if (iconItem.get("icon_name").equals(WAIT_PAY)) {
				//代付款
				iconItem.put("num", orderStatusNum.get(OrderConstant.WAIT_PAY));
			}
			if (iconItem.get("icon_name").equals(WAIT_RECEIVE)) {
				//待收货
				iconItem.put("num", orderStatusNum.get(OrderConstant.SHIPPED));
			}
			if (iconItem.get("icon_name").equals(WAIT_CONFIRM)) {
				//待审核
				iconItem.put("num", orderStatusNum.get(OrderConstant.AUDIT));
			}
			if (iconItem.get("icon_name").equals(WAIT_DELIVERY)) {
				//代发货
				iconItem.put("num", orderStatusNum.get(OrderConstant.WAIT_DELIVERY));
			}
			if (iconItem.get("icon_name").equals(SHIPPED)) {
				//已发货
				iconItem.put("num", orderStatusNum.get(OrderConstant.SHIPPED));
			}
			if (iconItem.get("icon_name").equals(REFUND)) {
				//退款退款
				iconItem.put("num", orderStatusNum.get(OrderConstant.REFUND));
			}
			if (iconItem.get("icon_name").equals(RETURNING)) {
				//已取消 (未支付取消)
				iconItem.put("num", orderStatusNum.get(OrderConstant.RETURNING));
			}
		}
		return data;
	}


	/**
	 * 我的资产
	 * @param record
	 * @param data
	 * @return
	 */
	public List<Map<String, Object>> parseAccountMoney(UserRecord record,List<Map<String, Object>> data) {
		logger().info("我的资产");
		logger().info("UserRecord"+record);
		for (Map<String, Object> iconItem : data) {
            String accountIconName = "account";
            if(iconItem.get("icon_name").equals(accountIconName)) {
				iconItem.put("num", record.getAccount()==null?0:record.getAccount());
			}
            String scoreIconName = "score";
            if(iconItem.get("icon_name").equals(scoreIconName)) {
				iconItem.put("num", record.getScore()==null?0:record.getScore());
			}
            String couponIconName = "coupon";
            if(iconItem.get("icon_name").equals(couponIconName)) {
				iconItem.put("num", this.coupon.getCanUseCouponNum(record.getUserId()));
			}
            String cardIconName = "card";
            if(iconItem.get("icon_name").equals(cardIconName)) {
				List<ValidUserCardBean> cardList = userCard.userCardDao.getValidCardList(record.getUserId());
				iconItem.put("num", cardList!=null?cardList.size():0);
			}
		}
		logger().info("我的资产结束");
		return data;
	}


	/**
	 * 个人信息
	 *
	 * @param userId
	 * @param data
	 * @return
	 */
	public Map<String, Object> parseCenterHeader(Integer userId, Map<String, Object> data) {
		// checkSignInScore
		CheckSignVo checkData = userCard.scoreService.checkSignInScore(userId);
		data.put("sign_score", checkData);
		data.put("qrcode", qrCode.getMpQrCode(QrCodeTypeEnum.PAGE_BOTTOM,"invite_id="+userId));
		logger().info("用户等级判断");
		// 用户等级判断
		String userGrade = userCard.getCurrentAvalidGradeCard(userId);
		logger().info("用户等级"+userGrade);
		if (StringUtils.isBlank(userGrade)) {
		    // 检测到用户可领取的等级卡
			logger().info("用户目前无等级卡，查询获取用户可升级的等级会员卡ID");
			try {
				Integer cardId = userCard.updateGrade(userId, null, (byte) 0);
				data.put("get_grade", cardId);
				logger().info("检测到用户可升级到的会员卡cardId的值为"+cardId);
			} catch (Exception e) {
				logger().error("userGrade为0时报错");
				e.printStackTrace();
			}

		} else {
		    //  直接检测并自动升级
			logger().info("直接检测并自动升级");
			try {
                int isGet = userCard.updateGrade(userId, null, (byte) 2);
				logger().info("isGet的值为"+isGet);
				if (isGet > 0) {
                    logger().info("已经自动升级到等级会员卡："+isGet);
                    data.put("get_grade", 0);
				} else {
					data.put("get_grade", isGet);
				}
			} catch (Exception e) {
				logger().error("userGrade不为0时报错");
				e.printStackTrace();
			}
		}
		logger().info("用户等级判断返回"+data);
		return data;
	}

	/**
	 * 获得个人中心配置数据
	 *
	 * @return
	 */
	public List<Map<String, Object>> getCenterModule() {
		logger().info("进入获得个人中心配置数据");
		ShopCfgRecord record = db().selectFrom(SHOP_CFG).where(SHOP_CFG.K.eq("user_center")).fetchAny();
		String data = null;
		List<Map<String, Object>> parseJson = new ArrayList<Map<String, Object>>();
		if (record == null || StringUtils.isEmpty(record.getV())) {
			data = Util.loadResource(userCenterJson);
		}else {
			data=record.getV();
		}
		VersionConfig mergeVersion = saas.shop.version.mergeVersion(getShopId());
		if (mergeVersion.getMainConfig().getSub4().size() > 0) {
			List<String> sub4 = mergeVersion.getMainConfig().getSub4();
			List<String> iconNames = new ArrayList<String>();
			iconNames.add("distribution");
			iconNames.add("bargain");
			parseJson = Util.parseJson(data, List.class);
			logger().info("读取配置完,size"+parseJson.size());
			for (Map<String, Object> module : parseJson) {
                String serviceModule = "service";
                if (module.get("module_name").equals(serviceModule)) {
					List<Map<String, Object>> object = (List<Map<String, Object>>) module.get("content");
					for (Map<String, Object> iconName : object) {
						if (iconNames.contains(iconName.get("iconName"))
								&& (!sub4.contains(iconName.get("iconName")))) {
							iconName.put("is_show", 0);
						}
					}
				}
			}
		}
		logger().info("getCenterModule运行完");
		return parseJson;
	}


	/**
	 * 是否展示激活公告
	 * @param userId
	 * @return
	 */
	public Boolean isNoticeUserActivation(Integer userId) {
		logger().info("是否展示激活公告");
		Boolean isEnable = checkModuleIsShow("service", "user_activate");
		logger().info("开关状态：{}",isEnable);
		if(!isEnable) {
			return false;
		}
		UserRecord user = getUserByUserId(userId);
		if(StringUtils.isEmpty(user.getMobile())) {
			logger().info("用户手机号为空");
			return true;
		}

		UserImportDetailRecord importUser = userCard.scoreService.member.getUserByMobile(user.getMobile());
		if(importUser==null) {
			logger().info("该手机号：{}，导入表没数据",user.getMobile());
			return false;
		}
		if(importUser.getIsActivate().equals((byte)1)) {
			logger().info("已激活");
			return false;
		}
		return true;

	}




	/**
	 * 检查模块开关是否开启
	 * @param moduleName
	 * @param iconName
	 * @return
	 */
	public Boolean checkModuleIsShow(String moduleName, String iconName) {
		List<Map<String, Object>> moduleData = getCenterModule();
		for (Map<String, Object> module : moduleData) {
			if (module.get("module_name").equals(moduleName) && isOne(module.get("is_show"))) {
				if (StringUtils.isNotEmpty(iconName)) {
					List<Map<String, Object>> cList = (List<Map<String, Object>>) module.get("content");
					for (Map<String, Object> iconItem : cList) {
						if (iconItem.get("icon_name").equals(iconName) && isOne(iconItem.get("is_show"))) {
							return true;
						}
					}
					return false;
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 用户详细信息
	 * @param userId
	 * @return
	 */
	public UserInfo getUserInfo(Integer userId) {
		User a = USER.as("a");
		UserDetail b = USER_DETAIL.as("b");
		User c = USER.as("c");
		return  db().select(a.USER_ID, a.USERNAME,a.INVITE_ID, a.USER_CID, a.MOBILE, a.USER_CODE, a.WX_OPENID, a.CREATE_TIME,a.WECHAT,
				a.FANLI_GRADE, a.USER_GRADE, a.ACCOUNT, a.DISCOUNT, a.WX_UNION_ID, a.DEVICE,a.UNIT_PRICE,a.IS_DISTRIBUTOR,
				a.INVITE_GROUP,a.DISTRIBUTOR_LEVEL,a.INVITE_TIME,a.DISCOUNT_GRADE, a.DEL_FLAG, a.DEL_TIME, a.GROWTH, a.SCORE,a.INVITATION_CODE,
				b.SEX, b.BIRTHDAY_YEAR, b.BIRTHDAY_MONTH, b.BIRTHDAY_DAY, b.REAL_NAME, b.PROVINCE_CODE,b.CITY_CODE, b.DISTRICT_CODE, b.ADDRESS, b.MARITAL_STATUS,
				b.MONTHLY_INCOME, b.CID,b.EDUCATION, b.INDUSTRY_INFO, b.BIG_IMAGE, b.BANK_USER_NAME, b.SHOP_BANK,b.BANK_NO, b.USER_AVATAR, c.USERNAME.as("invite_name")).from(a)
		.leftJoin(b).on(a.USER_ID.eq(b.USER_ID)).leftJoin(c).on(a.INVITE_ID.eq(c.USER_ID)).where(a.USER_ID.eq(userId)).fetchAny().into(UserInfo.class);
	}


	/**
	 * 账号设置
	 * @param param
	 * @param user
	 * @return
	 */
	public JsonResultCode accountSetting( UserAccountSetParam param,WxAppSessionUser user) {
		UserInfo userInfo = getUserInfo(user.getUserId());
		UserDetailRecord userDetailRecord=db().newRecord(USER_DETAIL);
		userDetailRecord.setUserId(userInfo.getUserId());
		userDetailRecord.setBirthdayYear(param.getBirthdayYear());
		userDetailRecord.setBirthdayMonth(param.getBirthdayMonth());
		userDetailRecord.setBirthdayDay(param.getBirthdayDay());
		userDetailRecord.setRealName(param.getRealName());
		userDetailRecord.setSex(param.getSex());
		if (param.getIsSetting() == 1 || IS_SETTING_STATUS.equals(param.getIsSetting())) {
			String provinceName = param.getProvinceCode();
			String cityName = param.getCityCode();
			String districtName = param.getDistrictCode();
			if (StringUtils.isNotEmpty(provinceName) && StringUtils.isNotEmpty(cityName) && StringUtils.isNotEmpty(districtName)) {
				Integer provinceId=100000;
				//省
				DictProvinceRecord provinceRecord = saas.region.province.getProvinceName(provinceName);
				if (provinceRecord != null) {
					provinceId = provinceRecord.getProvinceId();
					if (provinceId < DEFAULT_PROVINCE_ID) {
						DictProvinceRecord provinceName2 = saas.region.province.getProvinceName(provinceName.substring(0, 2));
						if (provinceName2 != null) {
							provinceId = provinceName2.getProvinceId();
							provinceId = provinceId < 100000 ? 100000 : provinceId;
						}
					}
				}
				//市
				Integer cityId=110000;
				DictCityRecord cityRecord = saas.region.city.getCityId(cityName, provinceId);
				if(cityRecord!=null) {
					cityId=cityRecord.getCityId();
				}else {
					  //没有市时新加
					cityId = saas.region.city.addNewCity(provinceId, cityName);
				}

				//地区
				Integer districtId=110100;
				DictDistrictRecord districtRecord = saas.region.district.getDistrictName(districtName, cityId);
				if(districtRecord!=null) {
					districtId=districtRecord.getDistrictId();
				}else {
					districtId = saas.region.district.addNewDistrict(cityId, districtName);
				}
				userDetailRecord.setProvinceCode(provinceId);
				userDetailRecord.setCityCode(cityId);
				userDetailRecord.setDistrictCode(districtId);
			}

		}
		if(userInfo!=null) {
			if (param.getIsSetting() == 1) {
				userDetail.updateRow(userDetailRecord);
				//syncMainUserDetail(userDetailRecord);
				return JsonResultCode.CODE_SUCCESS;
			}
			if (IS_SETTING_STATUS.equals(param.getIsSetting())) {
				//激活卡标志
				userDetail.updateRow(userDetailRecord);
				//syncMainUserDetail(userDetailRecord);
				UserCardRecord newRecord = USER_CARD.newRecord();
				newRecord.setActivationTime(DateUtils.getSqlTimestamp());
				int ret = userCard.updateUserCardByNo(param.getCardNo(),newRecord);
				if(ret>0) {
					//return $this->response(0, '', '激活成功');
					return JsonResultCode.CODE_CARD_ACTIVATE_SUCCESS;
				}else {
					// return $this->response(1, '', '激活失败 ');
					return JsonResultCode.CODE_CARD_ACTIVATE_FAIL;
				}
			}
		}
		return JsonResultCode.CODE_FAIL;

	}

	/**
	 * 查询用户详细信息，原来在accountSetting中
	 * @param userId
	 * @return
	 */
	public UserAccountSetVo accountSetting(Integer userId,Byte isSetting)  {
        if (isSetting == 1 || IS_SETTING_STATUS.equals(isSetting)) {
            return null;
        }
		UserInfo userInfo = getUserInfo(userId);
		UserAccountSetVo vo=null;
		if(userInfo!=null) {
			vo=new UserAccountSetVo();
			Integer provinceId = userInfo.getProvinceCode() != null ? userInfo.getProvinceCode() : 100000;
			Integer cityId = userInfo.getCityCode() != null ? userInfo.getCityCode() : 110000;
			Integer districtId = userInfo.getDistrictCode() != null ? userInfo.getDistrictCode() : 110100;

			vo.setUserInfo(userInfo);
			DictProvinceRecord provinceName = saas.region.province.getProvinceName(provinceId);
			DictCityRecord cityName = saas.region.city.getCityName(cityId);
			DictDistrictRecord districtName = saas.region.district.getDistrictName(districtId);
			vo.setProvinceCode(provinceName!=null?provinceName.getName():null);
			vo.setCityCode(cityName!=null?cityName.getName():null);
			vo.setDistrictCode(districtName!=null?districtName.getName():null);
		}
		return vo;
	}

	/**
	 * 店铺库的user同步到主库
	 * @param shopRecord
     * @return
	 */
	public int[] syncMainUser(UserRecord shopRecord) {
		return  saas().wxUserService.syncMainUser(getShopId(),shopRecord.getUserId(),shopRecord);
	}


	/**
	 * 店铺库的userdetail同步到主库
	 * @param shopRecord
	 * @param int[]
     * @return
	 */
	public int[] syncMainUserDetail(UserDetailRecord shopRecord) {
		return	saas().wxUserService.syncMainUserDetail(getShopId(),shopRecord.getUserId(),shopRecord);
	}

    /**
     * Get source integer.更新字段值
     *
     * @param condition the condition
     * @param fields    the fields
     */
    public void updateFields(Condition condition, Map<Field<?>, ?> fields) {
        db().update(USER).set(fields).where(condition).execute();
    }
    /**
     * 根据用户openId获取用户数据
     * @param openId
     * @return UserRecord
     */
	public UserRecord getUserFromOpenId(String openId) {
		if(StringUtils.isBlank(openId)) {
			return null;
		}
		return db().selectFrom(USER).where(USER.WX_OPENID.eq(openId)).fetchAny();
	}


    /**
     * 解析手机号
     * @param param
     * @param tokenPrefix
     * @return
     */
	public WxMaPhoneNumberInfo wxDecryptData(WxAppAccountParam param,String tokenPrefix) {
		Integer userId=param.getUserId();
		Integer shopId = this.getShopId();
		WxOpenMaService maService = saas.shop.mp.getMaServiceByShopId(shopId);
		String sessionKey2 = getSessionKey(shopId, userId);
		String sessionKey = jedis.get(sessionKey2);
		logger().info("获取sessionKey："+sessionKey2+"结果"+StringUtils.isNotEmpty(sessionKey));
		WxMaPhoneNumberInfo phoneNoInfo = maService.getUserService().getPhoneNoInfo(sessionKey,param.getEncryptedData(), param.getIv());
		logger().info("获取手机号"+phoneNoInfo);
		if(phoneNoInfo!=null) {
			UserInfo userInfo = getUserInfo(userId);
			if(StringUtils.isEmpty(userInfo.getMobile())) {
				//TODO 短信平台
				 //$this->shop()->serviceRequest->smsPlatform->addMobile($shopInfo->seller_account_action, $shopInfo->seller_account, $objData['data']->purePhoneNumber);
			}
			if(StringUtils.isEmpty(phoneNoInfo.getPhoneNumber())) {
				logger().info("userId："+userId+"没有手机号");
				return null;
			}
			String phoneNumber = phoneNoInfo.getPhoneNumber();
			int execute = db().update(USER).set(USER.MOBILE,phoneNumber).where(USER.USER_ID.eq(userId)).execute();
			logger().info("更新用户userId："+userId+"手机号,结果"+execute);
			String token = tokenPrefix + Util.md5(shopId + "_" +userId);
			String json = jedis.get(token);
			WxAppSessionUser parseJson = Util.parseJson(json, WxAppSessionUser.class);
			if(!phoneNumber.equals(parseJson.getWxUser().getMobile())) {
				logger().info("redis更新用户手机号");
				WxUserInfo wxUser = parseJson.getWxUser();
				wxUser.setMobile(phoneNumber);
				parseJson.setWxUser(wxUser);
				jedis.set(token, Util.toJson(parseJson));
			}
			return phoneNoInfo;
		}
		return null;

    }

	public UserRecord getUserByUnionId(String wxUnionId) {
		return db().fetchAny(USER, USER.WX_UNION_ID.eq(wxUnionId));
	}

    public Result<UserRecord> getAllUser() {
		return db().fetch(USER);
	}
	public Result<UserDetailRecord> getAllUserDetail() {
		return db().fetch(USER_DETAIL);
	}
	/**
	 * 同步用户信息
     * @return
	 */
	public UserSysVo synchronizeUser() {
		Result<UserRecord> allUser = getAllUser();
		logger().info(getShopId()+"用户数量"+allUser.size());
		int updateSuccess=0;
		int insertSuccess=0;
		int updateFail=0;
		int insertFail=0;
		for (UserRecord userRecord : allUser) {
			int[] syncMainUser = syncMainUser(userRecord);
			if (syncMainUser[0] == 1) {
				updateSuccess = updateSuccess + syncMainUser[0];
			}
			if (syncMainUser[0] == 0) {
				updateFail = updateFail + syncMainUser[0];
			}
			if (syncMainUser[1] == 1) {
				insertSuccess = insertSuccess + syncMainUser[1];
			}
			if (syncMainUser[1] == 0) {
				insertFail = insertFail + syncMainUser[1];
			}
		}
		UserSysVo vo=new UserSysVo(allUser.size(), updateSuccess, insertSuccess, updateFail, insertFail);
		return vo;
	}

    public UserSysVo synchronizeUserDetail() {
		Result<UserDetailRecord> allUserDetail = getAllUserDetail();
		logger().info(getShopId()+"用户详情数量"+allUserDetail.size());
		int updateSuccess=0;
		int insertSuccess=0;
		int updateFail=0;
		int insertFail=0;
		for (UserDetailRecord userDetailRecord : allUserDetail) {
			int[] syncMainUserDetail = syncMainUserDetail(userDetailRecord);
			if (syncMainUserDetail[0] == 1) {
				updateSuccess = updateSuccess + syncMainUserDetail[0];
			}
			if (syncMainUserDetail[0] == 0) {
				updateFail = updateFail + syncMainUserDetail[0];
			}
			if (syncMainUserDetail[1] == 1) {
				insertSuccess = insertSuccess + syncMainUserDetail[1];
			}
			if (syncMainUserDetail[1] == 0) {
				insertFail = insertFail + syncMainUserDetail[1];
			}
		}
		UserSysVo vo=new UserSysVo(allUserDetail.size(), updateSuccess, insertSuccess, updateFail, insertFail);
		return vo;
	}

    public UserRecord getUserByMobile(String mobile) {
		return db().selectFrom(USER).where(USER.MOBILE.eq(mobile)).fetchAny();
	}

    /**
     * Is new boolean.是否店铺新用户，0否，1是
     *
     * @param userId the user id
     * @return the boolean
     */
    public boolean isNew(Integer userId) {
        UserRecord record = getUserByUserId(userId);
        if (Objects.isNull(record)) {
            return false;
        }
        if (Timestamp.valueOf(LocalDateTime.now()).getTime() - record.getCreateTime().getTime() <= DEFAULT_NEW_USER_TIME) {
            return true;
        }
        return false;
    }

    /**
     * 获取邀请数量
     * @param userId
     */
    public int getInviteCount(Integer userId) {
        return db().select(DSL.count(USER.USER_ID)).from(USER).where(USER.INVITE_ID.eq(userId)).fetchOneInto(int.class);
    }

    /**
     * 获取用户关联医师列表
     * @param userAssociatedDoctorParam 关联医师入参
     * @return PageResult<UserAssociatedDoctorVo>
     */
    public PageResult<UserAssociatedDoctorVo> getUserAssociatedDoctor(UserAssociatedDoctorParam userAssociatedDoctorParam) {
        //获取医师信息
        PageResult<UserAssociatedDoctorVo> userAssociatedDoctor = userDao.getUserAssociatedDoctor(userAssociatedDoctorParam);
        // 如果没有查询科室那么查询出医师所属全部科室
        if (userAssociatedDoctorParam.getDepartmentName() == null || "".equals(userAssociatedDoctorParam.getDepartmentName())) {
            userAssociatedDoctor.getDataList().forEach(userAssociatedDoctorVo -> {
                List<String> departmentNameByDoctor = departmentService.getDepartmentNameByDoctor(userAssociatedDoctorVo.getDoctorId());
                userAssociatedDoctorVo.setDepartmentName(StringUtils.join(departmentNameByDoctor, ","));
            });
        }
        // 查询该医师处方信息
        userAssociatedDoctor.getDataList().forEach(userAssociatedDoctorVo -> {
            PrescriptionDoctorVo prescriptionDoctorVo = prescriptionService.getDoctorPrescription(userAssociatedDoctorVo.getDoctorCode(), userAssociatedDoctorParam.getUserId());
            userAssociatedDoctorVo.setPrescriptionNum(prescriptionDoctorVo.getTotalCount());
            PrescriptionDoctorVo doctorInquiry = inquiryOrderService.getDoctorInquiry(userAssociatedDoctorVo.getDoctorId(), userAssociatedDoctorParam.getUserId());
            userAssociatedDoctorVo.setInquiryNum(doctorInquiry.getTotalCount());
            userAssociatedDoctorVo.setTotalCost(prescriptionDoctorVo.getTotalPrice().add(doctorInquiry.getTotalPrice()));
        });
        List<UserAssociatedDoctorVo> collect = userAssociatedDoctor.getDataList().stream().distinct().collect(Collectors.toList());
        collect.forEach(userAssociatedDoctorVo -> {
            if (userAssociatedDoctorVo.getIsFav() == null) {
                userAssociatedDoctorVo.setIsFav(true);
            }
        });
        userAssociatedDoctor.setDataList(collect);
        return userAssociatedDoctor;
    }
}
