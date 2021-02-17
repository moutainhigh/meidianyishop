package com.meidianyi.shop.service.shop.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.shop.config.pledge.ShopPledgeService;

/**
 * 商家-基础配置(主Service,引用分支)
 * @author 卢光耀
 */
@Service

public class ShopBasicConfigService extends ShopBaseService {

	@Autowired
    public ShopPledgeService shopPledge;


}
