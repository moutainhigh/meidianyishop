//package com.meidianyi.shop.service.saas.es;
//
//import com.meidianyi.shop.service.pojo.saas.estest.GoodsEsEntity;
//import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
//
//import java.util.List;
//
///**
// * @author chenjie
// * @date 2020年08月06日
// */
//public interface GoodsEsRepository extends ElasticsearchRepository<GoodsEsEntity, Long> {
//
//    /**
//     * 根据goodsId区间查询
//     * @param min
//     * @param max
//     * @return
//     */
//    List<GoodsEsEntity> findByGoodsIdBetween(Integer min, Integer max);
//
//}