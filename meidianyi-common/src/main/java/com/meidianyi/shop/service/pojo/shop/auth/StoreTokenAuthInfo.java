package com.meidianyi.shop.service.pojo.shop.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author chenjie
 * @date 2020年08月20日
 */
@Data
@NoArgsConstructor
public class StoreTokenAuthInfo {
    public Integer sysId = 0;
    public String userName = "";
    public Integer subAccountId = 0;
    public String subUserName = "";
    public Integer loginShopId = 0;
    public String token = "";
    public String accountName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String currency;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String shopLanguage;

    public Integer storeAccountId=0;
    /**
     * 门店账号类型：1店员，2店长
     */
    public Byte storeAccountType=2;
    /**
     * 门店id：店员时某一个门店id；店长时是所属账号有权限门店全部id，逗号隔开
     */
    public List<Integer> storeIds;
    public String storeAccountName;
    /**
     * 操作账户类型（操作记录）
     */
    public Byte accountType;
    public StoreAuthInfoVo storeAuthInfoVo;
    /**
     * 小程序信息
     */
    public String qrcodeUrl;
    public String nickName;
}
