package com.meidianyi.shop.service.saas.shop;

import static com.meidianyi.shop.db.main.tables.MpJumpVersion.MP_JUMP_VERSION;

import org.jooq.Record1;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.service.foundation.service.MainBaseService;

/**
 * @author lixinguo
 */
@Service
public class MpJumpVersionService extends MainBaseService {

	/**
	 * 提交版本申请
	 *
	 * @return
	 */
	public int appletsJumpAddVersion(Integer shopId) {
		// 申请成功，审核即将提交，请等待
		return db().insertInto(MP_JUMP_VERSION, MP_JUMP_VERSION.SHOP_ID).values(String.valueOf(shopId)).execute();
	}

	/**
	 * 查询最新的一跳申请记录
	 * 
	 * @return
	 */
	public int getAppletsJumpAddVersion(Integer shopId) {
		Record1<Integer> result = db().selectCount()
				.from(MP_JUMP_VERSION)
				.where(MP_JUMP_VERSION.SHOP_ID.eq(String.valueOf(shopId)))
				.and(MP_JUMP_VERSION.FLAG.eq((byte) 0))
				.orderBy(MP_JUMP_VERSION.ID.desc()).fetchAny();
		return result.value1();
	}

	/**
	 *  最新提交审核的记录为已发布
	 * @param shopId
	 * @return
	 */
	public int updateMpJumpVersion(Integer shopId) {
		return db().update(MP_JUMP_VERSION).set(MP_JUMP_VERSION.FLAG, (byte) 1)
				.where(MP_JUMP_VERSION.SHOP_ID.eq(shopId.toString())).execute();
	}
}
