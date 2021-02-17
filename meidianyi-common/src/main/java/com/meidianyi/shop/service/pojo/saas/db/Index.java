package com.meidianyi.shop.service.pojo.saas.db;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;

/**
 * 
 * @author lixinguo
 *
 */
@Data
public class Index {
	/**
	 * 索引名称
	 */
	String keyName;

	/**
	 * 是否为唯一索引：0否，1是
	 */
	String nonUnique;
	

	/**
	 * 索引列
	 */
	List<String> columnNames = new ArrayList<String>();
	
	/**
	 * 索引列subPart
	 */
	List<String> columnSubPart = new ArrayList<String>();
	
	/**
	 * 当前索引SQL语句
	 */
	String createSql;

	@Override
	public String toString() {
		return "keyName=" + keyName + ", nonUnique=" + nonUnique + ",columnNames=" + columnNames.toString();
	}

	/**
	 * 两个索引是否相同
	 * 
	 * @param i1
	 * @param i2
	 * @return
	 */
	static public boolean isEquals(Index i1, Index i2) {
		boolean result = StringUtils.equalsIgnoreCase(i1.getKeyName(), i2.getKeyName())
				&& StringUtils.equalsIgnoreCase(i1.getNonUnique(), i2.getNonUnique())
				&& i1.getColumnNames().size() == i2.getColumnNames().size();
		if (result) {
			for (int i = 0; i < i1.getColumnNames().size(); i++) {
				if (!StringUtils.equalsIgnoreCase(i1.getColumnNames().get(i), i2.getColumnNames().get(i))) {
					return false;
				}
				if (!StringUtils.equalsIgnoreCase(i1.getColumnSubPart().get(i), i2.getColumnSubPart().get(i))) {
					return false;
				}
			}
		}
		return result;
	}

}
