package com.meidianyi.shop.service.shop.version;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.saas.shop.version.VersionConfig;
import com.meidianyi.shop.service.shop.decoration.MpDecorationService;
import com.meidianyi.shop.service.shop.image.ImageService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author lixinguo
 *
 */
@Service

public class VersionService extends ShopBaseService {

	@Autowired protected ImageService image;
	@Autowired protected MpDecorationService mpDecoration;

	String mainConfig = "main_config";
	String numConfig = "num_config";
	String pictureNum = "picture_num";
	String decorateNum = "decorate_num";
	String videoNum = "video_num";
	String goodsNum = "goods_num";
	String storeNum = "store_num";
	String formNum = "form_num";

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class VersionQueryParam {
		public String configName;
		public String modName;
		public String subName;
	};

	/**
	 * 得到店铺等级对应的数量和权限
	 * 
	 * @param configName
	 * @param modName
	 * @param subName
	 * @return
	 */
	public Map<String, Object> getVersionDetail(VersionQueryParam param) {
		if (param.configName == null || param.modName == null) {
			return null;
		}

		VersionConfig config = saas().shop.version.mergeVersion(this.getShopId());
		if (config == null) {
			return null;
		}
		if (param.configName.equals(mainConfig)) {
			if (param.subName == null) {
				return null;
			}
			return saas().shop.version.mainJudgment(param.modName, param.subName, this.getShopId());
		} else if (param.configName.equals(numConfig)) {
			Map<String, Object> versionNumberMap = saas().shop.version.versionNumShow(param.modName, this.getShopId());
			return versionNumberMap;
		}
		return null;

	}

	public Map<String, Object> getPictureNumConfig() {
		return this.getVersionDetail(new VersionQueryParam(numConfig, pictureNum, null));
	}

	public Map<String, Object> getDecorateNumConfig() {
		return this.getVersionDetail(new VersionQueryParam(numConfig, decorateNum, null));
	}

	public Map<String, Object> getVideoNumConfig() {
		return this.getVersionDetail(new VersionQueryParam(numConfig, videoNum, null));
	}

	public Map<String, Object> getGoodsNumConfig() {
		return this.getVersionDetail(new VersionQueryParam(numConfig, goodsNum, null));
	}

	public Map<String, Object> getStoreNumConfig() {
		return this.getVersionDetail(new VersionQueryParam(numConfig, storeNum, null));
	}

	public Map<String, Object> getFormNumConfig() {
		return this.getVersionDetail(new VersionQueryParam(numConfig, formNum, null));
	}
	
	/**
	 * 获取模块对应权限大小
	 * @param name  VersionNumConfig
	 * @return
	 */
	public Integer getLimitNum(String name) {
		VersionConfig config = saas().shop.version.mergeVersion(this.getShopId());
		if (config == null) {
			return null;
		}
		Integer number = saas().shop.version.getConfigNumber(config, name);
		return number;
	}

}
