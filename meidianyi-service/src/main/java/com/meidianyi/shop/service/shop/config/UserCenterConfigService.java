package com.meidianyi.shop.service.shop.config;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.meidianyi.shop.service.pojo.shop.config.center.UserCenterConfigParam;
import com.meidianyi.shop.service.pojo.shop.config.center.UserCenterConfigVo;

/**
 * 个人中心
 *
 * @author 孔德成
 * @date 2019/7/11 9:52
 */
@Service

public class UserCenterConfigService extends BaseShopConfigService {

    /**
     * value是json格式
     */
    final public static String K_USER_CENTER = "user_center";


    public List<UserCenterConfigVo> getkUserCenterConfig(){
        return getJsonObject(K_USER_CENTER, new TypeReference<List<UserCenterConfigVo>>(){});
    }


    public Boolean updateUserCenterConfig(List<UserCenterConfigParam> object){
        return setJsonObject(K_USER_CENTER,object)==1;
    }


}
