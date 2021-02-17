package com.meidianyi.shop.service.pojo.shop.rebate;

import com.meidianyi.shop.common.pojo.shop.table.DoctorWithdrawDo;
import lombok.Data;

/**
 * @author yangpengcheng
 * @date 2020/8/27
 **/
@Data
public class DoctorWithdrawVo extends DoctorWithdrawDo {
    private String doctorName;
    private String mobile;
}
