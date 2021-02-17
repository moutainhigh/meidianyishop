package com.meidianyi.shop.service.pojo.wxapp.goods.groupdraw;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhaojianqiang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupJoinDetailVo {
	private Byte groupStatus;
	private Integer surplusGroupNum;
	private GroupDrawBotton button;
	private DrawUser drawUser;
	private List<GroupDrawList> userList;
}
