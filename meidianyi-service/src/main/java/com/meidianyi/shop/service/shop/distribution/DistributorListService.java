package com.meidianyi.shop.service.shop.distribution;

import static com.meidianyi.shop.common.foundation.data.DistributionConstant.*;
import static com.meidianyi.shop.db.shop.Tables.*;
import static com.meidianyi.shop.db.shop.Tables.DISTRIBUTOR_APPLY;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.sum;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.excel.ExcelWriter;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.User;
import com.meidianyi.shop.db.shop.tables.records.UserRemarkRecord;
import com.meidianyi.shop.service.pojo.shop.config.distribution.DistributionParam;
import com.meidianyi.shop.service.pojo.shop.distribution.*;
import com.meidianyi.shop.service.shop.config.DistributionConfigService;
import jodd.util.StringUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.service.foundation.service.ShopBaseService;

/**
 * 分销员列表
 * @author 常乐
 * 2019年8月5日
 */
@Service
public class DistributorListService extends ShopBaseService{
    public static final String ORDER_ASC = "asc";
    @Autowired
    public MpDistributionService mpDis;

    @Autowired
    public DistributionConfigService dcs;

    /**
     * 分销员列表｜导出公共部分
     * @return
     */
    public SelectConditionStep<? extends Record> distributorListComm(DistributorListParam param){
        com.meidianyi.shop.db.shop.tables.User d = USER.as("d");
        com.meidianyi.shop.db.shop.tables.User a = USER.as("a");
        Field nextNumber = count(a.USER_ID).as("nextNumber");
        Field sublayerNumber = sum(USER_TOTAL_FANLI.SUBLAYER_NUMBER).as("sublayerNumber");
        Field totalCanFanliMoney = sum(ORDER_GOODS.CAN_CALCULATE_MONEY).as("totalCanFanliMoney");
        Field totalFanliMoney = sum(USER_FANLI_STATISTICS.TOTAL_FANLI_MONEY).as("totalFanliMoney");
        Field waitFanliMoney = sum(ORDER_GOODS_REBATE.REAL_REBATE_MONEY).as("waitFanliMoney");

        //下级用户数
        Table<Record1<Integer>> recordTable = db().select(d.USER_ID,nextNumber.as("nextNumber")).from(d).leftJoin(a).on(a.INVITE_ID.eq(d.USER_ID)).where(d.IS_DISTRIBUTOR.eq((byte) 1)).groupBy(d.USER_ID).asTable();

        //分销审核开关是否开启
        DistributionParam distributionCfg = dcs.getDistributionCfg();
        if(distributionCfg.getJudgeStatus() != null && distributionCfg.getJudgeStatus() == 0){//分销审核开关开启展示审核过的分销员
            //下级用户数
            recordTable = db().select(d.USER_ID,nextNumber.as("nextNumber")).from(d).leftJoin(a).on(a.INVITE_ID.eq(d.USER_ID)).groupBy(d.USER_ID).asTable();
        }

        //间接邀请人数
        Table<Record2<Integer, BigDecimal>> record2Table = db().select(d.USER_ID, sublayerNumber.as("sublayerNumber")).from(d).leftJoin(a).on(d.USER_ID.eq(a.INVITE_ID)).leftJoin(USER_TOTAL_FANLI).on(USER_TOTAL_FANLI.USER_ID.eq(a.USER_ID))
            .where(d.IS_DISTRIBUTOR.eq((byte) 1)).groupBy(d.USER_ID).asTable();

        //累计返利商品总额；累计获得佣金金额
        Table<Record3<Integer, BigDecimal, BigDecimal>> record3Table = db().select(USER_FANLI_STATISTICS.FANLI_USER_ID,totalFanliMoney.as("totalFanliMoney")).from(d).leftJoin(USER_FANLI_STATISTICS).on(d.USER_ID.eq(USER_FANLI_STATISTICS.FANLI_USER_ID)).where(d.IS_DISTRIBUTOR.eq((byte) 1))
            .groupBy(USER_FANLI_STATISTICS.FANLI_USER_ID).asTable();

        //累计返利商品总额
        Table<Record2<BigDecimal, Integer>> record2Table1 = db().select(totalCanFanliMoney.as("totalCanFanliMoney"), ORDER_GOODS_REBATE.REBATE_USER_ID).from(ORDER_GOODS_REBATE).leftJoin(ORDER_GOODS).on(ORDER_GOODS_REBATE.ORDER_SN.eq(ORDER_GOODS.ORDER_SN))
            .groupBy(ORDER_GOODS_REBATE.REBATE_USER_ID).asTable();

        //待返利佣金金额
        Table<Record2<Integer, BigDecimal>> record2Table2 = db().select(ORDER_GOODS_REBATE.REBATE_USER_ID, waitFanliMoney.as("waitFanliMoney")).from(d).leftJoin(ORDER_GOODS_REBATE).on(d.USER_ID.eq(ORDER_GOODS_REBATE.REBATE_USER_ID)).leftJoin(ORDER_INFO).on(ORDER_GOODS_REBATE.ORDER_SN
            .eq(ORDER_INFO.ORDER_SN)).where(ORDER_INFO.SETTLEMENT_FLAG.eq((byte) 0)).groupBy(d.USER_ID,ORDER_GOODS_REBATE.REBATE_USER_ID).asTable();

        SelectOnConditionStep<? extends Record> select = db().select(
            d.USER_ID, d.USERNAME,d.INVITE_ID,
            d.MOBILE,d.INVITATION_CODE,d.CREATE_TIME,USER_DETAIL.REAL_NAME,DISTRIBUTOR_LEVEL.LEVEL_NAME,DISTRIBUTOR_GROUP.GROUP_NAME,
            recordTable.field("nextNumber"),
            record2Table.field("sublayerNumber"),
            record2Table1.field("totalCanFanliMoney"),
            record3Table.field("totalFanliMoney"),
            record2Table2.field("waitFanliMoney"))
            .from(d).leftJoin(a).on(d.USER_ID.eq(a.INVITE_ID))
            .leftJoin(USER_DETAIL).on(d.USER_ID.eq(USER_DETAIL.USER_ID))
            .leftJoin(DISTRIBUTOR_LEVEL).on(d.DISTRIBUTOR_LEVEL.eq(DISTRIBUTOR_LEVEL.LEVEL_ID))
            .leftJoin(DISTRIBUTOR_GROUP).on(d.INVITE_GROUP.eq(DISTRIBUTOR_GROUP.ID));
        //临时表拼接
        SelectConditionStep<? extends Record> where = select
            .leftJoin(recordTable).on(recordTable.field(USER.USER_ID).eq(d.USER_ID))
            .leftJoin(record2Table).on(record2Table.field(USER.USER_ID).eq(d.USER_ID))
            .leftJoin(record3Table).on(record3Table.field(USER_FANLI_STATISTICS.FANLI_USER_ID).eq(d.USER_ID))
            .leftJoin(record2Table1).on(record2Table1.field(ORDER_GOODS_REBATE.REBATE_USER_ID).eq(d.USER_ID))
            .leftJoin(record2Table2).on(record2Table2.field(ORDER_GOODS_REBATE.REBATE_USER_ID).eq(d.USER_ID)).where(d.USER_ID.gt(0));

        if(distributionCfg.getJudgeStatus() != null && distributionCfg.getJudgeStatus() == 1){//分销审核开关开启展示审核过的分销员
            where.and(d.IS_DISTRIBUTOR.eq((byte) 1));
        }
        SelectConditionStep<? extends Record> sql = buildOptions(where, param,record2Table,record3Table,record2Table2,recordTable,record2Table1);
        return sql;
    }
    /**
     * 分销员分页列表
     * @param param
     */
    public PageResult<DistributorListVo> getPageList(DistributorListParam param) {

        SelectConditionStep<? extends Record> sql = distributorListComm(param);
        PageResult<DistributorListVo> distributorList = this.getPageResult(sql, param.getCurrentPage(), param.getPageRows(), DistributorListVo.class);
        for(DistributorListVo dis:distributorList.dataList){
            Record record = db().select(USER.USERNAME).from(USER).where(USER.USER_ID.eq(dis.getInviteId())).fetchOne();
            if(record != null){
                dis.setInviteName(record.into(String.class));
            }else{
                dis.setInviteName(null);
            }
            //分销员审核通过时间查询
            Record1<Timestamp> fetch = db().select(DISTRIBUTOR_APPLY.UPDATE_TIME)
                .from(DISTRIBUTOR_APPLY)
                .where(DISTRIBUTOR_APPLY.USER_ID.eq(dis.getUserId()))
                .orderBy(DISTRIBUTOR_APPLY.UPDATE_TIME.desc())
                .limit(1)
                .fetchOne();
            if (fetch != null) {
                dis.setUpdateTime(fetch.into(Timestamp.class));
            } else {
                dis.setUpdateTime(null);
            }
            //备注条数
            Integer remarkNum = db().selectCount().from(USER_REMARK).where(USER_REMARK.IS_DELETE.eq((byte) 0)).and(USER_REMARK.USER_ID.eq(dis.getUserId())).fetchOne().into(Integer.class);
            dis.setRemarkNum(remarkNum);
        }
        return distributorList;
    }

    /**
     * 分销列表条件查询
     * @param where
     * @param param
     * @return
     */
    public  SelectConditionStep<? extends Record> buildOptions(SelectConditionStep<? extends Record> where,DistributorListParam param,Table<Record2<Integer, BigDecimal>> record2Table,Table<Record3<Integer, BigDecimal, BigDecimal>> record3Table,Table<Record2<Integer, BigDecimal>> record2Table2,Table<Record1<Integer>> recordTable,Table<Record2<BigDecimal, Integer>> record2Table1) {
        com.meidianyi.shop.db.shop.tables.User d = USER.as("d");
        com.meidianyi.shop.db.shop.tables.User a = USER.as("a");
        //分销员ID
        if(param.getDistributorId() != null){
            where.and(d.USER_ID.eq(param.getDistributorId()));
        }
        //微信昵称
        if(StringUtil.isNotEmpty(param.getUsername())) {
            where.and(d.USERNAME.contains(param.getUsername()));
        }

        //手机号
        if(StringUtil.isNotEmpty(param.getMobile())) {
            where.and(d.MOBILE.contains(param.getMobile()));
        }
        //真实姓名
        if(StringUtil.isNotEmpty(param.getRealName())) {
            where.and(USER_DETAIL.REAL_NAME.contains(param.getRealName()));
        }
        //邀请人
        if(StringUtil.isNotEmpty(param.getInviteName())){
            List<Integer> inviteIds = db().select(USER.USER_ID).from(USER).where(USER.USERNAME.contains(param.getInviteName())).fetch().into(Integer.class);
            where.and(d.INVITE_ID.in(inviteIds));
        }
        //邀请码
        if(StringUtil.isNotEmpty(param.getInvitationCode())){
            where.and(d.INVITATION_CODE.eq(param.getInvitationCode()));
        }
        //创建时间
        if(param.getStartCreateTime() != null && param.getEndCreateTime() != null) {
            where.and(d.CREATE_TIME.ge(param.getStartCreateTime())).and(d.CREATE_TIME.le(param.getEndCreateTime()));
        }
        //被邀请人昵称 || 手机号
        buildNameAndMobileOption(where, param, a);

        //分销分组
        if(param.getDistributorGroup() != null) {
            where.and(d.INVITE_GROUP.eq(param.getDistributorGroup()));
        }
        //分销等级
        if(param.getDistributorLevel() != null) {
            where.and(d.DISTRIBUTOR_LEVEL.eq(param.getDistributorLevel()));
        }
//		//有下级用户
        if(param.getHaveNextUser() != null && param.getHaveNextUser()== 1){
            where.and(d.USER_ID.in(db().select(a.INVITE_ID).from(a).fetch()));
        }
        //有手机号
        if(param.getHaveMobile() != null &&  param.getHaveMobile() == 1){
            where.and(d.MOBILE.isNotNull());
        }
        //有真是姓名
        if(param.getHaveRealName() != null && param.getHaveRealName() == 1){
            where.and(USER_DETAIL.REAL_NAME.isNotNull());
        }
        if(param.getOptGroupId() != null){
            where.and(a.INVITE_GROUP.ne(param.getOptGroupId()));
        }
        where.groupBy(d.USER_ID,recordTable.field("nextNumber"),record2Table.field("sublayerNumber"), record2Table1.field("totalCanFanliMoney"),
            record3Table.field("totalFanliMoney"), record2Table2.field("waitFanliMoney"),d.USERNAME,d.MOBILE,d.INVITATION_CODE,
            d.CREATE_TIME,USER_DETAIL.REAL_NAME,DISTRIBUTOR_LEVEL.LEVEL_NAME,DISTRIBUTOR_GROUP.GROUP_NAME,d.INVITE_ID);

        //表头排序
        buildOrderOption(where, param, record2Table, record3Table, record2Table2, recordTable);

        return where;
    }
    private void buildOrderOption(SelectConditionStep<? extends Record> where, DistributorListParam param, Table<Record2<Integer, BigDecimal>> record2Table, Table<Record3<Integer, BigDecimal, BigDecimal>> record3Table, Table<Record2<Integer, BigDecimal>> record2Table2, Table<Record1<Integer>> recordTable) {
        String asc = "asc";
        if(param.getSortField().equals(SORT_BY_NEXT_NUM)){
            if(asc.equals(param.getSortWay())){
                where.orderBy(recordTable.field("nextNumber").asc());
            }else{
                where.orderBy(recordTable.field("nextNumber").desc());
            }
        }

        if(param.getSortField().equals(SORT_BY_SUBLAYER_NUM)){
            if(asc.equals(param.getSortWay())){
                where.orderBy(record2Table.field("sublayerNumber").asc());
            }else{
                where.orderBy(record2Table.field("sublayerNumber").desc());
            }
        }

        if(param.getSortField().equals(SORT_BY_TOTAL_CAN_FANLI)){
            if(asc.equals(param.getSortWay())){
                where.orderBy(record3Table.field("totalCanFanliMoney").asc());
            }else{
                where.orderBy(record3Table.field("totalCanFanliMoney").desc());
            }
        }

        if(param.getSortField().equals(SORT_BY_TOTAL_FANLI)){
            if(asc.equals(param.getSortWay())){
                where.orderBy(record3Table.field("totalFanliMoney").asc());
            }else{
                where.orderBy(record3Table.field("totalFanliMoney").desc());
            }
        }

        if(param.getSortField().equals(SORT_BY_WAIT_FANLI)){
            if(asc.equals(param.getSortWay())){
                where.orderBy(record2Table2.field("waitFanliMoney").asc());
            }else{
                where.orderBy(record2Table2.field("waitFanliMoney").desc());
            }
        }
    }
    private void buildNameAndMobileOption(SelectConditionStep<? extends Record> where, DistributorListParam param, User a) {
        if(StringUtil.isNotEmpty(param.getInvitedMobile()) || StringUtil.isNotEmpty(param.getInvitedUserName())) {
            SelectConditionStep<Record1<Integer>> selectInvites = db().select(a.INVITE_ID)
                .from(a).where(a.INVITE_ID.ge(0));
            if(StringUtil.isNotEmpty(param.getInvitedUserName())) {
                selectInvites.and(a.USERNAME.contains(param.getInvitedUserName()));
            }
            if(StringUtil.isNotEmpty(param.getInvitedMobile())) {
                selectInvites.and(a.MOBILE.contains(param.getInvitedMobile()));
            }
            ArrayList<Integer> inviteIds = new ArrayList<Integer>();
            List<Integer> invites = selectInvites.fetch().into(Integer.class);
            for(int invite : invites) {
                inviteIds.add(invite);
            }
            where.and(a.USER_ID.in(inviteIds));
        }
    }

    private void processOrderBy(SelectConditionStep<? extends Record> where, DistributorListParam param, Table<Record2<Integer, BigDecimal>> record2Table, Table<Record3<Integer, BigDecimal, BigDecimal>> record3Table, Table<Record2<Integer, BigDecimal>> record2Table2, Table<Record1<Integer>> recordTable) {
        //表头排序
        if(param.getSortField().equals(SORT_BY_NEXT_NUM)){
            if(param.getSortWay().equals(ORDER_ASC)){
                where.orderBy(recordTable.field("nextNumber").asc());
            }else{
                where.orderBy(recordTable.field("nextNumber").desc());
            }
        }

        if(param.getSortField().equals(SORT_BY_SUBLAYER_NUM)){
            if(param.getSortWay().equals(ORDER_ASC)){
                where.orderBy(record2Table.field("sublayerNumber").asc());
            }else{
                where.orderBy(record2Table.field("sublayerNumber").desc());
            }
        }

        if(param.getSortField().equals(SORT_BY_TOTAL_CAN_FANLI)){
            if(param.getSortWay().equals(ORDER_ASC)){
                where.orderBy(record3Table.field("totalCanFanliMoney").asc());
            }else{
                where.orderBy(record3Table.field("totalCanFanliMoney").desc());
            }
        }

        if(param.getSortField().equals(SORT_BY_TOTAL_FANLI)){
            if(param.getSortWay().equals(ORDER_ASC)){
                where.orderBy(record3Table.field("totalFanliMoney").asc());
            }else{
                where.orderBy(record3Table.field("totalFanliMoney").desc());
            }
        }

        if(param.getSortField().equals(SORT_BY_WAIT_FANLI)){
            if(param.getSortWay().equals(ORDER_ASC)){
                where.orderBy(record2Table2.field("waitFanliMoney").asc());
            }else{
                where.orderBy(record2Table2.field("waitFanliMoney").desc());
            }
        }
    }

    private void processInviteMobileAndName(SelectConditionStep<? extends Record> where, DistributorListParam param, User a) {
        //被邀请人昵称 || 手机号
        if(StringUtil.isNotEmpty(param.getInvitedMobile()) || StringUtil.isNotEmpty(param.getInvitedUserName())) {
            SelectConditionStep<Record1<Integer>> selectInvites = db().select(a.INVITE_ID)
                .from(a).where(a.INVITE_ID.ge(0));
            if(StringUtil.isNotEmpty(param.getInvitedUserName())) {
                selectInvites.and(a.USERNAME.contains(param.getInvitedUserName()));
            }
            if(StringUtil.isNotEmpty(param.getInvitedMobile())) {
                selectInvites.and(a.MOBILE.contains(param.getInvitedMobile()));
            }
            ArrayList<Integer> inviteIds = new ArrayList<Integer>();
            List<Integer> invites = selectInvites.fetch().into(Integer.class);
            for(int invite : invites) {
                inviteIds.add(invite);
            }
            where.and(a.USER_ID.in(inviteIds));
        }
    }

    /**
     * 导出分销员列表数据
     * @param param
     * @param lang
     */
    public Workbook exportDistributorList(DistributorListParam param, String lang){
        SelectJoinStep<? extends Record> select = db().select(USER.USER_ID,USER.USERNAME.as("distributor_name"),
            USER.INVITATION_CODE,USER.MOBILE.as("distributor_mobile"),USER.CREATE_TIME,USER_DETAIL.REAL_NAME.as("distributor_real_name"),
            DISTRIBUTOR_GROUP.GROUP_NAME,DISTRIBUTOR_LEVEL.LEVEL_NAME,USER_TOTAL_FANLI.SUBLAYER_NUMBER,USER.INVITE_ID)
            .from(USER.leftJoin(USER_TOTAL_FANLI).on(USER.USER_ID.eq(USER_TOTAL_FANLI.USER_ID))
                .leftJoin(USER_DETAIL).on(USER.USER_ID.eq(USER_DETAIL.USER_ID))
                .leftJoin(DISTRIBUTOR_GROUP).on(DISTRIBUTOR_GROUP.ID.eq(USER.INVITE_GROUP))
                .leftJoin(DISTRIBUTOR_LEVEL).on(USER.DISTRIBUTOR_LEVEL.eq(DISTRIBUTOR_LEVEL.LEVEL_ID)));
        SelectConditionStep<? extends Record> sql = buildOptions(select,param);
        sql.limit(param.getStartNum(),param.getEndNum());
        List<DistributorListExportVo> distributorListExportVos = sql.fetchInto(DistributorListExportVo.class);

        for(DistributorListExportVo distributor : distributorListExportVos) {
            //邀请人
            Record3<String, String, String> record = db().select(USER.USERNAME.as("invite_name"), USER.MOBILE.as("invite_mobile"), USER_DETAIL.REAL_NAME.as("invite_real_name")).from(USER)
                .leftJoin(USER_DETAIL).on(USER.USER_ID.eq(USER_DETAIL.USER_ID))
                .where(USER.USER_ID.eq(distributor.getInviteId())).fetchOne();
            if(record != null){
                DistributorListExportVo info = record.into(DistributorListExportVo.class);
                distributor.setInviteName(info.getInviteName());
                distributor.setInviteMobile(info.getInviteMobile());
                distributor.setInviteRealName(info.getInviteRealName());
            }

            //分销员标签
            Result<Record1<String>> fetch = db().select(TAG.TAG_NAME).from(USER_TAG).leftJoin(TAG).on(TAG.TAG_ID.eq(USER_TAG.TAG_ID)).where(USER_TAG.USER_ID.eq(distributor.getUserId())).fetch();
            if(fetch != null){
                List<String> info = fetch.into(String.class);
                String userTags = Util.listToString(info);
                distributor.setDistributorTags(userTags);
            }

            //用户备注
            Result<Record1<String>> fetch1 = db().select(USER_REMARK.REMARK).from(USER_REMARK).where(USER_REMARK.USER_ID.eq(distributor.getUserId())).and(USER_REMARK.IS_DELETE.eq((byte) 0)).fetch();
            if(fetch1 != null){
                List<String> remark = fetch1.into(String.class);
                distributor.setRemark(Util.listToString(remark));
            }

            //审核通过时间
            Record1<Timestamp> timestampRecord1 = db().select(DISTRIBUTOR_APPLY.UPDATE_TIME).from(DISTRIBUTOR_APPLY)
                .where(DISTRIBUTOR_APPLY.USER_ID.eq(distributor.getUserId())).and(DISTRIBUTOR_APPLY.STATUS.eq((byte) 1))
                .orderBy(DISTRIBUTOR_APPLY.ID.desc()).limit(1).fetchOne();
            if(timestampRecord1 != null){
                Timestamp checkTime = timestampRecord1.into(Timestamp.class);
                distributor.setCheckTime(checkTime);
            }

            //间接邀请用户数
            Integer nextNum = db().select(sum(USER_TOTAL_FANLI.SUBLAYER_NUMBER).as("next_num")).from(USER_TOTAL_FANLI).where(USER_TOTAL_FANLI.USER_ID.in(
                db().select(USER.USER_ID).from(USER).where(USER.INVITE_ID.eq(distributor.getUserId())).fetch().into(Integer.class)))
                .fetchOne().into(Integer.class);
            if(nextNum == null) {
                nextNum = 0;
            }
            distributor.setNextNumber(nextNum);

            //累积获得佣金金额
            BigDecimal totalFanliMoney = db().select(sum(USER_FANLI_STATISTICS.TOTAL_FANLI_MONEY).as("can_fanli_goods_money"))
                .from(USER_FANLI_STATISTICS).where(USER_FANLI_STATISTICS.FANLI_USER_ID.eq(distributor.getUserId()))
                .fetchOne().into(BigDecimal.class);
            distributor.setTotalFanliMoney(totalFanliMoney);

            //待返利佣金金额
            BigDecimal waitFanliMoney = db().select(sum(ORDER_GOODS_REBATE.REAL_REBATE_MONEY).as("wait_fanli_money")).from(ORDER_GOODS_REBATE
                .leftJoin(ORDER_INFO).on(ORDER_GOODS_REBATE.ORDER_SN.eq(ORDER_INFO.ORDER_SN)))
                .where(ORDER_INFO.SETTLEMENT_FLAG.eq((byte)0))
                .and(ORDER_INFO.ORDER_STATUS.ge((byte)3))
                .and(ORDER_GOODS_REBATE.REBATE_USER_ID.eq(distributor.getUserId()))
                .fetchOne().into(BigDecimal.class);
            distributor.setWaitFanliMoney(waitFanliMoney);
        }
        System.out.println(distributorListExportVos);
        Workbook workbook= ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(lang,workbook);
        excelWriter.writeModelList(distributorListExportVos, DistributorListExportVo.class);
        return workbook;
    }

    /**
     * 导出分销员列表条件查询
     * @param select
     * @param param
     * @return
     */
    public  SelectConditionStep<? extends Record> buildOptions(SelectJoinStep<? extends Record> select,DistributorListParam param) {
        SelectConditionStep<? extends Record> sql = select.where(USER.IS_DISTRIBUTOR.eq((byte) 1));
        System.out.println(param);
        //微信昵称
        if(param.getUsername() != null) {
            sql = sql.and(USER.USERNAME.eq(param.getUsername()));
        }
        //手机号
        if(param.getMobile() != null) {
            sql = sql.and(USER.MOBILE.contains(param.getMobile()));
        }
        //真实姓名
        if(param.getRealName() != null) {
            sql = sql.and(USER_DETAIL.REAL_NAME.contains(param.getRealName()));
        }
        //创建时间
        if(param.getStartCreateTime() != null && param.getEndCreateTime() != null) {
            sql = sql.and(USER.CREATE_TIME.ge(param.getStartCreateTime())).and(USER.CREATE_TIME.le(param.getStartCreateTime()));
        }
        //被邀请人昵称 || 手机号
        if(param.getInvitedMobile() != null || param.getInvitedUserName() != null) {
            SelectConditionStep<Record1<Integer>> selectInvites = db().select(USER.INVITE_ID)
                .from(USER).where(USER.INVITE_ID.ge(0));
            if(param.getInvitedUserName() != null) {
                selectInvites.and(USER.USERNAME.contains(param.getInvitedUserName()));
            }
            if(param.getInvitedMobile() != null) {
                selectInvites.and(USER.MOBILE.contains(param.getInvitedMobile()));
            }
            ArrayList<Integer> inviteIds = new ArrayList<Integer>();
            List<Integer> invites = selectInvites.fetch().into(Integer.class);
            for(int invite : invites) {
                inviteIds.add(invite);
            }
            sql = sql.and(USER.USER_ID.in(inviteIds));
        }
        //分销分组
        if(param.getDistributorGroup() != null) {
            sql = sql.and(USER.INVITE_GROUP.eq(param.getDistributorGroup()));
        }
        //分销等级
        if(param.getDistributorLevel() != null) {
            sql = sql.and(USER.DISTRIBUTOR_LEVEL.eq(param.getDistributorLevel()));
        }
        //有下级用户
        if(param.getHaveNextUser() !=null &&  param.getHaveNextUser()== 1){
            sql = sql.and(USER.USER_ID.in(db().select(USER.INVITE_ID).from(USER).fetch()));
        }
        //有手机号
        if(param.getHaveMobile() != null &&  param.getHaveMobile() == 1){
            sql = sql.and(USER.MOBILE.ne("null"));
        }
        //有真是姓名
        if(param.getHaveRealName() != null && param.getHaveRealName() == 1){
            sql = sql.and(USER_DETAIL.REAL_NAME.ne("null"));
        }
        if(param.getOptGroupId() != null){
            sql = sql.and(USER.INVITE_GROUP.ne(param.getOptGroupId()));
        }
        return sql;
    }

    /**
     * 分销员已邀请用户列表
     * @param param
     * @return
     */
    public DistributorInvitedListVo getInvitedList(DistributorInvitedListParam param) {
        SelectJoinStep<? extends Record> select = db().select(USER.USER_ID,USER.USERNAME,USER_DETAIL.REAL_NAME,USER.MOBILE,USER.CREATE_TIME,USER.INVITE_EXPIRY_DATE,USER.INVITE_TIME,USER.INVITE_PROTECT_DATE,sum(USER_FANLI_STATISTICS.ORDER_NUMBER).as("ORDER_NUMBER"),sum(USER_FANLI_STATISTICS.TOTAL_CAN_FANLI_MONEY).as("TOTAL_CAN_FANLI_MONEY"),sum(USER_FANLI_STATISTICS.TOTAL_FANLI_MONEY).as("TOTAL_FANLI_MONEY"))
            .from(USER.leftJoin(USER_FANLI_STATISTICS).on(USER.USER_ID.eq(USER_FANLI_STATISTICS.USER_ID)))
            .leftJoin(USER_DETAIL).on(USER.USER_ID.eq(USER_DETAIL.USER_ID));
        List<Integer> userIds = null;
        SelectConditionStep<? extends Record> sql = getInvitedListOptions(select,param,userIds);
        PageResult<InviteUserInfoVo> invitedList = this.getPageResult(sql, param.getCurrentPage(), param.getPageRows(), InviteUserInfoVo.class);
        BigDecimal totalGetFanliMoney = new BigDecimal(0);
        DistributorInvitedListVo inviteInfo = new DistributorInvitedListVo();
        for(InviteUserInfoVo info:invitedList.dataList){
            if(info.getTotalFanliMoney() != null){
                totalGetFanliMoney = totalGetFanliMoney.add(info.getTotalFanliMoney());
            }
        }
        inviteInfo.setTotalGetFanliMoney(totalGetFanliMoney);
        inviteInfo.setInviteUserInfo(invitedList);
        return inviteInfo;
    }

    /**
     * 清除分销员身份
     * @param userId
     * @return
     */
    public int delDistributor(Integer userId) {
        int res = db().update(USER).set(USER.IS_DISTRIBUTOR,(byte)0)
            .set(USER.INVITATION_CODE, (String) null)
            .where(USER.USER_ID.eq(userId)).execute();
        return res;
    }

    /**
     * 已邀请用户列表条件查询
     * @param select
     * @param param
     * @return
     */
    public SelectConditionStep<? extends Record> getInvitedListOptions(SelectJoinStep<? extends Record> select,DistributorInvitedListParam param,List<Integer> userIds) {
        SelectConditionStep<? extends Record> sql = select.where(USER.INVITE_ID.gt(0));
        if(param.getInviteType() == 0) {
            sql = select.where(USER.INVITE_ID.eq(param.getUserId()));
        }
        if(param.getInviteType() == 1){
            sql = select.where(USER.INVITE_ID.in(userIds));
        }

        if(StringUtil.isNotEmpty(param.getMobile())) {
            sql = sql.and(USER.MOBILE.contains(param.getMobile()));
        }
        if(StringUtil.isNotEmpty(param.getUsername())) {
            sql = sql.and(USER.USERNAME.contains(param.getUsername()));
        }
        if(StringUtil.isNotEmpty(param.getRealName())) {
            sql = sql.and(USER_DETAIL.REAL_NAME.contains(param.getRealName()));
        }
        if(param.getStartCreateTime() != null && param.getEndCreateTime() != null) {
            sql = sql.and(USER.CREATE_TIME.ge(param.getStartCreateTime()).and(USER.CREATE_TIME.le(param.getEndCreateTime())));
        }
        if(param.getStartInviteTime() != null && param.getEndInviteTime() != null) {
            sql = sql.and(USER.INVITE_TIME.ge(param.getStartInviteTime()).and(USER.INVITE_TIME.le(param.getEndInviteTime())));
        }
        sql.groupBy(USER_FANLI_STATISTICS.USER_ID,USER.USERNAME,USER.MOBILE,USER.CREATE_TIME,USER.INVITE_EXPIRY_DATE,USER.INVITE_PROTECT_DATE,USER.INVITE_TIME,USER.USER_ID,USER_DETAIL.REAL_NAME);
        return sql;
    }

    /**
     * 间接邀请用户列表
     * @param param
     */
    public DistributorInvitedListVo getIndirectInviteList(DistributorInvitedListParam param){
        //直接下级
        Result<Record1<Integer>> record = db().select(USER.USER_ID).from(USER).where(USER.INVITE_ID.eq(param.getUserId())).fetch();
        System.out.println("record:"+ record);
        if(record != null){
            List<Integer> userIds = record.into(Integer.class);
            SelectJoinStep<? extends Record> select = db().select(USER.USER_ID,USER.USERNAME,USER_DETAIL.REAL_NAME,USER.MOBILE,USER.CREATE_TIME,USER.INVITE_EXPIRY_DATE,USER.INVITE_TIME,USER.INVITE_PROTECT_DATE,sum(USER_FANLI_STATISTICS.ORDER_NUMBER).as("ORDER_NUMBER"),sum(USER_FANLI_STATISTICS.TOTAL_CAN_FANLI_MONEY).as("TOTAL_CAN_FANLI_MONEY"),sum(USER_FANLI_STATISTICS.TOTAL_FANLI_MONEY).as("TOTAL_FANLI_MONEY"))
                .from(USER.leftJoin(USER_FANLI_STATISTICS).on(USER.USER_ID.eq(USER_FANLI_STATISTICS.USER_ID)))
                .leftJoin(USER_DETAIL).on(USER.USER_ID.eq(USER_DETAIL.USER_ID));
            param.setInviteType((byte)1);
            SelectConditionStep<? extends Record> sql = getInvitedListOptions(select,param,userIds);
            PageResult<InviteUserInfoVo> invitedList = this.getPageResult(sql, param.getCurrentPage(), param.getPageRows(), InviteUserInfoVo.class);
            BigDecimal totalGetFanliMoney = new BigDecimal(0);
            DistributorInvitedListVo inviteInfo = new DistributorInvitedListVo();
            for(InviteUserInfoVo info:invitedList.dataList){
                if(info.getTotalFanliMoney() != null){
                    totalGetFanliMoney = totalGetFanliMoney.add(info.getTotalFanliMoney());
                }
            }
            inviteInfo.setTotalGetFanliMoney(totalGetFanliMoney);
            inviteInfo.setInviteUserInfo(invitedList);
            return inviteInfo;
        }else{
            return null;
        }
    }

    /**
     * 获取返利订单数量
     * @param userId
     * @return
     */
    public int getRebateOrderNum(Integer userId) {
        return db().fetchCount(USER_FANLI_STATISTICS, USER_FANLI_STATISTICS.FANLI_USER_ID.eq(userId));
    }

    /**
     *给分销员设置分销邀请码
     * @param param
     * @return
     */
    public int setInviteCode(SetInviteCodeParam param){
        Integer res = mpDis.sentInviteCodeVerify(param.getInviteCode());
        if(res == 0){
            db().update(USER).set(USER.INVITATION_CODE,param.getInviteCode()).where(USER.USER_ID.eq(param.getUserId())).execute();
            return 1;
        }else{
            return 0;
        }
    }

    /**
     * 根据分销配置自动给没有分销邀请码的分销员生成邀请码
     * @return
     */
    public int autoSetInviteCode(){
        //没有邀请码的分销员
        Result<Record1<Integer>> record = db().select(USER.USER_ID).from(USER).where(USER.IS_DISTRIBUTOR.eq((byte) 1)).and(USER.INVITATION_CODE.isNull()).fetch();
        System.out.println(record);
        int res = 0;
        if(record != null){
            List<Integer> noInvitationCode = record.into(Integer.class);
            System.out.println(noInvitationCode);
            for(Integer userId:noInvitationCode){
                String code = mpDis.generateInvitationCode();
                SetInviteCodeParam setInviteCodeParam = new SetInviteCodeParam();
                setInviteCodeParam.setUserId(userId);
                setInviteCodeParam.setInviteCode(code);
                res = setInviteCode(setInviteCodeParam);
            }
        }
        return res;
    }

    /**
     * 会员备注列表
     * @param param
     * @return
     */
    public List<UserRemarkListVo> userRemarkList(UserRemarkListVo param){
        Result<Record> record = db().select().from(USER_REMARK).where(USER_REMARK.USER_ID.eq(param.getUserId())).and(USER_REMARK.IS_DELETE.eq((byte)0)).fetch();
        if(record != null){
            return record.into(UserRemarkListVo.class);
        }else{
            return null;
        }
    }

    /**
     *添加会员备注
     * @param param
     * @return
     */
    public int addUserRemark(UserRemarkListVo param){
        UserRemarkRecord record = new UserRemarkRecord();
        assign(param, record);
        return db().executeInsert(record);
    }

    /**
     * 删除会员备注
     * @param id
     * @return
     */
    public int delUserRemark(Integer id){
        int res = db().update(USER_REMARK).set(USER_REMARK.IS_DELETE, (byte) 1).where(USER_REMARK.ID.eq(id)).execute();
        return res;
    }

    /**
     * 分销员设置分组
     * @param param
     * @return
     */
    public int setGroup(DistributorSetGroupParam param){
        int res = db().update(USER).set(USER.INVITE_GROUP, param.getGroupId()).where(USER.USER_ID.in(param.getUserId())).execute();
        return res;
    }

    /**
     * 获取分销员的分组名称
     * @return 分组名称
     */
    public String getGroupName(Integer userId) {
        return db().select(DISTRIBUTOR_GROUP.GROUP_NAME)
            .from(USER.leftJoin(DISTRIBUTOR_GROUP).on(DISTRIBUTOR_GROUP.ID.eq(USER.INVITE_GROUP)))
            .where(USER.USER_ID.eq(userId))
            .fetchAnyInto(String.class);
    }


}
