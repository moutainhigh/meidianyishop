package com.meidianyi.shop.service.pojo.shop.auth;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author zhaojianqiang
 *
 */
@Data
@NoArgsConstructor
public class MenuParam {
	@JsonProperty("first_web_manage")
	private List<String> firstWebManage;
	
	@JsonProperty("first_web_decoration")
	private List<String> firstWebDecoration;
	
	@JsonProperty("goods_manage")
	private List<String> goodsManage;
	
	@JsonProperty("first_trade_manage")
	private List<String> firstTradeManage;
	
	@JsonProperty("first_market_manage")
	private List<String> firstMarketManage;
	
	@JsonProperty("user_manger")
	private List<String> userManger;
	
	@JsonProperty("store_manage")
	private List<String> storeManage;
	
	@JsonProperty("base_manger")
	private List<String> baseManger;

    @JsonProperty("prescription_manage")
    private List<String> prescriptionManage;

    @JsonProperty("doctor_manage")
    private List<String> doctorManage;
	
	
	private List<MenuInnerParam> plus;

	@JsonIgnore
	public List<MenuInnerParam> getPlus() {
		return plus;
	}

	@JsonProperty
	public void setPlus(List<MenuInnerParam> plus) {
		this.plus = plus;
	}

}
