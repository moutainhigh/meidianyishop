package com.meidianyi.shop.service.shop.task.market;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.dao.foundation.database.DslPlus;
import com.meidianyi.shop.db.shop.Tables;
import com.meidianyi.shop.db.shop.tables.records.BargainGoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.BargainRecord;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.UserRecord;
import com.meidianyi.shop.service.foundation.jedis.data.DBOperating;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobsConstant;
import com.meidianyi.shop.service.pojo.shop.coupon.give.CouponGiveQueueParam;
import com.meidianyi.shop.service.pojo.shop.market.message.RabbitMessageParam;
import com.meidianyi.shop.service.pojo.shop.market.message.RabbitParamConstant;
import com.meidianyi.shop.service.pojo.shop.message.MpTemplateConfig;
import com.meidianyi.shop.service.pojo.shop.message.MpTemplateData;
import com.meidianyi.shop.service.pojo.shop.user.message.MaSubscribeData;
import com.meidianyi.shop.service.pojo.shop.user.message.MaTemplateData;
import com.meidianyi.shop.service.shop.goods.GoodsService;
import com.meidianyi.shop.service.shop.goods.es.EsDataUpdateMqService;
import com.meidianyi.shop.service.shop.market.bargain.BargainRecordService;
import com.meidianyi.shop.service.pojo.shop.market.message.maconfig.SubcribeTemplateCategory;
import jodd.util.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.jooq.Record4;
import org.jooq.Record5;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.meidianyi.shop.db.shop.tables.Bargain.BARGAIN;
import static com.meidianyi.shop.db.shop.tables.BargainGoods.BARGAIN_GOODS;
import static com.meidianyi.shop.db.shop.tables.BargainRecord.BARGAIN_RECORD;
import static com.meidianyi.shop.db.shop.tables.Goods.GOODS;
import static com.meidianyi.shop.db.shop.tables.User.USER;
import static java.util.stream.Collectors.toList;

/**
 * @author: 王兵兵
 * @create: 2019-12-09 17:07
 **/
@Service
public class BargainTaskService extends ShopBaseService {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private EsDataUpdateMqService esDataUpdateMqService;

    /**
     * 监控goodsType
     */
    public void monitorGoodsType(){
        //目前Goods表里还是砍价类型的商品
        List<Integer> pastBargainGoodsIdList = getPastBargainGoodsId();
        //在活动有效期内的砍价记录的ID列表
        List<Integer> currentBargainGoodsIdList = getCurrentBargainGoodsIdList();

        //求差集
        List<Integer> changeToNormalGoodsIds = Util.diffList(pastBargainGoodsIdList,currentBargainGoodsIdList);
        List<Integer> changeToActGoodsIds = Util.diffList(currentBargainGoodsIdList,pastBargainGoodsIdList);

        if(CollectionUtils.isNotEmpty(changeToNormalGoodsIds)){
            //活动已失效，将goodsType改回去
            goodsService.changeToNormalType(changeToNormalGoodsIds);
            //异步更新ES
            esDataUpdateMqService.addEsGoodsIndex(changeToNormalGoodsIds,getShopId(), DBOperating.UPDATE);
            //TODO 记录变动
        }

        if(CollectionUtils.isNotEmpty(changeToActGoodsIds)){
            //有新的活动生效，商品goodsType标记活动类型
            changeToBargainType(changeToActGoodsIds);
            //刷新砍价库存
            updateBargainGoodsStock(changeToActGoodsIds);
            //异步更新ES
            esDataUpdateMqService.addEsGoodsIndex(changeToActGoodsIds,getShopId(), DBOperating.UPDATE);
            //TODO 记录变动
        }
    }

    /**
     * 关闭已经失效的砍价记录
     */
    public void closeBargainRecord(){
        //活动已失效，但砍价状态还在进行中的发起记录
        List<Record4<Integer, Integer,Integer,String>> records = db().select(BARGAIN_RECORD.ID,BARGAIN_RECORD.USER_ID,BARGAIN.ID,BARGAIN.REWARD_COUPON_ID).from(BARGAIN_RECORD.leftJoin(BARGAIN).on(BARGAIN_RECORD.BARGAIN_ID.eq(BARGAIN.ID))).where(
            (BARGAIN.DEL_FLAG.eq(DelFlag.DISABLE_VALUE)
            .or(BARGAIN.STATUS.eq(BaseConstant.ACTIVITY_STATUS_DISABLE))
            .or(BARGAIN.START_TIME.gt(DateUtils.getLocalDateTime()))
            .or(BARGAIN.END_TIME.lt(DateUtils.getLocalDateTime())))
            .and(BARGAIN_RECORD.STATUS.eq(BargainRecordService.STATUS_IN_PROCESS))
        ).fetch();
        if(records != null && records.size() > 0){
            //更新砍价记录状态为失败
            List<Integer> recordIdList = records.stream().map(Record4::value1).collect(toList());
            db().update(BARGAIN_RECORD).set(BARGAIN_RECORD.STATUS,BargainRecordService.STATUS_FAILED).where(BARGAIN_RECORD.ID.in(recordIdList)).execute();

            //过滤不用发优惠券的发起记录
            records = records.stream().filter(record -> StringUtil.isNotEmpty(record.value4())).collect(Collectors.toList());
            //按照砍价活动ID分组
            Map<Integer,List<Record4<Integer, Integer,Integer,String>>> couponBargainRecords = records.stream().collect(Collectors.groupingBy(Record4::value3));

            if(couponBargainRecords != null && !couponBargainRecords.isEmpty()){
                couponBargainRecords.forEach((bargainId,recordList) -> {
                    //发放优惠券
                    CouponGiveQueueParam newParam = new CouponGiveQueueParam(
                        getShopId(), recordList.stream().map(Record4::value2).collect(toList()), bargainId, recordList.get(0).value4().split(","), BaseConstant.ACCESS_MODE_ISSUE, BaseConstant.GET_SOURCE_BARGAIN_FAILED);
                    //队列异步发放
                    saas.taskJobMainService.dispatchImmediately(newParam, CouponGiveQueueParam.class.getName(), getShopId(), TaskJobsConstant.TaskJobEnum.GIVE_COUPON.getExecutionType());
                });
            }
        }
    }

    /**
     * 定时发送砍价进度
     */
    public void sendBargainProgress() {
        Result<Record5<Integer, Integer, Integer, String, BigDecimal>> records = getExpiringBargainRecords();
        records.forEach(r -> {
            sendBargainProgressNotify(r.get(BARGAIN_RECORD.USER_ID), r.get(BARGAIN_GOODS.EXPECTATION_PRICE), r.get(GOODS.GOODS_NAME), r.get(BARGAIN_RECORD.ID), r.get(BARGAIN_RECORD.USER_NUMBER));
        });
    }

    private List<Integer> getPastBargainGoodsId(){
        return db().select(GOODS.GOODS_ID).from(GOODS).where(GOODS.GOODS_TYPE.eq(BaseConstant.ACTIVITY_TYPE_BARGAIN)).fetchInto(Integer.class);
    }

    private List<Integer> getCurrentBargainGoodsIdList(){
        List<Integer> res=  db().select(BARGAIN_GOODS.GOODS_ID).from(BARGAIN)
            .leftJoin(BARGAIN_GOODS).on(BARGAIN.ID.eq(BARGAIN_GOODS.BARGAIN_ID))
            .where(BARGAIN.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)
            .and(BARGAIN.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL))
            .and(BARGAIN.START_TIME.lt(DateUtils.getLocalDateTime()))
            .and(BARGAIN.END_TIME.gt(DateUtils.getLocalDateTime()))
        ).fetchInto(Integer.class);
        return res.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 批量将活动商品改成砍价商品
     * @param goodsIds
     */
    private void changeToBargainType(List<Integer> goodsIds){
        //比砍价优先级高的活动，不覆盖goodsType是这些的商品（5、10）
        List<Byte> highPriorityAct = Stream.of(BaseConstant.ACTIVITY_TYPE_SEC_KILL,BaseConstant.ACTIVITY_TYPE_PRE_SALE).collect(toList());
        db().update(GOODS).set(GOODS.GOODS_TYPE, BaseConstant.ACTIVITY_TYPE_BARGAIN).where(GOODS.GOODS_TYPE.notIn(highPriorityAct).and(GOODS.GOODS_ID.in(goodsIds))).execute();
    }

    /**
     * 刷新砍价库存
     * @param goodsIds
     */
    private void updateBargainGoodsStock(List<Integer> goodsIds){
        goodsIds.forEach(goodsId->{
            BargainRecord bargain = db().selectFrom(BARGAIN)
                .where(BARGAIN.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL))
                .and(BARGAIN.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
                .and(BARGAIN.START_TIME.lt(DateUtils.getLocalDateTime()))
                .and(BARGAIN.END_TIME.gt(DateUtils.getLocalDateTime()))
                .and(DslPlus.findInSet(goodsId, BARGAIN.GOODS_ID))
                .orderBy(BARGAIN.FIRST.desc(),BARGAIN.CREATE_TIME.desc())
                .fetchAny();
            if(bargain != null){
                BargainGoodsRecord bargainGoods = db().fetchAny(BARGAIN_GOODS,BARGAIN_GOODS.BARGAIN_ID.eq(bargain.getId()).and(BARGAIN_GOODS.GOODS_ID.eq(goodsId)));
                GoodsRecord goods = goodsService.getGoodsRecordById(goodsId);
                if(goods.getGoodsNumber() < bargainGoods.getStock()){
                    bargainGoods.setStock(goods.getGoodsNumber());
                    bargainGoods.update();
                }
            }
        });
    }

    /**
     * 获取距3小时结束的砍价记录
     *
     * @return
     */
    private Result<Record5<Integer, Integer, Integer, String, BigDecimal>> getExpiringBargainRecords() {
        Timestamp time = DateUtils.getDalyedDateTime(3600 * 3);
        Timestamp start = DateUtils.convertToTimestamp(DateUtils.dateFormat("yyyy-MM-dd HH:mm:00", time));
        Timestamp end = DateUtils.convertToTimestamp(DateUtils.dateFormat("yyyy-MM-dd HH:mm:59", time));
        logger().info("定时发送砍价进度");
        return db().select(BARGAIN_RECORD.ID, BARGAIN_RECORD.USER_ID, BARGAIN_RECORD.USER_NUMBER, GOODS.GOODS_NAME, BARGAIN_GOODS.EXPECTATION_PRICE)
            .from(BARGAIN_RECORD)
            .leftJoin(BARGAIN).on(BARGAIN_RECORD.BARGAIN_ID.eq(BARGAIN.ID))
            .leftJoin(GOODS).on(BARGAIN_RECORD.GOODS_ID.eq(GOODS.GOODS_ID))
            .leftJoin(BARGAIN_GOODS).on(BARGAIN_GOODS.BARGAIN_ID.eq(BARGAIN_RECORD.BARGAIN_ID).and(BARGAIN_GOODS.GOODS_ID.eq(BARGAIN_RECORD.GOODS_ID)))
            .where(BARGAIN_RECORD.STATUS.eq(BargainRecordService.STATUS_IN_PROCESS))
            .and(BARGAIN.END_TIME.le(end))
            .and(BARGAIN.END_TIME.ge(start))
            .fetch();
    }

    /**
     * 砍价进度提醒消息
     *
     * @param userId
     * @param bargainPrice
     * @param goodsName
     * @param recordId
     * @param bargainUserNumber
     */
    private void sendBargainProgressNotify(int userId, BigDecimal bargainPrice, String goodsName, Integer recordId, Integer bargainUserNumber) {
        //订阅消息
        String[][] maData = new String[][]{{goodsName}, {"已有" + bargainUserNumber.toString() + "人砍价"}, {"只剩3小时"}};
        MaSubscribeData buildData = MaSubscribeData.builder().data307(maData).build();

        //模板消息
        String[][] mpData = new String[][]{{"您有新的砍价进度", "#173177"}, {goodsName, "#173177"},
            {bargainPrice.toString(), "#173177"},
            {"已有" + bargainUserNumber.toString() + "人砍价，砍价剩余时间" + "03时00分00秒", "#173177"}};
        String wxUnionId = db().select(Tables.USER.WX_UNION_ID).from(USER).where(USER.USER_ID.eq(userId)).fetchOptionalInto(String.class).orElse(null);
        String officeAppId = saas.shop.mp.findOffcialByShopId(getShopId());
        UserRecord wxUserInfo = saas.getShopApp(getShopId()).user.getUserByUnionId(wxUnionId);
        if (wxUnionId == null || officeAppId == null || wxUserInfo == null) {
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
