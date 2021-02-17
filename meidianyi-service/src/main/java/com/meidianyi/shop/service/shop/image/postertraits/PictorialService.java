package com.meidianyi.shop.service.shop.image.postertraits;

import com.upyun.UpException;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.PictorialRecord;
import com.meidianyi.shop.service.foundation.exception.BusinessException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.foundation.util.ImageUtil;
import com.meidianyi.shop.service.pojo.shop.config.GoodsShareConfig;
import com.meidianyi.shop.service.pojo.shop.config.PictorialShareConfig;
import com.meidianyi.shop.service.pojo.wxapp.account.UserInfo;
import com.meidianyi.shop.service.pojo.wxapp.share.*;
import com.meidianyi.shop.service.shop.config.ShopCommonConfigService;
import com.meidianyi.shop.service.shop.image.ImageService;
import com.meidianyi.shop.service.shop.user.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jooq.Condition;
import org.jooq.lambda.tuple.Tuple2;
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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static com.meidianyi.shop.common.foundation.util.DateUtils.DATE_FORMAT_FULL_NO_UNDERLINE;
import static com.meidianyi.shop.db.shop.Tables.PICTORIAL;
import static org.apache.commons.lang3.math.NumberUtils.BYTE_ZERO;

/**
 * @author zhaojianqiang
 * 2019年10月17日 下午5:19:04
 */
@Service
public class PictorialService extends ShopBaseService {
    @Autowired
    private UserService user;
    @Autowired
    private ImageService imageService;
    @Autowired
    private ShopCommonConfigService shopCommonConfigService;

    /**
     * 分享海报时使用的默认头像
     */
    public static final String DEFAULT_USER_AVATAR = "image/wxapp/default_user_avatar.png";


    /**
     * 表单分享海报图片上的指纹图片
     */
    public static final String FINGER_IMG = "/image/admin/usr_codes.png";

    /**
     * 获取海报中用户头像
     * @param userId 用户ID
     * @return 用户海报信息
     */
    public PictorialUserInfo getPictorialUserInfo(Integer userId, ShopRecord shop) throws IOException {
        UserInfo userInfo = user.getUserInfo(userId);

        String userName = StringUtils.isBlank(userInfo.getUsername()) ?
            Util.translateMessage(shop.getShopLanguage(), JsonResultMessage.WX_MA_DEFAULT_USER_NAME, "messages")
            : userInfo.getUsername();
        BufferedImage userAvatarImage;
        if (StringUtils.isBlank(userInfo.getUserAvatar())) {
            try (InputStream inputStream = Util.loadFile(DEFAULT_USER_AVATAR)) {
                userAvatarImage = ImageIO.read(inputStream);
            } catch (IOException e) {
                logger().debug("小程序-生成图片-获取用户默认头像错误：" + e.getMessage());
                throw e;
            }
        } else {
            try {
                userAvatarImage = ImageIO.read(new URL(imageService.getImgFullUrl(userInfo.getUserAvatar())));
            } catch (IOException e) {
                logger().debug("小程序-生成图片-获取用户头像错误 userId{" + userId + "}：" + e.getMessage());
                throw e;
            }
        }
        PictorialUserInfo pictorialUserInfo = new PictorialUserInfo();
        pictorialUserInfo.setUserName(userName);
        pictorialUserInfo.setUserAvatarImage(userAvatarImage);

        return pictorialUserInfo;
    }

    /**
     * 获取海报分享中用到的商品图片对象
     * @param shareConfig 分享配置
     * @param goodsRecord 商品对象
     * @return 商品图片
     */
    public BufferedImage getGoodsPictorialImage(PictorialShareConfig shareConfig, GoodsRecord goodsRecord) throws IOException {
        String goodsImg;
        if (PictorialShareConfig.DEFAULT_STYLE.equals(shareConfig.getShareAction())) {
            goodsImg = goodsRecord.getGoodsImg();
        } else {
            if (PictorialShareConfig.DEFAULT_IMG.equals(shareConfig.getShareImgAction())) {
                goodsImg = goodsRecord.getGoodsImg();
            } else {
                goodsImg = shareConfig.getShareImg();
            }
        }
        try {
            return ImageIO.read(new URL(imageService.getImgFullUrl(goodsImg)));
        } catch (IOException e) {
            logger().debug("小程序-生成图片-获取商品图片错误，图片地址{}：{}", imageService.getImgFullUrl(goodsImg), e.getMessage());
            throw e;
        }
    }

    /**
     * 生成默认海报背景图
     * @param userInfo         用户信息
     * @param shop             店铺配置
     * @param qrCodeImg        二维码
     * @param goodsImg         商品图片
     * @param shareDoc         海报分享文案
     * @param goodsName        商品名称
     * @param activityTipIcon  活动提示icon
     * @param activityTipText  活动提示文字
     * @param bottomFirstText  底部第一个文字信息（一般是商品真实价格）
     * @param bottomSecondText 底部第一个文字信息（一般是商品真实价格）
     * @param imgPx            图片规格信息
     * @return 通过图片
     */
    public BufferedImage createPictorialBgImage(PictorialUserInfo userInfo, ShopRecord shop, BufferedImage qrCodeImg, BufferedImage goodsImg, String shareDoc, String goodsName, BufferedImage activityTipIcon, String activityTipText, String bottomFirstText, String bottomSecondText, PictorialImgPx imgPx) {
        BufferedImage bgBufferedImage = createBgImage(imgPx);
        addHeaderItemInfo(bgBufferedImage, userInfo.getUserAvatarImage(), userInfo.getUserName(), shareDoc, imgPx);
        addGoodsPicInfo(bgBufferedImage, goodsImg, imgPx);
        addBottomItemInfo(bgBufferedImage, shop, goodsName, activityTipIcon, activityTipText, bottomFirstText, bottomSecondText, qrCodeImg, imgPx);
        return bgBufferedImage;
    }

    /**
     * 生成海报基础版背景图
     * @param shop         店铺配置
     * @param qrCodeImg    二维码
     * @param goodsImg     商品图片
     * @param goodsName    商品名称
     * @param realPriceStr 商品原件
     * @param linePriceStr 商品划线价
     * @param imgPx        图片规格信息
     * @return 图片
     */
    public BufferedImage createBasicStylePictorialBgImage(ShopRecord shop, BufferedImage qrCodeImg, BufferedImage goodsImg, String goodsName, String realPriceStr, String linePriceStr, PictorialImgPx imgPx) {
        //设置背景图
        BufferedImage bgBufferedImage = createBgImage(imgPx);
        ImageUtil.addRect(bgBufferedImage, 5, 5,  imgPx.getBgWidth()-10, imgPx.getBgHeight()-10,  imgPx.getShopStyleColor(),10.0f, Color.WHITE);

        // 设置商品图片
        goodsImg = ImageUtil.resizeImage(imgPx.getGoodsWidth(), imgPx.getGoodsHeight(), goodsImg);
        ImageUtil.addTwoImage(bgBufferedImage, goodsImg, imgPx.getGoodsStartX(), imgPx.getGoodsStartY());

        Integer priceX = imgPx.getBottomTextStartX();

        // 设置原价和划线价
        pictorialAddRealLinePrice(shop, realPriceStr, linePriceStr, imgPx, bgBufferedImage, priceX);
        Integer textAscent = ImageUtil.getTextAscent(bgBufferedImage, imgPx.getPriceFont());
        imgPx.setGoodsNameStartY(imgPx.getPriceY() + textAscent + imgPx.getPriceNamePadding());

        // 设置商品名称
        int goodsNameHeight = pictorialAddFontName(bgBufferedImage, goodsName, 2, imgPx);
        // 画分割线
        int lineStartY = imgPx.getGoodsNameStartY() + goodsNameHeight + imgPx.getPriceNamePadding();
        ImageUtil.addLine(bgBufferedImage, imgPx.getBgPadding(), lineStartY, imgPx.getBgPadding() + imgPx.getGoodsWidth(), lineStartY, PictorialImgPx.LINE_PRICE_COLOR);

        imgPx.setQrCodeStartY(lineStartY + imgPx.getPriceNamePadding() / 2);
        // 设置二维码
        qrCodeImg = ImageUtil.resizeImageTransparent(imgPx.getQrCodeWidth(), imgPx.getQrCodeWidth(), qrCodeImg);
        ImageUtil.addTwoImage(bgBufferedImage, qrCodeImg, imgPx.getQrCodeStartX(), imgPx.getQrCodeStartY());
        return bgBufferedImage;
    }

    /**
     * 生成海报带分享用户信息
     * @param userInfo     用户信息
     * @param shop         店铺配置
     * @param qrCodeImg    二维码
     * @param goodsImg     商品图片
     * @param shareDoc     海报分享文案
     * @param goodsName    商品名称
     * @param realPriceStr 商品原件
     * @param linePriceStr 商品划线价
     * @param imgPx        图片规格信息
     * @return
     */
    public BufferedImage createSharePersonInfoPictorialBgImage(PictorialUserInfo userInfo, ShopRecord shop, BufferedImage qrCodeImg, BufferedImage goodsImg, String shareDoc, String goodsName, String realPriceStr, String linePriceStr, PictorialImgPx imgPx) {

        BufferedImage bgBufferedImage = createBgImage(imgPx);
        ImageUtil.addCircle(bgBufferedImage, imgPx.getBgCircleStartX(), imgPx.getBgCircleStartY(), imgPx.getMajorAxis(), imgPx.getMinorAxis(), imgPx.getShopStyleColor());
        // 设置title名
        String userNameText = userInfo.getUserName() + " " + Util.translateMessage(shop.getShopLanguage(), JsonResultMessage.WX_MA_PICTORIAL_RECOMMEND_INFO, null, "messages");
        ;
        addHeaderItemInfo(bgBufferedImage, userInfo.getUserAvatarImage(), userNameText, shareDoc, imgPx);
        addGoodsPicInfo(bgBufferedImage, goodsImg, imgPx);
        addBottomItemInfo(bgBufferedImage, shop, goodsName, null, null, realPriceStr, linePriceStr, qrCodeImg, imgPx);
        return bgBufferedImage;
    }

    /**
     * 生成海报带店铺信息
     * @param shop         店铺配置
     * @param qrCodeImg    二维码
     * @param goodsImg     商品图片
     * @param goodsName    商品名称
     * @param realPriceStr 商品原件
     * @param linePriceStr 商品划线价
     * @param imgPx        图片规格信息
     * @return
     */
    public BufferedImage createShareShopInfoPictorialBgImage(BufferedImage shopImg, ShopRecord shop, BufferedImage qrCodeImg, BufferedImage goodsImg, String goodsName, String realPriceStr, String linePriceStr, PictorialImgPx imgPx) {

        BufferedImage bgBufferedImage = createBgImage(imgPx);
        ImageUtil.addCircle(bgBufferedImage, imgPx.getBgCircleStartX(), imgPx.getBgCircleStartY(), imgPx.getMajorAxis(), imgPx.getMinorAxis(), imgPx.getShopStyleColor());
        // 设置title名
        addHeaderItemInfo(bgBufferedImage, shopImg, shop.getShopName(), null, imgPx);
        addGoodsPicInfo(bgBufferedImage, goodsImg, imgPx);
        addBottomItemInfo(bgBufferedImage, shop, goodsName, null, null, realPriceStr, linePriceStr, qrCodeImg, imgPx);
        return bgBufferedImage;
    }

    /**
     * 生成海报带店铺信息
     * @param userInfo     用户信息
     * @param shop         店铺配置
     * @param qrCodeImg    二维码
     * @param goodsImg     商品图片
     * @param shareDoc     海报分享文案
     * @param goodsName    商品名称
     * @param realPriceStr 商品原件
     * @param linePriceStr 商品划线价
     * @param imgPx        图片规格信息
     * @return
     */
    public BufferedImage createSharePersonShopInfoPictorialBgImage(PictorialUserInfo userInfo, ShopRecord shop, BufferedImage qrCodeImg, BufferedImage shopIconImg, BufferedImage goodsImg, String shareDoc, String goodsName, String realPriceStr, String linePriceStr, PictorialImgPx imgPx) {

        BufferedImage bgBufferedImage = createBgImage(imgPx);
        ImageUtil.addCircle(bgBufferedImage, imgPx.getBgCircleStartX(), imgPx.getBgCircleStartY(), imgPx.getMajorAxis(), imgPx.getMinorAxis(), imgPx.getShopStyleColor());
        // 设置title名
        String userNameText = userInfo.getUserName() + " " + Util.translateMessage(shop.getShopLanguage(), JsonResultMessage.WX_MA_PICTORIAL_RECOMMEND_INFO, null, "messages");
        ;
        addHeaderItemInfo(bgBufferedImage, userInfo.getUserAvatarImage(), userNameText, shareDoc, imgPx);
        addGoodsPicInfo(bgBufferedImage, goodsImg, imgPx);
        addBottomItemInfo(bgBufferedImage, shop, goodsName, null, null, realPriceStr, linePriceStr, qrCodeImg, imgPx);
        // 添加店铺icon和名字信息,先话外部圆（最左侧），再画矩形，矩形长度是文字长度加上外圆的半径。再画店铺头像，再画文字
        String shopName = shop.getShopName();
        Integer shopNameWidth = ImageUtil.getTextWidth(bgBufferedImage, imgPx.getShopInfoNameFont(), shopName);
        Integer getShopInfoStartY = imgPx.getShopInfoStartY();
        Integer shopIconWrapStartX = imgPx.getShopIconWrapStartX(shopNameWidth);
        ;
        ImageUtil.addCircle(bgBufferedImage, shopIconWrapStartX, getShopInfoStartY, imgPx.getShopIconWrapHeight(), imgPx.getShopIconWrapHeight(), imgPx.getShopInfoBgColor());
        ImageUtil.addRect(bgBufferedImage, imgPx.getShopInfoRectStartX(shopNameWidth), getShopInfoStartY, shopNameWidth + imgPx.getShopIconWrapHeight() / 2, imgPx.getShopInfoHeight(), imgPx.getShopInfoBgColor(), imgPx.getShopInfoBgColor());
        shopIconImg = ImageUtil.makeRound(shopIconImg, imgPx.getShopIconDiameter());
        ImageUtil.addTwoImage(bgBufferedImage, shopIconImg, imgPx.getShopIconStartX(shopIconWrapStartX), imgPx.getShopIconStartY(getShopInfoStartY));
        ImageUtil.addFont(bgBufferedImage, shopName, imgPx.getShopInfoNameFont(), imgPx.getShopInfoTextStartX(shopIconWrapStartX), imgPx.getShopInfoTextStartY(getShopInfoStartY), Color.BLACK, true);
        return bgBufferedImage;
    }


    /**
     * 海报背景白板生成
     * @param imgPx
     * @return
     */
    private BufferedImage createBgImage(PictorialImgPx imgPx) {
        //设置背景图
        BufferedImage bgBufferedImage = new BufferedImage(imgPx.getBgWidth(), imgPx.getBgHeight(), BufferedImage.TYPE_USHORT_555_RGB);
        ImageUtil.addRect(bgBufferedImage, 0, 0, imgPx.getBgWidth(), imgPx.getBgHeight(), null, Color.WHITE);
        return bgBufferedImage;
    }

    /**
     * 添加海报头部信息内容
     * @param bgBufferedImage
     * @param headerIconImg
     * @param titleName
     * @param shareDoc
     * @param imgPx
     */
    private void addHeaderItemInfo(BufferedImage bgBufferedImage, BufferedImage headerIconImg, String titleName, String shareDoc, PictorialImgPx imgPx) {
        // 设置用户头像
        BufferedImage userAvatarImage = ImageUtil.makeRound(headerIconImg, imgPx.getUserHeaderDiameter());
        ImageUtil.addTwoImage(bgBufferedImage, userAvatarImage, imgPx.getHeaderStartX(), imgPx.getHeaderStartY());
        // 设置用户名或店铺名
        ImageUtil.addFont(bgBufferedImage, titleName, imgPx.getUserNameFont(), imgPx.getUserNameX(), imgPx.getUserNameY(), imgPx.getHeadFontColor(), false);
        // 设置宣传语
        pictorialAddShareDoc(bgBufferedImage, shareDoc, imgPx);
    }

    /**
     * 添加商品图片
     * @param bgBufferedImage
     * @param goodsImg
     * @param imgPx
     */
    private void addGoodsPicInfo(BufferedImage bgBufferedImage, BufferedImage goodsImg, PictorialImgPx imgPx) {
        // 设置商品图片
        goodsImg = ImageUtil.resizeImage(imgPx.getGoodsWidth(), imgPx.getGoodsHeight(), goodsImg);
        ImageUtil.addTwoImage(bgBufferedImage, goodsImg, imgPx.getGoodsStartX(), imgPx.getGoodsStartY());
    }

    /**
     * 添加底部内容信息
     * @param bgBufferedImage
     * @param shop
     * @param goodsName
     * @param activityTipIcon
     * @param activityTipText
     * @param bottomFirstText  底部第一个文字信息（一般是商品真实价格）
     * @param bottomSecondText 底部第一个文字信息（一般是商品真实价格）
     * @param qrCodeImg
     * @param imgPx
     */
    private void addBottomItemInfo(BufferedImage bgBufferedImage, ShopRecord shop, String goodsName, BufferedImage activityTipIcon, String activityTipText, String bottomFirstText, String bottomSecondText, BufferedImage qrCodeImg, PictorialImgPx imgPx) {

        if (qrCodeImg != null) {
            // 设置二维码
            qrCodeImg = ImageUtil.resizeImageTransparent(imgPx.getQrCodeWidth(), imgPx.getQrCodeWidth(), qrCodeImg);
            ImageUtil.addTwoImage(bgBufferedImage, qrCodeImg, imgPx.getQrCodeStartX(), imgPx.getQrCodeStartY());
        }

        if (goodsName != null) {
            // 设置商品名称
            int goodsNameHeight = pictorialAddFontName(bgBufferedImage, goodsName, imgPx);
            imgPx.setPriceY(imgPx.getBgHeight()-120);
        }

        Integer priceX = imgPx.getBottomTextStartX();
        //  某些活动价格前面显示的活动提示图标如：限时降价
        if (activityTipIcon != null) {
            ImageUtil.addTwoImage(bgBufferedImage, activityTipIcon, imgPx.getBottomTextStartX(), imgPx.getActivityTipTextY());
            priceX = imgPx.getBottomTextStartX() + activityTipIcon.getWidth() + imgPx.getPriceMargin();
        }
        //  某些活动价格前面显示的活动提示文字如：首单特惠
        if (activityTipText != null && activityTipIcon == null) {
            int tipWidth = ImageUtil.addFontWithRect(bgBufferedImage, imgPx.getBottomTextStartX(), imgPx.getActivityTipTextY(), activityTipText, ImageUtil.sourceHanSansCn(Font.PLAIN, PictorialImgPx.SMALL_FONT_SIZE), imgPx.getRealPriceColor(), null, imgPx.getRealPriceColor());
            priceX = imgPx.getBottomTextStartX() + tipWidth + imgPx.getPriceMargin();
        }
        // 设置原价和划线价
        pictorialAddRealLinePrice(shop, bottomFirstText, bottomSecondText, imgPx, bgBufferedImage, priceX);
    }

    /**
     * 添加底部划线价
     * @param shop
     * @param bottomFirstText  底部第一个文字信息（一般是商品真实价格）
     * @param bottomSecondText 底部第一个文字信息（一般是商品真实价格）
     * @param imgPx
     * @param bgBufferedImage
     * @param priceX
     */
    private void pictorialAddRealLinePrice(ShopRecord shop, String bottomFirstText, String bottomSecondText, PictorialImgPx imgPx, BufferedImage bgBufferedImage, Integer priceX) {
        if (bottomFirstText != null) {
            ImageUtil.addFont(bgBufferedImage, bottomFirstText, imgPx.getPriceFont(), priceX, imgPx.getPriceY(), imgPx.getRealPriceColor(), false);
            // 设置划线价
            if (bottomSecondText != null) {
                Integer lineStartX = ImageUtil.getTextWidth(bgBufferedImage, imgPx.getPriceFont(), bottomFirstText) + priceX + imgPx.getPriceMargin();
                ImageUtil.addFontWithLine(bgBufferedImage, lineStartX, imgPx.getPriceLineY(), bottomSecondText, imgPx.getLinePriceFont(), PictorialImgPx.LINE_PRICE_COLOR);
            }
        }
    }

    /**
     * 添加文字，可以自动换行
     * @param bgBufferedImage 背景
     * @param text            文字
     * @param startX          开始x
     * @param startY          开始y
     * @param maxWidth        最大可使用宽度
     * @param maxRows         最大行数
     * @param font            字体
     * @return 文字高度
     */
    public int addTextWithBreak(BufferedImage bgBufferedImage, String text, Integer startX, Integer startY, Integer maxWidth, Integer maxRows, Font font) {
        // 名称单个字符高度
        int nameCharHeight = ImageUtil.getTextAscent(bgBufferedImage, font)+3;
        // 名称总长度
        int nameTextLength = ImageUtil.getTextWidth(bgBufferedImage, font, text);

        if (nameTextLength <= maxWidth) {
            ImageUtil.addFont(bgBufferedImage, text, font, startX, startY, PictorialImgPx.GOODS_NAME_COLOR, false);
            return nameCharHeight;
        } else {
            int line = 1;
            int nextTextStartY = startY;
            String oneLineStr = null;
            StringBuilder oneLineSb = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                Integer curWidth = ImageUtil.getTextWidth(bgBufferedImage, font, oneLineSb.toString());
                if (curWidth >= maxWidth) {
                    // 最后一行且长度过长
                    if (line == maxRows) {
                        break;
                    }else{
                        oneLineStr = oneLineSb.toString();
                    }
                    ImageUtil.addFont(bgBufferedImage,oneLineStr, font, startX, nextTextStartY, PictorialImgPx.GOODS_NAME_COLOR, false);
                    nextTextStartY +=nameCharHeight;
                    line++;
                    oneLineSb = new StringBuilder();
                }
                oneLineSb.append(text.charAt(i));
            }

            if (line == maxRows) {
                Integer curWidth =ImageUtil.getTextWidth(bgBufferedImage, font, oneLineSb.toString());
                if (curWidth>maxWidth){
                    oneLineStr = oneLineSb.subSequence(0, oneLineSb.length() / 2).toString()+"...";
                }else {
                    oneLineStr = oneLineSb.toString();
                }
            }else{
                oneLineStr = oneLineSb.toString();
            }
            ImageUtil.addFont(bgBufferedImage,oneLineStr, font, startX, nextTextStartY, PictorialImgPx.GOODS_NAME_COLOR, false);

            return nextTextStartY - startY;
        }
    }

    /**
     * 添加宣传语
     * @param bgBufferedImage
     * @param shareDoc
     * @param imgPx
     */
    private void pictorialAddShareDoc(BufferedImage bgBufferedImage, String shareDoc, PictorialImgPx imgPx) {
        if (shareDoc != null) {
            Integer textWidth = ImageUtil.getTextWidth(bgBufferedImage, imgPx.getShareDocFont(), shareDoc);
            if (textWidth > imgPx.getShareDocCanUseWidth()) {
                double oneCharWidth = Math.ceil(textWidth * 1.0 / shareDoc.length());
                int canUseCharNum = (int) (imgPx.getShareDocCanUseWidth() / oneCharWidth);
                shareDoc = shareDoc.substring(0, canUseCharNum) + "...";
            }
            ImageUtil.addFont(bgBufferedImage, shareDoc, imgPx.getShareDocFont(), imgPx.getShareDocX(), imgPx.getShareDocY(), imgPx.getHeadFontColor(), false);
        }
    }

    /**
     * 海报添加商品名称，根据长度自动折行或截断商品名称
     * @param bgBufferedImage 背景图bufferImage
     * @param goodsName       商品名称
     * @param imgPx           图片规格信息
     * @return 商品名称高度
     */
    private int pictorialAddFontName(BufferedImage bgBufferedImage, String goodsName, PictorialImgPx imgPx) {
        return addTextWithBreak(bgBufferedImage, goodsName, imgPx.getBottomTextStartX(), imgPx.getGoodsNameStartY(), imgPx.getGoodsNameCanUseWidth(), 3, imgPx.getGoodsNameFont());
    }

    private int pictorialAddFontName(BufferedImage bgBufferedImage, String goodsName, Integer maxRows, PictorialImgPx imgPx) {
        return addTextWithBreak(bgBufferedImage, goodsName, imgPx.getBottomTextStartX(), imgPx.getGoodsNameStartY(), imgPx.getGoodsNameCanUseWidth(), maxRows, imgPx.getGoodsNameFont());
    }

    /**
     * 给海报添加自定义内容区域内容
     * @param bufferedImg       海报背景对象
     * @param iconBufferImg     图标对象，没有为null
     * @param firstContentText  一级自定义内容
     * @param secondContentText 二级自定义内容
     * @param secondNeedLine    二级内容是否需要划线
     * @param imgPx             图片规格信息
     */
    public void addPictorialSelfCustomerContent(BufferedImage bufferedImg, BufferedImage iconBufferImg, String firstContentText, String secondContentText, boolean secondNeedLine, PictorialImgPx imgPx) {
        addPictorailSelfCustomerContent(bufferedImg, iconBufferImg, null, firstContentText, secondContentText, secondNeedLine, imgPx);
    }

    /**
     * 给海报添加自定义内容区域内容
     * @param bufferedImg        海报背景对象
     * @param activityPosterText 海报上自定义内容带边框的宣传文字，没有为null
     * @param firstContentText   一级自定义内容
     * @param secondContentText  二级自定义内容
     * @param secondNeedLine     二级内容是否需要划线
     * @param imgPx              图片规格信息
     */
    public void addPictorialSelfCustomerContent(BufferedImage bufferedImg, String activityPosterText, String firstContentText, String secondContentText, boolean secondNeedLine, PictorialImgPx imgPx) {
        addPictorailSelfCustomerContent(bufferedImg, null, activityPosterText, firstContentText, secondContentText, secondNeedLine, imgPx);
    }

    /**
     * 给海报添加自定义内容区域内容
     * @param bufferedImg        海报背景对象
     * @param iconBufferImg      图标对象，没有为null
     * @param activityPosterText 海报上自定义内容带边框的宣传文字，没有为null
     * @param firstContentText   一级自定义内容
     * @param secondContentText  二级自定义内容
     * @param secondNeedLine     二级内容是否需要划线
     * @param imgPx              图片规格信息
     */
    private void addPictorailSelfCustomerContent(BufferedImage bufferedImg, BufferedImage iconBufferImg, String activityPosterText, String firstContentText, String secondContentText, boolean secondNeedLine, PictorialImgPx imgPx) {

        ImageUtil.addRect(bufferedImg, imgPx.getCustomerRectStartX(), imgPx.getCustomerRectStartY(), imgPx.getCustomerRectWidth(), imgPx.getCustomerRectHeight(), null, imgPx.getCustomerRectFillColor());
        // 添加自定义图标
        if (iconBufferImg != null) {
            iconBufferImg = ImageUtil.resizeImageTransparent(imgPx.getCustomerIconWidth(), imgPx.getCustomerIconHeight(), iconBufferImg);
            ImageUtil.addTwoImage(bufferedImg, iconBufferImg, imgPx.getCustomerIconStartX(), imgPx.getCustomerIconStartY());
            imgPx.setCustomerTextStartX(imgPx.getCustomerIconStartX() + imgPx.getCustomerIconWidth() + imgPx.getPriceMargin());
        }
        if (activityPosterText != null && iconBufferImg == null) {
            int width = ImageUtil.addFontWithRect(bufferedImg, imgPx.getCustomerIconStartX(), imgPx.getCustomerIconStartY(), activityPosterText, ImageUtil.sourceHanSansCn(Font.PLAIN, PictorialImgPx.SMALL_FONT_SIZE), PictorialImgPx.CUSTOMER_TEXT_FONT_COLOR, null, PictorialImgPx.CUSTOMER_TEXT_FONT_COLOR);
            imgPx.setCustomerTextStartX(imgPx.getCustomerIconStartX() + width + imgPx.getPriceMargin());
        }
        // 添加自定义一级内容（原价等）
        ImageUtil.addFont(bufferedImg, firstContentText, ImageUtil.sourceHanSansCn(Font.PLAIN, PictorialImgPx.LARGE_FONT_SIZE), imgPx.getCustomerTextStartX(), imgPx.getCustomerTextStartY(), PictorialImgPx.CUSTOMER_TEXT_FONT_COLOR, false);
        Integer realContentTextLength = ImageUtil.getTextWidth(bufferedImg, ImageUtil.sourceHanSansCn(Font.PLAIN, PictorialImgPx.LARGE_FONT_SIZE), firstContentText);
        if (secondContentText != null) {
            // 添加二级内容(带划线)
            Integer secondContentTextStartX = imgPx.getCustomerTextStartX() + realContentTextLength + imgPx.getPriceMargin();
            if (secondNeedLine) {
                ImageUtil.addFontWithLine(bufferedImg, secondContentTextStartX, imgPx.getCustomerSecondTextStartY(), secondContentText, ImageUtil.sourceHanSansCn(Font.PLAIN, PictorialImgPx.SMALL_FONT_SIZE), PictorialImgPx.CUSTOMER_TEXT_FONT_COLOR);
            } else {
                ImageUtil.addFont(bufferedImg, secondContentText, ImageUtil.sourceHanSansCn(Font.PLAIN, PictorialImgPx.MEDIUM_FONT_SIZE), secondContentTextStartX, imgPx.getCustomerSecondTextStartY(), PictorialImgPx.CUSTOMER_TEXT_FONT_COLOR, false);
            }
        }

    }

    public <T extends Rule> void uploadToUpanYun(BufferedImage bufferedImage, String relativePath, T pictorialRule, Integer goodsId, PictorialRecord pictorialRecord, Integer userId) throws UpException, IOException {
        uploadToUpanYun(bufferedImage, relativePath, pictorialRule, goodsId, null, null, pictorialRecord, userId);
    }

    /**
     * 将待分享图片上传到U盘云，并在数据库缓存记录
     * @param bufferedImage   待上传图片
     * @param relativePath    相对路径
     * @param pictorialRule   缓存规则
     * @param goodsId         商品ID
     * @param pictorialRecord 对应的记录行
     * @param userId          用户ID
     * @throws UpException 上传异常
     * @throws IOException 文件io异常
     */
    public <T extends Rule> void uploadToUpanYun(BufferedImage bufferedImage, String relativePath, T pictorialRule, Integer goodsId, Integer activityId, Byte action, PictorialRecord pictorialRecord, Integer userId) throws UpException, IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
            // 上传upanyun
            this.imageService.getUpYunClient().writeFile(relativePath, byteArrayOutputStream.toByteArray(), true);
        } catch (IOException e) {
            logger().debug("小程序-图片上传u盘云操作错误：" + e.getMessage());
            throw e;
        }

        // 新增
        if (pictorialRecord == null) {
            pictorialRecord = new PictorialRecord();
            pictorialRecord.setAction(action);
            pictorialRecord.setPath(relativePath);
            pictorialRecord.setUserId(userId);
            pictorialRecord.setIdentityId(goodsId);
            pictorialRecord.setRule(Util.toJson(pictorialRule));
            pictorialRecord.setActivityId(activityId);
            addPictorialDao(pictorialRecord);
        } else {
            // 更新
            pictorialRecord.setPath(relativePath);
            pictorialRecord.setRule(Util.toJson(pictorialRule));
            updatePictorialDao(pictorialRecord);
        }
    }


    /**
     * 判断Pictorial内存的数据是否还有效
     * @param rule               判断规则
     * @param goodsUpdateTime    商品更新时间
     * @param activityUpdateTime 活动更新时间
     * @return true 缓存有效，false 无效
     */
    public boolean isGoodsSharePictorialRecordCanUse(String rule, Timestamp goodsUpdateTime, Timestamp activityUpdateTime, Color shopStyleColor) {
        PictorialRule pictorialRule = Util.parseJson(rule, PictorialRule.class);
        if (pictorialRule == null) {
            return false;
        }
        boolean goodsTimeOk = true, activityTimeOk = true, colorOk = true;
        if (pictorialRule.getGoodsUpdateTime() != null) {
            goodsTimeOk = pictorialRule.getGoodsUpdateTime().compareTo(goodsUpdateTime) >= 0;
        }
        if (pictorialRule.getActivityUpdateTime() != null) {
            activityTimeOk = pictorialRule.getActivityUpdateTime().compareTo(activityUpdateTime) >= 0;
        }
        if (pictorialRule.getRed() != null) {
            Color color = new Color(pictorialRule.getRed(), pictorialRule.getGreen(), pictorialRule.getBlue());
            colorOk = color.equals(shopStyleColor);
        }

        // 之前生成的图片依然可用，则直接返回其在upanyun上的相对路径
        return goodsTimeOk && activityTimeOk && colorOk;
    }

    /**
     * Gets pictorial from db.从库中获取海报信息
     * @param userId the user id
     * @param id     the id
     * @param action the action
     * @return the pictorial from db
     */
    public PictorialRecord getPictorialFromDb(int userId, int id, byte action) {
        return db().selectFrom(PICTORIAL)
            .where(PICTORIAL.USER_ID.eq(userId))
            .and(PICTORIAL.IDENTITY_ID.eq(id))
            .and(PICTORIAL.ACTION.eq(action))
            .and(PICTORIAL.DEL_FLAG.eq(BYTE_ZERO))
            .fetchOneInto(PICTORIAL);
    }


    /**
     * Is need new pictorial boolean.是否需要创建新海报
     * @param rule   the rule
     * @param record the record
     * @return the boolean
     */
    public boolean isNeedNewPictorial(String rule, PictorialRecord record) {
        return !Objects.isNull(record) && rule.equals(record.getRule());
    }


    /**
     * 根据过了条件查询指定的记录
     * @param identityId 画报关联的实体ID，如goodsId
     * @param activityId 活动id
     * @param action     画报类型，{@link com.meidianyi.shop.service.pojo.wxapp.share.PictorialConstant}
     * @param userId     用户Id
     * @return 画报详情
     */
    public PictorialRecord getPictorialDao(Integer identityId, Integer activityId, Byte action, Integer userId) {
        Condition condition = PICTORIAL.IDENTITY_ID.eq(identityId).and(PICTORIAL.ACTION.eq(action));
        if (userId != null) {
            condition = condition.and(PICTORIAL.USER_ID.eq(userId));
        }
        if (activityId != null) {
            condition = condition.and(PICTORIAL.ACTIVITY_ID.eq(activityId));
        }

        return db().selectFrom(PICTORIAL).where(PICTORIAL.DEL_FLAG.eq(DelFlag.NORMAL.getCode()))
            .and(condition)
            .fetchAny();
    }

    /**
     * 添加记录
     * @param record 画报记录
     */
    public void addPictorialDao(PictorialRecord record) {
        db().executeInsert(record);
    }

    /**
     * 修改记录
     * @param record 画报记录
     */
    public void updatePictorialDao(PictorialRecord record) {
        db().executeUpdate(record);
    }

    /**
     * 根据店铺通用配置，获取用户配置的分享和海报下载时宣语
     * @param userName    用户名
     * @param goodsName   商品名
     * @param goodsPrice  商品价格
     * @param isPictorial true下载海报，false商品分享
     * @return null 表示使用的是默认宣传语，否则用户定义的宣传语，已进行长度截断
     */
    public String getCommonConfigDoc(String userName, String goodsName, BigDecimal goodsPrice, String lang, Boolean isPictorial) {
        final String userNameTag = "【分享人昵称】", goodsNameTag = "【商品名称】", goodsPriceTag = "【商品价格】";

        GoodsShareConfig goodsShareConfig = shopCommonConfigService.getGoodsShareConfig();
        String shareDoc = null;
        // 分享
        if (!isPictorial) {
            // 自定义文案
            if (!GoodsShareConfig.DEFAULT_VALUE.equals(goodsShareConfig.getGoodsShareCommon())) {
                shareDoc = goodsShareConfig.getCommonDoc();
            }
        } else {
            // 自定义文案
            if (!GoodsShareConfig.DEFAULT_VALUE.equals(goodsShareConfig.getGoodsSharePictorial())) {
                shareDoc = goodsShareConfig.getPictorialDoc();
            }
        }
        if (shareDoc != null) {
            String moneyFlag = Util.translateMessage(lang, JsonResultMessage.WX_MA_PICTORIAL_MONEY, "messages");
            shareDoc = shareDoc.replace(userNameTag, userName);
            shareDoc = shareDoc.replace(goodsNameTag, goodsName);
            shareDoc = shareDoc.replace(goodsPriceTag, goodsPrice.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + moneyFlag);
            if (!isPictorial) {
                shareDoc = shareDoc.length() > 23 ? shareDoc.substring(0, 23) + "..." : shareDoc;
            } else {
                shareDoc = shareDoc.length() > 20 ? shareDoc.substring(0, 20) + "..." : shareDoc;
            }
        }
        return shareDoc;
    }

    /**
     * 生成表单海报背景图
     * @param userAvatarImg 用户头像
     * @param qrCodeImg     二维码
     * @param bgImg         背景图
     * @param imgPx         图片规格信息
     * @return 通过图片 buffered image
     */
    public BufferedImage createFormPictorialBgImage(BufferedImage userAvatarImg, BufferedImage qrCodeImg, BufferedImage bgImg
        , PictorialFormImgPx imgPx, String shareDpc, String accountName) {
        //设置白画布
        BufferedImage bgBufferedImage = new BufferedImage(imgPx.getBgWidth(), imgPx.getBgHeight(), BufferedImage.TYPE_USHORT_555_RGB);
        ImageUtil.addRect(bgBufferedImage, 0, 0, imgPx.getBgWidth(), imgPx.getBgHeight(), null, Color.WHITE);

        // 设置用户头像
        BufferedImage userAvatarImage = ImageUtil.makeRound(userAvatarImg, imgPx.getUserHeaderDiameter());
        ImageUtil.addTwoImage(bgBufferedImage, userAvatarImage, imgPx.getBgPadding(), imgPx.getBgPadding());

        ImageUtil.addFont(bgBufferedImage, accountName, ImageUtil.sourceHanSansCn(Font.PLAIN, 18), 140, 85, new Color(102, 102, 102));

        ImageUtil.addFont(bgBufferedImage, shareDpc, ImageUtil.sourceHanSansCn(Font.PLAIN, 22), imgPx.getBgPadding(), imgPx.getQrCodeWidth(), new Color(51, 51, 51));

        // 设置背景图
        bgImg = ImageUtil.resizeImage(imgPx.getBgImgWidth(), imgPx.getBgImgWidth(), bgImg);
        ImageUtil.addTwoImage(bgBufferedImage, bgImg, imgPx.getBgPadding(), imgPx.getHeaderHeight());

        // 设置二维码
        qrCodeImg = ImageUtil.resizeImageTransparent(imgPx.getQrCodeWidth(), imgPx.getQrCodeWidth(), qrCodeImg);
        ImageUtil.addTwoImage(bgBufferedImage, qrCodeImg, imgPx.getQrCodeStartX(), imgPx.getQrCodeStartY());

        // 设置指纹
        try {
            BufferedImage fingerImg = ImageIO.read(new URL(imageService.getImgFullUrl(FINGER_IMG)));
            fingerImg = ImageUtil.resizeImageTransparent(imgPx.getQrCodeWidth(), imgPx.getQrCodeWidth(), fingerImg);
            ImageUtil.addTwoImage(bgBufferedImage, fingerImg, imgPx.getFingerStartX(), imgPx.getFingerStartY());
        } catch (IOException e) {
            logger().error("加载指纹图片{}异常:{}", FINGER_IMG, ExceptionUtils.getStackTrace(e));
            throw new BusinessException(JsonResultCode.CODE_FAIL);
        }

        return bgBufferedImage;
    }

    public static final String UPLOAD = "upload/";
    public static final String PICTORIAL_S = "pictorial/";
    public static final String PICTORIAL_ = "pictorial_";
    public static final String SLASH = "/";
    public static final String UNDERLINE = "_";
    public static final String IMG_PNG = ".png";

    /**
     * Gets img dir.获取海报图片路径
     * @param action   the action
     * @param fileName the file name
     * @return the img dir value1:relativePath, value2:fullPath
     */
    public Tuple2<String, String> getImgDir(int action, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        String relativePath = stringBuilder.append(UPLOAD).append(this.getShopId()).append(SLASH).append(PICTORIAL_S).append(action).append(SLASH).append(fileName).toString();
        String fullPath = imageService.getImgFullUrl(relativePath);
        return new Tuple2<>(relativePath, fullPath);
    }

    /**
     * Gets img file name.获取海报图片文件名
     * @param pageId  the page id
     * @param action  the action
     * @param action1 the action 1
     * @return the img file name
     */
    public String getImgFileName(String pageId, String action, String action1) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT_FULL_NO_UNDERLINE));
        return String.join(StringUtils.EMPTY, PICTORIAL_, action, UNDERLINE, pageId, UNDERLINE, action1, UNDERLINE, date, IMG_PNG);
    }
}
