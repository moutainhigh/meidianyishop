package com.meidianyi.shop.service.saas.schedule;

import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.MathUtil;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.main.tables.records.TaskJobContentRecord;
import com.meidianyi.shop.db.main.tables.records.TaskJobMainRecord;
import com.meidianyi.shop.service.foundation.jedis.JedisKeyConstant;
import com.meidianyi.shop.service.foundation.jedis.JedisManager;
import com.meidianyi.shop.service.foundation.mq.RabbitmqSendService;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.saas.schedule.BaseTaskJob;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobInfo;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobsConstant;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.main.tables.TaskJobContent.TASK_JOB_CONTENT;
import static com.meidianyi.shop.db.main.tables.TaskJobMain.TASK_JOB_MAIN;


/**
 * 定时任务实现的部分逻辑
 * @author 卢光耀
 * @date 2019-08-13 14:13
 *
*/
@Service
public class TaskJobMainService extends MainBaseService {

    @Autowired
    private RabbitmqSendService rabbitmqSendService;

    @Autowired
    private JedisManager jedisManager;

    /**
     * 通用定时任务job处理（BaseTaskJob 自己定义）
     * @param job
     * @return taskJobMain的ID
     */
    public Integer dispatch(BaseTaskJob job) {
        TaskJobMainRecord mainRecord= db().newRecord(TASK_JOB_MAIN,job);
        Integer contentId = insertTaskJobContent(job.getContent());
        mainRecord.setContentId(contentId);
        if( job.getType().equals(TaskJobsConstant.TYPE_ONCE) ){
            mainRecord.setType(TaskJobsConstant.STATUS_EXECUTING);
        }
        Integer mainId = insertTaskJobMain(mainRecord);
        if( job.getType().equals(TaskJobsConstant.TYPE_ONCE) ){
            TaskJobsConstant.TaskJobEnum jobEnum = TaskJobsConstant.TaskJobEnum
                .getTaskJobEnumByExecutionType(job.getExecutionType());
            logger().info("发送job："+jobEnum.getExchangeName());
            rabbitmqSendService.sendMessage(jobEnum.getExchangeName(),
                jobEnum.getRoutingKey(),setTaskJobId(job.getContent(),job.getClassName(),mainId),job.getClassName());
        }
        return mainId;
    }

    /**
     * 判断任务是否已被终止
     * @param id taskJob id
     * @return false代表不可以执行，反之亦然
     */
    public Boolean assertExecuting(Integer id){
        TaskJobMainRecord record = getTaskJobMainRecordById(id);
        if( record.getStatus().equals(TaskJobsConstant.STATUS_TERMINATION)|| record.getStatus().equals(TaskJobsConstant.STATUS_COMPLETE)){
            return Boolean.FALSE;
        }else{
            return Boolean.TRUE;
        }
    }
    /**
     * 修改taskJob的状态
     * @param id taskJob id
     * @param status 状态
     */
    public void updateTaskJobStatus(Integer id,Byte status){
        db().update(TASK_JOB_MAIN)
            .set(TASK_JOB_MAIN.STATUS,status)
            .where(TASK_JOB_MAIN.ID.eq(id))
            .execute();
    }

    private String setTaskJobId(String jsonStr,String clzName,Integer jobId){
        String taskJobId = "taskJobId";
        if( !jsonStr.contains(taskJobId)&& assertContainsTaskJobId(clzName) ){
                String jobIdStr = "\"taskJobId\":"+jobId+",";
                String resultString = jsonStr.replaceFirst("\\[\\{","[{"+jobIdStr);
                if (!resultString.contains(taskJobId)) {
					resultString = resultString.replaceFirst("\\{", "{"+jobIdStr);
				}
                return  resultString;
            }else {
            String taskJobNullKey = "\"taskJobId\":null";
            if( jsonStr.contains(taskJobNullKey) ){
                String jobIdStr = "\"taskJobId\":"+jobId;
                return jsonStr.replaceFirst(taskJobNullKey,jobIdStr);
            }
            else{
                return jsonStr;
            }
        }
    }
    private Boolean assertContainsTaskJobId(String clzName)  {
        try {
            return Class.forName(clzName).getMethod("setTaskJobId",Integer.class) != null;
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            return Boolean.FALSE;
        }
    }
    /**
     *
     * @param param MQ传参类
     * @param className MQ传参类全称class.getName()
     * @param shopId 门店id
     * @param executionType 指定传输路由{@link TaskJobsConstant.TaskJobEnum}
     */
    public Integer dispatchImmediately(Object param,String className,Integer shopId,Integer executionType){
        TaskJobInfo  info = TaskJobInfo.builder(shopId)
            .type(TaskJobsConstant.TYPE_ONCE)
            .content(param)
            .className(className)
            .startTime(DateUtils.getLocalDateTime())
            .endTime(DateUtils.getLocalDateTime())
            .executionType(Objects.requireNonNull(TaskJobsConstant.TaskJobEnum
                .getTaskJobEnumByExecutionType(executionType)))
            .builder();
         return dispatch(info);
    }

    /**
     * 查询并发送待执行的任务给各自的队列
     */
    public  void  getAndSendMessage(){
        String uuid = Util.randomId();
        int timeOut = 60 * 60 * 1000;
        if( jedisManager.addLock(JedisKeyConstant.TASK_JOB_LOCK,uuid, timeOut) ){
            db().transaction(configuration -> {

                Result<Record4<Integer,String,String,Integer>> result = DSL.using(configuration)
                    .select(TASK_JOB_MAIN.ID,TASK_JOB_CONTENT.CONTENT,TASK_JOB_MAIN.CLASS_NAME,TASK_JOB_MAIN.EXECUTION_TYPE)
                    .from(TASK_JOB_MAIN)
                    .leftJoin(TASK_JOB_CONTENT).on(TASK_JOB_CONTENT.ID.eq(TASK_JOB_MAIN.CONTENT_ID))
                    .where(TASK_JOB_MAIN.STATUS.eq(TaskJobsConstant.STATUS_NEW))
                    .and(TASK_JOB_MAIN.TYPE.notEqual(TaskJobsConstant.TYPE_ONCE))
                    .and(TASK_JOB_MAIN.NEXT_EXECUTE_TIME.lessOrEqual(DateUtils.getLocalDateTime()))
                    .fetch();
                List<Integer> jobIds = result.stream().map(x->x.get(TASK_JOB_CONTENT.ID)).collect(Collectors.toList());
                result.forEach(r->{
                    TaskJobsConstant.TaskJobEnum job =
                        TaskJobsConstant.TaskJobEnum.getTaskJobEnumByExecutionType(r.get(TASK_JOB_MAIN.EXECUTION_TYPE));
                    if (job != null) {
                        rabbitmqSendService.sendMessage(job.getExchangeName(),job.getRoutingKey(),
                            setTaskJobId(r.get(TASK_JOB_CONTENT.CONTENT),r.get(TASK_JOB_MAIN.CLASS_NAME),r.get(TASK_JOB_MAIN.ID)) ,r.get(TASK_JOB_MAIN.CLASS_NAME));
                    }
                });

                if( !jobIds.isEmpty() ){
                    db().update(TASK_JOB_MAIN)
                        .set(TASK_JOB_MAIN.STATUS,TaskJobsConstant.STATUS_EXECUTING)
                        .where(TASK_JOB_MAIN.ID.in(jobIds))
                        .execute();
                }

            });
            jedisManager.releaseLock(JedisKeyConstant.TASK_JOB_LOCK,uuid);
        }
    }

    public TaskJobMainRecord getTaskJobMainRecordById(Integer taskJobId){
        return db().selectFrom(TASK_JOB_MAIN).where(TASK_JOB_MAIN.ID.eq(taskJobId)).fetchOne();
    }
    private TaskJobContentRecord getTaskJobContentRecordById(Integer taskContentId){
        return db().selectFrom(TASK_JOB_CONTENT).where(TASK_JOB_CONTENT.ID.eq(taskContentId)).fetchOne();
    }

    /**
     * 判断taskJob里面是否包含对应的数据
     * @param executionType 指定传输路由{@link TaskJobsConstant.TaskJobEnum}
     * @param status 状态
     * @return true:包含 false:不包含
     */
    public Boolean assertHasStatusTaskJob(Integer executionType,List<Byte> status){
        return db()
            .selectFrom(TASK_JOB_MAIN)
            .where(TASK_JOB_MAIN.EXECUTION_TYPE.eq(executionType))
            .and(TASK_JOB_MAIN.STATUS.in(status))
            .fetchOptional()
            .isPresent();
    }
    /**
     * 更新进度和状态
     * @param content
     * @param taskJobId
     * @param failSize
     * @param allSize
     */
    public void updateProgress(String content,Integer taskJobId, int failSize ,int allSize) {
        TaskJobMainRecord record = getTaskJobMainRecordById(taskJobId);
        if( record != null ){
            TaskJobContentRecord contentRecord = getTaskJobContentRecordById(record.getContentId());
            contentRecord.setContent(content);
            contentRecord.update();
            record.setProgress((byte)(MathUtil.deciMal(allSize-failSize,allSize)*100));
            if( record.getType().equals(TaskJobsConstant.TYPE_CYCLE_ONCE) ){
                Timestamp next = DateUtils.getDalyedDateTime(record.getCycle());
                if( next.before(record.getEndTime()) ){
                    record.setNextExecuteTime(next);
                    record.setStatus(TaskJobsConstant.STATUS_EXECUTING);
                }else{
                    record.setStatus(TaskJobsConstant.STATUS_COMPLETE);
                }
            }else{
                record.setStatus(TaskJobsConstant.STATUS_COMPLETE);
            }
            record.update();
        }
    }
    private Integer insertTaskJobContent(String content){
        return  db().insertInto(TASK_JOB_CONTENT)
            .set(TASK_JOB_CONTENT.CONTENT,content)
            .returning(TASK_JOB_CONTENT.ID)
            .fetchOne()
            .getId();
    }
    private Integer insertTaskJobMain(TaskJobMainRecord mainRecord){
        return db()
            .insertInto(TASK_JOB_MAIN)
            .set(mainRecord)
            .returning(TASK_JOB_MAIN.ID)
            .fetchOne()
            .getId();
    }
}
