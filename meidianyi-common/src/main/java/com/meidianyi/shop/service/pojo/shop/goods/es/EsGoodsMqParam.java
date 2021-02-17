package com.meidianyi.shop.service.pojo.shop.goods.es;


import com.meidianyi.shop.service.foundation.jedis.data.DBOperating;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.List;

/**
 * es goods send to mq param
 * @author 卢光耀
 * @date 2019/10/23 11:00 上午
 *
*/
@Data
@Builder
public class EsGoodsMqParam {

    private List<Integer> idList;

    private Integer shopId;

    private DBOperating operate;
    @Tolerate
    public EsGoodsMqParam(){

    }
}
