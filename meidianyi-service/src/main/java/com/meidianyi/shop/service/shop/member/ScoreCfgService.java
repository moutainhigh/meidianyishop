package com.meidianyi.shop.service.shop.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.ShopCfgRecord;
import com.meidianyi.shop.db.shop.tables.records.UserScoreSetRecord;
import com.meidianyi.shop.db.shop.tables.records.XcxCustomerPageRecord;
import com.meidianyi.shop.service.pojo.shop.member.ShopCfgParam;
import com.meidianyi.shop.service.pojo.shop.member.score.ScoreCfgVo;
import com.meidianyi.shop.service.pojo.shop.member.score.ScoreFrontParam;
import com.meidianyi.shop.service.pojo.shop.member.score.ScoreFrontVo;
import com.meidianyi.shop.service.pojo.shop.member.score.UserScoreSetValue;
import com.meidianyi.shop.service.shop.decoration.MpDecorationService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.InsertValuesStep3;
import org.jooq.Record2;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Map;

import static com.meidianyi.shop.db.shop.tables.ShopCfg.SHOP_CFG;
import static com.meidianyi.shop.db.shop.tables.UserScoreSet.USER_SCORE_SET;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.BUTTON_OFF;
import static com.meidianyi.shop.service.pojo.shop.member.card.CardConstant.BUTTON_ON;
/**
 * 积分配置Service
 *
 * @author 黄壮壮 2019-07-15 14:13
 */
@Service

public class ScoreCfgService extends BaseScoreCfgService {

	@Autowired
	public MpDecorationService mpDecoration;

	/**
	 * 购物送积分
	 */
	final public static String BUY="buy";
	final public static String BUY_EACH="buy_each";


    /** 积分国际化信息 */
	final public static String SCORE_CFG_TITLE="member.score.cfg.title";
	final public static Byte ZERO = NumberUtils.BYTE_ZERO;
	final public static Byte ONE = NumberUtils.BYTE_ONE;
	public int setShopCfg(ShopCfgParam param) {

		int result = 1;
		cfgScoreEffectiveRule(param);
		// 积分兑换比
		setScoreProportion(param.getScoreProportion());

		// 积分支付限制
		Byte scorePayLimit = param.getScorePayLimit();
		if (NumberUtils.BYTE_ZERO.equals(scorePayLimit)) {
			// 不限制
			setScorePayLimit(scorePayLimit);
		} else if (NumberUtils.BYTE_ONE.equals(scorePayLimit)) {
			// 自定义积分
			setScorePayLimit(scorePayLimit);
			setScorePayNum(param.getScorePayNum());
		}

		setScoreDiscountRatio(param.getScoreDiscountRatio());

        /** 购物送积分 */
		/** 积分开关 */
		byte shoppingScore = (BUTTON_ON.equals(param.getShoppingScore()))?ONE:ZERO;
		setShoppingScore(shoppingScore);

		/** 购物送积分的类型 0 购物多少送多少 1 购买每多少送多少 */
		Byte scoreType = param.getScoreType();
		setScoreType(scoreType);

		if(ONE.equals(shoppingScore)) {
			if(ZERO.equals(scoreType)) {
				/** 更新满多少送多少积分 */
				updateRecord(param,BUY);
			}

			if(ONE.equals(scoreType)) {
				/** 更新每满多少送多少积分 */
				updateRecord(param,BUY_EACH);
			}
		}

        //门店买单返送积分开关 on 1
		byte storeScore = BUTTON_ON.equals(param.getStoreScore()) ? ONE:ZERO;
		setStoreScore(storeScore);

		//登录送给积分
		byte loginScore = BUTTON_ON.equals(param.getLoginScore())? ONE:ZERO;
		setLoginScore(loginScore);
		if(ONE == loginScore ) {
			//登录送积分开关on
			setScoreLogin(param.getScoreLogin());
		}

		setDiscount(String.valueOf(param.getDiscountHasShipping()));
        //签到送积分
		byte signInScore = BUTTON_ON.equals(param.getSignInScore()) ? ONE:ZERO;
		setSignScore(signInScore,param.getSignScore(),param.getSignInRules());
		return result;
	}






	/**
	 * 配置积分有效规则
	 */
	private void cfgScoreEffectiveRule(ShopCfgParam param) {
		Byte type = param.getScoreLimit();
		Assert.isTrue(isEffectiveForever(type) || isEffectiveUtilToYmd(type) ||isEffectiveFromCurrentDay(type)
			, "配置积分有效规则错误");
		setScoreLimit(type);
		if ( isEffectiveUtilToYmd(type)) {
			setScoreDay(param.getScoreDay());
			setScoreMonth(param.getScoreMonth());
			setScoreYear(param.getScoreYear());
		} else if (isEffectiveFromCurrentDay(type)) {
			setScoreLimitNumber(param.getScoreLimitNumber());
			setScorePeriod(param.getScorePeriod());
		}
	}

	/**
	 * 是否永久有效
	 */
	private boolean isEffectiveForever(Byte effectiveType) {
		return NumberUtils.BYTE_ZERO.equals(effectiveType);
	}
	/**
	 * 是否有效为： 从获得开始至某年某月某日
	 */
	private boolean isEffectiveUtilToYmd(Byte effectiveType) {
		return NumberUtils.BYTE_ONE.equals(effectiveType);
	}
	/**
	 * 是否有效为：从获得积分当天起
	 */
	private boolean isEffectiveFromCurrentDay(Byte effectiveType) {
		return Byte.valueOf((byte) 2).equals(effectiveType);
	}



	/**
	 * 更新设置签到积分
	 */
	private void setSignScore(Byte enable,String[] signScore,Byte signInRules) {
		signInRules=null==signInRules?ZERO:signInRules;
		signScore=null==signScore?new String[] {"0"}:signScore;
		UserScoreSetValue userScore=new UserScoreSetValue(enable, signScore, signInRules);
		String value = Util.toJson(userScore);
		setJsonObject(SIGN_IN_SCORE,enable,value);
	}

	/**
	 * 获取数据库中set_val3的值
	 */
	public UserScoreSetValue getScoreValueThird(String scoreName) {
		UserScoreSetRecord  record = this.db()
						.selectFrom(USER_SCORE_SET)
						.where(USER_SCORE_SET.SCORE_NAME.eq(scoreName))
						.fetchAny();
		UserScoreSetValue userScore=null;
		if(record!=null) {
			userScore = Util.parseJson(record.getSetVal3(),UserScoreSetValue.class);
		}
		return userScore;
	}
	/**
	 * 插入到数据库
	 */
	private void setJsonObject(String scoreName,Byte status,String value) {
		UserScoreSetRecord record = db().selectFrom(USER_SCORE_SET).where(USER_SCORE_SET.SCORE_NAME.eq(scoreName)).fetchAny();
		if(record!=null) {
			record.setStatus(status);
			record.setSetVal3(value);
			int update = record.update();
			logger().info("更新积分设置："+value+"。状态："+update);
		}else {
			int execute = this.db().insertInto(USER_SCORE_SET,USER_SCORE_SET.SCORE_NAME,USER_SCORE_SET.STATUS,USER_SCORE_SET.SET_VAL3)
			.values(scoreName, status,value).execute();
			logger().info("插入积分设置："+value+"。状态："+execute);
		}
	}

	/**
	 * 清空之前的记录
	 * @param name
	 * @return
	 */
	public int deleteRecord(String name) {
		return this.db()
					.delete(USER_SCORE_SET)
					.where(USER_SCORE_SET.SCORE_NAME.eq(name))
					.execute();
	}


    /**
	 * 更新积分
	 * @param param
	 * @return
	 */
	public void updateRecord(ShopCfgParam param,String scoreName) {

			// 清空之前的积分
			deleteRecord(scoreName);

        //插入新的积分
			 InsertValuesStep3<UserScoreSetRecord, String, String, String> insert = this.db()
					 							.insertInto(USER_SCORE_SET,USER_SCORE_SET.SCORE_NAME,USER_SCORE_SET.SET_VAL,USER_SCORE_SET.SET_VAL2);

        String[] buy;
			 String[] score;

        if(BUY.equals(scoreName)) {
				 buy = param.getBuy();
				 score = param.getScore();
			 }else {
				 buy = param.getBuyEach();
				 score = param.getScoreEach();
			 }

        int length = buy.length<score.length?buy.length:score.length;
			 for(int i=0;i<length;i++) {
				 insert = insert.values(scoreName, buy[i], score[i]);
			 }
			insert.execute();

    }

	/**
	 * 获取积分配置信息
	 * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
	 */
	public ScoreCfgVo getShopScoreCfg() {

		ScoreCfgVo vo = new ScoreCfgVo();


		// 查询配置文件的key-value
		Result<Record2<String, String>> resMap = db().select(SHOP_CFG.K,SHOP_CFG.V).from(SHOP_CFG).fetch();
		Map<String, String> intoMap = null;
		if(resMap != null) {
		 intoMap =  resMap.intoMap(SHOP_CFG.K, SHOP_CFG.V);
		}
		ObjectMapper objectMapper = new ObjectMapper();
		// 将查询的结果赋值到pojo
		vo = objectMapper.convertValue(intoMap, ScoreCfgVo.class);
		logger().info("查询结果赋值成功");

        //查询buy
		Result<Record2<String, String>> result = getValFromUserScoreSet(BUY);
		for(int i=0;i<result.size();i++) {
			Record2<String, String> record = result.get(i);
			vo.getBuy().add((String) record.get(0));
			vo.getBuyScore().add((String)record.get(1));
		}
		logger().info("查询buy处理成功");

        //查询buyEach
		Result<Record2<String, String>> resultEach = getValFromUserScoreSet(BUY_EACH);
		for(int i=0;i<resultEach.size();i++) {
			Record2<String, String> record = resultEach.get(i);
			vo.getBuyEach().add((String) record.get(0));
			vo.getBuyEachScore().add((String)record.get(1));
		}
		logger().info("查询buyEach处理成功");

		// 处理门店买单送积分开关
		Byte storeScore = getStoreScore();
		if(ZERO.equals(storeScore)) {
			vo.setStoreScore(BUTTON_OFF);
		}else {
			vo.setStoreScore(BUTTON_ON);
		}

		// 处理积分开关
		Byte shoppingScore = getShoppingScore();
		if(ZERO.equals(shoppingScore)) {
			vo.setShoppingScore(BUTTON_OFF);
		}else {
			vo.setShoppingScore(BUTTON_ON);
		}

		// 处理登录送积分开关
		Byte loginScore = getLoginScore();
		if(ZERO.equals(loginScore)) {
			vo.setLoginScore(BUTTON_OFF);
		}else {
			vo.setLoginScore(BUTTON_ON);
		}

        // 处理签到积分
		logger().info("处理签到积分");
		UserScoreSetValue userScore = getScoreValueThird(SIGN_IN_SCORE);
		if(null==userScore) {
			userScore=new UserScoreSetValue(ZERO, new String[] {"0"}, ZERO);
		}
		if(userScore.getEnable()!=null && ONE.equals(userScore.getEnable())) {
			vo.setSignInScore(BUTTON_ON);
		}else {
			vo.setSignInScore(BUTTON_OFF);
		}
		logger().info("处理签到积分开关成功");

        vo.setSignScore(userScore.getScore());
        logger().info("处理签到数据成功");
        Byte signInRules = userScore.getSignInRules();
        vo.setSignInRules(signInRules==null?ZERO:signInRules);
        // 模板名称
		if(!StringUtils.isBlank(vo.getScorePageId())) {
			XcxCustomerPageRecord xcxCustomerPage = mpDecoration.getPageById(Integer.parseInt(vo.getScorePageId()));
			if(xcxCustomerPage != null) {
				vo.setPageName(xcxCustomerPage.getPageName());
			}
		}
		logger().info("模板名称处理成功");
		Integer scoreProportion = getScoreProportion();
		logger().info("积分兑换比查询："+scoreProportion);
		vo.setScoreProportion(scoreProportion==null?100:scoreProportion);
		vo.setDiscountHasShipping(Byte.valueOf(getDiscount()));
		return vo;
	}

	/**
	 * 获取数据
	 * @return
	 */
    public Result<Record2<String, String>> getValFromUserScoreSet(String value) {
		Result<Record2<String, String>> result = db().select(USER_SCORE_SET.SET_VAL,USER_SCORE_SET.SET_VAL2)
													.from(USER_SCORE_SET)
													.where(USER_SCORE_SET.SCORE_NAME.eq(value)).fetch();
		return result;
	}

    /**
     * 获取数据
     */
    public Result<Record2<String, String>> getValFromUserScoreSet(String value, String money) {
        Result<Record2<String, String>> result = db().select(USER_SCORE_SET.SET_VAL, USER_SCORE_SET.SET_VAL2)
            .from(USER_SCORE_SET)
            .where(USER_SCORE_SET.SCORE_NAME.eq(value))
            .and(USER_SCORE_SET.SET_VAL.lessOrEqual(money))
            .orderBy(USER_SCORE_SET.SET_VAL.desc()).fetch();
        return result;
    }


	/**
	 * 查询某种活动对应积分
	 */
	public ShopCfgRecord getScoreNum(String k) {
		return db().selectFrom(SHOP_CFG).where(SHOP_CFG.K.eq(k)).fetchAny();
	}

	/**
	 * 保存文档
	 */
	public void saveScoreDocument(ScoreFrontParam param) {
		param.setUpdateTime(DateUtils.getLocalDateTime());
		String value = Util.toJson(param);
		logger().info(value);
		setScoreDocument(value);
	}

	/**
	 * 获取文档
	 */
	public ScoreFrontVo scoreCopywriting() {
		String value = db().select(SHOP_CFG.V).from(SHOP_CFG)
							.where(SHOP_CFG.K.eq(SCORE_DOCUMENT))
							.fetchAnyInto(String.class);

		ScoreFrontVo vo = new ScoreFrontVo();
		vo.setTitle(SCORE_CFG_TITLE);
		if(value != null) {
			ScoreFrontParam param = Util.parseJson(value, ScoreFrontParam.class);
			vo.setDocument(param.getDocument());
			return vo;
		}else {
			return null;
        }
	}

    /**
     * 	积分模板页添加 addScore
	 */
	public void addScoreCfgForDecoration(ShopCfgParam param) {
		if(param.getScorePageId() != null) {
			setScorePageId(""+param.getScorePageId());
		}else {
			setScorePageId(null);
		}
	}
}
