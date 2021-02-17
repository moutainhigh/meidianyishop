package com.meidianyi.shop.service.shop.image;

import com.upyun.UpException;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.common.foundation.util.qrcode.QrCodeGenerator;
import com.meidianyi.shop.db.main.tables.records.MpAuthShopRecord;
import com.meidianyi.shop.db.shop.tables.records.CodeRecord;
import com.meidianyi.shop.db.shop.tables.records.MemberCardRecord;
import com.meidianyi.shop.db.shop.tables.records.WxpUnlimitSceneRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.foundation.util.CardUtil;
import com.meidianyi.shop.service.foundation.util.ImageUtil;
import com.meidianyi.shop.service.pojo.shop.qrcode.QrCodeTypeEnum;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import static com.meidianyi.shop.db.shop.tables.Code.CODE;
import static com.meidianyi.shop.db.shop.tables.WxpUnlimitScene.WXP_UNLIMIT_SCENE;
import static java.lang.String.format;


/**
 * 小程序码
 *
 * @author 郑保乐
 */
@Service
@Slf4j
public class QrCodeService extends ShopBaseService {

    private final ImageService imageService;

    public QrCodeService(ImageService imageService) {
        this.imageService = imageService;
    }

    /**
     *  获取小程序分享码，无url参数
     * @param typeEnum 活动类型
     * @return null 无法获取，否则对应二维码地址
     */
    public String getMpQrCode(QrCodeTypeEnum typeEnum) {
        return getMpQrCode(typeEnum, "");
    }

    /**
     *  获取小程序分享码
     * @param typeEnum 活动类型
     * @param  paramStr url参数
     * @return null 无法获取，否则对应二维码地址
     */
    public String getMpQrCode(QrCodeTypeEnum typeEnum, String paramStr) {
        String typeUrl = StringUtils.isBlank(paramStr) ? typeEnum.getUrl() : typeEnum.getUrl() + "?" + paramStr;
        String paramId = Util.md5(typeUrl);

        return getMpQrCode(typeEnum.getUrl(),typeEnum.getType(),paramStr,paramId);
    }

    /**
     *  获取小程序分享码
     * @param typeEnum 活动类型
     * @param  paramId
     * @return null 无法获取，否则对应二维码地址
     */
    public CodeRecord getMpQrCode(QrCodeTypeEnum typeEnum, int paramId) {
        return db().selectFrom(CODE)
            .where(CODE.PARAM_ID.eq(Integer.toString(paramId))).and(CODE.TYPE.eq(typeEnum.getType())).and(CODE.DEL_FLAG.eq(DelFlag.NORMAL.getCode()))
            .fetchAny();
    }

    /**
     * 获取小程序码
     * @param typeUrl 带参数的小程序页面url
     * @param type  类型id
     * @param paramId 记录的唯一值，由typeUrl加密后产生
     * @return 小程序码图片url，null表示无法获取相应二维码
     */
    private String getMpQrCode(String typeUrl, Short type, String paramStr,String paramId) {
        String relativePath = db().select(CODE.QRCODE_IMG).from(CODE)
            .where(CODE.PARAM_ID.eq(paramId)).and(CODE.TYPE.eq(type)).and(CODE.DEL_FLAG.eq(DelFlag.NORMAL.getCode()))
            .fetchAny(CODE.QRCODE_IMG);
        log.debug("get img url from db:{}", relativePath);

        //数据库存在该图片路劲
        if (!StringUtils.isBlank(relativePath)) {
            String fullPath=this.imageService.imageUrl(relativePath);

            try {
                //判断upYun上是否有该图片
                Map<String, String> fileInfo = this.imageService.getUpYunClient().getFileInfo(relativePath);
                if (fileInfo != null) {
                    //有图片则直接返回图片全路径
                    return fullPath;
                }
            } catch (IOException | UpException e) {
                //如果失败则认为图片不存在
                logger().warn("upYun 获取图片信息失败："+e.getMessage());
            }
            //upYun不存在则将该记录设置为删除状态
            db().update(CODE).set(CODE.DEL_FLAG,DelFlag.DISABLE.getCode())
                .where(CODE.PARAM_ID.eq(paramId)).execute();
        }

        //获取小程序分享码
        Integer shopId = getShopId();
        log.info("获取二维码，shopId:"+shopId);
        MpAuthShopRecord mp = saas.shop.mp.getAuthShopByShopId(shopId);
        if(mp==null) {
        	log.info("店铺还没有授权小程序，shopId:"+shopId);
        	return null;
        }
        String appId =  mp.getAppId();
        log.info("获取二维码，appId:"+appId);

        //二维码图片大小
        int qrcodWidth = 430;

        try {
            log.debug("调取微信接口，尝试请求二维码图片");
            byte[] qrcodeBytes = open().getWxOpenComponentService().getWxMaServiceByAppid(appId)
                .getQrcodeService().createWxaCodeUnlimitBytes(paramStr,typeUrl, qrcodWidth,true,null,true);
            log.debug("调取微信二维码接口，图片字节长度：{}",qrcodeBytes==null? 0 : qrcodeBytes.length);

            relativePath = getQrCodeImgRelativePath(type) + format("T%sP%s_%s.jpg", type, paramId, DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL_NO_UNDERLINE));

            this.imageService.getUpYunClient().writeFile(relativePath, qrcodeBytes, true);
            log.debug("通过UpYun将二进制写入磁盘，磁盘路径{}",relativePath);
        } catch (WxErrorException e) {
            logger().warn("获取小程序分享码错误：" + e.getMessage());
            return null;
        } catch (Exception e) {
            logger().warn("upYun上传文件错误：" + e.getMessage());
            return null;
        }

        CodeRecord codeRecord = db().newRecord(CODE);
        codeRecord.setType(type);
        codeRecord.setParamId(paramId);
        codeRecord.setTypeUrl(typeUrl);
        codeRecord.setQrcodeImg(relativePath);
        codeRecord.insert();

        return this.imageService.imageUrl(relativePath);
    }

    public String getQrCodeImgRelativePath(short type){
        return format("upload/%s/qrcode/%s/",getShopId(), type);
    }


    /**
     * 生成用户会员卡二维码图
     * @return 二维码地址
     */
    public String getUserCardQrCode(String cardNo,MemberCardRecord card) {

		// 	获取底图背景
		BufferedImage bgImg = getBgImg(600, 800);
    	if(isNull(bgImg)) {
    		return null;
    	}
    	//	获取会员卡头像
    	BufferedImage cardAvatar = getCardVatar();
    	if(isNull(cardAvatar)) {
    		return null;
    	}
    	// 	获取会员卡二维码
    	BufferedImage cardQrCode = getCardNoQrCode(cardNo);
    	if(isNull(cardQrCode)) {
    		return null;
    	}

    	// 	获取会员卡条形码
    	BufferedImage cardBarCode = getCardNoBarCode(cardNo);
    	if(isNull(cardBarCode)) {
    		return null;
    	}

    	// 设置会员卡号二维码
    	ImageUtil.addTwoImage(bgImg, cardQrCode, 65, 200);
    	// 设置背景
    	setCardBgType(bgImg,card);
    	// 设置卡名称
    	ImageUtil.addFont(bgImg, card.getCardName(), ImageUtil.sourceHanSansCn(Font.BOLD, 30), 150, 70,
				Color.WHITE);
    	// 设置折扣
    	if(null != card.getDiscount()) {
    		String dis = card.getDiscount().toString()+" 折";
        	ImageUtil.addFont(bgImg, dis, ImageUtil.sourceHanSansCn(Font.BOLD, 22), 500, 180,
    				Color.WHITE);
    	}
    	// 会员卡号
    	ImageUtil.addFont(bgImg, cardNo, new Font(null, Font.ITALIC, 22), 190, 750, Color.GRAY);
    	// 会员卡头像
    	ImageUtil.addTwoImage(bgImg, cardAvatar, 20, 35);
    	// 会员卡条形码
    	ImageUtil.addTwoImage(bgImg, cardBarCode,15, 620);
    	return ImageUtil.toBase64(bgImg);
    }

	/**
	 * 	设置会员卡背景
	 */
	private void setCardBgType(BufferedImage bgImg,MemberCardRecord card) {
		// add color
    	Byte bgType = card.getBgType();
    	if(isNull(bgType)) {
    		return;
    	}
    	Integer bgWidth = 600,bgHeight = 208;
    	if(CardUtil.isBgColorType(bgType)) {
    		logger().info("设置会员卡颜色");

    		if(StringUtils.isBlank(card.getBgColor())) {
    			// 默认背景色
    			card.setBgColor(CardUtil.getDefaultBgColor());
    		}

	    	Color color = Color.decode(card.getBgColor());


	    	Graphics2D graph = bgImg.createGraphics();
	    	graph.setPaint(color);
	    	graph.fillRect(0, 0, bgWidth, bgHeight);
    	}else if(CardUtil.isBgImgType(bgType)){
    		// 背景图片
    		logger().info("设置会员卡背景图片");
        	String bgImgUrl = imageService.imageUrl(card.getBgImg());
    		BufferedImage bgImgTwo = null;
        	try {
        		bgImgTwo = ImageIO.read(new URL(bgImgUrl));
    		} catch (Exception e) {
    			logger().info("背景图片解析失败: "+bgImgUrl);
    			logger().info(e.getMessage(),e);
    			return;
    		}
        	bgImgTwo  = ImageUtil.resizeImage(bgWidth, bgHeight, bgImgTwo);
        	ImageUtil.addTwoImage(bgImg, bgImgTwo, 0, 0);
    	}
	}

    /**
     * 生成会员卡号条形码
     */
	private BufferedImage getCardNoBarCode(String cardNo) {
		BufferedImage cardBarCode = null;
		Integer width = 560;
		Integer height = 100;
    	try {
			byte[] barCodeByte = QrCodeGenerator.generateBarCodeImg(cardNo, width, height);
			ByteArrayInputStream bais = new ByteArrayInputStream(barCodeByte);
			cardBarCode = ImageIO.read(bais);
		} catch (Exception e) {
			logger().info("会员卡号生成条形码失败");
			return null;
		}
		return cardBarCode;
	}

    /**
     * 获取会员卡号二维码
     */
	private BufferedImage getCardNoQrCode(String cardNo) {
		BufferedImage cardQrCode = null;
		Integer size = 450;
    	try {
			byte[] qrCodeByte = QrCodeGenerator.generateQrCodeImg(cardNo, size, size);
			ByteArrayInputStream bais = new ByteArrayInputStream(qrCodeByte);
			cardQrCode = ImageIO.read(bais);
		} catch (Exception e) {
			logger().info("用户会员卡卡二维码生成失败");
			return null;
		}
		return cardQrCode;
	}

    /**
     * 	获取 会员卡头像
     */
	private BufferedImage getCardVatar() {
		String shopAvatar = saas().shop.getShopAvatarById(this.getShopId());
		if(StringUtils.isBlank(shopAvatar)) {
			return null;
		}
		String cardAvatarAddress = imageService.imageUrl(shopAvatar);

    	if(StringUtils.isBlank(cardAvatarAddress)) {
    		return null;
    	}
    	BufferedImage cardAvatar = null;
    	try {
    		cardAvatar = ImageIO.read(new URL(cardAvatarAddress));
		} catch (Exception e) {
			logger().info("获取会员卡头像失败: "+ cardAvatarAddress);
			return null;
		}

    	cardAvatar = ImageUtil.makeRound(cardAvatar, 110);
		return cardAvatar;
	}

    /**
     * 	获取底层图片
     */
	private BufferedImage getBgImg(Integer width,Integer height) {
		String userBgPath = "image/wxapp/user_background.png";
		return getBgImg(width,height,userBgPath);
	}

	private BufferedImage getBgImg(Integer width,Integer height,String userBgPath) {
		// 背景图片
    	BufferedImage bgImg = null;
    	InputStream loadFile = null;
    	try {
    		loadFile = Util.loadFile(userBgPath);
			bgImg = ImageIO.read(loadFile);
		} catch (IOException e) {
			logger().info("获取背景图片失败");
			return null;
		}finally {
			if(loadFile != null) {
				try {
					loadFile.close();
				} catch (IOException e) {
					logger().info("关闭失败");
				}
			}
		}
    	bgImg = ImageUtil.resizeImage(width, height, bgImg);
		return bgImg;
	}


	private boolean isNull(Object obj) {
		return obj == null;
	}

    /**
     * 	获取转赠会员卡图
     *	@return	图片绝对路径
     */
    public String createCardGiveAwayImage(MemberCardRecord card) {
    	logger().info("获取转赠会员卡图");
    	final short type = 45;
    	//	背景
    	final String bgPath = "image/wxapp/card_give_away.png";
    	BufferedImage giveWayBgImg = getBgImg(500,400,bgPath);

    	//	头像
    	BufferedImage cardVatar = getCardVatar();
    	if(cardVatar == null){
    	    cardVatar = getBgImg(60,60,"image/wxapp/default_user_avatar.png");
        }
    	//	会员卡名称
    	ImageUtil.addTwoImage(giveWayBgImg, cardVatar, 30, 20);
    	ImageUtil.addFont(giveWayBgImg, card.getCardName(), ImageUtil.sourceHanSansCn(Font.BOLD, 30), 170, 90,
				Color.WHITE);

    	String relativePath =getQrCodeImgRelativePath(type)+format("T%sP%s_%s.jpg", type, card.getId(), DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL_NO_UNDERLINE));
    	byte[] imgBytes = ImageUtil.changeImageToByteArr(giveWayBgImg);
    	try {
			this.imageService.getUpYunClient().writeFile(relativePath, imgBytes, true);
		} catch (Exception e) {
			logger().error("转赠卡上传upyun失败");
		}

    	return imageService.imageUrl(relativePath);
    }

    /**
     * 根据sceneId获取分享二维码内的参数信息
     * @param sceneId
     * @return
     */
    public String getQrCodeParamInfoBySceneId(Integer sceneId) {
       return db().select(WXP_UNLIMIT_SCENE.SCENE_VALUE)
            .from(WXP_UNLIMIT_SCENE)
            .where(WXP_UNLIMIT_SCENE.SCENE_ID.eq(sceneId))
            .fetchAny(WXP_UNLIMIT_SCENE.SCENE_VALUE);
    }

    /**
     * 添加scene新项
     * @param sceneValue
     * @return
     */
    public Integer addScene(String sceneValue){
        WxpUnlimitSceneRecord sceneRecord = db().newRecord(WXP_UNLIMIT_SCENE);
        sceneRecord.setSceneValue(sceneValue);
        sceneRecord.insert();
        return sceneRecord.getSceneId();
    }

}
