package com.meidianyi.shop.controller.admin;

import com.meidianyi.shop.common.foundation.data.JsonResult;
import com.meidianyi.shop.service.pojo.shop.question.FeedbackParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author luguangyao
 */
@RestController
@RequestMapping("/api/admin/question")
public class AdminQuestionController extends AdminBaseController {


    @PostMapping("/feedback")
    public JsonResult insertQuestion(@RequestBody FeedbackParam param){
        shop().feedbackService.insert(param);
        return success();
    }
    @PostMapping("/feedback1")
    public JsonResult insertQuestion1(@RequestBody FeedbackParam param){
//        shop().feedbackService.insert(param);
        saas.getShopApp(245547).shopTaskService.wechatTaskService.beginDailyTask();
        return success();
    }
}
