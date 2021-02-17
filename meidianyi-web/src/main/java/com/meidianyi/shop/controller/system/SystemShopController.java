package com.meidianyi.shop.controller.system;

import java.sql.Timestamp;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.main.tables.records.ShopAccountRecord;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.service.pojo.saas.shop.ShopListQueryParam;
import com.meidianyi.shop.service.pojo.saas.shop.ShopPojo;
import com.meidianyi.shop.service.pojo.saas.shop.VersionEditParam;
import com.meidianyi.shop.service.pojo.saas.shop.VersionListQueryParam;
import com.meidianyi.shop.service.pojo.saas.shop.VersionShowParam;
import com.meidianyi.shop.service.pojo.saas.shop.version.VersionConfig;
import com.meidianyi.shop.service.pojo.shop.auth.ShopEnableReq;
import com.meidianyi.shop.service.pojo.shop.auth.ShopMobileReq;
import com.meidianyi.shop.service.pojo.shop.auth.ShopRenewListParam;
import com.meidianyi.shop.service.pojo.shop.auth.ShopRenewReq;
import com.meidianyi.shop.service.pojo.shop.auth.ShopRenewVo;
import com.meidianyi.shop.service.pojo.shop.auth.ShopReq;

/**
 * 
 * @author 新国
 *
 */
@RestController
@RequestMapping("/api")
public class SystemShopController extends SystemBaseController {
	final String TOKEN = "V-Token";
	private static Logger log = LoggerFactory.getLogger(SystemShopController.class);
	
	/**
	 * 验证手机号是否重复
	 * 
	 * @param mobile
	 * @return
	 */
	@PostMapping("/system/shop/check/mobile")
	public JsonResult checkMobile(@RequestBody ShopMobileReq shopMobileReq) {
		if (StringUtils.isEmpty(shopMobileReq.mobile)) {
			return fail(JsonResultCode.CODE_FAIL);
		}
		ShopRecord recode = saas.shop.getShopByMobile(shopMobileReq.mobile);
		if (recode != null) {
			return fail(JsonResultCode.CODE_ACCOUNT_MODILE_APPLIED);
		}
		return success(JsonResultCode.CODE_SUCCESS);
	}

	/**
	 * 添加店铺
	 * 
	 * @param shopReq
	 * @return
	 */
	@PostMapping("/system/shop/add")
	public JsonResult shopAdd(@RequestBody @Valid ShopReq shopReq) {
		ShopAccountRecord accountInfo = saas.shop.account.getAccountInfoForId(shopReq.getSysId());
		if (accountInfo == null) {
			return fail();
		}
		if (saas.shop.addShop(shopReq, sysAuth.user(), request)) {
			return success(JsonResultCode.CODE_SUCCESS);
		}
		return fail();
	}

	/**
	 * 续费
	 * 
	 * @param sReq
	 * @return
	 */
	@PostMapping("/system/shop/renew")
	public JsonResult shopRenew(@RequestBody @Valid ShopRenewReq sReq) {
		ShopRecord checkShop = saas.shop.checkShop(sReq.getShopId(), sReq.getSysId());
		if(checkShop==null) {
			//店铺id或sysid错误
			return fail(JsonResultCode.CODE_FAIL);
		}
		int num = saas.shop.renew.insertShopRenew(sReq, sysAuth.user());
		if (num < 1) {
			return fail(JsonResultCode.CODE_FAIL);
		}
		return success(JsonResultCode.CODE_SUCCESS);
	}
	
	/**
	 * 续费前查询上次续费到期时间
	 * @param sReq
	 * @return
	 */
	@PostMapping("/system/shop/renew/query")
	public JsonResult shopRenewQuery(@RequestBody ShopRenewReq sReq) {
		ShopRecord checkShop = saas.shop.checkShop(sReq.getShopId(), sReq.getSysId());
		if(checkShop==null) {
			//店铺id或sysid错误
			return fail(JsonResultCode.CODE_FAIL);
		}
		Timestamp shopRenewExpireTime = saas.shop.renew.getShopRenewExpireTime(sReq.getShopId());
		return success(shopRenewExpireTime);
	}
	
	
	/**
	 * 续费列表
	 * @param sReq
	 * @return
	 */
	@PostMapping("/system/shop/renew/queryList")
	public JsonResult shopRenewQueryList(@RequestBody ShopRenewListParam sReq) {
		ShopRecord checkShop = saas.shop.checkShop(sReq.getShopId(), sReq.getSysId());
		if(checkShop==null) {
			//店铺id或sysid错误
			return fail(JsonResultCode.CODE_FAIL);
		}
		PageResult<ShopRenewVo> shopRenewVoResult = saas.shop.renew.getShopRenewList(sReq);
		return success(shopRenewVoResult);
	}

	/**
	 * 店铺列表
	 * @param param
	 * @return
	 */
	@PostMapping(value = "/system/shop/list")
	public JsonResult shopList(@RequestBody ShopListQueryParam param) {
		return success(saas.shop.getPageList(param));
	}

	/**
	 * 版本权限查询
	 * 
	 * @return
	 */
	@PostMapping("/system/version/list")
	public JsonResult versionList(@RequestBody VersionListQueryParam vParam) {
		return success(saas.shop.version.getPageList(vParam));
	}

	/**
	 * 版本功能展示
	 * 
	 * @return
	 */
	@PostMapping("/system/version/show")
	public JsonResult showVersion(@RequestBody VersionShowParam vParam) {
		VersionConfig vConfig = saas.shop.version.mergeVersion(vParam.shopId);
		if (vConfig == null) {
			return fail(JsonResultCode.CODE_FAIL);
		}
		return success(vConfig);
	}
	

	/**
	 * 店铺列表--版本权限
	 * @param vParam
	 * @return
	 */
	@PostMapping("/system/version/edit")
	public JsonResult editVersion(@RequestBody VersionEditParam vParam) {
		if(saas.shop.version.editVersion(vParam)==1) {
			return success(JsonResultCode.CODE_SUCCESS);
		}
		return fail(JsonResultCode.CODE_FAIL);
	}
	
	/**
	 * 更改是否禁用
	 * @param vParam
	 * @return
	 */
	@PostMapping("/system/shop/upEnable")
	public JsonResult changeIsEnable(@RequestBody ShopEnableReq vParam) {
		log.info("更改禁用"+vParam.toString());
		if(!StringUtils.isEmpty(vParam.getIsEnable())) {
			Byte isEnable=null;
            String enable = "yes";
            if(enable.equals(vParam.getIsEnable())) {
				isEnable=1;
			}
            String disable = "no";
            if(disable.equals(vParam.getIsEnable())) {
				isEnable=0;
			}
			if(!StringUtils.isEmpty(isEnable)) {
				log.info("开始更新"+isEnable);
				saas.shop.updateRowIsEnable(vParam.getShopId(), isEnable);	
				return success(JsonResultCode.CODE_SUCCESS);
			}
		}
		return fail(JsonResultCode.CODE_FAIL);
	}
	
	/**
	 * 更改隐藏底部导航
	 * @param vParam
	 * @return
	 */
	@PostMapping("/system/shop/upBottom")
	public JsonResult changeHidBottom(@RequestBody ShopEnableReq vParam) {
		log.info("更改隐藏底部导航"+vParam.toString());
		if(!StringUtils.isEmpty(vParam.getHidBottom())) {
			Byte hidBottom=null;
            String hideBottomNav = "yes";
            if(hideBottomNav.equals(vParam.getHidBottom())) {
				hidBottom=1;
			}
            String noHideBottomNav = "no";
            if(noHideBottomNav.equals(vParam.getHidBottom())) {
				hidBottom=0;
			}
			if(!StringUtils.isEmpty(hidBottom)) {
				log.info("开始更新"+hidBottom);
				saas.shop.updateRowHidBottom(vParam.getShopId(), hidBottom);	
				return success(JsonResultCode.CODE_SUCCESS);
			}
		}
		return fail(JsonResultCode.CODE_FAIL);
	}
	
	
	/**
	 * 修改店铺之前查询
	 * @param account
	 * @return
	 */
	@GetMapping("/system/shop/editList/{shopId}")
	public JsonResult editListShopBefore(@PathVariable Integer shopId) {
		ShopRecord shopRecord = saas.shop.getShopById(shopId);
		if(shopRecord!=null) {
			Timestamp shopRenewExpireTime = saas.shop.renew.getShopRenewExpireTime(shopId);
			//ShopPojo.
			ShopPojo pojo = shopRecord.into(ShopPojo.class);
			pojo.setExpireTime(shopRenewExpireTime);
			return success(pojo);
		}
		//店铺不存在
		return fail(JsonResultCode.CODE_ACCOUNT_SHOP_NULL);
	}
	
	
	/**
	 * 修改店铺
	 * @param account
	 * @return
	 */
	@PostMapping("/system/shop/edit")
	public JsonResult editShop(@RequestBody @Valid ShopReq shopReq) {
		ShopRecord shopRecord = saas.shop.getShopById(shopReq.getShopId());
		if(shopRecord==null) {
			return fail(JsonResultCode.CODE_ACCOUNT_SHOP_NULL);
		}
		ShopAccountRecord accountInfo = saas.shop.account.getAccountInfoForId(shopReq.getSysId());
		if (accountInfo == null) {
			return fail(JsonResultCode.CODE_ACCOUNT_SHOP_NULL);
		}
		if (saas.shop.editShop(shopReq, sysAuth.user(), request,shopRecord)) {
			return success(JsonResultCode.CODE_SUCCESS);
		}
		return fail();
	}
	
	/**
	 * system版本权限
	 * @return
	 */
	@RequestMapping("/system/version/getList")
	public JsonResult getVersion() {
		return success(saas.shop.menu.getVersion());
	}
	
	/**
	 * 版本单个的版本的权限
	 * 
	 * @return
	 */
	@GetMapping("/system/version/getOne/{level}")
	public JsonResult getVersionOne(@PathVariable String level) {
		return success(saas.shop.version.getOneVersion(level));
	}

	@GetMapping("/system/shop/getList")
	public JsonResult getAllShopList(){
        return success(saas.shop.getShopListInfo());
    }
}
