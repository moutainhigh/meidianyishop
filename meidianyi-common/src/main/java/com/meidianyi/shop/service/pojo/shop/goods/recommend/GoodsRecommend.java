package com.meidianyi.shop.service.pojo.shop.goods.recommend;

import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsView;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author 黄荣刚
 * @date 2019年7月9日
 */
@Data
public class GoodsRecommend {
  /** 全部商品 */
  public static final Byte ALLTYPE = 0;

  /** 指定商品 */
  public static final Byte PARTTYPE = 1;

  /** 激活状态 */
  public static final Byte STATUS_ACTIVE = 0;

  /** 暂时停用状态 */
  public static final Byte STATUS_PAUSE = 1;

  /** 常规推荐 */
  public static final Byte NORMAL_RECOMMENDATION = 0;

  /** 智能推荐 */
  public static final Byte INTELIGENT_RECOMMENDATION = 1;

  /** 商品、商家分类、平台分类 ID分隔符 */
  public static final String DELIMITER = ",";

  private Integer id;
  private String recommendName;

  /** 推荐类型 0普通推荐 1智能推荐 */
  private Byte chooseType;
  /** 推荐商品数量 */
  private Integer recommendNumber;

  /** 类型：全部商品、指定商品 */
  private Byte recommendType;

  /** 当类型为指定商品时 选择的商品列表集合 */
  private List<GoodsView> recommendGoods;
  /** 当类型为指定商品时 选择的商家分类列表ID集合 */
  private List<Integer> recommendSortIds;
  /** 当类型为指定商品时 选择的平台分类列表ID集合 */
  private List<Integer> recommendCatIds;

  /** 应用页面 */
  private List<String> recommendUsePage;

  /** 启用状态 */
  private Byte status;

  /** 删除标志 1表示已删除 */
  private Byte delFlag;

  private Timestamp createTime;

  private Timestamp updateTime;
}
