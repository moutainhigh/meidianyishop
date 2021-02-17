package com.meidianyi.shop.controller.admin;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.pojo.shop.market.groupdraw.analysis.GroupDrawAnalysisParam;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meidianyi.shop.service.pojo.shop.market.groupdraw.GroupDrawAddParam;
import com.meidianyi.shop.service.pojo.shop.market.groupdraw.GroupDrawListParam;
import com.meidianyi.shop.service.pojo.shop.market.groupdraw.GroupDrawShareParam;
import com.meidianyi.shop.service.pojo.shop.market.groupdraw.GroupDrawUpdateParam;
import com.meidianyi.shop.service.pojo.shop.market.groupdraw.group.GroupListParam;
import com.meidianyi.shop.service.pojo.shop.market.groupdraw.invite.InvitedUserListParam;
import com.meidianyi.shop.service.pojo.shop.market.groupdraw.join.JoinUserListParam;
import com.meidianyi.shop.service.pojo.shop.market.groupdraw.order.OrderListParam;

/**
 * 拼团抽奖
 *
 * @author 郑保乐
 */
@RestController
@RequestMapping("/api/admin/group_draw")
public class AdminGroupDrawController extends AdminBaseController {

    /**
     * 拼团抽奖列表
     */
    @PostMapping("/list")
    public JsonResult getGroupDrawPageList(@RequestBody GroupDrawListParam param) {
        return success(shop().groupDraw.getGroupDrawList(param));
    }

    /**
     * 创建活动
     */
    @PostMapping("/add")
    public JsonResult addGroupDraw(@RequestBody @Valid GroupDrawAddParam param) {
        shop().groupDraw.addGroupDraw(param);
        return success();
    }

    /**
     * 删除活动
     */
    @PostMapping("/delete/{id}")
    public JsonResult deleteGroupDraw(@PathVariable Integer id) {
        shop().groupDraw.deleteGroupDraw(id);
        return success();
    }

    /**
     * 禁用活动
     */
    @PostMapping("/disable/{id}")
    public JsonResult disableGroupDraw(@PathVariable Integer id) {
        shop().groupDraw.disableGroupDraw(id);
        return success();
    }
    /**
     * 启用活动
     */
    @PostMapping("/enable/{id}")
    public JsonResult enableGroupDraw(@PathVariable Integer id) {
        shop().groupDraw.enableGroupDraw(id);
        return success();
    }

    /**
     * 编辑活动 - 查询
     */
    @PostMapping("/detail/{id}")
    public JsonResult groupDrawDetail(@PathVariable Integer id) {
        return success(shop().groupDraw.getGroupDrawById(id));
    }

    /**
     * 编辑活动 - 更新
     */
    @PostMapping("/update")
    public JsonResult updateGroupDraw(@RequestBody @Valid GroupDrawUpdateParam param) {
        shop().groupDraw.updateGroupDraw(param);
        return success();
    }

    /**
     * 参与用户
     */
    @PostMapping("/join_user/list")
    public JsonResult getJoinUserList(@RequestBody @Valid JoinUserListParam param) {
        return success(shop().groupDraw.groupDrawUsers.getJoinUserList(param));
    }

    /**
     * 活动订单
     */
    @PostMapping("/order/list")
    public JsonResult getGroupDrawOrderList(@RequestBody @Valid OrderListParam param) {
        return success(shop().groupDraw.groupDrawOrders.getGroupDrawOrderList(param));
    }
    private static final String LANGUAGE_TYPE_EXCEL = "excel";
    /**
     * 订单详情导出表格
     */
    @PostMapping("/order/export")
    public void orderExport(@RequestBody @Valid OrderListParam param,HttpServletResponse response) {
        Workbook workbook = shop().groupDraw.groupDrawOrders.orderExport(param,getLang());
        String fileName = Util.translateMessage(getLang(), JsonResultMessage.GROUP_ORDER_EXPORT, LANGUAGE_TYPE_EXCEL,LANGUAGE_TYPE_EXCEL)+ DateUtils.getLocalDateTime().toString();
        export2Excel(workbook, fileName, response);
    }
    /**
     * 新用户
     */
    @PostMapping("/invited_user/list")
    public JsonResult getInvitedUserList(@RequestBody @Valid InvitedUserListParam param) {
        return success(shop().groupDraw.groupDrawUsers.getInvitedUserList(param));
    }

    /**
     * 开团明细
     */
    @PostMapping("/group/list")
    public JsonResult getOpenGroupDetailList(@RequestBody GroupListParam param) {
        return success(shop().groupDraw.groupDrawGroups.getGroupList(param));
    }

    /**
     * 活动分享
     */
    @PostMapping("/share")
    public JsonResult getGroupDrawShare(@RequestBody GroupDrawShareParam param) throws Exception {
        return success(shop().groupDraw.getMpQrCode(param));
    }
    /**
     * 活动分享
     */
    @PostMapping("/effect")
    public JsonResult getGroupDrawEffect(@RequestBody GroupDrawAnalysisParam param)  {
        return success(shop().groupDraw.getAnalysis(param));
    }
}
