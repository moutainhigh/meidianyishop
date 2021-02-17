package com.meidianyi.shop.service.foundation.es.handler;

import com.google.common.collect.Lists;
import org.elasticsearch.index.query.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author luguangyao
 */
public class QueryBuilderArrays {
    private List<TermQueryBuilder> termQueryBuilders;
    private List<TermsQueryBuilder> termsQueryBuilders;
    private List<MatchQueryBuilder> matchQueryBuilders;
    private List<RangeQueryBuilder> rangeQueryBuilders;

    protected QueryBuilderArrays() {
        termQueryBuilders = Lists.newArrayList();
        termsQueryBuilders = Lists.newArrayList();
        matchQueryBuilders = Lists.newArrayList();
        rangeQueryBuilders = Lists.newArrayList();
    }


    public List<TermQueryBuilder> getTermQueryBuilders() {
        return termQueryBuilders;
    }

    public List<TermsQueryBuilder> getTermsQueryBuilders() {
        return termsQueryBuilders;
    }

    public List<MatchQueryBuilder> getMatchQueryBuilders() {
        return matchQueryBuilders;
    }

    public List<RangeQueryBuilder> getRangeQueryBuilders() {
        return rangeQueryBuilders;
    }

    public List<List<? extends QueryBuilder>> getAll(){
        return Arrays.asList(this.matchQueryBuilders,this.rangeQueryBuilders,this.termQueryBuilders,this.termsQueryBuilders);
    }
}
