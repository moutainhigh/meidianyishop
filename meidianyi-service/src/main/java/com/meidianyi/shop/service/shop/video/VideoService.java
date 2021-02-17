package com.meidianyi.shop.service.shop.video;

import com.UpYun;
import com.upyun.UpException;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.DomainConfig;
import com.meidianyi.shop.config.UpYunConfig;
import com.meidianyi.shop.db.shop.tables.records.UploadedVideoRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.foundation.video.UpyunSynVideo;
import com.meidianyi.shop.service.foundation.video.UpyunSynVideo.AvMeta;
import com.meidianyi.shop.service.foundation.video.UpyunSynVideo.Stream;
import com.meidianyi.shop.service.pojo.saas.shop.version.VersionNumConfig;
import com.meidianyi.shop.service.pojo.shop.base.ResultMessage;
import com.meidianyi.shop.service.pojo.shop.video.*;
import com.meidianyi.shop.service.shop.version.VersionService;
import org.apache.commons.lang3.StringUtils;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.tables.UploadedVideo.UPLOADED_VIDEO;
import static com.meidianyi.shop.db.shop.tables.UploadedVideoCategory.UPLOADED_VIDEO_CATEGORY;

/**
 * @author lixinguo
 */
@Service
public class VideoService extends ShopBaseService {

    @Autowired
    public VideoCategoryService category;

    @Autowired
    protected UpYunConfig upYunConfig;

    @Autowired
    protected DomainConfig domainConfig;
    @Autowired
    private VersionService versionService;

    /**
     * 又拍云实例
     *
     * @return
     */
    public UpYun getUpYunClient() {
        return new UpYun(
            upYunConfig.getVideoServer(),
            upYunConfig.getVideoOpName(),
            upYunConfig.getVideoOpPassword());
    }

    public UpyunSynVideo getUpyunSynVideo() {
        return new UpyunSynVideo(
            upYunConfig.getVideoServer(),
            upYunConfig.getVideoOpName(),
            upYunConfig.getVideoOpPassword());
    }

    /**
     * 删除又拍云视频
     *
     * @param path 视频路径
     * @return boolean
     * @throws UpException
     * @throws IOException
     */
    public boolean deleteYpYunVideo(String path) throws IOException, UpException {
        return getUpYunClient().deleteFile(path, null);
    }

    /**
     * 判断视频格式格式是否支持，简单判断下扩展名
     *
     * @param type
     * @return boolean
     */
    public boolean validVideoType(String type) {
        return StringUtils.equalsAnyIgnoreCase(type, ".mp4", ".mkv", ".wmv");
    }

    /**
     * 判断视频格式格式是否支持，简单判断下扩展名
     *
     * @param contentType
     * @return boolean
     */
    public boolean validVideoContentType(String contentType) {
        return StringUtils.equalsAnyIgnoreCase(contentType, "video/mp4", "video/mkv", "video/wmv");
    }

    /**
     * 返回文件扩展名
     *
     * @param pathOrUrl
     * @return
     */
    public String getFileType(String pathOrUrl) {
        int pos = pathOrUrl.lastIndexOf(".");
        if (pos != -1) {
            return pathOrUrl.substring(pos);
        }
        return "";
    }

    /**
     * 上传视频到又拍云
     *
     * @param upYunPath
     * @param inStream
     * @return
     * @throws IOException
     * @throws Exception
     */
    public boolean uploadToUpYunBySteam(String upYunPath, InputStream inStream)
        throws IOException, UpException {
        return this.getUpYunClient().writeFile(upYunPath, inStream, true, null);
    }

    /**
     * 根据byte[] 上传视频到又拍云
     *
     * @return
     * @throws IOException
     * @throws Exception
     */
    public boolean uploadToUpYunByByte(String filePath, byte[] datas) throws IOException, Exception {
        return this.getUpYunClient().writeFile(filePath, datas, true);
    }

    /**
     * 上传视频文件
     *
     * @param name
     * @param size
     * @param inStream
     * @param ext
     * @param catId
     * @param userId
     * @return
     * @throws IOException
     * @throws Exception
     */
    public UploadedVideoRecord uploadVideoFile(
        String name, Integer size, InputStream inStream, String ext, Integer catId, Integer userId)
        throws IOException, Exception {
        String videoPath =
            String.format(
                "/video/%s/%s%s",
                DateUtils.dateFormat(DateUtils.DATE_FORMAT_SHORT), Util.randomId(), ext);
        this.uploadToUpYunBySteam(videoPath, inStream);
        UpyunSynVideo synVideo = getUpyunSynVideo();
        AvMeta avMeta = synVideo.avMeta(videoPath);
        Stream videoStream = UpyunSynVideo.getStream(avMeta, "video");
        String snapshot =
            String.format(
                "/snapshot/%s/%s.jpg",
                DateUtils.dateFormat(DateUtils.DATE_FORMAT_SHORT), Util.randomId());
        synVideo.snapshot(videoPath, snapshot, "00:00:00");

        return this.insertVideoRecord(
            name, videoPath, size, catId, userId, avMeta, videoStream, snapshot, ext);
    }

    /**
     * 插入记录
     *
     * @param name
     * @param videoPath
     * @param size
     * @param catId
     * @param userId
     * @param avMeta
     * @param videoStream
     * @param snapshot
     * @param ext
     * @return
     */
    protected UploadedVideoRecord insertVideoRecord(
        String name,
        String videoPath,
        Integer size,
        Integer catId,
        Integer userId,
        AvMeta avMeta,
        Stream videoStream,
        String snapshot,
        String ext) {
        UploadedVideoRecord record = db().newRecord(UPLOADED_VIDEO);
        record.setVideoType(ext.substring(1));
        record.setVideoSize(size);
        record.setVideoName(name);
        record.setVideoOrigFname(name);
        record.setVideoPath(videoPath);
        record.setVideoSnapPath(snapshot);
        record.setVideoUrl(upYunConfig.videoUrl(videoPath));
        record.setVideoCatId(catId);
        record.setVideoWidth(videoStream.getVideoWidth());
        record.setVideoHeight(videoStream.getVideoHeight());
        record.setVideoDuration(videoStream.getDuration().intValue());
        record.setVideoMeta(Util.toJson(avMeta));
        record.setUserId(userId);
        record.insert();
        return record;
    }

    /**
     * 删除行
     *
     * @param videoId
     * @return
     */
    public int removeRow(Integer videoId) {
        return db().update(UPLOADED_VIDEO)
            .set(UPLOADED_VIDEO.DEL_FLAG, (byte) 1)
            .set(UPLOADED_VIDEO.DEL_TIME, DateUtils.getLocalDateTime())
            .where(UPLOADED_VIDEO.VIDEO_ID.eq(videoId))
            .execute();
    }

    /**
     * 删除多行
     *
     * @param videoIds
     * @return
     */
    public int removeRows(Integer[] videoIds) {
        return db().update(UPLOADED_VIDEO)
            .set(UPLOADED_VIDEO.DEL_FLAG, (byte) 1)
            .set(UPLOADED_VIDEO.DEL_TIME, DateUtils.getLocalDateTime())
            .where(UPLOADED_VIDEO.VIDEO_ID.in(videoIds))
            .execute();
    }

    /**
     * 视频列表分页
     *
     * @param param
     * @return
     */
    public PageResult<UploadVideoCatNameVo> getPageList(VideoListQueryParam param) {

        SelectWhereStep select = db()
            .select(UPLOADED_VIDEO_CATEGORY.VIDEO_CAT_NAME, UPLOADED_VIDEO.VIDEO_ID, UPLOADED_VIDEO.VIDEO_TYPE, UPLOADED_VIDEO.VIDEO_SIZE,UPLOADED_VIDEO.VIDEO_NAME,
                UPLOADED_VIDEO.VIDEO_URL,UPLOADED_VIDEO.VIDEO_PATH,UPLOADED_VIDEO.VIDEO_CAT_ID,UPLOADED_VIDEO.VIDEO_WIDTH,UPLOADED_VIDEO.VIDEO_HEIGHT,
                UPLOADED_VIDEO.VIDEO_DURATION,UPLOADED_VIDEO.CREATE_TIME,UPLOADED_VIDEO.VIDEO_SNAP_PATH)
            .from(UPLOADED_VIDEO)
            .leftJoin(UPLOADED_VIDEO_CATEGORY)
            .on(UPLOADED_VIDEO.VIDEO_CAT_ID.eq(UPLOADED_VIDEO_CATEGORY.VIDEO_CAT_ID));
        select = this.buildOptions(select, param);
        select.orderBy(UPLOADED_VIDEO.VIDEO_ID.desc());
        PageResult<UploadVideoCatNameVo> result =
            this.getPageResult(select, param.page, param.pageRows, UploadVideoCatNameVo.class);
        for (UploadedVideoVo uploadedVideo : result.dataList) {
            uploadedVideo.setSnapshotUrl(this.upYunConfig.videoUrl(uploadedVideo.getVideoSnapPath()));
            Integer dur = uploadedVideo.getVideoDuration();
            if (dur != null) {
                String formatDuration =
                    String.format(
                        "%02d:%02d:%02d",
                        Math.floorMod(Math.floorDiv(dur, 3600), 24),
                        Math.floorMod(Math.floorDiv(dur, 60), 60),
                        Math.floorMod(dur, 60));
                uploadedVideo.setFormatDuration(formatDuration);
            }
        }
        return result;
    }

    /**
     * 拼接查询条件
     *
     * @param select
     * @param param
     * @return
     */
    public SelectWhereStep<Record> buildOptions(
        SelectWhereStep<Record> select, VideoListQueryParam param) {
        if (param == null) {
            return select;
        }
        Byte noDel = 0;
        select
            .where(UPLOADED_VIDEO.DEL_FLAG.eq(noDel))
            .and(UPLOADED_VIDEO.VIDEO_WIDTH.gt(0))
            .and(UPLOADED_VIDEO.VIDEO_HEIGHT.gt(0));

        if (param.videoCatId != null && param.videoCatId > 0) {
            List<Integer> videoCatIds = category.getChildCategoryIds(param.videoCatId);
            select.where(UPLOADED_VIDEO.VIDEO_CAT_ID.in(videoCatIds.toArray(new Integer[0])));
        }
        if (!StringUtils.isBlank(param.keywords)) {
            select.where(UPLOADED_VIDEO.VIDEO_NAME.like(this.likeValue(param.keywords)));
        }
        if (param.videoWidth != null && param.videoWidth > 0) {
            select.where(UPLOADED_VIDEO.VIDEO_WIDTH.eq(param.videoWidth));
        }
        if (param.videoHeight != null && param.videoHeight > 0) {
            select.where(UPLOADED_VIDEO.VIDEO_HEIGHT.eq(param.videoHeight));
        }

        SortField<?>[] sortFields = {
            UPLOADED_VIDEO.CREATE_TIME.desc(),
            UPLOADED_VIDEO.CREATE_TIME.asc(),
            UPLOADED_VIDEO.VIDEO_SIZE.desc(),
            UPLOADED_VIDEO.VIDEO_SIZE.asc(),
            UPLOADED_VIDEO.VIDEO_NAME.desc(),
            UPLOADED_VIDEO.VIDEO_NAME.asc()
        };
        if (param.uploadSortId != null
            && param.uploadSortId >= 0
            && param.uploadSortId < sortFields.length) {
            select.orderBy(sortFields[param.uploadSortId]);
        } else {
            select.orderBy(UPLOADED_VIDEO.VIDEO_ID.desc());
        }
        return select;
    }

    /**
     * 通过视频相对路径获取视频信息
     *
     * @param videoPath 视频相对路径
     * @return
     */
    public UploadedVideoRecord getVideoFromVideoPath(String videoPath) {
        return db().fetchAny(UPLOADED_VIDEO, UPLOADED_VIDEO.VIDEO_PATH.eq(videoPath));
    }

    /**
     * 获取总大小（不包括删除）
     *
     * @return
     */
    public Integer getAllSize() {
        Record1<BigDecimal> rec =
            db().select(DSL.sum(UPLOADED_VIDEO.VIDEO_SIZE).as("total"))
                .from(UPLOADED_VIDEO)
                .where(UPLOADED_VIDEO.DEL_FLAG.eq((byte) 0))
                .and(UPLOADED_VIDEO.VIDEO_WIDTH.gt(0))
                .and(UPLOADED_VIDEO.VIDEO_HEIGHT.gt(0))
                .fetchAny();
        if (rec == null) {
            return 0;
        }
        return Util.getInteger(rec.getValue(0));
    }

    /**
     * 删除用户上传的过期视频
     *
     * @return
     */
    public int deleteOvertimeVideo() {
        return db().update(UPLOADED_VIDEO)
            .set(UPLOADED_VIDEO.DEL_FLAG, (byte) 1)
            .set(UPLOADED_VIDEO.DEL_TIME, DateUtils.getLocalDateTime())
            .where(UPLOADED_VIDEO.DEL_FLAG.eq((byte) 0))
            .and(UPLOADED_VIDEO.VIDEO_CAT_ID.eq(-1))
            .and(UPLOADED_VIDEO.UPDATE_TIME.lt(DateUtils.getDalyedDateTime(-24 * 3600 * 7)))
            .execute();
    }

    /**
     * 清除又拍云里已经删除一个月的视频
     */
    public void deleteUpYunOvertimeVideo() {
        Result<UploadedVideoRecord> records =
            db().fetch(
                UPLOADED_VIDEO,
                UPLOADED_VIDEO
                    .DEL_FLAG
                    .eq((byte) 1)
                    .and(UPLOADED_VIDEO.VIDEO_CAT_ID.eq(-1))
                    .and(UPLOADED_VIDEO.UPYUN_DEL.eq((byte) 0))
                    .and(
                        UPLOADED_VIDEO.UPDATE_TIME.lt(
                            DateUtils.getDalyedDateTime(-24 * 3600 * 30))));
        for (UploadedVideoRecord record : records) {
            try {
                this.deleteYpYunVideo(record.getVideoPath());
                db().update(UPLOADED_VIDEO)
                    .set(UPLOADED_VIDEO.UPYUN_DEL, (byte) 1)
                    .where(UPLOADED_VIDEO.VIDEO_ID.eq(record.getVideoId()))
                    .execute();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (UpException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 校验添加视频参数
     *
     * @param param 入参
     * @param file  文件流
     * @return jsonResultCode, object
     */
    public ResultMessage validVideoParam(UploadVideoParam param, Part file) throws IOException {
        Integer maxSize = 10 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            return ResultMessage.builder()
                .jsonResultCode(JsonResultCode.CODE_VIDEO_UPLOAD_GT_10M)
                .build();
        }
        if (!validVideoContentType(file.getContentType())) {
            return ResultMessage.builder()
                .jsonResultCode(JsonResultCode.CODE_VIDEO_FORMAT_INVALID)
                .build();
        }
        Integer limitNum = versionService.getLimitNum(VersionNumConfig.VIDEONUM);
        //todo 视频大小 图片大小 表单数量 质询产品
        return ResultMessage.builder().flag(true).build();
    }

    /**
     * 设置视频的分类
     *
     * @param videoIds
     * @param catId
     * @return
     */
    public int setCatId(Integer[] videoIds, Integer catId) {
        return db().update(UPLOADED_VIDEO)
            .set(UPLOADED_VIDEO.VIDEO_CAT_ID, catId)
            .where(UPLOADED_VIDEO.VIDEO_ID.in((videoIds)))
            .execute();
    }

    public double getVideoUsedSpace() {
        List<Integer> list = db().select(UPLOADED_VIDEO.VIDEO_SIZE).from(UPLOADED_VIDEO)
            .where(UPLOADED_VIDEO.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
            .fetch(UPLOADED_VIDEO.VIDEO_SIZE);
        DoubleSummaryStatistics summary = list.stream().collect(Collectors.summarizingDouble(val -> val));
        double space = summary.getSum();
        space = space / 1024 / 1024;
        return BigDecimalUtil.setDoubleScale(space, 2, true);
    }
    public UploadedVideoVo selectOneVideo(VideoSelectParam param){
        UploadedVideoVo vo = db().select().from(UPLOADED_VIDEO)
            .where(UPLOADED_VIDEO.VIDEO_ID.eq(param.getVideoId()))
            .fetchOne().into(UploadedVideoVo.class);
        return vo;
    }
}
