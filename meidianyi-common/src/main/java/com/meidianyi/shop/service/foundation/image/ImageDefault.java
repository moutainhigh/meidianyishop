package com.meidianyi.shop.service.foundation.image;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;

import com.upyun.UpException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.jooq.tools.StringUtils;

import com.UpYun;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.pojo.shop.image.CropImageParam;
import com.meidianyi.shop.service.pojo.shop.image.ImageDim;
import com.meidianyi.shop.service.pojo.shop.image.UploadPath;

import net.coobird.thumbnailator.Thumbnails;

/**
 * 图片公用接口，实现了默认方法
 *
 * @author lixinguo
 *
 */
public interface ImageDefault {

	/**
	 * 删除文件
	 * @param path
	 * @return
	 */
	public default boolean deleteFile(String path) {
		return FileUtils.deleteQuietly(new File(path));
	}

	/**
	 * 图片URL
	 *
	 * @param relativePath
	 * @return
	 */
	public String imageUrl(String relativePath);

	/**
	 * 本地全路径
	 *
	 * @param relativePath
	 * @return
	 */
	public String fullPath(String relativePath);

	/**
	 * 当前店铺Id
	 *
	 * @return
	 */
	public default Integer currentShopId() {
		return 0;
	}

	/**
	 * 当前店铺Id
	 *
	 * @return
	 */
	public default Integer getShopId() {
		return 0;
	}

	/**
	 * 得到相对路径
	 *
	 * @param type
	 * @param sysId
	 * @return
	 */
	public default String getRelativePathDirectory(String type, Integer sysId) {
		Calendar cal = Calendar.getInstance();
		if (sysId != null && !sysId.equals(0)) {
			return String.format("upload/%d/%s/%04d%02d%02d/", sysId, type, cal.get(Calendar.YEAR),
					cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE));
		}
		return String.format("upload/%d/%s/%04d%02d%02d/", this.getShopId(), type, cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE));
	}

	/**
	 * 得到可写上传路径
	 *
	 * @param type
	 * @param filename
	 * @param extension
	 * @param sysId
	 * @return
	 */
	public default UploadPath getWritableUploadPath(String type, String filename, String extension, Integer sysId) {
		UploadPath uploadPath = new UploadPath();
		uploadPath.filname = filename;
		uploadPath.extension = extension;
		uploadPath.relativeDirectory = getRelativePathDirectory(type, sysId);
		uploadPath.relativeFilePath = uploadPath.relativeDirectory + filename + "." + extension;
		uploadPath.type = type;
		uploadPath.fullPath = fullPath(uploadPath.relativeFilePath);
		uploadPath.fullDirectory = fullPath(uploadPath.relativeDirectory);
		uploadPath.setImageUrl(imageUrl(uploadPath.relativeFilePath));
		mkdir(uploadPath.fullDirectory);
		return uploadPath;
	}

	/**
	 * 强制创建目录，支持多目录
	 *
	 * @param fullPath
	 * @return
	 */
	public default boolean mkdir(String fullPath) {
		File dir = new File(fullPath);
		if (!dir.exists()) {
			return dir.mkdirs();
		}
		return true;
	}

	/**
	 * 删除文件
	 *
	 * @param fullPath
	 * @return
	 */
	public default boolean rmFile(String fullPath) {
		File file = new File(fullPath);
		if (file.isFile()&&file.exists()) {
			return file.delete();
		}
		return true;
	}

	/**
	 * 得到又拍云客户端
	 *
	 * @return
	 */
	public UpYun getUpYunClient();

	/**
	 * 上传到又拍云
	 *
	 * @param upYunPath 又拍云路径
	 * @param localFile 本地文件
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public default boolean uploadToUpYun(String upYunPath, File localFile) throws IOException, Exception {
		return this.getUpYunClient().writeFile(upYunPath, localFile);
	}

	/**
	 * 随机文件名
	 *
	 * @return
	 */
	public default String randomFilename() {
		return RandomStringUtils.randomAlphanumeric(20);
	}

	/**
	 * 得到图片扩展名
	 *
	 * @param pathOrUrl
	 * @return
	 */
	public default String getImageExension(String pathOrUrl) {
		String suffix = null;
		try {
			String contentType = URLConnection.getFileNameMap().getContentTypeFor(pathOrUrl);
			Iterator<ImageReader> readers = ImageIO.getImageReadersByMIMEType(contentType);
			while (suffix == null && readers.hasNext()) {
				ImageReaderSpi provider = readers.next().getOriginatingProvider();
				if (provider != null) {
					String[] suffixes = provider.getFileSuffixes();
					if (suffixes != null) {
						suffix = suffixes[0];
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return StringUtils.defaultIfNull(suffix, "");
	}

	/**
	 * 得到图片宽高
	 * @param path
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public default ImageDim getImageDim(String path) throws IOException {
        BufferedImage img =getImageInfo(path);
		return new ImageDim(img.getWidth(), img.getHeight());
	}

	/**
	 * 得到图片buffer对象
	 *
	 * @param path
	 * @return
	 * @throws IOException
	 */
    public default BufferedImage getImageInfo(String path) throws IOException {
        BufferedImage bufferedImage = null;
        try (FileInputStream fileInputStream = new FileInputStream(path)) {
            bufferedImage = ImageIO.read(fileInputStream);
        }
        return bufferedImage;
    }

	/**
	 * 得到图片buffer对象
	 * @param imageUrl
	 * @return
	 * @throws IOException
	 */
	public default BufferedImage getRemoteImageInfo(String imageUrl) throws IOException {
		try (InputStream inputStream =new URL(imageUrl).openStream()) {
			return ImageIO.read(inputStream);
		}

	}
	/**
	 * 下载
	 * @param imageUrl
	 * @return
	 * @throws IOException
	 */
	public default InputStream getStreamInfo(String imageUrl) throws IOException {
		try (InputStream inputStream =new URL(imageUrl).openStream()) {
			return inputStream;
		}
	}
	/**
	 * 校验图片合法性
	 * @param contentType
	 * @return
	 */
	public default boolean validImageType(String contentType) {
		String[] types = { "image/jpg", "image/jpeg", "image/gif", "image/png", "image/bmp" };
		return Arrays.asList(types).contains(contentType);
	}

	/**
	 * 校验图片合法性
	 * @param contentType
	 * @return
	 */
	public default String getImageExtension(String contentType) {
		String[] types = { "image/jpg", "image/jpeg", "image/gif", "image/png", "image/bmp" };
		return Arrays.asList(types).contains(contentType) ? contentType.split("/")[1] : "unkown";
	}

    /**
     * 得到图片可写上传路径
     * @param contentType
     * @return
     */
    public default UploadPath getImageWritableUploadPath(String contentType) {
        return this.getWritableUploadPath("image", randomFilename(), getImageExtension(contentType), null);
    }

	/**
	 * 得到图片可写上传路径
	 * @param contentType
     * @param sysId  用户id
	 * @return
	 */
	public default UploadPath getImageWritableUploadPath(String contentType,Integer sysId) {
		return this.getWritableUploadPath("image", randomFilename(), getImageExtension(contentType), sysId);
	}

	/**
	 * 得到图片ContentType可写上传路径
	 * @param fileUrl
	 * @return
	 */
	public default String getContentType(String fileUrl) {
		return getContentType(new File(fileUrl));
	}

	/**
	 * 得到图片ContentType可写上传路径
	 * @param  file
	 * @return
	 */
	public default String getContentType(File file) {
		String contentType = null;
		try {
			contentType = new MimetypesFileTypeMap().getContentType(file);
		} catch (Exception e) {
		}
		return contentType;
	}

	/**
	 * 得到图片basename
	 * @param  filename
	 * @return
	 */
	public default String baseFilename(String filename) {
		int p = filename.lastIndexOf('.');
		return p == -1 ? filename : filename.substring(0, p);
	}


	/**
	 * 裁剪图片
	 * @param param
	 * @return
	 * @throws IOException
	 * @throws UpException
	 */
	public default UploadPath makeCrop(CropImageParam param) throws IOException, UpException {
		return makeCrop(param,null);
	}
		/**
         * 裁剪图片
         * @param  param
         * @param  sysId
         * @return
         * @throws IOException
         * @throws UpException
         */
	public default UploadPath makeCrop(CropImageParam param,Integer sysId) throws IOException, UpException {
		String fullPath = fullPath(param.remoteImgPath);
		String extension = this.getImageExension(fullPath);
		BufferedImage bufferedImage = getRemoteImageInfo(imageUrl(param.remoteImgPath));
		//原图压缩放大
		if (param.imgScaleW != null) {
			double ratio = bufferedImage.getWidth() / param.imgScaleW;
			param.x = (int) (ratio * param.x);
			param.y = (int) (ratio * param.y);
			param.w = (int) (ratio * param.w);
			param.h = (int) (ratio * param.h);
		}
		//比例改变处理
		if (Util.getInteger(param.cropWidth) > 0 && Util.getInteger(param.cropHeight) <= 0) {
			param.cropHeight = param.h / param.w * param.cropWidth;
		} else if (Util.getInteger(param.cropHeight) > 0 && Util.getInteger(param.cropWidth) <= 0) {
			param.cropWidth = param.w / param.h * param.cropHeight;
		} else if (Util.getInteger(param.cropHeight) <= 0 && Util.getInteger(param.cropWidth) <= 0) {
			param.cropWidth = param.w;
			param.cropHeight = param.h;
		}
		UploadPath uploadPath = getWritableUploadPath("image", randomFilename(), extension,sysId);
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			//剪切
			Thumbnails.of(bufferedImage)
					.sourceRegion(param.x, param.y, param.w, param.h)
					//强制使用设置尺寸
					.forceSize(param.cropWidth, param.cropHeight)
					//使用bufferedImage必须指定类型
					.outputFormat(extension)
					.toOutputStream(outputStream);
			InputStream inputStream= new ByteArrayInputStream(outputStream.toByteArray());
			//上传又拍云
			boolean b = uploadToUpYunBySteam(uploadPath.relativeFilePath, inputStream);

			inputStream.close();
			if (b){
				param.setSize(outputStream.size());
				return uploadPath;
			}
		}
		return null;
	}

	/**
	 * 上传图片到又拍云
	 * @param  upYunPath
	 * @param  inStream
	 * @return
	 * @throws IOException
	 * @throws UpException
	 */
	public default boolean uploadToUpYunBySteam(String upYunPath, InputStream inStream) throws IOException, UpException {
		return this.getUpYunClient().writeFile(upYunPath, inStream, true, null);
	}

    /**
     * 根据byte[] 上传图片到又拍云
     *
     * @param filePath
     * @param datas
     * @return
     * @throws Exception
     */
	public default boolean uploadToUpYunByByte(String filePath, byte[] datas) throws Exception {
		return this.getUpYunClient().writeFile(filePath,datas,true);
	}



}
