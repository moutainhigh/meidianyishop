package com.meidianyi.shop.service.shop.goods.es.convert.param;

import com.meidianyi.shop.service.pojo.shop.goods.es.Fact;
import com.meidianyi.shop.service.pojo.shop.goods.es.FieldProperty;
import com.meidianyi.shop.service.pojo.shop.goods.es.Sort;
import com.meidianyi.shop.service.foundation.es.pojo.goods.EsSearchSource;

import java.util.List;

/**
 * @author lixinguo
 */
public abstract class BaseSearchParam {


    private Integer currentPage;

    private Integer pageRows;
    /**
     * 查询条件
     */
    private List<FieldProperty> searchList;
    /**
     * 聚合条件
     */
    private List<Fact> factList;

    /**
     * 排序条件
     */
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

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageRows() {
        return pageRows;
    }

    public void setPageRows(Integer pageRows) {
        this.pageRows = pageRows;
    }

    public List<FieldProperty> getSearchList() {
        return searchList;
    }

    public void setSearchList(List<FieldProperty> searchList) {
        this.searchList = searchList;
    }

    public List<Fact> getFactList() {
        return factList;
    }

    public void setFactList(List<Fact> factList) {
        this.factList = factList;
    }

    public List<Sort> getSorts() {
        return sorts;
    }

    public void setSorts(List<Sort> sorts) {
        this.sorts = sorts;
    }

    public String[] getIncludes() {
        return includes;
    }

    public void setIncludes(String[] includes) {
        this.includes = includes;
    }

    public Boolean getQueryByPage() {
        return queryByPage;
    }

    public void setQueryByPage(Boolean queryByPage) {
        this.queryByPage = queryByPage;
    }

    public EsSearchSource getSearchSource() {
        return searchSource;
    }

    public void setSearchSource(EsSearchSource searchSource) {
        this.searchSource = searchSource;
    }

    public Boolean getAnalyzerStatus() {
        return analyzerStatus;
    }

    public void setAnalyzerStatus(Boolean analyzerStatus) {
        this.analyzerStatus = analyzerStatus;
    }
}
