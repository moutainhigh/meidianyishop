package com.meidianyi.shop.common.foundation.excel.bean;

import lombok.Data;

/**
 * 
 * @author zhaojianqiang
 *
 *         2019年9月6日 下午2:40:06
 */
@Data
public class ClassList {
	/**
	 * 类  比如/mp-service/src/test/java/com/vpu/mp/controller/excel/OrderListInfo.java
	 */
	private Class<?> upClazz;
	/**
	 * 内部类 比如OrderListInfo类中的OrderGoods
	 */
	private Class<?> innerClazz;

}
