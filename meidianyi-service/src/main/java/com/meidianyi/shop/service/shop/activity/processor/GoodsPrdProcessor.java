package com.meidianyi.shop.service.shop.activity.processor;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.db.shop.tables.records.GoodsSpecProductRecord;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsListMpBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailMpBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailCapsuleParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.GoodsPrdMpVo;
import com.meidianyi.shop.service.shop.activity.dao.GoodsPrdProcessorDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Record3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.Tables.GOODS_SPEC_PRODUCT;

/**
 * @author 李晓冰
 * @date 2019年11月04日
 */
@Service
@Slf4j
public class GoodsPrdProcessor implements Processor,ActivityGoodsListProcessor,GoodsDetailProcessor{
    @Autowired
    GoodsPrdProcessorDao goodsPrdProcessorDao;

    /*****处理器优先级*****/
    @Override
    public Byte getPriority() {
        return 0;
    }

    @Override
    public Byte getActivityType() {
        return BaseConstant.ACTIVITY_TYPE_GENERAL;
    }

    /*****************商品列表处理*******************/
    @Override
    public void processForList(List<GoodsListMpBo> capsules, Integer userId) {
        if (capsules.size()==0||capsules.get(0).getIsDisposedByEs()) {
            return;
        }

        List<Integer> goodsIds = capsules.stream().map(GoodsListMpBo::getGoodsId).collect(Collectors.toList());
        Map<Integer, List<Record3<Integer, BigDecimal, String>>> goodsPrdInfos = goodsPrdProcessorDao.getGoodsPrdInfo(goodsIds);

        capsules.forEach(capsule->{
            List<Record3<Integer, BigDecimal, String>> record = goodsPrdInfos.get(capsule.getGoodsId());
            capsule.setDefaultPrd(record.size()==1&&StringUtils.isBlank(record.get(0).get(GOODS_SPEC_PRODUCT.PRD_DESC)));
            // 设置规格最大价格，最低价为商品的shop_price
            capsule.setPrdMaxPrice(record.get(0).get(GOODS_SPEC_PRODUCT.PRD_PRICE));
        });
    }
    /*****************商品详情处理******************/
    @Override
    public void processGoodsDetail(GoodsDetailMpBo goodsDetailMpBo, GoodsDetailCapsuleParam param) {
        log.debug("小程序-商品详情-商品规格processor,规格信息：");
        log.debug(goodsDetailMpBo.getProducts()==null?"无规格信息":goodsDetailMpBo.getProducts().toString());
        if (!goodsDetailMpBo.getIsDisposedByEs()) {
            List<GoodsSpecProductRecord> prdInfos = goodsPrdProcessorDao.getGoodsDetailPrds(param.getGoodsId());
            List<GoodsPrdMpVo> prdMpVos = prdInfos.stream().map(GoodsPrdMpVo::new).collect(Collectors.toList());
            goodsDetailMpBo.setProducts(prdMpVos);
            if (prdMpVos.size() == 1 && StringUtils.isBlank(prdMpVos.get(0).getPrdDesc())) {
                goodsDetailMpBo.setDefaultPrd(true);
            } else {
                goodsDetailMpBo.setDefaultPrd(false);
            }
            log.debug("商品详情-商品规格查询完成");
        }
    }

}
