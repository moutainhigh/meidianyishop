package com.meidianyi.shop.service.shop.decoration;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.DomainConfig;
import com.meidianyi.shop.db.main.tables.records.DecorationTemplateRecord;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.db.shop.tables.records.UserRecord;
import com.meidianyi.shop.db.shop.tables.records.XcxCustomerPageRecord;
import com.meidianyi.shop.service.foundation.es.pojo.goods.EsGoodsConstant;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.saas.shop.ShopPojo;
import com.meidianyi.shop.service.pojo.saas.shop.version.VersionConfig;
import com.meidianyi.shop.service.pojo.saas.shop.version.VersionName;
import com.meidianyi.shop.service.pojo.shop.config.ShopShareConfig;
import com.meidianyi.shop.service.pojo.shop.config.SuspendWindowConfig;
import com.meidianyi.shop.service.pojo.shop.config.distribution.DistributionParam;
import com.meidianyi.shop.service.pojo.shop.decoration.module.ModuleBargain;
import com.meidianyi.shop.service.pojo.shop.decoration.module.ModuleCard;
import com.meidianyi.shop.service.pojo.shop.decoration.module.ModuleCaseHistory;
import com.meidianyi.shop.service.pojo.shop.decoration.module.ModuleConstant;
import com.meidianyi.shop.service.pojo.shop.decoration.module.ModuleCoupon;
import com.meidianyi.shop.service.pojo.shop.decoration.module.ModuleGoods;
import com.meidianyi.shop.service.pojo.shop.decoration.module.ModuleGoodsGroup;
import com.meidianyi.shop.service.pojo.shop.decoration.module.ModuleGroupDraw;
import com.meidianyi.shop.service.pojo.shop.decoration.module.ModuleGroupIntegration;
import com.meidianyi.shop.service.pojo.shop.decoration.module.ModuleHotArea;
import com.meidianyi.shop.service.pojo.shop.decoration.module.ModuleImageAdver;
import com.meidianyi.shop.service.pojo.shop.decoration.module.ModuleImageGuide;
import com.meidianyi.shop.service.pojo.shop.decoration.module.ModuleIntegral;
import com.meidianyi.shop.service.pojo.shop.decoration.module.ModuleMagicCube;
import com.meidianyi.shop.service.pojo.shop.decoration.module.ModuleMap;
import com.meidianyi.shop.service.pojo.shop.decoration.module.ModulePrescription;
import com.meidianyi.shop.service.pojo.shop.decoration.module.ModuleScrollImage;
import com.meidianyi.shop.service.pojo.shop.decoration.module.ModuleSeckill;
import com.meidianyi.shop.service.pojo.shop.decoration.module.ModuleShop;
import com.meidianyi.shop.service.pojo.shop.decoration.module.ModuleTextImage;
import com.meidianyi.shop.service.pojo.shop.decoration.module.ModuleTitle;
import com.meidianyi.shop.service.pojo.shop.decoration.module.ModuleVideo;
import com.meidianyi.shop.service.pojo.shop.goods.GoodsConstant;
import com.meidianyi.shop.service.pojo.shop.market.collect.CollectGiftParam;
import com.meidianyi.shop.service.pojo.shop.medicalhistory.MedicalHistoryPageInfoParam;
import com.meidianyi.shop.service.pojo.shop.medicalhistory.MedicalHistoryPageInfoVo;
import com.meidianyi.shop.service.pojo.shop.patient.UserPatientParam;
import com.meidianyi.shop.service.pojo.shop.prescription.PrescriptionListVo;
import com.meidianyi.shop.service.pojo.shop.prescription.PrescriptionPatientListParam;
import com.meidianyi.shop.service.pojo.wxapp.config.ShareConfig;
import com.meidianyi.shop.service.pojo.wxapp.coupon.CouponPageDecorationVo;
import com.meidianyi.shop.service.pojo.wxapp.coupon.ShopCollectInfo;
import com.meidianyi.shop.service.pojo.wxapp.decorate.PageCfgVo;
import com.meidianyi.shop.service.pojo.wxapp.decorate.WxAppPageModuleParam;
import com.meidianyi.shop.service.pojo.wxapp.decorate.WxAppPageParam;
import com.meidianyi.shop.service.pojo.wxapp.decorate.WxAppPageVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsListMpBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.list.GoodsGroupListMpParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.list.GoodsListMpParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.list.GoodsListMpVo;
import com.meidianyi.shop.service.pojo.wxapp.member.card.MemberCardPageDecorationVo;
import com.meidianyi.shop.service.shop.config.ConfigService;
import com.meidianyi.shop.service.shop.config.DistributionConfigService;
import com.meidianyi.shop.service.shop.config.SuspendWindowConfigService;
import com.meidianyi.shop.service.shop.goods.mp.GoodsMpService;
import com.meidianyi.shop.service.shop.medicine.MedicalHistoryService;
import com.meidianyi.shop.service.shop.member.MemberService;
import com.meidianyi.shop.service.shop.patient.PatientService;
import com.meidianyi.shop.service.shop.prescription.PrescriptionService;
import com.meidianyi.shop.service.shop.user.user.UserService;
import jodd.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.tables.XcxCustomerPage.XCX_CUSTOMER_PAGE;

/**
 * @author lixinguo
 */

@Service
public class MpDecorationService extends ShopBaseService {

    public static final String PAGE_ID = "page_id";
    public static final String PAGE = "page";
    public static final String C_ = "c_";
    public static final String PAGE_CFG = "page_cfg";
    @Autowired
    protected ConfigService config;
    @Autowired
    protected UserService user;
    @Autowired
    protected MemberService member;
    @Autowired
    protected GoodsMpService goodsMpService;
    @Autowired
    private DomainConfig domainConfig;
    @Autowired
    private SuspendWindowConfigService suspendWindowConfigService;
    @Autowired
    private PrescriptionService prescriptionService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private MedicalHistoryService medicalHistoryService;

    public int setPageCatId(Integer pageId, Integer catId) {
        return db().update(XCX_CUSTOMER_PAGE)
            .set(XCX_CUSTOMER_PAGE.CAT_ID, catId)
            .where(XCX_CUSTOMER_PAGE.PAGE_ID.eq((pageId)))
            .execute();
    }

    public int removeRow(Integer pageId) {
        return db().deleteFrom(XCX_CUSTOMER_PAGE)
            .where(XCX_CUSTOMER_PAGE.PAGE_ID.eq((pageId)))
            .execute();
    }

    /**
     * 得到一个空页面
     *
     * @return
     */
    public XcxCustomerPageRecord getEmptyPage() {
        XcxCustomerPageRecord record = db().newRecord(XCX_CUSTOMER_PAGE);
        record.setPageContent("{}");
        return record;
    }

    /**
     * 过滤页面内容
     *
     * @param pageContent
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, Map<String, Object>> filterPageContent(String pageContent) {
        if (pageContent == null) {
            return null;
        }
        Map<String, Map<String, Object>> result = Util.parseJson(pageContent, Map.class);
        for (Map.Entry<String, Map<String, Object>> entry : result.entrySet()) {
            Map<String, Object> v = entry.getValue();
            filterActivityModules(v);
            filterImageModules(v);
        }
        return result;
    }

    protected void filterActivityModules(Map<String, Object> v) {

    }

    protected void filterImageModules(Map<String, Object> v) {

    }

    /**
     * 得到店铺版本模块
     *
     * @return
     */
    public Map<String, Integer> getVersionModules() {
        VersionConfig config = saas().shop.version.mergeVersion(this.getShopId());
        List<String> sub2 = config.mainConfig.sub2;
        Map<String, Integer> moduleMap = new HashMap<String, Integer>(8);
        String[] modules = {"m_member_card", "m_voucher", "m_bargain", "m_video",
            "m_integral_goods", "m_seckill_goods", "m_group_draw", "m_pin_integration"};
        for (String module : modules) {
            moduleMap.put(module, sub2.contains(module) ? 1 : 0);
        }
        return moduleMap;
    }

    /**
     * 克隆系统模板
     *
     * @param templateId
     * @return
     */
    public XcxCustomerPageRecord cloneTemplate(Integer templateId) {
        DecorationTemplateRecord record = saas.shop.decoration.getRow(templateId);
        XcxCustomerPageRecord page = db().newRecord(XCX_CUSTOMER_PAGE);
        page.setPageContent(record.getPageContent());
        page.insert();
        return page;
    }

    /**
     * 获取分类下页面个数
     *
     * @param catId
     * @return
     */
    public int getPageCount(Integer catId) {
        return db().fetchCount(XCX_CUSTOMER_PAGE, XCX_CUSTOMER_PAGE.CAT_ID.eq(catId));
    }

    /**
     * 获取首页
     *
     * @return
     */
    public XcxCustomerPageRecord getIndex() {
        return db().fetchAny(XCX_CUSTOMER_PAGE, XCX_CUSTOMER_PAGE.PAGE_TYPE.eq((byte) 1));
    }

    /**
     * 获取装修页面
     *
     * @param pageId
     * @return
     */
    public XcxCustomerPageRecord getPageById(Integer pageId) {
        return db().fetchAny(XCX_CUSTOMER_PAGE, XCX_CUSTOMER_PAGE.PAGE_ID.eq((pageId)));
    }

    /**
     * 得到前端装修页面信息
     *
     * @param param
     * @return
     */
    public WxAppPageVo getPageInfo(WxAppPageParam param) {
        XcxCustomerPageRecord record = null;
        Integer pageId = param.getPageId();
        String pageContent;
        if (pageId == null || pageId == 0) {
            if(StringUtil.isNotEmpty(param.getScene())){
                //scene的格式有：
                // page_id=1,url编码，表示装修预览，取未发布的装修内容
                // page=1,url编码，表示页面分享，取已发布的装修内容
                String scene = null;
                try {
                    scene = URLDecoder.decode(param.getScene(),"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    logger().error("URLDecoder.decode error",e);
                }
                if(StringUtil.isNotEmpty(scene)){
                    String[] sceneParam = scene.split("=",2);
                    pageId =  Integer.valueOf(sceneParam[1]);
                    record = getPageById(pageId);
                    if(PAGE_ID.equals(sceneParam[0])){
                        //页面预览
                        pageContent = record.getPageContent();
                    }else if(PAGE.equals(sceneParam[0])){
                        //页面分享
                        pageContent = record.getPagePublishContent();
                    }else{
                        logger().error("未知scene");
                        pageContent = null;
                    }

                }else {
                    return null;
                }
            }else{
                //首页
                record = this.getIndex();
                pageId = record.getPageId();
                pageContent = record.getPagePublishContent();
            }

        } else {
            record = this.getPageById(pageId);
            pageContent = record.getPagePublishContent();
        }

        UserRecord userRecord = user.getUserByUserId(param.getUserId());
        Integer patientId = patientService.defaultPatientId(param.getUserId());
        Map<String, Object> pageInfo = convertPageContent(pageContent, userRecord, patientId);
        WxAppPageVo page = new WxAppPageVo();
        page.setPageInfo(pageInfo);
        page.setIsFirstPage(record.getPageType());
        page.setPageId(pageId);
        page.setScene(param.getScene());
        page.setPageName(record.getPageName());
        page.setPageType(record.getPageType());
//		this.setCollectInfo(page.getCollectInfo(), shop, userRecord);
        this.setShareConfig(page.getShareInfo());
        return page;
    }


    /**
     * 设置用户收藏有礼信息
     *
     * @param collectInfo
     * @param shop
     */
    protected void setCollectInfo(ShopCollectInfo collectInfo, ShopRecord shop, UserRecord userRecord) {
        CollectGiftParam collect = config.collectGiftConfigService.collectGiftConfig();
        collectInfo.setShopName(shop.getShopName());
        collectInfo.setLookCollectTime(userRecord.getLookCollectTime());
        collectInfo.setGetCollectGift(userRecord.getGetCollectGift());
        FieldsUtil.assignNotNull(collect, collectInfo);
    }

    /**
     * 设置店铺分享配置
     *
     * @param shareConfig
     */
    protected void setShareConfig(ShareConfig shareConfig) {
        ShopShareConfig shopShareConfig = config.shopCommonConfigCacheService.getShareConfig();
        if (shopShareConfig != null) {
            FieldsUtil.assignNotNull(shopShareConfig, shareConfig);
        }
        shareConfig.setShareTitle(shareConfig.getShareDoc());
    }

    /**
     * 转换装修页面内容
     *
     * @param pageContent
     * @return
     */
    protected Map<String, Object> convertPageContent(String pageContent, UserRecord user, Integer patientId) {
        return convertPageContent(pageContent, null, user, patientId);
    }

    /**
     * 转换装修页面内容
     *
     * @param pageContent
     * @param keyIdx
     * @param user
     * @return
     */
    protected Map<String, Object> convertPageContent(String pageContent, String keyIdx, UserRecord user, Integer patientId) {
        pageContent = StringUtils.isBlank(pageContent) ? "{}" : pageContent;
        Map<String, Object> result = new LinkedHashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            JsonNode root = objectMapper.readTree(pageContent);
            Iterator<Entry<String, JsonNode>> elements = root.fields();

            while (elements.hasNext()) {
                Entry<String, JsonNode> node = elements.next();
                String key = node.getKey();
                if (keyIdx == null || key.equals(keyIdx)) {
                    Object element = this.convertModule(objectMapper, node, user, patientId);
                    if(element!=null) {
                    	result.put(key, element);
                    }
                }
            }
        } catch (Exception e) {
            logger().error("装修转换错误:",e);
        }
        return result;
    }

    /**
     * 转换模块，一些模块需要要转换，得到当前上数据
     *
     * @param objectMapper
     * @param node
     * @return
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public Object convertModule(ObjectMapper objectMapper, Entry<String, JsonNode> node, UserRecord user, Integer patientId)
        throws JsonParseException, JsonMappingException, IOException {
        if (node.getKey().startsWith(C_)) {
            String moduleName = node.getValue().get("module_name").asText();
            switch (moduleName) {
                case ModuleConstant.M_GOODS_GROUP:
                    return this.convertGoodsGroupForIndex(objectMapper, node, user);
                case ModuleConstant.M_GOODS:
                    ModuleGoods moduleGoods = this.convertGoodsForIndex(objectMapper, node, user);
                    if (GoodsConstant.AUTO_RECOMMEND.equals(moduleGoods.getRecommendType()) && GoodsConstant.AUTO_RECOMMEND_PRESCRIPTION.equals(moduleGoods.getAutoRecommendType()) && GoodsConstant.ZERO.equals(patientId)) {
                        return  null;
                    }
                    return moduleGoods;
                case ModuleConstant.M_COUPON:
					if (isNoAuth(VersionName.SUB_2_M_VOUCHER)) { return null; }
                    return this.convertCouponForIndex(objectMapper, node, user);
                case ModuleConstant.M_CARD:
					if (isNoAuth(VersionName.SUB_2_M_MEMBER_CARD)) { return null; }
					return this.convertCardForIndex(objectMapper, node, user);
                case ModuleConstant.M_BARGAIN:
					if (isNoAuth(VersionName.SUB_2_M_MEMBER_CARD)) { return null; }
                    return this.convertBargainForIndex(objectMapper, node, user);
                case ModuleConstant.M_SECKILL:
					if (isNoAuth(VersionName.SUB_2_M_SECKILL_GOODS)) { return null; }
                    return this.convertSeckillForIndex(objectMapper, node, user);
                case ModuleConstant.M_IMAGE_ADVER:
                    return this.convertImageAdverForIndex(objectMapper, node, user);
                case ModuleConstant.M_PIN_INTEGRATION:
					if (isNoAuth(VersionName.SUB_2_M_PIN_INTEGRATION)) { return null; }
                    return this.convertGroupIntegrationForIndex(objectMapper, node, user);
                case ModuleConstant.M_GROUP_DRAW:
					if (isNoAuth(VersionName.SUB_2_M_GROUP_DRAW)) {
						return null;
					}
                    return this.convertGroupDrawForIndex(objectMapper, node, user);
                case ModuleConstant.M_SCROLL_IMAGE:
                    return this.convertScrollImageForIndex(objectMapper, node, user);
                case ModuleConstant.M_VIDEO:
					if (isNoAuth(VersionName.SUB_2_M_VIDEO)) { return null; }
                    return this.convertVideoForIndex(objectMapper, node, user);
                case ModuleConstant.M_IMAGE_GUIDE:
                    return this.convertImageGuideForIndex(objectMapper, node, user);
                case ModuleConstant.M_MAGIC_CUBE:
                    return this.convertMagicCubeForIndex(objectMapper, node, user);
                case ModuleConstant.M_HOT_AREA:
                    return this.convertHotAreaForIndex(objectMapper, node, user);
                case ModuleConstant.M_TEXT_IMAGE:
                    return this.convertTextImageForIndex(objectMapper, node, user);
                case ModuleConstant.M_TITLE:
                    return this.convertTitleForIndex(objectMapper, node, user);
                case ModuleConstant.M_MAP:
                    return this.convertMapForIndex(objectMapper, node, user);
                case ModuleConstant.M_SHOP:
                    return this.convertShopBgForIndex(objectMapper, node, user);
                case ModuleConstant.M_INTEGRAL:
					if (isNoAuth(VersionName.SUB_2_M_INTEGRAL_GOODS)) { return null; }
                    return this.convertIntegralForIndex(objectMapper, node, user);
                case ModuleConstant.M_PRESCRIPTION:
                    if (GoodsConstant.ZERO.equals(patientId)) { return null; }
                    return this.convertPrescriptionForIndex(objectMapper, node, user);
                case ModuleConstant.M_CASE_HISTORY:
                    return this.convertCaseHistoryForIndex(objectMapper, node, user);
                /**
                 * TODO: 添加其他模块，一些不需要转换的模块，可以走最后默认的转换。
                 */
                default:
            }
        }
        if(PAGE_CFG.equals(node.getKey())){
            return this.convertPageCfgIndex(objectMapper, node, user);
        }
        return objectMapper.readValue(node.getValue().toString(), Object.class);
    }

    /**
     * 没有权限是true
     * @return
     */
	private boolean isNoAuth(String modeName) {
		String[] verPurview = saas.shop.version.verifyVerPurview(getShopId(), modeName);
        String strTrue = "true";
        if(!verPurview[0].equals(strTrue)) {
			logger().info("店铺：{}，的模块：{}没有权限",getShopId(),modeName);
			return true;
		}
		return false;
	}


    /**
     * 转换商品分组模块
     *
     * @param objectMapper
     * @param node
     * @param user
     * @return
     * @throws IOException
     */
    public ModuleGoodsGroup convertGoodsGroupForIndex(ObjectMapper objectMapper, Entry<String, JsonNode> node, UserRecord user) throws IOException {
        ModuleGoodsGroup element = objectMapper.readValue(node.getValue().toString(), ModuleGoodsGroup.class);
        element.setNeedRequest(true);
        return element;
    }

    /**
     * @param objectMapper
     * @param node
     * @param user
     * @return
     * @throws IOException
     */
    private ModuleGoods convertGoodsForIndex(ObjectMapper objectMapper, Entry<String, JsonNode> node, UserRecord user) throws IOException {
        ModuleGoods moduleGoods = objectMapper.readValue(node.getValue().toString(), ModuleGoods.class);
        moduleGoods.setNeedRequest(true);
        return moduleGoods;
    }

    /**
     * 优惠券需要setNeedRequest
     *
     * @param objectMapper
     * @param node
     * @param user
     * @return
     * @throws IOException
     */
    private ModuleCoupon convertCouponForIndex(ObjectMapper objectMapper, Entry<String, JsonNode> node, UserRecord user) throws IOException {
        ModuleCoupon moduleCoupon = objectMapper.readValue(node.getValue().toString(), ModuleCoupon.class);
        moduleCoupon.setNeedRequest(true);
        return moduleCoupon;
    }

    /**
     * 会员卡需要setNeedRequest
     *
     * @param objectMapper
     * @param node
     * @param user
     * @return
     * @throws IOException
     */
    private ModuleCard convertCardForIndex(ObjectMapper objectMapper, Entry<String, JsonNode> node, UserRecord user) throws IOException {
        ModuleCard moduleCard = objectMapper.readValue(node.getValue().toString(), ModuleCard.class);
        moduleCard.setNeedRequest(true);
        return moduleCard;
    }

    /**
     * 砍价需要setNeedRequest
     *
     * @param objectMapper
     * @param node
     * @param user
     * @return
     * @throws IOException
     */
    private ModuleBargain convertBargainForIndex(ObjectMapper objectMapper, Entry<String, JsonNode> node, UserRecord user) throws IOException {
        ModuleBargain moduleBargain = objectMapper.readValue(node.getValue().toString(), ModuleBargain.class);
        moduleBargain.setNeedRequest(true);
        return moduleBargain;
    }

    /**
     * 秒杀需要setNeedRequest
     *
     * @param objectMapper
     * @param node
     * @param user
     * @return
     * @throws IOException
     */
    private ModuleSeckill convertSeckillForIndex(ObjectMapper objectMapper, Entry<String, JsonNode> node, UserRecord user) throws IOException {
        ModuleSeckill moduleSecKill = objectMapper.readValue(node.getValue().toString(), ModuleSeckill.class);
        moduleSecKill.setNeedRequest(true);
        return moduleSecKill;
    }

    /**
     * 图片广告模块处理
     *
     * @param objectMapper
     * @param node
     * @param user
     * @return
     * @throws IOException
     */
    private ModuleImageAdver convertImageAdverForIndex(ObjectMapper objectMapper, Entry<String, JsonNode> node, UserRecord user) throws IOException {
        ModuleImageAdver moduleImageAdver = objectMapper.readValue(node.getValue().toString(), ModuleImageAdver.class);
        boolean isNewUser = saas.getShopApp(getShopId()).readOrder.orderInfo.isNewUser(user.getUserId());
        Iterator<ModuleImageAdver.ImageAdItem> it = moduleImageAdver.getImageList().iterator();
        while (it.hasNext()){
            ModuleImageAdver.ImageAdItem img = it.next();
            if(img.getCanShow() == 1 && !isNewUser){
               it.remove();
            }
            img.setImage(domainConfig.imageUrl(img.getImage()));
        }
        return moduleImageAdver;
    }

    /**
     * 轮播图模块处理
     *
     * @param objectMapper
     * @param node
     * @param user
     * @return
     * @throws IOException
     */
    private ModuleScrollImage convertScrollImageForIndex(ObjectMapper objectMapper, Entry<String, JsonNode> node, UserRecord user) throws IOException {
        ModuleScrollImage moduleScrollImage = objectMapper.readValue(node.getValue().toString(), ModuleScrollImage.class);
        boolean isNewUser = saas.getShopApp(getShopId()).readOrder.orderInfo.isNewUser(user.getUserId());
        Iterator<ModuleScrollImage.ImageItem> it = moduleScrollImage.getImgItems().iterator();
        while (it.hasNext()){
            ModuleScrollImage.ImageItem img = it.next();
            if(img.getCanShow() == 1 && !isNewUser){
                it.remove();
            }
            img.setImageUrl(domainConfig.imageUrl(img.getImageUrl()));
        }
        return moduleScrollImage;
    }

    /**
     * 视频模块处理
     * @param objectMapper
     * @param node
     * @param user
     * @return
     * @throws IOException
     */
    private ModuleVideo convertVideoForIndex(ObjectMapper objectMapper, Entry<String, JsonNode> node, UserRecord user) throws IOException {
        ModuleVideo moduleVideo = objectMapper.readValue(node.getValue().toString(), ModuleVideo.class);
        moduleVideo.setVideoUrl(domainConfig.videoUrl(moduleVideo.getVideoUrl()));
        if(StringUtil.isNotEmpty(moduleVideo.getVideoImg())){
            moduleVideo.setVideoImg(domainConfig.videoUrl(moduleVideo.getVideoImg()));
        }
        if(StringUtil.isNotEmpty(moduleVideo.getImgUrl())){
            moduleVideo.setImgUrl(domainConfig.imageUrl(moduleVideo.getImgUrl()));
        }
        return moduleVideo;
    }

    /**
     * 图片导航模块处理
     * @param objectMapper
     * @param node
     * @param user
     * @return
     * @throws IOException
     */
    private ModuleImageGuide convertImageGuideForIndex(ObjectMapper objectMapper, Entry<String, JsonNode> node, UserRecord user) throws IOException {
        ModuleImageGuide moduleImageGuide = objectMapper.readValue(node.getValue().toString(), ModuleImageGuide.class);
        moduleImageGuide.getNavGroup().forEach(navItem -> {
            navItem.setNavSrc(domainConfig.imageUrl(navItem.getNavSrc()));
        });
        return moduleImageGuide;
    }

    /**
     * 魔方多图模块处理
     * @param objectMapper
     * @param node
     * @param user
     * @return
     * @throws IOException
     */
    private ModuleMagicCube convertMagicCubeForIndex(ObjectMapper objectMapper, Entry<String, JsonNode> node, UserRecord user) throws IOException {
        ModuleMagicCube moduleMagicCube = objectMapper.readValue(node.getValue().toString(), ModuleMagicCube.class);
        moduleMagicCube.getData().forEach((s, blockItem) -> {
            blockItem.setImgUrl(domainConfig.imageUrl(blockItem.getImgUrl()));
        });
        return moduleMagicCube;
    }

    /**
     * 热区模块处理
     * @param objectMapper
     * @param node
     * @param user
     * @return
     * @throws IOException
     */
    private ModuleHotArea convertHotAreaForIndex(ObjectMapper objectMapper, Entry<String, JsonNode> node, UserRecord user) throws IOException {
        ModuleHotArea moduleHotArea = objectMapper.readValue(node.getValue().toString(), ModuleHotArea.class);
        moduleHotArea.getData().setBgImgUrl(domainConfig.imageUrl(moduleHotArea.getData().getBgImgUrl()));
        return moduleHotArea;
    }

    /**
     *
     * @param objectMapper
     * @param node
     * @param user
     * @return
     * @throws IOException
     */
    private ModuleTextImage convertTextImageForIndex(ObjectMapper objectMapper, Entry<String, JsonNode> node, UserRecord user) throws IOException {
        ModuleTextImage moduleTextImage = objectMapper.readValue(node.getValue().toString(), ModuleTextImage.class);
        moduleTextImage.setImgUrl(domainConfig.imageUrl(moduleTextImage.getImgUrl()));
        return moduleTextImage;
    }

    /**
     * 标题模块
     * @param objectMapper
     * @param node
     * @param user
     * @return
     * @throws IOException
     */
    private ModuleTitle convertTitleForIndex(ObjectMapper objectMapper, Entry<String, JsonNode> node, UserRecord user) throws IOException {
        ModuleTitle moduleTitle = objectMapper.readValue(node.getValue().toString(), ModuleTitle.class);
        if(StringUtil.isNotEmpty(moduleTitle.getImgUrl())){
            moduleTitle.setImgUrl(domainConfig.imageUrl(moduleTitle.getImgUrl()));
        }
        return moduleTitle;
    }

    /**
     * 地图模块
     * @param objectMapper
     * @param node
     * @param user
     * @return
     * @throws IOException
     */
    private ModuleMap convertMapForIndex(ObjectMapper objectMapper, Entry<String, JsonNode> node, UserRecord user) throws IOException {
        ModuleMap moduleMap = objectMapper.readValue(node.getValue().toString(), ModuleMap.class);
        if(StringUtil.isNotEmpty(moduleMap.getImgPath())){
            moduleMap.setImgPath(domainConfig.imageUrl(moduleMap.getImgPath()));
        }
        return moduleMap;
    }

    /**
     * 瓜分积分
     *
     * @param objectMapper
     * @param node
     * @param user
     * @return
     * @throws IOException
     */
    private ModuleGroupIntegration convertGroupIntegrationForIndex(ObjectMapper objectMapper, Entry<String, JsonNode> node, UserRecord user) throws IOException {
        ModuleGroupIntegration moduleGroupIntegration = objectMapper.readValue(node.getValue().toString(), ModuleGroupIntegration.class);
        moduleGroupIntegration.setNeedRequest(true);
        return moduleGroupIntegration;
    }

    /**
     * 拼团抽奖
     *
     * @param objectMapper
     * @param node
     * @param user
     * @return
     * @throws IOException
     */
    private ModuleGroupDraw convertGroupDrawForIndex(ObjectMapper objectMapper, Entry<String, JsonNode> node, UserRecord user) throws IOException {
        ModuleGroupDraw moduleGroupDraw = objectMapper.readValue(node.getValue().toString(), ModuleGroupDraw.class);
        moduleGroupDraw.setNeedRequest(true);
        return moduleGroupDraw;
    }

    /**
     * page_cfg模块
     *
     * @param objectMapper
     * @param node
     * @param user
     * @return
     * @throws IOException
     */
    private PageCfgVo convertPageCfgIndex(ObjectMapper objectMapper, Entry<String, JsonNode> node, UserRecord user) throws IOException {
        PageCfgVo pageCfg =  objectMapper.readValue(node.getValue().toString(), PageCfgVo.class);
        if(StringUtil.isNotEmpty(pageCfg.getPageBgImage())){
            pageCfg.setPageBgImage(imageUrl(pageCfg.getPageBgImage()));
        }
        if(StringUtil.isNotEmpty(pageCfg.getPictorial().getShareImgPath())){
            pageCfg.getPictorial().setShareImgPath(imageUrl(pageCfg.getPictorial().getShareImgPath()));
        }
        return pageCfg;
    }

    /**
     * 获取指定装修模块数据
     *
     * @param param 请求模块参数 {@link com.meidianyi.shop.service.pojo.wxapp.decorate.WxAppPageModuleParam}
     * @return 对应模块的数据内容
     */
    public Object getPageModuleInfo(WxAppPageModuleParam param) {
        UserRecord userRecord = user.getUserByUserId(param.getUserId());
        XcxCustomerPageRecord pageRecord;
        if (param.getPageId() == null || param.getPageId() == 0) {
            pageRecord = this.getIndex();
        } else {
            pageRecord = this.getPageById(param.getPageId());
        }

        String pageContent = StringUtil.isNotEmpty(param.getScene())  ? pageRecord.getPageContent() : pageRecord.getPagePublishContent();
        DistributionParam distributionCfg = config.distributionCfg.getDistributionCfg();
        // 是否是分销员
        boolean isDistributor = 1 == userRecord.getIsDistributor();

        if (distributionCfg != null && DistributionConfigService.ENABLE_STATUS.equals(distributionCfg.getStatus()) && isDistributor) {
            pageContent = pageContent.replace("pages/distribution/distribution", "pages/distributionspread/distributionspread");
        }
        UserPatientParam userPatientParam = patientService.defaultPatientByUser(param.getUserId());
        Object o = getDetailDecoratePageModule(pageContent, param.getModuleIndex(), userRecord);

        return o;
    }

    /**
     * @param pageContent
     * @param keyIdx
     * @param user
     */
    private Object getDetailDecoratePageModule(String pageContent, String keyIdx, UserRecord user) {
        Integer patientId = patientService.defaultPatientId(user.getUserId());
        pageContent = StringUtils.isBlank(pageContent) ? "{}" : pageContent;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            JsonNode root = objectMapper.readTree(pageContent);
            Iterator<Entry<String, JsonNode>> elements = root.fields();

            while (elements.hasNext()) {
                Entry<String, JsonNode> node = elements.next();
                String key = node.getKey();
                if (key.equals(keyIdx) && key.startsWith("c_")) {
                    String moduleName = node.getValue().get("module_name").asText();
                    switch (moduleName) {
                        case ModuleConstant.M_GOODS:
                            ModuleGoods moduleGoods = this.convertGoodsForModule(objectMapper, node, user.getUserId());
                            if (!(GoodsConstant.AUTO_RECOMMEND.equals(moduleGoods.getRecommendType()) && GoodsConstant.AUTO_RECOMMEND_PRESCRIPTION.equals(moduleGoods.getAutoRecommendType())
                                && GoodsConstant.ZERO.equals(patientId))) {
                                return moduleGoods;
                            }
                        case ModuleConstant.M_GOODS_GROUP:
                            return this.convertGoodsGroupForModule(objectMapper, node, user.getUserId());
                        case ModuleConstant.M_COUPON:
                            return this.convertCouponForModule(objectMapper, node, user);
                        case ModuleConstant.M_CARD:
                            return this.convertMemberCardForModule(objectMapper, node, user);
                        case ModuleConstant.M_BARGAIN:
                            return this.convertBargainForModule(objectMapper, node, user);
                        case ModuleConstant.M_SECKILL:
                            return this.convertSeckillForModule(objectMapper, node, user);
                        case ModuleConstant.M_PIN_INTEGRATION:
                            return  this.convertPinIntegrationForModule(objectMapper, node, user);
                        case ModuleConstant.M_GROUP_DRAW:
                            return  this.convertGroupDrawForModule(objectMapper, node, user);
                        case ModuleConstant.M_INTEGRAL:
                            return  this.convertIntegralForModule(objectMapper, node, user);
                        case ModuleConstant.M_PRESCRIPTION:
                            return  this.convertPrescriptionForModule(objectMapper, node, user, patientId);
                        case ModuleConstant.M_CASE_HISTORY:
                            return  this.convertCaseHistoryForModule(objectMapper, node, user, patientId);
                        //TODO case
                        default:
                    }
                }
            }
        } catch (Exception e) {
            logger().error("小程序首页装修模块内容转换错误,pageContent:{}", pageContent);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 商品模块
     *
     * @param objectMapper
     * @param node
     * @param userId
     * @return
     * @throws IOException
     */
    public ModuleGoods convertGoodsForModule(ObjectMapper objectMapper, Entry<String, JsonNode> node, Integer userId) throws IOException {
        ModuleGoods moduleGoods = objectMapper.readValue(node.getValue().toString(), ModuleGoods.class);
        GoodsListMpParam param = new GoodsListMpParam();
        param.setRecommendType(moduleGoods.getRecommendType());
        param.setAutoRecommendType(moduleGoods.getAutoRecommendType());
        if (moduleGoods.getGoodsItems() == null) {
            param.setGoodsItems(new ArrayList<>());
        } else {
            List<Integer> ids = moduleGoods.getGoodsItems().stream().map(ModuleGoods.PhpPointGoodsConverter::getGoodsId).collect(Collectors.toList());
            param.setGoodsItems(ids);
        }
        param.setKeywords(moduleGoods.getKeywords());
        param.setMinPrice(moduleGoods.getMinPrice());
        param.setMaxPrice(moduleGoods.getMaxPrice());
        param.setGoodsArea(moduleGoods.getGoodsArea());
        param.setGoodsAreaData(moduleGoods.getGoodsAreaData());
        param.setGoodsType(moduleGoods.getGoodsType());
        param.setSortType(moduleGoods.getSortType());
        param.setGoodsNum(moduleGoods.getGoodsNum());
        param.setFromPage(EsGoodsConstant.GOODS_LIST_PAGE);
        // 转换实时信息
        PageResult<GoodsListMpBo> pageIndexGoodsList = goodsMpService.getPageIndexGoodsList(param, userId);
        List<? extends GoodsListMpVo> goodsList = pageIndexGoodsList.getDataList();
        moduleGoods.setGoodsListData(goodsList);
        moduleGoods.setHasMore(pageIndexGoodsList.getPage().getLastPage() > 1);

        return moduleGoods;
    }

    /**
     * 商品分组模块
     *
     * @param objectMapper
     * @param node
     * @param userId
     * @return
     * @throws IOException
     */
    public ModuleGoodsGroup convertGoodsGroupForModule(ObjectMapper objectMapper, Entry<String, JsonNode> node, Integer userId) throws IOException {
        ModuleGoodsGroup moduleGoodsGroup = objectMapper.readValue(node.getValue().toString(), ModuleGoodsGroup.class);
        GoodsGroupListMpParam param = new GoodsGroupListMpParam();
        param.setUserId(userId);

        if (!GoodsConstant.GOODS_GROUP_LIST_TOP_POSITION.equals(moduleGoodsGroup.getPositionStyle()) || !GoodsConstant.GOODS_GROUP_LIST_SHOW_ALL_COLUMN.equals(moduleGoodsGroup.getGroupDisplay())) {
            ModuleGoodsGroup.SortGroup sortGroup = moduleGoodsGroup.getSortGroupArr().get(0);
            GoodsGroupListMpParam.SortGroup paramGroup = new GoodsGroupListMpParam.SortGroup(sortGroup.getSortId(), sortGroup.getSortType(), sortGroup.getGroupGoodsId(), sortGroup.getIsAll());
            param.setSortGroupArr(Collections.singletonList(paramGroup));
        } else {
            List<GoodsGroupListMpParam.SortGroup> groups = new ArrayList<>();
            for (ModuleGoodsGroup.SortGroup sortGroup : moduleGoodsGroup.getSortGroupArr()) {
                GoodsGroupListMpParam.SortGroup paramGroup = new GoodsGroupListMpParam.SortGroup(sortGroup.getSortId(),sortGroup.getSortType(),sortGroup.getGroupGoodsId(),sortGroup.getIsAll());
                groups.add(paramGroup);
            }
            param.setSortGroupArr(groups);
        }
        // 转换实时信息
        List<? extends GoodsListMpVo> pageIndexGoodsList = goodsMpService.goodsGroupMpService.getGoodsGroupList(param);
        moduleGoodsGroup.setGoodsListData(pageIndexGoodsList);
        return moduleGoodsGroup;
    }

    /**
     * 优惠券模块
     *
     * @param objectMapper
     * @param node
     * @param user
     * @return
     * @throws IOException
     */
    private List<CouponPageDecorationVo> convertCouponForModule(ObjectMapper objectMapper, Entry<String, JsonNode> node, UserRecord user) throws IOException {
        ModuleCoupon moduleCoupon = objectMapper.readValue(node.getValue().toString(), ModuleCoupon.class);
        Integer userId = user.getUserId();

        // 转换实时信息
        return saas.getShopApp(getShopId()).mpCoupon.getPageIndexCouponList(moduleCoupon, userId);
    }

    /**
     * 会员卡模块
     *
     * @param objectMapper
     * @param node
     * @param user
     * @return
     * @throws IOException
     */
    private MemberCardPageDecorationVo convertMemberCardForModule(ObjectMapper objectMapper, Entry<String, JsonNode> node, UserRecord user) throws IOException {
        ModuleCard moduleCard = objectMapper.readValue(node.getValue().toString(), ModuleCard.class);
        Integer userId = user.getUserId();

        // 转换实时信息
        return member.card.getPageIndexMemberCard(moduleCard, userId);
    }

    /**
     * 砍价模块
     *
     * @param objectMapper
     * @param node
     * @param user
     * @return
     * @throws IOException
     */
    private ModuleBargain convertBargainForModule(ObjectMapper objectMapper, Entry<String, JsonNode> node, UserRecord user) throws IOException {
        ModuleBargain moduleBargain = objectMapper.readValue(node.getValue().toString(), ModuleBargain.class);

        // 转换实时信息
        return saas.getShopApp(getShopId()).bargain.getPageIndexBargain(moduleBargain);
    }

    /**
     * 秒杀模块
     *
     * @param objectMapper
     * @param node
     * @param user
     * @return
     * @throws IOException
     */
    private ModuleSeckill convertSeckillForModule(ObjectMapper objectMapper, Entry<String, JsonNode> node, UserRecord user) throws IOException {
        ModuleSeckill moduleSecKill = objectMapper.readValue(node.getValue().toString(), ModuleSeckill.class);

        // 转换实时信息
        return saas.getShopApp(getShopId()).seckill.getPageIndexSeckill(moduleSecKill);
    }

    /**
     * 瓜分积分模块
     *
     * @param objectMapper
     * @param node
     * @param user
     * @return
     * @throws IOException
     */
    private ModuleGroupIntegration convertPinIntegrationForModule(ObjectMapper objectMapper, Entry<String, JsonNode> node, UserRecord user) throws IOException {
        ModuleGroupIntegration moduleGroupIntegration = objectMapper.readValue(node.getValue().toString(), ModuleGroupIntegration.class);

        // 转换实时信息
        return saas.getShopApp(getShopId()).groupIntegration.getPageIndexGroupIntegration(moduleGroupIntegration,user.getUserId());
    }

    /**
     * 拼团抽奖模块
     *
     * @param objectMapper
     * @param node
     * @param user
     * @return
     * @throws IOException
     */
    private ModuleGroupDraw convertGroupDrawForModule(ObjectMapper objectMapper, Entry<String, JsonNode> node, UserRecord user) throws IOException {
        ModuleGroupDraw moduleGroupDraw = objectMapper.readValue(node.getValue().toString(), ModuleGroupDraw.class);

        // 转换实时信息
        return saas.getShopApp(getShopId()).groupDraw.getPageIndexGroupDraw(moduleGroupDraw);
    }

    /**
     * 积分兑换模块
     *
     * @param objectMapper
     * @param node
     * @param user
     * @return
     * @throws IOException
     */
    private ModuleIntegral convertIntegralForModule(ObjectMapper objectMapper, Entry<String, JsonNode> node, UserRecord user) throws IOException {
        ModuleIntegral moduleIntegral = objectMapper.readValue(node.getValue().toString(), ModuleIntegral.class);

        // 转换实时信息
        return saas.getShopApp(getShopId()).integralConvertService.getPageIndexIntegral(moduleIntegral);
    }

    /**
     * 我的处方模块
     *
     * @param objectMapper
     * @param node
     * @param user
     * @return
     * @throws IOException
     */
    private ModulePrescription convertPrescriptionForModule(ObjectMapper objectMapper, Entry<String, JsonNode> node, UserRecord user, Integer patientId) throws IOException {
        ModulePrescription moduleIntegral = objectMapper.readValue(node.getValue().toString(), ModulePrescription.class);
        PrescriptionPatientListParam prescriptionListParam = new PrescriptionPatientListParam();
        prescriptionListParam.setPageRows((Integer) 3);
        prescriptionListParam.setPatientId(patientId);
        prescriptionListParam.setUserId(user.getUserId());
        PageResult<PrescriptionListVo> prescriptionListData = prescriptionService.listPatientPageResult(prescriptionListParam);
        moduleIntegral.setPrescriptionListData(prescriptionListData.getDataList());
        moduleIntegral.setHasMore(prescriptionListData.getPage().getLastPage() > 1);
        return moduleIntegral;
        // 转换实时信息
//        return saas.getShopApp(getShopId()).integralConvertService.getPageIndexIntegral(moduleIntegral);
    }

    /**
     * 我的处方模块
     *
     * @param objectMapper
     * @param node
     * @param user
     * @return
     * @throws IOException
     */
    private ModuleCaseHistory convertCaseHistoryForModule(ObjectMapper objectMapper, Entry<String, JsonNode> node, UserRecord user, Integer patientId) throws IOException {
        ModuleCaseHistory moduleCaseHistory = objectMapper.readValue(node.getValue().toString(), ModuleCaseHistory.class);
        MedicalHistoryPageInfoParam medicalHistoryPageInfoParam = new MedicalHistoryPageInfoParam();
        medicalHistoryPageInfoParam.setPageRows((Integer) 3);
        medicalHistoryPageInfoParam.setPatientId(patientId);
        medicalHistoryPageInfoParam.setUserId(user.getUserId());
        PageResult<MedicalHistoryPageInfoVo> medicalHistoryListData = medicalHistoryService.getMedicalHistoryPageInfo(medicalHistoryPageInfoParam);
        moduleCaseHistory.setCaseHistoryListData(medicalHistoryListData.getDataList());
        moduleCaseHistory.setHasMore(medicalHistoryListData.getPage().getLastPage() > 1);
        return moduleCaseHistory;
        // 转换实时信息
//        return saas.getShopApp(getShopId()).integralConvertService.getPageIndexIntegral(moduleIntegral);
    }

    /**
     * 店招模块
     * @param objectMapper
     * @param node
     * @param user
     * @return
     * @throws IOException
     */
    private ModuleShop convertShopBgForIndex(ObjectMapper objectMapper, Entry<String, JsonNode> node, UserRecord user) throws IOException {
        ModuleShop moduleShop = objectMapper.readValue(node.getValue().toString(), ModuleShop.class);
        String shopBgPath = moduleShop.getShopBgPath();
        if(StringUtils.isNotEmpty(shopBgPath)) {
        	//shop_bg_path返回店铺设置中的地址
        	ShopPojo shopInfo = saas.shop.getShopById(getShopId()).into(ShopPojo.class);
        	moduleShop.setShopBgPath("/"+shopInfo.getShopAvatar());
        }
		return moduleShop;
    }

    /**
     * 店招模块
     * @param objectMapper
     * @param node
     * @param user
     * @return
     * @throws IOException
     */
    private ModuleIntegral convertIntegralForIndex(ObjectMapper objectMapper, Entry<String, JsonNode> node, UserRecord user) throws IOException {
        ModuleIntegral moduleIntegral = objectMapper.readValue(node.getValue().toString(), ModuleIntegral.class);
        moduleIntegral.setNeedRequest(true);
        return moduleIntegral;
    }

    /**
     * 我的处方模块
     * @param objectMapper
     * @param node
     * @param user
     * @return
     * @throws IOException
     */
    private ModulePrescription convertPrescriptionForIndex(ObjectMapper objectMapper, Entry<String, JsonNode> node, UserRecord user) throws IOException {
        ModulePrescription modulePrescription = objectMapper.readValue(node.getValue().toString(), ModulePrescription.class);
        modulePrescription.setNeedRequest(true);
        return modulePrescription;
    }

    /**
     * 我的病历模块
     * @param objectMapper
     * @param node
     * @param user
     * @return
     * @throws IOException
     */
    private ModuleCaseHistory convertCaseHistoryForIndex(ObjectMapper objectMapper, Entry<String, JsonNode> node, UserRecord user) throws IOException {
        ModuleCaseHistory moduleCaseHistory = objectMapper.readValue(node.getValue().toString(), ModuleCaseHistory.class);
        moduleCaseHistory.setNeedRequest(true);
        return moduleCaseHistory;
    }

    /**
     * 获取店铺悬浮窗配置
     *
     * @return
     */
    public SuspendWindowConfig getSuspendWindowConfig(WxAppPageParam param) {
        SuspendWindowConfig res = suspendWindowConfigService.getSuspendCfg();
        if (res == null || res.getPageFlag().equals(BaseConstant.NO) || CollectionUtils.isEmpty(res.getPageIds()) || !res.getPageIds().contains(param.getPageId())) {
            return null;
        }

        if (StringUtil.isNotBlank(res.getMainBefore())) {
            res.setMainBefore(domainConfig.imageUrl(res.getMainBefore()));
        }
        if (StringUtil.isNotBlank(res.getMainAfter())) {
            res.setMainAfter(domainConfig.imageUrl(res.getMainAfter()));
        }
        Iterator<SuspendWindowConfig.ChildIcon> it = res.getChildrenArr().iterator();
        while (it.hasNext()) {
            SuspendWindowConfig.ChildIcon c = it.next();
            if (c.getChildFlag().equals(BaseConstant.NO)) {
                it.remove();
            }
            if (StringUtil.isNotBlank(c.getImg())) {
                c.setImg(domainConfig.imageUrl(c.getImg()));
            }
        }
        return res;
    }
}
