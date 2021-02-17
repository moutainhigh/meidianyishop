package com.meidianyi.shop.service.pojo.shop.market.message;

import java.util.List;

import com.meidianyi.shop.service.pojo.saas.shop.mp.MpAuthShopListVo;

import lombok.Data;
import lombok.ToString;

/**
 * 
 * @author zhaojianqiang
 *
 *         2019年9月6日 下午6:13:40
 */
@Data
@ToString
public class BatchUploadCodeParam {

	private Integer templateId;
	private List<MpAuthShopListVo> list;
	private Integer recId;
	private Byte packageVersion;
}
