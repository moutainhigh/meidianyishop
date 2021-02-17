package com.meidianyi.shop.service.shop;

import com.meidianyi.shop.service.shop.anchor.AnchorPointsService;
import com.meidianyi.shop.service.shop.assess.AssessService;
import com.meidianyi.shop.service.shop.collection.CollectService;
import com.meidianyi.shop.service.shop.config.ConfigService;
import com.meidianyi.shop.service.shop.config.ShopBasicConfigService;
import com.meidianyi.shop.service.shop.config.ThirdAuthConfigService;
import com.meidianyi.shop.service.shop.config.TradeService;
import com.meidianyi.shop.service.shop.config.WxShoppingListConfigService;
import com.meidianyi.shop.service.shop.coupon.CouponMpService;
import com.meidianyi.shop.service.shop.coupon.CouponService;
import com.meidianyi.shop.service.shop.decoration.AdminDecorationService;
import com.meidianyi.shop.service.shop.decoration.AppletsJumpService;
import com.meidianyi.shop.service.shop.decoration.ChooseLinkService;
import com.meidianyi.shop.service.shop.decoration.MpDecorationService;
import com.meidianyi.shop.service.shop.decoration.PageClassificationService;
import com.meidianyi.shop.service.shop.department.DepartmentService;
import com.meidianyi.shop.service.shop.distribution.BrokerageStatisticalService;
import com.meidianyi.shop.service.shop.distribution.DistributorCheckService;
import com.meidianyi.shop.service.shop.distribution.DistributorGroupService;
import com.meidianyi.shop.service.shop.distribution.DistributorLevelService;
import com.meidianyi.shop.service.shop.distribution.DistributorListService;
import com.meidianyi.shop.service.shop.distribution.DistributorWithdrawService;
import com.meidianyi.shop.service.shop.distribution.MpDistributionGoodsService;
import com.meidianyi.shop.service.shop.distribution.MpDistributionService;
import com.meidianyi.shop.service.shop.distribution.MpDistributorLevelService;
import com.meidianyi.shop.service.shop.distribution.PromotionLanguageService;
import com.meidianyi.shop.service.shop.distribution.RebateGoodsService;
import com.meidianyi.shop.service.shop.distribution.RebateStrategyService;
import com.meidianyi.shop.service.shop.distribution.WithdrawService;
import com.meidianyi.shop.service.shop.doctor.DoctorLoginLogService;
import com.meidianyi.shop.service.shop.doctor.DoctorService;
import com.meidianyi.shop.service.shop.doctor.DoctorStatisticService;
import com.meidianyi.shop.service.shop.express.ExpressService;
import com.meidianyi.shop.service.shop.goods.ApiGoodsService;
import com.meidianyi.shop.service.shop.goods.FootPrintService;
import com.meidianyi.shop.service.shop.goods.GoodsAnalysisService;
import com.meidianyi.shop.service.shop.goods.GoodsRecommendService;
import com.meidianyi.shop.service.shop.goods.GoodsService;
import com.meidianyi.shop.service.shop.goods.GoodsWrapService;
import com.meidianyi.shop.service.shop.goods.MedicalGoodsService;
import com.meidianyi.shop.service.shop.goods.es.EsDataUpdateMqService;
import com.meidianyi.shop.service.shop.goods.es.EsGoodsCreateService;
import com.meidianyi.shop.service.shop.goods.es.EsGoodsSearchService;
import com.meidianyi.shop.service.shop.goods.es.goods.label.EsGoodsLabelCreateService;
import com.meidianyi.shop.service.shop.goods.es.goods.label.EsGoodsLabelSearchService;
import com.meidianyi.shop.service.shop.goods.es.goods.product.EsGoodsProductCreateService;
import com.meidianyi.shop.service.shop.goods.goodsimport.GoodsImportRecordService;
import com.meidianyi.shop.service.shop.goods.goodsimport.GoodsImportService;
import com.meidianyi.shop.service.shop.goods.mp.GoodsActivityShareRecordService;
import com.meidianyi.shop.service.shop.goods.mp.GoodsMpService;
import com.meidianyi.shop.service.shop.im.ImSessionService;
import com.meidianyi.shop.service.shop.image.ImageCategoryService;
import com.meidianyi.shop.service.shop.image.ImageService;
import com.meidianyi.shop.service.shop.image.QrCodeService;
import com.meidianyi.shop.service.shop.image.postertraits.PictorialIntegrationService;
import com.meidianyi.shop.service.shop.image.postertraits.UserCenterTraitService;
import com.meidianyi.shop.service.shop.market.bargain.BargainService;
import com.meidianyi.shop.service.shop.market.channel.ChannelService;
import com.meidianyi.shop.service.shop.market.channel.ChannelStatisticalService;
import com.meidianyi.shop.service.shop.market.commentaward.CommentAwardService;
import com.meidianyi.shop.service.shop.market.coopen.CoopenRecordService;
import com.meidianyi.shop.service.shop.market.coopen.CoopenService;
import com.meidianyi.shop.service.shop.market.coopen.EnterPolitelyService;
import com.meidianyi.shop.service.shop.market.couponpack.CouponPackService;
import com.meidianyi.shop.service.shop.market.firstspecial.FirstSpecialService;
import com.meidianyi.shop.service.shop.market.form.FormStatisticsService;
import com.meidianyi.shop.service.shop.market.freeshipping.FreeShippingService;
import com.meidianyi.shop.service.shop.market.friendpromote.FriendPromoteService;
import com.meidianyi.shop.service.shop.market.fullcut.MrkingStrategyService;
import com.meidianyi.shop.service.shop.market.gift.GiftService;
import com.meidianyi.shop.service.shop.market.givegift.GiveGiftService;
import com.meidianyi.shop.service.shop.market.goupbuy.GroupBuyListService;
import com.meidianyi.shop.service.shop.market.goupbuy.GroupBuyService;
import com.meidianyi.shop.service.shop.market.groupdraw.GroupDrawService;
import com.meidianyi.shop.service.shop.market.increasepurchase.IncreasePurchaseService;
import com.meidianyi.shop.service.shop.market.integralconvert.IntegralConvertService;
import com.meidianyi.shop.service.shop.market.integration.GroupIntegrationService;
import com.meidianyi.shop.service.shop.market.live.LiveService;
import com.meidianyi.shop.service.shop.market.lottery.LotteryService;
import com.meidianyi.shop.service.shop.market.message.MessageTemplateService;
import com.meidianyi.shop.service.shop.market.packagesale.PackSaleService;
import com.meidianyi.shop.service.shop.market.payaward.PayAwardService;
import com.meidianyi.shop.service.shop.market.presale.PreSaleOrderService;
import com.meidianyi.shop.service.shop.market.presale.PreSaleService;
import com.meidianyi.shop.service.shop.market.prize.PrizeRecordService;
import com.meidianyi.shop.service.shop.market.reduceprice.ReducePriceService;
import com.meidianyi.shop.service.shop.market.seckill.SeckillService;
import com.meidianyi.shop.service.shop.market.sharereward.ShareRewardService;
import com.meidianyi.shop.service.shop.market.sharereward.WxShareRewardService;
import com.meidianyi.shop.service.shop.marketcalendar.MarketCalendarService;
import com.meidianyi.shop.service.shop.medicine.MedicalHistoryService;
import com.meidianyi.shop.service.shop.member.AddressService;
import com.meidianyi.shop.service.shop.member.CardVerifyService;
import com.meidianyi.shop.service.shop.member.MemberService;
import com.meidianyi.shop.service.shop.member.ScoreCfgService;
import com.meidianyi.shop.service.shop.member.TagService;
import com.meidianyi.shop.service.shop.member.UserCardService;
import com.meidianyi.shop.service.shop.message.UserMessageService;
import com.meidianyi.shop.service.shop.operation.RecordAdminActionService;
import com.meidianyi.shop.service.shop.operation.RecordTradeService;
import com.meidianyi.shop.service.shop.order.OrderApiService;
import com.meidianyi.shop.service.shop.order.OrderReadService;
import com.meidianyi.shop.service.shop.order.OrderWriteService;
import com.meidianyi.shop.service.shop.order.action.AuditService;
import com.meidianyi.shop.service.shop.order.action.base.OrderOperateFactory;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import com.meidianyi.shop.service.shop.order.inquiry.InquiryOrderService;
import com.meidianyi.shop.service.shop.order.virtual.CouponPackOrderService;
import com.meidianyi.shop.service.shop.order.virtual.MemberCardOrderService;
import com.meidianyi.shop.service.shop.overview.AssetManagementService;
import com.meidianyi.shop.service.shop.overview.CommodityStatisticsService;
import com.meidianyi.shop.service.shop.overview.MallOverviewService;
import com.meidianyi.shop.service.shop.overview.OverviewService;
import com.meidianyi.shop.service.shop.overview.RealTimeOverviewService;
import com.meidianyi.shop.service.shop.overview.TransactionStatisticsService;
import com.meidianyi.shop.service.shop.patient.PatientService;
import com.meidianyi.shop.service.shop.payment.PaymentService;
import com.meidianyi.shop.service.shop.prescription.PrescriptionService;
import com.meidianyi.shop.service.shop.question.FeedbackService;
import com.meidianyi.shop.service.shop.rebate.DoctorWithdrawService;
import com.meidianyi.shop.service.shop.rebate.InquiryOrderRebateService;
import com.meidianyi.shop.service.shop.rebate.PrescriptionRebateService;
import com.meidianyi.shop.service.shop.recommend.RecommendService;
import com.meidianyi.shop.service.shop.store.statistic.StoreOrderSummaryTrendService;
import com.meidianyi.shop.service.shop.store.store.StoreGoodsService;
import com.meidianyi.shop.service.shop.store.store.StoreService;
import com.meidianyi.shop.service.shop.summary.portrait.PortraitService;
import com.meidianyi.shop.service.shop.summary.visit.AmountService;
import com.meidianyi.shop.service.shop.summary.visit.DistributionService;
import com.meidianyi.shop.service.shop.summary.visit.PageService;
import com.meidianyi.shop.service.shop.summary.visit.RetainService;
import com.meidianyi.shop.service.shop.task.ShopTaskService;
import com.meidianyi.shop.service.shop.task.department.DepartmentTaskService;
import com.meidianyi.shop.service.shop.task.doctor.DoctorTaskService;
import com.meidianyi.shop.service.shop.task.order.InquiryOrderTaskService;
import com.meidianyi.shop.service.shop.task.prescription.PrescriptionTaskService;
import com.meidianyi.shop.service.shop.task.store.StoreTaskService;
import com.meidianyi.shop.service.shop.title.TitleService;
import com.meidianyi.shop.service.shop.user.cart.CartService;
import com.meidianyi.shop.service.shop.user.message.MessageRecordService;
import com.meidianyi.shop.service.shop.user.message.SubscribeMessageService;
import com.meidianyi.shop.service.shop.user.message.WechatMessageTemplateService;
import com.meidianyi.shop.service.shop.user.user.MpOfficialAccountUserByShop;
import com.meidianyi.shop.service.shop.user.user.UserLoginRecordService;
import com.meidianyi.shop.service.shop.user.user.UserService;
import com.meidianyi.shop.service.shop.version.VersionService;
import com.meidianyi.shop.service.shop.video.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author 新国
 *
 */
@Service
public class ShopApplication {

	@Autowired
	public GoodsService goods;
	@Autowired
    public GoodsWrapService goodsWrap;
	@Autowired
	public GoodsMpService goodsMp;
    @Autowired
    public GoodsImportService goodsImportService;
    @Autowired
    public GoodsImportRecordService goodsImportRecordService;
	@Autowired
	public GoodsActivityShareRecordService goodsActivityShareRecord;
	@Autowired
	public ImageService image;
	@Autowired
	public QrCodeService qrCode;
	@Autowired
	public PictorialIntegrationService pictorialIntegrationService;
	@Autowired
	public ImageCategoryService imageCatgory;
	@Autowired
	public VideoService video;

	@Autowired
	public MpDecorationService mpDecoration;
    @Autowired
    public AdminDecorationService adminDecoration;
    @Autowired
    public UserMessageService userMessageService;

	/**
	 * 订单普通查询
	 */
	@Autowired
	public OrderReadService readOrder;
	/**
	 * 订单普通写操作
	 */
	@Autowired
	public OrderWriteService writeOrder;
    /**
     * 订单业务操作
     */
	@Autowired
	public OrderInfoService orderInfoService;
    /**
     * 订单对外统一接口
     */
    @Autowired
    public OrderApiService orderApi;
	/**
	 * 订单状态查询、操作(目前支持发货、退货等)
	 */
	@Autowired
	public OrderOperateFactory orderActionFactory;
	/**快递公司*/
	@Autowired
    public ExpressService express;
	@Autowired
	public PageClassificationService pageClassification;
	@Autowired
	public VersionService version;
	@Autowired
	public ConfigService config;
	@Autowired
	public StoreService store;
	@Autowired
	public ChooseLinkService chooselink;
	@Autowired
	public TradeService trade;
	@Autowired
	public GoodsRecommendService goodsRecommend;
	@Autowired
	public AmountService amount;
	@Autowired
	public OverviewService overview;
	@Autowired
	public DistributionService distribution;
	@Autowired
	public AppletsJumpService appletsJump;
	@Autowired
	public MallOverviewService mallOverview;
    /**
     * 门店商品
     */
    @Autowired
    public StoreGoodsService storeGoodsService;
    /**
     * 优惠券管理
     */
	@Autowired
	public CouponService coupon;
	@Autowired
	public RetainService retain;
	@Autowired
	public PageService page;
	@Autowired
	public RecordAdminActionService record;
	@Autowired
	public PortraitService portrait;
	@Autowired
	public RebateStrategyService rebateStrategy;
	@Autowired
	public RealTimeOverviewService realTimeOverview;
    @Autowired
    public CouponMpService mpCoupon; //小程序端优惠券
    /**
     * 分销员分组
     */
	@Autowired
	public DistributorGroupService distributorGroup;
    /**
     * 分销员等级配置
     */
	@Autowired
	public DistributorLevelService distributorLevel;
    /**
     * 分销员列表
     */
    @Autowired
	public DistributorListService distributorList;
    /**
     * 佣金统计
     */
    @Autowired
	public BrokerageStatisticalService brokerage;
    /**
     * 返利商品统计
     */
    @Autowired
	public RebateGoodsService rebateGoods;
    /**
     * 分销员审核
     */
    @Autowired
   	public DistributorCheckService distributorCheck;
    /**
     * 分销推广语
     */
    @Autowired
	public PromotionLanguageService promotionLanguage;
    /**
     * 分销提现
     */
    @Autowired
	public DistributorWithdrawService withdraw;

    /**
     * 分销提现
     */
    @Autowired
    public WithdrawService withdrawService;
    /**
     * mp分销service
     */
    @Autowired
    public MpDistributionService  mpDistribution;
    @Autowired
    public MpDistributorLevelService mpDisLevel;
    /**分销商品service*/
    @Autowired
    public MpDistributionGoodsService mpDisGoods;
	@Autowired
	public MemberCardOrderService memberCardOrder;
	@Autowired
	public TransactionStatisticsService transactionService;
	@Autowired
	public CouponPackOrderService couponPackOrder;
    @Autowired public AssessService assess;
	/**
	 * 满免包邮
	 */
	@Autowired
	public FreeShippingService freeShipping;
	/**
	 * 幸运抽奖
	 */
	@Autowired
	public LotteryService lottery;

	@Autowired
	public ShopTaskService shopTaskService;

    @Autowired
    public StoreTaskService storeTaskService;

	/**
	 * 组团购
	 */
	@Autowired
	public GroupBuyService groupBuy;
	@Autowired
	public GroupBuyListService groupBuyList;
	/**
	 * 会员
	 */
	@Autowired
	public MemberService member;
	/**
	 * 会员标签
	 */
	@Autowired
	public TagService tag;
	/**
	 * 积分管理
	 */
	@Autowired
	public ScoreCfgService score;

	/**
	 * 会员用户-持卡服务
	 */
	@Autowired
	public UserCardService userCard;
	@Autowired
	public ShopBasicConfigService shopBasicConfig;

	/**
	 * 砍价
	 */
	@Autowired
	public BargainService bargain;

	/**
	 * 秒杀
	 */
	@Autowired
	public SeckillService seckill;

	@Autowired
	public CommodityStatisticsService statisticsService;
	@Autowired
	public AssetManagementService assetService;

	/**
	 * 拼团抽奖
	 */
	@Autowired
	public GroupDrawService groupDraw;

	/**
	 * 瓜分积分
	 */
	@Autowired
	public GroupIntegrationService groupIntegration;
	/**
	 * 微信好物圈配置
	 */
	@Autowired
	public WxShoppingListConfigService shoppingListConfig;

	/**
	 * 好友助力活动
	 */
	@Autowired
	public FriendPromoteService friendPromoteService;

	@Autowired
	public FormStatisticsService formService;

	@Autowired
	public UserService user;

	@Autowired
	public UserLoginRecordService userLoginRecordService;

	/**
	 * 活动有礼
	 */
	@Autowired
	public CoopenService coopen;
	@Autowired
	public CoopenRecordService coopenRecord;

	/**
	 * 满折满减
	 */
	@Autowired
	public MrkingStrategyService mrkingStrategy;

	/**
	 * 支付有礼
	 */
	@Autowired
	public PayAwardService payAward;

	/**
	 * 我要送礼
	 */
	@Autowired public GiveGiftService giveGift;
	/**
	 * 奖品记录
	 */
	@Autowired
	public PrizeRecordService prizeRecord;
    /**
     * 定金膨胀
     */
    @Autowired public PreSaleService preSale;

	@Autowired
	public PreSaleOrderService preSaleOrder;

	/**
	 * 打包一口价
	 */
	@Autowired
	public PackSaleService packSale;

    /**
     * 限时降价
     */
    @Autowired public ReducePriceService reducePrice;

	/**
	 * 小程序跳转
	 */
	@Autowired
  	protected AppletsJumpService appletsJumpService;

	/** 加价购 */
	@Autowired
	public IncreasePurchaseService increaseService;

	/**
	 * 首单特惠
	 */
	@Autowired
	public FirstSpecialService firstSpecial;
	/**
	 * 积分兑换
	 */
	@Autowired
	public IntegralConvertService integralConvertService;

    /** 渠道页面分析 */
    @Autowired public ChannelService channelService;
    @Autowired public ChannelStatisticalService channelStatitical;
	@Autowired
	public PaymentService pay;

    /**
     * 赠品
     */
    @Autowired
    public GiftService gift;

	@Autowired public ShareRewardService shareRewardService;

	/**
	 * 评价有礼
	 */
	@Autowired
    public CommentAwardService commentAward;

    /**
     * 优惠券礼包
     */
    @Autowired
    public CouponPackService couponPack;
    /**
     * 消息推送
     */
    @Autowired
    public MessageTemplateService messageTemplateService;
    /**
     * 微信小程序/公众号模版消息
     */
    @Autowired
    public WechatMessageTemplateService wechatMessageTemplateService;
    /**
     * 交易明细
     */
    @Autowired
    public RecordTradeService recordTradeService;

    /**
     * 店铺库的公众号用户
     */
	@Autowired
	public MpOfficialAccountUserByShop officialAccountUser;
	/**
	 * 商品收藏
	 */
	@Autowired
	public CollectService collect;

	/**
	 * 购物车
	 */
	@Autowired
	public CartService cart;

    @Autowired
    public EsGoodsCreateService esGoodsCreateService;
    @Autowired
    public EsDataUpdateMqService esDataUpdateMqService;
    @Autowired
    public EsGoodsSearchService esGoodsSearchService;
    @Autowired
    public EsGoodsLabelCreateService esGoodsLabelCreateService;
    @Autowired
    public EsGoodsLabelSearchService esGoodsLabelSearchService;
    @Autowired
    public EsGoodsProductCreateService esGoodsProductCreateService;

    @Autowired
    public UserCenterTraitService  ucTraitService;
	/**
	 * 足迹
	 */
	@Autowired
    public FootPrintService footPrintService;
	/**
	 * 用户卡审核
	 */
	@Autowired
	public CardVerifyService cardVerifyService;

	/**
	 * 好物圈相关接口
	 */
	@Autowired
	public RecommendService recommendService;

	/**
	 * 用户地址
	 */
	@Autowired
	public AddressService addressService;

	/**
	 * 小程序订阅消息
	 */
	@Autowired
	public SubscribeMessageService subservice;

    @Autowired
    public EnterPolitelyService enterPolitelyService;

    /**
     * 小程序端分享有礼
     */
    @Autowired
    public WxShareRewardService wxShareReward;

    /**
     * 小程序公众号短信发送记录表
     */
    @Autowired
    public MessageRecordService msgRecordService;
    /**
     * 第三方对接配置
     */
    @Autowired
    public ThirdAuthConfigService thirdAuthService;

    /**
     * 第三方对接
     */
    @Autowired
    public ApiGoodsService apiGoodsService;

    @Autowired
    public FeedbackService feedbackService;

    /**
     * 小程序直播
     */
    @Autowired
    public LiveService liveService;
    /**
     * 营销日历
     */
    @Autowired
    public MarketCalendarService calendarService;

    /**
     * 科室
     */
    @Autowired
    public DepartmentService departmentService;

    /**
     * 职称
     */
    @Autowired
    public TitleService titleService;

    /**
     * 医师
     */
    @Autowired
    public DoctorService doctorService;

    /**
     * 患者
     */
    @Autowired
    public PatientService patientService;

    /**
     * 药品
     */
    @Autowired
    public MedicalGoodsService medicalGoodsService;

    /**
     * 病历
     */
    @Autowired
    public MedicalHistoryService medicalHistoryService;

    @Autowired
    public AuditService auditService;

    /**
     * 处方
     */
    @Autowired
    public PrescriptionService prescriptionService;

    /**
     * 问诊订单
     */
    @Autowired
    public InquiryOrderService inquiryOrderService;
    /**
     * 问诊订单任务
     */
    @Autowired
    public InquiryOrderTaskService inquiryOrderTaskService;

    /**
     * 商品
     */
    @Autowired
    public GoodsService goodsService;
    /**
     * 处方任务
     */
    @Autowired
    public PrescriptionTaskService prescriptionTaskService;
    /**咨询聊天*/
    @Autowired
    public ImSessionService imSessionService;
    /**
     * 处方返利
     */
    @Autowired
    public PrescriptionRebateService prescriptionRebateService;
    /**
     * 咨询返利
     */
    @Autowired
    public InquiryOrderRebateService inquiryOrderRebateService;
    /**
     * 医师提现
     */
    @Autowired
    public DoctorWithdrawService doctorWithdrawService;
    /**
     * 门店订单统计
     */
    @Autowired
    public StoreOrderSummaryTrendService storeSummary;

    /**
     * 科室信息统计
     */
    @Autowired
    public DepartmentTaskService departmentTaskService;

    /**
     * 医师业绩统计
     */
    @Autowired
    public DoctorTaskService doctorTaskService;

    /**
     * 医师数据统计
     */
    @Autowired
    public DoctorStatisticService doctorStatisticService;

    /**
     * 热销商品数据统计
     */
    @Autowired
    public GoodsAnalysisService goodsAnalysisService;

    /**
     * 医师登录统计
     */
    @Autowired
    public DoctorLoginLogService doctorLoginLogService;
	/**
	 * 埋点
	 */
	@Autowired
	public AnchorPointsService anchorPointsService;

}
