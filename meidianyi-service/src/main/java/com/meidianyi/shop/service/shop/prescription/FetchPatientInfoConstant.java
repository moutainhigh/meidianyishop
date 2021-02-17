package com.meidianyi.shop.service.shop.prescription;

/**
 * @author 赵晓东
 * @description
 * @create 2020-08-13 18:35
 **/

public class FetchPatientInfoConstant {

    public static final Integer GET_HIS_PATIENT_INFO = 0;

    public static final Integer GET_HIS_MEDICAL_ADVICE = 1;

    public static final Integer GET_HIS_MEDICAL_HISTORY = 2;

    public static final Integer GET_HIS_PRESCRIPTION = 3;

    public static final Integer FETCH_HIS_CHECK_CODE_ERROR = 1;

    public static final Integer FETCH_HIS_NO_PATIENT = 2;

    public static final Integer FETCH_HIS_SUCCESS = 0;
    /**
     * 如果没有拉取过，跳转至拉取页面
     */
    public static final Integer START_TO_COMMIT = 3;

    public static final Byte ALREADY_FETCH = 1;

    public static final Byte NO_FETCH = 0;



}
