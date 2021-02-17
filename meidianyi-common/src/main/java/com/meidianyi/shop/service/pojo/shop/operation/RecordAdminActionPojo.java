package com.meidianyi.shop.service.pojo.shop.operation;


import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 操作记录的POJO
 * @author: 卢光耀
 * @date: 2019-07-11 15:01
 *
*/
public class RecordAdminActionPojo implements Serializable {

    private static final long serialVersionUID = 7201260344021566810L;


    private Integer  id;
    private Integer   sysId;
    private Integer   accountId;
    private Byte      actionType;
    private String    templateId;
    private Timestamp createTime;
    private Timestamp updateTime;
    private String    templateData;
    private String    mobile;
    private String    userName;
    private Byte      accountType;
    /**
     * 操作模块(装修、商品、订单、会员、营销)
     */
    public  enum ActionType{
        /**
         * 装修
         */
        DECORATE(1,"装修"),
        /**
         * 商品
         */
        GOODS(2,"商品"),
        /**
         * 订单
         */
        ORDER(3,"订单"),
        /**
         * 会员
         */
        MEMBER(4,"会员"),
        /**
         * 营销
         */
        MARKET(5,"营销");

        public int getCode() {
            return code;
        }
        public String getName() {
            return name;
        }
        private int code;
        private String name;
        ActionType(int code,String name){
            this.code = code;
            this.name = name;
        }
        public static String getNameByCode(int code){
            for (ActionType t: ActionType.values()) {
                if(t.getCode()==code){
                    return t.getName();
                }
            }
            return null;
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSysId() {
        return sysId;
    }

    public void setSysId(Integer sysId) {
        this.sysId = sysId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Byte getActionType() {
        return actionType;
    }

    public void setActionType(Byte actionType) {
        this.actionType = actionType;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public String getTemplateData() {
        return templateData;
    }

    public void setTemplateData(String templateData) {
        this.templateData = templateData;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    

    public Byte getAccountType() {
		return accountType;
	}

	public void setAccountType(Byte accountType) {
		this.accountType = accountType;
	}

	@Override
	public String toString() {
		return "RecordAdminActionPojo [id=" + id + ", sysId=" + sysId + ", accountId=" + accountId + ", actionType="
				+ actionType + ", templateId=" + templateId + ", createTime=" + createTime + ", updateTime="
				+ updateTime + ", templateData=" + templateData + ", mobile=" + mobile + ", userName=" + userName
				+ ", accountType=" + accountType + "]";
	}

	
}
