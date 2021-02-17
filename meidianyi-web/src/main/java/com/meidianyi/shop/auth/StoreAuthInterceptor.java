package com.meidianyi.shop.auth;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.dao.foundation.database.DatabaseManager;
import com.meidianyi.shop.service.foundation.language.LanguageManager;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.foundation.service.StoreBaseService;
import com.meidianyi.shop.service.pojo.shop.auth.StoreTokenAuthInfo;
import com.meidianyi.shop.service.saas.SaasApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author chenjie
 * @date 2020年08月20日
 */
@Component
public class StoreAuthInterceptor extends HandlerInterceptorAdapter {

    private static final String URL_NO_AUTH = "/api/store/authority/not";
    private static final String URL_LOGIN = "/api/store/login";

    Logger log= LoggerFactory.getLogger(StoreAuthInterceptor.class);

    @Autowired
    protected StoreAuth storeAuth;

    @Autowired
    protected SaasApplication saas;

    @Autowired
    protected DatabaseManager databaseManager;

    final String LANG = "V-Lang";

    /**
     * 账号登录例外URL
     */
    protected String[] accountLoginExcept = { "/api/store/login", "/api/store/login/*", "/api/store/logout", "/region/*",
        "/wechat/notify/*", "/api/admin/notice/*", "/api/admin/subPasswordModify", "/api/admin/password", "/api/admin/official/*",
        "/api/admin/public/service/auth/list", "/api/admin/public/service/auth/detail", "/api/admin/public/image/account/*",
        "/api/admin/authority/*", "/api/admin/message" };


    /**
     * 一些特殊的api，不校验
     */
    protected String[] specialExcept = { "/api/store/checkMenu/*", "/api/store/showMenu" };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        String path = request.getRequestURI();
        String language = request.getHeader(LANG);
        String enName = request.getHeader("V-EnName");
        // 版本控制传的值
        String vsName = request.getHeader("V-VsName");
        // 切换语言
        LanguageManager.switchLanguage(language);
        // 如果为账户登录例外URL，直接通过
        if (match(this.accountLoginExcept, path)) {
            return true;
        }
        StoreTokenAuthInfo user = storeAuth.user();
        // 设置当前线程登录用户
        StoreBaseService.setCurrentStoreLoginUser(user);
        if (user == null) {
            errorResponse(request, response, URL_LOGIN,
                (new JsonResult()).fail(language, JsonResultCode.CODE_ACCOUNT_LOGIN_EXPIRED));
            return false;
        } else {
            storeAuth.reTokenTtl();
            databaseManager.switchShopDb(user.getLoginShopId());
            if(match(specialExcept, path)) {
                return true;
            }
            // 账号登录，判断权限
            Integer shopId = user.getLoginShopId();

            // 判断页面对应api权限
            if (!saas.shop.storeMenu.apiAccess(user.getStoreAccountType(), path, shopId)) {
                log.info("子账户判断页面对应api权限");
                errorResponse(request, response, URL_NO_AUTH,
                    (new JsonResult()).fail(language, JsonResultCode.CODE_ACCOUNT_ROLE__AUTH_INSUFFICIENT));
                return false;
            }

        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                @Nullable Exception ex) throws Exception {
        ShopBaseService.removeCurrentAdminLoginUser();
    }

    protected void errorResponse(HttpServletRequest request, HttpServletResponse response, String path,
                                 JsonResult result) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write(Util.toJson(result));
        writer.close();
        response.flushBuffer();
    }

    public boolean match(String[] regexps, String path) {
        for (String regexp : regexps) {
            if (match(regexp, path)) {
                return true;
            }
        }
        return false;
    }

    public boolean match(String regexp, String path) {
        char asterisk = '*';
        if (regexp.charAt(regexp.length() - 1) == asterisk) {
            regexp = regexp.substring(0, regexp.length() - 1);
            return path.startsWith(regexp);
        } else {
            return regexp.equals(path);
        }
    }
}
