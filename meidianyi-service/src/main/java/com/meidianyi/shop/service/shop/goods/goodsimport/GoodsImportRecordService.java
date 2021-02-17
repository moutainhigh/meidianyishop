package com.meidianyi.shop.service.shop.goods.goodsimport;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.excel.ExcelWriter;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.GoodsImportDetailRecord;
import com.meidianyi.shop.db.shop.tables.records.GoodsImportRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsDataIIllegalEnum;
import com.meidianyi.shop.service.pojo.shop.goods.goodsimport.vpu.GoodsVpuExcelImportBo;
import com.meidianyi.shop.service.pojo.shop.goods.goodsimport.vpu.GoodsVpuExcelImportFailModel;
import com.meidianyi.shop.service.pojo.shop.goods.goodsimport.vpu.GoodsVpuImportListParam;
import com.meidianyi.shop.service.pojo.shop.goods.goodsimport.vpu.GoodsVpuImportListVo;
import com.meidianyi.shop.service.shop.image.ImageService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectSeekStep1;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.meidianyi.shop.db.shop.Tables.GOODS_IMPORT;
import static com.meidianyi.shop.db.shop.Tables.GOODS_IMPORT_DETAIL;

/**
 * 商品导入操作记录
 * @author 李晓冰
 * @date 2020年03月21日
 */
@Service
public class GoodsImportRecordService extends ShopBaseService {

    @Autowired
    ImageService imageService;

    public static final Byte FINISH = 1;
    /**
     * 分页查询导入操作记录
     * @param param
     * @return
     */
    public PageResult<GoodsVpuImportListVo> getGoodsVpuImportList(GoodsVpuImportListParam param) {
        Condition condition = GOODS_IMPORT.DEL_FLAG.eq(DelFlag.NORMAL_VALUE);
        if (param.getBatchId() != null) {
            condition = condition.and(GOODS_IMPORT.ID.eq(param.getBatchId()));
        }
        if (param.getBeginTime() != null) {
            condition = condition.and(GOODS_IMPORT.CREATE_TIME.ge(param.getBeginTime()));
        }
        if (param.getEndTime() != null) {
            condition = condition.and(GOODS_IMPORT.CREATE_TIME.le(param.getEndTime()));
        }

        SelectSeekStep1<Record, Integer> records = db().select(DSL.asterisk()).from(GOODS_IMPORT).where(condition).orderBy(GOODS_IMPORT.ID.desc());
        PageResult<GoodsVpuImportListVo> pageResult = this.getPageResult(records, param.getCurrentPage(), param.getPageRows(), GoodsVpuImportListVo.class);
        // 处理文件地址
        List<GoodsVpuImportListVo> dataList = pageResult.dataList;
        for (GoodsVpuImportListVo goodsVpuImportListVo : dataList) {
            if (StringUtils.isNotBlank(goodsVpuImportListVo.getImportFilePath())) {
                goodsVpuImportListVo.setImportFilePath(imageService.getImgFullUrl(goodsVpuImportListVo.getImportFilePath()));
            }
        }

        return pageResult;
    }

    /**
     * 下载失败数据
     * @param batchId
     * @return
     */
    public Workbook downloadFailData(Integer batchId,String lang) {
        List<GoodsVpuExcelImportFailModel> failList = db().selectFrom(GOODS_IMPORT_DETAIL)
            .where(GOODS_IMPORT_DETAIL.IS_SUCCESS.eq((byte) 0).and(GOODS_IMPORT_DETAIL.BATCH_ID.eq(batchId)))
            .orderBy(GOODS_IMPORT_DETAIL.ID).fetchInto(GoodsVpuExcelImportFailModel.class);

        for (GoodsVpuExcelImportFailModel failModel : failList) {
            GoodsDataIIllegalEnum illegalEnum = GoodsDataIIllegalEnum.getIllegalEnum(failModel.getErrorCode());
            String messages = Util.translateMessage(lang, illegalEnum.getErrorMsg(), null, "messages");
            failModel.setErrorMsg(messages);
        }

        Workbook workbook = ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(lang, workbook);
        excelWriter.writeModelList(failList,GoodsVpuExcelImportFailModel.class);
        return workbook;
    }

    /**
     * 获取导入时间
     * @param batchId
     * @return
     */
    public Timestamp getOperateTime(Integer batchId) {
        Timestamp timestamp = db().select(GOODS_IMPORT.CREATE_TIME).from(GOODS_IMPORT).where(GOODS_IMPORT.ID.eq(batchId)).fetchAny(GOODS_IMPORT.CREATE_TIME);
        return timestamp;
    }

    /**
     * 导入信息操作记录插入
     * @param totalNum 总数
     * @param filePath 文件路径
     * @param isUpdate 是否更新
     * @return 操作id
     */
    public Integer insertGoodsImportInfo(Integer totalNum, String filePath, boolean isUpdate) {
        GoodsImportRecord goodsImportRecord = db().newRecord(GOODS_IMPORT);
        goodsImportRecord.setTotalNum(totalNum);
        goodsImportRecord.setImportFilePath(filePath);
        goodsImportRecord.setIsUpdate((byte) (isUpdate?1:0));
        goodsImportRecord.insert();
        return goodsImportRecord.getId();
    }

    /**
     * 以下两个方法在10上添加，需要依次写到11、12、13上
     * @param batchId
     */
    public void updateImportFinish(Integer batchId){
        db().update(GOODS_IMPORT).set(GOODS_IMPORT.IS_FINISH,FINISH)
            .where(GOODS_IMPORT.ID.eq(batchId)).execute();
    }

    /**
     * 导入是否完成
     * @param batchId
     * @return
     */
    public boolean isFinish(Integer batchId){
        Byte isFinish = db().select(GOODS_IMPORT.IS_FINISH).from(GOODS_IMPORT)
            .where(GOODS_IMPORT.ID.eq(batchId).and(GOODS_IMPORT.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))).fetchAny(GOODS_IMPORT.IS_FINISH);
        return FINISH.equals(isFinish);
    }
    /**
     * 更新导入的成功数
     * @param successNum 成功数量
     * @param batchId 操作id
     */
    public void updateGoodsImportSuccessNum(Integer successNum,Integer batchId) {
        db().update(GOODS_IMPORT).set(GOODS_IMPORT.SUCCESS_NUM,successNum)
            .where(GOODS_IMPORT.ID.eq(batchId)).execute();
    }

    /**
     * 插入数据操作记录
     * @param list
     */
    public void insertGoodsImportDetailBatch(List<GoodsImportDetailRecord> list) {
        db().batchInsert(list).execute();
    }

    /**
     * 转换bo为GoodsImportDetail
     * @param bo GoodsVpuExcelImportBo 后期可以修改为一个通用的基类
     * @param e 错误类型枚举类
     * @param batchId 本次批量导入的batchId
     * @return
     */
    public GoodsImportDetailRecord convertVpuExcelImportBoToImportDetail(GoodsVpuExcelImportBo bo, GoodsDataIIllegalEnum e, Integer batchId) {
        GoodsImportDetailRecord record = new GoodsImportDetailRecord();
        record.setBatchId(batchId);
        record.setGoodsSn(bo.getGoodsSn());
        record.setPrdSn(bo.getPrdSn());
        record.setGoodsName(bo.getGoodsName());
        record.setPrdDesc(bo.getPrdDesc());
        record.setIsSuccess((byte) 0);
        record.setErrorCode(e.getErrorCode());
        return record;
    }

    /**
     * 转换bo集合为GoodsImportDetail
     * @param bos
     * @param e
     * @param batchId
     * @return
     */
    public List<GoodsImportDetailRecord> convertVpuExcelImportBosToImportDetails(List<GoodsVpuExcelImportBo> bos, GoodsDataIIllegalEnum e,Integer batchId) {
        return this.convertVpuExcelImportBosToImportDetails(bos,e,batchId,false);
    }

    /**
     * 转换bo集合为GoodsImportDetail
     * @param bos
     * @param e
     * @param batchId
     * @return
     */
    public List<GoodsImportDetailRecord> convertVpuExcelImportBosToImportDetails(List<GoodsVpuExcelImportBo> bos, GoodsDataIIllegalEnum e,int batchId,boolean isSuccess) {
        List<GoodsImportDetailRecord> records = new ArrayList<>(bos.size());
        for (GoodsVpuExcelImportBo bo : bos) {
            GoodsImportDetailRecord record = new GoodsImportDetailRecord();
            record.setBatchId(batchId);
            record.setGoodsSn(bo.getGoodsSn());
            record.setPrdSn(bo.getPrdSn());
            record.setGoodsName(bo.getGoodsName());
            record.setPrdDesc(bo.getPrdDesc());
            record.setIsSuccess((byte) (isSuccess?1:0));
            record.setErrorCode(e.getErrorCode());
            records.add(record);
        }
        return records;
    }
}
