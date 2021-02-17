package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.shop.doctor.comment.DoctorCommentAddParam;
import com.meidianyi.shop.service.pojo.shop.doctor.comment.DoctorCommentListParam;
import com.meidianyi.shop.service.pojo.shop.doctor.comment.reply.DoctorCommentReplyAddParam;
import com.meidianyi.shop.service.pojo.shop.doctor.comment.reply.DoctorCommentReplyIdParam;
import com.meidianyi.shop.service.shop.doctor.DoctorCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 医师评价
 * @author 孔德成
 * @date 2020/8/12 14:54
 */
@RestController
public class WxAppDoctorCommentController extends WxAppBaseController {

    @Autowired
    private DoctorCommentService doctorCommentService;

    /**
     * 患者医师评价 列表
     * @return
     */
    @PostMapping("/api/wxapp/patient/doctor/comment/list")
    public JsonResult listDoctorComment(@RequestBody @Validated  DoctorCommentListParam param){
        Integer userId = wxAppAuth.user().getUserId();
        param.setUserId(userId);
        if (param.getDoctorId()==null){
            param.setDoctorId(wxAppAuth.user().getDoctorId());
        }
        return success(doctorCommentService.listDoctorComment(param));
    }

    /**
     * 添加医师评价
     * @return
     */
    @PostMapping("/api/wxapp/patient/doctor/comment/add")
    public JsonResult addComment(@RequestBody @Validated DoctorCommentAddParam param){
        param.setUserId(wxAppAuth.user().getUserId());
        param.setUserName(wxAppAuth.user().getUsername());
        doctorCommentService.addComment(param);
        return success();
    }


    /**
     * 删除医师评价
     * @return
     */
    @PostMapping("/api/wxapp/doctor/comment/delete")
    public JsonResult deleteComment(@RequestBody @Validated DoctorCommentReplyIdParam param){
        doctorCommentService.deleteComment(param.getId());
        return success();
    }


    /**
     * 添加医师评价回复
     * @return
     */
    @PostMapping("/api/wxapp/doctor/comment/reply/add")
    public JsonResult addCommentReply(@RequestBody @Validated DoctorCommentReplyAddParam param){
        return success(doctorCommentService.addCommentReply(param));
    }

    /**
     * 删除医师评价回复
     * @return
     */
    @PostMapping("/api/wxapp/doctor/comment/reply/delete")
    public JsonResult deleteCommentReply(@RequestBody @Validated DoctorCommentReplyIdParam param){
        doctorCommentService.deleteCommentReply(param.getId());
        return success();
    }
}
