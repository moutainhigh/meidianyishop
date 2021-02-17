package com.meidianyi.shop.service.pojo.shop.overview.analysis;

import lombok.Data;

/**
 * @author liangchen
 * @date  2019年7月26日
 */
@Data
public class OneDayAgoVo {
    /** 打开次数 */
    private Integer sessionCnt;
    /** 访问次数 */
    private Integer visitPv;
    /** 访问人数 */
    private Integer visitUv;
    /** 新用户数 */
    private Integer visitUvNew;
    /** 转发次数 */
    private Integer sharePv;
    /** 转发人数 */
    private Integer shareUv;
}
