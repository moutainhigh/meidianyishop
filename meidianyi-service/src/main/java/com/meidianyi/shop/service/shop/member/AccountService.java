package com.meidianyi.shop.service.shop.member;

import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.main.tables.records.MpAuthShopRecord;
import com.meidianyi.shop.db.main.tables.records.MpOfficialAccountRecord;
import com.meidianyi.shop.db.main.tables.records.MpOfficialAccountUserRecord;
import com.meidianyi.shop.db.shop.tables.records.UserRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.foundation.util.RemarkUtil;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobsConstant.TaskJobEnum;
import com.meidianyi.shop.service.pojo.shop.auth.AdminTokenAuthInfo;
import com.meidianyi.shop.service.pojo.shop.config.distribution.DistributionParam;
import com.meidianyi.shop.service.pojo.shop.config.message.MessageTemplateConfigConstant;
import com.meidianyi.shop.service.pojo.shop.market.message.RabbitMessageParam;
import com.meidianyi.shop.service.pojo.shop.member.account.*;
import com.meidianyi.shop.service.pojo.shop.member.builder.UserAccountRecordBuilder;
import com.meidianyi.shop.service.pojo.shop.message.MpTemplateConfig;
import com.meidianyi.shop.service.pojo.shop.message.MpTemplateData;
import com.meidianyi.shop.service.pojo.shop.operation.RecordContentTemplate;
import com.meidianyi.shop.service.pojo.shop.operation.RecordTradeEnum;
import com.meidianyi.shop.service.pojo.shop.operation.RemarkTemplate;
import com.meidianyi.shop.service.pojo.shop.operation.TradeOptParam;
import com.meidianyi.shop.service.pojo.shop.user.message.MaSubscribeData;
import com.meidianyi.shop.service.pojo.shop.user.message.MaTemplateData;
import com.meidianyi.shop.service.shop.config.DistributionConfigService;
import com.meidianyi.shop.service.shop.config.ShopCommonConfigService;
import com.meidianyi.shop.service.shop.distribution.UserTotalFanliService;
import com.meidianyi.shop.service.shop.member.dao.AccountDao;
import com.meidianyi.shop.service.shop.member.dao.UserAccountDao;
import com.meidianyi.shop.service.shop.operation.RecordTradeService;
import com.meidianyi.shop.service.pojo.shop.market.message.maconfig.SubcribeTemplateCategory;

import jodd.util.StringUtil;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.tools.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.meidianyi.shop.common.foundation.data.JsonResultCode.CODE_MEMBER_ACCOUNT_UPDATE_FAIL;
import static com.meidianyi.shop.db.shop.tables.RecordAdminAction.RECORD_ADMIN_ACTION;
import static com.meidianyi.shop.db.shop.tables.UserAccount.USER_ACCOUNT;
import static com.meidianyi.shop.service.pojo.shop.operation.RecordTradeEnum.*;
/**
 * 余额管理
 * @author 黄壮壮
 * @Date:  2019年8月26日
 * @Description: 会员余额管理
 */
@Service
public class AccountService extends ShopBaseService {
	@Autowired private MemberService memberService;
	@Autowired private RecordTradeService recordTradeService;
	@Autowired private UserAccountDao uAccountDao;
	@Autowired private AccountDao accountDao;
	@Autowired private UserTotalFanliService userTotalFanliService;
	@Autowired private DistributionConfigService distributionConfigService;
	@Autowired private ShopCommonConfigService shopCommonConfigService;
	/**
	 * 会员余额变动
	 * @param param 余额对象参数
	 * @param tradeOpt 交易操作数据
	 */
	public void  updateUserAccount(AccountParam param, TradeOptParam tradeOpt) throws MpException {
		if (isNull(param.getUserId()) || isNull(param.getAmount())) {
			logger().info("用户id或用户卡余额不能为空");
			throw new MpException(CODE_MEMBER_ACCOUNT_UPDATE_FAIL);
		}
		
		UserRecord user = memberService.getUserRecordById(param.getUserId());
		checkForUserExistAndHaveAccount(user);
		BigDecimal newAccount = calcAccount(user, param);
		dealWithRemark(param);
		dealWithPayType(param, newAccount,tradeOpt);
		
		
		/** -支付类型 不能为null */
		if(isNull(param.getPayment())) {
			param.setPayment("");
		}
		
		this.transaction(() ->{
			logger().info("事务处理中");
			
			addRow(param, tradeOpt.getAdminUserId());
			/** 更新用户余额user表  */
			updateUserAccount(newAccount,user.getUserId());
			/** 插入交易明细数据 到trades_record */
			addTradeRecord(param, tradeOpt);
			String account = param.getAmount().compareTo(BigDecimal.ZERO)<0 ? param.getAmount().toString():"+"+param.getAmount().toString();
			saas().getShopApp(getShopId()).record.insertRecord(Arrays.asList(new Integer[] {RecordContentTemplate.MEMBER_ACCOUNT.code}), String.valueOf(user.getUserId()),user.getUsername(),account);
		});
		
		// 发送消息
		String subscribueRemark = null;
		if(RemarkTemplate.USER_INPUT_MSG.code.equals(param.getRemarkId())) {
			subscribueRemark = param.getRemarkData();
		}else {
			String message = RemarkTemplate.getMessageByCode(param.getRemarkId());
			subscribueRemark = Util.translateMessage(null, message,"","remark", param.getRemarkData());
		}
		
		// 订阅消息
		String[][] maData = new String[][] {
			{String.valueOf(param.getAmount().abs())},
			{String.valueOf(newAccount.abs())},
			{Util.getdate("yyyy-MM-dd HH:mm:ss")},
			{param.getRemarkData()}
		};
		
		List<Integer> arrayList = Collections.<Integer>singletonList(param.getUserId());
		MaSubscribeData data = MaSubscribeData.builder().data307(maData).build();
		
		// 公众号消息
		String[][] mpData = new String[][] {
			{"您好，您会有新的余额变动"},
			{Util.getdate("yyyy-MM-dd HH:mm:ss")},
			{String.valueOf(param.getAmount().abs())},
			{String.valueOf(newAccount.abs())},
			{param.getRemarkData()}
		};
		RabbitMessageParam param2 = RabbitMessageParam.builder()
				.maTemplateData(
						MaTemplateData.builder().config(SubcribeTemplateCategory.BALANCE_CHANGE).data(data).build())
				.mpTemplateData(
						MpTemplateData.builder().config(MpTemplateConfig.MONEY_CHANGE).data(mpData).build())
				.page("pages/account/account").shopId(getShopId())
				.userIdList(arrayList)
				.type(MessageTemplateConfigConstant.MONEY_CHANGE).build();
		saas.taskJobMainService.dispatchImmediately(param2, RabbitMessageParam.class.getName(), getShopId(), TaskJobEnum.SEND_MESSAGE.getExecutionType());		
	}

	/**
	 * 检测确保用户存在并有用余额
	 */
	private void checkForUserExistAndHaveAccount(UserRecord user) throws MpException {
		if (isNull(user) || isNull(user.getAccount())) {
			logger().info("此用户不存在或余额不存在");
			throw new MpException( CODE_MEMBER_ACCOUNT_UPDATE_FAIL);
		}
	}

	private void dealWithPayType(AccountParam param, BigDecimal newAccount,TradeOptParam tradeOpt) throws MpException {
		/** -支付类型  */
		if(isConsump(param)) {
			/** -消费 */
			if(isNotConsumpAvailable(newAccount)) {
				logger().info("余额不足");
				throw new MpException( CODE_MEMBER_ACCOUNT_UPDATE_FAIL);
			}
			param.setIsPaid(UACCOUNT_CONSUMPTION.val());
			tradeOpt.setTradeFlow(TRADE_FLOW_OUT.val());
		}else {
			/** -充值 */
			param.setIsPaid(UACCOUNT_RECHARGE.val());
			tradeOpt.setTradeFlow(TRADE_FLOW_IN.val());
		}
	}

	/**
	 * 处理备注
	 */
	private void dealWithRemark(AccountParam param) {
		// 用户输入备注
		if(!StringUtil.isBlank(param.getUserInputRemark())) {
			param.setRemarkId(RemarkTemplate.USER_INPUT_MSG.code);
			param.setRemarkData(param.getUserInputRemark());
		}else {
			// 系统定义的备注
			if(param.getRemarkId()==null) {
				// 默认管理员操作
				param.setRemarkId(RemarkTemplate.ADMIN_OPERATION.code);
			}
		}
	}
	
	private BigDecimal calcAccount(UserRecord user,AccountParam param) {
		return BigDecimalUtil.add(param.getAmount(), user.getAccount());
	}
	
	/**
	 * 余额是否不满足消费
	 * @return true: 不满足；false: 满足
	 */
	private boolean isNotConsumpAvailable(BigDecimal val) {
		return !isConsumpAvailable(val);
	}
	
	/**
	 * 余额是否满足消费
	 * @return true: 满足；false: 不满足
	 */
	private boolean isConsumpAvailable(BigDecimal val) {
		return !isLessThanZero(val);
	}
	/**
	 * 是否为消费
	 * @return true: 是；false: 不是
	 */
	private boolean isConsump(AccountParam param) {
		return isLessThanZero(param.getAmount());
	}
	private boolean isLessThanZero(BigDecimal val) {
		return BigDecimalUtil.compareTo(val, BigDecimal.ZERO)<0;
	}
	
	private boolean isGreatOrEqualThanZero(BigDecimal val) {
		return BigDecimalUtil.compareTo(val, BigDecimal.ZERO)>=0;
	}
	
    /**
     * 插入交易记录
     * @param param
     * @param tradeOpt
     */
	private void addTradeRecord(AccountParam param,TradeOptParam tradeOpt) {
		logger().info("记录用户余额到交易tradeRecord表");
		recordTradeService.insertTradeRecord(TradeOptParam.builder()
				.tradeNum(param.getAmount().abs())
				.tradeSn(param.getOrderSn())
				.userId(param.getUserId())
				.tradeContent(TRADE_CONTENT_CASH.val())
				.tradeType(tradeOpt.getTradeType())
				.tradeFlow(tradeOpt.getTradeFlow())
				.tradeStatus(tradeOpt.getTradeFlow() == 2 ? TRADE_FLOW_IN.val() : tradeOpt.getTradeFlow())
				.build());
	}
	/**
	 * 更新user表的account字段
	 */
	private void updateUserAccount(BigDecimal account, Integer userId) {
		Assert.isTrue(isGreatOrEqualThanZero(account),"余额不能为负数");
		if(isNotNull(account) && isGreatOrEqualThanZero(account)) {
			accountDao.updateUserAccount(account, userId);
		}
	}



	/**
	 * 将数据插入到user_account
	 */
	public void addRow(AccountParam param, int adminUser) {
		logger().info("插入userAccount记录");
		if(param.getIsPaid()==null) {
			if(isConsump(param)) {
				param.setIsPaid(RecordTradeEnum.UACCOUNT_CONSUMPTION.val());
			}else {
				param.setIsPaid(RecordTradeEnum.UACCOUNT_RECHARGE.val());
			}
		}
		UserAccountRecordBuilder
			.create(db().newRecord(USER_ACCOUNT))
			.userId(param.getUserId())
			.amount(param.getAmount())
			.orderSn(param.getOrderSn())
			.remarkId(String.valueOf(param.getRemarkId()))
			.remarkData(param.getRemarkData())
			.adminUser(String.valueOf(adminUser))
			.isPaid(param.getIsPaid())
			.payment(param.getPayment())
			.build()
			.insert();
	}

	/**
	 * 添加操作记录到b2c_record_admin_action
	 */
	public void addActionRecord(AccountParam param, UserRecord user, AdminTokenAuthInfo admin) {
		//TODO 删除掉这段代码
		Integer userId = user.getUserId();
		String name = user.getUsername();
		BigDecimal zero = new BigDecimal(0);
		String account = param.getAmount().compareTo(zero)<0 ? param.getAmount().toString():"+"+param.getAmount().toString();
		String actionDesc = String.format("修改\"ID: %d  昵称: %s \"的余额 %s", userId, name, account);
		
		Byte actionType=4;
		Integer subAccountId = admin.getSubAccountId()==null?0:admin.getSubAccountId();
		String userName = admin.getUserName();

		db().insertInto(RECORD_ADMIN_ACTION,RECORD_ADMIN_ACTION.SYS_ID,RECORD_ADMIN_ACTION.ACCOUNT_ID
							,RECORD_ADMIN_ACTION.ACTION_TYPE,RECORD_ADMIN_ACTION.TEMPLATE_DATA,RECORD_ADMIN_ACTION.USER_NAME)
			.values(admin.getSysId(),subAccountId,actionType,actionDesc,userName)
			.execute();
	}
	
	
	public BigDecimal getUserAccount(Integer userId) {
		UserRecord user = memberService.getUserRecordById(userId);
		if (user == null || user.getAccount() == null) {
			return BigDecimal.ZERO;
		}else {
			return user.getAccount();
		}
	}

	
	/**
	 * 分页查询会员用户余额详情
	 */
	public PageResult<AccountPageListVo> getPageListOfAccountDetails(AccountPageListParam param,String language) {
		PageResult<AccountPageInfo> resultBefore = uAccountDao.getPageListOfAccountDetails(param);
		return  accountInfoToAccountVo(language,resultBefore);
	}

	/**
	 * 数据类型转换
	 */
	private PageResult<AccountPageListVo> accountInfoToAccountVo(String language,
			PageResult<AccountPageInfo> resultBefore) {
		
		PageResult<AccountPageListVo> resultAfter = new PageResult<>();
		List<AccountPageListVo> dataList = new ArrayList<>();
		for(AccountPageInfo info: resultBefore.dataList) {
			// change AccountPageInfo to AccountPageListVo
			AccountPageListVo vo = new AccountPageListVo();
			BeanUtils.copyProperties(info, vo);
			// remark i18n 
			String remark = RemarkUtil.remarkI18N(language, info.getRemarkId(), info.getRemarkData());
			vo.setRemark(remark);
			dataList.add(vo);
		}
		resultAfter.setPage(resultBefore.getPage());
		resultAfter.setDataList(dataList);
		return resultAfter;
	}

	private boolean isNotNull(BigDecimal account) {
		return account != null;
	}
	
	private boolean isNull(Object obj) {
		return obj == null;
	}
	
	/**
	 * 	获取用户的余额和提现金额
	 */
	public AccountWithdrawVo getUserAccountWithdraw(Integer userId) {
		logger().info("获取用户的余额和提现金额");
		AccountWithdrawVo vo = new AccountWithdrawVo();
		BigDecimal account = memberService.getUserAccount(userId);
		BigDecimal withDraw = userTotalFanliService.getTotalMoney(userId);
		DistributionParam distributionCfg = distributionConfigService.getDistributionCfg();
		if(distributionCfg!=null) {
			vo.setWithdrawStatus(distributionCfg.getWithdrawStatus());
			vo.setWithdrawSource(distributionCfg.getWithdrawSource());
		}
		Byte bindMobile = shopCommonConfigService.getBindMobile();
		vo.setIsBindMobile(bindMobile);
		MpAuthShopRecord wxapp = saas.shop.mp.getAuthShopByShopId(getShopId());
		//可提现金额小于余额是以可提现金额为主
		if(withDraw.compareTo(account)<1) {
			vo.setWithdraw(withDraw);
		}else {
			vo.setWithdraw(account);
		}
		vo.setAccount(account);

		// 已绑定公众号 
		if(wxapp!=null && !StringUtils.isBlank(wxapp.getLinkOfficialAppId())) {
			String wxOpenId = memberService.getUserWxOpenId(userId);
			Byte subscribe = NumberUtils.BYTE_ZERO;
			String isPublicUser = saas.shop.mpOfficialAccountUserService.getOpenIdFromMpOpenId(wxapp.getLinkOfficialAppId(), wxapp.getAppId(), wxOpenId);
			if(!StringUtils.isBlank(isPublicUser)) {
				MpOfficialAccountUserRecord user = saas.shop.mpOfficialAccountUserService.getUser(wxapp.getLinkOfficialAppId(),isPublicUser);
				subscribe = user.getSubscribe();
			}
			vo.setIsPublicUser(subscribe);
			MpOfficialAccountRecord officialAccount = saas.shop.mpOfficialAccountService.getOfficialAccountByAppid(wxapp.getLinkOfficialAppId());
			if(officialAccount != null) {
				vo.setNickName(officialAccount.getNickName());
			}
		}
		return vo;
	}
	
	/**
	 * 获取用户账户信息
	 * @param userId
	 * @return AccountNumberVo 用户
	 */
	public AccountNumberVo getUserAccountNumber(Integer userId) {
		UserRecord user = memberService.getUserRecordById(userId);
		if(user == null) {
			return null;
		}
		return AccountNumberVo
				.builder()
				.score(user.getScore())
				.account(user.getAccount())
				.build();
			
	}

}


