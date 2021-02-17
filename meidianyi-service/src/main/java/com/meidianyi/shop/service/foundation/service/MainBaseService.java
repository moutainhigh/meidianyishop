package com.meidianyi.shop.service.foundation.service;

import java.sql.Timestamp;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.excel.ExcelWriter;
import com.meidianyi.shop.dao.foundation.base.MainBaseDao;
import com.meidianyi.shop.service.saas.SaasApplication;
import com.meidianyi.shop.service.wechat.OpenPlatform;
import org.apache.poi.ss.usermodel.Workbook;
import org.jooq.Configuration;
import org.jooq.ContextTransactionalRunnable;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.SQLDataType;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author lixinguo
 *
 */
public class MainBaseService extends MainBaseDao {

    @Autowired
    protected OpenPlatform open;

    @Autowired
    protected SaasApplication saas;


    protected OpenPlatform open() {
        return open;
    }

    protected SaasApplication saas() {
        return saas;
    }

    protected <T> Workbook export(List<T> list, Class<T> clazz){
        Workbook workbook = ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(workbook);
        excelWriter.writeModelList(list, clazz);
        return workbook;
    }
}
