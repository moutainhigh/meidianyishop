package com.meidianyi.shop.service.pojo.shop.member.card.create;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import java.util.List;

/**
 * 	卡自定义权益
 * @author 黄壮壮
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardCustomRights {
	/**
	 * 	自定义权限开关枚举
	 */
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public enum RightSwitch{
		off,on
	}
	
	/**
	 * 	自定义权限开关
	 */
	private RightSwitch customRightsFlag;
	
	/**
	 * 	自定义权限
	 */
	private List<CardRight> customRightsAll;
}
