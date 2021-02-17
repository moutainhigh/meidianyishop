package com.meidianyi.shop.service.pojo.shop.member.account;

import java.math.BigDecimal;
import lombok.Data;
/**
 * 
 * @author 黄壮壮
 *	增加减少会员卡余额次数
 */
@Data
public class CardConsumeParam {
	private Integer userId;
	private Integer cardId;
	private Byte cardType;
	/**	备注 */
	private String message;
	/**	修改类型	*/
	private Byte type;
	/**	卡号	 */
	private String cardNo;
	/**	当前次数	*/
	private Integer countDis;
	/**	当前卡余额 	 */
	private BigDecimal moneyDis;
	/**	变动值（余额，次数）	 */
	private BigDecimal reduce;
}
