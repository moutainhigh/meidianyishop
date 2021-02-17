package com.meidianyi.shop.service.foundation.jedis.data.label;

import com.meidianyi.shop.db.shop.tables.records.GoodsLabelRecord;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.sql.Timestamp;


/**
 * 商品标签
 * @author 卢光耀
 * @date 2019/11/20 9:47 上午
 *
*/
@Getter
@Setter
public class GoodsLabelInfo {

    private Integer id;

    private String name;

    private Boolean detailShow = Boolean.FALSE;

    private Boolean listShow = Boolean.FALSE;

    private Boolean searchShow = Boolean.FALSE;

    private Short listPattern;

    private GoodsLabelInfo(){}

    public static GoodsLabelInfo getGoodsLabelInfo(){
        return new GoodsLabelInfo();
    }

    public  GoodsLabelInfo convert( GoodsLabelRecord source){
        BeanUtils.copyProperties(source,this);
        this.detailShow = source.getGoodsDetail() == 1;
        this.listShow = source.getGoodsList() == 1;
        this.searchShow = source.getGoodsSelect() == 1;
        return this;
    }

}
