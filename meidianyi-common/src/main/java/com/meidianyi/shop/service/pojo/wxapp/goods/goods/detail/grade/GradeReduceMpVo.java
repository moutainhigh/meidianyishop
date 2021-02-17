package com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.grade;

import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.GoodsActivityDetailMp;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

/**
 * 等级会员价和限时降价同时存在时使用的数据类
 * @author 李晓冰
 * @date 2020年01月08日
 */
@Getter
@Setter
public class GradeReduceMpVo extends GoodsActivityDetailMp {
    /**活动开始时间点,用于预热使用*/
    private Timestamp nextStartTimestamp;
    /**活动结束时间点*/
    private Timestamp currentEndTimestamp;

    /**是否限购 */
    private Boolean isLimit;
    /**限购数量*/
    private Integer limitAmount;
    /**是否进行超购限制*/
    private Boolean limitFlag;
    /**对应的规格数据*/
    List<GradeReducePrdMpVo> gradeReducePrdVos;
}
