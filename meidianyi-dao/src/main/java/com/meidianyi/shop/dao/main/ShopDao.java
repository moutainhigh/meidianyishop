package com.meidianyi.shop.dao.main;

import com.meidianyi.shop.dao.foundation.base.MainBaseDao;
import com.meidianyi.shop.service.pojo.saas.shop.ShopListInfoVo;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.meidianyi.shop.db.main.Tables.SHOP;

/**
 * @author 李晓冰
 * @date 2020年08月18日
 */
@Repository
public class ShopDao extends MainBaseDao {

    public List<ShopListInfoVo> getShopListInfo(){
        return db().select(SHOP.SHOP_ID,SHOP.SHOP_NAME).from(SHOP).fetchInto(ShopListInfoVo.class);
    }
}
