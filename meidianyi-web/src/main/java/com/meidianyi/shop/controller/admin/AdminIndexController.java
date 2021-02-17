package com.meidianyi.shop.controller.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.foundation.jedis.JedisManager;
import com.meidianyi.shop.service.pojo.saas.shop.VersionPath;
import com.meidianyi.shop.service.pojo.saas.shop.version.VersionConfig;
import com.meidianyi.shop.service.pojo.saas.shop.version.VersionMainConfig;
import com.meidianyi.shop.service.pojo.saas.shop.version.VersionName;
import com.meidianyi.shop.service.pojo.shop.auth.MenuParam;
import com.meidianyi.shop.service.pojo.shop.auth.MenuReturnParam;
import com.meidianyi.shop.service.pojo.shop.auth.PrivilegeAndPassParam;
import com.meidianyi.shop.service.pojo.shop.auth.PrivilegeVo;
import com.meidianyi.shop.service.pojo.shop.auth.ShopVersionParam;

/**
 * 
 * @author 新国
 *
 */
@RestController
@RequestMapping("/api")
public class AdminIndexController extends AdminBaseController {
	
	@Autowired
	protected JedisManager jedis;
	
	final protected String menuJsonPath = "admin.privilegeList.json";
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

	@RequestMapping(value = "/admin/showMenu")
	public JsonResult showMenu() {
		String json = Util.loadResource(menuJsonPath);
		String json2 = Util.loadResource(privilegeJsonPath);
		MenuParam menuParam = Util.parseJson(json, MenuParam.class);
		PrivilegeAndPassParam privilParam = Util.parseJson(json2, PrivilegeAndPassParam.class);
		Integer roleId = saas.shop.getShopAccessRoleId(adminAuth.user().sysId, adminAuth.user().loginShopId,
				adminAuth.user().subAccountId);
		PrivilegeVo privilegeVo = new PrivilegeVo();
		//版本权限
		VersionConfig vConfig = saas.shop.version.mergeVersion(adminAuth.user().loginShopId);
		if (vConfig == null) {
			// 版本存在问题，请联系管理员
			return fail(JsonResultCode.CODE_FAIL);
		}
		VersionMainConfig mainConfig = vConfig.getMainConfig();
		privilegeVo.setVMainConfig(mainConfig);
		if (roleId != 0) {
			// 是子账户
			MenuReturnParam menuReturnParam = saas.shop.role.getPrivilegeListPublic(roleId);
			// 加个非空判断
			MenuParam outParam = saas.shop.role.outParam(menuParam, menuReturnParam.getPrivilegeList());
			PrivilegeAndPassParam privilParam2 = new PrivilegeAndPassParam();

			privilParam2.setPrivilegeLlist(Arrays.asList(menuReturnParam.getPrivilegePass().get(0)));
			privilParam2.setPassList((Arrays.asList(menuReturnParam.getPrivilegePass().get(1))));

			
			MenuParam specialParam = saas.shop.role.specialParam(outParam, menuParam.getPlus());
			
			privilegeVo.setMenuParam(specialParam);
			privilegeVo.setPassParam(privilParam2);
			return success(privilegeVo);
		} else {
			// 不是子账户
			MenuParam specialParam = saas.shop.role.specialParam(menuParam, menuParam.getPlus());
			privilegeVo.setMenuParam(specialParam);
			privilegeVo.setPassParam(privilParam);
			return success(privilegeVo);
		}
	}

	/**
	 * 点击的菜单或者功能有没有权限
	 * 
	 * @return
	 */
	@RequestMapping(value = "/admin/checkMenu")
	public JsonResult checkMenu(@RequestBody VersionPath path) {
		String enName = path.getEName();
		String vsName = path.getVName();
		if (StringUtils.isEmpty(enName)) {
			return fail(JsonResultCode.CODE_ACCOUNT_ENNAME_ISNULL);
		}
		
		if (StringUtils.isEmpty(adminAuth.user().loginShopId)) {
			return fail(JsonResultCode.CODE_ACCOUNT_ROLE__SHOP_SELECT);
		}
		
		//判断版本的权限
		JsonResultCode judgeVersion = judgeVersion(enName,vsName,path.getPath());
		if(!judgeVersion.equals(JsonResultCode.CODE_SUCCESS)) {
			return fail(judgeVersion);
		}
		
		Integer roleId = saas.shop.getShopAccessRoleId(adminAuth.user().sysId, adminAuth.user().loginShopId,
				adminAuth.user().subAccountId);
		if (roleId == -1) {
			// 错误
			return fail(JsonResultCode.CODE_FAIL);
		}
		if (roleId == 0) {
			// 不是子账户,返回有权限
			return success(JsonResultCode.CODE_SUCCESS);
		}
		// 子账户，判断是否可以点击
		if (saas.shop.role.checkPrivilegeList(roleId, enName)) {
			return success(JsonResultCode.CODE_SUCCESS);
		}
		return fail(JsonResultCode.CODE_FAIL);
	}
	
	/**
	 * 前端小程序装修，模块的显示问题
	 * @return
	 */
	@RequestMapping(value = "/admin/checkMenu/showMa")
	public JsonResult wxMaShow() {
		String[] verifys = saas.shop.version.verifyVerPurview(adminAuth.user().loginShopId, VersionName.SUB_2);
		String[] sub2 = VersionName.SUB_2;
		List<String> list=new ArrayList<String>();
		for(int i=0;i<verifys.length;i++) {
			if("true".equals(verifys[i])) {
				list.add(sub2[i]);
			}
		}
		return success(list);
	}


	/**
	 * 店铺权限的判断
	 * @param enName
	 * @param vsName
	 * @return
	 */
	public JsonResultCode judgeVersion(String enName, String vsName,String path) {
		logger().info("权限判断传入的enName："+enName+"和vsName值："+vsName+"请求的地址："+path);
		VersionConfig vConfig = saas.shop.version.mergeVersion(adminAuth.user().loginShopId);
		if (vConfig == null) {
			logger().info("版本存在问题，请联系管理员");
			// 版本存在问题，请联系管理员
			return JsonResultCode.CODE_FAIL;
		}
		VersionMainConfig mainConfig = vConfig.getMainConfig();
		if (StringUtils.isEmpty(enName)) {
			logger().info("enName为空");
			return JsonResultCode.CODE_ACCOUNT_ENNAME_ISNULL;
		}
		 
		String json = Util.loadResource(versionJson);
		List<ShopVersionParam> list = Util.parseJson(json, new TypeReference<List<ShopVersionParam>>() {
		});
		
		//enName校验在不在admin.versionNew.json中，不在返回成功
		if (!includeEname(list, enName,null)) {
			// 请求不在所有定义的权限里，返回成功
			logger().info("请求enName："+enName+"不在json文件中，不校验");
			return JsonResultCode.CODE_SUCCESS;
		}
		
		//带vsname的定义为需要校验，以后添加权限要添加vsname，前端要做好添加
		if(StringUtils.isEmpty(vsName)) {
			logger().info("vsName为空，不校验权限");
			return JsonResultCode.CODE_SUCCESS;
		}
		
		//校验enName和VsName是否匹配
		if (!includeEname(list, enName,vsName)) {
			// 请求不在所有定义的权限里
			return JsonResultCode.CODE_ACCOUNT_ROLE__AUTH_INSUFFICIENT;
		}
		
		//去vConfig中校验是否存在
		if (!saas.shop.version.checkMainConfig(mainConfig, vsName)) {
			logger().info("vsName："+vsName+"不在vConfig权限里");
			// 此功能需要更高版本才可使用。如需了解详情我们的产品顾问将尽快与您联系！！
			return JsonResultCode.CODE_ACCOUNT_VERSIN_NO_POWER;
		}
		//不校验api，由拦截器进行校验
//		//校验API是否在请求里
//		if(!checkApi(list, enName, vsName, path)) {
//			//先返回true
//			return JsonResultCode.CODE_SUCCESS;
//			// 请求不在所有定义的权限里
//			//return JsonResultCode.CODE_ACCOUNT_ROLE__AUTH_INSUFFICIENT;
//		}
		
		return JsonResultCode.CODE_SUCCESS;

	}

	/**
	 * 匹配返回true
	 * 
	 * @param eNameList
	 * @param reqEnName
	 * @return
	 */
	private Boolean includeEname(List<ShopVersionParam> eNameList, String reqEnName, String reqVsName) {
		for (ShopVersionParam allEname : eNameList) {
			if(!StringUtils.isEmpty(reqVsName)) {
				if(allEname.getVsName().equals(reqVsName)) {
					if(allEname.getEnName().equals(reqEnName)) {
						return true;
					}
				}
			}else {
				if(allEname.getEnName().equals(reqEnName)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 校验api是否在里面
	 * @param eNameList
	 * @param reqEnName
	 * @param reqVsName
	 * @return
	 */
	private Boolean checkApi(List<ShopVersionParam> eNameList, String reqEnName, String reqVsName,String path) {
		for (ShopVersionParam allEname : eNameList) {
			if (allEname.getEnName().equals(reqEnName)) {
				if (!StringUtils.isEmpty(reqVsName)) {
					if (allEname.getVsName().equals(reqVsName)) {
						List<String> includeApi = allEname.getIncludeApi();
						if(match(includeApi, path)) {
							return true;
						}
						logger().info("请求的api："+path+"没在ename："+reqEnName+"；vsName："+reqVsName+"的includeApi里");
						return false;
					} else {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}
	
	private boolean match(List<String> regexps, String path) {
		for (String regexp : regexps) {
			if (match(regexp, path)) {
				return true;
			}
		}
		return false;
	}

	private boolean match(String regexp, String path) {
		char asterisk = '*';
		if (regexp.charAt(regexp.length() - 1) == asterisk) {
			regexp = regexp.substring(0, regexp.length() - 1);
			return path.startsWith(regexp);
		} else {
			return regexp.equals(path);
		}
	}
	
}
