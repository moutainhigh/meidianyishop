package com.meidianyi.shop.common.pojo.saas.api;

/**
 * @author 李晓冰
 * @date 2020年07月15日
 */
public class ApiExternalRequestConstant {
    /**
     * app_id码值定义
     */
    public static final String APP_ID_ERP = "200000";
    public static final String APP_ID_POS = "200001";
    public static final String APP_ID_CRM = "200002";
    public static final String APP_ID_HIS = "200003";
    public static final String APP_ID_STORE = "200004";

    /**
     * 响应码
     */

    /**成功*/
    public static final Integer ERROR_CODE_SUCCESS = 0;
    /**请求错误*/
    public static final Integer ERROR_CODE = 1;
    /**店铺未配置相应权限*/
    public static final Integer ERROR_CODE_NOT_AUTH = 1001;
    /**解析返回值异常*/
    public static final Integer ERROR_CODE_PARSE_RETVAL = 2001;
    /**网络请求失败*/
    public static final Integer ERROR_CODE_NET_ILLEGAL = 2002;

    /**
     * His 服务方法名称
     */
    /**拉取患者信息*/
    public static final String SERVICE_NAME_FETCH_PATIENT_INFO = "fetchPatientInfo";
    /**拉取药品信息*/
    public static final String SERVICE_NAME_FETCH_MEDICAL_INFOS = "fetchMedicalInfos";
    /**同步订单药品信息*/
    public static final String SERVICE_NAME_SYNC_MEDICAL_ORDER_STATUS = "syncMedicalOrderStatus";
    /**同步处方信息*/
    public static final String SERVICE_NAME_FETCH_PRESCRIPTION_INFOS= "fetchPrescriptionInfos";
    /**同步处方详情*/
    public static final String SERVICE_NAME_FETCH_PRESCRIPTION_DETAIL= "fetchPrescriptionDetail";
    /**上传订单处方信息*/
    public static final String SERVICE_NAME_UPLOAD_ORDER_PRESCRIPTION= "uploadOrderPrescription";
    /**同步医生信息*/
    public static final String SERVICE_NAME_FETCH_DOCTOR_INFOS=  "fetchDoctorInfos";
    /**同步科室信息*/
    public static final String SERVICE_NAME_FETCH_DEPARTMENT_INFOS  = "fetchDepartmentInfos";
    /**同步医师职称信息*/
    public static final String SERVICE_NAME_FETCH_DOCTOR_TITLE_INFOS=  "fetchDoctorTitleInfos";
    /**拉取病历信息**/
    public static final String SERVICE_NAME_FETCH_MEDICAL_HISTORY_INFOS = "fetchMedicalHistoryInfos";
    /**同步医嘱信息**/
    public static final String SERVICE_NAME_FETCH_MEDICAL_ADVICE_INFOS = "fetchMedicalAdviceInfos";

    /**
     * Store 服务方法名
     */
    /**推送订单至store pos系统*/
    public static final String SERVICE_NAME_SYNC_ORDER_POS_INFO = "syncOrderPosInfo";
    /**分页拉取药房商品信息*/
    public static final String SERVICE_NAME_PULL_GOODS_INFOS = "pullGoodsInfos";
    /**批量同步药品信息*/
    public static final String SERVICE_NAME_SYNC_GOODS_INFOS = "syncGoodsInfos";
    /**请求药品库存足够的药店列表*/
    public static final String SERVICE_NAME_GET_STOCK_ENOUGH_SHOP_LIST = "getStockEnoughShopList";
    /**取消药房订单接口*/
    public static final String SERVICE_NAME_CANCEL_ORDER = "cancelOrder";

    /**
     * 请求his接口，server拒绝服务，需要过一会后再请求
     */
    public static final Integer HIS_NET_ERROR_UNEXPECTED_FILE_CODE = 2002;

    public static final String HIS_NET_ERROR_UNEXPECTED_FILE_MSG = "Unexpected end of file from server";

}
