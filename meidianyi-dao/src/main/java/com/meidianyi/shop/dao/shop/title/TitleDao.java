package com.meidianyi.shop.dao.shop.title;

import com.meidianyi.shop.common.foundation.util.FieldsUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.DoctorTitleRecord;
import com.meidianyi.shop.service.pojo.shop.title.TitleListParam;
import com.meidianyi.shop.service.pojo.shop.title.TitleOneParam;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

import static com.meidianyi.shop.db.shop.Tables.DOCTOR;
import static com.meidianyi.shop.db.shop.Tables.DOCTOR_TITLE;

/**
 * @author chenjie
 */
@Repository
public class TitleDao extends ShopBaseDao{

    /**
     * 职称列表
     *
     * @param param
     * @return
     */
    public PageResult<TitleOneParam> getTitleList(TitleListParam param) {
        SelectSeekStep1<Record6<Integer, Integer, String, String, Timestamp, Integer>, Integer> select = db().select(
            DOCTOR_TITLE.ID,
            DOCTOR_TITLE.FIRST,
            DOCTOR_TITLE.NAME,
            DOCTOR_TITLE.CODE,
            DOCTOR_TITLE.CREATE_TIME
            , DSL.count(DOCTOR.TITLE_ID).as("doctorNum"))
            .from(DOCTOR_TITLE)
            .leftJoin(DOCTOR)
            .on(DOCTOR.TITLE_ID.eq(DOCTOR_TITLE.ID))
            .groupBy(DOCTOR.TITLE_ID,
                DOCTOR_TITLE.ID,
                DOCTOR_TITLE.NAME,
                DOCTOR_TITLE.FIRST,
                DOCTOR_TITLE.CODE,
                DOCTOR_TITLE.CREATE_TIME)
            .orderBy(DOCTOR_TITLE.ID.asc());
        return this.getPageResult(select, param.getCurrentPage(),
            param.getPageRows(), TitleOneParam.class);
    }

    /**
     * 职称搜索查询
     *
     * @param select
     * @param param
     */
    protected void buildOptions(SelectJoinStep<? extends Record> select, TitleListParam param) {
        Timestamp nowDate = new Timestamp(System.currentTimeMillis());
    }

    /**
     * 获取一条职称的信息
     *
     * @param titleId
     * @return
     */
    public TitleOneParam getOneInfo(Integer titleId) {
        TitleOneParam info = db().select().from(DOCTOR_TITLE).where(DOCTOR_TITLE.ID.eq(titleId))
            .fetchAnyInto(TitleOneParam.class);
        return info;
    }

    /**
     * 编辑保存
     *
     * @param param
     * @return
     */
    public int updateTitle(TitleOneParam param) {
        DoctorTitleRecord record = new DoctorTitleRecord();
        FieldsUtil.assign(param, record);
        return db().executeUpdate(record);
    }

    /**
     * 添加测评活动信息
     *
     * @param param
     * @return
     */
    public void insertTitle(TitleOneParam param) {
        DoctorTitleRecord record = db().newRecord(DOCTOR_TITLE);
        FieldsUtil.assign(param, record);
        record.insert();
        param.setId(record.getId());
    }

    /**
     * 删除
     *
     * @param titleId
     * @return
     */
    public int deleteTitle(Integer titleId) {
        int res = db().update(DOCTOR_TITLE).set(DOCTOR_TITLE.IS_DELETE, (byte) 1).where(DOCTOR_TITLE.ID.eq(titleId))
            .execute();
        return res;
    }

    /**
     * 职称是否存在，用来新增检查
     * @param titleId 科室ID
     * @param name 科室名称
     * @return true 存在 false 不存在
     */
    public boolean isNameExist(Integer titleId,String name) {
        Condition condition = DOCTOR_TITLE.NAME.eq(name);
        if (titleId != null) {
            condition = condition.and(DOCTOR_TITLE.ID.ne(titleId));
        }
        int count = db().fetchCount(DOCTOR_TITLE, condition);
        return count>0;
    }

    /**
     * 获取职称列表
     *
     * @return
     */
    public List<TitleOneParam> listTitles() {
        List<TitleOneParam> titleList = db().select().from(DOCTOR_TITLE).where(DOCTOR_TITLE.IS_DELETE.eq((byte) 0))
            .fetchInto(TitleOneParam.class);
        return titleList;
    }


    /**
     * 获取一条职称的信息
     *
     * @param code
     * @return
     */
    public Integer getTitleByCode(String code) {
        return db().select(DOCTOR_TITLE.ID).from(DOCTOR_TITLE).where(DOCTOR_TITLE.CODE.eq(code))
            .fetchOneInto(Integer.class);
    }
}
