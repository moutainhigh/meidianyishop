package com.meidianyi.shop.service.pojo.shop.message;

import com.meidianyi.shop.db.shop.tables.UserMessage;
import lombok.Data;

import java.util.List;

/**
 * @author 赵晓东
 * @description
 * @create 2020-08-10 10:01
 **/

@Data
public class UserMainMessageVo {

    private List<UserMessageVo> userMessages;

    private Integer announcementMessageCount;

    private Integer orderMessageCount;

    private Integer imSessionMessageCount;

}
