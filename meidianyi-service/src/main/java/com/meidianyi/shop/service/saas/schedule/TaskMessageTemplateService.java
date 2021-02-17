package com.meidianyi.shop.service.saas.schedule;

import static com.meidianyi.shop.db.main.tables.TaskJobContent.TASK_JOB_CONTENT;
import static com.meidianyi.shop.db.main.tables.TaskJobMain.TASK_JOB_MAIN;

import org.jooq.Record2;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.service.foundation.mq.RabbitmqSendService;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobsConstant;

/**
 * 消息发送TaskService
 * @author 卢光耀
 * @date 2019-08-20 15:43
 *
*/
@Service
public class TaskMessageTemplateService extends MainBaseService {
    @Autowired
    private RabbitmqSendService rabbitmqSendService;

    public Result<Record2<String,Integer>> getNeedMessage(Integer executionType){
        return db().select(TASK_JOB_CONTENT.CONTENT,TASK_JOB_MAIN.SHOP_ID)
            .from(TASK_JOB_MAIN)
            .leftJoin(TASK_JOB_CONTENT).on(TASK_JOB_MAIN.CONTENT_ID.eq(TASK_JOB_CONTENT.ID))
            .where(TASK_JOB_MAIN.EXECUTION_TYPE.eq(executionType))
            .and(TASK_JOB_MAIN.NEXT_EXECUTE_TIME.lessThan(DateUtils.getLocalDateTime()))
            .fetch();
    }
    public void sendMessage(Result<Record2<String,Integer>> result, TaskJobsConstant.TaskJobEnum job){
        result.forEach(r->
            rabbitmqSendService.sendMessage(
                job.getExchangeName(),job.getRoutingKey(),r.get(TASK_JOB_MAIN.SHOP_ID),r.get(TASK_JOB_CONTENT.CONTENT)
            )
        );
    }
}
