package com.meidianyi.shop.service.shop.distribution;

import java.sql.Timestamp;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.shop.tables.records.DistributorGroupRecord;
import com.meidianyi.shop.db.shop.tables.records.ShopCfgRecord;
import com.meidianyi.shop.service.pojo.shop.distribution.*;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.service.foundation.service.ShopBaseService;

import static com.meidianyi.shop.db.shop.Tables.*;

/**
 * @author changle
 */
@Service
public class DistributorGroupService extends ShopBaseService{
	/**
	 * 分销员分组列表
	 * @param param
	 * @return 
	 */
	public PageResult<DistributorGroupListVo> getDistributorGroupList(DistributorGroupListParam param) {
		SelectJoinStep<? extends Record> select = db()
				.select(DISTRIBUTOR_GROUP.ID,DISTRIBUTOR_GROUP.GROUP_NAME,DISTRIBUTOR_GROUP.IS_DEFAULT,DISTRIBUTOR_GROUP.DEL_FLAG,DISTRIBUTOR_GROUP.CREATE_TIME,DISTRIBUTOR_GROUP.CAN_SELECT)
				.from(DISTRIBUTOR_GROUP);
		SelectConditionStep<? extends Record> sql = buildOptions(select,param);
		PageResult<DistributorGroupListVo> groupList = this.getPageResult(sql, param.getCurrentPage(), param.getPageRows(), DistributorGroupListVo.class);
		
		//每个分组下的分销员数量
		for(DistributorGroupListVo group : groupList.dataList) {
			int groupAmount = db().selectCount().from(USER).where(USER.INVITE_GROUP.eq(group.getId())).fetchOne().into(Integer.class);
			group.setDistributorAmount(groupAmount);
		}
		return groupList;
	}
	
	/**
	 * 分组列表条件查询
	 * @param select
	 * @param param
	 * @return
	 */
	public SelectConditionStep<? extends Record> buildOptions(SelectJoinStep<? extends Record> select,DistributorGroupListParam param) {
		SelectConditionStep<? extends Record> sql = select.where(DISTRIBUTOR_GROUP.DEL_FLAG.eq((byte) 0));
		if(param.getGroupName() != null) {
			sql = sql.and(DISTRIBUTOR_GROUP.GROUP_NAME.eq(param.getGroupName()));
		}
		sql.orderBy(DISTRIBUTOR_GROUP.ID.desc());
		return sql;
	}
	
	/**
	 * 添加分组
	 * @param param
	 * @return
	 */
	public boolean adddistributorGroup(DistributorGroupListParam param) {
		DistributorGroupRecord record = new DistributorGroupRecord();
		assign(param,record);
		return db().executeInsert(record) > 0 ? true : false;
	}
	
	/**
	 * 判断分组分组是否存在
	 * @param param
	 * @return
	 */
	public boolean isExistGroup(DistributorGroupListParam param) {
		Integer res = db().selectCount()
				.from(DISTRIBUTOR_GROUP)
				.where(DISTRIBUTOR_GROUP.GROUP_NAME.eq(param.getGroupName()))
				.fetchOne().into(Integer.class);
        if (res > 0) {
            return true;
        } else {
            return false;
        }
	}
	
	/**
	 * 设置默认分组-事务
	 * @param id
	 * @return
	 */
	public boolean setDefault(Integer id) {
		this.transaction(()->{
			db().update(DISTRIBUTOR_GROUP)
				.set(DISTRIBUTOR_GROUP.IS_DEFAULT,(byte) 0)
				.where(DISTRIBUTOR_GROUP.IS_DEFAULT.eq((byte) 1))
				.execute();
			
			db().update(DISTRIBUTOR_GROUP)
				.set(DISTRIBUTOR_GROUP.IS_DEFAULT,(byte)1)
				.where(DISTRIBUTOR_GROUP.ID.eq(id))
				.execute();
		});
		return true;
	}
	
	/**
	 * 取消默认分组
	 * @param id
	 * @return
	 */
	public boolean cancleDefault(Integer id) {
		int res = db().update(DISTRIBUTOR_GROUP)
				.set(DISTRIBUTOR_GROUP.IS_DEFAULT,(byte) 0)
				.where(DISTRIBUTOR_GROUP.IS_DEFAULT.eq((byte) 1))
				.execute();
		return res > 0 ? true : false;
	}
	
	/**
	 * 获取单条分组信息
	 * @param id
	 * @return
	 */
	public DistributorGroupListVo getOneInfo(Integer id) {
        Record record = db().select().from(DISTRIBUTOR_GROUP)
            .where(DISTRIBUTOR_GROUP.ID.eq(id)).fetchOne();
        if(record != null){
             return record.into(DistributorGroupListVo.class);
        }else{
            return null;
        }
	}

	/**
	 * 编辑保存分销分组
	 * @param param
	 * @return
	 */
	public int groupSave(DistributorGroupListParam param) {
		DistributorGroupRecord record = new DistributorGroupRecord();
		assign(param,record);
		return db().executeUpdate(record);
	}
	
	/**
	 * 删除分组，假删除
	 * @param id
	 * @return
	 */
	public boolean delGroup(Integer id) {
		int result = db().update(DISTRIBUTOR_GROUP)
				.set(DISTRIBUTOR_GROUP.DEL_FLAG,(byte)1)
				.set(DISTRIBUTOR_GROUP.DEL_TIME,new Timestamp(System.currentTimeMillis()))
				.where(DISTRIBUTOR_GROUP.ID.eq(id))
				.execute();
		return result > 0 ? true : false;
	}
	
	/**
	 * 分组添加分销员
	 * @param param
	 * @return
	 */
	public boolean addDistributorGroup(AddDistributorToGroupParam param) {
		 int result = db().update(USER)
				.set(USER.INVITE_GROUP,(param.getGroupId()))
				.where(USER.USER_ID.in(param.getUserIds()))
				.execute();
		 return result > 0 ? true : false;
	}

    /**
     * 根据用户id获取分组
     * @param userId
     * @return
     */
	public DistributorGroupListVo getGroupByUserId(Integer userId){
        Record record = db().select().from(USER.leftJoin(DISTRIBUTOR_GROUP).on(USER.INVITE_GROUP.eq(DISTRIBUTOR_GROUP.ID)))
            .where(USER.USER_ID.eq(userId)).fetchOne();
        if(record != null){
          return record.into(DistributorGroupListVo.class);
        }else{
            return null;
        }
    }

    /**
     * 支持用户可选 1：支持 0：不支持
     * @param param
     * @return
     */
    public int userCanSelect(GroupCanSelectParam param){
        int res = db().update(DISTRIBUTOR_GROUP).set(DISTRIBUTOR_GROUP.CAN_SELECT,param.getCanSelect()).where(DISTRIBUTOR_GROUP
        .ID.eq(param.getGroupId())).execute();
        return res;
    }

    /**
     * 设置分销分组是否在小程序端展示
     * @param param
     * @return
     */
    public int showDistributionGroup(ShowDistributionGroupParam param){
        int res;
        //判断是否已设置
        int showDistributorGroup = db().selectCount().from(SHOP_CFG).where(SHOP_CFG.K.eq("show_distributor_group")).fetchOne().into(Integer.class);
        if(showDistributorGroup == 1){
            res = db().update(SHOP_CFG).set(SHOP_CFG.V, param.getV()).where(SHOP_CFG.K.eq("show_distributor_group")).execute();
        }else{
            ShopCfgRecord record = new ShopCfgRecord();
            assign(param,record);
            res = db().executeInsert(record);
        }
        return res;
    }

    /**
     * 获取分组显示配置
     * @return
     */
    public int getGroupCfg(){
        Record1<String> record = db().select(SHOP_CFG.V).from(SHOP_CFG).where(SHOP_CFG.K.eq("show_distributor_group")).fetchOne();
        if(record != null) {
            return record.into(Integer.class);
        }else{
            return 0;
        }

    }
}
