package com.meidianyi.shop.service.shop.config;

import com.meidianyi.shop.service.pojo.shop.config.SuspendWindowConfig;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * 悬浮窗配置
 *
 * @author: 王兵兵
 * @create: 2020-05-08 17:18
 **/
@Service
public class SuspendWindowConfigService extends BaseShopConfigService {
    /**
     * 悬浮窗配置草稿
     * value是json格式
     */
    final public static String K_SUSPEND_CFG_DRAFT = "suspend_cfg_draft";

    /**
     * 悬浮窗配置
     * value是json格式
     */
    final public static String K_SUSPEND_CFG = "suspend_cfg";

    /**
     * @return
     */
    public SuspendWindowConfig getSuspendCfgDraft() {
        return this.getJsonObject(K_SUSPEND_CFG_DRAFT, SuspendWindowConfig.class, null);
    }

    /**
     * @param
     * @return
     */
    public int setSuspendCfgDraft(SuspendWindowConfig suspendWindowConfig) {
        Assert.isTrue(suspendWindowConfig != null, "Value must not null");
        return this.setJsonObject(K_SUSPEND_CFG_DRAFT, suspendWindowConfig);
    }

    /**
     * @return
     */
    public SuspendWindowConfig getSuspendCfg() {
        return this.getJsonObject(K_SUSPEND_CFG, SuspendWindowConfig.class, null);
    }

    /**
     * @param
     * @return
     */
    public int setSuspendCfg(SuspendWindowConfig suspendWindowConfig) {
        Assert.isTrue(suspendWindowConfig != null, "Value must not null");
        return this.setJsonObject(K_SUSPEND_CFG, suspendWindowConfig);
    }

}
