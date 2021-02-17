package com.meidianyi.shop.service.saas.shop;

import static com.meidianyi.shop.db.main.tables.ShopVersion.SHOP_VERSION;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectWhereStep;
import org.jooq.impl.DSL;
import org.jooq.tools.StringUtils;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.db.main.tables.records.ShopVersionRecord;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.saas.shop.ShopVersionPojo;
import com.meidianyi.shop.service.pojo.saas.shop.ShopVersionVo;
import com.meidianyi.shop.service.pojo.saas.shop.VersionEditParam;
import com.meidianyi.shop.service.pojo.saas.shop.VersionListQueryParam;
import com.meidianyi.shop.service.pojo.saas.shop.version.VersionConfig;
import com.meidianyi.shop.service.pojo.saas.shop.version.VersionMainConfig;
import com.meidianyi.shop.service.pojo.saas.shop.version.VersionNumberConfig;

/**
 * 
 * @author 新国
 *
 */
@Service

public class ShopVersionService extends MainBaseService {

	public PageResult<ShopVersionPojo> getPageList(VersionListQueryParam param) {
		SelectWhereStep<Record> select = db().select().from(SHOP_VERSION);
		if (!StringUtils.isEmpty(param.versionName)) {
			select.where(SHOP_VERSION.VERSION_NAME.like(this.likeValue(param.versionName)));
		}
		select.where(SHOP_VERSION.FLAG.eq((byte) 0));
		select.orderBy(SHOP_VERSION.ID.desc());
		return this.getPageResult(select, param.currentPage, param.pageRows, ShopVersionPojo.class);
	}

	public Result<ShopVersionRecord> getAllVersion() {
		return db().selectFrom(SHOP_VERSION)
				.where(SHOP_VERSION.FLAG.eq((byte) 0))
				.orderBy(SHOP_VERSION.LEVEL.desc())
				.fetch();
	}

	public Map<String, String> getVersionMap() {
		Map<String, String> result = new HashMap<String, String>(10);
		for (ShopVersionRecord record : getAllVersion()) {
			result.put(record.getLevel(), record.getVersionName());
		}
		return result;
	}

	public ShopVersionRecord getVersionByLevel(String level) {
		return db().selectFrom(SHOP_VERSION).where(SHOP_VERSION.LEVEL.eq(level)).fetchAny();
	}

	public String getVersionNameByLevel(String level) {
		return db().selectFrom(SHOP_VERSION).where(SHOP_VERSION.LEVEL.eq(level)).fetchAny()
				.get(SHOP_VERSION.VERSION_NAME);
	}

	/**
	 * 得到店铺对应的版本配置
	 * 
	 * @param shopId
	 * @return
	 */
	public VersionConfig mergeVersion(Integer shopId) {
		ShopRecord shop = saas().shop.getShopById(shopId);
		if (shop == null || StringUtils.isEmpty(shop.getShopType())) {
			return null;
		}
		String json = shop.getVersionConfig();
		VersionConfig versionConfig = StringUtils.isBlank(json) ? null : Util.parseJson(json, VersionConfig.class);
		if (versionConfig == null) {
			versionConfig = new VersionConfig();
		}

		ShopVersionRecord versionRecord = getVersionByLevel(shop.getShopType());
		if (versionRecord == null) {
			return null;
		}

		json = versionRecord.getContent();
		VersionConfig levelVersionConfig = StringUtils.isBlank(json) ? null : Util.parseJson(json, VersionConfig.class);
		if (levelVersionConfig == null) {
			return null;
		}
		Util.mergeList(versionConfig.mainConfig.sub0, levelVersionConfig.mainConfig.sub0);
		Util.mergeList(versionConfig.mainConfig.sub1, levelVersionConfig.mainConfig.sub1);
		Util.mergeList(versionConfig.mainConfig.sub2, levelVersionConfig.mainConfig.sub2);
		Util.mergeList(versionConfig.mainConfig.sub3, levelVersionConfig.mainConfig.sub3);
		Util.mergeList(versionConfig.mainConfig.sub4, levelVersionConfig.mainConfig.sub4);
		Util.mergeList(versionConfig.mainConfig.sub5, levelVersionConfig.mainConfig.sub5);

		VersionNumberConfig num1 = versionConfig.numConfig;
		VersionNumberConfig num2 = levelVersionConfig.numConfig;
		// 下面的值
		// 装修页面数量
		num1.decorateNum = (num1.decorateNum == -1 && num2.decorateNum == -1) ? -1
				: num2.decorateNum + num1.decorateNumPlus;
		// 表单数量
		num1.formNum = (num1.formNum == -1 && num2.formNum == -1) ? -1 : num2.formNum + num1.formNumPlus;
		// 商品数量
		num1.goodsNum = (num1.goodsNum == -1 && num2.goodsNum == -1) ? -1 : num2.goodsNum + num1.goodsNumPlus;
		// 图片空间大小
		num1.pictureNum = (num1.pictureNum == -1 && num2.pictureNum == -1) ? -1 : num2.pictureNum + num1.pictureNumPlus;
		// 门店数量
		num1.storeNum = (num1.storeNum == -1 && num2.storeNum == -1) ? -1 : num2.storeNum + num1.storeNumPlus;
		// 视频空间大小
		num1.videoNum = (num1.videoNum == -1 && num2.videoNum == -1) ? -1 : num2.videoNum + num1.videoNumPlus;

		versionConfig.setShopType(shop.getShopType());
		versionConfig.setVersionName(versionRecord.getVersionName());
		return versionConfig;
	}

	/**
	 * 获取所有未删除的level
	 * 
	 * @return
	 */
	public List<String> getAllLevel() {
		Byte normalFlag = 0;
		return db().select().from(SHOP_VERSION).where(SHOP_VERSION.FLAG.eq(normalFlag)).fetch(SHOP_VERSION.LEVEL);
	}

	/**
	 * 删除版本
	 * 
	 * @param id
	 * @return
	 */
	public int delVersion(Integer id) {
		Byte delFlag = 1;
		return db().update(SHOP_VERSION)
				.set(SHOP_VERSION.FLAG, delFlag)
				.set(SHOP_VERSION.LEVEL, DSL.concat("del_" + id, SHOP_VERSION.LEVEL))
				.where(SHOP_VERSION.ID.eq(id)).execute();
	}

	/**
	 * 功能判断
	 * 
	 * @param modName
	 * @param subName
	 * @param shopId
	 * @return
	 */
	public Map<String, Object> mainJudgment(String modName, String subName, Integer shopId) {
		ShopRecord shop = saas().shop.getShopById(shopId);
		VersionConfig config = this.mergeVersion(shopId);
		VersionMainConfig m = config.mainConfig;
		String[] subNames = { "sub_0", "sub_1", "sub_2", "sub_3", "sub_4", "sub_5" };
		Object[] subObjects = { m.sub0, m.sub1, m.sub2, m.sub3, m.sub4, m.sub5 };
		int index = Arrays.asList(subNames).indexOf(subName);
		Integer content = -1;
		if (index != -1 && Arrays.asList(subObjects[index]).indexOf(modName) != -1) {
			content = 1;
		}
		Map<String, Object> result = new HashMap<String, Object>(0);
		Map<String, Object> message = new HashMap<String, Object>(0);
		result.put("content", content);
		result.put("version_name", getVersionByLevel(shop.getShopType()).getVersionName());
		result.put("version_id", shop.getShopType());
		result.put("expire_time", saas().shop.renew.getShopRenewExpireTime(shopId));
		message.put("message", result);
		return message;
	}

	/**
	 * 所有版本数量控制展示
	 * 
	 * @param modName
	 * @param shopId
	 * @return
	 */
	public Map<String, Object> versionNumShow(String modName, Integer shopId) {
		ShopRecord shop = saas().shop.getShopById(shopId);
		VersionConfig config = this.mergeVersion(shopId);

		ShopVersionRecord version = this.getVersionByLevel(shop.getShopType());

		Map<String, Object> shopMap = new HashMap<String, Object>(10);
		shopMap.put("version_name", version.getVersionName());
		shopMap.put("num", getConfigNumber(config, modName));

		List<Map<String, Object>> allVersions = this.getAllVersion().intoMaps();
		for (Map<String, Object> map : allVersions) {
			VersionConfig cfg = this.getVersionConfig((String) map.get("level"));
			map.put("num", getConfigNumber(cfg, modName));
		}

		Map<String, Object> result = new HashMap<String, Object>(2);
		result.put("self", shopMap);
		result.put("all", allVersions);

		return result;
	}

	/**
	 * 根据等级获取版本配置
	 * 
	 * @param level
	 * @return
	 */
	protected VersionConfig getVersionConfig(String level) {
		ShopVersionRecord version = this.getVersionByLevel(level);
		return Util.parseJson(version.getContent(), VersionConfig.class);
	}

	/**
	 * 得到版本配置获取对应模块的数量
	 * 
	 * @param config
	 * @param modName
	 * @return
	 */
	public Integer getConfigNumber(VersionConfig config, String modName) {
		VersionNumberConfig m = config.numConfig;
		String[] modNames = { "picture_num", "video_num", "goods_num", "store_num", "decorate_num", "form_num",
				"picture_num_plus", "video_num_plus", "goods_num_plus", "store_num_plus", "decorate_num_plus",
				"form_num_plus",
		};
		Object[] modObjects = { m.pictureNum, m.videoNum, m.goodsNum, m.storeNum, m.decorateNum, m.formNum,
				m.pictureNumPlus, m.videoNumPlus, m.goodsNumPlus, m.storeNumPlus, m.decorateNumPlus, m.formNumPlus };
		int index = Arrays.asList(modNames).indexOf(modName);
		Integer number = -1;
		if (index != -1) {
			number = (Integer) modObjects[index];
		}
		return number;
	}

	public Integer editVersion(VersionEditParam vParam) {
		ShopRecord shop = saas().shop.getShopById(vParam.getShopId());
		if (shop == null || StringUtils.isEmpty(shop.getShopType())) {
			return null;
		}

		// TODO 判断传入的字符是不是符合要求
		VersionMainConfig mainConfig = vParam.getMainConfig();
		VersionNumberConfig numConfig = vParam.getNumConfig();

		VersionConfig versionConfig = new VersionConfig();
		versionConfig.setMainConfig(mainConfig);
		versionConfig.setNumConfig(numConfig);

		ShopRecord record = new ShopRecord();
		record.setShopId(vParam.getShopId());
		record.setVersionConfig(Util.toJson(versionConfig));
		return db().executeUpdate(record);
	}

	/**
	 * 校验是否在version
	 * @param mainConfig
	 * @param enName
	 * @return
	 */
	public Boolean checkMainConfig(VersionMainConfig mainConfig, String enName) {
		Class<?> clazz = mainConfig.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			try {
				PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
				// 获得读方法
				Method rm = pd.getReadMethod();
				@SuppressWarnings("unchecked")
				List<String> list = (List<String>) rm.invoke(mainConfig);
				for (String string : list) {
					if (enName.equals(string)) {
						return true;
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return false;
	}

	/**
	 * 校验是否需要判断权限。
	 * 为true表示客户有权限，为false表示没有权限，此时要返回JsonResultCode.CODE_ACCOUNT_VERSIN_NO_POWER;
	 * saas.shop.version.verifyVerPurview
	 * VersionName 版本vsName对应的类
	 * @param shopId
	 * @param vsNames
	 * @return
	 */
	public String[] verifyVerPurview(Integer shopId, String... vsNames) {
		String[] result = new String[vsNames.length];
		for(int i=0;i<result.length;i++) {
			result[i]="false";
		}
		VersionConfig vConfig = saas().shop.version.mergeVersion(shopId);
		if (vConfig == null) {
			// 版本存在问题
			result[0] = "erro";
			return result;
		}
		VersionMainConfig mainConfig = vConfig.getMainConfig();

		Class<?> clazz = mainConfig.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			try {
				PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
				// 获得读方法
				Method rm = pd.getReadMethod();
				@SuppressWarnings("unchecked")
				List<String> list = (List<String>) rm.invoke(mainConfig);
				// for (String string : list) {
				for (int i = 0; i < vsNames.length; i++) {
					for (String string : list) {
						if (vsNames[i].equals(string)) {
							result[i] = "true";
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return result;
	}
	
	public ShopVersionVo getOneVersion(String level) {
		// ShopVersionVo
		ShopVersionRecord record = db().selectFrom(SHOP_VERSION).where(SHOP_VERSION.LEVEL.eq(level)).fetchAny();
		VersionConfig versionConfig = getVersionConfig(record.getLevel());
		ShopVersionVo vo = new ShopVersionVo();
		vo.setId(record.getId());
		vo.setVersionName(record.getVersionName());
		vo.setCreated(record.getCreated());
		vo.setContent(versionConfig);
		vo.setUpdateTime(record.getUpdateTime());
		vo.setDesc(record.getDesc());
		vo.setFlag(record.getFlag());
		vo.setLevel(record.getLevel());
		return vo;
	}

}
