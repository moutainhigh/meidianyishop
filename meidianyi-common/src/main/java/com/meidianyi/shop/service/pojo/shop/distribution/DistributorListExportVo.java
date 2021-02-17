package com.meidianyi.shop.service.pojo.shop.distribution;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelIgnore;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author changle
 * date:2020-05-28 16:20
 */
@ExcelSheet
@Data
public class DistributorListExportVo {
    @ExcelIgnore
    private Integer inviteId;
    @ExcelIgnore
    private Integer userId;
    /**分销员昵称*/
    @ExcelColumn(columnName = JsonResultMessage.DISTRIBUTOR_LIST_DISTRIBUTOR_NAME,columnIndex = 0)
    private String distributorName;

    /**分销员手机号*/
    @ExcelColumn(columnName = JsonResultMessage.DISTRIBUTOR_LIST_DISTRIBUTOR_MOBILE,columnIndex = 1)
    private String distributorMobile;

    /**分销员真实姓名*/
    @ExcelColumn(columnName = JsonResultMessage.DISTRIBUTOR_LIST_DISTRIBUTOR_REAL_NAME,columnIndex = 2)
    private String distributorRealName;

    /**分销员标签*/
    @ExcelColumn(columnName = JsonResultMessage.DISTRIBUTOR_LIST_DISTRIBUTOR_TAGS,columnIndex = 3)
    private String distributorTags;

    /**分销员注册时间*/
    @ExcelColumn(columnName = JsonResultMessage.DISTRIBUTOR_LIST_CREATE_TIME,columnIndex = 4)
    private Timestamp createTime;

    /**审核通过时间*/
    @ExcelColumn(columnName = JsonResultMessage.DISTRIBUTOR_LIST_CHECK_TIME,columnIndex = 5)
    private Timestamp checkTime;

    /**邀请人昵称*/
    @ExcelColumn(columnName = JsonResultMessage.DISTRIBUTOR_LIST_INVITE_NAME,columnIndex = 6)
    private String inviteName;

    /**邀请人真实姓名*/
    @ExcelColumn(columnName = JsonResultMessage.DISTRIBUTOR_LIST_INVITE_REAL_NAME,columnIndex = 7)
    private String inviteRealName;

    /**邀请人手机号*/
    @ExcelColumn(columnName = JsonResultMessage.DISTRIBUTOR_LIST_INVITE_MOBILE,columnIndex = 8)
    private String inviteMobile;

    /**分销员分组*/
    @ExcelColumn(columnName = JsonResultMessage.DISTRIBUTOR_LIST_GROUP_NAME,columnIndex = 9)
    private String groupName;

    /**分销员等级*/
    @ExcelColumn(columnName = JsonResultMessage.DISTRIBUTOR_LIST_LEVEL_NAME,columnIndex = 10)
    private String levelName;

    /**下级用户数*/
    @ExcelColumn(columnName = JsonResultMessage.DISTRIBUTOR_LIST_SUBLAYER_NUMBER,columnIndex = 11)
    private String sublayerNumber;

    /**间接邀请用户数*/
    @ExcelColumn(columnName = JsonResultMessage.DISTRIBUTOR_LIST_NEXT_NUMBER,columnIndex = 12)
    private Integer nextNumber;

    /**累计获得佣金金额*/
    @ExcelColumn(columnName = JsonResultMessage.DISTRIBUTOR_LIST_TOTAL_FANLI_MONEY,columnIndex = 13)
    private BigDecimal totalFanliMoney;

    /**待返利佣金金额*/
    @ExcelColumn(columnName = JsonResultMessage.DISTRIBUTOR_LIST_WAIT_FANLI_MONEY,columnIndex = 14)
    private BigDecimal waitFanliMoney;

    /**邀请码*/
    @ExcelColumn(columnName = JsonResultMessage.DISTRIBUTOR_LIST_INVITATION_CODE,columnIndex = 15)
    private String invitationCode;

    /**备注*/
    @ExcelColumn(columnName = JsonResultMessage.DISTRIBUTOR_LIST_REMARK,columnIndex = 16)
    private String remark;
}
