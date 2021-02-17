package com.meidianyi.shop.service.pojo.shop.market.bargain;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 帮砍价用户
 * @author 王兵兵
 *
 * 2019年7月26日
 */
@Data
public class BargainUserListQueryVo {

	private Integer id;
    private Integer userId;
	private String username;
	private String mobile;
	private Timestamp createTime;
	private BigDecimal bargainMoney;
}
