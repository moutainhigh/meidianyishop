package com.meidianyi.shop.service.pojo.wxapp.goods.goodssort;

/**
 * @author luguangyao
 */
public class GoodsSortCacheInfo {

    private Integer sortId;

    private Short level;

    private Integer parentId;

    private String sortName;

    public Integer getSortId() {
        return sortId;
    }

    public void setSortId(Integer sortId) {
        this.sortId = sortId;
    }

    public Short getLevel() {
        return level;
    }

    public void setLevel(Short level) {
        this.level = level;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }
}
