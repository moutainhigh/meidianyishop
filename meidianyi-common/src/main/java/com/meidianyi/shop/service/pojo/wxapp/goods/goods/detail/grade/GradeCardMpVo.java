package com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.grade;

import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.GoodsActivityDetailMp;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 等级会员卡-变价信息
 * @author 李晓冰
 * @date 2020年01月08日
 */
@Setter
@Getter
public class GradeCardMpVo extends GoodsActivityDetailMp {
    List<GradePrdMpVo> gradePrdMpVos;
}
