package com.meidianyi.shop.service.saas.schedule.rabbit;

import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.domain.QueueInfo;
import com.meidianyi.shop.common.foundation.util.MathUtil;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.saas.schedule.rabbit.RabbitDataConstant;
import com.meidianyi.shop.service.pojo.saas.schedule.rabbit.RabbitInfoData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luguangyao
 */
@Slf4j
@Service
public class RabbitDataService extends MainBaseService {

    @Autowired
    private Client client;

    public List<RabbitInfoData> getDetails(){
        List<QueueInfo> list = client.getQueues();
        List<RabbitInfoData>  result = new ArrayList<>(list.size());
        for( QueueInfo info:list ){
            result.add(RabbitInfoData.builder()
                .messages(info.getTotalMessages())
                .name(info.getName())
                .memory(MathUtil.deciMal(info.getMemoryUsed(),1024L))
                .stateName(RabbitDataConstant.State.getNameByState(info.getState()))
                .build());

        }
        return result;
    }
}
