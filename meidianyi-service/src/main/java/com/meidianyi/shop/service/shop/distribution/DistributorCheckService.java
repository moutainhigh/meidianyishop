package com.meidianyi.shop.service.shop.distribution;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.DistributorApplyRecord;
import com.meidianyi.shop.db.shop.tables.records.UserRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.decoration.DistributorApplyParam;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributionApplyOptParam;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorCheckListParam;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorCheckListVo;
import com.meidianyi.shop.service.pojo.wxapp.distribution.UserBindParam;
import jodd.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.meidianyi.shop.db.shop.Tables.DISTRIBUTOR_APPLY;
import static com.meidianyi.shop.db.shop.Tables.USER;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * 分销员审核service
 * @author 常乐
 * 2019年9月20日
 */
@Service
public class DistributorCheckService extends ShopBaseService{
    @Autowired
    private MpDistributionService mpd;
    /**
     * 分销员审核列表
     * @param param
     * @return
     */
	public PageResult<DistributorCheckListVo> getDistributorCheckList(DistributorCheckListParam param) {
        SelectConditionStep<Record> select = db().select(DISTRIBUTOR_APPLY.fields()).select(USER.USERNAME, USER.MOBILE)
            .from(DISTRIBUTOR_APPLY.leftJoin(USER).on(DISTRIBUTOR_APPLY.USER_ID.eq(USER.USER_ID)))
               .where(DSL.trueCondition());
        buildOptions(select,param);
        PageResult<DistributorCheckListVo> pageResult = this.getPageResult(select, param.getCurrentPage(), param.getPageRows(), DistributorCheckListVo.class);
        for(DistributorCheckListVo applyInfo: pageResult.getDataList()){
            DistributorApplyParam.InfoField infoField = Util.parseJson(applyInfo.getActivationFields(), DistributorApplyParam.InfoField.class);
            if(infoField != null){
                //邀请人信息
                Record record = db().select().from(USER).where(USER.INVITATION_CODE.eq(infoField.getInvitationCode())).fetchOne();
                if(record != null){
                    UserRecord into = record.into(UserRecord.class);
                    applyInfo.setInviteName(into.getUsername());
                    applyInfo.setInviteId(into.getUserId());
                }
            }
            applyInfo.setCheckField(Util.parseJson(applyInfo.getActivationFields(),DistributorApplyParam.InfoField.class));
        }
        return pageResult;
	}


    /**
     * 分销员列表审核列表条件查询
     * @param select
     * @param param
     */
	public void buildOptions(SelectConditionStep<Record> select,DistributorCheckListParam param){
	    //根据手机号查询
        if(isNotEmpty(param.getMobile())){
            select.and(USER.MOBILE.like(likeValue(param.getMobile())));
        }
        //根据用户名
        if(isNotEmpty(param.getUsername())){
            select.and(USER.USERNAME.contains(param.getUsername()));
        }
        //申请开始时间
        if(param.getStartTime() !=null){
            select.and(DISTRIBUTOR_APPLY.CREATE_TIME.ge(param.getStartTime()));
        }
        //申请结束时间
        if(param.getEndTime() != null){
            select.and(DISTRIBUTOR_APPLY.CREATE_TIME.le(param.getEndTime()));
        }
        //根据ID
        if (param.getUserId() != null) {
            select.and(DISTRIBUTOR_APPLY.USER_ID.eq(param.getUserId()));
        }
        //根据邀请码
        if (StringUtil.isNotEmpty(param.getInvitationCode())) {
            //1.根据邀请码查询出发出邀请码的邀请人ID
            SelectConditionStep<Record1<Integer>> selectConditionStep =
                db().select(USER.USER_ID)
                    .from(USER)
                    .where(USER.INVITATION_CODE.contains(param.getInvitationCode()));
            if (selectConditionStep != null) {
                Set<Integer> integers = selectConditionStep.fetchSet(USER.USER_ID);
                //2.根据邀请人ID查询出受邀请人集合
                select.and(USER.INVITE_ID.in(integers));
            }
        }
        //根据分销员分组
        if (param.getInviteGroup() != null) {
            select.and(USER.INVITE_GROUP.eq(param.getInviteGroup()));
        }
        //审核开始时间
        if (param.getCheckStartTime() != null) {
            select.and(DISTRIBUTOR_APPLY.UPDATE_TIME.ge(param.getCheckStartTime()));
        }
        //审核结束时间
        if (param.getCheckEndTime() != null) {
            select.and(DISTRIBUTOR_APPLY.UPDATE_TIME.le(param.getCheckEndTime()));
        }
        //flag = 1是从店铺助手过来
        if(param.getFlag() == 1){
            select.and(DISTRIBUTOR_APPLY.STATUS.eq((byte)0).and(DISTRIBUTOR_APPLY.CREATE_TIME.add(param.getNumberDays()).lessThan(Timestamp.valueOf(LocalDateTime.now()))));
        }
        //状态 0：待审核；1：审核通过；2：未通过
        if(param.getNav() != null) {
            select.and(DISTRIBUTOR_APPLY.STATUS.eq(param.getNav()));
        }
        //根据申请时间降序排序
        select.orderBy(DISTRIBUTOR_APPLY.CREATE_TIME.desc());
    }

    /**
     * Distribution review timeout integer.分销审核超过N天未处理数量
     *
     * @param nDays the n days
     * @return the integer
     */
    public Integer distributionReviewTimeout(Integer nDays) {
        return db().fetchCount(DISTRIBUTOR_APPLY, DISTRIBUTOR_APPLY.CREATE_TIME.add(nDays).lessThan(Timestamp.valueOf(LocalDateTime.now())));
    }

    /**
     * Distribution review timeout integer.分销审核超过N天未处理集合
     *
     * @param nDays the n days
     * @return the integer
     */
    public Set<Integer> distributionReviewTimeoutSet(Integer nDays) {
        Condition condition = DISTRIBUTOR_APPLY.CREATE_TIME.add(nDays).lessThan(Timestamp.valueOf(LocalDateTime.now()));
        return db().select(DISTRIBUTOR_APPLY.ID).from(DISTRIBUTOR_APPLY).where(condition).fetchSet(DISTRIBUTOR_APPLY.ID);
    }

    /**
     * 分销审核通过
     * @param param
     * @return
     */
    public boolean applyPass(DistributionApplyOptParam param){
        //获取申请信息
        DistributorApplyRecord into = db().select().from(DISTRIBUTOR_APPLY).where(DISTRIBUTOR_APPLY.ID.eq(param.getId())).fetchOne().into(DistributorApplyRecord.class);
        //事务处理
        this.transaction(() -> {
            //更新审核状态 1：审核通过；2：审核拒绝
            changeApplyStatus(param.getId(),(byte)1);
            //如果有邀请码处理邀请绑定
            if(StringUtil.isNotEmpty(param.getInvitationCode())){
                Record1<Integer> integerRecord1 = db().select(USER.USER_ID).from(USER).where(USER.INVITATION_CODE.eq(param.getInvitationCode())).fetchOne();
                if(integerRecord1 != null){
                    Integer inviteId = integerRecord1.into(Integer.class);
                    UserBindParam userBindParam = new UserBindParam();
                    userBindParam.setInviteId(inviteId);
                    userBindParam.setUserId(into.getUserId());
                    mpd.userBind(userBindParam);
                }
            }
            String invitationCode = mpd.validInviteCode();
            //更新分销身份状态，分组情况
            updateApplyGroup(into.getUserId(),param.getGroupId(),invitationCode);
        });
        //TODO：更改分销员的返利信息
        //TODO：操作记录
        return true;
    }

    /**
     * 分销员审核拒绝
     * @param param
     * @return
     */
    public boolean applyRefuse(DistributionApplyOptParam param){
        //获取申请信息
        Integer userId = db().select(DISTRIBUTOR_APPLY.USER_ID).from(DISTRIBUTOR_APPLY).where(DISTRIBUTOR_APPLY.ID.eq(param.getId())).fetchOne().into(Integer.class);
        //事务处理
        this.transaction(() -> {
            //更新审核状态 1：审核通过；2：审核拒绝
            changeApplyStatus(param.getId(),(byte)2);
            //添加审核内容
            if(isNotEmpty(param.getMsg())){
                db().update(DISTRIBUTOR_APPLY).set(DISTRIBUTOR_APPLY.MSG,param.getMsg()).where(DISTRIBUTOR_APPLY.ID.eq(param.getId())).execute();
            }
        });
        //TODO:操作记录
        return true;
    }

    /**
     * 更新分销审核状态
     * @param id 申请id
     * @param status 审核状态 1：通过；2：拒绝
     */
    private void changeApplyStatus(Integer id,Byte status){
        db().update(DISTRIBUTOR_APPLY)
            .set(DISTRIBUTOR_APPLY.STATUS, status)
            .where(DISTRIBUTOR_APPLY.ID.eq(id))
            .execute();
    }

    /**
     * 设置审核更新数据
     * @param userId 申请人id
     * @param groupId 分组id
     */
    private void updateApplyGroup(Integer userId,Integer groupId,String invitationCode){
        db().update(USER)
            .set(USER.IS_DISTRIBUTOR, (byte)1)
            .set(USER.INVITE_GROUP,groupId)
            .set(USER.INVITATION_CODE,invitationCode)
            .where(USER.USER_ID.eq(userId))
            .execute();
    }
}
