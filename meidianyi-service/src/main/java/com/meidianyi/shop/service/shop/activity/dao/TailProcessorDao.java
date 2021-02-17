package com.meidianyi.shop.service.shop.activity.dao;

import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.config.pledge.PledgeBo;
import com.meidianyi.shop.service.pojo.shop.config.pledge.PledgeInfo;
import com.meidianyi.shop.service.shop.config.PledgeConfigService;
import com.meidianyi.shop.service.shop.config.pledge.ShopPledgeService;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.meidianyi.shop.db.shop.Tables.USER_COLLECTION;

/**
 *小程序商品tail处理器获取数据库信息类
 * @author 李晓冰
 * @date 2019年11月19日
 */
@Service
public class TailProcessorDao extends ShopBaseService {

        @Autowired
        private ShopPledgeService pledgeService;
        @Autowired
        private PledgeConfigService pledgeConfigService;

    public boolean isCollectedGoods(Integer userId,Integer goodsId){
        int i = db().fetchCount(USER_COLLECTION, USER_COLLECTION.USER_ID.eq(userId).and(USER_COLLECTION.GOODS_ID.eq(goodsId)));
        return i>0;
    }


    /**
     * 商户信息
     * @param shopId
     * @return
     */
    public ShopRecord  shopInfo(Integer shopId){
        return saas.shop.getShopById(shopId);
    }

    /**
     * 服务承诺
     * @return PledgeBo
     */
    public PledgeBo getPledgeList() {
        PledgeBo pledgeBo =new PledgeBo();
        // 得到当前配置开关信息
        String pledgeSwitch = pledgeConfigService.getPledgeConfig();
        pledgeBo.setPledgeSwitch(pledgeSwitch);
        // 若开关状态为0-关闭
        if (PledgeConfigService.V_PLEDGE_OPEN.equals(pledgeSwitch)) {
            List<PledgeInfo> pledgeList = pledgeService.getPledgeList();
            pledgeBo.setPledgeList(pledgeList);
        }
        return pledgeBo;
    }
}
