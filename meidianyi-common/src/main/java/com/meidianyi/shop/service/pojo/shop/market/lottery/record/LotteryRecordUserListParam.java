package com.meidianyi.shop.service.pojo.shop.market.lottery.record;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author 孔德成
 * @date 2019/8/6 14:32
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class LotteryRecordUserListParam {

    private String mobile;
    private String userName;
    /**
     * 邀请人
     */
    private String inviteUserName;

    /**
     * 营销活动主键
     */
    @NotNull
    private Integer activityId;

    /**
     * 	分页信息
     */
    private Integer currentPage ;
    private Integer pageRows ;


}
