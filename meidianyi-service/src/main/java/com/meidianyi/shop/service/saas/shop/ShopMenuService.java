package com.meidianyi.shop.service.saas.shop;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.foundation.jedis.JedisManager;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.saas.shop.version.VersionConfig;
import com.meidianyi.shop.service.pojo.saas.shop.version.VersionMainConfig;
import com.meidianyi.shop.service.pojo.shop.auth.AuthConstant;
import com.meidianyi.shop.service.pojo.shop.auth.ShopMenuParam;
import com.meidianyi.shop.service.pojo.shop.auth.ShopPriPassParam;
import com.meidianyi.shop.service.pojo.shop.auth.ShopVersionListVo;
import com.meidianyi.shop.service.pojo.shop.auth.ShopVersionParam;
import com.meidianyi.shop.service.pojo.shop.auth.ShopMenuList;

/**
 * 
 * @author 新国
 *
 */
@Service
public class ShopMenuService extends MainBaseService {

	final protected String menuJson = "admin.authorityNew.json";
	final protected String authorityJson = "admin.privilegePassNew.json";
	final protected String versionJson = "admin.versionNew.json";

	private static final String PRNAMELIST = "prNameList";

	private static final String ENNAMELIST = "enNameList";

	private static final String CHILDCONFIG = "child_config";

	@Autowired
	private JedisManager jedis;
	
	/**
	 * 子账户对应展示按钮和输入密码的权限校验
	 * 
	 * @param roleId
	 * @param path
	 * @param reqeName
	 * @return
	 */
	public JsonResultCode passwdAccess(Integer roleId, String path, String reqeName, String passwd) {
		String[] privilegePass = roleId == 0 ? null : saas().shop.role.getPrivilegePass(roleId);
		if (privilegePass == null) {
			return JsonResultCode.CODE_SUCCESS;
		}
		if (StringUtils.isEmpty(privilegePass[0])) {
			// 您没有权限。不能展示
			return JsonResultCode.CODE_ACCOUNT_ROLE__AUTH_INSUFFICIENT;
		}
		if (StringUtils.isEmpty(privilegePass[1])) {
			// 不需要密码，验证成功
			return JsonResultCode.CODE_SUCCESS;
		}

		//Util.loadResource(authorityJson);
		String json = getCacheInfo(AuthConstant.KEY_AUTHORITY,AuthConstant.FILE_AUTHORITYJSON);

		List<ShopPriPassParam> list = Util.parseJson(json, new TypeReference<List<ShopPriPassParam>>() {
		});
		ShopPriPassParam sPassParam = list.get(0);
		String preName = sPassParam.getPrName();
		if (PRNAMELIST.equals(preName)) {
			List<String> prNameList = sPassParam.getIncludeApi();
			if (!includeEname(prNameList, reqeName)) {
				// 请求不在所有定义的权限里
				return JsonResultCode.CODE_ACCOUNT_ROLE__AUTH_INSUFFICIENT;
			}
		}

		String[] pShow = privilegePass[0].split(",");
		String[] pPass = privilegePass[1].split(",");

		if (!include(pShow, reqeName)) {
			// 请求不在权限json里
			return JsonResultCode.CODE_ACCOUNT_ROLE__AUTH_INSUFFICIENT;
		}

		for (int i = 1; i < list.size(); i++) {
			ShopPriPassParam sParam = list.get(i);
			String prName2 = sParam.getPrName();
			if (prName2.equals(reqeName)) {
				if (includeEname(sParam.getIncludeApi(), path)) {
					// 请求api在json对应的api里面
					//
					if (include(pShow, reqeName)) {
						// 按钮可以点
						if (include(pPass, reqeName)) {
							// 需要密码
							if (StringUtils.isEmpty(passwd)) {
								// 需要权限密码。
								return JsonResultCode.CODE_ACCOUNT_NEED_PRIVILEGEPASS;
							}
							if (saas().shop.role.veryPasswd(passwd, roleId)) {
								// 密码校验正常
								return JsonResultCode.CODE_SUCCESS;

							} else {
								// 密码错误
								return JsonResultCode.CODE_ACCOUNT_OR_PASSWORD_INCRRECT;

							}
						}
						return JsonResultCode.CODE_SUCCESS;
					}

				}
			}
		}

		return JsonResultCode.CODE_FAIL;
	}

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
	 * 子账户对应发送api权限的校验
	 * 
	 * @param roleId
	 * @param path
	 * @param reqeName
	 * @return
	 */
	public Boolean apiAccess(Integer roleId, String path, String reqeName) {
		String[] privilegeList = roleId == 0 ? null : saas().shop.role.getPrivilegeList(roleId);
		if (privilegeList == null) {
			// 主账户登录，暂时不校验权限。
			// TODO 加不加权限看以后
			return true;
		}
		if (StringUtils.isEmpty(reqeName)) {
			return false;
		}
		//Util.loadResource(menuJson);
		String json = getCacheInfo(AuthConstant.KEY_MENU, AuthConstant.FILE_MENUJSON);

		List<ShopMenuParam> list = Util.parseJson(json, new TypeReference<List<ShopMenuParam>>() {
		});

		ShopMenuParam sParam = list.get(0);
		String eName = sParam.getEnName();
		if (ENNAMELIST.equals(eName)) {
			List<String> eNameList = sParam.getIncludeApi();
			if (!includeEname(eNameList, reqeName)) {
				// 请求不在所有定义的权限里
				return false;
			}
		}

		// 单独处理：子账户不能操作店铺权限菜单
		if (CHILDCONFIG.equals(reqeName)) {
			return false;
		}

		if (!include(privilegeList, reqeName)) {
			// 请求菜单不在用户权限里
			return false;
		}

		// 去json查询这个权限对应的api
		for (int i = 1; i < list.size(); i++) {
			ShopMenuParam shopMenuParam = list.get(i);
			String eName2 = shopMenuParam.getEnName();
			if (eName2.equals(reqeName)) {
				// 请求api在权限对应的api里面
				if (includeEname(shopMenuParam.getIncludeApi(), path)) {
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
			match(String.valueOf(allEname), reqEnName);
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
