package com.meidianyi.shop.service.foundation.util;

import com.thoughtworks.xstream.core.util.Base64Encoder;
import com.meidianyi.shop.common.foundation.util.Util;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;


/**
 * 图片的处理
 * @author zhaojianqiang
 * <p>
 * 2019年10月18日 上午9:42:04
 */
@Slf4j
public final class ImageUtil {
    /**
     * 重新设置图片大小
     * @param width
     * @param height
     * @param bufferedImage
     * @return
     */
    public static BufferedImage resizeImage(int width, int height, BufferedImage bufferedImage) {
        BufferedImage newBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_555_RGB);
        newBufferedImage.getGraphics().drawImage(bufferedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0,
            0, null);
        return newBufferedImage;
    }


    /**
     * 重新设置图片大小，图带Alpha，存在透明像素时调用
     * @param width
     * @param height
     * @param bufferedImage
     * @return
     */
    public static BufferedImage resizeImageTransparent(int width, int height, BufferedImage bufferedImage) {
        BufferedImage newBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        newBufferedImage.getGraphics().drawImage(bufferedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0,
            0, null);
        return newBufferedImage;
    }

    /**
     * 给图片添加文字
     * @param backgroundImage 背景图片
     * @param message         文字
     * @param font            字体
     * @param x               x轴数值
     * @param y               y轴数值 添加文本时的y值表示的是文本的底边位置
     * @param color           颜色,默认黑色
     * @returns
     */
    public static BufferedImage addFont(BufferedImage backgroundImage, String message, Font font, int x, int y, Color color) {
        Graphics2D graphics = backgroundImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 在背景图片上添加文字
        graphics.setColor(color == null ? Color.BLACK : color);
        graphics.setFont(font);
        graphics.drawString(message, x, y);
        graphics.dispose();
        return backgroundImage;
    }

    /**
     * 给图片添加文字
     * @param backgroundImage 背景图片
     * @param message         文字
     * @param font            字体
     * @param x               x轴数值
     * @param y               y轴数值
     * @param color           颜色,默认黑色
     * @param onBaseLine      true表示y值是文本底边位置
     * @returns
     */
    public static BufferedImage addFont(BufferedImage backgroundImage, String message, Font font, int x, int y, Color color, boolean onBaseLine) {
        if (!onBaseLine) {
            int textHeight = getTextAscent(backgroundImage, font);
            y += textHeight;
        }
        return addFont(backgroundImage, message, font, x, y, color);
    }

    /**
     * 在图片上绘制矩形
     * @param bufferedImage 目标背景图对象
     * @param x             矩形左上角x位置
     * @param y             矩形左上角y位置
     * @param width         矩形宽度
     * @param height        矩形高度
     * @param lineColor     矩形边框线颜色，如果为null则表示不画边框色，直接进行填充
     * @param fillColor     矩形填充颜色，如果为null表示不填充颜色
     */
    public static void addRect(BufferedImage bufferedImage, int x, int y, int width, int height, Color lineColor, Float strokeVal, Color fillColor) {
        if (lineColor == null && fillColor == null) {
            return;
        }
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (fillColor != null) {
            graphics.setColor(fillColor);
            graphics.fillRect(x, y, width, height);
        }
        if (lineColor != null) {
            graphics.setColor(lineColor);
            Stroke stroke = null;
            if (strokeVal != null) {
                stroke = graphics.getStroke();
                graphics.setStroke(new BasicStroke(strokeVal));
            }
            graphics.drawRect(x, y, width, height);
            if (stroke != null) {
                graphics.setStroke(stroke);
            }
        }
        graphics.dispose();
    }

    public static void addRect(BufferedImage bufferedImage, int x, int y, int width, int height, Color lineColor, Color fillColor) {
        addRect(bufferedImage, x, y, width, height, lineColor, null, fillColor);
    }

    /**
     * 添加文字附带边框
     * @param bufferedImage 背景图
     * @param x             开始x
     * @param y             开始y
     * @param message       文字内容
     * @param font          文本字体
     * @param lineColor     边框线颜色
     * @param fillColor     边框内部填充颜色
     * @param fontColor     文本颜色
     */
    public static int addFontWithRect(BufferedImage bufferedImage, int x, int y, String message, Font font, Color lineColor, Color fillColor, Color fontColor) {
        int paddingLeft = 5;
        int paddingBottom = font.getSize() / 4;
        int fontHeight = getTextAscent(bufferedImage, font) + paddingBottom;
        int textWidth = getTextWidth(bufferedImage, font, message);
        addRect(bufferedImage, x, y, textWidth + 2 * paddingLeft, fontHeight, lineColor, fillColor);
        // 添加文本时的y值表示的是文本的底边位置
        addFont(bufferedImage, message, font, x + paddingLeft, y + fontHeight - paddingBottom, fontColor);

        return textWidth + 2 * paddingLeft;
    }

    /**
     * 添加线段
     * @param bufferedImage 目标背景图对象
     * @param x1            开始x
     * @param y1            开始y
     * @param x2            结束x
     * @param y2            结束y
     * @param color         颜色
     */
    public static void addLine(BufferedImage bufferedImage, int x1, int y1, int x2, int y2, Color color) {
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(color);
        graphics.drawLine(x1, y1, x2, y2);
        graphics.dispose();
    }


    public static void addCircle(BufferedImage bufferedImage, int x, int y, int majorAxis,int minorAxis, Color color) {
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setColor(color);
        graphics.fillArc(x, y, majorAxis, minorAxis, 0, 360);
    }

    /**
     * 添加文字附带中划线
     * @param bufferedImage 背景图片
     * @param x             文本左上角开始x坐标
     * @param y             文本左上角开始y坐标
     * @param message       文本内容
     * @param font          字体
     * @param fontColor     字体颜色
     * @return rectWidth
     */
    public static int addFontWithLine(BufferedImage bufferedImage, int x, int y, String message, Font font, Color fontColor) {
        int linePad = 5;
        int textWidth = getTextWidth(bufferedImage, font, message);
        int textHeight = getTextAscent(bufferedImage, font);
        addFont(bufferedImage, message, font, x + linePad, y + textHeight, fontColor);
        addLine(bufferedImage, x, y + textHeight / 3 * 2, x + textWidth + 2 * linePad, y + textHeight / 3 * 2, fontColor);
        return textWidth + 2 * linePad;
    }

    /**
     * 背景图上添加前景图
     * @param backgroundImage 背景图
     * @param foregroundImage 前景图
     * @param x               x轴数值
     * @param y               y轴数值
     * @return
     */
    public static BufferedImage addTwoImage(BufferedImage backgroundImage, BufferedImage foregroundImage, int x, int y) {
        Graphics2D graphics = backgroundImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.drawImage(foregroundImage, x, y, foregroundImage.getWidth(), foregroundImage.getHeight(), null);
        graphics.dispose();
        return backgroundImage;

    }


    /**
     * 图片裁成圆形
     * @param image    图片
     * @param diameter 直径
     * @return
     */
    public static BufferedImage makeRound(BufferedImage image, Integer diameter) {
        // 透明底的图片
        BufferedImage formatAvatarImage = new BufferedImage(diameter, diameter, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D createGraphics = formatAvatarImage.createGraphics();
        createGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 留一个像素的空白区域，这个很重要，画圆的时候把这个覆盖
        int border = 1;
        // 图片是一个圆型
        Ellipse2D.Double shape = new Ellipse2D.Double(border, border, diameter - border * 2, diameter - border * 2);
        createGraphics.setClip(shape);
        createGraphics.drawImage(image, border, border, diameter - border * 2, diameter - border * 2, null);
        createGraphics.dispose();
        return formatAvatarImage;
    }

    /**
     * 通过读取文件并获取其width及height的方式，来判断判断当前文件是否图片，这是一种非常简单的方式。
     * @param imageFile
     * @return
     */
    public static boolean isImage(File imageFile) {
        if (!imageFile.exists()) {
            return false;
        }
        Image img = null;
        try {
            img = ImageIO.read(imageFile);
            if (img == null || img.getWidth(null) <= 0 || img.getHeight(null) <= 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            img = null;
        }
    }

    /**
     * image转base64
     * @param image
     * @return
     */
    public static String toBase64(BufferedImage image) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        byte[] byteArray = os.toByteArray();
        Base64Encoder encoder = new Base64Encoder();
        String encode = encoder.encode(byteArray);
        return "data:image/png;base64," + encode;
    }

    /**
     * 生成字体，思源黑体
     * @param style Font.BOLD
     * @param size  大小
     * @return
     */
    public static Font sourceHanSansCn(Integer style, Integer size) {
        Font actionJsonBase = new Font(null, style, size);
        InputStream loadFile = Util.loadFile("font/wxapp/SourceHanSansCN-Normal.ttf");
        try {
            Font createFont = Font.createFont(Font.TRUETYPE_FONT, loadFile);
            actionJsonBase = createFont.deriveFont(style, size);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return actionJsonBase;
    }

    /**
     * 获取文本内容的宽度
     * @param font 文本使用的字体
     * @param text 文本内容
     * @return 文本宽度
     */
    public static Integer getTextWidth(BufferedImage bufferedImage, Font font, String text) {
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setFont(font);
        FontMetrics metrics = graphics.getFontMetrics(font);
        int width = 0;
        for (int i = 0; i < text.length(); i++) {
            width += metrics.charWidth(text.charAt(i));
        }
        return width;
    }

    /**
     * 获取对应字体文本的ascent高度
     * 字体高度height包括：ascent+descent+leading高度
     * ascent:字母b k的高度（外加了一个留白，不同字体的留白存在差异）
     * descent:字母p g的下半部分高度（一般是负数）
     * leading:是当前行和上一行的间隔高度
     * @param bufferedImage
     * @param font
     * @return
     */
    public static Integer getTextAscent(BufferedImage bufferedImage, Font font) {
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setFont(font);
        FontMetrics metrics = graphics.getFontMetrics(font);
        return metrics.getAscent();
    }

    /**
     * 将图片转换为字节数组
     * @param image
     * @return byte[]
     */
    public static byte[] changeImageToByteArr(BufferedImage image) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return os.toByteArray();
    }
}
