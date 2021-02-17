package com.meidianyi.shop.service.shop.distribution;

import static com.meidianyi.shop.db.shop.Tables.PROMOTION_LANGUAGE;

import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.shop.tables.records.PromotionLanguageRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.distribution.PromotionLanguageAddParam;
import com.meidianyi.shop.service.pojo.shop.distribution.PromotionLanguageListParam;
import com.meidianyi.shop.service.pojo.shop.distribution.PromotionLanguageListVo;

/**
 * @author changle
 */
@Service
public class PromotionLanguageService extends ShopBaseService{
	/**
	 * 分销推广语列表
	 * @param param
	 * @return
	 */
	public PageResult<PromotionLanguageListVo> getPromotionLanguageList(PromotionLanguageListParam param) {
		SelectJoinStep<? extends Record> select = db().select(PROMOTION_LANGUAGE.ID,PROMOTION_LANGUAGE.TITLE,PROMOTION_LANGUAGE.PROMOTION_LANGUAGE_,
				PROMOTION_LANGUAGE.CREATE_TIME,PROMOTION_LANGUAGE.UPDATE_TIME,PROMOTION_LANGUAGE.IS_BLOCK,PROMOTION_LANGUAGE.DEL_FLAG)
				.from(PROMOTION_LANGUAGE);
		buildOptions(select,param);
		PageResult<PromotionLanguageListVo> pageList = this.getPageResult(select, param.getCurrentPage(), param.getPageRows(), PromotionLanguageListVo.class);
		return pageList;
	}
	
	/**
	 * 分销推广语条件查询
	 * @param select
	 * @param param
	 */
	public void buildOptions(SelectJoinStep<? extends Record> select,PromotionLanguageListParam param) {
		select.where(PROMOTION_LANGUAGE.DEL_FLAG.eq((byte)0));
		//推广内容
		if(param.getPromotionLanguage() != null) {
			select.where(PROMOTION_LANGUAGE.PROMOTION_LANGUAGE_.contains(param.getPromotionLanguage()));
		}
		//创建时间
		if(param.getStartCreateTime() != null ) {
			select.where(PROMOTION_LANGUAGE.CREATE_TIME.ge(param.getStartCreateTime()));
		}
		if(param.getEndCreateTime() != null){
            select.where(PROMOTION_LANGUAGE.CREATE_TIME.le(param.getEndCreateTime()));
        }
		//修改时间
		if(param.getStartUpdateTime() != null) {
			select.where(PROMOTION_LANGUAGE.UPDATE_TIME.ge(param.getStartUpdateTime()));
		}
		if(param.getEndUpdateTime() != null){
            select.where(PROMOTION_LANGUAGE.UPDATE_TIME.le(param.getEndUpdateTime()));
        }
		select.orderBy(PROMOTION_LANGUAGE.UPDATE_TIME.desc());
	}
	
	/**
	 * 添加分销推广语
	 * @param param
	 * @return
	 */
	public int addPromotionLanguage(PromotionLanguageAddParam param) {
		PromotionLanguageRecord record = new PromotionLanguageRecord();
		assign(param, record);
		return db().executeInsert(record);
	}
	
	/**
	 * 获取单条分销推广语信息
	 * @param id
	 * @return
	 */
	public PromotionLanguageListVo getOnePromotion(int id) {
		PromotionLanguageListVo result = db().select(PROMOTION_LANGUAGE.ID ,PROMOTION_LANGUAGE.TITLE,PROMOTION_LANGUAGE.PROMOTION_LANGUAGE_)
				.from(PROMOTION_LANGUAGE)
				.where(PROMOTION_LANGUAGE.ID.eq(id))
				.fetchOne().into(PromotionLanguageListVo.class);
		return result;
	}
	
	/**
	 * 分销推广语编辑保存
	 * @return
	 */
	public int savePromotionLanguage(PromotionLanguageAddParam param) {
		PromotionLanguageRecord record = new PromotionLanguageRecord();
		assign(param, record);
		return db().executeUpdate(record);
	}
	
	/**
	 * 删除分销推广语
	 * @param id
	 * @return
	 */
	public int delPromotionLanguage(int id) {
		int result = db().update(PROMOTION_LANGUAGE)
				.set(PROMOTION_LANGUAGE.DEL_FLAG, (byte) 1)
				.where(PROMOTION_LANGUAGE.ID.eq(id))
				.execute();
		return result;
	}
	
	/**
	 * 停用分销推广语
	 * @param id
	 * @return
	 */
	public int pausePromotionLanguage(int id) {
		int result = db().update(PROMOTION_LANGUAGE)
				.set(PROMOTION_LANGUAGE.IS_BLOCK, (byte) 1)
				.where(PROMOTION_LANGUAGE.ID.eq(id))
				.execute();
		return result;
	}
	
	/**
	 * 启用分销推广语
	 * @param id
	 * @return
	 */
	public int openPromotionLanguage(int id) {
		int result = db().update(PROMOTION_LANGUAGE)
				.set(PROMOTION_LANGUAGE.IS_BLOCK, (byte) 0)
				.where(PROMOTION_LANGUAGE.ID.eq(id))
				.execute();
		return result;
	}

}
