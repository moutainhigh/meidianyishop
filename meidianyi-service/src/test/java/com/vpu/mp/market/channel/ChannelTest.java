package com.meidianyi.shop.market.channel;

import com.meidianyi.shop.App;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.shop.tables.records.ChannelRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.market.channel.ChannelConstant;
import com.meidianyi.shop.service.pojo.shop.market.channel.ChannelPageParam;
import com.meidianyi.shop.service.pojo.shop.market.channel.ChannelPageVo;
import com.meidianyi.shop.service.pojo.shop.market.channel.ChannelParam;
import com.meidianyi.shop.service.shop.market.channel.ChannelService;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static com.meidianyi.shop.db.shop.tables.Channel.CHANNEL;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = App.class)
@Slf4j
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles("dev-mac")
public class ChannelTest extends ShopBaseService {

    @Autowired(required = false)
    ChannelService channelService;

    ChannelParam param;

    Integer id;

    @Before
    public void init(){
        param = new ChannelParam();;
        param.setChannelName("单元测试数据01");
        param.setContentId(286);
        param.setSourceType(ChannelConstant.SOURCETYPE_CUSTOMIZE);
        saas().getShopApp(245547);
    }

    @Test
    public void test1Insert(){
        channelService.insert(param);
        Assert.assertEquals(db().selectCount().from(CHANNEL).where(CHANNEL.PAGE_ID.eq(286)).execute(),1);
    }
    @Test
    public void test2GetPageList(){
        ChannelPageParam pageParam = new ChannelPageParam();
        pageParam.setChannelName("单元测试数据01");
        PageResult<ChannelPageVo> result = channelService.getPageList(pageParam);
        id = result.dataList.get(0).getId();
        Assert.assertNotNull(result);
    }

    @Test
    public void test3SelectChannelName(){
        String name = channelService.selectChannelName(getId());
        Assert.assertEquals(name,"单元测试数据01");
    }
    @Test
    public void test4DisableChannel(){
        channelService.disableChannel(getId());
        ChannelRecord record = getChannel();
        Assert.assertEquals(record.getDelFlag().byteValue(),(byte)1);
    }
    @Test
    public void test5EnableChannel(){
        channelService.enableChannel(getId());
        ChannelRecord record = getChannel();
        Assert.assertEquals(record.getDelFlag().byteValue(),(byte)0);
    }

    @Test
    public  void test6Delete(){
        db().delete(CHANNEL).where(CHANNEL.ID.eq(getId())).execute();
    }
    private Integer getId(){
        ChannelPageParam pageParam = new ChannelPageParam();
        pageParam.setChannelName("单元测试数据01");
        PageResult<ChannelPageVo> result = channelService.getPageList(pageParam);
        return result.dataList.get(0).getId();
    }

    private ChannelRecord getChannel(){
        ChannelRecord record = db().select().from(CHANNEL).where(CHANNEL.ID.eq(getId())).fetchAnyInto(CHANNEL);
        return record;
    }
}
