package com.meidianyi.shop.service.pojo.shop.market.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

/**
 * 推送人群查询条件
 * @author 卢光耀
 * @date 2019-08-09 11:20
 *
*/
@Data
public class UserInfoQuery {
    /** redis存储的唯一标识 */
    private String userKey;
    /** 勾选加购人群 */
    private Boolean onClickNoPay = Boolean.FALSE;
    /** 勾选购买指定商品人群 */
    private Boolean onClickGoods = Boolean.FALSE;
    /** 商品ID集合 */
    private List<Integer> goodsIdList;
    /** 勾选指定会员卡人群 */
    private Boolean onClickCard = Boolean.FALSE;
    /** 会员卡ID集合 */
    private List<Integer> cardIdsList;
    /** 勾选指定标签人群 */
    private Boolean onClickTag = Boolean.FALSE;
    /** 标签ID集合 */
    private List<Integer> tagIdList;
    /** 勾选指定会员 */
    private Boolean onClickUser = Boolean.FALSE;
    /** 用户ID集合 */
    private List<Integer> userIdList;
    /** 勾选自定人群 */
    private Boolean onClickCustomRule = Boolean.FALSE;
    /** 自定义实体 */
    private CustomRuleInfo customRuleInfo;
}
