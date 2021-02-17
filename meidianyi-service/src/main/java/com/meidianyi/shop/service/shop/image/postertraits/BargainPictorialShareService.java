package com.meidianyi.shop.service.shop.image.postertraits;

import com.upyun.UpException;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.db.shop.tables.records.BargainRecord;
import com.meidianyi.shop.db.shop.tables.records.CodeRecord;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.PictorialRecord;
import com.meidianyi.shop.service.foundation.util.ImageUtil;
import com.meidianyi.shop.service.pojo.shop.config.PictorialShareConfig;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import com.meidianyi.shop.service.pojo.shop.qrcode.QrCodeTypeEnum;
import com.meidianyi.shop.service.pojo.wxapp.market.bargain.BargainRecordInfo;
import com.meidianyi.shop.service.pojo.wxapp.share.*;
import com.meidianyi.shop.service.pojo.wxapp.share.bargain.BargainSceneValue;
import com.meidianyi.shop.service.pojo.wxapp.share.bargain.BargainShareInfoParam;
import com.meidianyi.shop.service.shop.market.bargain.BargainService;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;

import static com.meidianyi.shop.db.shop.tables.Code.CODE;
import static java.lang.String.format;

/**
 * 砍价分享图片生成器
 *
 * @author 李晓冰
 * @date 2020年01月03日
 */
@Service
public class BargainPictorialShareService extends BaseShareService {
    @Autowired
    private BargainService bargainService;


    @Override
    Record getActivityRecord(Integer activityId) {
        return bargainService.getBargainActById(activityId);
    }

    @Override
    PictorialShareConfig getPictorialConfig(Record aRecord, GoodsRecord goodsRecord) {
        BargainRecord bargainRecord = (BargainRecord) aRecord;
        return Util.parseJson(bargainRecord.getShareConfig(), PictorialShareConfig.class);
    }

    /**
     * 砍价商品详情分享图片地址
     */
    private static final String BARGAIN_BG_IMG = "image/wxapp/bargain_bg.png";

    /**
     * 创建分享图片
     *
     * @param aRecord     分享活动
     * @param goodsRecord 分享的商品
     * @param baseParam   分享请求参数
     * @return 图片路径
     */
    @Override
    String createShareImage(Record aRecord, GoodsRecord goodsRecord, GoodsShareBaseParam baseParam) {
        BargainRecord bargainRecord = (BargainRecord) aRecord;
        BargainShareInfoParam param = (BargainShareInfoParam) baseParam;
        Color shopStyleColor = getShopStyleColor();

        PictorialRecord pictorialRecord = pictorialService.getPictorialDao(goodsRecord.getGoodsId(), param.getActivityId(), PictorialConstant.BARGAIN_ACTION_SHARE, null);
        // 已存在生成的图片
        if (pictorialRecord != null && pictorialService.isGoodsSharePictorialRecordCanUse(pictorialRecord.getRule(), goodsRecord.getUpdateTime(), bargainRecord.getUpdateTime(),shopStyleColor)) {
            return pictorialRecord.getPath();
        }

        try (InputStream bgInputStream = Util.loadFile(BARGAIN_BG_IMG)) {

            BufferedImage bgBufferImg = ImageIO.read(bgInputStream);
            BufferedImage goodsBufferImg = ImageIO.read(new URL(imageService.getImgFullUrl(goodsRecord.getGoodsImg())));

            goodsBufferImg = ImageUtil.resizeImage(132, 132, goodsBufferImg);
            ImageUtil.addTwoImage(bgBufferImg, goodsBufferImg, 60, 120);

            ShopRecord shop = saas.shop.getShopById(getShopId());
            // 添加商品名称
            ImageUtil.addFont(bgBufferImg, goodsRecord.getGoodsName(), ImageUtil.sourceHanSansCn(Font.PLAIN, 14), 200, 140, new Color(51, 51, 51));

            // 添加原价
            String realPriceStr = param.getRealPrice().setScale(2, BigDecimal.ROUND_HALF_UP) + Util.translateMessage(shop.getShopLanguage(), JsonResultMessage.WX_MA_PICTORIAL_MONEY, "messages");
            ImageUtil.addFont(bgBufferImg, realPriceStr, ImageUtil.sourceHanSansCn(Font.PLAIN, 23), 200, 180, shopStyleColor, false);
            Integer realPriceLength = ImageUtil.getTextWidth(bgBufferImg, ImageUtil.sourceHanSansCn(Font.PLAIN, 23), realPriceStr);
            // 添加划线价
            String linePriceStr = param.getLinePrice().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
            ImageUtil.addFontWithLine(bgBufferImg, 200 + realPriceLength + 5, 185, linePriceStr, ImageUtil.sourceHanSansCn(Font.PLAIN, 18), PictorialImgPx.LINE_PRICE_COLOR);

            // 上传u盘云并缓存入库
            String relativePath = createFilePath(bargainRecord.getId());
            PictorialRule pictorialRule = new PictorialRule(goodsRecord.getUpdateTime(), bargainRecord.getUpdateTime(),shopStyleColor.getRed(),shopStyleColor.getGreen(),shopStyleColor.getBlue());
            pictorialService.uploadToUpanYun(bgBufferImg, relativePath, pictorialRule, goodsRecord.getGoodsId(), param.getActivityId(), PictorialConstant.BARGAIN_ACTION_SHARE, pictorialRecord, param.getUserId());

            return relativePath;
        } catch (IOException e) {
            shareLog(getActivityName(), "图片生成错误：" + e.getMessage());
        } catch (UpException e) {
            shareLog(getActivityName(), "UpanYun上传错误：" + e.getMessage());
        }
        return null;
    }

    @Override
    String createDefaultShareDoc(String lang, Record aRecord, GoodsRecord goodsRecord, GoodsShareBaseParam baseParam) {
        return Util.translateMessage(lang, JsonResultMessage.WX_MA_BARGAIN_DOC, "messages", baseParam.getRealPrice().setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    /**
     * 砍价海报中的硬币图片
     */
    private static final String BARGAIN_MONEY_ICON_IMG = "image/wxapp/money_icon.png";

    @Override
    String createMpQrCode(Record aRecord, GoodsRecord goodsRecord, GoodsShareBaseParam baseParam) {
        BargainShareInfoParam param = (BargainShareInfoParam) baseParam;
        BargainRecord bargainRecord = (BargainRecord) aRecord;
        String mpQrCode;
        if (GoodsConstant.GOODS_ITEM.equals(param.getPageType())) {
            SceneValueBase sceneValueBase = new SceneValueBase(param.getUserId(), goodsRecord.getGoodsId(), bargainRecord.getId(), BaseConstant.ACTIVITY_TYPE_BARGAIN, param.getShareAwardId());
            String sceneParam = addAndGetSceneStr(sceneValueBase);
            mpQrCode = qrCodeService.getMpQrCode(QrCodeTypeEnum.GOODS_ITEM, sceneParam);
        } else {
            BargainSceneValue sceneValue = new BargainSceneValue();
            sceneValue.setUid(param.getUserId());
            sceneValue.setRecordId(param.getRecordId());
            String sceneParam = addAndGetSceneStr(sceneValue);
            mpQrCode = qrCodeService.getMpQrCode(QrCodeTypeEnum.POSTER_BARGAIN_INFO,sceneParam);
        }

        return mpQrCode;
    }

    @Override
    void createPictorialImg(BufferedImage qrCodeBufferImg, BufferedImage goodsImage, PictorialUserInfo userInfo, String shareDoc, Record aRecord, GoodsRecord goodsRecord, ShopRecord shop, GoodsShareBaseParam baseParam, GoodsPictorialInfo goodsPictorialInfo) {
        // 拼装背景图
        PictorialImgPx imgPx = new PictorialImgPx(getShopStyleColor());
        BufferedImage bgBufferedImage = pictorialService.createPictorialBgImage(userInfo, shop, qrCodeBufferImg, goodsImage, shareDoc, goodsRecord.getGoodsName(),null,null, convertPriceWithFlag(shop.getShopLanguage(),baseParam.getRealPrice()),  convertPriceWithFlag(shop.getShopLanguage(),baseParam.getLinePrice()), imgPx);

        BufferedImage moneyIconImg;
        try (InputStream moneyIconIo = Util.loadFile(BARGAIN_MONEY_ICON_IMG)) {
            moneyIconImg = ImageIO.read(moneyIconIo);
        } catch (IOException e) {
            pictorialLog(getActivityName(), "读取本地图片money_icon错误" + e.getMessage());
            goodsPictorialInfo.setPictorialCode(PictorialConstant.GOODS_PIC_ERROR);
            return;
        }
        // 原价
        String realPriceText = Util.translateMessage(shop.getShopLanguage(), JsonResultMessage.WX_MA_BARGAIN_TAKE, null, "messages", baseParam.getRealPrice().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        // 划线价
        String linePriceText = convertPriceWithFlag(shop.getShopLanguage(), baseParam.getLinePrice());
        // 自定义区域添加内容
        pictorialService.addPictorialSelfCustomerContent(bgBufferedImage, moneyIconImg, realPriceText, linePriceText, true, imgPx);

        String base64 = ImageUtil.toBase64(bgBufferedImage);
        goodsPictorialInfo.setBase64(base64);
        goodsPictorialInfo.setBgImg(bgBufferedImage);
    }


    /**
     * 砍价活动砍价详情-分享图片生成
     *
     * @param bargainRecordInfo 砍价发起信息
     *                          bargain
     * @return 砍价分享图片信息
     */
    public String getBargainInfoShareImg(BargainRecordInfo bargainRecordInfo) {
        // 获取分享码
        CodeRecord mpQrCode = qrCodeService.getMpQrCode(QrCodeTypeEnum.BARGAIN_INFO_SHARE, bargainRecordInfo.getId());
        if (mpQrCode == null || bargainRecordInfo.getUpdateTime().after(mpQrCode.getCreateTime())) {
            String relativePath = createBargainInfoShareImg(bargainRecordInfo);
            return relativePath;
        } else {
            return mpQrCode.getQrcodeImg();
        }
    }

    /**
     * 砍价详情分享图背景图
     */
    private static final String BARGAIN_INFO_BG_IMG = "image/wxapp/bargain_bg.png";

    /**
     * 生成砍价详情分享图
     *
     * @param bargainRecordInfo
     * @return
     */
    private String createBargainInfoShareImg(BargainRecordInfo bargainRecordInfo) {

        String dir = qrCodeService.getQrCodeImgRelativePath(QrCodeTypeEnum.BARGAIN_INFO_SHARE.getType());
        String fileName = format("T%sP%s_%s.jpg", QrCodeTypeEnum.BARGAIN_INFO_SHARE.getType(), bargainRecordInfo.getId(), DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL_NO_UNDERLINE));

        try (InputStream bgInputStream = Util.loadFile(BARGAIN_INFO_BG_IMG)) {

            BufferedImage bgBufferImg = ImageIO.read(bgInputStream);
            BufferedImage goodsBufferImg = ImageIO.read(new URL(imageService.getImgFullUrl(bargainRecordInfo.getGoodsImg())));

            goodsBufferImg = ImageUtil.resizeImage(132, 132, goodsBufferImg);
            ImageUtil.addTwoImage(bgBufferImg, goodsBufferImg, 60, 120);

            ShopRecord shop = saas.shop.getShopById(getShopId());
            // 添加商品名称
            ImageUtil.addFont(bgBufferImg, bargainRecordInfo.getGoodsName(), ImageUtil.sourceHanSansCn(Font.PLAIN, 14), 200, 140, new Color(51, 51, 51));

            // 活动价
            BigDecimal bargainPrice = bargainRecordInfo.getBargainType().equals(BargainService.BARGAIN_TYPE_RANDOM) ? bargainRecordInfo.getFloorPrice() : bargainRecordInfo.getExpectationPrice();
            String bargainPriceString = bargainPrice.setScale(2, BigDecimal.ROUND_HALF_UP) + Util.translateMessage(shop.getShopLanguage(), JsonResultMessage.WX_MA_PICTORIAL_MONEY, "messages");
            ImageUtil.addFont(bgBufferImg, bargainPriceString, ImageUtil.sourceHanSansCn(Font.PLAIN, 23), 200, 250, new Color(255, 102, 102));


            Integer bargainPriceLength = ImageUtil.getTextWidth(bgBufferImg, ImageUtil.sourceHanSansCn(Font.PLAIN, 23), bargainPriceString);
            // 添加划线价
            String linePriceStr = bargainRecordInfo.getGoodsPrice().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
            ImageUtil.addFont(bgBufferImg, linePriceStr, ImageUtil.sourceHanSansCn(Font.PLAIN, 13), 220 + bargainPriceLength, 251, new Color(153, 153, 153));
            Integer linePriceLength = ImageUtil.getTextWidth(bgBufferImg, ImageUtil.sourceHanSansCn(Font.PLAIN, 10), linePriceStr);
            ImageUtil.addLine(bgBufferImg, 220 + bargainPriceLength, 245, 222 + bargainPriceLength + linePriceLength, 245, new Color(153, 153, 153));

            // 上传u盘云并缓存入库
            String relativePath = dir + fileName;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bgBufferImg, "jpg", byteArrayOutputStream);
            // 上传又拍云
            imageService.getUpYunClient().writeFile(relativePath, byteArrayOutputStream.toByteArray(), true);

            CodeRecord codeRecord = db().newRecord(CODE);
            codeRecord.setType(QrCodeTypeEnum.BARGAIN_INFO_SHARE.getType());
            codeRecord.setParamId(bargainRecordInfo.getId().toString());
            codeRecord.setTypeUrl("");
            codeRecord.setQrcodeImg(relativePath);
            codeRecord.insert();

            return relativePath;
        } catch (IOException | UpException e) {
            logger().error("分享图片生成错误", e);
        }
        return null;
    }


    @Override
    protected String getActivityName() {
        return "砍价";
    }
}
