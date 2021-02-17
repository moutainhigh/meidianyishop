package com.meidianyi.shop.service.pojo.shop.market.groupbuy.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 孔德成
 * @date 2019/8/9 15:16
 */
@Getter
@Setter
public class GroupBuyAnalysisVo {

    List<BigDecimal> marketPriceList = new ArrayList<>();
    List<BigDecimal> goodsPriceList = new ArrayList<>();
    List<BigDecimal> ratioList = new ArrayList<>();
    List<Integer> oldUserList = new ArrayList<>();
    List<Integer> newUserList = new ArrayList<>();
    List<String> dateList = new ArrayList<>();;

    private BigDecimal totalPrice =BigDecimal.ZERO;
    private BigDecimal totalMarketPrice =BigDecimal.ZERO;
    private BigDecimal totalRatio =BigDecimal.ZERO;
    private Integer totalOldUser =0;
    private Integer totalNewUser =0;


}
