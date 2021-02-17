package com.meidianyi.shop.service.pojo.wxapp.store;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author: 王兵兵
 * @create: 2020-02-11 15:39
 **/
@Getter
@Setter
public class AllCommentVo {

    private List<Comment> comment;

    /** 评价的数量，4条数据，顺序是全部、好评、中评、差评 */
    private int[] numbers;

    /** 好评、中评、差评 的比率 */
    private double[] ratio;

    @Setter
    @Getter
    public static class Comment{

        private Byte commstar;
        private Byte anonymousflag;
        private String commNote;
        private String commImg;
        private List<String> commImgList;
        private String username;
        private String userAvatar;
        private Timestamp createTime;
    }
}
