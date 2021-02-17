package com.meidianyi.shop.service.shop.user.user;

import static com.meidianyi.shop.db.shop.Tables.CART;
import static com.meidianyi.shop.db.shop.Tables.ORDER_GOODS;
import static com.meidianyi.shop.db.shop.Tables.ORDER_INFO;
import static com.meidianyi.shop.db.shop.Tables.USER;
import static com.meidianyi.shop.db.shop.Tables.USER_CARD;
import static com.meidianyi.shop.db.shop.Tables.USER_LOGIN_RECORD;
import static com.meidianyi.shop.db.shop.Tables.USER_TAG;
import static com.meidianyi.shop.db.shop.tables.MpOfficialAccountUser.MP_OFFICIAL_ACCOUNT_USER;
import static com.meidianyi.shop.db.shop.tables.MpTemplateFormId.MP_TEMPLATE_FORM_ID;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.notExists;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.sum;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.User;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Record4;
import org.jooq.SelectConditionStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.service.foundation.jedis.JedisKeyConstant;
import com.meidianyi.shop.service.foundation.jedis.JedisManager;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.market.message.CustomRuleInfo;
import com.meidianyi.shop.service.pojo.shop.market.message.MessageUserQuery;
import com.meidianyi.shop.service.pojo.shop.market.message.UserInfoByRedis;
import com.meidianyi.shop.service.pojo.shop.market.message.UserInfoQuery;
import com.meidianyi.shop.service.pojo.shop.market.message.UserInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;

/**
 * 发送人群实现
 * @author 卢光耀
 * @date 2019-08-12 10:10
 *
*/
@Service
public class SendUserService extends ShopBaseService {


    @Autowired
    private  JedisManager jedisManager;

    @Autowired
    private MpOfficialAccountUserByShop mpOfficialAccountUserByShop;


    public String getKeyWithRedisBySendUser(){
        return JedisKeyConstant.SEND_USER_KEY+getShopId()+"."+Util.randomId();
    }

    /**
     * 从redis获取数据并删除
     * @param key 标识
     * @return 从redis获取的数据
     */
    public List<UserInfoByRedis> getAndDeleteSendUserIdByRedisKey(String key){
        List<UserInfoByRedis> results = Util.readValue(jedisManager.get(key),List.class,UserInfoByRedis.class);
        jedisManager.delete(key);
        return results;


    }

    /**
     * 从redis获取数据
     * @param key 标识
     * @return 从redis获取的数据
     */
    public List<UserInfoByRedis> getSendUserInfoByRedisKey(String key){
        String value = jedisManager.get(key);
        if( StringUtils.isNotBlank(value) ){
            return Util.readValue(value,List.class,UserInfoByRedis.class);
        }else {
            return Lists.newArrayList();
        }

    }

    /**
     * 获取发送人群，返回存入redis的key，key是复用的
     * @param query 发送人群的筛选条件
     * @param key 调用{@link SendUserService#getKeyWithRedisBySendUser}获得
     * @return sendUserId的size
     */
    public Integer getSendUserByQuery(UserInfoQuery query,String key){
        Set<Integer> userIdSet = new HashSet<>();
        int result = 0;
        if(query.getOnClickNoPay()){
            userIdSet.addAll(getSendUserByShoppingcartNoPay());
        }
        if(query.getOnClickGoods() && !CollectionUtils.isEmpty(query.getGoodsIdList()) ){
            userIdSet.addAll(getSendUserByGoodsIdList(query.getGoodsIdList()));
        }
        if (query.getOnClickCard() && !CollectionUtils.isEmpty(query.getCardIdsList()) ){
            userIdSet.addAll(getSendUserByMemberCardList(query.getCardIdsList()));
        }
        if (query.getOnClickTag() && !CollectionUtils.isEmpty(query.getTagIdList())){
            userIdSet.addAll(getSendUserByUserTagList(query.getTagIdList()));
        }
        if (query.getOnClickUser() && !CollectionUtils.isEmpty(query.getUserIdList())){
            userIdSet.addAll(new ArrayList<>(query.getUserIdList()));
        }

        processCustomRule(query, userIdSet);


        if( !userIdSet.isEmpty() ){
                List<UserInfoByRedis> list= new ArrayList<>(userIdSet.size());
                Map<Integer,UserInfoByRedis> map = Maps.newHashMap();
                if( StringUtils.isNotBlank(query.getUserKey()) ){
                    List<UserInfoByRedis> redisList = getSendUserInfoByRedisKey(query.getUserKey());
                    map= redisList
                        .stream()
                        .collect(Collectors.toMap(UserInfoByRedis::getUserId,x->x));
                }
                Map<Integer, Record4<Integer,String,String,String>> mpMap = getAllMpUser(new ArrayList<>(userIdSet));
                for( Integer id:userIdSet ){
                    UserInfoByRedis info = new UserInfoByRedis();
                    info.setUserId(id);
                    if(mpMap.containsKey(id)  ){
                        info.setIsChecked(Boolean.TRUE);
                        info.setIsVisitMp(Boolean.TRUE);
                        info.setCanSend(Boolean.TRUE);
                    }
                    if( StringUtils.isNotBlank(query.getUserKey()) && map.containsKey(id)){
                        info.setIsChecked(map.get(id).getIsChecked());
                    }
                    if( info.getCanSend() && info.getIsChecked()){
                        result += 1;
                    }
                    list.add(info);
                }
                setUserToJedis(key,list);
            }
        return result;
    }

    private void processCustomRule(UserInfoQuery query, Set<Integer> userIdSet) {
        if (query.getOnClickCustomRule()){
            CustomRuleInfo info = query.getCustomRuleInfo();
            if (info != null){
                Date today = new Date();
                if ( null != info.getPayedDay()){
                    userIdSet.addAll(getSendUserByPayedDay(info.getPayedDay(),today));
                }
                if ( null != info.getNoPayDay()){
                    userIdSet.addAll(getSendUserByNoPayDay(info.getNoPayDay(),today));
                }
                if ( null != info.getBuyTimesMore()){
                    userIdSet.addAll(getSendUserByBuyTimesMore(info.getBuyTimesMore()));
                }
                if ( null != info.getBuyTimesLess()){
                    userIdSet.addAll(getSendUserByBuyTimesLess(info.getBuyTimesLess()));
                }
                if ( null != info.getMoneyAvgMore()){
                    userIdSet.addAll(getSendUserByMoneyAvgMore(info.getMoneyAvgMore()));
                }
                if ( null != info.getMoneyAvgLess()){
                    userIdSet.addAll(getSendUserByMoneyAvgLess(info.getMoneyAvgLess()));
                }
                if ( null != info.getLoginStart() || null != info.getLoginEnd()){
                    Date min = new Date(Long.MIN_VALUE);
                    userIdSet.addAll(getSendUserByLoginTime(
                        null!=info.getLoginStart()?info.getLoginStart():new Timestamp(min.getTime()),
                        null!=info.getLoginEnd()?info.getLoginEnd():new Timestamp(today.getTime())
                    ));
                }
            }
        }
    }

    public void setUserToJedis(String key,List<UserInfoByRedis> list){
        jedisManager.delete(key);
        jedisManager.set(key, Util.toJson(list));
    }
    /**
     * 获取关注公众号的的人数
     * @param userIds
     * @return
     */
    private Map<Integer, Record4<Integer,String,String,String>> getAllMpUser(List<Integer> userIds){
        return mpOfficialAccountUserByShop.getAccountUserListByUserIds(userIds)
            .stream()
            .collect(Collectors.toMap(x->x.get(User.USER.USER_ID), x->x));
    }
    private Map<Integer, Integer> getAllMaUser(List<Integer> userIds){
        return db().select(MP_TEMPLATE_FORM_ID.USER_ID,count(MP_TEMPLATE_FORM_ID.USER_ID).as("numbers"))
            .from(MP_TEMPLATE_FORM_ID)
            .where(MP_TEMPLATE_FORM_ID.USER_ID.in(userIds))
            .and(MP_TEMPLATE_FORM_ID.USE_STATE.eq((byte)0))
            .groupBy(MP_TEMPLATE_FORM_ID.USER_ID)
            .orderBy(MP_TEMPLATE_FORM_ID.CREATE_TIME.desc())
            .fetch()
            .stream()
            .collect(Collectors.toMap(x->x.get(MP_TEMPLATE_FORM_ID.USER_ID),x->Integer.parseInt(x.get("numbers").toString())));
    }

    private List<Integer> getSendUserByShoppingcartNoPay(){
        Date today = new Date();
        Timestamp cartDay = Util.getEarlyTimeStamp(today, -30);
        return db().select(CART.USER_ID).
            from(CART).
            where(CART.CREATE_TIME.greaterOrEqual(cartDay)).
            fetch().into(Integer.class);
    }
    private List<Integer> getSendUserByGoodsIdList(List<Integer> goodsIdList){
        return db().select(ORDER_INFO.USER_ID).
            from(ORDER_INFO).
            leftJoin(ORDER_GOODS).on(ORDER_GOODS.ORDER_ID.eq(ORDER_INFO.ORDER_ID)).
            where(ORDER_INFO.ORDER_STATUS.greaterOrEqual(OrderConstant.ORDER_CLOSED)).
            and(ORDER_GOODS.GOODS_ID.in(goodsIdList)).
            fetch().into(Integer.class);
    }
    private List<Integer> getSendUserByMemberCardList(List<Integer> cardList){
        return db().selectDistinct(USER_CARD.USER_ID).
            from(USER_CARD).
            where(USER_CARD.FLAG.eq((byte) 0)).and(USER_CARD.CARD_ID.in(cardList)).
            fetch().into(Integer.class);
    }
    private List<Integer> getSendUserByUserTagList(List<Integer> tagList){
    	
    	return db().select(USER_TAG.USER_ID).
                from(USER_TAG).
                where(USER_TAG.TAG_ID.in(tagList)).
                fetch().into(Integer.class);
    }
    private List<Integer> getSendUserByPayedDay(Integer days,Date today){
        Timestamp havePayDay = Util.getEarlyTimeStamp(today, -days);
        return db().select(ORDER_INFO.USER_ID).from(ORDER_INFO).
            where(ORDER_INFO.ORDER_STATUS.greaterOrEqual(OrderConstant.ORDER_CLOSED)).
            and(ORDER_INFO.CREATE_TIME.greaterOrEqual(havePayDay)).
            fetch().into(Integer.class);
    }
    private List<Integer> getSendUserByNoPayDay(Integer days,Date today){
        Timestamp noPayDay = Util.getEarlyTimeStamp(today, -days);
        return db().select(USER.USER_ID).from(USER).
            where(
                notExists(
                    select(ORDER_INFO.USER_ID).from(ORDER_INFO).
                    where(ORDER_INFO.ORDER_STATUS.greaterOrEqual(OrderConstant.ORDER_CLOSED).
                        and(ORDER_INFO.CREATE_TIME.greaterOrEqual(noPayDay))))
            ).
            fetch().into(Integer.class);
    }
    private List<Integer> getSendUserByBuyTimesMore(Integer times){
        return db().select(ORDER_INFO.USER_ID).from(ORDER_INFO).
            where(ORDER_INFO.ORDER_STATUS.greaterOrEqual(OrderConstant.ORDER_CLOSED)).
            groupBy(ORDER_INFO.USER_ID).
            having(count(ORDER_INFO.ORDER_ID).greaterThan(times)).
            fetch().into(Integer.class);
    }
    private List<Integer> getSendUserByBuyTimesLess(Integer times){
        return db().select(ORDER_INFO.USER_ID).from(ORDER_INFO).
            where(ORDER_INFO.ORDER_STATUS.greaterOrEqual(OrderConstant.ORDER_CLOSED)).
            groupBy(ORDER_INFO.USER_ID).
            having(count(ORDER_INFO.ORDER_ID).lessThan(times)).
            fetch().into(Integer.class);
    }
    private List<Integer> getSendUserByMoneyAvgMore(BigDecimal money){
        return db().select(ORDER_INFO.USER_ID).from(ORDER_INFO).
            where(ORDER_INFO.ORDER_STATUS.greaterOrEqual(OrderConstant.ORDER_CLOSED)).
            groupBy(ORDER_INFO.USER_ID).
            having((sum(ORDER_INFO.ORDER_AMOUNT).divide(sum(ORDER_INFO.GOODS_AMOUNT))).greaterThan(money)).
            fetch().into(Integer.class);
    }
    private List<Integer> getSendUserByMoneyAvgLess(BigDecimal money){
        return db().select(ORDER_INFO.USER_ID).from(ORDER_INFO).
            where(ORDER_INFO.ORDER_STATUS.greaterOrEqual(OrderConstant.ORDER_CLOSED)).
            groupBy(ORDER_INFO.USER_ID).
            having((sum(ORDER_INFO.ORDER_AMOUNT).divide(sum(ORDER_INFO.GOODS_AMOUNT))).lessThan(money)).
            fetch().into(Integer.class);
    }
    private List<Integer> getSendUserByLoginTime(Timestamp start,Timestamp end){
        return db().select(USER_LOGIN_RECORD.USER_ID).from(USER_LOGIN_RECORD)
            .where(USER_LOGIN_RECORD.CREATE_TIME.between(start,end))
            .fetch().into(Integer.class);
    }
    public PageResult<UserInfoVo> getUserInfoByIds(List<Integer> userIds, MessageUserQuery query){
        SelectConditionStep select = db()
            .select(USER.USER_ID,USER.USERNAME,USER.MOBILE,MP_OFFICIAL_ACCOUNT_USER.SUBSCRIBE,count(MP_TEMPLATE_FORM_ID.USER_ID).as("numbers"))
            .from(USER)
            .leftJoin(MP_OFFICIAL_ACCOUNT_USER).on(MP_OFFICIAL_ACCOUNT_USER.UNIONID.eq(USER.WX_UNION_ID))
            .leftJoin(MP_TEMPLATE_FORM_ID).on(MP_TEMPLATE_FORM_ID.USER_ID.eq(USER.USER_ID))
            .where(USER.USER_ID.in(userIds));
        if( query.getIsVisit() != null ){
            select.and(MP_OFFICIAL_ACCOUNT_USER.SUBSCRIBE.eq(query.getIsVisit()));
        }
        if(StringUtils.isNotBlank(query.getUserName()) ){
            select.and(USER.USERNAME.contains(query.getUserName()));
        }
        if( query.getId() != null){
            select.and(USER.USER_ID.eq(query.getId()));
        }
        if( StringUtils.isNotBlank(query.getPhone()) ){
            select.and(USER.MOBILE.contains(query.getPhone()));
        }
        select.groupBy(USER.USER_ID,USER.USERNAME,USER.MOBILE,MP_OFFICIAL_ACCOUNT_USER.SUBSCRIBE)
            .orderBy(MP_OFFICIAL_ACCOUNT_USER.SUBSCRIBE.asc());
        return getPageResult(select,query.getCurrentPage(), UserInfoVo.class);
    }






}
