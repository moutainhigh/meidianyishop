package com.meidianyi.shop.service.pojo.shop.goods.es;

import com.meidianyi.shop.service.foundation.es.pojo.goods.EsSearchSource;
import lombok.Data;

import java.util.List;

/**
 * es 查询条件(admin)
 * @author 卢光耀
 * @date 2019/10/28 4:16 下午
 *
*/
@Data
public class EsSearchParam {

    private Integer currentPage;

    private Integer pageRows;

    private List<FieldProperty> searchList;
    /**
     * 聚合条件
     */
    private List<Fact> factList;


    private List<Sort> sorts;
    /**
     * ElasticSearch返回的查询字段
     */
    private String[] includes;
    /**
     * 是否分页
     */
    private Boolean queryByPage;
    /**
     * 查询来源
     */
    private EsSearchSource searchSource;
    /**
     * 分词配置状态
     */
    private Boolean analyzerStatus;

    private String indexName;

}
