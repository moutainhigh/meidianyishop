package com.meidianyi.shop.service.pojo.wxapp.goods.groupdraw;

import com.fasterxml.jackson.annotation.JsonProperty;

import jodd.util.StringUtil;
import lombok.Data;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * 小程序拼团抽奖列表入参
 * 
 * @author zhaojianqiang
 * @time 下午2:18:35
 */
@Data
public class GroupDrawParam {
	@JsonProperty(value = "group_draw_id")
	private Integer groupDrawId;

    /**
     * 小程序扫码进详情页带的参数
     */
    private String scene;

    public void initScene(){
        boolean canResolve = (this.groupDrawId == null || this.groupDrawId <= 0) && StringUtil.isNotBlank(this.scene);
        if(canResolve){
            String scene = null;
            try {
                scene = URLDecoder.decode(this.scene,"UTF-8");
            } catch (UnsupportedEncodingException e) {
            }
            if(StringUtil.isNotEmpty(scene)){
                String[] sceneParam = scene.split("=",2);
                this.groupDrawId =  Integer.valueOf(sceneParam[1]);
            }
        }
    }

}
