package com.meidianyi.shop.service.shop.goods.es;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.index.reindex.ReindexRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.service.foundation.es.EsManager;
import com.meidianyi.shop.service.foundation.es.EsUtil;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.foundation.es.pojo.goods.EsGoods;
import com.meidianyi.shop.service.foundation.es.pojo.goods.EsGoodsConstant;
import com.meidianyi.shop.service.shop.goods.es.goods.product.EsAssemblyProductService;
import com.meidianyi.shop.service.shop.goods.es.goods.product.EsGoodsProductCreateService;
import com.meidianyi.shop.service.foundation.es.pojo.goods.product.EsGoodsProductEntity;

import lombok.extern.slf4j.Slf4j;

/**
 * EsGoods相关操作
 * @author 卢光耀
 * @date 2019/10/9 10:23 上午
*/
@Slf4j
@Service
public class EsGoodsCreateService extends ShopBaseService {

    @Autowired
    private EsManager esManager;
    @Autowired
    private EsAssemblyDataService esAssemblyDataService;
    @Autowired
    private EsAssemblyProductService esAssemblyProductService;
    @Autowired
    private EsGoodsProductCreateService esGoodsProductCreateService;

    /**
     * 批量更新es数据（修改调用）
     * @param goodsIds goodsId List
     * @param shopId shop id
     */
    public void batchUpdateEsGoodsIndex( List<Integer> goodsIds,Integer shopId){
        List<EsGoods> esGoodsList = esAssemblyDataService.assemblyEsGoods(goodsIds, shopId);
        List<EsGoodsProductEntity> productEntityList = esAssemblyProductService.getEsProduct(esGoodsList);
        esGoodsProductCreateService.deleteEsGoods(productEntityList,shopId);
        deleteEsGoods(goodsIds,shopId);
        batchCommitEsGoodsIndex(esGoodsProductCreateService.buildIndexRequest(productEntityList), WriteRequest.RefreshPolicy.NONE);
        batchCommitEsGoodsIndex(getIndexRequest(esGoodsList));
    }


    /**
     * 给es新加数据
     * @param esGoodsList {@link EsGoods}集合
     */
    public void batchCreateEsGoodsIndex(List<EsGoods> esGoodsList){
        batchCommitEsGoodsIndex(getIndexRequest(esGoodsList));
    }

    /**
     * 给es新加数据
     * @param esGoodsList {@link EsGoods}集合
     */
    public void batchCreateEsGoodsIndex(List<EsGoods> esGoodsList,String indexName){
        batchCommitEsGoodsIndex(getIndexRequest(esGoodsList,indexName));
    }


    /**
     * 单个更新es数据（修改调用）
     * @param goodsId 商品id
     */
    public void updateEsGoodsIndex(Integer goodsId,Integer shopId){
        List<EsGoods> esGoodsList = esAssemblyDataService.assemblyEsGoods(Collections.singletonList(goodsId), shopId);
        List<EsGoodsProductEntity> productEntityList = esAssemblyProductService.getEsProduct(esGoodsList);
        deleteEsGoods(goodsId,shopId);
        esGoodsProductCreateService.deleteEsGoods(productEntityList,shopId);
        batchCommitEsGoodsIndex(esGoodsProductCreateService.buildIndexRequest(productEntityList), WriteRequest.RefreshPolicy.IMMEDIATE);
        batchCommitEsGoodsIndex(getIndexRequest(esGoodsList),WriteRequest.RefreshPolicy.IMMEDIATE);
    }
    /**
     * 单个更新es数据（修改调用）
     * @param goodsId 商品id
     */
    public void updateEsGoodsProductIndex(Integer goodsId,Integer productId,Integer shopId){
        List<EsGoods> esGoodsList = esAssemblyDataService.assemblyEsGoods(Collections.singletonList(goodsId), shopId);
        deleteEsGoods(goodsId,shopId);
        batchCommitEsGoodsIndex(getIndexRequest(esGoodsList),WriteRequest.RefreshPolicy.IMMEDIATE);
    }

    /**
     * 单个删除es数据（删除调用）
     * @param goodsId goodsId
     * @param shopId shop id
     */
    public void deleteEsGoods(Integer goodsId,Integer shopId){
        try {
            esManager.deleteIndexById(EsGoodsConstant.GOODS_ALIA_NAME,shopId+goodsId.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 批量删除es数据（删除调用）
     * @param goodsIds goodsId List
     * @param shopId shop id
     */
    public void deleteEsGoods(List<Integer> goodsIds,Integer shopId){
        try {
            List<String> list = goodsIds.stream().map(x->shopId.toString()+x).collect(Collectors.toList());
            esManager.deleteIndexById(EsGoodsConstant.GOODS_ALIA_NAME,list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 提交数据更新请求到es
     * @param requests 更新请求集合
     * @param policy 刷新策略
     */
    private void batchCommitEsGoodsIndex(List<IndexRequest> requests,WriteRequest.RefreshPolicy policy){
        BulkRequest bulkRequest = new BulkRequest();
        requests.forEach(bulkRequest::add);
        bulkRequest.setRefreshPolicy(policy);
        try {
            esManager.batchDocuments(bulkRequest);
        } catch (IOException e) {
            log.error("批量建立索引失败");
        }
    }

    /**
     * 提交数据更新请求到es（使用默认刷新策略）
     * @param requests 更新请求集合
     */
    private void batchCommitEsGoodsIndex(List<IndexRequest> requests){
        batchCommitEsGoodsIndex(requests,WriteRequest.RefreshPolicy.NONE);
    }

    /**
     * 通过要更新的数据封装更新请求
     * @param goods 要更新的数据
     * @return 更新请求
     */
    private IndexRequest getIndexRequest(EsGoods goods){
        return EsUtil.assemblyRequest(EsGoodsConstant.GOODS_ALIA_NAME,goods);
    }
    /**
     * 通过要更新的数据封装更新请求
     * @param goodsList 要更新的数据集合
     * @return 更新请求
     */
    private List<IndexRequest> getIndexRequest(List<EsGoods> goodsList){
        return goodsList.stream().
            map(x->EsUtil.assemblyRequest(EsGoodsConstant.GOODS_ALIA_NAME,x)).
            collect(Collectors.toList());
    }
    /**
     * 通过要更新的数据封装更新请求
     * @param goodsList 要更新的数据集合
     * @return 更新请求
     */
    private List<IndexRequest> getIndexRequest(List<EsGoods> goodsList,String indexName){
        return goodsList.stream().
            map(x->EsUtil.assemblyRequest(indexName,x)).
            collect(Collectors.toList());
    }



    private void reIndexGoods(List<EsGoods> esGoodsList){
        ReindexRequest request = new ReindexRequest();
    }
}
