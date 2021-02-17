package com.meidianyi.shop.service.pojo.shop.decoration.module;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *
 * @author chenjie
 *
 */
@Getter
@Setter
public class ModuleCaseHistory extends ModuleBase {
    private List<?> caseHistoryListData;
    private Boolean hasMore = false;
}
