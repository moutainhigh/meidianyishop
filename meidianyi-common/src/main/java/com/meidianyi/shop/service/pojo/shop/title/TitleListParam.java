package com.meidianyi.shop.service.pojo.shop.title;

import lombok.Data;

/**
 * @author chenjie
 */
@Data
public class TitleListParam {
    private Integer nav;
    private Integer currentPage;
    private Integer pageRows;

    @Override
    public String toString() {
        return "TitleListParam{" +
            "nav=" + nav +
            ", currentPage=" + currentPage +
            ", pageRows=" + pageRows +
            '}';
    }
}
