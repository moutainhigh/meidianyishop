package com.meidianyi.shop.service.pojo.shop.store.store;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 王兵兵
 * <p>
 * 2019年7月4日
 */
@Data
@NoArgsConstructor
public class StorePageListVo {

    public Integer storeId;
    public String storeName;
    public String storeCode;
    /**
     * 门店编码（对接pos之后的posId）
     */
    public Integer posShopId;
    public String groupName;
    public String provinceCode;
    public String cityCode;
    public String districtCode;
    public String address;
    public String manager;
    public String mobile;
    public String openingTime;
    public String closeTime;

    private Byte storeType;
    /**
     * 营业状态1:营业，0:关店
     */
    public Byte businessState;
    /**
     * The Auto pick.是否自提设置，0否，1是
     */
    public Byte autoPick;
    /**
     * 是否支持同城配送 0否 1支持
     */
    public Byte cityService;
    /**
     * The Business type.营业时间1：每天，0：工作日
     */
    public Byte businessType;
    private Byte storeExpress;
}
