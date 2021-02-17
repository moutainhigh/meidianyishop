package com.meidianyi.shop.service.shop.distribution;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.dao.shop.distribution.distributorlevel.DistributorLevelDao;
import com.meidianyi.shop.dao.shop.distribution.distributorlevel.DistributorLevelRecordDao;
import com.meidianyi.shop.db.shop.tables.records.DistributorLevelRecord;
import com.meidianyi.shop.db.shop.tables.records.UserDetailRecord;
import com.meidianyi.shop.db.shop.tables.records.UserRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorSpendVo;
import com.meidianyi.shop.service.pojo.shop.distribution.UserRebateLevelDetail;
import com.meidianyi.shop.service.pojo.wxapp.distribution.distributorlevel.DistributorLevelDetailParam;
import com.meidianyi.shop.service.pojo.wxapp.distribution.distributorlevel.DistributorLevelDetailVo;
import com.meidianyi.shop.service.pojo.wxapp.distribution.distributorlevel.DistributorLevelRecordParam;
import com.meidianyi.shop.service.pojo.wxapp.distribution.distributorlevel.DistributorLevelRecordVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author changle
 * @date 2020/8/17 9:49 上午
 */
@Service
public class MpDistributorLevelService extends ShopBaseService {
    @Autowired
    protected DistributorLevelRecordDao disLevelRecDao;

    @Autowired
    protected DistributorLevelDao disLevelDao;

    @Autowired
    protected DistributorLevelService disLevelSer;

    /**
     * 分销员等级详情页
     * @param param
     * @return
     */
    public DistributorLevelDetailVo getDistributorLevelDetail(DistributorLevelDetailParam param){
        String imgAvatar = "/image/admin/head_icon.png";
        UserRecord userInfo = disLevelDao.getUserInfo(param.getUserId());
        UserDetailRecord userDetail = disLevelDao.getUserDetail(param.getUserId());
        //是否是最高等级 0：否；1：是；
        byte isHigtLevel = 1;
        List<Integer> userId = new ArrayList<>();
        userId.add(param.getUserId());
        DistributorLevelDetailVo distributorLevelDetailVo = new DistributorLevelDetailVo();
        //当前等级信息
        DistributorLevelRecord distributorLevel = disLevelDao.getDistributorLevelByLevelId(userInfo.getDistributorLevel());
        //启用中的自动升级等级
        List<DistributorLevelRecord> autoUpLevel = disLevelDao.getAutoUpLevel();
        //获取分销员分销信息（分销金额）USER.USER_ID,USER_TOTAL_FANLI.SUBLAYER_NUMBER,USER.DISTRIBUTOR_LEVEL,DISTRIBUTOR_LEVEL.LEVEL_NAME
        List<UserRebateLevelDetail> distributorInfo = disLevelSer.getUserRebateLevelDetail(userId);
        //获取邀请人的推广金额
        Map<Integer, BigDecimal> distributorRebate = disLevelSer.getDistributorRebate(userId);
        //获取用户的订单和门店买单消费总额
        DistributorSpendVo userSpend = disLevelSer.getTotalSpend(param.getUserId());
        distributorLevelDetailVo.setUsername(userDetail.getUsername());
        if(imgAvatar.equals(userDetail.getUserAvatar())){
            distributorLevelDetailVo.setUserAvator(imageUrl(userDetail.getUserAvatar()));
        }else{
            distributorLevelDetailVo.setUserAvator(userDetail.getUserAvatar());
        }
        System.out.println(distributorRebate);
        distributorLevelDetailVo.setLevelName(distributorLevel.getLevelName());
        distributorLevelDetailVo.setNextNumber(distributorInfo.get(0).getSublayerNumber());
        distributorLevelDetailVo.setRebateMoney(distributorRebate.get(param.getUserId()));
        distributorLevelDetailVo.setRebateAndPayMoney(userSpend.getTotal());

        if(autoUpLevel != null){
            for(DistributorLevelRecord item : autoUpLevel){
                if (item.getLevelId().compareTo(distributorLevel.getLevelId()) > 0){
                    isHigtLevel = 0;
                    distributorLevelDetailVo.setIsHigtLevel(isHigtLevel);
                    distributorLevelDetailVo.setNextLevelName(item.getLevelName());
                    distributorLevelDetailVo.setToNextNumber(item.getInviteNumber());
                    distributorLevelDetailVo.setToRebateMoney(item.getTotalDistributionMoney());
                    distributorLevelDetailVo.setToRebateAndPayMoney(item.getTotalBuyMoney());
                }
            }
        }
        return distributorLevelDetailVo;
    }


    /**
     * 分销员等级变更记录
     * @param param
     * @return
     */
    public PageResult<DistributorLevelRecordVo> distributorLevelRecord(DistributorLevelRecordParam param){
        PageResult<DistributorLevelRecordVo> levelRecord = disLevelRecDao.getDistributorLevelRecordByUserId(param);
        for(DistributorLevelRecordVo item : levelRecord.dataList){
            String newLevelName = disLevelRecDao.getLevelNameByLevelId(item.getNewLevel());
            if(item.getOldLevel().compareTo(item.getNewLevel()) > 0)  {
                item.setLevelRecordDesc("降级为"+newLevelName);
            }
            if(item.getOldLevel().compareTo(item.getNewLevel()) < 0)  {
                item.setLevelRecordDesc("升级为"+newLevelName);
            }
        }
        return levelRecord;
    }
}
