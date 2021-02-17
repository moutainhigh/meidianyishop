package com.meidianyi.shop.service.pojo.shop.member.userimp;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件上传，队列用
 *
 * @author zhaojianqiang
 * @time 下午4:34:06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserImportMqParam {
	private List<com.meidianyi.shop.service.pojo.shop.member.userimp.UserImportPojo> models;
	private String lang;
	private Integer shopId;
	private String cardId;
	private Integer groupId;
	private Integer tagId;
	/**
	 * 任务id
	 */
	private Integer taskJobId;
}
