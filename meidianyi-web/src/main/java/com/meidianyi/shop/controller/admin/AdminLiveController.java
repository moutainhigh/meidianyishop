package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;
import com.meidianyi.shop.service.pojo.shop.market.live.LiveCheckVo;
import com.meidianyi.shop.service.pojo.shop.market.live.LiveListParam;
import com.meidianyi.shop.service.pojo.shop.market.live.LiveListVo;
import com.meidianyi.shop.service.pojo.shop.market.live.LiveRomeGoodListVo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小程序直播
 * 
 * @author zhaojianqiang
 * @time 下午4:12:34
 */
@RestController
public class AdminLiveController extends AdminBaseController {

	/**
	 * 直播列表
	 * @param param
	 * @return
	 */
	@PostMapping("/api/admin/market/live/list")
	public JsonResult getLiveList(@RequestBody LiveListParam param) {
		LiveCheckVo checkLive = saas.shop.mp.checkLive(shopId());
		if(checkLive.getIsAuthLive()) {
			return success(checkLive);
		}
		PageResult<LiveListVo> pageList = shop().liveService.getList(param);
		checkLive.setPageList(pageList);
		return success(checkLive);
	}

    /**
     *  商品编辑处获取直播间信息
     * @return
     */
	@PostMapping("/api/admin/market/goods/edit/live/list")
	public JsonResult getLiveListForGoodsEdit(@RequestBody BasePageParam pageParam){
        LiveCheckVo checkLive = saas.shop.mp.checkLive(shopId());
        if(checkLive.getIsAuthLive()) {
            return success(checkLive);
        }
        PageResult<LiveListVo> pageList = shop().liveService.getListForGoodsEdit(pageParam);
        checkLive.setPageList(pageList);
        return success(checkLive);
    }

	/**
	 * 商品列表
	 * @param id
	 * @return
	 */
	@GetMapping("/api/admin/market/live/goodList/{id}")
	public JsonResult getGoodsList(@PathVariable Integer id) {
		List<LiveRomeGoodListVo> goodsList = shop().liveService.liveGoods.packageGoodsList(id);
		return success(goodsList);

	}

}
