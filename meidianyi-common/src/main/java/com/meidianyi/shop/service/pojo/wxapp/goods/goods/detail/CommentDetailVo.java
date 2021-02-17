package com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author 李晓冰
 * @date 2019年11月19日
 */
@Data
public class CommentDetailVo {
    /**好评*/
    public static final Byte GOODS_LEVEL = 1;
    /**中评*/
    public static final Byte NORMAL_LEVEL = 2;
    /**差评*/
    public static final Byte BAD_LEVEL = 3;
    /**带图片评价*/
    public static final Byte IMG_LEVEL = 4;

    private CommentInfo commentInfo;
    private List<CommentLevelInfo> commentLevelsInfo;

    @Data
    public static class CommentInfo{
        /**评价id*/
        private Integer id;
        /**是否匿名显示*/
        private Byte anonymousFlag;
        /**用户名*/
        private String userName;
        /**用户头像*/
        private String userAvatar;
        /**评价时间*/
        private Timestamp createTime;
        /**评价内容*/
        private String commNote;
        /**评价星级*/
        private Byte commStar;
        /**评价图片*/
        private List<String> commImgs;
        /**评价回复id*/
        private Integer answerId;
        /**评价回复内容*/
        private String answer;
    }

    @Data
    @AllArgsConstructor
    public static class CommentLevelInfo{
        private Byte type;
        private Long num;
    }
}
