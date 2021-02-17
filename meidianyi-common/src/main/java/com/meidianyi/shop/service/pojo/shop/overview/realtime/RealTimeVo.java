package com.meidianyi.shop.service.pojo.shop.overview.realtime;

import com.meidianyi.shop.service.pojo.shop.overview.Tuple2;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author:liufei
 * @Date:2019/7/22
 * @Description:
 */
@Data
@Component
public class RealTimeVo {
    /** 访客量 */
    private Tuple2<Integer,Integer> visitUsers;
    /** 支付订单数 */
    private Tuple2<Integer,Integer> payOrderNum;
    /** 支付用户数 */
    private Tuple2<Integer,Integer> payUserNum;
    /** 浏览量 */
    private Tuple2<Long,Long> pageViews;
    /** 今日付款金额 */
    private List<Tuple2<Byte,Double>> todayPaidMoney;
    /** 昨日全天付款金额 */
    private List<Tuple2<Byte,Double>> yesterdayPaidMoney;
}
