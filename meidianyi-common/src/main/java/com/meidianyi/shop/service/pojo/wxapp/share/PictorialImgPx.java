package com.meidianyi.shop.service.pojo.wxapp.share;

import com.meidianyi.shop.service.foundation.util.ImageUtil;
import lombok.Data;

import java.awt.*;

/**
 * 海报背景图片内容相关像素
 * @author 李晓冰
 * @date 2019年12月31日
 */
@Data
public class PictorialImgPx {

    /**
     * 海报样式
     */
    public static final Byte DEFAULT_STYLE = 0;
    /**
     * 基础样式
     */
    public static final Byte BASIC_STYLE = 1;
    /**
     * 显示分享人信息
     */
    public static final Byte SHARE_PERSON_STYLE = 2;
    /**
     * 显示店铺信息
     */
    public static final Byte SHOP_STYLE = 3;
    /**
     * 分享人+店铺信息
     */
    public static final Byte SHARE_PERSON_SHOP_STYLE = 4;

    public static final Color SHOP_DEFAULT_STYLE_COLOR = new Color(255, 102, 102);

    /***********************不随店铺风格改变的色值***************************/
    /**
     * 商品名称颜色
     */
    public static final Color GOODS_NAME_COLOR = new Color(52, 52, 52);

    /**
     * 底部划线价颜色
     */
    public static final Color LINE_PRICE_COLOR = new Color(146, 146, 146);

    /**
     * 活动-自定义区域内部字体颜色
     */
    public static final Color CUSTOMER_TEXT_FONT_COLOR = new Color(255, 255, 255);

    /***********************随店铺风格或海报样式改变的色值***************************/
    private Color headFontColor;

    private Color shareImgRectInnerColor;
    /**
     * 活动-自定义区域内部填充颜色
     */
    private Color customerRectFillColor;
    /**
     * 底部商品价格颜色
     */
    private Color realPriceColor;

    private Color shopStyleColor;

    /***********************随店铺风格改变的色值结束***************************/
    /**
     * 可用的三种字体size
     */
    public static final Integer SMALL_FONT_SIZE = 24;
    public static final Integer MEDIUM_FONT_SIZE = 28;
    public static final Integer LARGE_FONT_SIZE = 33;

    /**
     * 背景图宽
     */
    private Integer bgWidth = 750;
    /**
     * 背景图内边距
     */
    private Integer bgPadding = 30;
    private Integer borderStroke = 15;
    /**
     * 图片底部padding
     */
    private Integer bottomPadding = 10;

    public PictorialImgPx() {
    }

    public PictorialImgPx(Color shopStyleColor) {
        this(DEFAULT_STYLE, shopStyleColor);
    }

    public PictorialImgPx(Byte picStyleConfig, Color shopStyleColor) {
        customerRectFillColor = new Color(shopStyleColor.getRed(), shopStyleColor.getGreen(), shopStyleColor.getBlue(), 140);
        shareImgRectInnerColor = new Color(shopStyleColor.getRed(), shopStyleColor.getGreen(), shopStyleColor.getBlue(), 30);
        realPriceColor = shopStyleColor;
        this.shopStyleColor = shopStyleColor;

        if (BASIC_STYLE.equals(picStyleConfig)) {
            initForBasicStyle();
        } else {
            if (!DEFAULT_STYLE.equals(picStyleConfig)) {
                headerHeight = 130;
            }
            // 默认样式值初始化
            initForDefault();

            if (!DEFAULT_STYLE.equals(picStyleConfig)) {
                headFontColor = Color.WHITE;
                majorAxis = bgWidth + bgWidth / 2;
                minorAxis = 500;
                bgCircleStartX = (bgWidth - majorAxis) / 2;
                bgCircleStartY = -100;
            }

            if (SHARE_PERSON_STYLE.equals(picStyleConfig)) {
                initForSharePersonInfoStyle();
            } else if (SHOP_STYLE.equals(picStyleConfig)) {
                initForShareShopInfoStyle();
            } else if (SHARE_PERSON_SHOP_STYLE.equals(picStyleConfig)) {
                initForSharePersonShopInfoStyle();
            } else {

            }
        }
        goodsNameFont = ImageUtil.sourceHanSansCn(Font.PLAIN, MEDIUM_FONT_SIZE);
        priceFont = ImageUtil.sourceHanSansCn(Font.PLAIN, PictorialImgPx.LARGE_FONT_SIZE);
        linePriceFont = ImageUtil.sourceHanSansCn(Font.PLAIN, PictorialImgPx.MEDIUM_FONT_SIZE);
    }

    private void initForDefault() {
        headFontColor = new Color(85, 85, 85);
        // 用户名
        userNameX = headerStartX + userHeaderDiameter + 15;
        userNameY = headerStartY + userHeaderDiameter / 5 + 5;
        userNameFont = ImageUtil.sourceHanSansCn(Font.PLAIN, SMALL_FONT_SIZE);

        // 宣传语
        shareDocX = bgPadding;
        shareDocY = headerStartY + userHeaderDiameter + 7;
        shareDocFont = ImageUtil.sourceHanSansCn(Font.PLAIN, MEDIUM_FONT_SIZE);
        shareDocCanUseWidth = bgWidth - 2 * bgPadding;

        // 商品
        goodsWidth = bgWidth - 2 * bgPadding;
        goodsStartX = bgPadding;
        goodsStartY = bgPadding + headerHeight;

        //自定义区域
        customerRectHeight = 60;
        customerRectWidth = goodsWidth;
        customerRectStartX = bgPadding;
        customerRectStartY = bgPadding + headerHeight + goodsHeight - customerRectHeight;
        // 自定义区域图标、文字位置设置
        customerIconWidth = 40;
        customerIconHeight = 30;
        customerIconStartX = customerRectStartX + 20;
        customerIconStartY = customerRectStartY + 17;

        customerTextStartX = customerRectStartX + 20;
        customerTextStartY = customerRectStartY + 12;
        customerSecondTextStartY = customerTextStartY+7;

        bottomStartY = headerHeight + goodsHeight + bgPadding + 5;
        // 底部二维码
        qrCodeStartX = bgWidth - (bgPadding + qrCodeWidth);
        qrCodeStartY = bottomStartY + 15;
        // 商品名称可用宽度，开始位置
        goodsNameCanUseWidth = bgWidth - 2 * bgPadding - qrCodeWidth - 20;
        goodsNameStartY = bottomStartY + 10;

        bottomTextStartX = bgPadding;
        priceY = goodsNameStartY + priceNamePadding;

    }

    private void initForBasicStyle() {
        headFontColor = new Color(85, 85, 85);
        headerHeight = 0;
        bgPadding = bgPadding + borderStroke;
        // 商品
        goodsWidth = bgWidth - 2 * bgPadding;
        goodsStartX = bgPadding;
        goodsStartY = bgPadding + headerHeight;
        goodsHeight = bgWidth - 2 * bgPadding;

        bottomHeight = 400;
        bgHeight = headerHeight + bottomHeight + goodsHeight + bgPadding + bottomPadding;
        bottomStartY = headerHeight + goodsHeight + bgPadding + 5;
        bottomTextStartX = bgPadding;

        priceY = bottomStartY + 10;
        priceX = bottomTextStartX;

        goodsNameCanUseWidth = bgWidth - 2 * bgPadding;
        qrCodeStartX = (bgWidth - qrCodeWidth) / 2;
    }

    /**
     * 样式3，带分享人信息
     */
    private void initForSharePersonInfoStyle() {
        userNameY = headerStartY + userHeaderDiameter / 8;
        userNameFont = ImageUtil.sourceHanSansCn(Font.PLAIN, MEDIUM_FONT_SIZE);
        // 宣传语
        shareDocX = userNameX;
        shareDocY = headerStartY + userHeaderDiameter - 40;
        shareDocFont = ImageUtil.sourceHanSansCn(Font.PLAIN, SMALL_FONT_SIZE);
        shareDocCanUseWidth = bgWidth - shareDocX - bgPadding;
    }

    /**
     * 样式4，分享带店铺信息
     */
    private void initForShareShopInfoStyle() {
        userNameY = headerStartY + userHeaderDiameter / 4;
        userNameFont = ImageUtil.sourceHanSansCn(Font.PLAIN, LARGE_FONT_SIZE);
    }

    /**
     * 样式5，分享带店铺和人员信息
     */
    private void initForSharePersonShopInfoStyle() {
        initForSharePersonInfoStyle();

        // 店铺头像信息
        shopInfoStartY = goodsStartY + goodsHeight - 150;
    }

    /**
     * 图片头部
     */
    private Integer headerHeight = 160;
    /**
     * 用户头像直径
     */
    private final Integer userHeaderDiameter = 96;
    private final Integer headerStartX = bgPadding;
    private final Integer headerStartY = bgPadding;

    /**
     * 用户名开始x,y坐标
     */
    private Integer userNameX;
    private Integer userNameY;
    private Font userNameFont;
    /**
     * 商品shareDoc内容
     */
    private Integer shareDocX;
    private Integer shareDocY;
    private Font shareDocFont;
    private Integer shareDocCanUseWidth;

    /**
     * 样式3,4,5中的背景椭圆
     */
    private Integer bgCircleStartX;
    private Integer bgCircleStartY;
    private Integer majorAxis;
    private Integer minorAxis;

    /**
     * 商品宽高,位置x,y
     */
    private Integer goodsWidth;
    private Integer goodsHeight = bgWidth - 2 * bgPadding;
    private Integer goodsStartX;
    private Integer goodsStartY;

    /**
     * 同时显示用户和店铺信息时店铺的位置信息
     */
    private Integer shopInfoHeight = 70;
    private Integer shopIconWrapHeight = shopInfoHeight;
    private Integer shopIconDiameter = 50;
    private Integer shopIconStartX;
    private Integer shopInfoTextStartX;
    private Color shopInfoBgColor = new Color(200, 200, 200, 200);

    public Integer getShopIconWrapStartX(Integer textWidth) {
        return goodsStartX + goodsWidth - textWidth - shopIconWrapHeight;
    }

    public Integer getShopInfoRectStartX(Integer textWidth) {
        return goodsStartX + goodsWidth - textWidth - shopIconWrapHeight / 2;
    }

    public Integer getShopIconStartX(Integer wrapIconStartX) {
        return wrapIconStartX + (shopIconWrapHeight - shopIconDiameter) / 2;
    }

    public Integer getShopIconStartY(Integer wrapIconStartY) {
        return wrapIconStartY + (shopIconWrapHeight - shopIconDiameter) / 2;
    }

    public Integer getShopInfoTextStartX(Integer wrapIconStartX) {
        return wrapIconStartX + shopIconWrapHeight;
    }

    public Integer getShopInfoTextStartY(Integer wrapIconStartY) {
        return wrapIconStartY + shopInfoHeight / 2+7;
    }

    private Integer shopInfoStartY;
    private Font shopInfoNameFont = ImageUtil.sourceHanSansCn(Font.PLAIN, SMALL_FONT_SIZE);
    /**
     * 图片上方，各个活动自定义内容区域
     * 自定义内容区域,背景边框位置和大小
     */
    private Integer customerRectHeight;
    private Integer customerRectWidth;
    private Integer customerRectStartX;
    private Integer customerRectStartY;
    /**
     * 自定内容图标宽高和位置
     */
    private Integer customerIconWidth;
    private Integer customerIconHeight;
    private Integer customerIconStartX;
    private Integer customerIconStartY;

    private Integer customerTextStartY;
    private Integer customerTextStartX;
    private Integer customerSecondTextStartY;


    /*****************海报底部图片配置参数******************/
    /**
     * 底部高度
     */
    private Integer bottomHeight = 230;
    /**
     * 图片总高度,底部padding设置为10
     */
    private Integer bgHeight = headerHeight + bottomHeight + goodsHeight + bgPadding + bottomPadding;

    /**
     * 底部开始Y
     */
    private Integer bottomStartY;

    /**
     * 二维码直径
     */
    private Integer qrCodeWidth = 180;
    /**
     * 二维码X
     */
    private Integer qrCodeStartX;
    private Integer qrCodeStartY;

    private Integer bottomTextStartX = bgPadding;

    /**
     * 商品名称可使用的最大宽度
     */
    private Integer goodsNameCanUseWidth;
    /**
     * 商品名称开始Y
     */
    private Integer goodsNameStartY;
    private Font goodsNameFont;

    /**
     * 商品价格和名字的距离
     */
    private Integer priceNamePadding = 50;
    /**
     * 底部商品价格开始y,需要根据商品名称行数计算得到
     */
    private Integer priceY;
    private Integer priceX;
    private Font priceFont;
    private Font linePriceFont;
    /**
     * 价格之间的距离
     */
    private Integer priceMargin = 10;

    public Integer getActivityTipTextY() {
        return priceY + 5;
    }

    /**
     * 底部划线价，线的Y
     */
    public Integer getPriceLineY() {
        return priceY+4;
    }

}
