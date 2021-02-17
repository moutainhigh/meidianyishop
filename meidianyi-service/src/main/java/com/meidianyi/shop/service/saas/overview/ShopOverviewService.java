package com.meidianyi.shop.service.saas.overview;

import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.main.tables.records.MpAuthShopRecord;
import com.meidianyi.shop.db.main.tables.records.MpOfficialAccountUserRecord;
import com.meidianyi.shop.db.main.tables.records.ShopAccountRecord;
import com.meidianyi.shop.db.main.tables.records.ShopChildAccountRecord;
import com.meidianyi.shop.service.foundation.exception.BusinessException;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.saas.overview.LoginRecordVo;
import com.meidianyi.shop.service.pojo.shop.auth.AdminTokenAuthInfo;
import com.meidianyi.shop.service.pojo.shop.auth.StoreTokenAuthInfo;
import com.meidianyi.shop.service.pojo.shop.image.ShareQrCodeVo;
import com.meidianyi.shop.service.pojo.shop.overview.*;
import com.meidianyi.shop.service.pojo.shop.qrcode.QrCodeTypeEnum;
import com.meidianyi.shop.service.pojo.shop.store.account.StoreAccountVo;
import com.meidianyi.shop.service.pojo.shop.store.store.StoreBestSellersParam;
import com.meidianyi.shop.service.pojo.shop.store.store.StoreBestSellersVo;
import com.meidianyi.shop.service.saas.shop.MpAuthShopService;
import com.meidianyi.shop.service.saas.shop.ShopChildAccountService;
import com.meidianyi.shop.service.saas.shop.ShopOfficialAccount;
import com.meidianyi.shop.service.saas.shop.StoreAccountService;
import com.meidianyi.shop.service.shop.image.QrCodeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.meidianyi.shop.common.foundation.util.DateUtils.getDate;
import static com.meidianyi.shop.db.main.tables.Article.ARTICLE;
import static com.meidianyi.shop.db.main.tables.MpAuthShop.MP_AUTH_SHOP;
import static com.meidianyi.shop.db.main.tables.Shop.SHOP;
import static com.meidianyi.shop.db.main.tables.ShopVersion.SHOP_VERSION;
import static com.meidianyi.shop.db.main.tables.UserLoginRecord.USER_LOGIN_RECORD;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.math.NumberUtils.*;

/**
 * @author liufei
 * date 2019/7/15
 * 概览
 */
@Slf4j
@Service
public class ShopOverviewService extends MainBaseService {
    @Autowired
    public MpAuthShopService mpAuthShopService;

    @Autowired
    public ShopChildAccountService childAccountService;

    @Autowired
    public ShopOfficialAccount shopOfficialAccount;

    @Autowired
    QrCodeService qrCodeService;

    @Autowired
    public StoreAccountService storeAccountService;

    /** 日期标识符 */
    private static final Integer CUSTOM_DAYS = 0;

    /**
     * 绑定解绑
     */
	public boolean bindUnBindOfficial(String act, AdminTokenAuthInfo user, Integer accountId) {
		if (StringUtils.isEmpty(act) || user == null) {
			return false;
		}
		byte isBind = 0;
        String bind = "bind";
        if (bind.equals(act)) {
			// 绑定
			isBind = 1;
		}
        String delBind = "del_bind";
        if (delBind.equals(act)) {
			// 解绑
			isBind = 0;
		}if(!(bind.equals(act)|| delBind.equals(act))) {
			logger().debug("绑定解绑传入act参数错误："+act);
			return false;
		}
		int num = 0;
		if (!user.subLogin) {
			// 主账户
			if(accountId!=0) {
				//主账户在子账户权限管理处操作子账户解绑绑定
				num = saas.shop.subAccount.updateRowBind(accountId, isBind);
			}else {
				num = saas.shop.account.updateRowBind(user.sysId, isBind);
			}
		} else {
			// 子账户
			num = saas.shop.subAccount.updateRowBind(user.subAccountId, isBind);
		}
		if (num > 0) {
			return true;
		}
		return false;
	}

    /**
     * 获取绑定/解绑状态  概览使用
     */
    public BindofficialVo getbindUnBindStatusUseByOver(AdminTokenAuthInfo user,String bindAppId){
    	BindofficialVo bindofficialVo=new BindofficialVo();
		String officialOpenId = null;
		String nickname=null;
		Byte isBind=0;
		if (user.subLogin) {
			ShopChildAccountRecord subAccountInfo = saas.shop.subAccount.getSubAccountInfo(user.sysId,user.subAccountId);
			isBind = subAccountInfo.getIsBind();
			officialOpenId = subAccountInfo.getOfficialOpenId();
			// 子账户登录
		} else {
			// 主账户登录
			ShopAccountRecord accountInfoForId = saas.shop.account.getAccountInfoForId(user.sysId);
			isBind = accountInfoForId.getIsBind();
			officialOpenId = accountInfoForId.getOfficialOpenId();
		}
		if (StringUtils.isNotEmpty(officialOpenId)) {
			// 绑定过公众号
			// shopId找auth_shop
			MpOfficialAccountUserRecord user2 = saas.shop.mpOfficialAccountUserService.getUser(bindAppId,officialOpenId);
			nickname = user2.getNickname();
		}
		bindofficialVo.setOfficialOpenId(officialOpenId);
		bindofficialVo.setIsBind(isBind);
		bindofficialVo.setNickName(nickname);
		return bindofficialVo;
	}

    /**
     * 获取店铺基本信息
     */
    public ShopBaseInfoVo getShopBaseInfo(Integer shopId, String bindAppId, AdminTokenAuthInfo user) {
        return ShopBaseInfoVo.builder()
            // 店铺到期时间
            .expireTime(shopExpireTime(shopId))
            // 店铺版本
            .version(shopVersion(shopId))
            // 当前绑定解绑状态
            .bindInfo(getbindUnBindStatusUseByOver(user, bindAppId))
            // 分享二维码信息
            .shareQrCodeVo(share(QrCodeTypeEnum.PAGE_BOTTOM, "0"))
            .build();
    }

    /**
     * Shop expire time timestamp.店铺到期时间
     *
     * @param shopId the shop id
     * @return the timestamp
     */
    public Timestamp shopExpireTime(Integer shopId) {
        return  db().select(SHOP.EXPIRE_TIME).from(SHOP).where(SHOP.SHOP_ID.eq(shopId))
            .fetchOptionalInto(Timestamp.class)
            .orElseThrow(() -> new BusinessException(JsonResultCode.CODE_ACCOUNT_SHOP_EXPRIRE));
    }

    /**
     * Shop version map.店铺版本
     *
     * @param shopId the shop id
     * @return the map<K, V> K=版本级别, V=版本名称
     */
    public Map<String, String> shopVersion(Integer shopId) {
        Select<Record1<String>> select = db().select(SHOP.SHOP_TYPE).from(SHOP).where(SHOP.SHOP_ID.eq(shopId));
        return db().select(SHOP_VERSION.LEVEL, SHOP_VERSION.VERSION_NAME).from(SHOP_VERSION).where(SHOP_VERSION.LEVEL.eq(select))
            .fetchMap(SHOP_VERSION.LEVEL, SHOP_VERSION.VERSION_NAME);
    }

    /**
     * Share store service share qr code vo.商城概览分享方法
     *
     * @param qrCodeTypeEnum the qr code type enum
     * @param pathParam      the path param
     * @return the share qr code vo
     */
    public ShareQrCodeVo share(QrCodeTypeEnum qrCodeTypeEnum, String pathParam) {
        String imageUrl = qrCodeService.getMpQrCode(qrCodeTypeEnum, pathParam);
        ShareQrCodeVo vo = new ShareQrCodeVo();
        vo.setImageUrl(imageUrl);
        vo.setPagePath(qrCodeTypeEnum.getPathUrl(null));
        return vo;
    }

    /**
     * 获取指定数量的公告title
     */
    public List<FixedAnnouncementVo> getFixedAnnouncement(FixedAnnouncementParam param){
        SortField<Timestamp> orderBy = "asc".equals(param.getOrderBy()) ? ARTICLE.CREATE_TIME.asc() : ARTICLE.CREATE_TIME.desc();
        List<FixedAnnouncementVo> listVo = db().select(ARTICLE.ARTICLE_ID, ARTICLE.TITLE, ARTICLE.CREATE_TIME)
            .from(ARTICLE)
            .where(ARTICLE.STATUS.eq(BYTE_ONE))
            .and(ARTICLE.CATEGORY_ID.eq(param.getCategoryId()))
                .orderBy(orderBy)
                .limit(param.getFixedNum())
                .fetchInto(FixedAnnouncementVo.class);
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");
        for (FixedAnnouncementVo vo : listVo){
            vo.setFormatTime(formatter.format(vo.getCreateTime()));
        }
        return listVo;
    }

    /**
     * 店铺助手
     */
    public AssiWxDataShop shopAssistant(Integer shopId, Integer sysId) {
        return shopNav(shopId, sysId);
    }

    private AssiWxDataShop shopNav(Integer shopId, Integer sysId) {
        AssiWxDataShop dataShop = new AssiWxDataShop();
        // 微信配置（授权和支付）
        MpAuthShopRecord authShopRecord = mpAuthShopService.getAuthShopByShopId(shopId);
        Metadata finished = Metadata.builder().value(INTEGER_ZERO).type(BYTE_ZERO).status(BYTE_ONE).build();
        Metadata unFinished = Metadata.builder().value(INTEGER_ONE).type(BYTE_ZERO).status(BYTE_ZERO).build();
        Metadata finished1 = Metadata.builder().value(INTEGER_ZERO).type(BYTE_ONE).status(BYTE_ONE).build();
        Metadata unFinished1 = Metadata.builder().value(INTEGER_ONE).type(BYTE_ONE).status(BYTE_ZERO).build();
        if (authShopRecord != null) {
            dataShop.setRegisterApplet(finished);
            dataShop.setAuthApplet(finished1);
            dataShop.setAppletService(finished);
            boolean condi1 = StringUtils.isNotBlank(authShopRecord.getPayCertContent());
            boolean condi2 = StringUtils.isNotBlank(authShopRecord.getPayKey());
            boolean condi3 = StringUtils.isNotBlank(authShopRecord.getPayKeyContent());
            boolean condi4 = StringUtils.isNotBlank(authShopRecord.getPayMchId());

            if(condi1 && condi2 && condi3 && condi4){
                dataShop.setWxPayment(finished);
                dataShop.setConfigWxPayment(finished);
            } else {
                dataShop.setWxPayment(unFinished);
                dataShop.setConfigWxPayment(unFinished);
            }
        } else {
            dataShop.setRegisterApplet(unFinished);
            dataShop.setAuthApplet(unFinished1);
            dataShop.setAppletService(unFinished);
            dataShop.setWxPayment(unFinished);
            dataShop.setConfigWxPayment(unFinished);
        }
        // 子账号设置 非0：已完成子账号设置，0：未完成
        dataShop.setChildAccountConf(Metadata.builder()
            .type(BYTE_ONE)
            .value(childAccountService.getInfoBySysId(sysId).size()).build());

        // 公众号  非0：已授权公众号，0：未授权公众号
        dataShop.setOfficialAccountConf(Metadata.builder()
            .type(BYTE_ONE)
            .value(shopOfficialAccount.isOfficialAccountBySysId(sysId, BYTE_ONE)).build());
        dataShop.ruleHandler();
        return dataShop;
    }

    /**
     * 用户登录日志
     * @param param
     * @return
     */
    public PageResult<LoginRecordVo> loginRecord(LoginRecordVo param){
        SelectConditionStep<? extends Record> select = db().select(USER_LOGIN_RECORD.SHOP_ID, USER_LOGIN_RECORD.SHOP_NAME, USER_LOGIN_RECORD.SYS_ID,
                USER_LOGIN_RECORD.USER_ID, USER_LOGIN_RECORD.USER_NAME, USER_LOGIN_RECORD.ADD_TIME, USER_LOGIN_RECORD.USER_IP, USER_LOGIN_RECORD.ACCOUNT_TYPE, MP_AUTH_SHOP.NICK_NAME)
                .from(USER_LOGIN_RECORD.leftJoin(MP_AUTH_SHOP).on(USER_LOGIN_RECORD.SHOP_ID.eq(MP_AUTH_SHOP.SHOP_ID)))
                .where(DSL.trueCondition());
        buildOptions(select,param);

        PageResult<LoginRecordVo> list = this.getPageResult(select, param.getCurrentPage(), param.getPageRows(), LoginRecordVo.class);
        return list;
    }

    /**
     * 用户登录日志条件查询
     * @param select
     * @param param
     */
    private void buildOptions(SelectConditionStep<? extends Record> select,LoginRecordVo param){
        if(param.getShopId() != null) {
            select.and(USER_LOGIN_RECORD.SHOP_ID.eq(param.getShopId()));
        }
        if(isNotEmpty(param.getShopName())){
            select.and(USER_LOGIN_RECORD.SHOP_NAME.eq(param.getShopName()));
        }
        if(param.getSysId() != null){
            select.and(USER_LOGIN_RECORD.SYS_ID.eq(param.getSysId()));
        }
        if(isNotEmpty(param.getUserName())){
            select.and(USER_LOGIN_RECORD.USER_NAME.eq(param.getUserName()));
        }
        if(param.getStartAddTime() != null){
            select.and(USER_LOGIN_RECORD.ADD_TIME.ge(param.getStartAddTime()));
        }
        if(param.getEndAddTime() != null){
            select.and(USER_LOGIN_RECORD.ADD_TIME.le(param.getEndAddTime()));
        }
        if(param.getAccountType() != null){
            select.and(USER_LOGIN_RECORD.ACCOUNT_TYPE.eq(param.getAccountType()));
        }
    }
    /**
     * Shop version String.店铺版本
     *
     * @param shopId the shop id
     * @return String 版本级别
     */
    public String getShopVersion(Integer shopId) {
        String level = db().select(SHOP.SHOP_TYPE).from(SHOP).where(SHOP.SHOP_ID.eq(shopId)).fetchOne().into(String.class);
        return level;
    }

    /**
     * 绑定解绑
     */
    public boolean bindUnBindOfficialForStore(String act, StoreTokenAuthInfo user, Integer accountId) {
        if (StringUtils.isEmpty(act) || user == null) {
            return false;
        }
        byte isBind = 0;
        String bind = "bind";
        if (bind.equals(act)) {
            // 绑定
            isBind = 1;
        }
        String delBind = "del_bind";
        if (delBind.equals(act)) {
            // 解绑
            isBind = 0;
        }if(!(bind.equals(act)|| delBind.equals(act))) {
            logger().debug("绑定解绑传入act参数错误："+act);
            return false;
        }
        int num = 0;
        //主账户在子账户权限管理处操作子账户解绑绑定
        num = saas.shop.storeAccount.updateRowBind(accountId, isBind);
        if (num > 0) {
            return true;
        }
        return false;
    }

    /**
     * 获取绑定/解绑状态  概览使用
     */
    public BindofficialVo getbindUnBindStatusUseByOverForStore(StoreTokenAuthInfo user,String bindAppId){
        BindofficialVo bindofficialVo=new BindofficialVo();
        String nickname=null;
        StoreAccountVo storeAccountVo = saas.shop.storeAccount.getOneInfo(user.getStoreAccountId());
        String officialOpenId = storeAccountVo.getOfficialOpenId();
        Byte isBind = storeAccountVo.getIsBind();

        if (StringUtils.isNotEmpty(officialOpenId)) {
            // 绑定过公众号
            // shopId找auth_shop
            MpOfficialAccountUserRecord user2 = saas.shop.mpOfficialAccountUserService.getUser(bindAppId,officialOpenId);
            nickname = user2.getNickname();
        }
        bindofficialVo.setOfficialOpenId(officialOpenId);
        bindofficialVo.setIsBind(isBind);
        bindofficialVo.setNickName(nickname);
        return bindofficialVo;
    }

    /**
     * 查询门店热销商品
     * @param storeBestSellersParam 门店热销商品查询入参
     * @return PageResult<StoreBestSellersVo>
     */
    public PageResult<StoreBestSellersVo> getBestSellers(StoreBestSellersParam storeBestSellersParam) {
        if (storeBestSellersParam.getType() == 0 && storeBestSellersParam.getStartDate() == null && storeBestSellersParam.getEndDate() == null) {
            storeBestSellersParam.setType(30);
        }
        //得到时间
        if (!storeBestSellersParam.getType().equals(CUSTOM_DAYS)) {
            storeBestSellersParam.setStartDate(getDate(storeBestSellersParam.getType()));
            storeBestSellersParam.setEndDate(getDate(NumberUtils.INTEGER_ZERO));
        }
        return storeAccountService.storeDao.getBestSellers(storeBestSellersParam);
    }

}
