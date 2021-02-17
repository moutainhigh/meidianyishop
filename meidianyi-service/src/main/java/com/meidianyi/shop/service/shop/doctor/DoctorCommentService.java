package com.meidianyi.shop.service.shop.doctor;

import cn.hutool.core.date.DateUtil;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.pojo.shop.table.DoctorCommentDo;
import com.meidianyi.shop.common.pojo.shop.table.DoctorCommentReplyDo;
import com.meidianyi.shop.common.pojo.shop.table.InquiryOrderDo;
import com.meidianyi.shop.common.pojo.shop.table.UserDo;
import com.meidianyi.shop.dao.shop.user.UserDao;
import com.meidianyi.shop.dao.shop.doctor.DoctorCommentDao;
import com.meidianyi.shop.dao.shop.doctor.DoctorCommentReplyDao;
import com.meidianyi.shop.dao.shop.doctor.DoctorDao;
import com.meidianyi.shop.dao.shop.order.InquiryOrderDao;
import com.meidianyi.shop.dao.shop.patient.PatientDao;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorOneParam;
import com.meidianyi.shop.service.pojo.shop.doctor.DoctorSortParam;
import com.meidianyi.shop.service.pojo.shop.doctor.comment.DoctorCommentAddParam;
import com.meidianyi.shop.service.pojo.shop.doctor.comment.DoctorCommentConstant;
import com.meidianyi.shop.service.pojo.shop.doctor.comment.DoctorCommentListParam;
import com.meidianyi.shop.service.pojo.shop.doctor.comment.DoctorCommentListVo;
import com.meidianyi.shop.service.pojo.shop.doctor.comment.reply.DoctorCommentReplyAddParam;
import com.meidianyi.shop.service.pojo.shop.patient.PatientOneParam;
import com.meidianyi.shop.service.shop.config.DoctorCommentAutoAuditConfigService;
import com.meidianyi.shop.service.shop.im.ImSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 添加医师评价
 * @author 孔德成
 * @date 2020/8/12 15:20
 */
@Service
public class DoctorCommentService extends ShopBaseService {

    @Autowired
    private DoctorCommentDao doctorCommentDao;
    @Autowired
    private DoctorCommentReplyDao doctorCommentReplyDao;
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private DoctorDao doctorDao;
    @Autowired
    private ImSessionService imSessionService;
    @Autowired
    private InquiryOrderDao inquiryOrderDao;
    @Autowired
    private DoctorCommentAutoAuditConfigService doctorCommentAutoAuditConfigService;
    @Autowired
    private PatientDao patientDao;
    @Autowired
    private UserDao userDao;


    /**
     * 添加评价
     * @param param
     */
    public void addComment(DoctorCommentAddParam param){
        //保存
        DoctorCommentDo doctorCommentDo = doctorCommentDao.getByImSessionId(param.getImSessionId());
        if (doctorCommentDo!=null){
            Integer autoAudit = doctorCommentAutoAuditConfigService.get();
            DoctorCommentDo commentDo =new DoctorCommentDo();
            if (Objects.equals(autoAudit.byteValue(), BaseConstant.YES)){
                commentDo.setAuditStatus(DoctorCommentConstant.CHECK_COMMENT_PASS);
            }else {
                commentDo.setAuditStatus(DoctorCommentConstant.CHECK_COMMENT_NOT_CHECK);
            }
            commentDo.setId(doctorCommentDo.getId());
            commentDo.setCreateTime(DateUtil.date().toTimestamp());
            commentDo.setCommNote(param.getCommNote());
            commentDo.setIsAnonymou(param.getIsAnonymou());
            commentDo.setStars(param.getStars());
            doctorCommentDao.update(commentDo);
            //更新会话
            imSessionService.updateSessionEvaluateStatusToAlready(param.getImSessionId());
        }else {
            param.setAuditStatus(DoctorCommentConstant.CHECK_COMMENT_PASS);
            PatientOneParam oneInfo = patientDao.getOneInfo(param.getPatientId());
            param.setPatientName(oneInfo.getName());
            DoctorOneParam doctorInfo = doctorDao.getOneInfo(param.getDoctorId());
            param.setDoctorName(doctorInfo.getName());
            InquiryOrderDo inquiryOrder = inquiryOrderDao.getByOrderSn(param.getOrderSn());
            param.setOrderId(inquiryOrder.getOrderId());
            param.setDoctorCode(doctorInfo.getHospitalCode());
            doctorCommentDao.save(param);
        }
        //更新医师评价
        BigDecimal avgCommentStar = doctorCommentDao.getAvgCommentStar(param.getDoctorId());
        DoctorSortParam param1 =new DoctorSortParam();
        param1.setAvgCommentStar(Optional.ofNullable(avgCommentStar).orElse(BigDecimal.ZERO));
        param1.setDoctorId(param.getDoctorId());
        doctorService.updateAvgCommentStar(param1);
    }

    /**
     * 添加评价
     */
    public void addDefaultComment(Integer doctorId,Integer userId,Integer patientId,String orderSn,Integer imSessionId)  {
        //保存
        PatientOneParam oneInfo = patientDao.getOneInfo(patientId);
        UserDo userDo = userDao.getUserById(userId);
        DoctorCommentAddParam param =new DoctorCommentAddParam();
        param.setAuditStatus(DoctorCommentConstant.CHECK_COMMENT_PASS);
        param.setDoctorId(doctorId);
        param.setUserId(userId);
        param.setPatientId(patientId);
        param.setIsAnonymou(BaseConstant.YES);
        param.setUserName(userDo.getUsername());
        param.setOrderSn(orderSn);
        param.setCommNote(DoctorCommentConstant.DOCTOR_DEFAULT_COMMENT);
        param.setImSessionId(imSessionId);
        param.setAuditStatus(DoctorCommentConstant.CHECK_COMMENT_PASS);
        param.setPatientName(oneInfo.getName());
        DoctorOneParam doctorInfo = doctorDao.getOneInfo(param.getDoctorId());
        param.setDoctorName(doctorInfo.getName());
        InquiryOrderDo inquiryOrder = inquiryOrderDao.getByOrderSn(param.getOrderSn());
        param.setOrderId(inquiryOrder.getOrderId());
        param.setDoctorCode(doctorInfo.getHospitalCode());
        doctorCommentDao.save(param);
        //更新医师评价
        BigDecimal avgCommentStar = doctorCommentDao.getAvgCommentStar(param.getDoctorId());
        DoctorSortParam param1 =new DoctorSortParam();
        param1.setAvgCommentStar(Optional.ofNullable(avgCommentStar).orElse(BigDecimal.ZERO));
        param1.setDoctorId(param.getDoctorId());
        doctorService.updateAvgCommentStar(param1);
    }

    /**
     * 医师评价列表
     * @param param
     * @return
     */
    public PageResult<DoctorCommentListVo> listDoctorComment(DoctorCommentListParam param) {
        PageResult<DoctorCommentListVo> pageResult = doctorCommentDao.listDoctorComment(param);
        List<Integer> ids =new ArrayList<>();
        pageResult.getDataList().forEach(item->{
            item.setCommNoteLength(item.getCommNote().length());
            if (BaseConstant.YES.equals(item.getIsAnonymou())&&! BaseConstant.YES.equals(param.getHasDelete())){
                item.setUserName(DoctorCommentConstant.DOCTOR_COMMENT_ANONYMOU_NAME);
            }
            ids.add(item.getId());
        });
        Map<Integer, List<DoctorCommentReplyDo>> map = doctorCommentReplyDao.mapDoctorReplyByIds(ids);
        pageResult.getDataList().forEach(item->{
            item.setReplylist(map.get(item.getId()));
        });
        return pageResult;
    }

    /**
     * 添加医师回复
     * @param param
     * @return
     */
    public DoctorCommentReplyDo addCommentReply(DoctorCommentReplyAddParam param) {
      return  doctorCommentReplyDao.save(param);
    }

    public void deleteCommentReply(Integer id) {
        doctorCommentReplyDao.deleteById(id);
    }

    public void deleteComment(Integer id) {
        doctorCommentDao.deleteById(id);
    }

    public void topComment(Integer id) {
        Integer top = doctorCommentDao.getTop();
        doctorCommentDao.updateTopComment(id,top+1);
    }

    public void unTopComment(Integer id) {
        doctorCommentDao.updateTopComment(id,0);
    }

    public void auditComment(Integer id, Byte status) {
        doctorCommentDao.updateAudit(id,status);
    }
}
