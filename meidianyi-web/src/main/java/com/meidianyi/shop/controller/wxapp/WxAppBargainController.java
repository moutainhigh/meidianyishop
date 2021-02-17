package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.wxapp.login.WxAppSessionUser;
import com.meidianyi.shop.service.pojo.wxapp.market.bargain.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author: 王兵兵
 * @create: 2019-10-24 11:20
 **/
@RestController
public class WxAppBargainController extends WxAppBaseController {

    /**
     * 个人中心页的“我的砍价”列表
     * @param param
     * @return
     */
    @PostMapping("/api/wxapp/bargain/list")
    public JsonResult getBargainRecordList(@RequestBody @Valid BargainRecordListQueryParam param) {
        WxAppSessionUser user = wxAppAuth.user();
        return success(shop().bargain.bargainRecord.getRecordPageList(user.getUserId(),param));
    }

    /**
     * 申请砍价
     * @param param
     * @return
     */
    @PostMapping("/api/wxapp/bargain/apply")
    public JsonResult applyBargain(@RequestBody @Valid BargainApplyParam param) {
        int userId = wxAppAuth.user().getUserId();
        byte resultCode = shop().bargain.bargainRecord.canApplyBargain(userId,param);

        if(BaseConstant.ACTIVITY_STATUS_MAX_COUNT_LIMIT.equals(resultCode)){
            //该用户已经发起过对这个活动的砍价
            return success(shop().bargain.bargainRecord.getUserBargainRecordId(userId,param));
        }else if(BaseConstant.ACTIVITY_STATUS_CAN_USE.equals(resultCode)){
            //可以发起砍价，进行发起动作
            return success(shop().bargain.bargainRecord.applyBargain(userId,param));
        }else {
            //无法发起
            return success(BargainApplyVo.builder().resultCode(resultCode).build());
        }
    }

    /**
     * 砍价详情
     * @param param
     * @return
     */
    @PostMapping("/api/wxapp/bargain/info")
    public JsonResult bargainInfo(@RequestBody @Valid BargainRecordSimpleParam param) {
        WxAppSessionUser user = wxAppAuth.user();
        return success(shop().bargain.bargainRecord.getBargainInfo(user.getUserId(),param.getRecordId()));
    }

    /**
     * 帮助砍价
     * @param param
     * @return
     */
    @PostMapping("/api/wxapp/bargain/cut")
    public JsonResult getBargainCut(@RequestBody @Valid BargainRecordSimpleParam param) {
        WxAppSessionUser user = wxAppAuth.user();
        return success(shop().bargain.bargainRecord.getBargainCut(user.getUserId(),param.getRecordId()));
    }

    /**
     * 帮助砍价用户列表
     *
     * @param param
     * @return
     */
    @PostMapping("/api/wxapp/bargain/users")
    public JsonResult getRecordUserList(@RequestBody @Valid BargainUsersListParam param) {
        return success(shop().bargain.bargainRecord.bargainUser.getWxPageList(param));
    }
}
