/**
  * Copyright 2019 bejson.com 
  */
package com.meidianyi.shop.service.pojo.shop.recommend.order;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Auto-generated: 2019-11-12 10:52:53
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class InvoiceInfo {

    private Byte type;
    private String title;
    
    @JsonProperty(value = "tax_number")
    private String taxNumber;
    
    @JsonProperty(value = "company_address")
    private String companyAddress;
    
    private String telephone;
    
    @JsonProperty(value = "bank_name")
    private String bankName;
    
    @JsonProperty(value = "bank_account")
    private String bankAccount;
    
    @JsonProperty(value = "invoice_detail_page")
    private InvoiceDetailPage invoiceDetailPage;

}