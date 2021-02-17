package com.meidianyi.shop.service.saas.shop.official;

import static com.meidianyi.shop.db.main.tables.MpOfficialAccount.MP_OFFICIAL_ACCOUNT;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.meidianyi.shop.db.main.tables.records.MpOfficialAccountRecord;
import com.meidianyi.shop.service.foundation.service.MainBaseService;

import me.chanjar.weixin.mp.api.WxMpService;

/**
 * @author lixinguo
 */
@Service
public class MpOfficialAccountService extends MainBaseService {


	/**
	 * 得到公众号的服务
	 * @param appId
	 * @return
	 */
	public WxMpService getOfficialAccountClient(String appId) {
		MpOfficialAccountRecord account = getOfficialAccountByAppid(appId);
		Assert.isTrue(account !=null &&account.getIsAuthOk() == (byte)1,"MpOfficial is null");
		return open.getWxOpenComponentService().getWxMpServiceByAppid(appId);
	}

	/**
	 * 
	 * @param appId
	 * @return
	 */
	public MpOfficialAccountRecord  getOfficialAccountByAppid(String appId){
		return db().fetchAny(MP_OFFICIAL_ACCOUNT,MP_OFFICIAL_ACCOUNT.APP_ID.eq(appId));
	}
	
	/**
	 * 判断是否授权
	 * @param appId
	 * @return
	 */
	public Boolean isAuthOk(String appId) {
		MpOfficialAccountRecord account = getOfficialAccountByAppid(appId);
		return (account !=null &&account.getIsAuthOk() == (byte)1);
	}
	   
}
