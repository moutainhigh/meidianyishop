package com.meidianyi.shop.service.pojo.shop.market.form;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liufei
 * @date 2019/8/12
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedBackInfoParam {
    /**
     * b2c_form_submit_list表所需入参
     */
    @NotNull
    private Integer pageId;
    @NotNull
    private Integer userId;

    /**
     * 反馈详细信息
     */
    private List<FeedBackDetail> detailList;
}
