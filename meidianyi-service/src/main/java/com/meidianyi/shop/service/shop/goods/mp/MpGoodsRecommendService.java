package com.meidianyi.shop.service.shop.goods.mp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.RecommendGoodsRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.config.ShowCartConfig;
import com.meidianyi.shop.service.pojo.shop.goods.recommend.GoodsLabelsVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.recommend.RecSource;
import com.meidianyi.shop.service.pojo.wxapp.goods.recommend.RecommendGoodsParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.recommend.RecommendGoodsVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.search.SortDirectionEnum;
import com.meidianyi.shop.service.pojo.wxapp.goods.search.SortItemEnum;
import com.meidianyi.shop.service.saas.categroy.SysCateService;
import com.meidianyi.shop.service.saas.shop.ShopAppService;
import com.meidianyi.shop.service.shop.config.ConfigService;
import com.meidianyi.shop.service.shop.config.ShopCommonConfigService;
import com.meidianyi.shop.service.shop.overview.HotWordsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.Tables.*;

/**
 * 小程序-商品推荐
 *
 * @author liangchen
 * @date 2019.12.23
 */
@Service
public class MpGoodsRecommendService extends ShopBaseService {
    @Autowired
    ConfigService configService;
    @Autowired
    SysCateService sysCateService;
    @Autowired
    HotWordsService hotWordsService;
    @Autowired
    ShopCommonConfigService shopCommonConfigService;
    @Autowired
    GoodsMpService goodsMpService;
    @Autowired
    ShopAppService shopAppService;
    /**
     * 商品推荐活动状态标识 0:未删除/未停用
     */
    private static final Byte GOING_STATUS = 0;
    /**
     * 商品推荐活动推荐类型 1:智能推荐
     */
    private static final Byte SMART_RECOMMEND = 1;
    /**
     * 商品推荐活动推荐类型 0:智能推荐
     */
    private static final Byte ALL_GOODS = 0;
    /**
     * 商品是否在售 1:在售
     */
    private static final Byte IS_ON_SALE = 1;
    /**
     * 商品是否删除 0:未删除
     */
    private static final Byte NOT_DELETE = 0;
    /**
     * 分类是否有子节点 0:没有
     */
    private static final Byte HAS_CHILD = 0;
    /**
     * 搜索条数：1
     */
    private static final Integer LIMIT_ONE_NUM = 1;
    /**
     * 标签分类类型type 1：关联商品
     */
    private static final Byte LABEL_TYPE_ONE = 1;
    /**
     * 标签分类类型type  2：平台分类
     */
    private static final Byte LABEL_TYPE_TWO = 2;
    /**
     * 标签分类类型type 3:店铺分类
     */
    private static final Byte LABEL_TYPE_THREE = 3;
    /**
     * 标签分类类型type 4：全部商品
     */
    private static final Byte LABEL_TYPE_FOUR = 4;
    /**
     * pos店铺id默认值：0
     */
    private static final Integer DEFAULT_POS_SHOP_ID = 0;
    /**
     * 门店商品是否已同步 1：已同步
     */
    private static final Byte IS_SYNC = 1;

    /**
     * 获取推荐商品
     */
    public RecommendGoodsVo getRecommendGoods(RecommendGoodsParam param) {
        //如果recommendGoodsIds是空数组,为其赋值
        if (param.getRecommendGoodsIds() == null) {
            param.setRecommendGoodsIds(getRecommendGoodsIds(param.getPageName(), param.getUserId()));
        }
        //判断赋值是否成功
//        if (param.getRecommendGoodsIds() == null) {
//            throw new BusinessException(JsonResultCode.GOODS_RECOMMEND_NO_RECOMMENDED_GOODS);
//        }
        //对接门店
        if (param.getStoreId()!=null){
            //获取商品规格id
            List<Integer> prdId = getGoodsProducts(param.getRecommendGoodsIds());
            //获取商品id
            List<Integer> storeGoodsIds = getStoreGoodsList(param.getStoreId(),prdId);
            //和推荐商品id取交集
            List<Integer> newGoodsIds = param.getRecommendGoodsIds();
            newGoodsIds.retainAll(storeGoodsIds);
            param.setRecommendGoodsIds(newGoodsIds);
        }

        SortItemEnum sortItemEnum = null;
        //开启了店铺默认排序规则
        if(shopCommonConfigService.getRecommendSort().equals((byte)1)){
            sortItemEnum = goodsMpService.getShopGoodsSortEnum();
        }

        //商品详情展示
        List<?> goodsListNormal = goodsMpService.getGoodsListNormal(param.getRecommendGoodsIds(), param.getUserId(), sortItemEnum, SortDirectionEnum.DESC);
        logger().info(goodsListNormal.toString());
        //pageNum:当前页码
        int pageNum = param.getPageNum();
        //pageSize:每页多少条数据
        int pageSize = param.getPageSize();
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = pageNum * pageSize;
        int total = goodsListNormal.size();
        //自定义分页处理
        if (pageNum * pageSize <= total) {
            goodsListNormal = goodsListNormal.subList(startIndex, endIndex);
        } else {
            if(startIndex>total){
                return null;
            }
            goodsListNormal = goodsListNormal.subList(startIndex, total);
        }
        //是否显示划线价开关
        Byte delMarket = shopCommonConfigService.getDelMarket();
        //是否显示购买按钮
        ShowCartConfig showCart = shopCommonConfigService.getShowCart();
        RecommendGoodsVo result = new RecommendGoodsVo();
        result.setShowCart(showCart);
        result.setDelMarket(delMarket);
        result.setRecommendGoodsIds(param.getRecommendGoodsIds());
        result.setRecommendGoods(goodsListNormal);
        return result;
    }

    /**
     * 获取推荐商品
     *
     * @param pageName 当前所在页面
     * @param userId   当前登录的用户id
     * @return 推荐商品id集合
     */
    public List<Integer> getRecommendGoodsIds(String pageName, Integer userId) {
        //得到已启用未删除的商品推荐活动
        List<RecommendGoodsRecord> records = getGoingRecommend();
        //找到第一个匹配当前页面的推荐活动
        RecommendGoodsRecord record = null;
        for (RecommendGoodsRecord item : records) {
            if (Util.json2Object(item.getRecommendUsePage(), new TypeReference<List<String>>() {
            }, false).contains(pageName)) {
                record = item;
                break;
            }
        }
        //没有推荐商品
        if (record == null) {
            return null;
        }
        logger().info("商品推荐活动id："+record.getId()+",商品推荐活动name："+record.getRecommendName());
        List<Integer> recommendGoodsIds;
        //智能推荐
        if (record.getChooseType().equals(SMART_RECOMMEND)) {
            recommendGoodsIds = getSmartRecommendGoodsIds(record, userId);
        }
        //普通推荐
        else {
            recommendGoodsIds = getSellRecommendGoodsIds(record);
        }
        //统一控制数量超出问题
        if (recommendGoodsIds.size() == 0) {
            return null;
        }
        int recommendNumber = record.getRecommendNumber() == null ? 6 : record.getRecommendNumber();
        if (recommendGoodsIds.size() > recommendNumber) {
            Collections.shuffle(recommendGoodsIds);
            return recommendGoodsIds.subList(0, recommendNumber);
        }
        logger().info("最终商品推荐活动id："+recommendGoodsIds);
        return recommendGoodsIds;
    }

    /**
     * 得到已启用未删除的商品推荐活动
     *
     * @return 符合条件的所有活动结果集 {@link com.meidianyi.shop.db.shop.tables.records.RecommendGoodsRecord}
     */
    public List<RecommendGoodsRecord> getGoingRecommend() {
        List<RecommendGoodsRecord> records = db().select()
            .from(RECOMMEND_GOODS)
            .where(RECOMMEND_GOODS.STATUS.eq(GOING_STATUS))
            .and(RECOMMEND_GOODS.DEL_FLAG.eq(GOING_STATUS))
            .orderBy(RECOMMEND_GOODS.CREATE_TIME.desc())
            .fetchInto(RecommendGoodsRecord.class);
        return records;
    }

    /**
     * 获取普通推荐商品id
     *
     * @param record 商品推荐活动
     * @return 商品id集合
     */
    public List<Integer> getSellRecommendGoodsIds(RecommendGoodsRecord record) {
        List<Integer> goodsIds = new ArrayList<>();
        //适用于全部商品
        if (record.getRecommendType().equals(ALL_GOODS)) {
            goodsIds = getOnShelfGoods(null, null, null);
            return goodsIds;
        }
        //适用于部分商品
        else {
            //指定商品
            if (StringUtils.isNotBlank(record.getRecommendGoodsId())) {
                goodsIds = getOnShelfGoods(record.getRecommendGoodsId(), null, null);
            }
            //指定平台分类
            if (StringUtils.isNotBlank(record.getRecommendCatId())) {
                goodsIds.addAll(getOnShelfGoods(null, record.getRecommendCatId(), null));
            }
            //指定商家分类
            if (StringUtils.isNotBlank(record.getRecommendSortId())) {
                goodsIds.addAll(getOnShelfGoods(null, null, record.getRecommendSortId()));
            }
        }
        //若数量不满足推荐数量 补充数量
//        int remainderNumber = record.getRecommendNumber() - goodsIds.size();
//        if (remainderNumber > 0) {
//            List<Integer> goodsList = getPageList(record.getRecommendNumber());
//            for (Integer item : goodsList) {
//                if (!goodsIds.contains(item)) {
//                    goodsIds.add(item);
//                    remainderNumber -= 1;
//                    if (remainderNumber <= 0) {
//                        break;
//                    }
//                }
//            }
//        }
        logger().info("补充数量后商品id集合："+goodsIds);
        //推荐商品id去重
        goodsIds = goodsIds.stream().distinct().collect(Collectors.toList());
        logger().info("去重后商品id集合："+goodsIds);
        return goodsIds;
    }

    /**
     * 得到在售商品id集合
     *
     * @param goodsIds 指定商品id
     * @param catIds   指定平台分类id
     * @param sortIds  指定商家分类id
     * @return 商品id
     */
    public List<Integer> getOnShelfGoods(String goodsIds, String catIds, String sortIds) {
        List<Integer> result = new ArrayList<>();
//        List<Integer> goodsResult = new ArrayList<>();
//        List<Integer> catResult = new ArrayList<>();
//        List<Integer> sortResult = new ArrayList<>();
        SelectConditionStep<Record1<Integer>> selectConditionStep = db().select(GOODS.GOODS_ID)
            .from(GOODS)
            .where(GOODS.DEL_FLAG.eq(NOT_DELETE))
            .and(GOODS.IS_ON_SALE.eq(IS_ON_SALE));
        // 是否展示售罄
        Byte soldOutGoods = configService.shopCommonConfigService.getSoldOutGoods();
        if (soldOutGoods == null || soldOutGoods.equals(NumberUtils.BYTE_ZERO)) {
            selectConditionStep.and(GOODS.GOODS_NUMBER.greaterThan(NumberUtils.INTEGER_ZERO));
        }
        //指定商品
        if (goodsIds != null) {
            String goodsIdsJson = "["+goodsIds+"]";
            List<Integer> goodsIdsList = Util.json2Object(goodsIdsJson, new TypeReference<List<Integer>>() {
            }, false);
            selectConditionStep.and(GOODS.GOODS_ID.in(goodsIdsList)).orderBy(GOODS.CREATE_TIME.desc());
        }
        //指定平台分类
        if (catIds != null) {
            String catIdsJson = "["+catIds+"]";
            List<Integer> catIdsList = Util.json2Object(catIdsJson, new TypeReference<List<Integer>>() {
            }, false);
            selectConditionStep.and(GOODS.CAT_ID.in(sysCateService.getAllChild(catIdsList))).orderBy(GOODS.CREATE_TIME.desc());
        }
        //指定商家分类
        if (sortIds != null) {
            String sortIdsJson = "["+sortIds+"]";
            List<Integer> sortIdsList = Util.json2Object(sortIdsJson, new TypeReference<List<Integer>>() {
            }, false);
            //在所有父子节点中查找
            selectConditionStep.and(GOODS.SORT_ID.in(getAllChild(sortIdsList))).orderBy(GOODS.CREATE_TIME.desc());
        }

        result = selectConditionStep.fetchInto(Integer.class);
        logger().info("推荐商品id集合："+result);
        return result;
    }

    /**
     * 得到当前节点自身及所有子节点的集合
     *
     * @param ids 节点集合
     * @return 所有子节点结合
     */
    public List<Integer> getAllChild(List<Integer> ids) {
        //得到子节点
        List<Integer> childIds = db().select(SORT.SORT_ID).from(SORT).where(SORT.PARENT_ID.in(ids)).fetchInto(Integer.class);
        //得到子节点中还有子节点的节点
        List<Integer> grandChildIds = db().select(SORT.SORT_ID).from(SORT)
            .where(SORT.PARENT_ID.in(childIds))
            .and(SORT.HAS_CHILD.greaterThan(HAS_CHILD))
            .fetchInto(Integer.class);
        //若存在二级子节点，递归得到后合并
        if (grandChildIds.size() > NumberUtils.INTEGER_ZERO) {
            List<Integer> anotherChild = getAllChild(grandChildIds);
            childIds.addAll(anotherChild);
            //id去重
            childIds = childIds.stream().distinct().collect(Collectors.toList());
        }
        logger().info("传入id集合："+ids);
        ids.addAll(childIds);
        logger().info("返回id集合："+ids);
        return ids;
    }

    /**
     * 获取智能推荐商品id
     *
     * @param record 商品推荐活动信息
     * @param userId 用户id
     * @return 商品id
     */
    public List<Integer> getSmartRecommendGoodsIds(RecommendGoodsRecord record, Integer userId) {
        logger().info("开始获取智能推荐商品id");
        //得到推荐商品数量
        int recommendNumber = record.getRecommendNumber() == null ? 6 : record.getRecommendNumber();
        //推荐的商品id、商家分类id、品牌id、平台分类id
        Map<String, RecSource> recSource = getSmartRecommendSource(userId);
        //全部商品id集合
        List<Integer> totalGoodsIds = getRecommendTotalGoodsIds(recommendNumber, recSource);
        //来源遍历结束
        int remainderNumber = recommendNumber - totalGoodsIds.size();
        if (remainderNumber > 0) {
            List<Integer> goodsList = getPageList(recommendNumber);
            for (Integer item : goodsList) {
                if (!totalGoodsIds.contains(item)) {
                    totalGoodsIds.add(item);
                    remainderNumber -= 1;
                    if (remainderNumber <= 0) {
                        break;
                    }
                }
            }
        }
        logger().info("智能筛选后的商品id为：{}",totalGoodsIds);
        return totalGoodsIds;
    }

    /**
     * 得到推荐的全部商品id集合
     * @param recommendNumber
     * @param recSource
     * @return
     */
    private List<Integer> getRecommendTotalGoodsIds(int recommendNumber, Map<String, RecSource> recSource) {
        List<Integer> totalGoodsIds = new ArrayList<>();
        if (recSource.size() > 0) {
            int average = (int) Math.floor(recommendNumber / recSource.size());
            int remainder = recommendNumber % recSource.size();
            Map<String, List<Integer>> numbers = new HashMap<>(8);
            numbers.put("index", new ArrayList<Integer>() {{
                add(1);
            }});
            numbers.put("sort", new ArrayList<>());
            numbers.put("label", new ArrayList<>());
            numbers.put("brand", new ArrayList<>());
            numbers.put("cat", new ArrayList<>());
            //遍历商品推荐来源
            for (Map.Entry<String, RecSource> source : recSource.entrySet()) {
                source.getValue().setRemainderNumber(average + ((remainder >= numbers.get("index").get(0)) ? 1 : 0));
                if (source.getValue().getRemainderNumber() <= 0) {
                    continue;
                }
                numbers.put("index", new ArrayList<Integer>() {{
                    add(numbers.get("index").get(0) + 1);
                }});
                //商家分类id
                if (source.getValue().getSortId() > 0) {
                    List<Integer> sortGoodsId = new ArrayList<>();
                    if (!numbers.get("sort").contains(source.getValue().getSortId())) {
                        List<Integer> sortGoods = getGoodsBySorts(source.getValue().getSortId());
                        sortGoodsId.addAll(sortGoods);
                    }
                    totalGoodsIds = processGoodsLabelIds(totalGoodsIds, source, sortGoodsId);
                    if (source.getValue().getRemainderNumber() <= 0) {
                        continue;
                    }
                }
                //标签
                Integer labelId = getGoodsLabelsByGoods(source.getValue().getGoodsId(), source.getValue().getCatId(), source.getValue().getSortId());
                if (labelId != null) {
                    List<Integer> labelGoodsId = new ArrayList<>();
                    if (!numbers.get("label").contains(labelId)) {
                        List<Integer> labelGoods = getGoodsByLabel(labelId);
                        labelGoodsId.addAll(labelGoods);
                    }
                    totalGoodsIds = processGoodsLabelIds(totalGoodsIds, source, labelGoodsId);
                    if (source.getValue().getRemainderNumber() <= 0) {
                        continue;
                    }
                }
                //品牌id
                if (source.getValue().getBrandId() > 0) {
                    List<Integer> brandGoodsId = new ArrayList<>();
                    if (!numbers.get("brand").contains(source.getValue().getBrandId())) {
                        List<Integer> brandGoods = getGoodsByBrands(source.getValue().getBrandId());
                        brandGoodsId.addAll(brandGoods);
                    }
                    totalGoodsIds = processGoodsLabelIds(totalGoodsIds, source, brandGoodsId);
                    if (source.getValue().getRemainderNumber() <= 0) {
                        continue;
                    }
                }
                //平台分类id
                if (source.getValue().getCatId() > 0) {
                    List<Integer> catGoodsId = new ArrayList<>();
                    if (!numbers.get("cat").contains(source.getValue().getCatId())) {
                        List<Integer> catGoods = getGoodsByCatIds(source.getValue().getCatId());
                        catGoodsId.addAll(catGoods);
                    }
                    totalGoodsIds = processGoodsLabelIds(totalGoodsIds, source, catGoodsId);
                    if (source.getValue().getRemainderNumber() <= 0) {
                        continue;
                    }
                }
            }
        }
        return totalGoodsIds;
    }

    private List<Integer> processGoodsLabelIds(List<Integer> totalGoodsIds, Map.Entry<String, RecSource> source, List<Integer> labelGoodsId) {
        while (labelGoodsId.size() > 0) {
            //当前id加入全部id集合中
            totalGoodsIds.addAll(labelGoodsId);
            //id去重
            totalGoodsIds = totalGoodsIds.stream().distinct().collect(Collectors.toList());
            //计数器自减
            source.getValue().setRemainderNumber(source.getValue().getRemainderNumber() - 1);
            if (source.getValue().getRemainderNumber() <= 0) {
                break;
            }
        }
        return totalGoodsIds;
    }

    /**
     * 得到智能推荐来源（用户最近操作涉及到的商品）
     *
     * @param userId 用户id
     * @return 各类型对应的商品id（搜索、购物车、访问、订单、分享、收藏）
     */
    public Map<String, RecSource> getSmartRecommendSource(Integer userId) {
        Map<String, RecSource> recommendInfo = new HashMap<>(8);
        //获取用户最近一次搜索热词
        String lastHotWords = hotWordsService.getUserLastSearch(userId);
        if (lastHotWords != null) {
            //根据搜索热词得到商品id信息
            RecSource idsByHotWords = getGoodsByHotWords(lastHotWords);
            if (idsByHotWords != null) {
                recommendInfo.put("search", idsByHotWords);
            }
        }
        //获取用户最近一次购物车记录
        Integer lastCartGoodsId = getUserLastCart(userId);
        if (lastCartGoodsId != null) {
            //根据商品id获取商品信息
            RecSource idsByGoodsId = getGoodsByGoodsId(lastCartGoodsId);
            if (idsByGoodsId != null) {
                recommendInfo.put("cart", idsByGoodsId);
            }
        }
        //获取用户最近一次访问商品记录
        Integer lastVisitGoodsId = getUserLastVisit(userId);
        if (lastVisitGoodsId != null) {
            //根据商品id获取商品信息
            RecSource idsByGoodsId = getGoodsByGoodsId(lastVisitGoodsId);
            if (idsByGoodsId != null) {
                recommendInfo.put("glance", idsByGoodsId);
            }
        }
        //获取用户最近一次订单商品
        Integer lastOrderGoodsId = getUserLastOrder(userId);
        if (lastOrderGoodsId != null) {
            //根据商品id获取商品信息
            RecSource idsByGoodsId = getGoodsByGoodsId(lastOrderGoodsId);
            if (idsByGoodsId != null) {
                recommendInfo.put("buy", idsByGoodsId);
            }
        }
        //获取用户最近一次分享商品
        Integer lastShareGoodsId = getUserLastShare(userId);
        if (lastShareGoodsId != null) {
            //根据商品id获取商品信息
            RecSource idsByGoodsId = getGoodsByGoodsId(lastShareGoodsId);
            if (idsByGoodsId != null) {
                recommendInfo.put("share", idsByGoodsId);
            }
        }
        //获取用户最近一次收藏商品
        Integer lastCollectionGoodsId = getUserLastCollection(userId);
        if (lastCollectionGoodsId != null) {
            //根据商品id获取商品信息
            RecSource idsByGoodsId = getGoodsByGoodsId(lastCollectionGoodsId);
            if (idsByGoodsId != null) {
                recommendInfo.put("collection", idsByGoodsId);
            }
        }
        return recommendInfo;
    }

    /**
     * 根据用户搜索热词获取商品信息
     *
     * @param hotWords 搜索热词
     * @return 商品id信息
     */
    public RecSource getGoodsByHotWords(String hotWords) {
        RecSource ids = db().select(GOODS.GOODS_ID, GOODS.SORT_ID, GOODS.BRAND_ID, GOODS.CAT_ID)
            .from(GOODS)
            .where(GOODS.GOODS_NAME.like(this.likeValue(hotWords)))
            .orderBy(GOODS.GOODS_SALE_NUM.desc())
            .limit(LIMIT_ONE_NUM)
            .fetchOptionalInto(RecSource.class)
            .orElse(null);
        return ids;
    }

    /**
     * 得到用户最近一次加入购物车的商品id
     *
     * @param userId 用户id
     * @return 商品id
     */
    public Integer getUserLastCart(Integer userId) {
        Integer goodsId = db().select(CART.GOODS_ID)
            .from(CART)
            .where(CART.USER_ID.eq(userId))
            .orderBy(CART.CREATE_TIME.desc())
            .limit(LIMIT_ONE_NUM)
            .fetchOptionalInto(Integer.class)
            .orElse(null);
        return goodsId;
    }

    /**
     * 根据商品id获取商品信息
     *
     * @param goodsId 商品id
     * @return 商品id信息
     */
    public RecSource getGoodsByGoodsId(Integer goodsId) {
        RecSource ids = db().select(GOODS.GOODS_ID, GOODS.SORT_ID, GOODS.BRAND_ID, GOODS.CAT_ID)
            .from(GOODS)
            .where(GOODS.GOODS_ID.eq(goodsId))
            .orderBy(GOODS.GOODS_SALE_NUM.desc())
            .limit(LIMIT_ONE_NUM)
            .fetchOptionalInto(RecSource.class)
            .orElse(null);
        return ids;
    }

    /**
     * 得到用户最近一次访问记录的商品id
     *
     * @param userId 用户id
     * @return 商品id
     */
    public Integer getUserLastVisit(Integer userId) {
        Integer goodsId = db().select(USER_GOODS_RECORD.GOODS_ID)
            .from(USER_GOODS_RECORD)
            .where(USER_GOODS_RECORD.USER_ID.eq(userId))
            .orderBy(USER_GOODS_RECORD.CREATE_TIME.desc())
            .limit(LIMIT_ONE_NUM)
            .fetchOptionalInto(Integer.class)
            .orElse(null);
        return goodsId;
    }

    /**
     * 得到用户最近一次订单商品的商品id
     *
     * @param userId 用户id
     * @return 商品id
     */
    public Integer getUserLastOrder(Integer userId) {
        Integer goodsId = db().select(ORDER_GOODS.GOODS_ID)
            .from(ORDER_GOODS)
            .leftJoin(ORDER_INFO).on(ORDER_GOODS.ORDER_SN.eq(ORDER_INFO.ORDER_SN))
            .leftJoin(GOODS).on(ORDER_GOODS.GOODS_ID.eq(GOODS.GOODS_ID))
            .where(ORDER_INFO.USER_ID.eq(userId))
            .orderBy(GOODS.GOODS_SALE_NUM.desc(), ORDER_INFO.CREATE_TIME.desc())
            .limit(LIMIT_ONE_NUM)
            .fetchOptionalInto(Integer.class)
            .orElse(null);
        return goodsId;
    }

    /**
     * 得到用户最近一次分享的商品id
     *
     * @param userId 用户id
     * @return 商品id
     */
    public Integer getUserLastShare(Integer userId) {
        Integer goodsId = db().select(SHARE_RECORD.ACTIVITY_ID)
            .from(SHARE_RECORD)
            .where(SHARE_RECORD.USER_ID.eq(userId))
            .and(SHARE_RECORD.ACTIVITY_TYPE.eq(NumberUtils.INTEGER_ZERO))
            .orderBy(SHARE_RECORD.CREATE_TIME.desc())
            .limit(LIMIT_ONE_NUM)
            .fetchOptionalInto(Integer.class)
            .orElse(null);
        return goodsId;
    }

    /**
     * 得到用户最近一次分享的商品id
     *
     * @param userId 用户id
     * @return 商品id
     */
    public Integer getUserLastCollection(Integer userId) {
        Integer goodsId = db().select(USER_COLLECTION.GOODS_ID)
            .from(USER_COLLECTION)
            .where(USER_COLLECTION.USER_ID.eq(userId))
            .orderBy(USER_COLLECTION.CREATE_TIME.desc())
            .limit(LIMIT_ONE_NUM)
            .fetchOptionalInto(Integer.class)
            .orElse(null);
        return goodsId;
    }

    /**
     * 根据分类id获取商品信息
     *
     * @param sortId 商家分类id
     * @return 商品id
     */
    public List<Integer> getGoodsBySorts(Integer sortId) {
        // 是否展示售罄
        Byte soldOutGoods = configService.shopCommonConfigService.getSoldOutGoods();
        SelectConditionStep<Record1<Integer>> goodsId = db().select(GOODS.GOODS_ID)
            .from(GOODS)
            .where(GOODS.SORT_ID.eq(sortId))
            .and(GOODS.IS_ON_SALE.eq(IS_ON_SALE))
            .and(GOODS.DEL_FLAG.eq(NOT_DELETE));
        if (soldOutGoods == null || soldOutGoods.equals(NumberUtils.BYTE_ZERO)) {
            goodsId.and(GOODS.GOODS_NUMBER.greaterThan(NumberUtils.INTEGER_ZERO));
        }
        return goodsId.orderBy(GOODS.GOODS_SALE_NUM.desc())
            .fetchInto(Integer.class);
    }

    /**
     * 根据商品id获取联系最紧密的标签
     *
     * @param goodsId 商品id
     * @param catId   平台分类id
     * @param sortId  商家分类id
     * @return 标签id
     */
    public Integer getGoodsLabelsByGoods(Integer goodsId, Integer catId, Integer sortId) {
        Integer labelId = db().select(GOODS_LABEL_COUPLE.LABEL_ID)
            .from(GOODS_LABEL_COUPLE)
            .leftJoin(GOODS_LABEL).on(GOODS_LABEL_COUPLE.LABEL_ID.eq(GOODS_LABEL.ID))
            .where(GOODS_LABEL_COUPLE.TYPE.eq(LABEL_TYPE_ONE).and(GOODS_LABEL_COUPLE.GTA_ID.eq(goodsId)).and(GOODS_LABEL.DEL_FLAG.eq(NOT_DELETE)))
            .or(GOODS_LABEL_COUPLE.TYPE.eq(LABEL_TYPE_TWO).and(GOODS_LABEL_COUPLE.GTA_ID.eq(catId)).and(GOODS_LABEL.DEL_FLAG.eq(NOT_DELETE)))
            .or(GOODS_LABEL_COUPLE.TYPE.eq(LABEL_TYPE_THREE).and(GOODS_LABEL_COUPLE.GTA_ID.eq(sortId)).and(GOODS_LABEL.DEL_FLAG.eq(NOT_DELETE)))
            .or(GOODS_LABEL_COUPLE.TYPE.eq(LABEL_TYPE_FOUR).and(GOODS_LABEL.DEL_FLAG.eq(NOT_DELETE)))
            .orderBy(GOODS_LABEL_COUPLE.TYPE.asc(), GOODS_LABEL.LEVEL.desc(), GOODS_LABEL.CREATE_TIME.desc())
            .limit(LIMIT_ONE_NUM)
            .fetchOptionalInto(Integer.class)
            .orElse(null);
        return labelId;
    }

    /**
     * 获取与该商品关系最紧密的前n个标签信息
     * @param goodsId
     * @param catId
     * @param sortId
     * @param limitNum
     * @return
     */
    public List<GoodsLabelsVo> getGoodsLabelsByGoods(Integer goodsId,Integer limitNum,Byte type) {
    	List<GoodsLabelsVo> list=new ArrayList<GoodsLabelsVo>();
    	if(type.equals(IS_ON_SALE)) {
    		list = db().select(GOODS_LABEL.ID,GOODS_LABEL.NAME)
    				.from(GOODS_LABEL_COUPLE)
    				.leftJoin(GOODS_LABEL).on(GOODS_LABEL_COUPLE.LABEL_ID.eq(GOODS_LABEL.ID)).leftJoin(GOODS).on(GOODS.GOODS_ID.eq(goodsId))
    				.where(GOODS_LABEL_COUPLE.TYPE.eq(LABEL_TYPE_ONE).and(GOODS_LABEL_COUPLE.GTA_ID.eq(goodsId)).and(GOODS_LABEL.DEL_FLAG.eq(NOT_DELETE)))
    				.or(GOODS_LABEL_COUPLE.TYPE.eq(LABEL_TYPE_TWO).and(GOODS_LABEL_COUPLE.GTA_ID.eq(GOODS.CAT_ID)).and(GOODS_LABEL.DEL_FLAG.eq(NOT_DELETE)))
    				.or(GOODS_LABEL_COUPLE.TYPE.eq(LABEL_TYPE_THREE).and(GOODS_LABEL_COUPLE.GTA_ID.eq(GOODS.SORT_ID)).and(GOODS_LABEL.DEL_FLAG.eq(NOT_DELETE)))
    				.or(GOODS_LABEL_COUPLE.TYPE.eq(LABEL_TYPE_FOUR).and(GOODS_LABEL.DEL_FLAG.eq(NOT_DELETE)))
    				.orderBy(GOODS_LABEL_COUPLE.TYPE.asc(), GOODS_LABEL.LEVEL.desc(), GOODS_LABEL.CREATE_TIME.desc())
    				.limit(limitNum).fetchInto(GoodsLabelsVo.class);
    		
    	}
    	else {
    		list = db().select(GOODS_LABEL.ID,GOODS_LABEL.NAME)
    				.from(GOODS_LABEL_COUPLE)
    				.leftJoin(GOODS_LABEL).on(GOODS_LABEL_COUPLE.LABEL_ID.eq(GOODS_LABEL.ID)).leftJoin(GOODS).on(GOODS.GOODS_ID.eq(goodsId))
    				.where(GOODS_LABEL_COUPLE.TYPE.eq(LABEL_TYPE_ONE).and(GOODS_LABEL_COUPLE.GTA_ID.eq(goodsId)).and(GOODS_LABEL.DEL_FLAG.eq(NOT_DELETE)))
    				.or(GOODS_LABEL_COUPLE.TYPE.eq(LABEL_TYPE_TWO).and(GOODS_LABEL_COUPLE.GTA_ID.eq(GOODS.CAT_ID)).and(GOODS_LABEL.DEL_FLAG.eq(NOT_DELETE)))
    				.or(GOODS_LABEL_COUPLE.TYPE.eq(LABEL_TYPE_THREE).and(GOODS_LABEL_COUPLE.GTA_ID.eq(GOODS.SORT_ID)).and(GOODS_LABEL.DEL_FLAG.eq(NOT_DELETE)))
    				.or(GOODS_LABEL_COUPLE.TYPE.eq(LABEL_TYPE_FOUR).and(GOODS_LABEL.DEL_FLAG.eq(NOT_DELETE)).and(GOODS_LABEL.GOODS_DETAIL.eq(IS_ON_SALE)))
    				.orderBy(GOODS_LABEL_COUPLE.TYPE.asc(), GOODS_LABEL.LEVEL.desc(), GOODS_LABEL.CREATE_TIME.desc())
    				.limit(limitNum).fetchInto(GoodsLabelsVo.class);
    	}
    	return list;
    }
    
    /**
     * 根据标签id获取商品信息
     *
     * @param labelId 标签id
     * @return 商品id
     */
    public List<Integer> getGoodsByLabel(Integer labelId) {
        // 是否展示售罄
        Byte soldOutGoods = configService.shopCommonConfigService.getSoldOutGoods();
        List<Integer> goodsLabelAll = getGoodsLabelCouple(labelId, LABEL_TYPE_FOUR);
        SelectConditionStep<Record1<Integer>> goodsId = db().select(GOODS.GOODS_ID)
            .from(GOODS)
            .where(GOODS.IS_ON_SALE.eq(IS_ON_SALE))
            .and(GOODS.DEL_FLAG.eq(NOT_DELETE));
        if (soldOutGoods == null || soldOutGoods.equals(NumberUtils.BYTE_ZERO)) {
            goodsId.and(GOODS.GOODS_NUMBER.greaterThan(NumberUtils.INTEGER_ZERO));
        }
        if (goodsLabelAll == null) {
            List<Integer> goodsId1 = getGoodsIdByLabelCouple(labelId, LABEL_TYPE_ONE);
            List<Integer> goodsId2 = getGoodsLabelCouple(labelId, LABEL_TYPE_TWO);
            List<Integer> goodsId3 = getGoodsLabelCouple(labelId, LABEL_TYPE_THREE);
            goodsId.or(GOODS.GOODS_ID.in(goodsId1))
                .or(GOODS.CAT_ID.in(goodsId2))
                .or(GOODS.SORT_ID.in(goodsId3));
        }
        return goodsId.orderBy(GOODS.GOODS_SALE_NUM.desc())
            .fetchInto(Integer.class);
    }

    /**
     * 根据标签id获取关联商品id
     *
     * @param labelId 标签id
     * @param type    1：关联商品
     * @return 商品id
     */
    public List<Integer> getGoodsIdByLabelCouple(Integer labelId, Byte type) {
        List<Integer> goodsId = db().select(GOODS_LABEL_COUPLE.LABEL_ID)
            .from(GOODS_LABEL_COUPLE)
            .leftJoin(GOODS).on(GOODS.GOODS_ID.eq(GOODS_LABEL_COUPLE.GTA_ID))
            .where(GOODS_LABEL_COUPLE.LABEL_ID.eq(labelId))
            .and(GOODS_LABEL_COUPLE.TYPE.eq(type))
            .fetchInto(Integer.class);
        return goodsId;
    }

    /**
     * 根据标签id获取catId sortId allGoods
     *
     * @param labelId 标签id
     * @param type    2：平台分类 3店铺分类 4：全部商品
     * @return catId sortId allGoods
     */
    public List<Integer> getGoodsLabelCouple(Integer labelId, Byte type) {
        List<Integer> gtaId = db().select(GOODS_LABEL_COUPLE.GTA_ID)
            .from(GOODS_LABEL_COUPLE)
            .where(GOODS_LABEL_COUPLE.LABEL_ID.eq(labelId))
            .and(GOODS_LABEL_COUPLE.TYPE.eq(type))
            .fetchInto(Integer.class);
        return gtaId;
    }

    /**
     * 根据品牌id获取商品信息
     *
     * @param brandId 品牌id
     * @return 商品id
     */
    public List<Integer> getGoodsByBrands(Integer brandId) {
        // 是否展示售罄
        Byte soldOutGoods = configService.shopCommonConfigService.getSoldOutGoods();
        SelectConditionStep<Record1<Integer>> goodsId = db().select(GOODS.GOODS_ID)
            .from(GOODS)
            .where(GOODS.BRAND_ID.eq(brandId))
            .and(GOODS.IS_ON_SALE.eq(IS_ON_SALE))
            .and(GOODS.DEL_FLAG.eq(NOT_DELETE));
        if (soldOutGoods == null || soldOutGoods.equals(NumberUtils.BYTE_ZERO)) {
            goodsId.and(GOODS.GOODS_NUMBER.greaterThan(NumberUtils.INTEGER_ZERO));
        }
        return goodsId.orderBy(GOODS.GOODS_SALE_NUM.desc())
            .fetchInto(Integer.class);
    }

    /**
     * 根据平台分类id获取商品信息
     *
     * @param catId 平台分类id
     * @return 商品id
     */
    public List<Integer> getGoodsByCatIds(Integer catId) {
        // 是否展示售罄
        Byte soldOutGoods = configService.shopCommonConfigService.getSoldOutGoods();
        SelectConditionStep<Record1<Integer>> goodsId = db().select(GOODS.GOODS_ID)
            .from(GOODS)
            .where(GOODS.CAT_ID.eq(catId))
            .and(GOODS.IS_ON_SALE.eq(IS_ON_SALE))
            .and(GOODS.DEL_FLAG.eq(NOT_DELETE));
        if (soldOutGoods == null || soldOutGoods.equals(NumberUtils.BYTE_ZERO)) {
            goodsId.and(GOODS.GOODS_NUMBER.greaterThan(NumberUtils.INTEGER_ZERO));
        }
        return goodsId.orderBy(GOODS.GOODS_SALE_NUM.desc())
            .fetchInto(Integer.class);
    }

    /**
     * 得到数量等同于推荐商品数的商品id
     *
     * @param limitNum 推荐商品数量
     * @return 商品id集合
     */
    public List<Integer> getPageList(Integer limitNum) {
        List<Integer> goodsList = db().select(GOODS.GOODS_ID)
            .from(GOODS)
            .where(GOODS.DEL_FLAG.eq(NOT_DELETE))
            .orderBy(GOODS.GOODS_SALE_NUM.desc())
            .limit(limitNum)
            .fetchInto(Integer.class);
        return goodsList;
    }

    /**
     * 获取商品规格id
     * @param goodsIds 商品id集合
     * @return 对应规格id集合
     */
    public List<Integer> getGoodsProducts(List<Integer> goodsIds){
        List<Integer> prdIds = db().select(GOODS_SPEC_PRODUCT.PRD_ID)
            .from(GOODS_SPEC_PRODUCT)
            .where(GOODS_SPEC_PRODUCT.GOODS_ID.in(goodsIds))
            .fetchInto(Integer.class);
        return prdIds;
    }

    /**
     * 判断是否对接pos
     * @param storeId 门店id
     * @return true or false
     */
    public boolean isHasPosService(Integer storeId) {
        Byte status = shopAppService.getShopAppByPos(getShopId());
        if (NumberUtils.BYTE_ZERO.equals(status)){
            return false;
        }
        if (storeId!=null){
            Integer posShopId = db().select(STORE.POS_SHOP_ID)
                .from(STORE)
                .where(STORE.STORE_ID.eq(storeId))
                .fetchOptionalInto(Integer.class)
                .orElse(DEFAULT_POS_SHOP_ID);
            if (NumberUtils.INTEGER_ZERO.equals(posShopId)){
                return false;
            }
        }
        return true;
    }

    public List<Integer> getStoreGoodsList(Integer storeId,List<Integer> productIds){
        boolean isHasPosService = isHasPosService(storeId);
        SelectConditionStep<Record1<Integer>> storeGoodsIds = db().select(STORE_GOODS.GOODS_ID)
            .from(STORE_GOODS)
            .leftJoin(GOODS).on(STORE_GOODS.GOODS_ID.eq(GOODS.GOODS_ID))
            .where(GOODS.DEL_FLAG.eq(NOT_DELETE))
            .and(STORE_GOODS.STORE_ID.eq(storeId))
            .and(STORE_GOODS.IS_ON_SALE.eq(IS_ON_SALE))
            .and(GOODS.IS_ON_SALE.eq(IS_ON_SALE));
        if (productIds!=null&&productIds.size()>0){
            storeGoodsIds.and(STORE_GOODS.PRD_ID.in(productIds));
        }
        if (isHasPosService){
            storeGoodsIds.and(STORE_GOODS.IS_SYNC.eq(IS_SYNC));
        }
        return storeGoodsIds.fetchInto(Integer.class);
    }
}
