package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.shop.overview.Tuple2;
import com.meidianyi.shop.service.pojo.shop.overview.realtime.CoreIndicatorParam;
import com.meidianyi.shop.service.pojo.shop.overview.realtime.CoreIndicatorVo;
import com.meidianyi.shop.service.pojo.shop.overview.realtime.RealTimeVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * The type Admin real time overview controller.
 *
 * @author liufei
 * @date 2019 /7/19
 */
@RestController
public class AdminRealTimeOverviewController extends AdminBaseController {

    /**
     * 实时概况
     *
     * @param param the param
     * @return json result
     */
    @PostMapping("/api/admin/realtimeoverview/realTime")
    public JsonResult realTime(@RequestBody @Validated CoreIndicatorParam param){
        RealTimeVo realTimeVo = shop().realTimeOverview.realTime();
        CoreIndicatorVo vo = shop().realTimeOverview.coreIndicator(param);
        Tuple2<RealTimeVo,CoreIndicatorVo> tuple = new Tuple2<>(realTimeVo,vo);
        return success(tuple);
    }

    /**
     * 核心指标
     *
     * @param param the param
     * @return json result
     */
    @PostMapping("/api/admin/realtimeoverview/coreIndicator")
    public JsonResult coreIndicator(@RequestBody @Validated CoreIndicatorParam param){
        CoreIndicatorVo vo = shop().realTimeOverview.coreIndicator(param);
        return vo !=null ? success(vo) : fail();
    }
}
