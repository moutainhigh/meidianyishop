package com.meidianyi.shop.service.pojo.shop.medical.sku.entity;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 李晓冰
 * @date 2020年07月02日
 */
@Data
public class SpecEntity {
    private Integer  specId;
    private Integer goodsId;
    private String specName;

    /**
     * 规格值
     */
    List<SpecValEntity> goodsSpecVals;

    /**
     * 规格名集合转换为对应的Map
     * @param specEntities 代转换规格名集合
     * @return Map,key是名字，value是实体类
     */
    public static Map<String, SpecEntity> mapNameToSpec(List<SpecEntity> specEntities){
        Map<String, SpecEntity> collect = specEntities.stream().filter(specEntity -> StrUtil.isNotBlank(specEntity.getSpecName()))
            .collect(Collectors.toMap(SpecEntity::getSpecName, Function.identity(), (x1, x2) -> x1));

        return collect;
    }
}
