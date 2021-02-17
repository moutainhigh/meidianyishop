package com.meidianyi.shop.service.foundation.jedis.data;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author luguangyao
 */
public interface DataHelperInterface<T> {

    Integer TIME_OUT = 60 * 60 * 24 * 15;

    /**
     * get
     *
     * @param ids
     * @return
     */
    List<T> get(List<Integer> ids);


    /**
     * update
     *
     * @param t
     */
    void update(T t);

    /**
     * update list
     *
     * @param id
     */
    void update(List<Integer> id);

    /**
     * batch update
     *
     * @param values
     */
    void batchUpdate(List<T> values);

    /**
     * delete
     *
     * @param id
     */
    void delete(Integer id);

    /**
     * delete
     *
     * @param ids
     */
    default void delete(List<Integer> ids) {
        for (Integer id : ids) {
            delete(id);
        }
    }

    /**
     * getKey
     *
     * @return
     */
    String getKey();

}
