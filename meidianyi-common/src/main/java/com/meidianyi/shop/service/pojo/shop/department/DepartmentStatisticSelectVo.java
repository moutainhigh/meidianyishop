package com.meidianyi.shop.service.pojo.shop.department;

import lombok.Builder;
import lombok.Data;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.SelectHavingStep;

import java.math.BigDecimal;

/**
 * @author chenjie
 * @date 2020年09月09日
 */
@Data
@Builder
public class DepartmentStatisticSelectVo {
    private SelectHavingStep<Record2<Integer, BigDecimal>> consultationTable;
    private SelectHavingStep<Record2<Integer, Integer>> doctorNumberTable;
    private SelectHavingStep<Record3<Integer, Integer, BigDecimal>> prescriptionTable;
    private SelectHavingStep<Record3<Integer, Integer, BigDecimal>> inquiryTable;
}
