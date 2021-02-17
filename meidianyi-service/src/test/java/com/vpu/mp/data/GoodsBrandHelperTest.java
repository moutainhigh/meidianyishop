package com.meidianyi.shop.data;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.meidianyi.shop.App;
import com.meidianyi.shop.dao.foundation.database.DatabaseManager;
import com.meidianyi.shop.service.foundation.jedis.data.GoodsBrandDataHelper;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.goods.brand.GoodsBrandAddParam;
import com.meidianyi.shop.service.shop.goods.GoodsBrandService;
import lombok.extern.slf4j.Slf4j;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.meidianyi.shop.db.shop.Tables.GOODS_BRAND;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
classes = App.class)
@Slf4j
@ActiveProfiles("dev-mac")
public class GoodsBrandHelperTest extends ShopBaseService {


    @Autowired(required = false)
    GoodsBrandDataHelper goodsBrandDataHelper;
    @Autowired(required = false)
    GoodsBrandService goodsBrandService;


    private static List<GoodsBrandAddParam> brands =Lists.newArrayList();

    private static Integer[] brandId = new Integer[10];

    @BeforeClass
    public static void ini(){
        for (int i = 0; i < 10; i++) {
            brandId[i] =  9999999+i;
            GoodsBrandAddParam param1 = new GoodsBrandAddParam();
            param1.setId(brandId[i]);
            param1.setBrandName("(不要做任何操作)单元测试数据_"+i);
            param1.setLogo("/upload/245547/image/20191217/JB6tc2isW3IT6HlYKsCP.jpg");
            param1.setIsRecommend((byte)0);
            brands.add(param1);
        }
    }

    @Before
    public  void init(){
        saas().getShopApp(245547);
        brands.forEach(x->goodsBrandService.insert(x));

    }
    @Test
    public void get() {
        for (int i = 0; i < 10; i++) {
            Stopwatch stopwatch = Stopwatch.createStarted();
            goodsBrandDataHelper.get(Arrays.asList(brandId));
            log.debug("第{}次耗时：{}ms",i+1,stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }

    @After
    public void after(){
        log.debug("门店id{}",getShopId());
        db().delete(GOODS_BRAND).where(GOODS_BRAND.ID.in(brandId)).execute();
        goodsBrandDataHelper.delete(Arrays.asList(brandId));
    }
}
