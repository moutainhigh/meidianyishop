package com.meidianyi.shop.service.foundation.video;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.upyun.AsyncProcessHandler;
import com.upyun.Result;
import com.upyun.UpException;
import com.upyun.UpYunUtils;
import com.meidianyi.shop.common.foundation.util.Util;

import lombok.Data;

/**
 * 
 * @author lixinguo
 *
 */
public class UpyunSynVideo extends AsyncProcessHandler {

	final String SYN_HOST = "p1.api.upyun.com";

	protected String uri;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public UpyunSynVideo(String bucketName, String userName, String password) {
		super(bucketName, userName, password);
	}

	/**
	 * 获取音视频的元信息
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 * @throws UpException
	 */
	public AvMeta avMeta(String path) throws IOException, UpException {
		Map<String, Object> params = new HashMap<>();
		params.put("source", path);
		this.setUri(String.format("/%s/avmeta/get_meta", this.bucketName));
		Result result = this.process(params);
		AvMeta avMeta = null;
		if (result.isSucceed()) {
			avMeta = Util.parseJson(result.getMsg(), AvMeta.class);
		}
		return avMeta;
	}

	/**
	 * 获取音视频的元信息
	 * 
	 * @param videoPath 视频的存储地址
	 * @param saveAs 截图保存地址
	 * @param point 截图时间点，格式为 HH:MM:SS
	 * @return
	 * @throws IOException
	 * @throws UpException
	 */
	public SnapShotResult snapshot(String videoPath, String saveAs, String point) throws IOException, UpException {
		Map<String, Object> params = new HashMap<>();
		params.put("source", videoPath);
		params.put("save_as", saveAs);
		params.put("point", point);
		this.setUri(String.format("/%s/snapshot", this.bucketName));
		Result result = this.process(params);
		SnapShotResult snapshotResult = null;
		if (result.isSucceed()) {
			snapshotResult = Util.parseJson(result.getMsg(), SnapShotResult.class);
		}
		return snapshotResult;
	}

	/**
	 * 得到流信息
	 * 
	 * @param avMeta
	 * @param type   video audio
	 * @return
	 */
	public static Stream getStream(AvMeta avMeta, String type) {
		if (avMeta != null) {
			for (Stream s : avMeta.streams) {
				if (type.equals(s.getType())) {
					return s;
				}
			}
		}
		return null;
	}

	@Override
	protected Result process(Map<String, Object> params) throws IOException, UpException {

		OutputStream os;
		HttpURLConnection conn;

		URL url = new URL("http://"+SYN_HOST + this.uri);

		conn = (HttpURLConnection) url.openConnection();

		String date = getGMTDate();

		// 设置必要参数
		conn.setConnectTimeout(timeout);
		conn.setRequestMethod("POST");
		conn.setUseCaches(false);
		conn.setDoOutput(true);
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.setRequestProperty("User-Agent", UpYunUtils.VERSION);
		conn.setRequestProperty("Content-Type", "application/json");

		// 设置时间
		conn.setRequestProperty(DATE, date);
		// 设置签名
		conn.setRequestProperty(AUTHORIZATION,
				sign("POST", this.uri, date, params));

		// 创建链接
		conn.connect();
		os = conn.getOutputStream();
		String json = Util.toJson(params);
		os.write(json.getBytes("UTF-8"));

		// 获取返回的信息
		Result result = getResp(conn);

		if (os != null) {
			os.close();
		}
		if (conn != null) {
			conn.disconnect();
		}
		return result;
	}

	@Data
	public static class AvMeta {
		List<Stream> streams = new ArrayList<>();
		Format format;
	};

	@Data
	public static class MetaData {
		@JsonProperty("handler_name")
		String handlerName;
		
		@JsonProperty("creation_time")
		String creationTime;
		String language;
		String encoder;
	};

	@Data
	public static class Stream {
		Integer index;
		String type;

		@JsonProperty("video_fps")
		Integer videoFps;

		@JsonProperty("video_height")
		Integer videoHeight;

		@JsonProperty("video_width")
		Integer videoWidth;

		@JsonProperty("audio_channels")
		Integer audioChannels;

		@JsonProperty("audio_samplerate")
		Integer audioSamplerate;

		@JsonProperty("codec_desc")
		String codecDesc;

		String codec;
		BigInteger bitrate;
		Double duration;
		String language;
		
		@JsonProperty("metadata")
		MetaData medaData;
	};

	@Data
	public static class Format {
		Double duration;
		String fullname;
		BigInteger bitrate;
		Integer filesize;
		String format;
	};

	@Data
	public static class SnapShotResult {
		@JsonProperty("status_code")
		Integer statusCode;

		String message;

		@JsonProperty("content_type")
		String contentType;

		@JsonProperty("content_length")
		Integer contentLength;

		@JsonProperty("save_as")
		String saveAs;
	}
}
