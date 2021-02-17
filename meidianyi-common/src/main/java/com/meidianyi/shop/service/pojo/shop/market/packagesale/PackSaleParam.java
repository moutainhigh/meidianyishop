package com.meidianyi.shop.service.pojo.shop.market.packagesale;

import com.meidianyi.shop.db.shop.tables.records.PackageSaleRecord;
import com.meidianyi.shop.service.pojo.shop.market.packagesale.PackSaleConstant.Status;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author huangronggang
 * @date 2019年8月12日
 */
@Data
@NoArgsConstructor
public class PackSaleParam {

	/** 活动ID */
	private Integer id;
	/** 活动名称 */
	@NotBlank
	private String packageName;
	/** 开始时间 */
	@NotNull
	private Timestamp startTime;
	/** 结束时间 */
	@NotNull
	private Timestamp endTime;
	/**活动类型**/
	private Byte packageType;
	/** 结算总价格 */
	private BigDecimal totalMoney;
	/**折扣比例**/
	private BigDecimal totalRatio;
	/** 商品组1 */
	@NotNull
	private GoodsGroup group1;
	/** 商品组2 */
	private GoodsGroup group2;
	/** 商品组3 */
	private GoodsGroup group3;
	
	@Setter
    @Getter
	public static class GoodsGroup{
        /**
         * 取值1或2或3
         */
		private Byte groupId;

		/** 商品组1名称 */
		@NotBlank
		private String groupName;
		/** 至少需要选择件数 */
		@NotNull
		private Integer goodsNumber;
		/** 商品ID列表 */
		private List<Integer> goodsIdList;
		/** 平台分类ID列表 */
		private List<Integer> catIdList;
		/** 商家分类ID列表 */
		private List<Integer> sortIdList;
	}
	public PackageSaleRecord convert2Record() {
		PackageSaleRecord record = new PackageSaleRecord();
		record.setId(this.getId());
		record.setPackageName(this.getPackageName());
		record.setStartTime(this.getStartTime());
		record.setEndTime(this.getEndTime());
		record.setPackageType(getPackageType());
		record.setTotalMoney(getTotalMoney());
		record.setTotalRatio(getTotalRatio());
		
		record.setGoodsGroup_1(Status.NORMAL);
		record.setGroupName_1(group1.getGroupName());
		record.setGoodsNumber_1(group1.getGoodsNumber());
		if(group1.getGoodsIdList() != null) {
			String goodsIds = StringUtils.join(group1.getGoodsIdList(), PackSaleConstant.ID_DELIMITER);
			record.setGoodsIds_1(goodsIds);
        }
        if (group1.getCatIdList() != null) {
            String catIds = StringUtils.join(group1.getCatIdList(), PackSaleConstant.ID_DELIMITER);
            record.setCatIds_1(catIds);
        }
        if (group1.getSortIdList() != null) {
            String sortIds = StringUtils.join(group1.getSortIdList(), PackSaleConstant.ID_DELIMITER);
            record.setSortIds_1(sortIds);
        }
        if (group2 == null) {
            record.setGoodsGroup_2(Status.DISABLED);
        } else {
            record.setGroupName_2(group2.getGroupName());
            record.setGoodsGroup_2(Status.NORMAL);
            record.setGoodsNumber_2(group2.getGoodsNumber());
            if (group2.getGoodsIdList() != null) {
                String goodsIds = StringUtils.join(group2.getGoodsIdList(), PackSaleConstant.ID_DELIMITER);
                record.setGoodsIds_2(goodsIds);
            }
            if (group2.getCatIdList() != null) {
                String catIds = StringUtils.join(group2.getCatIdList(), PackSaleConstant.ID_DELIMITER);
                record.setCatIds_2(catIds);
            }
            if (group2.getSortIdList() != null) {
                String sortIds = StringUtils.join(group2.getSortIdList(), PackSaleConstant.ID_DELIMITER);
                record.setSortIds_2(sortIds);
            }
        }

        if (group3 == null) {
            record.setGoodsGroup_3(Status.DISABLED);
        } else {
            record.setGoodsGroup_3(Status.NORMAL);
            record.setGroupName_3(group3.getGroupName());
            record.setGoodsNumber_3(group3.getGoodsNumber());
            if (group3.getGoodsIdList() != null) {
                String goodsIds = StringUtils.join(group3.getGoodsIdList(), PackSaleConstant.ID_DELIMITER);
                record.setGoodsIds_3(goodsIds);
            }
            if (group3.getCatIdList() != null) {
                String catIds = StringUtils.join(group3.getCatIdList(), PackSaleConstant.ID_DELIMITER);
                record.setCatIds_3(catIds);
            }
            if (group3.getSortIdList() != null) {
                String sortIds = StringUtils.join(group3.getSortIdList(), PackSaleConstant.ID_DELIMITER);
                record.setSortIds_3(sortIds);
            }
        }

        return record;
    }
}
