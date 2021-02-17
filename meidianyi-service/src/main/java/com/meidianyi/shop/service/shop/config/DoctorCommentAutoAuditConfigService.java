package com.meidianyi.shop.service.shop.config;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * 医师评论自动审核
 * @author 孔德成
 * @date 2020/8/26 15:31
 */
@Service
public class DoctorCommentAutoAuditConfigService extends BaseShopConfigService {



    /**
     * 评论自动审核
     */
    final public static String K_DOCTOR_COMMENT_AUTO_AUDIT = "doctor_comment_auto_audit";

    /**
     * 获取自动评论审核
     * @return
     */
    public Integer get() {
        return this.get(K_DOCTOR_COMMENT_AUTO_AUDIT, Integer.class, 0);
    }

    /**
     * 设置自动评论审核
     * @return
     */
    public int set(int value) {
        Assert.isTrue(value >= 0,"value need >=0");
        return this.set(K_DOCTOR_COMMENT_AUTO_AUDIT,  value, Integer.class);
    }

}
