package com.meidianyi.shop.service.foundation.es;

import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.foundation.es.annotation.EsFiled;
import com.meidianyi.shop.service.foundation.es.annotation.EsFiledSerializer;
import com.meidianyi.shop.service.foundation.es.annotation.EsFiledTypeConstant;
import com.meidianyi.shop.service.foundation.es.pojo.goods.EsGoods;
import com.meidianyi.shop.service.foundation.es.pojo.goods.EsGoodsConstant;
import com.meidianyi.shop.service.foundation.es.pojo.goods.EsGoodsGrade;
import com.meidianyi.shop.service.foundation.es.pojo.goods.EsGoodsProduct;
import com.meidianyi.shop.service.foundation.es.pojo.goods.label.EsGoodsLabel;
import com.meidianyi.shop.service.foundation.es.pojo.goods.product.EsGoodsProductEntity;
import com.meidianyi.shop.service.pojo.shop.goods.es.EsSearchName;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.common.xcontent.json.JsonXContent;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Objects;

/**
 * @author luguangyao
 */
public class EsUtil {


    public static final EsFiledSerializer ES_FILED_SERIALIZER = new EsFiledSerializer();

    /**
     * EsGoods Mapping init
     * @return XContentBuilder
     */
    public static XContentBuilder createGoodsMapping(Class<?> clz) {
        XContentBuilder xContentBuilder=null;
        try {
            xContentBuilder = JsonXContent.contentBuilder()
                .startObject()
                .startObject("properties");
            assemblyXcontentBuilder(xContentBuilder,clz);
            xContentBuilder.endObject().endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return xContentBuilder;
    }
    private static void assemblyXcontentBuilder(XContentBuilder xContentBuilder, Class<?> clz) throws IOException {
        Field[] fieldArray = clz.getDeclaredFields();
        for( Field field : fieldArray ){
            EsFiled a = field.getAnnotation(EsFiled.class);
            if( a != null ){
                xContentBuilder.startObject(a.name());
                xContentBuilder.field("type",a.type());
                if( field.getName().equals(EsSearchName.PRDS) ){
                    xContentBuilder.startObject("properties");
                    assemblyXcontentBuilder(xContentBuilder,EsGoodsProduct.class);
                    xContentBuilder.endObject();
                }else if(EsSearchName.GRADES.equals(field.getName())){
                    xContentBuilder.startObject("properties");
                    assemblyXcontentBuilder(xContentBuilder, EsGoodsGrade.class);
                    xContentBuilder.endObject();
                }else{
                    xContentBuilder.field("index",a.index());
                }
                if( EsFiledTypeConstant.DATE.equals(a.type()) ){
                    xContentBuilder.field("format","yyyy-MM-dd HH:mm:ss");
                }
                if(StringUtils.isNotBlank(a.analyzer()) ){
                    xContentBuilder.field("analyzer",a.analyzer());
                }
                if( StringUtils.isNotBlank(a.copyTo()) ){
                    xContentBuilder.field("copy_to",a.copyTo());
                }
                if(StringUtils.isNotBlank(a.searchAnalyzer()) ){
                    xContentBuilder.field("search_analyzer",a.searchAnalyzer());
                }
                if( "scaled_float".equals(a.type()) ){
                    xContentBuilder.field("scaling_factor",a.scaledNumber());
                }
                String goodsName = "goods_name";
                if( a.name().equals(goodsName) ){
                    xContentBuilder.startObject("fields")
                        .startObject("sing")
                        .field("type","text")
                        .field("analyzer","diy_my_analyzer")
//                            .field("index","not_analyzed")
                        .endObject().endObject();
                }

                xContentBuilder.endObject();
            }
        }
    }

    /**
     * 生成商品索引名称
     * @return 别名
     */
    private static String getEsGoodsIndexName(){
        return EsGoodsConstant.GOODS_INDEX_NAME_PREFIX + Calendar.getInstance().getTimeInMillis();
    }
    /**
     * 生成商品标签索引名称
     * @return 别名
     */
    private static String getEsLabelIndexName(){
        return EsGoodsConstant.LABEL_INDEX_NAME_PREFIX+ Calendar.getInstance().getTimeInMillis();
    }
    /**
     * 生成商品标签索引名称
     * @return 别名
     */
    private static String getEsProductIndexName(){
        return EsGoodsConstant.PRODUCT_INDEX_NAME_PREFIX+ Calendar.getInstance().getTimeInMillis();
    }

    /**
     * 根据索引名称生成对应的新建索引请求
     * @param aliaName 索引别名
     * @return 新建索引请求
     */
    public static CreateIndexRequest getCreateRequest(String aliaName,Boolean setAlias){
        String indexName;
        XContentBuilder mapping;
        if( aliaName.equals(EsGoodsConstant.GOODS_ALIA_NAME) ){
            indexName = getEsGoodsIndexName();
            mapping = createGoodsMapping(EsGoods.class);
        }else if ( aliaName.equals(EsGoodsConstant.LABEL_ALIA_NAME) ){
            indexName = getEsLabelIndexName();
            mapping = EsUtil.createGoodsMapping(EsGoodsLabel.class);
        }else if ( aliaName.equals(EsGoodsConstant.PRODUCT_ALIA_NAME) ){
            indexName = getEsProductIndexName();
            mapping = EsUtil.createGoodsMapping(EsGoodsProductEntity.class);
        }else{
            throw new Error("ElasticSearch create index error");
        }
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
        createIndexRequest.settings(getIndexSetting(aliaName));
        createIndexRequest.mapping(mapping);
        if( setAlias ){
            createIndexRequest.alias(new Alias(aliaName));
        }
        return createIndexRequest;
    }

    private static XContentBuilder getIndexSetting(String aliaName){
        try {
            //由于现阶段的ElasticSearch的部署是单机版因此副分片数量设为0（副分片需要至少两个ES服务才能生效）
            XContentBuilder builder = JsonXContent.contentBuilder()
                .startObject()
                .field("number_of_replicas",0)
                .field("number_of_shards",1);
            if( aliaName.equals(EsGoodsConstant.GOODS_ALIA_NAME) || aliaName.equals(EsGoodsConstant.PRODUCT_ALIA_NAME)){
                getNgramAnalyzerSetting(builder);
            }
            return builder.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 实现类似sql like 查询的自定分词器
     * @return json（setting）
     */
    private static XContentBuilder getNgramAnalyzerSetting(XContentBuilder builder){
        try {
                builder.startObject("analysis")
                    .startObject("analyzer")
                        .startObject("diy_my_analyzer")
                            .field("tokenizer","my_tokenizer")
                        .endObject()
                    .endObject()
                    .startObject("tokenizer")
                        .startObject("my_tokenizer")
                            .field("type","ngram")
                            .field("min_gram",1)
                            .field("max_gram",1)
//                            .field("token_chars", Lists.newArrayList("letter", "digit"))
                        .endObject()
                    .endObject()
                .endObject();
            return builder;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 封装es的commit的request
     * @param indexName 索引名称
     * @param object 对应的索引对象
     * @return IndexRequest
     */
    public static IndexRequest assemblyRequest(@NotNull String indexName, Object object){
        IndexRequest request = new IndexRequest(indexName);
        //商品索引ID设为shopId+goodsId
        if( object instanceof EsGoods ){
            EsGoods goods = (EsGoods)object;
            request.id(goods.getShopId().toString()+goods.getGoodsId().toString());
        }
        //商品标签索引ID设为shopId+goodsId+labelId
        if( object instanceof EsGoodsLabel){
            EsGoodsLabel goodsLabel = (EsGoodsLabel)object;
            request.id(goodsLabel.getShopId().toString()+goodsLabel.getGoodsId().toString()+goodsLabel.getId().toString());
        }
        //商品标签索引ID设为shopId+goodsId+labelId
        if( object instanceof EsGoodsProductEntity){
            EsGoodsProductEntity entity = (EsGoodsProductEntity)object;
            request.id(entity.getShopId().toString()+entity.getGoodsId().toString()+entity.getPrdId().toString());
        }
        String objectJsonStr = Util.toJson(object, ES_FILED_SERIALIZER);
        Objects.requireNonNull(objectJsonStr);
        request.source(objectJsonStr, XContentType.JSON);
        return request;
    }
//    public static List<String> get
}
