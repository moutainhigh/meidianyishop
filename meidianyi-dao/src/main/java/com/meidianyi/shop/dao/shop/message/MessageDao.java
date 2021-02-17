package com.meidianyi.shop.dao.shop.message;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.pojo.shop.table.ImSessionDo;
import com.meidianyi.shop.common.pojo.shop.table.UserMessageDo;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.UserMessageRecord;
import com.meidianyi.shop.service.pojo.shop.message.AnnounceBo;
import com.meidianyi.shop.service.pojo.shop.message.MessageParam;
import com.meidianyi.shop.service.pojo.shop.message.UserMessageParam;
import com.meidianyi.shop.service.pojo.shop.message.UserMessageVo;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.InquiryOrderConstant;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

import static com.meidianyi.shop.db.shop.Tables.*;
import static com.meidianyi.shop.service.pojo.shop.message.UserMessageConstant.*;
import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.*;

/**
 * @author 赵晓东
 * @description 用户消息dao层
 * @create 2020-07-23 15:35
 **/

@Repository
public class MessageDao extends ShopBaseDao {

    private static final String USER_MESSAGE_SYSTEM_ANNOUNCEMENT = "系统公告";

    private static final Byte IS_PULL_YES = 1;

    private static final Byte IS_PULL_NO = 0;

    /**
     * 新增系统消息
     * @param list 新增公告列表
     */
    public void addAnnouncementMessage(List<AnnounceBo> list){
        list.forEach(e -> {
            if (IS_PULL_YES.equals(e.getIsPull())) {
                db().insertInto(USER_MESSAGE).set(USER_MESSAGE.MESSAGE_NAME, USER_MESSAGE_SYSTEM_ANNOUNCEMENT)
                    .set(USER_MESSAGE.MESSAGE_CONTENT, e.getShopText())
                    .set(USER_MESSAGE.MESSAGE_TYPE, USER_MESSAGE_SYSTEM)
                    .execute();
            }
        });
    }

    /**
     * 医师端显示待问诊数量
     * @param doctorId 医师id
     * @return Integer
     */
    public Integer countDoctorImMessageNum(Integer doctorId){
        return db().selectCount().from(INQUIRY_ORDER)
            .where(INQUIRY_ORDER.ORDER_STATUS.eq(InquiryOrderConstant.ORDER_TO_RECEIVE)
            .and(INQUIRY_ORDER.DOCTOR_ID.eq(doctorId))
            .and(INQUIRY_ORDER.IS_DELETE.eq(DelFlag.NORMAL_VALUE)))
            .fetchOneInto(Integer.class);
    }

    /**
     * 医师端显示待开方数量
     * @return Integer
     */
    public Integer countDoctorOrderMessageNum(){
        return db().selectCount().from(ORDER_INFO)
            .where(ORDER_INFO.ORDER_STATUS.eq(ORDER_TO_AUDIT_OPEN))
            .and(ORDER_INFO.ORDER_AUDIT_TYPE.eq(MEDICAL_ORDER_AUDIT_TYPE_CREATE))
            .and(ORDER_INFO.ORDER_AUDIT_STATUS.eq(MEDICAL_AUDIT_DEFAULT))
            .and(ORDER_INFO.ORDER_MEDICAL_TYPE.eq(MEDICAL_TYPE_RX))
            .and(ORDER_INFO.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
            .fetchOneInto(Integer.class);
    }

    /**
     * 用户删除列表消息
     * @param messageId 消息id
     */
    public void deleteUserMessage(Integer messageId){
        db().update(USER_MESSAGE)
            .set(USER_MESSAGE.IS_DELETE, DelFlag.DISABLE_VALUE)
            .where(USER_MESSAGE.MESSAGE_ID.eq(messageId)).execute();
    }

    /**
     * 查询订单消息
     * @param userId 接收消息用户id
     * @return List<UserMessageVo>
     */
    public List<UserMessageVo> selectOrderUserMessage(Integer userId){
        return db().select().from(USER_MESSAGE)
            .where(USER_MESSAGE.MESSAGE_TYPE.eq(USER_MESSAGE_ORDER))
            .and(USER_MESSAGE.RECEIVER_ID.eq(userId))
            .and(USER_MESSAGE.IS_DELETE.eq(DelFlag.NORMAL_VALUE)).fetchInto(UserMessageVo.class);
    }

    /**
     * 查询会话消息
     * @param userId 接收消息用户ID
     * @return List<UserMessageVo>
     */
    public List<UserMessageVo> selectImSessionUserMessage(Integer userId){
        return db().select().from(USER_MESSAGE)
            .where(USER_MESSAGE.MESSAGE_TYPE.eq(USER_MESSAGE_CHAT))
            .and(USER_MESSAGE.RECEIVER_ID.eq(userId))
            .and(USER_MESSAGE.IS_DELETE.eq(DelFlag.NORMAL_VALUE)).fetchInto(UserMessageVo.class);
    }

    /**
     * 查询用户是否有历史会话记录
     * @param sessionId 会话id
     * @param userId 当前用户id
     * @return UserMessageVo
     */
    public UserMessageVo getImSessionBySessionId(Integer sessionId, Integer userId) {
        return db().select().from(USER_MESSAGE)
            .where(USER_MESSAGE.MESSAGE_RELEVANCE_ID.eq(sessionId))
            .and(USER_MESSAGE.MESSAGE_TYPE.eq(USER_MESSAGE_CHAT))
            .and(USER_MESSAGE.RECEIVER_ID.eq(userId))
            .and(USER_MESSAGE.IS_DELETE.eq(DelFlag.NORMAL_VALUE)).fetchAnyInto(UserMessageVo.class);
    }

    /**
     * 根据orderSn查询消息
     * @param orderSn 订单号
     * @return 消息信息
     */
    public UserMessageVo getMessageByOrderSn(String orderSn) {
        return db().select().from(USER_MESSAGE)
            .where(USER_MESSAGE.MESSAGE_RELEVANCE_ORDER_SN.eq(orderSn))
            .fetchAnyInto(UserMessageVo.class);
    }

    /**
     * 储存会话消息
     * @param userMessageParam 消息入参
     */
    public void saveMessage(UserMessageParam userMessageParam) {
        UserMessageRecord messageRecord = db().newRecord(USER_MESSAGE);
        FieldsUtil.assign(userMessageParam, messageRecord);
        messageRecord.insert();
    }

    /**
     * 更新订单消息
     * @param userMessageParam 消息入参
     */
    public void updateMessage(UserMessageParam userMessageParam) {
        UserMessageDo userMessageDo = db().select().from(USER_MESSAGE)
            .where(USER_MESSAGE.MESSAGE_RELEVANCE_ORDER_SN.eq(userMessageParam.getMessageRelevanceOrderSn()))
            .fetchAnyInto(UserMessageDo.class);
        if (!userMessageDo.getMessageContent().equals(userMessageParam.getMessageContent())) {
            db().update(USER_MESSAGE)
                .set(USER_MESSAGE.MESSAGE_CONTENT, userMessageParam.getMessageContent())
                .set(USER_MESSAGE.MESSAGE_STATUS, USER_MESSAGE_STATUS_NOT_READ)
                .set(USER_MESSAGE.IS_DELETE, DelFlag.NORMAL_VALUE)
                .where(USER_MESSAGE.MESSAGE_RELEVANCE_ORDER_SN.eq(userMessageParam.getMessageRelevanceOrderSn()))
                .execute();
        }
    }

    /**
     * 更新会话消息
     * @param userMessageParam 消息入参
     */
    public void updateImMessage(UserMessageParam userMessageParam) {
        db().update(USER_MESSAGE)
            .set(USER_MESSAGE.MESSAGE_CONTENT, userMessageParam.getMessageContent())
            .set(USER_MESSAGE.MESSAGE_STATUS, userMessageParam.getMessageStatus())
            .set(USER_MESSAGE.IS_DELETE, DelFlag.NORMAL_VALUE)
            .where(USER_MESSAGE.MESSAGE_RELEVANCE_ORDER_SN.eq(userMessageParam.getMessageRelevanceOrderSn())).execute();
    }

    /**
     * 查询用户订单消息
     * @param orderId
     * @return
     */
    public UserMessageVo selectOrderMessageByOrderId(Integer orderId, Integer userId) {
        return db().select().from(USER_MESSAGE)
            .where(USER_MESSAGE.MESSAGE_RELEVANCE_ID.eq(orderId))
            .and(USER_MESSAGE.MESSAGE_TYPE.eq(USER_MESSAGE_ORDER))
            .and(USER_MESSAGE.RECEIVER_ID.eq(userId))
            .fetchAnyInto(UserMessageVo.class);
    }

    /**
     * 增量获取未读的公告消息
     * @param timestamp 上次打开公告消息时间戳
     * @return List<UserMessageVo>
     */
    public List<UserMessageVo> selectLastAnnouncement(Timestamp timestamp){
        return db().select().from(USER_MESSAGE)
            .where(USER_MESSAGE.MESSAGE_TIME.gt(timestamp))
            .and(USER_MESSAGE.MESSAGE_TYPE.eq(USER_MESSAGE_SYSTEM))
            .and(USER_MESSAGE.IS_DELETE.eq(DelFlag.NORMAL_VALUE)).fetchInto(UserMessageVo.class);
    }

    /**
     * 拉取该用户所有信息
     * @param userId 用户id
     * @return List<UserMessageVo>
     */
    public List<UserMessageVo> selectMainMessage(Integer userId) {
        return db().select().from(USER_MESSAGE)
            .where(USER_MESSAGE.RECEIVER_ID.eq(userId))
            .or(USER_MESSAGE.RECEIVER_ID.eq(USER_MESSAGE_RECEIVE_ANN))
            .and(USER_MESSAGE.IS_DELETE.eq(DelFlag.NORMAL_VALUE))
            .orderBy(USER_MESSAGE.CREATE_TIME.desc()).fetchInto(UserMessageVo.class);
    }

    /**
     * 查询用户未读公告数
     * @param userId 用户id
     * @return Integer
     */
    public Integer selectAnnouncementCount(Integer userId) {
        return db().selectCount().from(USER_ANNOUNCEMENT)
            .join(USER_MESSAGE).on(USER_ANNOUNCEMENT.MESSAGE_ID.eq(USER_MESSAGE.MESSAGE_ID))
            .and(USER_ANNOUNCEMENT.USER_ID.eq(userId))
            .and(USER_ANNOUNCEMENT.MESSAGE_STATUS.eq(USER_MESSAGE_STATUS_NOT_READ))
            .where(USER_MESSAGE.IS_DELETE.eq(DelFlag.NORMAL_VALUE)).fetchAnyInto(Integer.class);
    }

    /**
     * 查询用户未读订单消息数
     * @param userId 用户id
     * @return Integer
     */
    public Integer selectOrderCount(Integer userId) {
        return db().selectCount().from(USER_MESSAGE)
            .where(USER_MESSAGE.RECEIVER_ID.eq(userId))
            .and(USER_MESSAGE.MESSAGE_TYPE.eq(USER_MESSAGE_ORDER))
            .and(USER_MESSAGE.MESSAGE_STATUS.eq(USER_MESSAGE_STATUS_NOT_READ))
            .and(USER_MESSAGE.IS_DELETE.eq(DelFlag.NORMAL_VALUE)).fetchAnyInto(Integer.class);
    }

    /**
     * 查询未读会话消息数
     * @param userId 用户id
     * @return Integer
     */
    public Integer selectChatCount(Integer userId) {
        return db().selectCount().from(USER_MESSAGE)
            .where(USER_MESSAGE.RECEIVER_ID.eq(userId))
            .and(USER_MESSAGE.MESSAGE_TYPE.eq(USER_MESSAGE_CHAT))
            .and(USER_MESSAGE.MESSAGE_STATUS.eq(USER_MESSAGE_STATUS_NOT_READ))
            .and(USER_MESSAGE.IS_DELETE.eq(DelFlag.NORMAL_VALUE)).fetchAnyInto(Integer.class);
    }

    /**
     * 更新消息表消息状态
     * @param userId 用户id
     * @param messageParam 消息类型，消息id
     */
    public void updateMessageStatus(Integer userId, MessageParam messageParam) {
        if (messageParam.getMessageId() == 0) {
            db().update(USER_MESSAGE)
                .set(USER_MESSAGE.MESSAGE_STATUS, USER_MESSAGE_STATUS_ALREADY_READ)
                .where(USER_MESSAGE.MESSAGE_TYPE.eq(messageParam.getMessageType()))
                .and(USER_MESSAGE.RECEIVER_ID.eq(userId)).execute();
        } else {
            db().update(USER_MESSAGE)
                .set(USER_MESSAGE.MESSAGE_STATUS, USER_MESSAGE_STATUS_ALREADY_READ)
                .where(USER_MESSAGE.MESSAGE_TYPE.eq(messageParam.getMessageType()))
                .and(USER_MESSAGE.RECEIVER_ID.eq(userId))
                .and(USER_MESSAGE.MESSAGE_ID.eq(messageParam.getMessageId())).execute();
        }
    }

    /**
     * 更新会话信息状态
     * @param userId 用户id
     * @param messageParam 消息入参
     */
    public void updateImMessageStatus(Integer userId, MessageParam messageParam) {
        db().update(USER_MESSAGE)
            .set(USER_MESSAGE.MESSAGE_STATUS, USER_MESSAGE_STATUS_ALREADY_READ)
            .where(USER_MESSAGE.MESSAGE_TYPE.eq(messageParam.getMessageType()))
            .and(USER_MESSAGE.RECEIVER_ID.eq(userId))
            .and(USER_MESSAGE.MESSAGE_ID.eq(messageParam.getMessageId())).execute();
    }

    /**
     * 根据消息id查询会话orderSN
     * @param messageId 消息id
     * @return String
     */
    public String selectOrderSnByMessageId(Integer messageId) {
        return db().select(USER_MESSAGE.MESSAGE_RELEVANCE_ORDER_SN).from(USER_MESSAGE)
            .where(USER_MESSAGE.MESSAGE_ID.eq(messageId))
            .fetchAnyInto(String.class);
    }

    /**
     * 根据orderSn查询会话
     * @param orderSn 订单Sn
     * @return ImSessionDo
     */
    public ImSessionDo selectImByOrderSn(String orderSn) {
        return db().select().from(IM_SESSION)
            .where(IM_SESSION.ORDER_SN.eq(orderSn))
            .fetchAnyInto(ImSessionDo.class);
    }

    /**
     * 根据消息id查消息详情
     * @param messageId 消息id
     * @return UserMessageDo
     */
    public UserMessageDo selectMessageById(Integer messageId) {
        return db().select().from(USER_MESSAGE)
            .where(USER_MESSAGE.MESSAGE_ID.eq(messageId))
            .fetchAnyInto(UserMessageDo.class);
    }


}
