package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.shop.base.ResultMessage;
import com.meidianyi.shop.service.pojo.shop.medical.goods.MedicalGoodsConstant;
import com.meidianyi.shop.service.pojo.wxapp.cart.CartGoodsNumParam;
import com.meidianyi.shop.service.pojo.wxapp.cart.CartGoodsNumVo;
import com.meidianyi.shop.service.pojo.wxapp.cart.CartSwitchActivityParam;
import com.meidianyi.shop.service.pojo.wxapp.cart.WxAppAddGoodsToCartParam;
import com.meidianyi.shop.service.pojo.wxapp.cart.WxAppChangeNumberParam;
import com.meidianyi.shop.service.pojo.wxapp.cart.WxAppRemoveCartProductParam;
import com.meidianyi.shop.service.pojo.wxapp.cart.WxAppRemoveCartProductsParam;
import com.meidianyi.shop.service.pojo.wxapp.cart.WxAppSwitchCartProductsParam;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.WxAppCartBo;
import com.meidianyi.shop.service.pojo.wxapp.cart.list.WxAppCartGoods;
import com.meidianyi.shop.service.pojo.wxapp.login.WxAppSessionUser;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/**
 *  购物车
 * @author 孔德成
 * @date 2019/10/14 16:51
 */
@RestController
@RequestMapping("/api/wxapp/cart")
public class WxAppCartController extends WxAppBaseController {


    @PostMapping("/list")
    public JsonResult getCartList() {
        WxAppSessionUser user = wxAppAuth.user();
        WxAppCartBo cartList = shop().cart.getCartList(user.getUserId());
        Iterator<WxAppCartGoods> iterator = cartList.getCartGoodsList().iterator();
        int initialCapacity = 16;
        cartList.setFullReductionGoodsMap(new HashMap<>(initialCapacity));
        cartList.setPurchasePriceGoodsMap(new HashMap<>(initialCapacity));
        while (iterator.hasNext()){
            WxAppCartGoods next = iterator.next();
            if (next.getGoodsImg()==null||next.getGoodsImg().trim().isEmpty()){
                next.setGoodsImg(MedicalGoodsConstant.MEDICAL_GOODS_WXAPP_DEFAULT_IMG);
            }
            if (BaseConstant.ACTIVITY_TYPE_PURCHASE_GOODS.equals(next.getActivityType())||BaseConstant.ACTIVITY_TYPE_PURCHASE_PRICE.equals(next.getActivityType())){
                if (cartList.getPurchasePriceGoodsMap().containsKey(next.getActivityId())){
                    cartList.getPurchasePriceGoodsMap().get(next.getActivityId()).add(next);
                }else {
                    cartList.getPurchasePriceGoodsMap().put(next.getActivityId(),  new ArrayList(Collections.singletonList(next)));
                }
                iterator.remove();
            }else if (BaseConstant.ACTIVITY_TYPE_FULL_REDUCTION.equals(next.getActivityType())){
                if (cartList.getFullReductionGoodsMap().containsKey(next.getActivityId())){
                    cartList.getFullReductionGoodsMap().get(next.getActivityId()).add(next);
                }else {
                    cartList.getFullReductionGoodsMap().put(next.getActivityId(),  new ArrayList(Collections.singletonList(next)));
                }
                iterator.remove();
            }
        }
        return success(cartList);
    }

    /**
     *  添加商品到购物车
     * @param param
     * @return
     */
    @PostMapping("/add")
    public JsonResult addGoodsToCart(@RequestBody @Valid WxAppAddGoodsToCartParam param){
        WxAppSessionUser user = wxAppAuth.user();
        param.setUserId(user.getUserId());
        ResultMessage s = shop().cart.addGoodsToCart(param);
        if (s.getFlag()){
            return success();
        }
        return fail(s);
    }

    /**
     * 从购物车删除商品
     * @param param
     * @return
     */
    @PostMapping("/remove")
    public JsonResult deleteCartById(@RequestBody @Valid WxAppRemoveCartProductParam param){
        WxAppSessionUser user = wxAppAuth.user();
        shop().cart.removeCartProductById(user.getUserId(),param.getCartId());
        return success();
    }

    /**
     *  改变购物车商品数量
     * @param param
     * @return
     */
    @PostMapping("/change")
    public JsonResult changeGoodsNumber(@RequestBody @Valid WxAppChangeNumberParam param){
        WxAppSessionUser user = wxAppAuth.user();
        ResultMessage resultMessage = shop().cart.changeGoodsNumber(user.getUserId(), param.getCartId(),param.getProductId(), param.getCartNumber());
        if (!resultMessage.getFlag()){
            return fail(resultMessage);
        }
        return success();
    }

    /**
     * 从购物车删除商品
     * @param param rceId
     * @return
     */
    @PostMapping("/removes")
    public JsonResult close(@RequestBody @Valid WxAppRemoveCartProductsParam param){
        WxAppSessionUser user = wxAppAuth.user();
        shop().cart.removeCartProductByIds(user.getUserId(),param.getCartIds());
        return success();
    }

    /**
     *  切换选择状态
     * @param param id
     * @return
     */
    @PostMapping("/switch")
    public JsonResult checked(@RequestBody @Valid WxAppSwitchCartProductsParam param){
        WxAppSessionUser user = wxAppAuth.user();
        int flag = shop().cart.switchCheckedProduct(user.getUserId(), param.getCartIds(),param.getIsChecked());
        if (flag>0){
            return success();
        }
        return fail();
    }

    /**
     *  切换选择活动
     * @param param id
     * @return
     */
    @PostMapping("/switch/activity")
    public JsonResult checkedActivity(@RequestBody @Valid CartSwitchActivityParam param){
        WxAppSessionUser user = wxAppAuth.user();
        int flag = shop().cart.switchActivityGoods(user.getUserId(), param.getCartIds(),param.getActivityId(),param.getActivityType());
        if (flag>0){
            return success();
        }
        return fail();
    }

    /**
     * 查询商品数量
     * @return
     */
    @PostMapping("/goods/num")
    public JsonResult cartGoodsNum(@RequestBody @Valid CartGoodsNumParam param){
        WxAppSessionUser user = wxAppAuth.user();
        Integer num = shop().cart.cartGoodsNum(user.getUserId(),param.getGoodsId());
        CartGoodsNumVo cartGoodsNumVo =new CartGoodsNumVo();
        cartGoodsNumVo.setGoodsNum(num==null?0:num);
        return success(cartGoodsNumVo);
    }

}
