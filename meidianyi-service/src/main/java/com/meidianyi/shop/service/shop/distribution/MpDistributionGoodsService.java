package com.meidianyi.shop.service.shop.distribution;

import com.meidianyi.shop.common.foundation.data.DistributionConstant;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.DomainConfig;
import com.meidianyi.shop.dao.shop.distribution.PromotionLanguageDao;
import com.meidianyi.shop.dao.shop.distribution.UserPromotionLanguageDao;
import com.meidianyi.shop.dao.shop.goods.GoodsDao;
import com.meidianyi.shop.db.shop.tables.records.*;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.config.distribution.DistributionParam;
import com.meidianyi.shop.service.pojo.shop.coupon.CouponListVo;
import com.meidianyi.shop.service.pojo.shop.coupon.MpGetCouponParam;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributionStrategyParam;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorLevelVo;
import com.meidianyi.shop.service.pojo.shop.distribution.RebateRatioVo;
import com.meidianyi.shop.service.pojo.shop.distribution.UserDistributionVo;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.wxapp.coupon.CouponPageDecorationVo;
import com.meidianyi.shop.service.pojo.wxapp.distribution.BaseGoodsVo;
import com.meidianyi.shop.service.pojo.wxapp.distribution.GoodsRebateChangePriceVo;
import com.meidianyi.shop.service.pojo.wxapp.distribution.RebateGoodsCfgParam;
import com.meidianyi.shop.service.pojo.wxapp.distribution.RebateGoodsCfgVo;
import com.meidianyi.shop.service.pojo.wxapp.distribution.ShareUserInfoParam;
import com.meidianyi.shop.service.pojo.wxapp.distribution.ShareUserInfoVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailMpBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.GoodsDetailMpParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.GoodsPrdMpVo;
import com.meidianyi.shop.service.shop.config.DistributionConfigService;
import com.meidianyi.shop.service.shop.coupon.CouponMpService;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.meidianyi.shop.common.foundation.data.DistributionConstant.*;
import static com.meidianyi.shop.db.shop.Tables.*;

/**
 * @Author 常乐
 * @Date 2020-03-04
 */
@Service
public class MpDistributionGoodsService extends ShopBaseService {

    @Autowired
    public DistributionConfigService distributionConf;

    @Autowired
    public UserRebatePriceService userRebatePrice;

    @Autowired
    public DistributorLevelService distributorLevel;

    @Autowired
    protected DomainConfig domainConfig;

    @Autowired
    protected CouponMpService mpCoupon;
    @Autowired
    protected PromotionLanguageDao promotionLanguageDao;
    @Autowired
    protected UserPromotionLanguageDao userPromotionLanguageDao;
    @Autowired
    protected GoodsDao goodsDao;

    public RebateRatioVo goodsRebateInfo(Integer goodsId,Integer catId,Integer sortId,Integer userId){
        //获取用户分销等级
        UserDistributionVo distributionLevel = this.userDistributionLevel(userId);
        DistributionStrategyParam goodsRebateStrategy = this.distributionStrategyInfo(goodsId,catId,sortId);
        if(goodsRebateStrategy == null){
            return null;
        }else{
            RebateRatioVo userRebateRatio = this.getUserRebateRatio(userId, distributionLevel, goodsRebateStrategy);
            //获取邀请用户下那首单返利配置
            if(userRebateRatio.getFirstRebate() != null && userRebateRatio.getFirstRebate() == 0){
                userRebateRatio.setFirstRatio(null);
            }
            //返利成本价保护
            costPriceProtect(userRebateRatio,goodsId,goodsRebateStrategy.getCostProtection());
            return userRebateRatio;
        }
    }

    /**
     * 分销配置
     * @param goodsId
     * @param catId
     * @param sortId
     * @return
     */
    public DistributionStrategyParam distributionStrategyInfo(Integer goodsId,Integer catId,Integer sortId){
        //分销配置
        DistributionParam distributionCfg = distributionConf.getDistributionCfg();

        //分销开关开启
        if(distributionCfg != null && distributionCfg.getStatus() == 1){
            //返利策略
            DistributionStrategyParam goodsRebateStrategy = this.getGoodsRebateStrategy(goodsId, catId, sortId);
            return goodsRebateStrategy;
        }
        return null;
    }

    /**
     * 进行中的返利策略
     * @return
     */
    public List<DistributionStrategyParam> goingStrategy(){
        Timestamp nowDate = Util.currentTimeStamp();
        Result<Record> fetch = db().select().from(DISTRIBUTION_STRATEGY).where(DISTRIBUTION_STRATEGY.STATUS.eq((byte) 0))
            .and(DISTRIBUTION_STRATEGY.DEL_FLAG.eq((byte) 0))
            .and(DISTRIBUTION_STRATEGY.START_TIME.le(nowDate)).and(DISTRIBUTION_STRATEGY.END_TIME.ge(nowDate))
            .orderBy(DISTRIBUTION_STRATEGY.STRATEGY_LEVEL.desc(), DISTRIBUTION_STRATEGY.CREATE_TIME.desc()).fetch();
        if(fetch != null){
            return fetch.into(DistributionStrategyParam.class);
        }else{
            return null;
        }
    }

    /**
     * 商品返利策略
     * @param goodsId
     * @param catId
     * @param sortId
     * @return
     */
    public DistributionStrategyParam getGoodsRebateStrategy(Integer goodsId,Integer catId,Integer sortId){
        List<DistributionStrategyParam> distributionStrategyVos = this.goingStrategy();
        return getGoodsStrategy(goodsId, sortId, distributionStrategyVos);
    }

    /**
     * 获取商品返利逻辑
     * @param goodsId
     * @param sortId
     * @param distributionStrategyVos
     * @return
     */
    public DistributionStrategyParam getGoodsStrategy(Integer goodsId, Integer sortId, List<DistributionStrategyParam> distributionStrategyVos) {
        for (DistributionStrategyParam rebateStrategy:distributionStrategyVos){
            //根据返利策略等级优先级排序、全部商品适用
            if(DistributionConstant.ALL_GOODS.equals(rebateStrategy.getRecommendType())){
                return rebateStrategy;
            }

            //适用商品判断,适用当前商品
            if(rebateStrategy.getRecommendGoodsId() != null){
                List<Integer> goodsIds = Util.stringToList(rebateStrategy.getRecommendGoodsId());
                if(goodsIds.contains(goodsId)){
                    return rebateStrategy;
                }
            }

            if(rebateStrategy.getRecommendSortId() != null){
                List<Integer> sortIds = Util.stringToList(rebateStrategy.getRecommendSortId());
                if(sortIds.contains(sortId)){
                    return rebateStrategy;
                }
            }
        }
        return null;
    }

    /**
     * 获取用户分销等级
     * @param userId
     * @return
     */
    public UserDistributionVo userDistributionLevel(Integer userId){
        Record record = db().select(USER.DISTRIBUTOR_LEVEL, USER.IS_DISTRIBUTOR, USER.INVITE_ID).from(USER).where(USER.USER_ID.eq(userId)).fetchOne();
        if(record != null){
            return record.into(UserDistributionVo.class);
        }else{
            return null;
        }
    }

    public RebateRatioVo getUserRebateRatio(Integer userId,UserDistributionVo distributionLevel,DistributionStrategyParam goodsRebateStrategy){
        List<Byte> levels = new ArrayList<Byte>(){
            {add((byte)1);add((byte)2);add((byte)3);add((byte)4);add((byte)5);}
        };

        //获取等级状态
        Record record = db().select().from(DISTRIBUTOR_LEVEL).where(DISTRIBUTOR_LEVEL.LEVEL_ID.eq(distributionLevel.getDistributorLevel())).fetchOne();
        DistributorLevelVo levelInfo = null;
        if(record != null){
             levelInfo = record.into(DistributorLevelVo.class);
        }

        if(levelInfo != null && levelInfo.getLevelStatus() != 1){
            //return 该分销员等级未开启
        }

        RebateRatioVo rebateRatio = new RebateRatioVo();
        if(levels.contains(distributionLevel.getDistributorLevel())){
            Byte level = distributionLevel.getDistributorLevel();
            rebateRatio.setSelfPurchase(goodsRebateStrategy.getSelfPurchase());
            getRebateRatio(goodsRebateStrategy, rebateRatio, level);
            rebateRatio.setFirstRebate(goodsRebateStrategy.getFirstRebate());
        }
        return rebateRatio;
    }

    private void getRebateRatio(DistributionStrategyParam goodsRebateStrategy, RebateRatioVo rebateRatio, Byte level) {
        if(level.equals(LEVEL_1)){
            rebateRatio.setRebateId(goodsRebateStrategy.getId());
            rebateRatio.setFirstRatio(goodsRebateStrategy.getFirstRatio());
            rebateRatio.setFanliRatio(goodsRebateStrategy.getFanliRatio());
            rebateRatio.setRebateRatio(goodsRebateStrategy.getRebateRatio());
            rebateRatio.setStrategyType(goodsRebateStrategy.getStrategyType());
        }
        if(level.equals(DistributionConstant.LEVEL_2)){
            rebateRatio.setRebateId(goodsRebateStrategy.getId());
            rebateRatio.setFirstRatio(goodsRebateStrategy.getFirstRatio2());
            rebateRatio.setFanliRatio(goodsRebateStrategy.getFanliRatio2());
            rebateRatio.setRebateRatio(goodsRebateStrategy.getRebateRatio2());
            rebateRatio.setStrategyType(goodsRebateStrategy.getStrategyType());
        }
        if(level.equals(DistributionConstant.LEVEL_3)){
            rebateRatio.setRebateId(goodsRebateStrategy.getId());
            rebateRatio.setFirstRatio(goodsRebateStrategy.getFirstRatio3());
            rebateRatio.setFanliRatio(goodsRebateStrategy.getFanliRatio3());
            rebateRatio.setRebateRatio(goodsRebateStrategy.getRebateRatio3());
            rebateRatio.setStrategyType(goodsRebateStrategy.getStrategyType());
        }
        if(level.equals(DistributionConstant.LEVEL_4)){
            rebateRatio.setRebateId(goodsRebateStrategy.getId());
            rebateRatio.setFirstRatio(goodsRebateStrategy.getFirstRatio4());
            rebateRatio.setFanliRatio(goodsRebateStrategy.getFanliRatio4());
            rebateRatio.setRebateRatio(goodsRebateStrategy.getRebateRatio4());
            rebateRatio.setStrategyType(goodsRebateStrategy.getStrategyType());
        }
        if(level.equals(DistributionConstant.LEVEL_5)){
            rebateRatio.setRebateId(goodsRebateStrategy.getId());
            rebateRatio.setFirstRatio(goodsRebateStrategy.getFirstRatio5());
            rebateRatio.setFanliRatio(goodsRebateStrategy.getFanliRatio5());
            rebateRatio.setRebateRatio(goodsRebateStrategy.getRebateRatio5());
            rebateRatio.setStrategyType(goodsRebateStrategy.getStrategyType());
        }
    }

    /**
     *
     * @param userInfo
     * @param goodsStrategy 返利策略
     * @param cfg 配置
     * @return
     */
    public RebateRatioVo getUserRebateRatio(UserRecord userInfo, DistributionStrategyParam goodsStrategy, DistributionParam cfg) {
        if(userInfo == null) {
            logger().info("分销员不存在");
            return null;
        }
        if(cfg.getJudgeStatus() == OrderConstant.YES && Byte.valueOf(OrderConstant.NO).equals(userInfo.getIsDistributor())) {
            logger().info("用户非审核分销员");
            return null;
        }
        //异常分销等级重置
        if(userInfo.getDistributorLevel() < LEVEL_1 || userInfo.getDistributorLevel() > LEVEL_5) {
            userInfo.setDistributorLevel(LEVEL_1);
        }
        //等级详情
        DistributorLevelVo levelInfo = distributorLevel.getOneLevelInfo(userInfo.getDistributorLevel());
        if(levelInfo == null || levelInfo.getLevelStatus() == OrderConstant.NO) {
            logger().info("该分销员等级未开启，level：{}", userInfo.getDistributorLevel());
            return null;
        }
        //该等级返利详情
        RebateRatioVo rebateRatio = new RebateRatioVo();
        getRebateRatio(goodsStrategy, rebateRatio, userInfo.getDistributorLevel());
        return rebateRatio;
    }

    /**
     * 商品分销改价页面信息
     * @param param
     * @return
     */
    public GoodsRebateChangePriceVo rebateGoodsCfg(RebateGoodsCfgParam param){
        //商品基本信息
        BaseGoodsVo goods = db().select(GOODS.GOODS_ID, GOODS.GOODS_NAME, GOODS.GOODS_IMG).from(GOODS).where(GOODS.GOODS_ID.eq(param.getGoodsId())).fetchOne().into(BaseGoodsVo.class);
        goods.setGoodsImg(domainConfig.imageUrl(goods.getGoodsImg()));

        //商品分销改价信息
        List<RebateGoodsCfgVo> rebatePrice = db().select(GOODS_SPEC_PRODUCT.PRD_ID, GOODS_SPEC_PRODUCT.PRD_PRICE, GOODS_SPEC_PRODUCT.PRD_DESC,
            GOODS_REBATE_PRICE.ADVISE_PRICE, GOODS_REBATE_PRICE.MIN_PRICE, GOODS_REBATE_PRICE.MAX_PRICE)
            .from(GOODS_SPEC_PRODUCT)
            .leftJoin(GOODS_REBATE_PRICE).on(GOODS_SPEC_PRODUCT.PRD_ID.eq(GOODS_REBATE_PRICE.PRODUCT_ID))
            .where(GOODS_SPEC_PRODUCT.GOODS_ID.eq(param.getGoodsId()))
            .and(GOODS_REBATE_PRICE.DEL_FLAG.eq((byte) 0))
            .fetch().into(RebateGoodsCfgVo.class);

        //推广赠送优惠券是否开启
        Byte sendCoupon = db().select(DISTRIBUTION_STRATEGY.SEND_COUPON).from(DISTRIBUTION_STRATEGY).where(DISTRIBUTION_STRATEGY.ID.eq(param.getRebateId())).fetchOne().into(Byte.class);

        List<CouponPageDecorationVo> allCoupon = null;
        if(sendCoupon.equals(DistributionConstant.SEND_COUPON)){
            allCoupon = mpCoupon.allGoodsCoupon();
        }

        GoodsRebateChangePriceVo goodsRebateChangePriceVo = new GoodsRebateChangePriceVo();
        goodsRebateChangePriceVo.setGoods(goods);

        goodsRebateChangePriceVo.setRebatePrice(rebatePrice);
        goodsRebateChangePriceVo.setCouponList(allCoupon);
        return goodsRebateChangePriceVo;
    }

    /**
     * 保存/更新商品分销价
     * @param param
     */
    public void addRebatePrice(GoodsDetailMpBo goodsDetailMpBo,GoodsDetailMpParam param){
        UserRebatePriceRecord userRebatePrice = new UserRebatePriceRecord();
        long rebateTime1 = param.getRebateConfig().getRebateTime();
        int addTime = 24*60*60;
        long toTime = (rebateTime1 + addTime) * 1000;

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String rebateTime = simpleDateFormat.format(toTime);
        Timestamp rebateToTime = Timestamp.valueOf(rebateTime);

        Timestamp nowTime = Util.currentTimeStamp();

        //添加分销改价记录
        addRebatePriceRecord(param);

        //判断当前用户对该商品是否进行过分销改价
        param.getRebateConfig().getRebatePrice().forEach((prdId,prdPrice)->{
            Record record = db().select().from(USER_REBATE_PRICE).where(USER_REBATE_PRICE.USER_ID.eq(param.getUserId()))
                .and(USER_REBATE_PRICE.PRODUCT_ID.eq(prdId))
                .fetchOne();
            userRebatePrice.setUserId(param.getUserId());
            userRebatePrice.setGoodsId(param.getGoodsId());
            userRebatePrice.setProductId(prdId);
            userRebatePrice.setAdvicePrice(prdPrice);
            userRebatePrice.setExpireTime(rebateToTime);
//            添加商品分销价
            if(record == null) {
                db().executeInsert(userRebatePrice);
            }else{
                UserRebatePriceRecord rebateInfo = record.into(UserRebatePriceRecord.class);
                //更新商品分销价
               db().update(USER_REBATE_PRICE).set(userRebatePrice).where(USER_REBATE_PRICE.ID.eq(rebateInfo.getId())).execute();
            }
            Record record1 = db().select().from(USER_REBATE_PRICE).where(USER_REBATE_PRICE.USER_ID.eq(param.getUserId()))
                .and(USER_REBATE_PRICE.PRODUCT_ID.eq(prdId)).and(USER_REBATE_PRICE.EXPIRE_TIME.gt(nowTime))
                .fetchOne();
            if(record1 != null){
                for(GoodsPrdMpVo item : goodsDetailMpBo.getProducts()){
                    if(item.getPrdId().equals(prdId)){
                        item.setPrdRealPrice(prdPrice);
                    }
                }
            }
        });
    }

    /**
     * 分销员信息
     * @param param
     * @return
     */
    public ShareUserInfoVo getShareUserInfo(ShareUserInfoParam param){
        ShareUserInfoVo info = db().select().from(USER).leftJoin(USER_DETAIL).on(USER.USER_ID.eq(USER_DETAIL.USER_ID)).where(USER.USER_ID.eq(param.getInviteId())).fetchOne().into(ShareUserInfoVo.class);
        return info;
    }

    /**
     * 分销赠送优惠券
     * @param param
     * @return
     */
    public List<CouponListVo> sendCoupon(GoodsDetailMpParam param){
        MpGetCouponParam mpGetCouponParam = new MpGetCouponParam();
        mpGetCouponParam.setUserId(param.getUserId());
        List<Integer> couponIds = Util.stringToList(param.getRebateConfig().getCouponIds());

        List<Integer> effectiveCoupon = new ArrayList<>();
        //发放优惠券
        for(Integer couponId:couponIds){
            //当前用户已领取优惠券数量
            Record1<Integer> integerRecord1 = db().selectCount().from(CUSTOMER_AVAIL_COUPONS).where(CUSTOMER_AVAIL_COUPONS.ACT_ID.eq(couponId)).fetchOne();
            int hasReceive = 0;
            if(integerRecord1 != null){
                hasReceive = integerRecord1.into(Integer.class);
            }
            //判断优惠券库存和每人限领
            MrkingVoucherRecord info = db().select().from(MRKING_VOUCHER).where(MRKING_VOUCHER.ID.eq(couponId)).fetchOne().into(MrkingVoucherRecord.class);

            boolean b = !((info.getSurplus() <= 0 && info.getLimitSurplusFlag() == 0)
                || (hasReceive >= info.getReceivePerPerson() && info.getReceivePerPerson() != 0));
            if(b){
                mpGetCouponParam.setCouponId(couponId);
                Byte res = mpCoupon.fetchCoupon(mpGetCouponParam);

                effectiveCoupon.add(couponId);
            }
        }
        //返回优惠券信息
        List<CouponListVo> info = db().select().from(MRKING_VOUCHER).where(MRKING_VOUCHER.ID.in(effectiveCoupon)).fetch().into(CouponListVo.class);
        return info;
    }

    /**
     * 添加分销改价记录
     * @param param
     * @return
     */
    private void addRebatePriceRecord(GoodsDetailMpParam param){
        String rebateConfigInfo = Util.toJson(param.getRebateConfig());
        String dataSign = Util.md5(rebateConfigInfo);
        Integer into = db().selectCount().from(REBATE_PRICE_RECORD).where(REBATE_PRICE_RECORD.DATA_SIGN.eq(dataSign)).fetchOne().into(Integer.class);
        if (into < 1) {
            RebatePriceRecordRecord rebatePriceRecord = new RebatePriceRecordRecord();
            rebatePriceRecord.setDataSign(dataSign);
            rebatePriceRecord.setRebateData(rebateConfigInfo);
            rebatePriceRecord.setUserId(param.getUserId());
            db().executeInsert(rebatePriceRecord);
        }
    }

    /**
     * 存在有效分销改价记录时从商品详情页进入显示分销价
     * @param goodsDetailMpBo
     * @param param
     */
    public void isShowRebatePrice(GoodsDetailMpBo goodsDetailMpBo,GoodsDetailMpParam param){
        Timestamp nowTime = Util.currentTimeStamp();
        //获取商品规格id
        List<Integer> prdIds = db().select(GOODS_SPEC_PRODUCT.PRD_ID)
            .from(GOODS_SPEC_PRODUCT)
            .where(GOODS_SPEC_PRODUCT.GOODS_ID.eq(param.getGoodsId()))
            .fetchInto(Integer.class);
        for(Integer prdId:prdIds){
            Record record1 = db().select().from(USER_REBATE_PRICE).where(USER_REBATE_PRICE.USER_ID.eq(param.getUserId()))
                .and(USER_REBATE_PRICE.PRODUCT_ID.eq(prdId)).and(USER_REBATE_PRICE.EXPIRE_TIME.gt(nowTime))
                .fetchOne();
            if(record1 != null){
                UserRebatePriceRecord reabatePriceInfo = record1.into(UserRebatePriceRecord.class);
                for(GoodsPrdMpVo item : goodsDetailMpBo.getProducts()){
                    if(item.getPrdId().equals(reabatePriceInfo.getProductId())){
                        item.setPrdRealPrice(reabatePriceInfo.getAdvicePrice());
                    }
                }
            }
        }
    }

    /**
     * 获取商品分销推广语
     * @param goodsId
     * @return
     */
    public String getGoodsPromotionLanguage(Integer userId, Integer goodsId){
        // 1.获取商品推广语
        String promotionLanguage = goodsDao.getPromotionLanguage(goodsId);

        if (StringUtils.isNotEmpty(promotionLanguage)) {
            return promotionLanguage;
        }

        // 2.获取分销中心默认推广语
        Integer languageId = userPromotionLanguageDao.getLanIdByUserId(userId);
        if (languageId != null) {
            promotionLanguage = promotionLanguageDao.getPromotionLanguage(languageId);
        }

        return promotionLanguage;
    }
    /**
     * 是否为多规格商品
     * @param goodsId
     * @return
     */
    public int isMorePrd(Integer goodsId){
        Integer into = db().selectCount().from(GOODS_SPEC_PRODUCT).where(GOODS_SPEC_PRODUCT.GOODS_ID.eq(goodsId)).fetchOne().into(Integer.class);
        //多规格
        if(into.compareTo(1)>0){
            return 1;
        }else{
            return 0;
        }
    }
    /**
     * 多规格商品最高规格价
     * @param goodsId
     * @return
     */
    public BigDecimal highPrdPrice(Integer goodsId){
        BigDecimal price = db().select(GOODS_SPEC_PRODUCT.PRD_PRICE).from(GOODS_SPEC_PRODUCT).where(GOODS_SPEC_PRODUCT.GOODS_ID.eq(goodsId))
            .orderBy(GOODS_SPEC_PRODUCT.PRD_PRICE.desc()).limit(1).fetchOne().into(BigDecimal.class);
        return price;
    }

    /**
     * 多规格商品最低规格价
     * @param goodsId
     * @return
     */
    public BigDecimal lowPrdPrice(Integer goodsId){
        BigDecimal price = db().select(GOODS_SPEC_PRODUCT.PRD_PRICE).from(GOODS_SPEC_PRODUCT).where(GOODS_SPEC_PRODUCT.GOODS_ID.eq(goodsId))
            .orderBy(GOODS_SPEC_PRODUCT.PRD_PRICE.asc()).limit(1).fetchOne().into(BigDecimal.class);
        return price;
    }

    /**
     * 多规格商品最高划线价
     * @param goodsId
     * @return
     */
    public BigDecimal highPrdLinePrice(Integer goodsId){
        BigDecimal linePrice = db().select(GOODS_SPEC_PRODUCT.PRD_MARKET_PRICE).from(GOODS_SPEC_PRODUCT).where(GOODS_SPEC_PRODUCT.GOODS_ID.eq(goodsId))
            .orderBy(GOODS_SPEC_PRODUCT.PRD_MARKET_PRICE.desc()).limit(1).fetchOne().into(BigDecimal.class);
        return linePrice;
    }
    /**
     * 获取商品多图
     * @param goodsId
     * @return
     */
    public List<String> goodsImgs(Integer goodsId){
        Result<Record1<String>> fetch = db().select(GOODS_IMG.IMG_URL).from(GOODS_IMG).where(GOODS_IMG.GOODS_ID.eq(goodsId)).fetch();
        if(fetch != null){
            List<String> imgs = fetch.into(String.class);
            List<String> googsMoreImgs = new ArrayList<String>();
            for (String img:imgs){
                googsMoreImgs.add(imageUrl(img));
            }
            return googsMoreImgs;
        }else{
            return null;
        }
    }
    /**
     * 返利成本保护价，多规格商品按规格算
     * @param
     * @return
     */
    public void costPriceProtect(RebateRatioVo userRebateRatio,Integer goodsId,Byte costProtection){
        //商品是否为多规格商品
        List<GoodsSpecProductRecord> into = db().select().from(GOODS_SPEC_PRODUCT).where(GOODS_SPEC_PRODUCT.GOODS_ID.eq(goodsId)).fetch().into(GoodsSpecProductRecord.class);
        //最高直接返利金额
        BigDecimal highDirectlyRebatePrice = BigDecimal.ZERO;
        //最高间接间接返利金额
        BigDecimal highIndirectRebatePrice = BigDecimal.ZERO;
        //最高首单返利金额
        BigDecimal highFirstRebatePrice = BigDecimal.ZERO;
        BigDecimal rat = new BigDecimal("100");
        //佣金计算方式
        if(userRebateRatio.getStrategyType().equals(STRATEGY_TYPE_GOODS_RPICE)) {
            logger().info("根据商品支付金额返利");
            for (GoodsSpecProductRecord item : into) {
                //当前规格商品直接返利金额
                BigDecimal directlyRebatePrice = item.getPrdPrice().multiply(new BigDecimal(userRebateRatio.getFanliRatio())).divide(rat);
                //当前规格商品间接返利金额
                BigDecimal indirectRebatePrice = item.getPrdPrice().multiply(new BigDecimal(userRebateRatio.getRebateRatio())).divide(rat);
                //当前规格商品直接返利金额
                BigDecimal firstRebatePrice = item.getPrdPrice().multiply(new BigDecimal(userRebateRatio.getFirstRebate())).divide(rat);
                //当前规格成本价保护
                BigDecimal prdCostPriceProtect = item.getPrdPrice().subtract(item.getPrdCostPrice());
                logger().info("成本价保护值：" + prdCostPriceProtect);
                byte costProtectionOpen = 1;
                if (costProtection == costProtectionOpen) {
                    logger().info("开启成本价保护");
                    if (prdCostPriceProtect.compareTo(BigDecimal.ZERO) > 0) {
                        //当前规格直接返利小于成本价保护值
                        if (directlyRebatePrice.compareTo(prdCostPriceProtect) > 0) {
                            if (prdCostPriceProtect.compareTo(highDirectlyRebatePrice) > 0) {
                                highDirectlyRebatePrice = prdCostPriceProtect;
                            }
                        } else {
                            if (directlyRebatePrice.compareTo(highDirectlyRebatePrice) > 0) {
                                highDirectlyRebatePrice = directlyRebatePrice;
                            }
                        }

                        //当前规格间接返利小于成本价保护值
                        if (indirectRebatePrice.compareTo(prdCostPriceProtect) > 0) {
                            if (prdCostPriceProtect.compareTo(highIndirectRebatePrice) > 0) {
                                highIndirectRebatePrice = prdCostPriceProtect;
                            }
                        } else {
                            if (indirectRebatePrice.compareTo(highIndirectRebatePrice) > 0) {
                                highIndirectRebatePrice = indirectRebatePrice;
                            }
                        }
                        //开启首单返利 0：关闭；1：开启
                        if (userRebateRatio.getFirstRebate() == 1) {
                            //当前规格首单返利成本价保护值
                            if (firstRebatePrice.compareTo(prdCostPriceProtect) > 0) {
                                if (prdCostPriceProtect.compareTo(highFirstRebatePrice) > 0) {
                                    highFirstRebatePrice = prdCostPriceProtect;
                                }
                            } else {
                                if (firstRebatePrice.compareTo(highFirstRebatePrice) > 0) {
                                    highFirstRebatePrice = firstRebatePrice;
                                }
                            }
                        }
                    }
                } else {
                    logger().info("未开成本价保护");
                    if (directlyRebatePrice.compareTo(highDirectlyRebatePrice) > 0) {
                        highDirectlyRebatePrice = directlyRebatePrice;
                    }
                    if (indirectRebatePrice.compareTo(highIndirectRebatePrice) > 0) {
                        highIndirectRebatePrice = indirectRebatePrice;
                    }
                    //开启首单返利 0：关闭；1：开启
                    if (userRebateRatio.getFirstRebate() == 1) {
                        if (firstRebatePrice.compareTo(highFirstRebatePrice) > 0) {
                            highFirstRebatePrice = firstRebatePrice;
                        }
                    }
                }
            }
            userRebateRatio.setHighDirectlyRebatePrice(highDirectlyRebatePrice);
            userRebateRatio.setHighIndirectRebatePrice(highIndirectRebatePrice);
            userRebateRatio.setHighFirstRebatePrice(highFirstRebatePrice);
        } else{
            //利润计算方式
            logger().info("根据商品利润返利");
            strageProfits(into,userRebateRatio,rat);
        }

    }
    /**
     * 根据利润计算返利佣金
     * @param into
     * @param userRebateRatio
     * @param rat
     */
    public void strageProfits(List<GoodsSpecProductRecord> into,RebateRatioVo userRebateRatio,BigDecimal rat){
        //最高直接返利金额
        BigDecimal highDirectlyRebatePrice = BigDecimal.ZERO;
        //最高间接间接返利金额
        BigDecimal highIndirectRebatePrice = BigDecimal.ZERO;
        //最高首单返利金额
        BigDecimal highFirstRebatePrice = BigDecimal.ZERO;
        for (GoodsSpecProductRecord item : into) {
            BigDecimal goodsPrice = item.getPrdPrice().subtract(item.getPrdCostPrice());
            //当前规格商品直接返利金额
            BigDecimal directlyRebatePrice =goodsPrice.multiply(new BigDecimal(userRebateRatio.getFanliRatio())).divide(rat);
            //当前规格商品间接返利金额
            BigDecimal indirectRebatePrice = goodsPrice.multiply(new BigDecimal(userRebateRatio.getRebateRatio())).divide(rat);
            //当前规格商品直接返利金额
            BigDecimal firstRebatePrice = goodsPrice.multiply(new BigDecimal(userRebateRatio.getFirstRebate())).divide(rat);
            if (directlyRebatePrice.compareTo(highDirectlyRebatePrice) > 0) {
                highDirectlyRebatePrice = directlyRebatePrice;
            }
            if (indirectRebatePrice.compareTo(highIndirectRebatePrice) > 0) {
                highIndirectRebatePrice = indirectRebatePrice;
            }
            //开启首单返利 0：关闭；1：开启
            if (userRebateRatio.getFirstRebate() == 1) {
                //当前规格首单返利成本价保护值
                if (firstRebatePrice.compareTo(highFirstRebatePrice) > 0) {
                    highFirstRebatePrice = firstRebatePrice;
                }
            }
        }
        userRebateRatio.setHighDirectlyRebatePrice(highDirectlyRebatePrice);
        userRebateRatio.setHighIndirectRebatePrice(highIndirectRebatePrice);
        userRebateRatio.setHighFirstRebatePrice(highFirstRebatePrice);
    }
}
