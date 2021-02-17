package com.meidianyi.shop.service.pojo.shop.market.message.maconfig;

/**
 * 小程序消息的rule对应字段
 * 
 * @author zhaojianqiang 2020年4月9日上午11:20:04
 */

public final class RuleKey {
	/** 事物 20个以内字符 可汉字、数字、字母或符号组合 */
	public static final String THING = "thing";
	/** 数字 32位以内数字 只能数字，可带小数 */
	public static final String NUMBER = "number";
	/** 字母 32位以内字母 只能字母 */
	public static final String LETTER = "letter";
	/** 符号 5位以内符号 只能符号 */
	public static final String SYMBOL = "symbol";
	/** 字符串 32位以内数字、字母或符号 可数字、字母或符号组合 */
	public static final String CHARACTER_STRING = "character_string";
	/** 时间 24小时制时间格式（支持+年月日） 例如：15:01，或：2019年10月1日 15:01 */
	public static final String TIME = "time";
	/** 日期 年月日格式（支持+24小时制时间） 例如：2019年10月1日，或：2019年10月1日 15:01 */
	public static final String DATE = "date";
	/** 金额 1个币种符号+10位以内纯数字，可带小数，结尾可带“元” 可带小数 */
	public static final String AMOUNT = "amount";
	/** 电话 17位以内，数字、符号 电话号码，例：+86-0766-66888866 */
	public static final String PHONE_NUMBER = "phone_number";
	/** 车牌 8位以内，第一位与最后一位可为汉字，其余为字母或数字 车牌号码：粤A8Z888挂 */
	public static final String CAR_NUMBER = "car_number";
	/** 姓名 10个以内纯汉字或20个以内纯字母或符号 中文名10个汉字内；纯英文名20个字母内；中文和字母混合按中文名算，10个字内 */
	public static final String NAME = "name";
	/** 汉字 5个以内汉字 5个以内纯汉字，例如：配送中 */
	public static final String PHRASE = "phrase";

	public static final String NUMBER_PATTERN = "([1-9]\\d*\\.?\\d*)|(0\\.\\d*[1-9])";
	public static final String LETTER_PATTERN = "^[a-zA-Z]+$";
	public static final String SYMBOL_CHARACTERSTR = "~!@#$%^&*()_+`-=/*\\':'\"\'<>,.?/";
	public static final String CHARACTER_STRING_PATTERN = ".*[\\u4e00-\\u9fa5]+.*$";
	public static final String PHONE_NUMBER_PATTERN = "[a-zA-Z0-9\\u4E00-\\u9FA5]*";
	public static final String CAR_NUMBER_PATTERN = "([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼]{1}(([A-HJ-Z]{1}[A-HJ-NP-Z0-9]{5})|([A-HJ-Z]{1}(([DF]{1}[A-HJ-NP-Z0-9]{1}[0-9]{4})|([0-9]{5}[DF]{1})))|([A-HJ-Z]{1}[A-D0-9]{1}[0-9]{3}警)))|([0-9]{6}使)|((([沪粤川云桂鄂陕蒙藏黑辽渝]{1}A)|鲁B|闽D|蒙E|蒙H)[0-9]{4}领)|(WJ[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼·•]{1}[0-9]{4}[TDSHBXJ0-9]{1})|([VKHBSLJNGCE]{1}[A-DJ-PR-TVY]{1}[0-9]{5})";
	public static final String PHRASE_PATTERN = "^[\\u4e00-\\u9fa5]{0,}$";
	/** 是否有数字*/
	public static final String HAVENUM_PATTERN=".*\\d+.*";
	/** 是否包含英文*/
	public static final String HAVEENGILSH_PATTERN=".*[a-zA-Z]+.*";
}
