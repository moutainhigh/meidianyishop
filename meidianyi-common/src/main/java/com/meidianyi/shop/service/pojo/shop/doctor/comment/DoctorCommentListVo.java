package com.meidianyi.shop.service.pojo.shop.doctor.comment;

import com.meidianyi.shop.common.pojo.shop.table.DoctorCommentReplyDo;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * 医师评价
 * 医师留言
 *
 * @author 孔德成
 * @date 2020/8/12 15:50
 */
@Data
public class DoctorCommentListVo {

    private Integer id;
    private String userName;
    private Integer userId;
    private Integer patientId;
    private String patientName;
    private Integer imSessionId;
    private Integer doctorId;
    private String doctorName;
    private String doctorCode;
    private Byte stars;
    private Byte isAnonymou;
    private String commNote;
    private Integer commNoteLength;
    private String orderSn;
    private Integer orderId;
    private Integer top;
    private Byte auditStatus;
    private Byte isDelete;
    private Timestamp createTime;
    private List<DoctorCommentReplyDo> replylist;
}
