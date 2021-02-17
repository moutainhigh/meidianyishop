package com.meidianyi.shop.service.shop.goods;

import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.DeliverFeeTemplateRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.area.AreaProvinceVo;
import com.meidianyi.shop.service.pojo.shop.config.DeliverTemplateConfig;
import com.meidianyi.shop.service.pojo.shop.goods.deliver.*;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.shop.config.DeliverTemplateConfigService;
import jodd.util.StringUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.Record4;
import org.jooq.SelectSeekStep1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static com.meidianyi.shop.db.shop.Tables.DELIVER_FEE_TEMPLATE;


/**
 * 
 * 运费模版
 * 
 * @author liangchen
 * @date   2019年7月11日
 */
@Service

public class GoodsDeliverTemplateService extends ShopBaseService{

    @Autowired
    private DeliverTemplateConfigService defaultTemplate;

    /**
	 *	查询所有省、市、区、县
	 *
	 * @return List<AreaSelectVo>
	 */
	public List<AreaProvinceVo> getAllArea() {
		return saas.areaSelectService.getAllArea();
	}
	
	/**
	 * 运费模版列表
	 * 
	 * @param param flag传0:普通运费模板，传1:重量运费模板
	 * @return 分页信息
	 */
	public PageResult<GoodsDeliverTemplateVo> getDeliverTemplateList(GoodsDeliverPageListParam param) {
        //flag传0:普通运费模板， 传1:重量运费模板
		SelectSeekStep1<Record4<Integer, String, String, Byte>, Integer> selectFrom =
				db().select(DELIVER_FEE_TEMPLATE.DELIVER_TEMPLATE_ID,DELIVER_FEE_TEMPLATE.TEMPLATE_NAME,DELIVER_FEE_TEMPLATE.TEMPLATE_CONTENT,DELIVER_FEE_TEMPLATE.FLAG)
				.from(DELIVER_FEE_TEMPLATE)
				.where(DELIVER_FEE_TEMPLATE.FLAG.eq(param.getFlag()))
				.orderBy(DELIVER_FEE_TEMPLATE.DELIVER_TEMPLATE_ID.desc());
		//整合分页信息
		PageResult<GoodsDeliverTemplateVo> pageResult = this.getPageResult(selectFrom, param.getCurrentPage(), param.getPageRows(), GoodsDeliverTemplateVo.class);
        //遍历将模板内容由字符串转对象
        for (GoodsDeliverTemplateVo tempVo:pageResult.dataList) {
            GoodsDeliverTemplateContentParam templateContent = Util.json2Object(tempVo.getTemplateContent(),GoodsDeliverTemplateContentParam.class,false);
            tempVo.setContent(templateContent);
            tempVo.setTemplateContent("null");
        }
		return pageResult;
	}
	
	/**
     * 添加运费模版
     *
     * @param param 名称 类型标识 详细配置信息
     */
	public void addDeliverTemplate(GoodsDeliverTemplateParam param) {
	    //flag传0:普通运费模板， 传1:重量运费模板
        db().insertInto(DELIVER_FEE_TEMPLATE,
            DELIVER_FEE_TEMPLATE.TEMPLATE_NAME,
            DELIVER_FEE_TEMPLATE.FLAG,
            DELIVER_FEE_TEMPLATE.TEMPLATE_CONTENT)
            .values(param.getTemplateName(),param.getFlag(),Util.toJsonNotNull(param.getContentParam()))
            .execute();
	}
	
	 /**
     * 真删除运费模版
     *
     * @param goodsDeliverIdParam 模板id
     */
    public void deleteDeliverTemplate(GoodsDeliverIdParam goodsDeliverIdParam) {
         db().deleteFrom(DELIVER_FEE_TEMPLATE)
        		.where(DELIVER_FEE_TEMPLATE.DELIVER_TEMPLATE_ID.eq(goodsDeliverIdParam.getDeliverTemplateId()))
                .execute();
    }
    /**
	 * 修改模版前先查询单个模版的信息，将其参数作为修改时的默认值
	 * 
	 * @param param 模板id
	 * @return 单个模板信息
	 */
	
	public GoodsDeliverTemplateVo selectOne(GoodsDeliverIdParam param) {
        //查询单个对象
		GoodsDeliverTemplateVo result =
				db().select(DELIVER_FEE_TEMPLATE.DELIVER_TEMPLATE_ID,DELIVER_FEE_TEMPLATE.TEMPLATE_NAME,DELIVER_FEE_TEMPLATE.TEMPLATE_CONTENT,DELIVER_FEE_TEMPLATE.FLAG)
				.from(DELIVER_FEE_TEMPLATE)
				.where(DELIVER_FEE_TEMPLATE.DELIVER_TEMPLATE_ID.eq(param.getDeliverTemplateId()))
				.fetchOneInto(GoodsDeliverTemplateVo.class);
        if (result == null){
            return null;
        }
        //遍历将模板内容由字符串转对象
        GoodsDeliverTemplateContentParam templateContent = Util.json2Object(result.getTemplateContent(),GoodsDeliverTemplateContentParam.class,false);
        result.setContent(templateContent);
        result.setTemplateContent("null");

		return result;
	}
	/**
     *修改运费模版
     *
     * @param param 模板id 名称 类型 详细配置信息
     */
	public void updateDeliverTemplate(GoodsDeliverTemplateParam param) {
				
			db().update(DELIVER_FEE_TEMPLATE)
					.set(DELIVER_FEE_TEMPLATE.TEMPLATE_NAME,param.getTemplateName())
	                .set(DELIVER_FEE_TEMPLATE.TEMPLATE_CONTENT,Util.toJsonNotNull(param.getContentParam()))
	                .where(DELIVER_FEE_TEMPLATE.DELIVER_TEMPLATE_ID.eq(param.getDeliverTemplateId()))
	                .execute();
	}

	/**
	 *	运费模板下拉框
	 *
	 * @param 
	 * @return List<GoodsDeliverBoxVo>
	 */
	public List<GoodsDeliverBoxVo> getBox() {
		
		List<GoodsDeliverBoxVo> boxVo = db().select().from(DELIVER_FEE_TEMPLATE)
				.fetch().into(GoodsDeliverBoxVo.class);
		return boxVo;
	}
	/**
	 *	复制运费模板
	 *
	 * @param param 模板id
	 */
	public void copyTemplate(GoodsDeliverIdParam param) {
		// 根据id查模板信息
		DeliverFeeTemplateRecord record = db().select(DELIVER_FEE_TEMPLATE.TEMPLATE_NAME,DELIVER_FEE_TEMPLATE.TEMPLATE_CONTENT,
				DELIVER_FEE_TEMPLATE.SHOP_ID,DELIVER_FEE_TEMPLATE.FLAG)
				.from(DELIVER_FEE_TEMPLATE)
				.where(DELIVER_FEE_TEMPLATE.DELIVER_TEMPLATE_ID.eq(param.getDeliverTemplateId()))
				.fetchOneInto(DELIVER_FEE_TEMPLATE);
		// 重命名复制出来的副本
		String newName = record.getTemplateName()+" 副本";
		record.setTemplateName(newName);
		record.setTemplateContent(record.getTemplateContent());
		record.setShopId(record.getShopId());
		record.setFlag(record.getFlag());
		// 将副本信息插入为一条新的模板信息
		db().executeInsert(record);
	}

    /**
     * wx_王帅
     * get by key
     * @param id id
     * @return 模板
     */
	public GoodsDeliverTemplateVo getById(Integer id){
	    return db().select(DELIVER_FEE_TEMPLATE.DELIVER_TEMPLATE_ID,DELIVER_FEE_TEMPLATE.TEMPLATE_NAME,DELIVER_FEE_TEMPLATE.TEMPLATE_CONTENT,DELIVER_FEE_TEMPLATE.FLAG)
            .from(DELIVER_FEE_TEMPLATE)
            .where(DELIVER_FEE_TEMPLATE.DELIVER_TEMPLATE_ID.eq(id))
            .fetchOneInto(GoodsDeliverTemplateVo.class);
    }

    /**
     * 王帅
     * 默认模板
     * @return 默认模板
     */
    public DeliverTemplateConfig getDefaultTemplate(){
        String defaultTemp = defaultTemplate.getDefaultDeliverTemplate();
        return Util.parseJson(defaultTemp, DeliverTemplateConfig.class);
    }

    /**
     * 王帅
     * 默认模板计算运费
     * @param totalPrica 该模板商品总价
     * @return 运费
     */
    public BigDecimal getShippingFeeByDefaultTemplate(BigDecimal totalPrica) throws MpException {
        DeliverTemplateConfig template = getDefaultTemplate();
        logger().info("默认模板计算运费:{}", template);
        if(OrderConstant.DEFAULT_SHIPPING_FEE_TEMPLATE_UNITY.equals(template.getTemplateName())){
            //统一模板
            logger().info("统一模板");
            return template.getPrice();
        }else if(OrderConstant.DEFAULT_SHIPPING_FEE_TEMPLATE_FREE_LIMIT.equals(template.getTemplateName())){
            //满包邮
            logger().info("满包邮");
            return BigDecimalUtil.compareTo(totalPrica, template.getFeeLimit()) == -1 ? template.getPrice() : BigDecimal.ZERO;
        }else {
            throw new MpException(JsonResultCode.CODE_ORDER_CALCULATE_SHIPPING_FEE_ERROR);
        }

    }

    /**
     * 王帅
     * 通过价格和数量计算运费
     * @param districtCode 区县code
     * @param rule 规则
     * @param totalNumber 总量
     * @param totalPrice 总价
     * @return
     */
    public BigDecimal getShippingFeeByNumber(Integer districtCode, GoodsDeliverTemplateContentParam rule, Integer totalNumber, BigDecimal totalPrice) throws MpException {
        logger().info("通过价格和数量计算运费");
        if(OrderConstant.S_HAS_FEE_CONDITION_OPEN.equals(rule.getHasFee0Condition()) && CollectionUtils.isNotEmpty(rule.getFeeConditionParam())){
            //开启指定包邮条件
            for (GoodsDeliverTemplateFeeConditionParam template : rule.getFeeConditionParam()) {
                //判断地区与规则
                if(CollectionUtils.isNotEmpty(template.getAreaList()) && matchDistrictCode(districtCode, template.getAreaList()) && matchFee(template, totalNumber, totalPrice)){
                    return BigDecimal.ZERO;
                }
            }
        }
        //支持区域
        ArrayList<GoodsDeliverTemplateAreaParam> area = rule.getAreaParam();
        if(CollectionUtils.isEmpty(area)){
            throw new MpException(JsonResultCode.CODE_ORDER_CALCULATE_SHIPPING_FEE_ERROR);
        }
        for (GoodsDeliverTemplateAreaParam  supportArea : rule.getAreaParam()) {
            if(matchDistrictCode(districtCode, supportArea.getAreaList())){
                //支持区域
                if(totalNumber <= supportArea.getFirstNum()){
                    //小于等于首件
                    return supportArea.getFirstFee();
                }else{
                    //续件
                    BigDecimal add = BigDecimalUtil.multiply(
                        BigDecimalUtil.divide(BigDecimal.valueOf((totalNumber - supportArea.getFirstNum())), BigDecimal.valueOf(supportArea.getContinueNum())),
                        supportArea.getContinueFee(),
                        RoundingMode.UP
                    );
                    return BigDecimalUtil.add(add, supportArea.getFirstFee());
                }
            }
        }
        //不支持区域
        if(rule.getLimitParam() == null || OrderConstant.S_LIMIT_DELIVER_AREA_OPEN.equals(rule.getLimitParam().getLimitDeliverArea())){
            logger().error("计算运费时运费模板,未找到可配送的区域");
            throw new MpException(JsonResultCode.CODE_ORDER_CALCULATE_SHIPPING_FEE_ERROR);
        }
        GoodsDeliverTemplateLimitParam limitArea = rule.getLimitParam();
        if(totalNumber <= limitArea.getFirstNum()){
            return limitArea.getFirstFee();
        }else{
            //续件
            BigDecimal add = BigDecimalUtil.multiply(
                BigDecimalUtil.divide(BigDecimal.valueOf((totalNumber - limitArea.getFirstNum())), BigDecimal.valueOf(limitArea.getContinueNum())),
                limitArea.getContinueFee(),
                RoundingMode.UP
            );
            return BigDecimalUtil.add(add, limitArea.getFirstFee());
        }
    }

    /**
     * 王帅
     * 通过价格和重量计算运费
     * @param districtCode 区县code
     * @param rule 规则
     * @param totalWeight 重量
     * @param totalPrice 总价
     * @return
     */
    public BigDecimal getShippingFeeByWeight(Integer districtCode, GoodsDeliverTemplateContentParam rule, BigDecimal totalWeight, BigDecimal totalPrice) throws MpException {
        logger().info("通过价格和重量计算运费");
        if(BigDecimalUtil.compareTo(totalWeight , BigDecimal.ZERO) == 0){
            return BigDecimal.ZERO;
        }
        if(OrderConstant.S_HAS_FEE_CONDITION_OPEN.equals(rule.getHasFee0Condition()) && CollectionUtils.isNotEmpty(rule.getFeeConditionParam())){
            //开启指定包邮条件
            for (GoodsDeliverTemplateFeeConditionParam template : rule.getFeeConditionParam()) {
                //判断地区与规则
                if(CollectionUtils.isNotEmpty(template.getAreaList()) && matchDistrictCode(districtCode, template.getAreaList()) && matchWeightFee(template, totalWeight, totalPrice)){
                    return BigDecimal.ZERO;
                }
            }
        }
        //支持区域
        ArrayList<GoodsDeliverTemplateAreaParam> area = rule.getAreaParam();
        if(CollectionUtils.isEmpty(area)){
            throw new MpException(JsonResultCode.CODE_ORDER_CALCULATE_SHIPPING_FEE_ERROR);
        }
        for (GoodsDeliverTemplateAreaParam  supportArea : rule.getAreaParam()) {
            if(matchDistrictCode(districtCode, supportArea.getAreaList())){
            //支持区域
                if(BigDecimalUtil.compareTo(totalWeight, BigDecimal.valueOf(supportArea.getFirstNum())) < 1){
                    //不足首重，按首重运费计算
                    return supportArea.getFirstFee();
                }else{
                    //续重的运费，按续每单位运费*续重单位份数计算
                    BigDecimal add = BigDecimalUtil.multiply(
                        BigDecimalUtil.divide((
                            BigDecimalUtil.subtrac(totalWeight, BigDecimal.valueOf(supportArea.getFirstNum()))),
                            BigDecimal.valueOf(supportArea.getContinueNum())),
                        supportArea.getContinueFee(),
                        RoundingMode.UP
                    );
                    return BigDecimalUtil.add(add, supportArea.getFirstFee());
                }
            }
        }
        //不支持区域
        if(rule.getLimitParam() == null || OrderConstant.S_LIMIT_DELIVER_AREA_OPEN.equals(rule.getLimitParam().getLimitDeliverArea())){
            logger().error("计算运费时，不支持区域不存在");
            throw new MpException(JsonResultCode.CODE_ORDER_CALCULATE_SHIPPING_FEE_ERROR);
        }
        GoodsDeliverTemplateLimitParam limitArea = rule.getLimitParam();
        if(BigDecimalUtil.compareTo(totalWeight, BigDecimal.valueOf(limitArea.getFirstNum())) < 1){
            return limitArea.getFirstFee();
        }else{
            //续重的运费，按续每单位运费*续重单位份数计算
            BigDecimal add = BigDecimalUtil.multiply(
                BigDecimalUtil.divide((
                        BigDecimalUtil.subtrac(totalWeight, BigDecimal.valueOf(limitArea.getFirstNum()))),
                    BigDecimal.valueOf(limitArea.getContinueNum())),
                limitArea.getContinueFee(),
                RoundingMode.UP
            );
            return BigDecimalUtil.add(add, limitArea.getFirstFee());
        }
    }

    /**
     * 王帅
     * 计算订单商品单个运费模板运费
     * @param districtCode 区县
     * @param templateId 模板id（no null）
     * @param totalNumber 总数量
     * @param totalPrice 总价
     * @param totalWeight 总重
     * @return 运费
     * @throws MpException
     */
	public BigDecimal getShippingFeeByTemplate(Integer districtCode, Integer templateId, Integer totalNumber, BigDecimal totalPrice, BigDecimal totalWeight) throws MpException {
	    logger().info("计算订单商品单个运费模板运费");
	    if(0 == templateId.intValue()||districtCode==null){
            //默认模板
            logger().debug("使用商品默认运费信息");
            return getShippingFeeByDefaultTemplate(totalPrice);
        }
        GoodsDeliverTemplateVo template = getById(templateId);
        if(template == null){
            //默认模板
            logger().debug("无法获取指定templateId:{} 模板数据,使用商品默认运费信息",templateId);
            return getShippingFeeByDefaultTemplate(totalPrice);
        }
        logger().debug("反序列化运费模板信息");
        GoodsDeliverTemplateContentParam rule = Util.parseJson(template.getTemplateContent(), GoodsDeliverTemplateContentParam.class);
        if(OrderConstant.SHIPPING_FEE_TEMPLATE_NUMBER.equals(template.getFlag())){
            //通过价格和数量计算运费
            logger().debug("通过价格和数量计算运费");
            return getShippingFeeByNumber(districtCode, rule, totalNumber, totalPrice);
        }else{
            //通过价格和重量计算运费
            logger().debug("通过价格和重量计算运费");
            return getShippingFeeByWeight(districtCode, rule, totalWeight, totalPrice);
        }
    }

    /**
     * 王帅
     * 判断该区县是否在该地区
     * @param districtCode
     * @param areaList
     * @return boolean
     */
    public boolean matchDistrictCode(Integer districtCode, List<String> areaList){
        if(CollectionUtils.isEmpty(areaList)){
            return false;
        }
        //省
        Integer provinceCode = districtCode / 10000 * 10000;
        //市
        Integer cityCode = districtCode / 100 * 100;

        if(areaList.contains(provinceCode.toString()) || areaList.contains(cityCode.toString()) || areaList.contains(districtCode.toString())){
            return true;
        }
        return false;
    }

    /**
     * 王帅
     * 是否匹配按件运费模板包邮条件
     * @param template 模板规则
     * @param totalNumber 总数
     * @param totalPrice 总价
     * @return 是否包邮
     */
    public boolean matchFee(GoodsDeliverTemplateFeeConditionParam template, Integer totalNumber, BigDecimal totalPrice){
        if(OrderConstant.CFSFRT_NUMBER_OR_WEIGHT.equals(template.getFee0Condition()) && totalNumber >= template.getFee0Con1Num()){
            //包邮类型:件数
            return true;
        }
        if(OrderConstant.CFSFRT_AMOUNT.equals(template.getFee0Condition()) && BigDecimalUtil.compareTo(totalPrice, template.getFee0Con2Fee()) >= 0){
            //包邮类型:金额
            return true;
        }
        if(OrderConstant.CFSFRT_MIXING.equals(template.getFee0Condition()) && totalNumber > template.getFee0Con3Num() && BigDecimalUtil.compareTo(totalPrice, template.getFee0Con3Fee()) >= 0){
            //包邮类型:件数+金额
            return true;
        }
        return false;
    }

    /**
     * 王帅
     * 是否匹配重量运费模板包邮条件
     * @param template
     * @param totalPrice
     * @param totalWeight
     * @return
     */
    public boolean matchWeightFee(GoodsDeliverTemplateFeeConditionParam template, BigDecimal totalPrice, BigDecimal totalWeight){
        if(OrderConstant.CFSFRT_NUMBER_OR_WEIGHT.equals(template.getFee0Condition()) && BigDecimalUtil.compareTo(totalWeight, BigDecimal.valueOf(template.getFee0Con1Num())) < 1){
            //包邮类型:公斤
            return true;
        }
        if(OrderConstant.CFSFRT_AMOUNT.equals(template.getFee0Condition()) && BigDecimalUtil.compareTo(totalPrice, template.getFee0Con2Fee()) >= 0){
            //包邮类型:金额
            return true;
        }
        if(OrderConstant.CFSFRT_MIXING.equals(template.getFee0Condition()) && BigDecimalUtil.compareTo(totalWeight, BigDecimal.valueOf(template.getFee0Con1Num())) < 1 && BigDecimalUtil.compareTo(totalPrice, template.getFee0Con3Fee()) >= 0){
            //包邮类型:公斤+金额
            return true;
        }
        return false;
    }
}
