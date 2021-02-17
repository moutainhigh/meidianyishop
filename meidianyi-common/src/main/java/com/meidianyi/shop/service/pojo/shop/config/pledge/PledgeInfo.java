package com.meidianyi.shop.service.pojo.shop.config.pledge;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author luguangyao
 */
@Data
@NoArgsConstructor
public class PledgeInfo {
    private Integer   id;
    private String    pledgeName;
    private String    pledgeLogo;
    private String    pledgeContent;
    private Byte      state;
    private Byte      type;
    private Integer   level;

    private List<Integer> goodsIds = Lists.newArrayList();

    private List<Integer> sortIds = Lists.newArrayList();

    private List<Integer> goodsBrandIds = Lists.newArrayList();
}
