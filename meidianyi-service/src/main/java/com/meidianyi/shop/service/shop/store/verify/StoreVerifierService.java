package com.meidianyi.shop.service.shop.store.verify;

import static com.meidianyi.shop.db.shop.tables.OrderVerifier.ORDER_VERIFIER;
import static com.meidianyi.shop.db.shop.tables.User.USER;

import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectWhereStep;
import org.jooq.tools.StringUtils;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.excel.ExcelWriter;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.shop.tables.records.OrderVerifierRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.store.verifier.VerifierAddParam;
import com.meidianyi.shop.service.pojo.shop.store.verifier.VerifierExportVo;
import com.meidianyi.shop.service.pojo.shop.store.verifier.VerifierListQueryParam;
import com.meidianyi.shop.service.pojo.shop.store.verifier.VerifierListVo;
import com.meidianyi.shop.service.pojo.shop.store.verifier.VerifierSimpleParam;

/**
 * @author 王兵兵
 *
 * 2019年7月11日
 */
@Service
public class StoreVerifierService extends ShopBaseService{
	/**
	 *	 门店核销员列表分页查询
	 * @param param
	 * @return VerifierListVo
	 */
	public PageResult<VerifierListVo> getPageList(VerifierListQueryParam param) {
		SelectWhereStep<? extends Record> select = db().select(ORDER_VERIFIER.USER_ID,USER.USERNAME,USER.MOBILE,ORDER_VERIFIER.VERIFY_ORDERS)
				.from(ORDER_VERIFIER)
				.leftJoin(USER).on(ORDER_VERIFIER.USER_ID.eq(USER.USER_ID));
		select = this.buildOptions(select, param);
		select.where(ORDER_VERIFIER.DEL_FLAG.eq(DelFlag.NORMAL.getCode())).orderBy(ORDER_VERIFIER.CREATE_TIME);
		return getPageResult(select,param.getCurrentPage(),param.getPageRows(),VerifierListVo.class);
	}
	
	/**
	 *	 查询条件构建
	 * @param select
	 * @param param
	 * @return
	 */
	public SelectWhereStep<? extends Record> buildOptions(SelectWhereStep<? extends  Record> select, VerifierListQueryParam param) {
		select.where(ORDER_VERIFIER.STORE_ID.eq(param.getStoreId()));
		if (!StringUtils.isEmpty(param.getMobile())) {
			select.where(USER.MOBILE.contains(param.getMobile()));
		}
		if (!StringUtils.isEmpty(param.getUsername())) {
			select.where(USER.USERNAME.contains(param.getUsername()));
		}
		return select;
	}
	
	/**
	 * 添加核销员
	 * @param param
	 * @return
	 */
	public void addVerifiers(VerifierAddParam param) {
        this.transaction(()->{
            for(int userId : param.getUserIds()){
                OrderVerifierRecord record = db().newRecord(ORDER_VERIFIER);
                record.setStoreId(param.getStoreId());
                record.setUserId(userId);
                record.setDelFlag(DelFlag.NORMAL_VALUE);
                record.store();
            }
        });
	}

    /**
     * 删除核销员
     * @param verifier
     * @return
     */
    public void delStoreVerifier(VerifierSimpleParam verifier) {
        db().update(ORDER_VERIFIER).set(ORDER_VERIFIER.DEL_FLAG,DelFlag.DISABLE_VALUE).where(ORDER_VERIFIER.STORE_ID.eq(verifier.getStoreId()).and(ORDER_VERIFIER.USER_ID.eq(verifier.getUserId()))).execute();
    }

    /**
     *	 门店核销员列表导出
     * @param param
     * @return VerifierListVo
     */
    public Workbook exportStoreVerifierList(VerifierListQueryParam param, String lang) {
        SelectWhereStep<? extends Record> select = db().select(ORDER_VERIFIER.USER_ID,USER.USERNAME,USER.MOBILE,ORDER_VERIFIER.VERIFY_ORDERS)
            .from(ORDER_VERIFIER)
            .leftJoin(USER).on(ORDER_VERIFIER.USER_ID.eq(USER.USER_ID));
        select = this.buildOptions(select, param);
        select.where(ORDER_VERIFIER.DEL_FLAG.eq(DelFlag.NORMAL.getCode())).orderBy(ORDER_VERIFIER.CREATE_TIME);
        List<VerifierExportVo> storeVerifierList =  select.fetchInto(VerifierExportVo.class);

        Workbook workbook= ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(lang,workbook);
        excelWriter.writeModelList(storeVerifierList, VerifierExportVo.class);
        return workbook;
    }
	
		public Result<OrderVerifierRecord> getUserVerifyStores(Integer userId) {
		return db().selectFrom(ORDER_VERIFIER)
				.where(ORDER_VERIFIER.USER_ID.eq(userId).and(ORDER_VERIFIER.DEL_FLAG.eq((byte) 0))).fetch();
	}
	
}
