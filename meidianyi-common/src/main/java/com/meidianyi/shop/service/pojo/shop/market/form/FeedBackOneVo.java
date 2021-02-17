package com.meidianyi.shop.service.pojo.shop.market.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author liufei
 * @date 2019/8/9
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedBackOneVo {
    private String curIdx;
    private Integer totalVotes;
    private Byte confirm;
    private Byte showTypes;
    private List<FeedBackInnerVo> innerVo;

}
