package com.meidianyi.shop.service.shop.distribution;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.DistributionStrategyRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributionStrategyParam;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributionStrategyVo;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

import static com.meidianyi.shop.db.shop.Tables.DISTRIBUTION_STRATEGY;

/**
 *返利策略配置
 * @author 常乐
 * 2019年7月18日
 */
@Service

public class RebateStrategyService extends ShopBaseService{

    public static final Byte STATUS_NORMAL = 0;
    public static final Byte STATUS_DISABLED = 1;
	
	/**
	 * 添加返利策略
	 * @param info
	 * @return
	 */
	public boolean setRebateStrategy(DistributionStrategyParam info) {
		DistributionStrategyRecord record = new DistributionStrategyRecord();
		this.assign(info,record);
		return db().executeInsert(record) > 0 ? true : false;
	}
	
	/**
	 * 返利策略分页列表
	 * @return
	 */
	public PageResult<DistributionStrategyVo> getStrategyList(DistributionStrategyParam param) {
		SelectJoinStep<? extends Record> select = db()
				.select(DISTRIBUTION_STRATEGY.ID,DISTRIBUTION_STRATEGY.STRATEGY_NAME,DISTRIBUTION_STRATEGY.START_TIME,DISTRIBUTION_STRATEGY.END_TIME,DISTRIBUTION_STRATEGY.FANLI_RATIO,DISTRIBUTION_STRATEGY.STRATEGY_LEVEL,DISTRIBUTION_STRATEGY.CREATE_TIME,DISTRIBUTION_STRATEGY.STATUS,DISTRIBUTION_STRATEGY.DEL_FLAG)
				.from(DISTRIBUTION_STRATEGY);
		SelectConditionStep<? extends Record> sql = buildOptions(select,param);
		PageResult<DistributionStrategyVo> list = this.getPageResult(sql, param.getCurrentPage(), param.getPageRows(),DistributionStrategyVo.class);
        list.dataList.forEach(vo -> {
            vo.setCurrentState(Util.getActStatus(vo.getStatus(),vo.getStartTime(),vo.getEndTime()));
        });
		return list;
	}
	
	/**
	 * 返利策略列表按状态查询
	 * @param select
	 * @param param
	 * @return
	 */
	public SelectConditionStep<? extends Record> buildOptions(SelectJoinStep<? extends Record> select,DistributionStrategyParam param) {
		SelectConditionStep<? extends Record> sql = select.where(DISTRIBUTION_STRATEGY.DEL_FLAG.eq(DelFlag.NORMAL_VALUE));
		
		Timestamp nowDate = new Timestamp(System.currentTimeMillis());
		
		if(param.getNav() != null) {
			switch(param.getNav()) {
				//进行中
				case 1:
					sql = sql.and(DISTRIBUTION_STRATEGY.START_TIME.le(nowDate))
							  .and(DISTRIBUTION_STRATEGY.END_TIME.ge(nowDate))
							  .and(DISTRIBUTION_STRATEGY.STATUS.eq(STATUS_NORMAL));
					break;
				//未开始
				case 2:
					sql = sql.and(DISTRIBUTION_STRATEGY.START_TIME.ge(nowDate))
							.and(DISTRIBUTION_STRATEGY.STATUS.eq(STATUS_NORMAL));
					break;
				//已过期
				case 3:
					sql = sql.and(DISTRIBUTION_STRATEGY.END_TIME.le(nowDate))
							.and(DISTRIBUTION_STRATEGY.STATUS.eq(STATUS_NORMAL));
					break;
				//已停用
				case 4:
					sql = sql.and(DISTRIBUTION_STRATEGY.STATUS.eq(STATUS_DISABLED));
					break;
                default:
                    break;
            }
		}
		return sql;
	}
	
	/**
	 * 编辑返利策略
	 * @param id
	 * @return
	 */
	public List<DistributionStrategyParam> getOneInfo(Integer id) {
		List<DistributionStrategyParam> info = db().select().from(DISTRIBUTION_STRATEGY)
				.where(DISTRIBUTION_STRATEGY.ID.eq(id))
				.fetch().into(DistributionStrategyParam.class);
		return info;
	}
	
	/**
	 * 返利策略编辑保存
	 * @param param
	 * @return
	 */
	public boolean saveRebateStrategy(DistributionStrategyParam param) {
		DistributionStrategyRecord record = new DistributionStrategyRecord();
		this.assign(param,record);
		return db().executeUpdate(record) > 0 ? true : false;
	}
	
	/**
	 * 返利策略停用
	 * @param id
	 * @return
	 */
	public boolean pauseRebate(Integer id) {
		int res = db().update(DISTRIBUTION_STRATEGY)
				.set(DISTRIBUTION_STRATEGY.STATUS,STATUS_DISABLED)
				.where(DISTRIBUTION_STRATEGY.ID.eq(id))
				.execute();
		return res > 0 ? true : false;
	}

	/**
	 * 返利策略启用
	 * @param id
	 * @return
	 */
	public boolean openRebate(Integer id) {
		int res = db().update(DISTRIBUTION_STRATEGY)
				.set(DISTRIBUTION_STRATEGY.STATUS,STATUS_NORMAL)
				.where(DISTRIBUTION_STRATEGY.ID.eq(id))
				.execute();
		return res > 0 ? true : false;
	}
	
	/**
	 * 返利策略删除
	 * @param id
	 * @return
	 */
	public boolean deleteRebate(Integer id) {
		int res = db().update(DISTRIBUTION_STRATEGY)
				.set(DISTRIBUTION_STRATEGY.DEL_FLAG,DelFlag.DISABLE_VALUE)
				.where(DISTRIBUTION_STRATEGY.ID.eq(id))
				.execute();
		return res > 0 ? true : false;
	}
}
