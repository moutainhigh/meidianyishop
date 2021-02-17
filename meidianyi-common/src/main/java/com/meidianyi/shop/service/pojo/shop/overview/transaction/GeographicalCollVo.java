package com.meidianyi.shop.service.pojo.shop.overview.transaction;

import lombok.Data;

import java.util.List;

/**
 * @author liufei
 * @date 2019/7/31
 * @description
 */
@Data
public class GeographicalCollVo {
    private List<GeographicalVo> vos;
    private double maxTotalDealMoney;
    private double minTotalDealMoney;
}
