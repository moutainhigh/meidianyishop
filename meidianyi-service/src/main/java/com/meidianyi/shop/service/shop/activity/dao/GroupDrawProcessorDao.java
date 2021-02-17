package com.meidianyi.shop.service.shop.activity.dao;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.db.shop.tables.records.GroupDrawRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.groupdraw.GroupDrawMpVo;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

import static com.meidianyi.shop.db.shop.Tables.JOIN_GROUP_LIST;
import static com.meidianyi.shop.db.shop.tables.GroupDraw.GROUP_DRAW;


/**
 * @author 李晓冰
 * @date 2020年02月27日
 */
@Service
public class GroupDrawProcessorDao extends ShopBaseService {

    /**
     * 拼团抽奖活动详情信息获取
     * @param activityId 活动id
     * @param userId 用户id
     * @return 拼团抽奖信息
     */
    public GroupDrawMpVo getGroupDrawInfoForDetail(Integer activityId,Integer userId){
        GroupDrawMpVo vo =new GroupDrawMpVo();
        vo.setActivityId(activityId);
        vo.setActivityType(BaseConstant.ACTIVITY_TYPE_GROUP_DRAW);
        vo.setActState(BaseConstant.ACTIVITY_STATUS_CAN_USE);
        GroupDrawRecord groupDrawRecord = db().selectFrom(GROUP_DRAW).where(GROUP_DRAW.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)).and(GROUP_DRAW.ID.eq(activityId))
            .fetchAny();

        Timestamp now = DateUtils.getLocalDateTime();
        if (groupDrawRecord == null) {
            logger().debug("小程序-商品详情-拼团抽奖信息-活动已删除");
            vo.setActState(BaseConstant.ACTIVITY_STATUS_NOT_HAS);
            return vo;
        } else if (BaseConstant.ACTIVITY_STATUS_DISABLE.equals(groupDrawRecord.getStatus())) {
            logger().debug("小程序-商品详情-拼团抽奖信息-已停用");
            vo.setActState(BaseConstant.ACTIVITY_STATUS_DISABLE);
        } else if (now.compareTo(groupDrawRecord.getStartTime()) < 0) {
            logger().debug("小程序-商品详情-拼团抽奖信息-活动未开始");
            vo.setStartTime((groupDrawRecord.getStartTime().getTime() - now.getTime())/1000);
            vo.setEndTime((groupDrawRecord.getEndTime().getTime() - now.getTime())/1000);
            vo.setActState(BaseConstant.ACTIVITY_STATUS_NOT_START);
        } else if (now.compareTo(groupDrawRecord.getEndTime()) > 0) {
            logger().debug("小程序-商品详情-拼团抽奖信息-活动已结束");
            vo.setActState(BaseConstant.ACTIVITY_STATUS_END);
        } else {
            // 处于活动可使用状态,设置活动结束时间，查询用户是否还可以进行开团
            vo.setEndTime((groupDrawRecord.getEndTime().getTime() - now.getTime())/1000);
            int beCaptainTime = db().fetchCount(JOIN_GROUP_LIST, JOIN_GROUP_LIST.GROUP_DRAW_ID.eq(activityId)
                .and(JOIN_GROUP_LIST.IS_GROUPER.eq(GoodsConstant.GROUP_CAPTAIN)).and(JOIN_GROUP_LIST.USER_ID.eq(userId)));
            if (beCaptainTime >= groupDrawRecord.getOpenLimit()) {
                logger().debug("小程序-商品详情-拼团抽奖信息-用户已达到开团上限，无法进行开团");
                vo.setActState(BaseConstant.ACTIVITY_STATUS_MAX_COUNT_LIMIT);
            }
        }

        vo.setPayMoney(groupDrawRecord.getPayMoney());
        vo.setLimitAmount(groupDrawRecord.getLimitAmount());
        int joinNum = db().fetchCount(JOIN_GROUP_LIST, JOIN_GROUP_LIST.GROUP_DRAW_ID.eq(activityId));
        if (joinNum >= groupDrawRecord.getToNumShow()) {
            vo.setShowJoinNumber(true);
            vo.setJoinNumber(joinNum);
        } else {
            vo.setShowJoinNumber(false);
        }

        return vo;
    }
}
