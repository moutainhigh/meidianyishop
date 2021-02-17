package com.meidianyi.shop.service.shop.overview;

import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.db.shop.tables.records.SearchHistoryRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.config.SearchConfig;
import com.meidianyi.shop.service.pojo.shop.overview.hotwords.*;
import com.meidianyi.shop.service.shop.config.SearchConfigService;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

import static com.meidianyi.shop.db.shop.Tables.SEARCH_HISTORY;

/**
 * 热词搜索表操作
 *
 * @author liangchen
 * @date 2019.12.19
 */
@Service
public class HotWordsService extends ShopBaseService {
    //搜索配置
    @Autowired SearchConfigService searchConfigService;
    /** del_flag：未删除 */
    public static final Byte NOT_DELETE = 0;
    /** del_flag：已删除 */
    public static final Byte HAS_DELETE = 1;
    /** 搜索条数：1 */
    public static final Integer LIMIT_ONE_NUM = 1;
    /** 搜索条数：10 */
    public static final Integer LIMIT_TEN_NUM = 10;
    /** 截取updateTime开始下标：0 */
    public static final Integer UPDATE_TIME_START = 0;
    /** 截取updateTime结束下标：10 */
    public static final Integer UPDATE_TIME_END = 10;
    /**
     * 获得用户搜索热词
     * @param param 用户id和热词数量
     * @return 用户最新搜索的热词
     */
    public List<HotWords> getUserSearchHots(UserIdAndNum param){
        List<HotWords> hotWords = db().select(SEARCH_HISTORY.HOT_WORDS)
            .from(SEARCH_HISTORY)
            .where(SEARCH_HISTORY.DEL_FLAG.eq(NOT_DELETE))
            .and(SEARCH_HISTORY.USER_ID.eq(param.getUserId()))
            .groupBy(SEARCH_HISTORY.HOT_WORDS)
            .orderBy(DSL.max(SEARCH_HISTORY.UPDATE_TIME).desc())
            .limit(param.getNum())
            .fetchInto(HotWords.class);
        return hotWords;
    }

    /**
     * 添加热词
     * @param param 用户id和热词
     */
    public void addHotWords(UserIdAndWords param){
        //当天有数据则更新数量
        if (getHistoryWords(param.getUserId(),param.getHotWords())!=null){
            increaseCount(param.getUserId(),param.getHotWords());
        }
        //当天没数据则插入新数据
        else {
            int isHotWords = isHotWords(param.getHotWords())?1:0;
            db().insertInto(SEARCH_HISTORY,SEARCH_HISTORY.USER_ID,SEARCH_HISTORY.HOT_WORDS,SEARCH_HISTORY.IS_HOT_WORDS)
                .values(param.getUserId(),param.getHotWords(), (byte) isHotWords)
                .execute();
        }
    }

    /**
     * 判断当前用户今天是否搜索指定热词并获得今天最近的一次搜索词
     * @param userId 用户id
     * @param words 搜索词
     * @return 最近的一条记录或null
     */
    public SearchHistoryRecord getHistoryWords(Integer userId,String words){
        SearchHistoryRecord record = db().select()
            .from(SEARCH_HISTORY)
            .where(SEARCH_HISTORY.DEL_FLAG.eq(NOT_DELETE))
            .and(SEARCH_HISTORY.USER_ID.eq(userId))
            .and(SEARCH_HISTORY.HOT_WORDS.eq(words))
            .orderBy(SEARCH_HISTORY.UPDATE_TIME.desc())
            .limit(LIMIT_ONE_NUM)
            .fetchOneInto(SearchHistoryRecord.class);
        if (record==null){
            return null;
        }
        else if (record.getUpdateTime().toString().substring(UPDATE_TIME_START,UPDATE_TIME_END).equals(DateUtils.yyyyMmDdDate(DateUtils.getLocalDate()).toString())){
            return record;
        }else {
            return null;
        }
    }

    /**
     * 递增搜索记录
     * @param userId 用户id
     * @param words 热词
     */
    public void increaseCount(Integer userId,String words){
        //原始数量
        Integer searchCount = getHistoryWords(userId,words).getSearchCount();
        db().update(SEARCH_HISTORY)
            .set(SEARCH_HISTORY.SEARCH_COUNT,searchCount+1)
            .where(SEARCH_HISTORY.DEL_FLAG.eq(NOT_DELETE))
            .and(SEARCH_HISTORY.USER_ID.eq(userId))
            .and(SEARCH_HISTORY.HOT_WORDS.eq(words))
            .and(SEARCH_HISTORY.UPDATE_TIME.greaterThan(Timestamp.valueOf(DateUtils.dateFormat(DateUtils.DATE_FORMAT_FULL_BEGIN, DateUtils.getLocalDateTime()))))
            .execute();
    }

    /**
     * 是否是热词搜索
     * @param words 热词
     * @return true或false
     */
    public boolean isHotWords(String words){
        SearchConfig searchConfig = searchConfigService.getSearchConfig();
        if (searchConfig==null){
            return false;
        }
        return searchConfig.hotWords.contains(words);
    }

    /**
     * 热词排行榜（从高到低）
     * @param param 查找日期及是否热词标识
     * @return  热词及对应搜索次数
     */
    public List<WordsAndCount> getHistoryTop(DateAndWordsFlag param){
        List<WordsAndCount> wordsAndCount = db().select(SEARCH_HISTORY.HOT_WORDS,DSL.sum(SEARCH_HISTORY.SEARCH_COUNT).as("count"))
            .from(SEARCH_HISTORY)
            .where(SEARCH_HISTORY.DEL_FLAG.eq(NOT_DELETE))
            .and(SEARCH_HISTORY.IS_HOT_WORDS.eq(param.getIsHotWords()))
            .and(SEARCH_HISTORY.UPDATE_TIME.between(param.getStartDate(),param.getEndDate()))
            .groupBy(SEARCH_HISTORY.HOT_WORDS)
            .orderBy(DSL.sum(SEARCH_HISTORY.SEARCH_COUNT).desc())
            .limit(LIMIT_TEN_NUM)
            .fetchInto(WordsAndCount.class);
        return wordsAndCount;
    }

    /**
     * 清除用户搜索词
     * @param param 用户id
     */
    public void clearUserHotWords(UserId param){
        db().update(SEARCH_HISTORY)
            .set(SEARCH_HISTORY.DEL_FLAG,HAS_DELETE)
            .set(SEARCH_HISTORY.DEL_TIME, DateUtils.getLocalDateTime())
            .where(SEARCH_HISTORY.USER_ID.eq(param.getUserId()))
            .and(SEARCH_HISTORY.DEL_FLAG.eq(NOT_DELETE))
            .execute();
    }

    /**
     * 获取用户最近一次搜索的热词
     * @param userId 用户id
     * @return 最近搜索热词
     */
    public String getUserLastSearch(Integer userId){
        String hotWords = db().select(SEARCH_HISTORY.HOT_WORDS)
            .from(SEARCH_HISTORY)
            .where(SEARCH_HISTORY.USER_ID.eq(userId))
            .and(SEARCH_HISTORY.DEL_FLAG.eq(NOT_DELETE))
            .orderBy(SEARCH_HISTORY.UPDATE_TIME.desc())
            .limit(LIMIT_ONE_NUM)
            .fetchOptionalInto(String.class)
            .orElse(null);
        return hotWords;
    }
}
