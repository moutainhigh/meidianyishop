package com.meidianyi.shop.service.shop.title;

import com.fasterxml.jackson.core.type.TypeReference;
import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalRequestConstant;
import com.meidianyi.shop.common.pojo.saas.api.ApiExternalRequestResult;
import com.meidianyi.shop.dao.shop.title.TitleDao;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.title.TitleExternalRequestParam;
import com.meidianyi.shop.service.pojo.shop.title.TitleFetchOneParam;
import com.meidianyi.shop.service.pojo.shop.title.TitleListParam;
import com.meidianyi.shop.service.pojo.shop.title.TitleOneParam;
import jodd.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author chenjie
 */
@Service
public class TitleService extends ShopBaseService{
    @Autowired
    protected TitleDao titleDao;
    public static final int ZERO = 0;

    public PageResult<TitleOneParam> getTitleList(TitleListParam param) {
        PageResult<TitleOneParam> titleList = titleDao.getTitleList(param);
        return titleList;
    }

    public boolean isNameExist(Integer titleId,String name) {
        boolean flag = titleDao.isNameExist(titleId,name);
        return flag;
    }

    public Integer insertTitle(TitleOneParam param) {
        titleDao.insertTitle(param);
        return param.getId();
    }

    public Integer updateTitle(TitleOneParam param) {
        TitleOneParam oldTitle = getOneInfo(param.getId());
        if (!StringUtil.isBlank(param.getName())) {
            oldTitle.setName(param.getName());
        }
        oldTitle.setFirst(param.getFirst());
        titleDao.updateTitle(oldTitle);
        return oldTitle.getId();
    }

    public TitleOneParam getOneInfo(Integer titleId){
        TitleOneParam titleInfo = titleDao.getOneInfo(titleId);
        return titleInfo;
    }

    public int deleteTitle(Integer titleId){
        int id = titleDao.deleteTitle(titleId);
        return id;
    }

    public List<TitleOneParam> listTitles(){
        List<TitleOneParam> titleList = titleDao.listTitles();
        return titleList;
    }

    public Integer getTitleByCode(String code) {
        return titleDao.getTitleByCode(code);
    }

    /**
     * 更新/新增职称
     * @param title
     */
    public void synchroTitle(TitleOneParam title) {
        if(getTitleByCode(title.getCode()) == null) {
            insertTitle(title);
        } else {
            title.setId(getTitleByCode(title.getCode()));
            updateTitle(title);
        }
    }

    public void fetchTitles(String json) {
        List<TitleFetchOneParam> titleFetchList = Util.parseJson(json, new TypeReference<List<TitleFetchOneParam>>() {
        });

        for (TitleFetchOneParam list : titleFetchList) {
            TitleOneParam title = new TitleOneParam();
            title.setName(list.getName());
            title.setCode(list.getPositionCode());
            if (list.getState() > 1) {
                title.setIsDelete((byte) 1);
            }
            synchroTitle(title);
        }
    }

    public Integer getTitleIdNew(String code) {
        if (StringUtil.isBlank(code)){
            return 0;
        }
        Integer titleId = getTitleByCode(code);
        if(titleId == null) {
            TitleOneParam titleTemp = new TitleOneParam();
            titleTemp.setCode(code);
            insertTitle(titleTemp);
            return titleTemp.getId();
        } else {
            return titleId;
        }
    }

    /**
     * 拉取职称列表
     * @return
     */
    public JsonResult fetchExternalTitles(){
        String appId = ApiExternalRequestConstant.APP_ID_HIS;
        Integer shopId = getShopId();
        String serviceName = ApiExternalRequestConstant.SERVICE_NAME_FETCH_DOCTOR_TITLE_INFOS;

        Long lastRequestTime = saas().externalRequestHistoryService.getLastRequestTime(ApiExternalRequestConstant.APP_ID_HIS, shopId, ApiExternalRequestConstant.SERVICE_NAME_FETCH_DOCTOR_TITLE_INFOS);
        TitleExternalRequestParam param =new TitleExternalRequestParam();
        param.setStartTime(null);

        ApiExternalRequestResult apiExternalRequestResult = saas().apiExternalRequestService.externalRequestGate(appId, shopId, serviceName, Util.toJson(param));

        // 数据拉取错误
        if (!ApiExternalRequestConstant.ERROR_CODE_SUCCESS.equals(apiExternalRequestResult.getError())){
            JsonResult result = new JsonResult();
            result.setError(apiExternalRequestResult.getError());
            result.setMessage(apiExternalRequestResult.getMsg());
            result.setContent(apiExternalRequestResult.getData());
            return result;
        }

        fetchTitles(apiExternalRequestResult.getData());

        return JsonResult.success();
    }

    /**
     * 小程序医师下拉职称列表
     * @return
     */
    public List<TitleOneParam> listTitlesSelect(){
        List<TitleOneParam> titleList = titleDao.listTitles();
        TitleOneParam allItem = new TitleOneParam();
        allItem.setId(0);
        allItem.setName("全部职称");
        titleList.add(0,allItem);
        return titleList;
    }

    /**
     * 获取职称名称
     * @param titleId
     * @return
     */
    public String getTitleName(Integer titleId){
        TitleOneParam titleInfo = titleDao.getOneInfo(titleId);
        return titleInfo==null ? null:titleInfo.getName();
    }
}
