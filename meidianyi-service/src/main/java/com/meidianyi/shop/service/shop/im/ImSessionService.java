package com.meidianyi.shop.service.shop.im;

import com.meidianyi.shop.common.foundation.data.ImSessionConstant;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.common.pojo.shop.table.ImSessionDo;
import com.meidianyi.shop.common.pojo.shop.table.ImSessionItemDo;
import com.meidianyi.shop.dao.shop.session.ImSessionDao;
import com.meidianyi.shop.dao.shop.session.ImSessionItemDao;
import com.meidianyi.shop.service.foundation.jedis.JedisKeyConstant;
import com.meidianyi.shop.service.foundation.jedis.JedisManager;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorSimpleVo;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorSortParam;
import com.meidianyi.shop.service.pojo.shop.patient.PatientSimpleInfoVo;
import com.meidianyi.shop.service.pojo.wxapp.medical.im.base.ImSessionItemBase;
import com.meidianyi.shop.service.pojo.wxapp.medical.im.bo.ImSessionItemBo;
import com.meidianyi.shop.service.pojo.wxapp.medical.im.condition.ImSessionCondition;
import com.meidianyi.shop.service.pojo.wxapp.medical.im.param.ImSessionNewParam;
import com.meidianyi.shop.service.pojo.wxapp.medical.im.param.ImSessionPageListParam;
import com.meidianyi.shop.service.pojo.wxapp.medical.im.param.ImSessionPullMsgParam;
import com.meidianyi.shop.service.pojo.wxapp.medical.im.param.ImSessionQueryPageListParam;
import com.meidianyi.shop.service.pojo.wxapp.medical.im.param.ImSessionRenderPageParam;
import com.meidianyi.shop.service.pojo.wxapp.medical.im.param.ImSessionSendMsgParam;
import com.meidianyi.shop.service.pojo.wxapp.medical.im.param.ImSessionUnReadMessageInfoParam;
import com.meidianyi.shop.service.pojo.wxapp.medical.im.vo.ImSessionItemRenderVo;
import com.meidianyi.shop.service.pojo.wxapp.medical.im.vo.ImSessionListVo;
import com.meidianyi.shop.service.pojo.wxapp.medical.im.vo.ImSessionPullMsgVo;
import com.meidianyi.shop.service.pojo.wxapp.medical.im.vo.ImSessionUnReadInfoVo;
import com.meidianyi.shop.service.shop.department.DepartmentService;
import com.meidianyi.shop.service.shop.doctor.DoctorCommentService;
import com.meidianyi.shop.service.shop.doctor.DoctorService;
import com.meidianyi.shop.service.shop.patient.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 会话处理service
 * @author 李晓冰
 * @date 2020年07月21日
 */
@Service
public class ImSessionService extends ShopBaseService {
    @Autowired
    DepartmentService departmentService;
    @Autowired
    PatientService patientService;
    @Autowired
    DoctorService doctorService;
    @Autowired
    DoctorCommentService doctorCommentService;

    @Autowired
    private ImSessionDao imSessionDao;
    @Autowired
    private ImSessionItemDao imSessionItemDao;
    @Autowired
    private JedisManager jedisManager;

    /**
     * 分页查询会话信息
     * @param param 分页条件
     * @return 分页结果
     */
    public PageResult<ImSessionListVo> pageList(ImSessionPageListParam param) {
        PageResult<ImSessionListVo> pageResult = imSessionDao.pageList(param);
        List<ImSessionListVo> dataList = pageResult.getDataList();
        List<Integer> doctorIds = new ArrayList<>(dataList.size() / 2);
        List<Integer> patientIds = new ArrayList<>(dataList.size());

        Integer shopId = getShopId();
        Map<Integer, String> imSessionRedisKeyMap = new HashMap<>(dataList.size());
        for (ImSessionListVo imSession : dataList) {
            patientIds.add(imSession.getPatientId());
            doctorIds.add(imSession.getDoctorId());
            // 准备处理未读消息，医师端展示对应会话信息
            if (param.getDoctorId() != null) {
                imSessionRedisKeyMap.put(imSession.getId(), getSessionRedisKey(shopId, imSession.getId(), imSession.getUserId(), imSession.getDoctorId()));
            } else {
                imSessionRedisKeyMap.put(imSession.getId(), getSessionRedisKey(shopId, imSession.getId(), imSession.getDoctorId(), imSession.getUserId()));
            }
        }

        Map<Integer, String> patientIdMap = patientService.listPatientInfo(patientIds).stream().collect(Collectors.toMap(PatientSimpleInfoVo::getId, PatientSimpleInfoVo::getName, (x1, x2) -> x2));
        Map<Integer, String> doctorIdMap = doctorService.listDoctorSimpleInfo(doctorIds).stream().collect(Collectors.toMap(DoctorSimpleVo::getId, DoctorSimpleVo::getName, (x1, x2) -> x2));

        for (ImSessionListVo imSession : dataList) {
            imSession.setPatientName(patientIdMap.get(imSession.getPatientId()));
            imSession.setDoctorName(doctorIdMap.get(imSession.getDoctorId()));
            String sessionKey = imSessionRedisKeyMap.get(imSession.getId());
            Long listSize = jedisManager.getListSize(sessionKey);
            if (listSize != null) {
                imSession.setNewMsgNum(listSize);
            } else {
                imSession.setNewMsgNum(0L);
            }
        }

        return pageResult;
    }

    /**
     * 恢复已有的聊天记录,目前没有做成分页的
     * @param renderPageParam 会话内容请求参数
     * @return 会话聊天内容信息
     */
    public List<ImSessionItemRenderVo> renderSession(ImSessionRenderPageParam renderPageParam) {
        Integer sessionId = renderPageParam.getSessionId();
        ImSessionDo imSessionDo = imSessionDao.getById(sessionId);
        Integer doctorId = imSessionDo.getDoctorId();

        List<ImSessionItemDo> imSessionItemDos = null;
        // 从redis中获取数据
        if (!ImSessionConstant.SESSION_DEAD.equals(imSessionDo.getSessionStatus()) && !ImSessionConstant.SESSION_CANCEL.equals(imSessionDo.getSessionStatus())) {
            imSessionItemDos = renderSessionFromRedis(renderPageParam, imSessionDo);
        } else {
            // 从mysql中获取数据
            imSessionItemDos = renderSessionFromDb(renderPageParam);
        }

        return imSessionItemDos.stream().map(item -> {
            ImSessionItemRenderVo itemVo = new ImSessionItemRenderVo();
            itemVo.setDoctor(doctorId.equals(item.getFromId()));
            itemVo.setMessage(item.getMessage());
            itemVo.setType(item.getType());
            itemVo.setSendTime(item.getSendTime());
            return itemVo;
        }).collect(Collectors.toList());
    }

    /**
     * 从 redis中查询分页内容
     * @param renderPageParam
     * @return
     */
    private List<ImSessionItemDo> renderSessionFromRedis(ImSessionRenderPageParam renderPageParam, ImSessionDo imSessionDo) {
        String sessionBakKey = getSessionRedisKeyBak(getShopId(), renderPageParam.getSessionId());
        String sessionMsgKey = null;
        if (renderPageParam.getIsDoctor()) {
            sessionMsgKey = getSessionRedisKey(getShopId(), imSessionDo.getId(), imSessionDo.getDoctorId(), imSessionDo.getUserId());
        } else {
            sessionMsgKey = getSessionRedisKey(getShopId(), imSessionDo.getId(), imSessionDo.getUserId(), imSessionDo.getDoctorId());
        }
        Integer unreadMsgLength = jedisManager.getListSize(sessionMsgKey).intValue();
        if (Boolean.FALSE.equals(renderPageParam.getIsFirstTime())) {
            renderPageParam.setStartLineIndex(renderPageParam.getStartLineIndex() - unreadMsgLength);
        }

        // redis 超过长度
        int totalRows = jedisManager.getListSize(sessionBakKey).intValue();
        List<ImSessionItemDo> imSessionItemDos = null;
        // 分页的开始下标已经超过redis中存的会话条目，则从数据库中查找对应的历史信息
        if (renderPageParam.getStartLineIndex() >= totalRows) {
            renderPageParam.setStartLineIndex(renderPageParam.getStartLineIndex() - totalRows);
            imSessionItemDos = renderSessionFromDb(renderPageParam);
        } else {
            int endIndex = totalRows - renderPageParam.getStartLineIndex() - 1;
            int startIndex = endIndex > renderPageParam.getPageRows() ? endIndex - renderPageParam.getPageRows() + 1 : 0;
            List<String> jsonStrs = jedisManager.lrange(sessionBakKey, startIndex, endIndex);
            imSessionItemDos = jsonStrs.stream().map(x -> Util.parseJson(x, ImSessionItemDo.class)).filter(Objects::nonNull).collect(Collectors.toList());
        }
        // 如果是从第一次打开会话内容，需要查询是否有自己已发送，但是对方未读取的消息
        if (renderPageParam.getIsFirstTime()) {
            List<String> list = jedisManager.getList(sessionMsgKey);
            for (String jsonStr : list) {
                ImSessionItemDo imSessionItemDo = Util.parseJson(jsonStr, ImSessionItemDo.class);
                if (imSessionItemDo == null) {
                    continue;
                }
                imSessionItemDo.setImSessionId(imSessionDo.getId());
                if (renderPageParam.getIsDoctor()) {
                    imSessionItemDo.setFromId(imSessionDo.getDoctorId());
                    imSessionItemDo.setToId(imSessionDo.getUserId());
                } else {
                    imSessionItemDo.setFromId(imSessionDo.getUserId());
                    imSessionItemDo.setToId(imSessionDo.getDoctorId());
                }
                imSessionItemDos.add(imSessionItemDo);
            }
        }
        return imSessionItemDos;
    }

    /**
     * 从db中查询分页内容
     * @param renderPageParam
     * @return
     */
    private List<ImSessionItemDo> renderSessionFromDb(ImSessionRenderPageParam renderPageParam) {
        List<Integer> sessionIds = imSessionDao.getRelevantSessionIds(renderPageParam.getSessionId());
        List<ImSessionItemDo> retList = imSessionItemDao.getRelevantSessionItemPageList(renderPageParam, sessionIds);
        Collections.reverse(retList);
        return retList;
    }

    /**
     * 查询用户或医师未读消息
     * @return 未读消息 没有未读消息则返回空集合
     */
    public List<ImSessionUnReadInfoVo> getUnReadMessageInfo(ImSessionUnReadMessageInfoParam param) {
        ImSessionCondition imSessionCondition = new ImSessionCondition();
        imSessionCondition.setDoctorId(param.getDoctorId());
        imSessionCondition.setUserId(param.getUserId());
        List<Byte> statusList = new ArrayList<>();
        statusList.add(ImSessionConstant.SESSION_ON);
        statusList.add(ImSessionConstant.SESSION_CONTINUE_ON);
        imSessionCondition.setStatusList(statusList);
        if (param.getDoctorId() != null) {
            imSessionCondition.getStatusList().add(ImSessionConstant.SESSION_READY_TO_START);
            imSessionCondition.getStatusList().add(ImSessionConstant.SESSION_END);
        }
        List<ImSessionDo> imSessionDos = imSessionDao.listImSession(imSessionCondition);
        if (imSessionDos == null) {
            return new ArrayList<>(0);
        }
        List<ImSessionUnReadInfoVo> retList = new ArrayList<>(imSessionDos.size());
        String redisKey = null;
        Integer shopId = getShopId();
        List<Integer> doctorIds = new ArrayList<>(imSessionDos.size());
        for (ImSessionDo imSessionDo : imSessionDos) {
            if (param.getDoctorId() == null) {
                // 用户查询自己未读信息
                redisKey = getSessionRedisKey(shopId, imSessionDo.getId(), imSessionDo.getDoctorId(), imSessionDo.getUserId());
            } else {
                // 医师查询自己未读信息
                redisKey = getSessionRedisKey(shopId, imSessionDo.getId(), imSessionDo.getUserId(), imSessionDo.getDoctorId());
            }
            if (!jedisManager.exists(redisKey)) {
                continue;
            }
            ImSessionUnReadInfoVo vo = new ImSessionUnReadInfoVo();
            doctorIds.add(imSessionDo.getDoctorId());
            vo.setSessionId(imSessionDo.getId());
            vo.setDoctorId(imSessionDo.getDoctorId());
            List<String> jsonStrs = jedisManager.getList(redisKey);
            List<ImSessionItemBase> messageList = new ArrayList<>(jsonStrs.size());
            for (String jsonStr : jsonStrs) {
                ImSessionItemBase imSessionItemBase = Util.parseJson(jsonStr, ImSessionItemBase.class);
                messageList.add(imSessionItemBase);
            }
            vo.setMessageInfos(messageList);
            retList.add(vo);
        }

        // 处理医师名称和用户名称
        Map<Integer, String> doctorIdMap = doctorService.listDoctorSimpleInfo(doctorIds).stream().collect(Collectors.toMap(DoctorSimpleVo::getId, DoctorSimpleVo::getName, (x1, x2) -> x2));
        for (ImSessionUnReadInfoVo imSessionUnReadInfoVo : retList) {
            imSessionUnReadInfoVo.setDoctorName(doctorIdMap.get(imSessionUnReadInfoVo.getDoctorId()));
        }
        return retList;
    }

    /**
     * 查询会话记录
     * @param sessionId 会话id
     * @return 会话记录
     */
    public ImSessionDo getSessionInfoById(Integer sessionId) {
        return imSessionDao.getById(sessionId);
    }

    /**
     * 查询会话状态
     * @param sessionId 会话id
     * @return 会话状态
     */
    public Byte getSessionStatus(Integer sessionId) {
        String statusKey = getSessionRedisStatusKey(getShopId(), sessionId);
        String s = jedisManager.get(statusKey);
        if (s == null) {
          return imSessionDao.getStatus(sessionId);
        } else {
            return Byte.valueOf(s);
        }
    }

    /**
     * 修改session在redis中保存的会话状态
     * @param sessionId 会话id
     * @param status    状态
     */
    private void updateSessionRedisStatusValue(Integer sessionId, Byte status) {
        String sessionRedisStatusKey = getSessionRedisStatusKey(getShopId(), sessionId);
        jedisManager.set(sessionRedisStatusKey, status.toString());
    }

    /**
     * 查询会话状态
     * @param orderSn
     * @return
     */
    public ImSessionDo getSessionInfoByOrderSn(String orderSn) {
        return imSessionDao.getByOrderSn(orderSn);
    }

    private Timestamp calculateSessionLimitTime() {
        return DateUtils.getTimeStampPlus(ImSessionConstant.CLOSE_LIMIT_TIME, ChronoUnit.HOURS);
    }

    /**
     * 新增待接诊会话
     * @param param 新增会话信息
     * @return 会话id
     */
    public Integer insertNewSession(ImSessionNewParam param) {
        ImSessionDo imSessionDo = new ImSessionDo();
        imSessionDo.setDoctorId(param.getDoctorId());
        imSessionDo.setUserId(param.getUserId());
        imSessionDo.setPatientId(param.getPatientId());
        imSessionDo.setOrderSn(param.getOrderSn());
        imSessionDo.setSessionStatus(ImSessionConstant.SESSION_READY_TO_START);
        imSessionDo.setWeightFactor(ImSessionConstant.SESSION_READY_TO_START_WEIGHT);
        // 可从结束状态转变为继续问诊次数
        imSessionDo.setContinueSessionCount(ImSessionConstant.CONTINUE_SESSION_TIME);
        ImSessionDo readyToDead = imSessionDao.getByAllInfo(param.getDoctorId(), param.getUserId(), param.getPatientId());
        if (readyToDead != null) {
            deadImSession(readyToDead);
        }
        imSessionDao.insert(imSessionDo);
        String sessionRedisStatusKey = getSessionRedisStatusKey(getShopId(), imSessionDo.getId());
        jedisManager.set(sessionRedisStatusKey, ImSessionConstant.SESSION_READY_TO_START.toString());
        return imSessionDo.getId();
    }

    /**
     * 会话评价状态由可评价修改为已评价
     * @param sessionId
     */
    public void updateSessionEvaluateStatusToAlready(Integer sessionId) {
        imSessionDao.batchUpdateSessionEvaluateStatus(Collections.singletonList(sessionId), ImSessionConstant.SESSION_EVALUATE_ALREADY_STATUS, ImSessionConstant.SESSION_EVALUATE_CAN_STATUS);
    }

    /**
     * 会话状态修改为进行中
     * @param sessionId 会话ID
     * @return
     */
    public void updateSessionToGoingOn(Integer sessionId) {
        ImSessionDo imSessionDo = imSessionDao.getById(sessionId);
        if (ImSessionConstant.SESSION_ON.equals(imSessionDo.getSessionStatus())) {
            return;
        }
        Byte prevStatus = imSessionDo.getSessionStatus();
        imSessionDo.setLimitTime(calculateSessionLimitTime());

        if (ImSessionConstant.SESSION_READY_TO_START.equals(prevStatus)) {
            // 状态从1->2
            updateSessionRedisStatusValue(sessionId, ImSessionConstant.SESSION_ON);
            imSessionDo.calculateReadyToOnAckTime();
            imSessionDo.setSessionStatus(ImSessionConstant.SESSION_ON);
            imSessionDo.setWeightFactor(ImSessionConstant.SESSION_ON_WEIGHT);
            imSessionDo.setReceiveStartTime(DateUtils.getLocalDateTime());
            imSessionDao.update(imSessionDo);
            statisticDoctorSessionState(imSessionDo.getDoctorId());
        } else {
            // 从结束状态变为继续问诊状态 4->5
            updateSessionRedisStatusValue(sessionId, ImSessionConstant.SESSION_CONTINUE_ON);
            imSessionDo.setSessionStatus(ImSessionConstant.SESSION_CONTINUE_ON);
            imSessionDo.setWeightFactor(ImSessionConstant.SESSION_CONTINUE_ON_WEIGHT);
            imSessionDo.setContinueSessionCount(imSessionDo.getContinueSessionCount() - 1);
            if (ImSessionConstant.SESSION_EVALUATE_CAN_STATUS.equals(imSessionDo.getEvaluateStatus())) {
                imSessionDo.setEvaluateStatus(ImSessionConstant.SESSION_EVALUATE_CAN_NOT_STATUS);
            }
            imSessionDao.update(imSessionDo);
        }

    }

    /**
     * 统计医师会话相关信息
     */
    private void statisticDoctorSessionState(Integer doctorId) {
        Integer sessionReadyToOnAckAvgTime = imSessionDao.getSessionReadyToOnAckAvgTime(doctorId);
        Integer sessionCount = imSessionDao.getSessionCount(doctorId);
        BigDecimal sessionMoney = imSessionDao.getSessionTotalMoney(doctorId);
        DoctorSortParam sortParam = new DoctorSortParam();
        sortParam.setDoctorId(doctorId);
        if (sessionReadyToOnAckAvgTime != null) {
            sortParam.setAvgAnswerTime(sessionReadyToOnAckAvgTime);
            doctorService.updateAvgAnswerTime(sortParam);
        }
        sortParam.setConsultationNumber(sessionCount);
        doctorService.updateConsultationNumber(sortParam);
        sortParam.setConsultationTotalMoney(sessionMoney);
        doctorService.updateConsultationTotalMoney(sortParam);

    }

    /**
     * 批量取消未接诊或关闭已接诊且退款的会话
     */
    public void batchCancelSession(List<String> orderSns) {
        ImSessionCondition condition = new ImSessionCondition();
        condition.setOrderSns(orderSns);
        List<ImSessionDo> imSessionDos = imSessionDao.listImSession(condition);
        List<Integer> cancelSessionIds = new ArrayList<>(imSessionDos.size());
        List<Integer> refundSessionIds = new ArrayList<>(imSessionDos.size());
        Integer shopId = getShopId();


        for (ImSessionDo imSessionDo : imSessionDos) {
            clearSessionRedisInfoAndDumpToDb(shopId, imSessionDo.getId(), imSessionDo.getUserId(), imSessionDo.getDoctorId());
            if (ImSessionConstant.SESSION_READY_TO_START.equals(imSessionDo.getSessionStatus())) {
                cancelSessionIds.add(imSessionDo.getId());
            } else {
                refundSessionIds.add(imSessionDo.getId());
            }
        }
        imSessionDao.batchUpdateSessionStatus(cancelSessionIds, ImSessionConstant.SESSION_CANCEL, ImSessionConstant.SESSION_CANCEL_WEIGHT);
        imSessionDao.batchUpdateSessionStatus(refundSessionIds, ImSessionConstant.SESSION_REFUND, ImSessionConstant.SESSION_REFUND_WEIGHT);
    }

    /**
     * 批量关闭到时间的会话
     */
    public void batchCloseSession(List<String> orderSns) {
        logger().info("批量关闭到时间的会话：" + orderSns);
        ImSessionCondition cancelCondition = new ImSessionCondition();
        cancelCondition.setOrderSns(orderSns);
        List<ImSessionDo> imSessionDos = imSessionDao.listImSession(cancelCondition);
        Integer shopId = getShopId();
        List<ImSessionDo> sessionDeads = new ArrayList<>(imSessionDos.size());
        List<ImSessionDo> sessionEnds = new ArrayList<>(imSessionDos.size());
        // 需要添加默认评价的会话集合
        List<ImSessionDo> canAddDefaultCommentSession = new ArrayList<>(0);

        for (ImSessionDo imSessionDo : imSessionDos) {
            if (imSessionDo.getContinueSessionCount() == 0) {
                imSessionDo.setSessionStatus(ImSessionConstant.SESSION_DEAD);
                imSessionDo.setWeightFactor(ImSessionConstant.SESSION_DEAD_WEIGHT);
                sessionDeads.add(imSessionDo);
                clearSessionRedisInfoAndDumpToDb(shopId, imSessionDo.getId(), imSessionDo.getUserId(), imSessionDo.getDoctorId());
            } else {
                if (ImSessionConstant.SESSION_ON.equals(imSessionDo.getSessionStatus())) {
                    canAddDefaultCommentSession.add(imSessionDo);
                }
                imSessionDo.setLimitTime(calculateSessionLimitTime());
                imSessionDo.setSessionStatus(ImSessionConstant.SESSION_END);
                imSessionDo.setWeightFactor(ImSessionConstant.SESSION_END_WEIGHT);
                sessionEnds.add(imSessionDo);
                updateSessionRedisStatusValue(imSessionDo.getId(), ImSessionConstant.SESSION_END);
            }
        }
        imSessionDao.batchUpdate(imSessionDos);
        // 修改评价状态
        List<Integer> sessionDeadIds = sessionDeads.stream().map(ImSessionDo::getId).collect(Collectors.toList());
        List<Integer> sessionEndIds = sessionEnds.stream().map(ImSessionDo::getId).collect(Collectors.toList());
        imSessionDao.batchUpdateSessionEvaluateStatus(sessionDeadIds, ImSessionConstant.SESSION_EVALUATE_CAN_NOT_STATUS, ImSessionConstant.SESSION_EVALUATE_CAN_STATUS);
        imSessionDao.batchUpdateSessionEvaluateStatus(sessionEndIds, ImSessionConstant.SESSION_EVALUATE_CAN_STATUS, ImSessionConstant.SESSION_EVALUATE_CAN_NOT_STATUS);

        for (ImSessionDo imSessionDo : canAddDefaultCommentSession) {
            doctorCommentService.addDefaultComment(imSessionDo.getDoctorId(), imSessionDo.getUserId(), imSessionDo.getPatientId(), imSessionDo.getOrderSn(), imSessionDo.getId());
        }
    }

    /**
     * 关闭对话session
     * @param sessionId 会话id
     */
    public void closeImSession(Integer sessionId){
        ImSessionDo imSessionDo = imSessionDao.getById(sessionId);
        if (!ImSessionConstant.SESSION_ON.equals(imSessionDo.getSessionStatus()) && !ImSessionConstant.SESSION_CONTINUE_ON.equals(imSessionDo.getSessionStatus())) {
            return;
        }
        if (imSessionDo.getContinueSessionCount() == 0) {
            clearSessionRedisInfoAndDumpToDb(getShopId(), imSessionDo.getId(), imSessionDo.getUserId(), imSessionDo.getDoctorId());
            imSessionDao.updateSessionStatus(sessionId, ImSessionConstant.SESSION_DEAD, ImSessionConstant.SESSION_DEAD_WEIGHT);
            imSessionDao.batchUpdateSessionEvaluateStatus(Collections.singletonList(sessionId), ImSessionConstant.SESSION_EVALUATE_CAN_NOT_STATUS, ImSessionConstant.SESSION_EVALUATE_CAN_STATUS);
        } else {
            // 第一次关闭会话插入默认评价
            if (ImSessionConstant.SESSION_ON.equals(imSessionDo.getSessionStatus())) {
                doctorCommentService.addDefaultComment(imSessionDo.getDoctorId(), imSessionDo.getUserId(), imSessionDo.getPatientId(), imSessionDo.getOrderSn(), imSessionDo.getId());
            }
            imSessionDao.updateSessionStatus(sessionId, ImSessionConstant.SESSION_END, ImSessionConstant.SESSION_END_WEIGHT);
            updateSessionRedisStatusValue(sessionId, ImSessionConstant.SESSION_END);
            imSessionDao.batchUpdateSessionEvaluateStatus(Collections.singletonList(sessionId), ImSessionConstant.SESSION_EVALUATE_CAN_STATUS, ImSessionConstant.SESSION_EVALUATE_CAN_NOT_STATUS);
        }

    }

    /**
     * 终止会话
     * @param imSessionDo
     */
    public void deadImSession(ImSessionDo imSessionDo) {
        clearSessionRedisInfoAndDumpToDb(getShopId(), imSessionDo.getId(), imSessionDo.getUserId(), imSessionDo.getDoctorId());
        imSessionDo.setSessionStatus(ImSessionConstant.SESSION_DEAD);
        imSessionDo.setWeightFactor(ImSessionConstant.SESSION_DEAD_WEIGHT);
        imSessionDo.setEvaluateStatus(ImSessionConstant.SESSION_EVALUATE_CAN_NOT_STATUS);
        imSessionDao.update(imSessionDo);
    }

    /**
     * 定时任务调用，结束已经超时的可继续问诊项
     */
    public void timingDeadReadyToContinueSession() {
        logger().debug("定时任务调用，结束已经超时的可继续问诊项");
        Timestamp limitTime = DateUtils.getTimeStampPlus(-1, ChronoUnit.DAYS);
        ImSessionCondition imSessionCondition = new ImSessionCondition();
        imSessionCondition.setStatus(ImSessionConstant.SESSION_END);
        imSessionCondition.setLimitTime(limitTime);
        List<ImSessionDo> imSessionDos = imSessionDao.listImSession(imSessionCondition);
        for (ImSessionDo imSessionDo : imSessionDos) {
            imSessionDo.setSessionStatus(ImSessionConstant.SESSION_DEAD);
            imSessionDo.setWeightFactor(ImSessionConstant.SESSION_DEAD_WEIGHT);
            imSessionDo.setEvaluateStatus(ImSessionConstant.SESSION_EVALUATE_CAN_NOT_STATUS);
        }
        imSessionDao.batchUpdate(imSessionDos);
        for (ImSessionDo imSessionDo : imSessionDos) {
            clearSessionRedisInfoAndDumpToDb(getShopId(), imSessionDo.getId(), imSessionDo.getUserId(), imSessionDo.getDoctorId());
        }
    }

    /**
     * 将消息发送，存储至redis
     * @param sendMsgParam 会话消息
     */
    public Byte sendMsg(ImSessionSendMsgParam sendMsgParam) {
        Integer shopId = getShopId();
        String sessionRedisStatusKey = getSessionRedisStatusKey(shopId, sendMsgParam.getSessionId());
        String statusVal = jedisManager.get(sessionRedisStatusKey);
        if (statusVal == null) {
            deleteAllSessionKey(shopId, sendMsgParam.getSessionId(), sendMsgParam.getFromId(), sendMsgParam.getToId());
            return ImSessionConstant.SESSION_CAN_NOT_USE;
        }

        String sessionKey = getSessionRedisKey(getShopId(), sendMsgParam.getSessionId(), sendMsgParam.getFromId(), sendMsgParam.getToId());

        jedisManager.rpush(sessionKey, Util.toJson(sendMsgParam.getImSessionItem()));

        return ImSessionConstant.SESSION_CAN_USE;
    }

    /**
     * 拉取对方发送的消息内
     * @return
     */
    public ImSessionPullMsgVo pullMsg(ImSessionPullMsgParam param) {
        ImSessionPullMsgVo vo = new ImSessionPullMsgVo();
        String sessionRedisStatusKey = getSessionRedisStatusKey(getShopId(), param.getSessionId());
        String statusVal = jedisManager.get(sessionRedisStatusKey);
        if (statusVal == null) {
            vo.setStatus(ImSessionConstant.SESSION_DEAD);
            return vo;
        }
        Byte status = Byte.valueOf(statusVal);
        vo.setStatus(status);

        List<ImSessionItemBase> imSessionItemBases = dumpSessionReadyToBak(getShopId(), param.getSessionId(), param.getPullFromId(), param.getSelfId());
        vo.setMessages(imSessionItemBases);
        return vo;
    }

    /**
     * 清空redis中的会话信息（将医生和患者未读信息全部dump到bak中），并入库
     * @param shopId    店铺id
     * @param sessionId 会话id
     * @param userId    用户id
     * @param doctorId  医师id
     */
    private void clearSessionRedisInfoAndDumpToDb(Integer shopId, Integer sessionId, Integer userId, Integer doctorId) {
        // dump医师发送的信息 对于list类型，如果长度为0则自动删除
        dumpSessionReadyToBak(shopId, sessionId, doctorId, userId);
        // dump用户发送的信息
        dumpSessionReadyToBak(shopId, sessionId, userId, doctorId);
        // dump所有bak信息并入库
        dumpSessionBakToDb(shopId, sessionId);

        String sessionRedisStatusKey = getSessionRedisStatusKey(shopId, sessionId);
        jedisManager.delete(sessionRedisStatusKey);
    }

    /**
     * 将待查看会话中的信息内容移动至已读会话记录列表内
     * @param shopId    店铺id
     * @param sessionId 会话id
     * @param fromId    发送者id
     * @param toId      接受者id
     * @return 待查看会话集合
     */
    private List<ImSessionItemBase> dumpSessionReadyToBak(Integer shopId, Integer sessionId, Integer fromId, Integer toId) {
        String sessionKey = getSessionRedisKey(shopId, sessionId, fromId, toId);
        String sessionBakKey = getSessionRedisKeyBak(shopId, sessionId);

        List<String> readyToReadList = jedisManager.getList(sessionKey);
        jedisManager.cleanList(sessionKey);
        List<ImSessionItemBase> retVos = new ArrayList<>(readyToReadList.size());
        // 没有需要读的信息
        if (readyToReadList.size() == 0) {
            return retVos;
        }

        List<String> dumpList = new ArrayList<>(readyToReadList.size());

        for (String s : readyToReadList) {
            ImSessionItemBase vo = Util.parseJson(s, ImSessionItemBase.class);
            if (vo == null) {
                continue;
            }
            retVos.add(vo);
            ImSessionItemBo imSessionItemBo = new ImSessionItemBo(sessionId, fromId, toId, vo);
            dumpList.add(Util.toJson(imSessionItemBo));
        }
        jedisManager.rpush(sessionBakKey, dumpList);
        return retVos;
    }

    /**
     * 将已读会话记录信息的从redis清空并入库
     * @param shopId    店铺id
     * @param sessionId 会话id
     */
    private void dumpSessionBakToDb(Integer shopId, Integer sessionId) {
        String sessionBakKey = getSessionRedisKeyBak(shopId, sessionId);
        List<String> list = jedisManager.getList(sessionBakKey);
        jedisManager.cleanList(sessionBakKey);
        if (list.size() == 0) {
            return;
        }
        List<ImSessionItemDo> readyToDb = new ArrayList<>();
        for (String s : list) {
            ImSessionItemDo imSessionItemDo = Util.parseJson(s, ImSessionItemDo.class);
            if (imSessionItemDo == null) {
                continue;
            }
            readyToDb.add(imSessionItemDo);
        }
        imSessionItemDao.batchInsert(readyToDb);
    }

    /**
     * 删除所有无用key,避免医师结束了会话而此刻用户又发了消息而产生的垃圾key
     * @param shopId    店铺id
     * @param sessionId 会话id
     * @param id1       会话者的id
     * @param id2       会话者的id
     */
    private void deleteAllSessionKey(Integer shopId, Integer sessionId, Integer id1, Integer id2) {
        String msgKey1 = getSessionRedisKey(shopId, sessionId, id1, id2);
        String msgKey2 = getSessionRedisKey(shopId, sessionId, id2, id1);
        String sessionRedisKeyBak = getSessionRedisKeyBak(shopId, sessionId);
        jedisManager.delete(new String[]{msgKey1, msgKey2, sessionRedisKeyBak});
    }

    /**
     * 判断信息
     * @param sessionId
     * @return
     */
    public boolean sessionExist(Integer sessionId) {
        return imSessionDao.getById(sessionId) != null;
    }

    /**
     * 获取待查看会话redis key
     * @param shopId    店铺id
     * @param sessionId 会话id
     * @param fromId    发送者id
     * @param toId      接受者id
     * @return
     */
    private String getSessionRedisKey(Integer shopId, Integer sessionId, Integer fromId, Integer toId) {
        return String.format(JedisKeyConstant.IM_SESSION_ITEM_LIST_KEY, shopId, sessionId, fromId, toId);
    }

    /**
     * 获取已查看会话记录redis key
     * @param shopId    店铺id
     * @param sessionId 会话id
     * @return
     */
    private String getSessionRedisKeyBak(Integer shopId, Integer sessionId) {
        return String.format(JedisKeyConstant.IM_SESSION_ITEM_LIST_KEY_BAK, shopId, sessionId);
    }

    /**
     * 获取标识会话是否可用的key
     * @param shopId    店铺id
     * @param sessionId 会话id
     * @return
     */
    private String getSessionRedisStatusKey(Integer shopId, Integer sessionId) {
        return String.format(JedisKeyConstant.IM_SESSION_STATUS, shopId, sessionId);
    }

    /**
     * 获取历史聊天记录
     * @param param
     * @return
     */
    public List<ImSessionItemRenderVo> getSessionHistory(ImSessionQueryPageListParam param) {
        ImSessionDo imSessionDo = imSessionDao.getByOrderSn(param.getOrderSn());
        Integer doctorId = imSessionDo.getDoctorId();
        List<ImSessionItemDo> list = imSessionItemDao.getBySessionId(imSessionDo.getId());
        return list.stream().map(item -> {
            ImSessionItemRenderVo itemVo = new ImSessionItemRenderVo();
            itemVo.setDoctor(doctorId.equals(item.getFromId()));
            itemVo.setMessage(item.getMessage());
            itemVo.setType(item.getType());
            itemVo.setSendTime(item.getSendTime());
            return itemVo;
        }).collect(Collectors.toList());
    }

}
