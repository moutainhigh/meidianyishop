package com.meidianyi.shop.controller.admin;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.shop.applets.page.PageClassificationParam;
import com.meidianyi.shop.service.pojo.shop.decoration.PageCategoryListQueryParam;
import com.meidianyi.shop.service.pojo.shop.decoration.PageClassificationPojo;


/**
 * @description 小程序管理-页面分类
 * @author lixinguo,liufei
 * @date 2019-07-03
 */
@RestController
public class AdminPageClassificationController extends AdminBaseController {
    /**
     * 添加页面分类
     * @param pageIn
     * @return
     */
    @PostMapping("/api/admin/applets/pageclassification/add")
    public JsonResult insert(@RequestBody PageClassificationParam pageIn){
        if(shop().pageClassification.checkExist(-1,pageIn.getPageName())){
            return fail(JsonResultCode.CODE_PAGE_CLASSIFICATION_EXIST);
        }
        return shop().pageClassification.addRow(pageIn.getPageName()) > 0 ? success() : fail(JsonResultCode.CODE_PAGE_CLASSIFICATION_INSERT_FAILED);
    }

    /**
     * 修改页面分类名称，id码值不变
     * @param pageIn
     * @return
     */
    @PostMapping("/api/admin/applets/pageclassification/updateCategoryName")
    public JsonResult updateCategoryName(@RequestBody PageClassificationParam pageIn){
        if(!shop().pageClassification.checkExist(pageIn.getPageId(),"")){
            return fail(JsonResultCode.CODE_PAGE_CLASSIFICATION_NOT_EXIST);
        }
        return shop().pageClassification.setName(pageIn.getPageId(),pageIn.getPageName()) > 0 ? success() : fail(JsonResultCode.CODE_PAGE_CLASSIFICATION_UPDATE_FAILED);
    }

    /**
     * 根据ID删除分类，同时将该分类下的页面重新归属于未分类类别，码值为0
     * @param pageIn
     * @return
     */
    @PostMapping("/api/admin/applets/pageclassification/deleteCategoryById")
    public JsonResult deleteCategoryById(@RequestBody PageClassificationParam pageIn){
        if(!shop().pageClassification.checkExist(pageIn.getPageId(),"")){
            return fail(JsonResultCode.CODE_PAGE_CLASSIFICATION_NOT_EXIST);
        }
        return shop().pageClassification.rmAndResetCategory(pageIn.getPageId()) ? success() : fail(JsonResultCode.CODE_PAGE_CLASSIFICATION_DELETE_FAILED);
    }

    /**
     * 页面分类分页查询，支持模糊查询
     * @param pageListParam
     * @return
     */
    @PostMapping("/api/admin/applets/pageclassification/getListByPage")
    public JsonResult getListByPage(@RequestBody  @Valid PageCategoryListQueryParam pageListParam){
        PageResult<PageClassificationPojo> pageResult = shop().pageClassification.getPageList(pageListParam);
        setSubPageCount(pageResult);
        return success(pageResult);
    }

    /**
     * 设置各个类目下包含的页面数
     * @param pageResult
     */
    public void setSubPageCount(PageResult<PageClassificationPojo> pageResult){
        for (PageClassificationPojo pojo : pageResult.dataList){
            try {
                PropertyDescriptor descriptorId = new PropertyDescriptor("id",pojo.getClass());
                Method method = descriptorId.getReadMethod();
                int pageId = (int)method.invoke(pojo);
                int subPageCount = shop().pageClassification.getPageCountByCategory(pageId);
                PropertyDescriptor desSubPageCount = new PropertyDescriptor("subPageCount",pojo.getClass());
                desSubPageCount.getWriteMethod().invoke(pojo,subPageCount);
            }catch(IntrospectionException | IllegalAccessException | InvocationTargetException e ){
                e.printStackTrace();
            }
        }
    }
}
