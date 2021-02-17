package com.meidianyi.shop.service.saas.es;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.mq.RabbitConfig;
import com.meidianyi.shop.service.foundation.es.EsManager;
import com.meidianyi.shop.service.foundation.es.EsUtil;
import com.meidianyi.shop.service.foundation.es.annotation.EsFiled;
import com.meidianyi.shop.service.foundation.es.annotation.EsFiledSerializer;
import com.meidianyi.shop.service.foundation.es.thread.EsDynamicAssemblyProcessor;
import com.meidianyi.shop.service.foundation.es.thread.EsGoodsThread;
import com.meidianyi.shop.service.foundation.mq.RabbitMqUtilService;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.foundation.es.pojo.goods.EsGoods;
import com.meidianyi.shop.service.foundation.es.pojo.goods.EsGoodsConstant;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest.AliasActions;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.GetAliasesResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.client.indices.GetMappingsResponse;
import org.elasticsearch.common.compress.CompressedXContent;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.VersionType;
import org.elasticsearch.index.reindex.ReindexRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author luguangyao
 */
@Service
@Slf4j
public class EsMappingUpdateService extends MainBaseService {

    @Autowired
    @Qualifier("vpuThreadPool")
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    public EsDynamicAssemblyProcessor processor;

    @Autowired
    private EsManager esManager;

    @Autowired
    private RabbitMqUtilService rabbitMqUtilService;

    private volatile Boolean enabled = Boolean.TRUE;

    public Boolean getEsStatus(){
        return this.enabled;
    }


    public void updateEsGoodsMapping(){
        //创建新索引
        String indexName = createNewGoodsIndex();

        String oldIndexName = getIndexNameByAlias(EsGoodsConstant.GOODS_ALIA_NAME);

        List<Future<Boolean>> futures = Lists.newArrayList();
        this.enabled = Boolean.FALSE;
        //停止ESGoods的mq的消费
        rabbitMqUtilService.stopQueueConsumption(RabbitConfig.QUEUE_ES_GOODS);
        rabbitMqUtilService.stopQueueConsumption(RabbitConfig.QUEUE_ES_LABEL);

        //线程池跑每个店铺的商品索引
        List<Integer> shopIds = saas().shop.getEnabledShopIds();
        for (Integer shopId :shopIds){
            EsGoodsThread thread = new EsGoodsThread(saas());
            thread.init(shopId,indexName);
            futures.add(taskExecutor.submit(thread));
        }
        for (Future<Boolean> future : futures){
            try {
                if ( future != null && !future.get() ){
                    log.error("【ES Thread Pool】--[{}] error",Thread.currentThread().getName());
                }
            } catch (InterruptedException  |ExecutionException e ) {
                e.printStackTrace();
            }
        }
        List<String> intersectionFields = getEsGoodsIntersectionFields(EsGoodsConstant.GOODS_ALIA_NAME,oldIndexName);
//        List<String> intersectionFields = Lists.newArrayList("goods_name","goods_ad");
        //复制已有字段的value
        reIndex(indexName,EsGoodsConstant.GOODS_ALIA_NAME,intersectionFields.toArray(new String[0]));

        esManager.deleteIndexByName(oldIndexName);

        addAliasToIndex(indexName,EsGoodsConstant.GOODS_ALIA_NAME);
        //重启mq消费
        rabbitMqUtilService.startQueueConsumption();
        this.enabled = Boolean.TRUE;

    }



    public IndexRequest assemblyRequest(@NotNull String indexName, Object object){
        IndexRequest request = new IndexRequest(indexName);
        //商品索引ID设为shopId+goodsId
        if( object instanceof EsGoods ){
            EsGoods goods = (EsGoods)object;
            request.id(goods.getShopId().toString()+goods.getGoodsId().toString());
        }
        String objectJsonStr = Util.toJson(object, new EsFiledSerializer());
        Objects.requireNonNull(objectJsonStr);
        request.source(objectJsonStr, XContentType.JSON);
        return request;
    }

    /**
     * 获取新旧索引中的不同字段
     * @return  Key:fieldName|Value:fieldType
     */
    public List<String> getEsGoodsIntersectionFields(String aliasName,String oldIndexName){
        CompressedXContent oldMapping = getOldMapping(aliasName,oldIndexName);
        Map<String,String> oldMappings = getOldMappingInfos(oldMapping);
        Map<String,String> newMappings = getNewMapping(EsGoods.class);
        return comparedAndGetIntersection(oldMappings,newMappings);
    }


    /**
     * 创建es商品索引(不关联索引别名)
     * @return 新索引的indexName(索引名称)
     */
    public String createNewGoodsIndex(){
        CreateIndexRequest indexRequest = EsUtil.getCreateRequest(EsGoodsConstant.GOODS_ALIA_NAME,Boolean.FALSE);
        esManager.createIndexRequest(indexRequest);
        return indexRequest.index();
    }

    /**
     * 获取es中索引的mapping
     * @param indexName indexName/alias
     * @param oldIndexName indexName
     * @return mapping(字段映射)
     */
    public CompressedXContent getOldMapping(String indexName,String oldIndexName){
        GetMappingsRequest request = new GetMappingsRequest();
        request.indices(indexName);
        GetMappingsResponse response = esManager.getMappingsResponse(request);
        return response.mappings().get(oldIndexName).source();
    }

    /**
     * 获取项目中es商品索引对应的实体类的映射关系
     * @param clz 实体类
     * @return 映射关系（key:字段名，value:类型)
     */
    private Map<String,String> getNewMapping(Class clz){
        Map<String,String> result = Maps.newHashMap();
        Field[] fieldArray = clz.getDeclaredFields();
        for( Field field : fieldArray ){
            EsFiled esFiled = field.getAnnotation(EsFiled.class);
            if( esFiled != null ){
                result.put(esFiled.name(),esFiled.type());
            }
        }
        return result;
    }

    /**
     * 把从es中获取的映射关系转化为Map
     * @param oldMapping es的映射关系
     * @return 映射关系（key:字段名，value:类型)
     */
    private Map<String,String> getOldMappingInfos(CompressedXContent oldMapping){
        Map<String,String> result = Maps.newHashMap();
        JsonNode jsonNode = Util.toJsonNode(oldMapping.toString());
        String properties = "properties";
        if( jsonNode == null || jsonNode.get(properties).isNull() ){
            return result;
        }
        Iterator<Map.Entry<String,JsonNode>> iterator =  jsonNode.get(properties).fields();
        while (iterator.hasNext()){
            Map.Entry<String,JsonNode> entry = iterator.next();
            result.put(entry.getKey(),entry.getValue().get("type").asText());
        }

        return result;
    }

    /**
     * map对比(取差集)
     * @param old   oldMap
     * @param now   newMap
     * @return different Key
     */
    private List<String> comparedAndGetDifferent(Map<String,String> old,Map<String,String> now){
        List<String> result = Lists.newArrayList();
        for ( Map.Entry<String,String> entry: now.entrySet()) {
            String fieldName = entry.getKey();
            String type = old.getOrDefault(fieldName,"");
            if( "".equals(type) || !type.equals(entry.getValue()) ){
                result.add(fieldName);
            }
        }
        return result;
    }
    /**
     * map对比(取交集集)
     * @param old   oldMap
     * @param now   newMap
     * @return Get Intersection
     */
    private List<String> comparedAndGetIntersection(Map<String,String> old,Map<String,String> now){
        List<String> result = Lists.newArrayList();
        for ( Map.Entry<String,String> entry: now.entrySet()) {
            String fieldName = entry.getKey();
            String type = old.getOrDefault(fieldName,"");
            if( (!"".equals(type)) && type.equals(entry.getValue()) ){
                result.add(fieldName);
            }
        }
        return result;
    }

    private void reIndex(String target,String source,String[] includes){
        ReindexRequest request = new ReindexRequest();
        request.setSourceIndices(source);
        request.getSearchRequest().source().fetchSource(includes,null);
        request.setDestIndex(target);
        request.setDestVersionType(VersionType.INTERNAL);
        request.setRefresh(true);
        esManager.reIndex(request);
    }

    private void addAliasToIndex(String indexName,String aliasName){
        IndicesAliasesRequest request = new IndicesAliasesRequest();
        AliasActions aliasesAction = new AliasActions(AliasActions.Type.ADD)
            .index(indexName)
            .alias(aliasName);

        request.addAliasAction(aliasesAction);
        esManager.indexAlias(request);
    }

    private String getIndexNameByAlias(String alias){
        GetAliasesRequest request = new GetAliasesRequest();
        request.aliases(alias);
        GetAliasesResponse response = esManager.getIndexByAlias(request);
        return response.getAliases().keySet().stream().findFirst().orElse("");
    }


//    private void switchIndex(){
//        IndicesAliasesRequest request = new IndicesAliasesRequest();
//        AliasActions aliasActions = new AliasActions(AliasActions.Type.ADD)
//            .index(EsGoodsConstant.GOODS_INDEX_NAME)
//            .alias("vpu_goods_1587628058991");
//        AliasActions aliasActions1 = new AliasActions(AliasActions.Type.REMOVE)
//            .index(ES_GOODS_NEW)
//            .alias("vpu_goods_1587628058991");
//        AliasActions aliasActions2 = new AliasActions(AliasActions.Type.REMOVE)
//            .index(EsGoodsConstant.GOODS_INDEX_NAME)
//            .alias("vpu_goods_1587093419183");
//        request.addAliasAction(aliasActions);
//        request.addAliasAction(aliasActions1);
//        request.addAliasAction(aliasActions2);
//        esManager.switchIndexAlias(request);
//    }

}
