package com.meidianyi.shop.service.foundation.es.pojo.goods.label;

import com.meidianyi.shop.service.foundation.es.annotation.EsFiled;
import com.meidianyi.shop.service.foundation.es.annotation.EsFiledTypeConstant;
import com.meidianyi.shop.service.pojo.shop.goods.es.EsLabelName;

/**
 * ElasticSearch中商品标签Mapping
 * @author 卢光耀
 * @date 2019/11/22 2:27 下午
 *
*/
public class EsGoodsLabel {

    @EsFiled(name = EsLabelName.ID,type = EsFiledTypeConstant.INTEGER)
    private Integer id;
    @EsFiled(name = EsLabelName.GOODS_ID,type = EsFiledTypeConstant.INTEGER)
    private Integer goodsId;
    @EsFiled(name = EsLabelName.SHOP_ID ,type = EsFiledTypeConstant.INTEGER)
    private Integer shopId;
    @EsFiled(name = EsLabelName.TYPE ,type = EsFiledTypeConstant.BYTE)
    private Byte type;
    @EsFiled(name = EsLabelName.TYPE_ID ,type = EsFiledTypeConstant.INTEGER)
    private Integer typeId;
    @EsFiled(name = EsLabelName.CREATE_TIME ,type = EsFiledTypeConstant.DATE)
    private String createTime;
    @EsFiled(name = EsLabelName.LEVEL ,type = EsFiledTypeConstant.BYTE)
    private Short level;
    @EsFiled(name = EsLabelName.DETAIL_SHOW ,type = EsFiledTypeConstant.BOOL)
    private Boolean detailShow;
    @EsFiled(name = EsLabelName.SEARCH_SHOW,type = EsFiledTypeConstant.BOOL)
    private Boolean searchShow;
    @EsFiled(name = EsLabelName.LIST_SHOW,type = EsFiledTypeConstant.BOOL)
    private Boolean listShow;
    @EsFiled(name = EsLabelName.NAME,type = EsFiledTypeConstant.KEYWORD,doc_values = false)
    private String name;
    @EsFiled(name = EsLabelName.LIST_PATTERN,type = EsFiledTypeConstant.BYTE,doc_values = false)
    private Short listPattern;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Short getLevel() {
        return level;
    }

    public void setLevel(Short level) {
        this.level = level;
    }

    public Boolean getDetailShow() {
        return detailShow;
    }

    public void setDetailShow(Boolean detailShow) {
        this.detailShow = detailShow;
    }

    public Boolean getSearchShow() {
        return searchShow;
    }

    public void setSearchShow(Boolean searchShow) {
        this.searchShow = searchShow;
    }

    public Boolean getListShow() {
        return listShow;
    }

    public void setListShow(Boolean listShow) {
        this.listShow = listShow;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Short getListPattern() {
        return listPattern;
    }

    public void setListPattern(Short listPattern) {
        this.listPattern = listPattern;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }


}
