package com.meidianyi.shop.controller.excel;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumn;
import com.meidianyi.shop.common.foundation.excel.annotation.ExcelSheet;

/**
 * 每一个Model对象对应一个excel中的sheet
 * @author 李晓冰
 * @date 2019年07月29日
 */

/**
 * sheetNum：指定对应sheet在excle中的索引{@link ExcelSheet#sheetNum()} 默认为0
 * headLineNum：指定sheet头所在的行（0开始） {@link ExcelSheet#headLineNum()} 默认为0
 * beginDataNum: 指定数据所在的开始行号（0开始） {@link ExcelSheet#beginDataNum()} 默认为1
 */
@ExcelSheet
@Data
public class PersonModel {

    /**
     * {@link ExcelColumn} 字段在sheet对应的列信息，如果不写则视为使用ExcelColumn默认值，
     *                      忽略对应的字段{@link com.meidianyi.shop.common.foundation.excel.annotation.ExcelIgnore}
     *                      设置字段不可为空{@link com.meidianyi.shop.common.foundation.excel.annotation.ExcelColumnNotNull}
     *
     * columnName: 指定sheet列名在资源文件中的对应码，如果不写则使用字段名作为对应码，
     *             如果对应码不存在，则列名为对应码{@link ExcelColumn#columnName()},对应的
     *             资源文件在static/i18n/“excel”下
     * columnIndex: 指定字段对应的列在sheet中出现的顺序{@link ExcelColumn#columnIndex()},
     *              如果不写在则该列会根据最大columnIndex值依次展示
     * args: 指定模板数据{@link }
     *
     *
     */
    @ExcelColumn(columnName = "excel.person.name",columnIndex = 0)
    private String personName;

    @ExcelColumn(columnName = "excel.person.birth",columnIndex = 4)
    private Timestamp birth;

    @ExcelColumn(columnName = "excel.person.age",columnIndex = 1)
    private int personAge;

    @ExcelColumn(columnName = "excel.person.address",columnIndex = 2)
    private String personAddress;

    @ExcelColumn(columnName = "excel.person.salary",columnIndex = 3)
    private BigDecimal personSalary;

}
