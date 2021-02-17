package com.meidianyi.shop.service.shop.message;

import com.fasterxml.jackson.core.type.TypeReference;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.common.pojo.shop.table.ImSessionDo;
import com.meidianyi.shop.common.pojo.shop.table.OrderInfoDo;
import com.meidianyi.shop.common.pojo.shop.table.UserAnnouncementDo;
import com.meidianyi.shop.common.pojo.shop.table.UserMessageDo;
import com.meidianyi.shop.dao.shop.user.UserDao;
import com.meidianyi.shop.dao.shop.doctor.DoctorDao;
import com.meidianyi.shop.dao.shop.message.MessageDao;
import com.meidianyi.shop.dao.shop.message.UserAnnouncementDao;
import com.meidianyi.shop.dao.shop.order.OrderGoodsDao;
import com.meidianyi.shop.dao.shop.order.OrderInfoDao;
import com.meidianyi.shop.dao.shop.prescription.PrescriptionDao;
import com.meidianyi.shop.dao.shop.session.ImSessionDao;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.decoration.PageStoreParam;
import com.meidianyi.shop.service.pojo.shop.message.*;
import com.meidianyi.shop.service.pojo.wxapp.medical.im.base.ImSessionItemBase;
import com.meidianyi.shop.service.pojo.wxapp.medical.im.param.ImSessionPullMsgParam;
import com.meidianyi.shop.service.pojo.wxapp.medical.im.param.ImSessionUnReadMessageInfoParam;
import com.meidianyi.shop.service.pojo.wxapp.medical.im.vo.ImSessionPullMsgVo;
import com.meidianyi.shop.service.pojo.wxapp.medical.im.vo.ImSessionUnReadInfoVo;
import com.meidianyi.shop.service.shop.decoration.AdminDecorationService;
import com.meidianyi.shop.service.shop.im.ImSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.meidianyi.shop.common.foundation.data.ImSessionConstant.*;
import static com.meidianyi.shop.service.pojo.shop.message.UserMessageConstant.*;


/**
 * @author 赵晓东
 * @description
 * @create 2020-07-23 15:46
 **/

@Service
public class UserMessageService extends ShopBaseService {

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private OrderGoodsDao orderGoodsDao;

    @Autowired
    private ImSessionService imSessionService;

    @Autowired
    private PrescriptionDao prescriptionDao;

    @Autowired
    private DoctorDao doctorDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private OrderInfoDao orderInfoDao;

    @Autowired
    private UserAnnouncementDao userAnnouncementDao;

    @Autowired
    private ImSessionDao imSessionDao;

    @Autowired
    private AdminDecorationService adminDecorationService;

    /**
     * 更改消息已读状态
     * @param messageParam 会话类型
     * @param userId 用户id
     * @return UserMessageCountVo
     */
    public UserMessageCountVo changeMessageStatus(MessageParam messageParam, Integer userId) {
        // 去除系统公告未读
        if (USER_MESSAGE_SYSTEM.equals(messageParam.getMessageType())) {
            userAnnouncementDao.updateUserAnnouncement(userId, messageParam.getMessageId());
        } else if (USER_MESSAGE_ORDER.equals(messageParam.getMessageType())) {
            // 去除订单消息未读
            messageDao.updateMessageStatus(userId, messageParam);
        } else if (USER_MESSAGE_CHAT.equals(messageParam.getMessageType())) {
            messageDao.updateImMessageStatus(userId, messageParam);
        }
        Integer announcementCount = messageDao.selectAnnouncementCount(userId);
        Integer orderCount = messageDao.selectOrderCount(userId);
        Integer chatCount = messageDao.selectChatCount(userId);
        UserMessageCountVo userMessageCountVo = new UserMessageCountVo();
        userMessageCountVo.setAnnouncementCount(announcementCount);
        userMessageCountVo.setChatCount(chatCount);
        userMessageCountVo.setOrderCount(orderCount);
        return userMessageCountVo;

    }

    /**
     * 医师首页展示未读消息计数
     * @param doctorId 医师id
     * @return DoctorMessageCountVo
     */
    public DoctorMessageCountVo countDoctorMessage(Integer doctorId, DoctorMainShowParam doctorMainShowParam) {
        // 将访问当前页面时间置入缓存中，如果存在上次缓存
        DoctorMessageCountVo doctorMessageCountVo = new DoctorMessageCountVo();
        // 根据缓存时间判断数据库中是否有未读新增数据
        // 根据时间判断是否有未读已续方消息
        String hospitalCode = doctorDao.selectDoctorCodeByDoctorId(doctorId);
        Boolean existAlreadyReadContinuedPrescription = prescriptionDao.isExistAlreadyReadContinuedPrescription(hospitalCode, doctorMainShowParam.getLastReadOrderGoodsTime());
        doctorMessageCountVo.setAlreadyPrescription(existAlreadyReadContinuedPrescription);
        // 判断是否有未读已开具消息
        Boolean existAlreadyReadPrescription = prescriptionDao.isExistAlreadyReadPrescription(hospitalCode, doctorMainShowParam.getLastReadPrescriptionTime());
        doctorMessageCountVo.setAlreadyOrderInfoCount(existAlreadyReadPrescription);
        // 判断是否有未读我的问诊消息
        ImSessionUnReadMessageInfoParam imSessionUnReadMessageInfoParam = new ImSessionUnReadMessageInfoParam();
        imSessionUnReadMessageInfoParam.setDoctorId(doctorId);
        List<ImSessionUnReadInfoVo> unReadMessageInfo = imSessionService.getUnReadMessageInfo(imSessionUnReadMessageInfoParam);
        Boolean existAlreadyReadImSession = !unReadMessageInfo.isEmpty();
        doctorMessageCountVo.setAlreadyImSessionCount(existAlreadyReadImSession);
        // 待问诊记录
        doctorMessageCountVo.setNotImSessionCount(messageDao.countDoctorImMessageNum(doctorId));
        // 待开方记录
        doctorMessageCountVo.setNotOrderInfoCount(messageDao.countDoctorOrderMessageNum());
        // 待续方记录
        Integer integer = orderGoodsDao.countAuditOrder();
        if (integer == null) {
            integer = 0;
        }
        doctorMessageCountVo.setNotOrderGoodsCount(integer);
        return doctorMessageCountVo;
    }

    /**
     * 根据消息id删除消息
     * @param messageId 消息id
     */
    public void deleteUserMessage(Integer messageId) {
        UserMessageDo userMessageDo = messageDao.selectMessageById(messageId);
        if (userMessageDo.getMessageType().equals(USER_MESSAGE_CHAT)) {
            ImSessionPullMsgParam imSessionPullMsgParam = selectImSessionByMessageId(messageId);
            ImSessionPullMsgVo imSessionPullMsgVo = imSessionService.pullMsg(imSessionPullMsgParam);
        }
        messageDao.deleteUserMessage(messageId);
    }

    /**
     * 根据messageId查询会话
     * @param messageId 消息入参
     * @return ImSessionPullMsgParam
     */
    private ImSessionPullMsgParam selectImSessionByMessageId(Integer messageId) {
        String orderSnByMessageId = messageDao.selectOrderSnByMessageId(messageId);
        ImSessionDo imSessionDo = messageDao.selectImByOrderSn(orderSnByMessageId);
        ImSessionPullMsgParam imSessionPullMsgParam = new ImSessionPullMsgParam();
        imSessionPullMsgParam.setPullFromId(imSessionDo.getDoctorId());
        imSessionPullMsgParam.setSelfId(imSessionDo.getUserId());
        imSessionPullMsgParam.setSessionId(imSessionDo.getId());
        return imSessionPullMsgParam;
    }



    /**
     * 根据用户id获取用户订单消息
     * @param userId 当前用户id
     * @return List<UserMessageVo>
     */
    public List<UserMessageVo> selectOrderUserMessage(Integer userId) {
        return messageDao.selectOrderUserMessage(userId);
    }

    /**
     * 根据用户id获取用户会话消息
     * @param userId 当前用户id
     * @return List<UserMessageVo>
     */
    public List<UserMessageVo> selectImSessionUserMessage(Integer userId) {
        return messageDao.selectImSessionUserMessage(userId);
    }

    /**
     * 向消息列表新增会话消息 每次打开消息列表时懒汉式更新数据库
     * @param imSessionUnReadMessageInfoParam 当前用户ID
     */
    public void setImSessionMessage(ImSessionUnReadMessageInfoParam imSessionUnReadMessageInfoParam) {
        List<ImSessionUnReadInfoVo> unReadMessageInfo = imSessionService.getUnReadMessageInfo(imSessionUnReadMessageInfoParam);
        // 如果当前用户有新会话
        if (!unReadMessageInfo.isEmpty()) {
            //查询当前用户有没有此医生的问诊记录
            for (ImSessionUnReadInfoVo imSessionUnReadInfoVo : unReadMessageInfo) {
                ImSessionDo sessionInfoById = imSessionService.getSessionInfoById(imSessionUnReadInfoVo.getSessionId());
                UserMessageVo imSessionBySessionId = messageDao.getMessageByOrderSn(sessionInfoById.getOrderSn());
                // 新增
                UserMessageParam userMessageParam = new UserMessageParam();
                ImSessionDo imSession = imSessionDao.getImSession(imSessionUnReadInfoVo.getSessionId());
                if (imSessionBySessionId == null) {
                    userMessageParam.setMessageType(USER_MESSAGE_CHAT);
                    String content = addImMessageContent(imSessionUnReadInfoVo);
                    userMessageParam.setMessageContent(content);
                    userMessageParam.setMessageRelevanceId(imSessionUnReadInfoVo.getSessionId());
                    userMessageParam.setSenderId(imSessionUnReadInfoVo.getDoctorId());
                    userMessageParam.setReceiverId(imSessionUnReadMessageInfoParam.getUserId());
                    userMessageParam.setReceiverName(userDao.getUserById(imSessionUnReadMessageInfoParam.getUserId()).getUsername());
                    userMessageParam.setSenderName(doctorDao.getOneInfo(imSessionUnReadInfoVo.getDoctorId()).getName());
                    userMessageParam.setMessageRelevanceOrderSn(imSession.getOrderSn());
                    userMessageParam.setMessageChatStatus(imSession.getSessionStatus());
                    messageDao.saveMessage(userMessageParam);
                } else { // 更新
                    userMessageParam.setMessageRelevanceOrderSn(imSession.getOrderSn());
                    userMessageParam.setMessageStatus(USER_MESSAGE_STATUS_NOT_READ);
                    String content = addImMessageContent(imSessionUnReadInfoVo);
                    userMessageParam.setMessageContent(content);
                    messageDao.updateImMessage(userMessageParam);
                }
            }
        }
    }

    /**
     * 修改会话消息内容格式
     * @param imSessionUnReadInfoVo 会话消息内容
     * @return String
     */
    private String addImMessageContent(ImSessionUnReadInfoVo imSessionUnReadInfoVo) {
        int size = imSessionUnReadInfoVo.getMessageInfos().size();
        ImSessionItemBase imSessionItemBase = imSessionUnReadInfoVo.getMessageInfos().get(size - 1);
        // 文本消息
        if (SESSION_ITEM_TYPE_TEXT.equals(imSessionItemBase.getType())) {
            return String.format(Objects.requireNonNull(UserMessageTemplate.USER_MESSAGE_IM_SESSION_ADD.getMessage()),
                imSessionUnReadInfoVo.getDoctorName(),
                Util.json2Object(imSessionItemBase.getMessage(), MessageContent.class, false).getContent());
        }
        // 图片消息
        if (SESSION_ITEM_TYPE_PICTURE.equals(imSessionItemBase.getType())) {
            return String.format(Objects.requireNonNull(UserMessageTemplate.USER_MESSAGE_IM_SESSION_PICTURE_ADD.getMessage()),
                imSessionUnReadInfoVo.getDoctorName());
        }
        // 处方消息
        if (SESSION_ITEM_TYPE_PRESCRIPTION.equals(imSessionItemBase.getType())) {
            return String.format(Objects.requireNonNull(UserMessageTemplate.USER_MESSAGE_IM_SESSION_PRESCRIPTION_ADD.getMessage()),
                imSessionUnReadInfoVo.getDoctorName());
        }
        return "";
    }

    /**
     * 向消息列表新增订单消息，打开消息列表时懒汉式更新数据库
     * @param userId 用户id
     */
    public void setOrderMessage(Integer userId) {
        List<OrderInfoDo> orderInfoDos = orderInfoDao.selectOrderInfoByUserId(userId);
        // 如果当前用户有订单
        if (!orderInfoDos.isEmpty()) {
            // 遍历该用户订单列表
            for (OrderInfoDo orderInfoDo : orderInfoDos) {
                UserMessageVo userMessageVo = messageDao.selectOrderMessageByOrderId(orderInfoDo.getOrderId(), userId);
                // 新增
                if (userMessageVo == null) {
                    UserMessageParam userMessageParam = new UserMessageParam();
                    String messageByOrderStatus = UserMessageTemplate.getMessageByOrderStatus(orderInfoDo.getOrderStatus());
                    assert messageByOrderStatus != null;
                    userMessageParam.setMessageContent(String.format(messageByOrderStatus, orderInfoDo.getOrderSn()));
                    userMessageParam.setReceiverId(userId);
                    userMessageParam.setMessageRelevanceId(orderInfoDo.getOrderId());
                    userMessageParam.setReceiverName(userDao.getUserById(userId).getUsername());
                    userMessageParam.setMessageType(USER_MESSAGE_ORDER);
                    userMessageParam.setMessageRelevanceId(orderInfoDo.getOrderId());
                    userMessageParam.setMessageRelevanceOrderSn(orderInfoDao.selectOrderSnByOrderId(orderInfoDo.getOrderId()));
                    messageDao.saveMessage(userMessageParam);
                } else { // 更改
                    UserMessageParam userMessageParam = new UserMessageParam();
                    userMessageParam.setMessageStatus(USER_MESSAGE_STATUS_NOT_READ);
                    String messageByOrderStatus = UserMessageTemplate.getMessageByOrderStatus(orderInfoDo.getOrderStatus());
                    assert messageByOrderStatus != null;
                    userMessageParam.setMessageContent(String.format(messageByOrderStatus, orderInfoDo.getOrderSn()));
                    userMessageParam.setMessageRelevanceOrderSn(orderInfoDao.selectOrderSnByOrderId(orderInfoDo.getOrderId()));
                    messageDao.updateMessage(userMessageParam);
                }
            }
        }
    }

    /**
     * 向用户推送系统公告
     * @param userId 用户id
     */
    public void setAnnouncementMessage(Integer userId) {
        // 获取该用户上次打开公告表时间
        Timestamp lastUserAnnouncement = userAnnouncementDao.getLastUserAnnouncement(userId);
        // 根据时间拉取未读公告
        if (lastUserAnnouncement == null) {
            lastUserAnnouncement = new Timestamp(0);
        }
        List<UserMessageVo> userMessageVos = messageDao.selectLastAnnouncement(lastUserAnnouncement);
        // 为该用户更新用户公告关联表
        for (UserMessageVo userMessageVo : userMessageVos) {
            UserAnnouncementDo userAnnouncementDo = new UserAnnouncementDo();
            userAnnouncementDo.setMessageId(userMessageVo.getMessageId());
            userAnnouncementDo.setUserId(userId);
            userAnnouncementDao.saveUserAnnouncement(userAnnouncementDo);
        }
    }

    /**
     * 用户消息页面获取用户消息列表和消息未读数
     * @param userId 用户id
     * @return UserMainMessageVo
     */
    public UserMainMessageVo selectUserMessage(Integer userId) {
        fetchUserMessage(userId);
        // 封装当前用户信息列表
        UserMainMessageVo userMainMessageVo = new UserMainMessageVo();
        List<UserMessageVo> userMessageVos = messageDao.selectMainMessage(userId);
        Integer announcementCount = messageDao.selectAnnouncementCount(userId);
        Integer orderCount = messageDao.selectOrderCount(userId);
        Integer chatCount = messageDao.selectChatCount(userId);
        userMainMessageVo.setUserMessages(userMessageVos);
        userMainMessageVo.setAnnouncementMessageCount(announcementCount);
        userMainMessageVo.setOrderMessageCount(orderCount);
        userMainMessageVo.setImSessionMessageCount(chatCount);
        return userMainMessageVo;
    }

    /**
     * 查询个人中心用户消息统计数量
     * @param userId 用户id
     * @return Integer
     */
    public Integer selectUserMessageCount(Integer userId) {
        fetchUserMessage(userId);
        Integer announcementCount = messageDao.selectAnnouncementCount(userId);
        Integer orderCount = messageDao.selectOrderCount(userId);
        Integer chatCount = messageDao.selectChatCount(userId);
        return announcementCount + orderCount + chatCount;
    }

    /**
     * 更新用户消息
     * @param userId 用户id
     */
    public void fetchUserMessage(Integer userId) {
            ImSessionUnReadMessageInfoParam imSessionUnReadMessageInfoParam = new ImSessionUnReadMessageInfoParam();
            imSessionUnReadMessageInfoParam.setUserId(userId);
            setImSessionMessage(imSessionUnReadMessageInfoParam);
            setOrderMessage(userId);
            setAnnouncementMessage(userId);
    }

    /**
     * 新增系统公告
     * @param pageStoreParam 系统公告入参
     */
    public void addAnnouncement(PageStoreParam pageStoreParam) {
        String pageContent = pageStoreParam.getPageContent();
        List<AnnounceBo> list = this.processPageContentBeforeSave(pageContent);
        if (list.isEmpty()) {
            return;
        }
        messageDao.addAnnouncementMessage(list);
    }

    /**
     * 处理装修的JSON，提出门店公告信息
     * @param pageContent 装修json
     * @return String
     */
    private List<AnnounceBo> processPageContentBeforeSave(String pageContent) {
        return toAnnouncementBo(pageContent);
    }

    /**
     * 记录转实体类
     * @param pageContent
     * @return
     */
    private List<AnnounceBo> toAnnouncementBo(String pageContent) {
        List<AnnounceBo> list = new ArrayList<>();
        Map<String, AnnounceBo> announceBo = Util.json2Object(pageContent, new TypeReference<Map<String, AnnounceBo>>() {}, false);
        assert announceBo != null;
        announceBo.forEach( (k, v) ->{
            if (v.getShopText() != null) {
                list.add(v);
            }
        });
        return list;
    }

}
