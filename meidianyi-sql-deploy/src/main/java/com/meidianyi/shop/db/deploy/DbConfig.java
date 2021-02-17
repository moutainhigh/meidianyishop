package com.meidianyi.shop.db.deploy;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 新国
 */
@Data
@NoArgsConstructor
public class DbConfig {
    public String host;
    public Integer port;
    public String database;
    public String username;
    public String password;
    public Boolean isMainDb = false;

    public DbConfig(String host, Integer port, String database, String username, String password, Boolean isMainDb) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.isMainDb = isMainDb;
    }

    public String getDatasourceKey() {
        return String.format("%s:port_%s_%s_%s", host, port, username, password);
    }

}
