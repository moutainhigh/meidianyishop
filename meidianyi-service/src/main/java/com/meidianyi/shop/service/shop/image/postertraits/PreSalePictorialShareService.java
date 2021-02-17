package com.meidianyi.shop.service.shop.image.postertraits;

import com.upyun.UpException;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.PictorialRecord;
import com.meidianyi.shop.db.shop.tables.records.PresaleRecord;
import com.meidianyi.shop.service.foundation.util.ImageUtil;
import com.meidianyi.shop.service.pojo.shop.config.PictorialShareConfig;
import com.meidianyi.shop.service.pojo.shop.qrcode.QrCodeTypeEnum;
import com.meidianyi.shop.service.pojo.wxapp.share.*;
import com.meidianyi.shop.service.pojo.wxapp.share.presale.PreSaleShareInfoParam;
import com.meidianyi.shop.service.shop.market.presale.PreSaleService;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;

/**
 * @author 李晓冰
 * @date 2020年02月05日
 */
@Service
public class PreSalePictorialShareService extends BaseShareService {

    @Autowired
    PreSaleService preSaleService;

    @Override
     Record getActivityRecord(Integer activityId) {
        return   preSaleService.getPresaleRecord(activityId);
    }

    @Override
     PictorialShareConfig getPictorialConfig(Record aRecord, GoodsRecord goodsRecord) {
        PresaleRecord record = (PresaleRecord) aRecord;
        return Util.parseJson(record.getShareConfig(), PictorialShareConfig.class);
    }

    /**
     * 定金膨胀分享背景图片地址
     */
    private static final String PRE_SALE_BG_IMG = "image/wxapp/presale.png";
    @Override
     String createShareImage(Record aRecord, GoodsRecord goodsRecord, GoodsShareBaseParam baseParam) {
        PresaleRecord presaleRecord = (PresaleRecord) aRecord;
        PreSaleShareInfoParam param = (PreSaleShareInfoParam) baseParam;
        Color shopStyleColor = getShopStyleColor();

        PictorialRecord pictorialRecord = pictorialService.getPictorialDao(goodsRecord.getGoodsId(), param.getActivityId(), PictorialConstant.PRE_SALE_ACTION_SHARE, null);
        // 已存在生成的图片
        if (pictorialRecord != null && pictorialService.isGoodsSharePictorialRecordCanUse(pictorialRecord.getRule(), goodsRecord.getUpdateTime(), presaleRecord.getUpdateTime(),shopStyleColor)) {
            return pictorialRecord.getPath();
        }
        try (InputStream bgInputStream = Util.loadFile(PRE_SALE_BG_IMG)) {
            BufferedImage bgBufferImg = ImageIO.read(bgInputStream);
            BufferedImage goodsBufferImg = ImageIO.read(new URL(imageService.getImgFullUrl(goodsRecord.getGoodsImg())));

            int goodsWidth = 162;
            int toTop = 85;
            int toLeft = 80;

            goodsBufferImg = ImageUtil.resizeImage(goodsWidth, goodsWidth, goodsBufferImg);
            ImageUtil.addTwoImage(bgBufferImg, goodsBufferImg, toTop, toLeft);

            ShopRecord shop = saas.shop.getShopById(getShopId());

            int textStartX = toLeft + goodsWidth + 20;
            PictorialImgPx imgPx = new PictorialImgPx(shopStyleColor);
            //定金膨胀文字
            String msg = Util.translateMessage(shop.getShopLanguage(), JsonResultMessage.WX_MA_PRESALE_SHARE_INFO, "messages");
            ImageUtil.addFontWithRect(bgBufferImg, textStartX, toTop + 20, msg, ImageUtil.sourceHanSansCn(Font.PLAIN, 16), imgPx.getRealPriceColor(), imgPx.getShareImgRectInnerColor(), imgPx.getRealPriceColor());

            //添加真实价格
            String moneyFlag = Util.translateMessage(shop.getShopLanguage(), JsonResultMessage.WX_MA_PICTORIAL_MONEY_FLAG, "messages");
            String realPrice = param.getRealPrice().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
            ImageUtil.addFont(bgBufferImg, moneyFlag + realPrice, ImageUtil.sourceHanSansCn(Font.PLAIN, 20), textStartX, toTop + 80,imgPx.getRealPriceColor());
            //添加划线价格
            String linePrice = param.getLinePrice().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
            ImageUtil.addFontWithLine(bgBufferImg, textStartX, toTop + 100, moneyFlag + linePrice, ImageUtil.sourceHanSansCn(Font.PLAIN, 18),PictorialImgPx.LINE_PRICE_COLOR);

            // 上传u盘云并缓存入库
            String relativePath = createFilePath(presaleRecord.getId());
            PictorialRule pictorialRule = new PictorialRule(goodsRecord.getUpdateTime(), presaleRecord.getUpdateTime(),shopStyleColor.getRed(),shopStyleColor.getGreen(),shopStyleColor.getBlue());
            pictorialService.uploadToUpanYun(bgBufferImg, relativePath, pictorialRule, goodsRecord.getGoodsId(),param.getActivityId(),PictorialConstant.PRE_SALE_ACTION_SHARE, pictorialRecord, param.getUserId());

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
        PresaleRecord presaleRecord = (PresaleRecord) aRecord;
        SceneValueBase sceneValueBase = new SceneValueBase(baseParam.getUserId(), goodsRecord.getGoodsId(), presaleRecord.getId(), BaseConstant.ACTIVITY_TYPE_PRE_SALE, baseParam.getShareAwardId());
        String paramStr = addAndGetSceneStr(sceneValueBase);
        return qrCodeService.getMpQrCode(QrCodeTypeEnum.GOODS_ITEM,paramStr);
    }

    @Override
    void createPictorialImg(BufferedImage qrCodeBufferImg, BufferedImage goodsImg, PictorialUserInfo userInfo, String shareDoc, Record aRecord, GoodsRecord goodsRecord, ShopRecord shop, GoodsShareBaseParam baseParam, GoodsPictorialInfo goodsPictorialInfo) {
        PictorialImgPx imgPx = new PictorialImgPx(getShopStyleColor());

        // 拼装背景图
        BufferedImage bgBufferedImage = pictorialService.createPictorialBgImage(userInfo, shop, qrCodeBufferImg, goodsImg, shareDoc, goodsRecord.getGoodsName(),null,null,convertPriceWithFlag(shop.getShopLanguage(), baseParam.getRealPrice()),convertPriceWithFlag(shop.getShopLanguage(),baseParam.getLinePrice()), imgPx);

        //定金膨胀文字
        String preSaleText = Util.translateMessage(shop.getShopLanguage(), JsonResultMessage.WX_MA_PRESALE_SHARE_INFO, "messages");

        PreSaleShareInfoParam param= (PreSaleShareInfoParam) baseParam;
        // 定金：33.55
        String depositPriceText = Util.translateMessage(shop.getShopLanguage(), JsonResultMessage.WX_MA_PRESALE_DEPOSIT, null, "messages", param.getDepositPrice().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        pictorialService.addPictorialSelfCustomerContent(bgBufferedImage, preSaleText, depositPriceText, null, true, imgPx);

        String base64 = ImageUtil.toBase64(bgBufferedImage);
        goodsPictorialInfo.setBase64(base64);
        goodsPictorialInfo.setBgImg(bgBufferedImage);
    }

    @Override
     String createDefaultShareDoc(String lang, Record aRecord, GoodsRecord goodsRecord, GoodsShareBaseParam baseParam) {
        PreSaleShareInfoParam param= (PreSaleShareInfoParam) baseParam;
        return Util.translateMessage(lang, JsonResultMessage.WX_MA_PRESALE_SHARE_DOC, "", "messages", param.getDepositPrice().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
    }

    @Override
    protected String getActivityName() {
        return "presale";
    }
}
