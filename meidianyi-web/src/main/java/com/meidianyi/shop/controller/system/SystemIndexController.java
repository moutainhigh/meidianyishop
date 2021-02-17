package com.meidianyi.shop.controller.system;


import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.saas.question.param.FeedBackParam;
import com.meidianyi.shop.service.saas.index.param.ShopViewParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * @author 新国
 *
 */
@RestController
public class SystemIndexController extends SystemBaseController {

    /**
     * system概览-概览
     * @param param {@link ShopViewParam}
     * @return {@link JsonResult}
     */
    @PostMapping("/api/system/index/shopView")
    public JsonResult shopView(@RequestBody ShopViewParam param){
        return success(saas.shopViewService.getShopViewData(param));
    }

    /**
     * system概览-问题反馈-列表
     * @param param {@link FeedBackParam}
     * @return {@link JsonResult}
     */
    @PostMapping("/api/system/index/feedback/list")
    public JsonResult feedbackList(@RequestBody FeedBackParam param){
        return success(saas.questionService.getPageByParam(param));
    }
    /**
     * system概览-问题反馈-详情
     * @param id 问题反馈id
     * @return {@link JsonResult}
     */
    @PostMapping("/api/system/index/feedback/detail")
    public JsonResult feedbackList(@RequestBody Integer id){
        return success(saas.questionService.detail(id));
    }
}
