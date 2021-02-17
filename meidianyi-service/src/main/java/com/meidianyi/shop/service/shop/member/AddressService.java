package com.meidianyi.shop.service.shop.member;

import com.upyun.UpException;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.HttpsUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.TxMapLbsConfig;
import com.meidianyi.shop.db.shop.tables.records.UserAddressRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.foundation.upyun.UpYunManager;
import com.meidianyi.shop.service.pojo.shop.member.address.AddressAddParam;
import com.meidianyi.shop.service.pojo.shop.member.address.AddressCode;
import com.meidianyi.shop.service.pojo.shop.member.address.AddressDataParam;
import com.meidianyi.shop.service.pojo.shop.member.address.AddressGoodsShippingParam;
import com.meidianyi.shop.service.pojo.shop.member.address.AddressInfo;
import com.meidianyi.shop.service.pojo.shop.member.address.AddressListVo;
import com.meidianyi.shop.service.pojo.shop.member.address.AddressLocation;
import com.meidianyi.shop.service.pojo.shop.member.address.AddressParam;
import com.meidianyi.shop.service.pojo.shop.member.address.ChooseAddressVo;
import com.meidianyi.shop.service.pojo.shop.member.address.UserAddressVo;
import com.meidianyi.shop.service.pojo.shop.member.address.WxAddress;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.DeliverFeeAddressDetailVo;
import com.meidianyi.shop.service.shop.goods.GoodsDeliverTemplateService;
import com.meidianyi.shop.service.shop.goods.GoodsService;
import com.meidianyi.shop.service.shop.goods.mp.GoodsMpService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.Strings;
import org.jooq.Record1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.meidianyi.shop.db.shop.Tables.USER_ADDRESS;
import static java.lang.String.format;

/**
 * @author 黄壮壮
 * @Date: 2019年8月16日
 * @Description: 用户地址服务
 */
@Service
@Slf4j
public class AddressService extends ShopBaseService {
    private static final String QQ_MAP_GEOCODER_URL = "https://apis.map.qq.com/ws/geocoder/v1";
    public static final Integer USER_ADDRESS_MAX_COUNT=50;
    private static final Integer FILE_NUM =6;
    @Autowired
    private TxMapLbsConfig txMapLbsConfig;
    @Autowired
    private GoodsDeliverTemplateService shippingFeeTemplate;
    @Autowired
    private GoodsMpService goodsMpService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private UpYunManager  upYunManager;


    /**
     * 获取id用户的详细地址信息
     */
    public List<String> getUserAddressById(Integer userId) {
        logger().info("获取用户" + userId + "的详细地址信息");
        List<String> addressList = db().select(USER_ADDRESS.COMPLETE_ADDRESS)
            .from(USER_ADDRESS)
            .where(USER_ADDRESS.USER_ID.eq(userId))
            .fetch()
            .into(String.class);
        addressList.forEach(logger()::info);
        return addressList;
    }

    /**
     * 王帅
     * 获取地址
     *
     * @param addressId 地址id
     * @param userId    用户id
     * @return 地址
     */
    public UserAddressVo get(Integer addressId, Integer userId) {
        if (addressId == null || userId == null) {
            return null;
        }
        UserAddressVo address = db().select().from(USER_ADDRESS).where(USER_ADDRESS.ADDRESS_ID.eq(addressId).and(USER_ADDRESS.USER_ID.eq(userId))).fetchAnyInto(UserAddressVo.class);
        return address;
    }

    public UserAddressVo getefaultAddress(Integer userId) {
        if (userId == null) {
            return null;
        }
        return db().select().from(USER_ADDRESS)
                .where(USER_ADDRESS.USER_ID.eq(userId).and(USER_ADDRESS.IS_DEFAULT.eq(OrderConstant.YES)))
                .and(USER_ADDRESS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
                .fetchAnyInto(UserAddressVo.class);
    }


    /**
     * 选择地址cd
     *
     * @return
     */
    public UserAddressRecord chooseAddress(Integer userId, WxAddress wxAddress) {
        log.info("添加用户地址[userid:" + userId + "]" + Util.toJson(wxAddress));
        UserAddressRecord  userAddress = addWxAddress(userId, wxAddress);
        return userAddress;
    }


    /**
     * 根据微信地图定位获取库中的区县code
     *      *
     * @param addressInfo 微信放回的地址信息
     * @return DistrictId 可以为null
     */
    public Integer getUserAddressDistrictId(AddressInfo addressInfo) {
        if (addressInfo == null) {
            return null;
        } else if (!AddressInfo.STATUS_OK.equals(addressInfo.getStatus())) {
            return null;
        }
        AddressInfo.Result.AddressComponent address = addressInfo.getResult().getAddressComponent();
        if (StringUtils.isEmpty(address.getCity())||StringUtils.isEmpty(address.getDistrict())
            ||StringUtils.isEmpty(address.getProvince())||StringUtils.isEmpty(address.getNation())){
            return null;
        }
        AddressCode addressCode = checkAndUpdateAddress(address.getProvince(), address.getCity(), address.getDistrict());
        if (addressCode==null){
            return null;
        }
        return addressCode.getDistrictCode();
    }

    /**
     * 根据微信地图定位获取库中ode
     *      *
     * @param addressInfo 微信放回的地址信息
     * @return DistrictId 可以为null
     */
    public AddressCode getUserAddress(AddressInfo addressInfo) {
        if (addressInfo == null) {
            return null;
        } else if (!AddressInfo.STATUS_OK.equals(addressInfo.getStatus())) {
            return null;
        }
        AddressInfo.Result.AddressComponent address = addressInfo.getResult().getAddressComponent();
        if (StringUtils.isEmpty(address.getCity())||StringUtils.isEmpty(address.getDistrict())
                ||StringUtils.isEmpty(address.getProvince())||StringUtils.isEmpty(address.getNation())){
            return null;
        }
        AddressCode addressCode = checkAndUpdateAddress(address.getProvince(), address.getCity(), address.getDistrict());
        if (addressCode==null){
            return null;
        }
        return addressCode;
    }

    /**
     * 添加地址信息
     *
     * @param wxAddress 微信的地址系信息
     * @return
     */
    public UserAddressRecord addWxAddress(Integer userId, WxAddress wxAddress) {
        AddressCode addressCode = checkAndUpdateAddress(wxAddress.getProvinceName(),wxAddress.getCityName(),wxAddress.getCountyName());
        UserAddressRecord address = db().newRecord(USER_ADDRESS);

        address.setProvinceCode(addressCode.getProvinceCode());

        AddressLocation addressLocation = getGeocoderAddressLocation(wxAddress.getCompleteAddress());
        if (AddressLocation.STATUS_OK.equals(addressLocation.getStatus())){
            address.setLat(addressLocation.getResult().getLocation().getLat());
            address.setLng(addressLocation.getResult().getLocation().getLng());
            address.update();
        }

        address.setProvinceName(wxAddress.getProvinceName());
        address.setCityCode(addressCode.getCityCode());
        address.setCityName(wxAddress.getCityName());
        address.setDistrictCode(addressCode.getDistrictCode());
        address.setDistrictName(wxAddress.getCountyName());
        address.setConsignee(wxAddress.getUserName());
        address.setAddressName(wxAddress.getNationalCode());
        address.setAddress(wxAddress.getDetailInfo());
        address.setCompleteAddress(wxAddress.getCompleteAddress());
        address.setZipcode(wxAddress.getPostalCode());
        address.setMobile(wxAddress.getTelNumber());
        address.setUserId(userId);
        address.insert();
        return address;
    }
    /**
     * 检查并跟新获取库中地址
     * @param provinceName 省
     * @param cityName 市
     * @param districtNmae 区县
     * @return  AddressCode 省市区的本地code 可能null
     */
    public AddressCode checkAndUpdateAddress(String provinceName,String cityName,String districtNmae){
        Integer provinceId = saas.region.province.getProvinceIdByName(provinceName);
        AddressCode addressCode =new AddressCode();
        if (provinceId == null) {
            provinceId = saas.region.province.getProvinceIdByName(provinceName.substring(0, 2));
            if (provinceId == null) {
                log.error("微信地址[province:" +provinceName + "] 在库中未找到!");
                return null;
            } else {
                log.info("根据微信地址,跟新[provinceId:" + provinceId + ",province:" + provinceName + "]");
                saas.region.province.updateProvinceName(provinceId, provinceName);
            }
        }
        Integer cityId = saas.region.city.getCityIdByNameAndProvinceId(provinceId, cityName);
        if (cityId == null) {
            log.info("新增库中没有的微信地址城市[cityName:" + cityName + "]");
            cityId = saas.region.city.addNewCity(provinceId, cityName);
        }
        Integer districtId = saas.region.district.getDistrictIdByNameAndCityId(cityId, districtNmae);
        if (districtId == null) {
            log.info("新增库中没有的微信地址区县[district:" + districtNmae + "]");
            districtId = saas.region.district.addNewDistrict(cityId, districtNmae);
        }
        addressCode.setProvinceCode(provinceId);
        addressCode.setProvinceName(provinceName);
        addressCode.setCityCode(cityId);
        addressCode.setCityName(cityName);
        addressCode.setDistrictCode(districtId);
        addressCode.setDistrictName(districtNmae);
        return addressCode;
    }
    /**
     * 地址解析获取定位
     *
     * @return
     */
    public AddressLocation getGeocoderAddressLocation(String address) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("address", address);
        param.put("key", txMapLbsConfig.getKey());
        return Util.json2Object(HttpsUtils.get(QQ_MAP_GEOCODER_URL, param, true), AddressLocation.class, true);
    }

    /**
     * 根据 经纬度获取地址
     *
     * @param lat 经度
     * @param lng 纬度
     * @return
     */
    public AddressInfo getGeocoderAddressInfo(String lat, String lng) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("location", lat + "," + lng);
        param.put("key", txMapLbsConfig.getKey());
        return Util.json2Object(HttpsUtils.get(QQ_MAP_GEOCODER_URL, param, true), AddressInfo.class, true);
    }

    public ChooseAddressVo chooseAddress(AddressParam param) {
        ChooseAddressVo userAddressVo = chooseAddress(param.getUserId(), param.getWxAddress()).into(ChooseAddressVo.class);
        if (param.getGoodsId() == null) {
            return userAddressVo;
        } else {
            Integer deliverTemplateId = goodsService.getGoodsDeliverTemplateIdById(param.getGoodsId());
            try {
                shippingFeeTemplate.getShippingFeeByTemplate(userAddressVo.getDistrictCode(), deliverTemplateId, 1, BigDecimal.ZERO, BigDecimal.ZERO);
                userAddressVo.setStatus((byte) 1);
            } catch (MpException e) {
                log.info("不能配送区域");
                userAddressVo.setStatus((byte) 2);
                userAddressVo.setMessage(Util.translateMessage(param.getLang(), e.getErrorCode().getMessage(), "message"));
                e.printStackTrace();
            }
        }
        return userAddressVo;
    }

    /**
     * 获取用户的地址信息
     * @param userId 用户id
     * @return
     */
    public AddressListVo getAddressList(Integer userId) {
        AddressListVo vo =new AddressListVo();
        List<UserAddressVo> list = db().selectFrom(USER_ADDRESS)
            .where(USER_ADDRESS.USER_ID.eq(userId))
            .and(USER_ADDRESS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
            .orderBy(USER_ADDRESS.IS_DEFAULT.desc(),USER_ADDRESS.CREATE_TIME.desc())
            .fetchInto(UserAddressVo.class);
        if (!list.isEmpty()){
            vo.setAddressList(list);
        }
        return vo;
    }

    /**
     * 获取用户地址信息
     * @param userId 用户id
     * @param addressId 地址di
     * @return
     */
    public UserAddressRecord getAddressById(Integer userId, Integer addressId) {
        return db().selectFrom(USER_ADDRESS)
            .where(USER_ADDRESS.ADDRESS_ID.eq(addressId))
            .and(USER_ADDRESS.USER_ID.eq(userId))
            .fetchOne();

    }

    /**
     * 获取默认地址
     * @param userId 用户id
     * @return 地址信息 or null
     */
    public UserAddressRecord getDefaultAddress(Integer userId){
        return db().selectFrom(USER_ADDRESS)
            .where(USER_ADDRESS.USER_ID.eq(userId))
            .and(USER_ADDRESS.IS_DEFAULT.eq(BaseConstant.YES))
            .fetchAny();
    }
    /**
     * 查询未删除的用户地址数量
     * @param userId 用户id
     * @return 地址数量
     */
    public Integer getAddressUserNotDeleteCount(Integer userId){
        Record1<Integer> fetchOne = db().selectCount().from(USER_ADDRESS)
            .where(USER_ADDRESS.USER_ID.eq(userId))
            .and(USER_ADDRESS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)).fetchOne();
        if (fetchOne!=null){
            return fetchOne.component1();
        }
        return 0;
    }

    /**
     * 添加手写地址
     * @param param 参数
     * @return 1添加成功 0失败
     */
    public int addAddress(AddressAddParam param) {
        UserAddressRecord record = db().newRecord(USER_ADDRESS, param);
        record.setCompleteAddress(param.getCompleteAddress());
        if (Strings.isNullOrEmpty(param.getLat())){
            AddressLocation location = getGeocoderAddressLocation(param.getCompleteAddress());
            if (AddressLocation.STATUS_OK.equals(location.getStatus())) {
                log.info("查询地址定位信息");
                record.setLat(location.getResult().getLocation().getLat());
                record.setLng(location.getResult().getLocation().getLng());
            }
        }
        if (param.getCityCode()==null||param.getDistrictCode()==null||param.getProvinceCode()==null){
            log.info("地址的");
            AddressCode addressCode = checkAndUpdateAddress(param.getProvinceName(), param.getCityName(), param.getDistrictName());
            record.setProvinceCode(addressCode.getProvinceCode());
            record.setCityCode(addressCode.getCityCode());
            record.setDistrictCode(addressCode.getDistrictCode());
        }

        int insert = record.insert();
        if (insert>0&&record.getIsDefault().equals(BaseConstant.YES)){
            log.info("修改默认地址");
            db().update(USER_ADDRESS)
                .set(USER_ADDRESS.IS_DEFAULT,BaseConstant.NO)
                .where(USER_ADDRESS.USER_ID.eq(param.getUserId()))
                .and(USER_ADDRESS.ADDRESS_ID.notEqual(record.getAddressId()))
                .execute();
        }
        return insert;
    }

    /**
     * 更新地址信息
     * @param param 地址信息
     * @return 1更新成功 0失败
     */
    public int updateAddress(AddressAddParam param) {
        UserAddressRecord record = db().newRecord(USER_ADDRESS, param);
        AddressLocation location = getGeocoderAddressLocation(param.getCompleteAddress());
        if (Strings.isNullOrEmpty(param.getLat())) {
            if (AddressLocation.STATUS_OK.equals(location.getStatus())) {
                record.setLat(location.getResult().getLocation().getLat());
                record.setLng(location.getResult().getLocation().getLng());
            }
        }
        if (BaseConstant.YES.equals(param.getIsDefault())){
            db().update(USER_ADDRESS)
                .set(USER_ADDRESS.IS_DEFAULT,BaseConstant.NO)
                .where(USER_ADDRESS.USER_ID.eq(param.getUserId()))
                .and(USER_ADDRESS.ADDRESS_ID.notEqual(record.getAddressId()))
                .execute();
        }
        return record.update();
    }

    /**
     * 删除地址
     * @param userId 用户id
     * @param addressId 地址di
     * @return 1成功 0失败
     */
    public int removeAddress(Integer userId, Integer addressId) {
      return db().update(USER_ADDRESS)
            .set(USER_ADDRESS.DEL_FLAG,DelFlag.DISABLE_VALUE)
            .where(USER_ADDRESS.ADDRESS_ID.eq(addressId))
            .and(USER_ADDRESS.USER_ID.eq(userId))
            .execute();
    }

    /**
     * 选中默认地址
     * @param userId 用户id
     * @param addressId 地址id
     * @return
     */
    public int defaultAddress(Integer userId, Integer addressId) {
        log.info("去除之前的默认地址");
        db().update(USER_ADDRESS)
            .set(USER_ADDRESS.IS_DEFAULT,BaseConstant.NO)
            .where(USER_ADDRESS.USER_ID.eq(userId))
            .execute();
        log.info("选中新的默认地址");
        return db().update(USER_ADDRESS)
            .set(USER_ADDRESS.IS_DEFAULT,BaseConstant.YES)
            .where(USER_ADDRESS.ADDRESS_ID.eq(addressId))
            .and(USER_ADDRESS.USER_ID.eq(userId))
            .execute();
    }

    /**
     * 通过定位获取省市区信息
     * @param lat 维度
     * @param lng 经度
     */
    public AddressCode getLocationAddressInfo(String lat, String lng) {
        AddressInfo addressInfo = getGeocoderAddressInfo(lat, lng);
        logger().debug("封装用户地址信息：{}", addressInfo);
        if (addressInfo!=null&&addressInfo.getStatus().equals(AddressInfo.STATUS_OK)){
            AddressInfo.Result.AddressComponent address = addressInfo.getResult().getAddressComponent();
            if (StringUtils.isEmpty(address.getCity())||StringUtils.isEmpty(address.getDistrict())
                ||StringUtils.isEmpty(address.getProvince())||StringUtils.isEmpty(address.getNation())){
                return null;
            }
            return checkAndUpdateAddress(address.getProvince(), address.getCity(), address.getDistrict());
        }
        return null;
    }

    /**
     * 计算邮费
     * @param param
     * @return
     */
    public DeliverFeeAddressDetailVo goodsDetailShipping(AddressGoodsShippingParam param) {
        DeliverFeeAddressDetailVo vo =new DeliverFeeAddressDetailVo();
        UserAddressVo userAddressVo = get(param.getAddressId(), param.getUserId());
        vo.setAddress(userAddressVo);
        if (userAddressVo!=null){
            try {
                BigDecimal price = shippingFeeTemplate.getShippingFeeByTemplate(userAddressVo.getDistrictCode(), param.getDeliverTemplateId(), param.getGoodsNum(), param.getGoodsPrice(), param.getGoodsWeight());
                vo.setDeliverPrice(price);
            } catch (MpException e) {
                e.printStackTrace();
                log.error("商品详情-获取邮费信息失败");
                String messages = Util.translateMessage("", e.getErrorCode().getMessage(), "messages");
                vo.setMessage(messages);
                vo.setStatus((byte)2);
            }catch (NullPointerException e1){
                e1.printStackTrace();
                vo.setMessage(Util.translateMessage("", JsonResultCode.CODE_ORDER_CALCULATE_SHIPPING_FEE_ERROR.getMessage(), "messages"));
                vo.setStatus((byte)2);
            }
        }else {
            vo.setMessage(Util.translateMessage("", JsonResultCode.CODE_ORDER_CALCULATE_SHIPPING_FEE_ERROR.getMessage(), "messages"));
            vo.setStatus((byte)2);
        }
        return vo;
    }

    /**
     * 获取json地址
     * @param param
     * @return
     */
    public List<String> getBaseJsonPath(AddressDataParam param) {
        List<String> pathList =new ArrayList<>();
        for (int i = 0; i <=FILE_NUM ; i++) {
            String relativePath = format("upload/static/address/addressData%s.json", i);
            try {
                Map<String, String> fileInfo = upYunManager.getFileInfo(relativePath);
                if (fileInfo==null){
                    String path = String.format("static/mp/address/addressData%s.json", i);
                    log.info("读取地址文件{}", path);
                    ClassPathResource resource = new ClassPathResource(path);
                    upYunManager.uploadToUpYun(relativePath,resource.getFile());
                }
                pathList.add(relativePath);
            } catch (IOException | UpException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return pathList;
    }
}
