package com.meidianyi.shop.service.shop.market.message;


import static com.meidianyi.shop.db.shop.tables.MessageTemplate.MESSAGE_TEMPLATE;
import static com.meidianyi.shop.db.shop.tables.ServiceMessageRecord.SERVICE_MESSAGE_RECORD;
import static com.meidianyi.shop.db.shop.tables.TemplateConfig.TEMPLATE_CONFIG;
import static com.meidianyi.shop.db.shop.tables.User.USER;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.MathUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.TemplateConfigRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobInfo;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobsConstant;
import com.meidianyi.shop.service.pojo.shop.coupon.give.CouponGiveQueueParam;
import com.meidianyi.shop.service.pojo.shop.market.message.MessageOutputVo;
import com.meidianyi.shop.service.pojo.shop.market.message.MessageStatisticsVo;
import com.meidianyi.shop.service.pojo.shop.market.message.MessageTemplateDetailVo;
import com.meidianyi.shop.service.pojo.shop.market.message.MessageTemplateParam;
import com.meidianyi.shop.service.pojo.shop.market.message.MessageTemplateQuery;
import com.meidianyi.shop.service.pojo.shop.market.message.MessageTemplateVo;
import com.meidianyi.shop.service.pojo.shop.market.message.MessageUserQuery;
import com.meidianyi.shop.service.pojo.shop.market.message.RabbitMessageParam;
import com.meidianyi.shop.service.pojo.shop.market.message.RabbitParamConstant;
import com.meidianyi.shop.service.pojo.shop.market.message.SendUserVo;
import com.meidianyi.shop.service.pojo.shop.market.message.UserInfoByRedis;
import com.meidianyi.shop.service.pojo.shop.market.message.UserInfoQuery;
import com.meidianyi.shop.service.pojo.shop.market.message.UserInfoVo;
import com.meidianyi.shop.service.pojo.shop.market.message.content.ContentMessageParam;
import com.meidianyi.shop.service.pojo.shop.market.message.content.ContentMessageVo;
import com.meidianyi.shop.service.pojo.shop.message.MpTemplateConfig;
import com.meidianyi.shop.service.pojo.shop.message.MpTemplateData;
import com.meidianyi.shop.service.saas.schedule.TaskJobMainService;
import com.meidianyi.shop.service.shop.order.trade.OrderPayService;
import com.meidianyi.shop.service.shop.user.user.SendUserService;

/**
 * 营销管理--推送消息实现类
 * @author 卢光耀
 * @date 2019-08-12 14:19
 *
*/
@Service
public class MessageTemplateService extends ShopBaseService {

    @Autowired
    private SendUserService sendUserService;

    private TaskJobMainService taskJobMainService;

	@Autowired
	public OrderPayService orderPayService;


    @PostConstruct
    private void init(){
        taskJobMainService = saas().taskJobMainService;
    }




    public SendUserVo getSendUsersSize(UserInfoQuery query){
        SendUserVo vo = new SendUserVo();
        String key = StringUtils.isNotBlank(query.getUserKey())?
            query.getUserKey():sendUserService.getKeyWithRedisBySendUser();
        vo.setUserKey(key);
        vo.setUserNumber(sendUserService.getSendUserByQuery(query,key));
        return vo;
    }


    public PageResult<UserInfoVo> getUserVoPage(MessageUserQuery query){
        List<UserInfoByRedis> list = sendUserService.getSendUserInfoByRedisKey(query.getUserKey());
        Map<Integer,UserInfoByRedis> map = list
            .stream()
            .collect(Collectors.toMap(UserInfoByRedis::getUserId,x->x));
        List<Integer> userIds = list.stream().map(UserInfoByRedis::getUserId).collect(Collectors.toList());
        PageResult<UserInfoVo> resultPage = sendUserService.getUserInfoByIds(userIds,query);
        assemblyUserPage(map,resultPage);
        return resultPage;
    }
    public void updateClickStatus(MessageUserQuery query){

        List<UserInfoByRedis> list = sendUserService.getSendUserInfoByRedisKey(query.getUserKey());
        List<UserInfoByRedis> newList = new ArrayList<>(list.size());
        Map<Integer,UserInfoByRedis> map = list
            .stream()
            .collect(Collectors.toMap(UserInfoByRedis::getUserId,x->x));
        for( UserInfoByRedis userInfoByRedis: list ){
            Integer userId = userInfoByRedis.getUserId();
            if( query.getUserIds().contains(userId) ){
                userInfoByRedis.setIsChecked(Boolean.TRUE);
            }else{
                userInfoByRedis.setIsChecked(Boolean.FALSE);
            }
        }
        for(Map.Entry<Integer,UserInfoByRedis> entry:map.entrySet()){
            newList.add(entry.getValue());
        }
        sendUserService.setUserToJedis(query.getUserKey(),newList);
    }

    private void assemblyUserPage(Map<Integer,UserInfoByRedis> map,PageResult<UserInfoVo> page){
        for( UserInfoVo vo:page.getDataList() ){
            UserInfoByRedis r = map.get(vo.getUserId());
            vo.setIsChecked(r.getIsChecked());
            vo.setCanSend(r.getCanSend());
        }
    }

    public void insertMessageTemplate(MessageTemplateParam param){
        String userIdStr = sendUserService.getAndDeleteSendUserIdByRedisKey(param.getUserKey())
            .stream()
            .map(UserInfoByRedis::getUserId)
            .map(Objects::toString)
            .collect(Collectors.joining(","));

        Integer shopId = getShopId();
//        String userIdStr = "12,";
        String sendConditionStr = Util.toJson(param.getUserInfo());
        TemplateConfigRecord record = db().newRecord(TEMPLATE_CONFIG,param);
        record.setToUser(userIdStr);
        record.setSendCondition(sendConditionStr);
        TemplateConfigRecord templateConfigRecord =db().insertInto(TEMPLATE_CONFIG)
            .set(record)
            .returning(TEMPLATE_CONFIG.ID,TEMPLATE_CONFIG.PAGE_LINK,TEMPLATE_CONFIG.TITLE,TEMPLATE_CONFIG.CONTENT)
            .fetchOne();
        createTaskJob(shopId, assemblyRabbitMessageParam(templateConfigRecord,userIdStr,shopId),param);
    }

    /**
     * 封装MQ传参
     * @param templateConfigRecord 消息推送的相关信息
     * @param userIdStr 接收人
     * @param shopId 门店ID
     * @return {@link RabbitMessageParam}
     */
    private RabbitMessageParam assemblyRabbitMessageParam(TemplateConfigRecord templateConfigRecord,String userIdStr,Integer shopId ){
        return RabbitMessageParam.builder()
            .shopId(shopId)
            .type(RabbitParamConstant.Type.DIY_MESSAGE_TEMPLATE)
            .page(templateConfigRecord.getPageLink())
            .messageTemplateId(templateConfigRecord.getId())
            .userIdList(Arrays.stream(userIdStr.split(",")).map(Integer::parseInt).collect(Collectors.toList()))
            .mpTemplateData(MpTemplateData.builder()
                .config(MpTemplateConfig.ACTIVITY_CONFIG)
                .data(new String[][]{
                    {""},
                    {templateConfigRecord.getTitle()},
                    {templateConfigRecord.getContent()},
                    {"点击查看详情"},
                    {DateUtils.getLocalDateTime().toString()}
                })
                .build())
            .build();
    }

    /**
     * 创建TaskJob
     * @param shopId 门店ID
     * @param messageTemplateParam 消息内容
     * @param param 消息的一些配置参数
     */
    private void createTaskJob(Integer shopId,RabbitMessageParam messageTemplateParam,MessageTemplateParam param){
        TaskJobInfo  info = TaskJobInfo.builder(shopId)
            .type(param.getSendAction())
            .content(messageTemplateParam)
            .className(messageTemplateParam.getClass().getName())
            .startTime(param.getStartTime())
            .endTime(param.getEndTime())
            .executionType(TaskJobsConstant.TaskJobEnum.SEND_MESSAGE)
            .builder();
        taskJobMainService.dispatch(info);
    }

    public PageResult<MessageTemplateVo> getPageByParam(MessageTemplateQuery param) {
        PageResult<MessageTemplateVo> resultPage = new PageResult<>();
        SelectLimitStep<Record> select  = db().select()
            .from(TEMPLATE_CONFIG)
            .where(buildParams(param))
            .and(TEMPLATE_CONFIG.DEL_FLAG.eq((byte)0))
            .orderBy(TEMPLATE_CONFIG.CREATE_TIME.desc());
        PageResult<TemplateConfigRecord> templatePage = getPageResult(select,param.getCurrentPage(),param.getPageRows(),TemplateConfigRecord.class);
        BeanUtils.copyProperties(templatePage,resultPage);
        return buildPageVo(resultPage,templatePage);
    }

    /**
     * vo转换
     * @param resultPage voPage
     * @param templatePage  sourcePage
     * @return voPage
     */
    private PageResult<MessageTemplateVo> buildPageVo(PageResult<MessageTemplateVo> resultPage,PageResult<TemplateConfigRecord> templatePage){
        List<TemplateConfigRecord> templateList = templatePage.getDataList();
        List<Integer> templateIdList = templateList.stream().map(TemplateConfigRecord::getId).collect(Collectors.toList());
        Map<String,Integer> sendMap = getSentPersonByTemplateId(templateIdList);
        Map<String,Integer> visitMap = getVisitedPersonByTemplateId(templateIdList);
        List<MessageTemplateVo> resultVoList = new ArrayList<>();
        for(TemplateConfigRecord record : templateList  ){
            MessageTemplateVo vo = new MessageTemplateVo();
            String idStr = record.getId().toString();
            int sentNumber = sendMap.getOrDefault(idStr, 0);
            int visitNumber = visitMap.getOrDefault(idStr, 0);
            BeanUtils.copyProperties(record,vo);
            vo.setSentNumber(sentNumber);
            vo.setClickedNumber(visitNumber);
            if( sendMap.containsKey(idStr) ){
                vo.setPercentage(MathUtil.deciMal(visitNumber,sentNumber)*100);
            }else{
                vo.setPercentage(0D);
            }

            resultVoList.add(vo);
        }
        resultPage.setDataList(resultVoList);
        return resultPage;
    }

    /**
     * 根据推送消息id获取已发送人数
     * @param templateIdList 推送消息id集合
     * @return 推送消息id和对应的发送人数
     */
    private Map<String,Integer> getSentPersonByTemplateId(List<Integer> templateIdList){
        return db()
            .select(SERVICE_MESSAGE_RECORD.LINK_IDENTITY, DSL.count(SERVICE_MESSAGE_RECORD.LINK_IDENTITY).as("number"))
            .from(SERVICE_MESSAGE_RECORD)
            .where(SERVICE_MESSAGE_RECORD.LINK_IDENTITY.in(templateIdList))
            .groupBy(SERVICE_MESSAGE_RECORD.LINK_IDENTITY)
            .orderBy(SERVICE_MESSAGE_RECORD.CREATE_TIME.desc())
            .fetch()
            .stream()
            .collect(Collectors.toMap(x->x.get(SERVICE_MESSAGE_RECORD.LINK_IDENTITY),x->Integer.parseInt(x.get("number").toString())));
    }

    /**
     * 根据推送消息id获取对应的访问人数
     * @param templateIdList 推送消息id集合
     * @return 推送消息id和对应的访问人数
     */
    private Map<String,Integer> getVisitedPersonByTemplateId( List<Integer> templateIdList ){
        return db()
            .select(SERVICE_MESSAGE_RECORD.LINK_IDENTITY, DSL.count(SERVICE_MESSAGE_RECORD.LINK_IDENTITY).as("number"))
            .from(SERVICE_MESSAGE_RECORD)
            .where(SERVICE_MESSAGE_RECORD.LINK_IDENTITY.in(templateIdList))
            .and(SERVICE_MESSAGE_RECORD.IS_VISIT.eq((byte)1))
            .groupBy(SERVICE_MESSAGE_RECORD.LINK_IDENTITY)
            .fetch()
            .stream()
            .collect(Collectors.toMap(x->x.get(SERVICE_MESSAGE_RECORD.LINK_IDENTITY),x->Integer.parseInt(x.get("number").toString())));
    }
    private List<Condition> buildParams(MessageTemplateQuery param){
        List<Condition> result = new ArrayList<>();
        if( StringUtils.isNotBlank(param.getMessageName()) ){
            result.add(TEMPLATE_CONFIG.NAME.contains(param.getMessageName()));
        }
        if( StringUtils.isNotBlank(param.getBusinessTitle()) ){
            result.add(TEMPLATE_CONFIG.TITLE.contains(param.getBusinessTitle()));
        }
        if( param.getStartTime() != null ){
            result.add(TEMPLATE_CONFIG.START_TIME.greaterOrEqual(param.getStartTime() ));
        }
        if( param.getEndTime() != null  ){
            result.add(TEMPLATE_CONFIG.END_TIME.lessOrEqual(param.getEndTime()));
        }
        if( param.getTemplateId() != null){
            result.add(SERVICE_MESSAGE_RECORD.LINK_IDENTITY.eq(param.getTemplateId().toString()));
        }
        if( StringUtils.isNotBlank(param.getUserName()) ){
            result.add(USER.USERNAME.contains(param.getUserName()));
        }
        if(  null != param.getSendType() && param.getSendType() != 0 ){
            result.add(SERVICE_MESSAGE_RECORD.TEMPLATE_PLATFORM.eq(param.getSendType().byteValue()));
        }
        if(  null != param.getIsOnClick() && param.getIsOnClick() != 0 ){
            if(param.getIsOnClick() == 1){
                result.add(SERVICE_MESSAGE_RECORD.IS_VISIT.eq(param.getIsOnClick() .byteValue()));
            }else{
                result.add(SERVICE_MESSAGE_RECORD.IS_VISIT.eq((byte)0));
            }

        }
        return result;
    }

    public void deleteById(Integer id) {
        db().update(TEMPLATE_CONFIG)
            .set(TEMPLATE_CONFIG.DEL_FLAG,(byte)1)
            .set(TEMPLATE_CONFIG.DEL_TIME, DateUtils.getLocalDateTime())
            .where(TEMPLATE_CONFIG.ID.eq(id))
            .execute();
    }

    public MessageTemplateDetailVo getMessageDetail(Integer id) {
        MessageTemplateDetailVo vo = new MessageTemplateDetailVo();
        TemplateConfigRecord record = getById(id);
        UserInfoQuery userInfo = Util.parseJson(record.getSendCondition(),UserInfoQuery.class);
        BeanUtils.copyProperties(record,vo);
        vo.setUserInfo(userInfo);
        return vo;
    }
    private TemplateConfigRecord getById(Integer id){
        return db().selectFrom(TEMPLATE_CONFIG)
            .where(TEMPLATE_CONFIG.ID.eq(id))
            .fetchAny();
    }
    public PageResult<MessageOutputVo> getSendRecord(MessageTemplateQuery query){
        SelectConditionStep<Record6<String, Byte, Byte, Byte, Timestamp, Timestamp>> select  = db().select(
            USER.USERNAME,
            SERVICE_MESSAGE_RECORD.TEMPLATE_PLATFORM,
            SERVICE_MESSAGE_RECORD.IS_VISIT,
            SERVICE_MESSAGE_RECORD.SEND_STATUS,
            SERVICE_MESSAGE_RECORD.VISIT_TIME,
            SERVICE_MESSAGE_RECORD.CREATE_TIME).from(SERVICE_MESSAGE_RECORD)
            .leftJoin(USER).on(USER.USER_ID.eq(SERVICE_MESSAGE_RECORD.USER_ID))
            .where(buildParams(query));
        return getPageResult(select,query.getCurrentPage(),MessageOutputVo.class);
    }

    public MessageStatisticsVo queryStatisticsData(MessageTemplateQuery query){
        MessageStatisticsVo vo = new MessageStatisticsVo();
        Integer allSendNum = 0,allSentNum = 0,allVisitNum = 0;
        List<MessageStatisticsVo.StatisticsByDay> allStatistics = new ArrayList<>();
        List<LocalDate> allDate = DateUtils
            .getAllDatesBetweenTwoDates(query.getStartTime().toLocalDateTime().toLocalDate(),query.getEndTime().toLocalDateTime().toLocalDate());
        Map<String,Integer> messageMap = db()
            .select(dateFormat(TEMPLATE_CONFIG.START_TIME,"%Y-%m-%d").as("everyDate"),DSL.count().as("numbers"))
            .from(TEMPLATE_CONFIG)
            .where(TEMPLATE_CONFIG.START_TIME.lessThan(query.getEndTime()))
            .and(TEMPLATE_CONFIG.START_TIME.greaterThan(query.getStartTime()))
            .groupBy(DSL.field("everyDate"))
            .fetch()
            .stream()
            .collect(Collectors.toMap(x->x.get("everyDate").toString(),x->Integer.parseInt(x.get("numbers").toString())));
        Result<Record3<String,Byte,Byte>> serviceMessageResult = db()
            .select(
                dateFormat(SERVICE_MESSAGE_RECORD.CREATE_TIME,"%Y-%m-%d").as("everyDate"),
                SERVICE_MESSAGE_RECORD.SEND_STATUS,
                SERVICE_MESSAGE_RECORD.IS_VISIT)
            .from(SERVICE_MESSAGE_RECORD)
            .where(SERVICE_MESSAGE_RECORD.CREATE_TIME.lessThan(query.getEndTime()))
            .and(SERVICE_MESSAGE_RECORD.CREATE_TIME.greaterThan(query.getStartTime()))
            .fetch();


        Map<String,Integer> sentMap = Maps.newHashMap();
        for( Record3<String,Byte,Byte> record3:  serviceMessageResult){
            if( record3.get(SERVICE_MESSAGE_RECORD.SEND_STATUS).equals((byte)1) ){
                int i = sentMap.getOrDefault(record3.get("everyDate").toString(),0);
                sentMap.put(record3.get("everyDate").toString(),i+1);
            }
        }
        Map<String,Integer> visitMap = Maps.newHashMap();
        for( Record3<String,Byte,Byte> record3:  serviceMessageResult){
            if( record3.get(SERVICE_MESSAGE_RECORD.IS_VISIT).equals((byte)1) ){
                int i = visitMap.getOrDefault(record3.get("everyDate").toString(),0);
                visitMap.put(record3.get("everyDate").toString(),i+1);
            }
        }

        for(LocalDate date: allDate  ){
            String localDate = date.toString();
            Integer sentNum = sentMap.getOrDefault(localDate,0);
            Integer msgNum = messageMap.getOrDefault(localDate,0);
            Integer visitNum = visitMap.getOrDefault(localDate,0);
            allSendNum+=msgNum;
            allSentNum+=sentNum;
            allVisitNum+=visitNum;
            MessageStatisticsVo.StatisticsByDay day = vo.new StatisticsByDay();
            day.setDate(date);
            day.setSendNumber(msgNum);
            day.setSendSuccessNumber(sentNum);
            day.setVisitNumber(visitNum);
            if( sentNum != 0){
                day.setVisitPercentage(MathUtil.deciMal(visitNum,sentNum));
            }else{
                if( visitNum != 0 ){
                    day.setVisitPercentage(100.00);
                }else{
                    day.setVisitPercentage(0.00);
                }
            }
            allStatistics.add(day);
        }
        vo.setAllStatistics(allStatistics);
        vo.setSendNumber(allSendNum);
        vo.setSendSuccessNumber(allSentNum);
        vo.setVisitNumber(allVisitNum);
        if( allSentNum != 0){
            vo.setVisitPercentage(MathUtil.deciMal(allVisitNum,allSentNum));
        }else{
            if( allVisitNum != 0 ){
                vo.setVisitPercentage(100.00);
            }else{
                vo.setVisitPercentage(0.00);
            }
        }
        return vo;
    }

    public List<ContentMessageVo> getContentTemplate(ContentMessageParam param) {
        return db().select(MESSAGE_TEMPLATE.CONTENT,MESSAGE_TEMPLATE.ID)
            .from(MESSAGE_TEMPLATE)
            .where(MESSAGE_TEMPLATE.ACTION.eq(param.getAction()))
            .fetchInto(ContentMessageVo.class);
    }


    public void updateTemplateSendStatus(Integer userId,Integer templateId){
        db().update(SERVICE_MESSAGE_RECORD).
            set(SERVICE_MESSAGE_RECORD.SEND_STATUS,(byte)1).
            where(SERVICE_MESSAGE_RECORD.USER_ID.eq(userId)).
            and(SERVICE_MESSAGE_RECORD.LINK_IDENTITY.eq(templateId.toString())).execute();

    }

    public void updateTemplateStatus(Integer templateId){
        db().update(TEMPLATE_CONFIG).set(TEMPLATE_CONFIG.SEND_STATUS,(byte)1).
            where(TEMPLATE_CONFIG.ID.eq(templateId)).execute();
    }

    public void addContentTemplate(ContentMessageParam param) {
        db().newRecord(MESSAGE_TEMPLATE,param).insert();
    }
    /**
     * 创建定向发券TaskJob
     * @param shopId 门店ID
     */
    public void createCouponTaskJob(Integer shopId,CouponGiveQueueParam couponGiveQueueParam,Timestamp startTime){
        TaskJobInfo  info = TaskJobInfo.builder(shopId)
            .type(TaskJobsConstant.EXECUTION_TIMING)
            .content(couponGiveQueueParam)
            .className(CouponGiveQueueParam.class.getName())
            .startTime(startTime)
            .executionType(TaskJobsConstant.TaskJobEnum.GIVE_COUPON)
            .builder();
        taskJobMainService.dispatch(info);
    }
}
