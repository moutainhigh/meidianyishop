package com.meidianyi.shop.service.pojo.shop.order.invoice;

import java.sql.Timestamp;

import lombok.Data;

/**
 * 
 * @author 王帅
 *
 */
@Data
public class InvoiceVo {

	private Integer id;
	private Integer userId;
	private Byte type;
	private String title;
	private String telephone;
	private String taxnumber;
	private String companyaddress;
	private String bankname;
	private String bankaccount;
	private Timestamp createTime;
}
