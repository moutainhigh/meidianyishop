package com.meidianyi.shop.service.saas.db;

import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.dao.foundation.database.DatabaseManager;
import com.meidianyi.shop.dao.foundation.database.MpDefaultDslContext;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.saas.db.Column;
import com.meidianyi.shop.service.pojo.saas.db.Index;
import com.meidianyi.shop.service.pojo.saas.db.Table;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.exception.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lixinguo
 */
@Service
public class RepairDatabaseService extends MainBaseService {

    @Autowired
    DatabaseManager databaseManager;

    /**
     * 是否只是检查SQL，不执行SQL
     */
    @Getter
    @Setter
    protected Boolean onlyCheck = false;

    /**
     * 修复主库
     */
    public void repairMainDb(Boolean onlyCheck) {
        logger().info("repairMainDb(onlyCheck={}) start.", onlyCheck);
        this.onlyCheck = onlyCheck;
        String sql = Util.loadResource("db/main/db_main.sql");
        List<Table> tables = this.parseSql(sql);
        repairDb(tables, (MpDefaultDslContext) databaseManager.mainDb());
        logger().info("repairMainDb(onlyCheck={}) done.", onlyCheck);
    }

    /**
     * 修复店铺库
     *
     * @param shopId
     */
    public void repairShopDb(Boolean onlyCheck, Integer shopId) {
        logger().info("repairShopDb(onlyCheck={})  shopId:{} start.", onlyCheck, shopId);
        this.onlyCheck = onlyCheck;
        String sql = Util.loadResource("db/shop/db_shop.sql");
        List<Table> tables = this.parseSql(sql);
        databaseManager.switchShopDb(shopId);
        repairDb(tables, databaseManager.currentShopDb());
        logger().info("repairShopDb(onlyCheck={})  shopId:{} done.", onlyCheck, shopId);
    }

    /**
     * 修复所有店铺库
     */
    public void repairAllShopDb(Boolean onlyCheck) {
        logger().info("repairAllShopDb(onlyCheck={}) start.", onlyCheck);
        this.onlyCheck = onlyCheck;
        String sql = Util.loadResource("db/shop/db_shop.sql");
        List<Table> tables = this.parseSql(sql);
        Result<ShopRecord> shops = saas().shop.getAll();
        for (ShopRecord shop : shops) {
            try {
                logger().info("repairDb(onlyCheck={})  shopId:{} ...", onlyCheck, shop.getShopId());
                databaseManager.switchShopDb(shop.getShopId());
                repairDb(tables, databaseManager.currentShopDb());
            } catch (Exception e) {
                logger().error("repairDb(onlyCheck={})  shopId:{} exception: {}", onlyCheck, e.getMessage());
            }
        }
        logger().info("repairAllShopDb(onlyCheck={}) done.", onlyCheck);
    }

    /**
     * 修复数据库所有表字段和索引
     *
     * @param tables
     * @param db
     */
    public void repairDb(List<Table> tables, MpDefaultDslContext db) {
        for (Table table : tables) {
            table.setDatabseName(db.getDbConfig().getDatabase());
            table.setFullTableName(table.getDatabseName() + "." + table.getTableName());
            this.processTable(table, db);
        }

    }

    /**
     * 处理单表
     *
     * @param table
     * @param db
     */
    public void processTable(Table table, MpDefaultDslContext db) {
        List<String> columnSqls = new ArrayList<>();
        List<String> indexSqls = new ArrayList<>();
        List<String> allSqls = new ArrayList<>();
        Index incrementIndex = null;
        if (isTableExists(table, db)) {
            Result<Record> columnRecords = db.fetch("show columns from " + table.getFullTableName());
            for (int i = 0; i < table.columns.size(); i++) {
                String sql = this.processColumn(table, i, columnRecords, db);
                if (StringUtils.isBlank(sql)) {
                    continue;
                }
                // add column auto_increment 单独处理
                if (StringUtils.containsIgnoreCase(sql, "add column")
                    && StringUtils.containsIgnoreCase(sql, "auto_increment")
                    && !StringUtils.containsIgnoreCase(sql, "primary")) {
                    incrementIndex = table.getIndexForAutoIncrement(table.columns.get(i).getField());
                    if (incrementIndex == null) {
                        logger().error("sql:{} index not found ", sql);
                    } else {
                        String primary = "PRIMARY";
                        if (incrementIndex.getKeyName().equals(primary)) {
                            sql = sql + " primary key";
                        } else {
                            sql = sql + " , add index " + incrementIndex.getKeyName() + "("
                                + incrementIndex.getColumnNames().get(0) + ")";
                        }
                        columnSqls.add(sql);
                    }
                } else {
                    columnSqls.add(sql);
                }
            }

            Result<Record> indexRecords = db.fetch("show indexes from " + table.getFullTableName());
            for (int i = 0; i < table.indexes.size(); i++) {
                String sql = this.processIndex(table, i, indexRecords, db);
                if (StringUtils.isBlank(sql)) {
                    continue;
                }
                // 删除索引为高优先级
                if (sql.contains("drop primary key") || sql.contains("drop index")) {
                    String[] sqls = sql.split(";");
                    allSqls.add(sqls[0]);
                    sql = sqls[1];
                }
                if (incrementIndex != null && incrementIndex.getKeyName().equals(table.indexes.get(i).getKeyName())) {
                    continue;
                }
                indexSqls.add(sql);
            }
        } else {
            String sql = table.createSql.replace(table.getTableName(), table.getFullTableName());
            allSqls.add(sql);
        }
        allSqls.addAll(columnSqls);
        allSqls.addAll(indexSqls);
        for (String sql : allSqls) {
            this.executeSql(db, sql);
        }
    }

    protected void executeSql(MpDefaultDslContext db, String sql) {
        try {
            if (this.onlyCheck) {
                logger().info("Check: Need Execute SQL: {}", sql);
                return;
            }
            logger().info("Repair Db, Execute SQL: {}", sql);
            db.execute(sql);
        } catch (DataAccessException e) {
            logger().error("Execute SQL Exception, message: {} ", e.getMessage());
        }
    }

    /**
     * 修复表字段
     *
     * @param table
     * @param colIdx
     * @param records
     * @param db
     * @return
     */
    public String processColumn(Table table, int colIdx, Result<Record> records, MpDefaultDslContext db) {
        Column col = table.columns.get(colIdx);
        boolean found = false;
        String regex0 = "(\\w+)\\((\\d+)\\)\\s+unsigned";
        String regex1 = "(\\w+)\\((\\d+),(\\d+)\\)";
        String regex2 = "(\\w+)\\((\\d+)\\)";
        String regex3 = "(\\w+)";
        String sql = "";
        for (Record r : records) {
            if (Column.equalField(r.get("Field").toString(), col.getField())) {
                found = true;
                String type = r.get("Type").toString();
                Column colFromDb = new Column();
                colFromDb.setField(col.getField());
                colFromDb.setNullType(r.get("Null").toString());
                colFromDb.setDefaultValue(r.get("Default") == null ? null : r.get("Default").toString());
                Matcher m;
                if ((m = Pattern.compile(regex0, Pattern.CASE_INSENSITIVE).matcher(type)).find()) {
                    colFromDb.setType(m.group(1));
                    colFromDb.setTypeRange1(m.group(2));
                    colFromDb.setTypeUnsigned("unsigned");
                } else if ((m = Pattern.compile(regex1, Pattern.CASE_INSENSITIVE).matcher(type)).find()) {
                    colFromDb.setType(m.group(1));
                    colFromDb.setTypeRange1(m.group(2));
                    colFromDb.setTypeRange2(m.group(3));
                } else if ((m = Pattern.compile(regex2, Pattern.CASE_INSENSITIVE).matcher(type)).find()) {
                    colFromDb.setType(m.group(1));
                    colFromDb.setTypeRange1(m.group(2));
                } else if ((m = Pattern.compile(regex3, Pattern.CASE_INSENSITIVE).matcher(type)).find()) {
                    colFromDb.setType(m.group(1));
                }

                // 默认精度处理
                if (!StringUtils.isBlank(colFromDb.getTypeRange1()) && StringUtils.isBlank(col.getTypeRange1())) {
                    col.setTypeRange1(colFromDb.getTypeRange1());
                }
                if (!StringUtils.isBlank(colFromDb.getTypeRange2()) && StringUtils.isBlank(col.getTypeRange2())) {
                    col.setTypeRange2(colFromDb.getTypeRange2());
                }

                if (!Column.isEquals(col, colFromDb)) {
                    // 列不同,则进行字段修改
                    sql = "alter table " + table.getFullTableName() + " modify column " + col.getCreateSql();
                }
                break;

            }
        }
        if (!found) {
            sql = "alter table " + table.getFullTableName() + " add column " + col.getCreateSql();
        }
        return sql;

    }

    /**
     * 修复表索引
     *
     * @param table
     * @param indexIdx
     * @param records
     * @param db
     * @return
     */
    public String processIndex(Table table, int indexIdx, Result<Record> records, MpDefaultDslContext db) {
        Index index = table.indexes.get(indexIdx);
        int findIndexCols = 0;
        boolean findKeyName = false;
        String sql = "";
        for (Record r : records) {
            if (Column.equalField(r.get("Key_name").toString(), index.getKeyName())) {
                findKeyName = true;
                String col = r.get("Column_name").toString();
                for (String indexCol : index.getColumnNames()) {
                    if (Column.equalField(indexCol, col)) {
                        findIndexCols++;
                    }
                }
            }
        }

        if (findKeyName) {
            if (findIndexCols != index.getColumnNames().size()) {
                sql = indexSql(index, table.getFullTableName(), true);
            }
        } else {
            sql = indexSql(index, table.getFullTableName(), false);
        }
        return sql;
    }

    /**
     * 索引语句生成
     *
     * @param index
     * @param tableName
     * @param modify
     * @return
     */
    protected String indexSql(Index index, String tableName, boolean modify) {
        String format = "alter table " + tableName + " add %s key %s (%s)";
        String keyProp = "";
        String primary = "PRIMARY";
        String unique = "0";

        StringBuffer buf = new StringBuffer();
        int size = index.getColumnNames().size();
        for (int i = 0; i < size; i++) {
            buf.append(index.getColumnNames().get(i));
            String subPart = index.getColumnSubPart().get(i);
            if (subPart != null) {
                buf.append("(").append(subPart).append(")");
            }
            if (i != size - 1) {
                buf.append(",");
            }
        }
        String cols = buf.toString();

        if (modify) {
            if (primary.equals(index.getKeyName())) {
                format = "alter table " + tableName + " drop primary key , add %s key %s (%s)";
            } else {
                format = "alter table " + tableName + " drop index " + index.getKeyName() + ", add %s key %s (%s)";
            }
        }

        String keyName = index.getKeyName();
        if (primary.equals(keyName)) {
            keyProp = "primary";
            keyName = "";
        } else if (unique.equals(index.getNonUnique())) {
            keyProp = "unique";
        }
        return String.format(format, keyProp, keyName, cols);
    }

    /**
     * 判断表是否存在
     *
     * @param table
     * @param db
     * @return
     */
    public boolean isTableExists(Table table, MpDefaultDslContext db) {
        Result<Record> tables = db.fetch("show tables from " + table.getDatabseName() + " like '"
            + getNoQuotStr(table.getTableName(), "`") + "'");
        return tables.size() > 0;
    }

    protected String getNoQuotStr(String str, String quot) {
        return str.startsWith(quot) && str.endsWith(quot) ? str.substring(1, str.length() - 1) : str;
    }

    /**
     * 解析SQL文件，分析出表 字段 和 索引
     *
     * @param sql
     * @return
     */
    public List<Table> parseSql(String sql) {
        return this.parseSql2(sql);
//		
//		List<Table> tables = new ArrayList<Table>();
////		sql = sql.replaceAll("`", "");
//		String createTableRegex = "create\\s+table\\s+(.*?)\\s*\\((.*?)\n\\s*\\)[^\\)]*?;";
//		Pattern pattern = Pattern.compile(createTableRegex,
//				Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
//		Matcher matcher = pattern.matcher(sql);
//		while (matcher.find()) {
//			Table table = new Table();
//			table.tableName = matcher.group(1);
////			if(!"b2c_presale".equals(table.tableName)) {
////				continue;
////			}
//			table.createSql = String.format("create table %s(%s)", matcher.group(1), matcher.group(2));
//			String[] columns = matcher.group(2).split(",\\s*\n");
//			for (String col : columns) {
//				if (StringUtils.isBlank(col.trim())) {
//					continue;
//				}
//				String[] tokens = this.parseTokens(col).toArray(new String[0]);
//				if (tokens.length == 0) {
//					logger().error("tokens.length == 0");
//					continue;
//				}
//				if (isIndex(tokens[0])) {
//					this.parseIndex(tokens, table, col.trim());
//				} else {
//					this.parseColumn(tokens, table, col.trim());
//				}
//			}
//			tables.add(table);
//		}
//
//		return tables;
    }

    public List<Table> parseSql2(String sql) {
        List<Table> tables = new ArrayList<Table>();
        String[] lines = sql.split("\n");

        String createTableRegex = "create\\s+table\\s+(.*?)\\s*\\(";
        int i = 0;
        int count = lines.length;
        while (i < count) {
            String line = lines[i];
            Pattern pattern = Pattern.compile(createTableRegex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                Table table = new Table();
                table.tableName = matcher.group(1);
                StringBuffer buf = new StringBuffer();
                buf.append(line).append("\n");
                i++;
                List<String> columns = new ArrayList<>();
                while (i < count) {
                    buf.append(lines[i]).append("\n");
                    line = lines[i].trim();
                    i++;
                    if (line.endsWith(";")) {
                        break;
                    } else {
                        if (line.endsWith(",")) {
                            line = line.substring(0, line.length() - 1);
                        }
                        columns.add(line);
                    }
                }
                table.createSql = buf.toString();
                for (String col : columns) {
                    if (StringUtils.isBlank(col.trim())) {
                        continue;
                    }
                    String[] tokens = this.parseTokens(col).toArray(new String[0]);
                    if (tokens.length == 0) {
                        logger().error("tokens.length == 0");
                        continue;
                    }
                    if (isIndex(tokens[0])) {
                        this.parseIndex(tokens, table, col.trim());
                    } else {
                        this.parseColumn(tokens, table, col.trim());
                    }
                }
                tables.add(table);
            } else {
                i++;
            }

        }
        return tables;
    }

    protected List<String> parseTokens(String str) {
        List<String> result = new ArrayList<>();
        str = str.trim();
        int i = 0;
        StringBuffer buf = new StringBuffer();
        while (i < str.length()) {
            char ch = str.charAt(i);
            if (ch == '"' || ch == '\'' || ch == '(') {
                bufAddAndReset(result, buf);

                char lastCh = ch == '(' ? ')' : ch;
                buf.append(ch);
                i++;
                int leftBracketCount = 0;
                while (i < str.length()) {
                    if (ch == '(' && str.charAt(i) == '(') {
                        leftBracketCount++;
                    }
                    buf.append(str.charAt(i));
                    if (str.charAt(i) == lastCh) {
                        if (leftBracketCount == 0) {
                            break;
                        }
                        if (leftBracketCount > 0) {
                            leftBracketCount--;
                        }
                    }
                    i++;
                }
                bufAddAndReset(result, buf);
            } else {
                if (!String.valueOf(ch).matches("\\s")) {
                    buf.append(ch);
                } else {
                    bufAddAndReset(result, buf);
                }
            }
            i++;
        }
        bufAddAndReset(result, buf);
        return result;
    }

    protected void bufAddAndReset(List<String> result, StringBuffer buf) {
        if (buf.length() > 0) {
            result.add(buf.toString());
            buf.setLength(0);
        }
    }

    /**
     * 解析表字段
     *
     * @param tokens
     * @param table
     * @param sql
     */
    public void parseColumn(String[] tokens, Table table, String sql) {

        int len = tokens.length;
        Column col = new Column();
        col.setField(tokens[0]);
        col.setCreateSql(sql);

        int i = 1;
        while (i < len) {

            if (i == 1) {
                col.setType(tokens[i]);
                i++;
                if (i < len && tokens[i].startsWith("(") && tokens[i].endsWith(")")) {
                    String[] props = tokens[i].substring(1, tokens[i].length() - 1).replaceAll("\\s", "").split(",");
                    col.setTypeRange1(props[0]);
                    col.setTypeRange2(props.length > 1 ? props[1] : "");
                    if (StringUtils.equals("timestamp", tokens[i])) {
                        col.setTypeRange1("");
                    }
                    i++;
                }
                continue;
            }
            i = processTokens(tokens, table, len, col, i);

        }
        table.columns.add(col);
    }

    private int processTokens(String[] tokens, Table table, int len, Column col, int i) {
        // process other
        switch (tokens[i].toUpperCase()) {
            case "NOT": {
                col.setNullType("NO");
                i += 2;
                break;
            }
            case "UNSIGNED": {
                col.setTypeUnsigned("unsigned");
                i++;
                break;
            }
            case "CHARACTER":
            case "NULL":
            case "AUTO_INCREMENT": {
                i++;
                break;
            }
            case "COMMENT": {
                i += 2;
                break;
            }
            case "COLLATE": {
                i += 2;
                break;
            }
            case "SET": {
                i += 2;
                break;
            }
            case "ON": {
                i += 3;
                if (i < len && tokens[i].startsWith("(")) {
                    i++;
                }
                break;
            }
            case "DEFAULT": {
                i++;
                if (tokens[i].startsWith("\"") || tokens[i].startsWith("'")) {
                    col.setDefaultValue(tokens[i].substring(1, tokens[i].length() - 1));
                } else {
                    String strNull = "null";
                    col.setDefaultValue(tokens[i].equalsIgnoreCase(strNull) ? null : tokens[i]);
                    if (i + 1 < len && tokens[i + 1].startsWith("(")) {
                        col.setDefaultValue(tokens[i] + tokens[i + 1]);
                        i++;
                    }
                }
                i++;
                break;
            }
            default: {
                logger().warn("{} column tokens{}, i={},{} not found!", table.getTableName(), tokens.toString(),
                    tokens[i]);
                i++;
            }
        }
        return i;
    }

    /**
     * 解析表索引
     *
     * @param tokens
     * @param table
     * @param sql
     */
    public void parseIndex(String[] tokens, Table table, String sql) {
        int len = tokens.length;
        Index index = new Index();
        index.setCreateSql(sql);

        int i = 0;
        while (i < len) {

            switch (tokens[i].toUpperCase()) {
                case "PRIMARY": {
                    index.setKeyName("PRIMARY");
                    index.setNonUnique("0");
                    i += 1;
                    break;

                }
                case "UNIQUE": {
                    index.setNonUnique("0");
                    i += 1;
                    break;
                }

                case "KEY":
                case "INDEX": {
                    i++;
                    if (!tokens[i].startsWith("(")) {
                        index.setKeyName(tokens[i]);
                        i++;
                    }
                    if (tokens[i].startsWith("(") && tokens[i].endsWith(")")) {
                        String[] cols = tokens[i].substring(1, tokens[i].length() - 1).replaceAll("\\s", "").split(",");
                        if (StringUtils.isBlank(index.getKeyName())) {
                            index.setKeyName(cols[0]);
                        }
                        for (int j = 0; j < cols.length; j++) {
                            int p = cols[j].indexOf('(');
                            if (p != -1) {
                                index.getColumnNames().add(cols[j].substring(0, p));
                                index.getColumnSubPart().add(cols[j].substring(p + 1, cols[j].length() - 1));
                            } else {
                                index.getColumnNames().add(cols[j]);
                                index.getColumnSubPart().add(null);
                            }
                        }
                        i++;
                    }
                    break;
                }
                case "USING": {
                    i += 2;
                    break;
                }
                default: {
                    logger().warn("{} found unkown index token {}.", table.getTableName(), tokens[i]);
                    i++;

                }
            }
        }
        table.indexes.add(index);
    }

    /**
     * 判断串是否双索引语句
     *
     * @param key
     * @return
     */
    protected boolean isIndex(String key) {
        return (StringUtils.equalsAnyIgnoreCase(key, "primary", "key", "unique", "index", "constraint"));
    }

}
