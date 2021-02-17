package com.meidianyi.shop.service.shop.overview;

import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.config.SearchConfig;
import com.meidianyi.shop.service.pojo.shop.overview.searchanalysis.*;
import com.meidianyi.shop.service.shop.config.SearchConfigService;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.meidianyi.shop.db.shop.Tables.SEARCH_HISTORY;
/**
 * 搜索统计
 *
 * @author liangchen
 * @date 2019年7月23日
 */
@Service
public class SearchAnalysisService extends ShopBaseService {
    @Autowired SearchConfigService searchConfigService;
    /** 日期标识符 */
    private static final Integer CUSTOM_DAYS = 0;
    /** 返回条数限制 */
    private static final Integer LIMIT_NUM = 10;
    /**
     *得到之前的某一天(字符串类型)
     *@param days N天前
     *@return preDay(String)
     */
    public String getDate(Integer days) {
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取当前时间
        Calendar c = Calendar.getInstance();
        //计算指定日期
        c.add(Calendar.DATE, - days);
        Date time = c.getTime();
        //返回格式化后的String日期
        return sdf.format(time);
    }
	/**
	 * 搜索历史统计
	 *
	 * @param param 查询时间段
	 * @return 10条搜索历史统计信息
	 */
	public SearchHistoryVo getSearchHistory(SearchHistoryParam param) {
        //得到时间
        if (!param.getType().equals(CUSTOM_DAYS)){
            param.setStartTime(getDate(param.getType()));
            param.setEndTime(getDate(NumberUtils.INTEGER_ZERO));
        }
        //设置返回时间
        SearchHistoryVo result = new SearchHistoryVo();
        result.setStartTime(param.getStartTime());
        result.setEndTime(param.getEndTime());
		//查询配置信息-是否开启搜索历史统计
        SearchConfig searchConfig = searchConfigService.getSearchConfig();
        if(null==searchConfig) {
            searchConfig=new SearchConfig(1, 1, 0);
        }
        result.setIsOpenHistory(searchConfig.isOpenHistory);
        //查找搜索历史数据
		List<SearchHistoryData> searchHistoryData =
            db().select(SEARCH_HISTORY.HOT_WORDS,DSL.sum(SEARCH_HISTORY.SEARCH_COUNT).as("count"))
				.from(SEARCH_HISTORY)
                .where(SEARCH_HISTORY.DEL_FLAG.eq(NumberUtils.BYTE_ZERO))
                .and(SEARCH_HISTORY.UPDATE_TIME.between(Timestamp.valueOf(param.getStartTime()), Timestamp.valueOf(param.getEndTime())))
				.groupBy(SEARCH_HISTORY.HOT_WORDS)
				.orderBy(DSL.sum(SEARCH_HISTORY.SEARCH_COUNT).as("count").desc())
                .limit(LIMIT_NUM)
				.fetchInto(SearchHistoryData.class);
		result.setData(searchHistoryData);
		return result;
	}
	/**
	 * 搜索热词统计
	 *
	 * @param param 查询时间段
     * @return 10条搜索热词统计信息
	 */
	public SearchHotWordsVo getHotSearchHistory(SearchHistoryParam param) {
        //得到时间
        if (!param.getType().equals(CUSTOM_DAYS)){
            param.setStartTime(getDate(param.getType()));
            param.setEndTime(getDate(NumberUtils.INTEGER_ZERO));
        }
        //设置返回时间
        SearchHotWordsVo result = new SearchHotWordsVo();
        result.setStartTime(param.getStartTime());
        result.setEndTime(param.getEndTime());
        //查询配置信息-是否开启搜索热词统计
        SearchConfig searchConfig = searchConfigService.getSearchConfig();
        if(null==searchConfig) {
            searchConfig=new SearchConfig(1, 1, 0);
        }
        result.setIsOpenHotWords(searchConfig.isOpenHotWords);
        //查找搜索历史数据
		List<SearchHotWordsData> searchHotWordsData =
            db().select(SEARCH_HISTORY.HOT_WORDS, DSL.sum(SEARCH_HISTORY.SEARCH_COUNT).as("count"))
				.from(SEARCH_HISTORY)
                .where(SEARCH_HISTORY.DEL_FLAG.eq(NumberUtils.BYTE_ZERO))
                .and(SEARCH_HISTORY.IS_HOT_WORDS.eq(NumberUtils.BYTE_ONE))
				.and(SEARCH_HISTORY.UPDATE_TIME.between(Timestamp.valueOf(param.getStartTime()), Timestamp.valueOf(param.getEndTime())))
				.groupBy(SEARCH_HISTORY.HOT_WORDS)
				.orderBy(DSL.sum(SEARCH_HISTORY.SEARCH_COUNT).as("count").desc())
                .limit(LIMIT_NUM)
				.fetchInto(SearchHotWordsData.class);
        result.setData(searchHotWordsData);
		return result;
	}
}
