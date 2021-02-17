package com.meidianyi.shop.service.pojo.shop.overview.useranalysis;

import lombok.Data;

import java.util.List;

/**
 * 客户复购趋势
 *
 * @author liangchen
 * @date 2019年7月22日
 */
@Data
public class RebuyVo {
  List<RebuyWeekVo> rebuyWeekVo;
}
