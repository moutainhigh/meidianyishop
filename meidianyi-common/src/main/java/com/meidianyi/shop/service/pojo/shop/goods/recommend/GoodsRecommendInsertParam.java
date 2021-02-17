package com.meidianyi.shop.service.pojo.shop.goods.recommend;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;

import java.util.List;

/**
 * @author 黄荣刚
 * @date 2019年7月9日
 */
@Data
public class GoodsRecommendInsertParam {

  @NotBlank(message = JsonResultMessage.GOODS_RECOMMEND_NAME_NOT_NULL)
  private String recommendName;

  /** 类型：全部商品、指定商品 */
  @NotNull(message = JsonResultMessage.GOODS_RECOMMEND_TYPE_NOT_NULL)
  private Byte recommendType;

  /** 推荐类型 0普通推荐 1智能推荐 */
  @NotNull(message = JsonResultMessage.GOODS_RECOMMEND_CHOOSE_TYPE_NOT_NULL)
  private Byte chooseType;

  /** 推荐商品数量 */
  @NotNull(message = JsonResultMessage.GOODS_RECOMMEND_NUMBER_NOT_NULL)
  private Integer recommendNumber;

  private Byte status = GoodsRecommend.STATUS_ACTIVE;

  /** 当类型为指定商品时 选择的商品列表集合 */
  private List<Integer> recommendGoods;
  /** 当类型为指定商品时 选择的商家分类列表ID集合 */
  private List<Integer> recommendSortIds;
  /** 当类型为指定商品时 选择的平台分类列表ID集合 */
  private List<Integer> recommendCatIds;

  /** 应用页面 */
  private List<String> recommendUsePage;
}
