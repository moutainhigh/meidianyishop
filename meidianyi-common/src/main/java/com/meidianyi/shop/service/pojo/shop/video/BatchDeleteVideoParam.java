package com.meidianyi.shop.service.pojo.shop.video;

import lombok.Data;

import java.util.List;

/**
 * @author 孔德成
 * @date 2019/7/9 14:30
 */
@Data
public class BatchDeleteVideoParam {

    List<Integer> videoIds;

}
