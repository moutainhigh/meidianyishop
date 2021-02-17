package com.meidianyi.shop.dao.shop.session;

import com.meidianyi.shop.common.pojo.shop.table.ImSessionItemDo;
import com.meidianyi.shop.dao.foundation.base.ShopBaseDao;
import com.meidianyi.shop.db.shop.tables.records.ImSessionItemRecord;
import com.meidianyi.shop.service.pojo.wxapp.medical.im.param.ImSessionRenderPageParam;
import org.jooq.Condition;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.meidianyi.shop.db.shop.Tables.IM_SESSION_ITEM;

/**
 * 会话处理详情Dao
 * @author 李晓冰
 * @date 2020年07月21日
 */
@Repository
public class ImSessionItemDao extends ShopBaseDao {

    /**
     * 根据sessionId获取对应的会话详情信息
     * @param sessionId
     * @return
     */
    public List<ImSessionItemDo> getBySessionId(Integer sessionId) {
        return db().selectFrom(IM_SESSION_ITEM).where(IM_SESSION_ITEM.IM_SESSION_ID.eq(sessionId))
            .fetchInto(ImSessionItemDo.class);
    }

    /**
     * 相关会话记录，分页获取会话的聊天详细内容
     * @param pageParam 分页信息
     * @return
     */
    public List<ImSessionItemDo> getRelevantSessionItemPageList(ImSessionRenderPageParam pageParam,List<Integer> relevantIds){
        Condition condition = IM_SESSION_ITEM.IM_SESSION_ID.in(relevantIds);
        return db().selectFrom(IM_SESSION_ITEM).where(condition).orderBy(IM_SESSION_ITEM.SEND_TIME.desc()).limit(pageParam.getStartLineIndex().intValue(), pageParam.getPageRows())
            .fetchInto(ImSessionItemDo.class);
    }


    public void batchInsert(List<ImSessionItemDo> imSessionItemDos) {
        List<ImSessionItemRecord> records = new ArrayList<>(imSessionItemDos.size());
        for (ImSessionItemDo imSessionItemDo : imSessionItemDos) {
            ImSessionItemRecord record =new ImSessionItemRecord();
            record.setImSessionId(imSessionItemDo.getImSessionId());
            record.setFromId(imSessionItemDo.getFromId());
            record.setToId(imSessionItemDo.getToId());
            record.setMessage(imSessionItemDo.getMessage());
            record.setType(imSessionItemDo.getType());
            record.setSendTime(imSessionItemDo.getSendTime());
            records.add(record);
        }
        db().batchInsert(records).execute();
    }
}
