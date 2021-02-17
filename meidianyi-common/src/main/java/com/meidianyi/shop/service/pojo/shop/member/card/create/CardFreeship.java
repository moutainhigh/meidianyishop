package com.meidianyi.shop.service.pojo.shop.member.card.create;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.Util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * 	卡的包邮策略信息
 * @author 黄壮壮
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardFreeship {
	/**
	 *	 包邮类型
	 */
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public static enum shipType{
		/**
		 * 	不包邮
		 */
		SHIP_NOT_AVAIL((byte)-1,JsonResultMessage.CARD_SHIP_NOT_AVAIL),
		
		/**
		 * 	不限制
		 */
		 SHIP_VIP((byte)0,JsonResultMessage.CARD_SHIP_VIP),
		 
		 /**
		 *	 持卡有效期内
		 */
		 SHIP_IN_EFFECTTIME((byte)1,JsonResultMessage.CARD_SHIP_IN_EFFECTTIME),
		 
		/**
		 * 	包邮类型为年
		 */
		 SHIP_YEAR((byte)2,JsonResultMessage.CARD_SHIP_YEAR),
		 
		/**
		 * 	包邮类型为季
		 */
		SHIP_SEASON((byte)3,JsonResultMessage.CARD_SHIP_SEASON),
		
		/**
		 *	 包邮类型为月
		 */
		 SHIP_MONTH((byte)4,JsonResultMessage.CARD_SHIP_MONTH),
		
		/**
		 * 	包邮类型为周
		 */
		 SHIP_WEEK((byte)5,JsonResultMessage.CARD_SHIP_WEEK),
		 
		 /**
		  *	 包邮类型为日
		  */
		 SHIP_DAY((byte)6,JsonResultMessage.CARD_SHIP_DAY);
		private byte type;
		private String desc;
		shipType(byte type,String desc) {
			this.type = type;
			this.desc = desc;
		}
		public String getDesc() {
			return desc;
		}
		
		public byte getType() {
			return this.type;
		}

	}
	
	/**
	 * 	一个周期内允许适用的最大包邮次数
	 */
	private Integer num;
	/**
	 * 	一个周期内剩余的包邮次数
	 */
	private Integer remainNum;
	
	/**
	 * 	包邮的类型
	 */
	private Byte type;
	
	/**
	 * 	包邮说明
	 */
	private String desc;
	
	
	/**
	 * 	包邮使用类型
	 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private static final Map<String,List<String>> LANG_MAP = new HashMap<>();
	private static String currentLang = null;

	/**
	 * 
	 * @param lang 语言类型，默认为中文
	 * @return 返回包邮可使用的类型，[无限包邮, 有效期内, 今年, 本季度, 本月, 本周, 今日]
	 */
	public static List<String> getFreeShipDesc(String lang) {
		if(LANG_MAP.get(lang)==null || LANG_MAP.get(lang).size()==0) {
			List<String> list = new ArrayList<>();
			for(shipType e: shipType.values()) {
				if(e.type>-1) {
					String res = Util.translateMessage(lang, e.getDesc(),"","messages");
					list.add(res);
				}
			}
			LANG_MAP.put(lang, list);
		}
		
		return LANG_MAP.get(lang).stream().collect(Collectors.toList());
	}
}
