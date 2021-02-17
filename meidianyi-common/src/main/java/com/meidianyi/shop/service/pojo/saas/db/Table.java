package com.meidianyi.shop.service.pojo.saas.db;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 
 * @author lixinguo
 *
 */
@Data
public class Table {
	public String databseName;
	public String tableName;
	public String fullTableName;
	public List<Column> columns = new ArrayList<Column>();
	public List<Index> indexes = new ArrayList<Index>();
	public String createSql;

	public Index getIndexForAutoIncrement(String field) {
		for (Index index : this.indexes) {
			if(index.getColumnNames().size() == 1) {
				for (String col: index.columnNames){
					if(Column.equalField(col, field)) {
						return index;
					}
				}
			}
		}
		return null;
	}
}
