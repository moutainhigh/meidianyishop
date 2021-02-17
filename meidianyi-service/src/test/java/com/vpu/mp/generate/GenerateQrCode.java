package com.meidianyi.shop.generate;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;

import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.common.foundation.util.qrcode.QrCodeGenerator;
import com.meidianyi.shop.service.foundation.util.ImageUtil;

/**
* @author 黄壮壮
* @Date: 2019年11月27日
* @Description:
*/
public class GenerateQrCode {
	public static void main(String[] args) {
		String cardNo = "2455471008284385";
		
		// 背景图片
    	BufferedImage bgImg = null;
    	InputStream loadFile = null;
    	
    	try {
    		loadFile = Util.loadFile("image/wxapp/user_background.png");
			bgImg = ImageIO.read(loadFile);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}finally {
			if(loadFile != null) {
				try {
					loadFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    	
    	bgImg = ImageUtil.resizeImage(600, 800, bgImg);
    	
    	
    	// http://jmpdevimg.weipubao.cn/upload/1/image/20190904/file.jpeg
// 会员卡头像
    	
    	String cardAvatarAddress = "http://jmpdevimg.weipubao.cn/upload/1/image/20190904/file.jpeg";
    	if(StringUtils.isBlank(cardAvatarAddress)) {
    		return;
    	}
    	
    	BufferedImage cardAvatar = null;
    	try {
    		cardAvatar = ImageIO.read(new URL(cardAvatarAddress));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
    	
    	
    	cardAvatar = ImageUtil.makeRound(cardAvatar, 110);
    	
    	
    	
    	
    	// 会员卡二维码
    	BufferedImage cardQrCode = null;
    	try {
			byte[] qrCodeByte = QrCodeGenerator.generateQrCodeImg(cardNo, 450, 450);
			ByteArrayInputStream bais = new ByteArrayInputStream(qrCodeByte);
			cardQrCode = ImageIO.read(bais);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	
    	
    	// 会员卡条形码
    	BufferedImage cardBarCode = null;
    	try {
			byte[] barCodeByte = QrCodeGenerator.generateBarCodeImg(cardNo, 560, 100);
			ByteArrayInputStream bais = new ByteArrayInputStream(barCodeByte);
			cardBarCode = ImageIO.read(bais);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	
    	ImageUtil.addTwoImage(bgImg, cardQrCode, 65, 200);
    	
    	// add color
    	Color color = Color.decode("#3daebf");
    	Graphics2D graph = bgImg.createGraphics();
    	graph.setPaint(color);
    	graph.fillRect(0, 0, 600, 208);
    	
    	
    	
    	// 背景图片
    	/**
    	String bgImgUrl = "http://jmpdevimg.weipubao.cn/upload/245547/image/20191119/NBFzWGNe55SJIackXqLt.jpeg";
    	BufferedImage bgImgTwo = null;
    	try {
    		bgImgTwo = ImageIO.read(new URL(bgImgUrl));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
    	bgImgTwo  = ImageUtil.resizeImage(600, 208, bgImgTwo);
    	ImageUtil.addTwoImage(bgImg, bgImgTwo, 0, 0);
    	*/
    	
    	// 会员卡名称
    	String cardName = "Q10Viking";
    	ImageUtil.addFont(bgImg, cardName, new Font(null, Font.BOLD, 30), 150, 70,
				Color.WHITE);
    	
    	// 会员卡积分
    	String dis = "9折";
    	ImageUtil.addFont(bgImg, dis, new Font(null, Font.BOLD, 22), 550, 180,
				Color.WHITE);
    	    	
    	// 会员卡号
    	
    	ImageUtil.addFont(bgImg, cardNo, new Font(null, Font.ITALIC, 22), 190, 750, Color.GRAY);
    	
    	ImageUtil.addTwoImage(bgImg, cardAvatar, 20, 35);
    	
    	
    	ImageUtil.addTwoImage(bgImg, cardBarCode,15, 620);
    	
    	File outPutFile = new File("./MyCardQrCode.jpg");
    	try {
			ImageIO.write(bgImg, "jpg", outPutFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
