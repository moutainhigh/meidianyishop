package com.meidianyi.shop.service.shop.market.bargain;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.excel.ExcelWriter;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.DomainConfig;
import com.meidianyi.shop.db.shop.tables.records.*;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.config.PictorialShareConfigVo;
import com.meidianyi.shop.service.pojo.shop.market.bargain.BargainRecordExportVo;
import com.meidianyi.shop.service.pojo.shop.market.bargain.BargainRecordPageListQueryParam;
import com.meidianyi.shop.service.pojo.shop.market.bargain.BargainRecordPageListQueryVo;
import com.meidianyi.shop.service.pojo.shop.market.bargain.analysis.BargainAnalysisParam;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.wxapp.market.bargain.*;
import com.meidianyi.shop.service.shop.activity.dao.BargainProcessorDao;
import com.meidianyi.shop.service.shop.image.postertraits.BargainPictorialShareService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import jodd.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jooq.Record;
import org.jooq.SelectWhereStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import static com.meidianyi.shop.db.shop.tables.Bargain.BARGAIN;
import static com.meidianyi.shop.db.shop.tables.BargainRecord.BARGAIN_RECORD;
import static com.meidianyi.shop.db.shop.tables.BargainUserList.BARGAIN_USER_LIST;
import static com.meidianyi.shop.db.shop.tables.Goods.GOODS;
import static com.meidianyi.shop.db.shop.tables.GoodsSpecProduct.GOODS_SPEC_PRODUCT;
import static com.meidianyi.shop.db.shop.tables.OrderInfo.ORDER_INFO;
import static com.meidianyi.shop.db.shop.tables.User.USER;
import static com.meidianyi.shop.db.shop.tables.UserDetail.USER_DETAIL;
import static com.meidianyi.shop.db.shop.tables.BargainGoods.BARGAIN_GOODS;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.date;

/**
 * @author 王兵兵
 * <p>
 * 2019年7月25日
 */
@Service
public class BargainRecordService extends ShopBaseService {

    /**
     * 0可以砍价（别人的砍价） 11可以邀请砍价（自己的砍价，但分享不再翻倍）
     * 1活动不存在 2砍价失败 3活动未开始 4或已结束
     * 5砍价成功 6商品已抢光 7可以邀请砍价（自己的砍价，已经砍了2刀）
     * 8可以再砍一刀（自己的砍价） 9我也要X元得好物（别人的砍价，已帮砍过一刀）
     * 10已完成订单（自己的砍价）
     */

    public static final int STATUS_CAN_BARGAIN = 1;
    public static final int STATUS_ACTIVITY_NOT_START = 3;
    public static final int STATUS_ACTIVITY_ENDED = 4;
    public static final int STATUS_GOODS_SALE_OUT = 6;
    public static final int STATUS_ACTIVITY_NO_EXISTS = 2;
    public static final int STATUS_CAN_INVITE_BARGAIN = 7;
    public static final int STATUS_CAN_BARGAIN_AGAIN = 8;
    public static final int STATUS_I_BARGAIN_TOO = 9;
    public static final int STATUS_BARGAIN_FAILED = 2;
    public static final int STATUS_ORDER_FINISHED = 10;
    public static final int STATUS_CAN_BARGAIN_AGAIN_SHARE_NO_AWARD = 11;
    public static final int STATUS_BARGAIN_SUCCESS = 5;
    /**
     * 帮忙砍价的用户
     */
    @Autowired
    public BargainUserService bargainUser;
    /**
     * 商品列表和详情页的砍价处理类
     */
    @Autowired
    private BargainProcessorDao bargainProcessorDao;
    @Autowired
    private DomainConfig domainConfig;
    @Autowired
    private OrderInfoService orderInfo;
    @Autowired
    private BargainPictorialShareService shareImgService;

    /**
     * 状态：正在砍价
     */
    public static final byte STATUS_IN_PROCESS = 0;
    /**
     * 状态：砍价成功
     */
    public static final byte STATUS_SUCCESS = 1;
    /**
     * 状态：砍价失败
     */
    public static final byte STATUS_FAILED = 2;

    /**
     * 已下单
     */
    public static final Byte IS_ORDERED_Y = 1;
    /**
     * 未下单
     */
    public static final Byte IS_ORDERED_N = 0;

    private static final String LANGUAGE_TYPE_EXCEL = "excel";

    /**
     * 根据状态取发起砍价的数量
     */
    public Integer getBargainRecordNumberByStatus(int bargainId, byte status) {
        return db().selectCount().from(BARGAIN_RECORD).where(BARGAIN_RECORD.STATUS.eq(status)).and(BARGAIN_RECORD.BARGAIN_ID.eq(bargainId)).and(BARGAIN_RECORD.DEL_FLAG.eq(DelFlag.NORMAL.getCode())).fetchOneInto(Integer.class);
    }

    /**
     * 某活动的发起砍价数量
     */
    public Integer getBargainRecordNumber(int bargainId) {
        return db().selectCount().from(BARGAIN_RECORD).where(BARGAIN_RECORD.BARGAIN_ID.eq(bargainId)).and(BARGAIN_RECORD.DEL_FLAG.eq(DelFlag.NORMAL.getCode())).fetchOneInto(Integer.class);
    }

    /**
     * 发起记录的分页列表
     */
    public PageResult<BargainRecordPageListQueryVo> getRecordPageList(BargainRecordPageListQueryParam param) {
        SelectWhereStep<? extends Record> select = db().select(
            BARGAIN_RECORD.ID, BARGAIN_RECORD.BARGAIN_ID, BARGAIN_RECORD.GOODS_ID, GOODS.GOODS_NAME, BARGAIN_RECORD.GOODS_PRICE, USER.USERNAME, USER.MOBILE, BARGAIN_RECORD.CREATE_TIME, BARGAIN_RECORD.BARGAIN_MONEY,
            BARGAIN_RECORD.USER_NUMBER, BARGAIN_RECORD.STATUS, BARGAIN.BARGAIN_TYPE
        ).
            from(BARGAIN_RECORD).
            leftJoin(GOODS).on(BARGAIN_RECORD.GOODS_ID.eq(GOODS.GOODS_ID)).
            leftJoin(USER).on(BARGAIN_RECORD.USER_ID.eq(USER.USER_ID)).
            leftJoin(BARGAIN).on(BARGAIN_RECORD.BARGAIN_ID.eq(BARGAIN.ID));
        select = this.buildOptions(select, param);
        select.where(BARGAIN_RECORD.BARGAIN_ID.eq(param.getBargainId())).and(BARGAIN_RECORD.DEL_FLAG.eq(DelFlag.NORMAL.getCode()));
        return getPageResult(select, param.getCurrentPage(), param.getPageRows(), BargainRecordPageListQueryVo.class);
    }

    public SelectWhereStep<? extends Record> buildOptions(SelectWhereStep<? extends Record> select, BargainRecordPageListQueryParam param) {
        if (param == null) {
            return select;
        }
        if (!StringUtils.isBlank(param.getUsername())) {
            select.where(USER.USERNAME.contains(param.getUsername()));
        }
        if (!StringUtils.isBlank(param.getMobile())) {
            select.where(USER.MOBILE.contains(param.getMobile()));
        }
        if (param.getStatus() > -1) {
            select.where(BARGAIN_RECORD.STATUS.eq(param.getStatus()));
        }
        if (param.getStartTime() != null) {
            select.where(BARGAIN_RECORD.CREATE_TIME.gt(param.getStartTime()));
        }
        if (param.getEndTime() != null) {
            select.where(BARGAIN_RECORD.CREATE_TIME.lt(param.getEndTime()));
        }
        return select;
    }

    /**
     * 算出待砍金额
     */
    public BigDecimal getBargainRecordSurplusMoney(BargainRecordPageListQueryVo record) {
        BargainGoodsRecord bargainGoodsRecord = saas.getShopApp(getShopId()).bargain.getBargainGoods(record.getBargainId(), record.getGoodsId());
        if (record.getBargainType() == BargainService.BARGAIN_TYPE_FIXED) {
            return record.getGoodsPrice().subtract(bargainGoodsRecord.getExpectationPrice()).subtract(record.getBargainMoney());
        } else if (record.getBargainType() == BargainService.BARGAIN_TYPE_RANDOM) {
            return record.getGoodsPrice().subtract(bargainGoodsRecord.getFloorPrice()).subtract(record.getBargainMoney());
        }
        return BigDecimal.ZERO;
    }

    public Workbook exportBargainRecordList(BargainRecordPageListQueryParam param, String lang) {
        SelectWhereStep<? extends Record> select = db().select(
            BARGAIN_RECORD.ID, BARGAIN_RECORD.GOODS_ID, GOODS.GOODS_NAME, BARGAIN_RECORD.GOODS_PRICE, USER.USERNAME, USER.MOBILE, BARGAIN_RECORD.CREATE_TIME, BARGAIN_RECORD.BARGAIN_MONEY,
            BARGAIN_RECORD.USER_NUMBER, BARGAIN_RECORD.STATUS, BARGAIN.BARGAIN_TYPE
        ).
            from(BARGAIN_RECORD).
            leftJoin(GOODS).on(BARGAIN_RECORD.GOODS_ID.eq(GOODS.GOODS_ID)).
            leftJoin(USER).on(BARGAIN_RECORD.USER_ID.eq(USER.USER_ID)).
            leftJoin(BARGAIN).on(BARGAIN_RECORD.BARGAIN_ID.eq(BARGAIN.ID));
        select = this.buildOptions(select, param);
        select.where(BARGAIN_RECORD.BARGAIN_ID.eq(param.getBargainId())).and(BARGAIN_RECORD.DEL_FLAG.eq(DelFlag.NORMAL.getCode()));
        List<BargainRecordExportVo> bargainRecordList = select.fetchInto(BargainRecordExportVo.class);

        /**循环处理状态和待砍金额列*/
        for (BargainRecordExportVo vo : bargainRecordList) {
            switch (vo.getStatus()) {
                case 0:
                    vo.setStatusName(Util.translateMessage(lang, JsonResultMessage.STATUS_IN_PROGRESS, LANGUAGE_TYPE_EXCEL));
                    break;
                case 1:
                    vo.setStatusName(Util.translateMessage(lang, JsonResultMessage.STATUS_SUCCESS, LANGUAGE_TYPE_EXCEL));
                    break;
                case 2:
                    vo.setStatusName(Util.translateMessage(lang, JsonResultMessage.STATUS_FAIL, LANGUAGE_TYPE_EXCEL));
                    break;
                default:
            }

            BargainGoodsRecord bargainGoods = saas.getShopApp(getShopId()).bargain.getBargainGoods(param.getBargainId(), vo.getGoodsId());
            if (vo.getBargainType() == BargainService.BARGAIN_TYPE_FIXED) {
                vo.setSurplusMoney(vo.getGoodsPrice().subtract(bargainGoods.getExpectationPrice()).subtract(vo.getBargainMoney()));
            } else if (vo.getBargainType() == BargainService.BARGAIN_TYPE_RANDOM) {
                vo.setSurplusMoney(vo.getGoodsPrice().subtract(bargainGoods.getFloorPrice()).subtract(vo.getBargainMoney()));
            }
        }

        Workbook workbook = ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(lang, workbook);
        excelWriter.writeModelList(bargainRecordList, BargainRecordExportVo.class);
        return workbook;
    }

    /**
     * 发起砍价的人次数据分析
     */
    public Map<Date, Integer> getRecordAnalysis(BargainAnalysisParam param) {
        Map<Date, Integer> map = db().select(date(BARGAIN_RECORD.CREATE_TIME).as("date"), count().as("number")).from(BARGAIN_RECORD).
            where(BARGAIN_RECORD.BARGAIN_ID.eq(param.getBargainId())).
            and(BARGAIN_RECORD.CREATE_TIME.between(param.getStartTime(), param.getEndTime())).
            groupBy(date(BARGAIN_RECORD.CREATE_TIME)).fetch().intoMap(date(BARGAIN_RECORD.CREATE_TIME).as("date"), count().as("number"));
        return map;
    }

    /**
     * 帮砍价的用户数据分析
     */
    public Map<Date, Integer> getBargainUserAnalysis(BargainAnalysisParam param) {
        Map<Date, Integer> map = db().select(date(BARGAIN_USER_LIST.CREATE_TIME).as("date"), count().as("number")).from(BARGAIN_USER_LIST).
            leftJoin(BARGAIN_RECORD).on(BARGAIN_USER_LIST.RECORD_ID.eq(BARGAIN_RECORD.ID)).
            where(BARGAIN_RECORD.BARGAIN_ID.eq(param.getBargainId())).
            and(BARGAIN_RECORD.CREATE_TIME.between(param.getStartTime(), param.getEndTime())).
            groupBy(date(BARGAIN_USER_LIST.CREATE_TIME)).fetch().intoMap(date(BARGAIN_USER_LIST.CREATE_TIME).as("date"), count().as("number"));
        return map;
    }

    /**
     * 小程序端-个人中心-我的砍价列表
     */
    public PageResult<BargainRecordListQueryVo> getRecordPageList(Integer userId, BargainRecordListQueryParam param) {
        SelectWhereStep<? extends Record> select = db().select(
            BARGAIN_RECORD.ID, BARGAIN_RECORD.GOODS_PRICE, BARGAIN_RECORD.CREATE_TIME, BARGAIN_RECORD.BARGAIN_MONEY,
            BARGAIN_RECORD.USER_NUMBER, BARGAIN_RECORD.STATUS, BARGAIN_RECORD.PRD_ID, BARGAIN_RECORD.BARGAIN_ID, GOODS.GOODS_NAME, GOODS.GOODS_ID, GOODS.GOODS_IMG, GOODS.GOODS_NUMBER, BARGAIN_GOODS.EXPECTATION_PRICE, BARGAIN.BARGAIN_TYPE, BARGAIN_GOODS.FLOOR_PRICE, BARGAIN_GOODS.STOCK, BARGAIN.END_TIME, ORDER_INFO.ORDER_STATUS, ORDER_INFO.ORDER_SN
        ).
            from(BARGAIN_RECORD).
            innerJoin(GOODS).on(BARGAIN_RECORD.GOODS_ID.eq(GOODS.GOODS_ID)).
            innerJoin(BARGAIN).on(BARGAIN_RECORD.BARGAIN_ID.eq(BARGAIN.ID)).
            innerJoin(BARGAIN_GOODS).on(BARGAIN_GOODS.GOODS_ID.eq(BARGAIN_RECORD.GOODS_ID)).
            leftJoin(ORDER_INFO).on(BARGAIN_RECORD.ORDER_SN.eq(ORDER_INFO.ORDER_SN));

        select.where(BARGAIN_RECORD.USER_ID.eq(userId)).and(BARGAIN_RECORD.STATUS.eq(param.getStatus())).and(BARGAIN_RECORD.DEL_FLAG.eq(DelFlag.NORMAL.getCode()));
        select.orderBy(BARGAIN_RECORD.CREATE_TIME.desc());
        PageResult<BargainRecordListQueryVo> list = getPageResult(select, param.getCurrentPage(), param.getPageRows(), BargainRecordListQueryVo.class);

        for (BargainRecordListQueryVo bargainRecord : list.dataList) {
            if (bargainRecord.getGoodsNumber() < bargainRecord.getStock()) {
                bargainRecord.setStock(bargainRecord.getGoodsNumber());
            }
            if (bargainRecord.getBargainType() == BargainService.BARGAIN_TYPE_RANDOM) {
                BigDecimal remainMoney = bargainRecord.getGoodsPrice().subtract(bargainRecord.getBargainMoney());
                if (remainMoney.compareTo(bargainRecord.getExpectationPrice()) < 0) {
                    bargainRecord.setExpectationPrice(remainMoney);
                }
            }
        }
        return list;
    }

    /**
     * 申请发起砍价
     *
     * @param param
     * @return bargain_record id 发起砍价的ID
     */
    public BargainApplyVo applyBargain(int userId, BargainApplyParam param) {
        GoodsSpecProductRecord goodsPrd = db().selectFrom(GOODS_SPEC_PRODUCT).where(GOODS_SPEC_PRODUCT.PRD_ID.eq(param.getPrdId())).fetchAny();
        //发起新的砍价
        BargainRecordRecord insertRecord = db().newRecord(BARGAIN_RECORD);
        insertRecord.setUserId(userId);
        insertRecord.setBargainId(param.getBargainId());
        insertRecord.setGoodsId(goodsPrd.getGoodsId());
        insertRecord.setPrdId(param.getPrdId());
        insertRecord.setGoodsPrice(goodsPrd.getPrdPrice());
        transaction(() -> {
            insertRecord.insert();
            bargainUser.addUserBargain(userId, insertRecord.getId());
        });

        //打标签
        saas.getShopApp(getShopId()).bargain.addLaunchUserTag(param.getBargainId(), userId);

        return insertRecord.getId() != null && insertRecord.getId() > 0 ? BargainApplyVo.builder().recordId(insertRecord.getId()).resultCode((byte) 0).build() : BargainApplyVo.builder().resultCode((byte) -1).build();
    }

    /**
     * 该用户已经发起了对这个活动的砍价 的ID
     *
     * @param userId
     * @param param
     * @return
     */
    public BargainApplyVo getUserBargainRecordId(int userId, BargainApplyParam param) {
        Integer recordId = db().select(BARGAIN_RECORD.ID).from(BARGAIN_RECORD).where(BARGAIN_RECORD.USER_ID.eq(userId).and(BARGAIN_RECORD.BARGAIN_ID.eq(param.getBargainId())).and(BARGAIN_RECORD.PRD_ID.eq(param.getPrdId())).and(BARGAIN_RECORD.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))).fetchOptionalInto(Integer.class).orElse(0);

        return recordId > 0 ? BargainApplyVo.builder().recordId(recordId).resultCode((byte) 0).build() : BargainApplyVo.builder().resultCode((byte) -1).build();
    }

    /**
     * 判断用户是否可以对该规格发起砍价
     *
     * @param userId 用户id
     * @param param
     * @return
     */
    public Byte canApplyBargain(Integer userId, BargainApplyParam param) {
        BargainRecord bargain = saas.getShopApp(getShopId()).bargain.getBargainActById(param.getBargainId());
        BargainGoodsRecord bargainGoods = saas.getShopApp(getShopId()).bargain.getBargainGoods(param.getBargainId(), param.getGoodsId());

        //校验活动信息
        Byte res = bargainProcessorDao.canApplyBargain(userId, DateUtils.getLocalDateTime(), bargain, param.getGoodsId());

        if (!res.equals(BaseConstant.ACTIVITY_STATUS_CAN_USE)) {
            return res;
        } else {
            //校验库存
            if (bargain.getStock() <= 0 || bargainGoods.getStock() <= 0) {
                return BaseConstant.ACTIVITY_STATUS_NOT_HAS_NUM;
            }

            GoodsSpecProductRecord goodsPrd = saas.getShopApp(getShopId()).goods.goodsSpecProductService.selectSpecByProId(param.getPrdId());
            if (goodsPrd.getPrdNumber() <= 0) {
                return BaseConstant.ACTIVITY_STATUS_NOT_HAS_NUM;
            }

            GoodsRecord goods = saas.getShopApp(getShopId()).goods.getGoodsRecordById(param.getGoodsId());
            if (goods.getGoodsNumber() <= 0) {
                return BaseConstant.ACTIVITY_STATUS_NOT_HAS_NUM;
            }
        }
        return BaseConstant.ACTIVITY_STATUS_CAN_USE;
    }

    /**
     * 砍价详情
     *
     * @param userId
     * @param recordId
     * @return
     */
    public BargainInfoVo getBargainInfo(int userId, int recordId) {
        BargainInfoVo vo = new BargainInfoVo();
        BargainRecordInfo recordInfo = getRecordInfo(recordId);

        //详情
        if (recordInfo != null) {
            //状态
            byte recordStatus = userBargainRecordStatus(userId, recordInfo);
            vo.setState(recordStatus);
            if (recordStatus == 9) {
                vo.setStateMoney(recordInfo.getBargainType().equals(BargainService.BARGAIN_MONEY_TYPE_RANDOM) ? recordInfo.getFloorPrice() : recordInfo.getExpectationPrice());
            } else if (recordStatus == 8) {
                vo.setStateMoney(bargainUser.getFirstUserBargain(userId, recordId).getBargainMoney());
            }

            //分享配置
            recordInfo.setRemainingTime((recordInfo.getEndTime().getTime() - DateUtils.getLocalDateTime().getTime()) / 1000);
            vo.setRecordShareImg(Util.parseJson(recordInfo.getShareConfig(), PictorialShareConfigVo.class));
            if (vo.getRecordShareImg() != null) {
                if (vo.getRecordShareImg().getShareAction().equals(PictorialShareConfigVo.CUSTOMER_STYLE) && vo.getRecordShareImg().getShareImgAction().equals(PictorialShareConfigVo.CUSTOMER_IMG) && StringUtil.isNotEmpty(vo.getRecordShareImg().getShareImg())) {
                    //自定义分享图
                    vo.getRecordShareImg().setShareImgFullUrl(domainConfig.imageUrl(vo.getRecordShareImg().getShareImg()));
                } else if (vo.getRecordShareImg().getShareAction().equals(PictorialShareConfigVo.CUSTOMER_STYLE) && vo.getRecordShareImg().getShareImgAction().equals(PictorialShareConfigVo.DEFAULT_IMG)) {
                    //分享商品图
                    vo.getRecordShareImg().setShareImg(recordInfo.getGoodsImg());
                    vo.getRecordShareImg().setShareImgFullUrl(domainConfig.imageUrl(recordInfo.getGoodsImg()));
                } else {
                    String shareImgPath = shareImgService.getBargainInfoShareImg(recordInfo);
                    vo.getRecordShareImg().setShareImg(shareImgPath);
                    vo.getRecordShareImg().setShareImgFullUrl(domainConfig.imageUrl(shareImgPath));
                }
            }
            vo.setRecordInfo(recordInfo);

            //商品图片地址
            vo.getRecordInfo().setGoodsImg(domainConfig.imageUrl(recordInfo.getGoodsImg()));

            //帮忙砍价用户
            vo.setRecordUserList(bargainUser.getBargainUserList(recordId));

            vo.setTimestamp(DateUtils.getLocalDateTime());
            vo.setBargainPrice(recordInfo.getBargainType().equals(BargainService.BARGAIN_MONEY_TYPE_RANDOM) ? recordInfo.getFloorPrice() : recordInfo.getExpectationPrice());

            vo.setNeedBindMobile(recordInfo.getNeedBindMobile());
            vo.setInitialSales(recordInfo.getInitialSales());
        }
        return vo;
    }

    public BargainRecordInfo getRecordInfo(int recordId) {
        return db().select(BARGAIN_RECORD.fields())
            .select(
                GOODS.GOODS_ID, GOODS.GOODS_IMG, GOODS.GOODS_NAME, GOODS.IS_DEFAULT_PRODUCT,
                USER_DETAIL.USER_AVATAR,
                GOODS_SPEC_PRODUCT.PRD_PRICE, GOODS_SPEC_PRODUCT.PRD_DESC, GOODS_SPEC_PRODUCT.PRD_NUMBER,
                BARGAIN.BARGAIN_TYPE, BARGAIN.START_TIME, BARGAIN.END_TIME, BARGAIN_GOODS.EXPECTATION_PRICE, BARGAIN_GOODS.FLOOR_PRICE, BARGAIN.UPDATE_TIME, BARGAIN.SHARE_CONFIG, BARGAIN_GOODS.STOCK, BARGAIN.NEED_BIND_MOBILE, BARGAIN.INITIAL_SALES, BARGAIN.FREE_FREIGHT,
                USER.WX_OPENID, USER.USERNAME).from(
                BARGAIN_RECORD
                    .innerJoin(USER_DETAIL).on(BARGAIN_RECORD.USER_ID.eq(USER_DETAIL.USER_ID))
                    .innerJoin(GOODS).on(GOODS.GOODS_ID.eq(BARGAIN_RECORD.GOODS_ID))
                    .innerJoin(GOODS_SPEC_PRODUCT).on(BARGAIN_RECORD.PRD_ID.eq(GOODS_SPEC_PRODUCT.PRD_ID))
                    .innerJoin(BARGAIN).on(BARGAIN.ID.eq(BARGAIN_RECORD.BARGAIN_ID))
                    .innerJoin(USER).on(USER.USER_ID.eq(BARGAIN_RECORD.USER_ID))
                    .innerJoin(BARGAIN_GOODS).on((BARGAIN_GOODS.GOODS_ID.eq(BARGAIN_RECORD.GOODS_ID)).and(BARGAIN_GOODS.BARGAIN_ID.eq(BARGAIN_RECORD.BARGAIN_ID)))
            )
            .where(BARGAIN_RECORD.ID.eq(recordId)).and(BARGAIN_RECORD.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
            .fetchOptionalInto(BargainRecordInfo.class).orElse(null);
    }

    /**
     * 判断记录状态
     *
     * @param userId
     * @param recordInfo
     * @return 状态码
     * 0可以砍价（别人的砍价） 11可以邀请砍价（自己的砍价，但分享不再翻倍） 1活动不存在 2砍价失败 3活动未开始 4或已结束
     * 5砍价成功 6商品已抢光 7可以邀请砍价（自己的砍价，已经砍了2刀） 8可以再砍一刀（自己的砍价） 9我也要X元得好物（别人的砍价，已帮砍过一刀） 10已完成订单（自己的砍价）
     */
    private byte userBargainRecordStatus(int userId, BargainRecordInfo recordInfo) {
        if (recordInfo == null) {
            return STATUS_CAN_BARGAIN;
        }
        if (recordInfo.getStartTime().after(DateUtils.getLocalDateTime())) {
            return STATUS_ACTIVITY_NOT_START;
        }
        if (recordInfo.getEndTime().before(DateUtils.getLocalDateTime())) {
            return STATUS_ACTIVITY_ENDED;
        }

        BigDecimal remainMoney = recordInfo.getGoodsPrice().subtract(recordInfo.getBargainMoney()).subtract(recordInfo.getFloorPrice().compareTo(BigDecimal.ZERO) > 0 ? recordInfo.getFloorPrice() : BigDecimal.ZERO);

        //自己的砍价详情
        Byte x = processSelfBargain(userId, recordInfo, remainMoney);
        if (x != null) {
            return x;
        }

        if (recordInfo.getStock() <= 0 || recordInfo.getPrdNumber() <= 0) {
            return STATUS_GOODS_SALE_OUT;
        }

        int userNumber = bargainUser.getUserBargainNumber(userId, recordInfo.getId());
        if (userId == recordInfo.getUserId()) {
            if (userNumber == STATUS_ACTIVITY_NO_EXISTS) {
                return STATUS_CAN_INVITE_BARGAIN;
            }
            if (userNumber == 1) {
                return STATUS_CAN_BARGAIN_AGAIN;
            }
        } else {
            if (userNumber > 0) {
                return STATUS_I_BARGAIN_TOO;
            } else {
                if (recordInfo.getBargainType().equals(BargainService.BARGAIN_TYPE_RANDOM)) {
                    if (recordInfo.getIsOrdered().equals(IS_ORDERED_Y)) {
                        OrderInfoRecord order = orderInfo.getOrderByOrderSn(recordInfo.getOrderSn());
                        if (order.getOrderAmount().equals(recordInfo.getFloorPrice()) || order.getOrderStatus() > OrderConstant.ORDER_CLOSED) {
                            return STATUS_I_BARGAIN_TOO;
                        } else {
                            if (remainMoney.compareTo(BigDecimal.ZERO) > 0) {
                                return STATUS_I_BARGAIN_TOO;
                            }
                        }
                    } else {
                        if (recordInfo.getStatus().equals(STATUS_SUCCESS) && remainMoney.compareTo(BigDecimal.ZERO) > 0) {
                            //如果是成功状态，库存肯定已经锁定了，还有剩余金额，允许再砍一次
                            return STATUS_CAN_BARGAIN;
                        }
                    }
                } else {
                    if (recordInfo.getIsOrdered().equals(IS_ORDERED_Y) || recordInfo.getStatus().equals(STATUS_SUCCESS)) {
                        return STATUS_I_BARGAIN_TOO;
                    }
                }
            }
        }
        return STATUS_CAN_BARGAIN;
    }

    private Byte processSelfBargain(int userId, BargainRecordInfo recordInfo, BigDecimal remainMoney) {
        if (userId == recordInfo.getUserId()) {
            if (recordInfo.getStatus().equals(STATUS_FAILED)) {
                return STATUS_BARGAIN_FAILED;
            }
            //区间结算
            if (recordInfo.getBargainType().equals(BargainService.BARGAIN_TYPE_RANDOM)) {
                //已下单
                if (recordInfo.getIsOrdered().equals(IS_ORDERED_Y)) {
                    OrderInfoRecord order = orderInfo.getOrderByOrderSn(recordInfo.getOrderSn());
                    if (order.getOrderAmount().equals(recordInfo.getFloorPrice()) || order.getOrderStatus() > OrderConstant.ORDER_CLOSED) {
                        return STATUS_ORDER_FINISHED;
                    } else {
                        if (remainMoney.compareTo(BigDecimal.ZERO) > 0) {
                            return STATUS_CAN_BARGAIN_AGAIN_SHARE_NO_AWARD;
                        } else {
                            return STATUS_BARGAIN_SUCCESS;
                        }
                    }
                } else {
                    if (recordInfo.getStatus().equals(STATUS_SUCCESS) && remainMoney.compareTo(BigDecimal.ZERO) <= 0) {
                        return STATUS_BARGAIN_SUCCESS;
                    }
                    if (recordInfo.getStatus().equals(STATUS_SUCCESS) && remainMoney.compareTo(BigDecimal.ZERO) > 0) {
                        return STATUS_CAN_BARGAIN_AGAIN_SHARE_NO_AWARD;
                    }
                }
            } else {
                if (recordInfo.getIsOrdered().equals(IS_ORDERED_Y)) {
                    return STATUS_ORDER_FINISHED;
                }
                if (recordInfo.getStatus().equals(STATUS_SUCCESS)) {
                    return STATUS_BARGAIN_SUCCESS;
                }
            }
        }
        return null;
    }

    /**
     * 帮助砍价
     *
     * @param userId
     * @param recordId
     * @return
     */
    public BargainCutVo getBargainCut(int userId, int recordId) {
        BargainCutVo vo = new BargainCutVo();
        BargainRecordInfo recordInfo = getRecordInfo(recordId);

        if (recordInfo.getUserId() != userId) {
            //判断今天砍价次数
            int daileCutTimes = saas.getShopApp(getShopId()).config.bargainCfg.getDailyCutTimes();
            int userTodayCutTimes = bargainUser.getUserTodayCutTimes(userId);
            if (daileCutTimes > 0 && userTodayCutTimes >= daileCutTimes) {
                vo.setState((byte) 12);
                return vo;
            }
        }

        //可用状态过滤
        byte canCutStatus = userBargainRecordStatus(userId, recordInfo);
        if (canCutStatus != 0 && canCutStatus != 8) {
            vo.setState(canCutStatus);
            return vo;
        }

        //进行砍价
        BigDecimal bargainMoney = bargainUser.addUserBargain(userId, recordId);
        vo.setState((byte) 0);
        vo.setBargainMoney(bargainMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
        return vo;
    }

}
