package com.meidianyi.shop.auth;

import com.google.common.base.Joiner;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.AuthConfig;
import com.meidianyi.shop.db.main.tables.records.MpAuthShopRecord;
import com.meidianyi.shop.db.main.tables.records.ShopAccountRecord;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.db.main.tables.records.UserLoginRecordRecord;
import com.meidianyi.shop.service.foundation.jedis.JedisManager;
import com.meidianyi.shop.service.pojo.shop.auth.StoreAuthConstant;
import com.meidianyi.shop.service.pojo.shop.auth.StoreAuthInfoVo;
import com.meidianyi.shop.service.pojo.shop.auth.StoreLoginParam;
import com.meidianyi.shop.service.pojo.shop.auth.StoreTokenAuthInfo;
import com.meidianyi.shop.service.pojo.shop.store.account.StoreAccountVo;
import com.meidianyi.shop.service.saas.SaasApplication;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;

/**
 * @author chenjie
 * @date 2020年08月20日
 */
@Component
public class StoreAuth {

    @Autowired
    protected AuthConfig authConfig;

    @Autowired
    protected HttpServletRequest request;

    @Autowired
    protected SaasApplication saas ;

    @Autowired
    protected JedisManager jedis;

    protected Logger log = LoggerFactory.getLogger(StoreAuth.class);

    protected static final String TOKEN = "V-Token";
    protected static final String AUTH_SECRET = "auth.secret";
    protected static final String AUTH_TIMEOUT = "auth.timeout";
    protected static final String TOKEN_PREFIX = "STO@";

    /**
     * 登出
     */
    public boolean logout() {
        StoreTokenAuthInfo info = user();
        if(info == null) {
            return false;
        }
        this.deleteTokenInfo(info);
        return true;
    }

    protected String getToken() {
        return request != null ? request.getHeader(TOKEN) : null;
    }

    /**
     * 是否有效store登录TOKEN
     *
     * @param  token
     * @return
     */
    public boolean isValidToken(String token) {
        return StringUtils.isNotEmpty(token) && StringUtils.startsWith(token, TOKEN_PREFIX);
    }

    /**
     * 登录账户
     *
     * @param  param
     * @return
     */
    public StoreTokenAuthInfo login(StoreLoginParam param) {
        ShopAccountRecord account = saas.shop.account.getAccountInfo(param.username);
        StoreTokenAuthInfo info = new StoreTokenAuthInfo();
        StoreAuthInfoVo storeAuthInfoVo = new StoreAuthInfoVo();
        if (account == null) {
            storeAuthInfoVo.setMsg(StoreAuthConstant.SHOP_ACCOUNT_NOT_EXIST);
            info.setStoreAuthInfoVo(storeAuthInfoVo);
            return info;
        }
        param.setSysId(account.getSysId());
        StoreAuthInfoVo storeAuthInfoVoNew = saas.shop.storeManageService.storeAccountService.verifyStoreLogin(param);
        info.setStoreAuthInfoVo(storeAuthInfoVoNew);
        if (!StoreAuthConstant.STORE_AUTH_OK.equals(storeAuthInfoVoNew.getIsOk())) {
            return info;
        }
        info.setSysId(account.getSysId());
        info.setUserName(account.getUserName());
        info.setSubAccountId(0);
        info.setSubUserName("");
        info.setLoginShopId(storeAuthInfoVoNew.getStoreAccountInfo().getShopId());
        info.setAccountName(account.getAccountName());
        StoreAccountVo storeAccountVo = storeAuthInfoVoNew.getStoreAccountInfo();
        info.setStoreAccountId(storeAccountVo.getAccountId());
        info.setStoreAccountName(storeAccountVo.getAccountName());
        info.setStoreAccountType(storeAccountVo.getAccountType());
        info.setStoreIds(storeAccountVo.getStoreLists());

        // 如果当前登录用户与正在登录的用户相同，则使用当前登录用户的Token
        StoreTokenAuthInfo user = user();
        if(user!=null && user.getSysId().equals(info.getSysId()) && user.getSubAccountId().equals(info.getSubAccountId())) {
            info.setToken(user.getToken());
        }
        //小程序信息
        MpAuthShopRecord record=saas.shop.mp.getAuthShopByShopIdAddUrl(storeAuthInfoVoNew.getStoreAccountInfo().getShopId());
        info.setQrcodeUrl(record.getQrcodeUrl());
        info.setNickName(record.getNickName());
        this.saveTokenInfo(info);
        return info;
    }

    /**
     * 保存登录信息
     *
     * @param info
     */
    public void saveTokenInfo(StoreTokenAuthInfo info) {
        if (StringUtils.isBlank(info.getToken())) {
            String loginToken = TOKEN_PREFIX
                + Util.md5(String.format("store_login_%d_%d_%d_%s_%s_%d", info.getSysId(), info.getSubAccountId(),
                info.getStoreAccountId(),info.getStoreIds().size()>0 ? Joiner.on(",").join(info.getStoreIds()):"",Util.randomId(), Calendar.getInstance().getTimeInMillis()));
            info.setToken(loginToken);
        }
        jedis.set(info.token, Util.toJson(info), authConfig.getTimeout());
    }

    /**
     * 删除登录信息
     *
     * @param info
     */
    public void deleteTokenInfo(StoreTokenAuthInfo info) {
        jedis.delete(info.token);
    }

    /**
     * 得到当前登录用户
     *
     * @return
     */
    public StoreTokenAuthInfo user() {
        String token = getToken();
        if (this.isValidToken(token)) {
            String json = jedis.get(token);
            if (!StringUtils.isBlank(json)) {
                return Util.parseJson(json, StoreTokenAuthInfo.class);
            }
        }
        return null;
    }

    /**
     * 重置token的存活时间
     *
     */
    public void reTokenTtl() {
        StoreTokenAuthInfo info = user();
        if (null != info) {
            this.saveTokenInfo(info);
        }
    }

    /**
     * 更新accountName
     * @param accountName
     */
    public void updateAccountName(String accountName) {
        StoreTokenAuthInfo user = user();
        user.setAccountName(accountName);
        jedis.set(getToken(), Util.toJson(user));
    }
}
