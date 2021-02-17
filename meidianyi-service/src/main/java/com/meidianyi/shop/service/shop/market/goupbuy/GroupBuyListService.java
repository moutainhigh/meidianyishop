package com.meidianyi.shop.service.shop.market.goupbuy;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.GroupBuyDefineRecord;
import com.meidianyi.shop.db.shop.tables.records.GroupBuyListRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.base.ResultMessage;
import com.meidianyi.shop.service.pojo.shop.market.MarketOrderListParam;
import com.meidianyi.shop.service.pojo.shop.market.MarketOrderListVo;
import com.meidianyi.shop.service.pojo.shop.market.MarketSourceUserListParam;
import com.meidianyi.shop.service.pojo.shop.market.groupbuy.param.GroupBuyDetailParam;
import com.meidianyi.shop.service.pojo.shop.market.groupbuy.param.GroupBuyListParam;
import com.meidianyi.shop.service.pojo.shop.market.groupbuy.vo.GroupBuyDetailListVo;
import com.meidianyi.shop.service.pojo.shop.market.groupbuy.vo.GroupBuyListVo;
import com.meidianyi.shop.service.pojo.shop.market.groupbuy.vo.GroupOrderVo;
import com.meidianyi.shop.service.pojo.shop.member.MemberInfoVo;
import com.meidianyi.shop.service.pojo.shop.member.MemberPageListParam;
import com.meidianyi.shop.service.pojo.wxapp.market.groupbuy.GroupBuyUserInfo;
import com.meidianyi.shop.service.shop.goods.GoodsService;
import com.meidianyi.shop.service.shop.goods.GoodsSpecService;
import com.meidianyi.shop.service.shop.member.MemberService;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.SelectHavingStep;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

import static com.meidianyi.shop.db.shop.Tables.GROUP_BUY_DEFINE;
import static com.meidianyi.shop.db.shop.Tables.GROUP_BUY_LIST;
import static com.meidianyi.shop.db.shop.Tables.USER;
import static com.meidianyi.shop.db.shop.Tables.USER_DETAIL;
import static com.meidianyi.shop.service.pojo.shop.market.groupbuy.GroupBuyConstant.IS_GROUPER_Y;
import static com.meidianyi.shop.service.pojo.shop.market.groupbuy.GroupBuyConstant.JOIN_LIMIT_N;
import static com.meidianyi.shop.service.pojo.shop.market.groupbuy.GroupBuyConstant.OPEN_LIMIT_N;
import static com.meidianyi.shop.service.pojo.shop.market.groupbuy.GroupBuyConstant.STATUS_DEFAULT_SUCCESS;
import static com.meidianyi.shop.service.pojo.shop.market.groupbuy.GroupBuyConstant.STATUS_FAILED;
import static com.meidianyi.shop.service.pojo.shop.market.groupbuy.GroupBuyConstant.STATUS_ONGOING;
import static com.meidianyi.shop.service.pojo.shop.market.groupbuy.GroupBuyConstant.STATUS_SUCCESS;
import static com.meidianyi.shop.service.pojo.shop.market.groupbuy.GroupBuyConstant.STATUS_WAIT_PAY;

/**
 * @author 孔德成
 * @date 2019/7/29 14:54
 */
@Service
public class GroupBuyListService extends ShopBaseService {




    private static final String GROUP_ORDER_NUM = "groupOrderNum";

    @Autowired
    private MemberService memberService;
    @Autowired
    private GroupBuyService groupBuyService;
    @Autowired
    private GoodsSpecService goodsSpecService;
    @Autowired
    private GoodsService goodsService;

    /**
     * 查询团购列表
     *
     * @param param
     * @return
     */
    public PageResult<GroupBuyListVo> getListGroupBuy(GroupBuyListParam param) {
        //子查询
        SelectHavingStep<Record2<Integer, Integer>> table = db()
                .select(GROUP_BUY_LIST.ACTIVITY_ID, DSL.count(GROUP_BUY_LIST.ID).as(GROUP_ORDER_NUM))
                .from(GROUP_BUY_LIST)
                .where(GROUP_BUY_LIST.STATUS.in(STATUS_SUCCESS,STATUS_DEFAULT_SUCCESS))
                .groupBy(GROUP_BUY_LIST.ACTIVITY_ID);

        SelectConditionStep<? extends Record> records = db().select(
                GROUP_BUY_DEFINE.ID, GROUP_BUY_DEFINE.NAME, GROUP_BUY_DEFINE.LEVEL, GROUP_BUY_DEFINE.ACTIVITY_TYPE,
                GROUP_BUY_DEFINE.START_TIME, GROUP_BUY_DEFINE.END_TIME, GROUP_BUY_DEFINE.STATUS, GROUP_BUY_DEFINE.LIMIT_AMOUNT,
                GROUP_BUY_DEFINE.GOODS_ID, DSL.ifnull(table.field(GROUP_ORDER_NUM), 0).as(GROUP_ORDER_NUM))
                .from(GROUP_BUY_DEFINE)
                .leftJoin(table).on(table.field(GROUP_BUY_LIST.ACTIVITY_ID).eq(GROUP_BUY_DEFINE.ID))
                .where(GROUP_BUY_DEFINE.DEL_FLAG.eq(DelFlag.NORMAL.getCode()));
        records.orderBy(GROUP_BUY_DEFINE.LEVEL.desc(),GROUP_BUY_DEFINE.ID.desc());
        this.buildOptions(param, records);

        PageResult<GroupBuyListVo> page = getPageResult(records, param.getCurrentPage(), param.getPageRows(), GroupBuyListVo.class);
        page.dataList.forEach(vo -> {
            //状态
            vo.setCurrentState(Util.getActStatus(vo.getStatus(), vo.getStartTime(), vo.getEndTime()));
            //商品信息
            vo.setGoodsViews(goodsService.selectGoodsViewList(Util.stringToList(vo.getGoodsId())));
        });
        return page;
    }

    private void buildOptions(GroupBuyListParam param, SelectConditionStep<? extends Record> records) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        if (param.getType() != null) {
            switch (param.getType()) {
                case BaseConstant.NAVBAR_TYPE_ONGOING:
                    //正在活动
                    records.and(GROUP_BUY_DEFINE.START_TIME.lt(timestamp))
                            .and(GROUP_BUY_DEFINE.END_TIME.gt(timestamp))
                            .and(GROUP_BUY_DEFINE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL));
                    break;
                case BaseConstant.NAVBAR_TYPE_NOT_STARTED:
                    //还未开始
                    records.and(GROUP_BUY_DEFINE.START_TIME.gt(timestamp))
                            .and(GROUP_BUY_DEFINE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL));
                    break;
                case BaseConstant.NAVBAR_TYPE_FINISHED:
                    //已经结束
                    records.and(GROUP_BUY_DEFINE.END_TIME.lt(timestamp))
                            .and(GROUP_BUY_DEFINE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL));
                    break;
                case BaseConstant.NAVBAR_TYPE_DISABLED:
                    //停用
                    records.and(GROUP_BUY_DEFINE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_DISABLE));
                    break;
                default:
            }
        }

    }


    /**
     * 拼团订单
     *
     * @return
     */
    public PageResult<MarketOrderListVo> groupBuyOrderList(MarketOrderListParam param) {
        return saas().getShopApp(getShopId()).readOrder.getMarketOrderList(param, BaseConstant.ACTIVITY_TYPE_GROUP_BUY);

    }


    /**
     * 活动新增用户
     *
     * @param param
     */
    public PageResult<MemberInfoVo> groupBuyNewUserList(MarketSourceUserListParam param) {
        MemberPageListParam pageListParam = new MemberPageListParam();
        pageListParam.setCurrentPage(param.getCurrentPage());
        pageListParam.setPageRows(param.getPageRows());
        pageListParam.setMobile(param.getMobile());
        pageListParam.setUsername(param.getUserName());
        pageListParam.setInviteUserName(param.getInviteUserName());
        return memberService.getSourceActList(pageListParam, MemberService.INVITE_SOURCE_GROUPBUY, param.getActivityId());
    }


    /**
     * 参团明细
     *
     * @param param
     * @return
     */
    public PageResult<GroupBuyDetailListVo> detailGroupBuyList(GroupBuyDetailParam param) {
        SelectConditionStep<Record3<Integer, String, String>> table = db().select(GROUP_BUY_LIST.GROUP_ID, USER.MOBILE, USER.USERNAME).from(GROUP_BUY_LIST).leftJoin(USER).on(USER.USER_ID.eq(GROUP_BUY_LIST.USER_ID))
                .where(GROUP_BUY_LIST.IS_GROUPER.eq(IS_GROUPER_Y));
        SelectConditionStep<? extends Record> select = db().select(
                GROUP_BUY_LIST.STATUS,
                GROUP_BUY_LIST.ORDER_SN,
                GROUP_BUY_LIST.START_TIME,
                GROUP_BUY_LIST.END_TIME,
                table.field(USER.MOBILE).as(GroupBuyDetailListVo.COMMANDER_MOBILE),
                table.field(USER.USERNAME).as(GroupBuyDetailListVo.COMMANDER_NAME),
                table.field(GROUP_BUY_LIST.GROUP_ID).as(GroupBuyDetailListVo.COMMANDER_GROUP_ID),
                USER.USERNAME,
                USER.MOBILE,
                GROUP_BUY_DEFINE.NAME,
                GROUP_BUY_DEFINE.IS_DEFAULT,
                GROUP_BUY_DEFINE.DEL_FLAG)
                .from(GROUP_BUY_LIST)
                .leftJoin(USER).on(GROUP_BUY_LIST.USER_ID.eq(USER.USER_ID))
                .leftJoin(table).on(table.field(GROUP_BUY_LIST.GROUP_ID).eq(GROUP_BUY_LIST.GROUP_ID))
                .leftJoin(GROUP_BUY_DEFINE).on(GROUP_BUY_LIST.ACTIVITY_ID.eq(GROUP_BUY_DEFINE.ID))
                .where(GROUP_BUY_DEFINE.DEL_FLAG.eq(DelFlag.NORMAL.getCode()));
        builderQuery(select, param);

        select.orderBy(GROUP_BUY_LIST.GROUP_ID.desc(), GROUP_BUY_LIST.IS_GROUPER.desc(), GROUP_BUY_LIST.ID.desc());

        return getPageResult(select, param.getCurrentPage(), param.getPageRows(), GroupBuyDetailListVo.class);
    }

    private void builderQuery(SelectConditionStep<? extends Record> select, GroupBuyDetailParam param) {
        select.and(GROUP_BUY_LIST.ACTIVITY_ID.eq(param.getActivityId()));
        if (param.getMobile() != null && !param.getMobile().isEmpty()) {
            select.and(USER.MOBILE.eq(param.getMobile()));
        }
        if (param.getNickName() != null && !param.getNickName().isEmpty()) {
            select.and(USER.USERNAME.eq(param.getNickName()));
        }
        if (param.getStatus() != null) {
            if (param.getStatus().equals(STATUS_WAIT_PAY)){
                select.and(GROUP_BUY_LIST.STATUS.eq(STATUS_WAIT_PAY));
            }else if (param.getStatus().equals(STATUS_ONGOING)){
                select.and(GROUP_BUY_LIST.STATUS.eq(param.getStatus()));
            }else if (param.getStatus().equals(STATUS_SUCCESS)){
                select.and(GROUP_BUY_LIST.STATUS.in(STATUS_SUCCESS,STATUS_DEFAULT_SUCCESS));
            }else if (param.getStatus().equals(STATUS_FAILED)){
                select.and(GROUP_BUY_LIST.STATUS.eq(STATUS_FAILED));
            }
        }

    }

    public GroupOrderVo getByOrder(String orderSn) {
        return db().select(GROUP_BUY_LIST.ID, GROUP_BUY_LIST.ACTIVITY_ID, GROUP_BUY_LIST.GOODS_ID, GROUP_BUY_LIST.GROUP_ID, GROUP_BUY_LIST.USER_ID, GROUP_BUY_LIST.IS_GROUPER, GROUP_BUY_LIST.ORDER_SN, GROUP_BUY_LIST.STATUS, GROUP_BUY_LIST.START_TIME, GROUP_BUY_LIST.END_TIME).
                from(GROUP_BUY_LIST).
                where(GROUP_BUY_LIST.ORDER_SN.eq(orderSn)).
                fetchOneInto(GroupOrderVo.class);
    }

    /**
     * 根据拼团获取团长
     * @param groupId
     * @return
     */
    public GroupBuyListRecord getGrouperByGroupId(Integer groupId) {
        return db().selectFrom(GROUP_BUY_LIST)
                .where(GROUP_BUY_LIST.STATUS.in(STATUS_ONGOING,STATUS_SUCCESS,STATUS_DEFAULT_SUCCESS,STATUS_FAILED))
                .and(GROUP_BUY_LIST.IS_GROUPER.in(IS_GROUPER_Y))
                .and(GROUP_BUY_LIST.GROUP_ID.eq(groupId)).fetchAny();

    }


    /**
     * 判断是否能创建拼团订单
     *  @param userId
     * @param date
     * @param activityId
     * @param groupId
     * @return
     */
    public ResultMessage canCreatePinGroupOrder(Integer userId, Timestamp date, Integer activityId, Integer groupId, Byte isGrouper) {
        logger().debug("判断是否能创建拼团订单[activityId:{}]",activityId);
        GroupBuyDefineRecord groupBuyRecord = groupBuyService.getGroupBuyRecord(activityId);
        if (Objects.isNull(groupBuyRecord)|| DelFlag.DISABLE_VALUE.equals(groupBuyRecord.getDelFlag())){
            logger().debug("拼团活动未找到,不存在或者已删除[activityId:{}]",activityId);
            return ResultMessage.builder().jsonResultCode(JsonResultCode.GROUP_BUY_GROUPID_DOES_NOT_EXIST).flag(false).build();
        }
        if (IS_GROUPER_Y.equals(isGrouper)){
            if (groupBuyRecord.getStatus().equals(BaseConstant.ACTIVITY_STATUS_DISABLE)){
                logger().debug("该活动未启用[activityId:{}]",activityId);
                return ResultMessage.builder().jsonResultCode(JsonResultCode.GROUP_BUY_ACTIVITY_STATUS_DISABLE).build();
            }
            if (groupBuyRecord.getStartTime().compareTo(date) >0){
                logger().debug("活动未开始[activityId:{}]",activityId);
                return ResultMessage.builder().jsonResultCode(JsonResultCode.GROUP_BUY_ACTIVITY_STATUS_NOTSTART).build();
            }
            if (groupBuyRecord.getEndTime().compareTo(date)<0){
                logger().debug("活动已经结束[activityId:{}]",activityId);
                return ResultMessage.builder().jsonResultCode(JsonResultCode.GROUP_BUY_ACTIVITY_STATUS_END).build();
            }
            Integer joinFlag = db().selectCount().from(GROUP_BUY_LIST).where(GROUP_BUY_LIST.USER_ID.eq(userId))
                    .and(GROUP_BUY_LIST.GROUP_ID.eq(groupId)).and(GROUP_BUY_LIST.ACTIVITY_ID.eq(activityId))
                    .and(GROUP_BUY_LIST.STATUS.in(STATUS_ONGOING, STATUS_SUCCESS)).fetchOneInto(Integer.class);
            if (joinFlag>0){
                logger().debug("你已参加过该团[activityId:{}]",activityId);
                return ResultMessage.builder().jsonResultCode(JsonResultCode.GROUP_BUY_ACTIVITY_GROUP_JOINING).build();
            }
            Integer count = db().selectCount().from(GROUP_BUY_LIST)
                    .where(GROUP_BUY_LIST.USER_ID.eq(userId))
                    .and(GROUP_BUY_LIST.ACTIVITY_ID.eq(activityId))
                    .and(GROUP_BUY_LIST.IS_GROUPER.eq(isGrouper))
                    .and(GROUP_BUY_LIST.STATUS.in(STATUS_ONGOING, STATUS_SUCCESS)).fetchOneInto(Integer.class);
            if (!groupBuyRecord.getOpenLimit().equals(OPEN_LIMIT_N.shortValue())&&groupBuyRecord.getOpenLimit()<=count){
                logger().debug("该活动开团个数已经达到上限[activityId:{}]",activityId);
                return ResultMessage.builder().jsonResultCode(JsonResultCode.GROUP_BUY_ACTIVITY_GROUP_OPEN_LIMIT_MAX).build();
            }
        }else {
            Result<GroupBuyListRecord> groupBuyList = db().selectFrom(GROUP_BUY_LIST).where(GROUP_BUY_LIST.GROUP_ID.eq(groupId)).fetch();
            GroupBuyListRecord groupBuyListRecord = groupBuyList.stream().filter(group -> group.getIsGrouper().equals(IS_GROUPER_Y)).findFirst().get();
            if (STATUS_FAILED.equals(groupBuyListRecord.getStatus())){
                logger().debug("该团已经取消");
                return ResultMessage.builder().jsonResultCode(JsonResultCode.GROUP_BUY_ACTIVITY_GROUP_STATUS_CANCEL).build();
            }
            long count1 = groupBuyList.stream().filter(group -> STATUS_SUCCESS.equals(group.getStatus()) || STATUS_DEFAULT_SUCCESS.equals(group.getStatus()) || STATUS_ONGOING.equals(group.getStatus())).count();
            if (groupBuyRecord.getLimitAmount()<=count1){
                logger().debug("该团人数已经满了");
                return ResultMessage.builder().jsonResultCode(JsonResultCode.GROUP_BUY_ACTIVITY_GROUP_EMPLOEES_MAX).build();
            }
            Integer count = db().selectCount().from(GROUP_BUY_LIST).where(GROUP_BUY_LIST.IS_GROUPER.eq(isGrouper))
                    .and(GROUP_BUY_LIST.USER_ID.eq(userId)).and(GROUP_BUY_LIST.ACTIVITY_ID.eq(activityId))
                    .and(GROUP_BUY_LIST.STATUS.in(STATUS_SUCCESS, STATUS_ONGOING)).fetchOneInto(Integer.class);
            if (!groupBuyRecord.getJoinLimit().equals(JOIN_LIMIT_N.shortValue())&&groupBuyRecord.getJoinLimit()<=count){
                logger().debug("该活动参团个数已经达到上限[activityId:{}]",activityId);
                return ResultMessage.builder().jsonResultCode(JsonResultCode.GROUP_BUY_ACTIVITY_GROUP_JOIN_LIMIT_MAX).build();
            }
            Integer joinFlag = db().selectCount().from(GROUP_BUY_LIST).where(GROUP_BUY_LIST.USER_ID.eq(userId))
                    .and(GROUP_BUY_LIST.GROUP_ID.eq(groupId)).and(GROUP_BUY_LIST.ACTIVITY_ID.eq(activityId))
                    .and(GROUP_BUY_LIST.STATUS.in(STATUS_ONGOING, STATUS_SUCCESS)).fetchOneInto(Integer.class);
            if (joinFlag>0){
                logger().debug("你已参加过该团[activityId:{}]",activityId);
                return ResultMessage.builder().jsonResultCode(JsonResultCode.GROUP_BUY_ACTIVITY_GROUP_JOINING).build();
            }
            GroupBuyListRecord grouperInfo = getGrouperByGroupId(groupId);
            if (STATUS_SUCCESS.equals(grouperInfo.getStatus())|| STATUS_DEFAULT_SUCCESS.equals(grouperInfo.getStatus())){
                return ResultMessage.builder().jsonResultCode(JsonResultCode.GROUP_BUY_ACTIVITY_GROUP_SUCCESS).build();
            }else if (grouperInfo.getStatus().equals(STATUS_FAILED)){
                return ResultMessage.builder().jsonResultCode(JsonResultCode.GROUP_BUY_ACTIVITY_GROUP_JOINING).build();
            }
        }
        return ResultMessage.builder().jsonResultCode(JsonResultCode.CODE_SUCCESS).flag(true).build();
    }

    /**
     * 获取用或开启的有效拼团数量 进行中或已成功
     * @param userId 用户id
     * @param activityId 活动id
     * @return 已参团的数量
     */
    public int getUserOpenGroupBuyActivityNum(Integer userId,Integer activityId){
        return db().selectCount().from(GROUP_BUY_LIST)
            .where(GROUP_BUY_LIST.USER_ID.eq(userId))
            .and(GROUP_BUY_LIST.ACTIVITY_ID.eq(activityId))
            .and(GROUP_BUY_LIST.IS_GROUPER.eq((byte) 1))
            .and(GROUP_BUY_LIST.STATUS.in(STATUS_ONGOING, STATUS_SUCCESS)).fetchOneInto(Integer.class);
    }


    /**
     * 拼团用户信息
     * @param groupId 拼团id
     * @return
     */
    public List<GroupBuyUserInfo> getGroupUserList(Integer groupId){
        return db().select(GROUP_BUY_LIST.ORDER_SN,GROUP_BUY_LIST.STATUS,GROUP_BUY_LIST.USER_ID, USER_DETAIL.USERNAME, USER_DETAIL.USER_AVATAR)
                .from(GROUP_BUY_LIST)
                .leftJoin(USER_DETAIL).on(USER_DETAIL.USER_ID.eq(GROUP_BUY_LIST.USER_ID))
                .where(GROUP_BUY_LIST.STATUS.in(STATUS_SUCCESS, STATUS_DEFAULT_SUCCESS, STATUS_ONGOING))
                .and(GROUP_BUY_LIST.GROUP_ID.eq(groupId))
                .orderBy(GROUP_BUY_LIST.IS_GROUPER.desc(), GROUP_BUY_LIST.CREATE_TIME.desc())
                .fetchInto(GroupBuyUserInfo.class);
    }
}
