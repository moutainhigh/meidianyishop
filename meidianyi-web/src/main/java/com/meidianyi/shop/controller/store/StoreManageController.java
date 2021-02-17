package com.meidianyi.shop.controller.store;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.jedis.JedisKeyConstant;
import com.meidianyi.shop.service.foundation.util.lock.annotation.RedisLock;
import com.meidianyi.shop.service.pojo.shop.store.group.StoreGroup;
import com.meidianyi.shop.service.pojo.shop.store.group.StoreGroupQueryParam;
import com.meidianyi.shop.service.pojo.shop.store.store.StoreListQueryParam;
import com.meidianyi.shop.service.pojo.shop.store.store.StoreParam;
import com.meidianyi.shop.service.pojo.shop.store.store.StorePojo;
import com.meidianyi.shop.service.pojo.shop.store.validated.StoreAddValidatedGroup;
import com.meidianyi.shop.service.pojo.shop.store.validated.StoreCodingCheckValidatedGroup;
import com.meidianyi.shop.service.pojo.shop.store.validated.StoreUpdateValidatedGroup;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.meidianyi.shop.common.foundation.data.BaseConstant.YES;
import static com.meidianyi.shop.service.pojo.shop.store.store.StorePojo.IS_EXIST_HOSPITAL;

/**
 * @author chenjie
 * @date 2020年08月26日
 */
@RestController
public class StoreManageController extends StoreBaseController{
    private static final String LANGUAGE_TYPE_EXCEL= "excel";

    /**
     * 门店分组-列表
     * @return
     */
    @PostMapping(value = "/api/store/store/group/list")
    public JsonResult getStoreGroupByPage(@RequestBody StoreGroupQueryParam param) {
        PageResult<StoreGroup> storeGroupPageResult = shop().store.storeGroup.getStoreGroupPageList(param);
        return success(storeGroupPageResult);
    }

    /**
     * 门店分组-全部分组
     * @return
     */
    @GetMapping(value = "/api/store/store/group/all")
    public JsonResult getAllStoreGroup() {
        return success(shop().store.storeGroup.getAllStoreGroup());
    }

    /**
     * 获取门店列表
     * @return
     */
    @PostMapping(value = "/api/store/store/list")
    public JsonResult getStorePageList(@RequestBody(required = false) StoreListQueryParam param) {
        param.setStoreIds(storeAuth.user().getStoreIds());
        return success(shop().store.getPageList(param));
    }

    /**
     * 门店-新增
     * @return
     */
    @PostMapping(value = "/api/store/store/add")
    @RedisLock(prefix = JedisKeyConstant.NotResubmit.ADD_STORE_LOCK, noResubmit = true)
    public JsonResult addStore(@RequestBody(required = true) @Validated({StoreAddValidatedGroup.class}) StorePojo store) {
        Byte addStore = shop().store.addStore(store);
        if (YES.equals(addStore)) {
            return success();
        } else if (IS_EXIST_HOSPITAL.equals(addStore)){
            return fail(JsonResultCode.CODE_IS_EXIST_HOSPITAL);
        }
        return fail();
    }

    /**
     * 门店-修改
     * @return
     */
    @PostMapping(value = "/api/store/store/update")
    public JsonResult updateStore(@RequestBody(required = true) @Validated({StoreUpdateValidatedGroup.class}) StorePojo store) {
        if(shop().store.updateStore(store)) {
            return success();
        }else {
            return fail();
        }
    }

    /**
     * 门店-批量修改
     */
    @PostMapping(value = "/api/store/store/batchupdate")
    public JsonResult batchUpdateStore(@RequestBody(required = true) @Validated({StoreUpdateValidatedGroup.class}) List<StorePojo> storeList) {
        shop().store.batchUpdateStore(storeList);
        return success();
    }

    /**
     * 门店-删除
     * @return
     */
    @PostMapping(value = "/api/store/store/del")
    public JsonResult delStore(@RequestBody(required = true) @Valid StoreParam store) {
        if(shop().store.delStore(store.getStoreId())) {
            return success();
        }else {
            return fail();
        }
    }

    /**
     * 门店-取单个门店信息
     * @return
     */
    @PostMapping(value = "/api/store/store/get")
    public JsonResult getStore(@RequestBody(required = true) @Valid StoreParam store) {
        StorePojo storeRes = shop().store.getStore(store.getStoreId());
        if(null != storeRes) {
            return success(storeRes);
        }else {
            return fail();
        }
    }

    /**
     * 检查门店编码
     * @return
     */
    @PostMapping(value = "/api/store/store/coding/check")
    public JsonResult checkStoreCoding(@RequestBody(required = true) @Validated({StoreCodingCheckValidatedGroup.class}) StorePojo store) {
        if(shop().store.checkStoreCoding(store.getPosShopId())) {
            return success();
        }else {
            return fail(JsonResultCode.CODE_POS_SHOP_ID_EXIST);
        }
    }

    /**
     * 门店分组-新增
     * @param param
     * @return
     */
    @PostMapping(value = "/api/store/store/group/add")
    public JsonResult addStoreGroup(@RequestBody StoreGroupQueryParam param) {
        boolean isExist = shop().store.storeGroup.isStoreGroupExist(param);
        if ( isExist ){
            int count = shop().store.storeGroup.insertStoreGroup(param);
            if (count > 0){
                return success();
            }else{
                return fail();
            }
        }else{
            return fail(JsonResultCode.CODE_STORE_GROUP_NAME_EXIST);
        }
    }
    /**
     * 门店分组-修改
     * @param param
     * @return
     */
    @PostMapping(value = "/store/group/update")
    public JsonResult updateStoreGroup(@RequestBody StoreGroupQueryParam param) {
        boolean isExist = shop().store.storeGroup.isStoreGroupExist(param);
        if ( isExist ){
            int count = shop().store.storeGroup.updateStoreGroup(param);
            if (count > 0){
                return success();
            }else{
                return fail();
            }
        }else{
            return fail(JsonResultCode.CODE_STORE_GROUP_NAME_EXIST);
        }
    }
    /**
     * 门店分组-删除
     * @param param
     * @return
     */
    @PostMapping(value = "/api/store/store/group/del")
    public JsonResult delStoreGroup(@RequestBody StoreGroupQueryParam param) {
        shop().store.storeGroup.deleteStoreGroup(param);
        return success();

    }

    /**
     * 获取自提和门店开关信息系
     */
    @GetMapping(value = "/api/store/store/get/config")
    public JsonResult getStoreConfig(){
        return success(shop().store.getStoreBtnConfig());
    }

}
