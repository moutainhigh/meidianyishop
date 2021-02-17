package com.meidianyi.shop.service.pojo.shop.market.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author liufei
 * @date 2019/8/9
 */
@Data
public class FeedBackStatisticsVo {
    /**   表单参与用户数 */
    private Integer participantsNum;
    /**   表单参与人数/反馈数 */
    private Integer submitNum;
    private Byte isForeverValid;
    private Timestamp startTime;
    private Timestamp endTime;
    private Byte state;
    @JsonIgnore
    private String pageContent;


    private List<FeedBackOneVo> oneVo;

}
