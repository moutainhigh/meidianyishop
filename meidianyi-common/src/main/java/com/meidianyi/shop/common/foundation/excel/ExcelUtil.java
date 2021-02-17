package com.meidianyi.shop.common.foundation.excel;


import com.meidianyi.shop.common.foundation.util.DateUtils;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * @author 李晓冰
 * @date 2019年07月17日
 */
public class ExcelUtil {

    private static final String STR_FLAG = "\"";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_EXCEL = "m/d/yy";


    public static String getCellStringValue(Cell cell, Workbook workbook) {
        //excel中公式执行器
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        return calculateCellStringValue(cell, evaluator);
    }

    public static String getCellStringValue(Cell cell) {
        return calculateCellStringValue(cell, null);
    }

    private static String calculateCellStringValue(Cell cell, FormulaEvaluator evaluator) {
        if (cell == null) {
            return null;
        }
        String cellValue = null;

        switch (cell.getCellTypeEnum()) {
            case STRING:
                cellValue = cell.getStringCellValue();
                break;
            case NUMERIC:
                //excel中以double类型来表示日期类型
                //判断是日期类型,对于没有中文的日期yyyy/MM/dd使用DateUtil.isCellDateFormatted判断
                //对于带中文yyyy年MM月dd日需要判断其getCellStyle().getDataFormat()
                //但是目前代码内未进行筛选，所以只能解析不带中文的日期，而且未单独处理时间类型

                //不带中文的日期
                if (DateUtil.isCellDateFormatted(cell)) {
                	String dataFormatString = cell.getCellStyle().getDataFormatString();
                	DateFormat formater=null;
                	if(dataFormatString.equals(DATE_FORMAT_EXCEL)) {
                		formater = new SimpleDateFormat(DateUtils.DATE_FORMAT_SIMPLE);
                	}else {
                		formater = new SimpleDateFormat(DATE_FORMAT);
                	}
                    Date date = cell.getDateCellValue();
                    cellValue = formater.format(date);
                } else {
                	//解决手机号会变成1.509303703E10这样的问题,目前不影响其他值
                	DecimalFormat df = new DecimalFormat("#");
                	cellValue = df.format(cell.getNumericCellValue());
                    //cellValue = String.valueOf(cell.getNumericCellValue());
                    if (isDecimalPointOfManyZero(cellValue)) {
                        cellValue = cellValue.split("\\.")[0];
                    }
                }
                break;
            case BOOLEAN:
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case FORMULA:
                if (evaluator != null) {
                    cellValue = evaluator.evaluate(cell).formatAsString();
                    //如何计算出来的是字符串则开头结尾会有\"
                    if (cellValue.startsWith(STR_FLAG)) {
                        cellValue = cellValue.substring(1);
                    }

                    if (cellValue.endsWith(STR_FLAG)) {
                        cellValue = cellValue.substring(0, cellValue.length() - 1);
                    }
                } else {
                    cellValue = cell.getCellFormula();
                }
                break;
            case BLANK:
            case _NONE:
            case ERROR:
            default:
                break;
        }

        cellValue = cellValue == null ? null : cellValue.trim();

        return cellValue;
    }

    public static void setFieldValue(Field field, Object it, String value) throws IllegalAccessException {

        Type genericType = field.getGenericType();

        field.setAccessible(true);

        if (Byte.class.equals(genericType) || byte.class.equals(genericType)) {
            field.set(it, Byte.valueOf(value));
        } else if (Short.class.equals(genericType) || short.class.equals(genericType)) {
            field.set(it, Short.valueOf(value));
        } else if (Integer.class.equals(genericType) || int.class.equals(genericType)) {
            field.set(it, Integer.valueOf(value));
        } else if (Long.class.equals(genericType) || long.class.equals(genericType)) {
            field.set(it, Long.valueOf(value));
        } else if (Float.class.equals(genericType) || float.class.equals(genericType)) {
            field.set(it, Float.valueOf(value));
        } else if (Double.class.equals(genericType) || double.class.equals(genericType)) {
            field.set(it, Double.valueOf(value));
        } else if (Character.class.equals(genericType) || char.class.equals(genericType)) {
            field.set(it, value.charAt(0));
        } else if (Boolean.class.equals(genericType) || boolean.class.equals(genericType)) {
            field.set(it, Boolean.valueOf(value));
        } else if (BigDecimal.class.equals(genericType)) {
            field.set(it, new BigDecimal(value));
        } else if (BigInteger.class.equals(genericType)) {
            field.set(it, new BigInteger(value));
        } else if (Timestamp.class.equals(genericType)) {
            field.set(it, Timestamp.valueOf(value));
        } else if (String.class.equals(genericType)) {
            field.set(it, value);
        }

    }

    public static Object getFieldValue(String fieldName, Object it) throws NoSuchFieldException, IllegalAccessException {
        Class<?> clazz = it.getClass();
        Field declaredField = clazz.getDeclaredField(fieldName);

        declaredField.setAccessible(true);

        Object value=declaredField.get(it);

        return value;
    }

    public static Object getFieldValue(String keyName, Object it, Field dynamicField) throws Exception {
        Object o = dynamicField.get(it);
        if (!(o instanceof Map)) {
            throw new  Exception("动态字段类型错误，仅支持Map类型");
        }
        Map map = (Map) o;
        return map.get(keyName);
    }
    /**
     * 根据java类型获得对应的cell类型
     * @param clazz
     * @return
     */
    public static CellType convertJavaType2CellType(Class<?> clazz) {

        CellType cellType = null;

        if (Byte.class.equals(clazz) || byte.class.equals(clazz)) {
            cellType = CellType.NUMERIC;
        } else if (Short.class.equals(clazz) || short.class.equals(clazz)) {
            cellType = CellType.NUMERIC;
        } else if (Integer.class.equals(clazz) || int.class.equals(clazz)) {
            cellType = CellType.NUMERIC;
        } else if (Long.class.equals(clazz) || long.class.equals(clazz)) {
            cellType = CellType.NUMERIC;
        } else if (Float.class.equals(clazz) || float.class.equals(clazz)) {
            cellType = CellType.NUMERIC;
        } else if (Double.class.equals(clazz) || double.class.equals(clazz)) {
            cellType = CellType.NUMERIC;
        } else if (Character.class.equals(clazz) || char.class.equals(clazz)) {
            cellType = CellType.STRING;
        } else if (Boolean.class.equals(clazz) || boolean.class.equals(clazz)) {
            cellType = CellType.BOOLEAN;
        } else if (BigDecimal.class.equals(clazz)) {
            cellType = CellType.NUMERIC;
        } else if (BigInteger.class.equals(clazz)) {
            cellType = CellType.NUMERIC;
        } else if (Timestamp.class.equals(clazz)) {
            cellType = CellType.NUMERIC;
        } else {
            cellType = CellType.STRING;
        }
        return cellType;
    }

    public static CellStyle getCellStyle(Class<?> clazz,Workbook workbook){
        CellStyle cellStyle = null;

        if (Byte.class.equals(clazz) || byte.class.equals(clazz)) {
            cellStyle = ExcelStyleFactory.createCommonNumberCellStyle(workbook);
        } else if (Short.class.equals(clazz) || short.class.equals(clazz)) {
            cellStyle = ExcelStyleFactory.createCommonNumberCellStyle(workbook);
        } else if (Integer.class.equals(clazz) || int.class.equals(clazz)) {
            cellStyle = ExcelStyleFactory.createCommonNumberCellStyle(workbook);
        } else if (Long.class.equals(clazz) || long.class.equals(clazz)) {
            cellStyle = ExcelStyleFactory.createCommonNumberCellStyle(workbook);
        } else if (Float.class.equals(clazz) || float.class.equals(clazz)) {
            cellStyle = ExcelStyleFactory.createCommonNumberCellStyle(workbook);
        } else if (Double.class.equals(clazz) || double.class.equals(clazz)) {
            cellStyle = ExcelStyleFactory.createCommonNumberCellStyle(workbook);
        } else if (Character.class.equals(clazz) || char.class.equals(clazz)) {
            cellStyle = ExcelStyleFactory.createCommonTextCellStyle(workbook);
        } else if (Boolean.class.equals(clazz) || boolean.class.equals(clazz)) {
            cellStyle = ExcelStyleFactory.createCommonTextCellStyle(workbook);
        } else if (BigDecimal.class.equals(clazz)) {
            cellStyle = ExcelStyleFactory.createCommonNumberCellStyle(workbook);
        } else if (BigInteger.class.equals(clazz)) {
            cellStyle = ExcelStyleFactory.createCommonNumberCellStyle(workbook);
        } else if (Timestamp.class.equals(clazz)) {
            cellStyle = ExcelStyleFactory.createCommonDateCellStyle(workbook);
        } else {
            cellStyle = ExcelStyleFactory.createCommonTextCellStyle(workbook);
        }
        return cellStyle;
    }

    public static void setCellValue(Cell cell,Object value){
        if (value == null) {
            return;
        }
        Class<?> clazz=value.getClass();

        if (Byte.class.equals(clazz) || byte.class.equals(clazz)) {
            cell.setCellValue((Byte)value);
        } else if (Short.class.equals(clazz) || short.class.equals(clazz)) {
            cell.setCellValue((Short)value);
        } else if (Integer.class.equals(clazz) || int.class.equals(clazz)) {
            cell.setCellValue((Integer)value);
        } else if (Long.class.equals(clazz) || long.class.equals(clazz)) {
            cell.setCellValue((Long)value);
        } else if (Float.class.equals(clazz) || float.class.equals(clazz)) {
            cell.setCellValue((Float)value);
        } else if (Double.class.equals(clazz) || double.class.equals(clazz)) {
            cell.setCellValue((Double) value);
        } else if (Character.class.equals(clazz) || char.class.equals(clazz)) {
            cell.setCellValue(String.valueOf(value));
        } else if (Boolean.class.equals(clazz) || boolean.class.equals(clazz)) {
            cell.setCellValue((Boolean) value);
        } else if (BigDecimal.class.equals(clazz)) {
            cell.setCellValue(((BigDecimal)value).doubleValue());
        } else if (BigInteger.class.equals(clazz)) {
            cell.setCellValue(((BigInteger)value).doubleValue());
        } else if (Timestamp.class.equals(clazz)) {
            Timestamp tt= (Timestamp) value;
            cell.setCellValue(new Date(tt.getTime()));
        } else if (String.class.equals(clazz)) {
            cell.setCellValue((String)value);
        }
    }


    /**
     * 判断123. .00 213.000的类似格式
     *
     * @param str
     * @return
     */
    public static boolean isDecimalPointOfManyZero(String str) {
        if (str == null||"".equals(str)) {
            return false;
        }

        Pattern pattern = compile("^[-\\+]?[\\d]*\\.[0]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 返回文件类型校验
     * @param multipartFile
     * @return
     */
	public static ExcelTypeEnum checkFile(MultipartFile multipartFile) {
		if (multipartFile == null) {
			return null;
		}
		ExcelTypeEnum type = null;
		try {
			InputStream inputStream = multipartFile.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(inputStream);
			FileMagic fileMagic = FileMagic.valueOf(bis);
			if (Objects.equals(fileMagic, FileMagic.OLE2)) {
				type = ExcelTypeEnum.XLS;
			}
			if (Objects.equals(fileMagic, FileMagic.OOXML)) {
				type = ExcelTypeEnum.XLSX;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return type;
	}
}
