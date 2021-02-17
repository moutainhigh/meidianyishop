package com.meidianyi.shop.service.shop.image.postertraits;

import com.upyun.UpException;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.PictorialRecord;
import com.meidianyi.shop.db.shop.tables.records.ReducePriceRecord;
import com.meidianyi.shop.service.foundation.util.ImageUtil;
import com.meidianyi.shop.service.pojo.shop.config.PictorialShareConfig;
import com.meidianyi.shop.service.pojo.shop.qrcode.QrCodeTypeEnum;
import com.meidianyi.shop.service.pojo.wxapp.share.*;
import com.meidianyi.shop.service.pojo.wxapp.share.reduce.ReducePriceShareInfoParam;
import com.meidianyi.shop.service.shop.market.reduceprice.ReducePriceService;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author 李晓冰
 * @date 2020年02月06日
 */
@Service
public class ReducePricePictorialShareService extends BaseShareService {

    @Autowired
    private ReducePriceService reducePriceService;

    @Override
    Record getActivityRecord(Integer activityId) {
        return reducePriceService.getReducePriceRecordCanDel(activityId);
    }

    @Override
    PictorialShareConfig getPictorialConfig(Record aRecord, GoodsRecord goodsRecord) {
        ReducePriceRecord record = (ReducePriceRecord) aRecord;
        return Util.parseJson(record.getShareConfig(), PictorialShareConfig.class);
    }

    /**
     * 限时降价分享背景图片地址
     */
    private static final String REDUCE_PRICE_SHARE_BG_IMG = "image/wxapp/reduce_price_share.png";

    @Override
    String createShareImage(Record aRecord, GoodsRecord goodsRecord, GoodsShareBaseParam baseParam) {
        ReducePriceRecord reducePriceRecord = (ReducePriceRecord) aRecord;
        ReducePriceShareInfoParam param = (ReducePriceShareInfoParam) baseParam;
        Color shopStyleColor  = getShopStyleColor();

        PictorialRecord pictorialRecord = pictorialService.getPictorialDao(goodsRecord.getGoodsId(), param.getActivityId(), PictorialConstant.REDUCE_PRICE_ACTION_SHARE, null);
        // 已存在生成的图片
        if (pictorialRecord != null && pictorialService.isGoodsSharePictorialRecordCanUse(pictorialRecord.getRule(), goodsRecord.getUpdateTime(), reducePriceRecord.getUpdateTime(),shopStyleColor)) {
            return pictorialRecord.getPath();
        }
        try (InputStream bgInputStream = Util.loadFile(REDUCE_PRICE_SHARE_BG_IMG)) {
            BufferedImage reducePriceBufferImg = ImageIO.read(bgInputStream);
            BufferedImage goodsBufferImg = ImageIO.read(new URL(imageService.getImgFullUrl(goodsRecord.getGoodsImg())));

            int goodsWidth = 458;
            int goodsHeight = 367;
            int toTop = 30;
            int toLeft = goodsWidth - 30 - reducePriceBufferImg.getWidth();

            goodsBufferImg = ImageUtil.resizeImage(goodsWidth, goodsHeight, goodsBufferImg);
            ImageUtil.addTwoImage(goodsBufferImg, reducePriceBufferImg, toLeft, toTop);

            // 上传u盘云并缓存入库
            String relativePath = createFilePath(reducePriceRecord.getId());
            PictorialRule pictorialRule = new PictorialRule(goodsRecord.getUpdateTime(), reducePriceRecord.getUpdateTime(),shopStyleColor.getRed(),shopStyleColor.getGreen(),shopStyleColor.getBlue());
            pictorialService.uploadToUpanYun(goodsBufferImg, relativePath, pictorialRule, goodsRecord.getGoodsId(), param.getActivityId(), PictorialConstant.REDUCE_PRICE_ACTION_SHARE, pictorialRecord, param.getUserId());
            return relativePath;
        } catch (IOException e) {
            shareLog(getActivityName(), "图片生成错误：" + e.getMessage());
        } catch (UpException e) {
            shareLog(getActivityName(), "UpanYun上传错误：" + e.getMessage());
        }
        return null;
    }

    @Override
    String createMpQrCode(Record aRecord, GoodsRecord goodsRecord, GoodsShareBaseParam baseParam) {
        ReducePriceRecord reducePriceRecord = (ReducePriceRecord) aRecord;
        SceneValueBase sceneValueBase = new SceneValueBase(baseParam.getUserId(), goodsRecord.getGoodsId(), reducePriceRecord.getId(), BaseConstant.ACTIVITY_TYPE_REDUCE_PRICE, baseParam.getShareAwardId());
        String paramStr = addAndGetSceneStr(sceneValueBase);
        return qrCodeService.getMpQrCode(QrCodeTypeEnum.GOODS_ITEM, paramStr);
    }

    private static final String REDUCE_PRICE_BG_IMG = "image/wxapp/reduce_price.png";

    @Override
    void createPictorialImg(BufferedImage qrCodeBufferImg, BufferedImage goodsImg, PictorialUserInfo userInfo, String shareDoc, Record aRecord, GoodsRecord goodsRecord, ShopRecord shop, GoodsShareBaseParam baseParam, GoodsPictorialInfo goodsPictorialInfo) {
        PictorialImgPx imgPx = new PictorialImgPx(getShopStyleColor());

        BufferedImage reduceIconBufferImg = null;
        // 拼装价值限时降价图片和商品价格
        try (InputStream reduceIconStream = Util.loadFile(REDUCE_PRICE_BG_IMG)) {
            reduceIconBufferImg = ImageIO.read(reduceIconStream);
        } catch (IOException e) {
            pictorialLog(getActivityName(), "装载限时降价图标失败");
            goodsPictorialInfo.setPictorialCode(PictorialConstant.GOODS_PIC_ERROR);
            return;
        }
        // 拼装背景图
        BufferedImage bgBufferedImage = pictorialService.createPictorialBgImage(userInfo, shop, qrCodeBufferImg, goodsImg, shareDoc, goodsRecord.getGoodsName(), reduceIconBufferImg,null,convertPriceWithFlag(shop.getShopLanguage(), baseParam.getRealPrice()), convertPriceWithFlag(shop.getShopLanguage(),baseParam.getLinePrice()), imgPx);

        String base64 = ImageUtil.toBase64(bgBufferedImage);
        goodsPictorialInfo.setBase64(base64);
        goodsPictorialInfo.setBgImg(bgBufferedImage);
    }

    @Override
    String createDefaultShareDoc(String lang, Record aRecord, GoodsRecord goodsRecord, GoodsShareBaseParam baseParam) {
        return Util.translateMessage(lang, JsonResultMessage.WX_MA_NORMAL_GOODS_SHARE_INFO, "", "messages", baseParam.getUserName(), goodsRecord.getGoodsName());
    }

    @Override
    protected String getActivityName() {
        return "reduce_price";
    }
}
