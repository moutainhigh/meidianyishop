package com.meidianyi.shop.service.shop.image.postertraits;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.GroupIntegrationDefineRecord;
import com.meidianyi.shop.service.foundation.util.ImageUtil;
import com.meidianyi.shop.service.pojo.shop.config.PictorialShareConfig;
import com.meidianyi.shop.service.pojo.shop.qrcode.QrCodeTypeEnum;
import com.meidianyi.shop.service.pojo.wxapp.share.*;
import com.meidianyi.shop.service.pojo.wxapp.share.integral.GroupIntegralSceneValue;
import com.meidianyi.shop.service.pojo.wxapp.share.integral.GroupIntegralShareInfoParam;
import com.meidianyi.shop.service.shop.market.integration.GroupIntegrationService;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

/**
 * 瓜分积分海报生成
 * @author 李晓冰
 * @date 2020年05月15日
 */
@Service
public class GroupIntegrationPictorialShareService extends BaseShareService {
    @Autowired
    GroupIntegrationService groupIntegrationService;

    @Override
    Record getActivityRecord(Integer activityId) {
        return groupIntegrationService.getOneInfoByIdNoInto(activityId);
    }

    @Override
    PictorialShareConfig getPictorialConfig(Record aRecord, GoodsRecord goodsRecord) {
        return null;
    }

    @Override
    String createDefaultShareDoc(String lang, Record aRecord, GoodsRecord goodsRecord, GoodsShareBaseParam baseParam) {
        GroupIntegrationDefineRecord record = (GroupIntegrationDefineRecord) aRecord;
        Short pNum = record.getLimitAmount();
        Integer scoreNum = record.getInteGroup();
        return Util.translateMessage(lang, JsonResultMessage.WX_MA_GROUP_INTEGRAL_SHARE_DOC, "", "messages", pNum,scoreNum);
    }

    @Override
    String createShareImage(Record aRecord, GoodsRecord goodsRecord, GoodsShareBaseParam baseParam) {
        return null;
    }

    @Override
    String createMpQrCode(Record aRecord, GoodsRecord goodsRecord, GoodsShareBaseParam baseParam) {
        GroupIntegralShareInfoParam param = (GroupIntegralShareInfoParam) baseParam;
        GroupIntegralSceneValue sceneValueBase =new GroupIntegralSceneValue();
        sceneValueBase.setPid(param.getActivityId());
        sceneValueBase.setGid(param.getGroupId());
        sceneValueBase.setInvid(param.getUserId());
        String paramStr = addAndGetSceneStr(sceneValueBase);
        return qrCodeService.getMpQrCode(QrCodeTypeEnum.PARTATION_INTEGRAL,paramStr);
    }

    @Override
    public GoodsPictorialInfo getPictorialInfo(GoodsShareBaseParam baseParam) {
        ShopRecord shop = saas.shop.getShopById(getShopId());
        GoodsPictorialInfo goodsPictorialInfo = new GoodsPictorialInfo();

        Record record = getActivityInfo(baseParam,goodsPictorialInfo);
        if (!GoodsPictorialInfo.OK.equals(goodsPictorialInfo.getPictorialCode())) {
            return goodsPictorialInfo;
        }
        String shareDoc = createDefaultShareDoc(shop.getShopLanguage(),record,null,null);

        PictorialUserInfo userInfo = getUserInfo(baseParam,shop,goodsPictorialInfo);
        if (!GoodsPictorialInfo.OK.equals(goodsPictorialInfo.getPictorialCode())) {
            return goodsPictorialInfo;
        }
        BufferedImage qrCodeImage =getQrcodInfo(null,null,baseParam,goodsPictorialInfo);
        if (!GoodsPictorialInfo.OK.equals(goodsPictorialInfo.getPictorialCode())) {
            return goodsPictorialInfo;
        }

        createPictorialImg(qrCodeImage,null,userInfo,shareDoc,record,null,shop,baseParam,goodsPictorialInfo);

        return goodsPictorialInfo;
    }

    /**
     * 瓜分积分背景图
     */
    private final String GROUP_INTEGRAL_BG_IMG = "image/wxapp/pin_score_bg.jpg";
    /**
     * 瓜分积分金币图片
     */
    private final String GROUP_INTEGRAL_SCORE_IMG = "image/wxapp/pin_score_gold.png";
    @Override
    void createPictorialImg(BufferedImage qrCodeBufferImg, BufferedImage goodsImg, PictorialUserInfo userInfo, String shareDoc, Record aRecord, GoodsRecord goodsRecord, ShopRecord shop, GoodsShareBaseParam baseParam, GoodsPictorialInfo goodsPictorialInfo) {
        // 拼装背景图
        PictorialImgPx imgPx = new PictorialImgPx(getShopStyleColor());
        GroupIntegrationDefineRecord record = (GroupIntegrationDefineRecord) aRecord;
        Integer scoreNum = record.getInteGroup();
        shareDoc = createDefaultShareDoc(shop.getShopLanguage(), aRecord, goodsRecord, baseParam);

        BufferedImage groupBgImg =null,groupGoldImg =null;
        try (InputStream groupBgImgIo = Util.loadFile(GROUP_INTEGRAL_BG_IMG);InputStream groupGoldImgIo = Util.loadFile(GROUP_INTEGRAL_SCORE_IMG)) {
            groupBgImg = ImageIO.read(groupBgImgIo);
            groupGoldImg = ImageIO.read(groupGoldImgIo);
        } catch (Exception e) {
            pictorialLog(getActivityName(), "读取本地图片错误" + e.getMessage());
            goodsPictorialInfo.setPictorialCode(PictorialConstant.GOODS_PIC_ERROR);
            return;
        }
        BufferedImage bgBufferedImage = pictorialService.createPictorialBgImage(userInfo, shop, null, groupBgImg, shareDoc, null,null , null,null, null, imgPx);

        // 添加 金币图标和积分数文字
        Integer bgMiddleX = imgPx.getBgWidth()/2;
        int goldIconStartY = (int)(imgPx.getGoodsStartY()+imgPx.getGoodsWidth()*(3.0/4)-70);
        int goldTextGap = 10;
        String scoreText = scoreNum.toString();
        Font scoreFont = ImageUtil.sourceHanSansCn(Font.BOLD,50);
        Integer scoreTextWidth = groupGoldImg.getWidth()+goldTextGap+ImageUtil.getTextWidth(bgBufferedImage,scoreFont,scoreText);

        int goldIconStartX=  bgMiddleX-scoreTextWidth/2;
        ImageUtil.addTwoImage(bgBufferedImage,groupGoldImg,goldIconStartX,goldIconStartY);
        ImageUtil.addFont(bgBufferedImage,scoreText,scoreFont,goldIconStartX+groupGoldImg.getWidth()+goldTextGap,goldIconStartY+groupGoldImg.getHeight(),Color.WHITE,true);

        // 添加 '新用户可瓜分双份'文字
        if (GroupIntegrationService.ACTIVITY_DIVIDE_TYPE_NEW.equals(record.getDivideType())) {
            String newUserTextStr = Util.translateMessage(shop.getShopLanguage(), JsonResultMessage.WX_MA_GROUP_INTEGRAL_SHARE_NEW_USER_DOC, "", "messages");
            Font textFont = ImageUtil.sourceHanSansCn(Font.BOLD,25);
            Integer newUserTextWidth = ImageUtil.getTextWidth(bgBufferedImage,textFont,newUserTextStr);
            Integer newUserTextStartX =  bgMiddleX-newUserTextWidth/2;
            int newUserTextStartY = imgPx.getGoodsStartY()+imgPx.getGoodsWidth()-130;
            ImageUtil.addFont(bgBufferedImage,newUserTextStr,textFont,newUserTextStartX,newUserTextStartY,Color.WHITE,true);
        }
        // 添加有效时间
        String startTime = DateUtils.dateFormat(DateUtils.DATE_FORMAT_DOT,record.getStartTime());
        String endTime = DateUtils.dateFormat(DateUtils.DATE_FORMAT_DOT,record.getEndTime());
        StringBuilder timeTextSb =new StringBuilder();
        timeTextSb.append(Util.translateMessage(shop.getShopLanguage(), JsonResultMessage.WX_MA_GROUP_INTEGRAL_LIMIT_TIME, "", "messages"));
        timeTextSb.append(startTime).append("-").append(endTime);
        String timeText = timeTextSb.toString();
        Font textFont = ImageUtil.sourceHanSansCn(Font.BOLD,20);
        Integer timeTextWidth = ImageUtil.getTextWidth(bgBufferedImage,textFont,timeText);
        Integer timeTextStartX =  bgMiddleX-timeTextWidth/2;
        int timeTextStartY = imgPx.getGoodsStartY()+imgPx.getGoodsWidth()-90;
        ImageUtil.addFont(bgBufferedImage,timeText,textFont,timeTextStartX,timeTextStartY,Color.WHITE,true);


        // 设置二维码
        qrCodeBufferImg = ImageUtil.resizeImageTransparent(imgPx.getQrCodeWidth(), imgPx.getQrCodeWidth(), qrCodeBufferImg);
        imgPx.setQrCodeStartX((imgPx.getBgWidth()-imgPx.getQrCodeWidth())/2);
        ImageUtil.addTwoImage(bgBufferedImage, qrCodeBufferImg, imgPx.getQrCodeStartX(), imgPx.getBottomStartY());
        // ‘瓜分积分’文字
        String bottomText = Util.translateMessage(shop.getShopLanguage(), JsonResultMessage.WX_MA_GROUP_INTEGRAL_SHARE_SCORE, "", "messages");
        Integer textWidth = ImageUtil.getTextWidth(bgBufferedImage,ImageUtil.sourceHanSansCn(Font.PLAIN,PictorialImgPx.MEDIUM_FONT_SIZE),bottomText);
        int bottomTextStartX = (imgPx.getBgWidth()-textWidth)/2;
        int bottomTextStartY =imgPx.getBottomStartY()+imgPx.getQrCodeWidth()+40;
        ImageUtil.addFont(bgBufferedImage,bottomText,ImageUtil.sourceHanSansCn(Font.PLAIN,PictorialImgPx.MEDIUM_FONT_SIZE),bottomTextStartX,bottomTextStartY,imgPx.getHeadFontColor(),true);

        String base64 = ImageUtil.toBase64(bgBufferedImage);
        goodsPictorialInfo.setBase64(base64);
        goodsPictorialInfo.setBgImg(bgBufferedImage);
    }

    @Override
    String getActivityName() {
        return null;
    }
}
