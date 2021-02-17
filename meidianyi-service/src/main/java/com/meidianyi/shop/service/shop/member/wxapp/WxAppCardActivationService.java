package com.meidianyi.shop.service.shop.member.wxapp;

import com.fasterxml.jackson.databind.node.NullNode;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.RegexUtil;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.main.tables.records.DictCityRecord;
import com.meidianyi.shop.db.main.tables.records.DictDistrictRecord;
import com.meidianyi.shop.db.main.tables.records.DictProvinceRecord;
import com.meidianyi.shop.db.shop.tables.records.CardExamineRecord;
import com.meidianyi.shop.db.shop.tables.records.MemberCardRecord;
import com.meidianyi.shop.db.shop.tables.records.UserDetailRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.foundation.util.CardUtil;
import com.meidianyi.shop.service.pojo.shop.member.MemberEducationEnum;
import com.meidianyi.shop.service.pojo.shop.member.MemberIndustryEnum;
import com.meidianyi.shop.service.pojo.shop.member.account.UserCardVo;
import com.meidianyi.shop.service.pojo.shop.member.card.CardVerifyConstant;
import com.meidianyi.shop.service.pojo.shop.member.card.create.CardCustomAction;
import com.meidianyi.shop.service.pojo.shop.member.exception.CardActivateException;
import com.meidianyi.shop.service.pojo.shop.member.ucard.ActivateCardParam;
import com.meidianyi.shop.service.pojo.shop.member.ucard.ActivateCardVo;
import com.meidianyi.shop.service.pojo.wxapp.account.UserInfo;
import com.meidianyi.shop.service.pojo.wxapp.card.param.CardCustomActionParam;
import com.meidianyi.shop.service.pojo.wxapp.card.vo.CardCustomActionVo;
import com.meidianyi.shop.service.shop.card.msg.CardMsgNoticeService;
import com.meidianyi.shop.service.shop.card.wxapp.WxCardDetailService;
import com.meidianyi.shop.service.shop.member.CardVerifyService;
import com.meidianyi.shop.service.shop.member.MemberCardService;
import com.meidianyi.shop.service.shop.member.MemberService;
import com.meidianyi.shop.service.shop.member.UserCardService;
import com.meidianyi.shop.service.shop.user.user.UserService;
import org.apache.commons.beanutils.PropertyUtils;
import org.jooq.tools.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.meidianyi.shop.db.shop.Tables.CARD_EXAMINE;
/**
 * @author 黄壮壮
 * 	激活会员卡服务
 */
@Service
public class WxAppCardActivationService extends ShopBaseService {
	@Autowired
	private UserCardService userCardService;
	@Autowired
	private CardVerifyService cardVerifyService;
	@Autowired
	private UserService userService;
	@Autowired
	private MemberService memberService;
	@Autowired
	private MemberCardService memberCardService;
	@Autowired
	private WxCardDetailService wxCardDetailSvc;
	@Autowired
	private CardMsgNoticeService cardMsgNoticeSvc;
	
	public final static String PROVINCE_CODE = "provinceCode";
	public final static String CITY_CODE = "cityCode";
	public final static String DISTRICT_CODE = "districtCode";

	/**  100000 110000 110100在省，市，区中都对应无效值 **/

	final static Integer DEFAULT_PROVINCEID = 100000;
	final static Integer DEFAULT_CITYID = 110000;
	final static Integer DEFAULT_DISTRICTID = 110100;
	
	private static final Map<Integer,DictProvinceRecord> PROVINCE_MAP =new HashMap<>();
	private static final Map<Integer,DictCityRecord> CITY_MAP = new HashMap<>();
	private static final Map<Integer,DictDistrictRecord> DISTRICT_MAP = new HashMap<>();
	
	/**
	 * 	获取会员卡激活数据
	 */
	public ActivateCardVo getActivationCard(ActivateCardParam param,String lang) {
		logger().info("获取会员卡激活信息");
		UserCardVo uCard = userCardService.getUserCardByCardNo(param.getCardNo());
		
		if(uCard == null) {
			return null;
		}
		List<String> fields = cardVerifyService.getActiveRequiredFieldWithHump(uCard.getActivationCfg());
		UserInfo user = userService.getUserInfo(param.getUserId());
		if(user == null) {
			return null;
		}
		Map<String, Object> userMap = filterActiveOption(fields, user);
		dealWithAddressCode(userMap);
		// add username for wxapp front end
		userMap.put("username",user.getUsername());
		userMap.put("invitationCode",user.getInvitationCode());
		fields.add("username");
		fields.add("invitationCode");
		
		
		
		List<String> allEducation = MemberEducationEnum.getAllEducation(lang,true);
		List<String> allIndustryName = MemberIndustryEnum.getAllIndustryName(lang,true);
		
		MemberCardRecord memberCard = memberCardService.getCardDetailByNo(param.getCardNo()).getMemberCard();
		List<CardCustomAction> tmpOptions = wxCardDetailSvc.getNeedActivationCustomOptions(memberCard);
		List<CardCustomActionVo> customOptions = new ArrayList<>();
		for(CardCustomAction t: tmpOptions) {
			try {
				CardCustomActionVo vo = new CardCustomActionVo();
				PropertyUtils.copyProperties(vo, t);
				customOptions.add(vo);
			} catch (Exception e) {
			}
			
		}
		//TODO 订阅消息

		return ActivateCardVo
				.builder()
				.education(allEducation)
				.industryInfo(allIndustryName)
				.data(userMap)
				.fields(fields)
				.customOptions(customOptions)
				.build();
	}
	
	/**
	 * 	过滤激活选项
	 * @param fields 激活的选项
	 * @param obj 包含的数据
	 * @return Map<String,Object> 激活的选项值
	 */
	public Map<String, Object> filterActiveOption(List<String> fields, Object obj) {
		if(obj == null || obj instanceof NullNode) {
			return null;
		}
		Map<String, Object> userMap = Util.convertPojoToMap(obj);
		userMap = changeKeyFromUnderlineToHump(userMap);
		userMap.entrySet().removeIf(e->!fields.contains(e.getKey()));
		return userMap;
	}
	
	public void dealWithAddressCode(Map<String, Object> userMap) {
		logger().info("处理用户地址信息");
		
		Integer provinceId = userMap.get(PROVINCE_CODE)==null? 
				DEFAULT_PROVINCEID:(Integer)userMap.get(PROVINCE_CODE);
		Integer cityId = userMap.get(CITY_CODE)==null?
				DEFAULT_CITYID:(Integer)userMap.get(CITY_CODE);
		Integer districtId = userMap.get(DISTRICT_CODE)==null?
				DEFAULT_DISTRICTID:(Integer)userMap.get(DISTRICT_CODE);	
		
		userMap.put(PROVINCE_CODE, mapProvinceCodeToName(provinceId));
		userMap.put(CITY_CODE,mapCityCodeToName(cityId)); 
		userMap.put(DISTRICT_CODE, mapDistrictCodeToName(districtId));
	}
	
	

	/**
	 * 	设置会员卡激活数据
	 * @throws CardActivateException  激活失败
	 */
	public ActivateCardVo setActivationCard(ActivateCardParam param) throws CardActivateException {
		logger().info("设置会员卡激活信息");
        ActivateCardVo vo = new ActivateCardVo();
		UserCardVo uCard = userCardService.getUserCardByCardNo(param.getCardNo());	
		if(uCard ==null) {
			logger().info("激活失败");
			throw new CardActivateException();
		}
		List<String> fields = cardVerifyService.getActiveRequiredFieldWithHump(uCard.getActivationCfg());
		Map<String, Object> activeData = this.filterActiveOption(fields, param.getActivateOption());
		//	自定义激活项
		setCustomAction(param, activeData);

		if(activeData != null ) {
			// prepare card examine data 
			// setActiveAddressInfo(activeData);
			activeData.put("cardNo",param.getCardNo());
			activeData.put("cardId",uCard.getCardId());
			activeData.put("userId",uCard.getUserId());
		
			
			if(CardUtil.isCardExamine(uCard.getExamine())) {
				activeData.put("status",CardVerifyConstant.VSTAT_CHECKING);
				//  提交审核成功
                vo.setMsg(JsonResultCode.MSG_CARD_EXAMINE_SUBMIT_SUCCESS);
			}else {
				activeData.put("status",CardVerifyConstant.VSTAT_PASS);
				//  激活成功
                vo.setMsg(JsonResultCode.MSG_CARD_EXAMINE_AUTO_SUCCESS);
			}
			Map<String, Object> data = changeKeyFromHumpToUnderline(activeData);
			
			this.transaction(()->{
				
				// update userdetail by activate data
				UserDetailRecord userDetailRecord = new UserDetailRecord();
				userDetailRecord.fromMap(data);
				memberService.updateUserDetail(userDetailRecord);
				
				// update usercard activate time
				if(!CardUtil.isCardExamine(uCard.getExamine())) {
					userCardService.updateActivationTime(param.getCardNo(), null);
					// send coupon
					memberCardService.sendCoupon(uCard.getUserId(), uCard.getCardId());
					
					// 发送消息
					cardMsgNoticeSvc.sendAuditSuccessMsg(param);
				
				}
				// add data into card examine
				CardExamineRecord cardExamineRecord = db().newRecord(CARD_EXAMINE);
				cardExamineRecord.fromMap(data);
				cardExamineRecord.insert();
			});
		}else {
			logger().info("没有传入激活数据,actovateOption=NullNode");
			throw new CardActivateException();
		}
		return vo;
	}

	private void setCustomAction(ActivateCardParam param, Map<String, Object> activeData) {
		List<CardCustomActionParam> customOptions = param.getCustomOptions();
		
		if(customOptions!=null) {
			for(CardCustomActionParam item: customOptions) {
				//	确保文本OptionArr不被存储
				if(CardCustomAction.ActionType.TEXT.val.equals(item.getCustomType())) {
					item.setOptionArr(null);
				}
				//	图片的OptionArr不被存储
				if(CardCustomAction.ActionType.PICTURE.val.equals(item.getCustomType())) {
					item.setOptionArr(null);
					dealWithPictureLinksToRelativePath(item);
				}else {
					item.setPictureLinks(null);
				}
				
				//	图片的路径处理
				
				
			}
			String customOptJson = Util.toJsonNotNull(customOptions);
			activeData.put(CARD_EXAMINE.CUSTOM_OPTIONS.getName(),customOptJson);
		}
	}

	private void dealWithPictureLinksToRelativePath(CardCustomActionParam item) {
		//	将图片路径处理成相对路径进行存储
		String[] pictureLinks = item.getPictureLinks();
		if(pictureLinks!=null && pictureLinks.length>0) {
			for(int i=0;i<pictureLinks.length;i++) {
                String url = pictureLinks[i];
                String url2 = (RegexUtil.getUri(url));
                pictureLinks[i] = url2;

            }
		}
		item.setPictureLinks(pictureLinks);
	}
	
	/**
	 *	 将map的驼峰key转化为下划线形式
	 * @param map
	 * @return
	 */
	private Map<String, Object> changeKeyFromHumpToUnderline(Map<String,Object> map) {
		Map<String,Object> myMap = new HashMap<>();
		map.entrySet().forEach(item->{
			String key = Util.humpToUnderline(item.getKey());
			myMap.put(key,item.getValue());
		});
		return myMap;
	}
	
	/**
	 *	 将map的驼峰key转化为下划线形式
	 * @param map
	 * @return
	 */
	private Map<String, Object> changeKeyFromUnderlineToHump(Map<String,Object> map) {
		Map<String,Object> myMap = new HashMap<>();
		map.entrySet().forEach(item->{
			String key = Util.underlineToHump(item.getKey());
			myMap.put(key,item.getValue());
		});
		return myMap;
	}
	
	
	
	/**
	 *	 设置激活的地址信息
	 */
	private void setActiveAddressInfo(Map<String, Object> activeData) {
		// get and set provinceId
		String provinceName = (String)activeData.get(PROVINCE_CODE);
		Integer provinceId = DEFAULT_PROVINCEID;
		if(!StringUtils.isBlank(provinceName)) {
			provinceId = saas.region.province.getProvinceIdByName(provinceName);
		}
		activeData.put(PROVINCE_CODE,provinceId);
		
		// get and set cityId
		String cityName = (String)activeData.get(CITY_CODE);
		Integer cityId = DEFAULT_CITYID;
		if(!StringUtils.isBlank(cityName)) {
			cityId = saas.region.city.getCityIdByNameAndProvinceId(provinceId, cityName);
		}
		activeData.put(CITY_CODE,cityId);
		
		// get and set districtId
		String districtName = (String)activeData.get(DISTRICT_CODE);
		Integer districtId = DEFAULT_DISTRICTID;
		if(!StringUtils.isBlank(districtName)) {
			districtId = saas.region.district.getDistrictIdByNameAndCityId(cityId, districtName);
		}
		activeData.put(DISTRICT_CODE,districtId);
	}
	/**
	 * 处理城市码到名称
	 * @param cityId 城市代号码
	 * @return 城市名称
	 */
	public String mapCityCodeToName(Integer cityId) {
		if(cityId==null) {
			cityId = DEFAULT_CITYID;
		}
		DictCityRecord cityName = CITY_MAP.get(cityId);
		if(cityName == null) {
			cityName = saas.region.city.getCityName(cityId);
			CITY_MAP.put(cityId,cityName);
		}
		if(cityName != null) {
			return cityName.getName();
		}else {
			return null;
		}
		
	}
	
	/**
	 * 处理省码到名称
	 * @param provinceId 省代号码
	 * @return 省名称
	 */
	public String mapProvinceCodeToName(Integer provinceId) {
		if(provinceId == null) {
			provinceId = DEFAULT_PROVINCEID;
		}
		DictProvinceRecord provinceName = PROVINCE_MAP.get(provinceId);
		if(provinceName==null) {
			provinceName = saas.region.province.getProvinceName(provinceId);
			PROVINCE_MAP.put(provinceId, provinceName);
		}
		if(provinceName!=null) {
			return provinceName.getName();
		}else {
			return null;
		}
	}
	
	/**
	 * 处理区码到名称
	 * @param provinceId 省代号码
	 * @return 省名称
	 */
	public String mapDistrictCodeToName(Integer districtId) {
		if(districtId == null) {
			districtId = DEFAULT_DISTRICTID;
		}
		
		DictDistrictRecord districtName = DISTRICT_MAP.get(districtId);
		if(districtName==null) {
			districtName = saas.region.district.getDistrictName(districtId);
			DISTRICT_MAP.put(districtId, districtName);
		}
		if(districtName!=null) {
			return districtName.getName();
		}else {
			return null;
		}
	}
}
