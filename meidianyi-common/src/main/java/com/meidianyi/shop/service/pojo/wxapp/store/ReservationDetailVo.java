package com.meidianyi.shop.service.pojo.wxapp.store;

import com.meidianyi.shop.service.pojo.shop.store.comment.ServiceCommentVo;
import com.meidianyi.shop.service.pojo.shop.store.service.StoreServiceParam;
import com.meidianyi.shop.service.pojo.shop.store.store.StorePojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * The type Reservation detail vo.
 *
 * @author liufei
 * @date 11 /5/19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDetailVo {
    private StoreServiceParam serviceInfo;
    private StorePojo storeInfo;
    private List<ReservationInfo> reservationInfoList;
    private ServiceCommentVo commentInfo;
    /**
     * 是否强制用户绑定手机号
     */
    private Byte isBindMobile;
    /**
     * 门店职称配置
     */
    private String technicianTitle;
}
