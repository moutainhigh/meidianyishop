package com.meidianyi.shop.service.pojo.shop.user.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * data数据，根据二级类目id进行扩充，全为date+类目id形式，方便后面进行反射操作；
 * 类目参考SubscribeMessageConfig中，按照从上到下顺序进行;
 * 比如audit在三个类目里都有，但是对应发送数据不同，需要在三个data中都塞入值
 * 
 * @author zhaojianqiang
 * @time 上午9:27:33
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaSubscribeData {
	private String[][] data307;
	private String[][] data321;
	private String[][] data786;
	private String[][] data47;
	private String[][] data411;
}
