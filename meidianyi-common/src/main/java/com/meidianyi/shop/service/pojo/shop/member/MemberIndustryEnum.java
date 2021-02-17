package com.meidianyi.shop.service.pojo.shop.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.pojo.shop.member.data.IndustryVo;

/**
* @author 黄壮壮
* @Date: 2019年9月12日
* @Description: 行业信息
*/
public enum MemberIndustryEnum {
	
	/** 计算机硬件及网络设备 */
	HARDWARE(1,"member.industry.hardware"),
	/** 计算机软件  */
	SOFTWARE(2,"member.industry.software"),
	/** IT服务（系统/数据/维护）/多领域经营 */
	SERVICE(3,"member.industry.it.service"),
	/** 互联网/电子商务 */
	COMMERCE(4,"member.industry.internet.commerce"),
	/** 网络游戏 */
	GAME(5,"member.industry.online.game"),
	
	/** 通讯（设备/运营/增值服务） */
	COMMUNICATION(6,"member.industry.communication"),
	/** 电子技术/半导体/集成电路 */
	ELECTRONIC_TECHNOLOGY(7,"member.industry.electronic.technology"),
	/** 仪器仪表及工业自动化 */
	INSTRUMENT(8,"member.industry.instrument"),
	/** 金融/银行/投资/基金/证券 */
	FINANCE(9,"member.industry.finance"),
	/** 保险 */
	INSURANCE(10,"member.industry.insurance"),
	
	/** 房地产/建筑/建材/工程 */
	ESTATE(11,"member.industry.estate"),
	/** 家居/室内设计/装饰装潢 */
	HOME(12,"member.industry.home"),
	/** 物业管理/商业中心 */
	BUSINESS(13,"member.industry.business"),
	/** 广告/会展/公关/市场推广 */
	ADVERTISEMENT(14,"member.industry.advertisement"),
	/** 媒体/出版/影视/文化/艺术 */
	MEDIA(15,"member.industry.media"),
	
	/** 印刷/包装/造纸 */
	PRINT(16,"member.industry.print"),
	/** 咨询/管理产业/法律/财会 */
	ADVISORY(17,"member.industry.advisory"),
	/** 教育/培训 */
	EDUCATION(18,"member.industry.education"),
	/** 检验/检测/认证 */
	DETECTION(19,"member.industry.detection"),
	/** 中介服务 */
	MEDIUM(20,"member.industry.medium"),
	
	/** 贸易/进出口 */
	TRADING(21,"member.industry.trading"),
	/** 零售/批发 */
	RETAIL(22,"member.industry.retail"),
	/** 快速消费品（食品/饮料/烟酒/化妆品  */
	FAST_CONSUMPTION(23,"member.industry.fast.consumption"),
	/** 耐用消费品（服装服饰/纺织/皮革/家具/家电） */
	DURABLE_CONSUMPTION(24,"member.industry.durable.consumption"),
	/** 办公用品及设备 */
	OFFICE_DEVICE(25,"member.industry.office.device"),
	
	/** 礼品/玩具/工艺美术/收藏品 */
	GIFT(26,"member.industry.gift"),
	/** 大型设备/机电设备/重工业 */
	INDUSTRY(27,"member.industry.heavy.industry"),
	/** 加工制造（原料加工/模具） */
	MANUFACTURE(28,"member.industry.manufacture"),
	/** 汽车/摩托车（制造/维护/配件/销售/服务） */
	CAR(29,"member.industry.car"),
	/** 交通/运输/物流 */
	TRAFFIC(30,"member.industry.traffic"),
	
	/** 医药/生物工程 */
	MEDICINE(31,"member.industry.medicine"),
	/** 医疗/护理/美容/保健 */
	NURSING(32,"member.industry.nursing"),
	/** 医疗设备/器械 */
	MEDICAL_DEVICE(33,"member.industry.medical.device"),
	/** 酒店/餐饮 */
	HOTEL(34,"member.industry.hotel"),
	/** 娱乐/体育/休闲 */
	ENTERTAINMENT(35,"member.industry.entertainment"),
	
	/** 旅游/度假 */
	TRAVEL(36,"member.industry.travel"),
	/**石油/石化/化工 */
	OIL(37,"member.industry.oil"),
	/** 能源/矿产/采掘/冶炼 */
	ENERGY(38,"member.industry.energy"),
	/** 电气/电力/水利 */
	ELECTRIC_POWER(39,"member.industry.electric.power"),
	/** 航空/航天 */
	AVIATION(40,"member.industry.aviation"),
	
	/** 学术/科研 */
	ACADEMIC(41,"member.industry.academic"),
	/** 政府/公共事业/非盈利机构 */
	GOVERNMENT(42,"member.industry.government"),
	/** 环保 */
	ENVIRONMENTAL_PROTECTION(43,"member.industry.environmental.protection"),
	/** 农/林/牧/渔 */
	AGRICULTURE(44,"member.industry.agriculture"),
	/** 跨领域经营 */
	CROSS_DOMAIN_MANAGEMENT(45,"member.industry.cross.domain.management"),
	
	/** 其他 */
	OTHER(46,"member.industry.other");
	/** 数字代号 */
	private int code;
	/**受教育程度*/
	private String name;
	private static Map<Integer,MemberIndustryEnum> map = new HashMap<>();
	static {
		for(MemberIndustryEnum item: MemberIndustryEnum.values()) {
			map.put(item.code, item);
		}
	}
	private MemberIndustryEnum(int code,String name) {
		this.code = code;
		this.name = name;
	}
	
	public static String getNameByCode(Integer code) {
		if(code == null) {
			return null;
		}
		MemberIndustryEnum memberIndustryEnum = map.get(code);
		return memberIndustryEnum.getName();
	}

    /**
     * 自定义语言  常乐
     * @param code 行业数字代号
     * @param lang 语言
     * @return
     */
    public static String getNameByCode(int code,String lang) {
        return Util.translateMessage(lang,map.get(code).getName(),"","member");
    }
	
	public static MemberIndustryEnum valueOf(int code) {
		return map.get(code);
	}
	
	public int getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}
	
	
	public static void main(String ...args) {
		List<IndustryVo> allIndustryInfo = getAllIndustryInfo();
		allIndustryInfo.stream().forEach(System.out::println);
	}
	
	/** 
	 * 获取行业所有信息
	 * @return List<IndustryVo>
	 */
	public static List<IndustryVo> getAllIndustryInfo() {
		return getAllIndustryInfo(null);
	}
	
	/**
	 * 获取行业所有信息，指定语言版本
	 * @param lang
	 * @return List<IndustryVo>
	 */
	public static List<IndustryVo> getAllIndustryInfo(String lang){
		List<IndustryVo> industryList = new ArrayList<>();
		for(MemberIndustryEnum item: MemberIndustryEnum.values()) {
			String name = getNameByCode(item.code,lang);
			industryList.add(new IndustryVo(item.code,name));
		}
		return industryList;
	}
	
	
	public static List<String> getAllIndustryName(String lang){
		return getAllIndustryName(lang,false);
	}
	
	
	public static List<String> getAllIndustryName(String lang,boolean choose){
		List<String> res = new ArrayList<String>();
		if(choose) {
			// 请选择
			res.add(Util.translateMessage(lang,"member.please.choose","","member"));
		}
		for(int i=1;i<=MemberIndustryEnum.values().length;i++) {
			res.add(getNameByCode(i,lang));
		}
		return res;
	}
	
	public static String[] getArrayIndustryInfo(String lang) {
		MemberIndustryEnum[] values = MemberIndustryEnum.values();
		String[] result=new String[values.length];
		for (int i = 0; i < values.length; i++) {
			result[i]=getNameByCode(i+1, lang);
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
		int length = MemberIndustryEnum.values().length;
		for (int i = 1; i <=length; i++) {
			map.put(MemberIndustryEnum.getNameByCode(i, lang), i);
		}
		Integer integer = map.get(name);
		if (integer != null) {
			return integer;
		}
		return null;
	}
}
