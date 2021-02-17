package com.meidianyi.shop.service.pojo.saas.question.bo;

import java.sql.Timestamp;

/**
 * @author luguangyao
 */
public class FeedbackBo {

    private String submitUser;

    private String submitUserPhone;

    private String mobile;

    private Timestamp createTime;

    private String version;

    private Integer categoryId;

    private String content;

    private Integer questionFeedbackId;

    private Byte isLook;

    public String getSubmitUser() {
        return submitUser;
    }

    public void setSubmitUser(String submitUser) {
        this.submitUser = submitUser;
    }

    public String getSubmitUserPhone() {
        return submitUserPhone;
    }

    public void setSubmitUserPhone(String submitUserPhone) {
        this.submitUserPhone = submitUserPhone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getQuestionFeedbackId() {
        return questionFeedbackId;
    }

    public void setQuestionFeedbackId(Integer questionFeedbackId) {
        this.questionFeedbackId = questionFeedbackId;
    }

    public Byte getIsLook() {
        return isLook;
    }

    public void setIsLook(Byte isLook) {
        this.isLook = isLook;
    }
}
