package com.meidianyi.shop.service.shop.member;

import static com.meidianyi.shop.db.shop.Tables.SHOP_CFG;
import static com.meidianyi.shop.db.shop.Tables.USER_IMPORT;
import static com.meidianyi.shop.db.shop.Tables.USER_IMPORT_DETAIL;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.ss.usermodel.Workbook;
import org.jooq.Result;
import org.jooq.SelectWhereStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelReader;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.excel.ExcelWriter;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.IdentityUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.main.tables.records.DictCityRecord;
import com.meidianyi.shop.db.main.tables.records.DictDistrictRecord;
import com.meidianyi.shop.db.main.tables.records.DictProvinceRecord;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.db.shop.tables.records.MemberCardRecord;
import com.meidianyi.shop.db.shop.tables.records.ShopCfgRecord;
import com.meidianyi.shop.db.shop.tables.records.UserDetailRecord;
import com.meidianyi.shop.db.shop.tables.records.UserImportDetailRecord;
import com.meidianyi.shop.db.shop.tables.records.UserImportRecord;
import com.meidianyi.shop.db.shop.tables.records.UserRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobsConstant;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobsConstant.TaskJobEnum;
import com.meidianyi.shop.service.pojo.shop.config.distribution.DistributionParam;
import com.meidianyi.shop.service.pojo.shop.coupon.CouponView;
import com.meidianyi.shop.service.pojo.shop.coupon.CouponWxUserImportVo;
import com.meidianyi.shop.service.pojo.shop.coupon.MpGetCouponParam;
import com.meidianyi.shop.service.pojo.shop.coupon.give.CouponGiveQueueParam;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorGroupListVo;
import com.meidianyi.shop.service.pojo.shop.member.MemberEducationEnum;
import com.meidianyi.shop.service.pojo.shop.member.MemberIndustryEnum;
import com.meidianyi.shop.service.pojo.shop.member.MemberMarriageEnum;
import com.meidianyi.shop.service.pojo.shop.member.MemberSexEnum;
import com.meidianyi.shop.service.pojo.shop.member.account.AddMemberCardParam;
import com.meidianyi.shop.service.pojo.shop.member.account.ScoreParam;
import com.meidianyi.shop.service.pojo.shop.member.userimp.CardInfoVo;
import com.meidianyi.shop.service.pojo.shop.member.userimp.SetNoticeJson;
import com.meidianyi.shop.service.pojo.shop.member.userimp.SetNoticeJsonDetailVo;
import com.meidianyi.shop.service.pojo.shop.member.userimp.SetNoticeJsonVo;
import com.meidianyi.shop.service.pojo.shop.member.userimp.SetNoticeParam;
import com.meidianyi.shop.service.pojo.shop.member.userimp.UiGetListParam;
import com.meidianyi.shop.service.pojo.shop.member.userimp.UiGetListVo;
import com.meidianyi.shop.service.pojo.shop.member.userimp.UiGetNoActListParam;
import com.meidianyi.shop.service.pojo.shop.member.userimp.UiGetNoActListVo;
import com.meidianyi.shop.service.pojo.shop.member.userimp.UserImportActivePojo;
import com.meidianyi.shop.service.pojo.shop.member.userimp.UserImportErroPojo;
import com.meidianyi.shop.service.pojo.shop.member.userimp.UserImportMqParam;
import com.meidianyi.shop.service.pojo.shop.member.userimp.UserImportParam;
import com.meidianyi.shop.service.pojo.shop.member.userimp.UserImportPojo;
import com.meidianyi.shop.service.pojo.shop.member.userimp.UserImportTemplate;
import com.meidianyi.shop.service.pojo.shop.operation.RecordTradeEnum;
import com.meidianyi.shop.service.pojo.shop.operation.RemarkTemplate;
import com.meidianyi.shop.service.shop.config.ConfigService;
import com.meidianyi.shop.service.shop.coupon.CouponMpService;
import com.meidianyi.shop.service.shop.coupon.CouponService;
import com.meidianyi.shop.service.shop.member.dao.CardDaoService;
import com.meidianyi.shop.service.shop.member.excel.UserImExcelWrongHandler;
import com.meidianyi.shop.service.shop.user.user.UserService;

/**
 * 会员导入
 *
 * @author zhaojianqiang
 * @time 下午1:51:15
 */
@Service
public class UserImportService extends ShopBaseService {
	private static final String EXCEL = "excel";
	@Autowired
	private UserService userService;
	@Autowired
	private CardDaoService cardDaoService;
	@Autowired
	private CouponMpService couponMpService;
	@Autowired
	private CouponService couponService;
	@Autowired
	private ScoreService scoreService;
	@Autowired
	private MemberCardService cardService;
	@Autowired
	private ConfigService configService;

	private static final String PHONEREG = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
	private static final String USER_IMPORT_NOTICE = "user_import_notice";
	private static final BigDecimal ZERO = new BigDecimal("0");
	// private static final String[] sexs = { "男", "女" };
	// private static final String[] marriages = { "未婚", "已婚", "保密" };
	// private static final String[] educations = { "初中", "高中", "中专", "大专", "本科",
	// "硕士", "博士", "其他" };
//	private static final String[] industrys = { "计算机硬件及网络设备", "计算机软件", "IT服务（系统/数据/维护）/多领域经营", "互联网/电子商务", "网络游戏",
//			"通讯（设备/运营/增值服务）", "电子技术/半导体/集成电路", "仪器仪表及工业自动化", "金融/银行/投资/基金/证券", "保险", "房地产/建筑/建材/工程", "家居/室内设计/装饰装潢",
//			"物业管理/商业中心", "广告/会展/公关/市场推广", "媒体/出版/影视/文化/艺术", "印刷/包装/造纸", "咨询/管理产业/法律/财会", "教育/培训", "检验/检测/认证", "中介服务",
//			"贸易/进出口", "零售/批发", "快速消费品（食品/饮料/烟酒/化妆品", "耐用消费品（服装服饰/纺织/皮革/家具/家电）", "办公用品及设备", "礼品/玩具/工艺美术/收藏品",
//			"大型设备/机电设备/重工业", "加工制造（原料加工/模具）", "汽车/摩托车（制造/维护/配件/销售/服务）", "交通/运输/物流", "医药/生物工程", "医疗/护理/美容/保健", "医疗设备/器械",
//			"酒店/餐饮", "娱乐/体育/休闲", "旅游/度假", "石油/石化/化工", "能源/矿产/采掘/冶炼", "电气/电力/水利", "航空/航天", "学术/科研", "政府/公共事业/非盈利机构",
//			"环保", "农/林/牧/渔", "跨领域经营", "其它" };



	private static final Byte ONE = 1;
	private static final Byte BYTE_ZERO = 0;
	private static final Byte BYTE_ONE = 1;
	private static final Byte BYTE_TWO = 2;
	private static final Byte BYTE_THREE = 3;
	private static final Byte BYTE_FOUR = 4;
	private static final Byte BYTE_FIVE = 5;
	private static final String DATE_FORMATE = "yyyy/MM/dd";

	/**
	 * 设置用户导入通知
	 *
	 * @param param
	 * @return
	 */
	public JsonResultCode setActivationNotice(SetNoticeParam param) {
		String explain = param.getExplain();
		String score = param.getScore();
		int[] couponIds = param.getCouponIds();
		if (StringUtils.isEmpty(explain)) {
			// 请设置通知说明
			return JsonResultCode.CODE_EXPLAIN_MUST;
		}
		if (StringUtils.isEmpty(score) && couponIds.length == 0) {
			// 至少选择一种激活奖励
			return JsonResultCode.CODE_NEED_ONE;
		}
		SetNoticeJson json = new SetNoticeJson();
		json.setExplain(explain);
		if (couponIds != null) {
			json.setMrkingVoucherId(setMrkingVoucher(couponIds));
		}
		json.setScore(StringUtils.isEmpty(score) ? "" : score);

		String json2 = Util.toJson(json);
		int setShopCfg = setShopCfg(USER_IMPORT_NOTICE, json2);
		return setShopCfg > 0 ? JsonResultCode.CODE_SUCCESS : JsonResultCode.CODE_FAIL;
	}

	/**
	 * 获取用户导入通知
	 *
	 * @return
	 */
	public SetNoticeJson getActivationNotice() {
		ShopCfgRecord record = db().selectFrom(SHOP_CFG).where(SHOP_CFG.K.eq(USER_IMPORT_NOTICE)).fetchAny();
		SetNoticeJson json = new SetNoticeJson();
		if (record == null) {
			return json;
		}
		json = Util.parseJson(record.getV(), SetNoticeJson.class);

		return json;
	}

	public SetNoticeJsonVo getAllActivationNotice() {
		SetNoticeJson json = getActivationNotice();
		String mrkingVoucherId = json.getMrkingVoucherId();
		List<CouponView> couponViewByIds=new ArrayList<CouponView>();
		if(StringUtils.isNotEmpty(mrkingVoucherId)) {
			couponViewByIds = couponService.getCouponViewByIds(Util.splitValueToList(mrkingVoucherId));
		}
		return new SetNoticeJsonVo(json.getExplain(), json.getScore(), mrkingVoucherId, couponViewByIds);
	}

	public SetNoticeJsonDetailVo getInfo(String lang) {
		SetNoticeJson activationNotice = getActivationNotice();
		String mrkingVoucherId = activationNotice.getMrkingVoucherId();
		List<CouponWxUserImportVo> voList = new ArrayList<CouponWxUserImportVo>();
		if (StringUtils.isNotEmpty(mrkingVoucherId)) {
			String[] split = mrkingVoucherId.split(",");
			for (String string : split) {
				CouponWxUserImportVo couponVo = couponService.getOneMvById(Integer.valueOf(string), lang);
				if (couponVo != null) {
					voList.add(couponVo);
				}
			}
		}
		return new SetNoticeJsonDetailVo(activationNotice.getExplain(), activationNotice.getScore(),
				activationNotice.getMrkingVoucherId(), voList);
	}

	/**
	 * 获取模板
	 *
	 * @param lang
	 * @return
	 */
	public Workbook getTemplate(String lang) {
		List<UserImportPojo> list = new ArrayList<UserImportPojo>();
		UserImportPojo vo = new UserImportPojo();
		vo.setMobile("15093037027");
		vo.setName(Util.translateMessage(lang, JsonResultCode.CODE_EXCEL_EXAMPLE_USERNAME.getMessage(), EXCEL, null));
		vo.setInviteUserMobile("18700000000");
		vo.setScore(1000);
		vo.setSex(Util.translateMessage(lang, JsonResultCode.CODE_EXCEL_EXAMPLE_SEX.getMessage(), EXCEL, null));
		vo.setBirthday("2019/12/30");
		vo.setProvince(
				Util.translateMessage(lang, JsonResultCode.CODE_EXCEL_EXAMPLE_PROVINCE.getMessage(), EXCEL, null));
		vo.setCity(Util.translateMessage(lang, JsonResultCode.CODE_EXCEL_EXAMPLE_CITY.getMessage(), EXCEL, null));
		vo.setDistrict(
				Util.translateMessage(lang, JsonResultCode.CODE_EXCEL_EXAMPLE_DISTRICT.getMessage(), EXCEL, null));
		vo.setAddress(Util.translateMessage(lang, JsonResultCode.CODE_EXCEL_EXAMPLE_ADDRESS.getMessage(), EXCEL, null));
		vo.setIdNumber("450328198102039022");
		vo.setEducation(MemberEducationEnum.getNameByCode(MemberEducationEnum.JUNIOR.getCode(), lang));
		vo.setIndustry(MemberIndustryEnum.getNameByCode(MemberIndustryEnum.COMMERCE.getCode(), lang));
		vo.setMarriage(
				Util.translateMessage(lang, JsonResultCode.CODE_EXCEL_EXAMPLE_MARRIAGE.getMessage(), EXCEL, null));
		vo.setIncome(new BigDecimal("100"));
		vo.setIsDistributor("1");
		list.add(vo);
		return getModel(lang, list);
	}

	public Boolean insertUser(String lang, UserImportParam param) {
		logger().info("会员导入");
		MultipartFile multipartFile = param.getFile();
		ExcelTypeEnum type = checkFile(multipartFile);
		if (type == null) {
			// 文件类型不正确，请上传Excel文件
			return false;
		}

		Workbook workbook = null;
		try {
			InputStream inputStream = multipartFile.getInputStream();
			workbook = ExcelFactory.createWorkbook(inputStream, type);
		} catch (IOException e) {
			logger().info("excel读取错误");
			logger().info(e.getMessage(), e);
			return false;
		}
		/**
		 * excel解析错误处理器
		 */
		UserImExcelWrongHandler handler = new UserImExcelWrongHandler();
		ExcelReader excelReader = new ExcelReader(lang, workbook, handler);
		List<UserImportPojo> models = excelReader.readModelList(UserImportPojo.class);
		String cardId = param.getCardId();
		Integer groupId = param.getGroupId();
		Integer tagId = param.getTagId();
		UserImportMqParam mqParam = new UserImportMqParam(models, lang, getShopId(), cardId, groupId, tagId,null);
		logger().info("会员导入发队列");
		saas.taskJobMainService.dispatchImmediately(mqParam, UserImportMqParam.class.getName(), getShopId(),
				TaskJobEnum.OTHER_MQ.getExecutionType());
		// checkList(models, cardId, groupId, tagId);
		return true;

	}

	public void checkList(List<UserImportPojo> list, String cardId, Integer groupId, Integer tagId, String lang) {
		logger().info("会员导入执行队列");
		int successNum = 0;
		int totalNum = list.size();
		for (UserImportPojo userImportPojo : list) {
			String mobile = userImportPojo.getMobile();
			logger().info("手机号"+mobile);
			if (StringUtils.isEmpty(mobile)) {
				logger().info("手机号为空");
				userImportPojo.setErrorMsg(UserImportTemplate.MOBILE_NULL.getCode());
				continue;
			}
			if (!Pattern.matches(PHONEREG, mobile)) {
				logger().info("手机号格式错误");
				userImportPojo.setErrorMsg(UserImportTemplate.MOBILE_ERROR.getCode());
				continue;
			}
			UserRecord userRecord = userService.getUserByMobile(mobile);
			if (userRecord != null) {
				logger().info("会员手机号已存在");
				userImportPojo.setErrorMsg(UserImportTemplate.MOBILE_EXIST.getCode());
				continue;
			}
			String name = userImportPojo.getName();
			logger().info("姓名"+name);
			if (StringUtils.isNotEmpty(name)) {
				if (name.length() > 10) {
					logger().info("姓名限制10个字符");
					userImportPojo.setErrorMsg(UserImportTemplate.NAME_LIMIT.getCode());
					continue;
				}
			}
			String inviteUserMobile = userImportPojo.getInviteUserMobile();
			logger().info("邀请人手机"+inviteUserMobile);
			if (StringUtils.isNotEmpty(inviteUserMobile)) {
				if (!Pattern.matches(PHONEREG, inviteUserMobile)) {
					logger().info("邀请人手机号格式错误");
					userImportPojo.setErrorMsg(UserImportTemplate.INVITEUSER_ERROR.getCode());
					continue;
				}
				UserRecord userByMobile = userService.getUserByMobile(inviteUserMobile);
				if (userByMobile == null) {
					logger().info("邀请人不存在");
					userImportPojo.setErrorMsg(UserImportTemplate.INVITEUSER_NO.getCode());
					continue;
				}
			}
			Integer score = userImportPojo.getScore();
			logger().info("积分"+score);
			if (null == score) {
				logger().info("积分为空");
				userImportPojo.setErrorMsg(UserImportTemplate.SCORE_NULL.getCode());
				continue;
			}
			if (score < 0) {
				logger().info("无效积分");
				userImportPojo.setErrorMsg(UserImportTemplate.SCORE_ERROR.getCode());
				continue;
			}

			String sex = userImportPojo.getSex();
			logger().info("性别"+sex);
			String[] sexs = MemberSexEnum.getArraySexs(lang);
			if (StringUtils.isNotEmpty(sex) && !checkRule(sexs, sex)) {
				logger().info("性别仅限男女");
				userImportPojo.setErrorMsg(UserImportTemplate.SEX_ERROR.getCode());
				continue;
			}
			String birthday = userImportPojo.getBirthday();
			logger().info("生日"+sex);
			if (StringUtils.isNotEmpty(birthday)) {
				try {
					// ExcelUtil.DATE_FORMAT
					LocalDate parse = LocalDate.parse(birthday, DateTimeFormatter.ofPattern(DateUtils.DATE_FORMAT_SIMPLE));
					//userImportPojo.setBirthday(parse.toString());
				} catch (Exception e) {
					logger().info("生日日期格式错误");
					userImportPojo.setErrorMsg(UserImportTemplate.BIRTHDAY_ERROR.getCode());
					logger().info(e.getMessage(), e);
					continue;
				}
			}

            if (errorArea(userImportPojo)) {
                continue;
            }

            String idNumber = userImportPojo.getIdNumber();
			logger().info("身份证"+idNumber);
			if (!IdentityUtils.isLegalPattern(idNumber)) {
				logger().info("无效身份证号");
				userImportPojo.setErrorMsg(UserImportTemplate.ID_ERROR.getCode());
				continue;
			}
			BigDecimal income = userImportPojo.getIncome();
			logger().info("收入"+income);
			if (income != null && income.compareTo(ZERO) == -1) {
				logger().info("无效收入");
				userImportPojo.setErrorMsg(UserImportTemplate.INCOME_ERROR.getCode());
				continue;
			}
			String marriage = userImportPojo.getMarriage();
			logger().info("婚姻状况"+marriage);
			String[] marriages = MemberMarriageEnum.getArrayMarriage(lang);
			if (StringUtils.isNotEmpty(marriage) && !checkRule(marriages, marriage)) {
				logger().info("无效婚姻状况");
				userImportPojo.setErrorMsg(UserImportTemplate.MARRIAGE_ERROR.getCode());
				continue;
			}
			String education = userImportPojo.getEducation();
			logger().info("教育状况"+education);
			String[] educations = MemberEducationEnum.getArrayEduction(lang);
			if (StringUtils.isNotEmpty(education) && !checkRule(educations, education)) {
				logger().info("无效教育");
				userImportPojo.setErrorMsg(UserImportTemplate.EDUCATION_ERROR.getCode());
				continue;
			}
			String industry = userImportPojo.getIndustry();
			logger().info("行业状况"+industry);
			String[] industrys = MemberIndustryEnum.getArrayIndustryInfo(lang);
			if (StringUtils.isNotEmpty(industry) && !checkRule(industrys, industry)) {
				logger().info("无效行业");
				userImportPojo.setErrorMsg(UserImportTemplate.INDUSTRY_ERROR.getCode());
				continue;
			}
			successNum++;
		}
		// 可能存在id不正确
        insertUserImport(list, cardId, groupId, tagId, successNum, totalNum);
    }

    private boolean errorArea(UserImportPojo userImportPojo) {
        String province = userImportPojo.getProvince();
        String city = userImportPojo.getCity();
        String district = userImportPojo.getDistrict();
        userImportPojo.setCity(city);
        userImportPojo.setDistrict(district);
        logger().info("省市区"+province+city+district);
        boolean isProvince = StringUtils.isEmpty(province);
        boolean isCity = StringUtils.isEmpty(city);
        boolean isDistrict = StringUtils.isEmpty(district);
        if (isProvince || isCity || isDistrict) {
            logger().info("省，市，区需完整填写");
            userImportPojo.setErrorMsg(UserImportTemplate.ADDRESS_ERROR.getCode());
            return true;
        }
        if (!isProvince) {
            Integer provinceId = saas.region.province.getProvinceIdByName(province);
            if (provinceId == null) {
                logger().info("无效省份");
                userImportPojo.setErrorMsg(UserImportTemplate.PROVINCE_ERROR.getCode());
                return true;
            }
            if (!isCity) {
                DictCityRecord cityId = saas.region.city.getCityId(city, provinceId);
                if (cityId == null) {
                    logger().info("无效市");
                    userImportPojo.setErrorMsg(UserImportTemplate.CITY_ERROR.getCode());
                    return true;
                }
                if (!isDistrict) {
                    Integer districtId = saas.region.district.getDistrictIdByNameAndCityId(cityId.getCityId(),
                            district);
                    if (districtId == null) {
                        logger().info("无效区");
                        userImportPojo.setErrorMsg(UserImportTemplate.DISTRICT_ERROR.getCode());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void insertUserImport(List<UserImportPojo> list, String cardId, Integer groupId, Integer tagId, int successNum, int totalNum) {
        logger().info("准备插入");
        UserImportRecord newRecord = db().newRecord(USER_IMPORT);
        newRecord.setSuccessNum(successNum);
        newRecord.setTotalNum(totalNum);
        newRecord.setCardId(cardId);
        newRecord.setTagId(tagId);
        newRecord.setGroupId(groupId);
        int insert2 = newRecord.insert();
        logger().info("插入USER_IMPORT"+insert2);
        for (UserImportPojo userImportPojo2 : list) {
            UserImportDetailRecord record = db().newRecord(USER_IMPORT_DETAIL, userImportPojo2);
            record.setCardId(cardId);
            record.setTagId(tagId);
            record.setGroupId(groupId);
            //FieldsUtil.assignNotNull(userImportPojo2, record);
            record.setBatchId(newRecord.getId());
            int insert = record.insert();
            logger().info("插入" + insert);
        }
    }

    private Boolean checkRule(String[] list, String mark) {
		for (String string : list) {
			if (string.equals(mark)) {
				return true;
			}
		}
		return false;
	}

	public ExcelTypeEnum checkFile(MultipartFile multipartFile) {
		if (multipartFile == null) {
			return null;
		}
		ExcelTypeEnum type = null;
		try {
			InputStream inputStream = multipartFile.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(inputStream);
			FileMagic fileMagic = FileMagic.valueOf(bis);
			if (Objects.equals(fileMagic, FileMagic.OLE2)) {
				type = ExcelTypeEnum.XLS;
			}
			if (Objects.equals(fileMagic, FileMagic.OOXML)) {
				type = ExcelTypeEnum.XLSX;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return type;
	}

	/**
	 * 返回Excel文件
	 *
	 * @param lang
	 * @param list
	 * @return
	 */
	public Workbook getModel(String lang, List<UserImportPojo> list) {
		Workbook workbook = ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
		ExcelWriter excelWriter = new ExcelWriter(lang, workbook);
		excelWriter.writeModelList(list, UserImportPojo.class);
		return workbook;
	}

	/**
	 * 错误信息的返回
	 *
	 * @param lang
	 * @param list
	 * @return
	 */
	public Workbook getModelErrorMsg(String lang, List<UserImportErroPojo> list) {
		Workbook workbook = ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
		ExcelWriter excelWriter = new ExcelWriter(lang, workbook);
		excelWriter.writeModelList(list, UserImportErroPojo.class);
		return workbook;
	}

	/**
	 * 查询激活成功
	 *
	 * @param lang
	 * @param list
	 * @return
	 */
	public Workbook getModelActive(String lang, List<UserImportActivePojo> list) {
		Workbook workbook = ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
		ExcelWriter excelWriter = new ExcelWriter(lang, workbook);
		excelWriter.writeModelList(list, UserImportActivePojo.class);
		return workbook;
	}

	/**
	 * 查询失败数据
	 *
	 * @param batchId
	 * @return
	 */
	public List<UserImportErroPojo> getErrorMsgById(Integer batchId, String lang) {
		Result<UserImportDetailRecord> fetch = db().selectFrom(USER_IMPORT_DETAIL).where(USER_IMPORT_DETAIL.ERROR_MSG
				.isNotNull().or(USER_IMPORT_DETAIL.ERROR_MSG.eq("")).and(USER_IMPORT_DETAIL.BATCH_ID.eq(batchId)))
				.fetch();
		List<UserImportErroPojo> into = new ArrayList<UserImportErroPojo>();
		if (fetch != null) {
			into = fetch.into(UserImportErroPojo.class);
		}
		for (UserImportErroPojo userImportErroPojo : into) {
			String errorMsg = UserImportTemplate.getNameByCode(userImportErroPojo.getErrorMsg(), lang);
			if (errorMsg != null) {
				userImportErroPojo.setErrorMsg(errorMsg);
			}
		}
		return into;
	}

	/**
	 * 查询激活成功数据
	 *
	 * @param batchId
	 * @return
	 */
	public List<UserImportActivePojo> getActiveById(Integer batchId) {
		Result<UserImportDetailRecord> fetch = db().selectFrom(USER_IMPORT_DETAIL)
				.where(USER_IMPORT_DETAIL.IS_ACTIVATE.eq(ONE).and(USER_IMPORT_DETAIL.BATCH_ID.eq(batchId))).fetch();
		List<UserImportActivePojo> into = new ArrayList<UserImportActivePojo>();
		if (fetch != null) {
			into = fetch.into(UserImportActivePojo.class);
		}
		return into;
	}

	/**
	 * 返回失败的信息
	 *
	 * @param batchId
	 * @param lang
	 * @return
	 */
	public Workbook getErrorMsg(Integer batchId, String lang) {
		return getModelErrorMsg(lang, getErrorMsgById(batchId, lang));
	}

	/**
	 * 激活成功的用户信息
	 *
	 * @param batchId
	 * @param lang
	 * @return
	 */
	public Workbook getActiveExcel(Integer batchId, String lang) {
		return getModelActive(lang, getActiveById(batchId));
	}

	private String setMrkingVoucher(int[] num) {
		int length = num.length;
		if (length != 0) {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < length; i++) {
				builder.append(num[i]);
				if (i != length - 1) {
					builder.append(",");
				}
			}
			return builder.toString();
		}
		return "";

	}

	public int setShopCfg(String key, String value) {
		ShopCfgRecord record = db().selectFrom(SHOP_CFG).where(SHOP_CFG.K.eq(key)).fetchAny();
		int result = 0;
		if (record != null) {
			record.setV(value);
			result = record.update();
		} else {
			record = db().newRecord(SHOP_CFG);
			record.setK(key);
			record.setV(value);
			result = record.insert();
		}
		return result;
	}

	public PageResult<UiGetListVo> getList(UiGetListParam param) {
		SelectWhereStep<UserImportRecord> selectFrom = db().selectFrom(USER_IMPORT);
		Integer batchId = param.getBatchId();
		if (batchId != null) {
			selectFrom.where(USER_IMPORT.ID.eq(batchId));
		}
		Timestamp startTime = param.getStartTime();
		if (startTime != null) {
			selectFrom.where(USER_IMPORT.CREATE_TIME.ge(startTime));
		}
		Timestamp endTime = param.getEndTime();
		if (endTime != null) {
			selectFrom.where(USER_IMPORT.CREATE_TIME.le(endTime));
		}
		selectFrom.where(USER_IMPORT.TOTAL_NUM.gt(0));
		selectFrom.orderBy(USER_IMPORT.ID.desc());
		return this.getPageResult(selectFrom, param.getCurrentPage(), param.getPageRows(), UiGetListVo.class);
	}

	/**
	 * 会员导入列表
	 *
	 * @param param
	 * @return
	 */
	public PageResult<UiGetListVo> descList(UiGetListParam param) {
		PageResult<UiGetListVo> list = getList(param);
		for (UiGetListVo vo : list.getDataList()) {
			vo.setFailNum(vo.getTotalNum() - vo.getSuccessNum());
			int activateNum = getActivateNum(vo.getId(), ONE);
			vo.setActivateNum(activateNum);
			String cardIds = vo.getCardId();
			List<CardInfoVo> cardList = new ArrayList<CardInfoVo>();
			if (StringUtils.isNotEmpty(cardIds)) {
				String[] caStrings = cardIds.split(",");
				for (String cardId : caStrings) {
					CardInfoVo cardVo = new CardInfoVo();
					MemberCardRecord cardInfo = cardDaoService.getInfoByCardId(Integer.parseInt(cardId));
					if (cardInfo != null) {
						cardVo.setCardId(cardInfo.getId());
						cardVo.setCardName(cardInfo.getCardName());
					}
					cardList.add(cardVo);
				}
				vo.setCardList(cardList);
			}
		}
		return list;
	}

	public int getActivateNum(Integer batchId, Byte isActivate) {
		return db().selectCount().from(USER_IMPORT_DETAIL)
				.where(USER_IMPORT_DETAIL.BATCH_ID.eq(batchId).and(USER_IMPORT_DETAIL.IS_ACTIVATE.eq(isActivate)))
				.fetchOne(0, int.class);
	}

	/**
	 * 会员导入明细列表
	 *
	 * @param param
	 * @return
	 */
	public PageResult<UiGetNoActListVo> getDetailList(UiGetNoActListParam param) {
		SelectWhereStep<UserImportDetailRecord> selectFrom = db().selectFrom(USER_IMPORT_DETAIL);
		selectFrom.where(USER_IMPORT_DETAIL.ERROR_MSG.isNull());
		Timestamp startTime = param.getStartTime();
		if (startTime != null) {
			selectFrom.where(USER_IMPORT_DETAIL.CREATE_TIME.ge(startTime));
		}
		Timestamp endTime = param.getEndTime();
		if (endTime != null) {
			selectFrom.where(USER_IMPORT_DETAIL.CREATE_TIME.le(endTime));
		}
		Byte isActivate = param.getIsActivate();
		if (isActivate != null) {
			selectFrom.where(USER_IMPORT_DETAIL.IS_ACTIVATE.eq(isActivate));
		}
		Integer batchId = param.getBatchId();
		if (batchId != null) {
			selectFrom.where(USER_IMPORT_DETAIL.BATCH_ID.eq(batchId));
		}
		String mobile = param.getMobile();
		if (StringUtils.isNotEmpty(mobile)) {
			selectFrom.where(USER_IMPORT_DETAIL.MOBILE.like(likeValue(mobile)));
		}
		String realName = param.getRealName();
		if (StringUtils.isNotEmpty(realName)) {
			selectFrom.where(USER_IMPORT_DETAIL.NAME.like(likeValue(realName)));
		}
		Byte isDistributor = param.getIsDistributor();
		if (isDistributor != null) {
			selectFrom.where(USER_IMPORT_DETAIL.IS_DISTRIBUTOR.eq(isDistributor));
		}
		Integer groupId = param.getGroupId();
		if (groupId != null) {
			selectFrom.where(USER_IMPORT_DETAIL.GROUP_ID.eq(groupId));
		}
		selectFrom.orderBy(USER_IMPORT_DETAIL.ID.desc());
		return this.getPageResult(selectFrom, param.getCurrentPage(), param.getPageRows(), UiGetNoActListVo.class);
	}

	public PageResult<UiGetNoActListVo> addGroupName(UiGetNoActListParam param) {
		PageResult<UiGetNoActListVo> detailList = getDetailList(param);
		for (UiGetNoActListVo vo : detailList.dataList) {
			DistributorGroupListVo oneInfo = saas.getShopApp(getShopId()).distributorGroup.getOneInfo(vo.getGroupId());
			if (oneInfo == null) {
				//TODO 国际化
				vo.setGroupName("分组已删除");
			} else {
				vo.setGroupName(oneInfo.getGroupName());
			}
		}
		return detailList;

	}
	public UserImportDetailRecord getUserByMobile(String mobile, Byte userAction) {
		return db().selectFrom(USER_IMPORT_DETAIL)
				.where(USER_IMPORT_DETAIL.ERROR_MSG.isNull().or(USER_IMPORT_DETAIL.ERROR_MSG.eq(""))
						.and(USER_IMPORT_DETAIL.MOBILE.eq(mobile)).and(USER_IMPORT_DETAIL.USER_ACTION.eq(userAction)))
				.fetchAny();
	}

    /**
     * 激活用户
     * @param userId
     * @return
     */
	public JsonResultCode toActivateUser(Integer userId) {
		UserRecord user = userService.getUserByUserId(userId);
		String mobile = user.getMobile();
		if (StringUtils.isEmpty(mobile)) {
			// 请授权手机号
			return JsonResultCode.CODE_EXCEL_NEED_MOBILE;
		}
		UserImportDetailRecord importUser = getUserByMobile(mobile, ONE);
		if (importUser == null) {
			// 很抱歉！由于您不是本店老会员或因本店还未导入您的会员信息，暂时无法激活，请稍后再试或咨询本店客服
			return JsonResultCode.CODE_EXCEL_SORRY;
		}
		if (Objects.equals(importUser.getIsActivate(), ONE)) {
			// 用户已激活，请勿重复操作
			return JsonResultCode.CODE_EXCEL_OK;
		}
		importUser.setIsActivate(ONE);
		importUser.update();
		SetNoticeJson activationNotice = getActivationNotice();
		grantCoupon(userId, activationNotice);
		sendUserScore(userId, activationNotice);
		activateUser(userId, importUser);
		return JsonResultCode.CODE_SUCCESS;
	}

	/**
	 * 赠送积分
	 *
	 * @param userId
	 * @param activationNotice
	 */
	private void sendUserScore(Integer userId, SetNoticeJson activationNotice) {
		logger().info("赠送积分");
		String score = activationNotice.getScore();
		if (StringUtils.isNotEmpty(score)) {
			ScoreParam param = new ScoreParam();
			// 写积分
			param.setScore(Integer.valueOf(score));
			param.setDesc("user_activate_score");
			param.setRemarkCode(RemarkTemplate.ADMIN_USER_IMPORT.code);
			param.setUserId(userId);
			param.setRemarkData(score);
			try {
				scoreService.updateMemberScore(param, 0, RecordTradeEnum.USER_IMPORT.val(),
						RecordTradeEnum.UACCOUNT_RECHARGE.val());
			} catch (MpException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 发放优惠券
	 *
	 * @param userId
	 */
	private void grantCoupon(Integer userId, SetNoticeJson activationNotice) {
		logger().info("发放优惠券");
		List<Integer> userIds = new ArrayList<Integer>();
		userIds.add(userId);
		String mrkingVoucherId = activationNotice.getMrkingVoucherId();
		if (StringUtils.isEmpty(mrkingVoucherId)) {
			return;
		}
		String[] split = mrkingVoucherId.split(",");
		List<String> list = new ArrayList<String>();
		for (String string : split) {
			Byte couponGetStatus = couponMpService.couponGetStatus(new MpGetCouponParam(Integer.valueOf(string), userId));
			if (Objects.equals(couponGetStatus, BYTE_ZERO)) {
				list.add(string);
			} else {
				logger().info("优惠券" + string + "状态：" + couponGetStatus);
			}
		}
		String[] array = list.toArray(new String[0]);
		CouponGiveQueueParam newParam = new CouponGiveQueueParam(getShopId(),userIds, 0, array, BaseConstant.ACCESS_MODE_ISSUE,
				BaseConstant.GET_SOURCE_ACT);
		saas.taskJobMainService.dispatchImmediately(newParam, CouponGiveQueueParam.class.getName(), getShopId(),
				TaskJobsConstant.TaskJobEnum.GIVE_COUPON.getExecutionType());
	}

    /**
     * 激活用户
     * @param userId
     * @param importUser
     */
	public void activateUser(Integer userId, UserImportDetailRecord importUser) {
		logger().info("激活用户");
		String cardId = importUser.getCardId();
		List<Integer> userIdList = new ArrayList<Integer>();
		userIdList.add(userId);
		logger().info("判断分配会员卡");
		if (StringUtils.isNotEmpty(cardId)) {
			logger().info("分配会员卡");
			String[] split = cardId.split(",");
			List<Integer> cardIdList = new ArrayList<Integer>();
			for (String string : split) {
				cardIdList.add(Integer.valueOf(string));
			}
			AddMemberCardParam param = new AddMemberCardParam(userIdList, cardIdList);
			logger().info("激活用户" + userId + "分配会员卡");
			cardService.addCardForMember(param);
		}
		logger().info("判断打标签");
		Integer tagId = importUser.getTagId();
		if (tagId != null) {
			logger().info("打标签" + tagId);
			scoreService.member.addUserTag(tagId, userId);
		}
		logger().info("判断原积分导入");
		Integer score = importUser.getScore();
		Byte userAction = importUser.getUserAction();
		if (score != null && !Objects.equals(userAction, BYTE_TWO)) {
			logger().info("原积分导入");
			ScoreParam param = new ScoreParam();
			param.setScore(score);
			param.setScoreStatus(BYTE_ZERO);
			param.setDesc("user_activate_score");
			param.setRemarkCode(RemarkTemplate.ADMIN_USER_ACTIVATE.code);
			param.setUserId(userId);
			param.setRemarkData(String.valueOf(score));
			try {
				scoreService.updateMemberScore(param, 0, RecordTradeEnum.USER_IMPORT.val(),
						RecordTradeEnum.UACCOUNT_RECHARGE.val());
			} catch (MpException e) {
				logger().info("userId" + userId + "激活用户消耗积分异常");
				logger().info(e.getMessage(), e);
			}
		}
		logger().info("判断邀请人手机号处理");
		String inviteUserMobile = importUser.getInviteUserMobile();
		UserRecord userRecord = userService.getUserByUserId(userId);
		UserDetailRecord userDetail = userService.getUserDetail(userId);
		if (StringUtils.isNotEmpty(inviteUserMobile)) {
			logger().info("邀请人手机号处理");
			UserRecord inviteUser = userService.getUserByMobile(inviteUserMobile);
			DistributionParam cfg = configService.distributionCfg.getDistributionCfg();
			if (cfg != null && inviteUser != null && !userId.equals(inviteUser.getUserId())
					&& cfg.getStatus().equals(BYTE_ONE) && !inviteUser.getInviteId().equals(userId)) {
				logger().info("处理邀请人");
				userRecord.setInviteId(inviteUser.getUserId());
				userRecord.setInviteTime(DateUtils.getSqlTimestamp());
				Byte vaild = cfg.getVaild();
				Timestamp time = vaild > BYTE_ZERO
						? DateUtils.getTimeStampPlus(Integer.valueOf(vaild) - 1, ChronoUnit.DAYS)
						: null;
				if (time != null) {
					userRecord.setInviteExpiryDate(new Date(time.getTime()));
				}
				Byte protectDate = cfg.getProtectDate();
				time = vaild > BYTE_ZERO ? DateUtils.getTimeStampPlus(Integer.valueOf(protectDate) - 1, ChronoUnit.DAYS)
						: null;
				if (time != null) {
					userRecord.setInviteProtectDate(Util.currentTimeStamp());
				}
				int update = userRecord.update();
				logger().info("更新" + update);

				// TODO 返利
//                $shop->rebate->updateTotalFanli($inviteUser->user_id);
//                if ($user->invite_id) {
//                    $shop->rebate->updateTotalFanli($user->invite_id);
//                }
			}
		}
		String name = importUser.getName();
		logger().info("判断realName姓名" + name);
		String realName = userDetail.getRealName();
		if (StringUtils.isNotEmpty(name) && StringUtils.isEmpty(realName)) {
			logger().info("更新realName" + name);
			userDetail.setRealName(name);
		}
		String sex = importUser.getSex();
		String sex2 = userDetail.getSex();
		ShopRecord shopById = saas.shop.getShopById(getShopId());
		String lang = shopById.getShopLanguage();
		logger().info("店铺语言" + lang);
		if (StringUtils.isNotEmpty(sex) && StringUtils.isEmpty(sex2)) {
			// userDetail.setSex(value);
			String sex3 = MemberSexEnum.getByName(sex, lang);
			if (sex3 != null) {
				logger().info("更新性别" + sex3);
				userDetail.setSex(sex3);
			}
		}

		String nickName = importUser.getNickName();
		String username = userDetail.getUsername();
		logger().info("判断昵称" + nickName);
		if (StringUtils.isNotEmpty(nickName) && StringUtils.isEmpty(username)) {
			logger().info("更新昵称" + nickName);
			userDetail.setUsername(nickName);
		}
		String userGrade = importUser.getUserGrade();
		if (StringUtils.isNotEmpty(userGrade)) {
			logger().info("升级用户");
			scoreService.userCardService.checkUserGradeCard(userId, userGrade);
		}
		String birthday = importUser.getBirthday();
		Integer birthdayYear = userDetail.getBirthdayYear();
		logger().info("判断是否更新生日");
		if (StringUtils.isNotEmpty(birthday) && birthdayYear == null) {
			logger().info("更新生日");
			String[] birthdays = birthday.split("/");
			if(birthdays.length==1) {
				birthdays=birthday.split("-");
			}
			userDetail.setBirthdayYear(Integer.valueOf(birthdays[0]));
			userDetail.setBirthdayMonth(Integer.valueOf(birthdays[1]));
			userDetail.setBirthdayDay(Integer.valueOf(birthdays[2]));
		}
		//省市区只判定了中文
		String provinceName = importUser.getProvince();
		String cityName = importUser.getCity();
		String districtName = importUser.getDistrict();
		logger().info("判断是否更新省市区" + provinceName + cityName + districtName);
		if (StringUtils.isNotEmpty(provinceName) && StringUtils.isNotEmpty(cityName) && StringUtils.isNotEmpty(districtName)) {
			logger().info("更新省市区");
			Integer provinceId=110000;
			//省
			DictProvinceRecord provinceRecord = saas.region.province.getProvinceName(provinceName);
			if (provinceRecord != null) {
				provinceId = provinceRecord.getProvinceId();
				if (provinceId < 100000) {
					DictProvinceRecord provinceName2 = saas.region.province.getProvinceName(provinceName.substring(0, 2));
					if (provinceName2 != null) {
						provinceId = provinceName2.getProvinceId();
						provinceId = provinceId < 100000 ? 110000 : provinceId;
					}
				}
			}
			//市
			Integer cityId=110100;
			DictCityRecord cityRecord = saas.region.city.getCityId(cityName, provinceId);
			if(cityRecord!=null) {
				cityId=cityRecord.getCityId();
			}

			//地区
			Integer districtId=110101;
			DictDistrictRecord districtRecord = saas.region.district.getDistrictName(districtName, cityId);
			if(districtRecord!=null) {
				districtId=districtRecord.getDistrictId();
			}
			userDetail.setProvinceCode(provinceId);
			userDetail.setCityCode(cityId);
			userDetail.setDistrictCode(districtId);
		}

		BigDecimal income = importUser.getIncome();
		logger().info("判断收入");
		if (income != null) {
			logger().info("更新收入");
			// userDetail.setMonthlyIncome(value);
			if (Objects.equals(income.compareTo(new BigDecimal("2000")), -1)) {
				userDetail.setMonthlyIncome(BYTE_ONE);
			}
			if (Objects.equals(income.compareTo(new BigDecimal("4000")), -1)) {
				userDetail.setMonthlyIncome(BYTE_TWO);
			}
			if (Objects.equals(income.compareTo(new BigDecimal("6000")), -1)) {
				userDetail.setMonthlyIncome(BYTE_THREE);
			}
			if (Objects.equals(income.compareTo(new BigDecimal("8000")), -1)) {
				userDetail.setMonthlyIncome(BYTE_FOUR);
			} else {
				userDetail.setMonthlyIncome(BYTE_FIVE);
			}
		}

		Integer groupId = importUser.getGroupId();
		logger().info("判断groupId");
		if (groupId != null) {
			logger().info("更新groupId");
			userRecord.setIsDistributor(BYTE_ONE);
			userRecord.setInviteGroup(groupId);
		}
		Byte isDistributor = importUser.getIsDistributor();
		logger().info("判断是否是分销员" + isDistributor);
		if (Objects.equals(isDistributor, BYTE_ONE)) {
			logger().info("更新分销员" + isDistributor);
			userRecord.setIsDistributor(BYTE_ONE);
		}

		String marriage = importUser.getMarriage();
		logger().info("判断婚姻" + marriage);
		if (StringUtils.isNotEmpty(marriage)) {
			Integer mem = MemberMarriageEnum.getByName(marriage, lang);
			if (mem != null) {
				logger().info("更新婚姻" + mem);
				userDetail.setMaritalStatus(Byte.valueOf(String.valueOf(mem)));
			}
		}

		String idNumber = importUser.getIdNumber();
		logger().info("判断身份证号" + idNumber);
		if (StringUtils.isNotEmpty(idNumber)) {
			logger().info("更新身份证号" + idNumber);
			userDetail.setCid(idNumber);
		}

		String education = importUser.getEducation();
		logger().info("判断教育程度" + education);
		if (StringUtils.isNotBlank(education)) {
			Integer edu = MemberEducationEnum.getByName(education, lang);
			if (edu != null) {
				logger().info("更新" + edu);
				userDetail.setEducation(Byte.valueOf(String.valueOf(edu)));
			}
		}

		String industry = importUser.getIndustry();
		logger().info("判断行业" + industry);
		if (StringUtils.isNotEmpty(industry)) {
			Integer ind = MemberIndustryEnum.getByName(industry, lang);
			if (ind != null) {
				logger().info("更新行业" + ind);
				userDetail.setIndustryInfo(Byte.valueOf(String.valueOf(ind)));
			}
		}
		int update = userRecord.update();
		logger().info("user更新" + update);
		int update2 = userDetail.update();
		logger().info("userDetail更新" + update2);
	}
}
