package com.meidianyi.shop.service.shop.collection;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.UserCollectionRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.wxapp.collection.*;
import com.meidianyi.shop.service.shop.goods.UserCollectionActionService;
import com.meidianyi.shop.service.shop.goods.mp.GoodsMpService;
import com.meidianyi.shop.service.shop.image.ImageService;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.meidianyi.shop.db.shop.Tables.*;

/**
 * 商品收藏service
 * @author 常乐
 * 2019年10月16日
 */
@Service
public class CollectService extends ShopBaseService{
    public static final Byte COLLECTION_TYPE = 1;
    public static final Byte COLLECTION_CANCEL_TYPE = -1;
    @Autowired
    ImageService imageService;

	@Autowired
    GoodsMpService goodsMpService;

	@Autowired
	UserCollectionActionService userCollectionActionService;
	/**
	 * 商品收藏列表
	 * @param param
	 * @param userId
	 * @return
	 */
	public PageResult<CollectListVo> collectList(CollectListParam param , Integer userId) {
		 SelectConditionStep<? extends Record> sql = db().select(USER_COLLECTION.ID,USER_COLLECTION.USER_ID,GOODS.GOODS_NAME,GOODS.GOODS_IMG,
				 GOODS.SHOP_PRICE,GOODS.GOODS_ID,GOODS.GOODS_TYPE,USER_COLLECTION.COLLECT_PRICE,USER_COLLECTION.USERNAME,USER_COLLECTION.CREATE_TIME)
				.from(USER_COLLECTION
				.leftJoin(GOODS).on(USER_COLLECTION.GOODS_ID.eq(GOODS.GOODS_ID)))
				.where(USER_COLLECTION.USER_ID.eq(userId).and(GOODS.DEL_FLAG.eq((byte)0)));
		PageResult<CollectListVo> lists = getPageResult(sql, param.getCurrentPage(), param.getPageRows(), CollectListVo.class);

		//判断是否是拼团商品
        for(CollectListVo goodsInfo:lists.dataList){
            if(goodsInfo.getGoodsType() == 1){
                //查询拼团信息
                List<CollectGroupVo> infos = db().select(GROUP_BUY_PRODUCT_DEFINE.GROUP_PRICE)
                        .from(GROUP_BUY_DEFINE.leftJoin(GROUP_BUY_PRODUCT_DEFINE)
                    .on(GROUP_BUY_DEFINE.ID.eq(GROUP_BUY_PRODUCT_DEFINE.ACTIVITY_ID)))
                    .where(GROUP_BUY_PRODUCT_DEFINE.GOODS_ID.eq(goodsInfo.getGoodsId())).fetch().into(CollectGroupVo.class);

                BigDecimal groupPrice = goodsInfo.getCollectPrice() ;
                System.out.println(groupPrice);
                for(CollectGroupVo info:infos){
                    //规格商品、获取最低规格价
                    System.out.println(info.getGroupPrice());
                    if(info.getGroupPrice().compareTo(groupPrice)<0){
                        groupPrice = info.getGroupPrice();
                    }
                    System.out.println(groupPrice);
                }
                goodsInfo.setGroupPrice(groupPrice);
            }
        }
		//处理图片路径为全路径
		for(CollectListVo list : lists.dataList) {
			if(!org.apache.commons.lang3.StringUtils.isBlank(list.getGoodsImg())) {
				list.setGoodsImg(imageService.imageUrl(list.getGoodsImg()));
			}
		}
		return lists;
	}

	/**
	 * 取消收藏
	 * @param param
	 * @return
	 */
	public int cancalCollect(CancleCollectParam param) {
		System.out.println(1234);
		int res = db().delete(USER_COLLECTION).where(USER_COLLECTION.ID.eq(param.getId())).execute();
		return res;
	}

    /**
     * 添加用户商品收藏信息
     * @param param 待添加商品信息
     * @param userId 用户id
     * @param userName 用户名称
     */
	public void addCollection(AddAndCancelCollectionParam param, Integer userId, String userName){
        GoodsRecord goodsInfo = goodsMpService.getGoodsCollectionInfoDao(param.getGoodsId());
        if (goodsInfo == null||isCollectedDao(userId, param.getGoodsId())) {
            return;
        }

        transaction(()->{
            goodsMpService.incOrDecGoodsCollectionNumDao(param.getGoodsId(),true);
            addCollectionDao(param.getGoodsId(),goodsInfo.getShopPrice(),userId,userName);
        });
    }

    /**
     * 取消商品收藏
     * @param param 商品信息
     * @param userId 用户id
     */
    public void cancelCollection(AddAndCancelCollectionParam param, Integer userId) {
        transaction(()->{
            if (!isCollectedDao(userId, param.getGoodsId())) {
                return;
            }
            int i = deleteCollectionDao(param.getGoodsId(), userId);
            if (i > 0) {
                GoodsRecord goodsInfo = goodsMpService.getGoodsCollectionInfoDao(param.getGoodsId());
                if (goodsInfo.getGoodsCollectNum() > 0) {
                    goodsMpService.incOrDecGoodsCollectionNumDao(param.getGoodsId(),false);
                }
            }
        });
    }

    /**
     * 判断用户是否已经收藏了该商品
     * @param userId 用户id
     * @param goodsId 商品id
     * @return true 已收藏 false 未收藏
     */
    private boolean isCollectedDao (Integer userId, Integer goodsId) {
        int count = db().fetchCount(USER_COLLECTION, USER_COLLECTION.USER_ID.eq(userId).and(USER_COLLECTION.GOODS_ID.eq(goodsId)));
        if (count == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 插入用户收藏商品信息
     * @param goodsId 商品id
     * @param shopPrice 商品收藏时候的价格
     * @param userId 用户id
     */
    private void addCollectionDao(Integer goodsId,BigDecimal shopPrice,Integer userId,String userName) {
        UserCollectionRecord userCollectionRecord = db().newRecord(USER_COLLECTION);
        userCollectionRecord.setGoodsId(goodsId);

        userCollectionRecord.setCollectPrice(shopPrice);
        userCollectionRecord.setUserId(userId);
        userCollectionRecord.setUsername(userName);
        userCollectionRecord.insert();
        userCollectionActionService.insertRecord(userId,goodsId,COLLECTION_TYPE);
    }

    /**
     * 删除用户收藏商品信息
     * @param goodsId 商品id
     * @param userId 用户id
     * @return 受影响行数
     */
    private int deleteCollectionDao(Integer goodsId, Integer userId) {
        userCollectionActionService.insertRecord(userId,goodsId,COLLECTION_CANCEL_TYPE);
       return db().delete(USER_COLLECTION).where(USER_COLLECTION.GOODS_ID.eq(goodsId).and(USER_COLLECTION.USER_ID.eq(userId))).execute();
    }
}
