package com.meidianyi.shop.service.pojo.shop.distribution;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelIgnore;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 返利商品统计出参
 * @author 常乐
 * 2020年6月26日 21:58
 */
@ExcelSheet
@Data
public class RebateGoodsExportVo {
	/**
	 * 商品id
	 */
    @ExcelIgnore
	private Integer goodsId;
    @ExcelIgnore
    private Integer sortId;
	/**
	 * 商品名称
	 */
    @ExcelColumn(columnName = JsonResultMessage.REBATE_GOODS_GOODS_NAME,columnIndex = 0)
	private String goodsName;
	/**
	 * 商品价格
	 */
    @ExcelColumn(columnName = JsonResultMessage.REBATE_GOODS_GOODS_PRICE,columnIndex = 1)
	private BigDecimal shopPrice;
	/**
	 * 所属分类
	 */
    @ExcelColumn(columnName = JsonResultMessage.REBATE_GOODS_GOODS_CATE,columnIndex = 2)
	private String catName;
	@ExcelIgnore
	private Integer catId;
	/**
	 * 商品总销量
	 */
    @ExcelColumn(columnName = JsonResultMessage.REBATE_GOODS_GOODS_SALE_NUM,columnIndex = 3)
	private Integer goodsSaleNum;
	/**
	 * 已返利总数量
	 */
    @ExcelColumn(columnName = JsonResultMessage.REBATE_GOODS_SALE_NUM,columnIndex = 4)
	private Integer saleNumber;
	/**
	 * 已返利总佣金
	 */
    @ExcelColumn(columnName = JsonResultMessage.REBATE_GOODS_TOTAL_FANLI,columnIndex = 5)
	private BigDecimal prdTotalFanli;
	/**
	 * 商品规格价
	 */
	@ExcelIgnore
	private BigDecimal prdPrice;
}
