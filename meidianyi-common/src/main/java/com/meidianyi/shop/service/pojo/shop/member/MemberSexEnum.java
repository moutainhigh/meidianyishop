package com.meidianyi.shop.service.pojo.shop.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.meidianyi.shop.common.foundation.util.Util;

/**
 * 性别
 * @author zhaojianqiang
 * @time   上午10:27:14
 */
public enum MemberSexEnum {
	
	/** 男性 */
	MALE(0,"member.sexs.male"),
	/** 女性 */
	FEMALE(1,"member.sexs.female");
	/** 数字代号 */
	private Integer code;
	/**受教育程度*/
	private String name;
	private static Map<Integer,MemberSexEnum> map = new HashMap<>();
	static {
		for(MemberSexEnum e: MemberSexEnum.values()) {
			map.put(e.code,e);
		}
	}
	
	private MemberSexEnum(int code,String name) {
		this.code = code;
		this.name = name;
	}
	
	/** 根据code Id获取枚举类 */
	public static MemberSexEnum valueOf(int code) {
		return (MemberSexEnum)map.get(code);
	}
	/**
	 * 根据 code id 获取相应的name
	 * @return
	 */
	public static String getNameByCode(int code) {
		MemberSexEnum obj = valueOf(code);
		return obj.getName();
	}

    /**
     * 自定义语言  常乐
     * @param code 教育程度数字代号
     * @param lang 语言
     * @return
     */
    public static String getNameByCode(int code,String lang) {
        MemberSexEnum obj = valueOf(code);
        return Util.translateMessage(lang, obj.getName(),"","member");
    }
	
	public int getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}
	/**
	 * 获取所有性别名称
	 * @param lang 语言
	 * @param choose true 加入"请选择"，false，没有此项
	 */
	public static List<String> getAllSex(String lang,boolean choose){
		List<String> eduList = new ArrayList<String>(); 
		if(choose) {
			// 请选择
			eduList.add(Util.translateMessage(lang,"member.please.choose","","member"));
		}
		int length = MemberSexEnum.values().length;
		for(int i=0;i<length;i++) {
			eduList.add(MemberSexEnum.getNameByCode(i,lang));
		}
		return eduList;
	}
	
	public static List<String> getAllSex(String lang){
		return getAllSex(lang,false);
	}
	
	public static String[] getArraySexs(String lang) {
		MemberSexEnum[] values = MemberSexEnum.values();
		String[] result=new String[values.length];
		for (int i = 0; i < values.length; i++) {
			result[i]=MemberSexEnum.getNameByCode(i,lang);
		}
		return result;
	}
	
	/**
	 * 根据名字和语言找id
	 * @param name
	 * @param lang
	 * @return
	 */
	public static String getByName(String name, String lang) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		int length = MemberSexEnum.values().length;
		for (int i = 0; i < length; i++) {
			map.put(MemberSexEnum.getNameByCode(i, lang), i);
		}
		Integer integer = map.get(name);
		if (integer != null) {
			if (integer == 0) {
				return "m";
			} else {
				return "f";
			}
		}
		return null;
	}
}
