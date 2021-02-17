package com.meidianyi.shop.service.pojo.shop.market.form;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liufei
 * @date 2019/8/12
 * @description
 */
@Data
@NoArgsConstructor
public class FeedBackDetail {
    public String moduleName;
    public String moduleType;
    public String moduleValue;
    public String curIdx;
}
