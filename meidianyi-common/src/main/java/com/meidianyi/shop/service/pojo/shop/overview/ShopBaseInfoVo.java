package com.meidianyi.shop.service.pojo.shop.overview;

import com.meidianyi.shop.service.pojo.shop.image.ShareQrCodeVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Map;

/**
 * @author liufei
 * date 2019/7/16
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopBaseInfoVo {
    private Timestamp expireTime;
    private Map<String, String> version;
    private BindofficialVo bindInfo;
    private ShareQrCodeVo shareQrCodeVo;
}
