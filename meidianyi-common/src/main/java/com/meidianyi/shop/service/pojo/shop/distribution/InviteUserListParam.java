package com.meidianyi.shop.service.pojo.shop.distribution;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author changle
 * @date 2020/6/27 6:10 下午
 */
@Data
public class InviteUserListParam {
    /** 用户Id*/
    private Integer userId;
    /** 用户昵称*/
    private String username;
    /** 用户头像*/
    private String userAvatar;;
    /** 等级ID*/
    private List<Byte> distributorLevels = new ArrayList<>();
    /** 排序字段 inviteTime：邀请时间；orderNumber：订单；totalFanliMoney：返利佣金*/
    private String sortField = "inviteTime";
    /** 排序类型 asc:升序；desc:降序*/
    private String sortType = "asc";
    /** 邀请用户类型；0：有效用户；1：即将过期用户；2：已失效用户*/
    private Integer inviteUserStatus;
    /** 邀请关系 0：全部；1：直接邀请；2：间接邀请*/
    private Integer inviteType;
    /** 有效期 0：保护有效期剩余不超过10天；1：返利有效期不超过10天；*/
    private Integer validityDate;
    /** 当前页*/
    private Integer currentPage;
    /** 每页展示条数*/
    private Integer pageRows;
}
