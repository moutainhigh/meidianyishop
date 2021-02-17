package com.meidianyi.shop.service.shop.image.postertraits;

import com.upyun.UpException;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.FileUtil;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.config.GoodsShareConfig;
import com.meidianyi.shop.service.pojo.shop.config.PictorialShareConfig;
import com.meidianyi.shop.service.pojo.shop.config.ShopStyleConfig;
import com.meidianyi.shop.service.pojo.shop.config.distribution.DistributionParam;
import com.meidianyi.shop.service.pojo.shop.image.UploadPath;
import com.meidianyi.shop.service.pojo.shop.image.UploadedImageDo;
import com.meidianyi.shop.service.pojo.wxapp.share.*;
import com.meidianyi.shop.service.shop.config.DistributionConfigService;
import com.meidianyi.shop.service.shop.config.ShopCommonConfigService;
import com.meidianyi.shop.service.shop.config.ShopStyleConfigService;
import com.meidianyi.shop.service.shop.distribution.MpDistributionService;
import com.meidianyi.shop.service.shop.goods.GoodsService;
import com.meidianyi.shop.service.shop.image.ImageService;
import com.meidianyi.shop.service.shop.image.QrCodeService;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 海报下载分享基类
 * @author 李晓冰
 * @date 2020年04月23日
 */
public abstract class BaseShareService extends ShopBaseService {
    @Autowired
    protected PictorialService pictorialService;
    @Autowired
    protected ImageService imageService;
    @Autowired
    protected GoodsService goodsService;
    @Autowired
    protected QrCodeService qrCodeService;
    @Autowired
    private ShopStyleConfigService shopStyleConfigService;
    @Autowired
    private ShopCommonConfigService commonConfigService;
    @Autowired
    private DistributionConfigService distributionConfigService;
    @Autowired
    private MpDistributionService distributionService;
    public static final Pattern PATTERN_NUMBER = Pattern.compile("\\d+");

    /**
     * 获取活动record信息
     * @param activityId 活动id
     * @return 活动对应的record类
     */
    abstract Record getActivityRecord(Integer activityId);

    /**
     * 获取活动或商品的分享配置信息
     * @param aRecord     活动record
     * @param goodsRecord 商品record
     * @return 分享配置信息
     */
    abstract PictorialShareConfig getPictorialConfig(Record aRecord, GoodsRecord goodsRecord);

    /**
     * 创建默认宣传语
     * @param lang        语言
     * @param aRecord     活动record
     * @param goodsRecord 商品record
     * @param baseParam   请求参数
     * @return 宣传语
     */
    abstract String createDefaultShareDoc(String lang, Record aRecord, GoodsRecord goodsRecord, GoodsShareBaseParam baseParam);

    /**
     * 创建分享图片
     * @param aRecord     分享活动
     * @param goodsRecord 分享的商品
     * @param baseParam   分享请求参数
     * @return 图片路径
     */
    abstract String createShareImage(Record aRecord, GoodsRecord goodsRecord, GoodsShareBaseParam baseParam);

    /**
     * 创建二维码图片
     * @param aRecord     分享活动
     * @param goodsRecord 分享的商品
     * @param baseParam   分享请求参数
     * @return 图片路径
     */
    abstract String createMpQrCode(Record aRecord, GoodsRecord goodsRecord, GoodsShareBaseParam baseParam);

    /**
     * 处理海报分享信息
     * @param qrCodeBufferImg
     * @param goodsImg
     * @param userInfo           用户信息
     * @param shareDoc           宣传语
     * @param aRecord
     * @param goodsRecord
     * @param shop
     * @param baseParam
     * @param goodsPictorialInfo
     */
    abstract void createPictorialImg(BufferedImage qrCodeBufferImg, BufferedImage goodsImg, PictorialUserInfo userInfo, String shareDoc, Record aRecord, GoodsRecord goodsRecord, ShopRecord shop, GoodsShareBaseParam baseParam, GoodsPictorialInfo goodsPictorialInfo);

    /**
     * 创建活动分享信息
     * @param param 分享接口参数
     * @return 分享信息
     */
    public GoodsShareInfo getShareInfo(GoodsShareBaseParam param) {
        GoodsShareInfo shareInfoVo = new GoodsShareInfo();

        Record activityRecord = null;
        if (param.getActivityId() != null) {
            activityRecord = getActivityRecord(param.getActivityId());
            // 活动信息不可用
            if (activityRecord == null) {
                shareLog(getActivityName(), "活动信息不可用");
                shareInfoVo.setShareCode(PictorialConstant.ACTIVITY_DELETED);
                return shareInfoVo;
            }
        }

        GoodsRecord goodsRecord = goodsService.getGoodsRecordById(param.getTargetId());
        // 商品信息不可用
        if (goodsRecord == null) {
            shareLog(getActivityName(), "商品信息不可用");
            shareInfoVo.setShareCode(PictorialConstant.GOODS_DELETED);
            return shareInfoVo;
        }

        PictorialShareConfig shareConfig = getPictorialConfig(activityRecord, goodsRecord);
        shareLog(getActivityName(), "分享配置信息:" + Util.toJson(shareConfig));

        // 用户自定义分享样式
        if (PictorialShareConfig.CUSTOMER_STYLE.equals(shareConfig.getShareAction())) {
            if (PictorialShareConfig.DEFAULT_IMG.equals(shareConfig.getShareImgAction())) {
                shareInfoVo.setImgUrl(goodsRecord.getGoodsImg());
            } else {
                shareInfoVo.setImgUrl(shareConfig.getShareImg());
            }
            shareInfoVo.setShareDoc(shareConfig.getShareDoc());
        } else {
            String imgPath = createShareImage(activityRecord, goodsRecord, param);
            if (imgPath == null) {
                shareInfoVo.setShareCode(PictorialConstant.GOODS_PIC_ERROR);
                return shareInfoVo;
            }
            shareInfoVo.setImgUrl(imgPath);
            String shareDoc = null;
            ShopRecord shop = saas.shop.getShopById(getShopId());
            shareDoc = pictorialService.getCommonConfigDoc(param.getUserName(), goodsRecord.getGoodsName(), param.getRealPrice(), shop.getShopLanguage(), false);
            if (shareDoc == null) {
                shareDoc = createDefaultShareDoc(shop.getShopLanguage(), activityRecord, goodsRecord, param);
            }
            shareInfoVo.setShareDoc(shareDoc);
        }
        shareInfoVo.setImgUrl(imageService.getImgFullUrl(shareInfoVo.getImgUrl()));
        return shareInfoVo;
    }


    /**
     * 获取商品海报信息
     * @param baseParam 请求参数
     * @return 海报获取的信息
     */
    public GoodsPictorialInfo getPictorialInfo(GoodsShareBaseParam baseParam) {
        ShopRecord shop = saas.shop.getShopById(getShopId());
        GoodsPictorialInfo goodsPictorialInfo = new GoodsPictorialInfo();

        Record activityRecord = getActivityInfo(baseParam, goodsPictorialInfo);
        if (!GoodsPictorialInfo.OK.equals(goodsPictorialInfo.getPictorialCode())) {
            return goodsPictorialInfo;
        }

        GoodsRecord goodsRecord = goodsService.getGoodsRecordById(baseParam.getTargetId());
        if (goodsRecord == null) {
            pictorialLog(getActivityName(), "商品信息已删除或失效");
            goodsPictorialInfo.setPictorialCode(PictorialConstant.GOODS_DELETED);
            return goodsPictorialInfo;
        }

        PictorialShareConfig shareConfig = getPictorialConfig(activityRecord, goodsRecord);
        shareLog(getActivityName(), "分享配置信息:" + Util.toJson(shareConfig));

        PictorialUserInfo userInfo = getUserInfo(baseParam, shop, goodsPictorialInfo);
        if (!GoodsPictorialInfo.OK.equals(goodsPictorialInfo.getPictorialCode())) {
            return goodsPictorialInfo;
        }

        BufferedImage goodsImage;
        try {
            pictorialLog(getActivityName(), "获取商品图片信息");
            goodsImage = pictorialService.getGoodsPictorialImage(shareConfig, goodsRecord);
        } catch (IOException e) {
            pictorialLog(getActivityName(), "获取商品图片信息失败：" + e.getMessage());
            goodsPictorialInfo.setPictorialCode(PictorialConstant.GOODS_PIC_ERROR);
            return goodsPictorialInfo;
        }

        pictorialLog(getActivityName(), "获取商品分享语");
        String shareDoc = null;
        if (PictorialShareConfig.DEFAULT_STYLE.equals(shareConfig.getShareAction())) {
            shareDoc = createDefaultShareDoc(shop.getShopLanguage(), activityRecord, goodsRecord, baseParam);
        } else {
            shareDoc = shareConfig.getShareDoc();
        }

        BufferedImage qrCodeImage = getQrcodInfo(activityRecord, goodsRecord, baseParam, goodsPictorialInfo);
        if (!GoodsPictorialInfo.OK.equals(goodsPictorialInfo.getPictorialCode())) {
            return goodsPictorialInfo;
        }

        pictorialLog(getActivityName(), "处理海报背景图片");
        createPictorialImg(qrCodeImage, goodsImage, userInfo, shareDoc, activityRecord, goodsRecord, shop, baseParam, goodsPictorialInfo);

//        BufferedImage bgImg = goodsPictorialInfo.getBgImg();
//        try {
//            FileOutputStream outputStream = new FileOutputStream(new File("E:/a.jpg"));
//            ImageIO.write(bgImg, "jpg", outputStream);
//            outputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return goodsPictorialInfo;
    }
    /**
     * 获取分销中心推广海报信息，与商品无关
     *
     * @param baseParam 分享参数
     * @return 海报获取的信息
     */
    public GoodsPictorialInfo getDistributionPictorialInfo(GoodsShareBaseParam baseParam) {
        GoodsPictorialInfo goodsPictorialInfo = new GoodsPictorialInfo();

        // 获取分销配置
        DistributionParam distributionCfg = distributionConfigService.getDistributionCfg();
        shareLog(getActivityName(), "分享配置信息:" + Util.toJson(distributionCfg));

        if (distributionCfg == null || distributionCfg.getStatus().equals((byte) 0)) {
            pictorialLog(getActivityName(), "已关闭");
            goodsPictorialInfo.setPictorialCode(PictorialConstant.ACTIVITY_DELETED);
            return goodsPictorialInfo;
        }

        ShopRecord shop = saas.shop.getShopById(getShopId());
        // 分享人信息
        PictorialUserInfo userInfo;
        try {
            userInfo = pictorialService.getPictorialUserInfo(baseParam.getUserId(), shop);
        } catch (IOException e) {
            pictorialLog(getActivityName(), "获取用户信息失败：" + e.getMessage());
            goodsPictorialInfo.setPictorialCode(PictorialConstant.USER_PIC_ERROR);
            return goodsPictorialInfo;
        }

        String mpQrCode = createMpQrCode(null, null, baseParam);
        BufferedImage qrCodeImage;
        try {
            qrCodeImage = ImageIO.read(new URL(mpQrCode));
        } catch (IOException e) {
            pictorialLog(getActivityName(), "获取二维码失败：" + e.getMessage());
            goodsPictorialInfo.setPictorialCode(PictorialConstant.QRCODE_ERROR);
            return goodsPictorialInfo;
        }

        BufferedImage activityShareImg;
        try {
            activityShareImg = ImageIO.read(new URL("https:" + distributionCfg.getBgImg()));
        } catch (IOException e) {
            pictorialLog(getActivityName(), "获取背景图片信息失败：" + e.getMessage());
            goodsPictorialInfo.setPictorialCode(PictorialConstant.GOODS_PIC_ERROR);
            return goodsPictorialInfo;
        }

        pictorialLog(getActivityName(), "处理海报背景图片");
        createPictorialImg(qrCodeImage, activityShareImg, userInfo,
            distributionCfg.getDesc(), null, null, shop, baseParam, goodsPictorialInfo);

        return goodsPictorialInfo;
    }
    /**
     * 获取分销员微信二维码海报，与商品无关
     * @param baseParam 分享参数
     * @return 海报获取的信息
     */
    public GoodsPictorialInfo getDistributorPictorialInfo(GoodsShareBaseParam baseParam) {
        GoodsPictorialInfo goodsPictorialInfo = new GoodsPictorialInfo();

        // 获取分销员微信二维码图片
        UploadedImageDo distributorImage = distributionService.getDistributorImage(baseParam.getUserId());

        if (distributorImage == null) {
            pictorialLog(getActivityName(), "获取用户信息失败，用户没有上传二维码");
            goodsPictorialInfo.setPictorialCode(PictorialConstant.USER_PIC_ERROR);
            return goodsPictorialInfo;
        }

        // 获取二维码信息
        BufferedImage qrCodeImage;
        try {
            qrCodeImage = ImageIO.read(new URL(distributorImage.getImgUrl()));
        } catch (IOException e) {
            pictorialLog(getActivityName(), "获取二维码失败：" + e.getMessage());
            goodsPictorialInfo.setPictorialCode(PictorialConstant.QRCODE_ERROR);
            return goodsPictorialInfo;
        }

        pictorialLog(getActivityName(), "处理海报背景图片");
        createPictorialImg(qrCodeImage, null, null, null, null, null, null, null, goodsPictorialInfo);

        // 海报图片上传又拍云并将其URL存入缓存中
        uploadAndSavePictorial(goodsPictorialInfo.getBase64(), baseParam.getUserId());

        return goodsPictorialInfo;
    }
    /**
     * 海报图片上传又拍云并将其URL存入缓存中
     * @param base64 微信二维码海报base64形式
     * @param userId 用户id
     */
    private void uploadAndSavePictorial(String base64, Integer userId) {
        // 1.构造图片文件
        MultipartFile multipartFile = FileUtil.base64MutipartFile(base64);
        // 2.上传又拍云
        UploadPath uploadPath = imageService.getImageWritableUploadPath(multipartFile.getContentType());
        boolean result = false;
        try {
            result = imageService.uploadToUpYunBySteam(uploadPath.relativeFilePath, multipartFile.getInputStream());
        } catch (IOException e) {
            pictorialLog(getActivityName(), "读取微信二维码海报失败：" + e.getMessage());
        } catch (UpException e) {
            pictorialLog(getActivityName(), "上传微信二维码海报失败：" + e.getMessage());
        }
        if (result) {
            // 3.海报url存入缓存
            imageService.setQrCodeUrlByRedisKey(String.valueOf(userId), uploadPath.getImageUrl());
        }
    }

    /**
     * 获取二维码内容带日志
     * @param aRecord
     * @param goodsRecord
     * @param baseParam
     * @param goodsPictorialInfo
     * @return
     */
    BufferedImage getQrcodInfo(Record aRecord, GoodsRecord goodsRecord, GoodsShareBaseParam baseParam, GoodsPictorialInfo goodsPictorialInfo) {
        String mpQrCode = createMpQrCode(aRecord, goodsRecord, baseParam);
        BufferedImage qrCodeImage = null;
        try {
            qrCodeImage = ImageIO.read(new URL(mpQrCode));
//            qrCodeImage = ImageIO.read(new File("E:/qrcode.jpg"));
        } catch (IOException e) {
            pictorialLog(getActivityName(), "获取二维码失败");
            goodsPictorialInfo.setPictorialCode(PictorialConstant.QRCODE_ERROR);
        }
        return qrCodeImage;
    }

    /**
     * 获取活动信息带日志
     * @param baseParam
     * @param goodsPictorialInfo
     * @return
     */
    Record getActivityInfo(GoodsShareBaseParam baseParam, GoodsPictorialInfo goodsPictorialInfo) {
        Record activityRecord = null;
        if (baseParam.getActivityId() != null) {
            activityRecord = getActivityRecord(baseParam.getActivityId());
            // 活动信息不可用
            if (activityRecord == null) {
                pictorialLog(getActivityName(), "活动信息已删除或失效");
                goodsPictorialInfo.setPictorialCode(PictorialConstant.ACTIVITY_DELETED);
            }
        }
        return activityRecord;
    }

    /**
     * 获取用户信息带日志
     * @param baseParam
     * @param shop
     * @param goodsPictorialInfo
     * @return
     */
    PictorialUserInfo getUserInfo(GoodsShareBaseParam baseParam, ShopRecord shop, GoodsPictorialInfo goodsPictorialInfo) {
        PictorialUserInfo userInfo = null;
        try {
            pictorialLog(getActivityName(), "获取用户信息");
            userInfo = pictorialService.getPictorialUserInfo(baseParam.getUserId(), shop);
        } catch (IOException e) {
            pictorialLog(getActivityName(), "获取用户信息失败：" + e.getMessage());
            goodsPictorialInfo.setPictorialCode(PictorialConstant.USER_PIC_ERROR);
        }
        return userInfo;
    }

    /**
     * 创建云盘上的相对路径
     * @param activityId 活动Id
     * @return 相对路径
     */
    String createFilePath(Integer activityId) {
        return String.format("/upload/%s/share/%s/%s.jpg", getShopId(), getActivityName(), activityId + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date()));
    }

    /**
     * 活动名称
     * @return 名称字符串
     */
    abstract String getActivityName();

    /**
     * 返回店铺配置样式颜色
     * @return
     */
    protected Color getShopStyleColor() {
        ShopStyleConfig shopStyleConfig = shopStyleConfigService.getShopStyleConfig();
        String shopStyleValue = shopStyleConfig.getShopStyleValue();
        // 店铺配置颜色
        Color shopStyleColor = PictorialImgPx.SHOP_DEFAULT_STYLE_COLOR;
        try {
            if (StringUtils.isNotBlank(shopStyleValue)) {
                String[] splits = shopStyleValue.split("\\)");
                Matcher matcher = PATTERN_NUMBER.matcher(splits[1]);
                matcher.find();
                int red = Integer.parseInt(matcher.group());
                matcher.find();
                int green = Integer.parseInt(matcher.group());
                matcher.find();
                int blue = Integer.parseInt(matcher.group());
                shopStyleColor = new Color(red, green, blue);
            }
        } catch (Exception e) {
        }
        return shopStyleColor;
    }

    /**
     * 获取店铺海报分享配置
     * @return
     */
    protected Byte getPictorialShareStyle() {
        GoodsShareConfig shareConfig = commonConfigService.getGoodsShareConfig();
        return shareConfig.getCustomPictorial();
    }

    /**
     * 分享打印日志
     * @param tag 活动名
     * @param msg 日志信息
     */
    void shareLog(String tag, String msg) {
        printLog("share", tag, msg);
    }

    /**
     * 海报打印日志
     * @param tag 活动名
     * @param msg 日志信息
     */
    void pictorialLog(String tag, String msg) {
        printLog("pictorial", tag, msg);
    }

    /**
     * 日志
     * @param type share或pictorial
     * @param tag  活动名
     * @param msg  日志信息
     */
    private void printLog(String type, String tag, String msg) {
        logger().debug("小程序-{}-{}-{}", type, tag, msg);
    }

    /**
     * 将二维码参数存储,并返回包含对应sceneId的url
     * @param sceneValue
     * @return
     */
    public String addAndGetSceneStr(SceneValueBase sceneValue){
        Integer sceneId = qrCodeService.addScene(Util.toJson(sceneValue));
        return "sceneId="+sceneId;
    }

    /**
     * 价格取两位小数，加上价格符号
     * @param lang  国际化
     * @param price 价格
     * @return 字符串价格
     */
    String convertPriceWithFlag(String lang, BigDecimal price) {
        if (price == null) {
            return null;
        }
        String moneyFlag = Util.translateMessage(lang, JsonResultMessage.WX_MA_PICTORIAL_MONEY_FLAG, "messages");
        return moneyFlag + price.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }
}
