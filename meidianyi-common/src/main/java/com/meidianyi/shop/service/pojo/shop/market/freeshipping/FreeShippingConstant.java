package com.meidianyi.shop.service.pojo.shop.market.freeshipping;

/**
 * 满包邮常量
 * @author 孔德成
 * @date 2020/2/20
 */
public class FreeShippingConstant {
    /**  ***************0:固定日期 1：永久有效 */
    public static final Byte FREE_SHIPPING_EXPIRE_FIXED =0;
    public static final Byte FREE_SHIPPING_EXPIRE_NEVER =1;
    /**  ******************条件 1全部 0部分 */
    public static final Byte FREE_SHIPPING_GOODS_ALL =0;
    public static final Byte FREE_SHIPPING_GOODS_PART =1;
    /**  *******************包邮条件  包邮条件 0满金额 1满件数 2满金额活件数 */
    public static final Byte FREE_SHIPPING_CONDITION_ACCOUNT =0;
    public static final Byte FREE_SHIPPING_CONDITION_NUM =1;
    public static final Byte FREE_SHIPPING_CONDITION_ACCOUNT_OR_NUM =2;

}
