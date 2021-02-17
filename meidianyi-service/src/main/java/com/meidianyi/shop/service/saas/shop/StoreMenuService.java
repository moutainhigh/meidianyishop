package com.meidianyi.shop.service.saas.shop;

import com.fasterxml.jackson.core.type.TypeReference;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.foundation.jedis.JedisManager;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.saas.shop.version.VersionConfig;
import com.meidianyi.shop.service.pojo.saas.shop.version.VersionMainConfig;
import com.meidianyi.shop.service.pojo.shop.auth.*;
import com.meidianyi.shop.service.pojo.shop.store.authority.StoreAuthParam;
import com.meidianyi.shop.service.pojo.shop.store.authority.StoreConstant;
import com.meidianyi.shop.service.pojo.shop.store.authority.Sub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenjie
 * @date 2020年08月24日
 */
@Service
public class StoreMenuService extends MainBaseService {

    /**
     * 账号登录例外URL
     */
    protected String[] authExcept = { "/api/store/overview","/api/store/showMenu","/api/store/overview/wait/data",
                                   "/api/store/overview/statistic/data" ,"/api/store/overview/article/list","/api/store/all/get",
                                    "/api/store/public/service/bind/getOfficialQrCode","/api/store/survey/official/bind",
                                    "/api/store/malloverview/getbindUnBindStatus","/api/store/overview/article/get/*"};
    final protected String menuJson = "admin.authorityNew.json";
    final protected String authorityJson = "admin.privilegePassNew.json";
    final protected String versionJson = "admin.versionNew.json";

    private static final String PRNAMELIST = "prNameList";

    private static final String ENNAMELIST = "enNameList";

    private static final String CHILDCONFIG = "child_config";

    @Autowired
    private JedisManager jedis;

    /**
     * 从缓存读
     * @return
     */
    private String getCacheInfo(String redisKey,String fileName) {
        String json = jedis.get(redisKey);
        if(StringUtils.isEmpty(json)) {
            logger().info("文件{}没有缓存，重新读取",fileName);
            json = Util.loadResource(fileName);
            jedis.set(redisKey, json);
        }
        return json;
    }

    /**
     * 店员账号账户对应发送api权限的校验
     * @param accountType
     * @param path
     * @param shopId
     * @return
     */
    public Boolean apiAccess(Byte accountType, String path, Integer shopId) {
        List<StoreAuthParam> privilegeList = StoreAuthConstant.STORE_DIRECTOR.equals(accountType) ? null : saas().shop.storeManageService.getstoreJson(shopId).getAuthList();
        if (privilegeList == null) {
            // 店长登录，暂时不校验权限。
            // TODO 加不加权限看以后
            return true;
        }
        if(match(authExcept, path)) {
            return true;
        }
        // 去json查询这个权限对应的api
        for (int i = 1; i < privilegeList.size(); i++) {
            StoreAuthParam storeAuthParam = privilegeList.get(i);
            // 请求api在权限对应的api里面
            if (StoreConstant.CHECK_ZERO.equals(storeAuthParam.getCheck())) {
                continue;
            }
            for (Sub sub : storeAuthParam.getSub()) {
                if (includeEname(sub.getIncludeApi(), path)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 匹配返回true
     *
     * @param eNameList
     * @param reqEnName
     * @return
     */
    private static Boolean includeEname(List<?> eNameList, String reqEnName) {
        for (Object allEname : eNameList) {
            if (match(String.valueOf(allEname), reqEnName)) {
                return true;
            }
        }
        return false;
    }

    private static Boolean include(String[] eNameList, String reqEnName) {
        for (Object allEname : eNameList) {
            if (allEname.equals(reqEnName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean match(String regexp, String path) {
        char asterisk = '*';
        if (regexp.charAt(regexp.length() - 1) == asterisk) {
            regexp = regexp.substring(0, regexp.length() - 1);
            return path.startsWith(regexp);
        } else {
            return regexp.equals(path);
        }
    }

    public boolean match(String[] regexps, String path) {
        for (String regexp : regexps) {
            if (match(regexp, path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 版本权限的校验 按照接口校验
     *
     * @param shopId
     * @param path
     * @param reqEnName
     * @param reqVsName
     * @return
     */
    public JsonResultCode versionAccess(Integer shopId, String path, String reqEnName, String reqVsName) {
        VersionConfig vConfig = saas().shop.version.mergeVersion(shopId);
        if (vConfig == null) {
            // 版本存在问题，请联系管理员
            return JsonResultCode.CODE_FAIL;
        }
        VersionMainConfig mainConfig = vConfig.getMainConfig();
        //Util.loadResource(versionJson);
        String json = getCacheInfo(AuthConstant.KEY_VERSION, AuthConstant.FILE_VERSIONJSON);
        List<ShopVersionParam> list = Util.parseJson(json, new TypeReference<List<ShopVersionParam>>() {
        });
        List<String> versionJson = (List<String>) list.get(0).getIncludeApi();

        if (!includeEname(versionJson, reqVsName)) {
            // 请求不在所有定义的权限里
            return JsonResultCode.CODE_ACCOUNT_ROLE__AUTH_INSUFFICIENT;
        }

        if (!saas().shop.version.checkMainConfig(mainConfig, reqVsName)) {
            // 此功能需要更高版本才可使用。如需了解详情我们的产品顾问将尽快与您联系！！
            return JsonResultCode.CODE_ACCOUNT_VERSIN_NO_POWER;
        }
        // 查询对应的api
        for (int i = 1; i < list.size(); i++) {
            ShopVersionParam sParam = list.get(i);
            if (reqVsName.equals(sParam.getVsName()) && reqEnName.equals(sParam.getEnName())) {
                versionJson = (List<String>) sParam.getIncludeApi();
                // 有些特殊的功能在对应的api方法里校验。规定这些特殊的IncludeApi为空
                // 以后请往后添加-》》》目前包括： 小程序管理中的十个，门店买单送积分，签到送积分，门店买单 ，技师管理，服务管理
                if (versionJson.size() == 0) {
                    return JsonResultCode.CODE_SUCCESS;
                }
                // 请求包含在api里
                if (includeEname(versionJson, path)) {
                    // 和用户自己的权限进行校验
                    return JsonResultCode.CODE_SUCCESS;
                }
                //
                return JsonResultCode.CODE_ACCOUNT_ROLE__AUTH_INSUFFICIENT;
            }

        }
        // 此功能需要更高版本才可使用。如需了解详情我们的产品顾问将尽快与您联系！！
        return JsonResultCode.CODE_ACCOUNT_VERSIN_NO_POWER;
    }


    /**
     * 返回所有权限信息
     * @return
     */
    public ShopMenuList getAuthority() {
        logger().info("查询Json");
        String json = Util.loadResource(menuJson);

        List<ShopMenuParam> list = Util.parseJson(json, new TypeReference<List<ShopMenuParam>>() {
        });
        List<List<ShopMenuParam>> outList=new ArrayList<List<ShopMenuParam>>();
        for(int i=0;i<=list.get(list.size()-1).getTopIndex()+1;i++) {
            List<ShopMenuParam> innerList=new ArrayList<ShopMenuParam>();
            for(ShopMenuParam param:list) {
                if(i==param.getTopIndex()) {
                    innerList.add(param);
                }
            }
            if(innerList.size()>0) {
                outList.add(innerList);
            }
        }
        logger().info("查询pwdJson");
        String pwdJson = Util.loadResource(authorityJson);

        List<ShopPriPassParam> pwdlist = Util.parseJson(pwdJson, new TypeReference<List<ShopPriPassParam>>() {
        });
        List<List<ShopPriPassParam>> outPwdList=new ArrayList<List<ShopPriPassParam>>();
        for(int i=0;i<=pwdlist.get(pwdlist.size()-1).getTopIndex()+1;i++) {
            List<ShopPriPassParam> innerList=new ArrayList<ShopPriPassParam>();
            for(ShopPriPassParam param:pwdlist) {
                if(i==param.getTopIndex()) {
                    innerList.add(param);
                }
            }
            if(innerList.size()>0) {
                outPwdList.add(innerList);
            }
        }
        ShopMenuList vo=new ShopMenuList();
        vo.setShopMenuList(outList);
        vo.setShopPriPassList(outPwdList);

        return vo;
    }


    /**
     * 返回system版本权限
     * @return
     */
    public ShopVersionListVo getVersion() {
        String json = Util.loadResource(versionJson);
        List<ShopVersionParam> list = Util.parseJson(json, new TypeReference<List<ShopVersionParam>>() {
        });
        ShopVersionListVo vo=new ShopVersionListVo();
        for(int i=0;i<=list.get(list.size()-1).getTopIndex()+1;i++) {
            List<ShopVersionParam> innerList=new ArrayList<ShopVersionParam>();
            for(ShopVersionParam param:list) {
                if(i==param.getTopIndex()) {
                    innerList.add(param);
                }
            }
            if(innerList.size()>0) {
                switch (i) {
                    case 0:
                        vo.setSub0(innerList);
                        break;
                    case 1:
                        vo.setSub1(innerList);
                        break;
                    case 2:
                        vo.setSub2(innerList);
                        break;
                    case 3:
                        vo.setSub3(innerList);
                        break;
                    case 4:
                        vo.setSub4(innerList);
                        break;
                    case 5:
                        vo.setSub5(innerList);
                        break;
                    default:
                        break;
                }
            }
        }
        return vo;
    }
}
