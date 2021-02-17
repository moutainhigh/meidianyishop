package com.meidianyi.shop.service.pojo.saas.db;

import org.apache.commons.lang3.StringUtils;

import com.meidianyi.shop.common.foundation.util.Util;

import lombok.Data;

/**
 * 
 * @author lixinguo
 *
 */
@Data
public class Column {
	/**
	 * 字段名
	 */
	String field;

	/**
	 * 类型
	 */
	String type;

	String typeRange1 = "";

	String typeRange2 = "";

	String typeUnsigned = "";

	/**
	 * 是否可以为NULL： YES NO
	 */
	String nullType = "YES";

	/**
	 * 默认值
	 */
	String defaultValue = null;

	/**
	 * 当前列SQL语句
	 */
	String createSql;

	@Override
	public String toString() {
		return "field=" + field + ", type=" + type + ", typeRange1=" + typeRange1 + ", typeRange2=" + typeRange2
				+ ", nullType=" + nullType + ", defaultValue=" + defaultValue;
	}

	static public boolean isEquals(Column c1, Column c2) {
		return equalField(c1.getField(), c2.getField())
				&& StringUtils.equalsIgnoreCase(c1.getType(), c2.getType())
				&& StringUtils.equalsIgnoreCase(c1.getTypeRange1(), c2.getTypeRange1())
				&& StringUtils.equalsIgnoreCase(c1.getTypeRange2(), c2.getTypeRange2())
				&& StringUtils.equalsIgnoreCase(c1.getTypeUnsigned(), c2.getTypeUnsigned())
				&& isDefaultValueEqual(c1.getType(),c1.getDefaultValue(),c2.getDefaultValue());
	}
	
	static public boolean equalField(String v1,String v2) {
		v1 = v1.startsWith("`") ? v1 : "`"+v1+"`";
		v2 = v2.startsWith("`") ? v2 : "`"+v2+"`";
		return StringUtils.equalsIgnoreCase(v1,v2);
	}
	
	static public boolean isIntType(String type) {
		return StringUtils.equalsAnyIgnoreCase(type,"TINYINT","SMALLINT","MEDIUMINT","INT","BIGINT","INTEGER");
	}
	
	static public boolean isDecimalType(String type) {
		return StringUtils.equalsAnyIgnoreCase(type,"DEC","DECIMAL","NUMERIC");
	}
	
	static public boolean isFloatType(String type) {
		return StringUtils.equalsAnyIgnoreCase(type,"FLOAT","DOUBLE","REAL","FIXED");
	}
	
	static public boolean isBitType(String type) {
		return StringUtils.equalsAnyIgnoreCase(type,"BIT");
	}
	
	static public boolean isDateType(String type) {
		return StringUtils.equalsAnyIgnoreCase(type,"DATE","TIME","DATETIME","TIMESTAMP","YEAR");
	}
	
	static public boolean isStringType(String type) {
		return StringUtils.equalsAnyIgnoreCase(type,"CHAR","VARCHAR","BINARY","VARBINARY","BLOB","TEXT","ENUM","SET");
	}
	
	static public boolean isDefaultValueEqual(String type,String v1,String v2) {
		if(StringUtils.equalsAnyIgnoreCase(v1,v2)) {
			return true;
		}
		if(isIntType(type) && v1!=null && v2!=null) {
			Integer i1 = Util.convert(v1, Integer.class, -1);
			Integer i2 = Util.convert(v2, Integer.class, -2);
			return i1.equals(i2);
		}
        boolean canConvertFloat = (isDecimalType(type) || isFloatType(type)) && v1 != null && v2 != null;
        if(canConvertFloat) {
			Double i1 = Util.convert(v1, Double.class, -1.0);
			Double i2 = Util.convert(v2, Double.class, -2.0);
			return i1.equals(i2);
		}
		
		if(isDateType(type)) {
			if(StringUtils.equalsAnyIgnoreCase(v1, "current_timestamp", "now()","now(0)","current_timestamp()","current_timestamp(0)")
					&& StringUtils.equalsAnyIgnoreCase(v2, "current_timestamp", "now()","now(0)","current_timestamp()","current_timestamp(0)")) {
				return true;
			}
		}
		return false;
	}

}
