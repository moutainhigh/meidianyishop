package com.meidianyi.shop.controller.store;

import com.fasterxml.jackson.core.type.TypeReference;
import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.foundation.jedis.JedisManager;
import com.meidianyi.shop.service.pojo.shop.auth.StoreAuthConstant;
import com.meidianyi.shop.service.pojo.shop.store.authority.StoreAuthParam;
import com.meidianyi.shop.service.pojo.shop.store.authority.StoreConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author chenjie
 * @date 2020年08月25日
 */
@RestController
@RequestMapping("/api/store")
public class StoreIndexController extends StoreBaseController {
    @Autowired
    protected JedisManager jedis;

    final protected String menuJsonPath = "admin.storeTop.json";
    final protected String privilegeJsonPath = "admin.privilegePass.json";

    private static final String ENNAME="V-EnName";
    /**
     * 版本控制传的值
     */
    private static final String VSNAME="V-VsName";
    final protected String versionJson = "admin.versionNew.json";

    /**
     * 返回店铺菜单
     *
     * @return
     */

    @RequestMapping(value = "/showMenu")
    public JsonResult showMenu() {
        if (StoreAuthConstant.STORE_CLERK.equals(storeAuth.user().getStoreAccountType())) {
            List<StoreAuthParam> menuList = saas.shop.storeManageService.getstoreJson(storeAuth.user().getLoginShopId()).getAuthList();
            return success(menuList);
        } else {
            String json = Util.loadResource(menuJsonPath);
            List<StoreAuthParam> menuList = Util.parseJson(json, new TypeReference<List<StoreAuthParam>>() {
            });
            for (StoreAuthParam menu:menuList) {
                menu.getSub().removeIf(sub-> StoreConstant.ISONLY_ONE.equals(sub.getIsOnly()));
            }
            return success(menuList);
        }
    }
}
