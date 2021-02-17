package com.meidianyi.shop.service.pojo.shop.overview.analysis;

import lombok.Data;

/**
 * @author liangchen
 * @date  2019.12.10
 */
@Data
public class YesterdayVo {
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
