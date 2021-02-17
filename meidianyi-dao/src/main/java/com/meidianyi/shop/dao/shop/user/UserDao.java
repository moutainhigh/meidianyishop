package com.meidianyi.shop.dao.shop.user;

import com.meidianyi.shop.common.foundation.data.DistributionConstant;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.pojo.shop.table.UserDo;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.service.pojo.shop.user.detail.UserAssociatedDoctorParam;
import com.meidianyi.shop.service.pojo.shop.user.detail.UserAssociatedDoctorVo;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

/**
 * @Description
 * @Author 赵晓东
 * @Create 2020-07-22 16:09
 **/

import java.util.List;

import static com.meidianyi.shop.db.shop.Tables.*;
import static com.meidianyi.shop.db.shop.tables.User.USER;

/**
 * 用户
 * @author 赵晓东
 * @date 2020年08月11日
 */
@Repository
public class UserDao extends ShopBaseDao {

    /**
     * 根据用户名和手机查询用户id，再讲该用户权限设置为医师
     * @return UserDo
     */
    public Integer updateDoctorAuth(Integer userId) {
        return db().update(USER).set(USER.USER_TYPE, (byte) 1)
            .where(USER.USER_ID.eq(userId))
            .execute();
    }

    public UserDo getUserById(Integer userId){
        UserDo userDo= db().select().from(USER).where(USER.USER_ID.eq(userId)).fetchOneInto(UserDo.class);
        return userDo;
    }
    /**
     * 查询分销员等级列表下的分销员id列表
     * @param levelIds 分销员等级列表
     * @return 分销员id列表
     */
    public List<Integer> listUserIdByDistributorLevel(List<Integer> levelIds) {
        return db().select(USER.USER_ID).from(USER)
            .where(USER.IS_DISTRIBUTOR.eq(DistributionConstant.IS_DISTRIBUTOR))
            .and(USER.DISTRIBUTOR_LEVEL.in(levelIds)).fetchInto(Integer.class);
    }
    /**
     * 根据用户名和手机查询用户id，再讲该用户权限设置为医师
     * @return UserDo
     */
    public Integer updateUserDoctorAuth(Integer userId,Byte type) {
        return db().update(USER).set(USER.USER_TYPE, type)
            .where(USER.USER_ID.eq(userId))
            .execute();
    }

    /**
     * 医师解绑将用户类型设置为患者
     * @param userId
     */
    public void unbundlingUserType(Integer userId) {
        db().update(USER).set(USER.USER_TYPE, (byte)0)
            .where(USER.USER_ID.eq(userId)).execute();
    }

    /**
     * 更新用户类型
     * @param userId
     * @param userType
     */
    public void updateUserType(Integer userId,Byte userType) {
        db().update(USER).set(USER.USER_TYPE, userType)
            .where(USER.USER_ID.eq(userId)).execute();
    }

    /**
     * 查询用户关联医师
     * @param userAssociatedDoctorParam 查询关联医师入参
     * @return PageResult<UserAssociatedDoctorVo>
     */
    public PageResult<UserAssociatedDoctorVo> getUserAssociatedDoctor(UserAssociatedDoctorParam userAssociatedDoctorParam) {
        Condition condition1 = PRESCRIPTION.USER_ID.eq(userAssociatedDoctorParam.getUserId()).or(INQUIRY_ORDER.USER_ID.eq(userAssociatedDoctorParam.getUserId()));
        SelectSelectStep<? extends Record> select1 = db().select(DOCTOR.NAME.as("doctorName"),
            DOCTOR.HOSPITAL_CODE.as("doctorCode"),
            DOCTOR.ID.as("doctorId"),
            DSL.ifnull(DSL.count(USER_DOCTOR_ATTENTION.DOCTOR_ID), true).as("isFav"));
        if (userAssociatedDoctorParam.getDepartmentName() != null) {
            select1.select(DEPARTMENT.NAME.as("departmentName"));
        }
        SelectConditionStep<? extends Record> select =
            select1
            .from(DOCTOR)
            .leftJoin(PRESCRIPTION)
            .on(DOCTOR.HOSPITAL_CODE.eq(PRESCRIPTION.DOCTOR_CODE))
            .leftJoin(INQUIRY_ORDER)
            .on(INQUIRY_ORDER.DOCTOR_ID.eq(DOCTOR.ID))
            .leftJoin(DOCTOR_DEPARTMENT_COUPLE)
            .on(DOCTOR_DEPARTMENT_COUPLE.DOCTOR_ID.eq(DOCTOR.ID))
            .leftJoin(DEPARTMENT)
            .on(DEPARTMENT.ID.eq(DOCTOR_DEPARTMENT_COUPLE.DEPARTMENT_ID))
            .leftJoin(USER_DOCTOR_ATTENTION)
            .on(USER_DOCTOR_ATTENTION.DOCTOR_ID.eq(DOCTOR.ID))
            .where(condition1);
        buildOption(select, userAssociatedDoctorParam);
        select.groupBy(DOCTOR.NAME, DOCTOR.HOSPITAL_CODE, DOCTOR.ID);
        if (userAssociatedDoctorParam.getDepartmentName() != null) {
            select.groupBy(DEPARTMENT.NAME);
        }
            select.orderBy(DOCTOR.NAME.asc());
        return this.getPageResult(select, userAssociatedDoctorParam.getCurrentPage(),
            userAssociatedDoctorParam.getPageRows(), UserAssociatedDoctorVo.class);
    }

    /**
     * 用户查询关联医师条件查询
     * @param select 查询实体
     * @param userAssociatedDoctorParam 查询参数
     */
    private void buildOption(SelectConditionStep<? extends Record> select, UserAssociatedDoctorParam userAssociatedDoctorParam){
        if (userAssociatedDoctorParam.getIsFavorite() != null && userAssociatedDoctorParam.getIsFavorite() == 1) {
            select.and(USER_DOCTOR_ATTENTION.USER_ID.eq(userAssociatedDoctorParam.getUserId()));
        }
        if (userAssociatedDoctorParam.getIsFavorite() != null && userAssociatedDoctorParam.getIsFavorite() == 0) {
            select.and(USER_DOCTOR_ATTENTION.USER_ID.isNull());
        }
        if (userAssociatedDoctorParam.getDoctorName() != null && userAssociatedDoctorParam.getDoctorName().trim().length() > 0) {
            select.and(DOCTOR.NAME.like(likeValue(userAssociatedDoctorParam.getDoctorName())));
        }
        if (userAssociatedDoctorParam.getDepartmentName() != null && userAssociatedDoctorParam.getDepartmentName().trim().length() > 0) {
            select.and(DEPARTMENT.NAME.like(likeValue(userAssociatedDoctorParam.getDepartmentName())));
        }
    }

}
