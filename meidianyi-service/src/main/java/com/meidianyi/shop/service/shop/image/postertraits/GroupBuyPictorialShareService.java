package com.meidianyi.shop.service.shop.image.postertraits;

import com.upyun.UpException;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.GroupBuyDefineRecord;
import com.meidianyi.shop.db.shop.tables.records.PictorialRecord;
import com.meidianyi.shop.service.foundation.util.ImageUtil;
import com.meidianyi.shop.service.pojo.shop.config.PictorialShareConfig;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import com.meidianyi.shop.service.pojo.shop.qrcode.QrCodeTypeEnum;
import com.meidianyi.shop.service.pojo.wxapp.share.*;
import com.meidianyi.shop.service.pojo.wxapp.share.groupbuy.GroupBuySceneValue;
import com.meidianyi.shop.service.pojo.wxapp.share.groupbuy.GroupBuyShareInfoParam;
import com.meidianyi.shop.service.shop.market.goupbuy.GroupBuyService;
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
 * 拼团图片
 *
 * @author 李晓冰
 * @date 2019/12/12 18:00
 */
@Service
public class GroupBuyPictorialShareService extends BaseShareService {
    @Autowired
    private GroupBuyService groupBuyService;

    @Override
     Record getActivityRecord(Integer activityId) {
        return  groupBuyService.getGroupBuyRecord(activityId);
    }

    @Override
     PictorialShareConfig getPictorialConfig(Record aRecord, GoodsRecord goodsRecord) {
        GroupBuyDefineRecord record= (GroupBuyDefineRecord) aRecord;
        return Util.parseJson(record.getShareConfig(), PictorialShareConfig.class);
    }

    /**
     * 拼团分享图片地址
     */
    private static final String PIN_GROUP_BG_IMG = "image/wxapp/pin_group_bg.jpg";

    @Override
     String createShareImage(Record aRecord, GoodsRecord goodsRecord, GoodsShareBaseParam baseParam) {
        GroupBuyDefineRecord groupBuyDefineRecord = (GroupBuyDefineRecord) aRecord;
        GroupBuyShareInfoParam param = (GroupBuyShareInfoParam) baseParam;
        Color shopStyleColor = getShopStyleColor();

        PictorialRecord pictorialRecord = pictorialService.getPictorialDao(goodsRecord.getGoodsId(), param.getActivityId(), PictorialConstant.GROUP_BUY_ACTION_SHARE, param.getUserId());
        // 已存在生成的图片
        if (pictorialRecord != null && pictorialService.isGoodsSharePictorialRecordCanUse(pictorialRecord.getRule(), goodsRecord.getUpdateTime(), groupBuyDefineRecord.getUpdateTime(),shopStyleColor)) {
            return pictorialRecord.getPath();
        }

        try (InputStream bgInputStream = Util.loadFile(PIN_GROUP_BG_IMG)) {
            BufferedImage bgBufferImg = ImageIO.read(bgInputStream);
            BufferedImage goodsBufferImg = ImageIO.read(new URL(imageService.getImgFullUrl(goodsRecord.getGoodsImg())));

            goodsBufferImg = ImageUtil.resizeImage(162, 162, goodsBufferImg);
            ImageUtil.addTwoImage(bgBufferImg, goodsBufferImg, 80, 85);

            String saveMoney = param.getLinePrice().subtract(param.getRealPrice()).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
            ShopRecord shop = saas.shop.getShopById(getShopId());
            String moneyFlag = Util.translateMessage(shop.getShopLanguage(), JsonResultMessage.WX_MA_PICTORIAL_MONEY_FLAG, "messages");
            String linePrice =moneyFlag+ param.getLinePrice().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
            String realPrice =moneyFlag+ param.getRealPrice().setScale(2, BigDecimal.ROUND_HALF_UP).toString();


            PictorialImgPx imgPx = new PictorialImgPx(shopStyleColor);
            // "开团省2元" 文字
            String startGroupMoneyText = Util.translateMessage(shop.getShopLanguage(), JsonResultMessage.WX_MA_GROUP_BUY_SAVE, null, "messages", saveMoney);
            ImageUtil.addFontWithRect(bgBufferImg, 250, 130, startGroupMoneyText, ImageUtil.sourceHanSansCn(Font.PLAIN, 18), imgPx.getRealPriceColor(), imgPx.getShareImgRectInnerColor(), imgPx.getRealPriceColor());

            // 添加拼团价￥
            ImageUtil.addFont(bgBufferImg, realPrice, ImageUtil.sourceHanSansCn(Font.PLAIN, 20), 250, 200, imgPx.getRealPriceColor());
            // 添加划线价￥
            ImageUtil.addFontWithLine(bgBufferImg,250,220,linePrice,ImageUtil.sourceHanSansCn(Font.PLAIN, 18),PictorialImgPx.LINE_PRICE_COLOR);

            // 上传u盘云并缓存入库
            String relativePath = createFilePath(groupBuyDefineRecord.getId());
            PictorialRule pictorialRule = new PictorialRule(goodsRecord.getUpdateTime(), groupBuyDefineRecord.getUpdateTime(),shopStyleColor.getRed(),shopStyleColor.getGreen(),shopStyleColor.getBlue());
            pictorialService.uploadToUpanYun(bgBufferImg, relativePath, pictorialRule, goodsRecord.getGoodsId(), param.getActivityId(), PictorialConstant.GROUP_BUY_ACTION_SHARE, pictorialRecord, param.getUserId());

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
        GroupBuyShareInfoParam param = (GroupBuyShareInfoParam) baseParam;
        GroupBuyDefineRecord groupBuyDefineRecord = (GroupBuyDefineRecord) aRecord;

        if (GoodsConstant.GOODS_ITEM.equals(param.getPageType())) {
            SceneValueBase sceneValueBase = new SceneValueBase(baseParam.getUserId(), goodsRecord.getGoodsId(), groupBuyDefineRecord.getId(), BaseConstant.ACTIVITY_TYPE_GROUP_BUY, baseParam.getShareAwardId());
            String paramStr = addAndGetSceneStr(sceneValueBase);
            return qrCodeService.getMpQrCode(QrCodeTypeEnum.GOODS_ITEM,paramStr);
        } else {
            GroupBuySceneValue sceneValue = new GroupBuySceneValue();
            sceneValue.setUid(baseParam.getUserId());
            sceneValue.setGroupId(param.getGroupId());
            String paramStr = addAndGetSceneStr(sceneValue);
            return qrCodeService.getMpQrCode(QrCodeTypeEnum.POSTER_GROUP_BOOKING_INFO, paramStr);
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
        BufferedImage bgBufferedImage = pictorialService.createPictorialBgImage(userInfo, shop, qrCodeBufferImg, goodsImg, shareDoc, goodsRecord.getGoodsName(),null,null, realPriceText, linePriceText, imgPx);

        // 拼装自定义内容
        BigDecimal saveMoney = baseParam.getLinePrice().subtract(baseParam.getRealPrice()).setScale(2, BigDecimal.ROUND_HALF_UP);
        // "开团省2元" 文字
        String saveText = Util.translateMessage(shop.getShopLanguage(), JsonResultMessage.WX_MA_GROUP_BUY_SAVE, null, "messages", saveMoney);
        pictorialService.addPictorialSelfCustomerContent(bgBufferedImage, saveText, realPriceText, linePriceText, true, imgPx);

        String base64 = ImageUtil.toBase64(bgBufferedImage);
        goodsPictorialInfo.setBase64(base64);
        goodsPictorialInfo.setBgImg(bgBufferedImage);
    }

    @Override
     String createDefaultShareDoc(String lang, Record aRecord, GoodsRecord goodsRecord, GoodsShareBaseParam baseParam) {
        GroupBuyDefineRecord activityRecord = (GroupBuyDefineRecord) aRecord;
        return Util.translateMessage(lang, JsonResultMessage.WX_MA_GROUP_BUY_DOC, "", "messages", activityRecord.getLimitAmount(), baseParam.getRealPrice().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
    }

    @Override
    protected String getActivityName() {
        return "group_buy";
    }
}
