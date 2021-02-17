package com.meidianyi.shop.service.pojo.shop.goods.comment;

import lombok.Data;
import java.sql.Timestamp;;
/**
 * @author liangchen
 * @date   2019年7月9日
 */

@Data
public class GoodsCommentConfigParam {
	private Integer recId;
	private String k;
	private String v;
	private Timestamp createTime;
	private Timestamp updateTime;
}
