package com.meidianyi.shop.service.shop.activity.dao;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.db.shop.tables.records.GroupBuyDefineRecord;
import com.meidianyi.shop.db.shop.tables.records.GroupBuyListRecord;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import com.meidianyi.shop.service.pojo.shop.market.groupbuy.GroupBuyConstant;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.GoodsActivityAnnounceMpVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.groupbuy.GroupBuyListMpVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.groupbuy.GroupBuyMpVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.groupbuy.GroupBuyPrdMpVo;
import com.meidianyi.shop.service.shop.image.ImageService;
import com.meidianyi.shop.service.shop.market.goupbuy.GroupBuyService;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.Tables.*;

/**
 * @author 李晓冰
 * @date 2019年10月29日
 */
@Service
public class GroupBuyProcessorDao extends GroupBuyService {

    @Autowired
    private ImageService imageService;

    /**
     * 获取集合内商品所参与的拼团信息
     *
     * @param goodsIds 待查询商品id集合
     * @param date     限制时间
     * @return key:商品id value:List<Record3<Integer, Integer, BigDecimal>> GROUP_BUY_DEFINE.ID, GROUP_BUY_DEFINE.GOODS_ID, GROUP_BUY_PRODUCT_DEFINE.GROUP_PRICE
     * @author 李晓冰
     */
    public Map<Integer, List<Record3<Integer, Integer, BigDecimal>>> getGoodsGroupBuyListInfo(List<Integer> goodsIds, Timestamp date) {

        // 获取有效拼团规格信息,首先根据实际，活动状态进行过滤，然后根据活动level进行从大到小排序，在根据规格价格从小到大排序，
        // 再根据商品id进行分组。（一个商品有多个拼团活动则取优先级最高的）
        return db().select(GROUP_BUY_PRODUCT_DEFINE.ACTIVITY_ID,GROUP_BUY_PRODUCT_DEFINE.GOODS_ID,GROUP_BUY_PRODUCT_DEFINE.GROUP_PRICE)
            .from(GROUP_BUY_DEFINE).innerJoin(GROUP_BUY_PRODUCT_DEFINE).on(GROUP_BUY_DEFINE.ID.eq(GROUP_BUY_PRODUCT_DEFINE.ACTIVITY_ID))
            .where(GROUP_BUY_DEFINE.START_TIME.lt(date)).and(GROUP_BUY_DEFINE.END_TIME.gt(date)).and(GROUP_BUY_DEFINE.STOCK.gt((short) 0))
            .and(GROUP_BUY_DEFINE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL)).and(GROUP_BUY_DEFINE.DEL_FLAG.eq(DelFlag.NORMAL.getCode())).and(GROUP_BUY_PRODUCT_DEFINE.GOODS_ID.in(goodsIds))
            .orderBy(GROUP_BUY_DEFINE.LEVEL.desc(),GROUP_BUY_DEFINE.CREATE_TIME.desc(),GROUP_BUY_PRODUCT_DEFINE.GROUP_PRICE.asc())
            .fetch().stream().collect(Collectors.groupingBy(x -> x.get(GROUP_BUY_PRODUCT_DEFINE.GOODS_ID)));
    }

    /**
     * 获取活动预告
     * @param goodsId 活动id
     * @param date 待比较时间
     * @return
     */
    public GoodsActivityAnnounceMpVo getAnnounceInfo(Integer goodsId,Timestamp date){

        Record4<Integer, Byte, Timestamp, Integer> activityInfo = db().select(GROUP_BUY_DEFINE.ID, GROUP_BUY_DEFINE.IS_GROUPER_CHEAP, GROUP_BUY_DEFINE.START_TIME,GROUP_BUY_DEFINE.PRE_TIME)
            .from(GROUP_BUY_DEFINE).innerJoin(GROUP_BUY_PRODUCT_DEFINE).on(GROUP_BUY_DEFINE.ID.eq(GROUP_BUY_PRODUCT_DEFINE.ACTIVITY_ID))
            .where(GROUP_BUY_DEFINE.END_TIME.gt(date)).and(GROUP_BUY_DEFINE.STOCK.gt((short) 0))
            .and(GROUP_BUY_DEFINE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL)).and(GROUP_BUY_DEFINE.DEL_FLAG.eq(DelFlag.NORMAL.getCode())).and(GROUP_BUY_PRODUCT_DEFINE.GOODS_ID.eq(goodsId))
            .orderBy(GROUP_BUY_DEFINE.LEVEL.desc()).fetchAny();

        if (activityInfo == null) {
            return null;
        }
        // 不预告
        if (GoodsConstant.ACTIVITY_NOT_PRE.equals(activityInfo.get(GROUP_BUY_DEFINE.PRE_TIME))) {
            return null;
        }
        // 定时预告判断
        if (GoodsConstant.ACTIVITY_NOT_PRE.compareTo(activityInfo.get(GROUP_BUY_DEFINE.PRE_TIME)) < 0) {
            Integer hours = activityInfo.get(GROUP_BUY_DEFINE.PRE_TIME);
            int timeHourDifference = DateUtils.getTimeHourDifference(activityInfo.get(GROUP_BUY_DEFINE.START_TIME), date);
            if (timeHourDifference > hours) {
                return null;
            }
        }

        SortField<?> sortField;
        if (GroupBuyService.GROUPER_CHEAP.equals(activityInfo.get(GROUP_BUY_DEFINE.IS_GROUPER_CHEAP))) {
            sortField = GROUP_BUY_PRODUCT_DEFINE.GROUPER_PRICE.asc();
        } else {
            sortField = GROUP_BUY_PRODUCT_DEFINE.GROUP_PRICE.asc();
        }

        Record2<BigDecimal, BigDecimal> prdInfo = db().select(GROUP_BUY_PRODUCT_DEFINE.GROUP_PRICE, GROUP_BUY_PRODUCT_DEFINE.GROUPER_PRICE).from(GROUP_BUY_PRODUCT_DEFINE)
            .where(GROUP_BUY_PRODUCT_DEFINE.ACTIVITY_ID.eq(activityInfo.get(GROUP_BUY_DEFINE.ID)).and(GROUP_BUY_PRODUCT_DEFINE.GOODS_ID.eq(goodsId)))
            .orderBy(sortField).fetchAny();

        GoodsActivityAnnounceMpVo mpVo = new GoodsActivityAnnounceMpVo();
        mpVo.setActivityType(BaseConstant.ACTIVITY_TYPE_GROUP_BUY);
        mpVo.setStartTime(activityInfo.get(GROUP_BUY_DEFINE.START_TIME));

        if (GroupBuyService.GROUPER_CHEAP.equals(activityInfo.get(GROUP_BUY_DEFINE.IS_GROUPER_CHEAP))) {
            mpVo.setRealPrice(prdInfo.get(GROUP_BUY_PRODUCT_DEFINE.GROUPER_PRICE));
        } else {
            mpVo.setRealPrice(prdInfo.get(GROUP_BUY_PRODUCT_DEFINE.GROUP_PRICE));
        }
        return mpVo;
    }
    /**
     * 商品详情-获取拼团信息
     *
     * @param userId     用户id
     * @param activityId 拼团活动id
     * @return 拼团活动信息
     */
    public GroupBuyMpVo getGroupBuyInfo(Integer userId, Integer activityId) {
        GroupBuyMpVo vo = new GroupBuyMpVo();
        vo.setActivityId(activityId);
        vo.setActivityType(BaseConstant.ACTIVITY_TYPE_GROUP_BUY);

        GroupBuyDefineRecord groupBuyDefineRecord = getGroupBuyDefinedInfoBuyId(activityId);
        Timestamp now = DateUtils.getLocalDateTime();

        Byte aByte = canCreatePinGroupOrder(userId, now, activityId, groupBuyDefineRecord);
        vo.setActState(aByte);

        // 活动不存在
        if (BaseConstant.ACTIVITY_STATUS_NOT_HAS.equals(aByte)) {
            return vo;
        }

        // 活动未开始
        if (BaseConstant.ACTIVITY_STATUS_NOT_START.equals(aByte)) {
            vo.setStartTime((groupBuyDefineRecord.getStartTime().getTime() - now.getTime())/1000);
        }
        vo.setEndTime((groupBuyDefineRecord.getEndTime().getTime() - now.getTime())/1000);

        /**拼团类型*/
        vo.setGroupType(groupBuyDefineRecord.getActivityType());
        /**是否团长优惠*/
        vo.setIsGrouperCheap(groupBuyDefineRecord.getIsGrouperCheap());
        /**参团人数*/
        vo.setLimitAmount(groupBuyDefineRecord.getLimitAmount());

        /** 商品拼团最小最大购买数量 */
        vo.setLimitBuyNum(groupBuyDefineRecord.getLimitBuyNum().intValue());
        vo.setLimitMaxNum(groupBuyDefineRecord.getLimitMaxNum().intValue());

        /** 拼团表中 shippingType 活动运费 1 免运费 2 按照商品原运费模板*/
        vo.setFreeShip((byte) (groupBuyDefineRecord.getShippingType() == 1 ? 0 : 1));

        /**已成功拼团数量*/
        logger().debug("小程序-商品详情-拼团信息-已成团数量");
        vo.setGroupBuySuccessCount(getGroupBuySuccessCount(activityId)+groupBuyDefineRecord.getBeginNum());

        /** 正在进行中拼团信息列表 */
        logger().debug("小程序-商品详情-拼团信息-正在拼团列表信息");
        vo.setGroupBuyListMpVos(getGroupBuyListInfo(activityId, groupBuyDefineRecord.getLimitAmount(), now));

        return vo;
    }


    /**
     * 商品详情-获取拼团规格信息
     *
     * @param activityId 拼团活动id
     * @param prdIds 活动对应商品的规格ID集合，避免商家在设置平台活动后又删除了对应的规格
     * @return {@link GroupBuyPrdMpVo} 拼团规格信息
     */
    public List<GroupBuyPrdMpVo> getGroupBuyPrdInfo(Integer activityId, Collection<Integer> prdIds) {
        return db().select(GROUP_BUY_PRODUCT_DEFINE.PRODUCT_ID, GROUP_BUY_PRODUCT_DEFINE.STOCK, GROUP_BUY_PRODUCT_DEFINE.GROUP_PRICE, GROUP_BUY_PRODUCT_DEFINE.GROUPER_PRICE)
            .from(GROUP_BUY_PRODUCT_DEFINE)
            .where(GROUP_BUY_PRODUCT_DEFINE.ACTIVITY_ID.eq(activityId).and(GROUP_BUY_PRODUCT_DEFINE.PRODUCT_ID.in(prdIds)))
            .fetchInto(GroupBuyPrdMpVo.class);
    }

    /**
     * 获取拼团成功数量
     *
     * @param activityId 拼团活动id
     * @return 已成团数量
     */
    private Integer getGroupBuySuccessCount(Integer activityId) {
        return db().fetchCount(GROUP_BUY_LIST, GROUP_BUY_LIST.ACTIVITY_ID.eq(activityId).and(GROUP_BUY_LIST.STATUS.eq(GroupBuyConstant.STATUS_SUCCESS)).and(GROUP_BUY_LIST.IS_GROUPER.eq(GroupBuyConstant.IS_GROUPER_Y)));
    }

    /**
     * 获取正在进行拼团-列表信息信息
     *
     * @param activityId  活动id
     * @param limitAmount 活动成团人数
     * @param now         当前时间
     * @return {@link GroupBuyListMpVo} 列表信息
     */
    private List<GroupBuyListMpVo> getGroupBuyListInfo(Integer activityId, Short limitAmount, Timestamp now) {
        Map<Integer, List<Record5<Integer, Byte, Timestamp, String, String>>> groups =
            db().select(GROUP_BUY_LIST.GROUP_ID, GROUP_BUY_LIST.IS_GROUPER, GROUP_BUY_LIST.START_TIME, USER_DETAIL.USERNAME, USER_DETAIL.USER_AVATAR)
                .from(GROUP_BUY_LIST).innerJoin(USER_DETAIL).on(GROUP_BUY_LIST.USER_ID.eq(USER_DETAIL.USER_ID))
                .where(GROUP_BUY_LIST.ACTIVITY_ID.eq(activityId).and(GROUP_BUY_LIST.STATUS.eq(GroupBuyConstant.STATUS_ONGOING)))
                .orderBy(GROUP_BUY_LIST.START_TIME.desc(), GROUP_BUY_LIST.IS_GROUPER.desc())
                .fetch().stream().collect(Collectors.groupingBy(x -> x.get(GROUP_BUY_LIST.GROUP_ID)));

        List<GroupBuyListMpVo> groupBuyListMpVos = new ArrayList<>(groups.size());

        groups.forEach((key, values) -> {
            GroupBuyListMpVo vo = new GroupBuyListMpVo();
            Record5<Integer, Byte, Timestamp, String, String> record5 = values.get(0);
            vo.setGroupId(record5.get(GROUP_BUY_LIST.GROUP_ID));
            vo.setUserName(record5.get(USER_DETAIL.USERNAME));
            vo.setUserAvatar(imageService.getImgFullUrl(record5.get(USER_DETAIL.USER_AVATAR)));
            vo.setRemainNum(limitAmount - values.size());
            long passedTime = (now.getTime() - record5.get(GROUP_BUY_LIST.START_TIME).getTime())/1000;
            vo.setRemainTime(GoodsConstant.GROUP_BUY_LIMIT_TIME - passedTime);
            groupBuyListMpVos.add(vo);
        });
        return groupBuyListMpVos;
    }

    /**
     * 保存
     * @param groupBuyProductList
     * @return
     */
    public int save(GroupBuyListRecord groupBuyProductList) {
        return db().executeInsert(groupBuyProductList);
    }

}
