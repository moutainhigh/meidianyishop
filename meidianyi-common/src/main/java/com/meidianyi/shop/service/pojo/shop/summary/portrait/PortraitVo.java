package com.meidianyi.shop.service.pojo.shop.summary.portrait;

import lombok.Data;

/**
 * 用户画像出参
 *
 * @author 郑保乐
 */
@Data
public class PortraitVo {

    private Portrait activeUser;
    private PortraitSum activeUserSum;
    private Portrait newAddUser;
    private PortraitSum newAddUserSum;
    private String startDate;
    private String endDate;
    private PortraitMaxAndMin activeProRange;
    private PortraitMaxAndMin newAddUserProRange;
}
