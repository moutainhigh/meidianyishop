package com.meidianyi.shop.service.pojo.shop.store.account;

import java.sql.Timestamp;
import java.util.List;

import com.meidianyi.shop.common.pojo.main.table.StoreAccountDo;
import lombok.Data;

/**
 * @author zhaojianqiang
 */
@Data
public class StoreAccountVo {
    private Integer   accountId;
    private Integer   shopId;
    private Integer   sysId;
    private String    mobile;
    private String    accountName;
    private Timestamp createTime;
    private Byte      accountType;
    private Byte      status;
    private Byte      delFlag;
    private String    accountPasswd;
    private String    storeList;
    private Timestamp updateTime;
    private Integer   userId;
    private String    officialOpenId;
    private Byte      isBind;
    private Byte      isPharmacist;
    private String    signature;
	private Integer storeNum;
	private List<Integer> storeLists;
}
