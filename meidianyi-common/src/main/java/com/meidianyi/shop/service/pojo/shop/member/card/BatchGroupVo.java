package com.meidianyi.shop.service.pojo.shop.member.card;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获得生成/导入记录
 * @author zhaojianqiang
 * @time   下午3:22:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchGroupVo {
	private Integer batchId;
	private Integer successNum;
	private Integer failNum;
	private String batchName;
	private Timestamp createTime;
	/** 是否是带密码的 */
	private Boolean isPwd;
}
