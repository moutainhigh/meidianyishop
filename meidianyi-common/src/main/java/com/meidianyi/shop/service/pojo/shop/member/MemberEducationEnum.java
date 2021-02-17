package com.meidianyi.shop.service.pojo.shop.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.pojo.shop.member.data.EducationVo;

/**
* @author 黄壮壮
* @Date: 2019年8月19日
* @Description: 会员受教育程度-枚举类
*/
public enum MemberEducationEnum {
	
	/** 初中 */
	JUNIOR(1,"member.education.junior"),
	/** 高中 */
	HIGH(2,"member.education.high"),
	/** 中专 */
	SECONDARY(3,"member.education.secondary"),
	/** 大专 */
	COLLEGE(4,"member.education.college"),
	/** 本科 */
	UNDERGRADUATE(5,"member.education.undergraduate"),
	/** 硕士 */
	MASTER(6,"member.education.master"),
	/** 博士 */
	DOCTOR(7,"member.education.doctor"),
	/** 其他 */
	OTHER(8,"member.education.other");
	/** 数字代号 */
	private Integer code;
	/**受教育程度*/
	private String name;
	private static Map<Integer,MemberEducationEnum> map = new HashMap<>();
	static {
		for(MemberEducationEnum e: MemberEducationEnum.values()) {
			map.put(e.code,e);
		}
	}
	
	private MemberEducationEnum(int code,String name) {
		this.code = code;
		this.name = name;
	}
	
	/** 根据code Id获取枚举类 */
	public static MemberEducationEnum valueOf(int code) {
		return (MemberEducationEnum)map.get(code);
	}
	/**
	 * 根据 code id 获取相应的name
	 * @return
	 */
	public static String getNameByCode(Integer code) {
		if(code == null) {
			return null;
		}
		MemberEducationEnum obj = valueOf(code);
		return obj.getName();
	}

    /**
     * 自定义语言  常乐
     * @param code 教育程度数字代号
     * @param lang 语言
     * @return 
     */
    public static String getNameByCode(int code,String lang) {
        MemberEducationEnum obj = valueOf(code);
        return Util.translateMessage(lang, obj.getName(),"","member");
    }
	
	public int getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}
	/**
	 * 获取所有教育名称 如： ["初中","高中"]
	 * @param lang 语言
	 * @param choose true 加入"请选择"，false，没有此项
	 * @return List<String>
	 */
	public static List<String> getAllEducation(String lang,boolean choose){
		List<String> eduList = new ArrayList<String>(); 
		if(choose) {
			// 请选择
			eduList.add(Util.translateMessage(lang,"member.please.choose","","member"));
		}
		int length = MemberEducationEnum.values().length;
		for(int i=1;i<=length;i++) {
			eduList.add(MemberEducationEnum.getNameByCode(i,lang));
		}
		return eduList;
	}
	

	/**
	 * 获取所有教育名称，默认不带"请选择选项"
	 * @param lang 语言
	 * @return
	 */
	public static List<String> getAllEducation(String lang){
		return getAllEducation(lang,false);
	}

	/**
	 * 获取所有教育名称 如： [{value: 0,label: "初中"},{value: 1,label: "高中"}]
	 * @param lang
	 * @return List<EducationVo>
	 */
	public static List<EducationVo> getAllEducationWithCode(String lang){
		List<EducationVo> eduList = new ArrayList<>();
		for(MemberEducationEnum item: MemberEducationEnum.values()) {
			String name = getNameByCode(item.getCode(),lang);
			eduList.add(new EducationVo(item.code,name));
		}
		return eduList;
	}
	
	public static String[] getArrayEduction(String lang) {
		MemberEducationEnum[] values = MemberEducationEnum.values();
		String[] result=new String[values.length];
		for (int i = 0; i < values.length; i++) {
			result[i]=MemberEducationEnum.getNameByCode(i+1,lang);
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
		int length = MemberEducationEnum.values().length;
		for (int i = 1; i <=length; i++) {
			map.put(MemberEducationEnum.getNameByCode(i, lang), i);
		}
		Integer integer = map.get(name);
		if (integer != null) {
			return integer;
		}
		return null;
	}
}
