package com.meidianyi.shop.service.pojo.saas.schedule.rabbit;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author luguangyao
 */
@Builder
@Getter
@Setter
public class RabbitInfoData {
    private String name;

    private Double memory;

    private Long messages;

    private String stateName;

}
