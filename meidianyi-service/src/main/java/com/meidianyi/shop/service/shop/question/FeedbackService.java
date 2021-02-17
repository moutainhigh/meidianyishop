package com.meidianyi.shop.service.shop.question;

import com.meidianyi.shop.service.pojo.shop.auth.AdminTokenAuthInfo;
import com.meidianyi.shop.service.pojo.shop.question.FeedbackParam;
import com.meidianyi.shop.service.shop.config.BaseShopConfigService;
import org.springframework.stereotype.Service;

/**
 *
 * @author luguangyao
 */
@Service
public class FeedbackService extends BaseShopConfigService {

    public void insert(FeedbackParam param){
        saas().questionService.insert(getShopId(),param,getCurrentAdminLoginUser());
    }
}
