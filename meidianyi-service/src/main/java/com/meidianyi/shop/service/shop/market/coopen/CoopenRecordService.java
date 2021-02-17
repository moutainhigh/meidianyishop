package com.meidianyi.shop.service.shop.market.coopen;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.shop.tables.CoopenActivityRecords;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.market.coopen.CoopenConstant;
import com.meidianyi.shop.service.pojo.shop.market.coopen.CoopenIssueListParam;
import com.meidianyi.shop.service.pojo.shop.market.coopen.CoopenIssueListVo;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.springframework.stereotype.Service;

import static com.meidianyi.shop.db.shop.Tables.COOPEN_ACTIVITY;
import static com.meidianyi.shop.db.shop.tables.User.USER;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * 开屏有礼 - 优惠券发放明细
 *
 * @author 郑保乐
 */
@Service
public class CoopenRecordService extends ShopBaseService {

    private static final CoopenActivityRecords TABLE = CoopenActivityRecords.COOPEN_ACTIVITY_RECORDS;

    /**
     * 发放明细列表
     */
    public PageResult<CoopenIssueListVo> getIssuePageList(CoopenIssueListParam param) {

        SelectConditionStep<? extends Record> select =
            db().select(TABLE.MRKING_VOUCHER_ID,TABLE.RECEIVE_TIME,TABLE.COMMENT,TABLE.USER_ID, TABLE.ACTIVITY_ACTION,COOPEN_ACTIVITY.NAME, USER.USERNAME, USER.MOBILE)
                    .from(TABLE)
                .leftJoin(USER).on(USER.USER_ID.eq(TABLE.USER_ID))
                .leftJoin(COOPEN_ACTIVITY).on(COOPEN_ACTIVITY.ID.eq(TABLE.ACTIVITY_ID)).where();
        buildOptions(select, param);
        PageResult<CoopenIssueListVo> issueList = getPageResult(select, param, CoopenIssueListVo.class);
        for (CoopenIssueListVo issue: issueList.getDataList()) {
            switch (issue.getActivityAction()){
                case CoopenConstant.COUPON:
                    issue.setAwardName(CoopenConstant.COUPON_NAME);
                    break;
                case CoopenConstant.DRAW:
                    issue.setAwardName(CoopenConstant.DRAW_NAME);
                    break;
                case CoopenConstant.CUSTOMIZE:
                    issue.setAwardName(CoopenConstant.CUSTOMIZE_NAME);
                    break;
                case CoopenConstant.SCORE:
                    issue.setAwardName(CoopenConstant.SCORE_NAME);
                    break;
                case CoopenConstant.ACCOUNT:
                    issue.setAwardName(CoopenConstant.ACCOUNT_NAME);
                    break;
                case CoopenConstant.SPLIT_COUPON:
                    issue.setAwardName(CoopenConstant.SPLIT_COUPON__NAME);
                    break;
                default:
            }
        }
        return issueList;
    }

    private void buildOptions(SelectConditionStep<? extends Record> select, CoopenIssueListParam param) {
        String mobile = param.getMobile();
        String username = param.getUsername();
        Integer activityId = param.getActivityId();
        if (isNotEmpty(mobile)) {
            select.and(USER.MOBILE.like(likeValue( mobile)));
        }
        if (isNotEmpty(username)) {
            select.and(USER.USERNAME.like(likeValue(username)));
        }
        select.and(TABLE.ACTIVITY_ID.eq(activityId));
    }
}
