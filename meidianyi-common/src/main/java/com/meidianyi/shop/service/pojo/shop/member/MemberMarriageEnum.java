package com.meidianyi.shop.service.pojo.shop.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.pojo.shop.member.data.MarriageData;

/**
* @author 黄壮壮
* @Date: 2019年8月19日
* @Description: 会员婚姻状况-枚举类
*/
public enum MemberMarriageEnum {
	
	/**未婚 */
	UNMARRIED(1,"member.marriage.unmarried"),
	/** 已婚  */
	MARRIED(2,"member.marriage.married"),
	/** 保密*/
	CONFIDENTIALITY(3,"member.marriage.confidentiality");
	/** 数字代号 */
	private Integer code;
	/**受教育程度*/
	private String name;
	private static Map<Integer,MemberMarriageEnum> map = new HashMap<>();
	static {
		for(MemberMarriageEnum e: MemberMarriageEnum.values()) {
			map.put(e.code,e);
		}
	}
	
	private MemberMarriageEnum(int code,String name) {
		this.code = code;
		this.name = name;
	}
	
	/** 根据code Id获取枚举类 */
	public static MemberMarriageEnum valueOf(int code) {
		return (MemberMarriageEnum)map.get(code);
	}
	/**
	 * 根据 code id 获取相应的name
	 * @return
	 */
	public static String getNameByCode(int code) {
		MemberMarriageEnum obj = valueOf(code);
		return obj.getName();
	}

    /**
     * 自定义语言  常乐
     * @param code 教育程度数字代号
     * @param lang 语言
     * @return
     */
    public static String getNameByCode(int code,String lang) {
        MemberMarriageEnum obj = valueOf(code);
        return Util.translateMessage(lang, obj.getName(),"","member");
    }
	
	public int getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}
	
	
	/**
	 * 获取婚姻状况 
	 * 返回例子： [{value=1, label=未婚}]
	 * @param lang 语言 为null时，为系统默认语言
	 * @return List<MarriageData> 婚姻状况列表
	 */
	public static List<MarriageData> getAllMarriageWithCode(String lang){
		List<MarriageData> data = new ArrayList<>();
		for(MemberMarriageEnum e: MemberMarriageEnum.values()) {
			String name = getNameByCode(e.getCode(),lang);
			data.add(new MarriageData(e.code,name));
		}
		return data;
	}
	
	public static void main(String[] args) {
		List<MarriageData> list = getAllMarriageWithCode(null);
		list.stream().forEach(System.out::println);
	}
	
	
	
	/**
	 * 获取所有教育名称
	 * @param lang 语言
	 * @param choose true 加入"请选择"，false，没有此项
	 */
	public static List<String> getAllEducation(String lang,boolean choose){
		List<String> eduList = new ArrayList<String>(); 
		if(choose) {
			// 请选择
			eduList.add(Util.translateMessage(lang,"member.please.choose","","member"));
		}
		int length = MemberMarriageEnum.values().length;
		for(int i=0;i<length;i++) {
			eduList.add(MemberMarriageEnum.getNameByCode(i,lang));
		}
		return eduList;
	}
	
	public static List<String> getAllEducation(String lang){
		return getAllEducation(lang,false);
	}
	
	public static String[] getArrayMarriage(String lang) {
		MemberMarriageEnum[] values = MemberMarriageEnum.values();
		String[] result=new String[values.length];
		for (int i = 0; i < values.length; i++) {
			result[i]=MemberMarriageEnum.getNameByCode(i+1,lang);
		}
		return result;
	}
	
	/**
	 * 根据名字和语言找id
	 * @param name
	 * @param lang
	 * @return
	 */
	public static Integer getByName(String name, String lang) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		int length = MemberMarriageEnum.values().length;
		for (int i = 1; i <= length; i++) {
			map.put(MemberMarriageEnum.getNameByCode(i, lang), i);
		}
		Integer integer = map.get(name);
		if (integer != null) {
			return integer;
		}
		return null;
	}
}
