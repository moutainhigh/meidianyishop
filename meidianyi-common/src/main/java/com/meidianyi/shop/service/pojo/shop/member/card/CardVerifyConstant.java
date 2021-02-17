package com.meidianyi.shop.service.pojo.shop.member.card;
/**
* @author 黄壮壮
* @Date: 2019年11月5日
* @Description: card_examine表涉及的状态
*/
public class CardVerifyConstant {
	/** -审核状态： 审核中 */
	public final static Byte VSTAT_CHECKING = 1;
	/** -审核状态： 通过 */
	public final static Byte VSTAT_PASS = 2;
	/** -审核状态： 拒绝 */
	public final static Byte VSTAT_REFUSED = 3;
	
	/** - 0： 没有删除 */
	public final static Byte VDF_NO = 0;
	/** - 1： 已经删除 */
	public final static Byte VDF_YES = 1;
	
	public final static String PROVINCE_NAME = "provinceName";
	public final static String CITY_NAME = "cityName";
	public final static String DISTRICT_NAME = "districtName";
	/** -有此条件 */
	public final static Byte  HAS_CONDITION = 1;
	/** -无此条件 */
	public final static Byte NO_CONDITION = 0;
}
