package com.meidianyi.shop.service.pojo.saas.offical;



import lombok.Data;

/**
 * @author 黄壮壮
 * 2019-07-19 09:42
 */
@Data
public class FreeExperiencePageListParam {

	
	public String company;
	public String contact;
	public String startTime;
	public String endTime;
	public Integer provinceId;
	public Integer searchShopId;
	
	/** 当前页 */
	public Integer currentPage;
	/** 每页最多显示项数 */
	public Integer pageRow;
	
	/** 
	 *  已处理： 1 ； 未处理： 0
	 */
	public Byte isDeal;   
}
