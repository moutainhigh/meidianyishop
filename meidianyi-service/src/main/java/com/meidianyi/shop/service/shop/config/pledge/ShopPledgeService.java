package com.meidianyi.shop.service.shop.config.pledge;

import com.google.common.collect.Lists;
import com.meidianyi.shop.db.shop.tables.Pledge;
import com.meidianyi.shop.db.shop.tables.PledgeRelated;
import com.meidianyi.shop.db.shop.tables.records.PledgeRecord;
import com.meidianyi.shop.db.shop.tables.records.PledgeRelatedRecord;
import com.meidianyi.shop.service.foundation.mq.RabbitmqSendService;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.config.pledge.*;
import com.meidianyi.shop.service.pojo.wxapp.config.pledge.PledgeListParam;
import com.meidianyi.shop.service.pojo.wxapp.config.pledge.PledgeListVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectWhereStep;
import org.jooq.impl.DSL;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.tables.Pledge.PLEDGE;
import static com.meidianyi.shop.db.shop.tables.PledgeRelated.PLEDGE_RELATED;

/**
 * PledgeService
 *
 * @author: 卢光耀
 * @date: 2019-07-09 15:05
 */
@Service
public class ShopPledgeService extends ShopBaseService {


  private static final int MAX_INSERT_NUMBERS = 20;
  private static final Byte ALL_GOODS = 1;
  private static final Byte PART_GOODS = 2;
  private static final Byte TYPE_GOODS = 1;
  private static final Byte TYPE_CAT = 2;
  private static final Byte TYPE_BRAND = 3;

  public List<PledgeInfo> getPledgeList() {
    List<PledgeInfo> list =
        db().select(
                PLEDGE.ID,
                PLEDGE.PLEDGE_NAME,
                PLEDGE.PLEDGE_LOGO,
                PLEDGE.PLEDGE_CONTENT,
                PLEDGE.STATE,
                PLEDGE.LEVEL,
                PLEDGE.TYPE)
            .from(PLEDGE)
            .where(PLEDGE.DEL_FLAG.eq(PledgePojo.DELFLAG_NOT))
            .orderBy(PLEDGE.CREATE_TIME.asc())
            .fetch()
            .into(PledgeInfo.class);
    assemblyPledgeRelatedForPledgeInfo(list);
    return list;
  }

  public void assemblyPledgeRelatedForPledgeInfo(List<PledgeInfo> infoList){
      Map<Integer,PledgeInfo> pledgeMap = infoList.stream().collect(Collectors.toMap(PledgeInfo::getId, Function.identity()));
      List<Integer> pledgeIds = Lists.newArrayList(pledgeMap.keySet());
      Result<PledgeRelatedRecord> result =db().select().
          from(PLEDGE_RELATED).
          where(PLEDGE_RELATED.PLEDGE_ID.in(pledgeIds)).
          fetchInto(PLEDGE_RELATED);
      for( PledgeRelatedRecord record: result ){
          PledgeInfo info = pledgeMap.get(record.getPledgeId());
          if( PledgeConstant.PLEDGE_RELATE_TYPE_GOODS.equals(record.getType()) ){
              info.getGoodsIds().add(record.getRelatedId());
          }else if( PledgeConstant.PLEDGE_RELATE_TYPE_SORT.equals(record.getType()) ){
              info.getSortIds().add(record.getRelatedId());
          }else {
              info.getGoodsBrandIds().add(record.getRelatedId());
          }
      }
  }

  /**
   * 小程序-向用户展示服务承诺
   *
   * @param param 预留部分-根据当前商品筛选服务承诺
   * @return list 满足条件的服务承诺列表
   */
  public List<PledgeListVo> wxAppPledgeList(PledgeListParam param) {
    // 判断是否第一次使用服务承诺
    Integer count = db().select(DSL.count(PLEDGE.ID)).from(PLEDGE).fetchOneInto(Integer.class);
    // 第一次使用服务承诺插入默认三条数据
    if (count == 0) {
        initPledgeParam();
    }
    List<PledgeListVo> list = new ArrayList<>();
    // 筛选-配置为全部商品的服务承诺
    List<PledgeListVo> vo =
        db().select(PLEDGE.ID, PLEDGE.PLEDGE_NAME, PLEDGE.PLEDGE_LOGO, PLEDGE.PLEDGE_CONTENT,PLEDGE.LEVEL)
            .from(PLEDGE)
            .where(PLEDGE.DEL_FLAG.eq(NumberUtils.BYTE_ZERO))
            .and(PLEDGE.STATE.eq(NumberUtils.BYTE_ONE))
            .and(PLEDGE.TYPE.eq(ALL_GOODS))
            .orderBy(PLEDGE.LEVEL.desc())
            .fetchInto(PledgeListVo.class);
      list.addAll(vo);
    // 筛选-配置为部分商品的服务承诺
    // 指定商品id
      List<PledgeListVo> goodsIdVo =
          db().select(PLEDGE.ID, PLEDGE.PLEDGE_NAME, PLEDGE.PLEDGE_LOGO, PLEDGE.PLEDGE_CONTENT,PLEDGE.LEVEL)
              .from(PLEDGE)
              .leftJoin(PLEDGE_RELATED).on(PLEDGE.ID.eq(PLEDGE_RELATED.PLEDGE_ID))
              .where(PLEDGE.DEL_FLAG.eq(NumberUtils.BYTE_ZERO))
              .and(PLEDGE.STATE.eq(NumberUtils.BYTE_ONE))
              .and(PLEDGE.TYPE.eq(PART_GOODS))
              .and(PLEDGE_RELATED.TYPE.eq(TYPE_GOODS))
              .and(PLEDGE_RELATED.RELATED_ID.eq(param.getGoodsId()))
              .orderBy(PLEDGE.LEVEL.desc())
              .fetchInto(PledgeListVo.class);
      list.addAll(goodsIdVo);
      //指定分类id
      if (null!=param.getCatId()&&param.getCatId()!=0){
          List<PledgeListVo> catIdVo =
              db().select(PLEDGE.ID, PLEDGE.PLEDGE_NAME, PLEDGE.PLEDGE_LOGO, PLEDGE.PLEDGE_CONTENT,PLEDGE.LEVEL)
                  .from(PLEDGE)
                  .leftJoin(PLEDGE_RELATED).on(PLEDGE.ID.eq(PLEDGE_RELATED.PLEDGE_ID))
                  .and(PLEDGE.STATE.eq(NumberUtils.BYTE_ONE))
                  .where(PLEDGE.DEL_FLAG.eq(NumberUtils.BYTE_ZERO))
                  .and(PLEDGE.TYPE.eq(PART_GOODS))
                  .and(PLEDGE_RELATED.TYPE.eq(TYPE_CAT))
                  .and(PLEDGE_RELATED.RELATED_ID.eq(param.getCatId()))
                  .orderBy(PLEDGE.LEVEL.desc())
                  .fetchInto(PledgeListVo.class);
          list.addAll(catIdVo);
      }
      //指定品牌id
      if (null!=param.getBrandId()&&param.getBrandId()!=0){
          List<PledgeListVo> brandIdVo =
              db().select(PLEDGE.ID, PLEDGE.PLEDGE_NAME, PLEDGE.PLEDGE_LOGO, PLEDGE.PLEDGE_CONTENT,PLEDGE.LEVEL)
                  .from(PLEDGE)
                  .leftJoin(PLEDGE_RELATED).on(PLEDGE.ID.eq(PLEDGE_RELATED.PLEDGE_ID))
                  .where(PLEDGE.DEL_FLAG.eq(NumberUtils.BYTE_ZERO))
                  .and(PLEDGE.TYPE.eq(PART_GOODS))
                  .and(PLEDGE.STATE.eq(NumberUtils.BYTE_ONE))
                  .and(PLEDGE_RELATED.TYPE.eq(TYPE_BRAND))
                  .and(PLEDGE_RELATED.RELATED_ID.eq(param.getBrandId()))
                  .orderBy(PLEDGE.LEVEL.desc())
                  .fetchInto(PledgeListVo.class);
          list.addAll(brandIdVo);
      }
      //根据优先级排序
      Collections.sort(list, (o1, o2) -> {
          if (o1.getLevel()>o2.getLevel()){
              return -1;
          }
          if (o1.getLevel().equals(o2.getLevel())){
              return 0;
          }
          return 1;
      });
    return list;
  }

    private void initPledgeParam() {
        PledgeParam firstParam =
            new PledgeParam() {
              {
                setPledgeName("7天退换");
                setPledgeLogo("/image/admin/pledge_seven.png");
                setPledgeContent("在未损坏商品的情况下，商家支持消费者申请7天无理由退换货。");
                setType(NumberUtils.BYTE_ONE);
              }
            };
        insertPledge(firstParam);
        PledgeParam secondParam =
            new PledgeParam() {
              {
                setPledgeName("正品保障");
                setPledgeLogo("/image/admin/pledge_zp.png");
                setPledgeContent("商家承诺，店铺内所有商品都为正品。");
                setType(NumberUtils.BYTE_ONE);
              }
            };
        insertPledge(secondParam);
        PledgeParam thirdParam =
            new PledgeParam() {
              {
                setPledgeName("闪电发货");
                setPledgeLogo("/image/admin/pledfe_flash.png");
                setPledgeContent("商家承诺23:00之前下单者，当日发货。");
                setType(NumberUtils.BYTE_ONE);
              }
            };
        insertPledge(thirdParam);
    }

    public boolean judgeInsertParam() {
    SelectWhereStep<? extends Record> select = db().select(PLEDGE.ID).from(PLEDGE);
    select.where(PLEDGE.DEL_FLAG.eq(PledgePojo.DELFLAG_NOT));
    int count = db().fetchCount(select);
    return count > MAX_INSERT_NUMBERS ? Boolean.FALSE : Boolean.TRUE;
  }

  public void insertPledge(PledgeParam param) {

    PledgeRecord record = db().newRecord(PLEDGE, param);
    Integer id = db().insertInto(PLEDGE).set(record).returning(PLEDGE.ID).fetchOne().getId();
    batchInsertPledgeRelated(param,id);
  }

    /**
     * 批量添加服务承诺关联表数据
     * @param param 商品范围相关数据
     * @param id 服务承诺id
     * @return 新加条数
     */
  private int batchInsertPledgeRelated(PledgeParam param,Integer id){
      if( PledgeConstant.PLEDGE_TYPE_DESIGNATED_GOODS.equals(param.getType()) ){
          List<PledgeRelatedRecord> pledgeRelatedList = getPledgeRelatedRecords(param,id);
          if( !pledgeRelatedList.isEmpty() ){
              return db().batchInsert(pledgeRelatedList).execute().length;
          }
      }
      return 0;
  }

  public void updatePledge(PledgeParam param) {
    PledgeRecord record = db().newRecord(PLEDGE, param);
    record.update();
    deletePledgeRelatedByPledgeId(record.getId());
    batchInsertPledgeRelated(param,record.getId());
  }

  public void updatePledgeState(PledgeStateUpdateParam param) {
    db().update(PLEDGE)
        .set(PLEDGE.STATE, param.getState())
        .where(PLEDGE.ID.eq(param.getId()))
        .execute();
  }

  public void deletePledgeState(PledgeStateUpdateParam param) {
    //服务承诺删除
    db().update(PLEDGE)
        .set(PLEDGE.DEL_FLAG, PledgePojo.DELFLAG_DEL)
        .where(PLEDGE.ID.eq(param.getId()))
        .execute();
    //服务承诺关联表删除
    db().delete(PLEDGE_RELATED).where(PLEDGE_RELATED.PLEDGE_ID.eq(param.getId())).execute();
  }

  private List<PledgeRelatedRecord> getPledgeRelatedRecords(PledgeParam param,Integer pledgeId){
      List<PledgeRelatedRecord> result = Lists.newArrayList();
      if( !CollectionUtils.isEmpty(param.getGoodsIds()) ){
          result.addAll(param.getGoodsIds().stream().
              map(x->
                  new PledgeRelatedRecord().
                      value2(pledgeId).value3(PledgeConstant.PLEDGE_RELATE_TYPE_GOODS).value4(x)).
              collect(Collectors.toList())
          );
      }
      if( !CollectionUtils.isEmpty(param.getSortIds()) ){
          result.addAll(param.getSortIds().stream().
              map(x->
                  new PledgeRelatedRecord().
                      value2(pledgeId).value3(PledgeConstant.PLEDGE_RELATE_TYPE_SORT).value4(x)).
              collect(Collectors.toList())
          );
      }
      if( !CollectionUtils.isEmpty(param.getGoodsBrandIds()) ){
          result.addAll(param.getGoodsBrandIds().stream().
              map(x->
                  new PledgeRelatedRecord().
                      value2(pledgeId).value3(PledgeConstant.PLEDGE_RELATE_TYPE_BRAND).value4(x)).
              collect(Collectors.toList())
          );
      }
      return result;
  }

  private void deletePledgeRelatedByPledgeId(Integer pledgeId){
      db().delete(PLEDGE_RELATED).where(PLEDGE_RELATED.PLEDGE_ID.eq(pledgeId)).execute();
  }

}
