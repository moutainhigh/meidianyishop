package com.meidianyi.shop.service.shop.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.meidianyi.shop.common.foundation.util.RegexUtil;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.DomainConfig;
import com.meidianyi.shop.service.foundation.jedis.JedisKeyConstant;
import com.meidianyi.shop.service.foundation.jedis.JedisManager;
import com.meidianyi.shop.service.pojo.shop.config.BottomNavigatorConfig;
import com.meidianyi.shop.service.pojo.shop.config.ShopShareConfig;
import com.meidianyi.shop.service.pojo.shop.config.ShopStyleConfig;
import jodd.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.meidianyi.shop.db.shop.tables.ShopCfg.SHOP_CFG;


/**
 * @author luguangyao
 */
@Service
public class ShopCommonConfigCacheService extends BaseShopConfigService {

    @Autowired
    private JedisManager jedisManager;
    @Autowired
    private DomainConfig domainConfig;
    @Autowired
    ShopShareConfig defaultShopShareConfig;

    public static final Integer MAX_TIME_OUT = 60 * 60 * 24;

    /**
     * 返回分词配置状态(cache)
     *
     * @return true:开启｜false:关闭
     */
    public boolean enabledAnalyzerStatus() {
        String key = JedisKeyConstant.CONFIG_ANALYZER_STATUS + getShopId();
        String result = jedisManager.getValueAndSave(
            key,
            MAX_TIME_OUT,
            () -> db().select()
                .from(SHOP_CFG)
                .where(SHOP_CFG.K.eq(ShopCommonConfigService.K_ACCURATE_SEARCH))
                .fetchAny(SHOP_CFG.V));
        String enabledAnalyzer = "1";
        return result != null && result.equals(enabledAnalyzer);
    }

    /**
     * 获取店铺风格配置
     *
     * @return
     */
    public ShopStyleConfig getShopStyleConfig() {
        String key = JedisKeyConstant.CONFIG_SHOP_STYLE + getShopId();
        String result = jedisManager.getValueAndSave(
            key,
            MAX_TIME_OUT,
            () -> db().select()
                .from(SHOP_CFG)
                .where(SHOP_CFG.K.eq(ShopCommonConfigService.K_SHOP_STYLE))
                .fetchAny(SHOP_CFG.V));
        return Util.parseJson(result, ShopStyleConfig.class);
    }

    /**
     * 是否显示Logo配置
     *
     * @return
     */
    public Byte getShowLogo() {
        String key = JedisKeyConstant.CONFIG_SHOW_LOGO + getShopId();
        String result = jedisManager.getValueAndSave(
            key,
            MAX_TIME_OUT,
            () -> db().select()
                .from(SHOP_CFG)
                .where(SHOP_CFG.K.eq(ShopCommonConfigService.K_SHOW_LOGO))
                .fetchAny(SHOP_CFG.V));
        return Util.convert(result, Byte.class, (byte) 0);
    }

    /**
     * 店铺logo点击时的跳转链接
     *
     * @return
     */
    public String getLogoLink() {
        String key = JedisKeyConstant.CONFIG_LOGO_LINK + getShopId();
        String result = jedisManager.getValueAndSave(
            key,
            MAX_TIME_OUT,
            () -> db().select()
                .from(SHOP_CFG)
                .where(SHOP_CFG.K.eq(ShopCommonConfigService.K_LOGO_LINK))
                .fetchAny(SHOP_CFG.V));
        return result == null ? "" : result;
    }

    /**
     * 获取店铺分享配置
     *
     * @return
     */
    public ShopShareConfig getShareConfig() {
        String key = JedisKeyConstant.CONFIG_SHARE_CONFIG + getShopId();
        String result = jedisManager.getValueAndSave(
            key,
            MAX_TIME_OUT,
            () -> db().select()
                .from(SHOP_CFG)
                .where(SHOP_CFG.K.eq(ShopCommonConfigService.K_SHARE_CONFIG))
                .fetchAny(SHOP_CFG.V));
        return result == null ? defaultShopShareConfig : Util.parseJson(result, ShopShareConfig.class);
    }

    /**
     * 获取底部导航配置
     *
     * @return
     */
    public List<BottomNavigatorConfig> getBottomNavigatorConfig() {
        String key = JedisKeyConstant.CONFIG_BOTTOM + getShopId();
        String result = jedisManager.getValueAndSave(
            key,
            MAX_TIME_OUT,
            () -> db().select()
                .from(SHOP_CFG)
                .where(SHOP_CFG.K.eq(BottomNavigatorConfigService.K_BOTTOM))
                .fetchAny(SHOP_CFG.V));
        List<BottomNavigatorConfig> res = Util.parseJson(result, new TypeReference<List<BottomNavigatorConfig>>() {
        });
        for (BottomNavigatorConfig c : res) {
            if (StringUtil.isNotBlank(c.getNormal())) {
                //兼容一下带域名和不带域名的图片路径
                c.setNormal(domainConfig.imageUrl(RegexUtil.getUri(c.getNormal())));
            }
            if (StringUtil.isNotBlank(c.getHover())) {
                c.setHover(domainConfig.imageUrl(RegexUtil.getUri(c.getHover())));
            }
        }
        return res;
    }
}
