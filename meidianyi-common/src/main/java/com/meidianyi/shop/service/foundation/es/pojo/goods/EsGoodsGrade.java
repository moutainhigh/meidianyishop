package com.meidianyi.shop.service.foundation.es.pojo.goods;

import com.meidianyi.shop.service.foundation.es.annotation.EsFiled;
import com.meidianyi.shop.service.foundation.es.annotation.EsFiledTypeConstant;
import com.meidianyi.shop.service.pojo.shop.goods.es.EsSearchName;

import java.math.BigDecimal;


/**
 * ES 商品中不同会员等级中的各个规格对应的价格
 * @author luguangyao
 */
public class EsGoodsGrade {


    @EsFiled(name = EsSearchName.Grade.PRD_ID,type = EsFiledTypeConstant.INTEGER)
    private Integer prdId;
    @EsFiled(name = EsSearchName.Grade.GRADE_PRICE,type = EsFiledTypeConstant.SCALED_FLOAT)
    private BigDecimal gradePrice;
    @EsFiled(name = EsSearchName.Grade.GRADE,type = EsFiledTypeConstant.KEYWORD)
    private String grade;

    public EsGoodsGrade(){}

    public Integer getPrdId() {
        return prdId;
    }

    public void setPrdId(Integer prdId) {
        this.prdId = prdId;
    }

    public BigDecimal getGradePrice() {
        return gradePrice;
    }

    public void setGradePrice(BigDecimal gradePrice) {
        this.gradePrice = gradePrice;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
