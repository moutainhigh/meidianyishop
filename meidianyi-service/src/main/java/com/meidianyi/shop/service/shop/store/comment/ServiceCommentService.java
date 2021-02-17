package com.meidianyi.shop.service.shop.store.comment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.DomainConfig;
import com.meidianyi.shop.db.shop.tables.records.CommentServiceRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.store.comment.CommentFlagEnum;
import com.meidianyi.shop.service.pojo.shop.store.comment.ServiceCommentPageListParam;
import com.meidianyi.shop.service.pojo.shop.store.comment.ServiceCommentVo;
import com.meidianyi.shop.service.pojo.wxapp.store.AllCommentParam;
import com.meidianyi.shop.service.pojo.wxapp.store.AllCommentVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.SelectConditionStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.tools.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.Tables.*;
import static com.meidianyi.shop.db.shop.tables.ServiceOrder.SERVICE_ORDER;
import static com.meidianyi.shop.db.shop.tables.StoreService.STORE_SERVICE;
import static org.apache.commons.lang3.math.NumberUtils.*;

/**
 * @author 黄荣刚
 * @date 2019年7月18日
 *服务评价相关方法
 */
@Service
@Slf4j
public class ServiceCommentService extends ShopBaseService {
    /**
     * The Domain config.
     */
    @Autowired
    public DomainConfig domainConfig;

	/**
	 * 分页查询服务评价列表
	 * @param param
	 * @return
	 */
	public PageResult<ServiceCommentVo> getPageList(ServiceCommentPageListParam param){
		SelectOnConditionStep<?> selectFrom = db().select(COMMENT_SERVICE.ID, COMMENT_SERVICE.STORE_ID,COMMENT_SERVICE.ORDER_SN,COMMENT_SERVICE.SERVICE_ID,STORE_SERVICE.SERVICE_IMG, STORE_SERVICE.SERVICE_NAME,USER.USER_ID,USER.USERNAME, USER.MOBILE,COMMENT_SERVICE.COMMSTAR, COMMENT_SERVICE.COMM_NOTE, COMMENT_SERVICE.COMM_IMG, COMMENT_SERVICE.TECHNICIAN_ID, SERVICE_TECHNICIAN.TECHNICIAN_NAME, COMMENT_SERVICE.CREATE_TIME, COMMENT_SERVICE.ANONYMOUSFLAG, COMMENT_SERVICE.FLAG)
			.from(COMMENT_SERVICE)
			.leftJoin(STORE_SERVICE)
			.on(COMMENT_SERVICE.SERVICE_ID.eq(STORE_SERVICE.ID))
			.leftJoin(USER)
			.on(COMMENT_SERVICE.USER_ID.eq(USER.USER_ID))
			.leftJoin(SERVICE_TECHNICIAN)
			.on(COMMENT_SERVICE.TECHNICIAN_ID.eq(SERVICE_TECHNICIAN.ID));
		SelectConditionStep<?> select = buildOptions(selectFrom,param);
		select.orderBy(COMMENT_SERVICE.CREATE_TIME);
        PageResult<ServiceCommentVo> result = getPageResult(select, param.getCurrentPage(), param.getPageRows(), ServiceCommentVo.class);
        result.dataList.forEach((e) -> {
            e.setServiceImg(imgDomain(e.getServiceImg()));
            List<String> imgs = Util.json2Object(e.getCommImg(), new TypeReference<List<String>>() {
            }, false);
            if(imgs!= null && !imgs.isEmpty()) {
                e.setCommImgList(imgs.stream().map(i->{return domainConfig.imageUrl(i);}).collect(Collectors.toList()));
            }
        });
        return result;
    }

    public String imgDomain(String imgs) {
        List<String> imgList = Util.json2Object(imgs, new TypeReference<List<String>>() {
        }, false);
        if (CollectionUtils.isNotEmpty(imgList)) {
            return domainConfig.imageUrl(imgList.get(INTEGER_ZERO));
        } else {
            return org.apache.commons.lang3.StringUtils.EMPTY;
        }
    }

	/**
	 * @param selectFrom
     * @param param
     * @return
	 */
	private SelectConditionStep<?> buildOptions(SelectOnConditionStep<?> selectFrom, ServiceCommentPageListParam param) {
		SelectConditionStep<?> select = selectFrom.where(COMMENT_SERVICE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE));
		if(!StringUtils.isBlank(param.getOrderSn()))	{
			select = select.and(COMMENT_SERVICE.ORDER_SN.like(this.likeValue(param.getOrderSn())));
		}
		if(!StringUtils.isBlank(param.getServiceName())) {
			select = select.and(STORE_SERVICE.SERVICE_NAME.like(this.likeValue(param.getServiceName())));
		}
		if(param.getStoreId() != null) {
			select = select.and(COMMENT_SERVICE.STORE_ID.eq(param.getStoreId()));
		}
		if(!StringUtils.isBlank(param.getMobile())) {
			select = select.and(USER.MOBILE.like(this.likeValue(param.getMobile())));
		}
		if(!StringUtils.isBlank(param.getTechnicianName())) {
			select = select.and(SERVICE_TECHNICIAN.TECHNICIAN_NAME.like(this.likeValue(param.getTechnicianName())));
		}
		if(param.getCommstar() != null) {
			select = select.and(COMMENT_SERVICE.COMMSTAR.eq(param.getCommstar()));
		}
		if(param.getFlag() != null) {
			select = select.and(COMMENT_SERVICE.FLAG.eq(param.getFlag()));
		}
		return select;
	}

	/**
	 * 批量将传入ID的评论置为删除状态
	 * @param commentIdList
	 * @return
	 */
	public int batchDelete(List<Integer> commentIdList) {
		if(commentIdList == null) {
			return 0;
		}
		this.transaction(()->{
			db().update(COMMENT_SERVICE)
				.set(COMMENT_SERVICE.DEL_FLAG, DelFlag.DISABLE_VALUE)
				.where(COMMENT_SERVICE.ID.in(commentIdList))
				.and(COMMENT_SERVICE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)).execute();
		});
		return 0;
	}

	/**
	 * 批量将传入ID的评论置为通过状态
	 * @param commentIdList
	 * @return
	 */
	public int batchPass(List<Integer> commentIdList) {
		if(commentIdList == null) {
			return 0;
		}
		this.transaction(()->{
			db().update(COMMENT_SERVICE)
				.set(COMMENT_SERVICE.FLAG,CommentFlagEnum.PASS.getValue())
				.where(COMMENT_SERVICE.ID.in(commentIdList))
				.and(COMMENT_SERVICE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)).execute();
		});
		return 0;
	}

    /**
	 * 批量将传入ID的评论置为拒绝通过状态
	 * @param commentIdList
	 * @return
	 */
	public int batchRefuse(List<Integer> commentIdList) {
		if(commentIdList == null) {
			return 0;
		}
		this.transaction(()->{
			db().update(COMMENT_SERVICE)
				.set(COMMENT_SERVICE.FLAG,CommentFlagEnum.REFUSE.getValue())
				.where(COMMENT_SERVICE.ID.in(commentIdList))
				.and(COMMENT_SERVICE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)).execute();
		});
		return 0;
	}

    /**
     * Gets comment by service id.获取服务所有评论(只选择评审通过的)
     *
     * @param serviceId the service id
     * @return the comment by service id
     */
    public List<ServiceCommentVo> getCommentByServiceId(Integer serviceId) {
        return db().select(COMMENT_SERVICE.asterisk()).from(COMMENT_SERVICE)
            .where(COMMENT_SERVICE.SERVICE_ID.eq(serviceId))
            .and(COMMENT_SERVICE.FLAG.eq(BYTE_ONE))
            .and(COMMENT_SERVICE.DEL_FLAG.eq(BYTE_ZERO))
            .fetchInto(ServiceCommentVo.class);
    }

    /**
     * Gets newestcomment.获取服务的最新评论(只选择评审通过的)
     *
     * @param serviceId the service id
     * @return the newestcomment
     */
    public ServiceCommentVo getNewestcomment(Integer serviceId) {
        return db().select(COMMENT_SERVICE.COMM_NOTE, COMMENT_SERVICE.COMM_IMG
            , COMMENT_SERVICE.COMMSTAR, COMMENT_SERVICE.ANONYMOUSFLAG, COMMENT_SERVICE.CREATE_TIME
            , USER_DETAIL.USERNAME, USER_DETAIL.USER_AVATAR).from(COMMENT_SERVICE)
            .leftJoin(USER_DETAIL).on(COMMENT_SERVICE.USER_ID.eq(USER_DETAIL.USER_ID))
            .where(COMMENT_SERVICE.SERVICE_ID.eq(serviceId))
            .and(COMMENT_SERVICE.DEL_FLAG.eq(BYTE_ZERO))
            .and(COMMENT_SERVICE.FLAG.eq(BYTE_ONE))
            .orderBy(COMMENT_SERVICE.CREATE_TIME.desc())
            .limit(INTEGER_ONE)
            .fetchOneInto(ServiceCommentVo.class);
    }

    /**
     * Is comment boolean.订单是否已评价
     *
     * @param orderSn the order sn
     * @return the boolean
     */
    public boolean isComment(String orderSn) {
        return db().fetchExists(COMMENT_SERVICE, COMMENT_SERVICE.ORDER_SN.eq(orderSn));
    }

    /**
     * Gets comment by order id.获取用户自己订单的评价(与审核状态是否删除无关，只要评价过就算)
     *
     * @param orderSn the order sn
     * @return the comment by order id
     */
    public ServiceCommentVo getCommentByOrderSn(String orderSn) {
        ServiceCommentVo vo = db().select(COMMENT_SERVICE.asterisk(), SERVICE_ORDER.SERVICE_DATE, SERVICE_ORDER.SERVICE_PERIOD)
            .from(COMMENT_SERVICE)
            .leftJoin(SERVICE_ORDER).on(COMMENT_SERVICE.ORDER_SN.eq(SERVICE_ORDER.ORDER_SN))
            .where(COMMENT_SERVICE.ORDER_SN.eq(orderSn))
            .orderBy(COMMENT_SERVICE.CREATE_TIME.desc())
            .limit(INTEGER_ONE)
            .fetchOneInto(ServiceCommentVo.class);
        if (Objects.nonNull(vo)) {
            // 图片加域名处理
            String imgs = vo.getCommImg();
            List<String> stringList = Util.json2Object(imgs, new TypeReference<List<String>>() {
            }, false);
            if (CollectionUtils.isEmpty(stringList)) {
                return vo;
            }
            stringList = stringList.stream().map(domainConfig::imageUrl).collect(Collectors.toList());
            log.debug("评论图片有：{}", stringList.toString());
            vo.setCommImg(Util.toJson(stringList));
        }
        return vo;
    }


    /**
     * Create comment.添加评价
     *
     * @param record the record
     */
    public void createComment(CommentServiceRecord record) {
        db().executeInsert(record);
    }

    /**
     * 取一个服务的所有评价
     * @param param
     * @return
     */
    public AllCommentVo getAllComment(AllCommentParam param){
        SelectConditionStep select = db().select(COMMENT_SERVICE.COMMSTAR,COMMENT_SERVICE.ANONYMOUSFLAG,COMMENT_SERVICE.COMM_NOTE,COMMENT_SERVICE.COMM_IMG,USER_DETAIL.USERNAME,USER_DETAIL.USER_AVATAR,COMMENT_SERVICE.CREATE_TIME).
            from(COMMENT_SERVICE.leftJoin(USER_DETAIL).on(COMMENT_SERVICE.USER_ID.eq(USER_DETAIL.USER_ID))).
            where(COMMENT_SERVICE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(COMMENT_SERVICE.SERVICE_ID.eq(param.getServiceId())));

        Byte commConfig = saas.getShopApp(getShopId()).config.storeConfigService.getServiceComment();
        int publishAfterAudit = 2;
        if(commConfig == publishAfterAudit){
            select.and(COMMENT_SERVICE.FLAG.eq(BYTE_ONE));
        }else{
            select.and(COMMENT_SERVICE.FLAG.notEqual((byte)2));
        }

        int typeGoodComment = 1;
        int typeMiddleComment = 2;
        int typeBadComment = 3;
        if(param.getType() == typeGoodComment){
            select.and(COMMENT_SERVICE.COMMSTAR.eq((byte)4).or(COMMENT_SERVICE.COMMSTAR.eq((byte)5)));
        }else {
            if(param.getType() == typeMiddleComment){
                select.and(COMMENT_SERVICE.COMMSTAR.eq((byte)2).or(COMMENT_SERVICE.COMMSTAR.eq((byte)3)));
            }else if(param.getType() == typeBadComment){
                select.and(COMMENT_SERVICE.COMMSTAR.eq((byte)0).or(COMMENT_SERVICE.COMMSTAR.eq((byte)1)));
            }
        }

        List<AllCommentVo.Comment> commentList = select.orderBy(COMMENT_SERVICE.CREATE_TIME.desc()).fetchInto(AllCommentVo.Comment.class);

        commentList.forEach(comment -> {
            List<String> imgs = Util.json2Object(comment.getCommImg(), new TypeReference<List<String>>() {
            }, false);
            if(imgs!= null && !imgs.isEmpty()){
                comment.setCommImgList(imgs.stream().map(i->{return domainConfig.imageUrl(i);}).collect(Collectors.toList()));
            }
        });

        AllCommentVo vo = new AllCommentVo();
        vo.setComment(commentList);
        int[] numbers = getCommentsNumber(param,commConfig);
        vo.setNumbers(numbers);

        double[] ratio = new double[3];
        if(numbers[0] > 0){
            ratio[0] = (numbers[1]/(double)numbers[0])*100;
            ratio[1] = (numbers[2]/(double)numbers[0])*100;
            ratio[2] = (numbers[3]/(double)numbers[0])*100;
        }
        vo.setRatio(ratio);

        return vo;
    }

    /**
     * 评价数量
     * @param param
     * @param commConfig
     * @return
     */
    private int[] getCommentsNumber(AllCommentParam param,Byte commConfig){
        int[] numbers = new int[4];
        int publishAfterAudit = 2;
        if(commConfig == publishAfterAudit){
            numbers[1] = db().selectCount().from(COMMENT_SERVICE).where(COMMENT_SERVICE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(COMMENT_SERVICE.SERVICE_ID.eq(param.getServiceId())).and(COMMENT_SERVICE.FLAG.eq(BYTE_ONE)).and(COMMENT_SERVICE.COMMSTAR.eq((byte)4).or(COMMENT_SERVICE.COMMSTAR.eq((byte)5)))).fetchOptionalInto(Integer.class).orElse(0);
            numbers[2] = db().selectCount().from(COMMENT_SERVICE).where(COMMENT_SERVICE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(COMMENT_SERVICE.SERVICE_ID.eq(param.getServiceId())).and(COMMENT_SERVICE.FLAG.eq(BYTE_ONE)).and(COMMENT_SERVICE.COMMSTAR.eq((byte)2).or(COMMENT_SERVICE.COMMSTAR.eq((byte)3)))).fetchOptionalInto(Integer.class).orElse(0);
            numbers[3] = db().selectCount().from(COMMENT_SERVICE).where(COMMENT_SERVICE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(COMMENT_SERVICE.SERVICE_ID.eq(param.getServiceId())).and(COMMENT_SERVICE.FLAG.eq(BYTE_ONE)).and(COMMENT_SERVICE.COMMSTAR.eq((byte)0).or(COMMENT_SERVICE.COMMSTAR.eq((byte)1)))).fetchOptionalInto(Integer.class).orElse(0);
        }else{
            numbers[1] = db().selectCount().from(COMMENT_SERVICE).where(COMMENT_SERVICE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(COMMENT_SERVICE.SERVICE_ID.eq(param.getServiceId())).and(COMMENT_SERVICE.FLAG.notEqual((byte)2)).and(COMMENT_SERVICE.COMMSTAR.eq((byte)4).or(COMMENT_SERVICE.COMMSTAR.eq((byte)5)))).fetchOptionalInto(Integer.class).orElse(0);
            numbers[2] = db().selectCount().from(COMMENT_SERVICE).where(COMMENT_SERVICE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(COMMENT_SERVICE.SERVICE_ID.eq(param.getServiceId())).and(COMMENT_SERVICE.FLAG.notEqual((byte)2)).and(COMMENT_SERVICE.COMMSTAR.eq((byte)2).or(COMMENT_SERVICE.COMMSTAR.eq((byte)3)))).fetchOptionalInto(Integer.class).orElse(0);
            numbers[3] = db().selectCount().from(COMMENT_SERVICE).where(COMMENT_SERVICE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(COMMENT_SERVICE.SERVICE_ID.eq(param.getServiceId())).and(COMMENT_SERVICE.FLAG.notEqual((byte)2)).and(COMMENT_SERVICE.COMMSTAR.eq((byte)0).or(COMMENT_SERVICE.COMMSTAR.eq((byte)1)))).fetchOptionalInto(Integer.class).orElse(0);
        }

        //0全部，1好评，2中评，3差评
        numbers[0] = numbers[1] + numbers[2] + numbers[3];

        return numbers;
    }

}
