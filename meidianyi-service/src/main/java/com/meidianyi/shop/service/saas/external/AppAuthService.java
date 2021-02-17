package com.meidianyi.shop.service.saas.external;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.pojo.main.table.AppAuthDo;
import com.meidianyi.shop.common.pojo.main.table.AppDo;
import com.meidianyi.shop.dao.main.AppAuthDao;
import com.meidianyi.shop.dao.main.AppDao;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.saas.app.vo.AppAuthVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 对接外部应用服务
 * @author 李晓冰
 * @date 2020年07月15日
 */
@Service
public class AppAuthService extends MainBaseService {

    @Autowired
    AppDao appDao;
    @Autowired
    AppAuthDao appAuthDao;

    public AppAuthVo getAppAuth(String appId,Integer shopId){
        AppDo appDo = appDao.getAppDo(appId);
        AppAuthDo appAuthDo = appAuthDao.getAppAuth(shopId, appId, BaseConstant.ACTIVITY_STATUS_NORMAL);
        if (appAuthDo == null) {
            return null;
        }
        AppAuthVo vo =new AppAuthVo();
        FieldsUtil.assign(appAuthDo,vo);
        vo.setAppSecret(appDo.getAppSecret());
        return vo;
    }
}
