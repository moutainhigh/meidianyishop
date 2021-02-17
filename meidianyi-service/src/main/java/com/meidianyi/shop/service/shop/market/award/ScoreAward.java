package com.meidianyi.shop.service.shop.market.award;

import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.pojo.shop.member.account.ScoreParam;
import com.meidianyi.shop.service.pojo.shop.operation.RemarkTemplate;
import com.meidianyi.shop.service.shop.member.ScoreService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.meidianyi.shop.service.pojo.shop.member.score.ScoreStatusConstant.NO_USE_SCORE_STATUS;
import static com.meidianyi.shop.service.pojo.shop.operation.RecordTradeEnum.TRADE_FLOW_IN;
import static com.meidianyi.shop.service.pojo.shop.operation.RecordTradeEnum.TYPE_SCORE_PAY_AWARD;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ZERO;

/**
 * @author liufei
 * @date 1/13/20
 */
@Slf4j
@Service
public class ScoreAward implements Award {
    @Autowired
    private ScoreService scoreService;

    @Override
    public void sendAward(AwardParam param) {
        ScoreParam scoreParam = new ScoreParam();
        scoreParam.setScore(param.getRule().getScore());
        scoreParam.setUserId(param.getUserId());
        scoreParam.setOrderSn(param.getOrderSn());
        scoreParam.setChangeWay(param.getChangeWay());
        scoreParam.setRemarkCode(RemarkTemplate.SHARE_HAS_GIFT.code);
        scoreParam.setScoreStatus(NO_USE_SCORE_STATUS);
        try {
            scoreService.updateMemberScore(scoreParam, INTEGER_ZERO, TYPE_SCORE_PAY_AWARD.val(), TRADE_FLOW_IN.val());
        } catch (MpException e) {
            log.error("积分发送失败：{}", ExceptionUtils.getStackTrace(e));
        }
        log.info("积分发放完成");
    }
}
