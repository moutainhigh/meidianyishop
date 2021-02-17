package com.meidianyi.shop.service.foundation.es.pojo.goods.label;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.meidianyi.shop.service.pojo.shop.goods.label.GoodsLabelCouple;
import com.meidianyi.shop.service.pojo.shop.goods.label.GoodsLabelCoupleTypeEnum;

import java.util.List;
import java.util.Map;

/**
 * 商品标签类型转换
 * @author 卢光耀
 * @date 2019/11/25 2:04 下午
 *
*/
public class GoodsLabelTypeInfo {

    /**List<LabelId>*/
    private List<Integer> allGoods = Lists.newArrayList();
    /**Map<LabelId,categoryId>*/
    private Map<Integer,  List<Integer>> categoryGoods = Maps.newIdentityHashMap();
    /**Map<LabelId,sortId>*/
    private Map<Integer,  List<Integer>> sortGoods = Maps.newIdentityHashMap();
    /**Map<LabelId,goodsId>*/
    private Map<Integer,  List<Integer>> idGoods = Maps.newIdentityHashMap();

    private List<Integer> labelIds = Lists.newArrayList();

    public List<Integer> getAllGoods() {
        return allGoods;
    }

    public Map<Integer,  List<Integer>> getCategoryGoods() {
        return categoryGoods;
    }

    public Map<Integer,  List<Integer>> getSortGoods() {
        return sortGoods;
    }

    public Map<Integer,  List<Integer>> getIdGoods() {
        return idGoods;
    }

    private GoodsLabelTypeInfo(){}

    public static GoodsLabelTypeInfo convert(List<GoodsLabelCouple> goodsLabelCouples){
        GoodsLabelTypeInfo infoMap = new GoodsLabelTypeInfo();
        for( GoodsLabelCouple labelCouple:goodsLabelCouples ){
            Integer labelId = labelCouple.getLabelId();
            if( GoodsLabelCoupleTypeEnum.ALLTYPE.getCode().equals(labelCouple.getType()) ){
                infoMap.getAllGoods().add(labelId);
            }else if (GoodsLabelCoupleTypeEnum.SORTTYPE.getCode().equals(labelCouple.getType())){
                infoMap.assembly(infoMap.getSortGoods(),labelCouple);
            }else if (GoodsLabelCoupleTypeEnum.CATTYPE.getCode().equals(labelCouple.getType())){
                infoMap.assembly(infoMap.getCategoryGoods(),labelCouple);
            }else {
                infoMap.assembly(infoMap.getIdGoods(),labelCouple);
            }
            if( !infoMap.labelIds.contains(labelId) ){
                infoMap.labelIds.add(labelId);
            }
        }
        return infoMap;
    }
    private void assembly(Map<Integer,List<Integer>> map,GoodsLabelCouple labelCouple){
        Integer key = labelCouple.getGtaId();
        if( map.containsKey( key ) ){
            map.get(key).add(labelCouple.getLabelId());
        }else{
            List<Integer> list = Lists.newArrayList();
            list.add(labelCouple.getLabelId());
            map.put(key,list);
        }
    }
    public List<Integer> getAllLabelId(){
       return this.labelIds;
    }
    public static void main(String[] args) {
        Map<Integer,Integer> map = Maps.newIdentityHashMap();
        List<Integer> id = Lists.newArrayList();
        id.add(1);
    }
}
