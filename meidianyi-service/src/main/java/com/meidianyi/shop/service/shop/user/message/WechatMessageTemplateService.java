package com.meidianyi.shop.service.shop.user.message;

import com.meidianyi.shop.common.foundation.util.RegexUtil;
import com.meidianyi.shop.db.main.tables.records.MpAuthShopRecord;
import com.meidianyi.shop.db.shop.tables.records.MpOfficialAccountUserRecord;
import com.meidianyi.shop.db.shop.tables.records.UserRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.market.message.RabbitMessageParam;
import com.meidianyi.shop.service.pojo.shop.market.message.RabbitParamConstant;
import com.meidianyi.shop.service.pojo.shop.message.MpTemplateConfig;
import com.meidianyi.shop.service.pojo.shop.message.MpTemplateData;
import com.meidianyi.shop.service.pojo.shop.user.message.MaSubscribeData;
import com.meidianyi.shop.service.pojo.shop.user.message.MaTemplateData;
import com.meidianyi.shop.service.pojo.shop.user.user.WxUserInfo;
import com.meidianyi.shop.service.saas.shop.MpAuthShopService;
import com.meidianyi.shop.service.saas.shop.official.MpOfficialAccountUserService;
import com.meidianyi.shop.service.saas.shop.official.message.MpOfficialAccountMessageService;
import com.meidianyi.shop.service.shop.config.ShopMsgTemplateConfigService;
import com.meidianyi.shop.service.shop.market.message.MessageTemplateService;
import com.meidianyi.shop.service.shop.user.user.UserService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static com.meidianyi.shop.db.shop.tables.MpTemplateFormId.MP_TEMPLATE_FORM_ID;


/**
 * 微信模版消息
 * @author 卢光耀
 * @date 2019-08-29 15:26
 *
*/
@Service
public class WechatMessageTemplateService extends ShopBaseService {

    @Autowired
    private MaTemplateService maTemplateService;

    @Autowired
    private UserService userService;

    /**
     * The Msg template config service.消息模板配置
     */
    @Autowired
    public ShopMsgTemplateConfigService msgTemplateConfigService;

    private MpOfficialAccountMessageService accountMessageService;

    private MpOfficialAccountUserService accountUserService;

    private MpAuthShopService mpAuthShopService;
    @Autowired
    private SubscribeMessageService subscribeMessageService;

    @PostConstruct
    public void init(){
        accountMessageService = saas().shop.mpOfficialAccountMessageService;
        accountUserService = saas().shop.mpOfficialAccountUserService;
        mpAuthShopService = saas().shop.mp;
    }

    /**
     * 小程序和公众号发送其中一个（优先小程序）
     * @param param MQ传参 封装参照{@link MessageTemplateService}的assemblyRabbitMessageParam
     * @param info 所需信息（openID，appID）
     * @return 是否发送成功
     */
    public Boolean sendMessage(RabbitMessageParam param,WxUserInfo info) {
    	Integer type = param.getType();
    	//type大于2000为小程序
        //String formId = getFormId(info.getUserId());
    	logger().info("小程序和公众号发送其中一个");
        Boolean success = Boolean.FALSE;
        if( param.getMaTemplateData() != null && !param.getType().equals(RabbitParamConstant.Type.DIY_MESSAGE_TEMPLATE)){
        	logger().info("发小程序");
            success = sendMaMessage(param,info);
            logger().info("发小程序结果："+success);
        }

        if( param.getMpTemplateData() != null && !success ){
            if( !saas().getShopApp(param.getShopId()).config.messageConfigService.checkConfig(param.getType()) ){
                logger().info("【消息模板监听】---商家{}未开通{}类型的公众号消息推送",param.getShopId(),param.getType());
                return false;
            }
            if(StringUtils.isBlank(info.getMpOpenId()) ){
                return false;
            }
			logger().info("发公众号");
            success = sendMpMessage(param,info);
            logger().info("发公众号结果："+success);
        }
        return success;
    }

    /**
     * 获得formId，并置为已使用
     * @param userId 用户id
     * @return formId
     */
    private String getFormId(Integer userId){
        String formId = db().select()
            .from(MP_TEMPLATE_FORM_ID)
            .where(MP_TEMPLATE_FORM_ID.USER_ID.eq(userId))
            .and(MP_TEMPLATE_FORM_ID.USE_STATE.eq((byte)0))
            .orderBy(MP_TEMPLATE_FORM_ID.CREATE_TIME.desc())
            .fetchAny(MP_TEMPLATE_FORM_ID.FORM_ID);
        db().update(MP_TEMPLATE_FORM_ID)
            .set(MP_TEMPLATE_FORM_ID.USE_STATE,(byte)2)
            .where(MP_TEMPLATE_FORM_ID.FORM_ID.eq(formId))
            .returning(MP_TEMPLATE_FORM_ID.FORM_ID,MP_TEMPLATE_FORM_ID.OPEN_ID)
            .fetch();
        return formId;
    }

    /**
     * 发送小程序模版消息
     * @param param MQ传参
     * @param info  所需信息（openID，appID）
     * @return 是否发送成功
     */
    public Boolean sendMaMessage(RabbitMessageParam param,WxUserInfo info) {
    	MaTemplateData maTemplateData = param.getMaTemplateData();
    	MaSubscribeData data = maTemplateData.getData();
    	Boolean sendMessage=Boolean.FALSE;
    	try {
			sendMessage = subscribeMessageService.sendMessage(info.getUserId(), maTemplateData.getConfig(), data, param.getPage());
		} catch (WxErrorException e) {
			e.printStackTrace();
			return Boolean.FALSE;
		}
    	 return sendMessage;
    }

    /**
     * 发送公众号模版消息
     * @param param MQ传参
     * @param info  所需信息（openID，appID）
     * @return 是否发送成功
     */
    public Boolean sendMpMessage(RabbitMessageParam param,WxUserInfo info) {
        List<WxMpTemplateData> wxDatalist = new ArrayList<>();
        MpTemplateData data = param.getMpTemplateData();
        MpTemplateConfig config =data.getConfig();
        List<String> names = RegexUtil.getSubStrList("{{",".",config.getContent());
        for (int i = 0,len = data.getData().length; i < len; i++) {
            String[] values = data.getData()[i];
            wxDatalist.add(new WxMpTemplateData(
                names.get(i),
                values[0],
                values.length==2?values[1]:config.getColors().get(names.get(i))
            ));
        }
        try{
            accountMessageService.sendMpTemplateMessage(info.getMpAppId(),info.getMpOpenId(),
                wxDatalist,config,info.getMaAppId(),param.getPage(),param.getPage(),param.getShopId(),param.getType(),
                info.getUserId(),param.getMessageTemplateId());
        } catch (WxErrorException e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
            return Boolean.TRUE;
    }

    /**
     * 封装发送模版消息所需要的用户相关信息
     * @param userIdList
     * @return 相关信息
     */
    public List<WxUserInfo> getUserInfoList(List<Integer> userIdList,Integer type,Integer shopId,RabbitMessageParam param) {
    	List<WxUserInfo> resultList = new ArrayList<>(userIdList.size());
    	MpAuthShopRecord authShopByShopId = mpAuthShopService.getAuthShopByShopId(shopId);
    	if( type.equals(RabbitParamConstant.Type.MP_TEMPLE_TYPE_NO_USER) ) {
    		//用户没有关注小程序没有unionId的，进行发公众号
    		for(Integer userId:userIdList) {
    			MpOfficialAccountUserRecord accountUserListByRecord =
    					saas.getShopApp(shopId).officialAccountUser.getAccountUserListByRecid(userId);
    			WxUserInfo info=WxUserInfo.builder()
    					.userId(userId)
    					.mpAppId(accountUserListByRecord.getAppId())
    					.mpOpenId(accountUserListByRecord.getOpenid())
    					.maAppId(authShopByShopId.getAppId())
                    .build();
    			resultList.add(info);
    		}
    		return resultList;
    	}if( null!=param.getMpTemplateData() && !type.equals(RabbitParamConstant.Type.MP_TEMPLE_TYPE_NO_USER)) {
    		//正常发公众号的
    		for(Integer userId:userIdList) {
				MpOfficialAccountUserRecord accountUserListByRecord = saas.getShopApp(shopId).officialAccountUser
						.getAccountUserByUserId(userId);
				if( accountUserListByRecord == null ){
				    continue;
                }
    			//通过shopId得到小程序信息
    			WxUserInfo info=WxUserInfo.builder()
    					.userId(userId)
    					.mpAppId(accountUserListByRecord.getAppId())
    					.mpOpenId(accountUserListByRecord.getOpenid())
    					.maAppId(authShopByShopId.getAppId())
                    .build();
    			resultList.add(info);
    		}
    		//return resultList;
    	}else if( param.getMaTemplateData()!=null ||type>4000 ){
    		//发小程序的
            //String appId = mpAuthShopService.getAuthShopByShopId(getShopId()).get(MP_AUTH_SHOP.APP_ID);
            List<UserRecord> userList = userService.getUserRecordByIds(userIdList);
            for (UserRecord userRecord : userList) {
            	if(!haveInfo(userRecord.getUserId(), resultList)) {
            		WxUserInfo info=WxUserInfo.builder()
        					.userId(userRecord.getUserId()).build();
            		resultList.add(info);
            	}
			}
            return resultList;
        }
        return resultList;
    }
    
    private boolean haveInfo(Integer userId,List<WxUserInfo> resultList) {
    	for (WxUserInfo wxUserInfo : resultList) {
			if(wxUserInfo.getUserId().equals(userId)) {
				return true;
			}
		}
		return false;
    }
}
