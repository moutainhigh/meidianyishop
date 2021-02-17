package com.meidianyi.shop.service.shop.distribution;

import static com.meidianyi.shop.db.shop.Tables.FANLI_GOODS_STATISTICS;
import static com.meidianyi.shop.db.shop.Tables.GOODS;
import static com.meidianyi.shop.db.shop.Tables.GOODS_SPEC_PRODUCT;
import static com.meidianyi.shop.db.shop.Tables.ORDER_GOODS_REBATE;
import static com.meidianyi.shop.db.shop.Tables.ORDER_INFO;
import static com.meidianyi.shop.db.shop.Tables.ORDER_GOODS;
import static com.meidianyi.shop.db.shop.Tables.USER;

import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.excel.ExcelWriter;
import com.meidianyi.shop.dao.shop.distribution.OrderGoodsRebateDao;
import com.meidianyi.shop.db.shop.tables.records.SortRecord;
import com.meidianyi.shop.service.pojo.shop.distribution.*;
import com.meidianyi.shop.service.shop.goods.GoodsSortService;
import org.apache.poi.ss.usermodel.Workbook;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.saas.category.SysCatevo;

import java.util.List;

/**
 * 	商品返利统计
 * @author 常乐
 * 2019年8月10日
 */
@Service
public class RebateGoodsService extends ShopBaseService{
    @Autowired
    public GoodsSortService gs;

    @Autowired
    protected OrderGoodsRebateDao orderGoodsRebateDao;
	
	/** 查询别名 **/
    private static final String INVITE = "n";
    
	/**
	 * 商品返利统计列表
	 * @param param
	 * @return
	 */
	public PageResult<RebateGoodsVo> getRebateGoods(RebateGoodsParam param) {
		 SelectJoinStep<? extends Record> select = db().select(GOODS.GOODS_ID,FANLI_GOODS_STATISTICS.SALE_NUMBER,FANLI_GOODS_STATISTICS.PRD_TOTAL_FANLI,GOODS.GOODS_NAME,GOODS.GOODS_SALE_NUM,GOODS.SHOP_PRICE,GOODS_SPEC_PRODUCT.PRD_PRICE,GOODS.CAT_ID).from(FANLI_GOODS_STATISTICS
				.leftJoin(GOODS).on(FANLI_GOODS_STATISTICS.GOODS_ID.eq(GOODS.GOODS_ID)))
				 .leftJoin(GOODS_SPEC_PRODUCT).on(FANLI_GOODS_STATISTICS.PRD_ID.eq(GOODS_SPEC_PRODUCT.PRD_ID));
		optionBuild(select,param);
		PageResult<RebateGoodsVo> pageList = this.getPageResult(select, param.getCurrentPage(), param.getPageRows(), RebateGoodsVo.class);
		//获取商品对应分类名称
		for(RebateGoodsVo listInfo : pageList.dataList) {
            SortRecord sort = gs.getSortDao(listInfo.getSortId());
            if(sort!=null){
                listInfo.setCatName(sort.getSortName());
            }
		}
		return pageList;
	}
	
	/**
	 * 商品返利统计条件查询
	 * @param select
	 * @param param
	 */
	public void optionBuild(SelectJoinStep<? extends Record> select,RebateGoodsParam param) {
		//商品类型
		if(param.getGoodsSort() != null) {
			select.where(FANLI_GOODS_STATISTICS.CAT_ID.eq(param.getGoodsSort()));
		}
		//商品名称
		if(param.getGoodsName() != null) {
			select.where(GOODS.GOODS_NAME.contains(param.getGoodsName()));
		}
		select.orderBy(FANLI_GOODS_STATISTICS.PRD_TOTAL_FANLI.desc());
	}

    /**
     * 商品返利统计导出Excel
     * @param param
     * @param lang
     * @return
     */
    public Workbook exportRebateGoodsList(RebateGoodsParam param, String lang){
        SelectJoinStep<? extends Record> select = db().select(GOODS.GOODS_ID,FANLI_GOODS_STATISTICS.SALE_NUMBER,FANLI_GOODS_STATISTICS.PRD_TOTAL_FANLI,GOODS.GOODS_NAME,GOODS.GOODS_SALE_NUM,GOODS.SHOP_PRICE,GOODS_SPEC_PRODUCT.PRD_PRICE,GOODS.SORT_ID)
            .from(FANLI_GOODS_STATISTICS)
            .leftJoin(GOODS).on(FANLI_GOODS_STATISTICS.GOODS_ID.eq(GOODS.GOODS_ID))
            .leftJoin(GOODS_SPEC_PRODUCT).on(FANLI_GOODS_STATISTICS.PRD_ID.eq(GOODS_SPEC_PRODUCT.PRD_ID));
        optionBuild(select,param);

        select.limit(param.getStartNum(),param.getEndNum());
        List<RebateGoodsExportVo> rebateGoodsExportVo = select.fetchInto(RebateGoodsExportVo.class);
        //获取商品对应分类名称
        for(RebateGoodsExportVo listInfo : rebateGoodsExportVo) {
            SortRecord sort = gs.getSortDao(listInfo.getSortId());
            if(sort != null){
                listInfo.setCatName(sort.getSortName());
            }
        }
        Workbook workbook= ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(lang,workbook);
        excelWriter.writeModelList(rebateGoodsExportVo, RebateGoodsExportVo.class);
        return workbook;
    }
	
	/**
	 * 商品返利明细列表
	 * @param param
	 * @return
	 */
	public PageResult<RebateGoodsDetailVo> getRebateGoodsDetail(RebateGoodsDetailParam param) {
        return orderGoodsRebateDao.listRebateGoodsDetails(param);
	}


    /**
     * 商品返利明细导出Excel
     * @param param
     * @param lang
     * @return
     */
    public Workbook exportRebateGoodsDetail(RebateGoodsDetailParam param, String lang) {
        List<RebateGoodsDetailExportVo> rebateGoodsDetailExportVoList = orderGoodsRebateDao.listRebateGoodsExportDetails(param);

        Workbook workbook= ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(lang, workbook);
        excelWriter.writeModelList(rebateGoodsDetailExportVoList, RebateGoodsDetailExportVo.class);

        return workbook;
    }
}
