package com.meidianyi.shop.service.shop.config;

import com.meidianyi.shop.db.main.tables.records.MpAuthShopRecord;
import com.meidianyi.shop.service.foundation.jedis.data.DBOperating;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributionDocumentParam;
import com.meidianyi.shop.service.pojo.shop.image.ShareQrCodeVo;
import com.meidianyi.shop.service.pojo.shop.operation.RecordContentTemplate;
import com.meidianyi.shop.service.pojo.shop.qrcode.QrCodeTypeEnum;
import com.meidianyi.shop.service.shop.image.QrCodeService;
import com.meidianyi.shop.service.shop.operation.RecordAdminActionService;
import com.meidianyi.shop.service.shop.payment.MpPaymentService;
import org.jooq.Record1;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static com.meidianyi.shop.db.shop.Tables.*;

import com.meidianyi.shop.service.pojo.shop.config.distribution.DistributionParam;

import java.util.ArrayList;
import java.util.List;

/**
 * 分销配置
 * @author 常乐
 * 2019年7月17日
 */
@Service

public class DistributionConfigService extends BaseShopConfigService {
    /**
     * 分销开关
     */
    final public static String K_FANLI = "fanli";
    final public static Byte ENABLE_STATUS = 1;

    final public static String INVITE_DOCUMENT = "invite_document";

    final public static Byte BYTE_ZERO =  0;
    final public static Byte BYTE_ONE =  1;

    @Autowired
    private QrCodeService qrCode;

    @Autowired
    public MpPaymentService ms;

    @Autowired
    public RecordAdminActionService record;

    /**
     * 获取返利配置
     *
     * @return
     */
    public DistributionParam getDistributionCfg() {
        DistributionParam jsonObject = this.getJsonObject(K_FANLI, DistributionParam.class);
        if(jsonObject == null){
            return null;
        }else{
            //是否有分销员
            Integer hasDistributor = hasDistributor();
            if(hasDistributor > 0){
                jsonObject.setHasDistributor(1);
            }else{
                jsonObject.setHasDistributor(0);
            }
            return jsonObject;
        }
    }

    /**
     * 设置返利配置
     *
     * @param config
     * @return
     */
    public int setDistributionCfg(DistributionParam config) {
        return this.setJsonObject(K_FANLI, config);
    }

    /**
     * 获取推广文案分享二维码
     */
    public ShareQrCodeVo getShareQrCode() {

//        String pathParam = Null;
        String imageUrl = qrCode.getMpQrCode(QrCodeTypeEnum.REBATE_POPULARIZE_DOCUMENT);

        ShareQrCodeVo vo = new ShareQrCodeVo();
        vo.setImageUrl(imageUrl);
        vo.setPagePath(QrCodeTypeEnum.REBATE_POPULARIZE_DOCUMENT.getPathUrl(null));
        return vo;
    }

    /**
     * 获取分销推广文案
     *
     * @return
     */
    public DistributionDocumentParam getDistributionDocument() {
        return this.getJsonObject(INVITE_DOCUMENT, DistributionDocumentParam.class);
    }

    /**
     * 设置分销推广文案
     *
     * @param param
     * @return
     */
    public int setDistributionDocument(DistributionDocumentParam param) {
        return this.setJsonObject(INVITE_DOCUMENT, param);
    }

    /**
     * 店铺已有分销员数量
     * @return
     */
    public Integer hasDistributor() {
        Integer hasDistributor = db().selectCount().from(USER).where(USER.IS_DISTRIBUTOR.eq((byte) 1)).fetchOne().into(Integer.class);
        return hasDistributor;
    }

    /**
     * 是否为自商户
     * @return
     */
    public Byte getMpPay(){
        MpAuthShopRecord mp = ms.getMpAuthShop();
        Byte isSubMerchant = mp.getIsSubMerchant();  //1为子商户
        return isSubMerchant;
    }

    /**
     * 添加分销变更记录
     * @param param
     */
    private void addDistributionRecord(DistributionParam param) {
        DistributionParam oldParam = this.getJsonObject(K_FANLI, DistributionParam.class);
        List<Integer> templateIds = new ArrayList<>();  List<String> templateData = new ArrayList<>();
        templateIds.add(RecordContentTemplate.DISTRIBUTION_CHANGE.code);
        if (!oldParam.getStatus().equals(param.getStatus())) {
            templateIds.add(param.getStatus().equals(BYTE_ONE) ?
                RecordContentTemplate.DISTRIBUTION_STATUS_ON.code : RecordContentTemplate.DISTRIBUTION_STATUS_OFF.code);
        }
        if (!oldParam.getJudgeStatus().equals(param.getJudgeStatus())) {
            templateIds.add(param.getJudgeStatus().equals(BYTE_ONE) ?
                RecordContentTemplate.DISTRIBUTION_JUDGE_STATUS_ON.code : RecordContentTemplate.DISTRIBUTION_JUDGE_STATUS_OFF.code);
        }
        if (!oldParam.getWithdrawStatus().equals(param.getWithdrawStatus())) {
            templateIds.add(param.getWithdrawStatus().equals(BYTE_ONE) ?
                RecordContentTemplate.DISTRIBUTION_WITHDRAW_STATUS_ON.code : RecordContentTemplate.DISTRIBUTION_WITHDRAW_STATUS_OFF.code);
        }
        // 有效期变更
        if (oldParam.getVaild() > 0 && param.getVaild() == 0) {
            templateIds.add(RecordContentTemplate.DISTRIBUTION_VALID_1.code);
        } else if (oldParam.getVaild() == 0 && param.getVaild() > 0) {
            templateIds.add(RecordContentTemplate.DISTRIBUTION_VALID_2.code);
            templateData.add(param.getVaild().toString());
        } else if (!oldParam.getVaild().equals(param.getVaild())) {
            templateIds.add(RecordContentTemplate.DISTRIBUTION_VALID_3.code);
            templateData.add(oldParam.getVaild().toString());
            templateData.add(param.getVaild().toString());
        }
        // 保护期变更
        if (oldParam.getProtectDate() > -1 && param.getProtectDate() == -1) {
            templateIds.add(RecordContentTemplate.DISTRIBUTION_PROTECT_DATA_1.code);
        } else if (oldParam.getProtectDate() == -1 && param.getProtectDate() > -1) {
            templateIds.add(RecordContentTemplate.DISTRIBUTION_PROTECT_DATA_2.code);
            templateData.add(param.getProtectDate().toString());
        } else if (!oldParam.getProtectDate().equals(param.getProtectDate())) {
            templateIds.add(RecordContentTemplate.DISTRIBUTION_PROTECT_DATA_3.code);
            templateData.add(oldParam.getProtectDate().toString());
            templateData.add(param.getProtectDate().toString());
        }
        if (templateIds.size() > BYTE_ONE) {
            record.insertRecord(templateIds, templateData.toArray(new String[]{}));
        }
    }
}