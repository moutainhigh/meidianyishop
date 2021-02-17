package com.meidianyi.shop.dao.shop.doctor;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.pojo.shop.table.DoctorCommentDo;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.DoctorCommentRecord;
import com.meidianyi.shop.service.pojo.shop.doctor.comment.DoctorCommentAddParam;
import com.meidianyi.shop.service.pojo.shop.doctor.comment.DoctorCommentConstant;
import com.meidianyi.shop.service.pojo.shop.doctor.comment.DoctorCommentListParam;
import com.meidianyi.shop.service.pojo.shop.doctor.comment.DoctorCommentListVo;
import org.elasticsearch.common.Strings;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Objects;

import static com.meidianyi.shop.db.shop.tables.DoctorComment.DOCTOR_COMMENT;

/**
 * @author 孔德成
 * @date 2020/8/12 15:28
 */
@Repository
public class DoctorCommentDao extends ShopBaseDao {

    /**
     * 创建时间
     */
    private final static String  CREATE_TIME ="createTime";


    /**
     * 保存评价
     * @param param
     */
    public Integer save(DoctorCommentAddParam param) {
        DoctorCommentRecord record = db().newRecord(DOCTOR_COMMENT, param);
        return record.insert();
    }

    /**
     * 更新
     * @param commentDo
     */
    public void  update(DoctorCommentDo commentDo){
        DoctorCommentRecord record = db().newRecord(DOCTOR_COMMENT, commentDo);
        record.update();
    }

    /**
     * 医师评价
     * @param imSessionId
     * @return
     */
    public DoctorCommentDo getByImSessionId(Integer imSessionId){
      return  db().selectFrom(DOCTOR_COMMENT)
                .where(DOCTOR_COMMENT.IM_SESSION_ID.eq(imSessionId))
                .fetchAnyInto(DoctorCommentDo.class);
    }

    /**
     * 医师评价列表
     * @param param
     * @return
     */
    public PageResult<DoctorCommentListVo> listDoctorComment(DoctorCommentListParam param) {
        SelectJoinStep<Record> records = db().select().from(DOCTOR_COMMENT);
        if (param.getDoctorId() != null) {
            records.where(DOCTOR_COMMENT.DOCTOR_ID.eq(param.getDoctorId()));
        }
        if (!Strings.isEmpty(param.getDoctorCode())) {
            records.where(DOCTOR_COMMENT.DOCTOR_CODE.eq(param.getDoctorCode()));
        }
        if (param.getDoctorName()!=null&&param.getDoctorName().trim().length()>0){
            records.where(DOCTOR_COMMENT.DOCTOR_NAME.like(likeValue(param.getDoctorName())));
        }
        if (param.getStars() != null && param.getStars() > 0) {
            records.where(DOCTOR_COMMENT.STARS.eq(param.getStars()));
        }
        if (param.getAuditStatus() != null) {
            records.where(DOCTOR_COMMENT.AUDIT_STATUS.eq(param.getAuditStatus()));
        }
         if (param.getUserId()!=null) {
             records.where(DOCTOR_COMMENT.AUDIT_STATUS.eq(DoctorCommentConstant.CHECK_COMMENT_PASS).or(DOCTOR_COMMENT.USER_ID.eq(param.getUserId())));
         }
        if (!Objects.equals(BaseConstant.YES,param.getHasDelete())){
            records.where(DOCTOR_COMMENT.IS_DELETE.eq(DelFlag.NORMAL_VALUE));
        }
        if (param.getSort()==null||Strings.isEmpty(param.getSort().trim())){
            records.orderBy(DOCTOR_COMMENT.TOP.desc(), DOCTOR_COMMENT.CREATE_TIME.desc());
        }else if (CREATE_TIME.equals(param.getSort())){
            records.orderBy(DOCTOR_COMMENT.CREATE_TIME.desc());
        }
        return getPageResult(records, param, DoctorCommentListVo.class);
    }

    /**
     * 获取医师的评价
     * @param doctorId
     * @return
     */
    public BigDecimal getAvgCommentStar(Integer doctorId) {
        return db().select(DSL.avg(DOCTOR_COMMENT.STARS)).from(DOCTOR_COMMENT)
                .where(DOCTOR_COMMENT.DOCTOR_ID.eq(doctorId))
                .and(DOCTOR_COMMENT.AUDIT_STATUS.eq(DoctorCommentConstant.CHECK_COMMENT_PASS))
                .and(DOCTOR_COMMENT.IS_DELETE.eq(DelFlag.NORMAL_VALUE))
                .fetchAnyInto(BigDecimal.class);
    }


    /**
     * 置顶
     * @param id
     */
    public void topComment(Integer id) {
        db().update(DOCTOR_COMMENT).set(DOCTOR_COMMENT.IS_DELETE,DelFlag.DISABLE_VALUE).where(DOCTOR_COMMENT.ID.eq(id)).execute();
    }

    public void deleteById(Integer id) {
        db().update(DOCTOR_COMMENT).set(DOCTOR_COMMENT.IS_DELETE,DelFlag.DISABLE_VALUE).where(DOCTOR_COMMENT.ID.eq(id)).execute();
    }

    /**
     * 置顶
     * @param id
     */
    public void updateTopComment(Integer id,Integer top) {
        db().update(DOCTOR_COMMENT).set(DOCTOR_COMMENT.TOP,top).where(DOCTOR_COMMENT.ID.eq(id)).execute();
    }

    public Integer getTop() {
       return db().select(DOCTOR_COMMENT.TOP).from(DOCTOR_COMMENT).orderBy(DOCTOR_COMMENT.TOP.desc()).fetchAny(DOCTOR_COMMENT.TOP);
    }

    public void updateAudit(Integer id, Byte status) {
        db().update(DOCTOR_COMMENT).set(DOCTOR_COMMENT.AUDIT_STATUS,status).where(DOCTOR_COMMENT.ID.eq(id)).execute();

    }
}
