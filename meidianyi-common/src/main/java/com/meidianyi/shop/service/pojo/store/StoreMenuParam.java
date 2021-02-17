package com.meidianyi.shop.service.pojo.store;

import lombok.Data;

import java.util.List;

/**
 * @author chenjie
 * @date 2020年08月25日
 */
@Data
public class StoreMenuParam {
    public String enName;
    public String name;
    public Byte check=1;
    public Byte isOnly=0;
    public String linkUrl;
    public Integer topIndex;
    public List<String> includeApi;
    public List<StoreMenuParam> sub;
}
