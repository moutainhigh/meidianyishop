package com.meidianyi.shop.service.pojo.shop.order.write.operate.ship.batch;

import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.service.pojo.shop.auth.AdminTokenAuthInfo;
import com.meidianyi.shop.service.pojo.shop.auth.StoreTokenAuthInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

/**
 * 批量发货
 * @author 王帅
 */
@Getter
@Setter
@ToString
public class BatchShipParam {
    /**excel文件*/
    private MultipartFile file;
    /**excel类型，controller层设置*/
    private ExcelTypeEnum excelTypeEnum;
    /**语言类型，controller层设置*/
    private String lang;
    private AdminTokenAuthInfo adminInfo;
    private StoreTokenAuthInfo storeInfo;
}
