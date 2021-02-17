package com.meidianyi.shop.service.pojo.wxapp.distribution;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.service.pojo.shop.config.distribution.DistributionParam;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorGroupListVo;
import com.meidianyi.shop.service.pojo.shop.member.data.EducationVo;
import com.meidianyi.shop.service.pojo.shop.member.data.IndustryVo;
import com.meidianyi.shop.service.pojo.shop.member.data.MarriageData;
import lombok.Data;

import java.util.List;

/**
 * @Author 常乐
 * @Date 2020-01-07
 * 提交申请页面数据出参
 */
@Data
public class ActivationInfoVo {
    private UserBaseInfoVo userBaseInfo;
    private List<DistributorGroupListVo> groupList;
    private List<IndustryVo> industryList;
    private List<EducationVo> educationList;
    @JsonProperty("MarriageData")
    private  List<MarriageData> marriageData;
    private DistributionParam cfg;
}
