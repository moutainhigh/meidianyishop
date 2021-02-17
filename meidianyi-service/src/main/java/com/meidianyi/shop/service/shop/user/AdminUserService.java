package com.meidianyi.shop.service.shop.user;

import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import org.springframework.stereotype.Service;

import static com.meidianyi.shop.db.shop.Tables.DOCTOR;

/**
 * @author 赵晓东
 * @description
 * @create 2020-07-28 15:33
 **/
@Service
public class AdminUserService extends ShopBaseService {

    /**
     * 得到当前医师
     * @param userId 用户id
     * @return String
     */
    public Integer getDoctorId(Integer userId){
        return db().select(DOCTOR.ID).from(DOCTOR).where(DOCTOR.USER_ID.eq(userId)).fetchOneInto(Integer.class);
    }
}
