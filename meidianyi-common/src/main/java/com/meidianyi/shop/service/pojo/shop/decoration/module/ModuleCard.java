package com.meidianyi.shop.service.pojo.shop.decoration.module;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 
 * @author lixinguo
 *
 */
@Getter
@Setter
public class ModuleCard extends ModuleBase {

    @JsonProperty("card_id")
    private Integer cardId=0;

    @JsonProperty("hidden_card")
    private Byte hiddenCard=0;

    @JsonProperty("card_name")
    private String cardName="";

    @JsonProperty("card_state")
    private Byte cardState=1;

    @JsonProperty("card_grade")
    private String cardGrade="v1";

    @JsonProperty("receive_day")
    private String receiveDay="";

    @JsonProperty("card_type")
    private Byte cardType=0;

    @JsonProperty("legal")
    private String legal="";

    @JsonProperty("exchang_count_legal")
    private String exchangCountLega="";

    @JsonProperty("bg_type")
    private Byte bgType=0;

    @JsonProperty("bg_color")
    private String bgColor="";

    @JsonProperty("bg_img")
    private String bgImg="";

    @JsonProperty("is_pay")
    private Byte isPay=2;

    @JsonProperty("pay_type")
    private Byte payType=0;

    @JsonProperty("pay_fee")
    private BigDecimal payFee=BigDecimal.ZERO;

}
