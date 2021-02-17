package com.meidianyi.shop.service.pojo.wxapp.score;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.shop.member.score.ScorePageListVo;

import lombok.Data;

/**
 * 
 * @author zhaojianqiang
 *
 * 2019年10月16日 下午3:37:39
 */
@Data
public class UserScoreListVo {
	private PageResult<ScorePageListVo> list;
	private ExpireVo expire;

}
