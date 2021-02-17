package com.meidianyi.shop.service.shop.market.bargain;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.excel.ExcelWriter;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.Tables;
import com.meidianyi.shop.db.shop.tables.records.*;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobsConstant;
import com.meidianyi.shop.service.pojo.shop.coupon.give.CouponGiveQueueParam;
import com.meidianyi.shop.service.pojo.shop.market.bargain.BargainUserExportVo;
import com.meidianyi.shop.service.pojo.shop.market.bargain.BargainUserListQueryParam;
import com.meidianyi.shop.service.pojo.shop.market.bargain.BargainUserListQueryVo;
import com.meidianyi.shop.service.pojo.shop.market.message.RabbitMessageParam;
import com.meidianyi.shop.service.pojo.shop.market.message.RabbitParamConstant;
import com.meidianyi.shop.service.pojo.shop.message.MpTemplateConfig;
import com.meidianyi.shop.service.pojo.shop.message.MpTemplateData;
import com.meidianyi.shop.service.pojo.shop.user.message.MaSubscribeData;
import com.meidianyi.shop.service.pojo.shop.user.message.MaTemplateData;
import com.meidianyi.shop.service.pojo.wxapp.market.bargain.BargainInfoVo;
import com.meidianyi.shop.service.pojo.wxapp.market.bargain.BargainUsersListParam;
import com.meidianyi.shop.service.pojo.wxapp.market.bargain.BargainUsersListVo;
import com.meidianyi.shop.service.shop.order.atomic.AtomicOperation;
import com.meidianyi.shop.service.pojo.shop.market.message.maconfig.SubcribeTemplateCategory;
import jodd.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectWhereStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.meidianyi.shop.db.shop.tables.BargainRecord.BARGAIN_RECORD;
import static com.meidianyi.shop.db.shop.tables.BargainUserList.BARGAIN_USER_LIST;
import static com.meidianyi.shop.db.shop.tables.User.USER;
import static com.meidianyi.shop.db.shop.tables.UserDetail.USER_DETAIL;

/**
 * 帮砍价用户
 * @author 王兵兵
 *
 * 2019年7月26日
 */
@Service
public class BargainUserService extends ShopBaseService{

    @Autowired
    private AtomicOperation atomicOperation;

	public PageResult<BargainUserListQueryVo> getPageList(BargainUserListQueryParam param){
		SelectWhereStep<? extends Record> select = db().select(
				BARGAIN_USER_LIST.ID,BARGAIN_USER_LIST.USER_ID,USER.USERNAME,USER.MOBILE,BARGAIN_USER_LIST.CREATE_TIME,BARGAIN_USER_LIST.BARGAIN_MONEY
				).
				from(BARGAIN_USER_LIST).
				leftJoin(USER).on(BARGAIN_USER_LIST.USER_ID.eq(USER.USER_ID));
		select = this.buildOptions(select, param);
		select.where(BARGAIN_USER_LIST.RECORD_ID.eq(param.getRecordId())).orderBy(BARGAIN_USER_LIST.CREATE_TIME.desc());
		return getPageResult(select,param.getCurrentPage(),param.getPageRows(),BargainUserListQueryVo.class);
	}

	private SelectWhereStep<? extends Record> buildOptions(SelectWhereStep<? extends  Record> select, BargainUserListQueryParam param) {
		if (param == null) {
			return select;
		}
		if(!StringUtils.isBlank(param.getUsername())) {
			select.where(USER.USERNAME.contains(param.getUsername()));
		}
		if(!StringUtils.isBlank(param.getMobile())) {
			select.where(USER.MOBILE.contains(param.getMobile()));
		}
		return select;
	}

	public Workbook exportBargainUserList(BargainUserListQueryParam param, String lang){
		SelectWhereStep<? extends Record> select = db().select(
				BARGAIN_USER_LIST.USER_ID,USER.USERNAME,USER.MOBILE,BARGAIN_USER_LIST.CREATE_TIME,BARGAIN_USER_LIST.BARGAIN_MONEY
		).
				from(BARGAIN_USER_LIST).
				leftJoin(USER).on(BARGAIN_USER_LIST.USER_ID.eq(USER.USER_ID));
		select = this.buildOptions(select, param);
		select.where(BARGAIN_USER_LIST.RECORD_ID.eq(param.getRecordId())).orderBy(BARGAIN_USER_LIST.CREATE_TIME.desc());
		List<BargainUserExportVo> voList = select.fetchInto(BargainUserExportVo.class);
		Workbook workbook= ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
		ExcelWriter excelWriter = new ExcelWriter(lang,workbook);
		excelWriter.writeModelList(voList,BargainUserExportVo.class);
		return workbook;
	}

    public BargainUsersListVo getWxPageList(BargainUsersListParam param){
        BargainUsersListVo vo = new BargainUsersListVo();
        SelectConditionStep<? extends Record> select = db().select(
            BARGAIN_USER_LIST.asterisk(),USER.MOBILE,USER.USERNAME,USER.WX_OPENID,USER_DETAIL.USER_AVATAR
        ).
            from(BARGAIN_USER_LIST).
            leftJoin(USER).on(BARGAIN_USER_LIST.USER_ID.eq(USER.USER_ID)).
            leftJoin(USER_DETAIL).on(BARGAIN_USER_LIST.USER_ID.eq(USER_DETAIL.USER_ID)).
            where(BARGAIN_USER_LIST.RECORD_ID.eq(param.getRecordId()));
        select.orderBy(BARGAIN_USER_LIST.CREATE_TIME.desc());
        PageResult<BargainUsersListVo.BargainUsers> res = getPageResult(select,param.getCurrentPage(),param.getPageRows(),BargainUsersListVo.BargainUsers.class);
        vo.setBargainUsers(res);
        vo.setTimestamp(DateUtils.getLocalDateTime());
        return vo;
    }

    /**
     * userId给recordId砍一刀
     * @param userId
     * @param recordId
     * @return 是否成功
     */
    public BigDecimal addUserBargain(int userId,int recordId){
        BargainRecordRecord bargainRecord = db().selectFrom(BARGAIN_RECORD).where(BARGAIN_RECORD.ID.eq(recordId)).fetchAny();
        BargainRecord bargain = saas.getShopApp(getShopId()).bargain.getBargainActById(bargainRecord.getBargainId());
        BargainGoodsRecord bargainGoods = saas.getShopApp(getShopId()).bargain.getBargainGoods(bargainRecord.getBargainId(),bargainRecord.getGoodsId());

        BigDecimal bargainMoney;
        if(bargain.getBargainType().equals(BargainService.BARGAIN_TYPE_RANDOM)){
            //区间内结算类型
            bargainMoney = getAbnormalBargainMoney(bargain,bargainGoods,bargainRecord,userId);
        }else{
            //固定人数类型
            bargainMoney = getNormalBargainMoney(bargain,bargainGoods,bargainRecord,userId);
        }

        //插入帮砍价记录
        BargainUserListRecord insertRecord = db().newRecord(BARGAIN_USER_LIST);
        insertRecord.setRecordId(recordId);
        insertRecord.setUserId(userId);
        insertRecord.setBargainMoney(bargainMoney);

        transaction(()->{
            insertRecord.insert();

            boolean isSuccess = bargain.getBargainType().equals(BargainService.BARGAIN_TYPE_RANDOM) ? (bargainRecord.getStatus().equals(BargainRecordService.STATUS_IN_PROCESS) && (bargainRecord.getGoodsPrice().subtract(bargainRecord.getBargainMoney().add(bargainMoney)).compareTo(bargainGoods.getExpectationPrice()) <= 0)) : (bargain.getExpectationNumber() == (bargainRecord.getUserNumber() + 1));

            if(isSuccess){
                //砍价成功了
                //库存更新
                saas.getShopApp(getShopId()).bargain.updateBargainStock(bargain.getId(),bargainRecord.getGoodsId(),1);
                atomicOperation.updateStockAndSalesByLock(bargainRecord.getGoodsId(),bargainRecord.getPrdId(),1,false);

                //砍价record的状态更新
                db().update(BARGAIN_RECORD).set(BARGAIN_RECORD.STATUS,BargainRecordService.STATUS_SUCCESS).set(BARGAIN_RECORD.BARGAIN_MONEY,BARGAIN_RECORD.BARGAIN_MONEY.add(bargainMoney)).set(BARGAIN_RECORD.USER_NUMBER,BARGAIN_RECORD.USER_NUMBER.add(1)).where(BARGAIN_RECORD.ID.eq(recordId)).execute();

                //推送砍价成功消息
                String goodsName = saas.getShopApp(getShopId()).goods.getGoodsRecordById(bargainRecord.getGoodsId()).getGoodsName();
                bargainSuccessNotify(bargainRecord.getUserId(), bargain.getExpectationPrice(), bargain.getBargainName(), goodsName, bargainRecord.getId());
            }else {
                //砍价record的状态更新
                db().update(BARGAIN_RECORD).set(BARGAIN_RECORD.BARGAIN_MONEY,BARGAIN_RECORD.BARGAIN_MONEY.add(bargainMoney)).set(BARGAIN_RECORD.USER_NUMBER,BARGAIN_RECORD.USER_NUMBER.add(1)).where(BARGAIN_RECORD.ID.eq(recordId)).execute();
            }
        });


        if(userId != bargainRecord.getUserId()){
            if(StringUtil.isNotEmpty(bargain.getMrkingVoucherId())){
                //向帮忙砍价的用户赠送优惠券
                CouponGiveQueueParam newParam = new CouponGiveQueueParam(
                    getShopId(), Arrays.asList(userId), bargain.getId(), bargain.getMrkingVoucherId().split(","), BaseConstant.ACCESS_MODE_ISSUE, BaseConstant.GET_SOURCE_BARGAIN_CUT);
                saas.taskJobMainService.dispatchImmediately(newParam, CouponGiveQueueParam.class.getName(), getShopId(), TaskJobsConstant.TaskJobEnum.GIVE_COUPON.getExecutionType());
            }

            //给帮砍用户打标签
            saas.getShopApp(getShopId()).bargain.addAttendUserTag(bargain,userId);
        }

        return bargainMoney;
    }

    /**
     * userId用户已对该发起砍价record的砍价次数
     * @param userId
     * @param recordId
     * @return
     */
    public int getUserBargainNumber(int userId,int recordId){
        return db().selectCount().from(BARGAIN_USER_LIST).where(BARGAIN_USER_LIST.USER_ID.eq(userId).and(BARGAIN_USER_LIST.RECORD_ID.eq(recordId))).fetchOptionalInto(Integer.class).orElse(0);
    }

    /**
     * 砍一刀能砍掉的金额(区间内结算)
     * @param bargain
     * @param bargainRecord
     * @param userId
//     * @param userBargainNumber
     * @return
     */
    private BigDecimal getAbnormalBargainMoney(BargainRecord bargain,BargainGoodsRecord bargainGoods,BargainRecordRecord bargainRecord,int userId){
        BigDecimal bargainMoney;
        BargainUserListRecord firstBargain;
        BigDecimal remainMoney = bargainRecord.getGoodsPrice().subtract(bargainRecord.getBargainMoney()).subtract(bargainGoods.getFloorPrice().compareTo(BigDecimal.ZERO) > 0 ? bargainGoods.getFloorPrice() : BigDecimal.ZERO);
        if(bargainRecord.getUserId() == userId && (firstBargain = getFirstUserBargain(userId,bargainRecord.getId())) != null){
            //给自己砍的第二次，与第一次相同金额(翻倍)，或者剩下的全砍掉
            bargainMoney = firstBargain.getBargainMoney();
        }else{
            bargainMoney = getFixedBargainMoney(bargain);
        }
        if(remainMoney.compareTo(bargainMoney) < 0){
            bargainMoney = remainMoney;
        }

        return bargainMoney;
    }

    /**
     * 砍到任意金额（区间）结算模式，砍出的金额
     * @param bargain
     * @return
     */
    private BigDecimal getFixedBargainMoney(BargainRecord bargain){
        if(bargain.getBargainMoneyType().equals(BargainService.BARGAIN_MONEY_TYPE_FIXED)){
            return bargain.getBargainFixedMoney();
        }else{
            //区间内随机金额
            return new BigDecimal(Math.random() * (bargain.getBargainMaxMoney().floatValue() - bargain.getBargainMinMoney().floatValue()) + bargain.getBargainMinMoney().floatValue());
        }
    }

    /**
     * 砍到固定金额结算模式，砍出的金额
     * @param bargain
     * @return
     */
    private BigDecimal getRandomBargainMoney(BargainRecord bargain,BargainGoodsRecord bargainGoods,BargainRecordRecord bargainRecord){
        BigDecimal randomBargainMoney;
        double randomMultiple;
        if(bargainRecord.getUserNumber() == 0){
            //首次砍价
            if(bargain.getBargainMin() != null && bargain.getBargainMin() >= (double) 1 && bargain.getBargainMax() != null && bargain.getBargainMax() <= (double)49 && bargain.getBargainMin() <= bargain.getBargainMax()){
                //设置了有效的首次砍价可砍价比例区间（1-49之内）
                randomMultiple = Math.random() * (bargain.getBargainMax() - bargain.getBargainMin()) + bargain.getBargainMin();
            }else{
                //默认首次砍价比例10-49
                randomMultiple = Math.random() * (double)39 + (double)10;
            }
        }else{
            //默认砍价比例1-49
            randomMultiple = Math.random() * (double)48 + (double)1;
        }

        randomMultiple /= 100;

        int currentUserNumber = bargainRecord.getUserNumber() + 1;
        BigDecimal remainMoney = bargainRecord.getGoodsPrice().subtract(bargainRecord.getBargainMoney()).subtract(bargainGoods.getExpectationPrice().compareTo(BigDecimal.ZERO) > 0 ? bargainGoods.getExpectationPrice() : BigDecimal.ZERO);
        if(currentUserNumber < bargain.getExpectationNumber()){
            //不是最后一个
            double userRatio = ((bargain.getExpectationNumber() - currentUserNumber)/(bargain.getExpectationNumber()/2.0) + 2)/4.0;
            if(remainMoney.compareTo(new BigDecimal(((bargain.getExpectationNumber() - bargainRecord.getUserNumber()) * 0.5))) >= 0){
                if(bargainRecord.getUserNumber() > 0){
                    randomBargainMoney = new BigDecimal(randomMultiple * (remainMoney.subtract(new BigDecimal(((bargain.getExpectationNumber() - bargainRecord.getUserNumber()) * 0.25)))).doubleValue() * userRatio);
                }else{
                    randomBargainMoney = new BigDecimal(randomMultiple * remainMoney.doubleValue() * userRatio);
                }
            }else{
                randomBargainMoney = new BigDecimal((randomMultiple + 0.5) * (remainMoney.doubleValue()/(double) (bargain.getExpectationNumber() - bargainRecord.getUserNumber())) * userRatio);
            }
            if(randomBargainMoney.compareTo(remainMoney) >= 0){
                randomBargainMoney = remainMoney.divide(new BigDecimal((double)(bargain.getExpectationNumber() - currentUserNumber)));
            }
        }else{
            //最后一个砍价的人
            randomBargainMoney = remainMoney;
        }
        return randomBargainMoney.compareTo(BigDecimal.ZERO) > 0 ? randomBargainMoney : BigDecimal.ZERO;
    }

    /**
     * 砍一刀能砍掉的金额(固定金额结算)
     * @param bargain
     * @param bargainRecord
     * @param userId
     * @return
     */
    private BigDecimal getNormalBargainMoney(BargainRecord bargain,BargainGoodsRecord bargainGoods,BargainRecordRecord bargainRecord,int userId){
        BigDecimal bargainMoney;
        BargainUserListRecord firstBargain;
        if(bargainRecord.getUserId() == userId && (firstBargain = getFirstUserBargain(userId,bargainRecord.getId())) != null){
            //给自己砍的第二次，与第一次相同金额(翻倍)，或者剩下的全砍掉
            BigDecimal remainMoney = bargainRecord.getGoodsPrice().subtract(bargainRecord.getBargainMoney()).subtract(bargainGoods.getExpectationPrice().compareTo(BigDecimal.ZERO) > 0 ? bargainGoods.getExpectationPrice() : BigDecimal.ZERO);
            if(remainMoney.compareTo(firstBargain.getBargainMoney()) > 0){
                if(bargainRecord.getUserNumber() + 1 == bargain.getExpectationNumber()){
                    //最后一刀
                    bargainMoney = remainMoney;
                }else {
                    bargainMoney = firstBargain.getBargainMoney();
                }
            }else{
                bargainMoney = getRandomBargainMoney(bargain,bargainGoods,bargainRecord);
            }
        }else{
            bargainMoney = getRandomBargainMoney(bargain,bargainGoods,bargainRecord);
        }
        return bargainMoney;
    }

    /**
     * userId在这次recordId砍价中砍的第一刀
     * @param userId
     * @param recordId
     * @return
     */
    public BargainUserListRecord getFirstUserBargain(int userId,int recordId){
        return db().selectFrom(BARGAIN_USER_LIST).where(BARGAIN_USER_LIST.USER_ID.eq(userId).and(BARGAIN_USER_LIST.RECORD_ID.eq(recordId))).orderBy(BARGAIN_USER_LIST.CREATE_TIME.asc()).limit(1).fetchOne();
    }

    /**
     * 帮忙砍价的用户列表
     * @param recordId
     * @return
     */
    public List<BargainInfoVo.BargainUser> getBargainUserList(int recordId){
        List<BargainInfoVo.BargainUser> res =  db().select(BARGAIN_USER_LIST.asterisk(),USER.USERNAME,USER.WX_OPENID,USER_DETAIL.USER_AVATAR).from(
            BARGAIN_USER_LIST
            .leftJoin(USER).on(USER.USER_ID.eq(BARGAIN_USER_LIST.USER_ID))
            .leftJoin(USER_DETAIL).on(USER_DETAIL.USER_ID.eq(BARGAIN_USER_LIST.USER_ID))
        ).where(BARGAIN_USER_LIST.RECORD_ID.eq(recordId)).orderBy(BARGAIN_USER_LIST.CREATE_TIME.desc()).limit(20).fetchInto(BargainInfoVo.BargainUser.class);
        return res;
    }

    /**
     * 用户当天砍价次数
     * @param userId
     * @return
     */
    public int getUserTodayCutTimes(int userId){
        return db().selectCount().from(BARGAIN_USER_LIST).leftJoin(BARGAIN_RECORD).on(BARGAIN_USER_LIST.RECORD_ID.eq(BARGAIN_RECORD.ID)).where(BARGAIN_USER_LIST.USER_ID.eq(userId).and(BARGAIN_RECORD.USER_ID.notEqual(userId)).and(BARGAIN_USER_LIST.CREATE_TIME.ge(Util.getStartToday(new Date())))).fetchOptionalInto(Integer.class).orElse(0);
    }

    /**
     * 砍价完成消息通知
     */
    private void bargainSuccessNotify(int userId, BigDecimal bargainPrice, String bargainName, String goodsName, int recordId) {
        //订阅消息
        String[][] maData = new String[][]{{bargainName}, {goodsName + "砍价已完成"}, {Util.getdate("yyyy-MM-dd HH:mm:ss")}};
        MaSubscribeData buildData = MaSubscribeData.builder().data307(maData).build();

        //模板消息
        String[][] mpData = new String[][]{{"您有新的砍价进度", "#173177"}, {goodsName, "#173177"},
            {bargainPrice.toString(), "#173177"},
            {"已砍价成功，您可以告知您的好友砍价成功哦！", "#173177"}};
        String wxUnionId = db().select(Tables.USER.WX_UNION_ID).from(USER).where(USER.USER_ID.eq(userId)).fetchOptionalInto(String.class).orElse(null);
        String officeAppId = saas.shop.mp.findOffcialByShopId(getShopId());
        UserRecord wxUserInfo = saas.getShopApp(getShopId()).user.getUserByUnionId(wxUnionId);
        if(wxUnionId == null || officeAppId == null || wxUserInfo == null){
            return;
        }

        List<Integer> userIdList = new ArrayList<>();
        userIdList.add(wxUserInfo.getUserId());
        String page = "pages/bargaininfo/bargaininfo?record_id=" + recordId;

        RabbitMessageParam param = RabbitMessageParam.builder()
            .mpTemplateData(MpTemplateData.builder().config(MpTemplateConfig.BARGAIN_SUCCESS).data(mpData).build())
            .maTemplateData(MaTemplateData.builder().config(SubcribeTemplateCategory.INVITE_SUCCESS).data(buildData).build())
            .page(page).shopId(getShopId()).userIdList(userIdList).type(RabbitParamConstant.Type.SUCCESS_BRING_DOWN)
            .build();
        saas.taskJobMainService.dispatchImmediately(param, RabbitMessageParam.class.getName(), getShopId(), TaskJobsConstant.TaskJobEnum.SEND_MESSAGE.getExecutionType());
    }

}
