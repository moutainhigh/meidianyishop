package com.meidianyi.shop.service.pojo.shop.member.account;
import lombok.Data;
/**
 * @author huangzhuangzhuang
 */
@Data
public class CardReceiveVo {
	private Boolean isMostGrade;
	private String cardNo;
	private String isContinue;
	private GradeCardData gradeCard;
}
