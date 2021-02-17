package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.shop.message.MessageParam;
import com.meidianyi.shop.service.shop.message.UserMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 赵晓东
 * @description
 * @create 2020-07-23 16:45
 **/

@RestController
public class WxAppUserMessageController extends WxAppBaseController{

    @Autowired
    private UserMessageService messageService;

    /**
     * 消息读取状态变更
     * @param messageParam 消息id
     * @return JsonResult
     */
    @RequestMapping("/api/wxapp/message/change")
    public JsonResult changeMessageStatus(@RequestBody MessageParam messageParam) {
        return this.success(messageService.changeMessageStatus(messageParam, wxAppAuth.user().getUserId()));
    }

    /**
     * 用户删除消息
     * @param messageParam 用户消息入参
     * @return JsonResult
     */
    @RequestMapping("/api/wxapp/message/delete")
    public JsonResult deleteUserMessage(@RequestBody MessageParam messageParam){
        messageService.deleteUserMessage(messageParam.getMessageId());
        return this.success();
    }

    /**
     * 查询订单消息
     * @return JsonResult
     */
    @PostMapping("/api/wxapp/message/Order/list")
    public JsonResult orderUserMessage() {
        return this.success(messageService.selectOrderUserMessage(wxAppAuth.user().getUserId()));
    }

    /**
     * 查询会话消息
     * @return JsonResult
     */
    @PostMapping("/api/wxapp/message/chat/list")
    public JsonResult selectImSessionUserMessage() {
        return this.success(messageService.selectImSessionUserMessage(wxAppAuth.user().getUserId()));
    }

    /**
     * 用户消息首页信息展示
     * @return JsonResult
     */
    @PostMapping("/api/wxapp/message/list")
    public JsonResult selectUserMessage() {
        return this.success(messageService.selectUserMessage(wxAppAuth.user().getUserId()));
    }

    /**
     * 用户个人中心消息计数
     * @return JsonResult
     */
    @PostMapping("/api/wxapp/message/count")
    public JsonResult selectUserMessageCount() {
        return this.success(messageService.selectUserMessageCount(wxAppAuth.user().getUserId()));
    }
}
