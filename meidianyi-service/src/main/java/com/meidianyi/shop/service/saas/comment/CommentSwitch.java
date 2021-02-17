package com.meidianyi.shop.service.saas.comment;

import com.meidianyi.shop.service.foundation.service.MainBaseService;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import static com.meidianyi.shop.db.main.Tables.SHOP_ACCOUNT;
/**
 * 商家添加评论开关
 * @author liangchen
 * @date 2019.11.19
 */
@Service
public class CommentSwitch extends MainBaseService {
    /**
     * 获取当前商家添加评论开关配置
     * @param sysId 当前店铺id
     * @return 开关配置
     */
    public Integer getAddCommentSwitch(Integer sysId){
       Integer addCommentSwitch = db().select(SHOP_ACCOUNT.ADD_COMMENT_SWITCH)
            .from(SHOP_ACCOUNT)
            .where(SHOP_ACCOUNT.SYS_ID.eq(sysId))
            .fetchOptionalInto(Integer.class)
            .orElse(NumberUtils.INTEGER_ZERO);
    return addCommentSwitch;
    }
}
