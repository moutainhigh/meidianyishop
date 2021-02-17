package com.meidianyi.shop.service.pojo.shop.member.card.dao;


import com.meidianyi.shop.db.shop.tables.records.MemberCardRecord;
import com.meidianyi.shop.db.shop.tables.records.UserCardRecord;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author 黄壮壮
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardFullDetail {
	private Integer userId;
	private Integer cardId;
	private String cardNo;
	private MemberCardRecord memberCard;
	private UserCardRecord userCard;
}
