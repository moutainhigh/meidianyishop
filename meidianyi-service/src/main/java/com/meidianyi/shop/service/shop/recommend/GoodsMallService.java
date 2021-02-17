package com.meidianyi.shop.service.shop.recommend;

import static com.meidianyi.shop.db.shop.tables.Goods.GOODS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.config.WxShoppingListConfig;
import com.meidianyi.shop.service.pojo.shop.goods.spec.GoodsSpecProduct;
import com.meidianyi.shop.service.pojo.shop.recommend.SkuAttrList;
import com.meidianyi.shop.service.pojo.shop.recommend.goods.BrandInfo;
import com.meidianyi.shop.service.pojo.shop.recommend.goods.ListProduct;
import com.meidianyi.shop.service.pojo.shop.recommend.goods.Product;
import com.meidianyi.shop.service.pojo.shop.recommend.goods.SkuList;
import com.meidianyi.shop.service.saas.categroy.SysCateService;
import com.meidianyi.shop.service.saas.shop.ShopService;
import com.meidianyi.shop.service.shop.config.WxShoppingListConfigService;
import com.meidianyi.shop.service.shop.goods.GoodsService;
import com.meidianyi.shop.service.shop.image.ImageService;

/**
 * 好物圈相关之商品推荐
 * 
 * @author zhaojianqiang
 *
 *         2019年11月11日 下午1:57:11
 */
@Service
public class GoodsMallService extends ShopBaseService {
	@Autowired
	private GoodsService goodsService;
	@Autowired
	private SysCateService syCateService;
	@Autowired
	private ShopService shopService;
	@Autowired
	private ImageService imageService;
	@Autowired
	private WxShoppingListConfigService shoppingListConfig;

	/**
	 * 好物推荐商品   小程序页面
	 * 
	 * @param goodsId
	 * @return
	 */
	public List<ListProduct> shippingRecommendGoods(List<Integer> goodsId) {
		ShopRecord shop = shopService.getShopById(getShopId());
		Result<GoodsRecord> goodsList = db().selectFrom(GOODS).where(GOODS.GOODS_ID.in(goodsId)).fetch();
		List<ListProduct> vo = new ArrayList<ListProduct>();
		for (GoodsRecord goods : goodsList) {
			ListProduct pListProduct = new ListProduct();
			Product productVo = new Product();
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
				imageList.add(imageService.imageUrl(goods.getGoodsImg()));
			}
			productVo.setImageList(imageList);
			productVo.setSrcMiniProgramPath("/pages/item/item?gid=" + goods.getGoodsId());
			productVo.setSkuList(liSkuLists);
			String logo = shop.getLogo();
			productVo.setBrandInfo(new BrandInfo(shop.getShopName(), StringUtils.isEmpty(logo)?"":imageService.imageUrl(shop.getLogo())));
			pListProduct.setProduct(productVo);
			pListProduct.setGoodsId(goods.getGoodsId());
			vo.add(pListProduct);
		}
		return vo;
	}
	
	/**
	 * 好物推荐，带校验是不是开了，用这个
	 * @param goodsId
	 * @return
	 */
	public List<ListProduct> checkShippingRecommendGoods(List<Integer> goodsId) {
		List<ListProduct> list=new ArrayList<ListProduct>();
        String showInItemDetailPage = "2";
        if(check(showInItemDetailPage)) {
			list=shippingRecommendGoods(goodsId);
		}
		return list;
	}

	/**
	 * 是否包含对应的数据 true：可以；false：不可以
	 * @param value "1"订单详情页显示，"2"商品详情页显示
	 * @return
	 */
	public boolean check(String value) {
		WxShoppingListConfig config = shoppingListConfig.getShoppingListConfig();
		String enabeld = config.getEnabeldWxShoppingList();
        String disabled = "0";
        if(enabeld.equals(disabled)) {
			//0"未开启 "1"开启
			return false;
		}
		String recommend = config.getWxShoppingRecommend();
		String[] split = recommend.split(",");
		for (String string : split) {
			if(string.equals(value)) {
				return true;
			}
		}
		return false;
	}

}
