package com.meidianyi.shop.service.pojo.shop.message;

import lombok.Data;
import org.bouncycastle.util.Times;

import java.sql.Timestamp;

/**
 * @author 赵晓东
 * @description
 * @create 2020-07-31 14:57
 **/

@Data
public class DoctorMainShowParam {

    /**
     * 上次打开已续方页面时间
     */
    private Timestamp lastReadOrderGoodsTime;
    /**
     * 上次打开已开具页面时间
     */
    private Timestamp lastReadPrescriptionTime;
    /**
     * 上次打开我的会话页面时间
     */
    private Timestamp lastReadImSession;

}
