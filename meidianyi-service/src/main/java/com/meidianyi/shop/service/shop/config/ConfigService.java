package com.meidianyi.shop.service.shop.config;

import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.DomainConfig;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.db.shop.tables.records.UserRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.auth.ShopSelectInnerResp;
import com.meidianyi.shop.service.pojo.shop.config.ShopStyleConfig;
import com.meidianyi.shop.service.pojo.wxapp.config.WxAppConfigVo;
import com.meidianyi.shop.service.pojo.wxapp.config.WxAppConfigVo.Setting;
import com.meidianyi.shop.service.pojo.wxapp.config.WxAppConfigVo.ShowPoster;
import com.meidianyi.shop.service.pojo.wxapp.login.WxAppSessionUser;
import com.meidianyi.shop.service.shop.config.message.MessageConfigService;
import jodd.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author 新国
 *
 */
@Service

public class ConfigService extends ShopBaseService {
	private static final byte ONE = 1;
	@Autowired
	public BottomNavigatorConfigService bottomCfg;
    @Autowired
    public SearchConfigService searchCfg;
    @Autowired
    public ShopStyleConfigService shopStyleCfg;
    @Autowired
    public CommentConfigService commentConfigService;
    @Autowired
    public PledgeConfigService pledgeCfg;
    @Autowired
    public ShopCommonConfigService shopCommonConfigService;
    @Autowired
    public ShopCommonConfigCacheService shopCommonConfigCacheService;
    @Autowired
    public ShopReturnConfigService returnConfigService;
    @Autowired
    public UserCenterConfigService userCenterConfigService;
    @Autowired
    public StoreConfigService storeConfigService;
    @Autowired
    public DistributionConfigService distributionCfg;
    @Autowired
    public ShopMsgTemplateConfigService shopMsgTemplateService;
	@Autowired
	public BargainConfigService bargainCfg;
	@Autowired
	public GoodsBrandConfigService goodsBrandConfigService;
	@Autowired
	public GoodsRecommendSortConfigService recommendSortConfigService;
	@Autowired
	public DeliverTemplateConfigService deliverTemplateConfigService;
	@Autowired
	public FirstSpecialConfigService firstSpecialConfigService;
	@Autowired
	public InsteadPayConfigService insteadPayConfigService;
	@Autowired
	public CollectGiftConfigService collectGiftConfigService;
    @Autowired
    public MessageConfigService messageConfigService;
    @Autowired
    public GiftConfigService giftConfigService;
    @Autowired
    public OrderExportConfigService orderExportCfg;
    @Autowired
    public DomainConfig domainConfig;
    @Autowired
    public GoodsConfigService goodsCfg;
    @Autowired
    public SuspendWindowConfigService suspendWindowConfigService;
    @Autowired
    public RebateConfigService rebateConfigService;

    /**
     * 得到店铺配置
     *
     * @return
     */
    public WxAppConfigVo getAppConfig(WxAppSessionUser user) {
        ShopRecord shop = saas.shop.getShopById(getShopId());
        Byte showLogo = shopCommonConfigCacheService.getShowLogo();
        WxAppConfigVo config = new WxAppConfigVo();
        Setting setting = WxAppConfigVo.Setting.builder().shopFlag(shop.getShopFlag())
            .shopStyle(convertShopStyle(shopCommonConfigCacheService.getShopStyleConfig()))
            .hideBottom(shop.getHidBottom()).build();
        config.setBottomNavigateMenuList(shopCommonConfigCacheService.getBottomNavigatorConfig());
        config.setShowLogo(showLogo);
        if (StringUtil.isNotEmpty(shop.getLogo())) {
            config.setLogo(domainConfig.imageUrl(shop.getLogo()));
        }
        config.setLogoLink(shopCommonConfigCacheService.getLogoLink());
        config.setSetting(setting);
        config.setStatus(getStatus(user != null ? user.getUserId() : null));
        //添加用户角色信息、医师id、药师id
        assert user != null;
        config.setUserType(user.getUserType());
        config.setDoctorId(user.getDoctorId());
        config.setPharmacistId(user.getPharmacistId());
        config.setStoreAccountId(user.getStoreAccountId());
        ShowPoster showPoster = new ShowPoster();
        // TODO: 取ShowPoster数据
        config.setShowPoster(showPoster);
        return config;
    }

	/**
	 * 获取店铺风格 rgb/rgba转16进制
	 *
	 * @return
	 */
	public String[] convertShopStyle(ShopStyleConfig config) {
		if (config == null || config.getShopStyleValue() == null) {
			return new String[] { "", "" };
		}
		String style = config.getShopStyleValue().trim();
        String rgb = "rgb";
        if (!style.contains(rgb)) {
			return style.split(",");
		} else {
            String rgba = "rgba";
            if (style.startsWith(rgba)){
                String pattern = "rgba\\(\\s*(\\d+),\\s*(\\d+),\\s*(\\d+),\\s*(\\d+)\\),\\s*rgba\\(\\s*(\\d+),\\s*(\\d+),\\s*(\\d+),\\s*(\\d+)\\)";
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(style);
                if (m.find()) {
                    Color color1 =new Color(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)),Integer.parseInt(m.group(4)));
                    Color color2 =new Color(Integer.parseInt(m.group(5)), Integer.parseInt(m.group(6)), Integer.parseInt(m.group(7)),Integer.parseInt(m.group(8)));
                    String hexColor1 = toHexFromColor(color1);
                    String hexColor2 = toHexFromColor(color2);
                    return new String[]{hexColor1,hexColor2};
                }
            } else {
                String pattern = "rgb\\(\\s*(\\d+),\\s*(\\d+),\\s*(\\d+)\\),\\s*rgb\\(\\s*(\\d+),\\s*(\\d+),\\s*(\\d+)\\)";
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(style);
                if (m.find()) {
                    Color color1 =new Color(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)));
                    Color color2 =new Color(Integer.parseInt(m.group(4)), Integer.parseInt(m.group(5)), Integer.parseInt(m.group(6)));
                    String hexColor1 = toHexFromColor(color1);
                    String hexColor2 = toHexFromColor(color2);
                    return new String[]{hexColor1,hexColor2};
                }
            }
        }
		return new String[] {};
	}
	/**
	 * Color对象转换成字符串
	 * @param color Color对象
	 * @return 16进制颜色字符串
	 * */
	private static String toHexFromColor(Color color){
		String r,g,b;
		StringBuilder su = new StringBuilder();
		r = Integer.toHexString(color.getRed());
		g = Integer.toHexString(color.getGreen());
		b = Integer.toHexString(color.getBlue());
		r = r.length() == 1 ? "0" + r : r;
		g = g.length() ==1 ? "0" +g : g;
		b = b.length() == 1 ? "0" + b : b;
		r = r.toUpperCase();
		g = g.toUpperCase();
		b = b.toUpperCase();
		su.append("#");
		su.append(r);
		su.append(g);
		su.append(b);
		return su.toString();
	}

	/**
	 * 得到语言包
	 *
	 * @return
	 */
	public String getLocalePack(String language) {
		return Util.loadResource("static/i18n/wxapp/" + language + ".json");
	}


	private byte getStatus(Integer userId) {
		byte status=0;
		ShopSelectInnerResp shopInfo = saas.shop.getShopInfo(getShopId());
		String expireTimeStatus = shopInfo.getExpireTimeStatus();
        String expiredStatus = "1";
        if(expireTimeStatus.equals(expiredStatus)) {
			//已过期
			status=1;
		}
		Byte isEnabled = shopInfo.getIsEnabled();
		if(Objects.equals(isEnabled, ONE)) {
			//已禁止
			status=2;
		}
		Byte businessState = shopInfo.getBusinessState();
		if(!Objects.equals(businessState, ONE)) {
			//未营业
			status=3;
		}
		if(userId!=null) {
			UserRecord user = saas.getShopApp(getShopId()).user.getUserByUserId(userId);
			Byte delFlag = user.getDelFlag();
			if(Objects.equals(delFlag, ONE)) {
				//用户被禁止登陆
				status=4;
			}
		}
		return status;

	}
}
