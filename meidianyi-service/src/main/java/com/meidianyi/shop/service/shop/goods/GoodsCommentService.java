package com.meidianyi.shop.service.shop.goods;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.shop.tables.CommentGoods;
import com.meidianyi.shop.db.shop.tables.records.CommentGoodsRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.coupon.give.CouponGiveQueueParam;
import com.meidianyi.shop.service.pojo.shop.goods.comment.*;
import com.meidianyi.shop.service.pojo.shop.member.account.AccountParam;
import com.meidianyi.shop.service.pojo.shop.member.account.ScoreParam;
import com.meidianyi.shop.service.pojo.shop.operation.RemarkTemplate;
import com.meidianyi.shop.service.pojo.shop.operation.TradeOptParam;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.wxapp.comment.*;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.CommentDetailVo;
import com.meidianyi.shop.service.saas.comment.CommentSwitch;
import com.meidianyi.shop.service.shop.activity.dao.GoodsCommentProcessorDao;
import com.meidianyi.shop.service.shop.config.CommentConfigService;
import com.meidianyi.shop.service.shop.coupon.CouponGiveService;
import com.meidianyi.shop.service.shop.goods.mp.MpGoodsRecommendService;
import com.meidianyi.shop.service.shop.member.AccountService;
import com.meidianyi.shop.service.shop.member.ScoreService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.jooq.tools.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static com.meidianyi.shop.db.shop.Tables.*;
import static org.apache.commons.lang3.math.NumberUtils.BYTE_ONE;
import static org.apache.commons.lang3.math.NumberUtils.BYTE_ZERO;

/**
 * 商品评价
 *
 * @author liangchen
 * @date 2019年7月7日
 */
@Service
public class GoodsCommentService extends ShopBaseService {
    @Autowired
    private CommentConfigService commentConfigService;
    @Autowired
    private CommentSwitch commentSwitch;
    @Autowired
    private CouponGiveService couponGiveService;
    @Autowired
    private ScoreService scoreService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private MpGoodsRecommendService mpGoodsRecommendService;
    @Autowired
    private GoodsCommentProcessorDao goodsCommentProcessorDao;
    private static final int THREE = 3;
    private static final int FOUR = 4;
    private static final int FIVE = 5;
    private static final byte GET_SOURCE = 3;
    /**
     * 审核配置对应码  0不审，1先发后审，2先审后发
     */
    private static final Byte NOT_AUDIT = 0;
    private static final Byte PUBLISH_FIRST = 1;
    private static final Byte CHECK_FIRST = 2;
    /**
     * 隐藏未填写心得评论配置值
     */
    private static final Byte NOT_SHOW_STATE = 1;
    /***商品评价状态对应码*/
    private static final Byte PASS_AUDIT = 1;
    private static final Byte NOT_PASS_AUDIT = 2;
    /**
     * 筛选条件
     */
    private static final Byte GOOD_TYPE = 1;
    private static final Byte MID_TYPE = 2;
    private static final Byte BAD_TYPE = 3;
    private static final Byte PIC_TYPE = 4;
    /**
     * 匿名标识
     */
    private static final Byte ANONYMOUS_FLAG = 0;
    public static final Condition TRUE_CONDITION = DSL.trueCondition();

    /**
     * 分页获取评价信息
     *
     * @param param 条件查询参数
     * @return 商品评价信息
     */
    public GoodsCommentListVo getPageList(GoodsCommentPageListParam param) {
        GoodsCommentListVo listVo = new GoodsCommentListVo();
        //查询内容
        SelectConditionStep<? extends Record> select = selectGoodsComments();
        //添加查询条件
        this.buildOptions(select, param);
        //从新到旧排序
        select.orderBy(COMMENT_GOODS.CREATE_TIME.desc());
        //整合分页信息
        PageResult<GoodsCommentVo> pageResult =
            this.getPageResult(
                select, param.getCurrentPage(), param.getPageRows(), GoodsCommentVo.class);
        //遍历当前分页结果，查询优惠券名称
        for (GoodsCommentVo vo : pageResult.dataList) {
            if (vo.getAwardType() != null && vo.getAwardType().equals(NumberUtils.INTEGER_TWO)) {
                Integer activityId = db().select(COMMENT_AWARD.ACTIVITY_ID)
                    .from(COMMENT_AWARD)
                    .where(COMMENT_AWARD.ID.eq(vo.getCommentAwardId()))
                    .fetchOneInto(Integer.class);
                String actName = db().select(MRKING_VOUCHER.ACT_NAME)
                    .from(MRKING_VOUCHER)
                    .where(MRKING_VOUCHER.ID.eq(activityId))
                    .fetchOneInto(String.class);
                vo.setActName(actName);
            }
        }
        listVo.setPage(pageResult.getPage());
        listVo.setDataList(pageResult.getDataList());
        Integer allNum = getNum(new GoodsCommentPageListParam() {{
            setCurrentPage(1);
            setPageRows(9999999);
        }});
        listVo.setAllNum(allNum);
        Integer toDoNum = getNum(new GoodsCommentPageListParam() {{
            setFlag((byte) 0);
            setCurrentPage(1);
            setPageRows(9999999);
        }});
        listVo.setToDoNum(toDoNum);
        Integer passNum = getNum(new GoodsCommentPageListParam() {{
            setFlag((byte) 1);
            setCurrentPage(1);
            setPageRows(9999999);
        }});
        listVo.setPassNum(passNum);
        Integer refuseNum = getNum(new GoodsCommentPageListParam() {{
            setFlag((byte) 2);
            setCurrentPage(1);
            setPageRows(9999999);
        }});
        listVo.setRefuseNum(refuseNum);
        Integer topNum = getNum(new GoodsCommentPageListParam() {{
            setIsTop((byte) 1);
            setCurrentPage(1);
            setPageRows(9999999);
        }});
        listVo.setTopNum(topNum);
        Integer showNum = getNum(new GoodsCommentPageListParam() {{
            setIsShow((byte) 1);
            setCurrentPage(1);
            setPageRows(9999999);
        }});
        listVo.setShowNum(showNum);
        return listVo;
    }

    private SelectConditionStep<? extends Record> selectGoodsComments() {
        return db().select(
            COMMENT_GOODS.ID,
            COMMENT_GOODS.ORDER_SN,
            COMMENT_GOODS.COMMSTAR,
            COMMENT_GOODS.COMM_NOTE,
            COMMENT_GOODS.COMM_IMG,
            COMMENT_GOODS.CREATE_TIME,
            COMMENT_GOODS.ANONYMOUSFLAG,
            COMMENT_GOODS.BOGUS_USERNAME,
            COMMENT_GOODS.COMMENT_AWARD_ID,
            COMMENT_GOODS.IS_TOP,
            COMMENT_GOODS.IS_SHOW,
            GOODS.GOODS_NAME,
            GOODS.GOODS_IMG,
            USER.USER_ID,
            USER.USERNAME,
            USER.MOBILE,
            COMMENT_AWARD.AWARD_TYPE,
            COMMENT_AWARD.SCORE,
            COMMENT_AWARD.ACCOUNT,
            COMMENT_GOODS_ANSWER.CONTENT,
            COMMENT_GOODS.FLAG,
            GOODS_SPEC_PRODUCT.PRD_DESC)
            .from(COMMENT_GOODS)
            .leftJoin(GOODS)
            .on(GOODS.GOODS_ID.eq(COMMENT_GOODS.GOODS_ID))
            .leftJoin(USER)
            .on(USER.USER_ID.eq(COMMENT_GOODS.USER_ID))
            .leftJoin(COMMENT_AWARD)
            .on(COMMENT_GOODS.COMMENT_AWARD_ID.eq(COMMENT_AWARD.ID))
            .leftJoin(COMMENT_GOODS_ANSWER)
            .on(COMMENT_GOODS.ID.eq(COMMENT_GOODS_ANSWER.COMMENT_ID))
            .leftJoin(GOODS_SPEC_PRODUCT)
            .on(COMMENT_GOODS.PRD_ID.eq(GOODS_SPEC_PRODUCT.PRD_ID))
            .where(COMMENT_GOODS.DEL_FLAG.eq(GoodsCommentPageListParam.IS_DELETE_DEFAULT_VALUE));
    }

    public Integer getNum(GoodsCommentPageListParam param) {
        //查询内容
        SelectConditionStep<? extends Record> select = selectGoodsComments();
        //添加查询条件
        this.buildOptions(select, param);
        //从新到旧排序
        select.orderBy(COMMENT_GOODS.CREATE_TIME.desc());
        //整合分页信息
        PageResult<GoodsCommentVo> pageResult =
            this.getPageResult(
                select, param.getCurrentPage(), param.getPageRows(), GoodsCommentVo.class);
        return pageResult.getDataList().size();
    }

    /**
     * 根据过滤条件构造对应的sql语句
     *
     * @param select 查询语句
     */
    private void buildOptions(
        SelectConditionStep<? extends Record> select, GoodsCommentPageListParam param) {
        //根据订单编号查询
        if (!StringUtils.isBlank(param.getOrderSn())) {
            select.and(COMMENT_GOODS.ORDER_SN.like(this.likeValue(param.getOrderSn())));
        }
        //根据商品名称查询
        if (!StringUtils.isBlank(param.getGoodsName())) {
            select.and(GOODS.GOODS_NAME.like(this.likeValue(param.getGoodsName())));
        }
        //根据手机号查询
        if (!StringUtils.isBlank(param.getMobile())) {
            select.and(USER.MOBILE.like(this.likeValue(param.getMobile())));
        }
        //根据评价星级查询
        if (!GoodsCommentPageListParam.COMMSTAR_DEFAULT_VALUE.equals(param.getCommstar())) {
            select.and(COMMENT_GOODS.COMMSTAR.eq(param.getCommstar()));
        }
        //根据奖励查询
        if (param.getAwardActivityId() != null) {
            select.and(COMMENT_GOODS.COMMENT_AWARD_ID.eq(param.getAwardActivityId()));
        }
        //根据审核状态查询
        if (!GoodsCommentPageListParam.FLAG_DEFAULT_VALUE.equals(param.getFlag())) {
            select.and(COMMENT_GOODS.FLAG.eq(param.getFlag()));
        }
        //店铺助手查出商品评价审核逾期的评价id列表
        if (GoodsCommentPageListParam.FROM_SHOP_ASSISTANT.equals(param.getShopAssistantFlag())) {
            select.and(COMMENT_GOODS.FLAG.eq(BYTE_ZERO))
                .and(COMMENT_GOODS.CREATE_TIME.add(param.getNDays()).lessThan(Timestamp.valueOf(LocalDateTime.now())));
        }
        //根据是否置顶查询
        if (GoodsCommentPageListParam.TOP.equals(param.getIsTop())) {
            select.and(COMMENT_GOODS.IS_TOP.eq(GoodsCommentPageListParam.TOP));
        }
        //根据是否买家秀查询
        if (GoodsCommentPageListParam.SHOW.equals(param.getIsShow())) {
            select.and(COMMENT_GOODS.IS_SHOW.eq(GoodsCommentPageListParam.SHOW));
        }
    }

    /**
     * 获得所有的评价有礼奖励
     *
     * @return 评价有礼奖励id和name
     */
    public List<CommentAwardVo> getCommentAward() {
        List<CommentAwardVo> result =
            db().select(COMMENT_AWARD.ID, COMMENT_AWARD.NAME)
                .from(COMMENT_AWARD)
                .where(COMMENT_AWARD.DEL_FLAG.eq(BYTE_ZERO))
                .fetchInto(CommentAwardVo.class);
        return result;
    }

    /**
     * 假删除指定评价
     *
     * @param goodsCommentId 评论id
     */
    public void delete(GoodsCommentIdParam goodsCommentId) {
        db().update(COMMENT_GOODS)
            .set(COMMENT_GOODS.DEL_FLAG, NumberUtils.BYTE_ONE)
            .where(COMMENT_GOODS.ID.eq(goodsCommentId.getId()))
            .execute();
        int goodsId = db().select(COMMENT_GOODS.GOODS_ID).from(COMMENT_GOODS).where(COMMENT_GOODS.ID.eq(goodsCommentId.getId())).fetchOneInto(Integer.class);
        saas.getShopApp(getShopId()).goods.updateGoodsCommentNum(goodsId);
    }

    /**
     * 评价回复
     *
     * @param goodsCommentAnswer 评价id和回复内容
     */
    public void insertAnswer(GoodsCommentAnswerParam goodsCommentAnswer) {
        db().insertInto(
            COMMENT_GOODS_ANSWER, COMMENT_GOODS_ANSWER.COMMENT_ID, COMMENT_GOODS_ANSWER.CONTENT)
            .values(goodsCommentAnswer.getCommentId(), goodsCommentAnswer.getContent())
            .execute();
    }

    /**
     * 删除评价回复
     *
     * @param goodsCommentId 评论id
     */
    public void delAnswer(GoodsCommentIdParam goodsCommentId) {
        db().update(COMMENT_GOODS_ANSWER)
            .set(COMMENT_GOODS_ANSWER.DEL_FLAG, NumberUtils.BYTE_ONE)
            .where(COMMENT_GOODS_ANSWER.COMMENT_ID.eq(goodsCommentId.getId()))
            .and(COMMENT_GOODS_ANSWER.DEL_FLAG.eq(BYTE_ZERO))
            .execute();
    }

    /**
     * 修改评价审核状态
     *
     * @param goodsCommentId 评论id
     */
    public void passflag(GoodsCommentIdParam goodsCommentId) {
        db().update(COMMENT_GOODS)
            .set(COMMENT_GOODS.FLAG, GoodsCommentPageListParam.FLAG_PASS_VALUE)
            .where(COMMENT_GOODS.ID.eq(goodsCommentId.getId()))
            .execute();
        int goodsId = db().select(COMMENT_GOODS.GOODS_ID).from(COMMENT_GOODS).where(COMMENT_GOODS.ID.eq(goodsCommentId.getId())).fetchOneInto(Integer.class);
        saas.getShopApp(getShopId()).goods.updateGoodsCommentNum(goodsId);
    }

    /**
     * 修改评价审核状态
     *
     * @param goodsCommentId 评论id
     */
    public void refuseflag(GoodsCommentIdParam goodsCommentId) {
        db().update(COMMENT_GOODS)
            .set(COMMENT_GOODS.FLAG, GoodsCommentPageListParam.FLAG_REFUSE_VALUE)
            .where(COMMENT_GOODS.ID.eq(goodsCommentId.getId()))
            .execute();
        int goodsId = db().select(COMMENT_GOODS.GOODS_ID).from(COMMENT_GOODS).where(COMMENT_GOODS.ID.eq(goodsCommentId.getId())).fetchOneInto(Integer.class);
        saas.getShopApp(getShopId()).goods.updateGoodsCommentNum(goodsId);
    }

    /**
     * 分页查询添加评论列表
     *
     * @param param 商品名称、商家分类筛选信息
     * @return 商品信息
     */
    public PageResult<GoodsCommentAddListVo> getAddList(GoodsCommentPageListParam param) {

        SelectConditionStep<? extends Record>
            selectFrom =
            db().select(
                GOODS_SPEC_PRODUCT.PRD_ID,
                GOODS_SPEC_PRODUCT.PRD_DESC,
                GOODS_SPEC_PRODUCT.GOODS_ID,
                GOODS.GOODS_IMG,
                GOODS.GOODS_NAME,
                GOODS.GOODS_SN,
                GOODS.SORT_ID,
                GOODS.SHOP_PRICE,
                GOODS.GOODS_NUMBER)
                .from(GOODS_SPEC_PRODUCT)
                .leftJoin(GOODS).on(GOODS.GOODS_ID.eq(GOODS_SPEC_PRODUCT.GOODS_ID))
                .where(TRUE_CONDITION);
        //构造查询条件
        this.buildAddOptions(selectFrom, param);
        //从新到旧排序
        selectFrom.orderBy(GOODS_SPEC_PRODUCT.CREATE_TIME.desc());
        //整合分页信息
        PageResult<GoodsCommentAddListVo> pageResult =
            this.getPageResult(
                selectFrom, param.getCurrentPage(), param.getPageRows(), GoodsCommentAddListVo.class);
        for (GoodsCommentAddListVo vo : pageResult.dataList) {
            //商家分类名称
            String sortName = db().select(SORT.SORT_NAME)
                .from(SORT)
                .where(SORT.SORT_ID.eq(vo.getSortId()))
                .fetchOneInto(String.class);
            vo.setSortName(sortName);
            //真实评论数
            Integer realCommNum = db().select(DSL.count(COMMENT_GOODS.ID).as("real_comm_num"))
                .from(COMMENT_GOODS)
                .where(COMMENT_GOODS.GOODS_ID.eq(vo.getGoodsId()))
                .and(COMMENT_GOODS.DEL_FLAG.eq(BYTE_ZERO))
                .and(COMMENT_GOODS.IS_SHOP_ADD.eq(BYTE_ZERO))
                .fetchOneInto(Integer.class);
            vo.setRealCommNum(realCommNum);
            //店铺添加评论数
            Integer shopCommNum = db().select(DSL.count(COMMENT_GOODS.ID).as("shop_comm_num"))
                .from(COMMENT_GOODS)
                .where(COMMENT_GOODS.GOODS_ID.eq(vo.getGoodsId()))
                .and(COMMENT_GOODS.DEL_FLAG.eq(BYTE_ZERO))
                .and(COMMENT_GOODS.IS_SHOP_ADD.eq(BYTE_ONE))
                .fetchOneInto(Integer.class);
            vo.setShopCommNum(shopCommNum);
            //访问量
            Integer uv = db().select(DSL.count(FOOTPRINT_RECORD.ID).as("uv"))
                .from(FOOTPRINT_RECORD)
                .where(FOOTPRINT_RECORD.GOODS_ID.eq(vo.getGoodsId()))
                .fetchOneInto(Integer.class);
            vo.setUv(uv);
            //浏览量
            Integer pv = db().select(DSL.sum(FOOTPRINT_RECORD.COUNT).as("pv"))
                .from(FOOTPRINT_RECORD)
                .where(FOOTPRINT_RECORD.GOODS_ID.eq(vo.getGoodsId()))
                .fetchOneInto(Integer.class);
            vo.setPv(pv);
        }
        return pageResult;
    }

    /**
     * 根据过滤条件构造对应的sql语句
     *
     * @param selectFrom sql语句
     * @param param      筛选条件
     */
    private void buildAddOptions(
        SelectConditionStep<? extends Record>
            selectFrom,
        GoodsCommentPageListParam param) {
        //根据商品名称搜索
        if (!StringUtils.isBlank(param.getGoodsName())) {
            selectFrom.and(GOODS.GOODS_NAME.like(this.likeValue(param.getGoodsName())));
        }
        //根据商家分类搜索
        if (!GoodsCommentPageListParam.SORT_DEFAULT_VALUE.equals(param.getSortId())) {
            List<Integer> sortIds = new ArrayList<>();
            sortIds.add(param.getSortId());
            selectFrom.and(GOODS.SORT_ID.in(mpGoodsRecommendService.getAllChild(sortIds)));
        }
    }

    /**
     * 商家手动添加评价
     *
     * @param goodsCommentAddComm 评论内容
     */
    public int addComment(GoodsCommentAddCommParam goodsCommentAddComm) {
        //有权限
        if (commentSwitch.getAddCommentSwitch(getSysId()).equals(NumberUtils.INTEGER_ONE)) {
            String sqlImg = "";
            if (!StringUtils.isEmpty(goodsCommentAddComm.getCommImg()) && !"".equals(goodsCommentAddComm.getCommImg())) {
                String img = goodsCommentAddComm.getCommImg();
                String[] imgArr = img.split(",");
                for (String i : imgArr) {
                    sqlImg = sqlImg + "\"" + i + "\"" + ",";
                }
                sqlImg = "[" + sqlImg.substring(0, sqlImg.length() - 1) + "]";
            }
            Byte flag = 1;

            //手动添加评价
            db().insertInto(
                COMMENT_GOODS,
                COMMENT_GOODS.USER_ID,
                COMMENT_GOODS.SHOP_ID,
                COMMENT_GOODS.GOODS_ID,
                COMMENT_GOODS.BOGUS_USERNAME,
                COMMENT_GOODS.BOGUS_USER_AVATAR,
                COMMENT_GOODS.CREATE_TIME,
                COMMENT_GOODS.COMMSTAR,
                COMMENT_GOODS.COMM_NOTE,
                COMMENT_GOODS.COMM_IMG,
                COMMENT_GOODS.ANONYMOUSFLAG,
                COMMENT_GOODS.IS_SHOP_ADD,
                COMMENT_GOODS.PRD_ID,
                COMMENT_GOODS.FLAG,
                COMMENT_GOODS.ORDER_SN)
                .values(
                    NumberUtils.INTEGER_ZERO,
                    NumberUtils.INTEGER_ZERO,
                    goodsCommentAddComm.getGoodsId(),
                    goodsCommentAddComm.getBogusUsername(),
                    goodsCommentAddComm.getBogusUserAvatar(),
                    goodsCommentAddComm.getCreateTime(),
                    goodsCommentAddComm.getCommstar(),
                    goodsCommentAddComm.getCommNote(),
                    sqlImg,
                    goodsCommentAddComm.getAnonymousFlag(),
                    NumberUtils.BYTE_ONE,
                    goodsCommentAddComm.getPrdId(),
                    flag,
                    NumberUtils.INTEGER_ZERO.toString())
                .execute();
            saas.getShopApp(getShopId()).goods.updateGoodsCommentNum(goodsCommentAddComm.getGoodsId());
            return 1;
        }
        //没有权限
        else {
            return -1;
        }
    }

    /**
     * 小程序-获取商品评价列表（待评价/已评价）
     *
     * @param param 获取商品评价列表入参
     * @return commentListVo
     */
    public List<CommentListVo> commentList(CommentListParam param) {

        // 声明要用到的订单编号集合
        List<String> orderSn = new ArrayList<>();
        // 如果传入的订单编号不为空，直接遍历对应订单
        if (!StringUtils.isBlank(param.getOrderSn())) {
            orderSn.add(param.getOrderSn());
        }
        // 如果传入的订单编号为空，查找数据库中满足条件的订单
        else {
            // 查询当前用户在当前店铺下的所有满足状态要求的订单编号
            orderSn =
                db().select(ORDER_INFO.ORDER_SN)
                    .from(ORDER_INFO)
                    .where(ORDER_INFO.USER_ID.eq(param.getUserId()))
//              .and(ORDER_INFO.SHOP_ID.eq(getShopId()))
                    .and(
                        ORDER_INFO
                            .ORDER_STATUS
                            .eq(OrderConstant.ORDER_RECEIVED)
                            .or(ORDER_INFO.ORDER_STATUS.eq(OrderConstant.ORDER_FINISHED)))
                    .fetchInto(String.class);
        }
        // 声明要用到的出参集合
        List<CommentListVo> commentListVo = new ArrayList<>();
        // 待评价商品
        if (param.getCommentFlag().equals(BYTE_ZERO)) {
            // 遍历订单查找商品行信息
            for (String orderSnTemp : orderSn) {
                List<CommentListVo> tempCommentList = listComments(param, orderSnTemp);
                // 当前订单下的所有满足条件的商品信息作为出参
                commentListVo.addAll(tempCommentList);
                // 为商品添加评价有礼相关信息
                selectCommentAward(commentListVo);
            }
        } else { // 已评价商品
            for (String orderSnTemp : orderSn) {
                List<CommentListVo> tempCommentList =
                    listGoodsComments(param, orderSnTemp);
                // 当前订单下的所有满足条件的商品信息作为出参
                commentListVo.addAll(tempCommentList);
                // 为商品添加评价有礼相关信息
                selectCommentAward(commentListVo);
            }
        }

        return commentListVo;
    }

    private List<CommentListVo> listGoodsComments(CommentListParam param, String orderSnTemp) {
        return db().select(
            ORDER_GOODS.REC_ID,
            ORDER_GOODS.PRODUCT_ID.as("prd_id"),
            GOODS_SPEC_PRODUCT.PRD_DESC,
            ORDER_GOODS.GOODS_ID,
            ORDER_GOODS.GOODS_NAME,
            ORDER_GOODS.GOODS_IMG,
            ORDER_GOODS.ORDER_SN,
            ORDER_INFO.CREATE_TIME,
            COMMENT_GOODS.COMMSTAR,
            COMMENT_GOODS.COMM_NOTE,
            COMMENT_GOODS.COMM_IMG,
            COMMENT_GOODS_ANSWER.CONTENT,
            ORDER_GOODS.COMMENT_FLAG)
            .from(ORDER_GOODS)
            .leftJoin(GOODS_SPEC_PRODUCT)
            .on(ORDER_GOODS.PRODUCT_ID.eq(GOODS_SPEC_PRODUCT.PRD_ID))
            .leftJoin(ORDER_INFO)
            .on(ORDER_GOODS.ORDER_SN.eq(ORDER_INFO.ORDER_SN))
            .leftJoin(COMMENT_GOODS)
            .on(
                ORDER_GOODS
                    .ORDER_SN
                    .eq(COMMENT_GOODS.ORDER_SN)
                    .and(ORDER_GOODS.GOODS_ID.eq(COMMENT_GOODS.GOODS_ID))
                    .and(ORDER_GOODS.REC_ID.eq(COMMENT_GOODS.REC_ID)))
            .leftJoin(COMMENT_GOODS_ANSWER)
            .on(
                COMMENT_GOODS
                    .ID
                    .eq(COMMENT_GOODS_ANSWER.COMMENT_ID)
                    .and(COMMENT_GOODS_ANSWER.DEL_FLAG.eq(BYTE_ZERO)))
            .where(ORDER_GOODS.ORDER_SN.eq(orderSnTemp))
//                .and(ORDER_GOODS.SHOP_ID.eq(getShopId()))
            .and(COMMENT_GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
            .and(ORDER_GOODS.COMMENT_FLAG.eq(param.getCommentFlag()))
            .and(ORDER_GOODS.GOODS_NUMBER.greaterThan(ORDER_GOODS.RETURN_NUMBER))
            .orderBy(COMMENT_GOODS.CREATE_TIME.desc())
            .fetchInto(CommentListVo.class);
    }

    private List<CommentListVo> listComments(CommentListParam param, String orderSnTemp) {
        return db().select(
            ORDER_GOODS.REC_ID,
            ORDER_GOODS.PRODUCT_ID.as("prd_id"),
            GOODS_SPEC_PRODUCT.PRD_DESC,
            ORDER_GOODS.GOODS_ID,
            ORDER_GOODS.GOODS_NAME,
            ORDER_GOODS.GOODS_IMG,
            ORDER_GOODS.ORDER_SN,
            ORDER_INFO.CREATE_TIME,
            ORDER_GOODS.COMMENT_FLAG)
            .from(ORDER_GOODS)
            .leftJoin(ORDER_INFO)
            .on(ORDER_GOODS.ORDER_SN.eq(ORDER_INFO.ORDER_SN))
            .leftJoin(GOODS_SPEC_PRODUCT)
            .on(ORDER_GOODS.PRODUCT_ID.eq(GOODS_SPEC_PRODUCT.PRD_ID))
            .where(ORDER_GOODS.ORDER_SN.eq(orderSnTemp))
//                .and(ORDER_GOODS.SHOP_ID.eq(getShopId()))
            .and(ORDER_GOODS.COMMENT_FLAG.eq(param.getCommentFlag()))
            .and(ORDER_GOODS.GOODS_NUMBER.greaterThan(ORDER_GOODS.RETURN_NUMBER))
            .orderBy(ORDER_GOODS.CREATE_TIME.desc())
            .fetchInto(CommentListVo.class);
    }

    /**
     * 得到当前所有可用活动和其触发条件的信息
     *
     * @return 所有可用活动和其触发条件
     */
    public List<TriggerConditionVo> getAllActivities() {
        // 得到系统当前时间作为筛选依据
        Timestamp nowTime = new Timestamp(System.currentTimeMillis());
        // 筛选评价有礼活动类型
        List<TriggerConditionVo> triggerConditionVos =
            db().select(
                COMMENT_AWARD.ID,
                COMMENT_AWARD.GOODS_TYPE,
                COMMENT_AWARD.GOODS_IDS,
                COMMENT_AWARD.COMMENT_NUM,
                COMMENT_AWARD.FIRST_COMMENT_GOODS)
                .from(COMMENT_AWARD)
                .where(COMMENT_AWARD.DEL_FLAG.eq(BYTE_ZERO))
                .and(COMMENT_AWARD.STATUS.eq(NumberUtils.BYTE_ONE))
                .and(COMMENT_AWARD.AWARD_NUM.greaterThan(COMMENT_AWARD.SEND_NUM))
                .and(
                    COMMENT_AWARD
                        .IS_FOREVER
                        .eq(NumberUtils.BYTE_ONE)
                        .or(
                            COMMENT_AWARD
                                .START_TIME
                                .lessOrEqual(nowTime)
                                .and(COMMENT_AWARD.END_TIME.greaterOrEqual(nowTime))))
                .fetchInto(TriggerConditionVo.class);
        return triggerConditionVos;
    }

    /**
     * 得到当前商品所满足触发条件的所有活动的id集合
     *
     * @param goodsId             当前商品id
     * @param triggerConditionVos 触发条件信息
     * @return 当前商品所满足触发条件的所有活动的id集合
     */
    public Set<Integer> getAllActIds(Integer goodsId, List<TriggerConditionVo> triggerConditionVos) {
        // 声明一个集合来存放满足条件的活动id
        Set<Integer> actIds = new HashSet<>();
        for (TriggerConditionVo vo : triggerConditionVos) {
            //先判断是否只有首次评价商品才送礼
            if (vo.getFirstCommentGoods() != null && vo.getFirstCommentGoods() == (byte) 1) {
                //判断当前商品 对于当前的评价有礼活动 是否是首次评价
                List<CommentGoodsRecord> record = db().select()
                    .from(COMMENT_GOODS)
                    .where(COMMENT_GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
                    .and(COMMENT_GOODS.GOODS_ID.eq(goodsId))
                    .and(COMMENT_GOODS.COMMENT_AWARD_ID.eq(vo.getId()))
                    .fetchInto(CommentGoodsRecord.class);
                //对于当前的评价有礼活动 当前商品 已经有人评价过了
                if (record != null && record.size() > 0) {
                    //这个活动不符合当前商品 跳出当前活动循环
                    continue;
                }
            }
            // 触发条件：全部商品
            if (vo.getGoodsType().equals(NumberUtils.INTEGER_ONE)) {
                actIds.add(vo.getId());
            } // 触发条件：指定商品
            else if (vo.getGoodsType().equals(NumberUtils.INTEGER_TWO)) {
                String[] arr = vo.getGoodsIds().substring(1, vo.getGoodsIds().length() - 1).split(",");
                if (Arrays.asList(arr).contains(goodsId.toString())) {
                    actIds.add(vo.getId());
                }
            } // 触发条件：评论数小于一定值
            else {
                Integer commNum =
                    db().select(DSL.count(COMMENT_GOODS.ID))
                        .from(COMMENT_GOODS)
                        .where(COMMENT_GOODS.GOODS_ID.eq(goodsId))
                        .and(COMMENT_GOODS.FLAG.eq(NumberUtils.BYTE_ONE))
                        .and(COMMENT_GOODS.DEL_FLAG.eq(BYTE_ZERO))
                        .fetchOptionalInto(Integer.class)
                        .orElse(0);
                if (commNum < vo.getCommentNum()) {
                    actIds.add(vo.getId());
                }
            }
        }
        return actIds;
    }

    /**
     * 根据优先级确定当前商品参加哪个评价有礼活动
     *
     * @param actIds 有所符合条件的活动id集合
     * @return 最终参与的活动的id
     */
    public Integer getTheActId(Set<Integer> actIds) {
        // 遍历筛选出来的评价有礼活动id，查找当前商品的活动奖励
        Integer actId =
            db().select(COMMENT_AWARD.ID)
                .from(COMMENT_AWARD)
                .where(COMMENT_AWARD.ID.in(actIds))
                .orderBy(COMMENT_AWARD.LEVEL.desc())
                .limit(NumberUtils.INTEGER_ONE)
                .fetchOptionalInto(Integer.class)
                .orElseThrow(() -> new RuntimeException("未找到对应活动id"));
        return actId;
    }

    /**
     * 为商品添加评价有礼相关信息
     *
     * @param commentListVo 待添加评价有礼的商品行
     */
    public void selectCommentAward(List<CommentListVo> commentListVo) {
        // 得到当前所有可用活动和其触发条件的信息
        List<TriggerConditionVo> triggerConditionVos = getAllActivities();
        if (triggerConditionVos == null || triggerConditionVos.isEmpty()) {
            return;
        } else {
            // 判断每个商品行对应的评价有礼活动奖励
            for (CommentListVo forGoodsId : commentListVo) {
                Integer actId;
                // 如果是待评价的商品
                if (forGoodsId.getCommentFlag().equals(BYTE_ZERO)) {
                    // 得到当前商品所满足触发条件的所有活动的id集合
                    Set<Integer> actIds = getAllActIds(forGoodsId.getGoodsId(), triggerConditionVos);
                    if (actIds == null || actIds.isEmpty()) {
                        continue;
                    } else {
                        // 根据优先级确定当前商品参加哪个评价有礼活动
                        actId = getTheActId(actIds);
                    }
                }
                // 如果是已评价的商品
                else {
                    actId =
                        db().select(COMMENT_GOODS.COMMENT_AWARD_ID)
                            .from(COMMENT_GOODS)
                            .where(COMMENT_GOODS.REC_ID.eq(forGoodsId.getRecId()))
                            .and(COMMENT_GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
                            .fetchOptionalInto(Integer.class)
                            .orElse(NumberUtils.INTEGER_ZERO);
                    if (actId.equals(NumberUtils.INTEGER_ZERO)) {
                        continue;
                    }
                }
                forGoodsId.setId(actId);
                // 查询活动奖励和获得奖励所需要的评价条件
                AwardContentVo awardContentVos = getAwardContentVo(actId);
                // 设置获得奖励所需要的评价条件
                forGoodsId.setCommentType(awardContentVos.getCommentType());
                forGoodsId.setCommentWords(awardContentVos.getCommentWords());
                forGoodsId.setHasPicNum(awardContentVos.getHasPicNum());
                forGoodsId.setHasFiveStars(awardContentVos.getHasFiveStars());
                // 设置活动奖励
                forGoodsId.setAwardType(awardContentVos.getAwardType());
                // 活动奖励1：赠送积分
                if (awardContentVos.getAwardType().equals(NumberUtils.INTEGER_ONE)) {
                    forGoodsId.setAward(awardContentVos.getScore().toString());
                }
                // 活动奖励2：赠送优惠券
                else if (awardContentVos.getAwardType().equals(NumberUtils.INTEGER_TWO)) {
                    String couponName =
                        db().select(MRKING_VOUCHER.ACT_NAME)
                            .from(MRKING_VOUCHER)
                            .where(MRKING_VOUCHER.ID.eq(awardContentVos.getActivityId()))
                            .fetchOptionalInto(String.class)
                            .orElse("评价有礼优惠券");
                    forGoodsId.setAward("id:" + awardContentVos.getActivityId() + "," + "name:" + couponName);
                }
                // 活动奖励3：赠送用户余额
                else if (awardContentVos.getAwardType().equals(THREE)) {
                    forGoodsId.setAward(awardContentVos.getAccount().toString());
                }
                // 活动奖励4：赠送抽奖机会
                else if (awardContentVos.getAwardType().equals(FOUR)) {
                    forGoodsId.setAward(awardContentVos.getActivityId().toString());
                }
                // 活动奖励5：自定义奖励
                else if (awardContentVos.getAwardType().equals(FIVE)) {
                    forGoodsId.setAward(
                        "path:"
                            + awardContentVos.getAwardPath()
                            + ","
                            + "img:"
                            + awardContentVos.getAwardImg());
                }
            }
        }
    }

    private AwardContentVo getAwardContentVo(Integer actId) {
        return db().select(
            COMMENT_AWARD.AWARD_TYPE,
            COMMENT_AWARD.SCORE,
            COMMENT_AWARD.ACTIVITY_ID,
            COMMENT_AWARD.ACCOUNT,
            COMMENT_AWARD.AWARD_PATH,
            COMMENT_AWARD.AWARD_IMG,
            COMMENT_AWARD.COMMENT_TYPE,
            COMMENT_AWARD.COMMENT_WORDS,
            COMMENT_AWARD.HAS_PIC_NUM,
            COMMENT_AWARD.HAS_FIVE_STARS)
            .from(COMMENT_AWARD)
            .where(COMMENT_AWARD.ID.eq(actId))
            .fetchOneInto(AwardContentVo.class);
    }

    /**
     * 小程序-用户添加商品评价
     *
     * @param param 用户添加评价的入参
     */
    public void addCommentByUser(AddCommentParam param) throws MpException {
        // 先查询当前店铺评价审核配置
        String commConfig =
            db().select(SHOP_CFG.V)
                .from(SHOP_CFG)
                .where(SHOP_CFG.K.eq("comment"))
                .fetchOptionalInto(String.class)
                .orElse("0");
        // 审核配置转Integer类型
        Integer commCfg = Integer.valueOf(commConfig);
        // 设置商品评论表里的审核标识
        Byte flag;
        // 不用审核时评价状态直接为通过，否则为待审核
        if (commCfg.equals(NumberUtils.INTEGER_ZERO)) {
            flag = NumberUtils.BYTE_ONE;
        } else {
            flag = BYTE_ZERO;
        }
        //添加前查找是否有这样的记录
        CommentGoodsRecord record = getCommentGoods(param);
        if (record == null) {
            if (StringUtils.isBlank(param.getCommNote())) {
                param.setCommNote(null);
            }
            if (StringUtils.isBlank(param.getCommImg())) {
                param.setCommNote(null);
            }

            // 添加评论
            insertCommentGoods(param, flag);

            updateCommentFlag(param);

            // 添加评价关联评价有礼-发放礼物
            if (awardComment(param)) {
                return;
            }
        }

        saas.getShopApp(getShopId()).goods.updateGoodsCommentNum(param.getGoodsId());
    }

    private boolean awardComment(AddCommentParam param) throws MpException {
        if (null != param.getId()) {
            //获取奖品份数和已发放份数
            Integer awardNum = getAwardNumById(param);
            Integer sendNum = getSendNum(param);
            //判断奖品是否发完
            if (awardNum <= sendNum) {
                return true;
            }
            // 判断是否满足当前活动需要的评价条件
            AwardConditionVo awardCondition =
                db().select(
                    COMMENT_AWARD.COMMENT_TYPE,
                    COMMENT_AWARD.COMMENT_WORDS,
                    COMMENT_AWARD.HAS_PIC_NUM,
                    COMMENT_AWARD.HAS_FIVE_STARS)
                    .from(COMMENT_AWARD)
                    .where(COMMENT_AWARD.ID.eq(param.getId()))
                    .fetchOneInto(AwardConditionVo.class);
            /** 评价类型 1评价即送 2 自定义 */
            byte typeCustom = (byte) 2;
            if (awardCondition.getCommentType().equals(typeCustom)) {
                // 心得超过规定字数
                if (param.getCommNote().length() < awardCondition.getCommentWords()) {
                    return true;
                }
                // 晒图评论
                else if (awardCondition.getHasPicNum().equals(NumberUtils.BYTE_ONE)
                    && StringUtils.isBlank(param.getCommImg())) {
                    return true;
                }
                // 五星好评
                else if (awardCondition.getHasFiveStars().equals(NumberUtils.BYTE_ONE)
                    && param.getCommstar() != FIVE) {
                    return true;
                }
            }
            // 为参与评价有礼活动的商品设置活动id 此时已经经过满足评价条件的校验了
            db().update(COMMENT_GOODS)
                .set(COMMENT_GOODS.COMMENT_AWARD_ID, param.getId())
                .where(COMMENT_GOODS.REC_ID.eq(param.getRecId()))
                .and(COMMENT_GOODS.DEL_FLAG.eq(BYTE_ZERO))
                .execute();
            //当前活动的评价数量
            Integer commentNum = db().select(COMMENT_AWARD.COMMENT_NUM)
                .from(COMMENT_AWARD)
                .where(COMMENT_AWARD.ID.eq(param.getId()))
                .fetchOptionalInto(Integer.class)
                .orElse(0);
            // 活动奖励1：赠送积分
            if (param.getAwardType().equals(NumberUtils.INTEGER_ONE)) {
                // 给当前用户赠送积分
                Integer[] userIdArray = {param.getUserId()};
                scoreService.updateMemberScore(
                    new ScoreParam() {
                        {
                            setUserId(param.getUserId());
                            setScore(Integer.valueOf(param.getAward()));
                            setScoreStatus(BYTE_ZERO);
                            setDesc("score");
                            setOrderSn(param.getOrderSn());
                            setRemarkCode(RemarkTemplate.COMMENT_HAS_GIFT.code);
                            //setRemark("评价有礼获得");
                        }
                    },
                    0,
                    (byte) 11,
                    (byte) 0);
                //更新奖品分数
                db().update(COMMENT_AWARD)
                    .set(COMMENT_AWARD.SEND_NUM, (sendNum + 1))
                    .set(COMMENT_AWARD.COMMENT_NUM, (commentNum + 1))
                    .where(COMMENT_AWARD.ID.eq(param.getId()))
                    .execute();
            }
            // 活动奖励2：赠送优惠券
            else if (param.getAwardType().equals(NumberUtils.INTEGER_TWO)) {
                // 给当前用户赠送优惠券
//          Integer shopId = getShopId();
                CouponGiveQueueParam giveCoupon = new CouponGiveQueueParam() {{
                    setActId(1);
                    setGetSource(GET_SOURCE);
                    setAccessMode(BYTE_ZERO);
                    setCouponArray(new String[]{param.getAward()});
                    setUserIds(new ArrayList<Integer>() {{
                        add(param.getUserId());
                    }});
                }};
                //调用定向发券中抽取出来的公共方法
                couponGiveService.handlerCouponGive(giveCoupon);
                //更新奖品分数
                db().update(COMMENT_AWARD)
                    .set(COMMENT_AWARD.SEND_NUM, (sendNum + 1))
                    .set(COMMENT_AWARD.COMMENT_NUM, (commentNum + 1))
                    .where(COMMENT_AWARD.ID.eq(param.getId()))
                    .execute();
            }
            // 活动奖励3：赠送用户余额
            else if (param.getAwardType().equals(THREE)) {
                // 给当前用户赠送余额
                // 获取语言 用于国际化
                accountService.updateUserAccount(
                    new AccountParam() {
                        {
                            setUserId(param.getUserId());
                            setAmount(BigDecimal.valueOf(Double.parseDouble(param.getAward())));
                            setOrderSn(param.getOrderSn());
                            setRemarkId(RemarkTemplate.COMMENT_HAS_GIFT.code);
                        }
                    },
                    TradeOptParam.builder().tradeType((byte) 8).tradeFlow((byte) 1).build());
                //更新奖品分数
                db().update(COMMENT_AWARD)
                    .set(COMMENT_AWARD.SEND_NUM, (sendNum + 1))
                    .set(COMMENT_AWARD.COMMENT_NUM, (commentNum + 1))
                    .where(COMMENT_AWARD.ID.eq(param.getId()))
                    .execute();
            }
            // 活动奖励4：赠送抽奖机会
            else if (param.getAwardType().equals(FOUR)) {
                // 前端展示内容为：获得一次抽奖机会，可以选择进入抽奖页面
                //更新奖品分数
                db().update(COMMENT_AWARD)
                    .set(COMMENT_AWARD.SEND_NUM, (sendNum + 1))
                    .set(COMMENT_AWARD.COMMENT_NUM, (commentNum + 1))
                    .where(COMMENT_AWARD.ID.eq(param.getId()))
                    .execute();
            }
            // 活动奖励5：自定义奖励
            else if (param.getAwardType().equals(FIVE)) {
                // 前端展示内容：查看神秘奖励，可以选择进入path链接
                //更新奖品分数
                db().update(COMMENT_AWARD)
                    .set(COMMENT_AWARD.SEND_NUM, (sendNum + 1))
                    .set(COMMENT_AWARD.COMMENT_NUM, (commentNum + 1))
                    .where(COMMENT_AWARD.ID.eq(param.getId()))
                    .execute();
            }
        }
        return false;
    }

    private Integer getSendNum(AddCommentParam param) {
        return db().select(COMMENT_AWARD.SEND_NUM)
                        .from(COMMENT_AWARD)
                        .where(COMMENT_AWARD.ID.eq(param.getId()))
                        .fetchOneInto(Integer.class);
    }

    private Integer getAwardNumById(AddCommentParam param) {
        return db().select(COMMENT_AWARD.AWARD_NUM)
                        .from(COMMENT_AWARD)
                        .where(COMMENT_AWARD.ID.eq(param.getId()))
                        .fetchOptionalInto(Integer.class)
                        .orElse(0);
    }

    private void updateCommentFlag(AddCommentParam param) {
        // 添加评论后将order_goods表中comment_flag置为1
        db().update(ORDER_GOODS)
            .set(ORDER_GOODS.COMMENT_FLAG, NumberUtils.BYTE_ONE)
            .where(ORDER_GOODS.REC_ID.eq(param.getRecId()))
            .execute();
        // order表中的comment_flag也置为1
        db().update(ORDER_INFO)
            .set(ORDER_INFO.COMMENT_FLAG, NumberUtils.BYTE_ONE)
            .where(ORDER_INFO.ORDER_SN.eq(param.getOrderSn()))
            .execute();
    }

    private CommentGoodsRecord getCommentGoods(AddCommentParam param) {
        return db().select()
                .from(COMMENT_GOODS)
                .where(COMMENT_GOODS.REC_ID.eq(param.getRecId()))
                .and(COMMENT_GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
                .fetchOneInto(CommentGoodsRecord.class);
    }

    private void insertCommentGoods(AddCommentParam param, Byte flag) {
        // 为指定商品添加评论
        db().insertInto(
            COMMENT_GOODS,
            COMMENT_GOODS.SHOP_ID,
            COMMENT_GOODS.USER_ID,
            COMMENT_GOODS.GOODS_ID,
            COMMENT_GOODS.ORDER_SN,
            COMMENT_GOODS.COMMSTAR,
            COMMENT_GOODS.COMM_NOTE,
            COMMENT_GOODS.COMM_IMG,
            COMMENT_GOODS.ANONYMOUSFLAG,
            COMMENT_GOODS.FLAG,
            COMMENT_GOODS.REC_ID,
            COMMENT_GOODS.PRD_ID)
            .values(
                0,
                param.getUserId(),
                param.getGoodsId(),
                param.getOrderSn(),
                param.getCommstar(),
                param.getCommNote(),
                param.getCommImg(),
                param.getAnonymousflag(),
                flag,
                param.getRecId(),
                param.getPrdId())
            .execute();
    }

    /**
     * 统计商品评价数量
     *
     * @param goodsIds 待统计商品id集合
     * @param config   评价配置： 0不审，1先发后审，2先审后发
     * @return
     */
    public Map<Integer, Integer> statisticGoodsComment(List<Integer> goodsIds, Byte config) {
        Map<Integer, Integer> results = null;
        int publishAfterAudit = 2;
        if (config == publishAfterAudit) {
            results =
                db().select(COMMENT_GOODS.GOODS_ID, DSL.count(COMMENT_GOODS.ID).as("num"))
                    .from(COMMENT_GOODS)
                    .where(COMMENT_GOODS.DEL_FLAG.eq(DelFlag.NORMAL.getCode()))
                    .and(COMMENT_GOODS.GOODS_ID.in(goodsIds))
                    .groupBy(COMMENT_GOODS.GOODS_ID)
                    .fetchMap(COMMENT_GOODS.GOODS_ID, DSL.field("num", Integer.class));
        } else {
            results =
                db().select(COMMENT_GOODS.GOODS_ID, DSL.count(COMMENT_GOODS.ID).as("num"))
                    .from(COMMENT_GOODS)
                    .where(COMMENT_GOODS.DEL_FLAG.eq(DelFlag.NORMAL.getCode()))
                    .and(COMMENT_GOODS.GOODS_ID.in(goodsIds))
                    .and(COMMENT_GOODS.FLAG.eq((byte) 1))
                    .groupBy(COMMENT_GOODS.GOODS_ID)
                    .fetchMap(COMMENT_GOODS.GOODS_ID, DSL.field("num", Integer.class));
        }
        return results;
    }

    /**
     * 判断订单是否参与评价有礼活动
     *
     * @param goodsList
     * @return
     */
    public boolean orderIsCommentAward(List<CommentListVo> goodsList) {
        // 得到当前所有可用活动和其触发条件的信息
        List<TriggerConditionVo> allActivities = getAllActivities();
        if (CollectionUtils.isEmpty(allActivities)) {
            return false;
        }
        // 判断每个商品行对应的评价有礼活动奖励
        for (CommentListVo forGoodsId : goodsList) {
            // 得到当前商品所满足触发条件的所有活动的id集合
            Set<Integer> actIds = getAllActIds(forGoodsId.getGoodsId(), allActivities);
            if (actIds != null && !actIds.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Review overdue integer.商品评价审核逾期
     *
     * @param nDays the n days
     * @return the integer
     */
    public Integer reviewOverdue(Integer nDays) {
        return db().fetchCount(CommentGoods.COMMENT_GOODS, CommentGoods.COMMENT_GOODS.DEL_FLAG.eq(BYTE_ZERO)
            .and(CommentGoods.COMMENT_GOODS.FLAG.eq(BYTE_ZERO))
            .and(CommentGoods.COMMENT_GOODS.CREATE_TIME.add(nDays).lessThan(Timestamp.valueOf(LocalDateTime.now()))));
    }

    /**
     * Review overdue integer.商品评价审核逾期id列表
     *
     * @param nDays the n days
     * @return the integer
     */
    public Set<Integer> reviewOverdueSet(Integer nDays) {
        Condition condition = COMMENT_GOODS.DEL_FLAG.eq(BYTE_ZERO)
            .and(COMMENT_GOODS.FLAG.eq(BYTE_ZERO))
            .and(COMMENT_GOODS.CREATE_TIME.add(nDays).lessThan(Timestamp.valueOf(LocalDateTime.now())));
        return db().select(COMMENT_GOODS.ID).from(COMMENT_GOODS).where(condition).fetchSet(COMMENT_GOODS.ID);
    }

    /**
     * 商品评价数
     *
     * @param goodsId
     * @return
     */
    public int getGoodsCommentNum(int goodsId) {
        Byte commSwitch = commentConfigService.getCommentConfig();
        Byte commStatusSwitch = commentConfigService.getSwitchConfig();
        SelectConditionStep<? extends Record> select = db().selectCount().from(COMMENT_GOODS).where(COMMENT_GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)).and(COMMENT_GOODS.GOODS_ID.eq(goodsId));
        byte publishAfterAudit = 2;
        if (commSwitch.equals(publishAfterAudit)) {
            select.and(COMMENT_GOODS.FLAG.eq(BYTE_ONE));
        } else {
            select.and(COMMENT_GOODS.FLAG.notEqual(Byte.valueOf((byte) 2)));
        }
        if (commStatusSwitch.equals(BYTE_ZERO)) {
            select.and(COMMENT_GOODS.COMM_NOTE.isNotNull()).and(COMMENT_GOODS.COMM_NOTE.notEqual(""));
        }
        return select.fetchOptionalInto(Integer.class).orElse(0);
    }

    /**
     * 将评论置顶
     *
     * @param param 评价记录id
     */
    public void setTop(GoodsCommentIdParam param) {
        db().update(COMMENT_GOODS)
            .set(COMMENT_GOODS.IS_TOP, GoodsCommentPageListParam.TOP)
            .set(COMMENT_GOODS.TOP_TIME, DateUtils.getSqlTimestamp())
            .where(COMMENT_GOODS.ID.eq(param.getId()))
            .execute();
    }

    /**
     * 取消评论置顶
     *
     * @param param 评价记录id
     */
    public void cancelTop(GoodsCommentIdParam param) {
        Timestamp topTime = null;
        db().update(COMMENT_GOODS)
            .set(COMMENT_GOODS.IS_TOP, GoodsCommentPageListParam.NOT_TOP)
            .set(COMMENT_GOODS.TOP_TIME, topTime)
            .where(COMMENT_GOODS.ID.eq(param.getId()))
            .execute();
    }

    /**
     * 单商品评价详情
     *
     * @param param goodsId和筛选条件
     * @return 评价详情和百分比
     */
    public CommentInfo goodsComment(MpGoodsCommentParam param) {
        //0不用审核，1先发后审，2先审后发
        Byte commentFlag = commentConfigService.getCommentConfig();
        //设置前端是否隐藏未填写心得的评价，0关，1开
        Byte commentSee = commentConfigService.getSwitchConfig();
        PageResult<MpGoodsCommentVo> comment = getGoodsComment(param.getGoodsId(), param.getType(), commentFlag, commentSee, param.getCurrentPage(), param.getPageRows());
        if (comment != null) {
            for (MpGoodsCommentVo item : comment.getDataList()) {
                Integer answerId = db().select(COMMENT_GOODS_ANSWER.ANSWER_ID)
                    .from(COMMENT_GOODS_ANSWER)
                    .where(COMMENT_GOODS_ANSWER.COMMENT_ID.eq(item.getId()))
                    .and(COMMENT_GOODS_ANSWER.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
                    .orderBy(COMMENT_GOODS_ANSWER.CREATE_TIME.desc())
                    .limit(1)
                    .fetchOneInto(Integer.class);
                String content = db().select(COMMENT_GOODS_ANSWER.CONTENT)
                    .from(COMMENT_GOODS_ANSWER)
                    .where(COMMENT_GOODS_ANSWER.COMMENT_ID.eq(item.getId()))
                    .and(COMMENT_GOODS_ANSWER.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
                    .orderBy(COMMENT_GOODS_ANSWER.CREATE_TIME.desc())
                    .limit(1)
                    .fetchOneInto(String.class);
                if (answerId != null) {
                    item.setAnswerId(answerId);
                    item.setAnswer(content);
                }
                //兼容商家添加的评价
                if (item.getUsername() == null) {
                    item.setUsername(item.getBogusUsername());
                }
                if (item.getUserAvatar() == null) {
                    item.setUserAvatar(item.getBogusUserAvatar());
                }
                //设置头像
                if (item.getUserAvatar() == null) {
                    item.setUserAvatar("/image/admin/head_icon.png");
                    if (!StringUtils.isEmpty(item.getBogusUserAvatar()) && !"".equals(item.getBogusUserAvatar())) {
                        item.setUserAvatar(item.getBogusUserAvatar());
                    }
                    if (ANONYMOUS_FLAG.equals(item.getAnonymousflag())) {
                        item.setUserAvatar("/image/admin/head_icon.png");
                    }
                }
            }
        }
        List<CommentDetailVo.CommentLevelInfo> number = goodsCommentProcessorDao.calculateGoodsCommentNumInfo(param.getGoodsId(), commentFlag, commentSee);
        CommentInfo commentInfo = new CommentInfo();
        commentInfo.setComment(comment);
        commentInfo.setNumber(number);
        return commentInfo;
    }


    /**
     * 得到当前商品的评价详情
     *
     * @param goodsId     商品id
     * @param commentFlag 审核配置
     * @param commentSee  是否展示未填写心得的评价
     * @param type        筛选条件
     * @return 评价详情
     */
    public PageResult<MpGoodsCommentVo> getGoodsComment(Integer goodsId, Byte type, Byte commentFlag, Byte commentSee, Integer currentPage, Integer pageRows) {
        SelectConditionStep<? extends Record> sql = db().select(COMMENT_GOODS.ID, COMMENT_GOODS.COMMSTAR,
            COMMENT_GOODS.ANONYMOUSFLAG, COMMENT_GOODS.COMM_NOTE, COMMENT_GOODS.COMM_IMG,
            ORDER_GOODS.GOODS_ATTR, USER_DETAIL.USERNAME, USER_DETAIL.USER_AVATAR,
            COMMENT_GOODS.CREATE_TIME, COMMENT_GOODS.BOGUS_USERNAME, COMMENT_GOODS.BOGUS_USER_AVATAR,
            GOODS_SPEC_PRODUCT.PRD_DESC, COMMENT_GOODS.IS_TOP, COMMENT_GOODS.TOP_TIME)
            .from(COMMENT_GOODS)
            .leftJoin(ORDER_GOODS).on(COMMENT_GOODS.REC_ID.eq(ORDER_GOODS.REC_ID))
            .leftJoin(GOODS_SPEC_PRODUCT).on(COMMENT_GOODS.PRD_ID.eq(GOODS_SPEC_PRODUCT.PRD_ID))
            .leftJoin(USER_DETAIL).on(COMMENT_GOODS.USER_ID.eq(USER_DETAIL.USER_ID))
            .where(COMMENT_GOODS.DEL_FLAG.eq(DelFlag.NORMAL.getCode()))
            .and(COMMENT_GOODS.GOODS_ID.eq(goodsId));
        //审核配置
        if (CHECK_FIRST.equals(commentFlag)) {
            sql.and(COMMENT_GOODS.FLAG.eq(PASS_AUDIT));
        } else {
            sql.and(COMMENT_GOODS.FLAG.notEqual(NOT_PASS_AUDIT));
        }
        //是否隐藏未填写心得的评价
        if (NOT_SHOW_STATE.equals(commentSee)) {
            sql.and(COMMENT_GOODS.COMM_NOTE.isNotNull());
        }
        //评价星级设置
        List<Byte> goodsType = new ArrayList<>();
        goodsType.add((byte) 5);
        goodsType.add((byte) 4);
        List<Byte> midType = new ArrayList<>();
        midType.add((byte) 3);
        midType.add((byte) 2);
        List<Byte> badType = new ArrayList<>();
        badType.add((byte) 1);
        badType.add((byte) 0);
        if (GOOD_TYPE.equals(type)) {
            sql.and(COMMENT_GOODS.COMMSTAR.in(goodsType));
        } else if (MID_TYPE.equals(type)) {
            sql.and(COMMENT_GOODS.COMMSTAR.in(midType));
        } else if (BAD_TYPE.equals(type)) {
            sql.and(COMMENT_GOODS.COMMSTAR.in(badType));
        } else if (PIC_TYPE.equals(type)) {
            sql.and(COMMENT_GOODS.COMM_IMG.isNotNull().and(COMMENT_GOODS.COMM_IMG.notEqual("[]")));
        }
        sql.orderBy(COMMENT_GOODS.IS_TOP.desc(), COMMENT_GOODS.TOP_TIME.desc(), COMMENT_GOODS.CREATE_TIME.desc())
            .fetchInto(MpGoodsCommentVo.class);
        PageResult<MpGoodsCommentVo> vo = this.getPageResult(sql, currentPage, pageRows, MpGoodsCommentVo.class);
        return vo;
    }

    /**
     * 将评论设置买家秀
     *
     * @param param 评价记录id
     */
    public void setShow(GoodsCommentIdParam param) {
        db().update(COMMENT_GOODS)
            .set(COMMENT_GOODS.IS_SHOW, GoodsCommentPageListParam.SHOW)
            .set(COMMENT_GOODS.SHOW_TIME, DateUtils.getSqlTimestamp())
            .where(COMMENT_GOODS.ID.eq(param.getId()))
            .execute();
    }

    /**
     * 取消评论置顶
     *
     * @param param 评价记录id
     */
    public void cancelShow(GoodsCommentIdParam param) {
        Timestamp showTime = null;
        db().update(COMMENT_GOODS)
            .set(COMMENT_GOODS.IS_SHOW, GoodsCommentPageListParam.NOT_SHOW)
            .set(COMMENT_GOODS.SHOW_TIME, showTime)
            .where(COMMENT_GOODS.ID.eq(param.getId()))
            .execute();
    }
}
