package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.shop.doctor.comment.DoctorCommentIdParam;
import com.meidianyi.shop.service.pojo.shop.doctor.comment.DoctorCommentListParam;
import com.meidianyi.shop.service.shop.config.DoctorCommentAutoAuditConfigService;
import com.meidianyi.shop.service.shop.doctor.DoctorCommentReplyService;
import com.meidianyi.shop.service.shop.doctor.DoctorCommentService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 医师评价
 *
 * @author 孔德成
 * @date 2020/8/25 16:59
 */
@Data
@RestController
public class AdminDoctorCommentController extends AdminBaseController {

    @Autowired
    private DoctorCommentAutoAuditConfigService doctorCommentAutoAuditConfigService;
    @Autowired
    private DoctorCommentService doctorCommentService;
    @Autowired
    private DoctorCommentReplyService doctorCommentReplyService;

    /**
     * 医师评价列表
     *
     * @return
     */
    @PostMapping("/api/admin/doctor/comment/list")
    public JsonResult list(@RequestBody DoctorCommentListParam param) {
        param.setHasDelete(BaseConstant.YES);
        return success(doctorCommentService.listDoctorComment(param));
    }

    /**
     * 自动审核
     *
     * @return
     */
    @PostMapping("/api/admin/doctor/comment/audit/auto")
    public JsonResult autoAudit(@RequestBody DoctorCommentIdParam param) {
        doctorCommentAutoAuditConfigService.set(param.getStatus());
        return success();
    }

    /**
     * 获取自动审核
     *
     * @return
     */
    @PostMapping("/api/admin/doctor/comment/audit/auto/get")
    public JsonResult getAutoAudit() {
        return success(doctorCommentAutoAuditConfigService.get());
    }

    /**
     * 置顶
     *
     * @return
     */
    @PostMapping("/api/admin/doctor/comment/top")
    public JsonResult top(@RequestBody DoctorCommentIdParam param) {
        if (BaseConstant.YES.equals(param.getStatus())){
            doctorCommentService.topComment(param.getId());
        }else {
            doctorCommentService.unTopComment(param.getId());
        }
        return success();
    }



    /**
     * 删除评价
     *
     * @return
     */
    @PostMapping("/api/admin/doctor/comment/delete")
    public JsonResult delete(@RequestBody DoctorCommentIdParam param) {
        doctorCommentService.deleteComment(param.getId());
        return success();
    }

    /**
     * 删除回复
     *
     * @return
     */
    @PostMapping("/api/admin/doctor/comment/reply/delete")
    public JsonResult deleteReply(@RequestBody DoctorCommentIdParam param) {
        doctorCommentService.deleteCommentReply(param.getId());
        return success();
    }

    /**
     * 审核评论
     *
     * @return
     */
    @PostMapping("/api/admin/doctor/comment/audit")
    public JsonResult audit(@RequestBody DoctorCommentIdParam param) {
        doctorCommentService.auditComment(param.getId(), param.getStatus());
        return success();
    }


}
