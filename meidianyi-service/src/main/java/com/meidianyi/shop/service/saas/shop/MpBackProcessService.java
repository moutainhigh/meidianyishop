package com.meidianyi.shop.service.saas.shop;

import static com.meidianyi.shop.db.main.tables.BackProcess.BACK_PROCESS;

import org.jooq.SelectWhereStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.main.tables.records.BackProcessRecord;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpUploadListParam;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpUploadListVo;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpVersionVo;

/**
 * 
 * @author zhaojianqiang
 *
 *         2019年9月6日 下午4:16:36
 */
@Service
public class MpBackProcessService extends MainBaseService {
	/**
	 * 0初始，1执行中，2完成，3失败,4终止
	 */
    private static final byte  STATE_RUN_INIT = 0;
    private static final byte  STATE_RUN_EXEC = 1;
    private static final byte  STATE_RUN_FINISH = 2;
    private static final byte  STATE_RUN_FAILED = 3;
    private static final byte  STATE_RUN_STOP = 4;
	

	public int updateProgress(Integer recId, Short progress, String progressInfo) {
		if (!StringUtils.isEmpty(progressInfo)) {
			return db().update(BACK_PROCESS).set(BACK_PROCESS.PROGRESS, progress)
					.set(BACK_PROCESS.PROGRESS_INFO, progressInfo).where(BACK_PROCESS.REC_ID.eq(recId)).execute();
		} else {
			return db().update(BACK_PROCESS).set(BACK_PROCESS.PROGRESS, progress).where(BACK_PROCESS.REC_ID.eq(recId))
					.execute();
		}
	}

	public int fail(Integer recId, String failReason) {
		return db().update(BACK_PROCESS).set(BACK_PROCESS.FAIL_REASON, failReason).set(BACK_PROCESS.STATE,STATE_RUN_FAILED).set(BACK_PROCESS.END_TIME,DSL.currentTimestamp())
				.where(BACK_PROCESS.REC_ID.eq(recId)).execute();
	}

	public int insertRow(BackProcessRecord record) {
		return db().executeInsert(record);
	}
	
	public int finish(Integer recId) {
		return updateStatus(recId, STATE_RUN_FINISH);
	}
	
	public int updateRow(Integer recId, Integer jobCode, String jobMessage,String jobResult) {
		return db().update(BACK_PROCESS).set(BACK_PROCESS.JOB_CODE, jobCode).set(BACK_PROCESS.END_TIME,DSL.currentTimestamp())
				.set(BACK_PROCESS.JOB_MESSAGE, jobMessage).set(BACK_PROCESS.JOB_RESULT, jobResult).where(BACK_PROCESS.REC_ID.eq(recId)).execute();
	}
	
	public int updateKill(Integer recId,String failReason) {
		return db().update(BACK_PROCESS).set(BACK_PROCESS.FAIL_REASON, failReason).set(BACK_PROCESS.STATE,STATE_RUN_STOP)
				.where(BACK_PROCESS.REC_ID.eq(recId)).execute();
	}
	
	public int updateStatus(Integer recId,byte state) {
		return db().update(BACK_PROCESS).set(BACK_PROCESS.STATE, state).set(BACK_PROCESS.END_TIME,DSL.currentTimestamp()).where(BACK_PROCESS.REC_ID.eq(recId)).execute();
	}

	public int begin(Integer recId) {
		return updateStatus(recId, STATE_RUN_EXEC);
	}
	/**
	 * 返回主键
	 * 
	 * @param vo
	 * @param className
	 * @param processId
	 * @return
	 */
	public int insertByInfo(MpVersionVo vo, String className, Integer processId) {
		BackProcessRecord bProcessRecord = db().newRecord(BACK_PROCESS);
		bProcessRecord.setShopId(0);
		bProcessRecord.setProcessId(processId);
		bProcessRecord.setClassName(className);
		bProcessRecord.setParameters(Util.toJson(vo));
		bProcessRecord.setState(STATE_RUN_INIT);
		bProcessRecord.setOnlyRunOne((byte) 1);
		bProcessRecord.setProgress((short) 0);
		bProcessRecord.setProgressInfo("开始提交");
		bProcessRecord.setJobName("批量提交小程序审核");
		bProcessRecord.insert();
		return bProcessRecord.getRecId();
	}
	
	/**
	 * 查询任务列表
	 * 查询所有传 -1
	 * @param state
	 * @return
	 */
	public PageResult<MpUploadListVo> getPageList(MpUploadListParam param) {
		SelectWhereStep<BackProcessRecord> selectFrom = db().selectFrom(BACK_PROCESS);
		Byte state=param.getState();
		if (!StringUtils.isEmpty(state)) {
			if (state != -1) {
				selectFrom.where(BACK_PROCESS.STATE.eq(state));
			}
			selectFrom.orderBy(BACK_PROCESS.CREATED.desc());
			return  this.getPageResult(selectFrom, param.getCurrentPage(), param.getPageRows(), MpUploadListVo.class);
		}
		return null;
	}
	
	
	public int updateProcessId(Integer recId, Integer processId) {
		return db().update(BACK_PROCESS).set(BACK_PROCESS.PROCESS_ID, processId).where(BACK_PROCESS.REC_ID.eq(recId)).execute();
	}
	
	public BackProcessRecord getRow(Integer recId) {
		 return db().selectFrom(BACK_PROCESS).where(BACK_PROCESS.REC_ID.eq(recId)).fetchAny();
	}
}
