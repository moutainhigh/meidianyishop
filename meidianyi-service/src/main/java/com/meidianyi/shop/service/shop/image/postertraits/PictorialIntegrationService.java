package com.meidianyi.shop.service.shop.image.postertraits;

import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.foundation.util.ImageUtil;
import com.meidianyi.shop.service.pojo.wxapp.share.GoodsPictorialInfo;
import com.meidianyi.shop.service.pojo.wxapp.share.GoodsShareBaseParam;
import com.meidianyi.shop.service.pojo.wxapp.share.GoodsShareInfo;
import com.meidianyi.shop.service.pojo.wxapp.share.bargain.BargainShareInfoParam;
import com.meidianyi.shop.service.pojo.wxapp.share.firstspecial.FirstSpecialShareInfoParam;
import com.meidianyi.shop.service.pojo.wxapp.share.group.GroupDrawShareInfoParam;
import com.meidianyi.shop.service.pojo.wxapp.share.groupbuy.GroupBuyShareInfoParam;
import com.meidianyi.shop.service.pojo.wxapp.share.integral.GroupIntegralShareInfoParam;
import com.meidianyi.shop.service.pojo.wxapp.share.integral.IntegralMallShareInfoParam;
import com.meidianyi.shop.service.pojo.wxapp.share.presale.PreSaleShareInfoParam;
import com.meidianyi.shop.service.pojo.wxapp.share.rebate.RebateShareInfoParam;
import com.meidianyi.shop.service.pojo.wxapp.share.reduce.ReducePriceShareInfoParam;
import com.meidianyi.shop.service.pojo.wxapp.share.seckill.SeckillShareInfoParam;
import com.meidianyi.shop.service.shop.goods.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 画报整合类，整合了商品和活动的图片生成service类，便于调用
 *
 * @author 李晓冰
 * @date 2019年12月31日
 */
@Service
public class PictorialIntegrationService extends ShopBaseService {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private NormalGoodsPictorialShareService normalGoodsPictorialService;

    @Autowired
    private GroupBuyPictorialShareService groupBuyPictorialService;

    @Autowired
    private BargainPictorialShareService bargainPictorialService;

    @Autowired
    private GroupDrawPictorialShareService groupDrawPictorialService;

    @Autowired
    private PreSalePictorialShareService preSalePictorialService;

    @Autowired
    private ReducePricePictorialShareService reducePricePictorialService;

    @Autowired
    private FirstSpecialPictorialShareService firstSpecialPictorialService;

    @Autowired
    private SeckillPictorialShareService seckillPictorialService;

    @Autowired
    private RebatePictorialShareService rebatePictorialService;

    @Autowired
    private IntegralMallPictorialShareService integralMallPictorialService;

    @Autowired
    private GroupIntegrationPictorialShareService groupIntegrationPictorialService;

    /**
     * 获取商品所有图片base64格式集合
     *
     * @return
     */
    public List<String> getGoodsImagesBase64(Integer goodsId) {
        List<String> urlList = goodsService.getGoodsAllImageList(goodsId);
        List<String> imgList = new ArrayList<>(urlList.size());
        for (String s : urlList) {
            try {
                BufferedImage bufferedImage = ImageIO.read(new URL(s));
                // 降低图片质量
                bufferedImage = ImageUtil.resizeImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage);
                String base64 = ImageUtil.toBase64(bufferedImage);
                imgList.add(base64);
            } catch (IOException e) {
                logger().debug("小程序-下载商品多图-读取base64错误-img url:" + s + "\nmsg:" + e.getMessage());
            }
        }
        return imgList;
    }

    private BaseShareService findShareBaseService(GoodsShareBaseParam param) {
        BaseShareService baseShareService;
        if (param instanceof GroupBuyShareInfoParam) {
            baseShareService = groupBuyPictorialService;
        } else if (param instanceof BargainShareInfoParam) {
            baseShareService = bargainPictorialService;
        } else if (param instanceof GroupDrawShareInfoParam) {
            baseShareService = groupDrawPictorialService;
        } else if (param instanceof PreSaleShareInfoParam) {
            baseShareService = preSalePictorialService;
        } else if (param instanceof ReducePriceShareInfoParam) {
            baseShareService = reducePricePictorialService;
        } else if (param instanceof FirstSpecialShareInfoParam) {
            baseShareService = firstSpecialPictorialService;
        } else if (param instanceof SeckillShareInfoParam) {
            baseShareService = seckillPictorialService;
        } else if (param instanceof RebateShareInfoParam) {
            baseShareService = rebatePictorialService;
        } else if (param instanceof IntegralMallShareInfoParam) {
            baseShareService = integralMallPictorialService;
        } else if (param instanceof GroupIntegralShareInfoParam) {
            baseShareService = groupIntegrationPictorialService;
        } else {
            baseShareService = normalGoodsPictorialService;
        }
        return baseShareService;
    }

    /**
     * 获取活动分享图片信息
     *
     * @param param 各个活动对应的 GoodsShareBaseParam类
     * @return {@link GoodsShareInfo}
     */
    public GoodsShareInfo getActivityShareInfo(GoodsShareBaseParam param) {
        BaseShareService baseShareService = findShareBaseService(param);
        return baseShareService.getShareInfo(param);
    }

    public GoodsPictorialInfo getActivityPictorialInfo(GoodsShareBaseParam param) {
        BaseShareService baseShareService = findShareBaseService(param);
        return baseShareService.getPictorialInfo(param);
    }
    /**
     * 分销中心推广海报
     * @param param
     * @return
     */
    public GoodsPictorialInfo getDistributionPictorialInfo(GoodsShareBaseParam param) {
        BaseShareService baseShareService = findShareBaseService(param);
        return baseShareService.getDistributionPictorialInfo(param);
    }
    /**
     * 分销员微信二维码海报
     * @param param
     * @return
     */
    public GoodsPictorialInfo getDistributorPictorialInfo(GoodsShareBaseParam param) {
        BaseShareService shareBaseService = findShareBaseService(param);
        return shareBaseService.getDistributorPictorialInfo(param);
    }
}
