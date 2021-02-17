package com.meidianyi.shop.config.mq;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitBootstrapConfiguration;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import java.util.HashMap;
import java.util.Map;

/**
 * rabbitMq相关配置
 * @author 卢光耀
 * @date 2019-07-30 10:40
 *
*/
@Import(RabbitBootstrapConfiguration.class)
@Configuration
public class RabbitConfig {

    @Value("${rabbitmq.host}")
    private String host;

    @Value("${rabbitmq.port}")
    private Integer port;

    @Value("${rabbitmq.username}")
    private String userName;

    @Value("${rabbitmq.password}")
    private String password;


    /** 发送失败消息默认存放队列 */
    public static final String QUEUE_ERROR_SEND = "error.send";
    /** 处理失败消息默认存放队列 */
    public static final String QUEUE_ERROR_DEAL = "error.deal";
    /** 发送优惠券默认存放队列 */
    public static final String QUEUE_COUPON_SEND = "marketing.coupon";
    /** 发送消息默认存放队列 */
    public static final String QUEUE_MESSAGE_SEND = "marketing.message";
    /** 获取公众号关注用户*/
    public static final String QUEUE_MA_MAP_BIND="bind.mamp.queue";
    /**批量提交小程序 */
    public static final String QUEUE_BATCH_UPLOAD="batch.upload.queue";
    /** ES的路由 */
    public static final String QUEUE_ES_GOODS = "es.goods";
    /*************好物圈队列start************/
    /** 订单*/
    public static final String QUEUE_WX_MALL_IMPORTORDER = "wx.mall.importorder";
    /** 收藏*/
    public static final String QUEUE_WX_MALL_ADDSHOPPINGLIST = "wx.mall.addshoppinglist";
    /** 物品信息*/
    public static final String QUEUE_WX_MALL_IMPORTPRODUCT = "wx.mall.importproduct";
    /*************好物圈队列end************/
    /**ElasticSearch--商品标签*/
    public static final String QUEUE_ES_LABEL = "es.label";
    /**读取Excel */
    public static final String QUEUE_EXCEL = "mq.excel";
    /** 关闭订单队列 */
    public static final String QUEUE_CLOSE_ORDER = "order.close";
    /**代付订单退款*/
    public static final String QUEUE_RETURN_SUB_ORDER = "order.return.sub";
    /**批量发货*/
    public static final String QUEUE_BATCH_SHIP = "order.batch.ship";
    /*************商品各种导入处理队列start************/
    /**微铺宝商品excel模板导入*/
    public static final String QUEUE_GOODS_VPU_EXCEL_IMPORT = "goods.vpu.excel.import";
    /*************商品各种导入处理队列end************/
    /**组团瓜分积分开奖*/
    public static final String QUEUE_GROUP_INTEGRATION_SUCCESS = "group.integration.success";
    /**system同步日历 */
    public static final String QUEUE_CALENDAR = "mq.calendar";
    /*************pos对接相关接口使用队列*************/
    public static final String QUEUE_POS_SYNC_PRODUCT = "pos.sync.product";

    /*************pos对接相关接口使用队列结束*************/

    /**
     * 路由和队列的对应关系是1:n不是1:1(路由按照模块区分)
     */
    /** 发送失败队列存储的路由 */
    public static final String EXCHANGE_ERROR = "direct.error";
    /** 营销功能的路由 */
    public static final String EXCHANGE_MARKETING = "direct.marketing";
    /** ES的路由 */
    public static final String EXCHANGE_ES = "direct.es";
    /** 微信的路由(所有跟微信相关的队列都由这个路由转发) */
    public static final String EXCHANGE_WX = "direct.wx";
    /**订单的路由*/
    public static final String EXCHANGE_ORDER = "direct.order";
    /**商品导入路由*/
    public static final String EXCHANGE_GOODS_IMPORT = "direct.goods";
    /**pos对接相关接口路由*/
    public static final String EXCHANGE_POS_SYNC = "direct.pos.sync";
    /**
     * 门店商品同步
     */
    public static final String EXCHANGE_STORE_GOODS_UPDATE = "direct.store.goods";
    /**
     * storeGoods刷新任务队列
     **/
    public static final String QUEUE_STORE_GOODS_UPDATE = "store.goods.update";


    /** 发送失败路由键 */
    public static final String BINDING_EXCHANGE_ERROR_KEY = "direct.error.send";
    /** 处理失败路由键 */
    public static final String BINDING_EXCHANGE_DEAL_KEY = "direct.error.deal";
    /** 发送优惠券路由键 */
    public static final String BINDING_EXCHANGE_COUPON_KEY = "direct.marketing.coupon";
    /** 发送消息路由键 */
    public static final String BINDING_EXCHANGE_MESSAGE_KEY = "direct.marketing.message";
    /** 获取公众号关注用户的路由键*/
    public static final String BINDING_MA_MAP_BIND_KEY="bind.mamp.key";
    /** 批量提交小程序*/
    public static final String BINDING_BATCH_UPLOAD_KEY="bind.batch.upload";
    /** 批量提交小程序*/
    public static final String BINDING_ES_GOODS_KEY = "bind.es.goods";
    /*************好物圈路由键start************/
    /**
     * 好物圈订单
     */
	public static final String BINDING_EXCHANGE_IMPORTORDER_KEY = "bind.wx.importorders";
	public static final String BINDING_EXCHANGE_ADDSHOPPINGLIST_KEY = "bind.wx.addshoppinglist";
	public static final String BINDING_EXCHANGE_IMPORTPRODUCT_KEY = "bind.wx.importproducts";
    /*************好物圈队列end************/
    /**商品标签缓存数据一致性*/
    public static final String BINDING_EXCHANGE_ES_GOODS_LABEL_KEY = "bind.es.goods.label";
    /**读Excel */
    public static final String BINDING_EXCHANGE_OTHER_KEY = "other.read.excel";
    /**关闭订单路由键 */
    public static final String BINDING_EXCHANGE_CLOSE_ORDER_KEY = "bind.wx.closeorder";
    /**代付订单退款 */
    public static final String BINDING_EXCHANGE_RETURN_SUB_ORDER_KEY = "bind.exchange.return.sub.order.key";
    /**微铺宝商品excel模板导入*/
    public static final String BINDING_EXCHANGE_GOODS_VPU_EXCEL_IMPORT_KEY  = "bind.exchange.goods.vpu.excel.import.key";
    /**组团瓜分积分 */
    public static final String BINDING_EXCHANGE_GROUP_INTEGRATION_MQ_KEY = "bind.groupInte.key";
    /**system同步日历 */
    public static final String BINDING_EXCHANGE_SYS_CALENDAR_MQ_KEY = "sys.calendar.key";
    /**批量发货 */
    public static final String BINDING_EXCHANGE_BATCH_SHIP_KEY = "bind.batch.ship.key";
    /*************pos对接相关接口路由键*************/
    public static final String BINDING_EXCHANGE_POS_SYNC_PRODUCT_KEY = "bind.pos.sync.product";
    /*************pos对接相关接口路由键结束*************/
    /**
     * 更新门店商品
     */
    public static final String BINDING_EXCHANGE_STORE_GOODS_UPDATE_KEY = "direct.update.store.good.key";

    @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory connectionFactory =
                new CachingConnectionFactory(host,port);
        connectionFactory.setUsername(userName);
        connectionFactory.setPassword(password);
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;
    }
    @Bean
    public SimpleRabbitListenerContainerFactory
    simpleRabbitListenerContainerFactory(ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory =
                new SimpleRabbitListenerContainerFactory();
        simpleRabbitListenerContainerFactory.setMessageConverter(new Jackson2JsonMessageConverter());
        simpleRabbitListenerContainerFactory.setConnectionFactory(connectionFactory);
        simpleRabbitListenerContainerFactory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return simpleRabbitListenerContainerFactory;
    }
    @Bean(name="currentSimpleRabbitListenerFactory")
    public SimpleRabbitListenerContainerFactory
    currentSimpleRabbitListenerFactory(ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory =
            new SimpleRabbitListenerContainerFactory();
        simpleRabbitListenerContainerFactory.setConcurrentConsumers(5);
        simpleRabbitListenerContainerFactory.setPrefetchCount(5);
        simpleRabbitListenerContainerFactory.setMessageConverter(new Jackson2JsonMessageConverter());
        simpleRabbitListenerContainerFactory.setConnectionFactory(connectionFactory);
        simpleRabbitListenerContainerFactory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return simpleRabbitListenerContainerFactory;
    }
    @Lazy
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         ErrorCallback errorCallback){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnCallback(errorCallback);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    /**
     * 1.队列名字
     * 2.durable="true" 是否持久化 rabbitmq重启的时候不需要创建新的队列
     * 3.auto-delete    表示消息队列没有在使用时将被自动删除 默认是false
     * 4.exclusive      表示该消息队列是否只在当前connection生效,默认是false
     * @return 发送错误存储队列
     */
    @Bean
    public Queue errorSendQueue() {
        Map<String,Object> args = new HashMap<String,Object>(1);
        args.put("x-max-length",10000);
        return new Queue(QUEUE_ERROR_SEND,true,false,false,args);
    }
    /**
     * @return 处理错误存储队列
     */
    @Bean
    public Queue errorDealWithQueue() {
        Map<String,Object> args = new HashMap<String,Object>(1);
        args.put("x-max-length",10000);
        return new Queue(QUEUE_ERROR_DEAL,true,false,false,args);
    }
    /**
     * @return 发送模版消息队列
     */
    @Bean
    public Queue sendMessageQueue() {
        return new Queue(QUEUE_MESSAGE_SEND,true,false,false);
    }
    /**
     * @return 发送优惠券队列
     */
    @Bean
    public Queue sendCouponWithQueue() {
        return new Queue(QUEUE_COUPON_SEND, true, false, false);
    }

    /**
     * @return 发送优惠券队列
     */
    @Bean
    public Queue sendEsGoodsWithQueue() {
        return new Queue(QUEUE_ES_GOODS,true,false,false);
    }

    /**
     * @return 获取关注公众号的用户信息
     */
    @Bean
    public Queue sendMpMaBindQueue() {
    	return new Queue(QUEUE_MA_MAP_BIND,true);
    }

    /**
     * @return 批量提交小程序和公众号
     */
    @Bean
    public Queue batchUploadQueue() {
    	return new Queue(QUEUE_BATCH_UPLOAD,true);
    }

    /**
     * @return 微信好物圈
     */
    @Bean
    public Queue sendWxMallTdQueue() {
    	return new Queue(QUEUE_WX_MALL_IMPORTORDER,true);
    }
    @Bean
    public Queue sendWxMallScQueue() {
    	return new Queue(QUEUE_WX_MALL_ADDSHOPPINGLIST,true);
    }
    @Bean
    public Queue sendWxMallWpQueue() {
    	return new Queue(QUEUE_WX_MALL_IMPORTPRODUCT,true);
    }
    /**
     * @return es label queue
     */
    @Bean
    public Queue cacheGoodsLabelWithQueue() {
        return new Queue(QUEUE_ES_LABEL,true,false,false);
    }
    
    @Bean
    public Queue excelQueue() {
        return new Queue(QUEUE_EXCEL,true,false,false);
    }

    /**
     *
     * 关闭订单
     * @return
     */
    @Bean
    public Queue closeOrderQueue() {
        return new Queue(QUEUE_CLOSE_ORDER,true,false,false);
    }
    /**
     * 代付订单退款
     */
    @Bean
    public Queue returnSubOrderQueue() {
        return new Queue(QUEUE_RETURN_SUB_ORDER,true,false,false);
    }

    /**
     * 微铺宝excel商品模板导入
     * @return
     */
    @Bean
    public Queue goodsVpuExcelImportQueue() {
        return new Queue(QUEUE_GOODS_VPU_EXCEL_IMPORT,true,false,false);
    }
    
    /**
     * 组团瓜分积分开奖
     * @return
     */
    @Bean
    public Queue groupIntegrationQueue() {
        return new Queue(QUEUE_GROUP_INTEGRATION_SUCCESS,true,false,false);
    }

    /**
     * system同步日历
     * @return
     */
    @Bean
    public Queue calendarQueue() {
        return new Queue(QUEUE_CALENDAR,true,false,false);
    }

    /**
     * 批量发货
     */
    @Bean
    public Queue batchShipQueue() {
        return new Queue(QUEUE_BATCH_SHIP,true,false,false);
    }

    /**
     * pos_sync_product pos 同步商品接口 主要同步上下架和价格
     */
    @Bean
    public Queue posSyncProductQueue(){return new Queue(QUEUE_POS_SYNC_PRODUCT,true,false,false);}


    /**
     * 更新门店商品路由
     */
    @Bean
    public Queue storeGoodsUpdateQueue(){
        return new Queue(QUEUE_STORE_GOODS_UPDATE,true);
    }

    /**
     * 1.路由名字
     * 2.durable="true" 是否持久化 rabbitmq重启的时候不需要创建新的交换机
     * 3.autoDelete    当所有消费客户端连接断开后，是否自动删除队列
     * @return 错误存储路由
     */
    @Bean
    public DirectExchange errorExchange(){
        return new DirectExchange(EXCHANGE_ERROR,true,false);
    }
    /**
     * @return es路由
     */
    @Bean
    public DirectExchange esExchange(){
        return new DirectExchange(EXCHANGE_ES,true,false);
    }
    /**
     * @return 营销存储路由
     */
    @Bean
    public DirectExchange marketingExchange(){
        return new DirectExchange(EXCHANGE_MARKETING, true, false);
    }

    /**
     * @return 订单支付模板消息路由
     */
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(EXCHANGE_ORDER,true,false);
    }

    @Bean
    public DirectExchange wxExchange() {
        return new DirectExchange(EXCHANGE_WX,true,false);
    }

    /**
     * @return 商品导入使用的路由器
     */
    @Bean
    public DirectExchange goodsImportExchange() {
        return new DirectExchange(EXCHANGE_GOODS_IMPORT,true,false);
    }
    /**
     * store.goods路由
     */
    @Bean
    public DirectExchange storeGoodsExchange() {
        return new DirectExchange(EXCHANGE_STORE_GOODS_UPDATE, true, false);
    }

    /**
     * @return pos对接使用路由器
     */
    @Bean
    public DirectExchange posSyncExchange(){return new DirectExchange(EXCHANGE_POS_SYNC,true,false);}
    /**
     * @return 路由和队列绑定
     */
    @Bean
    public Binding bindingErrorSend(){
        return BindingBuilder.bind(errorSendQueue()).to(errorExchange()).with(BINDING_EXCHANGE_ERROR_KEY);
    }
    @Bean
    public Binding bindingErrorDeal(){
        return BindingBuilder.bind(errorDealWithQueue()).to(errorExchange()).with(BINDING_EXCHANGE_DEAL_KEY);
    }
    @Bean
    public Binding bindingEs(){
        return BindingBuilder.bind(sendEsGoodsWithQueue()).to(esExchange()).with(BINDING_ES_GOODS_KEY);
    }
    @Bean
    public Binding bindingCouponSend(){
        return BindingBuilder.bind(sendCouponWithQueue()).to(marketingExchange()).with(BINDING_EXCHANGE_COUPON_KEY);
    }

    @Bean
    public Binding bindingMessageSend(){
        return BindingBuilder.bind(sendMessageQueue()).to(marketingExchange()).with(BINDING_EXCHANGE_MESSAGE_KEY);
    }
    /**
     * 获取关注公众号的用户信息
     * @return
     */
    @Bean
    public Binding bindingTemplateSend() {
    	return BindingBuilder.bind(sendMpMaBindQueue()).to(wxExchange()).with(BINDING_MA_MAP_BIND_KEY);
    }


    @Bean
    public Binding batchUpload() {
    	return BindingBuilder.bind(batchUploadQueue()).to(wxExchange()).with(BINDING_BATCH_UPLOAD_KEY);
    }


    /**
     * 微信好物圈 订单  收藏  物品信息
     * @return
     */
    @Bean
    public Binding bindingWxMallTd() {
    	return BindingBuilder.bind(sendWxMallTdQueue()).to(wxExchange()).with(BINDING_EXCHANGE_IMPORTORDER_KEY);
    }
    @Bean
    public Binding bindingWxMallSc() {
    	return BindingBuilder.bind(sendWxMallScQueue()).to(wxExchange()).with(BINDING_EXCHANGE_ADDSHOPPINGLIST_KEY);
    }
    @Bean
    public Binding bindingWxMallWp() {
    	return BindingBuilder.bind(sendWxMallWpQueue()).to(wxExchange()).with(BINDING_EXCHANGE_IMPORTPRODUCT_KEY);
    }
    @Bean
    public Binding bindingCacheGoodsLabel() {
        return BindingBuilder.bind(cacheGoodsLabelWithQueue()).to(esExchange()).with(BINDING_EXCHANGE_ES_GOODS_LABEL_KEY);
    }
    @Bean
    public Binding bindingExcelSome() {
    	   return BindingBuilder.bind(excelQueue()).to(marketingExchange()).with(BINDING_EXCHANGE_OTHER_KEY);
    }

    @Bean
    public Binding bindingWxCloseOrder() {
        return BindingBuilder.bind(closeOrderQueue()).to(wxExchange()).with(BINDING_EXCHANGE_CLOSE_ORDER_KEY);
    }

    @Bean
    public Binding bindingReturnSubOrderQueue() {
        return BindingBuilder.bind(returnSubOrderQueue()).to(orderExchange()).with(BINDING_EXCHANGE_RETURN_SUB_ORDER_KEY);
    }
    @Bean
    public Binding bindingGoodsVpuExcelImportQueue() {
        return BindingBuilder.bind(goodsVpuExcelImportQueue()).to(goodsImportExchange()).with(BINDING_EXCHANGE_GOODS_VPU_EXCEL_IMPORT_KEY);
    }
    @Bean
    public Binding bindingGroupIntegrationQueue() {
    	   return BindingBuilder.bind(groupIntegrationQueue()).to(marketingExchange()).with(BINDING_EXCHANGE_GROUP_INTEGRATION_MQ_KEY);
    }

    @Bean
    public Binding bindingSysCalendar() {
    	   return BindingBuilder.bind(calendarQueue()).to(marketingExchange()).with(BINDING_EXCHANGE_SYS_CALENDAR_MQ_KEY);
    }

    @Bean
    public Binding bindingBatchShipQueue() {
        return BindingBuilder.bind(batchShipQueue()).to(orderExchange()).with(BINDING_EXCHANGE_BATCH_SHIP_KEY);
    }

    /**pos.sync.product队列绑定 direct.pos.sync 路由 */
    @Bean
    public Binding bindingPosSyncProductQueue(){
        return BindingBuilder.bind(posSyncProductQueue()).to(posSyncExchange()).with(BINDING_EXCHANGE_POS_SYNC_PRODUCT_KEY);
    }

    @Bean
    public Binding bingingStoreGoodsUpdateQueue() {
        return BindingBuilder.bind(storeGoodsUpdateQueue()).to(storeGoodsExchange()).with(BINDING_EXCHANGE_STORE_GOODS_UPDATE_KEY);
    }

    @Bean
    public RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry(){
        RabbitListenerEndpointRegistry registry = new RabbitListenerEndpointRegistry();
        return registry;
    }
}
