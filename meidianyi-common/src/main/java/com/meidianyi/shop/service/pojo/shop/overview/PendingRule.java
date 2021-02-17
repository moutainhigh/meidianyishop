package com.meidianyi.shop.service.pojo.shop.overview;

import java.util.Arrays;
import java.util.Objects;

import static org.apache.commons.lang3.math.NumberUtils.BYTE_ONE;
import static org.apache.commons.lang3.math.NumberUtils.BYTE_ZERO;

/**
 * The interface Pending rule.
 *
 * @param <R> the type parameter
 * @author liufei
 * @date 11 /1/19
 */
public interface PendingRule<R> {

    /**
     * ruleHandler
     *
     * @return
     */
    R ruleHandler();

    /**
     * getUnFinished
     *
     * @return
     */
    int getUnFinished();


    /**
     * Handler 1.规则一(0已完成, 非0未完成)
     *
     * @param metadata
     */
    default void handler1(Metadata... metadata) {
        Arrays.stream(metadata).filter(Objects::nonNull).forEach(e -> {
            if (e.getValue() == 0) {
                e.setStatus(BYTE_ONE);
            } else {
                e.setStatus(BYTE_ZERO);
            }
        });
    }


    /**
     * Handler 2.规则二(非0已完成, 0未完成)
     *
     * @param metadata
     */
    default void handler2(Metadata... metadata) {
        Arrays.stream(metadata).filter(Objects::nonNull).forEach(e -> {
            if (e.getValue() == 0) {
                e.setStatus(BYTE_ZERO);
            } else {
                e.setStatus(BYTE_ONE);
            }
        });
    }


    /**
     * unFinished
     *
     * @param metadata
     * @return
     */
    default int unFinished(Metadata... metadata) {
        return (int) Arrays.stream(metadata)
            .filter(e -> e.getStatus() == BYTE_ZERO).count();
    }
}
