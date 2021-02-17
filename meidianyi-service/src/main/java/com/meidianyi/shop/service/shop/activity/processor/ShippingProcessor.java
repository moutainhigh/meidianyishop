package com.meidianyi.shop.service.shop.activity.processor;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.pojo.shop.member.address.AddressCode;
import com.meidianyi.shop.service.pojo.shop.member.address.AddressInfo;
import com.meidianyi.shop.service.pojo.shop.member.address.UserAddressVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailCapsuleParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailMpBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.DeliverFeeAddressDetailVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.GoodsPrdMpVo;
import com.meidianyi.shop.service.shop.goods.GoodsDeliverTemplateService;
import com.meidianyi.shop.service.shop.member.AddressService;
import com.meidianyi.shop.service.shop.order.action.CreateService;
import com.meidianyi.shop.service.shop.order.action.base.Calculate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.Comparator;

/**
 * 配送信息,运费设置
 * @author 孔德成
 * @date 2020/4/28
 */
@Component
@Slf4j
public class ShippingProcessor implements Processor,GoodsDetailProcessor {
    @Autowired
    private AddressService addressService;
    @Autowired
    private CreateService createService;
    @Autowired
    private GoodsDeliverTemplateService shippingFeeTemplate;
    @Autowired
    private Calculate calculate;

    @Override
    public Byte getPriority() {
        return Byte.MAX_VALUE-1;
    }

    @Override
    public Byte getActivityType() {
        return BaseConstant.ACTIVITY_TYPE_GENERAL;
    }


    /**
     * 商品详情页
     * 1 已设置位置 未设置收货地址 显示当前位置
     * 2 未设置位置 未设置地址 显示不可配送
     * 3 设置地址 显示地址
     * 计算运费
     *
     * @param goodsDetailMpBo  商品详情对象{@link com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailMpBo}
     * @param param
     */
    @Override
    public void processGoodsDetail(GoodsDetailMpBo goodsDetailMpBo, GoodsDetailCapsuleParam param) {
        DeliverFeeAddressDetailVo vo =new DeliverFeeAddressDetailVo();
        UserAddressVo defaultAddress = createService.getDefaultAddress(param.getUserId(), null);
        int defaultNum  = Integer.valueOf(0).equals(goodsDetailMpBo.getLimitBuyNum())? 1:goodsDetailMpBo.getLimitBuyNum();
        BigDecimal prdRealPrice = goodsDetailMpBo.getProducts().stream().max(Comparator.comparing(GoodsPrdMpVo::getPrdRealPrice)).get().getPrdRealPrice();
        if(defaultAddress==null&&param.getLat()!=null&&param.getLon()!=null){
            log.info("默认地址为空,去定位地址");
            AddressInfo geocoderAddressInfo = addressService.getGeocoderAddressInfo(param.getLat(), param.getLon());
            AddressCode address = addressService.getUserAddress(geocoderAddressInfo);
            if (address!=null){
                defaultAddress =new UserAddressVo();
                defaultAddress.setProvinceName(address.getProvinceName());
                defaultAddress.setProvinceCode(address.getProvinceCode());
                defaultAddress.setCityCode(address.getCityCode());
                defaultAddress.setCityName(address.getCityName());
                defaultAddress.setDistrictCode(address.getDistrictCode());
                defaultAddress.setDistrictName(address.getDistrictName());
            }
        }
        log.info("商品详情-用户地址信息{}",defaultAddress);
        vo.setAddress(defaultAddress);
        BigDecimal totalPrice = BigDecimalUtil.multiply(prdRealPrice, BigDecimal.valueOf(defaultNum));
        BigDecimal totalWeight = BigDecimalUtil.multiply(goodsDetailMpBo.getGoodsWeight(), BigDecimal.valueOf(defaultNum));
        goodsDetailMpBo.setDeliverPrice(BigDecimal.ZERO);
        try {
            Integer districtCode =null;
            if (defaultAddress!=null){
                districtCode =defaultAddress.getDistrictCode();
            }
            vo.setDeliverPrice(shippingFeeTemplate.getShippingFeeByTemplate(districtCode, goodsDetailMpBo.getDeliverTemplateId(), defaultNum, totalPrice, totalWeight));
            goodsDetailMpBo.setDeliverPrice(vo.getDeliverPrice());
        } catch (MpException e) {
            log.error("商品详情-获取邮费信息失败");
            String messages = Util.translateMessage("", e.getErrorCode().getMessage(), "messages");
            vo.setMessage(messages);
            vo.setStatus((byte)2);
            e.printStackTrace();
        }catch (NullPointerException e1){
            e1.printStackTrace();
            vo.setMessage(Util.translateMessage("", JsonResultCode.CODE_ORDER_CALCULATE_SHIPPING_FEE_ERROR.getMessage(), "messages"));
            vo.setStatus((byte)2);
        }
        goodsDetailMpBo.setDeliverFeeAddressVo(vo);
    }
}
