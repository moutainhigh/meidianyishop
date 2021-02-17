package com.meidianyi.shop.service.pojo.shop.config.pledge;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author luguangyao
 */
@Data
@NoArgsConstructor
public class PledgeVo {
    private Byte state;
    private List<PledgeInfo> list;
}
