package com.meidianyi.shop.service.shop.task.goods;

import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import static com.meidianyi.shop.db.shop.tables.FootprintRecord.FOOTPRINT_RECORD;

/**
 * 删除三个月前的足迹
 * @author: 王兵兵
 * @create: 2019-12-04 17:21
 **/
@Service
public class FootprintDeleteTaskService extends ShopBaseService {

    /**
     * 足迹保存的时长（自然月）
     */
    private static final int RECORD_SAVED_MONTHS = 3;

    public void deleteFootprint(){
        db().delete(FOOTPRINT_RECORD).where(FOOTPRINT_RECORD.UPDATE_TIME.lt(monthsAgo())).execute();
    }

    private Timestamp monthsAgo(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -RECORD_SAVED_MONTHS);
        return new Timestamp(calendar.getTime().getTime());
    }
}
