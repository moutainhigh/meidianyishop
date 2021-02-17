package com.meidianyi.shop.service.shop.distribution;

import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.DistributorLevelRecord;
import com.meidianyi.shop.db.shop.tables.records.UserRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.config.distribution.DistributionParam;
import com.meidianyi.shop.service.pojo.shop.distribution.AddDistributorToLevelParam;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorLevelCfgVo;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorLevelListVo;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorLevelParam;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorLevelUserNumVo;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorLevelVo;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorSpendVo;
import com.meidianyi.shop.service.pojo.shop.distribution.UpdateUserLevel;
import com.meidianyi.shop.service.pojo.shop.distribution.UserRebateLevelDetail;
import com.meidianyi.shop.service.pojo.shop.operation.RecordContentTemplate;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.shop.config.DistributionConfigService;
import com.meidianyi.shop.service.shop.operation.RecordAdminActionService;
import com.meidianyi.shop.service.shop.order.action.base.OrderOperateSendMessage;
import com.meidianyi.shop.service.shop.user.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import static com.meidianyi.shop.common.foundation.data.DistributionConstant.JUDGE_STATUS_OPEN;
import static com.meidianyi.shop.db.shop.Tables.DISTRIBUTOR_LEVEL;
import static com.meidianyi.shop.db.shop.Tables.DISTRIBUTOR_LEVEL_RECORD;
import static com.meidianyi.shop.db.shop.Tables.ORDER_INFO;
import static com.meidianyi.shop.db.shop.Tables.SERVICE_ORDER;
import static com.meidianyi.shop.db.shop.Tables.STORE_ORDER;
import static com.meidianyi.shop.db.shop.Tables.USER;
import static com.meidianyi.shop.db.shop.Tables.USER_FANLI_STATISTICS;
import static com.meidianyi.shop.db.shop.Tables.USER_TOTAL_FANLI;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.sum;

/**
 * 分销员等级配置
 * @author 常乐
 * 2019年8月1日
 */
@Service
public class DistributorLevelService extends ShopBaseService{

    @Autowired
    UserService user;

    @Autowired
    DistributorLevelRecordService distributorLevelRecord;

    @Autowired
    OrderOperateSendMessage sendMessage;

    @Autowired
    public DistributionConfigService dcs;

    @Autowired
    public RecordAdminActionService record;

	/**
	 * 分销员等级配置
	 * @return 
	 * @return
	 */
	public DistributorLevelCfgVo levelConfig() {
	    //获取每级分销员等级信息
		Integer levelInfo = db().selectCount()
				.from(DISTRIBUTOR_LEVEL)
				.where(DISTRIBUTOR_LEVEL.LEVEL_ID.eq((byte) 1))
				.fetchOne().into(Integer.class);
		
		//初始化没有默认等级，自动创建默认等级
		if(levelInfo == 0) {
			db().insertInto(DISTRIBUTOR_LEVEL,DISTRIBUTOR_LEVEL.LEVEL_ID,DISTRIBUTOR_LEVEL.LEVEL_NAME,DISTRIBUTOR_LEVEL.LEVEL_STATUS)
			.values((byte) 1,"普通分销员",(byte) 1).execute();
		}
		
		//获取全部等级信息
		List<DistributorLevelVo> allLevel = db().select().from(DISTRIBUTOR_LEVEL).fetch().into(DistributorLevelVo.class);
		HashMap<Byte, DistributorLevelVo> levelData = new HashMap<Byte, DistributorLevelVo>();
		for(DistributorLevelVo level : allLevel) {
			levelData.put(level.getLevelId(),level);
		}
		//等级用户数量
		List<DistributorLevelUserNumVo> levelUserNum = this.getUserDistributorLevel();

		DistributorLevelCfgVo distributorLevelCfg = new DistributorLevelCfgVo();
		distributorLevelCfg.setLavelData(allLevel);
		distributorLevelCfg.setLevelUserNum(levelUserNum);
		return distributorLevelCfg;
	}

    /**
     * 设置分销员等级信息
     * @param levelData
     */
    public void saveDistributorLevel(DistributorLevelParam[] levelData){
        BigDecimal baseMoney = new BigDecimal("0.00");
        int resetLevel = 0; //是否重新定级标记 0：否；1：是；
        //定义需要重新定等级的用户
        List<Integer> upUserIds = new ArrayList<Integer>();
        List<Integer> notAutoUpUser = new ArrayList<Integer>();//手动升级用户
        List<String> changeLevels = new ArrayList<>();

        //遍历各等级提交数据
        for(DistributorLevelParam level : levelData) {
            //现在表中各等级配置信息
            DistributorLevelVo levelInfo = getOneLevelInfo(level.getLevelId());
            if(levelInfo != null) {
                //等级信息配置是否有更新 （累积用户数、累积推广金、累积消费总额、升级类型、状态）
                boolean noChange = levelInfo.getInviteNumber().equals(level.getInviteNumber())
                    && (levelInfo.getTotalBuyMoney().compareTo(level.getTotalBuyMoney()) == 0)
                    && (levelInfo.getTotalDistributionMoney().compareTo(level.getTotalDistributionMoney()) == 0)
                    && levelInfo.getLevelUpRoute().equals(level.getLevelUpRoute())
                    && levelInfo.getLevelStatus().equals(level.getLevelStatus());
                boolean a = levelInfo.getTotalBuyMoney().equals(level.getTotalBuyMoney());
                //编辑配置保存
                level.setId(levelInfo.getId());
                boolean res = updateLevel(level);
                //自动升级更改
                if (res && !noChange) {
                    changeLevels.add('“' + level.getLevelName() + '”');
                    resetLevel = 1;
                }
            }else {//没有当前等级信息，插入数据
                changeLevels.add('“' + level.getLevelName() + '”');
                saveLevel(level);
            }
            if(level.getLevelUpRoute() == 1 && level.getLevelStatus() == 1){
                notAutoUpUser.addAll(Util.stringToList(level.getLevelUserIds()));
            }
        }
        if (changeLevels.size() > 0) {
            record.insertRecord(Collections.singletonList(RecordContentTemplate.DISTRIBUTION_UPDATE_LEVEL.code), StringUtils.join(changeLevels,""));
        }
        if(resetLevel == 1){
            upUserIds = distributorIds(notAutoUpUser);
            logger().info("重新计算等级用户ids"+upUserIds);
            if(upUserIds.size() > 0) {
                logger().info("开始重新计算用户等级...");
                updateUserLevel(upUserIds,"保存更改等级配置",1);
            }
        }
    }
	
	/**
	 * 获取等级用户的数量
	 * @return
	 */
	public List<DistributorLevelUserNumVo> getUserDistributorLevel() {
		SelectHavingStep<Record2<Byte, Integer>> sql = db().select(USER.DISTRIBUTOR_LEVEL,count().as("user_number")).from(USER).groupBy(USER.DISTRIBUTOR_LEVEL);
		
		DistributionParam fanliCfg = saas().getShopApp(getShopId()).config.distributionCfg.getDistributionCfg();
		
		if(fanliCfg.getJudgeStatus() == 1) {
			sql = ((SelectWhereStep<Record2<Byte, Integer>>) sql).where(USER.IS_DISTRIBUTOR.eq((byte) 1));
		}
		List<DistributorLevelUserNumVo> res = sql.fetch().into(DistributorLevelUserNumVo.class);
		return res;
	}

	/**
	 * 获得单个等级信息
	 * @param levelId
	 * @return
	 */
	public DistributorLevelVo getOneLevelInfo(Byte levelId) {
		DistributorLevelVo res = null;
		Record levelInfo = db().select().from(DISTRIBUTOR_LEVEL)
				.where(DISTRIBUTOR_LEVEL.LEVEL_ID.eq(levelId)).fetchOne();
		if(levelInfo != null){
			res = levelInfo.into(DistributorLevelVo.class);
		}
		return res;
	}
	
	/**
	 * 编辑保存分销员等级设置
	 * @param level
	 * @return
	 */
	public boolean updateLevel(DistributorLevelParam level) {
		DistributorLevelRecord record = db().newRecord(DISTRIBUTOR_LEVEL,level);
		return record.update() > 0 ? true : false;
	}

	/**
	 * 获取分销员等级配置信息
	 * @return
	 */
	public DistributorLevelListVo distributorLevelList(){
		List<DistributorLevelParam> lists = db().select(DISTRIBUTOR_LEVEL.ID, DISTRIBUTOR_LEVEL.LEVEL_ID, DISTRIBUTOR_LEVEL.LEVEL_NAME,
				DISTRIBUTOR_LEVEL.LEVEL_STATUS, DISTRIBUTOR_LEVEL.TOTAL_BUY_MONEY, DISTRIBUTOR_LEVEL.TOTAL_DISTRIBUTION_MONEY,
				DISTRIBUTOR_LEVEL.INVITE_NUMBER, DISTRIBUTOR_LEVEL.LEVEL_UP_ROUTE)
				.from(DISTRIBUTOR_LEVEL).fetch().into(DistributorLevelParam.class);
		DistributorLevelListVo level = new DistributorLevelListVo();
        DistributionParam distributionCfg = dcs.getDistributionCfg();
        for(DistributorLevelParam list : lists){
            SelectConditionStep<Record1<Integer>> sql = db().selectCount().from(USER).where(USER.DISTRIBUTOR_LEVEL.eq(list.getLevelId()));
            if(distributionCfg.getJudgeStatus()!=null && distributionCfg.getJudgeStatus() == 1){
                sql = sql.and(USER.IS_DISTRIBUTOR.eq((byte)1));
            }
            int userNum = sql.fetchOne().into(Integer.class);
            list.setUsers(userNum);
            //获取当前等级分销员ID
            List<Integer> userIds = db().select(USER.USER_ID).from(USER).where(USER.DISTRIBUTOR_LEVEL.eq(list.getLevelId())).fetch().into(Integer.class);
            list.setLevelUserIds(Util.listToString(userIds));
        }
		level.setLevelList(lists);
		return level;
	}

	/**
	 * 停用分销员等级配置
	 * @param id
	 * @return
	 */
	public int pauseDistributorLevel(Integer id){
		int res = db().update(DISTRIBUTOR_LEVEL).set(DISTRIBUTOR_LEVEL.LEVEL_STATUS,(byte)0).where(DISTRIBUTOR_LEVEL.ID.eq(id)).execute();
		return res;
	}

	/**
	 * 启用分销员等级配置
	 * @param id
	 * @return
	 */
	public int openDistributorLevel(Integer id){
		int res = db().update(DISTRIBUTOR_LEVEL).set(DISTRIBUTOR_LEVEL.LEVEL_STATUS,(byte)1).where(DISTRIBUTOR_LEVEL.ID.eq(id)).execute();
		return res;
	}
	
	/**
	 * 创建保存分销员等级配置
	 * @param level
	 * @return
	 */
	public int saveLevel(DistributorLevelParam level) {
		DistributorLevelRecord record = new DistributorLevelRecord();
		assign(level, record);
		return db().executeInsert(record);
	}
	
	/**
	 * 可升级的等级
	 * @param levelId
	 * @return 
	 * @return 
	 */
	public List<Byte> getLowerCanUpLevels(int levelId) {
		List<Byte> canUplevels = new ArrayList<Byte>();
		if(levelId <= 1) {
			canUplevels.add((byte)1);
			return canUplevels;
		}
		
		List<DistributorLevelVo> goingLevels = this.getGoingLevels();
		for(levelId = levelId - 1;levelId >= 1;levelId--) {
			for(DistributorLevelVo level : goingLevels) {
				if(levelId == level.getLevelId()) {
					if(level.getLevelUpRoute() == 1) {
						canUplevels.add((byte)levelId);
					}
				}
			}
		}
		return canUplevels;	
	}
	
	/**
	 * 获取启用中的等级
	 * @return
	 */
	public List<DistributorLevelVo> getGoingLevels() {
		List<DistributorLevelVo> res = db().select().from(DISTRIBUTOR_LEVEL)
				.where(DISTRIBUTOR_LEVEL.LEVEL_STATUS.eq((byte) 1)).orderBy(DISTRIBUTOR_LEVEL.LEVEL_ID)
				.fetch().into(DistributorLevelVo.class);
		return res;
	}
	
	/**
	 * 每级下用户id
	 * @param canUpLevel
	 * @return
	 */
	public List<Integer> getLevelUser(Byte canUpLevel) {
		 List<Integer> userIds = db().select(USER.USER_ID).from(USER).where(USER.DISTRIBUTOR_LEVEL.eq(canUpLevel)).fetch().into(Integer.class);
		 return userIds;
	}
	
	/**
	 * 手动升级改为自动升级分销员，等级更新为最低级
	 * @param updateUserIds
	 */
	public void updateLevelToOne(List<Integer> updateUserIds) {
		Result<Record> users = db().select().from(USER .leftJoin(DISTRIBUTOR_LEVEL)
				.on(USER.DISTRIBUTOR_LEVEL.eq(DISTRIBUTOR_LEVEL.LEVEL_ID)))
				.where(USER.USER_ID .in(updateUserIds))
				.and(DISTRIBUTOR_LEVEL.LEVEL_ID.ne((byte)1)).fetch();	
		Record levelInfo = db().select().from(DISTRIBUTOR_LEVEL).where(DISTRIBUTOR_LEVEL.LEVEL_ID.eq((byte)1)).fetchOne();	
		
		for(Record user : users) {
			Byte isGoUp = user.get(USER.DISTRIBUTOR_LEVEL) > 1 ? (byte)0 : (byte)1;
				
			db().insertInto(DISTRIBUTOR_LEVEL_RECORD,DISTRIBUTOR_LEVEL_RECORD.USER_ID,
					DISTRIBUTOR_LEVEL_RECORD.IS_GO_UP,DISTRIBUTOR_LEVEL_RECORD.OLD_LEVEL,
					DISTRIBUTOR_LEVEL_RECORD.OLD_LEVEL_NAME,DISTRIBUTOR_LEVEL_RECORD.NEW_LEVEL,
					DISTRIBUTOR_LEVEL_RECORD.NEW_LEVEL_NAME,DISTRIBUTOR_LEVEL_RECORD.UPDATE_NOTE)
			.values(user.get(USER.USER_ID),isGoUp,user.get(USER.DISTRIBUTOR_LEVEL),user.get(DISTRIBUTOR_LEVEL.LEVEL_NAME),(byte) 1,
					levelInfo.get(DISTRIBUTOR_LEVEL.LEVEL_NAME),"后台修改配置")
			.execute();
		}
	}

    /**
     *
     * @param upUserIds
     * @param updateNote
     * @param upType 等级升级类型 0：只升级；1：升级&降级
     * @return
     */
    public boolean updateUserLevel(List<Integer> upUserIds, String updateNote ,Integer upType) {
        logger().info("需要重新定级用户"+upUserIds);
        //auto_levels order by LEVEL(asc)
        Result<DistributorLevelRecord> autoLevels = this.getAutoUpdateLevels();
        if(autoLevels.size() <= 0) {
            return true;
        }
        //获取分销员分销信息（分销金额）USER.USER_ID,USER_TOTAL_FANLI.SUBLAYER_NUMBER,USER.DISTRIBUTOR_LEVEL,DISTRIBUTOR_LEVEL.LEVEL_NAME
        List<UserRebateLevelDetail> distributorInfo = this.getUserRebateLevelDetail(upUserIds);
        //获取邀请人的推广金额
        Map<Integer, BigDecimal> distributorRebate = this.getDistributorRebate(upUserIds);
        //更新用户信息
        ArrayList<UserRecord> updateUser = new ArrayList<>();
        //消息推送
        Map<Integer,UpdateUserLevel> updateUserLevelMsg = new HashMap<>();
        //根据配置条件给分销员定等级
        for(UserRebateLevelDetail upUser : distributorInfo) {
            //当前用户是否进行过重新定级 0：否；1：是；
            int hasResetLevel = 0;
            //获取用户的订单和门店买单消费总额
            DistributorSpendVo userSpend = this.getTotalSpend(upUser.getUserId());
            for(DistributorLevelRecord level : autoLevels) {
                if(upType == 0){
                    //不用升级
                    if(upUser.getDistributorLevel() >= level.getLevelId() || level.getLevelStatus().equals(OrderConstant.NO)){
                        continue;
                    }
                }
                //累计邀请用户数
                logger().info("重新定级用户id--invite:"+level.getInviteNumber()+"--SublayerNumber:"+upUser.getSublayerNumber());
                boolean compareInviteNumber = false;
                if(upUser.getSublayerNumber() != null) {
                    compareInviteNumber = level.getInviteNumber() > 0 && (upUser.getSublayerNumber() >= level.getInviteNumber());
                }
                //累计推广金
                boolean compareDistributionMoney = BigDecimalUtil.compareTo(level.getTotalDistributionMoney(), null) > 0 && BigDecimalUtil.compareTo(distributorRebate.get(upUser.getUserId()), level.getTotalDistributionMoney()) >= 0;
                //累积推广金与消费金总和
                boolean compareFanliMoney = BigDecimalUtil.compareTo(level.getTotalBuyMoney(), null) > 0 && BigDecimalUtil.compareTo(
                    BigDecimalUtil.add(distributorRebate.get(upUser.getUserId()), userSpend.getTotal()),
                    level.getTotalBuyMoney()) >= 0;
                logger().info("重新定级用户id" + upUser.getUserId()+"----"+compareInviteNumber+"----"+compareDistributionMoney+"---"+compareFanliMoney);
                if(compareInviteNumber || compareDistributionMoney || compareFanliMoney) {
                    hasResetLevel = 1;
                    logger().info("正重新定级用户" + upUser.getUserId());
                    UserRecord userInfo = user.getUserByUserId(upUser.getUserId());
                    UpdateUserLevel updateUserLevel = updateUserLevelMsg.get(userInfo.getUserId());
                    if(upUser.getDistributorLevel().compareTo(level.getLevelId()) < 0){
                        if(updateUserLevel == null) {
                            updateUserLevelMsg.put(userInfo.getUserId(), new UpdateUserLevel(upUser.getUserId(), (byte)1, upUser.getDistributorLevel(),level.getLevelName() ,level.getLevelId(),upUser.getLevelName() , updateNote));
                        }else {
                            updateUserLevelMsg.put(userInfo.getUserId(), new UpdateUserLevel(upUser.getUserId(), (byte)1, updateUserLevel.getOldLevel(), updateUserLevel.getOldLevelName(), level.getLevelId(), level.getLevelName(), updateNote));
                        }
                    }
                    userInfo.setDistributorLevel(level.getLevelId());
                    updateUser.add(userInfo);
                }
            }
            if(hasResetLevel == 0){
                logger().info("降级到最低等级"+upUser.getUserId());
                UserRecord userInfo = user.getUserByUserId(upUser.getUserId());
                userInfo.setDistributorLevel((byte)1);
                updateUser.add(userInfo);
            }
        }
        //更新
        db().batchUpdate(updateUser).execute();
        //等级变化记录
        distributorLevelRecord.add(updateUserLevelMsg.values());
        //消息推送
        sendMessage.rebateUpdateUserLevel(updateUserLevelMsg.values());
        return true;
    }
	
	/**
	 * 自动升级的等级
	 * @return
	 */
	public Result<DistributorLevelRecord> getAutoUpdateLevels() {
        return db().selectFrom(DISTRIBUTOR_LEVEL)
				.where(DISTRIBUTOR_LEVEL.LEVEL_UP_ROUTE.eq((byte)0))
				.orderBy(DISTRIBUTOR_LEVEL.LEVEL_ID)
				.fetch();
	}

	/**
	 * 	分销员分销信息
	 * @param upUserIds
	 * @return
	 */
	public List<UserRebateLevelDetail> getUserRebateLevelDetail(List<Integer> upUserIds) {
		List<UserRebateLevelDetail> distributorInfo = db().select(USER.USER_ID,USER_TOTAL_FANLI.SUBLAYER_NUMBER,USER.DISTRIBUTOR_LEVEL,DISTRIBUTOR_LEVEL.LEVEL_NAME)
				.from(USER.leftJoin(USER_TOTAL_FANLI).on(USER.USER_ID.eq(USER_TOTAL_FANLI.USER_ID))
				.leftJoin(DISTRIBUTOR_LEVEL).on(USER.DISTRIBUTOR_LEVEL.eq(DISTRIBUTOR_LEVEL.LEVEL_ID)))
				.where(USER.USER_ID.in(upUserIds)).fetchInto(UserRebateLevelDetail.class);
		return distributorInfo;
	}
	
	/**
	 * 邀请人推广金
	 * @param upUserIds
	 * @return
	 */
	public Map<Integer, BigDecimal> getDistributorRebate(List<Integer> upUserIds) {
        return db()
            .select(USER_FANLI_STATISTICS.FANLI_USER_ID, sum(USER_FANLI_STATISTICS.TOTAL_CAN_FANLI_MONEY))
            .from(USER_FANLI_STATISTICS)
            .where(USER_FANLI_STATISTICS.FANLI_USER_ID.in(upUserIds))
            .and(USER_FANLI_STATISTICS.REBATE_LEVEL.eq((byte) 1))
            .groupBy(USER_FANLI_STATISTICS.FANLI_USER_ID)
            .fetchMap(USER_FANLI_STATISTICS.FANLI_USER_ID, sum(USER_FANLI_STATISTICS.TOTAL_CAN_FANLI_MONEY));
	}
	
	/**
	 * 获取用户的订单和门店买单消费总额
	 */
	public DistributorSpendVo getTotalSpend(int userId) {
		//会员卡支付
		BigDecimal memeberCardBalance = db().select(sum(ORDER_INFO.MEMBER_CARD_BALANCE)).from(ORDER_INFO).where(ORDER_INFO.ORDER_STATUS.eq((byte) 6)).and(ORDER_INFO.USER_ID.eq(userId)).fetchSingleInto(BigDecimal.class);
		logger().info("会员卡支付"+memeberCardBalance);
		//微信实际支付
		BigDecimal moneyPaid = db().select(sum(ORDER_INFO.MONEY_PAID)).from(ORDER_INFO).where(ORDER_INFO.PAY_CODE.in("balance","wxpay")).and(ORDER_INFO.ORDER_STATUS.eq((byte) 6)).and(ORDER_INFO.USER_ID.eq(userId)).fetchSingleInto(BigDecimal.class);
		logger().info("微信实际支付"+moneyPaid);
		//余额支付
		BigDecimal useAccount = db().select(sum(ORDER_INFO.USE_ACCOUNT)).from(ORDER_INFO).where(ORDER_INFO.ORDER_STATUS.eq((byte) 6)).and(ORDER_INFO.USER_ID.eq(userId)).fetchSingleInto(BigDecimal.class);
		logger().info("余额支付"+useAccount);
		//门店消费
		BigDecimal storeMemeberCardBalance = db().select(sum(STORE_ORDER.MEMBER_CARD_BALANCE)).from(STORE_ORDER).where(STORE_ORDER.ORDER_STATUS.eq((byte)1)).and(STORE_ORDER.USER_ID.eq(userId)).fetchSingleInto(BigDecimal.class);
		BigDecimal storeMoneyPaid = db().select(sum(STORE_ORDER.MONEY_PAID)).from(STORE_ORDER).where(STORE_ORDER.ORDER_STATUS.eq((byte)1)).and(STORE_ORDER.USER_ID.eq(userId)).fetchSingleInto(BigDecimal.class);
		BigDecimal storeUseAccount = db().select(sum(STORE_ORDER.USE_ACCOUNT)).from(STORE_ORDER).where(STORE_ORDER.ORDER_STATUS.eq((byte)1)).and(STORE_ORDER.USER_ID.eq(userId)).fetchSingleInto(BigDecimal.class);
		logger().info("门店消费"+storeMemeberCardBalance);
		logger().info("门店消费"+storeMoneyPaid);
		logger().info("门店消费"+storeUseAccount);
		//门店预约
		BigDecimal serviceMoneyPaid = db().select(sum(SERVICE_ORDER.MONEY_PAID)).from(SERVICE_ORDER).where(SERVICE_ORDER.ORDER_STATUS.eq((byte)2)).and(SERVICE_ORDER.PAY_CODE.in("balance","wxpay")).and(SERVICE_ORDER.USER_ID.eq(userId)).fetchSingleInto(BigDecimal.class);
		logger().info("门店预约"+serviceMoneyPaid);
		BigDecimal card = check(memeberCardBalance).add(check(storeMemeberCardBalance));
		BigDecimal paid = check(moneyPaid).add(check(storeMoneyPaid));
		BigDecimal account = check(useAccount).add(check(storeUseAccount));
		BigDecimal total = check(card).add(check(paid).add(check(account)).add(check(serviceMoneyPaid)));
		logger().info("card:"+card+"paid:"+paid+"account"+account+"total"+total);
		DistributorSpendVo spendInfo = new DistributorSpendVo();
		spendInfo.setCard(card);
		spendInfo.setPaid(paid);
		spendInfo.setAccount(account);
		spendInfo.setTotal(total);
		
		return spendInfo;
		
	}
	private BigDecimal check(BigDecimal bigDecimal) {
		if(null==bigDecimal) {
			return new BigDecimal(0);
		}
		return bigDecimal;
		
	}

    /**
     * 分销等等级配置，手动升级添加分销员
     * @param param
     * @return
     */
	public Integer addDistributorToLevel(AddDistributorToLevelParam param){
        int result = db().update(USER)
            .set(USER.DISTRIBUTOR_LEVEL,(param.getLevel()))
            .where(USER.USER_ID.in(param.getUserIds()))
            .execute();
        return result;
    }

    /**
     * 获取下级用户数
     * @param userId
     * @return
     */
    public Integer getNextUser(Integer userId){
        Record1<Integer> record1 = db().selectCount().from(USER).where(USER.INVITE_ID.eq(userId)).fetchOne();
        if(record1 != null){
            return record1.into(Integer.class);
        }else{
            return 0;
        }
    }

    /**
     * 根据userId获取对应分组
     * @param userId
     * @return
     */
    public DistributorLevelVo getLevelByUser(Integer userId){
        Record record = db().select().from(USER.leftJoin(DISTRIBUTOR_LEVEL).on(USER.DISTRIBUTOR_LEVEL.eq(DISTRIBUTOR_LEVEL.LEVEL_ID)))
            .where(USER.USER_ID.eq(userId)).fetchOne();
        if(record != null){
            return record.into(DistributorLevelVo.class);
        }else{
            return null;
        }
    }

    /**
     * 全部分销员用户ID
     * @return
     */
    public List<Integer> distributorIds(List<Integer> notAutoUpUser){
        SelectConditionStep<Record1<Integer>> sql = db().select(USER.USER_ID).from(USER).where(USER.USER_ID.notIn(notAutoUpUser));

        //是否全民分销，看是分销审核是否开启
        DistributionParam cfg = dcs.getDistributionCfg();
        if(cfg != null && cfg.getJudgeStatus().equals(JUDGE_STATUS_OPEN)){
            sql.and(USER.IS_DISTRIBUTOR.eq((byte) 1));
        }

        Result<Record1<Integer>> record = sql.fetch();
        if(record != null){
            return record.into(Integer.class);
        }else{
            return null;
        }
    }

}
