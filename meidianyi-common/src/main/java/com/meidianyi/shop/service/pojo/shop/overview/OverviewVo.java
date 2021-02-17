package com.meidianyi.shop.service.pojo.shop.overview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author liufei
 * date 2019/7/18
 * 商城概览综合出参
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OverviewVo {
    private ShopBaseInfoVo shopBaseInfoVo;
    private List<FixedAnnouncementVo> announcementVoList;
    private DataDemonstrationVo dataDemonstrationVo;
    private ToDoItemVo toDoItemVo;
    private ShopAssistantVo shopAssistantVo;
}
