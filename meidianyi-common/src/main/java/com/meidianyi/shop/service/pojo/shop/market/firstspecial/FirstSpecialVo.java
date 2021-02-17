package com.meidianyi.shop.service.pojo.shop.market.firstspecial;

import com.meidianyi.shop.service.pojo.shop.config.PictorialShareConfigVo;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;


/**
 * @author: 王兵兵
 * @create: 2019-08-16 17:32
 **/
@Data
public class FirstSpecialVo {

    /** 活动ID */
    private Integer id;

    /** 活动名称 */
    private String name;

    /** 活动优先级 */
    private Byte first;

    /** 是否永久有效 */
    private Byte isForever;

    /** 活动开始时间 */
    private Timestamp startTime;

    /** 活动结束时间 */
    private Timestamp endTime;

    /** 限购数量 */
    private Integer limitAmount;

    /** 超限购买设置标记，1禁止超限购买，0超限全部恢复原价 */
    private Byte limitFlag;

    /** 改价的商品数组 */
    private List<FirstSpecialGoodsVo> firstSpecialGoods;

    /** 分享设置 */
    private String shareConfig;
    private PictorialShareConfigVo shopShareConfig;
}
