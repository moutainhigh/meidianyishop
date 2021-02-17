package com.meidianyi.shop.service.pojo.shop.prescription;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * @Description
 * @Author zhaoxiaodong
 * @create 2020-07-16 14:34
 */

@Data
@NoArgsConstructor
public class FetchPrescriptionOneParam {
    @NonNull
    private String prescriptionCode;
}
