package com.meidianyi.shop.service.shop.recommend;

import static com.meidianyi.shop.db.shop.tables.Goods.GOODS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.main.tables.records.MpAuthShopRecord;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobsConstant.TaskJobEnum;
import com.meidianyi.shop.service.pojo.shop.goods.spec.GoodsSpecProduct;
import com.meidianyi.shop.service.pojo.shop.recommend.SendProductBean;
import com.meidianyi.shop.service.pojo.shop.recommend.SkuAttrList;
import com.meidianyi.shop.service.pojo.shop.recommend.product.JsonProductBean;
import com.meidianyi.shop.service.pojo.shop.recommend.product.ProductList;
import com.meidianyi.shop.service.pojo.shop.recommend.product.SkuList;
import com.meidianyi.shop.service.saas.categroy.SysCateService;
import com.meidianyi.shop.service.shop.goods.GoodsService;
import com.meidianyi.shop.service.wechat.api.impl.WxOpenMaServiceExtraImpl;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.open.bean.result.WxOpenResult;

/**
 * 好物圈相关之物品信息
 * 
 * @author zhaojianqiang
 *
 *         2019年11月11日 下午1:57:11
 */
@Slf4j
@Service
public class ProductMallService extends ShopMallBaseService {
	@Autowired
	private GoodsService goodsService;
	@Autowired
	private SysCateService syCateService;

	/**
	 * 更新商品信息，商家可以对“想买清单”与“已购订单”中的商品信息进行更新，如上架状态改变、价格更新等
	 * 
	 * @param goodsId
	 * @return
	 */
	public Boolean updateGoods(List<Integer> goodsId) {
		Boolean updateGood = false;
		try {
			updateGood = updateGood(goodsId);
		} catch (Exception e) {
			logger().info(e.getMessage(),e);
		}
		return updateGood;
		
	}


	private Boolean updateGood(List<Integer> goodsId) {
		Boolean isOk = checkNoUserId();
		if (!isOk) {
			return false;
		}

		Result<GoodsRecord> goodsList = db().selectFrom(GOODS).where(GOODS.GOODS_ID.in(goodsId)).fetch();
		List<ProductList> vo = new ArrayList<ProductList>();
		for (GoodsRecord goods : goodsList) {
			ProductList productVo = new ProductList();
			// getAllProductListByGoodsId
			List<GoodsSpecProduct> productList = goodsService.goodsSpecProductService
					.selectByGoodsId(goods.getGoodsId());
			List<SkuList> liSkuLists = new ArrayList<SkuList>();
			for (GoodsSpecProduct product : productList) {
				SkuList skuList = new SkuList();
				String prdDesc = product.getPrdDesc();
				List<SkuAttrList> skuAttrList = new ArrayList<SkuAttrList>();
				if (!StringUtils.isEmpty(prdDesc)) {
					skuAttrList = goodsService.goodsSpecProductService.getSkuAttrList(prdDesc);
				} else {
					skuAttrList.add(new SkuAttrList(goods.getGoodsName(), goods.getGoodsName()));
				}
				skuList.setSkuId(String.valueOf(product.getPrdId()));
				skuList.setPrice(product.getPrdPrice().multiply(new BigDecimal(100)));
				skuList.setOriginalPrice(product.getPrdPrice().multiply(new BigDecimal(100)));
				skuList.setStatus(goods.getIsOnSale() == (byte) 1 ? (byte) 1 : (byte) 2);
				skuList.setSkuAttrList(skuAttrList);
				skuList.setVersion(1200);
				liSkuLists.add(skuList);
			}
			List<String> list = syCateService.getCategories(goods.getCatId());
			if (list == null || list.size() == 0) {
				list = new ArrayList<String>();
				list.add("未知");
			}
			productVo.setItemCode(String.valueOf(goods.getGoodsId()));
			productVo.setTitle(goods.getGoodsName());
			productVo.setDesc(goods.getGoodsName());
			productVo.setCategoryList(list);
			List<String> imageList = goodsService.getGoodsImageList(goods.getGoodsId());
			if(imageList.isEmpty()) {
				imageList.add(imageUrl(goods.getGoodsImg()));
			}
			productVo.setImageList(imageList);
			productVo.setSrcWxappPath("/pages/item/item?gid=" + goods.getGoodsId());
			productVo.setVersion(200);
			vo.add(productVo);
		}
		JsonProductBean jsonRootBean=new JsonProductBean(vo);
		SendProductBean bean=new SendProductBean(1, jsonRootBean,getShopId(),null);
		saas.taskJobMainService.dispatchImmediately(bean,SendProductBean.class.getName(),getShopId(),TaskJobEnum.WX_IMPORTPRODUCT.getExecutionType());
		return true;
	}
	
	
	/**
	 * 更新或导入物品信息，这是消费者，不要直接调用
	 * 
	 * @param jsonRootBean
	 * @return
	 */
	public WxOpenResult importProductUpdate(JsonProductBean jsonRootBean) {
		WxOpenMaServiceExtraImpl maService = open.getMaExtService();
		MpAuthShopRecord shop = saas.shop.mp.getAuthShopByShopId(getShopId());
		WxOpenResult result = null;
		try {
			String jsonNotNull = Util.toJsonNotNull(jsonRootBean);
			log.info("发送的报文" + jsonNotNull);
			result = maService.importProductUpdate(shop.getAppId(), jsonNotNull);
			log.info(" 更新或导入物品信息");
			log.info(result.getErrmsg(), result.getErrcode());
		} catch (WxErrorException e) {
			log.error(e.getMessage(), e);
		}
		return result;
	}


}
