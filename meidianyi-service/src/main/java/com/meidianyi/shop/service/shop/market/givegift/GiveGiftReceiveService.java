package com.meidianyi.shop.service.shop.market.givegift;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.shop.tables.GiveGiftReceive;
import com.meidianyi.shop.db.shop.tables.User;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.market.givegift.GiveGiftConstant;
import com.meidianyi.shop.service.pojo.shop.market.givegift.receive.GiveGiftReceiveListParam;
import com.meidianyi.shop.service.pojo.shop.market.givegift.receive.GiveGiftReceiveListVo;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.jooq.tools.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

import static com.meidianyi.shop.db.shop.Tables.*;
import static com.meidianyi.shop.db.shop.tables.GiveGiftReceive.GIVE_GIFT_RECEIVE;
import static com.meidianyi.shop.db.shop.tables.User.USER;

/**
 * Table:GIVE_GIFT_RECEIVE
 *
 * @author 王帅, kdc
 */

@Service
public class GiveGiftReceiveService extends ShopBaseService {

    public final GiveGiftReceive TABLE = GIVE_GIFT_RECEIVE;

    @Autowired
    private GiveGiftCartService giveGiftCart;

    /**
     * 活动收到的礼物量
     *
     * @param actId
     * @return
     */
    public Integer getGetGiftOrderCt(Integer actId) {
        return db().selectCount()
                .from(GIVE_GIFT_RECEIVE)
                .leftJoin(GIVE_GIFT_CART).on(GIVE_GIFT_RECEIVE.GIFT_CART_ID.eq(GIVE_GIFT_RECEIVE.ID))
                .where(GIVE_GIFT_CART.GIVE_GIFT_ID.eq(actId)).fetchOne().component1();
    }

    /**
     * 收礼人数
     * @param mainOrderSn
     * @return
     */
    public Integer getReceiveNumByoOrderSn(String mainOrderSn) {
        return db().selectCount().from(GIVE_GIFT_RECEIVE).where(GIVE_GIFT_RECEIVE.MAIN_ORDER_SN.eq(mainOrderSn)).fetchOne().component1();
    }

    /**
     * 获取收礼列表
     *
     * @param param GiveGiftReceiveListParam
     * @return PageResult<GiveGiftReceiveListVo>
     */
    public PageResult<GiveGiftReceiveListVo> giveGiftReceiveList(GiveGiftReceiveListParam param) {
        User receive = USER.as("receive");
        User giver = USER.as("giver");
        SelectJoinStep<? extends Record> select = db()
                .select(
                        receive.USER_ID.as(receive.getName() + receive.USER_ID.getName()),
                        receive.USERNAME.as(receive.getName() + receive.USERNAME.getName()),
                        receive.MOBILE.as(receive.getName() + receive.MOBILE.getName()),
                        giver.USER_ID.as(giver.getName() + giver.USER_ID.getName()),
                        giver.USERNAME.as(giver.getName() + giver.USERNAME.getName()),
                        giver.MOBILE.as(giver.getName() + giver.MOBILE.getName()),
                        GIVE_GIFT_RECEIVE.PRODUCT_ID,
                        GIVE_GIFT_RECEIVE.MAIN_ORDER_SN,
                        GIVE_GIFT_RECEIVE.CREATE_TIME,
                        GIVE_GIFT_RECEIVE.ORDER_SN,
                        GIVE_GIFT_RECEIVE.STATUS,
                        GIVE_GIFT_RECEIVE.STATUS_NAME,
                        ORDER_INFO.ORDER_STATUS,
                        ORDER_INFO.ORDER_STATUS_NAME,
                        GIVE_GIFT_CART.GIFT_TYPE
                )
                .from(GIVE_GIFT_RECEIVE)
                .leftJoin(ORDER_INFO).on(ORDER_INFO.ORDER_SN.eq(GIVE_GIFT_RECEIVE.ORDER_SN))
                .leftJoin(GIVE_GIFT_CART).on(GIVE_GIFT_CART.ID.eq(GIVE_GIFT_RECEIVE.GIFT_CART_ID))
                .leftJoin(receive).on(receive.USER_ID.eq(GIVE_GIFT_RECEIVE.USER_ID))
                .leftJoin(giver).on(giver.USER_ID.eq(GIVE_GIFT_CART.USER_ID));
        select.where(GIVE_GIFT_CART.GIVE_GIFT_ID.eq(param.getActivityId()));
        buildSelect(select, param, receive, giver);

        return getPageResult(select, param.getCurrentPage(), param.getPageRows(), GiveGiftReceiveListVo.class);
    }

    private void buildSelect(SelectJoinStep<? extends Record> select, GiveGiftReceiveListParam param, User receive, User giver) {
        Timestamp nowTime = new Timestamp(System.currentTimeMillis());

        if (!StringUtils.isBlank(param.getGoodsSn())||!StringUtils.isBlank(param.getGoodsName())) {
            select.leftJoin(ORDER_GOODS).on(ORDER_GOODS.ORDER_SN.eq(ORDER_INFO.ORDER_SN));
            if (!StringUtils.isBlank(param.getGoodsName())) {
                select.where(ORDER_GOODS.GOODS_NAME.like(likeValue(param.getGoodsName())));
            }
            if (!StringUtils.isBlank(param.getGoodsSn())) {
                select.where(ORDER_GOODS.GOODS_SN.like(likeValue(param.getGoodsSn())));
            }
        }
        if (!StringUtils.isBlank(param.getReceiveMobile())) {
            select.where(receive.MOBILE.like(likeValue(param.getReceiveMobile())));
        }
        if (!StringUtils.isBlank(param.getReceiveUserName())) {
            select.where(receive.USERNAME.like(likeValue(param.getReceiveUserName())));
        }
        if (!StringUtils.isBlank(param.getGiveUserName())) {
            select.where(giver.USERNAME.like(param.getGiveUserName()));
        }
        if (!StringUtils.isBlank(param.getGiveMobile())) {
            select.where(giver.MOBILE.like(likeValue(param.getGiveMobile())));
        }
        if (!StringUtils.isBlank(param.getMainOrderSn())) {
            select.where(ORDER_INFO.MAIN_ORDER_SN.eq(param.getMainOrderSn()));
        }
        if (param.getReturnFinished() != null && param.getReturnFinished() > 0) {
            select.where(ORDER_INFO.ORDER_STATUS.eq(OrderConstant.ORDER_RETURN_FINISHED)
                    .or(ORDER_INFO.ORDER_STATUS.eq(OrderConstant.ORDER_REFUND_FINISHED)));
        }

        if (param.getOrderStatus() != null && param.getOrderStatus() > -1) {
            switch (param.getOrderStatus()) {
                case 0:
                    select.where(GIVE_GIFT_RECEIVE.STATUS.eq(GiveGiftConstant.GIFT_RECEIVE_UNSUBMITTED_ADDRESS))
                            .and(GIVE_GIFT_RECEIVE.CREATE_TIME.gt(nowTime));
                    break;
                case 4:
                    select.where(GIVE_GIFT_RECEIVE.STATUS.eq(GiveGiftConstant.GIFT_RECEIVE_HAVE_GIFT))
                            .and(ORDER_INFO.ORDER_STATUS.eq(OrderConstant.ORDER_WAIT_DELIVERY));
                    break;
                case 5:
                    select.where(GIVE_GIFT_RECEIVE.STATUS.eq(GiveGiftConstant.GIFT_RECEIVE_HAVE_GIFT))
                            .and(ORDER_INFO.ORDER_STATUS.eq(OrderConstant.ORDER_SHIPPED));
                    break;
                case 6:
                    select.where(GIVE_GIFT_RECEIVE.STATUS.eq(GiveGiftConstant.GIFT_RECEIVE_HAVE_GIFT))
                            .and(ORDER_INFO.ORDER_STATUS.eq(OrderConstant.ORDER_FINISHED));
                    break;
                case 7:
                    select.where(GIVE_GIFT_RECEIVE.STATUS.eq(GiveGiftConstant.GIFT_RECEIVE_UNSUBMITTED_ADDRESS))
                            .and(GIVE_GIFT_RECEIVE.CREATE_TIME.le(nowTime));
                    break;
                default:
                    select.where(GIVE_GIFT_RECEIVE.STATUS.eq(param.getOrderStatus()));
            }
        }

    }
}
