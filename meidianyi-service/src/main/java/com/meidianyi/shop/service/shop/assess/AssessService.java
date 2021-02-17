package com.meidianyi.shop.service.shop.assess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.dao.shop.assess.AssessActivityDao;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.assess.AssessActivityListParam;
import com.meidianyi.shop.service.pojo.shop.assess.AssessActivityListVo;
import com.meidianyi.shop.service.pojo.shop.assess.AssessActivityOneParam;
import com.meidianyi.shop.service.pojo.shop.assess.AssessResultOneParam;
import com.meidianyi.shop.service.pojo.shop.assess.AssessTopicOneParam;

/**
 * 测评活动
 * 
 * @author 常乐 2019年8月15日
 */
@Service
public class AssessService extends ShopBaseService {

	@Autowired
	protected AssessActivityDao assessActivityDao;

	/**
	 * 测评活动列表
	 * 
	 * @param param
	 * @return
	 */
	public PageResult<AssessActivityListVo> getActivityList(AssessActivityListParam param) {
		PageResult<AssessActivityListVo> activityList = assessActivityDao.getActivityList(param);

		for (AssessActivityListVo list : activityList.dataList) {
			// 测评题目数
			int topicNum = assessActivityDao.countTopics(list.getId());
			// 测评结果数
			int resultNum = assessActivityDao.countResults(list.getId());
			// 反馈总数
			int recordNum = assessActivityDao.countRecords(list.getId());

			list.setTopicNum(topicNum);
			list.setResultNum(resultNum);
			list.setRecordNum(recordNum);
		}
		return activityList;
	}

	/**
	 * 获取一条测评活动信息
	 * 
	 * @param assessId
	 * @return
	 */
	public AssessActivityOneParam getOneInfo(Integer assessId) {
		return assessActivityDao.getOneInfo(assessId);
	}

	/**
	 * 编辑保存
	 * 
	 * @param param
	 * @return
	 */
	public int saveAssess(AssessActivityOneParam param) {
		return assessActivityDao.updateAssess(param);
	}

	/**
	 * 添加测评活动信息
	 * 
	 * @param param
	 * @return
	 */
	public int addAssess(AssessActivityOneParam param) {
		return assessActivityDao.insertAssess(param);
	}

	/**
	 * 停用活动
	 * 
	 * @param assessId
	 * @return
	 */
	public int pauseAssess(Integer assessId) {
		return assessActivityDao.updateAssessBlock(assessId, (byte) 1);
	}

	/**
	 * 启用活动
	 * 
	 * @param assessId
	 * @return
	 */
	public int openAssess(Integer assessId) {
		return assessActivityDao.updateAssessBlock(assessId, (byte) 0);
	}

	/**
	 * 发布活动
	 * 
	 * @param assessId
	 * @return
	 */
	public int publishAssess(Integer assessId) {
		return assessActivityDao.updateAssessPubFlag(assessId, (byte) 1);
	}

	/**
	 * 添加测评题目
	 * 
	 * @param param
	 * @return
	 */
	public int addAssessTopic(AssessTopicOneParam param) {
		return assessActivityDao.insertAssessTopic(param);
	}

	/**
	 * 获取单条测评题目信息
	 * 
	 * @param id
	 * @return
	 */
	public AssessTopicOneParam getAssessTopicOne(Integer id) {
		return assessActivityDao.getAssessTopicOne(id);
	}

	/**
	 * 测评题目编辑保存
	 * 
	 * @param param
	 * @return
	 */
	public int saveAssessTopic(AssessTopicOneParam param) {
		return assessActivityDao.updateAssessTopic(param);
	}

	/**
	 * 删除测评题目
	 * 
	 * @param id
	 * @return
	 */
	public int delAssessTopic(Integer id) {
		return assessActivityDao.deleteAssessTopic(id);
	}

	/**
	 * 添加测评结果
	 * 
	 * @param param
	 * @return
	 */
	public int addAssessResult(AssessResultOneParam param) {
		return assessActivityDao.insertAssessResult(param);
	}

}
