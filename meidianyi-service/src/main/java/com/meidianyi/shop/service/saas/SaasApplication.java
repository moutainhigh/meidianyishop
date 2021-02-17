package com.meidianyi.shop.service.saas;

import com.meidianyi.shop.dao.foundation.database.DatabaseManager;
import com.meidianyi.shop.service.saas.api.ApiExternalRequestService;
import com.meidianyi.shop.service.saas.area.AreaSelectService;
import com.meidianyi.shop.service.saas.article.ArticleCategoryService;
import com.meidianyi.shop.service.saas.article.ArticleService;
import com.meidianyi.shop.service.saas.categroy.SysCateService;
import com.meidianyi.shop.service.saas.db.DataExportService;
import com.meidianyi.shop.service.saas.db.RepairDatabaseService;
import com.meidianyi.shop.service.saas.es.EsMappingUpdateService;
import com.meidianyi.shop.service.saas.external.AppAuthService;
import com.meidianyi.shop.service.saas.external.ExternalRequestHistoryService;
import com.meidianyi.shop.service.saas.image.SystemImageService;
import com.meidianyi.shop.service.saas.index.ShopViewOrderService;
import com.meidianyi.shop.service.saas.index.ShopViewService;
import com.meidianyi.shop.service.saas.official.OfficialService;
import com.meidianyi.shop.service.saas.order.MainInquiryOrderService;
import com.meidianyi.shop.service.saas.order.SaasOrderService;
import com.meidianyi.shop.service.saas.overview.ShopOverviewService;
import com.meidianyi.shop.service.saas.privilege.ChildAccountService;
import com.meidianyi.shop.service.saas.privilege.MenuService;
import com.meidianyi.shop.service.saas.privilege.RoleService;
import com.meidianyi.shop.service.saas.privilege.SystemUserService;
import com.meidianyi.shop.service.saas.question.QuestionService;
import com.meidianyi.shop.service.saas.region.CityService;
import com.meidianyi.shop.service.saas.region.RegionService;
import com.meidianyi.shop.service.saas.schedule.TaskJobMainService;
import com.meidianyi.shop.service.saas.schedule.cron.MpCronRegistration;
import com.meidianyi.shop.service.saas.schedule.rabbit.RabbitDataService;
import com.meidianyi.shop.service.saas.shop.MpDeployHistoryService;
import com.meidianyi.shop.service.saas.shop.ShopService;
import com.meidianyi.shop.service.saas.shop.UserLoginService;
import com.meidianyi.shop.service.saas.shop.WxMainUserService;
import com.meidianyi.shop.service.shop.ShopApplication;
import com.meidianyi.shop.service.shop.market.message.MessageTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author 新国
 *
 */
@Service
public class SaasApplication {
	@Autowired
	public SystemUserService sysUser;
	@Autowired
	public ChildAccountService childAccount;
	@Autowired
	public RoleService role;
	@Autowired
	public MenuService menu;
	@Autowired
	public ShopService shop;
	@Autowired
	public RegionService region;
	@Autowired
	public ArticleService article;
	@Autowired
	public ArticleCategoryService articleCategory;
	@Autowired
	public OfficialService official;
	@Autowired
	public SysCateService sysCate;
	@Autowired
	public RepairDatabaseService repairDb;
	@Autowired
	public ShopOverviewService overviewService;
	@Autowired
	public CityService city;
	@Autowired
	public SystemImageService sysImage;
	@Autowired
	public MpDeployHistoryService deployHistoryService;
	@Autowired
    public TaskJobMainService taskJobMainService;
    @Autowired
    public DataExportService dataExportService;
	@Autowired
	DatabaseManager databaseManager;

	@Autowired
	protected ShopApplication shopApplication;

	@Autowired
    public RabbitDataService rabbitDataService;

	@Autowired
    public AreaSelectService areaSelectService;

    @Autowired
	public MessageTemplateService messageTemplateService;

    @Autowired
	public WxMainUserService wxUserService;

    @Autowired
	public UserLoginService userLoginService;

    @Autowired
    public MpCronRegistration mpCronRegistration;

    @Autowired
    public ShopViewService shopViewService;

    @Autowired
    public ShopViewOrderService shopViewOrderService;


    @Autowired
    public QuestionService questionService;

    @Autowired
    public SaasOrderService orderService;

    @Autowired
    public EsMappingUpdateService esMappingUpdateService;

    @Autowired
    public AppAuthService appAuthService;

    @Autowired
    public ExternalRequestHistoryService externalRequestHistoryService;

    @Autowired
    public ApiExternalRequestService apiExternalRequestService;
    @Autowired
    public MainInquiryOrderService mainInquiryOrderService;
	@Autowired
	public SaasOrderService saasOrderService;
	@Autowired
	public ShopService shopService;

	public ShopApplication getShopApp(Integer shopId) {
		databaseManager.switchShopDb(shopId);
		return shopApplication;
	}

}
