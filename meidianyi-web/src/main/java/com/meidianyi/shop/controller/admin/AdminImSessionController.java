package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.wxapp.medical.im.param.ImSessionQueryPageListParam;
import com.meidianyi.shop.service.pojo.wxapp.medical.im.vo.ImSessionItemRenderVo;
import com.meidianyi.shop.service.shop.im.ImSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author yangpengcheng
 * @date 2020/8/12
 **/
@RestController
public class AdminImSessionController extends AdminBaseController{
    @Autowired
    private ImSessionService imSessionService;

    /**
     * 根据订单号获取历史聊天记录
     * @param param
     * @return
     */
    @PostMapping("/api/admin/im/session/history")
    public JsonResult getSessionHistory(@RequestBody ImSessionQueryPageListParam param){
        List<ImSessionItemRenderVo> pageResult=imSessionService.getSessionHistory(param);
        return success(pageResult);
    }
}
