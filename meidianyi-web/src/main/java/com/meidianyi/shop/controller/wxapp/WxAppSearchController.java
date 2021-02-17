package com.meidianyi.shop.controller.wxapp;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.shop.config.SearchConfig;
import com.meidianyi.shop.service.pojo.shop.overview.hotwords.*;
import com.meidianyi.shop.service.pojo.wxapp.login.WxAppSessionUser;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小程序-搜索历史相关
 * @author liangchen
 * @date 2020.02.10
 */
@RestController
@RequestMapping("/api/wxapp/search")
public class WxAppSearchController extends WxAppBaseController {
    /**
     * 获得用户搜索热词
     * @param param 用户id和热词数量
     * @return 用户最新搜索的热词
     */
    @PostMapping("/userSearchHot")
    public JsonResult getUserSearchHots(@RequestBody UserIdAndNum param) {
        WxAppSessionUser user = wxAppAuth.user();
        param.setUserId(user.getUserId());
        List<HotWords> result = shop().overview.hotWordsService.getUserSearchHots(param);

        return success(result);
    }

    /**
     * 添加热词
     * @param param 用户id和热词
     */
    @PostMapping("/addHotWords")
    public JsonResult addHotWords(@RequestBody UserIdAndWords param) {
        WxAppSessionUser user = wxAppAuth.user();
        param.setUserId(user.getUserId());
        shop().overview.hotWordsService.addHotWords(param);

        return success();
    }

    /**
     * 热词排行榜（从高到低）
     * @param param 查找日期及是否热词标识
     * @return 热词及对应搜索次数
     */
    @PostMapping("/historyTop")
    public JsonResult getHistoryTop(@RequestBody DateAndWordsFlag param) {
        List<WordsAndCount> result = shop().overview.hotWordsService.getHistoryTop(param);

        return success(result);
    }

    /**
     * 清除用户搜索词
     * @param param 用户id
     */
    @PostMapping("/clearWords")
    public JsonResult clearUserHotWords(@RequestBody UserId param) {
        WxAppSessionUser user = wxAppAuth.user();
        param.setUserId(user.getUserId());
        shop().overview.hotWordsService.clearUserHotWords(param);

        return success();
    }
    /**
     * 查询 搜索配置
     * @return 搜索配置
     */
    @PostMapping("/config")
    public JsonResult getSearchCfg() {
        SearchConfig searchConfig = shop().config.searchCfg.getSearchConfig();
        if(null==searchConfig) {
            searchConfig=new SearchConfig(1, 1, 0);
        }
        return success(searchConfig);
    }
}
