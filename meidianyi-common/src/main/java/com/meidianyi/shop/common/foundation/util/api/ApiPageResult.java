package com.meidianyi.shop.common.foundation.util.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 对外接口分页查询方法返回数据
 * @author 李晓冰
 * @date 2020年05月28日
 */
@Data
public class ApiPageResult<T> {
    @JsonProperty("cur_page_no")
    Integer curPageNo;
    @JsonProperty("page_size")
    Integer pageSize;

    @JsonIgnore
    Integer totalCount;
    @JsonIgnore
    public List<T> dataList;}
