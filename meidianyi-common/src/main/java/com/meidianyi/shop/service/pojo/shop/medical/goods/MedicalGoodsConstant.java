package com.meidianyi.shop.service.pojo.shop.medical.goods;

/**
 * 药品、规格使用常量名值定义处
 * @author 李晓冰
 * @date 2020年07月05日
 */
public class MedicalGoodsConstant {
    /**
     * 默认规格标识
     */
    public static final Byte DEFAULT_SKU = 1;

    /**
     * 是否是药品
     */
    public static final Byte GOODS_IS_MEDICAL = 1;
    /**
     * 颜色:红色;尺寸:xl
     * 2:1!!3:1
     * 规格名值分割符
     */
    public static final String SPEC_NAME_VAL_SPLITER = ":";

    /**
     * 规格id分隔符
     */
    public static final String PRD_SPEC_SPLITER = "!!";

    /**
     * 规格描述分隔符
     */
    public static final String PRD_DESC_SPLITER =";";

    /**
     * 售罄
     */
    public static final Byte SALE_OUT_YES = 1;

    /**
     * 未售罄
     */
    public static final Byte SALE_OUT_NO = 0;

    /**上架*/
    public static final Byte ON_SALE = 1;
    /**下架*/
    public static final Byte OFF_SALE = 0;

    /**药品默认数量*/
    public static final Integer MEDICAL_GOODS_DEFAULT_NUM = 1000;

    /**药品默认图片*/
    public static final String MEDICAL_GOODS_WXAPP_DEFAULT_IMG = "image/wxapp/medicalGodsDefault.jpg";

    /**商品是药品*/
    public static final Byte MEDICAL_GOODS = 1;

    /**从his拉取的数据*/
    public static final Byte SOURCE_FROM_HIS = 1;
    /**从门店拉取的数据*/
    public static final Byte SOURCE_FROM_STORE = 2;
    /**药房商品拉取后，对应唯一码值前缀*/
    public static final String STORE_GOODS_CODE_PREFIX= "STORE_";

    public static Byte DESC = 1;

    /**拉取对方数据的开始时间*/
    public static final String PULL_START_TIME = "2018-01-01 00:00:00";
    /**
     * 是否处方药 0不是 1是
     */
    public static final Byte IS_NO_RX=0;
    public static final Byte IS_RX=1;

    /**his独有药品*/
    public static final Byte ONLY_IN_HIS=1;
    /**store独有药品*/
    public static final Byte ONLY_IN_STORE=2;
    /**双方都有药品*/
    public static final Byte IN_BOTH=3;

    /**未配对*/
    public static final Byte NOT_MATCHED = 0;
    /**成功配对*/
    public static final Byte ALREADY_MATCHED = 1;
    /**配对失败*/
    public static final Byte FAIL_MATCHED = 2;


    /**从external_form_his 获取分页信息*/
    public static final Byte PAGE_LIST_FROM_HIS = 1;
}
