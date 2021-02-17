package com.meidianyi.shop.service.pojo.wxapp.order.inquiry.vo;

import com.meidianyi.shop.common.pojo.shop.table.InquiryOrderDo;
import com.meidianyi.shop.common.pojo.shop.table.InquiryOrderRebateDo;
import com.meidianyi.shop.common.pojo.shop.table.InquiryOrderRefundListDo;
import com.meidianyi.shop.service.pojo.shop.rebate.InquiryOrderRebateVo;
import lombok.Data;

import java.util.List;

/**
 * @author yangpengcheng
 * @date 2020/7/27
 **/
@Data
public class InquiryOrderDetailVo extends InquiryOrderDo {
    private String userName;
    private String userMobile;
    private Integer patientAge;
    private List<String> imgUrlList;
    private List<InquiryOrderRefundListDo> refundList;
    private InquiryOrderRebateVo rebate;
}
