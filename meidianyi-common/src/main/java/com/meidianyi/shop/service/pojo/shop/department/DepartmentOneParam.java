package com.meidianyi.shop.service.pojo.shop.department;

import lombok.Data;

/**
 * @author chenjie
 */
@Data
public class DepartmentOneParam {
    private Integer id;
    /**
     * 科室代码
     */
    private String code;
    /**
     * 科室名称
     */
    private String name;
    /**
     * 父节点ID
     */
    private Integer parentId;
    /**
     * 层级
     */
    private Integer level;
    /**
     * 是否叶子节点
     */
    private byte isLeaf;
    /**
     * 父节点集合
     */
    private String parentIds;

    /**
     * 删除状态
     */
    private Byte isDelete=0;

    /**
     * 优先级
     */
    private Integer   first=0;
}
