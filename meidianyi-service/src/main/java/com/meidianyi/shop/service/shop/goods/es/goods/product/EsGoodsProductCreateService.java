package com.meidianyi.shop.service.shop.goods.es.goods.product;

import com.beust.jcommander.internal.Lists;
import com.meidianyi.shop.service.foundation.es.EsManager;
import com.meidianyi.shop.service.foundation.es.EsUtil;
import com.meidianyi.shop.service.foundation.es.pojo.goods.product.EsGoodsProductEntity;
import com.meidianyi.shop.service.shop.goods.es.EsAssemblyDataService;
import com.meidianyi.shop.service.foundation.es.pojo.goods.EsGoods;
import com.meidianyi.shop.service.foundation.es.pojo.goods.EsGoodsConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author luguangyao
 */
@Slf4j
@Service
public class EsGoodsProductCreateService {

    @Autowired
    EsAssemblyDataService  esAssemblyDataService;
    @Autowired
    EsAssemblyProductService esAssemblyProductService;
    @Autowired
    EsManager esManager;
    /**
     * 批量更新es数据（修改调用）
     * @param goodsIds goodsId List
     * @param shopId shop id
     */
    public void batchUpdateEsProductIndex(List<Integer> goodsIds, Integer shopId){
        List<EsGoods> esGoodsList = esAssemblyDataService.assemblyEsGoods(goodsIds, shopId);
        List<EsGoodsProductEntity> esProducts= esAssemblyProductService.getEsProduct(esGoodsList);
        deleteEsGoods(getProductIds(esProducts,shopId));
        batchCommitProductIndex(getIndexRequest(esProducts), WriteRequest.RefreshPolicy.NONE);
    }
    /**
     * 批量删除es数据（删除调用）
     */
    public void deleteEsGoods(List<String> ids){
        try {
            esManager.deleteIndexById(EsGoodsConstant.PRODUCT_ALIA_NAME,ids);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 批量删除es数据（删除调用）
     */
    public void deleteEsGoods(List<EsGoodsProductEntity> entityList,Integer shopId){
        deleteEsGoods(getProductIds(entityList, shopId));
    }

    /**
     * 提交数据更新请求到es
     */
    public List<IndexRequest> buildIndexRequest(List<EsGoodsProductEntity> productEntityList){
        List<IndexRequest> requests = Lists.newArrayList();
        if( CollectionUtils.isEmpty(productEntityList) ){
            return requests;
        }
        for( EsGoodsProductEntity entity:productEntityList ){
            IndexRequest request = EsUtil.assemblyRequest(EsGoodsConstant.PRODUCT_ALIA_NAME,entity);
            requests.add(request);
        }
        return requests;
    }

    private List<String> getProductIds(List<EsGoodsProductEntity> productEntities,Integer shopId){
        List<String> results = Lists.newArrayList();
        if(CollectionUtils.isEmpty(productEntities)){
            return results;
        }
        for( EsGoodsProductEntity entity:productEntities ){
            results.add(shopId.toString()+entity.getGoodsId()+entity.getPrdId());
        }
        return results;
    }
    /**
     * 提交数据更新请求到es
     * @param requests 更新请求集合
     * @param policy 刷新策略
     */
    private void batchCommitProductIndex(List<IndexRequest> requests, WriteRequest.RefreshPolicy policy){
        BulkRequest bulkRequest = new BulkRequest();
        requests.forEach(bulkRequest::add);
        bulkRequest.setRefreshPolicy(policy);
        try {
            esManager.batchDocuments(bulkRequest);
        } catch (IOException e) {
            log.error("批量建立索引失败");
        }
    }
    private List<IndexRequest> getIndexRequest(List<EsGoodsProductEntity> goodsList){
        return goodsList.stream().
            filter(x->x.getShopId()!=null&&x.getGoodsId()!=null&&x.getPrdId()!=null).
            map(x-> EsUtil.assemblyRequest(EsGoodsConstant.PRODUCT_ALIA_NAME,x)).
            collect(Collectors.toList());
    }
}
