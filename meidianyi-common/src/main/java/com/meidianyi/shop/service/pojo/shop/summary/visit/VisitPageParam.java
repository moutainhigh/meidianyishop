package com.meidianyi.shop.service.pojo.shop.summary.visit;

import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.db.shop.tables.records.MpVisitPageRecord;

import lombok.Data;
import org.jooq.Field;
import org.jooq.SortField;
import org.jooq.TableField;
import org.jooq.impl.DSL;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static com.meidianyi.shop.db.shop.tables.MpVisitPage.MP_VISIT_PAGE;

/**
 * 访问页面统计入参
 *
 * @author 郑保乐
 */
@Data
public class VisitPageParam {

    public static final int ASC = 1;
    public static final int DESC = 2;

    /** 日期类型 7:最近7天 30:最近30天 0:自定义 */
    private Integer type = 7;
    private String startDate;
    private String endDate;

    /**
     * 按第几个字段排序
     */
    @NotNull(message = JsonResultMessage.MSG_PARAM_ERROR)
    @Min(1)
    @Max(7)
    private Integer action;

    /**
     * 排序方式（1: ASC 2: DESC）
     */
    @NotNull(message = JsonResultMessage.MSG_PARAM_ERROR)
    @Min(1)
    @Max(2)
    private Integer asc;

    /**
     * action 和字段的对应关系
     */
    public enum Actions {

        /** action **/
        PV(MP_VISIT_PAGE.PAGE_VISIT_PV),
        UV(MP_VISIT_PAGE.PAGE_VISIT_UV),
        STAY_TIME(MP_VISIT_PAGE.PAGE_STAYTIME_PV),
        ENTRY_PV(MP_VISIT_PAGE.ENTRYPAGE_PV),
        EXIT_PV(MP_VISIT_PAGE.EXITPAGE_PV),
        SHARE_PV(MP_VISIT_PAGE.PAGE_SHARE_PV),
        SHARE_UV(MP_VISIT_PAGE.PAGE_SHARE_UV);

        private TableField<MpVisitPageRecord, ? extends Number> field;

        Actions(TableField<MpVisitPageRecord, ? extends Number> field) {
            this.field = field;
        }

        public TableField<MpVisitPageRecord, ? extends Number> getField() {
            return field;
        }
    }

    /**
     * 取字段及排序方式
     */
    public SortField<?> getSortField() {
        Field<? extends Number> field = getField();
        switch (getAsc()) {
            case ASC:
                return DSL.sum(field).asc();
            case DESC:
                return DSL.sum(field).desc();
            default:
                throw new IllegalStateException("Unexpected asc: " + getAsc());
        }
    }

    public Field<? extends Number> getField() {
        Integer action = getAction();
        Actions[] fields = Actions.values();
        int length = fields.length;
        if (0 > action || length < action) {
            throw new IllegalStateException("Unexpected action: " + action);
        }
        return fields[action - 1].getField();
    }
}
