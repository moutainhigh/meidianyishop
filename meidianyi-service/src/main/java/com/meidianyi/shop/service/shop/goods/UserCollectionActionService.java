package com.meidianyi.shop.service.shop.goods;

import com.meidianyi.shop.dao.shop.goods.UserCollectionActionDao;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author chenjie
 * @date 2020年09月17日
 */
@Service
public class UserCollectionActionService extends ShopBaseService {
    @Autowired
    protected UserCollectionActionDao userCollectionActionDao;

    /**
     * 新增药品收藏行为记录
     * @param userId
     * @param goodsId
     * @param collectionType
     */
    public void insertRecord(Integer userId, Integer goodsId, Byte collectionType){
        userCollectionActionDao.insertRecord(userId,goodsId,collectionType);
    }
}
