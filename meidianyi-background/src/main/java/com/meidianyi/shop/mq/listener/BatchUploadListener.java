package com.meidianyi.shop.mq.listener;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.mq.RabbitConfig;
import com.meidianyi.shop.db.main.tables.records.BackProcessRecord;
import com.meidianyi.shop.service.foundation.mq.handler.BaseRabbitHandler;
import com.meidianyi.shop.service.pojo.saas.shop.mp.MpAuthShopListVo;
import com.meidianyi.shop.service.pojo.shop.market.message.BatchUploadCodeParam;
import com.meidianyi.shop.service.pojo.shop.market.message.BatchUploadVo;
import com.meidianyi.shop.service.saas.SaasApplication;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.open.bean.result.WxOpenResult;

/**
 * 批量提交小程序
 * 
 * @author zhaojianqiang
 *
 *         2019年9月9日 上午9:24:10
 */
@Component
@RabbitListener(queues = { RabbitConfig.QUEUE_BATCH_UPLOAD }, containerFactory = "simpleRabbitListenerContainerFactory")
public class BatchUploadListener implements BaseRabbitHandler {

	@Autowired
	private SaasApplication saas;

	private Logger log = LoggerFactory.getLogger(getClass());

	@RabbitHandler
	public void handler(@Payload BatchUploadCodeParam param, Message message, Channel channel){
		List<MpAuthShopListVo> list = param.getList();
		// 总数
		BatchUploadVo vo = new BatchUploadVo();
		int count = list.size();
		int success = 0;
		int fail = 0;
		short progress = 0;
		begin(param.getRecId());
		setNum(vo, success, fail, count);
		Boolean isKill = true;
		for (int i = 0; i < list.size(); i++) {
			int a = i + 1;
			String appId = list.get(i).getAppId();
			String nickName = list.get(i).getNickName();
			String progressInfo = nickName + "(" + appId + ")";
			isKill = isKill(param.getRecId());
			if (!isKill) {
				// 终结了
				updateKill(param.getRecId(), "进程终止");
				log.debug("RecId "+param.getRecId()+nickName+"进程终止");
				break;
			}

			Byte mpPackageVersion = saas.shop.mp.getMpPackageVersion(appId);
			// 算进度
			progress = (short) ((new BigDecimal((float) a / count).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue())
					* 100);
			updateProgress(param.getRecId(), progress, "正在提交" + progressInfo);
			log.debug("recid" + param.getRecId() + progressInfo + " 进度" + progress + "%");
			WxOpenResult result = new WxOpenResult();

			try {
				if (!mpPackageVersion.equals(param.getPackageVersion())) {
					result.setErrcode("0");
					result.setErrmsg("该小程序暂不提交审核");
				} else {
					result = saas.shop.mp.uploadCodeAndApplyAudit(appId, param.getTemplateId());
				}
				if (result.isSuccess()) {
					success++;
				} else {
					fail++;
				}
				log.debug("提交审核" + progressInfo + "  " + result.toString());
			} catch (WxErrorException e) {
				log.debug("提交审核" + progressInfo + "失败，原因" + e.getMessage());
				fail++;

			}
		}
		if (isKill) {
			// 没有终结
			updateProgress(param.getRecId(), progress, "处理完成");
			setNum(vo, success, fail, count, 0, null);
			resultManage(vo, param.getRecId());
		}
		updateJobState(Util.toJson(list), param.getRecId(), fail, count);
	}

	@Override
	public void executeException(Object[] datas, Throwable throwable) {
		for (Object object : datas) {
			if (object instanceof BatchUploadCodeParam) {
				BatchUploadCodeParam param = (BatchUploadCodeParam) object;
				fail(param.getRecId(), "意外终止，原因：" + throwable.getMessage());
				break;
			}
		}
	}

	private void setNum(BatchUploadVo vo, Integer success, Integer fail, Integer total) {
		vo.setSuccess(success);
		vo.setFail(fail);
		vo.setTotalNum(total);
	}

	private void setNum(BatchUploadVo vo, Integer success, Integer fail, Integer total, Integer code, String message) {
		vo.setSuccess(success);
		vo.setFail(fail);
		vo.setTotalNum(total);
		vo.setCode(code);
		vo.setMessage(message);
	}

	public void resultManage(BatchUploadVo vo, Integer recId) {
		if (vo.getCode() == 0) {
			finish(recId);
		} else {
			fail(recId, null);
		}
		updateRow(recId, vo.getCode(), vo.getMessage(), Util.toJson(vo));
	}

	private void fail(Integer recId, String failReason) {
		saas.shop.backProcessService.fail(recId, failReason);
	}

	private void updateProgress(Integer recId, Short progress, String progressInfo) {
		saas.shop.backProcessService.updateProgress(recId, progress, progressInfo);
	}

	private void finish(Integer recId) {
		saas.shop.backProcessService.finish(recId);
	}

	private void updateRow(Integer recId, Integer jobCode, String jobMessage, String jobResult) {
		saas.shop.backProcessService.updateRow(recId, jobCode, jobMessage, jobResult);
	}

	private void begin(Integer recId) {
		saas.shop.backProcessService.begin(recId);
	}

	private BackProcessRecord getRecord(Integer recId) {
		return saas.shop.backProcessService.getRow(recId);
	}


    /**
     * 更新b2c_task_job_main表中数据
     *
     * @param content
     * @param recId
     * @param failSize
     * @param allSize
     */
	private void updateJobState(String content, Integer recId, int failSize, int allSize) {
		BackProcessRecord row = getRecord(recId);
		if (row != null) {
			saas.taskJobMainService.updateProgress(content, row.getProcessId(), failSize, allSize);
		}
	}

	private void updateKill(Integer recId, String failReason) {
		saas.shop.backProcessService.updateKill(recId, failReason);
	}

    /**
     * 可以运行是true，不可运行是false
     * @param recId
     * @return
     */
	private Boolean isKill(Integer recId) {
		BackProcessRecord row = getRecord(recId);
		if (row != null) {
			return saas.taskJobMainService.assertExecuting(row.getProcessId());
		}
		return true;
	}

}
