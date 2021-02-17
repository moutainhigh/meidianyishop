package com.meidianyi.shop.service.shop.image.postertraits;

import com.upyun.UpException;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.PictorialRecord;
import com.meidianyi.shop.db.shop.tables.records.SecKillDefineRecord;
import com.meidianyi.shop.service.foundation.util.ImageUtil;
import com.meidianyi.shop.service.pojo.shop.config.PictorialShareConfig;
import com.meidianyi.shop.service.pojo.shop.qrcode.QrCodeTypeEnum;
import com.meidianyi.shop.service.pojo.wxapp.share.*;
import com.meidianyi.shop.service.pojo.wxapp.share.seckill.SeckillShareInfoParam;
import com.meidianyi.shop.service.shop.market.seckill.SeckillService;
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
 * 秒杀活动分享图片生成器
 * @author 李晓冰
 * @date 2020年04月22日
 */
@Service
public class SeckillPictorialShareService extends BaseShareService {
    @Autowired
    private SeckillService seckillService;

    @Override
     Record getActivityRecord(Integer activityId) {
        return   seckillService.getSeckillActById(activityId);
    }

    @Override
     PictorialShareConfig getPictorialConfig(Record aRecord, GoodsRecord goodsRecord) {
        SecKillDefineRecord record = (SecKillDefineRecord) aRecord;
        return Util.parseJson(record.getShareConfig(), PictorialShareConfig.class);
    }

    /**
     * 拼团分享图片地址
     */
    private static final String SECKILL_BG_IMG = "image/wxapp/sec_kill_bg.jpg";
    @Override
     String createShareImage(Record aRecord, GoodsRecord goodsRecord, GoodsShareBaseParam baseParam) {
        SecKillDefineRecord secKillDefineRecord = (SecKillDefineRecord) aRecord;
        SeckillShareInfoParam param = (SeckillShareInfoParam) baseParam;
        Color shopStyleColor = getShopStyleColor();

        PictorialRecord pictorialRecord = pictorialService.getPictorialDao(goodsRecord.getGoodsId(), param.getActivityId(), PictorialConstant.SECKILL_ACTION_SHARE, param.getUserId());
        // 已存在生成的图片
        if (pictorialRecord != null && pictorialService.isGoodsSharePictorialRecordCanUse(pictorialRecord.getRule(), goodsRecord.getUpdateTime(), secKillDefineRecord.getUpdateTime(),shopStyleColor)) {
            return pictorialRecord.getPath();
        }
        try (InputStream bgInputStream = Util.loadFile(SECKILL_BG_IMG)) {
            BufferedImage bgBufferImg = ImageIO.read(bgInputStream);
            BufferedImage goodsBufferImg = ImageIO.read(new URL(imageService.getImgFullUrl(goodsRecord.getGoodsImg())));

            int goodsWidth = 162;
            int toTop = 85;
            int toLeft = 80;

            goodsBufferImg = ImageUtil.resizeImage(goodsWidth, goodsWidth, goodsBufferImg);
            ImageUtil.addTwoImage(bgBufferImg, goodsBufferImg, toTop, toLeft);

            ShopRecord shop = saas.shop.getShopById(getShopId());
            String moneyFlag = Util.translateMessage(shop.getShopLanguage(), JsonResultMessage.WX_MA_PICTORIAL_MONEY_FLAG, "messages");
            String realPrice = moneyFlag+param.getRealPrice().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
            String linePrice = moneyFlag+param.getLinePrice().setScale(2, BigDecimal.ROUND_HALF_UP).toString();

            int textStartX = toLeft + goodsWidth + 20;

            PictorialImgPx imgPx = new PictorialImgPx(shopStyleColor);
            // "秒杀" 文字
            String seckillText = Util.translateMessage(shop.getShopLanguage(), JsonResultMessage.WX_MA_SECKILL, null, "messages");
            ImageUtil.addFontWithRect(bgBufferImg,  textStartX, toTop + 20, seckillText, ImageUtil.sourceHanSansCn(Font.PLAIN, 16), imgPx.getRealPriceColor(), imgPx.getShareImgRectInnerColor(), imgPx.getRealPriceColor());

            // 添加秒杀价￥
            ImageUtil.addFont(bgBufferImg, realPrice, ImageUtil.sourceHanSansCn(Font.PLAIN, 20), textStartX, toTop + 80, imgPx.getRealPriceColor() );
            // 添加划线价￥
            ImageUtil.addFontWithLine(bgBufferImg, textStartX, toTop + 100, linePrice, ImageUtil.sourceHanSansCn(Font.PLAIN, 18),PictorialImgPx.LINE_PRICE_COLOR);

            // 上传u盘云并缓存入库
            String relativePath = createFilePath(secKillDefineRecord.getSkId());
            PictorialRule pictorialRule = new PictorialRule(goodsRecord.getUpdateTime(), secKillDefineRecord.getUpdateTime(),shopStyleColor.getRed(),shopStyleColor.getGreen(),shopStyleColor.getBlue());
            pictorialService.uploadToUpanYun(bgBufferImg, relativePath, pictorialRule, goodsRecord.getGoodsId(), param.getActivityId(),PictorialConstant.SECKILL_ACTION_SHARE,pictorialRecord, param.getUserId());
            return relativePath;
        } catch (IOException e) {
            shareLog(getActivityName(), "图片生成错误：" + e.getMessage());
        } catch (UpException e) {
            shareLog(getActivityName(),"UpanYun上传错误：" + e.getMessage());
        }
        return null;
    }

    @Override
    String createMpQrCode(Record aRecord, GoodsRecord goodsRecord, GoodsShareBaseParam baseParam) {
        SecKillDefineRecord secKillDefineRecord = (SecKillDefineRecord) aRecord;
        SceneValueBase sceneValueBase = new SceneValueBase(baseParam.getUserId(), goodsRecord.getGoodsId(), secKillDefineRecord.getSkId(), BaseConstant.ACTIVITY_TYPE_SEC_KILL, baseParam.getShareAwardId());
        String paramStr = addAndGetSceneStr(sceneValueBase);
        return qrCodeService.getMpQrCode(QrCodeTypeEnum.GOODS_ITEM, paramStr);
    }

    @Override
    void createPictorialImg(BufferedImage qrCodeBufferImg, BufferedImage goodsImg, PictorialUserInfo userInfo, String shareDoc, Record aRecord, GoodsRecord goodsRecord, ShopRecord shop, GoodsShareBaseParam baseParam, GoodsPictorialInfo goodsPictorialInfo) {
        PictorialImgPx imgPx = new PictorialImgPx(getShopStyleColor() );
        // 活动价
        String realPriceText = convertPriceWithFlag(shop.getShopLanguage(), baseParam.getRealPrice());
        // 划线价格
        String linePriceText = convertPriceWithFlag(shop.getShopLanguage(), baseParam.getLinePrice());
        // 拼装背景图
        BufferedImage bgBufferedImage = pictorialService.createPictorialBgImage(userInfo, shop, qrCodeBufferImg, goodsImg, shareDoc, goodsRecord.getGoodsName(),null,null, realPriceText,linePriceText, imgPx);

        //秒杀文字
        String seckillText =Util.translateMessage(shop.getShopLanguage(), JsonResultMessage.WX_MA_SECKILL, null, "messages");

        pictorialService.addPictorialSelfCustomerContent(bgBufferedImage, seckillText, realPriceText, linePriceText, true, imgPx);

        String base64 = ImageUtil.toBase64(bgBufferedImage);
        goodsPictorialInfo.setBase64(base64);
        goodsPictorialInfo.setBgImg(bgBufferedImage);
    }

    @Override
     String createDefaultShareDoc(String lang, Record aRecord, GoodsRecord goodsRecord, GoodsShareBaseParam baseParam) {
        return  Util.translateMessage(lang, JsonResultMessage.WX_MA_SECKILL_DOC, "", "messages",  baseParam.getRealPrice().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
    }

    @Override
    protected String getActivityName() {
        return "seckill";
    }
}
