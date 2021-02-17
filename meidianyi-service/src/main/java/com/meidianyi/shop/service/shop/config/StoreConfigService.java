package com.meidianyi.shop.service.shop.config;

import com.meidianyi.shop.service.pojo.shop.config.store.StoreServiceConfig;
import org.springframework.stereotype.Service;

/**
 * @author 王兵兵
 *
 * 2019年7月10日
 */
@Service

public class StoreConfigService extends BaseShopConfigService{
	
	/**
	 * 门店服务评论配置：0不用审核    1先发后审   2先审后发
	 */
	final public static String K_SERVICE_COMMENT = "service_comment";
	
	/**
	 * 门店买单开关
	 */
	final public static String K_STORE_BUY = "store_buy";
	
	/**
	 * 门店职称配置，String类型
	 */
	final public static String K_TECHNICIAN_TITLE = "technician_title";
	
	/**
	 * 选择开启“扫码购”功能的门店ID列表，逗号分隔
	 */
	final public static String K_STORE_SCAN_IDS = "store_scan_ids";

    /**
     * 门店配送距离
     */
    final public static String K_STORE_DISTANCE = "store_distance";

    /**
     * 获取门店配送距离
     * @return Integer
     */
    public Double getStoreDistance() {
        return this.get(K_STORE_DISTANCE, Double.class, 100.00);
    }

    /**
     * 配置门店配送距离
     * @param storeDistance 门店配送距离
     * @return Integer
     */
    public int setStoreDistance(Double storeDistance) {
        assert storeDistance != null;
        return this.set(K_STORE_DISTANCE, storeDistance, Double.class);
    }
	

	/**
	 * 获取门店服务评论配置
	 * @return
	 */
	public Byte getServiceComment() {
		return this.get(K_SERVICE_COMMENT, Byte.class, (byte)0);
	}
	
	/**
	 * 设置门店服务评论配置
	 * @param value 0 或者 1或者2
	 * @return
	 */
	public int setServiceComment(Byte value) {
        assert (value == (byte) 0 || value == (byte) 1 || value == (byte) 2);
		return this.set(K_SERVICE_COMMENT, value,Byte.class);
	}
	
	/**
	 * 获取 门店买单开关
	 * @return
	 */
	public Byte getStoreBuy() {
		return this.get(K_STORE_BUY, Byte.class, (byte)0);
	}
	
	/**
	 * 设置 门店买单开关
	 * @param value 0 或者1
	 * @return
	 */
	public int setStoreBuy(Byte value) {
        assert (value == (byte) 0 || value == (byte) 1);
		return this.set(K_STORE_BUY, value,Byte.class);
	}
	
	/**
	 * 获取门店职称配置
	 * @return
	 */
	public String getTechnicianTitle() {
        return this.get(K_TECHNICIAN_TITLE, String.class, "技师");
	}
	
	/**
	 * 设置门店职称配置
	 * @param value 
	 * @return
	 */
	public int setTechnicianTitle(String value) {
        assert (!"".equals(value));
		return this.set(K_TECHNICIAN_TITLE, value);
	}
	
	/**
	 * 获取选择开启“扫码购”功能的门店ID列表，逗号分隔
	 * @return
	 */
	public String getStoreScanIds() {
		return this.get(K_STORE_SCAN_IDS, String.class, "");
	}
	
	/**
	 * 设置选择开启“扫码购”功能的门店ID列表，逗号分隔
	 * @param value
	 * @return
	 */
	public int setStoreScanIds(String value) {
        assert (!"".equals(value));
		return this.set(K_STORE_SCAN_IDS, value);
	}
	
	/**
	 * 获取门店服务的配置
	 * @return StoreServiceConfig
	 */
	public StoreServiceConfig getStoreServiceConfig() {
		StoreServiceConfig storeServiceConfig = new StoreServiceConfig();
		this.transaction(() ->{
			storeServiceConfig.setServiceComment(this.getServiceComment());
			storeServiceConfig.setStoreBuy(this.getStoreBuy());
			storeServiceConfig.setTechnicianTitle(this.getTechnicianTitle());
			storeServiceConfig.setStoreScanIds(this.getStoreScanIds());
			storeServiceConfig.setStoreDistance(this.getStoreDistance());
		});
		return storeServiceConfig;
	}
	
	public Boolean setStoreServiceConfig(StoreServiceConfig storeServiceConfig) {
		 this.transaction(() -> {
			this.setServiceComment(storeServiceConfig.getServiceComment());
	        this.setStoreBuy(storeServiceConfig.getStoreBuy());
	        this.setTechnicianTitle(storeServiceConfig.getTechnicianTitle());
	        this.setStoreScanIds(storeServiceConfig.getStoreScanIds());
	        this.setStoreDistance(storeServiceConfig.storeDistance);
		 });
		return true;
	}
	

}
