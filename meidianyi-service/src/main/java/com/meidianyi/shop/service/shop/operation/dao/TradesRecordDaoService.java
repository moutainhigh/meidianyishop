package com.meidianyi.shop.service.shop.operation.dao;

import static com.meidianyi.shop.db.shop.Tables.TRADES_RECORD;



import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.operation.TradeOptParam;
import com.meidianyi.shop.service.pojo.shop.operation.builder.TradesRecordRecordBuilder;

/**
* @author 黄壮壮
* @Date: 2019年10月24日
* @Description: 对交易记录的操作
*/
@Service
public class TradesRecordDaoService extends ShopBaseService{

	/**
	 * 插入交易记录
	 */
	public void insertTradesRecord(TradeOptParam tradeOpt) {

		int res = TradesRecordRecordBuilder
			.create(db().newRecord(TRADES_RECORD))
			.tradeNum(tradeOpt.getTradeNum())
			.tradeSn(tradeOpt.getTradeSn())
			.userId(tradeOpt.getUserId())
			.tradeContent(tradeOpt.getTradeContent())
			.tradeType(tradeOpt.getTradeType())
			.tradeFlow(tradeOpt.getTradeFlow())
			.tradeStatus(tradeOpt.getTradeStatus())
			.tradeTime(DateUtils.getLocalDateTime())
			.build()
			.insert();
		logger().info(String.format("成功插入%d条交易记录", res));
	}
}
