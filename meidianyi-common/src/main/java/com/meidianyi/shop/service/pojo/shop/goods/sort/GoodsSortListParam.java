package com.meidianyi.shop.service.pojo.shop.goods.sort;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * 分类列表查询数据入参
 * @author 李晓冰
 * @date 2019年07月02日
 */
@Data
public class GoodsSortListParam {
    /** 分类名称*/
    private String sortName;
    /** 父节点id*/
    private Integer parentId;
    /** 分类类型 0 普通 1 推荐*/
    private Byte type;
    private Timestamp startCreateTime;
    private Timestamp endCreateTime;

    private List<Integer> sortIds;
}