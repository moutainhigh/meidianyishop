package com.meidianyi.shop.service.pojo.shop.video;

import java.sql.Timestamp;

import lombok.Data;

/**
 * @author lixinguo
 */
@Data
public class UploadedVideoVo {
	private Integer videoId;
	private String videoType;
	private Integer videoSize;
	private String videoName;
	private String videoUrl;
	private String videoPath;
	private Integer videoCatId;
	private Integer videoWidth;
	private Integer videoHeight;
	private Integer videoDuration;
	private Timestamp createTime;
	private String videoSnapPath;
	private String snapshotUrl;
	private String formatDuration;
	private Byte delFlag;
}
