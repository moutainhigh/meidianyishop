package com.meidianyi.shop.service.shop.image.postertraits;

import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.foundation.util.ImageUtil;
import com.meidianyi.shop.service.pojo.shop.image.UserCenterTraitVo;
import com.meidianyi.shop.service.pojo.shop.qrcode.QrCodeTypeEnum;
import com.meidianyi.shop.service.pojo.wxapp.account.UserInfo;
import com.meidianyi.shop.service.shop.user.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 生成分享图片
 *
 * @author zhaojianqiang
 *
 *         2019年10月17日 下午5:09:19
 */
@Service
public class UserCenterTraitService extends ShopBaseService {
	@Autowired
	private UserService user;
	@Autowired
	public PictorialService pService;

	private final static byte PSTATUS_ZERO = 0;
	private final static byte PSTATUS_ONE = 1;

	public UserCenterTraitVo getUserCenter(Integer userId) {
		UserInfo userInfo = user.getUserInfo(userId);
		UserCenterTraitVo vo = new UserCenterTraitVo();
		logger().info("读取背景图");
		// 读取背景图片
		BufferedImage backgroundImage = null;
		InputStream loadFile = null;
		try {
			loadFile = Util.loadFile("image/wxapp/user_background.png");
			backgroundImage = ImageIO.read(loadFile);
		} catch (IOException e) {
			vo.setMsg(JsonResultCode.WX_GETBG_FAIL);
			vo.setStatus(PSTATUS_ZERO);
			logger().error(e.getMessage(), e);
			return vo;
		} finally {
			if (loadFile != null) {
				try {
					loadFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// 重新设置大小
		backgroundImage = ImageUtil.resizeImageTransparent(600, 800, backgroundImage);
		logger().info("重新设置大小完成");

		logger().info("获取微信二维码");
		// 获取微信二维码地址
		String mpQrCode = user.qrCode.getMpQrCode(QrCodeTypeEnum.INVITE, "invite_id=" + userId);
		if (StringUtils.isEmpty(mpQrCode)) {
			//小程序获取二维码失败
			vo.setMsg(JsonResultCode.WX_GETQRCODE_FAIL);
			vo.setStatus(PSTATUS_ZERO);
			return vo;
		}

		// 微信微信二维码
		BufferedImage qrCodeImage = null;
		try {
			qrCodeImage = ImageIO.read(new URL(mpQrCode));
		} catch (Exception e) {
			//小程序读取二维码失败
			vo.setMsg(JsonResultCode.WX_READQRCODE_FAIL);
			vo.setStatus(PSTATUS_ZERO);
			logger().error(e.getMessage(), e);
			return vo;
		}
		logger().info("获取微信头像");
		// 微信头像
		BufferedImage avatarImage = null;
		try {
			avatarImage = ImageIO.read(new URL(userInfo.getUserAvatar()));
		} catch (Exception e) {
			//小程序获取头像失败
			vo.setMsg(JsonResultCode.WX_GETHEAD_FAIL);
			vo.setStatus(PSTATUS_ZERO);
			logger().error(e.getMessage(), e);
			return vo;
		}

		// 把头像盘圆
		avatarImage = ImageUtil.makeRound(avatarImage, 110);

		// 添加字体
		ImageUtil.addFont(backgroundImage, userInfo.getUsername(), ImageUtil.sourceHanSansCn(Font.BOLD, 30), 180, 100,
				Color.BLACK);

		ShopRecord shop = saas.shop.getShopById(getShopId());
		//分享给你一个好店铺
		String titel1 = Util.translateMessage(shop.getShopLanguage(), JsonResultCode.WX_SHARESHOP.getMessage(),"messages",null);
		logger().info("titel1:"+titel1);
		ImageUtil.addFont(backgroundImage, titel1, ImageUtil.sourceHanSansCn(Font.BOLD, 22), 180, 145, Color.GRAY);
		//扫一扫上面的二维码，进店选购商品
		String titel2 = Util.translateMessage(shop.getShopLanguage(), JsonResultCode.WX_SCAN_QRSHOP.getMessage(),"messages",null);
		logger().info("titel2:"+titel2);
		ImageUtil.addFont(backgroundImage, titel2, ImageUtil.sourceHanSansCn(Font.BOLD, 22), 120, 750, Color.GRAY);

		// 合并头像图片
		ImageUtil.addTwoImage(backgroundImage, avatarImage, 50, 60);

		// 合并二维码图片
		ImageUtil.addTwoImage(backgroundImage, qrCodeImage, 100, 260);

		logger().info("开始转换成base64");
		String base64 = ImageUtil.toBase64(backgroundImage);
		vo.setImage(base64);
		vo.setStatus(PSTATUS_ONE);
		return vo;

	}
}
