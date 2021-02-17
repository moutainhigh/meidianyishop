package com.meidianyi.shop.service.pojo.shop.store.group;

import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author: 卢光耀
 * @date: 2019-07-11 09:49
 *
*/
@Getter
@Setter
public class StoreGroupQueryParam extends BasePageParam {

    private String groupName;

    private boolean needAccurateQuery = Boolean.FALSE;

    private Integer groupId;
}
