package com.meidianyi.shop.service.foundation.es;

import com.meidianyi.shop.service.foundation.email.EmailMsgTemplate;
import com.meidianyi.shop.service.foundation.email.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.GetAliasesResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.*;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.ReindexRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * ES对外服务
 * @author 卢光耀
 * @date 2019/10/9 10:07 上午
 *
*/
@Service
@Slf4j
public class EsManager {

    @Autowired(required = false)
    @Qualifier("esConfig")
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private EmailService emailService;



    private final static String EMAIL_TO_USER = "luguangyao@huice.com";

    /**
     * 通用搜索方法
     * @param searchRequest 查询参数
     * @return 查询响应
     * @throws IOException 连接异常
     */
    public SearchResponse searchResponse(@NotNull SearchRequest searchRequest) throws IOException {
        log.info("\n本次搜索条件【{}】",searchRequest.source().toString());
        return restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
    }


    /**
     * 判断ES服务是否可用
     * @return true:可用/false:不可用
     */
    public boolean esState(){
        try {
            return restHighLevelClient.ping(RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("ElasticSearch can't use");
            return false;
        }
    }

    /**
     * 单个对象建立索引（异步）
     * @param indexName 索引名称
     * @param object 对应的索引对象
     */
    public void createIndexAsync(@NotNull String indexName,Object object)  {
        IndexRequest request = EsUtil.assemblyRequest(indexName,object);
        ActionListener<IndexResponse> listener = new ActionListener<IndexResponse>() {
            @Override
            public void onResponse(IndexResponse createIndexResponse) {
                log.info("【ES建立索引】--索引建立成功");
            }
            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                log.info("【ES建立索引】--索引建立失败");
            }
        };
        restHighLevelClient.indexAsync(request, RequestOptions.DEFAULT,listener);
    }



    /**
     * 批量处理索引（同步）
     * @param request 批量索引的请求
     * @throws IOException 索引失败的异常（自己捕获处理）
     */
    public void batchDocuments(BulkRequest request) throws IOException {
        if( request.requests().isEmpty() ){
            return ;
        }
        BulkResponse response =  restHighLevelClient.bulk(request,RequestOptions.DEFAULT);
        if( response.hasFailures() ){
            String table = EmailMsgTemplate.TableTemplate.table;
            String idTh = String.format(EmailMsgTemplate.TableTemplate.th,"id");
            String msgTh = String.format(EmailMsgTemplate.TableTemplate.th,"msg");
            String tr = String.format(EmailMsgTemplate.TableTemplate.tr,idTh+msgTh);
            StringBuilder tableContext = new StringBuilder(tr);
            for(BulkItemResponse itemResponse: response){
                if( itemResponse.isFailed() ){
                    String td = String.format(EmailMsgTemplate.TableTemplate.td,itemResponse.getId());
                    String td2 = String.format(EmailMsgTemplate.TableTemplate.td,itemResponse.getFailureMessage());
                    String msgTr = String.format(EmailMsgTemplate.TableTemplate.tr,td+td2);
                    tableContext.append(msgTr);
                }
            }
            emailService.sendHtmlMessage(EMAIL_TO_USER,"ElasticSearch Error Message",
                new Date()+String.format(table, tableContext.toString()));
            log.error("ERROR");
        }
    }
    /**
     * 单个建立索引（同步）
     * @param request 批量索引的请求
     * @throws IOException 索引失败的异常（自己捕获处理）
     */
    public void createDocuments(IndexRequest request) throws IOException {
        IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);

    }

    /**
     * 根据索引名称删除索引全部数据
     * @param indexName 索引名称
     */
    public void deleteIndexByName(String indexName){
        DeleteIndexRequest request = new DeleteIndexRequest(indexName);
        try {
            restHighLevelClient.indices().delete(request,RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据索引名称和documentID删除索引
     * @param indexName 索引名称
     * @param id documentId
     * @throws IOException 连接错误
     */
    public void deleteIndexById(String indexName,String id) throws IOException {
        DeleteRequest request = new DeleteRequest(indexName,id);
        restHighLevelClient.delete(request,RequestOptions.DEFAULT);
    }
    /**
     * 根据索引名称和documentID删除索引
     * @param indexName 索引名称
     * @param ids documentId List
     * @throws IOException 连接错误
     */
    public void deleteIndexByIdAsync(String indexName, List<String> ids) throws IOException {
        BulkRequest request = new BulkRequest();
        ids.forEach(x-> request.add(new DeleteRequest(indexName,x)));
        batchDocuments(request);
    }
    /**
     * 根据索引名称和documentID删除索引(同步)
     * @param indexName 索引名称
     * @param ids documentId List
     * @throws IOException 连接错误
     */
    public void deleteIndexById(String indexName, List<String> ids) throws IOException {
        BulkRequest request = new BulkRequest();
        ids.forEach(x-> request.add(new DeleteRequest(indexName,x)));
        request.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        batchDocuments(request);
    }

    /**
     * execute DeleteByQueryRequest
     * @param request {@link DeleteByQueryRequest}
     */
    public void deleteIndexByQuery(DeleteByQueryRequest request){
        try {
            log.info("\n本次删除搜索条件【{}】",request.toString());
            BulkByScrollResponse bulkResponse =
                restHighLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);

            if( bulkResponse.getVersionConflicts() > 0 ){
                //Number of version conflicts
                log.error("this delete has VersionConflicts");
            }else if( !bulkResponse.getSearchFailures().isEmpty() ){
                log.error("this delete querying error");
            }else if( !bulkResponse.getBulkFailures().isEmpty() ){
                log.error("this delete execute error");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通用搜索方法
     * @param countRequest 查询参数
     * @return 查询响应
     * @throws IOException 连接异常
     */
    public CountResponse getDocumentCount(@NotNull CountRequest countRequest) throws IOException {
        log.info("\n本次统计数量的搜索条件【{}】",countRequest.source().toString());
        return restHighLevelClient.count(countRequest,RequestOptions.DEFAULT);
    }

    /**
     * 创建索引请求
     * @param request createRequest
     * @return response
     */
    public CreateIndexResponse createIndexRequest(CreateIndexRequest request){
        try {
            return restHighLevelClient.indices().create(request,RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.info("【ElasticSearch】------ send create index request fail");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取索引的映射
     * @param request getMapping
     * @return response
     */
    public GetMappingsResponse getMappingsResponse(GetMappingsRequest request){
        try {
            return restHighLevelClient.indices().getMapping(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.info("【ElasticSearch】------ send get Mapping request fail");
            e.printStackTrace();
        }
        return null;
    }

    public BulkByScrollResponse reIndex(ReindexRequest reindexRequest){
        try {
            log.info(reindexRequest.toString());
            return restHighLevelClient.reindex(reindexRequest,RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public AcknowledgedResponse indexAlias(IndicesAliasesRequest request){
        try {
            return restHighLevelClient.indices().updateAliases(request,RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean checkIndexExists(GetIndexRequest getIndexRequest){
        try {
            return restHighLevelClient.indices().exists(getIndexRequest,RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("\nElasticSearch index init fail，host/port fail");
            return false;
        }
    }

    public GetAliasesResponse getIndexByAlias(GetAliasesRequest request){
        try {
            return restHighLevelClient.indices().getAlias(request,RequestOptions.DEFAULT) ;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
