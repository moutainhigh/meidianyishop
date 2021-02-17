package com.meidianyi.shop.service.foundation.service;

import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.excel.ExcelWriter;
import com.meidianyi.shop.config.DomainConfig;
import com.meidianyi.shop.dao.foundation.base.StoreBaseDao;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.service.pojo.shop.auth.StoreTokenAuthInfo;
import com.meidianyi.shop.service.saas.SaasApplication;
import com.meidianyi.shop.service.wechat.OpenPlatform;
import org.apache.poi.ss.usermodel.Workbook;
import org.jooq.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * @author chenjie
 * @date 2020年08月24日
 */
public class StoreBaseService extends StoreBaseDao {
    /**
     * Shop DB连接事务配置，线程内单例
     */
    private static ThreadLocal<Deque<Configuration>> shopDbConfiguration = ThreadLocal.withInitial(ArrayDeque<Configuration>::new);

    /**
     * 当前登录用户信息，线程单例
     */
    private static ThreadLocal<StoreTokenAuthInfo> currentStoreLoginUser = new ThreadLocal<>();

    /**
     * 当前线程设置当前登录用户
     *
     * @param user
     */
    public static void setCurrentStoreLoginUser(StoreTokenAuthInfo user) {
        currentStoreLoginUser.set(user);
    }

    /**
     * 溢出当前登录用户
     */
    public static void removeCurrentStoreLoginUser() {
        currentStoreLoginUser.remove();
    }

    /**
     * 当前线程得到当前登录用户
     *
     * @return
     */
    public static StoreTokenAuthInfo getCurrentStoreLoginUser() {
        return currentStoreLoginUser.get();
    }


    /**
     * 当前店铺对于SysId
     *
     * @return
     */
    public Integer getSysId() {
        ShopRecord shop = saas.shop.getShopById(this.getShopId());
        return shop == null ? 0 : shop.getSysId();
    }

    @Autowired
    private DomainConfig domainConfig;

    public String imageUrl(String path) {
        return domainConfig.imageUrl(path);
    }

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

    protected <T> Workbook export(List<T> list, Class<T> clazz) {
        Workbook workbook = ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(workbook);
        excelWriter.writeModelList(list, clazz);
        return workbook;
    }
}

