package com.meidianyi.shop.service.pojo.shop.overview.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author liufei
 * @date 2019/8/1
 * @description
  */
@Data
public class LabelAnalysisVo {
    /**  标签id */
    @JsonIgnore
    private Integer tagId;
    /**  标签 */
    private String tagName;
    /**  用户数 */
    private Integer userNum;
    /**  有手机号客户数 */
    private Integer userNumWithPhone;
    /**  付款笔数 */
    private Integer paidNum;
    /**  付款金额 */
    private BigDecimal paidMoney;
    /**  付款用户数 */
    private Integer paidUserNum;
    /**  付款商品数 */
    private Integer paidGoodsNum;
}
