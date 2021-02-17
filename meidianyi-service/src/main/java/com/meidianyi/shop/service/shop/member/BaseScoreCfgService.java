package com.meidianyi.shop.service.shop.member;

import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.pojo.shop.member.score.UserScoreSetValue;
import com.meidianyi.shop.service.shop.config.BaseShopConfigService;
import static org.apache.commons.lang3.math.NumberUtils.BYTE_ZERO;

import java.util.Arrays;
import java.util.List;

import org.springframework.util.Assert;

/**
* @author 黄壮壮
* @Date: 2019年10月25日
* @Description: 积分配置
*/
public class BaseScoreCfgService extends BaseShopConfigService {
	
    /**
     * 积分有效期  0: 永久积分
     */
	final public static Byte SCORE_LT_FOREVER = 0;
    /**
     * 积分有效期  1: 从获得开始至 年-月-日
     */
	final public static Byte SCORE_LT_YMD = 1;

    /**
     * 2: 从获得积分当天起-内有效
     */
    final public static Byte SCORE_LT_NOW = 2;
	final public static List<Integer> SCORE_PROPORTION_LIST = Arrays.asList(new Integer[] {1,10,100,1000});

    /**
     * 积分抵扣比例 默认50%
     */
    final public static Integer DEFAULT_SCORE_DISCOUNT_RATIO=50;

    /**
     * 积分有效期  0: 永久积分; 1: 从获得开始至 年-月-日; 2: 从获得积分当天起-内有效
     */
    final public static String SCORE_LIMIT = "score_limit";
    /**
     *  积分有效期-从获得开始至 ：日
     */
    final public static String SCORE_DAY = "score_day";
	/*** 积分有效期-从获得开始至 ：月 */
	final public static String SCORE_MONTH = "score_month";
	/**
     *  积分有效期-从获得开始至 ：年
     */
	final public static String SCORE_YEAR = "score_year";
	/** 积分有效期-从获得积分当天起 多少数额 */
	final public static String SCORE_LIMIT_NUMBER = "score_limit_number";
    /** 积分有效期-从获得积分当天起  数额单位  */
	final public static String SCORE_PERIOD = "score_period";
	/** 积分支付限制 0： 不限制；1：自定义 */
	final public static String SCORE_PAY_LIMIT = "score_pay_limit";
	/** 每单支付的积分数量少于 score_pay_num 积分，不可使用积分支付 */
	final public static String SCORE_PAY_NUM = "score_pay_num";
	/** 积分抵扣比例用户可使用积分抵扣订单折后金额的 百分比  */
	final public static String SCORE_DISCOUNT_RATIO="score_discount_ratio";
	/** 购物送积分开关： 0： 关闭； 1： 开启 */
	final public static String SHOPPING_SCORE ="shopping_score";
	/** 购物送积分类型： 0： 购物满；1：购物每满 */
	final public static String SCORE_TYPE = "score_type";
	/** 门店买单送积分： 0： 关闭； 1： 开启 */
	final public static String STORE_SCORE = "store_score";
	/** 登录送积分： 0： 关闭；1： 开启 */
	final public static String LOGIN_SCORE = "login_score";
	/** 登录送 score_login 积分 */
	final public static String SCORE_LOGIN = "score_login";
	/** 签到送积分： json字符数据，包括开关和数据 */
	final public static String SIGN_IN_SCORE = "sign_in_score";
	/** 模板页面id */
	final public static String SCORE_PAGE_ID = "score_page_id";
	/** 积分说明 */
	final public static String SCORE_DOCUMENT = "score_document";
	/** 积分兑换比 */
	final public static String SCORE_PROPORTION = "score_proportion";
    /** 订单折后金额是否包含运费  */
	public static final String DISCOUNT_HAS_SHIPPING="discount_has_shipping";
	
	//-------------------------------------------------------
	
	public void setScoreLimit(Byte value){
		Assert.isTrue(value==(byte)0 || value==(byte)1 || value == (byte)2,"积分有效期类型错误");
		set(SCORE_LIMIT, value,Byte.class);
	}
	
	public Byte getScoreLimit(){
		return get(SCORE_LIMIT,Byte.class,BYTE_ZERO);
	}
	
	public void setScoreDay(Integer value){
		set(SCORE_DAY, value,Integer.class);
	}
	public Integer getScoreDay(){
		return get(SCORE_DAY,Integer.class,1);
	}
	
	public void setScoreMonth(Integer value){
		set(SCORE_MONTH, value,Integer.class);
	}
	public Integer getScoreMonth(){
		return get(SCORE_MONTH,Integer.class,1);
	}
	
	public void setScoreYear(Integer value){
		set(SCORE_YEAR, value,Integer.class);
	}
	public Integer getScoreYear(){
		return get(SCORE_YEAR,Integer.class,1);
	}
	
	public void setScoreLimitNumber(Integer value){
		set(SCORE_LIMIT_NUMBER, value,Integer.class);
	}
	public Integer getScoreLimitNumber(){
		return get(SCORE_LIMIT_NUMBER,Integer.class,0);
	}
	
	public void setScorePeriod(Integer value){
		set(SCORE_PERIOD, value,Integer.class);
	}
	public Integer getScorePeriod(){
		return get(SCORE_PERIOD,Integer.class,1);
	}
	
	public void setScorePayLimit(Byte value){
		Assert.isTrue(value==(byte)0 || value==(byte)1,"积分支付限制类型错误");
		set(SCORE_PAY_LIMIT, value,Byte.class);
	}
	
	public Byte getScorePayLimit(){
		return get(SCORE_PAY_LIMIT,Byte.class,BYTE_ZERO);
	}

	public void setScorePayNum(Integer value){
		set(SCORE_PAY_NUM, value,Integer.class);
	}
	public Integer getScorePayNum(){
		return get(SCORE_PAY_NUM,Integer.class,100);
	}
	
	public void setScoreDiscountRatio(Integer value) {
		if(value==null) {
			// 默认值50
			set(SCORE_DISCOUNT_RATIO,String.valueOf(DEFAULT_SCORE_DISCOUNT_RATIO));
		}else {
			set(SCORE_DISCOUNT_RATIO,value,Integer.class);
		}
	}
	
	public Integer getScoreDiscountRatio() {
		return get(SCORE_DISCOUNT_RATIO,Integer.class,DEFAULT_SCORE_DISCOUNT_RATIO);
	}
	
	public void setShoppingScore(Byte value){
		Assert.isTrue(value==(byte)0 || value==(byte)1 ,"购物送积分开关错误");
		set(SHOPPING_SCORE, value,Byte.class);
	}
	public Byte getShoppingScore(){
		return get(SHOPPING_SCORE,Byte.class,BYTE_ZERO);
	}
	
	public void setScoreType(Byte value){
		Assert.isTrue(value==(byte)0 || value==(byte)1,"购物送积分类型错误");
		set(SCORE_TYPE, value,Byte.class);
	}
	
	public Byte getScoreType(){
		return get(SCORE_TYPE,Byte.class,BYTE_ZERO);
	}
	
	public void setStoreScore(Byte value){
		Assert.isTrue(value==(byte)0 || value==(byte)1 ,"门店买单送积分开关类型错误");
		set(STORE_SCORE, value,Byte.class);
	}
	public Byte getStoreScore(){
		return get(STORE_SCORE,Byte.class,BYTE_ZERO);
	}
	
	public void setLoginScore(Byte value){
		Assert.isTrue(value==(byte)0 || value==(byte)1 ,"登录送积分开关类型错误");
		set(LOGIN_SCORE, value,Byte.class);
	}
	
	public Byte getLoginScore(){
		return get(LOGIN_SCORE,Byte.class,BYTE_ZERO);
	}
	
	public void setScoreLogin(Integer value){
		set(SCORE_LOGIN, value,Integer.class);
	}
	
	public Integer getScoreLogin(){
		return get(SCORE_LOGIN,Integer.class,0);
	}
	
	public void setSignInScore(UserScoreSetValue obj){
		if(obj == null) {
			obj = new UserScoreSetValue(BYTE_ZERO,new String[0],BYTE_ZERO); 
		}
		String json = Util.toJson(obj);
		set(SIGN_IN_SCORE, json,UserScoreSetValue.class);
	}
	
	public UserScoreSetValue getSignInScore(){
		UserScoreSetValue obj = null;
		String value = get(SIGN_IN_SCORE);
		if(value==null) {
			obj = new UserScoreSetValue(BYTE_ZERO,new String[0],BYTE_ZERO); 
		}else {
			obj = Util.parseJson(value,UserScoreSetValue.class);
			Byte signInRules2 = obj.getSignInRules();
			signInRules2 = null == signInRules2 ? (byte) 0 : signInRules2;
			obj.setSignInRules(signInRules2);
		}
		return obj;
	}
	
	public void setScorePageId(String value){
		set(SCORE_PAGE_ID, value);
	}
	public String getScorePageId(){
		return get(SCORE_PAGE_ID);
	}
	
	public void setScoreDocument(String value){
		set(SCORE_DOCUMENT, value);
	}
	public String getScoreDocument(){
		return get(SCORE_DOCUMENT);
	}
	
	public void setScoreProportion(Integer value) {
		if(SCORE_PROPORTION_LIST.contains(value)) {
			set(SCORE_PROPORTION,value,Integer.class);
		}
	}

	public Integer getScoreProportion() {
		return get(SCORE_PROPORTION,Integer.class,SCORE_PROPORTION_LIST.get(2));
	}
	
	
	public void setDiscount(String value) {
		set(DISCOUNT_HAS_SHIPPING, value);
	}

	public String getDiscount() {
		return get(DISCOUNT_HAS_SHIPPING,String.class,"1");
	}
	
}
