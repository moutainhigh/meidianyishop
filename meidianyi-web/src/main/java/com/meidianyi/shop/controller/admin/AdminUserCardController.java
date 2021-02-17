package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.foundation.util.CardUtil;
import com.meidianyi.shop.service.pojo.shop.member.account.CardConsumeParam;
import com.meidianyi.shop.service.pojo.shop.member.account.UserCardParam;
import com.meidianyi.shop.service.pojo.shop.member.card.CardConstant;
import com.meidianyi.shop.service.pojo.shop.member.card.UserCardChargeListParam;
import com.meidianyi.shop.service.pojo.shop.member.card.UserCardConsumeBean;
import com.meidianyi.shop.service.pojo.shop.member.card.UserCardConsumeBean.UserCardConsumeBeanBuilder;
import com.meidianyi.shop.service.pojo.shop.member.card.UserCardRenewListParam;
import com.meidianyi.shop.service.pojo.shop.order.virtual.AnalysisParam;
import com.meidianyi.shop.service.pojo.shop.store.store.StoreParam;
import com.meidianyi.shop.service.pojo.wxapp.store.ValidCon;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author 黄壮壮
 * @Date: 2019年11月26日
 * @Description:
 */
@RestController
public class AdminUserCardController extends AdminBaseController {
    /**
     * 用户卡 - 删除
	 */
	@PostMapping("/api/admin/user/card/delete")
	public JsonResult deleteUserCard(@RequestBody UserCardParam userCard) {
		logger().info("删除用户会员卡");
		shop().userCard.repealCardByCardNo(userCard.getCardNo());
		return this.success("删除用户会员卡成功");
	}

    /**
     * Gets store valid card list.获取用户在门店有效会员卡列表
     *
     * @param param the param
     * @return the store valid card list
     */
    @PostMapping("/api/admin/user/card/available")
    public JsonResult getStoreValidCardList(@RequestBody @Validated(ValidCon.class) StoreParam param) {
        return this.success(shop().userCard.userCardDao.getStoreValidCardList(param.getUserId(), param.getStoreId()));
    }

    /**
     *	 增加减少会员卡余额和次数
     */
    @PostMapping("/api/admin/user/card/consume")
    public JsonResult chargeConsume(@RequestBody CardConsumeParam param) {
    	logger().info("增加减少会员卡余额和次数");
    	System.out.println(param);
    	UserCardConsumeBeanBuilder consumer = UserCardConsumeBean.builder();
    	Byte cardType = param.getCardType();
    	Boolean isContinue = true;

    	if(CardUtil.isLimitCard(cardType)) {
    		// 限次卡
    		if(NumberUtils.BYTE_ONE.equals(param.getType())) {
    			// 兑换商品次数、
    			consumer.exchangCount(param.getReduce().intValue())
    					.countDis(param.getCountDis());
    			if(param.getReduce().intValue()<0) {
    				isContinue = false;
    			}
    		}else {
    			// 兑换门店次数
    			consumer.count(param.getReduce().intValue())
    					.countDis(param.getCountDis());
    		}
    	}else {
    		// 普通卡
    		consumer.money(param.getReduce())
    				.moneyDis(param.getMoneyDis());
    	}

        UserCardConsumeBean bean = consumer.type(cardType)
            .userId(param.getUserId())
            .cardId(param.getCardId())
            .message(param.getMessage())
            .payment("")
            .cardNo(param.getCardNo())
            .changeType(CardConstant.CHARGE_ADMIN_OPT)
            .build();
        shop().userCard.cardConsumer(bean, 0, (byte) 10, (byte) 2, param.getType(), isContinue);
        return success();
    }

    /**
     * 会员卡续费记录
     */
    @PostMapping("/api/admin/user/card/renew/order")
    public JsonResult getCardRenewList(@RequestBody UserCardRenewListParam param) {
        return success(shop().userCard.getCardRenewList(param));
    }

    /**
     * 会员卡续费记录数据分析
     */
    @PostMapping("/api/admin/user/card/renew/analysis")
    public JsonResult cardRenewAnalysis(@RequestBody AnalysisParam param) {
        return success(shop().userCard.cardRenewAnalysis(param));
    }

    /**
     * 会员卡续费记录导出
     *
     * @param param
     * @param response
     */
    @PostMapping("/api/admin/user/card/renew/export")
    public void cardRenewExport(@RequestBody @Valid UserCardRenewListParam param, HttpServletResponse response) {
        Workbook workbook = shop().userCard.exportRenewList(param, getLang());
        String fileName = Util.translateMessage(getLang(), JsonResultMessage.USER_CARD_RENEW_FILE_NAME, "excel", "excel") + DateUtils.dateFormat(DateUtils.DATE_FORMAT_SHORT);
        export2Excel(workbook, fileName, response);
    }

    /**
     * 会员卡充值记录
     */
    @PostMapping("/api/admin/user/card/charge/order")
    public JsonResult getCardChargeList(@RequestBody UserCardChargeListParam param) {
        return success(shop().userCard.getCardChargeList(param, getLang()));
    }

    /**
     * 会员卡充值记录数据分析
     */
    @PostMapping("/api/admin/user/card/charge/analysis")
    public JsonResult cardChargeAnalysis(@RequestBody AnalysisParam param) {
        return success(shop().userCard.cardChargeAnalysis(param));
    }

    /**
     * 会员卡充值记录导出
     *
     * @param param
     * @param response
     */
    @PostMapping("/api/admin/user/card/charge/export")
    public void cardChargeExport(@RequestBody @Valid UserCardChargeListParam param, HttpServletResponse response) {
        Workbook workbook = shop().userCard.exportChargeList(param, getLang());
        String fileName = Util.translateMessage(getLang(), JsonResultMessage.USER_CARD_CHARGE_FILE_NAME, "excel", "excel") + DateUtils.dateFormat(DateUtils.DATE_FORMAT_SHORT);
        export2Excel(workbook, fileName, response);
    }


}
