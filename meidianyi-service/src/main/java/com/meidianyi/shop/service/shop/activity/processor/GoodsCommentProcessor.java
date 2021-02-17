package com.meidianyi.shop.service.shop.activity.processor;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailCapsuleParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsDetailMpBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity.GoodsListMpBo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.CommentDetailVo;
import com.meidianyi.shop.service.shop.activity.dao.GoodsCommentProcessorDao;
import com.meidianyi.shop.service.shop.image.ImageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 李晓冰
 * @date 2019年11月04日
 */
@Service
public class GoodsCommentProcessor implements Processor,ActivityGoodsListProcessor,GoodsDetailProcessor{
    @Autowired
    GoodsCommentProcessorDao goodsCommentProcessorDao;
    @Autowired
    ImageService imageService;

    /*****处理器优先级*****/
    @Override
    public Byte getPriority() {
        return 0;
    }

    @Override
    public Byte getActivityType() {
        return BaseConstant.ACTIVITY_TYPE_GENERAL;
    }

    /*****************商品列表处理*******************/
    @Override
    public void processForList(List<GoodsListMpBo> capsules, Integer userId) {
        List<Integer> goodsIds = capsules.stream().map(GoodsListMpBo::getGoodsId).collect(Collectors.toList());
        Map<Integer, Long> goodsCommentNumInfo = goodsCommentProcessorDao.getGoodsCommentNumInfo(goodsIds);

        capsules.forEach(capsule->{
            Long commentNum = goodsCommentNumInfo.get(capsule.getGoodsId());
            if (commentNum != null) {
                capsule.setCommentNum(commentNum.intValue());
            } else {
                capsule.setCommentNum(0);
            }
        });
    }

    /*****************商品详情处理*******************/
    @Override
    public void processGoodsDetail(GoodsDetailMpBo goodsDetailMpBo, GoodsDetailCapsuleParam param) {
        CommentDetailVo goodsCommentInfoForDetail = goodsCommentProcessorDao.getGoodsCommentInfoForDetail(param.getGoodsId());
       goodsDetailMpBo.setComment(goodsCommentInfoForDetail);
       if (goodsCommentInfoForDetail==null){
           return;
       }
        CommentDetailVo.CommentInfo commentInfo = goodsCommentInfoForDetail.getCommentInfo();
        commentInfo.setUserAvatar(getImgFullUrlUtil(commentInfo.getUserAvatar()));
        List<String> fullImgs = new ArrayList<>();
        commentInfo.getCommImgs().forEach(img->fullImgs.add(getImgFullUrlUtil(img)));
        commentInfo.setCommImgs(fullImgs);
    }

    /**
     * 将相对路劲修改为全路径
     * @param relativePath 相对路径
     * @return null或全路径
     */
    private String getImgFullUrlUtil(String relativePath) {
        if (StringUtils.isBlank(relativePath)) {
            return null;
        } else {
            return imageService.imageUrl(relativePath);
        }
    }
}
