package com.meidianyi.shop.dao.main.user;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.dao.foundation.base.MainBaseDao;
import com.meidianyi.shop.db.main.tables.records.UserRecord;
import com.meidianyi.shop.service.pojo.saas.user.MainUserPageListParam;
import com.meidianyi.shop.service.pojo.saas.user.MainUserVo;
import org.jooq.Condition;
import org.jooq.SelectSeekStep1;
import org.springframework.stereotype.Repository;

import static com.meidianyi.shop.db.main.Tables.USER;

/**
 * @author 李晓冰
 * @date 2020年08月17日
 */
@Repository
public class MainUserDao extends MainBaseDao {

    public PageResult<MainUserVo> getPageList(MainUserPageListParam pageListParam) {
        Condition condition = buildPageListCondition(pageListParam);

        SelectSeekStep1<UserRecord, Long> select = db().selectFrom(USER).where(condition).orderBy(USER.ID.asc());
        PageResult<MainUserVo> pageResult = this.getPageResult(select, pageListParam.getCurrentPage(), pageListParam.getPageRows(), MainUserVo.class);
        return pageResult;
    }

    private Condition buildPageListCondition(MainUserPageListParam pageListParam) {
        Condition condition = USER.DEL_FLAG.eq(DelFlag.NORMAL_VALUE);
        if (pageListParam.getShopId() != null) {
            condition = condition.and(USER.SHOP_ID.eq(pageListParam.getShopId()));
        }
        if (pageListParam.getMobile() != null) {
            condition = condition.and(USER.MOBILE.eq(pageListParam.getMobile()));
        }
        if (pageListParam.getUsername() != null) {
            condition = condition.and(USER.USERNAME.like(likeValue(pageListParam.getUsername())));
        }
        if (pageListParam.getStartTime() != null) {
            condition = condition.and(USER.CREATE_TIME.ge(pageListParam.getStartTime()));
        }
        if (pageListParam.getEndTime() != null) {
            condition = condition.and(USER.CREATE_TIME.le(pageListParam.getEndTime()));
        }
        return condition;
    }
}
