package com.meidianyi.shop.service.shop.payment;

import cn.hutool.json.JSONUtil;
import com.github.binarywang.wxpay.bean.entpay.EntPayRequest;
import com.github.binarywang.wxpay.bean.entpay.EntPayResult;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPaySendRedpackRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.*;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.util.SignUtils;
import com.meidianyi.shop.common.foundation.data.DistributionConstant;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.DomainConfig;
import com.meidianyi.shop.config.WxSerMchConfig;
import com.meidianyi.shop.db.main.tables.records.MpAuthShopRecord;
import com.meidianyi.shop.db.shop.tables.records.UserRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.payment.PayCode;
import com.meidianyi.shop.service.pojo.shop.payment.PaymentRecordParam;
import com.meidianyi.shop.service.pojo.wxapp.pay.base.WebPayVo;
import com.meidianyi.shop.service.pojo.wxapp.pay.jsapi.JsApiVo;
import com.meidianyi.shop.service.shop.member.MemberService;
import com.meidianyi.shop.service.wechat.WxPayment;
import com.meidianyi.shop.service.wechat.WxPaymentAttachParam;
import com.meidianyi.shop.support.PemToPkcs12;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.meidianyi.shop.service.shop.order.store.StoreOrderService.HUNDRED;

/**
 * @author lixinguo
 */
@Service
public class MpPaymentService extends ShopBaseService {

	@Autowired
	protected DomainConfig domainConfig;

	@Autowired
	public PaymentService pay;
    @Autowired
    private WxSerMchConfig wxSerMchConfig;
    @Autowired
    private MemberService memberService;

	/**
	 * 得到店铺小程序支付实例
     *
	 * @return
	 */
	public WxPayment getMpPay() {
		MpAuthShopRecord mp = getMpAuthShop();
		WxPayConfig wxPayConfig = this.getWxPayConfig(mp);
		WxPayment wxPayment = new WxPayment();
		wxPayment.setConfig(wxPayConfig);
		return wxPayment;
	}

	/**
	 * 得到当前店铺小程序授权信息
     *
	 * @return
	 */
	public MpAuthShopRecord getMpAuthShop() {
		return saas.shop.mp.getAuthShopByShopId(getShopId());
	}

	/**
	 * 小程序支付配置
     *
	 * @param mp
	 * @return
	 */
	protected WxPayConfig  getWxPayConfig(MpAuthShopRecord mp) {
		byte[] keyContent = null;
		//子商户模式
		Byte subMch = 1;
        try {
            //TODO 后期加缓存
            if(subMch.equals(mp.getIsSubMerchant())) {
                keyContent = PemToPkcs12.pemToPkcs12(wxSerMchConfig.getWxSrvPayKey(), wxSerMchConfig.getWxSrvPayCert(), wxSerMchConfig.getWxSrvMchId().toCharArray());
            }else {
                keyContent = PemToPkcs12.pemToPkcs12(mp.getPayKeyContent(), mp.getPayCertContent(), mp.getPayMchId().toCharArray());
            }
        } catch (Exception e) {
            this.logger().error("pemToPkcs12 error, message: {}", e.getMessage());
        }
		WxPayConfig payConfig = new WxPayConfig();
		if(subMch.equals(mp.getIsSubMerchant())) {
		    //子商户模式
            payConfig.setAppId(wxSerMchConfig.getWxSrvAppId());
            payConfig.setMchId(wxSerMchConfig.getWxSrvMchId());
            payConfig.setMchKey(wxSerMchConfig.getWxSrvKey());
            payConfig.setSubAppId(mp.getAppId());
            payConfig.setSubMchId(mp.getPayMchId());
            payConfig.setKeyContent(keyContent);
        }else {
            //微信直连
            payConfig.setAppId(mp.getAppId());
            payConfig.setMchId(mp.getPayMchId());
            payConfig.setMchKey(mp.getPayKey());
            payConfig.setKeyContent(keyContent);
        }

		return payConfig;
	}

    /**
     * Wx query order.
     * <p>
     * 该接口提供所有微信支付订单的查询，商户可以通过查询订单接口主动查询订单状态，完成下一步的业务逻辑。
     * 需要调用查询接口的情况：
     * ◆ 当商户后台、网络、服务器等出现异常，商户系统最终未接收到支付通知；
     * ◆ 调用支付接口后，返回系统错误或未知交易状态情况；
     * ◆ 调用刷卡支付API，返回USERPAYING的状态；
     * ◆ 调用关单或撤销接口API之前，需确认支付状态；
     *
     * @param outTradeNo the out trade no
     * @return the wx pay order query result
     * @throws WxPayException the wx pay exception
     */
    public WxPayOrderQueryResult wxQueryOrder(String outTradeNo) throws WxPayException {
        WxPayment wxPayment = this.getMpPay();
        // 第一个参数为transactionId微信订单号，此处置为空字符串，使用第二个参数商家订单号outTradeNo查询订单
        return wxPayment.queryOrder(StringUtils.EMPTY, outTradeNo);
    }

    /**
     * Wx close order.
     * <p>
     * 以下情况需要调用关单接口：
     * 商户订单支付失败需要生成新单号重新发起支付，要对原订单号调用关单，避免重复支付；
     * 系统下单后，用户支付超时，系统退出不再受理，避免用户继续，请调用关单接口。
     * 注意：订单生成后不能马上调用关单接口，最短调用时间间隔为5分钟。
     *
     * @throws WxPayException the wx pay exception
     */
    public boolean wxCloseOrder(String outTradeNo) throws WxPayException {
        WxPayment wxPayment = this.getMpPay();
        wxPayment.closeOrder(outTradeNo);
        // 取消后再次查询结果是否为已取消
        WxPayOrderQueryResult queryResult = wxPayment.queryOrder(StringUtils.EMPTY, outTradeNo);
        if (Objects.isNull(queryResult)) {
            return true;
        }
        return WxPayConstants.WxpayTradeStatus.CLOSED.equals(queryResult.getTradeState());
    }

	/**
	 * 统一下单
     *
	 * @param clientIp  客户IP
	 * @param goodsName 商品名称
	 * @param orderSn   订单号
	 * @param amount    单位分
	 * @param openId    用户OpenId
	 * @return
	 * @throws WxPayException
	 */
    public WebPayVo wxUnitOrder(String clientIp, String goodsName, String orderSn, BigDecimal amount, String openId) throws WxPayException, MpException {
        logger().info("微信预支付调用接口start,clientIp:{},goodsName:{},orderSn:{},amount:{},openId:{}", clientIp,  goodsName,  orderSn,  amount,  openId);
		WxPayment wxPayment = this.getMpPay();
		WxPayUnifiedOrderRequest payInfo = WxPayUnifiedOrderRequest.newBuilder()
            .openid(StringUtils.isBlank(wxPayment.getConfig().getSubAppId()) ? openId : null)
            .subOpenid(StringUtils.isBlank(wxPayment.getConfig().getSubAppId()) ? null : openId)
            .outTradeNo(orderSn)
            // 订单总金额，单位为分
            .totalFee(amount.multiply(HUNDRED).intValue())
            .body(Util.filterEmoji(goodsName, ""))
            .tradeType(WxPayConstants.TradeType.JSAPI)
            .spbillCreateIp(clientIp)
            .notifyUrl(domainConfig.getWxMaPayNotifyUrl(this.getShopId()))
            .build();
		this.logger().info("PartnerKey is : {}", wxPayment.getConfig().getMchKey());
		//已经校验
		WxPayUnifiedOrderResult result = wxPayment.unifiedOrder(payInfo);
		this.logger().info("微信预支付调用接口result : {}", result);
        //获取前台支付调用参数
        WebPayVo webParam = getWebPayParam(result, wxPayment.getConfig());
        this.logger().info("前台支付调用参数result : {}", webParam);
		return webParam;
	}

    /**
     * Continue pay web pay vo.
     *
     * @param result   the result
     * @param prepayId the prepay id
     * @return the web pay vo
     * @throws MpException the mp exception
     */
    public WebPayVo continuePay(WxPayOrderQueryResult result, String prepayId) throws MpException {
        result.setTradeType(WxPayConstants.TradeType.JSAPI);
        WxPayment wxPayment = this.getMpPay();
        WebPayVo vo = null;
        Map<String, String> payInfo = new HashMap<>();
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonceStr = String.valueOf(System.currentTimeMillis());
        if (WxPayConstants.TradeType.NATIVE.equals(result.getTradeType())) {
            //TODO 原生扫码支付
        } else if (WxPayConstants.TradeType.APP.equals(result.getTradeType())) {
            //TODO App支付
        } else if (WxPayConstants.TradeType.JSAPI.equals(result.getTradeType())) {
            vo = getWebPayVo(result.getAppid(), result.getSubAppId(), wxPayment.getConfig(), prepayId, payInfo, timestamp, nonceStr);
        }
        return vo;
    }

    /**
     * 王帅
     * 获取web端调用微信支付参数
     * @param result result
     * @return WebPayVo
     * @source com.github.binarywang.wxpay.service.impl.BaseWxPayServiceImpl.getPayInfo()
     */
    private WebPayVo getWebPayParam(WxPayUnifiedOrderResult result, WxPayConfig config) throws MpException {
        WebPayVo vo = null;
	    //微信生成的预支付回话标识，用于后续接口调用中使用，该值有效期为2小时
        String prepayId = result.getPrepayId();
        if (StringUtils.isBlank(prepayId)) {
            logger().info(String.format("无法获取prepay id，错误代码： '%s'，信息：%s。", result.getErrCode(), result.getErrCodeDes()));
            throw new MpException(JsonResultCode.CODE_WX_PAY_PREPAY_ID_IS_NULL);
        }
        Map<String, String> payInfo = new HashMap<>();
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonceStr = String.valueOf(System.currentTimeMillis());
        if (WxPayConstants.TradeType.NATIVE.equals(result.getTradeType())) {
            //TODO 原生扫码支付
        } else if (WxPayConstants.TradeType.APP.equals(result.getTradeType())) {
            //TODO App支付
        } else if (WxPayConstants.TradeType.JSAPI.equals(result.getTradeType())) {
            vo = getWebPayVo(result.getAppid(), result.getSubAppId(), config, prepayId, payInfo, timestamp, nonceStr);
            vo.setResult(result);
        }
        return vo;
    }

    private WebPayVo getWebPayVo(String appid,String subAppId, WxPayConfig config, String prepayId, Map<String, String> payInfo, String timestamp, String nonceStr) {
        WebPayVo vo;
        if (StringUtils.isNotEmpty(subAppId)) {
            appid = subAppId;
        }
        //二次签名
        payInfo.put("appId", appid);
        payInfo.put("timeStamp", timestamp);
        payInfo.put("nonceStr", nonceStr);
        payInfo.put("package", "prepay_id=" + prepayId);
        payInfo.put("signType", WxPayConstants.SignType.MD5);
        String md5 = SignUtils.createSign(payInfo, WxPayConstants.SignType.MD5, config.getMchKey(), null);
        //公众号支付/小程序支付.
        vo = JsApiVo.builder().
            appId(appid).
            timeStamp(timestamp).
            nonceStr(nonceStr).
            packageAlias("prepay_id=" + prepayId).
            signType(WxPayConstants.SignType.MD5).
            paySign(md5).
            build();
        return vo;
    }

    /**
	 * 通过微信订单号退款
     *
	 * @param transactionId 微信订单号
	 * @param outRefundNo   退款单号
	 * @param refundFee     退款金额，单位分
	 * @param totalFee      订单总金额，单位分
	 * @return
	 * @throws WxPayException
	 */
	public WxPayRefundResult refundByTransactionId(String transactionId, String outRefundNo, Integer totalFee, Integer refundFee) throws WxPayException {
        if(Boolean.FALSE) {
            //TODO 扩展微信支付类型（国际、通联、子商账户）
            return null;
        }else {
            return refund(null, transactionId, outRefundNo, totalFee, refundFee);
        }

	}

	/**
	 * 通过订单号进行退款
     *
	 * @param outTradeNo  商家订单号
	 * @param outRefundNo 退款单号
	 * @param refundFee   退款金额，单位分
	 * @param totalFee    订单总金额，单位分
	 * @return
	 * @throws WxPayException
	 */
	public WxPayRefundResult refundByOutTradeNo(String outTradeNo, String outRefundNo, Integer refundFee,
			Integer totalFee) throws WxPayException {
		return this.refund(outTradeNo, null, outRefundNo, refundFee, totalFee);
	}

	/**
	 * 退款
     *
	 * @param outTradeNo    商家订单号
	 * @param transactionId 微信订单号
	 * @param outRefundNo   退款单号
	 * @param refundFee     退款金额，单位分
	 * @param totalFee      订单总金额，单位分
	 * @return WxPayRefundResult
	 * @throws WxPayException
	 */
	public WxPayRefundResult refund(String outTradeNo, String transactionId, String outRefundNo, Integer totalFee,
			Integer refundFee)
			throws WxPayException {
		WxPayment wxPayment = this.getMpPay();
		WxPayRefundRequest request = WxPayRefundRequest.newBuilder()
				.transactionId(transactionId)
				.outTradeNo(outTradeNo)
				.outRefundNo(outRefundNo)
				.refundFee(refundFee)
				.totalFee(totalFee).build();
		return wxPayment.refund(request);
	}

    /**
     * 企业付款到个人
     * @param mchAppid 公众账号appid
     * @param clientIp 来源IP
     * @param outTradeNumber 付款单号
     * @param openId 用户OpenId
     * @param realUserName 用户真实姓名
     * @param amount 付款金额，单位分
     * @param desc 描述
     * @return
     * @throws WxPayException
     */
    public EntPayResult companyPay(String mchAppid, String clientIp, String outTradeNumber, String openId, String realUserName,
                                   Integer amount, String desc)
        throws WxPayException {
        WxPayment wxPayment = this.getMpPay();
        EntPayRequest wxEntPayRequest = EntPayRequest.newBuilder()
            .mchAppid(mchAppid)
            .spbillCreateIp(clientIp)
            .openid(openId)
            .amount(amount)
            .description(desc)
            .reUserName(realUserName)
            .partnerTradeNo(outTradeNumber)
            .checkName("FORCE_CHECK")
            .build();
        EntPayResult entPayResult = wxPayment.getEntPayService().entPay(wxEntPayRequest);
        entPayResult.checkResult(wxPayment, WxPayConstants.SignType.MD5, true);
        return entPayResult;
    }

	/**
	 * 微信小程序支付回调
	 * @param orderResult
	 */
    public void onPayNotify(WxPayOrderNotifyResult orderResult) throws MpException, WxPayException {
		PaymentRecordParam param = PaymentRecordParam.builder()
				.payCode(PayCode.PAY_CODE_WX_PAY)
				.tradeNo(orderResult.getTransactionId())
				.trdaeStatus((byte)0)
				.trdaeOriginStatus(orderResult.getResultCode())
				.quantity(1)
				.orderSn(orderResult.getOutTradeNo())
				.totalFee(BaseWxPayResult.fenToYuan(orderResult.getTotalFee()))
				.buyerId(orderResult.getOpenid())
				.sellerId(orderResult.getMchId())
                .gmtCreate(DateUtils.dateFormatToTimeStamp(DateUtils.DATE_FORMAT_FULL_NO_UNDERLINE, orderResult.getTimeEnd()))
                .notifyTime(DateUtils.dateFormatToTimeStamp(DateUtils.DATE_FORMAT_FULL_NO_UNDERLINE, orderResult.getTimeEnd()))
                .gmtCloseTime(DateUtils.dateFormatToTimeStamp(DateUtils.DATE_FORMAT_FULL_NO_UNDERLINE, orderResult.getTimeEnd()))
				.created(Timestamp.valueOf(LocalDateTime.now()))
				.remark2(orderResult.toString())
				.build();
		pay.unionPayNotify(param);
	}

    /**
     * 现金红包查询
     * @param mchBillNo  订单号
     * @return
     * @throws WxPayException
     */
    public WxPayRedpackQueryResult queryRedpack(String mchBillNo) throws WxPayException {
        WxPayment wxPayment = this.getMpPay();
        WxPayRedpackQueryResult wxPayRedpackQueryResult = wxPayment.queryRedpack(mchBillNo);
        return wxPayRedpackQueryResult;
    }

    /**
     * 发放普通红包（场景：目前特约商户不支持'企业付款到个人接口'，现采用红包方式实现）
     *    场景金额限制------默认红包金额为1-200元，如有需要，可前往商户平台进行设置和申请
     *    其他限制------商户单日出资金额上限--100万元；单用户单日收款金额上限--1000元；单用户可领取红包个数上限--10个；
     *
     * @param linkOfficialAppId 子商户公众号id
     * @param mchBillNo 商户订单号
     * @param clientIp  ip
     * @param openId    openId
     * @param amount    金额（分）
     * @param wishing   红包祝福语
     * @param actName   活动名称
     * @param remark    备注
     * @return result
     * @throws WxPayException exc
     */
    public WxPaySendRedpackResult sendRedpack(String linkOfficialAppId, String mchBillNo, String clientIp, String openId, Integer amount, String wishing, String actName, String remark)
        throws WxPayException {
        WxPayment wxPayment = this.getMpPay();
        WxPaySendRedpackRequest requestParam = WxPaySendRedpackRequest.newBuilder()
            .mchBillNo(mchBillNo)
            .wxAppid(wxPayment.getConfig().getAppId())
            .msgAppid(linkOfficialAppId)
            .sendName(wxPayment.getShopName())
            .reOpenid(openId)
            .totalAmount(amount)
            .totalNum(1)
            .wishing(wishing)
            .clientIp(clientIp)
            .actName(actName)
            .remark(remark)
            .build();
        WxPaySendRedpackResult wxPaySendRedpackResult = wxPayment.sendRedpack(requestParam);
        wxPaySendRedpackResult.checkResult(wxPayment, WxPayConstants.SignType.MD5, true);
        return wxPaySendRedpackResult;
    }

    /**
     * 提现
     * @param orderSn orderSn
     * @param ip ip
     * @param realName realName
     * @param userId userId
     * @param type type
     * @param money money
     * @throws MpException mpe
     */
    public void pay2Person(String orderSn, String ip, String realName, Integer userId, Byte type, BigDecimal money) throws MpException {
        logger().info("pay2Person start");
        //小程序消息
        MpAuthShopRecord wxapp = saas.shop.mp.getAuthShopByShopId(getShopId());
        try {
            if(DistributionConstant.RT_WX_OPEN.equals(type)) {
                if(wxapp == null || StringUtils.isBlank(wxapp.getLinkOfficialAppId())) {
                    throw new MpException(JsonResultCode.DISTRIBUTOR_WITHDRAW_NO_FOCUS_WECHAT_OFFICIAL_ACCOUNTS);
                }
                String wxOpenId = memberService.getUserWxOpenId(userId);
                String openId = saas.shop.mpOfficialAccountUserService.getOpenIdFromMpOpenId(wxapp.getLinkOfficialAppId(), wxapp.getAppId(), wxOpenId);
                companyPay(wxapp.getLinkOfficialAppId(), ip, orderSn, openId, realName, BigDecimalUtil.multiply(money, new BigDecimal(Byte.valueOf(OrderConstant.TUAN_FEN_RATIO).toString())).intValue(), "佣金提现");
            }else if(DistributionConstant.RT_WX_MINI.equals(type)) {
                UserRecord userRecord = memberService.getUserRecordById(userId);
                companyPay(wxapp.getAppId(), ip, orderSn, userRecord.getWxOpenid(), realName, BigDecimalUtil.multiply(money, new BigDecimal(Byte.valueOf(OrderConstant.TUAN_FEN_RATIO).toString())).intValue(), "佣金提现");
            }else if(DistributionConstant.RT_SUB_MCH.equals(type)) {
                if(wxapp == null || StringUtils.isBlank(wxapp.getLinkOfficialAppId())) {
                    throw new MpException(JsonResultCode.DISTRIBUTOR_WITHDRAW_NO_FOCUS_WECHAT_OFFICIAL_ACCOUNTS);
                }
                UserRecord userRecord = memberService.getUserRecordById(userId);
                String wxOpenId = memberService.getUserWxOpenId(userId);
                String openId = saas.shop.mpOfficialAccountUserService.getOpenIdFromMpOpenId(wxapp.getLinkOfficialAppId(), wxapp.getAppId(), wxOpenId);
                sendRedpack(wxapp.getLinkOfficialAppId(), orderSn, ip, openId, BigDecimalUtil.multiply(money, new BigDecimal(Byte.valueOf(OrderConstant.TUAN_FEN_RATIO).toString())).intValue(), userRecord.getUsername() + "的红包", "佣金提现", "推广商品赚取更多佣金");
            }
        } catch (WxPayException e) {
            throw new MpException(JsonResultCode.DISTRIBUTOR_WITHDRAW_WX_ERROR,
                e.getMessage(), StringUtils.isBlank(e.getErrCodeDes()) ? e.getCustomErrorMsg() : e.getErrCodeDes());
        }
        logger().info("pay2Person end");
    }
}
