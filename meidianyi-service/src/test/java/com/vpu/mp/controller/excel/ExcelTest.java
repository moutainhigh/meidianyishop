package com.meidianyi.shop.controller.excel;

import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelReader;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.excel.ExcelWriter;
import com.meidianyi.shop.common.foundation.excel.bean.ClassList;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author 李晓冰
 * @date 2019年07月29日
 */
public class ExcelTest {

    private HttpServletResponse response;

    private HttpServletRequest request;

    private MultipartFile multipartFile;

    private static final String HEADER_LANG = "V-Lang";

    private static final String WRITER_PATH = "/excel/excelWriter.xlsx";
    private static final String READER_PATH = "/excel/excelReader.xlsx";


    @Before
    public void setUp() throws IOException {
        response = mock(HttpServletResponse.class);
        request = mock(HttpServletRequest.class);
        multipartFile = mock(MultipartFile.class);

        URL resource = ExcelTest.class.getResource(WRITER_PATH);


        when(request.getHeader(HEADER_LANG)).thenReturn("zh_CN");
        System.out.println(request.getHeader(HEADER_LANG));
        when(request.getParameter("fileType")).thenReturn("a.xlsx");
        InputStream in = new FileInputStream(new File(ExcelTest.class.getResource(READER_PATH).getPath()));
        when(multipartFile.getInputStream()).thenReturn(in);

        ServletOutputStream outputStream = new ServletOutputStream() {

            FileOutputStream fileOutputStream = new FileOutputStream(new File(resource.getPath()));

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setWriteListener(WriteListener listener) {

            }

            @Override
            public void write(int b) throws IOException {
                fileOutputStream.write(b);
            }
        };

        when(response.getOutputStream()).thenReturn(outputStream);
    }


    @After
    public void testAfter() {
        System.out.println("生成的测试文件在 /mp-service/target/test-classes/excel中");
    }

    /**
     * 导出excel模板
     *
     * @throws IOException
     */
    @Test
    public void excelExportTemplate() throws IOException {
        String lang = request.getHeader(HEADER_LANG);

        Workbook workbook = ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(lang, workbook);

        excelWriter.createExcelTemplate(PersonModel.class);

        ServletOutputStream outputStream = response.getOutputStream();

        workbook.write(outputStream);

        outputStream.close();
    }

    /**
     * 导出excel模板和数据
     * （导出的excel文件最终是在target文件夹下）
     *
     * @throws IOException
     */
    @Test
    public void excelExportData() throws IOException {
        List<PersonModel> modelData = getModelData();

        String lang = request.getHeader(HEADER_LANG);

        Workbook workbook = ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(lang, workbook);

        excelWriter.writeModelList(modelData, PersonModel.class);

        ServletOutputStream outputStream = response.getOutputStream();

        workbook.write(outputStream);

        outputStream.close();
    }

    private List<PersonModel> getModelData() {
        List<PersonModel> models = new ArrayList<>();
        PersonModel personModel = new PersonModel();
        personModel.setPersonName("张四");
        personModel.setPersonAge(22);
        personModel.setPersonAddress("address");
        personModel.setPersonSalary(BigDecimal.valueOf(125.5));
        personModel.setBirth(Timestamp.valueOf(LocalDateTime.now()));
        models.add(personModel);

        return models;
    }


    @Test
    public void excelExportDynamicData() throws IOException {
        List<DynamicFieldModel> modelData = getDynamicFieldModel();

        String lang = request.getHeader(HEADER_LANG);

        Workbook workbook = ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(lang, workbook);

        excelWriter.writeModelListWithDynamicColumn(modelData,DynamicFieldModel.class);

        ServletOutputStream outputStream = response.getOutputStream();

        workbook.write(outputStream);

        outputStream.close();
    }

    private List<DynamicFieldModel> getDynamicFieldModel() {
        List<DynamicFieldModel> dynamicFieldModels = new ArrayList<>();
        DynamicFieldModel model1 = new DynamicFieldModel();
        model1.setGoodsName("商品1");
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("2012-num", 10);
        map.put("2012-price", BigDecimal.valueOf(12.55));
        model1.setDynamicValue(map);

        DynamicFieldModel model2 = new DynamicFieldModel();
        model2.setGoodsName("商品1");
        Map<String, Object> map2 = new LinkedHashMap<>();
        map2.put("2012-num", 22);
        map2.put("2012-price", BigDecimal.valueOf(122.55));
        model2.setDynamicValue(map2);

        dynamicFieldModels.add(model1);
        dynamicFieldModels.add(model2);
        return dynamicFieldModels;
    }

    /**
     * 读取上传文件
     *
     * @throws IOException
     */
    @Test
    public void excelReadData() throws IOException {

        String lang = request.getHeader("HEADER_LANG");

        ExcelTypeEnum type;
        if (request.getParameter("fileType").indexOf(ExcelTypeEnum.XLSX.getSuffix()) > 0) {
            type = ExcelTypeEnum.XLSX;
        } else {
            type = ExcelTypeEnum.XLS;
        }

        InputStream inputStream = multipartFile.getInputStream();

        Workbook workbook = ExcelFactory.createWorkbook(inputStream, type);

        /**
         * excel解析错误处理器
         */
        MyExcelWrongHandler handler = new MyExcelWrongHandler();

        ExcelReader excelReader = new ExcelReader(lang, workbook, handler);

        List<PersonModel> models = excelReader.readModelList(PersonModel.class);

        System.out.println(models);
    }

    /**
     * 合并单元格的测试
     *
     * @throws IOException
     */
    @Test
    public void excelExportDataByMergedRegion() throws Exception {
        List<OrderListInfo> modelData = getList();

        String lang = request.getHeader(HEADER_LANG);

        Workbook workbook = ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(lang, workbook);
        ClassList cList = new ClassList();
        cList.setUpClazz(OrderListInfo.class);
        cList.setInnerClazz(OrderGoods.class);
        excelWriter.writeModelListByRegion(modelData, cList);
        //  excelWriter.writeModelListByRegion(modelData,OrderListInfo.class,OrderGoods.class);

        ServletOutputStream outputStream = response.getOutputStream();

        workbook.write(outputStream);

        outputStream.close();

    }

    private List<OrderListInfo> getList() {
        List<OrderListInfo> list = new ArrayList<OrderListInfo>();

        OrderListInfo vo = getVo("P201901041135261953");
        OrderListInfo vo1 = getVo2("P201901041135261222");
        OrderListInfo vo2 = getVo3("P201901041135261333");
        list.add(vo);
        list.add(vo1);
        list.add(vo2);
        return list;

    }

    public OrderListInfo getVo(String srt) {
        OrderListInfo vo = new OrderListInfo();
        List<OrderGoods> gooList = new ArrayList<OrderGoods>();
        OrderGoods goods = new OrderGoods();
        goods.setGoodsName("东方骆驼2018秋季长袖棒球领拉链开衫男士外套");
        goods.setGoodsPrice(new BigDecimal(128));

        OrderGoods goods1 = new OrderGoods();
        goods1.setGoodsName("2018秋冬新款 真皮草领 保暖鹅绒 奢华厚款滩羊毛领鹅绒女士羽绒服11");
        goods1.setGoodsPrice(new BigDecimal(368));

        OrderGoods goods12 = new OrderGoods();
        goods12.setGoodsName("奥术大师大所大所大所多");
        goods12.setGoodsPrice(new BigDecimal(222));

        gooList.add(goods);
        gooList.add(goods1);
        gooList.add(goods12);
        vo.setGoods(gooList);

        vo.setOrderSn(srt);
        vo.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
        vo.setUserName("义博云天");
        vo.setConsignee("王义博;18236936252");
        vo.setOrderStatus("待发货");
        vo.setMoneyPaid("￥500.00(含快递:￥0.00)");
        return vo;
    }

    public OrderListInfo getVo2(String srt) {
        OrderListInfo vo = new OrderListInfo();
        List<OrderGoods> gooList = new ArrayList<OrderGoods>();
        OrderGoods goods = new OrderGoods();
        goods.setGoodsName("蒙牛牛奶 激情测试");
        goods.setGoodsPrice(new BigDecimal(222));

        OrderGoods goods1 = new OrderGoods();
        goods1.setGoodsName("激情测试 伊利牛奶 ");
        goods1.setGoodsPrice(new BigDecimal(333));

        OrderGoods goods12 = new OrderGoods();
        goods12.setGoodsName("激情 三元牛奶 测试 ");
        goods12.setGoodsPrice(new BigDecimal(1111));

        gooList.add(goods);

        gooList.add(goods1);
        gooList.add(goods12);

        vo.setGoods(gooList);

        vo.setOrderSn(srt);
        vo.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
        vo.setUserName("测试人员");
        vo.setConsignee("测发;1821111111");
        vo.setOrderStatus("已经发货");
        vo.setMoneyPaid("￥100.00(含快递:￥2.00)");
        return vo;
    }

    public OrderListInfo getVo3(String srt) {
        OrderListInfo vo = new OrderListInfo();
        List<OrderGoods> gooList = new ArrayList<OrderGoods>();
        OrderGoods goods = new OrderGoods();
        goods.setGoodsName("蒙牛牛奶 激情测试");
        goods.setGoodsPrice(new BigDecimal(222));

        gooList.add(goods);

        vo.setGoods(gooList);

        vo.setOrderSn(srt);
        vo.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
        vo.setUserName("测试人员2");
        vo.setConsignee("测发;12222221");
        vo.setOrderStatus("已经发货");
        vo.setMoneyPaid("￥1030.00(含快递:￥2.00)");
        return vo;
    }

}
