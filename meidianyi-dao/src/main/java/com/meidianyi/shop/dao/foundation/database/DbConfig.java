package com.meidianyi.shop.dao.foundation.database;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author 新国
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DbConfig {
	public String host;
	public Integer port;
	public String database;
	public String username;
	public String password;

	public String getDatasourceKey() {
		return String.format("%s:port_%s_%s_%s", host, port, username, password);
	}
}
