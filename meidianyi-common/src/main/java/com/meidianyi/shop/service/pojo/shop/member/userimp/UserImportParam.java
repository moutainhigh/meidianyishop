package com.meidianyi.shop.service.pojo.shop.member.userimp;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

/**
 * 文件上传
 * 
 * @author zhaojianqiang
 * @time 下午4:34:06
 */
@Data
public class UserImportParam {
	private MultipartFile file;
	private String cardId;
	private Integer tagId;
	private Integer groupId;
}
