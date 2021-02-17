package com.meidianyi.shop.service.shop.anchor;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.pojo.shop.table.AnchorPointsDo;
import com.meidianyi.shop.dao.shop.anchor.AnchorPointsDao;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.anchor.AnchorPointsChartReportVo;
import com.meidianyi.shop.service.pojo.shop.anchor.AnchorPointsListParam;
import com.meidianyi.shop.service.pojo.shop.anchor.AnchorPointsListVo;
import com.meidianyi.shop.service.pojo.shop.anchor.AnchorPointsParam;
import com.meidianyi.shop.service.pojo.shop.anchor.AnchorPointsPerReportVo;
import com.meidianyi.shop.service.pojo.shop.anchor.AnchorPointsReportVo;
import com.meidianyi.shop.service.pojo.shop.anchor.AnchorPotionEventBo;
import org.elasticsearch.common.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 锚点
 * @author 孔德成
 * @date 2020/8/28 16:43
 */
@Component
public class AnchorPointsService extends ShopBaseService {

    @Autowired
    private AnchorPointsDao anchorPointsDao;

    public void add(AnchorPointsParam param){
        param.getList().forEach(item->{
            AnchorPointsEvent event = AnchorPointsEvent.getInstance(param.getEvent(), item.getKey());
            if(event!=null){
                AnchorPointsDo anchorPointsDo = event.getDo();
                anchorPointsDo.setDevice(param.getDevice());
                anchorPointsDo.setValue(item.getValue());
                anchorPointsDo.setKey(item.getKey());
                anchorPointsDo.setPage(param.getPage());
                anchorPointsDo.setPlatform(param.getPlatform());
                anchorPointsDo.setStoreId(param.getStoreId());
                anchorPointsDo.setUserId(param.getUserId());
                anchorPointsDao.save(anchorPointsDo);
            }else {
                AnchorPointsDo anchorPointsDo =new AnchorPointsDo();
                anchorPointsDo.setEvent(param.getEvent());
                anchorPointsDo.setStoreId(param.getStoreId());
                anchorPointsDo.setKey(item.getKey());
                anchorPointsDo.setDevice(param.getDevice());
                anchorPointsDo.setValue(item.getValue());
                anchorPointsDo.setPage(param.getPage());
                anchorPointsDo.setPlatform(param.getPlatform());
                anchorPointsDo.setStoreId(param.getStoreId());
                anchorPointsDo.setUserId(param.getUserId());
                anchorPointsDao.save(anchorPointsDo);
            }
        });
    }


    public PageResult<AnchorPointsListVo> list(AnchorPointsListParam param) {
       return anchorPointsDao.list(param);
    }

    public List<AnchorPotionEventBo> eventKeyMap() {
        return AnchorPointsEvent.eventKeyMap();
    }

    /**
     * 报表
     * @param param
     * @return
     */
    public AnchorPointsChartReportVo countReport(AnchorPointsListParam param) {
        Map<Date, List<AnchorPointsReportVo>> countMap = getDateListMap(param);
        //时间轴
        List<String> datalist1 =new ArrayList<>();
        AnchorPointsChartReportVo option =new AnchorPointsChartReportVo();
        Set<String> valueSet =new HashSet<>();
        AnchorPointsChartReportVo.SeriesData seriesData;
        List<AnchorPointsChartReportVo.SeriesData> seriesDataList =new ArrayList<>();
        Map<String,AnchorPointsChartReportVo.SeriesData> map =new HashMap<>();
        // 初始化 legend-data
        countMap.forEach((k,v)->{
            v.forEach(item->{
                if (!Strings.isEmpty(item.getValue())){
                    valueSet.add(item.getValue());
                }else if (AnchorPointsEvent.LOGIN_WXAPP.getKey().equals(item.getKey())){
                    valueSet.add(item.getKey());
                }
            });
        });
        // 初始化 series
        for (String value : valueSet) {
            seriesData = new AnchorPointsChartReportVo.SeriesData();
            seriesData.setName(value);
            seriesData.setStack(param.getKey());
            seriesDataList.add(seriesData);
        }
        // series放入data数据
        for (AnchorPointsChartReportVo.SeriesData data : seriesDataList) {
            Timestamp startDate = param.getStartTime();
            Timestamp endDate = param.getEndTime();
            String count ="0";
            datalist1 =new ArrayList<>();
            while (endDate.compareTo(startDate) >= 0) {
                List<AnchorPointsReportVo> list = countMap.get(DateUtil.date(startDate).toSqlDate());
                count ="0";
                if (list!=null){
                    for (AnchorPointsReportVo report : list) {
                        if (data.getName().equals(report.getValue())) {
                            if (AnchorPointsEvent.CREATE_ORDER_SUBMIT_MONEY.getKey().equals(param.getKey())){
                                count = report.getMoney().toString();
                            }else {
                                count = report.getCount()+"";
                            }

                        }
                    }
                }
                data.getDataMap().put(DateUtil.formatDate(startDate),count);
                datalist1.add(DateUtil.formatDate(startDate));
                startDate = DateUtil.offset(startDate, DateField.DAY_OF_YEAR, 1).toTimestamp();
            }
            if (Strings.isEmpty(data.getName())){
                data.setName(param.getKey());
                valueSet.add(param.getKey());
            }
            data.setDataList(new ArrayList<>(data.getDataMap().values()));
        }
        option.setXAxisData(datalist1);
        option.setSeriesList(seriesDataList);
        option.setLegendData(new ArrayList<>(valueSet));
        return option;
    }

    private Map<Date, List<AnchorPointsReportVo>> getDateListMap(AnchorPointsListParam param) {
        Map<Date, List<AnchorPointsReportVo>> countMap;
        if (AnchorPointsEvent.CREATE_ORDER_SUBMIT_MONEY.getKey().equals(param.getKey())){
            //金额 计算方式累计sum 设备分类
            countMap = anchorPointsDao.moneyDateDeviceReport(param);
            countMap.forEach((k,v)-> {
                v.forEach(item -> {
                    item.setValue(item.getDevice());
                });
            });
        }else if (AnchorPointsEvent.LOGIN_WXAPP.getKey().equals(param.getKey())){
            // 点击事件 count 设备分类
            countMap = anchorPointsDao.countDateDeviceReport(param);
            countMap.forEach((k,v)-> {
                v.forEach(item -> {
                    item.setValue(item.getDevice());
                });
            });
        }else {
            // key value分类
            countMap = anchorPointsDao.countDateReport(param);
        }
        return countMap;
    }


    public  AnchorPointsPerReportVo moneyReport(AnchorPointsListParam param){
        List<AnchorPointsReportVo> countReport = anchorPointsDao.countReport(param);
        List<AnchorPointsReportVo> deviceReport = anchorPointsDao.moneyDeviceReport(param);
        AnchorPointsPerReportVo vo =new AnchorPointsPerReportVo();
        vo.setDeviceReport(deviceReport);
        vo.setPrescriptionReport(countReport);
        return vo;
    }


    public String getDoctorAttendanceRate(AnchorPointsListParam param) {
        return null;
    }
}
