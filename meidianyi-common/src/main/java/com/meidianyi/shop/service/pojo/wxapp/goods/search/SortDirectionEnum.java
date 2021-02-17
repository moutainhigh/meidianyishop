package com.meidianyi.shop.service.pojo.wxapp.goods.search;

/**
 * @author 李晓冰
 * @date 2019年12月10日
 */
public enum SortDirectionEnum {
    /**从大到小排序0*/
    DESC{
        @Override
        public String toString(){
            return "desc";
        }
    },
    /**从小到大排序1*/
    ASC{
        @Override
        public String toString(){
            return "asc";
        }
    }
}
