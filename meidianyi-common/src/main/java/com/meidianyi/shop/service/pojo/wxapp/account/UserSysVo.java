package com.meidianyi.shop.service.pojo.wxapp.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 同步返回类
 * @author zhaojianqiang
 * @time   下午2:44:10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSysVo {
	/**  用户总数 */
	private Integer sum;
	/**  更新成功总数 */
	private Integer updateSuccess;
	/**  插入成功总数 */
	private Integer insertSuccess;
	/**  更新失败总数 */
	private Integer updateFail;
	/**  插入失败总数 */
	private Integer insertFail;
}
