package com.meidianyi.shop.service.saas.shop.official;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.main.tables.records.MpAuthShopRecord;
import com.meidianyi.shop.db.main.tables.records.MpOfficialAccountUserRecord;
import com.meidianyi.shop.db.shop.tables.records.UserRecord;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.shop.config.group.ShopRoleAddListVo;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.meidianyi.shop.db.main.tables.MpOfficialAccountUser.MP_OFFICIAL_ACCOUNT_USER;

/**
 * @author lixinguo
 */
@Service
public class MpOfficialAccountUserService extends MainBaseService {

    public List<MpOfficialAccountUserRecord> getAccountUserListByUnionIds(List<String> unionIds) {
        return db().select(MP_OFFICIAL_ACCOUNT_USER.OPENID,MP_OFFICIAL_ACCOUNT_USER.UNIONID,MP_OFFICIAL_ACCOUNT_USER.APP_ID)
            .from(MP_OFFICIAL_ACCOUNT_USER)
            .where(MP_OFFICIAL_ACCOUNT_USER.UNIONID.in(unionIds))
            .and(MP_OFFICIAL_ACCOUNT_USER.SUBSCRIBE.eq((byte)1))
            .fetchInto(MP_OFFICIAL_ACCOUNT_USER);
    }
    
    public MpOfficialAccountUserRecord getUser(String appId,String openId) {
    	return db().selectFrom(MP_OFFICIAL_ACCOUNT_USER).where(MP_OFFICIAL_ACCOUNT_USER.APP_ID.eq(appId).and(MP_OFFICIAL_ACCOUNT_USER.OPENID.eq(openId))).fetchAny();
    }
    
    public PageResult<ShopRoleAddListVo> getUserList(PageResult<ShopRoleAddListVo> accountRolePageList,String appId) {
    	for(ShopRoleAddListVo sAddListVo:accountRolePageList.dataList) {
    		if(sAddListVo.getOfficialOpenId()!=null) {
    			MpOfficialAccountUserRecord user = getUser(appId, sAddListVo.getOfficialOpenId());
    			sAddListVo.setOfficialNickName(user.getNickname());
    			sAddListVo.setHeadImgUrl(user.getHeadimgurl());    			
    		}
    	}
    	return accountRolePageList;
    }
    
    
    public MpOfficialAccountUserRecord getAccountUserListByRecid(Integer recId) {
        return db().select(MP_OFFICIAL_ACCOUNT_USER.OPENID,MP_OFFICIAL_ACCOUNT_USER.UNIONID,MP_OFFICIAL_ACCOUNT_USER.APP_ID)
            .from(MP_OFFICIAL_ACCOUNT_USER)
            .where(MP_OFFICIAL_ACCOUNT_USER.REC_ID.eq(recId))
            .and(MP_OFFICIAL_ACCOUNT_USER.SUBSCRIBE.eq((byte)1))
            .fetchAnyInto(MP_OFFICIAL_ACCOUNT_USER);
    }
    
    
    /**
     * 通过小程序用户OpenId得到公众号用户的openId
     * @param officialAccountAppId 公众号AppId
     * @param mpAppId 小程序AppId
     * @param mpOpenId 小程序openId
     * @return
     */
    public String getOpenIdFromMpOpenId(String officialAccountAppId,String mpAppId,String mpOpenId) {
    	MpAuthShopRecord mp = saas.shop.mp.getAuthShopByAppId(mpAppId);
    	if(mp != null) {
    		UserRecord user = this.saas.getShopApp(mp.getShopId()).user.getUserFromOpenId(mpOpenId);
    		if(user != null && !StringUtils.isBlank(user.getWxUnionId())) {
    			MpOfficialAccountUserRecord accountUser = getUserByUnionId(officialAccountAppId,user.getWxUnionId());
    			return accountUser != null ? accountUser.getOpenid():null;
    		}
    	}
    	return null;
 
    }
    
    public MpOfficialAccountUserRecord getUserByUnionId(String appId,String openId) {
    	Condition condition = DSL.noCondition();
    	condition = condition
    			.and(MP_OFFICIAL_ACCOUNT_USER.APP_ID.eq(appId))
    			.and(MP_OFFICIAL_ACCOUNT_USER.OPENID.eq(openId));
    	return db().selectFrom(MP_OFFICIAL_ACCOUNT_USER).where(condition).fetchAny();
    }
    
    public MpOfficialAccountUserRecord getUser(String appId) {
    	return db().fetchAny(MP_OFFICIAL_ACCOUNT_USER,MP_OFFICIAL_ACCOUNT_USER.APP_ID.eq(appId));
    }
    
    public MpOfficialAccountUserRecord getUserByUnionIdAndAppId(String unionId,String appId) {
    	return db().selectFrom(MP_OFFICIAL_ACCOUNT_USER).where(MP_OFFICIAL_ACCOUNT_USER.UNIONID.eq(unionId).and(MP_OFFICIAL_ACCOUNT_USER.APP_ID.eq(appId))).fetchAny();
    }
}
