package com.meidianyi.shop.generate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.ship.batch.BatchShipListVo;

import java.lang.reflect.Field;

public class GenerateWiki {
    public static void main(String[] args) {
        Class<?> cls = BatchShipListVo.class;
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(JsonIgnore.class)) {
                continue;
            }
            System.out.print("||");
            System.out.print(field.getName());
            System.out.print("||");
            System.out.print(field.getType().getSimpleName());
            System.out.print("|| ");
            System.out.print(" ||");
            System.out.println();
        }
    }
}
