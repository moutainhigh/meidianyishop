package com.meidianyi.shop.service.shop.user.message;

import com.google.common.base.Objects;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.RegexUtil;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.main.tables.records.MpAuthShopRecord;
import com.meidianyi.shop.db.shop.tables.records.SubscribeMessageRecord;
import com.meidianyi.shop.db.shop.tables.records.UserRecord;
import com.meidianyi.shop.service.foundation.jedis.JedisManager;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.market.message.maconfig.RuleKey;
import com.meidianyi.shop.service.pojo.shop.market.message.maconfig.SubscribeMessageConfig;
import com.meidianyi.shop.service.pojo.shop.market.message.maconfig.WxMaSubscribeMessage;
import com.meidianyi.shop.service.pojo.shop.market.message.maconfig.WxMaSubscribeMessageData;
import com.meidianyi.shop.service.pojo.shop.user.message.MaSubscribeData;
import com.meidianyi.shop.service.pojo.wxapp.subscribe.TemplateVo;
import com.meidianyi.shop.service.pojo.wxapp.subscribe.UpdateTemplateParam;
import com.meidianyi.shop.service.shop.user.user.UserService;
import com.meidianyi.shop.service.wechat.OpenPlatform;
import com.meidianyi.shop.service.wechat.bean.open.WxOpenMaSubScribeGetCategoryResult;
import com.meidianyi.shop.service.wechat.bean.open.WxOpenMaSubScribeGetCategoryResult.WxOpenSubscribeCategory;
import com.meidianyi.shop.service.wechat.bean.open.WxOpenMaSubScribeGetTemplateListResult;
import com.meidianyi.shop.service.wechat.bean.open.WxOpenMaSubScribeGetTemplateListResult.WxOpenSubscribeTemplate;
import com.meidianyi.shop.service.wechat.bean.open.WxOpenMaSubscribeAddTemplateResult;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.open.bean.result.WxOpenResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static com.meidianyi.shop.db.shop.tables.SubscribeMessage.SUBSCRIBE_MESSAGE;

/**
 * 小程序订阅消息
 *
 * @author zhaojianqiang
 *
 *         2019年12月4日 上午9:26:12
 */
@Service
public class SubscribeMessageService extends ShopBaseService {
	private static final byte ZERO = 0;
	private static final byte ONE = 1;
	private static final byte TWO = 2;
	private static final byte THREE = 3;
	private static final byte FOUR = 4;
	private static final byte FIVE = 5;

	private static final String WX_UNSUBSCRIBE ="43101";

	@Autowired
	protected OpenPlatform open;

	@Autowired
	private UserService userService;

	@Autowired
	protected JedisManager jedis;

    /**
     * 获取小程序的AppId
     * @return
     */
	private String getMaAppId() {
		MpAuthShopRecord authShop = saas.shop.mp.getAuthShopByShopId(getShopId());
		if (null == authShop) {
			// 没有小程序，报错
			throw new RuntimeException("ShopId:" + getShopId() + "没有绑定小程序");
		}
		return authShop.getAppId();
	}

	/**
	 * 获得类目列表
	 * @return
	 * @throws WxErrorException
	 */
	private List<Integer> getcategoryList() throws WxErrorException {
		WxOpenMaSubScribeGetCategoryResult templateCategory = open.getMaExtService().getTemplateCategory(getMaAppId());
		if (templateCategory != null) {
			List<Integer> list = new ArrayList<Integer>();
			for (WxOpenSubscribeCategory wxcat : templateCategory.getData()) {
				list.add(wxcat.getId());
			}
			return list;
		}
		return null;
	}
	/**
	 * 获取所需的类目Id ,找第一个符合的
	 * @return
	 * @throws WxErrorException
	 */
	public Integer getcategoryId() throws WxErrorException {
		List<Integer> getcategoryList = getcategoryList();
		if (getcategoryList == null) {
			// 抛错
			logger().info("AppId：" + getMaAppId() + " 获取类目失败");
			return 0;
		}
		//已经定义的类目ID
		Set<Integer> idList = SubscribeMessageConfig.getSecondIdList();
		int id=0;
		//自己定义的顺序为第一优先级
		for (Integer haveId : idList) {
			for (Integer ids : getcategoryList) {
				if (ids.equals(haveId)) {
					id = haveId;
					return id;
				}
			}
		}
		return id;

	}

	/**
	 * 获取发送时候要的template_id
	 *
	 * @param user
	 * @param templateNo
	 * @return
	 */
	public SubscribeMessageRecord getCommonTemplateId(UserRecord user, String templateNo) {
		SubscribeMessageRecord sRecord = db().selectFrom(SUBSCRIBE_MESSAGE)
				.where(SUBSCRIBE_MESSAGE.USER_ID.eq(user.getUserId())
						.and(SUBSCRIBE_MESSAGE.WX_OPENID.eq(user.getWxOpenid())
								.and(SUBSCRIBE_MESSAGE.TEMPLATE_NO.eq(templateNo)))
						.and(SUBSCRIBE_MESSAGE.STATUS.eq((byte) 1).and(SUBSCRIBE_MESSAGE.CAN_USE_NUM.gt(0))))
				.fetchAny();
		if (sRecord != null) {
			// 存在，取
			return sRecord;
		}
		logger().info("userId:" + user.getUserId() + "的templateNo：" + templateNo + "没有记录，不发送信息");
		return null;
	}

	/**
	 * 发送订阅消息
	 * @param userId
	 * @param templateName 在SubcribeTemplateCategory中定义的类
	 * @param data
	 * @param page
	 * @return
	 * @throws WxErrorException
	 */
	public Boolean sendMessage(Integer userId,String templateName,MaSubscribeData data,
			String page) throws WxErrorException {
		UserRecord user = userService.getUserByUserId(userId);
		if (null == user) {
			logger().info("userId：" + userId + " 在店铺：" + getShopId() + "不存在");
			return false;
		}
		logger().info("开始获取类目id");
		// 类目ID
		Integer secondId = getcategoryId();
		if (secondId==0) {
			// 没有在现在版本定义的订阅消息中，直接发公众号
			logger().info("AppId：" + getMaAppId() + " 的账号对应类目ID：" + secondId + "不在当前版本定义的订阅消息中，准备发送公众号");
			// TODO发送到公众号
			return false;
		}
		logger().info("获取的类目id：{}",secondId);
		logger().info("获取当前帐号下的个人模板列表");
		// 获取当前帐号下的个人模板列表
		WxOpenMaSubScribeGetTemplateListResult templateList = open.getMaExtService().getTemplateList(getMaAppId());
		jedis.getIncrSequence("subScribe", Integer.MAX_VALUE, 0);
		if (!templateList.isSuccess()) {
			logger().info("获取AppId：" + getMaAppId() + " 的账号下的个人模板失败，准备发送公众号");
			// TODO发送到公众号
			return false;
		}

		SubscribeMessageConfig config = SubscribeMessageConfig.getByTempleName(secondId, templateName);
		if(config==null) {
			logger().info("类目：{};下没有模板：{}",secondId,templateName);
			config = SubscribeMessageConfig.getIsExeit(getcategoryList(), templateName);
			if(config==null) {
				logger().info("小程序：{}；的所有类目下没有模板：{}",getMaAppId(),templateName);
				return false;
			}
		}
		// 发送用的TemplateId
		SubscribeMessageRecord templateIdRecord = getCommonTemplateId(user, String.valueOf(config.getTid()));
		if (StringUtils.isEmpty(templateIdRecord)) {
			return false;
		}
		String templateId = templateIdRecord.getTemplateId();
		// 小程序中是否配置了这个模板
		templateId = addTemplate(templateIdRecord.getTemplateId(), config,templateList);
		logger().info("对应消息的模板templateId：{}",templateId);
		//拼装报文
		WxMaSubscribeMessage postData = assembleData(data, config, page, templateId, user.getWxOpenid(),secondId);
		if(postData==null) {
			logger().info("拼装报文失败，为空");
			return false;
		}
		//进行要发送数据的格式校验
		verification(templateList, templateId, postData);

		logger().info("开始发送：{}",postData.toJson());
		WxOpenResult sendResult = open.getMaExtService().sendTemplate(getMaAppId(),postData);
		boolean success = sendResult.isSuccess();
		logger().info("发送结果" + success);
		if (success) {
			decrementSubscribeNum(templateIdRecord);
			incrementSubscribeNum(templateIdRecord);
			return true;
		}
		// 用户拒绝接受消息，如果用户之前曾经订阅过，则表示用户取消了订阅关系
		if (WX_UNSUBSCRIBE.equals(sendResult.getErrcode())) {
			modifySubscribeStatus(templateIdRecord);
			return false;
		}
		return false;

	}
	/**
	 * 拼装发送的消息
	 * @param data
	 * @param config
	 * @param page
	 * @param templateId
	 * @param touser
	 * @return
	 */

	private WxMaSubscribeMessage assembleData(MaSubscribeData data,SubscribeMessageConfig config,String page,String templateId,String touser,Integer secondId) {
		WxMaSubscribeMessage postData=new WxMaSubscribeMessage();
		String content = config.getContent();
		List<String> names = RegexUtil.getSubStrList("{{", ".", content);
		List<WxMaSubscribeMessageData> wxDatalist = new ArrayList<WxMaSubscribeMessageData>();
		logger().info("传入的data：{}",data.toString());
		String[][] stringData = getSubscribeData(data, secondId);
		if(stringData==null) {
			logger().info("未解析出小程序中塞入需要的值，类目id：{}",secondId);
			return null;
		}
		if(!Objects.equal(config.getKidList().length, stringData.length)) {
			logger().info("传送的字段长度不对，类目id：{},定义长度：{}，传送长度：{}", secondId, config.getKidList().length, stringData.length);
			return null;
		}
		for (int i = 0, len = stringData.length; i < len; i++) {
			String[] values = stringData[i];
			wxDatalist.add(new WxMaSubscribeMessageData(names.get(i), values[0]));
		}
		postData.setData(wxDatalist);
		postData.setPage(page);
		postData.setTemplateId(templateId);
		postData.setTouser(touser);
		return postData;

	}

	/**
	 * 根据类目id解析出对应的data数组
	 * @param data
	 * @param secondId
	 * @return
	 */
	private String[][] getSubscribeData(MaSubscribeData data, Integer secondId){
		Class<? extends MaSubscribeData> clazz = data.getClass();
		String methodName="data"+secondId;
		String[][] stringData = null;
		try {
			PropertyDescriptor pd = new PropertyDescriptor(methodName, clazz);
			Method method = pd.getReadMethod();
			Object invoke = method.invoke(data);
			stringData=(String[][]) invoke;
		} catch (Exception e) {
			logger().info(e.getMessage(),e);
			return null;
		}
		return stringData;
	}

	/**
	 * 返回前端需要的TemplateId
	 * @param data
	 * @return
	 * @throws WxErrorException
	 */
	public TemplateVo[] getTemplateId(String[] data) throws WxErrorException {
		//获取所有类目id
		List<Integer> getcategoryList = getcategoryList();
		List<SubscribeMessageConfig> titleList =new ArrayList<>();
		for (Integer category : getcategoryList) {
			for (int i = 0; i < data.length; i++) {
				SubscribeMessageConfig byTempleName = SubscribeMessageConfig.getByTempleName(category, data[i]);
				if (byTempleName == null) {
					logger().info("appid：" + getMaAppId() + "。数据：" + data[i] + "在类目" + category + "暂时未定义");
				} else {
					logger().info("添加的TempleName为：" + byTempleName.getTitle()+"。类目为"+category);
					titleList.add(byTempleName);
				}
			}
		}
		logger().info("titleList大小为"+titleList.size());
		logger().info(Util.toJson(titleList));
		List<TemplateVo> results =new ArrayList<>();
		WxOpenMaSubScribeGetTemplateListResult templateList = open.getMaExtService().getTemplateList(getMaAppId());
		jedis.getIncrSequence("subScribe", Integer.MAX_VALUE, 0);
		List<WxOpenSubscribeTemplate> data2 = templateList.getData();
		if (data2.size() != 0) {
			for (SubscribeMessageConfig title : titleList) {
				Boolean flag = false;
				for (WxOpenSubscribeTemplate template : data2) {
					if (template.getTitle().contains(title.getTitle())) {
						// 存在，直接赋值
						flag = true;
						logger().info("已经定义了模板：" + title.getTitle());
						results.add(new TemplateVo(template.getPriTmplId(), title.getId(), title.getTempleName()));
					}
				}
				if (!flag) {
					logger().info("没有定义模板：" + title.getTitle());
					results.add(new TemplateVo(addTemplate(title), title.getId(), title.getTempleName()));
				}
			}
		}else {
			for (SubscribeMessageConfig title : titleList) {
				logger().info("没有定义模板："+title.getTitle());
				results.add(new TemplateVo(addTemplate(title), title.getId(),title.getTempleName()));
			}
		}
		return results.toArray(new TemplateVo[0]);
	}


	/**
	 * 更新表中可用数量
	 * @param userId
	 * @param param
	 */
	public void updateStatusAndNum(Integer userId,UpdateTemplateParam param) {
		List<TemplateVo> successs = param.getAccept();
		List<TemplateVo> rejects = param.getReject();
		List<TemplateVo> bans = param.getBan();
		if(successs!=null) {
			logger().info("进入success的");
			for(TemplateVo success:successs) {
				SubscribeMessageConfig successConfig = SubscribeMessageConfig.getByTempleName(success.getId(), success.getTempleName());
				SubscribeMessageRecord record = db().selectFrom(SUBSCRIBE_MESSAGE)
						.where(SUBSCRIBE_MESSAGE.USER_ID.eq(userId))
						.and(SUBSCRIBE_MESSAGE.TEMPLATE_ID.eq(success.getTemplateId())
								.and(SUBSCRIBE_MESSAGE.TEMPLATE_NO.eq(String.valueOf(successConfig.getTid()))))
						.fetchAny();
				if(record==null) {
					SubscribeMessageRecord insertRecord=db().newRecord(SUBSCRIBE_MESSAGE);
					insertRecord.setUserId(userId);
					UserRecord user = userService.getUserByUserId(userId);
					insertRecord.setWxOpenid(user.getWxOpenid());
					insertRecord.setTemplateId(success.getTemplateId());
					insertRecord.setTemplateNo(String.valueOf(successConfig.getTid()));
					insertRecord.setCanUseNum(1);
					int insert = insertRecord.insert();
					logger().info("成功的templateId："+success.getTemplateId()+"插入结果"+insert);
				}else {
					record.setStatus((byte)1);
					record.setCanUseNum(record.getCanUseNum()==null?1:record.getCanUseNum()+1);
					int update = record.update();
					logger().info("成功的templateId："+success.getTemplateId()+"更新结果"+update);
				}
			}
		}
		if(rejects!=null) {
			logger().info("进入rejects的");
			for (TemplateVo reject:rejects) {
				SubscribeMessageConfig rejectConfig = SubscribeMessageConfig.getByTempleName(reject.getId(), reject.getTempleName());
				SubscribeMessageRecord rejrecord = db().selectFrom(SUBSCRIBE_MESSAGE)
						.where(SUBSCRIBE_MESSAGE.USER_ID.eq(userId))
						.and(SUBSCRIBE_MESSAGE.TEMPLATE_ID.eq(reject.getTemplateId())
								.and(SUBSCRIBE_MESSAGE.TEMPLATE_NO.eq(String.valueOf(rejectConfig.getTid()))))
						.fetchAny();
				if(rejrecord!=null) {
					if(rejrecord.getStatus().equals(ONE)) {
						Integer canUseNum = rejrecord.getCanUseNum();
						logger().info("可用数量"+canUseNum);
						if(canUseNum>0) {
							canUseNum=canUseNum-1;
							rejrecord.setCanUseNum(canUseNum);
							if(canUseNum==0) {
								logger().info("可用次数为0，状态变更为取消");
								rejrecord.setStatus((byte)0);
							}
						}
					}
					int update = rejrecord.update();
					logger().info("拒绝的templateId："+reject.getTemplateId()+"更新结果"+update);
				}
			}
		}
		if(bans!=null) {
			logger().info("进入bans的");
			for (TemplateVo ban:bans) {
				SubscribeMessageConfig banConfig = SubscribeMessageConfig.getByTempleName(ban.getId(), ban.getTempleName());
				SubscribeMessageRecord rejrecord = db().selectFrom(SUBSCRIBE_MESSAGE)
						.where(SUBSCRIBE_MESSAGE.USER_ID.eq(userId))
						.and(SUBSCRIBE_MESSAGE.TEMPLATE_ID.eq(ban.getTemplateId())
								.and(SUBSCRIBE_MESSAGE.TEMPLATE_NO.eq(String.valueOf(banConfig.getTid()))))
						.fetchAny();
				if(rejrecord!=null) {
					rejrecord.setStatus((byte)2);
					int update = rejrecord.update();
					logger().info("已被后台封禁的templateId："+ban.getTemplateId()+"更新结果"+update);
				}
			}
		}
	}
	/**
	 *  带校验的添加模板
	 * @param templateId
	 * @param config
	 * @return
	 * @throws WxErrorException
	 */
	public String addTemplate(String templateId, SubscribeMessageConfig config,WxOpenMaSubScribeGetTemplateListResult templateList) throws WxErrorException {
		if (!checkTemplate(templateId,templateList)) {
			return addTemplate(config);
		}
		return templateId;
	}

	/**
	 * 添加模板
	 * @param config
	 * @return
	 * @throws WxErrorException
	 */
	private String addTemplate(SubscribeMessageConfig config) throws WxErrorException {
		WxOpenMaSubscribeAddTemplateResult addTemplate = open.getMaExtService().addTemplate(getMaAppId(),
				String.valueOf(config.getTid()), config.getKidList(), config.getTitle());
		logger().info("创建模板" + config.getTitle()+"。结果："+addTemplate.getErrmsg() + "  " + addTemplate.getErrcode());
		if (addTemplate.isSuccess()) {

			return addTemplate.getPriTmplId();
		}
		return null;
	}
	/**
	 * 当前帐号下的个人模板列表中是否有要用的templateId，有：true；没有false
	 *
	 * @param templateId
	 * @return
	 * @throws WxErrorException
	 */
	public boolean checkTemplate(String templateId,WxOpenMaSubScribeGetTemplateListResult templateList) throws WxErrorException {
		if(templateList==null) {
			templateList = open.getMaExtService().getTemplateList(getMaAppId());
			jedis.getIncrSequence("subScribe", Integer.MAX_VALUE, 0);
		}
		logger().info("传入的templateId："+templateId);
		if (templateList.isSuccess()) {
			//账户下的模板
			List<WxOpenSubscribeTemplate> data = templateList.getData();
			Boolean flag = false;
			for (WxOpenSubscribeTemplate template : data) {
				if (template.getPriTmplId().equals(templateId)) {
					logger().info("循环的template："+template);
					flag=true;
				}
			}
			return flag;
		} else {
			logger().info("获取当前AppId：" + getMaAppId() + "下的个人模板列表失效");
			throw new RuntimeException("获取当前AppId：" + getMaAppId() + "下的个人模板列表失效");
		}
	}

	/**
	 * 减少用户的订阅数
	 *
	 * @param templateIdRecord
	 */
	public void decrementSubscribeNum(SubscribeMessageRecord templateIdRecord) {
		Integer canUseNum = templateIdRecord.getCanUseNum() - 1;
		templateIdRecord.setCanUseNum(canUseNum);
		if(canUseNum==0) {
			templateIdRecord.setStatus((byte)0);
		}
		int update = templateIdRecord.update();
		logger().info("减少用户订阅数" + update);
	}

	/**
	 * 增加用户的发送成功数
	 *
	 * @param templateIdRecord
	 */
	public void incrementSubscribeNum(SubscribeMessageRecord templateIdRecord) {
		templateIdRecord.setSuccessNum(templateIdRecord.getSuccessNum() == null ? 0 : templateIdRecord.getSuccessNum() + 1);
		int update = templateIdRecord.update();
		logger().info("增加用户的发送成功数"+update);
	}

	/**
	 * 更新消息订阅状态
	 *
	 * @param templateIdRecord
	 */
	public void modifySubscribeStatus(SubscribeMessageRecord templateIdRecord) {
		templateIdRecord.setStatus((byte) 0);
		int update = templateIdRecord.update();
		logger().info(" 更新消息订阅状态"+update);
	}

	/**
	 * 是否可用发送
	 * @param userId
	 * @param templateNo
	 * @return
	 */
	public boolean getCanUse(Integer userId,Integer templateNo) {
		SubscribeMessageRecord fetchAny = db().selectFrom(SUBSCRIBE_MESSAGE)
				.where(SUBSCRIBE_MESSAGE.USER_ID.eq(userId)
						.and(SUBSCRIBE_MESSAGE.TEMPLATE_NO.eq(String.valueOf(templateNo)))
						.and(SUBSCRIBE_MESSAGE.STATUS.eq((byte)1).and(SUBSCRIBE_MESSAGE.CAN_USE_NUM.gt(0)))).fetchAny();
		if(fetchAny==null) {
			return false;
		}
		return true;
	}

	/**
	 * 对数据的格式进行校验并修改
	 * @param templateLists
	 * @param templateId
	 * @param postData
	 */
	public void verification(WxOpenMaSubScribeGetTemplateListResult templateLists,String templateId,WxMaSubscribeMessage postData) {
		logger().info("进入格式校验");
		List<WxOpenSubscribeTemplate> templateList = templateLists.getData();
		String content = null;
		for (WxOpenSubscribeTemplate wxOpenSubscribeTemplate : templateList) {
			if(wxOpenSubscribeTemplate.getPriTmplId().equals(templateId)) {
				content=wxOpenSubscribeTemplate.getContent();
				break;
			}
		}
		String[] contents = content.split("\n");
		List<String> list=new ArrayList<String>();
		for (String string : contents) {
			String[] split = string.split(":");
			list.add(split[0]);
		}
		thingRule(postData.getData(),list);
		logger().info("出格式校验");
	}

	/**
	 * 对数据切割，目前没啥用
	 * @param content
	 * @return
	 */
	private List<String> toGetRuleKey(String content){
		List<String> list=new ArrayList<String>();
		if(content==null) {
			return list;
		}
		String key="\n";
		String[] split = content.split(key);
		for (String string : split) {
			int indexOf = string.indexOf("{{");
			String substring = string.substring(indexOf+2, string.length()-7);
			String str2=null;
			for (int i = 0; i < substring.length(); i++) {
				//防止末尾数字大于两位，直接判断数字第一次出现位置进行裁切
				if(substring.charAt(i)>=48 && substring.charAt(i)<=57){
					str2 = substring.substring(0,i);
					break;
				}
			}
			list.add(str2);
		}
		return list;
	}

	public void ruleCheck(List<String> ruleKeys,List<WxMaSubscribeMessageData> data) {
		for (int i = 0; i < ruleKeys.size(); i++) {

		}

	}

	private void thingRule(List<WxMaSubscribeMessageData> data,List<String> contentList) {
		for (int i = 0; i < data.size(); i++) {
			String name = data.get(i).getName();
			name = clearNum(name);
			String content = contentList.get(i);
			String value = data.get(i).getValue();
			String check = toCheck(name, value,content);
			data.get(i).setValue(check);
		}

	}
	/**
	 * 具体根据类型校验
	 * @param name
	 * @param value
	 * @return
	 */
	private String toCheck(String name, String value,String content) {
		String targValue = " ";
		switch (name) {
		case RuleKey.THING:
			value = subLimit(name,value, 20);
			break;
		case RuleKey.NUMBER:
			value = subLimit(name,value, 32);
			if (!Pattern.matches(RuleKey.NUMBER_PATTERN, value)) {
				value = toReturnAnLog(name, value,targValue);
			}
			break;
		case RuleKey.LETTER:
			value = subLimit(name,value, 32);
			if (!Pattern.matches(RuleKey.LETTER_PATTERN, value)) {
				value = toReturnAnLog(name, value,targValue);
			}
			break;
		case RuleKey.SYMBOL:
			value = subLimit(name,value, 5);
			value = specialSymbol(value,false);
			break;
		case RuleKey.CHARACTER_STRING:
			value = subLimit(name,value, 32);
			if (Pattern.matches(RuleKey.CHARACTER_STRING_PATTERN, value)) {
				//包含中文
				value = toReturnAnLog(name, value,targValue);
			}
			break;
		case RuleKey.TIME:
			boolean specialDate = specialDate(value, DateUtils.DATE_FORMAT_FULL);
			if(!specialDate) {
				value = toReturnAnLog(name, value,targValue);
			}
			break;
		case RuleKey.DATE:
			boolean specialDate1 = specialDate(value, DateUtils.DATE_FORMAT_FULL);
			boolean specialDate2 = specialDate(value, DateUtils.DATE_FORMAT_SIMPLE);
			if(!specialDate1&&!specialDate2) {
				value = toReturnAnLog(name, value,targValue);
			}
			break;
		case RuleKey.AMOUNT:
            value = checkAmount(name, value, targValue);
            break;
		case RuleKey.PHONE_NUMBER:
            value = checkPhoneNumber(name, value, targValue);
            break;
		case RuleKey.CAR_NUMBER:
			if (!Pattern.matches(RuleKey.CAR_NUMBER_PATTERN, value)) {
				value = toReturnAnLog(name, value,targValue);
			}
			break;
		case RuleKey.NAME:
            value = checkName(name, value, content);
            break;
		case RuleKey.PHRASE:
            value = checkPhrase(name, value);
            break;
		default:
			break;
		}
		return value;
	}

    private String checkAmount(String name, String value, String targValue) {
        int yuan = value.indexOf("元");
        if(yuan>0) {
            value = subLimit(name,value,yuan);
            if (!Pattern.matches(RuleKey.NUMBER_PATTERN, value)) {
                value = toReturnAnLog(name, value,targValue);
            }
        }else {
            if (!Pattern.matches(RuleKey.NUMBER_PATTERN, value)) {
                value = toReturnAnLog(name, value,targValue);
            }
        }
        return value;
    }

    private String checkPhoneNumber(String name, String value, String targValue) {
        value = subLimit(name,value,17);
        if (Pattern.matches(RuleKey.CHARACTER_STRING_PATTERN, value)) {
            //包含中文
            value = toReturnAnLog(name, value,targValue);
        }
        if (Pattern.matches(RuleKey.HAVEENGILSH_PATTERN, value)) {
            //包含英文
            value = toReturnAnLog(name, value,targValue);
        }
        return value;
    }

    private String checkPhrase(String name, String value) {
        if (!Pattern.matches(RuleKey.PHRASE_PATTERN, value)) {
            //包含非中文
            logger().info("类型：{}，校验不通过，原来值：{}",name,value);
            StringBuilder builder=new StringBuilder();
            for (int i = 0; i < value.length(); i++) {
                if(isChineseChar(value.charAt(i))) {
                    builder.append(value.charAt(i));
                }
            }
            value=builder.toString();
            logger().info("类型：{}，校验不通过，新值：{}",name,value);
        }
        return value;
    }

    private static final String SHOP_ACTIVITY  ="店铺活动";
    private static final String SHOP_ACTIVITY_NAME  ="活动名称";
    private static final String SHOP_ACTIVITY_PRIZE  ="店铺奖品";
    private static final String SHOP_ACTIVITY_PRIZE_NAME  ="店铺奖品";
    private String checkName(String name, String value, String content) {
        if(Pattern.matches(RuleKey.HAVENUM_PATTERN, value)) {
            //有数字
//            value=SHOP_ACTIVITY;
            if(SHOP_ACTIVITY_NAME.equals(content)) {
                value = toReturnAnLog(name, value,SHOP_ACTIVITY);
            }
            if(SHOP_ACTIVITY_PRIZE_NAME.equals(content)) {
                value = toReturnAnLog(name, value,SHOP_ACTIVITY_PRIZE);
            }
            logger().info("最后：{}",value);
            return value;
        }
        if (!Pattern.matches(RuleKey.CHARACTER_STRING_PATTERN, value)) {
            //不包含中文
            value = subLimit(name,value,20);
        }else {
            value = subLimit(name,value,10);
        }
        return value;
    }

    /**
	 * 类型校验不通过的log
	 * @param name       thing之类
	 * @param value      原来的值
	 * @param targValue  要变成的值
	 * @return
	 */
	private String toReturnAnLog(String name, String value,String targValue) {
		logger().info("类型：{}，校验不通过，原来值：{}",name,value);
		value=targValue;
		logger().info("类型：{}，校验不通过，新值：{}",name,value);
		return value;
	}

	/**
	 * 对末尾的数字类型进行处理
	 * @param name
	 * @return
	 */
	private String clearNum(String name) {
		for (int i = 0; i < name.length(); i++) {
			// 防止末尾数字大于两位，直接判断数字第一次出现位置进行裁切
			if (name.charAt(i) >= 48 && name.charAt(i) <= 57) {
				name = name.substring(0, i);
				break;
			}
		}
		return name;
	}

	/**
	 * 长度的截取
	 * @param value
	 * @param num
	 * @return
	 */
	private String subLimit(String name,String value,int num) {
		if (value.length() > num) {
			logger().info("类型：{}，原来值：{}；长度要求:{}",name,value,num);
			value = value.substring(0, num);
			logger().info("类型：{}，长度要求:{}，新值：{}；",name,num,value);
		}
		return value;
	}

	/**
	 * 对时间样式的校验
	 * @param value
	 * @param type
	 * @return
	 */
	private  boolean  specialDate(String value,String type) {
		try {
			LocalDate parse = LocalDate.parse(value, DateTimeFormatter.ofPattern(type));
		} catch (Exception e) {
			//打log
			return false;
		}
		return true;
	}

	/**
	 *
	 * @param value
	 * @param flag  true:返回为去掉特殊符号的值；false：返回值为特殊符号的值
	 * @return
	 */
	private String specialSymbol(String value,boolean flag) {
		StringBuilder builder=new StringBuilder();
		for (int i = 0; i < value.length(); i++) {
			if(flag) {
				if(RuleKey.SYMBOL_CHARACTERSTR.indexOf(value.charAt(i))<0) {
					builder.append(value.charAt(i));
				}
			}else {
				if(RuleKey.SYMBOL_CHARACTERSTR.indexOf(value.charAt(i))>=0) {
					builder.append(value.charAt(i));
				}
			}
		}
		value=builder.toString();
		return value;
	}

    public boolean isChineseChar(char c) {
        try {
            return String.valueOf(c).getBytes("UTF-8").length > 1;
        } catch (Exception e) {
            return false;
        }
    }
}
