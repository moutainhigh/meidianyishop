package com.meidianyi.shop.service.shop.image.postertraits;

import com.upyun.UpException;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.GroupDrawRecord;
import com.meidianyi.shop.db.shop.tables.records.PictorialRecord;
import com.meidianyi.shop.service.foundation.util.ImageUtil;
import com.meidianyi.shop.service.pojo.shop.config.PictorialShareConfig;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsSharePostConfig;
import com.meidianyi.shop.service.pojo.shop.qrcode.QrCodeTypeEnum;
import com.meidianyi.shop.service.pojo.wxapp.share.*;
import com.meidianyi.shop.service.pojo.wxapp.share.group.GroupDrawShareInfoParam;
import com.meidianyi.shop.service.shop.market.groupdraw.GroupDrawService;
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
 * 拼团抽将分享图片生成器
 *
 * @author 李晓冰
 * @date 2020年02月03日
 */
@Service
public class GroupDrawPictorialShareService extends BaseShareService {
    @Autowired
    private GroupDrawService groupDrawService;

    @Override
     Record getActivityRecord(Integer activityId) {
        return  groupDrawService.getById(activityId);
    }

    @Override
     PictorialShareConfig getPictorialConfig(Record aRecord, GoodsRecord goodsRecord) {
        GoodsSharePostConfig goodsShareConfig = Util.parseJson(goodsRecord.getShareConfig(), GoodsSharePostConfig.class);
        return PictorialShareConfig.createFromGoodsShareInfoConfig(goodsShareConfig);
    }

    /**
     * 拼团抽奖分享背景图片地址
     */
    private static final String GROUP_DRAW_BG_IMG = "image/wxapp/group_draw.png";

    @Override
     String createShareImage(Record aRecord, GoodsRecord goodsRecord, GoodsShareBaseParam baseParam) {
        GroupDrawRecord groupDrawRecord= (GroupDrawRecord) aRecord;
        GroupDrawShareInfoParam param = (GroupDrawShareInfoParam) baseParam;
        Color shopStyleColor = getShopStyleColor();

        PictorialRecord pictorialRecord = pictorialService.getPictorialDao(goodsRecord.getGoodsId(), param.getActivityId(), PictorialConstant.GROUP_DRAW_ACTION_SHARE, null);
        // 已存在生成的图片
        if (pictorialRecord != null && pictorialService.isGoodsSharePictorialRecordCanUse(pictorialRecord.getRule(), goodsRecord.getUpdateTime(), groupDrawRecord.getUpdateTime(),shopStyleColor)) {
            return pictorialRecord.getPath();
        }

        try (InputStream bgInputStream = Util.loadFile(GROUP_DRAW_BG_IMG)) {

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
            //添加拼团抽奖文字
            String msg = Util.translateMessage(shop.getShopLanguage(), JsonResultMessage.WX_MA_GROUP_DRAW_SHARE_INFO, "messages");
            ImageUtil.addFontWithRect(bgBufferImg, textStartX, toTop + 20, msg, ImageUtil.sourceHanSansCn(Font.PLAIN, 16), imgPx.getRealPriceColor(),imgPx.getShareImgRectInnerColor(), imgPx.getRealPriceColor());

            //添加真实价格
            String moneyFlag = Util.translateMessage(shop.getShopLanguage(), JsonResultMessage.WX_MA_PICTORIAL_MONEY_FLAG, "messages");
            String realPrice = param.getRealPrice().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
            ImageUtil.addFont(bgBufferImg, moneyFlag + realPrice, ImageUtil.sourceHanSansCn(Font.PLAIN, 20), textStartX, toTop + 80,imgPx.getRealPriceColor());
            //添加划线价格
            String linePrice = param.getLinePrice().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
            ImageUtil.addFontWithLine(bgBufferImg, textStartX, toTop + 100, moneyFlag+linePrice, ImageUtil.sourceHanSansCn(Font.PLAIN, 18),PictorialImgPx.LINE_PRICE_COLOR);

            // 上传u盘云并缓存入库
            String relativePath = createFilePath(groupDrawRecord.getId());
            PictorialRule pictorialRule = new PictorialRule(goodsRecord.getUpdateTime(), groupDrawRecord.getUpdateTime(),shopStyleColor.getRed(),shopStyleColor.getGreen(),shopStyleColor.getBlue());
            pictorialService.uploadToUpanYun(bgBufferImg, relativePath, pictorialRule, goodsRecord.getGoodsId(),param.getActivityId(),PictorialConstant.GROUP_DRAW_ACTION_SHARE, pictorialRecord, param.getUserId());

            return relativePath;
        } catch (IOException e) {
            shareLog(getActivityName(),  "图片生成错误：" + e.getMessage());
        } catch (UpException e) {
            shareLog(getActivityName() , "UpanYun上传错误：" + e.getMessage());
        }
        return null;
    }

    @Override
    String createMpQrCode(Record aRecord, GoodsRecord goodsRecord, GoodsShareBaseParam baseParam) {
        GroupDrawShareInfoParam param = (GroupDrawShareInfoParam) baseParam;
        GroupDrawRecord groupDrawRecord = (GroupDrawRecord) aRecord;

        SceneValueBase sceneValueBase = new SceneValueBase(param.getUserId(), goodsRecord.getGoodsId(), groupDrawRecord.getId(), BaseConstant.ACTIVITY_TYPE_GROUP_DRAW, baseParam.getShareAwardId());
        String paramStr = addAndGetSceneStr(sceneValueBase);
        if (GoodsConstant.GOODS_ITEM.equals(param.getPageType())) {
           return qrCodeService.getMpQrCode(QrCodeTypeEnum.GOODS_ITEM, paramStr);
        } else {
            return qrCodeService.getMpQrCode(QrCodeTypeEnum.LOTTERY, paramStr);
        }
    }

    @Override
    void createPictorialImg(BufferedImage qrCodeBufferImg, BufferedImage goodsImg, PictorialUserInfo userInfo, String shareDoc, Record aRecord, GoodsRecord goodsRecord, ShopRecord shop, GoodsShareBaseParam baseParam, GoodsPictorialInfo goodsPictorialInfo) {
        PictorialImgPx imgPx = new PictorialImgPx(getShopStyleColor());
        // 活动价
        String realPriceText = convertPriceWithFlag(shop.getShopLanguage(), baseParam.getRealPrice());
        // 划线价格
        String linePriceText = convertPriceWithFlag(shop.getShopLanguage(), baseParam.getLinePrice());
        // 拼装背景图
        BufferedImage bgBufferedImage = pictorialService.createPictorialBgImage(userInfo, shop, qrCodeBufferImg, goodsImg, shareDoc, goodsRecord.getGoodsName(),null,null,realPriceText,linePriceText, imgPx);

        // 拼团抽奖文字
        String groupDrawText = Util.translateMessage(shop.getShopLanguage(), JsonResultMessage.WX_MA_GROUP_DRAW_SHARE_INFO, "messages");
        pictorialService.addPictorialSelfCustomerContent(bgBufferedImage, groupDrawText, realPriceText, linePriceText, true, imgPx);
        String base64 = ImageUtil.toBase64(bgBufferedImage);
        goodsPictorialInfo.setBase64(base64);
        goodsPictorialInfo.setBgImg(bgBufferedImage);
    }

    @Override
     String createDefaultShareDoc(String lang, Record aRecord, GoodsRecord goodsRecord, GoodsShareBaseParam baseParam) {
        return Util.translateMessage(lang, JsonResultMessage.WX_MA_GROUP_DRAW_SHARE_DOC, "","messages", baseParam.getRealPrice());
    }

    @Override
    protected String getActivityName() {
        return "group_draw";
    }
}
