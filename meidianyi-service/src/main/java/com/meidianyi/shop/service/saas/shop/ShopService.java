package com.meidianyi.shop.service.saas.shop;

import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.dao.foundation.database.DatasourceManager;
import com.meidianyi.shop.dao.foundation.database.DbConfig;
import com.meidianyi.shop.dao.main.ShopDao;
import com.meidianyi.shop.dao.main.SmsConfigDao;
import com.meidianyi.shop.db.main.tables.records.*;
import com.google.common.collect.Lists;
import com.meidianyi.shop.db.main.tables.records.AppAuthRecord;
import com.meidianyi.shop.db.main.tables.records.ShopAccountRecord;
import com.meidianyi.shop.db.main.tables.records.ShopOperationRecord;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.db.main.tables.records.UserLoginRecordRecord;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.saas.auth.SystemTokenAuthInfo;
import com.meidianyi.shop.service.pojo.saas.marketcalendar.SysCalendarActVo;
import com.meidianyi.shop.service.pojo.saas.shop.*;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpAuditStateVo;
import com.meidianyi.shop.service.pojo.saas.shop.version.VersionConfig;
import com.meidianyi.shop.service.pojo.saas.shop.version.VersionMainConfig;
import com.meidianyi.shop.service.pojo.saas.shop.version.VersionNumberConfig;
import com.meidianyi.shop.service.pojo.shop.auth.AdminTokenAuthInfo;
import com.meidianyi.shop.service.pojo.shop.auth.ShopReq;
import com.meidianyi.shop.service.pojo.shop.auth.ShopSelectInnerResp;
import com.meidianyi.shop.service.pojo.shop.config.ShopBaseConfig;
import com.meidianyi.shop.service.saas.marketcalendar.MarketSysCalendarService;
import com.meidianyi.shop.service.saas.shop.official.MpOfficialAccountService;
import com.meidianyi.shop.service.saas.shop.official.MpOfficialAccountUserService;
import com.meidianyi.shop.service.saas.shop.official.message.MpOfficialAccountMessageService;
import jodd.util.StringUtil;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.main.tables.MpAuthShop.MP_AUTH_SHOP;
import static com.meidianyi.shop.db.main.tables.Shop.SHOP;
import static com.meidianyi.shop.db.main.tables.ShopAccount.SHOP_ACCOUNT;
import static com.meidianyi.shop.db.main.tables.ShopChildRole.SHOP_CHILD_ROLE;

/**
 *
 * @author 新国
 *
 */
@Service
public class ShopService extends MainBaseService {

	@Autowired
	protected DatasourceManager datasourceManager;
	@Autowired
	public ShopAccountService account;
	@Autowired
	public ShopImageManageService image;
	@Autowired
	public ShopRenewService renew;
	@Autowired
	public ShopVersionService version;
	@Autowired
	public ShopChildAccountService subAccount;
	@Autowired
	public ShopRoleService role;
	@Autowired
	public ShopMenuService menu;
	@Autowired
	public MpDecorationService decoration;
	@Autowired
	public MpAuthShopService mp;

	@Autowired
	public MpVersionService mpVersion;

	@Autowired
	public MpOperateLogService mpOperateLog;

	@Autowired
	public MpJumpVersionService mpJumpVersion;

	@Autowired
	public ShopOfficialAccount officeAccount;

    @Autowired
    public MpOfficialAccountMessageService mpOfficialAccountMessageService;

    @Autowired
    public MpOfficialAccountUserService mpOfficialAccountUserService;
    @Autowired
    public MpOfficialAccountService mpOfficialAccountService;
    @Autowired
    public MpBackProcessService backProcessService;

    @Autowired
    public ShopAppService shopApp;

    @Autowired
    public StoreManageService storeManageService;

    @Autowired
    public ThirdPartyMsgServices thirdPartyMsgServices;

    @Autowired
    public MarketSysCalendarService calendarService;

    @Autowired
    public ShopApplyService shopApply;

    @Autowired
    private SmsConfigDao smsConfigDao;

    @Autowired
    private ShopDao shopDao;

    @Autowired
    public StoreMenuService storeMenu;

    @Autowired
    public StoreAccountService storeAccount;

    public PageResult<ShopListQueryResultVo> getPageList(ShopListQueryParam param) {
        SelectWhereStep<?> select = db()
            .select(SHOP.SYS_ID, SHOP.SHOP_ID, SHOP.SHOP_NAME, SHOP.SHOP_TYPE, SHOP.MOBILE, SHOP.CREATED,
                SHOP.IS_ENABLED, SHOP_ACCOUNT.USER_NAME, SHOP.SHOP_FLAG, SHOP.HID_BOTTOM, SHOP.RECEIVE_MOBILE,
                SHOP.SHOP_PHONE, SHOP.SHOP_NOTICE, SHOP.SHOP_WX, SHOP.SHOP_EMAIL, SHOP.SHOP_QQ, SHOP.MEMBER_KEY,
                SHOP.TENANCY_NAME, MP_AUTH_SHOP.APP_ID, MP_AUTH_SHOP.IS_AUTH_OK, MP_AUTH_SHOP.NICK_NAME,
                MP_AUTH_SHOP.PRINCIPAL_NAME, SHOP.VERSION_CONFIG, SHOP.SHOP_LANGUAGE, SHOP.CURRENCY)
            .from(SHOP).join(SHOP_ACCOUNT).on(SHOP.SYS_ID.eq(SHOP_ACCOUNT.SYS_ID)).leftJoin(MP_AUTH_SHOP)
            .on(SHOP.SHOP_ID.eq(DSL.cast(MP_AUTH_SHOP.SHOP_ID, Integer.class)));
		select = this.buildOptions(select, param);
		select.orderBy(SHOP.CREATED.desc());
		PageResult<ShopListQueryResultVo> result = this.getPageResult(select, param.currentPage, param.pageRows,
				ShopListQueryResultVo.class);
		for (ShopListQueryResultVo shopList : result.dataList) {
			shopList.setRenewMoney(this.renew.getShopRenewTotal(shopList.getShopId()));
			Timestamp expireTime = this.renew.getShopRenewExpireTime(shopList.getShopId());
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
			shopList.setExpireTime(expireTime);
			shopList.setShopExpireStatus(expireStatus);
			shopList.setSpecialInfo(shopSpecialConf(shopList));
		}
		return result;
	}

	public SelectWhereStep<?> buildOptions(SelectWhereStep<?> select, ShopListQueryParam param) {
		if (param == null) {
			return select;
		}
		if (!StringUtils.isEmpty(param.keywords)) {
			Integer shopId = Util.convert(param.keywords, Integer.class, 0);
			String keywords = likeValue(param.keywords);
			select.where(SHOP.SHOP_NAME.like(keywords).or(SHOP.MOBILE.like(keywords))
					.or(MP_AUTH_SHOP.NICK_NAME.like(keywords)).or(SHOP.SHOP_ID.eq(shopId)));
		}

        buildShopExpireOption(select, param);

        if (!StringUtils.isEmpty(param.shopType)) {
			select.where(SHOP.SHOP_TYPE.eq(param.shopType));
		}

        buildShopTypeOption(select, param);

        if (param.shopFlag != null) {
			if (param.shopFlag == 0) {
				select.where(SHOP.SHOP_FLAG.eq(param.shopFlag).or(SHOP.SHOP_FLAG.isNull()));
			} else {
				select.where(SHOP.SHOP_FLAG.eq(param.shopFlag));
			}
		}

		if (!StringUtils.isEmpty(param.accountKey)) {
			String key = this.likeValue(param.accountKey);
			select.where(SHOP_ACCOUNT.COMPANY.like(key).or(MP_AUTH_SHOP.PRINCIPAL_NAME.like(key))
					.or(SHOP.SHOP_ID.like(key)));
		}

		if (param.isEnabled != null) {
			select.where(SHOP.IS_ENABLED.eq(param.isEnabled));
		}
		if (param.hidBottom != null) {
			select.where(SHOP.HID_BOTTOM.eq(param.hidBottom));
		}

		if(!StringUtils.isEmpty(param.expireStartTime)) {
			select.where(SHOP.EXPIRE_TIME.ge(param.expireStartTime));
		}
		if(!StringUtils.isEmpty(param.expireEndTime)) {
			select.where(SHOP.EXPIRE_TIME.le(param.expireEndTime));
		}
		if (param.getCalendarActId() != null && param.getCalendarActId() != 0) {
			MarketCalendarActivityRecord record = calendarService.calendarActivityService.getInfoById(param.getCalendarActId());
			if(record!=null) {
				SysCalendarActVo vo = record.into(SysCalendarActVo.class);
				String shopIds = vo.getShopIds();
				if(!StringUtils.isEmpty(shopIds)) {
					String[] split = shopIds.split(",");
					List<String> list = Arrays.asList(split);
					select.where(SHOP.SHOP_ID.in(list));
				}
			}
		}
		return select;
	}

    private void buildShopTypeOption(SelectWhereStep<?> select, ShopListQueryParam param) {
        if (StringUtils.isEmpty(param.shopType) && !StringUtils.isEmpty(param.shopTypes)) {
            // 区分体验版和付费版
            if (param.shopTypes.equals(ShopConst.ShopTypes.TRIAL_VERSION)) {
                // 体验版
                select.where(SHOP.SHOP_TYPE.eq(ShopConst.ShopType.V_1));
            }
            if (param.shopTypes.equals(ShopConst.ShopTypes.PAID_VERSION)) {
                // 付费版
                select.where(SHOP.SHOP_TYPE.in(ShopConst.ShopType.V_2, ShopConst.ShopType.V_3, ShopConst.ShopType.V_4));
            }

        }
    }

    private void buildShopExpireOption(SelectWhereStep<?> select, ShopListQueryParam param) {
        //使用中
        Integer shopUsingStatus = 1;
        //已过期
        Integer shopExpiredStatus = 2;
        //即将过期
        Integer shopSoonExpiredStatus = 3;
        if (param.isUse != null && param.isUse.equals(shopUsingStatus)) {
            // 店铺在使用中
            select.where(SHOP.EXPIRE_TIME.ge(DSL.currentTimestamp()));
        }

        if (param.isUse != null && param.isUse.equals(shopExpiredStatus)) {
            // 店铺已过期
            select.where(SHOP.EXPIRE_TIME.lt(DSL.currentTimestamp()));
        }
        if (param.isUse != null && param.isUse.equals(shopSoonExpiredStatus)) {
            // 即将过期
            select.where(SHOP.EXPIRE_TIME.le(DSL.timestampAdd(DSL.currentTimestamp(), 1, DatePart.MONTH)).and(SHOP.EXPIRE_TIME.ge(DSL.currentTimestamp())));
        }
    }

    /**
	 * TODO 加个事务
	 *
	 * @param shopReq
	 * @return
	 */
	public Boolean addShop(ShopReq shopReq, SystemTokenAuthInfo user, HttpServletRequest request) {
		shopReq.setShopId(getCanUseShopId());
		DbConfig dbConfig = datasourceManager.getInstallShopDbConfig(shopReq.getShopId());
		if(!StringUtils.isEmpty(shopReq.getDbConfigId())) {
			//TODO 加插入传来的数据库信息
		}
		shopReq.setDbConfig(Util.toJson(dbConfig));
		if (!databaseManager.installShopDb(dbConfig)) {
			return false;
		}
		logger().info("数据库创建成功");
		ShopRecord record = new ShopRecord();
		FieldsUtil.assignNotNull(shopReq, record);
		if (db().executeInsert(record) != 1) {
			return false;
		}
		logger().info("插入数据成功");

		// 更新version_config
		if (updateConfig(shopReq) == 1) {
			logger().info("更新version_config成功");
		}
		// 更新记录表
		if (updateOperation(record, user, request,new ShopRecord()) == 1) {
			logger().info("更新ShopOperation记录表成功");
		}
		// 初始化店铺配置
		initShopConfig(shopReq.getShopId());
		return true;
	}

	/**
	 * 得到所有店铺
	 *
	 * @return
	 */
	public Result<ShopRecord> getAll() {
		return db().fetch(SHOP);
	}

	/**
	 * 得到可用店铺ID
	 *
	 * @return
	 */
	public Integer getCanUseShopId() {
		int maxNumber = 1000;
		for (int i = 0; i < maxNumber; i++) {
			Integer shopId = Util.randomInteger(100000, 1000000);
			if (db().fetchCount(SHOP, SHOP.SHOP_ID.eq(shopId)) == 0) {
				return shopId;
			}
		}
		Integer maxShopId = (Integer) db().select(DSL.max(SHOP.SHOP_ID)).from(SHOP).fetch().getValue(0, 0);
		return maxShopId + 1;
	}

	public Integer updateShop(ShopPojo shop) {
		shop.setIsEnabled(shop.getIsEnabled() == null ? 0 : shop.getIsEnabled());
		shop.setIsEnabled(shop.getHidBottom() == null ? 0 : shop.getHidBottom());
		ShopRecord record = db().newRecord(SHOP, shop);
		return db().executeUpdate(record);
	}

	public boolean hasMobile(String mobile) {
		return getShopByMobile(mobile) != null;
	}

	public ShopRecord getShopById(Integer shopId) {
		return db().selectFrom(SHOP).where(SHOP.SHOP_ID.eq(shopId)).fetchAny();
	}

	public ShopRecord getShopByMobile(String mobile) {
		return db().selectFrom(SHOP).where(SHOP.MOBILE.eq(mobile)).fetchAny();
	}

	public Integer getShopAccessRoleId(Integer sysId, Integer shopId, Integer subAccountId) {
		if (subAccountId == 0) {
			ShopRecord shop = this.getShopById(shopId);
			if (shop != null && shop.getSysId().equals(sysId)) {
				return 0;
			}
			return -1;
		}
		Record1<Integer> role = db().select(SHOP_CHILD_ROLE.ROLE_ID).from(SHOP).leftJoin(SHOP_CHILD_ROLE)
				.on(SHOP.SHOP_ID.eq(SHOP_CHILD_ROLE.SHOP_ID)).where(SHOP.SHOP_ID.eq(shopId)).and(SHOP.SYS_ID.eq(sysId))
				.and(SHOP_CHILD_ROLE.ACCOUNT_ID.eq(subAccountId)).fetchAny();
		if (role != null) {
			return role.value1();
		}
		return -1;
	}

	/**
	 * 得到角色权限对应店铺列表 `state` '0 入驻申请，1审核通过，2审核不通过',审核不通过不能登录到店铺后台 `business_state`
	 * '营业状态 0未营业 1营业',未营业不能下单，加入购物车，下单接口提示 expire_time 过期可以登录到后台，店铺是未营业状态
	 *
	 * @param sysId
	 * @param subAccountId
	 * @return
	 */
	public List<ShopRecord> getRoleShopList(
			Integer sysId, Integer subAccountId) {
		SelectWhereStep<? extends Record> select = db().selectDistinct(SHOP.asterisk())
				.from(SHOP).leftJoin(SHOP_CHILD_ROLE).on(SHOP.SHOP_ID.eq(SHOP_CHILD_ROLE.SHOP_ID));
		select.where(SHOP.SYS_ID.eq(sysId));
		if (subAccountId > 0) {
			select.where(SHOP_CHILD_ROLE.ACCOUNT_ID.eq(subAccountId));
		}
		return select.orderBy(SHOP.CREATED.asc()).fetchInto(ShopRecord.class);
	}

    /**
     * 店铺基础配置-店铺基础信息get
     */
	public ShopBaseConfig getShopBaseInfoById(Integer shopId) {
		ShopBaseConfig shopBaseCfgInfo = db().select()
            .from(SHOP).where(SHOP.SHOP_ID.eq(shopId)).fetchOneInto(ShopBaseConfig.class);
        shopBaseCfgInfo.setExpireTime(saas.shop.renew.getShopRenewExpireTime(shopId));
        shopBaseCfgInfo.setShowLogo(saas.getShopApp(shopId).config.shopCommonConfigService.getShowLogo());
        shopBaseCfgInfo.setLogoLink(saas.getShopApp(shopId).config.shopCommonConfigService.getLogoLink());
        return shopBaseCfgInfo;
	}

    /**
     * 店铺基础配置-店铺基础信息更新
     *
     */
	public void updateShopBaseInfo(ShopBaseConfig shop,int shopId) {
        shop.setCreated(null);
        shop.setExpireTime(null);
	    ShopRecord record = new ShopRecord();
	    assign(shop,record);
        record.setShopId(shopId);
        this.transaction(() -> {
            db().executeUpdate(record);
            saas.getShopApp(shopId).config.shopCommonConfigService.setShowLogo(shop.getShowLogo());
            if(StringUtil.isNotEmpty(shop.getLogoLink())){
                saas.getShopApp(shopId).config.shopCommonConfigService.setLogoLink(shop.getLogoLink());
            }
        });
	}
	public List<ShopSelectInnerResp> getShopList(AdminTokenAuthInfo info, List<ShopRecord> shopList) {
		List<ShopSelectInnerResp> dataList = new ArrayList<>(shopList.size());
		for (ShopRecord record : shopList) {
			Timestamp expireTime = renew.getShopRenewExpireTime(Util.getInteger(record.get(SHOP.SHOP_ID)));
			MpAuthShopRecord authShop = saas.shop.mp.getAuthShopByShopId(record.getShopId());

			ShopSelectInnerResp shopInner = new ShopSelectInnerResp();
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
			shopInner.setShopId(record.get(SHOP.SHOP_ID));
			shopInner.setSysId(record.get(SHOP.SYS_ID));
			shopInner.setShopName(record.get(SHOP.SHOP_NAME));
			shopInner.setShopMpName(authShop.getNickName());
			shopInner.setShopAvatar(StringUtils.isEmpty(record.get(SHOP.SHOP_AVATAR))?null:image.imageUrl(record.get(SHOP.SHOP_AVATAR)));
			shopInner.setCreated(record.get(SHOP.CREATED));
			shopInner.setState(record.get(SHOP.STATE));
			shopInner.setBusinessState(record.get(SHOP.BUSINESS_STATE));
			shopInner.setIsEnabled(record.get(SHOP.IS_ENABLED));
			shopInner.setShopType(record.get(SHOP.SHOP_TYPE));
			shopInner.setExpireTime(expireTime);
			shopInner.setExpireTimeStatus(expireStatus);
			shopInner.setCurrency(record.get(SHOP.CURRENCY));
			shopInner.setShopLanguage(record.get(SHOP.SHOP_LANGUAGE));
			dataList.add(shopInner);
		}
		return dataList;
	}

	/**
	 * 判断店铺是否过期,true为过期，false没过期
	 *
	 * @return
	 */
	public Boolean checkExpire(Integer shopId) {
		Timestamp expireTime = renew.getShopRenewExpireTime(shopId);
		if (expireTime == null) {
			return true;
		}
		if (expireTime != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(expireTime);
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			if (cal.getTimeInMillis() > Calendar.getInstance().getTimeInMillis()) {
				return false;
			}
		}
		return true;
	}

	public int insertUserLoginRecord(UserLoginRecordRecord record) {
		return db().executeInsert(record);
	}

	/**
	 * 更新version_config
	 *
	 * @param shopReq
	 * @param shopId
	 * @return
	 */
	public Integer updateConfig(ShopReq shopReq) {
		VersionConfig versionConfig = version.getVersionConfig(shopReq.getShopType());
		// 把mainConfig置空，用户的菜单权限为自己mainConfig+shop_version中的权限，这样当增减shop_version中权限时候，当前表不用进行权限增删
		versionConfig.mainConfig = new VersionMainConfig();
		return db().update(SHOP).set(SHOP.VERSION_CONFIG, Util.toJson(versionConfig))
				.where(SHOP.SHOP_ID.eq(shopReq.getShopId())).execute();
	}

	public Integer updateOperation(ShopRecord shopRecord, SystemTokenAuthInfo user, HttpServletRequest request,ShopRecord newShopRecord) {
		ShopOperationRecord sRecord = new ShopOperationRecord();
		sRecord.setShopId(shopRecord.getShopId());
		if (user.isSubLogin()) {
			sRecord.setOperatorId(user.subAccountId);
			sRecord.setOperator(user.getSubUserName());
		}
		sRecord.setOperator(user.getUserName());
		sRecord.setOperatorId(user.getSystemUserId());
		sRecord.setDesc(diffEdit(shopRecord, newShopRecord));
		sRecord.setIp(Util.getCleintIp(request));
		return db().executeInsert(sRecord);
	}

	/**
	 * 根据id查询ShopRecord，diffEdit里用
	 *
	 * @param shopId
	 * @return
	 */
	public ShopRecord queryOldRcord(Integer shopId) {
		ShopRecord oldShop = db().selectFrom(SHOP).where(SHOP.SHOP_ID.eq(shopId)).fetchOne();
		return oldShop;
	}

	/**
	 * 更新b2c_shop_operation
	 * @param newShop
	 * @param oldShop
	 * @return
	 */
	public String diffEdit(ShopRecord newShop, ShopRecord oldShop) {
		StringBuffer sbf = new StringBuffer("新建或者更新");
		if ((!StringUtils.isEmpty(newShop.getMobile()))&&!newShop.getMobile().equals(oldShop.getMobile())) {
			sbf.append("电话:" + newShop.getMobile() + ",");
		}
		if ((!StringUtils.isEmpty(newShop.getShopName()))&&!newShop.getShopName().equals(oldShop.getShopName())) {
			sbf.append("店铺名称:" + newShop.getShopName() + ",");
		}
		if ((!StringUtils.isEmpty(newShop.getShopPhone()))&&!newShop.getShopPhone().equals(oldShop.getShopPhone())) {
			sbf.append("店铺客服电话:" + newShop.getShopPhone() + ",");
		}
		if ((!StringUtils.isEmpty(newShop.getShopNotice()))&&!newShop.getShopNotice().equals(oldShop.getShopNotice())) {
			sbf.append("店铺公告:" + newShop.getShopNotice() + ",");
		}
		if ((!StringUtils.isEmpty(newShop.getShopWx()))&&!newShop.getShopWx().equals(oldShop.getShopWx())) {
			sbf.append("店铺微信:" + newShop.getShopWx() + ",");
		}
		if ((!StringUtils.isEmpty(newShop.getShopEmail()))&&!newShop.getShopEmail().equals(oldShop.getShopEmail())) {
			sbf.append("店铺邮箱:" + newShop.getShopEmail() + ",");
		}
		if ((!StringUtils.isEmpty(newShop.getIsEnabled()))&&!newShop.getIsEnabled().equals(oldShop.getIsEnabled())) {
			sbf.append("店铺禁用:" + newShop.getIsEnabled() + ",");
		}
		if ((!StringUtils.isEmpty(newShop.getShopQq()))&&!newShop.getShopQq().equals(oldShop.getShopQq())) {
			sbf.append("店铺客服QQ:" + newShop.getShopQq() + ",");
		}
		if ((!StringUtils.isEmpty(newShop.getShopType()))&&!newShop.getShopType().equals(oldShop.getShopType())) {
			sbf.append("店铺类型:" + version.getVersionNameByLevel(newShop.getShopType()));
		}
		return sbf.toString();

	}
	public ShopRecord checkShop(Integer shopId,Integer sysId) {
		 return db().selectFrom(SHOP).where(SHOP.SHOP_ID.eq(shopId).and(SHOP.SYS_ID.eq(sysId))).fetchOne();
	}

	public Integer getShopNumber(Integer sysId) {
		return (Integer) db().select(DSL.count(SHOP.SYS_ID)).from(SHOP).where(SHOP.SYS_ID.eq(sysId)).fetchAny(0);
	}

	public Record getShop(Integer shopId) {
		return db().select(SHOP.fields()).from(SHOP).join(MP_AUTH_SHOP)
				.on(SHOP.SHOP_ID.eq(MP_AUTH_SHOP.SHOP_ID)).where(SHOP.SHOP_ID.eq(shopId)).fetchAny();
	}

	public Integer updateRowIsEnable(Integer shopId,Byte isEnable) {
		return db().update(SHOP).set(SHOP.IS_ENABLED,isEnable).where(SHOP.SHOP_ID.eq(shopId)).execute();
	}
	public Integer updateRowHidBottom(Integer shopId,Byte hidBottem) {
		return db().update(SHOP).set(SHOP.HID_BOTTOM,hidBottem).where(SHOP.SHOP_ID.eq(shopId)).execute();
	}

	public List<String> shopSpecialConf(ShopListQueryResultVo shopList) {
		List<String> specialInfo=new ArrayList<String>();
		ShopAccountRecord accountInfoForId = account.getAccountInfoForId(shopList.getSysId());
		if(accountInfoForId.getBaseSale()==1) {
			//初始销量功能开启
			specialInfo.add("BaseSale");
		}if(accountInfoForId.getAddCommentSwitch()==1) {
			//商家添加评价功能开启
			specialInfo.add("AddCommentSwitch");
		}
		AppAuthRecord shopAppByErp = shopApp.getShopAppByErp(shopList.getShopId());
		if(shopAppByErp!=null) {
			if(shopAppByErp.getStatus()==1) {
				//erp已对接
				specialInfo.add("ErpStatus");
			}
		}
		//TODO  开启微信全链路 shop表加 seller_account
		if(shopList.getHidBottom()==1) {
			//隐藏底部功能开启
			specialInfo.add("HidBottom");
		}
		if(shopList.getVersionConfig()!=null) {
			VersionConfig parseJson = Util.parseJson(shopList.getVersionConfig(), VersionConfig.class);
			VersionNumberConfig numConfig = parseJson.numConfig;
			if(numConfig.pictureNumPlus!=null&&numConfig.pictureNumPlus!=0) {
				//图片空间已扩容
				specialInfo.add("PictureNumPlus");
			}if(numConfig.videoNumPlus!=null&&numConfig.videoNumPlus!=0) {
				//视频空间已扩容
				specialInfo.add("VideoNumPlus");
			}if(numConfig.decorateNumPlus!=null&&numConfig.decorateNumPlus!=0) {
				//页面装修数量已扩容
				specialInfo.add("DecorateNumPlus");
			}
		}
		return specialInfo;
	}

	/**
	 * 编辑店铺
	 * @param shopReq
	 * @param user
	 * @param request
	 * @return
	 */
	public Boolean editShop(ShopReq shopReq, SystemTokenAuthInfo user, HttpServletRequest request,ShopRecord oldShopReq) {
		ShopRecord record = new ShopRecord();
		FieldsUtil.assignNotNull(shopReq, record);
		if (db().executeUpdate(record) != 1) {
			return false;
		}
		logger().info("更新数据成功");

		// 更新记录表
		if (updateOperation(record, user, request,oldShopReq) == 1) {
			logger().info("更新ShopOperation记录表成功");
		}
		return true;
	}

	/**
	    * 根据店铺id获取店铺头像
	 * @param shopId
	 * @return
	 */
	public String getShopAvatarById(Integer shopId) {
	    return db().select(SHOP.SHOP_AVATAR).from(SHOP).where(SHOP.SHOP_ID.eq(shopId)).fetchOptionalInto(String.class).orElse(null);
	}

	/**
	 * 获取单个店铺信息
	 * @param shopId
	 * @return
	 */
	public ShopSelectInnerResp getShopInfo(Integer shopId) {
		ShopRecord record = getShopById(shopId);
		Timestamp expireTime = renew.getShopRenewExpireTime(shopId);
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
		ShopSelectInnerResp vo = record.into(ShopSelectInnerResp.class);
		vo.setExpireTime(expireTime);
		vo.setExpireTimeStatus(expireStatus);
		vo.setShopAvatar(StringUtils.isEmpty(record.get(SHOP.SHOP_AVATAR))?null:image.imageUrl(record.get(SHOP.SHOP_AVATAR)));
		return vo;
	}

    /**
     * 获取当前店铺币种
     * @param shopId shopId
     * @return currency
     */
	public String getCurrency(Integer shopId) {
        String currency = db().select(SHOP.CURRENCY).from(SHOP).where(SHOP.SHOP_ID.eq(shopId)).fetchOneInto(String.class);
        if(currency == null) {
            return "CNY";
        }
        return currency;
    }

    /**
     * 获取所有可用店铺id
     * @return 可用店铺id集合
     */
    public List<Integer> getEnabledShopIds(){
	    Result<ShopRecord> records = getAll();
	    if( !CollectionUtils.isEmpty(records) ){
	        return records.stream()
//                .filter(x-> x.getIsEnabled() == 1)
                .map(ShopRecord::getShopId)
                .collect(Collectors.toList());
        }
	    return Lists.newArrayList();
    }

    /**
     * 根据店铺id获取对应小程序名称
     * @param shopId 店铺id
     * @return       小程序名称
     */
    public String getShopNickName(Integer shopId){
        return db().select(MP_AUTH_SHOP.NICK_NAME)
            .from(MP_AUTH_SHOP)
            .where(MP_AUTH_SHOP.SHOP_ID.eq(shopId))
            .limit(1)
            .fetchOneInto(String.class);
    }

    /**
     * 初始化店铺配置
     * @param shopId 店铺id
     */
    public Integer initShopConfig(Integer shopId) {
        // 初始化短信配置
        return smsConfigDao.initSmsConfig(shopId);
    }

    public List<ShopListInfoVo> getShopListInfo(){
        return shopDao.getShopListInfo();
    }
}
