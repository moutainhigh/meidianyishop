package com.meidianyi.shop.service.pojo.shop.overview.commodity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Date;
import java.time.LocalDate;

/**
 * @author liufei
 * @date 2/10/2020
 */
@Data
public class RankingParam {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date endTime;
    /**   0天，1月，2年 3周 */
    private Byte unit;

    /**   0 商品销售额；1 商品销售订单 */
    private Byte flag;

    /**
     * 默认获取30天的数据
     */
    public RankingParam defaultValue() {
        LocalDate now = LocalDate.now();
        this.endTime = Date.valueOf(now);
        this.startTime = Date.valueOf(now.minusDays(30));
        return this;
    }

}
