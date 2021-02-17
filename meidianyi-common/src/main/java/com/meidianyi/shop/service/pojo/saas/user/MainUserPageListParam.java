package com.meidianyi.shop.service.pojo.saas.user;

import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 主库用户查询分页表
 * @author 李晓冰
 * @date 2020年08月17日
 */
@Data
public class MainUserPageListParam extends BasePageParam {
    private Integer shopId;
    private String mobile;
    private String username;
    private Timestamp startTime;
    private Timestamp endTime;
}
