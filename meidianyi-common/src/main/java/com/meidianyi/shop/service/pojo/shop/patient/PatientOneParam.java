package com.meidianyi.shop.service.pojo.shop.patient;

import com.meidianyi.shop.common.pojo.shop.table.UserDo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author chenjie
 */
@Data
public class  PatientOneParam {
    private Integer   id;
    private String    name;
    private String    mobile;
    private String    identityCode;
    private Byte      identityType;
    private String    treatmentCode;
    private String    insuranceCardCode;
    private Byte      sex;
    private Date birthday;
    private Integer age;
    private Byte isFetch;
    private String    remarks;
    private String    patientCode;
    private Byte      isDefault;
    private String    diseaseHistory;
    private String    allergyHistory;
    private String    familyDiseaseHistory;
    private Byte      gestationType;
    private Byte      kidneyFunctionOk;
    private Byte      liverFunctionOk;
    private List<PatientMoreInfoParam> diseaseHistoryList;
    private List<PatientMoreInfoParam> familyDiseaseHistoryList;
    private String diseaseHistoryStr;
    private String familyDiseaseHistoryStr;
    private String diseaseHistoryNameStr;
    private Integer patientId;
    private Integer userId;
    /**
     * 用户ID
     */
    private List<Integer> userIdNew;
    /**
     * 用户昵称
     */
    private List<String> wxNickName;

    private List<UserParam> userParamList;
    /**
     * 注册时间
     */
    private Timestamp createTime;
    /**
     * 处方数量2
     */
    private Integer countPrescription;

    @Data
    public static class UserParam{
        /**
         * 用户ID
         */
        private Integer userIdNew;
        /**
         * 用户昵称
         */
        private String wxNickName;

        public UserParam(Integer userIdNew, String wxNickName) {
            this.userIdNew = userIdNew;
            this.wxNickName = wxNickName;
        }
    }
}
