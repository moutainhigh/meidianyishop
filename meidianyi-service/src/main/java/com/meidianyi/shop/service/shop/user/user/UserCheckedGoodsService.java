package com.meidianyi.shop.service.shop.user.user;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.shop.tables.CheckedGoodsCart;
import com.meidianyi.shop.db.shop.tables.records.CheckedGoodsCartRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.member.card.CardConstant;
import com.meidianyi.shop.service.pojo.wxapp.user.UserCheckedGoodsParam;
import com.meidianyi.shop.service.pojo.wxapp.user.UserCheckedGoodsVo;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import static com.meidianyi.shop.db.shop.Tables.CHECKED_GOODS_CART;
import static com.meidianyi.shop.db.shop.Tables.GOODS;
import static com.meidianyi.shop.db.shop.Tables.GOODS_SPEC_PRODUCT;
/**
 * 用户已选商品服务
 * @author 黄壮壮
 */
@Service
public class UserCheckedGoodsService extends ShopBaseService {
	private final CheckedGoodsCart TABLE = CHECKED_GOODS_CART;
	
	/**
	 * 根据商品的Id和规格，获得用户已经选择的商品数量
	 * @return 商品数量 | 没有则为0
	 */
	public Integer getUserCheckedCount(UserCheckedGoodsParam param) {
		logger().info("获得所有已选商品数");
		Condition condition = getSearchCheckedGoodsCondition(param);
		
		return db().select(DSL.sum(TABLE.GOODS_NUMBER))
			.from(TABLE)
			.where(condition)
			.fetchOne(0,int.class);
	}
	
	/**
	 * 根据商品Id,获得用户已选商品数量
	 * @return 已选商品的数量
	 */
	public Integer getUserCheckedGoodsByGoodsId(Integer userId,Integer goodsId,String cardNo) {
		logger().info("根据商品ID,获得已选该商品数");
		Condition condition = DSL.noCondition()
				.and(TABLE.USER_ID.eq(userId))
				.and(TABLE.ACTION.eq(CardConstant.MCARD_TP_LIMIT))
				.and(TABLE.IDENTITY_ID.eq(cardNo))
				.and(TABLE.GOODS_ID.eq(goodsId));
		return db().select(DSL.sum(TABLE.GOODS_NUMBER)).from(TABLE).where(condition).fetchOne(0,int.class);
	}
	
	
	/**
	 * 获得用户已经选择的商品数量
	 * @return 商品数量 | 没有则为0
	 */
	public Integer getUserCheckedCount(Integer userId,String cardNo) {
		UserCheckedGoodsParam checkedGoodsParam = new UserCheckedGoodsParam();
		checkedGoodsParam.setAction(CardConstant.MCARD_TP_LIMIT);
		checkedGoodsParam.setIdentityId(cardNo);
		checkedGoodsParam.setUserId(userId);
		return getUserCheckedCount(checkedGoodsParam);
	}
	
	
	private Condition getSearchCheckedGoodsCondition(UserCheckedGoodsParam param) {
		Condition condition = DSL.noCondition()
			.and(TABLE.USER_ID.eq(param.getUserId()))
			.and(TABLE.ACTION.eq(param.getAction()))
			.and(TABLE.IDENTITY_ID.eq(param.getIdentityId()));
		return condition;
	}
	
	/**
	 * 根据商品的规格Id和商品Id,获得用户已选该商品记录
	 * @return 
	 */
	public CheckedGoodsCartRecord getUserCheckedGoods(UserCheckedGoodsParam param) {
		Condition condition = getSearchCheckedGoodsCondition(param);
		condition = condition.and(TABLE.PRODUCT_ID.eq(param.getProductId()))
				 			 .and(TABLE.GOODS_ID.eq(param.getGoodsId()));
		return db().selectFrom(TABLE).where(condition).fetchAny();
	}
	
	
	
	
	/**
	 * 获得已选商品列表
	 * @return 
	 */
	public PageResult<UserCheckedGoodsVo> getUsercheckedList(UserCheckedGoodsParam param) {
		Condition condition = getSearchCheckedGoodsCondition(param);
		SelectConditionStep<Record> select = db().select(TABLE.fields())
			.select(GOODS.GOODS_NAME,GOODS.GOODS_SN,GOODS.MARKET_PRICE,GOODS.GOODS_IMG,GOODS.IS_ON_SALE,GOODS.GOODS_TYPE)
			.select(GOODS_SPEC_PRODUCT.PRD_NUMBER,GOODS_SPEC_PRODUCT.PRD_PRICE,GOODS_SPEC_PRODUCT.PRD_SN,GOODS_SPEC_PRODUCT.PRD_SPECS,GOODS_SPEC_PRODUCT.PRD_DESC,GOODS_SPEC_PRODUCT.PRD_IMG)
			.from(TABLE)
			.leftJoin(GOODS).on(TABLE.GOODS_ID.eq(GOODS.GOODS_ID))
			.leftJoin(GOODS_SPEC_PRODUCT).on(TABLE.PRODUCT_ID.eq(GOODS_SPEC_PRODUCT.PRD_ID))
			.where(condition);
		
		return getPageResult(select, param.getCurrentPage(), param.getPageRows(), UserCheckedGoodsVo.class);
	}

	/**
	 * 删除记录
	 * @param param
	 */
	public void removeChoosedGoods(UserCheckedGoodsParam param) {
		db().deleteFrom(TABLE)
			.where(TABLE.IDENTITY_ID.eq(param.getIdentityId()))
			.and(TABLE.PRODUCT_ID.eq(param.getProductId()))
			.and(TABLE.GOODS_ID.eq(param.getGoodsId()))
			.and(TABLE.ACTION.eq(CardConstant.MCARD_TP_LIMIT))
			.execute();
	}


    /**
     * 下单结束后删除记录
     * @param cardNo 卡号
     */
    public void removeGoodsAfterOrder(String cardNo) {
        db().deleteFrom(TABLE).where(TABLE.IDENTITY_ID.eq(cardNo)).execute();
    }
}
