package com.meidianyi.shop.service.shop.market.groupdraw;

import com.meidianyi.shop.db.shop.tables.records.GroupDrawInviteRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import org.springframework.stereotype.Service;

import static com.meidianyi.shop.db.shop.tables.GroupDrawInvite.GROUP_DRAW_INVITE;

import java.util.Map;

/**
 * 拼团抽奖邀请用户
 *
 * @author 郑保乐
 */
@Service
public class GroupDrawInviteService extends ShopBaseService {

    /** 拼团抽奖 **/
    private static final int ACTION_GROUP_DRAW = 1;

    /**
     * 获取可用邀请用户信息
     */
    GroupDrawInviteRecord getAvailableInviteUser(Integer groupDrawId, Integer goodsId, Integer userId) {
        return db().selectFrom(GROUP_DRAW_INVITE).where(GROUP_DRAW_INVITE.ACTION.eq(ACTION_GROUP_DRAW)
            .and(GROUP_DRAW_INVITE.IDENTITY_ID.eq(groupDrawId))
            .and(GROUP_DRAW_INVITE.GOODS_ID.eq(goodsId))
            .and(GROUP_DRAW_INVITE.USER_ID.eq(userId))
            .and(GROUP_DRAW_INVITE.IS_USED.eq((byte) 0))).fetchOneInto(GROUP_DRAW_INVITE);
    }
    
    
    /**
     * 创建邀请记录
     * @param path
     * @param identityId
     * @param query
     * @param isNew
     */
	public void createInviteRecord(String path, Integer identityId, Map<String, String> query, Byte isNew) {
		logger().info("创建邀请记录");
		Integer goodsId = Integer.valueOf(query.get("goods_id"));
		GroupDrawInviteRecord record = getAvailableInviteUser(identityId, goodsId,Integer.valueOf(query.get("user_id")));
		Integer groupId = Integer.valueOf(query.get("group_id"));
		Integer inviteId = Integer.valueOf(query.get("invite_id"));
		if (record != null) {
			record.setInviteUserId(inviteId);
			if (groupId != null) {
				record.setGroupId(groupId);
			}
			int update = record.update();
			logger().info("更新邀请记录" + update);
		} else {
			record = db().newRecord(GROUP_DRAW_INVITE);
			record.setIdentityId(identityId);
			record.setPath(path);
			record.setGoodsId(goodsId);
			record.setGroupId(groupId);
			record.setInviteUserId(inviteId);
			record.setUserId(Integer.parseInt(query.get("user_id")));
			record.setIsNew(isNew);
			int insert = record.insert();
			logger().info("插入邀请记录"+insert);
		}
	}
	
	public void updateInviteRow(Integer id,Byte isUsed) {
		db().update(GROUP_DRAW_INVITE).set(GROUP_DRAW_INVITE.IS_USED, isUsed).where(GROUP_DRAW_INVITE.ID.eq(id)).execute();
	}
}
