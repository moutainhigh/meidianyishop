package com.meidianyi.shop.service.pojo.shop.store.comment;

import com.meidianyi.shop.service.pojo.wxapp.store.ValidCon;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author 黄荣刚
 * @date 2019年7月18日
 *
 */
@Data
@NoArgsConstructor
public class ServiceCommentVo {
	/**评论ID */
	private Integer id;
	/** 门店ID */
    private Integer storeId;
	/** 订单编号 */
    @NotBlank(groups = ValidCon.class)
    private String orderSn;

	/** 服务ID */
    private Integer serviceId;
	/** 服务主图 */
	private String serviceImg;
	/** 服务名称 */
	private String serviceName;
    private String serviceDate;
    private String servicePeriod;

    /** 用户ID*/
    @PositiveOrZero(groups = ValidCon.class)
    private Integer userId;
	/** 用户名*/
	private String username;
	/** 用户手机号 */
	private String mobile;
    private String userAvatar;



    /** 评价星级 */
    @NotNull(groups = ValidCon.class)
    private Byte commstar;
	/** 评论内容 */
    @NotBlank(groups = ValidCon.class)
    private String commNote;
	/** 评论图片*/
	private String commImg;
	private List<String> commImgList;

    /** 技师ID*/
	private Integer technicianId;
	/** 技师名称 */
	private String technicianName;
    private String technicianTitle;

    /** 评论创建时间 */
	private Timestamp createTime;


    /** 匿名评价  0.未匿名；1.匿名 */
	private Byte anonymousflag;

	/** 0:未审批,1:审批通过,2:审批未通过 */
	private Byte flag;

}
