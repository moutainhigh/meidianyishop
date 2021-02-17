package com.meidianyi.shop.service.pojo.shop.distribution;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 更新分销员等级参数
 * @author 王帅
 */
@Data
@AllArgsConstructor
public class UpdateUserLevel {
    private Integer userId;
    private Byte isGoUp;
    private Byte oldLevel;
    private String oldLevelName;
    private Byte newLevel;
    private String newLevelName;
    private String updateNote;
}
