package com.meidianyi.shop.service.shop.overview;

import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author liangchen
 * @date  2019年7月15日
 */
@Service
public class OverviewService extends ShopBaseService{
	@Autowired public OverviewAnalysisService overviewAnalysisService;
	@Autowired public UserAnalysisService userAnalysisService;
	@Autowired public SearchAnalysisService searchAnalysisService;
	@Autowired public HotWordsService hotWordsService;
}
