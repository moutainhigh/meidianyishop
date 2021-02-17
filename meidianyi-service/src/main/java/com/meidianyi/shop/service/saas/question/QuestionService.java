package com.meidianyi.shop.service.saas.question;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.main.tables.records.QfImgRecord;
import com.meidianyi.shop.db.main.tables.records.ShopAccountRecord;
import com.meidianyi.shop.db.main.tables.records.ShopChildAccountRecord;
import com.meidianyi.shop.db.main.tables.records.ShopQuestionFeedbackRecord;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.saas.question.bo.FeedbackBo;
import com.meidianyi.shop.service.pojo.saas.question.param.FeedBackParam;
import com.meidianyi.shop.service.pojo.saas.question.vo.FeedbackVo;
import com.meidianyi.shop.service.pojo.shop.auth.AdminTokenAuthInfo;
import com.meidianyi.shop.service.pojo.shop.question.FeedbackParam;
import com.meidianyi.shop.service.saas.shop.ShopService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.SelectWhereStep;
import org.jooq.impl.DSL;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.main.tables.QfImg.QF_IMG;
import static com.meidianyi.shop.db.main.tables.ShopQuestionFeedback.SHOP_QUESTION_FEEDBACK;

/**
 * @author luguangyao
 */
@Service
public class QuestionService extends MainBaseService {

    @Autowired
    private ShopService shopService;


    /**
     * 问题反馈新增
     * @param shopId 店铺id
     * @param param 反馈参数
     * @param userInfo 反馈用户信息
     */
    public void insert(Integer shopId, FeedbackParam param, AdminTokenAuthInfo userInfo){
        ShopQuestionFeedbackRecord record = buildRecord(param,shopId,userInfo);
        Integer id = insertFeedback(record);
        if(!CollectionUtils.isEmpty(param.getImgs()) ){
            batchInsertImg(buildImgRecord(param.getImgs(),id));
        }
    }

    /**
     * 问题反馈-详情
     * @param id 问题反馈id
     * @return vo
     */
    public FeedbackVo detail(Integer id){
        updateFeedbackLookedStatus(id);
        return getFeedbackDetailById(id);
    }
    /**
     * 根据问题反馈id获取反馈相关信息
     * @param id 问题反馈id
     * @return {@link FeedbackVo}
     */
    private FeedbackVo getFeedbackDetailById(Integer id){
        FeedbackBo feedbackBo = db().select()
            .from(SHOP_QUESTION_FEEDBACK)
            .where(SHOP_QUESTION_FEEDBACK.QUESTION_FEEDBACK_ID.eq(id))
            .fetchAny(r->r.into(FeedbackBo.class));
        Map<Integer,List<String>> urlMap = getQfImgByIds(Lists.newArrayList(id));
        return convertVo(feedbackBo,urlMap);
    }

    /**
     * 问题反馈分页列表
     * @param feedBackParam 筛选条件
     * @return {@link FeedbackVo}
     */
    public PageResult<FeedbackVo> getPageByParam(FeedBackParam feedBackParam){
        SelectWhereStep<?> selectWhereStep = db().select().from(SHOP_QUESTION_FEEDBACK);
        selectWhereStep.where(buildParam(feedBackParam)).orderBy(SHOP_QUESTION_FEEDBACK.CREATE_TIME.desc());

        PageResult<FeedbackBo> pageResult =
            getPageResult(selectWhereStep,feedBackParam.getCurrentPage(),feedBackParam.getPageRows(), FeedbackBo.class);

        return assemblyDataList(pageResult);
    }

    /**
     * 修改问题反馈的查看状态
     * @param id 问题反馈id
     */
    private void updateFeedbackLookedStatus(Integer id){
        db().update(SHOP_QUESTION_FEEDBACK)
            .set(SHOP_QUESTION_FEEDBACK.IS_LOOK,(byte)1)
            .where(SHOP_QUESTION_FEEDBACK.QUESTION_FEEDBACK_ID.eq(id))
            .execute();
    }
    /**
     * 把boPage封装转换为voPage
     * @param boPageResult boage
     * @return {@link FeedbackVo}
     */
    private PageResult<FeedbackVo> assemblyDataList(PageResult<FeedbackBo> boPageResult){
        PageResult<FeedbackVo> result = new PageResult<>();
        BeanUtils.copyProperties(boPageResult,result);
        List<Integer> feedbackIds = boPageResult.dataList.stream()
            .map(FeedbackBo::getQuestionFeedbackId)
            .collect(Collectors.toList());
        if( feedbackIds.isEmpty() ){
            return result;
        }
        Map<Integer,List<String>> urlMap = getQfImgByIds(feedbackIds);
        List<FeedbackVo> vos = Lists.newArrayList();
        for( FeedbackBo bo : boPageResult.getDataList() ){
            FeedbackVo vo = convertVo(bo,urlMap);
            vos.add(vo);
        }
        result.dataList = vos;
        return result;
    }

    /**
     * bo转vo
     * @param bo {@link FeedbackBo}
     * @param urlMap {@link FeedbackVo}
     * @return vo
     */
    private FeedbackVo convertVo(FeedbackBo bo,Map<Integer,List<String>> urlMap){
        FeedbackVo vo = new FeedbackVo();
        BeanUtils.copyProperties(bo,vo);
        vo.setVersion(shopService.version.getVersionNameByLevel(bo.getVersion()));
        vo.setImageUrls(urlMap.getOrDefault(vo.getQuestionFeedbackId(),Lists.newArrayList()));
        return vo;
    }

    /**
     * 根据问题反馈id集合获取对应的图片map
     * @param ids 问题反馈id集合
     * @return Map<Integer,List<String>>
     */
    private Map<Integer,List<String>> getQfImgByIds(List<Integer> ids){
        Map<Integer,List<String>> imgMap = Maps.newHashMap();
        List<QfImgRecord> qfImgRecords =db().select(QF_IMG.QUESTION_FEEDBACK_ID,QF_IMG.IMG_URL,QF_IMG.IMG_DESC)
            .from(QF_IMG)
            .where(QF_IMG.QUESTION_FEEDBACK_ID.in(ids))
            .orderBy(QF_IMG.IMG_DESC.asc())
            .fetch(r->r.into(QfImgRecord.class));
        for( QfImgRecord record : qfImgRecords ){
            Integer feedbackId= record.getQuestionFeedbackId();
            if( imgMap.containsKey(feedbackId) ){
                imgMap.get(feedbackId).add(record.getImgUrl());
            }else{
                List<String> urls = Lists.newArrayList(record.getImgUrl());
                imgMap.put(feedbackId,urls);
            }
        }
        return imgMap;
    }

    /**
     * 根据筛选条件生成对应的sql查询语句
     * @param param 筛选条件
     * @return sql查询条件
     */
    private Condition buildParam(FeedBackParam param){
        Condition condition = DSL.noCondition();
        if( StringUtils.isNotBlank(param.getName()) ){
            condition = condition.and(SHOP_QUESTION_FEEDBACK.SUBMIT_USER.like(likeValue(param.getName())));
        }
        if( null != param.getCategoryId() ){
            condition =  condition.and(SHOP_QUESTION_FEEDBACK.CATEGORY_ID.eq(param.getCategoryId()));
        }
        if( null != param.getLookType() ){
            condition = condition.and(SHOP_QUESTION_FEEDBACK.IS_LOOK.eq(param.getLookType()));
        }
        if( null != param.getStartTime() ){
            condition = condition.and(SHOP_QUESTION_FEEDBACK.CREATE_TIME.greaterOrEqual(param.getStartTime()));
        }
        if( null != param.getEndTime() ){
            condition = condition.and(SHOP_QUESTION_FEEDBACK.CREATE_TIME.lessOrEqual(param.getEndTime()));
        }
        return condition;
    }

    /**
     * 批量插入问题反馈图片
     * @param records QfImgRecord
     */
    private void batchInsertImg(List<QfImgRecord> records){
        db().batchInsert(records).execute();
    }

    /**
     * 插入问题反馈信息
     * @param record 问题反馈信息
     * @return 问题反馈id
     */
    private Integer insertFeedback(ShopQuestionFeedbackRecord record){
        return  db().insertInto(SHOP_QUESTION_FEEDBACK).set(record).
            returning().
            fetchOne().
            getQuestionFeedbackId();
    }

    /**
     * 根据相关参数生成对应的问题反馈record
     * @param param 前端传参
     * @param shopId 店铺id
     * @param userInfo 登陆用户信息
     * @return 问题反馈record
     */
    private ShopQuestionFeedbackRecord buildRecord(FeedbackParam param,Integer shopId,AdminTokenAuthInfo userInfo){
        ShopQuestionFeedbackRecord record = new ShopQuestionFeedbackRecord();
        record.setCategoryId(param.getType().intValue());
        record.setContent(param.getContent());
        record.setShopId(shopId);


        record.setSubmitUser(userInfo.getAccountName());
        if(StringUtils.isNotBlank(param.getMobile())){
            record.setMobile(param.getMobile());
        }

        Integer userId = userInfo.isSubLogin()? userInfo.getSubAccountId():userInfo.getSysId();
        if( userInfo.isSubLogin() ){
           ShopChildAccountRecord childAccountRecord =shopService.subAccount.getSubAccountInfo(userId);
           record.setSubmitUser(childAccountRecord.getAccountName());
           record.setSubmitUserPhone(childAccountRecord.getMobile());
        }else{
            ShopAccountRecord accountRecord =shopService.account.getAccountInfoForId(userId);
            record.setSubmitUser(accountRecord.getAccountName());
            record.setSubmitUserPhone(accountRecord.getMobile());
        }
        record.setVersion(shopService.getShopById(userInfo.getLoginShopId()).getShopType());

        return record;
    }

    /**
     * 建立问题反馈对应的图片record集合
     * @param imgs 图片url
     * @param id 问题反馈id
     * @return 图片record集合
     */
    private List<QfImgRecord> buildImgRecord(List<String> imgs,Integer id){
        List<QfImgRecord> result = Lists.newArrayList();
        for (int i = 0; i < imgs.size(); i++) {
            QfImgRecord record = new QfImgRecord();
            record.setImgUrl(imgs.get(i));
            record.setQuestionFeedbackId(id);
            record.setImgDesc(String.valueOf(i));
            result.add(record);
        }

        return result;
    }
}
