package com.meidianyi.shop.service.pojo.wxapp.store;

import com.meidianyi.shop.service.pojo.shop.store.technician.TechnicianInfo;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

/**
 * The type Reservation time.
 *
 * @author liufei
 * @date 11 /6/19
 */
@Data
@Builder
public class ReservationTime {
    private LocalTime startTime;
    private LocalTime endTime;
    private Byte technicianFlag;
    private List<TechnicianInfo> technicianPojoList;
}
