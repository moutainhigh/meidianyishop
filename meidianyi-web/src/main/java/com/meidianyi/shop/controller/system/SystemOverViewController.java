package com.meidianyi.shop.controller.system;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.saas.overview.LoginRecordVo;
import org.springframework.web.bind.annotation.*;

/**
 * @author  常乐
 * @Date 2019-12-03
 */
@RestController
@RequestMapping("api/system/overView")
public class SystemOverViewController extends SystemBaseController{
    /**
     * 用户登录日志
     * @param param
     * @return
     */
    @PostMapping("/loginRecord")
    public JsonResult loginRecord(@RequestBody LoginRecordVo param){
        PageResult<LoginRecordVo> res = saas.overviewService.loginRecord(param);
        return this.success(res);
    }
}
