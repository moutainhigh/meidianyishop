package com.meidianyi.shop.service.pojo.wxapp.store;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * The type Reservation info.
 *
 * @author liufei
 * @date 11 /5/19
 */
@Data
@Builder
public class ReservationInfo {
    private LocalDate reservationDate;
    private List<ReservationTime> reservationTimeList;
}
