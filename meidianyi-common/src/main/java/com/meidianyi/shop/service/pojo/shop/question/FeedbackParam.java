package com.meidianyi.shop.service.pojo.shop.question;

import java.util.List;

/**
 * @author luguangyao
 */
public class FeedbackParam {


    private Byte type;

    private String content;

    private String mobile;

    private List<String> imgs;

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public List<String> getImgs() {
        return imgs;
    }

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
    }
}
