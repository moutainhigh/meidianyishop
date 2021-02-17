package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.shop.tables.records.GroupBuyDefineRecord;
import com.meidianyi.shop.service.pojo.shop.image.ShareQrCodeVo;
import com.meidianyi.shop.service.pojo.shop.market.MarketOrderListParam;
import com.meidianyi.shop.service.pojo.shop.market.MarketSourceUserListParam;
import com.meidianyi.shop.service.pojo.shop.market.groupbuy.param.GroupBuyAnalysisParam;
import com.meidianyi.shop.service.pojo.shop.market.groupbuy.param.GroupBuyDetailParam;
import com.meidianyi.shop.service.pojo.shop.market.groupbuy.param.GroupBuyEditParam;
import com.meidianyi.shop.service.pojo.shop.market.groupbuy.param.GroupBuyIdParam;
import com.meidianyi.shop.service.pojo.shop.market.groupbuy.param.GroupBuyListParam;
import com.meidianyi.shop.service.pojo.shop.market.groupbuy.param.GroupBuyStatusVaild;
import com.meidianyi.shop.service.pojo.shop.market.groupbuy.vo.GroupBuyDetailListVo;
import com.meidianyi.shop.service.pojo.shop.market.groupbuy.vo.GroupBuyDetailVo;
import com.meidianyi.shop.service.pojo.shop.market.groupbuy.vo.GroupBuyParam;
import com.meidianyi.shop.service.pojo.shop.member.MemberInfoVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.sql.Timestamp;

/**
 * 团购、多人平团
 *
 * @author 孔德成
 * @date 2019/7/18 14:27
 */
@RestController
@RequestMapping("/api/admin/market/groupbuy")
public class AdminGroupBuyController extends AdminBaseController {


    /**
     * 查询团购列表
     *
     * @param param GroupBuyListParam
     * @return JsonResult
     */
    @PostMapping("/list")
    public JsonResult getListGroupBuy(@RequestBody @Valid GroupBuyListParam param) {
        return success(shop().groupBuy.getListGroupBuy(param));
    }


    /**
     * 增加拼团活动
     *
     * @param param GroupBuyParam
     * @return JsonResult
     */
    @PostMapping("/add")
    public JsonResult addGroupBuy(@RequestBody @Valid GroupBuyParam param) {
        Timestamp date = DateUtils.getLocalDateTime();
        shop().groupBuy.addGroupBuy(param);
        return success();
    }


    /**
     * 删除拼团活动
     *
     * @param param GroupBuyIdParam
     * @return  JsonResult
     */
    @PostMapping("/delete")
    public JsonResult deleteGroupBuy(@RequestBody @Valid GroupBuyIdParam param) {
        if (param.getId()==null){
            return fail(JsonResultCode.CODE_PARAM_ERROR);
        }
        shop().groupBuy.deleteGroupBuy(param.getId());
        return success();
    }


    /**
     * 修改拼团设置
     *
     * @param param GroupBuyEditParam
     * @return JsonResult
     */
    @PostMapping("/update")
    public JsonResult updateGroupBuy(@RequestBody @Valid GroupBuyEditParam param) {
        //校验参数
        if (param == null ||param.getId()==null ||
        param.getProduct() == null || param.getProduct().size() == 0 ) {
            return fail(JsonResultCode.CODE_PARAM_ERROR);
        }
        shop().groupBuy.updateGroupBuy(param);
        return success();
    }


    /**
     * 参团明细
     *
     * @param param GroupBuyIdParam
     * @return JsonResult
     */
    @PostMapping("/detail")
    public JsonResult detailGroupBuy(@RequestBody @Valid GroupBuyIdParam param) {
        if (param.getId()==null){
            return fail(JsonResultCode.CODE_PARAM_ERROR);
        }
        GroupBuyDetailVo vo = shop().groupBuy.detailGroupBuy(param.getId());
        return success(vo);
    }


    /**
     * 分享拼团
     *
     * @param param GroupBuyIdParam  活动Id
     * @return JsonResult qrCodeVo 二维码信息
     */
    @PostMapping("/share")
    public JsonResult shareGroupBuy(@RequestBody @Valid GroupBuyIdParam param) {
        ShareQrCodeVo qrCodeVo = shop().groupBuy.shareGroupBuy(param.getId());
        return success(qrCodeVo);
    }

    /**
     * 停用,启用拼团
     *
     * @param param GroupBuyIdParam
     * @return JsonResult
     */
    @PostMapping("/change/status")
    public JsonResult changeStatusActivity(@RequestBody @Validated(GroupBuyStatusVaild.class) GroupBuyIdParam param) {
        GroupBuyDefineRecord groupBuyRecord = shop().groupBuy.getGroupBuyRecord(param.getId());
        if (groupBuyRecord==null){
            return fail(JsonResultCode.CODE_PARAM_ERROR);
        }
        Byte status = groupBuyRecord.getStatus();
        if (param.getStatus().equals(status)){
            return success();
        }
        int resFlag = shop().groupBuy.changeStatusActivity(param.getId(),param.getStatus());
        if (resFlag>0){
         return success();
        }
        return fail();
    }

    /**
     * 参团明细列表
     *
     * @param param GroupBuyDetailParam
     * @return JsonResult
     */
    @PostMapping("/detail/list")
    public JsonResult detailGroupBuyList(@RequestBody @Valid GroupBuyDetailParam param) {
        PageResult<GroupBuyDetailListVo> vo = shop().groupBuy.detailGroupBuyList(param);
        return success(vo);
    }


    /**
     * 拼团订单
     * @return JsonResult
     */
    @PostMapping("/order/list")
    public JsonResult groupBuyOrderList(@RequestBody @Valid MarketOrderListParam param) {
        return success(shop().groupBuy.groupBuyOrderList(param));
    }

    /**
     * 用户列表
     *
     * @return JsonResult
     */
    @PostMapping("/user/list")
    public JsonResult groupBuyNewUserList(@RequestBody @Valid MarketSourceUserListParam param) {
        PageResult<MemberInfoVo> pageResult = shop().groupBuy.groupBuyNewUserList(param);
        return success(pageResult);
    }


    /**
     * 拼团活动效果数据
     *
     * @return JsonResult
     */
    @PostMapping("/analysis")
    public JsonResult groupBuyAnalysis(@RequestBody @Valid GroupBuyAnalysisParam param) {
        return success(shop().groupBuy.groupBuyAnalysis(param));
    }


}
