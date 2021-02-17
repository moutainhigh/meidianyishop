package com.meidianyi.shop.service.shop.decoration;

import static com.meidianyi.shop.db.shop.tables.MpJump.MP_JUMP;
import static com.meidianyi.shop.db.shop.tables.MpJumpUsable.MP_JUMP_USABLE;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.sum;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.db.shop.tables.records.MpJumpUsableRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.applets.AppletsJumpAddPrarm;
import com.meidianyi.shop.service.pojo.shop.applets.AppletsJumpUpdatePrarm;
import com.meidianyi.shop.service.pojo.shop.applets.AppletsJumpUsable;
import com.meidianyi.shop.service.pojo.shop.applets.AppletsJumpVo;

/**
 * 小程序跳转
 *
 * @author 孔德成
 * @date 2019/7/11 18:21
 */
@Service

public class AppletsJumpService extends ShopBaseService {

	private static final Integer EIGHT = 8;

	/**
	 * 添加小程序跳转
	 *
	 * @param param
	 * @return
	 */
	public int addAppletsJump(AppletsJumpAddPrarm param) {
		return db().insertInto(MP_JUMP)
				.set(MP_JUMP.APP_ID, param.getAppId())
				.set(MP_JUMP.APP_NAME, param.getAppName())
				.execute();
	}

	/**
	 * 删除小程序跳转
	 *
	 * @param param
	 * @return
	 */
	public int deleteAppletsJump(AppletsJumpUpdatePrarm param) {
		return db().update(MP_JUMP).set(MP_JUMP.DEL_FLAG, DelFlag.DISABLE.getCode()).where(MP_JUMP.ID.eq(param.getId()))
				.execute();
	}

	/**
	 * 停用或启用小程序跳转
	 *
	 * @param param
	 * @return
	 */
	public int updateProgramJump(AppletsJumpUpdatePrarm param) {
		if (param.getFlag() == 1) {
			return db().update(MP_JUMP).set(MP_JUMP.FLAG, (byte) 0).where(MP_JUMP.ID.eq(param.getId())).execute();
		}
		return db().update(MP_JUMP).set(MP_JUMP.FLAG, (byte) 1).where(MP_JUMP.ID.eq(param.getId())).execute();

	}

	/**
	 * 取已经可用的跳转小程序数量 等于9就不用提交了，必须停用或删除一部分 前8个有不可用的可以申请发布
	 *
	 * 前八个没有需要提交的
	 *
	 * @return
	 */
	public Boolean usableAppletsJumpValid() {
		int count = db().selectCount().from(MP_JUMP)
				.leftJoin(MP_JUMP_USABLE).on(MP_JUMP.APP_ID.eq(MP_JUMP_USABLE.APP_ID))
				.where(MP_JUMP.DEL_FLAG.eq((byte) 0))
				.and(MP_JUMP.FLAG.eq((byte) 0))
				.and(MP_JUMP_USABLE.USABLE.eq((byte) 0)).execute();
		if (count > EIGHT) {
			return false;
		}
		// 查前8条数据
		List<AppletsJumpUsable> jumpUsable = db()
				.select(count(MP_JUMP.ID).as("count"), sum(MP_JUMP_USABLE.USABLE).as("usableCount"))
				.from(MP_JUMP)
				.leftJoin(MP_JUMP_USABLE).on(MP_JUMP.APP_ID.eq(MP_JUMP_USABLE.APP_ID))
				.where(MP_JUMP.DEL_FLAG.eq((byte) 0))
				.and(MP_JUMP.FLAG.eq((byte) 0))
				.orderBy(MP_JUMP.CREATE_TIME.desc()).limit(8).fetch().into(AppletsJumpUsable.class);
		if (!jumpUsable.get(0).getCount().equals(jumpUsable.get(0).getUsableCount())) {
			return true;
		}
		return false;
	}

	/**
	 * 获取列表
	 * 
	 * @return
	 */
	public List<AppletsJumpVo> getAppletsJump() {
		List<AppletsJumpVo> appletsJump = db()
				.select(MP_JUMP.APP_NAME, MP_JUMP.APP_ID, MP_JUMP.FLAG, MP_JUMP_USABLE.USABLE, MP_JUMP.CREATE_TIME)
				.from(MP_JUMP)
				.leftJoin(MP_JUMP_USABLE).on(MP_JUMP.APP_ID.eq(MP_JUMP_USABLE.APP_ID))
				.where(MP_JUMP.DEL_FLAG.eq((byte) 0))
				.fetch().into(AppletsJumpVo.class);
		return appletsJump;
	}

	

	public List<String> getMpJumpAppIdList() {
		List<String> values = db().selectFrom(MP_JUMP)
				.where(MP_JUMP.DEL_FLAG.eq((byte) 0).and(MP_JUMP.FLAG.eq((byte) 0))).orderBy(MP_JUMP.CREATE_TIME)
				.fetch().getValues(MP_JUMP.APP_ID, String.class);
		List<String> returnList = new ArrayList<String>();
		// wx56c8f077de74b07c 微信购物清单中的appid
		returnList.add("wx56c8f077de74b07c");
		// wx6885acbedba59c14 快递100
		returnList.add("wx6885acbedba59c14");
		for (String value : values) {
			if (returnList.size() >= 10) {
				break;
			}
			// 去重
			if (!returnList.contains(value)) {
				returnList.add(value);
			}
		}
		return returnList;
	}

	/**
	 * 上传代码保存小程序跳转的提交的appid 版本号，appid ,状态
	 * 
	 * @param jumpList
	 * @param templateId
	 */
	public void saveMpJumpAppIdList(List<String> jumpList, Integer templateId) {
		// TODO 状态usable插不插？？？
		if (jumpList.size() == 0) {
			// 为空
			db().insertInto(MP_JUMP_USABLE).set(MP_JUMP_USABLE.APP_ID, "").set(MP_JUMP_USABLE.TEMPLATE_ID, templateId)
					.execute();
		} else {
			for (String appId : jumpList) {
				MpJumpUsableRecord fetchAny = db().selectFrom(MP_JUMP_USABLE).where(MP_JUMP_USABLE.APP_ID.eq(appId))
						.fetchAny();
				MpJumpUsableRecord record = db().newRecord(MP_JUMP_USABLE);
				record.setAppId(appId);
				record.setTemplateId(templateId);
				if (fetchAny == null) {
					// 插入
					db().executeInsert(record);
				} else {
					// 更新
					db().executeUpdate(record);
				}
			}
		}
	}
	
	/**
     * 调用更新小程序跳转appid可用状态
     * 审核更新跳转小程序的appid为不可用，在更新本版本提交的appid为可用
     * @param templateId
     */
	public void updateMpJumpAppIdList(Integer templateId) {
		db().update(MP_JUMP_USABLE).set(MP_JUMP_USABLE.USABLE, (byte) 0)
				.set(MP_JUMP_USABLE.UPDATE_TIME, new Timestamp(System.currentTimeMillis())).execute();
		db().update(MP_JUMP_USABLE).set(MP_JUMP_USABLE.USABLE, (byte) 1)
				.set(MP_JUMP_USABLE.UPDATE_TIME, new Timestamp(System.currentTimeMillis()))
				.where(MP_JUMP_USABLE.TEMPLATE_ID.eq(templateId)).execute();
	}
}
