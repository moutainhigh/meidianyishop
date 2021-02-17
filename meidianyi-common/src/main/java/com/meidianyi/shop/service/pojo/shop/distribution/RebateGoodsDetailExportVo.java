package com.meidianyi.shop.service.pojo.shop.distribution;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

import static com.meidianyi.shop.common.foundation.data.DistributionConstant.*;

/**
 * @author panjing
 * @date 2020/7/14 15:41
 */
@ExcelSheet
@Data
public class RebateGoodsDetailExportVo {
    /**
     * 商品名称
     */
    @JsonProperty
    @ExcelColumn(columnName = JsonResultMessage.REBATE_GOODS_DETAIL_GOODS_NAME, columnIndex = 0)
    private String goodsName;
    /**
     * 商品数量
     */
    @ExcelColumn(columnName = JsonResultMessage.REBATE_GOODS_DETAIL_GOODS_NUMBER, columnIndex = 1)
    private Integer goodsNumber;
    /**
     * 返利订单号
     */
    @ExcelColumn(columnName = JsonResultMessage.REBATE_GOODS_DETAIL_ORDER_SN, columnIndex = 2)
    private String orderSn;
    /**
     * 下单用户手机号
     */
    @ExcelColumn(columnName = JsonResultMessage.REBATE_GOODS_DETAIL_MOBILE, columnIndex = 3)
    private String mobile;
    /**
     * 下单用户昵称
     */
    @ExcelColumn(columnName = JsonResultMessage.REBATE_GOODS_DETAIL_USERNAME, columnIndex = 4)
    private String username;
    /**
     * 分销员手机号
     */
    @ExcelColumn(columnName = JsonResultMessage.REBATE_GOODS_DETAIL_DISTRIBUTOR_MOBILE, columnIndex = 5)
    private String distributorMobile;
    /**
     * 分销员昵称
     */
    @ExcelColumn(columnName = JsonResultMessage.REBATE_GOODS_DETAIL_DISTRIBUTOR_NAME, columnIndex = 6)
    private String distributorName;
    /**
     * 返利比例
     */
    @ExcelColumn(columnName = JsonResultMessage.REBATE_GOODS_DETAIL_REBATE_PERCENT, columnIndex = 7)
    private Integer rebatePercent;
    /**
     * 返利状态
     */
    @ExcelColumn(columnName = JsonResultMessage.REBATE_GOODS_DETAIL_REBATE_STATUS, columnIndex = 8)
    private String settlementFlag;
    /**
     * 返利日期
     */
    @ExcelColumn(columnName = JsonResultMessage.REBATE_GOODS_DETAIL_FINISHED_TIME, columnIndex = 9)
    private Timestamp finishedTime;
    /**
     * 返利关系：0 自购返利 1 直接返利 2 二级返利
     */
    @ExcelColumn(columnName = JsonResultMessage.REBATE_GOODS_DETAIL_REBATE_LEVEL, columnIndex = 10)
    private String rebateLevel;
    /**
     * 分销员真实姓名
     */
    @ExcelColumn(columnName = JsonResultMessage.REBATE_GOODS_DETAIL_DISTRIBUTOR_REAL_NAME, columnIndex = 11)
    private String distributorRealName;
    /**
     * 商品返利佣金金额
     */
    @ExcelColumn(columnName = JsonResultMessage.REBATE_GOODS_DETAIL_REAL_REBATE_MONEY, columnIndex = 12)
    private BigDecimal realRebateMoney;
    /**
     * 返利商品金额
     */
    @ExcelColumn(columnName = JsonResultMessage.REBATE_GOODS_DETAIL_CAN_CALCULATE_MONEY, columnIndex = 13)
    private BigDecimal canCalculateMoney;

    public void setRebateLevel(String rebateLevel) {
        if (REBATE_TYPE_SELF.equals(this.rebateLevel)) {
            this.rebateLevel = "自购返利";
        } else if (REBATE_TYPE_DIRECT.equals(this.rebateLevel)) {
            this.rebateLevel = "直接返利";
        } else if (REBATE_TYPE_INDIRECT.equals(this.rebateLevel)) {
            this.rebateLevel = "间接返利";
        }
    }

    public void setSettlementFlag(String settlementFlag) {
        if (WAIT_SETTLEMENT_FLAG.equals(this.settlementFlag)) {
            this.settlementFlag = "待返利";
        } else if (HAS_SETTLEMENT_FLAG.equals(this.settlementFlag)) {
            this.settlementFlag = "已返利";
        } else if (NO_SETTLEMENT_FLAG.equals(this.settlementFlag)) {
            this.settlementFlag = "不返利";
        }
    }

}
